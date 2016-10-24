package com.nowfloats.Login;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Model.MessageModel;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.sync.DbController;
import com.nowfloats.sync.model.Updates;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Dell on 11-02-2015.
 */
public class Fetch_Home_Data {
    Activity appActivity ;
    private FloatsMessageModel sendJson = null;
    private boolean dataExists = false;
    private boolean newPost = false,interfaceInvoke = true;
    private DbController mDbController;

    public int getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    public int interfaceType = 0;

    public interface Fetch_Home_Data_Interface {
        public void dataFetched(int skip, boolean isNewMessage);
        public void sendFetched(FloatsMessageModel jsonObject);
    }

    public Fetch_Home_Data_Interface fetchHomeDataInterface = null;

    public Fetch_Home_Data(Activity activity,int type){
        appActivity = activity ;
        setInterfaceType(type);
        mDbController = DbController.getDbController(appActivity);
    }

    public void setFetchDataListener(Fetch_Home_Data_Interface fetchHomeDataInterface)
    {
        this.fetchHomeDataInterface = fetchHomeDataInterface ;
    }
    public void setNewPostListener(boolean value){
        this.newPost = value;
    }
    public void getMessages(final String fpId,final String skipByCount)
    {
        Log.d("Fetch_Home_Data","getMessages : "+fpId);
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("skipBy",skipByCount);
        map.put("fpId",fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getMessages(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel,fpId,skipByCount, false);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public void getNewAvailableMessage(String messageId, final String fpId){
        HashMap<String,String> map = new HashMap<>();
        map.put("clientId",Constants.clientId);
        map.put("messageId",messageId);
        map.put("merchantId",fpId);
        Login_Interface login_interface = Constants.restAdapter.create(Login_Interface.class);
        login_interface.getNewAvailableMessage(map, new Callback<MessageModel>() {
            @Override
            public void success(MessageModel messageModel, retrofit.client.Response response) {
                parseMessages(messageModel,fpId,"0", true);
            }
            @Override
            public void failure(RetrofitError error) {
                if (fetchHomeDataInterface!=null && interfaceType==0){
                    fetchHomeDataInterface.dataFetched(0, false);
                }else if (fetchHomeDataInterface!=null && interfaceType==1){
                    fetchHomeDataInterface.sendFetched(sendJson);
                }
            }
        });
    }

    public void parseMessages(MessageModel response,String fpId,String skip, boolean isNewMessage){
        BoostLog.d("Called Parse Message: ", "Parsing Message");
        if (response!=null) {
            interfaceInvoke = true;
            ArrayList<FloatsMessageModel> bizData 	= response.floats;
            Constants.moreStorebizFloatsAvailable 	= response.moreFloatsAvailable;
            if(bizData.size() > 0 ) {
                sendJson = bizData.get(0);
                Constants.NumberOfUpdates = HomeActivity.StorebizFloats.size() ;
                MixPanelController.setProperties("NoOfUpdates", "" + Constants.NumberOfUpdates);

                /*for (int i = 0; i < bizData.size(); i++) {
                    FloatsMessageModel data = bizData.get(i);
                    if (HomeActivity.StorebizFloats!=null) {
                        String formatted = Methods.getFormattedDate(data.createdOn);
                        data.createdOn = formatted;

                        for (int j = 0; j < HomeActivity.StorebizFloats.size(); j++) {
                           if (HomeActivity.StorebizFloats.get(j)._id.equals(data._id)) {
                               dataExists = true; break;
                           }else{
                               dataExists = false;
                           }
                        }

                        if(newPost) {
                            if (dataExists && interfaceType==0){
                                interfaceInvoke = false;
                                //TODO delay
                                getMessages(fpId,skip);
                                break;
                            }else{
                                newPost = false;
                                HomeActivity.StorebizFloats.add(0,data);
                            }
                        }else if(!dataExists){
                            HomeActivity.StorebizFloats.add(data);
                        }
                    }
                }*/
                for(int i=0; i<bizData.size(); i++){
                    FloatsMessageModel data = bizData.get(i);
                    Updates update = new Updates();
                    update.setServerId(data._id)
                            .setDate(data.createdOn.split("\\(")[1].split("\\)")[0])
                            .setImageUrl(data.imageUri)
                            .setSynced(1)
                            .setTileImageUrl(data.imageUri)
                            .setType(data.type)
                            .setUpdateText(data.message);
                    mDbController.postUpdate(update);
                    BoostLog.d("Saving To Db:", "Oh Saved to Db" + data.message);
                }
            }

            if(HomeActivity.StorebizFloats!=null && HomeActivity.StorebizFloats.size()==0){
                if (Home_Main_Fragment.emptyMsgLayout!=null && !Constants.fromLogin) {
                        Home_Main_Fragment.emptyMsgLayout.setVisibility(View.VISIBLE);
                }
            }else{
                if (Home_Main_Fragment.emptyMsgLayout!=null)
                    Home_Main_Fragment.emptyMsgLayout.setVisibility(View.GONE);
            }

            if (interfaceInvoke){
                if (fetchHomeDataInterface!=null && interfaceType==0){
                    fetchHomeDataInterface.dataFetched(Integer.parseInt(skip), isNewMessage);
                }else if (fetchHomeDataInterface!=null && interfaceType==1){
                    fetchHomeDataInterface.sendFetched(sendJson);
                }
            }
        }else{

                if (fetchHomeDataInterface!=null && interfaceType==0){
                    fetchHomeDataInterface.dataFetched(Integer.parseInt(skip), isNewMessage);
                }else if (fetchHomeDataInterface!=null && interfaceType==1){
                    fetchHomeDataInterface.sendFetched(sendJson);
                }

        }
    }

}