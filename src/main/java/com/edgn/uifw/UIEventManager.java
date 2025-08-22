package com.edgn.uifw;

import com.edgn.uifw.elements.UIElement;
import com.edgn.uifw.elements.item.items.ScrollbarItem;
import java.util.*;
import java.util.stream.Collectors;

public class UIEventManager {
    private final Set<UIElement> elements = new HashSet<>();
    private UIElement focusedElement = null;
    private UIElement hoveredElement = null;
    private long lastInteractionTime = 0;

    public void registerElement(UIElement element) {
        elements.add(element);
    }

    public void unregisterElement(UIElement element) {
        elements.remove(element);
        if (focusedElement == element) focusedElement = null;
        if (hoveredElement == element) hoveredElement = null;
    }

    // Méthode utilitaire pour trier les éléments par Z-Index et priorité d'interaction
    private List<UIElement> getSortedInteractableElements(double mouseX, double mouseY) {
        return elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(element -> element.canInteract(mouseX, mouseY))
                .sorted((a, b) -> {
                    // Priorité 1: Z-Index (plus élevé = plus prioritaire)
                    int zCompare = Integer.compare(b.getZIndex(), a.getZIndex());
                    if (zCompare != 0) return zCompare;

                    // Priorité 2: Éléments focusés
                    if (a.isFocused() && !b.isFocused()) return -1;
                    if (!a.isFocused() && b.isFocused()) return 1;

                    // Priorité 3: Éléments plus petits (plus spécifiques)
                    int aArea = a.getCalculatedWidth() * a.getCalculatedHeight();
                    int bArea = b.getCalculatedWidth() * b.getCalculatedHeight();
                    return Integer.compare(aArea, bArea);
                })
                .collect(Collectors.toList());
    }

    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        lastInteractionTime = System.currentTimeMillis();

        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseClick(mouseX, mouseY, button)) {
                setFocus(element);
                return true;
            }
        }

        setFocus(null);
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        // Gestion spéciale des scrollbars
        for (UIElement element : elements) {
            if (element instanceof ScrollbarItem scrollbar && scrollbar.isVisible() && scrollbar.isEnabled()) {
                if (scrollbar.handleMouseRelease(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }

        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        // Gestion spéciale des scrollbars
        for (UIElement element : elements) {
            if (element instanceof ScrollbarItem scrollbar && scrollbar.isVisible() && scrollbar.isEnabled()) {
                if (scrollbar.handleMouseScroll(mouseX, mouseY, scrollDelta)) {
                    return true;
                }
            }
        }

        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseScroll(mouseX, mouseY, scrollDelta)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Les éléments en cours de drag ont la priorité absolue
        List<UIElement> sortedElements = elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .sorted((a, b) -> Integer.compare(b.getZIndex(), a.getZIndex()))
                .collect(Collectors.toList());

        for (UIElement element : sortedElements) {
            if (element.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    public void onMouseMove(double mouseX, double mouseY) {
        UIElement newHovered = null;

        // Trouve l'élément avec la plus haute priorité d'interaction
        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);
        if (!sortedElements.isEmpty()) {
            newHovered = sortedElements.get(0);
        }

        // Gestion du changement de hover
        if (hoveredElement != newHovered) {
            if (hoveredElement != null) {
                hoveredElement.onMouseLeave();
            }
            if (newHovered != null) {
                newHovered.onMouseEnter();
            }
            hoveredElement = newHovered;
        }

        // Notification de mouvement à tous les éléments
        for (UIElement element : elements) {
            element.onMouseMove(mouseX, mouseY);
        }
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return focusedElement != null && focusedElement.onKeyPress(keyCode, scanCode, modifiers);
    }

    public boolean onCharTyped(char chr, int modifiers) {
        return focusedElement != null && focusedElement.onCharTyped(chr, modifiers);
    }

    public void setFocus(UIElement element) {
        if (focusedElement != element) {
            if (focusedElement != null) {
                focusedElement.onFocusLost();
            }
            focusedElement = element;
            if (element != null) {
                element.onFocusGained();
            }
        }
    }

    public void resetAllElements() {
        if (focusedElement != null) {
            focusedElement.onFocusLost();
            focusedElement = null;
        }

        if (hoveredElement != null) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }

        for (UIElement element : elements) {
            element.updateConstraints();
        }

        lastInteractionTime = 0;
    }

    public void cleanup() {
        elements.clear();
        focusedElement = null;
        hoveredElement = null;
        lastInteractionTime = 0;
    }

    public UIElement getTopElementAt(double mouseX, double mouseY) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        return sorted.isEmpty() ? null : sorted.getFirst();
    }

    public List<UIElement> getAllElementsAt(double mouseX, double mouseY) {
        return getSortedInteractableElements(mouseX, mouseY);
    }

    public void updateAllConstraints() {
        for (UIElement element : elements) {
            element.updateConstraints();
        }
    }

    // Getters
    public UIElement getFocusedElement() { return focusedElement; }
    public UIElement getHoveredElement() { return hoveredElement; }
    public long getLastInteractionTime() { return lastInteractionTime; }
    public Set<UIElement> getAllElements() { return new HashSet<>(elements); }
}