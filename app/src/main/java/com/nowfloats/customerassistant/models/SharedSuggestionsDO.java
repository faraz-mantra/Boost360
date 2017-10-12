package com.nowfloats.customerassistant.models;

/**
 * Created by Admin on 12-10-2017.
 */

public class SharedSuggestionsDO {
    String name, url;

    public SharedSuggestionsDO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  SharedSuggestionsDO){
            SharedSuggestionsDO data = (SharedSuggestionsDO) obj;
            return getName().equals(data.getName()) && getUrl().equals(data.getUrl());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode()+getName().hashCode();
    }
}
