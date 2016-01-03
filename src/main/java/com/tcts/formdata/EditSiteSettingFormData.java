package com.tcts.formdata;

/**
 * The data fields needed to edit a site setting.
 */
public class EditSiteSettingFormData {
    private String settingName;
    private String settingValue;

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
