package com.edgn.core.minecraft.ui.screens.terminal;

import com.edgn.uifw.templates.HtmlTemplate;
import com.edgn.uifw.elements.container.BaseContainer;
import com.edgn.uifw.elements.container.containers.FlexContainer;
import com.edgn.uifw.elements.container.containers.ListContainer;
import com.edgn.uifw.elements.item.items.TextFieldItem;
import com.edgn.uifw.elements.item.items.TextItem;
import com.edgn.uifw.components.TextComponent;
import com.edgn.uifw.css.StyleKey;
import com.edgn.core.minecraft.ui.screens.MainScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TerminalScreen extends HtmlTemplate {

    private final List<String> terminalHistory;
    private final List<String> commandHistory;
    private int historyIndex;
    private TextFieldItem inputField;
    private final CommandManager commandManager;
    private ListContainer terminalList;
    private TextItem promptText;
    private TextItem headerText;

    private static final int TERMINAL_TEXT = 0xFF00FF00;

    public TerminalScreen() {
        super(Text.literal("Terminal"), new MainScreen());
        this.terminalHistory = new ArrayList<>();
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;
        this.commandManager = new CommandManager();

        addToHistory("Minecraft Terminal v2.0 - Advanced Command System");
        addToHistory("Built with modular command architecture");
        addToHistory("Type 'help' for available commands or 'man <command>' for detailed help");
        addToHistory("");
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.P_2, StyleKey.FLEX_ROW, StyleKey.ITEMS_CENTER);

        String headerTextContent = "┌─ Minecraft Terminal ─ " + getCurrentTime() + " ─┐";
        headerText = new TextItem(uiSystem, 0, 0, width, headerHeight, headerTextContent)
                .color(TERMINAL_TEXT)
                .shadow()
                .setZIndex(100);

        header.add(headerText);
        return header;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, headerHeight, width, contentHeight)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.FLEX_COLUMN, StyleKey.P_1, StyleKey.GAP_2)
                .setZIndex(1);

        terminalList = new ListContainer(uiSystem, 0, 0, width - 20, 100)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.ROUNDED_SM, StyleKey.FLEX_GROW_1)
                .setItemHeight(12)
                .setItemSpacing(0)
                .setAlwaysShowScrollbar(false)
                .setPixelScrolling(true)
                .setSmoothScrolling(true)
                .setZIndex(10);

        updateTerminalDisplay();

        FlexContainer inputArea = new FlexContainer(uiSystem, 0, 0, width - 20, 50)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.FLEX_ROW, StyleKey.ITEMS_CENTER, StyleKey.GAP_2, StyleKey.P_2, StyleKey.FLEX_GROW_0)
                .setZIndex(20);

        String promptTextContent = getPrompt();
        promptText = new TextItem(uiSystem, 0, 0, 200, 35, promptTextContent)
                .color(TERMINAL_TEXT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .addClass(StyleKey.FLEX_GROW_0)
                .setZIndex(25);

        inputField = new TextFieldItem(uiSystem, 0, 0, 300, 35)
                .addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_SM, StyleKey.P_2, StyleKey.FLEX_GROW_1)
                .setTextStyle(new TextComponent("", textRenderer).color(TERMINAL_TEXT))
                .setPlaceholder(new TextComponent("Enter command...", textRenderer).color(0xFF666666))
                .onEnterPressed(this::executeCommand)
                .onTextChanged(this::onInputChanged)
                .setZIndex(30);

        inputArea.add(promptText).add(inputField);
        return content.add(terminalList).add(inputArea);
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer footer = new FlexContainer(uiSystem, 0, height - footerHeight, width, footerHeight)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.P_2, StyleKey.FLEX_ROW, StyleKey.ITEMS_CENTER);

        String footerTextContent = "└─ Use ↑↓ for history, Mouse wheel/drag to scroll, Ctrl+C to clear, Ctrl+L to clear screen ─┘";

        TextItem footerText = new TextItem(uiSystem, 0, 0, width, footerHeight, footerTextContent)
                .color(adjustBrightness())
                .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .setSafetyMargin(8)
                .setZIndex(100);

        footer.add(footerText);
        return footer;
    }

    @Override
    public void tick() {
        super.tick();
        updateDynamicTexts();
    }

    private void updateDynamicTexts() {
        String newHeaderText = "┌─ Minecraft Terminal ─ " + getCurrentTime() + " ─┐";
        if (headerText != null && !headerText.getText().equals(newHeaderText)) {
            headerText.setText(newHeaderText);
        }

        String newPromptText = getPrompt();
        if (promptText != null && !promptText.getText().equals(newPromptText)) {
            promptText.setText(newPromptText);

            int promptWidth = textRenderer.getWidth(newPromptText);
            inputField.setWidth(width - promptWidth - 50);
        }
    }

    private void updateTerminalDisplay() {
        terminalList.clearItems();

        for (String line : terminalHistory) {
            TerminalLineItem lineItem = new TerminalLineItem(uiSystem, 0, 0,
                    terminalList.getWidth() - 20, 12, line);
            terminalList.add(lineItem);
        }

        terminalList.scrollToBottom();
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            performAutoComplete();
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_C && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            inputField.setText("");
            addToHistory(getPrompt() + "^C");
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_L && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            terminalHistory.clear();
            updateTerminalDisplay();
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            navigateHistory(-1);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            navigateHistory(1);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            terminalList.scrollBy(5);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            terminalList.scrollBy(-5);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_HOME && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            terminalList.scrollToTop();
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_END && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            terminalList.scrollToBottom();
        }
    }

    private void executeCommand() {
        String command = inputField.getText().trim();
        String fullCommand = getPrompt() + command;

        addToHistory(fullCommand);

        if (!command.isEmpty()) {
            commandHistory.add(command);
            historyIndex = -1;

            List<String> output = commandManager.executeCommand(command);
            for (String line : output) {
                if (line.equals("__CLEAR_SCREEN__")) {
                    terminalHistory.clear();
                    updateTerminalDisplay();
                } else if (line.equals("__EXIT_TERMINAL__")) {
                    this.close();
                    return;
                } else {
                    addToHistory(line);
                }
            }
        }

        inputField.setText("");
        updateTerminalDisplay();
    }

    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) return;

        if (direction < 0) {
            if (historyIndex == -1) {
                historyIndex = commandHistory.size() - 1;
            } else if (historyIndex > 0) {
                historyIndex--;
            }
        } else {
            if (historyIndex == -1) return;
            if (historyIndex < commandHistory.size() - 1) {
                historyIndex++;
            } else {
                historyIndex = -1;
                inputField.setText("");
                return;
            }
        }

        if (historyIndex >= 0 && historyIndex < commandHistory.size()) {
            inputField.setText(commandHistory.get(historyIndex));
        }
    }

    private void addToHistory(String line) {
        terminalHistory.add(line);

        if (terminalHistory.size() > 1000) {
            terminalHistory.removeFirst();
        }

        if (terminalList != null) {
            updateTerminalDisplay();
        }
    }

    private String getPrompt() {
        TerminalContext context = commandManager.getContext();
        String user = context.getEnvironment().get("USER");
        String currentDir = context.getCurrentDirectory();

        if (currentDir.equals(context.getEnvironment().get("HOME"))) {
            currentDir = "~";
        } else if (currentDir.startsWith("/home/" + user + "/")) {
            currentDir = "~" + currentDir.substring(("/home/" + user).length());
        }

        return user + "@minecraft:" + currentDir + "$ ";
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private void onInputChanged(String newText) {}

    private void performAutoComplete() {
        String currentInput = inputField.getText();
        if (currentInput.isEmpty()) return;

        String[] parts = currentInput.split("\\s+");
        if (parts.length == 0) return;

        String commandName = parts[0].toLowerCase();
        List<String> completions;

        if (parts.length == 1) {
            completions = getCommandCompletions(commandName);
        } else {
            completions = getArgumentCompletions(commandName,
                    Arrays.copyOfRange(parts, 1, parts.length));
        }

        if (completions.isEmpty()) {
            return;
        }

        if (completions.size() == 1) {
            String completion = completions.getFirst();
            if (parts.length == 1) {
                inputField.setText(completion + " ");
            } else {
                StringBuilder newCommand = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    newCommand.append(parts[i]).append(" ");
                }
                newCommand.append(completion);
                inputField.setText(newCommand.toString());
            }
        } else {
            addToHistory(getPrompt() + currentInput);

            String commonPrefix = findCommonPrefix(completions);
            if (!commonPrefix.isEmpty() && !commonPrefix.equals(parts[parts.length - 1])) {
                StringBuilder newCommand = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    newCommand.append(parts[i]).append(" ");
                }
                newCommand.append(commonPrefix);
                inputField.setText(newCommand.toString());
            }

            StringBuilder options = new StringBuilder();
            for (String completion : completions) {
                options.append(completion).append("  ");
            }
            addToHistory(options.toString());
            addToHistory("");
        }
    }

    private List<String> getCommandCompletions(String prefix) {
        List<String> completions = new ArrayList<>();

        for (String commandName : commandManager.getCommands().keySet()) {
            if (commandName.startsWith(prefix)) {
                completions.add(commandName);
            }
        }

        for (String alias : commandManager.getContext().getAliases().keySet()) {
            if (alias.startsWith(prefix)) {
                completions.add(alias);
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private List<String> getArgumentCompletions(String commandName, String[] args) {
        String resolvedCommand = commandManager.getContext().getAliases()
                .getOrDefault(commandName, commandName);

        TerminalCommand command = commandManager.getCommands().get(resolvedCommand);
        if (command == null) {
            return new ArrayList<>();
        }

        return command.getCompletions(commandManager.getContext(), args);
    }

    private String findCommonPrefix(List<String> strings) {
        if (strings.isEmpty()) return "";
        if (strings.size() == 1) return strings.getFirst();

        String first = strings.getFirst();
        int commonLength = 0;

        for (int i = 0; i < first.length(); i++) {
            char c = first.charAt(i);
            boolean allMatch = true;

            for (String str : strings) {
                if (i >= str.length() || str.charAt(i) != c) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                commonLength++;
            } else {
                break;
            }
        }

        return first.substring(0, commonLength);
    }

    private int adjustBrightness() {
        int g = (TerminalScreen.TERMINAL_TEXT >> 8) & 0xFF;
        int b = TerminalScreen.TERMINAL_TEXT & 0xFF;

        g = (int) (g * (float) 0.6);

        return 0xFF000000 | (0) | (g << 8) | b;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}