package com.nowfloats.ProductGallery.Model;

import java.util.ArrayList;

/**
 * Created by guru on 10/06/2015.
 */
public class Product_Gallery_Update_Model {
    public String clientId;
    public String productId;
    public String productType;
    public ArrayList<UpdateValue> updates;

    public Product_Gallery_Update_Model(String clientId, String productId, ArrayList<UpdateValue> updates) {
        this.clientId = clientId;
        this.productId = productId;
        this.updates = updates;
    }

    public Product_Gallery_Update_Model(String clientId, String productId, String productType, ArrayList<UpdateValue> updates) {
        this.clientId = clientId;
        this.productId = productId;
        this.productType = productType;
        this.updates = updates;
    }
}