package com.edgn.core.utils;

import com.edgn.core.minecraft.render.StyledText;
import net.minecraft.util.Formatting;

public class TagUtil {
    public static final String MAIN_TAG = StyledText.INSTANCE.encase(
            "WE >>", Formatting.WHITE, Formatting.DARK_GREEN
    ) + " ";

}
