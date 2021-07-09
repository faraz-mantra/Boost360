package com.nowfloats.signup.UI.Model;

import java.io.Serializable;

/**
 * Created by Admin on 03-07-2017.
 */

public class CategoryModel implements Serializable{
    public String Value;
    public String Key;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
