package com.nowfloats.BusinessProfile.UI.API;


//Uncomment all the lines to the changes the location from the map for and using the locatingbusinessaddress()

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.gson.JsonArray;
import com.nowfloats.BusinessProfile.UI.Model.BusinessAddressUpdateModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public class BusinessAddressUpdateApi {

    ProgressDialog pd = null;
    UserSessionManager session;
    String city, pincode, address;
    double latitude, longitude;
    ListenerNew listenerNew;
    BusinessAddressUpdateModel addressModel = new BusinessAddressUpdateModel();
    List<BusinessAddressUpdateModel.Update> list = new ArrayList<>();
    private Activity appContext = null;

    public BusinessAddressUpdateApi(double latitude, double longitude,
                                    Activity context, String city, String pincode,
                                    String address, boolean addressAdd, String fpTag, ListenerNew listenerNew) {
//values are coming from the bussiness address fragment
        this.appContext = context;
        String location = latitude + "," + longitude;
//Log.v("ggg","constructor geocoordinate "+location);
        if (addressAdd) {
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.pincode = pincode;
            BusinessAddressUpdateModel.Update update1 = new BusinessAddressUpdateModel().new Update();
            update1.setKey("ADDRESS");
            update1.setValue(address);
            list.add(update1);
            BusinessAddressUpdateModel.Update update2 = new BusinessAddressUpdateModel().new Update();
            update2.setKey("CITY");
            update2.setValue(city);
            list.add(update2);
            BusinessAddressUpdateModel.Update update3 = new BusinessAddressUpdateModel().new Update();
            update3.setKey("PINCODE");
            update3.setValue(pincode);
            list.add(update3);
        }
        BusinessAddressUpdateModel.Update update4 = new BusinessAddressUpdateModel().new Update();
        update4.setKey("GEOLOCATION");
        update4.setValue(location);
        list.add(update4);
        addressModel.setClientId(Constants.clientId);
        addressModel.setUpdates(list);
        addressModel.setFpTag(fpTag);
        this.listenerNew = listenerNew;
        session = new UserSessionManager(appContext, context);

    }

    public void update() {
        pd = ProgressDialog.show(appContext, null, "Updating your address");
        pd.show();
        UpdateCallApi callApi = Constants.restAdapter.create(UpdateCallApi.class);
        callApi.updateAddress(addressModel, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                if (response.getStatus() != 200 || jsonElements == null) return;
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY, city);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE, pincode);
                session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS, address);
                session.storeFPDetails(Key_Preferences.LATITUDE, String.valueOf(latitude));
                session.storeFPDetails(Key_Preferences.LONGITUDE, String.valueOf(longitude));
                Methods.showSnackBarPositive(appContext, "Update Address successful");
                NewMapViewDialogBusinessAddress.updatingPostionFromMap = true;
                if (listenerNew != null) listenerNew.updateAddress(true);

            }

            @Override
            public void failure(RetrofitError error) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                //Log.v("ggg",error+"update address json error");
                if (listenerNew != null) listenerNew.updateAddress(false);
            }
        });
    }

    interface UpdateCallApi {
        @POST("/Discover/v1/FloatingPoint/update/")
        void updateAddress(@Body BusinessAddressUpdateModel model, Callback<JsonArray> response);
    }

    public interface ListenerNew {
        void updateAddress(boolean success);
    }
}
