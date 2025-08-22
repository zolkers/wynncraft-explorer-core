package com.edgn.wynncraft.encoders;

public class ItemEncoderConfig {
    
    private EncodingType encodingType = EncodingType.WYNNTILS;
    
    private byte itemType = 0;
    private byte formatVersion = 1;
    private String[] fakeItemNames = {};
    private boolean addAdditionalBlocks = true;
    private int maxIdentificationProperties = 10;

    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    @Deprecated
    public boolean isUseItemEncoding() {
        return encodingType == EncodingType.ARTEMIS;
    }

    @Deprecated
    public void setUseItemEncoding(boolean useItemEncoding) {
        this.encodingType = useItemEncoding ? EncodingType.ARTEMIS : EncodingType.STANDARD;
    }

    public byte getItemType() {
        return itemType;
    }

    public void setItemType(byte itemType) {
        if (itemType >= 0 && itemType <= 5) { 
            this.itemType = itemType;
        }
    }

    public byte getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(byte formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String[] getFakeItemNames() {
        return fakeItemNames;
    }

    public void setFakeItemNames(String[] fakeItemNames) {
        this.fakeItemNames = fakeItemNames;
    }

    public boolean isAddAdditionalBlocks() {
        return addAdditionalBlocks;
    }

    public void setAddAdditionalBlocks(boolean addAdditionalBlocks) {
        this.addAdditionalBlocks = addAdditionalBlocks;
    }

    public int getMaxIdentificationProperties() {
        return maxIdentificationProperties;
    }

    public void setMaxIdentificationProperties(int maxIdentificationProperties) {
        if (maxIdentificationProperties > 0) {
            this.maxIdentificationProperties = maxIdentificationProperties;
        }
    }

    private static final ItemEncoderConfig instance = new ItemEncoderConfig();

    public static ItemEncoderConfig getInstance() {
        return instance;
    }

    private ItemEncoderConfig() {
        
    }
}
