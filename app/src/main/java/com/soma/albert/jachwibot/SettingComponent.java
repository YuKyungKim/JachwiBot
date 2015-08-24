package com.soma.albert.jachwibot;

/**
 * Created by whee6409 on 15. 8. 23.
 */
public class SettingComponent {
    private int settingType;
    private int settingName;
    private int isEnable;

    public SettingComponent() {
    }

    public SettingComponent(int settingType, int settingName, int isEnable) {
        this.settingType = settingType;
        this.settingName = settingName;
        this.isEnable = isEnable;
    }

    public int getSettingType() {
        return settingType;
    }

    public void setSettingType(int settingType) {
        this.settingType = settingType;
    }

    public int getSettingName() {
        return settingName;
    }

    public void setSettingName(int settingName) {
        this.settingName = settingName;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }
}
