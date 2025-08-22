package com.edgn.uifw.elements.item.items;

import com.edgn.uifw.elements.item.BaseItem;
import com.edgn.uifw.utils.Render2D;
import com.edgn.uifw.css.UIStyleSystem;
import com.edgn.uifw.css.StyleKey;
import com.edgn.uifw.css.rules.Shadow;
import com.edgn.uifw.layout.LayoutConstraints;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class ImageItem extends BaseItem {
    public enum ScaleMode {
        STRETCH,    // Stretch to fill bounds, may distort
        FIT,        // Scale to fit within bounds, maintain aspect ratio
        FILL,       // Scale to fill bounds, maintain aspect ratio, may crop
        CENTER,     // Display at original size, centered
        TILE        // Repeat image to fill bounds
    }

    public enum ImageSource {
        IDENTIFIER,  // Minecraft resource identifier
        URL,         // Web URL
        FILE_PATH,   // Local file path
        BYTES        // Raw byte array
    }

    private static final ConcurrentHashMap<String, Identifier> imageCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CompletableFuture<Identifier>> loadingImages = new ConcurrentHashMap<>();

    private Identifier imageId;
    private String imagePath;
    private String imageUrl;
    private byte[] imageBytes;
    private ImageSource imageSource = ImageSource.IDENTIFIER;

    private ScaleMode scaleMode = ScaleMode.FIT;
    private boolean isClickable = false;
    private boolean showBorder = false;
    private boolean showLoadingPlaceholder = true;
    private boolean showErrorPlaceholder = true;
    private boolean allowCaching = true;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private boolean imageLoaded = false;
    private boolean imageError = false;
    private boolean isLoading = false;
    private float opacity = 1.0f;
    private int rotation = 0; // 0, 1, 2, 3 for 0°, 90°, 180°, 270°
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    private Color tintColor = Color.WHITE;

    private int borderColor = 0xFF888888;
    private int borderThickness = 1;

    private String loadingText = "Loading...";
    private String errorText = "Failed to load image";
    private int placeholderColor = 0xFF444444;
    private int placeholderTextColor = 0xFFAAAAAA;

    private int loadingDots = 0;
    private long lastLoadingUpdate = 0;
    private static final long LOADING_UPDATE_INTERVAL = 500;

    public ImageItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.ROUNDED_SM);
    }

    public ImageItem(UIStyleSystem styleSystem, int x, int y, int width, int height, Identifier imageId) {
        this(styleSystem, x, y, width, height);
        setImage(imageId);
    }

    public ImageItem(UIStyleSystem styleSystem, int x, int y, int width, int height, String imagePath) {
        this(styleSystem, x, y, width, height);
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            setImageUrl(imagePath);
        } else {
            setImagePath(imagePath);
        }
    }

    public ImageItem setImage(Identifier imageId) {
        if (this.imageId != imageId) {
            clearCurrentImage();
            this.imageId = imageId;
            this.imageSource = ImageSource.IDENTIFIER;

            if (imageId != null) {
                this.imageLoaded = true;
                this.imageWidth = 64;  // Default texture size
                this.imageHeight = 64; // Default texture size
            }
        }
        return this;
    }

    public ImageItem setImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            clearCurrentImage();
            return this;
        }

        if (!url.equals(this.imageUrl)) {
            clearCurrentImage();
            this.imageUrl = url;
            this.imageSource = ImageSource.URL;
            loadImageFromUrl(url);
        }
        return this;
    }

    public ImageItem setImagePath(String path) {
        if (path == null || path.isEmpty()) {
            clearCurrentImage();
            return this;
        }

        if (!path.equals(this.imagePath)) {
            clearCurrentImage();
            this.imagePath = path;
            this.imageSource = ImageSource.FILE_PATH;
            loadImageFromFile(path);
        }
        return this;
    }

    public ImageItem setImageBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            clearCurrentImage();
            return this;
        }

        clearCurrentImage();
        this.imageBytes = bytes.clone();
        this.imageSource = ImageSource.BYTES;
        loadImageFromBytes(bytes);
        return this;
    }

    public ImageItem setAllowCaching(boolean allowCaching) {
        this.allowCaching = allowCaching;
        return this;
    }

    private void clearCurrentImage() {
        this.imageId = null;
        this.imagePath = null;
        this.imageUrl = null;
        this.imageBytes = null;
        this.imageLoaded = false;
        this.imageError = false;
        this.isLoading = false;
        this.imageWidth = 0;
        this.imageHeight = 0;
    }

    private void loadImageFromUrl(String url) {
        if (url == null || url.isEmpty()) return;

        String cacheKey = "url:" + url;

        if (allowCaching && imageCache.containsKey(cacheKey)) {
            this.imageId = imageCache.get(cacheKey);
            this.imageLoaded = true;
            this.imageError = false;
            this.isLoading = false;
            return;
        }

        if (loadingImages.containsKey(cacheKey)) {
            this.isLoading = true;
            loadingImages.get(cacheKey).thenAccept(this::handleLoadedImage);
            return;
        }

        this.isLoading = true;
        this.imageError = false;

        CompletableFuture<Identifier> future = CompletableFuture.supplyAsync(() -> {
            try {
                URL imageUrl = new URI(url).toURL();
                URLConnection connection = imageUrl.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Minecraft ImageLoader)");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);

                try (InputStream inputStream = connection.getInputStream()) {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    if (bufferedImage == null) {
                        throw new IOException("Could not decode image from URL: " + url);
                    }

                    return createTextureFromBufferedImage(bufferedImage, cacheKey);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image from URL: " + url + " - " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        loadingImages.put(cacheKey, future);
        future.thenAccept(this::handleLoadedImage)
                .exceptionally(throwable -> {
                    handleImageError();
                    loadingImages.remove(cacheKey);
                    return null;
                });
    }

    private void loadImageFromFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;

        String cacheKey = "file:" + filePath;

        if (allowCaching && imageCache.containsKey(cacheKey)) {
            this.imageId = imageCache.get(cacheKey);
            this.imageLoaded = true;
            this.imageError = false;
            this.isLoading = false;
            return;
        }

        if (loadingImages.containsKey(cacheKey)) {
            this.isLoading = true;
            loadingImages.get(cacheKey).thenAccept(this::handleLoadedImage);
            return;
        }

        this.isLoading = true;
        this.imageError = false;

        CompletableFuture<Identifier> future = CompletableFuture.supplyAsync(() -> {
            try {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) {
                    throw new FileNotFoundException("Image file not found: " + filePath);
                }

                byte[] fileBytes = Files.readAllBytes(path);
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    if (bufferedImage == null) {
                        throw new IOException("Could not decode image from file: " + filePath);
                    }

                    return createTextureFromBufferedImage(bufferedImage, cacheKey);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image from file: " + filePath + " - " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        loadingImages.put(cacheKey, future);
        future.thenAccept(this::handleLoadedImage)
                .exceptionally(throwable -> {
                    handleImageError();
                    loadingImages.remove(cacheKey);
                    return null;
                });
    }

    private void loadImageFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return;

        String cacheKey = "bytes:" + java.util.Arrays.hashCode(bytes);

        if (allowCaching && imageCache.containsKey(cacheKey)) {
            this.imageId = imageCache.get(cacheKey);
            this.imageLoaded = true;
            this.imageError = false;
            this.isLoading = false;
            return;
        }

        this.isLoading = true;
        this.imageError = false;

        CompletableFuture<Identifier> future = CompletableFuture.supplyAsync(() -> {
            try {
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    if (bufferedImage == null) {
                        throw new IOException("Could not decode image from byte array");
                    }

                    return createTextureFromBufferedImage(bufferedImage, cacheKey);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image from bytes: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        future.thenAccept(this::handleLoadedImage)
                .exceptionally(throwable -> {
                    handleImageError();
                    return null;
                });
    }

    private Identifier createTextureFromBufferedImage(BufferedImage bufferedImage, String cacheKey) {
        try {
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            NativeImage nativeImage = new NativeImage(width, height, false);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    nativeImage.setColorArgb(x, y, rgb);
                }
            }

            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            Identifier textureId = Identifier.of("imageitem", "dynamic/" + System.currentTimeMillis() + "/" + Math.abs(cacheKey.hashCode()));

            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);

            if (allowCaching) {
                imageCache.put(cacheKey, textureId);
            }

            this.imageWidth = width;
            this.imageHeight = height;

            return textureId;

        } catch (Exception e) {
            System.err.println("Failed to create texture from BufferedImage: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handleLoadedImage(Identifier loadedImageId) {
        if (loadedImageId != null) {
            this.imageId = loadedImageId;
            this.imageLoaded = true;
            this.imageError = false;
        } else {
            handleImageError();
        }
        this.isLoading = false;
    }

    private void handleImageError() {
        this.imageError = true;
        this.imageLoaded = false;
        this.isLoading = false;
        this.imageId = null;
    }

    public static void clearImageCache() {
        for (Identifier texture : imageCache.values()) {
            try {
                MinecraftClient.getInstance().getTextureManager().destroyTexture(texture);
            } catch (Exception e) {
                System.err.println("Failed to destroy cached texture: " + e.getMessage());
            }
        }
        imageCache.clear();
        loadingImages.clear();
    }

    public static void removeFromCache(String imagePath) {
        String cacheKey;
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            cacheKey = "url:" + imagePath;
        } else {
            cacheKey = "file:" + imagePath;
        }

        Identifier texture = imageCache.remove(cacheKey);
        if (texture != null) {
            try {
                MinecraftClient.getInstance().getTextureManager().destroyTexture(texture);
            } catch (Exception e) {
                System.err.println("Failed to destroy texture: " + e.getMessage());
            }
        }

        loadingImages.remove(cacheKey);
    }

    public static int getCacheSize() {
        return imageCache.size();
    }

    public ImageItem setImageSize(int width, int height) {
        this.imageWidth = Math.max(0, width);
        this.imageHeight = Math.max(0, height);
        return this;
    }

    public ImageItem setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode != null ? scaleMode : ScaleMode.FIT;
        return this;
    }

    public ImageItem setClickable(boolean clickable) {
        this.isClickable = clickable;
        return this;
    }

    public ImageItem setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
        return this;
    }

    public ImageItem setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
        return this;
    }

    public ImageItem setRotation(int rotation) {
        this.rotation = rotation % 4;
        if (this.rotation < 0) this.rotation += 4;
        return this;
    }

    public ImageItem setFlip(boolean horizontal, boolean vertical) {
        this.flipHorizontal = horizontal;
        this.flipVertical = vertical;
        return this;
    }

    public ImageItem setTintColor(Color color) {
        this.tintColor = color != null ? color : Color.WHITE;
        return this;
    }

    public ImageItem setTintColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        this.tintColor = new Color(r, g, b, a);
        return this;
    }

    public ImageItem setBorderProperties(int color, int thickness) {
        this.borderColor = color;
        this.borderThickness = Math.max(0, thickness);
        return this;
    }

    public ImageItem setPlaceholderTexts(String loadingText, String errorText) {
        this.loadingText = loadingText != null ? loadingText : "Loading...";
        this.errorText = errorText != null ? errorText : "Failed to load image";
        return this;
    }

    public ImageItem setPlaceholderColors(int backgroundColor, int textColor) {
        this.placeholderColor = backgroundColor;
        this.placeholderTextColor = textColor;
        return this;
    }

    public ImageItem setShowPlaceholders(boolean showLoading, boolean showError) {
        this.showLoadingPlaceholder = showLoading;
        this.showErrorPlaceholder = showError;
        return this;
    }

    public Identifier getImage() { return imageId; }
    public String getImagePath() { return imagePath; }
    public String getImageUrl() { return imageUrl; }
    public byte[] getImageBytes() { return imageBytes != null ? imageBytes.clone() : null; }
    public ImageSource getImageSource() { return imageSource; }
    public ScaleMode getScaleMode() { return scaleMode; }
    public boolean isClickable() { return isClickable; }
    public boolean isImageLoaded() { return imageLoaded; }
    public boolean hasImageError() { return imageError; }
    public boolean isLoading() { return isLoading; }
    public int getImageWidth() { return imageWidth; }
    public int getImageHeight() { return imageHeight; }
    public float getOpacity() { return opacity; }
    public int getRotation() { return rotation; }
    public boolean isFlipHorizontal() { return flipHorizontal; }
    public boolean isFlipVertical() { return flipVertical; }
    public boolean isCachingAllowed() { return allowCaching; }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!isClickable || !enabled || !visible || !contains(mouseX, mouseY)) {
            return false;
        }

        setState(ItemState.PRESSED);
        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public void onMouseEnter() {
        if (isClickable) {
            super.onMouseEnter();
        }
    }

    @Override
    public void onMouseLeave() {
        if (isClickable) {
            super.onMouseLeave();
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        renderBackground(context);

        if (imageId != null && imageLoaded && !imageError) {
            renderImage(context);
        } else if (isLoading && showLoadingPlaceholder) {
            renderLoadingPlaceholder(context);
        } else if (imageError && showErrorPlaceholder) {
            renderErrorPlaceholder(context);
        } else if (!imageLoaded && !isLoading && showLoadingPlaceholder) {
            renderLoadingPlaceholder(context);
        }

        if (showBorder) {
            renderBorder(context);
        }

        if (isClickable && focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            int borderRadius = getBorderRadius();
            Render2D.drawRoundedRectBorder(context, x - 2, y - 2, width + 4, height + 4,
                    borderRadius + 2, focusColor, 2);
        }
    }

    private void renderBackground(DrawContext context) {
        int bgColor = getStateColor();
        int borderRadius = getBorderRadius();
        Shadow shadow = getShadow();

        if (isClickable && state == ItemState.HOVERED && hasClass(StyleKey.HOVER_SCALE)) {
            float animationProgress = getAnimationProgress();
            float scale = 1.0f + (0.05f * animationProgress);

            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            int offsetX = (scaledWidth - width) / 2;
            int offsetY = (scaledHeight - height) / 2;

            if (shadow != null) {
                Render2D.drawShadow(context, x - offsetX, y - offsetY, scaledWidth, scaledHeight, 3, 3, shadow.color);
            }

            if (bgColor != 0) {
                Render2D.drawRoundedRect(context, x - offsetX, y - offsetY, scaledWidth, scaledHeight, borderRadius, bgColor);
            }
        } else {
            if (shadow != null) {
                Render2D.drawShadow(context, x, y, width, height, 2, 2, shadow.color);
            }

            if (bgColor != 0) {
                Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);
            }
        }
    }

    private void renderImage(DrawContext context) {
        int contentX = x + getPaddingLeft();
        int contentY = y + getPaddingTop();
        int contentWidth = width - getPaddingLeft() - getPaddingRight();
        int contentHeight = height - getPaddingTop() - getPaddingBottom();

        if (contentWidth <= 0 || contentHeight <= 0) return;

        Render2D.enableClipping(context, contentX, contentY, contentWidth, contentHeight);

        ImageBounds bounds = calculateImageBounds(contentX, contentY, contentWidth, contentHeight);

        try {
            Color renderColor = new Color(
                    tintColor.getRed(),
                    tintColor.getGreen(),
                    tintColor.getBlue(),
                    (int) (tintColor.getAlpha() * opacity)
            );

            this.drawImage(imageId, bounds.x1, bounds.y1, bounds.x2, bounds.y2,
                    rotation, flipHorizontal ^ flipVertical, renderColor);

        } catch (Exception e) {
            imageError = true;
            imageLoaded = false;
            renderErrorPlaceholder(context);
        }

        Render2D.disableClipping(context);
    }

    private void drawImage(Identifier id, int x1, int y1, int x2, int y2, int rotation, boolean parity, Color color) {
        int[][] texCoords = {{0, 1}, {1, 1}, {1, 0}, {0, 0}};
        for (int i = 0; i < rotation % 4; i++) {
            int temp1 = texCoords[3][0], temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0];
            texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0];
            texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
        }
        if (parity) {
            int temp1 = texCoords[1][0];
            texCoords[1][0] = texCoords[0][0];
            texCoords[0][0] = temp1;
            temp1 = texCoords[3][0];
            texCoords[3][0] = texCoords[2][0];
            texCoords[2][0] = temp1;
        }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, id);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        RenderSystem.enableBlend();
        bufferbuilder.vertex(x1, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[0][0], texCoords[0][1]);
        bufferbuilder.vertex(x2, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[1][0], texCoords[1][1]);
        bufferbuilder.vertex(x2, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[2][0], texCoords[2][1]);
        bufferbuilder.vertex(x1, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[3][0], texCoords[3][1]);
        end(bufferbuilder);
        RenderSystem.disableBlend();
    }

    private void end(BufferBuilder bufferBuilder) {
        BuiltBuffer builtBuffer = bufferBuilder.endNullable();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }
    }

    private void renderLoadingPlaceholder(DrawContext context) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLoadingUpdate > LOADING_UPDATE_INTERVAL) {
            loadingDots = (loadingDots + 1) % 4;
            lastLoadingUpdate = currentTime;
        }

        String animatedText = loadingText + ".".repeat(loadingDots);
        renderPlaceholder(context, animatedText, placeholderColor, placeholderTextColor);
    }

    private void renderErrorPlaceholder(DrawContext context) {
        renderPlaceholder(context, errorText, darkenColor(placeholderColor, 20), placeholderTextColor);
    }

    private void renderPlaceholder(DrawContext context, String text, int bgColor, int textColor) {
        int borderRadius = getBorderRadius();

        Render2D.drawRoundedRect(context, x, y, width, height, borderRadius, bgColor);

        if (textRenderer != null && !text.isEmpty()) {
            int textX = x + (width - textRenderer.getWidth(text)) / 2;
            int textY = y + (height - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, text, textX, textY, textColor, false);
        }

        int iconSize = Math.min(width, height) / 4;
        int iconX = x + (width - iconSize) / 2;
        assert textRenderer != null;
        int iconY = y + (height - iconSize) / 2 - textRenderer.fontHeight / 2;

        Render2D.drawRoundedRect(context, iconX, iconY, iconSize, iconSize, 2, 0xFF666666);
        context.fill(iconX + 2, iconY + 2, iconX + iconSize - 2, iconY + iconSize - 2, 0xFF333333);

        for (int i = 0; i < iconSize / 6; i++) {
            context.fill(iconX + 4 + i, iconY + 4 + i, iconX + 5 + i, iconY + 5 + i, 0xFF888888);
            context.fill(iconX + iconSize - 5 - i, iconY + 4 + i, iconX + iconSize - 4 - i, iconY + 5 + i, 0xFF888888);
        }
    }

    private void renderBorder(DrawContext context) {
        int borderRadius = getBorderRadius();
        Render2D.drawRoundedRectBorder(context, x, y, width, height, borderRadius, borderColor, borderThickness);
    }

    private ImageBounds calculateImageBounds(int contentX, int contentY, int contentWidth, int contentHeight) {
        ImageBounds imageBounds = new ImageBounds(contentX, contentY, contentX + contentWidth, contentY + contentHeight);
        if (imageWidth <= 0 || imageHeight <= 0) {
            return imageBounds;
        }

        return switch (scaleMode) {
            case STRETCH -> imageBounds;

            case FIT -> {
                float scaleX = (float) contentWidth / imageWidth;
                float scaleY = (float) contentHeight / imageHeight;
                float scale = Math.min(scaleX, scaleY);

                int scaledWidth = (int) (imageWidth * scale);
                int scaledHeight = (int) (imageHeight * scale);

                int offsetX = (contentWidth - scaledWidth) / 2;
                int offsetY = (contentHeight - scaledHeight) / 2;

                yield new ImageBounds(
                        contentX + offsetX,
                        contentY + offsetY,
                        contentX + offsetX + scaledWidth,
                        contentY + offsetY + scaledHeight
                );
            }

            case FILL -> {
                float scaleX = (float) contentWidth / imageWidth;
                float scaleY = (float) contentHeight / imageHeight;
                float scale = Math.max(scaleX, scaleY);

                int scaledWidth = (int) (imageWidth * scale);
                int scaledHeight = (int) (imageHeight * scale);

                int offsetX = (contentWidth - scaledWidth) / 2;
                int offsetY = (contentHeight - scaledHeight) / 2;

                yield new ImageBounds(
                        contentX + offsetX,
                        contentY + offsetY,
                        contentX + offsetX + scaledWidth,
                        contentY + offsetY + scaledHeight
                );
            }

            case CENTER -> {
                int offsetX = (contentWidth - imageWidth) / 2;
                int offsetY = (contentHeight - imageHeight) / 2;

                yield new ImageBounds(
                        contentX + offsetX,
                        contentY + offsetY,
                        contentX + offsetX + imageWidth,
                        contentY + offsetY + imageHeight
                );
            }

            case TILE -> new ImageBounds(contentX, contentY, contentX + imageWidth, contentY + imageHeight);
        };
    }

    public ImageItem asAvatar() {
        return setScaleMode(ScaleMode.FILL)
                .addClass(StyleKey.ROUNDED_FULL)
                .setShowBorder(true)
                .setBorderProperties(0xFF888888, 2);
    }

    public ImageItem asIcon() {
        return setScaleMode(ScaleMode.FIT)
                .addClass(StyleKey.ROUNDED_SM);
    }

    public ImageItem asThumbnail() {
        return setScaleMode(ScaleMode.FILL)
                .addClass(StyleKey.ROUNDED_MD, StyleKey.SHADOW_SM)
                .setShowBorder(true);
    }

    public ImageItem asBackground() {
        return setScaleMode(ScaleMode.FILL)
                .setOpacity(0.8f);
    }

    public ImageItem asClickableImage() {
        return setClickable(true)
                .addClass(StyleKey.HOVER_SCALE, StyleKey.FOCUS_RING);
    }

    public static ImageItem fromUrl(UIStyleSystem styleSystem, int x, int y, int width, int height, String url) {
        return new ImageItem(styleSystem, x, y, width, height).setImageUrl(url);
    }

    public static ImageItem fromFile(UIStyleSystem styleSystem, int x, int y, int width, int height, String filePath) {
        return new ImageItem(styleSystem, x, y, width, height).setImagePath(filePath);
    }

    public static ImageItem fromBytes(UIStyleSystem styleSystem, int x, int y, int width, int height, byte[] bytes) {
        return new ImageItem(styleSystem, x, y, width, height).setImageBytes(bytes);
    }

    public static ImageItem fromResource(UIStyleSystem styleSystem, int x, int y, int width, int height, Identifier identifier) {
        return new ImageItem(styleSystem, x, y, width, height, identifier);
    }

    private record ImageBounds(int x1, int y1, int x2, int y2) {}

    @Override
    public ImageItem addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public ImageItem removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public ImageItem onClick(Runnable handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public ImageItem onMouseEnter(Runnable handler) {
        super.onMouseEnter(handler);
        return this;
    }

    @Override
    public ImageItem onMouseLeave(Runnable handler) {
        super.onMouseLeave(handler);
        return this;
    }

    @Override
    public ImageItem onFocusGained(Runnable handler) {
        super.onFocusGained(handler);
        return this;
    }

    @Override
    public ImageItem onFocusLost(Runnable handler) {
        super.onFocusLost(handler);
        return this;
    }

    @Override
    public ImageItem setVisible(boolean visible) {
        super.setVisible(visible);
        return this;
    }

    @Override
    public ImageItem setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public ImageItem setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        return this;
    }

    @Override
    public ImageItem setConstraints(LayoutConstraints constraints) {
        super.setConstraints(constraints);
        return this;
    }

    @Override
    public ImageItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }
}