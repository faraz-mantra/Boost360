package com.nowfloats.webactions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Property {

    @SerializedName("DisplayName")
    @Expose
    private String displayName;
    @SerializedName("PropertyName")
    @Expose
    private String propertyName;
    @SerializedName("PropertyType")
    @Expose
    private Integer propertyType;
    @SerializedName("DataType")
    @Expose
    private String dataType;
    @SerializedName("IsRequired")
    @Expose
    private Boolean isRequired;
    @SerializedName("ValidationRegex")
    @Expose
    private Object validationRegex;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Object getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(Object validationRegex) {
        this.validationRegex = validationRegex;
    }

}