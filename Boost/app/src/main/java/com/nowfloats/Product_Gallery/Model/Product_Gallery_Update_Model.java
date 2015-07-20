package com.nowfloats.Product_Gallery.Model;

import java.util.ArrayList;

/**
 * Created by guru on 10/06/2015.
 */
public class Product_Gallery_Update_Model {
    public String clientId;
    public String productId;
    public ArrayList<UpdateValue> updates;

    public Product_Gallery_Update_Model(String clientId,String productId,ArrayList<UpdateValue> updates)
    {
        this.clientId = clientId ;
        this.productId = productId;
        this.updates = updates ;
    }
}