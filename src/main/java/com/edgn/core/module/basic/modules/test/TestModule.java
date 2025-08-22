package com.edgn.core.module.basic.modules.test;

import com.edgn.annotations.Test;
import com.edgn.core.config.ConfigManager;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.SettingsGroup;
import com.edgn.core.minecraft.render.utils.ColorUtil;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Test
@ModuleInfo(
        name = "Test",
        description = "test",
        category = ModuleCategory.UTILITY,
        authors = {"EDGN"},
        version = "0.0.1"
)
public class TestModule extends AbstractModule {

    @ConfigManager.SaveField @Expose private double test1 = 0;
    @ConfigManager.SaveField @Expose private String test2 = "";
    @ConfigManager.SaveField @Expose private boolean test4 = false;
    @ConfigManager.SaveField @Expose private int test5 = ColorUtil.INSTANCE.GRAY;

    @ConfigManager.SaveField @Expose
    private List<String> testList = new ArrayList<>(Arrays.asList("Default 1", "Default 2"));

    @ConfigManager.SaveField @Expose
    private List<Integer> intList = new ArrayList<>(Arrays.asList(42, 1337));

    @ConfigManager.SaveField @Expose
    private TestEnum testEnum = TestEnum.OPTION_A;

    public enum TestEnum {
        OPTION_A,
        OPTION_B,
        OPTION_C
    }

    public TestModule() {
        super("Test");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeSettings() {
        SettingsGroup mainGroup = new SettingsGroup("Test Panel", "A group for various test settings");

        mainGroup.addDoubleSlider("Test 1", "double", test1, 0, 100, 1)
                .setOnValueChanged((oldValue, newValue) -> this.test1 = (Double) newValue);

        mainGroup.addString("Test 2", "string", test2)
                .setOnValueChanged((oldValue, newValue) -> this.test2 = (String) newValue);

        mainGroup.addBoolean("Test 4", "boolean", test4)
                .setOnValueChanged((oldValue, newValue) -> this.test4 = (Boolean) newValue);

        mainGroup.addColor("Test 5", "color", test5)
                .setOnValueChanged((oldValue, newValue) -> this.test5 = (Integer) newValue);

        mainGroup.addList("Test List", "A list of strings.", testList, s -> s, Object::toString)
                .setOnValueChanged((oldValue, newValue) -> this.testList = (List<String>) newValue);

        mainGroup.addList("Integer List", "A list of integers.", intList, Integer::parseInt, Object::toString)
                .setOnValueChanged((oldValue, newValue) -> {
                    if (newValue instanceof List<?> rawList) {
                        this.intList = rawList.stream()
                                .filter(o -> o instanceof Number)
                                .map(o -> ((Number) o).intValue())
                                .collect(Collectors.toList());
                    }
                });

        mainGroup.addEnum("Test Enum", "A setting for testing enums.", testEnum)
                .setOnValueChanged((oldValue, newValue) -> this.testEnum = (TestEnum) newValue);

        this.settingsGroups.add(mainGroup);
    }

    @Override public void onSettingsChanged() {}

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}