package com.nowfloats.BusinessProfile.UI.API;



//Uncomment all the lines to the changes the location from the map for and using the locatingbusinessaddress() 

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.nowfloats.BusinessProfile.UI.Model.BusinessAddressUpdateModel;
import com.nowfloats.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public class BusinessAddressUpdateApi {

	private Activity appContext = null;
	ProgressDialog pd 	= null;
	SharedPreferences sharedpreferences;

    BusinessAddressUpdateModel addressModel=new BusinessAddressUpdateModel();
    List<BusinessAddressUpdateModel.Update> list=new ArrayList<>();

    public BusinessAddressUpdateApi(double latitude,
                                    double longitude,
                                    Activity context,
                                    String city, String pincode,
                                    String address, boolean addressAdd, String fpTag) {
//values are coming from the bussiness address fragment
        this.appContext = context;
        String location = latitude+","+longitude;
//Log.v("ggg","constructor geocoordinate "+location);
        if(addressAdd){
            BusinessAddressUpdateModel.Update update1=new BusinessAddressUpdateModel().new Update();
            update1.setKey("ADDRESS");
            update1.setValue(address);
            list.add(update1);
            BusinessAddressUpdateModel.Update update2=new BusinessAddressUpdateModel().new Update();
            update2.setKey("CITY");
            update2.setValue(city);
            list.add(update2);
            BusinessAddressUpdateModel.Update update3=new BusinessAddressUpdateModel().new Update();
            update3.setKey("PINCODE");
            update3.setValue(pincode);
            list.add(update3);
        }
        BusinessAddressUpdateModel.Update update4=new BusinessAddressUpdateModel().new Update();
        update4.setKey("GEOLOCATION");
        update4.setValue(location);
        list.add(update4);
        addressModel.setClientId(Constants.clientId);
        addressModel.setUpdates(list);
        addressModel.setFpTag(fpTag);

    }
    public void update(){
        pd= ProgressDialog.show(appContext, null, "Updating Your Address");
        pd.show();
        RestAdapter adapter=new RestAdapter.Builder()
                .setEndpoint(Constants.NOW_FLOATS_API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("ggg"))
                .build();
        UpdateCallApi callApi=adapter.create(UpdateCallApi.class);
        callApi.updateAddress(addressModel, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                if(pd!=null)
                    pd.dismiss();
                if(response.getStatus()!=200 ||jsonElements==null) return;
                Toast.makeText(appContext,"Update Address successful",Toast.LENGTH_SHORT).show();
                NewMapViewDialogBusinessAddress.updatingPostionFromMap = true;
                for (JsonElement element:jsonElements) {
                    //Log.v("ggg","json update elements"+element.getAsString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(pd!=null)
                    pd.dismiss();
                //Log.v("ggg",error+"update address json error");
            }
        });
    }
    interface UpdateCallApi{
        @POST("/Discover/v1/FloatingPoint/update/")
        void updateAddress(@Body BusinessAddressUpdateModel model, Callback<JsonArray> response);
    }

}
