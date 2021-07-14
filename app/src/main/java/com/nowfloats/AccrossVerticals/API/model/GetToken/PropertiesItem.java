package com.nowfloats.AccrossVerticals.API.model.GetToken;

import com.google.gson.annotations.SerializedName;

public class PropertiesItem {

    @SerializedName("PropertyName")
    private String propertyName;

    @SerializedName("IsRequired")
    private boolean isRequired;

    @SerializedName("DisplayName")
    private String displayName;

    @SerializedName("DataType")
    private String dataType;

    @SerializedName("PropertyType")
    private int propertyType;

    @SerializedName("ValidationRegex")
    private String validationRegex;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(int propertyType) {
        this.propertyType = propertyType;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }
}