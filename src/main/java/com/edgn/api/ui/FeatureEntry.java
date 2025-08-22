package com.edgn.api.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class FeatureEntry {
    public final String id;
    public final String title;
    public final String description;
    public final String version;
    public final Identifier icon;
    public final Supplier<Screen> openScreen;

    private FeatureEntry(Builder b) {
        this.id = b.id; this.title = b.title; this.description = b.description;
        this.version = b.version; this.icon = b.icon; this.openScreen = b.openScreen;
    }
    public static Builder builder(String id) { return new Builder(id); }
    public static final class Builder {
        private final String id;
        private String title, description, version = "v1.0.0";
        private Identifier icon;
        private Supplier<Screen> openScreen;
        public Builder(String id){ this.id = id; }
        public Builder title(String v){ this.title = v; return this; }
        public Builder description(String v){ this.description = v; return this; }
        public Builder version(String v){ this.version = v; return this; }
        public Builder icon(Identifier v){ this.icon = v; return this; }
        public Builder openScreen(Supplier<Screen> v){ this.openScreen = v; return this; }
        public FeatureEntry build(){ return new FeatureEntry(this); }
    }
}