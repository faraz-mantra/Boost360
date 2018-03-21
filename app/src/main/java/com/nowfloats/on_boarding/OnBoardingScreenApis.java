package com.nowfloats.on_boarding;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Admin on 20-03-2018.
 */

public class OnBoardingScreenApis {

    private Context mContext;
    private ArrayList<ApiModel> apiQueue;
    public OnBoardingScreenApis(Context context){
        mContext = context;
        apiQueue = new ArrayList<>();
    }

    public void addApi(ApiModel model){
        apiQueue.add(model);
    }

    public void startApisExecution(){
        //Constants.restAdapter.create(CustomPageInterface.class).getPageList();
        //Constants.restAdapter.create(ProductGalleryInterface.class).getProducts();
        //Constants.restAdapter.create(Login_Interface.class).getMessages();
    }
}
