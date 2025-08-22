package com.edgn.wynncraft.encoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncoderConfig {
    private String rootSeed = "GgIbgiuGUIgiuGiuGIUgiGijBVIJpJ^pùJ¨XùpY0WxypoCPuymImU%vùPUocuxBGNIcuFJYhkgjjBHVJHKnghjkKJGIHhjkvbjhgj?NBJHjjh";
    
    private List<String> symbolsToIgnore = new ArrayList<>(Arrays.asList(
            "󏿼", "󏿿", "󏿾", "󐀆",
            "\u00A7",
            "\u2014",
            "\u2424",
            "\u200B",
            "\u200C",
            "\u200D"
    ));
    
    public String getRootSeed() {
        return rootSeed;
    }
    
    public void setRootSeed(String rootSeed) {
        if (rootSeed != null && !rootSeed.isEmpty()) {
            this.rootSeed = rootSeed;
        }
    }
    
    public List<String> getSymbolsToIgnore() {
        return symbolsToIgnore;
    }
    
    public void addSymbolToIgnore(String symbol) {
        if (symbol != null && !symbol.isEmpty() && !symbolsToIgnore.contains(symbol)) {
            symbolsToIgnore.add(symbol);
        }
    }
    
    public void removeSymbolToIgnore(String symbol) {
        symbolsToIgnore.remove(symbol);
    }
    
    public void clearSymbolsToIgnore() {
        symbolsToIgnore.clear();
    }
    
    public void updateFrom(EncoderConfig other) {
        if (other != null) {
            if (other.rootSeed != null && !other.rootSeed.isEmpty()) {
                this.rootSeed = other.rootSeed;
            }
            
            if (other.symbolsToIgnore != null && !other.symbolsToIgnore.isEmpty()) {
                this.symbolsToIgnore = new ArrayList<>(other.symbolsToIgnore);
            }
        }
    }
}