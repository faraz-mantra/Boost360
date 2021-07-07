package com.nowfloats.webactions.models;

import java.io.Serializable;

/**
 * Created by NowFloats on 17-04-2018.
 */

public class ProductImage implements Serializable {
    public String url;
    public String description;

    public ProductImage(String url, String desc) {
        this.url = url;
        this.description = desc;
    }
}
