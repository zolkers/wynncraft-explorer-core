package com.edgn.uifw.templates;

import com.edgn.Main;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DocumentationScreen extends BaseScreen {

    private final List<DocumentLine> documentLines = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int contentHeight;
    private int visibleLines;
    private boolean documentLoaded = false;
    protected Screen previousScreen;

    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern ITALIC_PATTERN = Pattern.compile("\\*(.+?)\\*");
    private static final Pattern CODE_PATTERN = Pattern.compile("`(.+?)`");
    private static final Pattern LIST_PATTERN = Pattern.compile("^\\s*[-*+]\\s+(.+)$");
    private static final Pattern NUMBERED_LIST_PATTERN = Pattern.compile("^\\s*\\d+\\.\\s+(.+)$");

    protected DocumentationScreen(Text title, Screen previousScreen) {
        super(title);
        this.previousScreen = previousScreen;
    }

    protected abstract String getDocumentationPath();

    @Override
    protected void init() {
        super.init();

        if (!documentLoaded) {
            loadDocumentation();
            documentLoaded = true;
        }

        calculateScrollLimits();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            if (this.client != null) {
                this.client.setScreen(null);
            }
        }).dimensions(this.width - 80, 10, 70, 20).build());
    }

    private void loadDocumentation() {
        documentLines.clear();

        try {
            Identifier resourceId = Identifier.of(Main.MOD_ID, getDocumentationPath());
            assert this.client != null;
            InputStream inputStream = this.client.getResourceManager().getResource(resourceId).orElseThrow().getInputStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseMarkdownLine(line);
                }
            }
        } catch (IOException e) {
            documentLines.add(new DocumentLine("Error: Couldn't load the doc",
                    DocumentLineType.ERROR, 0, 0xFF0000));
            documentLines.add(new DocumentLine("File: " + getDocumentationPath(),
                    DocumentLineType.NORMAL, 0, 0xFF0000));
        }
    }

    private void parseMarkdownLine(String line) {
        if (line.trim().isEmpty()) {
            documentLines.add(new DocumentLine("", DocumentLineType.NORMAL, 0, 0xFFFFFF));
            return;
        }

        Matcher headerMatcher = HEADER_PATTERN.matcher(line);
        if (headerMatcher.matches()) {
            int level = headerMatcher.group(1).length();
            String text = headerMatcher.group(2);
            int color = getHeaderColor(level);
            documentLines.add(new DocumentLine(text, DocumentLineType.HEADER, level, color));
            return;
        }

        Matcher listMatcher = LIST_PATTERN.matcher(line);
        if (listMatcher.matches()) {
            String text = "• " + listMatcher.group(1);
            String processedText = processInlineFormatting(text);
            int color = text.contains("**") ? 0xFFFFFF : 0xCCCCCC;
            documentLines.add(new DocumentLine(processedText, DocumentLineType.LIST, 1, color));
            return;
        }

        Matcher numberedListMatcher = NUMBERED_LIST_PATTERN.matcher(line);
        if (numberedListMatcher.matches()) {
            String processedText = processInlineFormatting(line.trim());
            int color = line.contains("**") ? 0xFFFFFF : 0xCCCCCC;
            documentLines.add(new DocumentLine(processedText, DocumentLineType.LIST, 1, color));
            return;
        }

        if (line.trim().startsWith("```")) {
            documentLines.add(new DocumentLine(line, DocumentLineType.CODE_BLOCK, 0, 0x88FF88));
            return;
        }

        String processedText = processInlineFormatting(line);
        int color = line.contains("**") ? 0xFFFFFF : 0xCCCCCC;
        documentLines.add(new DocumentLine(processedText, DocumentLineType.NORMAL, 0, color));
    }

    private String processInlineFormatting(String text) {
        text = BOLD_PATTERN.matcher(text).replaceAll("$1");
        text = ITALIC_PATTERN.matcher(text).replaceAll("$1");
        text = CODE_PATTERN.matcher(text).replaceAll("$1");
        return text;
    }

    private int getHeaderColor(int level) {
        return switch (level) {
            case 1 -> 0xFFFF00;
            case 2 -> 0x00FFFF;
            case 3 -> 0xFF8800;
            case 4 -> 0x88FF88;
            case 5 -> 0xFF88FF;
            case 6 -> 0x8888FF;
            default -> 0xFFFFFF;
        };
    }

    private void calculateScrollLimits() {
        contentHeight = this.height - headerHeight - footerHeight;
        visibleLines = (contentHeight - 20) / 12;
        maxScrollOffset = Math.max(0, documentLines.size() - visibleLines);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            scrollOffset = Math.max(0, scrollOffset - 3);
        } else if (verticalAmount < 0) {
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + 3);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return switch (keyCode) {
            case 265 -> {
                scrollOffset = Math.max(0, scrollOffset - 1);
                yield true;
            }
            case 264 -> {
                scrollOffset = Math.min(maxScrollOffset, scrollOffset + 1);
                yield true;
            }
            case 266 -> {
                scrollOffset = Math.max(0, scrollOffset - visibleLines);
                yield true;
            }
            case 267 -> {
                scrollOffset = Math.min(maxScrollOffset, scrollOffset + visibleLines);
                yield true;
            }
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    @Override
    protected void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDefaultHeader(context);
    }

    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        int currentY = headerHeight + 10;

        context.fill(10, headerHeight + 5, width - 10, height - footerHeight - 5, 0x88000000);

        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleLines, documentLines.size()); i++) {
            DocumentLine line = documentLines.get(i);
            int x = 15;

            if (line.type == DocumentLineType.LIST) {
                x += 20;
            } else if (line.type == DocumentLineType.HEADER && line.level > 1) {
                x += (line.level - 1) * 10;
            }

            if (!line.text.isEmpty()) {
                context.drawTextWithShadow(textRenderer, line.text, x, currentY, line.color);
            }

            currentY += 12;
        }

        if (maxScrollOffset > 0) {
            renderScrollBar(context);
        }

        String scrollInfo = String.format("Line %d/%d",
                Math.min(scrollOffset + visibleLines, documentLines.size()),
                documentLines.size());
        context.drawTextWithShadow(textRenderer, scrollInfo, width - 100, height - footerHeight - 15, 0x888888);
    }

    private void renderScrollBar(DrawContext context) {
        int scrollBarX = width - 20;
        int scrollBarY = headerHeight + 10;
        int scrollBarHeight = contentHeight - 20;

        context.fill(scrollBarX, scrollBarY, scrollBarX + 8, scrollBarY + scrollBarHeight, 0x88888888);

        float scrollPercentage = (float) scrollOffset / maxScrollOffset;
        int cursorY = scrollBarY + (int) (scrollPercentage * (scrollBarHeight - 20));

        context.fill(scrollBarX + 1, cursorY, scrollBarX + 7, cursorY + 20, 0xFFCCCCCC);
    }

    @Override
    protected void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDefaultFooter(context);
    }

    @Override
    protected void renderOverridElements(DrawContext context, int mouseX, int mouseY, float delta) {}

    private record DocumentLine(String text, DocumentLineType type, int level, int color) {
    }

    private enum DocumentLineType {
        NORMAL,
        HEADER,
        LIST,
        CODE_BLOCK,
        ERROR
    }
}