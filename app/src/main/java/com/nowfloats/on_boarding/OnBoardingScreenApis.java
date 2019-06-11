package com.nowfloats.on_boarding;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nowfloats.CustomPage.CustomPageInterface;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 20-03-2018.
 */

public class OnBoardingScreenApis implements Observer{

    private static final int APIS_COMPLETE = -1;
    public static final String UPDATES = "updates", PRODUCTS = "products", PAGES = "pages";
    private WeakReference<OnBoardingCallback> callbackListener;
    private HashMap<String, Integer> responseApis = new HashMap<>();
    private CountDownLatch counter = new CountDownLatch(3);
    private ApiNumberObservable numberObservable;

    public OnBoardingScreenApis(Context context, String tag, String id){
        numberObservable = new ApiNumberObservable();
        numberObservable.setFpId(id);
        numberObservable.setFpTag(tag);
        numberObservable.addObserver(this);
    }

    public void setCallbackListener(OnBoardingCallback callback){
        callbackListener = new WeakReference<OnBoardingCallback>(callback);
    }

    public void startProcess(){
        numberObservable.setValue(0);
    }
    public void startApisExecution(final Handler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {

                HashMap<String, Integer> map = new HashMap<>();
                getMessages(numberObservable.getFpId());
                getPages(numberObservable.getFpTag());
                getProductList(numberObservable.getFpTag());
                try {
                    counter.await();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //callback send data

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void getMessages(final String fpId)
    {
        Log.d("Fetch_Home_Data","getMessages : "+fpId);
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("skipBy","0");
        map.put("fpId",fpId);
        Constants.restAdapter.create(Login_Interface.class)
                .getMessages(map, new Callback<MessageModel>() {
                    @Override
                    public void success(MessageModel messageModel, retrofit.client.Response response) {
                        if (messageModel != null && messageModel.floats != null){
                            responseApis.put(UPDATES,messageModel.floats.size());
                        }else{
                            responseApis.put(UPDATES,0);
                        }
                        numberObservable.setValue(numberObservable.getValue()+1);
                        counter.countDown();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        responseApis.put(UPDATES,-1);
                        numberObservable.setValue(numberObservable.getValue()+1);
                        counter.countDown();
                    }
                });
    }

    private void getPages (String tag){

        Constants.restAdapter.create(CustomPageInterface.class)
                .getPageList(tag, Constants.clientId, new Callback<ArrayList<CustomPageModel>>() {
                    @Override
                    public void success(ArrayList<CustomPageModel> data, Response response) {
                        responseApis.put(PAGES,data != null?data.size():0);
                        numberObservable.setValue(numberObservable.getValue()+1);
                        counter.countDown();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        responseApis.put(PAGES,-1);
                        numberObservable.setValue(numberObservable.getValue()+1);
                        counter.countDown();
                    }
                });
    }

    private void getProductList(String tag){
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);
        map.put("skipBy","0");
        map.put("fpTag",tag);
        Constants.restAdapter.create(ProductGalleryInterface.class)
                .getProducts(map, new Callback<ArrayList<ProductListModel>>() {
                    @Override
                    public void success(ArrayList<ProductListModel> data, Response response) {
                        responseApis.put(PRODUCTS,data != null?data.size():0);
                        numberObservable.setValue(-1);
                        counter.countDown();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        responseApis.put(UPDATES,-1);
                        numberObservable.setValue(-1);
                        counter.countDown();
                    }
                });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ApiNumberObservable){
            if (callbackListener.get() == null){
                return;
            }
            switch (((ApiNumberObservable) o).getValue()){
                case 0:
                    getMessages(((ApiNumberObservable) o).getFpId());
                    break;
                case 1:
                    getPages(((ApiNumberObservable) o).getFpTag());
                    break;
                case 2:
                    getProductList(((ApiNumberObservable) o).getFpTag());
                    break;
                case APIS_COMPLETE:
                    //responseApis
                    callbackListener.get().onBoardingCall(responseApis);
                    break;
            }
        }
    }
}
