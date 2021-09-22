package com.nowfloats.Business_Enquiries;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Enterprise_Model;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by Dell on 28-01-2015.
 */
public class API_Business_enquiries {

  private RequestQueue queue;
  private String fpIdPrefs = null;
  private UserSessionManager session;
  private Bus bus;

  public API_Business_enquiries(Context context) {
    queue = AppController.getInstance().getRequestQueue();
  }

  public API_Business_enquiries(Bus bus, UserSessionManager session) {
    queue = AppController.getInstance().getRequestQueue();
    fpIdPrefs = session.getFPID();
    this.session = session;
    this.bus = bus;
  }

  public void getMessages() {
    try {
      if (session.getISEnterprise().equals("true")) {
        String startDate = "2014-08-06";
        String endDate = "2015-06-05";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf.format(c.getTime());

        String scope = "1";
        Business_enquiries_interface BE = Constants.restAdapter.create(Business_enquiries_interface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", Constants.clientId);
//                params.put("startDate", startDate);
//                params.put("endDate", endDate);
//                params.put("scope", scope);
//                BE.getMessagesMethod(session.getFPParentId(), params, new Callback<Business_Enquiry_Enterprise_Model>() {
//                            @Override
//                            public void success(Business_Enquiry_Enterprise_Model business_enquiry_models, Response response) {
//                                parseEnterpriseEnquiryMessages(business_enquiry_models);
//                            }
//
//                            @Override
//                            public void failure(RetrofitError error) {
//                                BoostLog.i("Enterprise_enquiry", "" + error.getLocalizedMessage());
//                                if (bus != null) bus.post("Getting error " + error.getLocalizedMessage());
//                            }
//                        });
        BE.getMessagesEnterPriseMethod(session.getFPParentId(), params, new Callback<ArrayList<Business_Enquiry_Model>>() {
          @Override
          public void success(ArrayList<Business_Enquiry_Model> data, Response response) {
            parseMessages(data);
          }

          @Override
          public void failure(RetrofitError error) {
            BoostLog.i("Enterprise_enquiry", "" + error.getLocalizedMessage());
            if (bus != null) bus.post("Getting error " + error.getLocalizedMessage());
          }
        });

      } else {
        HashMap<String, String> map = new HashMap<>();
        map.put("clientId", Constants.clientId);

        Business_enquiries_interface BE = Constants.restAdapter.create(Business_enquiries_interface.class);

        BE.postMessagesMethod(new JSONObject(), map, fpIdPrefs, new Callback<ArrayList<Business_Enquiry_Model>>() {

          @Override
          public void success(ArrayList<Business_Enquiry_Model> data, Response response) {
            parseMessages(data);
          }

          @Override
          public void failure(RetrofitError error) {
            BoostLog.d("Business_enquiry", " Error : " + error.getLocalizedMessage());
            if (bus != null) bus.post("Getting error " + error.getLocalizedMessage());
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (bus != null) bus.post("Getting error " + e.getLocalizedMessage());
    }
  }

  private void parseEnterpriseEnquiryMessages(Business_Enquiry_Enterprise_Model response) {
    Constants.StorebizEnterpriseQueries = new ArrayList<>();
    String formatted = "";
    String temp[] = new String[0];
    String dateString = null, dateTime = null;

    try {
      ArrayList<Entity_model> entityArray = response.Entity;
      if (entityArray.size() >= 1) {
        for (int i = 0; i < entityArray.size(); i++) {
          Entity_model data = entityArray.get(i);
          //dateString = data.CreatedDate.replace("/Date(", "").replace("+0530)/", "");

          data.CreatedDate = Methods.getFormattedDate(data.CreatedDate);
          Constants.StorebizEnterpriseQueries.add(data);
        }
      }
      if (bus != null) bus.post(new BzQueryEvent(null, Constants.StorebizEnterpriseQueries));
    } catch (Exception e) {
      e.printStackTrace();
      if (bus != null) bus.post("Getting error " + e.getLocalizedMessage());
    }
  }

  private void parseMessages(ArrayList<Business_Enquiry_Model> response) {
    Constants.StorebizQueries = new ArrayList<>();
    String dateString = null, dateTime = null;
    String formatted = "";
    String temp[] = new String[0];
    if (response != null && response.size() >= 1) {
      for (int i = 0; i < response.size(); i++) {
        Business_Enquiry_Model data = response.get(i);
        try {
          dateString = data.createdOn.replace("/Date(", "").replace("+0000)/", "").replace("+0530)/", "");
          //data.createdOn = Methods.getFormattedDate(dateString);
          //long timestamp = Long.valueOf(dateString);
          String formattedDate = Methods.getUTC_To_Local(Long.parseLong(dateString));
          if (formattedDate != null) data.createdOn = formattedDate;
        } catch (Exception e) {
          e.printStackTrace();
        }
        Constants.StorebizQueries.add(data);
      }
    }
    if (bus != null) bus.post(new BzQueryEvent(Constants.StorebizQueries, null));
  }

  public interface Business_enquiries_interface {
    //GET ENTERPRIse
    //baseURL = Constants.NOW_FLOATS_API_URL+ "/Dashboard/v1/"+Constants.parentID+"/topmessagedetails?clientId="+
    //Constants.clientId+"&startDate="+startDate+"&endDate="+endDate+"&scope="+scope;
//        @GET("/Dashboard/v1/{ParentId}/topmessagedetails")
//        public void getMessagesMethod(@Path("ParentId") String ParentId,
//                                      @Field("clientId") String clientId,
//                                      @Field("startDate") String startDate,
//                                      @Field("endDate") String endDate,
//                                      @Field("scope") int scope,
//                                      Callback<Business_Enquiry_Enterprise_Model> callback);

    @GET("/Dashboard/v1/{ParentId}/topmessagedetails")
    public void getMessagesMethod(@Path("ParentId") String ParentId, @QueryMap Map<String, String> map,
                                  Callback<Business_Enquiry_Enterprise_Model> callback);

    //https://api.withfloats.com/Discover/v1/floatingPoint/usermessages/5406bd254ec0a40d409f2b2b?
    // clientId=2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21


    //        @FormUrlEncoded
    @Headers({"Content-Type: application/json"})
    @POST("/Discover/v1/floatingPoint/usermessages/{fpId}")
    public void postMessagesMethod(@Body JSONObject empty, @QueryMap HashMap<String, String> map, @Path("fpId") String fpId, Callback<ArrayList<Business_Enquiry_Model>> callback);

    @GET("/dashboard/v1/floatingPoint/usermessageswithoffset/{ParentId}")
    public void getMessagesEnterPriseMethod(@Path("ParentId") String ParentId, @QueryMap Map<String, String> map, Callback<ArrayList<Business_Enquiry_Model>> callback);
  }

}