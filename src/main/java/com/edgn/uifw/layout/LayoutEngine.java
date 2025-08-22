package com.edgn.uifw.layout;

import com.edgn.uifw.elements.UIElement;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LayoutEngine {

    public static void applyElementStyles(UIElement element) {
        LayoutConstraints constraints = element.getConstraints();
        if (constraints == null) return;

        element.updateConstraints();

        if (constraints.getWidthPercent() != null && element.getParent() != null) {
            int newWidth = (int)(element.getParent().getCalculatedWidth() * constraints.getWidthPercent());
            element.setWidth(Math.max(constraints.getMinWidth() != null ? constraints.getMinWidth() : 0,
                    Math.min(constraints.getMaxWidth() != null ? constraints.getMaxWidth() : Integer.MAX_VALUE, newWidth)));
        }

        if (constraints.getHeightPercent() != null && element.getParent() != null) {
            int newHeight = (int)(element.getParent().getCalculatedHeight() * constraints.getHeightPercent());
            element.setHeight(Math.max(constraints.getMinHeight() != null ? constraints.getMinHeight() : 0,
                    Math.min(constraints.getMaxHeight() != null ? constraints.getMaxHeight() : Integer.MAX_VALUE, newHeight)));
        }
    }

    public static void applyMargins(UIElement element, int parentX, int parentY, int parentWidth, int parentHeight) {
        element.updateConstraints();

        int marginTop = element.getMarginTop();
        int marginLeft = element.getMarginLeft();
        int marginRight = element.getMarginRight();
        int marginBottom = element.getMarginBottom();

        element.setX(parentX + marginLeft);
        element.setY(parentY + marginTop);

        int availableWidth = parentWidth - marginLeft - marginRight;
        int availableHeight = parentHeight - marginTop - marginBottom;

        if (element.getWidth() > availableWidth) {
            element.setWidth(Math.max(0, availableWidth));
        }
        if (element.getHeight() > availableHeight) {
            element.setHeight(Math.max(0, availableHeight));
        }
    }

    public static LayoutBox calculateContentBox(UIElement element) {
        element.updateConstraints();

        int paddingTop = element.getPaddingTop();
        int paddingRight = element.getPaddingRight();
        int paddingBottom = element.getPaddingBottom();
        int paddingLeft = element.getPaddingLeft();

        return new LayoutBox(
                element.getCalculatedX() + paddingLeft,
                element.getCalculatedY() + paddingTop,
                element.getCalculatedWidth() - paddingLeft - paddingRight,
                element.getCalculatedHeight() - paddingTop - paddingBottom
        );
    }

    public static List<UIElement> sortByZIndex(List<UIElement> elements) {
        return elements.stream()
                .sorted(Comparator.comparingInt(UIElement::getZIndex))
                .collect(Collectors.toList());
    }

    public static List<UIElement> sortByInteractionPriority(List<UIElement> elements, double mouseX, double mouseY) {
        return elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(element -> element.canInteract(mouseX, mouseY))
                .sorted((a, b) -> {
                    // Z-Index priority
                    int zCompare = Integer.compare(b.getZIndex(), a.getZIndex());
                    if (zCompare != 0) return zCompare;

                    // Focus priority
                    if (a.isFocused() && !b.isFocused()) return -1;
                    if (!a.isFocused() && b.isFocused()) return 1;

                    // Smaller area priority (more specific)
                    int aArea = a.getCalculatedWidth() * a.getCalculatedHeight();
                    int bArea = b.getCalculatedWidth() * b.getCalculatedHeight();
                    return Integer.compare(aArea, bArea);
                })
                .collect(Collectors.toList());
    }

    public record LayoutBox(int x, int y, int width, int height) {
        public boolean contains(int pointX, int pointY) {
            return pointX >= this.x && pointX < (this.x + this.width) && pointY >= this.y && pointY < (this.y + this.height);
        }

        public boolean intersects(LayoutBox other) {
            return x < other.x + other.width && x + width > other.x &&
                    y < other.y + other.height && y + height > other.y;
        }

        public LayoutBox intersection(LayoutBox other) {
            if (!intersects(other)) return new LayoutBox(0, 0, 0, 0);

            int newX = Math.max(x, other.x);
            int newY = Math.max(y, other.y);
            int newWidth = Math.min(x + width, other.x + other.width) - newX;
            int newHeight = Math.min(y + height, other.y + other.height) - newY;

            return new LayoutBox(newX, newY, Math.max(0, newWidth), Math.max(0, newHeight));
        }
    }
}