package com.nowfloats.Product_Gallery;

import com.nowfloats.Product_Gallery.Model.ProductListModel;

import java.util.ArrayList;

/**
 * Created by guru on 11-06-2015.
 */
public class LoadMoreProductEvent {
    ArrayList<ProductListModel> data;
    public LoadMoreProductEvent(ArrayList<ProductListModel> data){
        this.data = data;
    }
}
