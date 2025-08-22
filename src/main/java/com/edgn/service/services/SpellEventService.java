package com.edgn.service.services;

import com.edgn.Main;
import com.edgn.event.EventManager;
import com.edgn.event.listeners.OverlayMessageListener;
import com.edgn.service.IService;
import com.edgn.event.wynncraft.spells.*;

import java.util.List;

public class SpellEventService implements OverlayMessageListener, IService {

    private static final List<String> RIGHT_CLICKS = List.of("\uE101", "\uE104");
    private static final List<String> LEFT_CLICKS = List.of("\uE100", "\uE103");
    private static final String SEPARATORS = "\uE106";
    private static final String SPACE = " ";

    @Override
    public void init() {
        Main.EVENT_MANAGER.add(OverlayMessageListener.class, this);
    }

    @Override
    public void onOverlayMessage(OverlayMessageEvent event) {
        String messageString = event.getMessage().getString();
        if (containsFirstSpell(messageString)) {
            this.onFirstSpell();
            this.onSpell();
        } else if (containsSecondSpell(messageString)) {
            this.onSecondSpell();
            this.onSpell();
        } else if (containsThirdSpell(messageString)) {
            this.onThirdSpell();
            this.onSpell();
        } else if (containsFourthSpell(messageString)) {
            this.onFourthSpell();
            this.onSpell();
        }
    }

    private void onSpell() {
        SpellListener.SpellEvent event = new SpellListener.SpellEvent();
        EventManager.fire(event);
    }

    private void onFirstSpell() {
        FirstSpellListener.FirstSpellEvent event = new FirstSpellListener.FirstSpellEvent();
        EventManager.fire(event);
    }

    private void onSecondSpell() {
        SecondSpellListener.SecondSpellEvent event = new SecondSpellListener.SecondSpellEvent();
        EventManager.fire(event);
    }

    private void onThirdSpell() {
        ThirdSpellListener.ThirdSpellEvent event = new ThirdSpellListener.ThirdSpellEvent();
        EventManager.fire(event);
    }


    private void onFourthSpell() {
        FourthSpellListener.FourthSpellEvent event = new FourthSpellListener.FourthSpellEvent();
        EventManager.fire(event);
    }

    public boolean containsFirstSpell(String message) {
        return checkSpellPattern(message, 0);
    }

    public boolean containsSecondSpell(String message) {
        return checkSpellPattern(message, 1);
    }

    public boolean containsThirdSpell(String message) {
        return checkSpellPattern(message, 2);
    }

    public boolean containsFourthSpell(String message) {
        return checkSpellPattern(message, 3);
    }

    private boolean checkSpellPattern(String message, int patternType) {
        for (int i = 0; i < LEFT_CLICKS.size(); i++) {
            String left = LEFT_CLICKS.get(i);
            String right = RIGHT_CLICKS.get(i);

            String spell1, spell2;

            switch (patternType) {
                case 0:
                    spell1 = right + SPACE + SEPARATORS + SPACE + left + SPACE + SEPARATORS + SPACE + right;
                    spell2 = left + SPACE + SEPARATORS + SPACE + right + SPACE + SEPARATORS + SPACE + left;
                    break;
                case 1:
                    spell1 = right + SPACE + SEPARATORS + SPACE + right + SPACE + SEPARATORS + SPACE + right;
                    spell2 = left + SPACE + SEPARATORS + SPACE + left + SPACE + SEPARATORS + SPACE + left;
                    break;
                case 2:
                    spell1 = right + SPACE + SEPARATORS + SPACE + left + SPACE + SEPARATORS + SPACE + left;
                    spell2 = left + SPACE + SEPARATORS + SPACE + right + SPACE + SEPARATORS + SPACE + right;
                    break;
                case 3:
                    spell1 = right + SPACE + SEPARATORS + SPACE + right + SPACE + SEPARATORS + SPACE + left;
                    spell2 = left + SPACE + SEPARATORS + SPACE + left + SPACE + SEPARATORS + SPACE + right;
                    break;

                default:
                    return false;
            }

            if (message.contains(spell1) || message.contains(spell2)) {
                return true;
            }
        }
        return false;
    }
}