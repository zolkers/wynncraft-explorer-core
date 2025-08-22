package com.edgn.core.module.basic.modules.chat;

import com.edgn.Main;
import com.edgn.core.config.ConfigManager;
import com.edgn.event.listeners.ChatMessageAddListener;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.SettingsGroup;
import com.google.gson.annotations.Expose;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "Freaky Message BEGONE",
        description = "Hides any player message containing specific words.",
        category = ModuleCategory.CHAT,
        authors = {"EDGN"},
        version = "1.0.1"
)
public class FreakyMessageBegoneModule extends AbstractModule implements ChatMessageAddListener {

    private static final Pattern PLAYER_MESSAGE_PATTERN = Pattern.compile("(\\S{3,16})[>:]\\s(.*)");

    @ConfigManager.SaveField
    @Expose
    private List<String> bannedWords = new ArrayList<>();

    private List<Pattern> compiledPatterns = Collections.emptyList();

    private enum Mode {
        REMOVE_MESSAGE_MODE("Remove entire message"),
        REPLACE_WORDS_MODE("Replace words with spaces");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    @ConfigManager.SaveField
    @Expose
    private Mode filterMode = Mode.REMOVE_MESSAGE_MODE;

    @ConfigManager.SaveField
    @Expose
    private boolean logOverlay = true;

    public FreakyMessageBegoneModule() {
        super("Freaky Message BEGONE");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeSettings() {
        SettingsGroup mainGroup = new SettingsGroup("General Panel", "General settings");

        mainGroup.addEnum("Filter Mode", "Choose how to handle filtered messages", filterMode)
                .setOnValueChanged((oldValue, newValue) -> this.filterMode = (Mode) newValue);

        mainGroup.addList("Banned Words", "Hides any player message containing these words. Use * as a wildcard.", bannedWords, s -> s, Object::toString)
                .setOnValueChanged((oldValue, newValue) -> this.bannedWords = (List<String>) newValue);

        mainGroup.addBoolean("Log filtered message in the overlay (advised)", "boolean", logOverlay)
                .setOnValueChanged((oldValue, newValue) -> this.logOverlay = (Boolean) newValue);

        this.settingsGroups.add(mainGroup);
    }

    private void compileBannedWordPatterns() {
        if (this.bannedWords == null || this.bannedWords.isEmpty()) {
            this.compiledPatterns = Collections.emptyList();
            return;
        }

        this.compiledPatterns = this.bannedWords.stream()
                .filter(word -> word != null && !word.trim().isEmpty())
                .map(this::convertWildcardToRegex)
                .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
    }

    private String convertWildcardToRegex(String wildcard) {
        String regex = Pattern.quote(wildcard);
        if (!wildcard.startsWith("*")) regex = "\\b" + regex;
        if (!wildcard.endsWith("*")) regex = regex + "\\b";
        return regex.replace("*", "\\E.\\*\\Q");
    }

    @Override
    public void onAddMessage(ChatMessageAddListener.ChatMessageAddEvent event) {

        if (compiledPatterns.isEmpty()) {
            return;
        }

        Text originalText = event.getOriginalMessage();
        String messageAsString = originalText.getString();

        Matcher matcher = PLAYER_MESSAGE_PATTERN.matcher(messageAsString);

        if (matcher.find()) {
            String playerName = matcher.group(1);
            String messageContent = matcher.group(2);
            boolean hasFilteredContent = false;
            String filteredContent = messageContent;

            for (Pattern pattern : compiledPatterns) {
                Matcher contentMatcher = pattern.matcher(filteredContent);
                if (contentMatcher.find()) {
                    hasFilteredContent = true;

                    if (filterMode == Mode.REMOVE_MESSAGE_MODE) {
                        if (logOverlay) {
                            Main.OVERLAY_MANAGER.getLoggerOverlay().success("Removed a freaky message: " + messageContent, false);
                        }
                        event.cancel();
                        return;
                    } else if (filterMode == Mode.REPLACE_WORDS_MODE) {
                        filteredContent = contentMatcher.replaceAll(match -> "\u00A0".repeat(match.group().length()));
                    }
                }
            }

            if (filterMode == Mode.REPLACE_WORDS_MODE && hasFilteredContent) {
                MutableText modifiedMessage = replaceTextRecursively(originalText, messageContent, filteredContent);

                if (modifiedMessage != null) {
                    if (logOverlay) {
                        Main.OVERLAY_MANAGER.getLoggerOverlay().success("Replaced words in message from " + playerName, false);
                    }
                    event.setNewMessage(modifiedMessage);
                }
            }
        }
    }

    private MutableText replaceTextRecursively(Text source, String toReplace, String replacement) {
        if (source.getString().equals(toReplace)) {
            return Text.literal(replacement).fillStyle(source.getStyle());
        }

        MutableText newCopy = source.copy();
        newCopy.getSiblings().clear();
        boolean wasAnythingReplaced = false;

        for (Text sibling : source.getSiblings()) {
            MutableText processedSibling = replaceTextRecursively(sibling, toReplace, replacement);
            if (processedSibling != null) {
                newCopy.append(processedSibling);
                wasAnythingReplaced = true;
            } else {
                newCopy.append(sibling);
            }
        }

        return wasAnythingReplaced ? newCopy : null;
    }

    @Override
    protected void onEnable() {
        Main.EVENT_MANAGER.add(ChatMessageAddListener.class, this);
    }

    @Override
    protected void onDisable() {
        Main.EVENT_MANAGER.remove(ChatMessageAddListener.class, this);
    }

    @Override
    public void onSettingsChanged() {
        compileBannedWordPatterns();
    }
}