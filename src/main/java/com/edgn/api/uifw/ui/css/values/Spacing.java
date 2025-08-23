package com.edgn.api.uifw.ui.css.values;

public enum Spacing {
    NONE(0), XS(2), SM(4), MD(8), LG(12), XL(16), XXL(24), XXXL(32), XXXXL(48);
    
    public final int value;
    
    Spacing(int value) {
        this.value = value;
    }
}