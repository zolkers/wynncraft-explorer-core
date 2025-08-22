package com.edgn.core.minecraft.ui.overlays.overlays;

import com.edgn.Main;
import com.edgn.event.listeners.MouseScrollingListener;
import com.edgn.core.minecraft.ui.overlays.AbstractOverlay;
import com.edgn.core.minecraft.render.utils.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LoggerOverlay extends AbstractOverlay implements MouseScrollingListener {

    private final List<LogMessage> logMessages = new ArrayList<>();
    private int scrollOffset = 0;

    private boolean isScrollbarDragging = false;
    private boolean wasMouseDown = false;

    private static final int INFO_COLOR = 0xFFFFFFFF;
    private static final int WARN_COLOR = 0xFFFFAA00;
    private static final int ERROR_COLOR = 0xFFFF5555;
    private static final int ACTION_COLOR = ColorUtil.INSTANCE.BLUE;
    private static final int SUCCESS_COLOR = ColorUtil.INSTANCE.GREEN;
    private static final int AI_COLOR = ColorUtil.INSTANCE.PURPLE;
    private static final int LINE_NUMBER_COLOR = 0xAAAAAA;

    private int lineNumber = 0;

    public record LogMessage(int lineNum, String message, LogLevel level) {
        public int getColor() {
            return switch (level) {
                case WARN -> WARN_COLOR;
                case ERROR -> ERROR_COLOR;
                case ACTION -> ACTION_COLOR;
                case SUCCESS -> SUCCESS_COLOR;
                case AI -> AI_COLOR;
                default -> INFO_COLOR;
            };
        }

        public String getPrefix() {
            return switch (level) {
                case INFO -> "INFO: ";
                case WARN -> "WARN: ";
                case ERROR -> "ERROR: ";
                case ACTION -> "ACTION: ";
                case SUCCESS -> "SUCCESS: ";
                case AI -> "AI";
            };
        }
    }

    public enum LogLevel {
        INFO,
        WARN,
        ERROR,
        ACTION,
        SUCCESS,
        AI
    }

    public void info(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.INFO);
        if(shouldPrint) Main.LOGGER.info(message);
    }

    public void warn(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.WARN);
        if(shouldPrint) Main.LOGGER.warn(message);
    }

    public void error(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.ERROR);
        if(shouldPrint) Main.LOGGER.error(message);
    }

    public void action(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.ACTION);
        if(shouldPrint) Main.LOGGER.info(message);
    }

    public void success(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.SUCCESS);
        if(shouldPrint) Main.LOGGER.info(message);
    }

    public void ai(String message, boolean shouldPrint) {
        addLogMessage(message, LogLevel.AI);
        if(shouldPrint) Main.LOGGER.info(message);
    }

    private void addLogMessage(String message, LogLevel level) {
        lineNumber++;
        logMessages.addFirst(new LogMessage(lineNumber, message, level));
        int MAX_LOGS = 50000;
        if (logMessages.size() > MAX_LOGS) {
            logMessages.removeLast();
        }
        scrollOffset = 0;
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!visible) return;

        int bgWidth = client.getWindow().getScaledWidth();
        int bgHeight = client.getWindow().getScaledHeight();

        context.fill(0, 0, bgWidth, bgHeight, 0x80000000);

        int lineHeight = 15;
        int maxVisibleLines = (bgHeight - 10) / lineHeight;
        int totalLines = logMessages.size();

        int maxScrollOffset = Math.max(0, totalLines - maxVisibleLines);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

        int y = 5;
        for (int i = scrollOffset; i < Math.min(totalLines, scrollOffset + maxVisibleLines); i++) {
            LogMessage logMsg = logMessages.get(i);
            if(logMsg == null) logMsg = new LogMessage(0, "CRITICAL LOGGER ERROR, THE MESSAGE WAS NULL", LogLevel.ERROR);

            String lineNumText = logMsg.lineNum + " - ";
            int lineNumWidth = client.textRenderer.getWidth(lineNumText);
            context.drawText(client.textRenderer, Text.of(lineNumText), 5, y, LINE_NUMBER_COLOR, true);

            String messageText = logMsg.getPrefix() + logMsg.message;
            context.drawText(client.textRenderer, Text.of(messageText), 5 + lineNumWidth, y, logMsg.getColor(), true);

            y += lineHeight;
        }

        if (totalLines > maxVisibleLines) {
            renderScrollbar(context, bgWidth, bgHeight, maxVisibleLines, totalLines);
        }

        handleMouseInput();
    }

    @Override
    public void initialize() {
        Main.EVENT_MANAGER.add(MouseScrollingListener.class, this);
    }

    private void renderScrollbar(DrawContext context, int bgWidth, int bgHeight, int visibleLines, int totalLines) {
        int scrollbarWidth = 8;
        int scrollbarX = bgWidth - scrollbarWidth - 5;
        int scrollbarY = 5;
        int scrollbarHeight = bgHeight - 10;

        int SCROLLBAR_BG_COLOR = 0x40000000;
        context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, SCROLLBAR_BG_COLOR);

        float thumbRatio = (float) visibleLines / totalLines;
        int thumbHeight = Math.max(20, (int) (thumbRatio * scrollbarHeight));
        int thumbY = scrollbarY;
        if (totalLines > visibleLines) {
            thumbY += (int) ((float) scrollOffset / (totalLines - visibleLines) * (scrollbarHeight - thumbHeight));
        }

        MinecraftClient client = MinecraftClient.getInstance();
        double mouseX = client.mouse.getX() / client.getWindow().getScaleFactor();
        double mouseY = client.mouse.getY() / client.getWindow().getScaleFactor();

        boolean isHoveringThumb = mouseX >= scrollbarX &&
                mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= thumbY &&
                mouseY <= thumbY + thumbHeight;

        int SCROLLBAR_COLOR = 0x80AAAAAA;
        int SCROLLBAR_HOVER_COLOR = 0xAAFFFFFF;
        context.fill(scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight,
                isHoveringThumb || isScrollbarDragging ? SCROLLBAR_HOVER_COLOR : SCROLLBAR_COLOR);
    }

    private void handleMouseInput() {
        if (!visible) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int totalLines = logMessages.size();
        int bgHeight = client.getWindow().getScaledHeight();
        int lineHeight = 15;
        int maxVisibleLines = (bgHeight - 10) / lineHeight;

        if (totalLines <= maxVisibleLines) return;

        double mouseX = client.mouse.getX() / client.getWindow().getScaleFactor();
        double mouseY = client.mouse.getY() / client.getWindow().getScaleFactor();
        boolean isLeftButtonDown = client.mouse.wasLeftButtonClicked();

        int scrollbarWidth = 8;
        int scrollbarX = client.getWindow().getScaledWidth() - scrollbarWidth - 5;
        int scrollbarY = 5;
        int scrollbarHeight = bgHeight - 10;

        float thumbRatio = (float) maxVisibleLines / totalLines;
        int thumbHeight = Math.max(20, (int) (thumbRatio * scrollbarHeight));
        int thumbY = scrollbarY;
        thumbY += (int) ((float) scrollOffset / (totalLines - maxVisibleLines) * (scrollbarHeight - thumbHeight));

        boolean isOverScrollbar = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight;
        boolean isOverScrollThumb = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= thumbY && mouseY <= thumbY + thumbHeight;

        if (isLeftButtonDown && !wasMouseDown) {
            wasMouseDown = true;
            if (isOverScrollbar) {
                isScrollbarDragging = true;
                if (!isOverScrollThumb) {
                    float clickPosition = (float)(mouseY - scrollbarY - (double) thumbHeight /2) / (scrollbarHeight - thumbHeight);
                    clickPosition = Math.max(0, Math.min(1, clickPosition));
                    scrollOffset = Math.max(0, Math.min((int)(clickPosition * (totalLines - maxVisibleLines)),
                            totalLines - maxVisibleLines));
                }
            }
        }
        else if (!isLeftButtonDown && wasMouseDown) {
            wasMouseDown = false;
            isScrollbarDragging = false;
        }

        if (isScrollbarDragging && isLeftButtonDown) {
            float dragPosition = (float)(mouseY - scrollbarY - (double) thumbHeight /2) / (scrollbarHeight - thumbHeight);
            dragPosition = Math.max(0, Math.min(1, dragPosition));
            scrollOffset = Math.max(0, Math.min((int)(dragPosition * (totalLines - maxVisibleLines)),
                    totalLines - maxVisibleLines));
        }
    }

    @Override
    public void onMouseScroll(MouseScrollingEvent event) {
        if (!visible) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int totalLines = logMessages.size();
        int bgHeight = client.getWindow().getScaledHeight();
        int lineHeight = 15;
        int maxVisibleLines = (bgHeight - 10) / lineHeight;

        if (totalLines <= maxVisibleLines) return;

        int scrollAmount = -(int)Math.signum(event.getVertical()) * 3;
        scrollOffset = Math.max(0, Math.min(totalLines - maxVisibleLines, scrollOffset + scrollAmount));
    }

    public void clear() {
        logMessages.clear();
        scrollOffset = 0;
    }
}