package com.nowfloats.customerassistant.models;

/**
 * Created by Admin on 12-10-2017.
 */

public class SharedSuggestionsDO {
    String name="", url="",image="";

    public SharedSuggestionsDO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
            return getName().equals(data.getName()) && getUrl().equals(data.getUrl()) && getImage().equals(data.getImage());
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode()+getName().hashCode()+getImage().hashCode();
    }
}
