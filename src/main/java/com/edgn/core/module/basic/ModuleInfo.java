package com.edgn.core.module.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name();
    String description() default "No description";
    ModuleCategory category() default ModuleCategory.UTILITY;
    String iconPath() default "textures/gui/icons/default_module.png";
    String[] authors() default {};
    String version() default "1.0.0";
}