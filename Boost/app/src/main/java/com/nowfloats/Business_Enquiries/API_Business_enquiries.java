package com.nowfloats.Business_Enquiries;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Enterprise_Model;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.BzQueryEvent;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Volley.AppController;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.squareup.otto.Bus;

import java.text.DateFormat;
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
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by Dell on 28-01-2015.
 */
public class API_Business_enquiries {

    private RequestQueue queue ;
    private String fpIdPrefs = null ;
    private UserSessionManager session;
    private Bus bus;
    public API_Business_enquiries(Context context)
    {
        queue = AppController.getInstance().getRequestQueue();
    }

    public API_Business_enquiries(Bus bus,UserSessionManager session)
    {
        queue = AppController.getInstance().getRequestQueue();
        fpIdPrefs = session.getFPID();
        this.session = session;
        this.bus = bus;
    }

    public interface  Business_enquiries_interface {
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
        public void getMessagesMethod(@Path("ParentId") String ParentId,@QueryMap Map<String,String> map,
                                      Callback<Business_Enquiry_Enterprise_Model> callback);

        //https://api.withfloats.com/Discover/v1/floatingPoint/usermessages/5406bd254ec0a40d409f2b2b?
        // clientId=2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21


//        @FormUrlEncoded
        @Headers({"Content-Type: application/json"})
        @POST("/Discover/v1/floatingPoint/usermessages/{fpId}")
        public void postMessagesMethod(@QueryMap HashMap<String,String> map,@Path("fpId") String fpId,Callback<ArrayList<Business_Enquiry_Model>> callback);

    }

    public void getMessages() {
        try {
            if (session.getISEnterprise().equals("true")) {
                String startDate = "2014-08-06";
                String endDate = "2015-06-05";
                try{
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    endDate = sdf.format(c.getTime());
                }catch(Exception e){e.printStackTrace();}

                String scope = "1";
                try{
                    Business_enquiries_interface BE = Constants.restAdapter.create(Business_enquiries_interface.class);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("clientId", Constants.clientId);
                    params.put("startDate", startDate);
                    params.put("endDate", endDate);
                    params.put("scope", scope);

                    BE.getMessagesMethod(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID),
                            params, new Callback<Business_Enquiry_Enterprise_Model>() {
                        @Override
                        public void success(Business_Enquiry_Enterprise_Model business_enquiry_models, Response response) {
                            parseEnterpriseEnquiryMessages(business_enquiry_models);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.i("Enterprise_enquiry", "" + error.getMessage());
                        }
                    });
                }catch (Exception e){e.printStackTrace();}
            } else {
                HashMap<String,String> map = new HashMap<>();
                map.put("clientId",Constants.clientId);
                try{
                    Business_enquiries_interface BE = Constants.restAdapter.create(Business_enquiries_interface.class);

                    BE.postMessagesMethod( map,fpIdPrefs, new Callback<ArrayList<Business_Enquiry_Model>>() {

                        @Override
                        public void success(ArrayList<Business_Enquiry_Model> data, Response response) {
                            parseMessages(data);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Business_enquiry", " Error : " + error.getMessage());
                        }
                    });
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseEnterpriseEnquiryMessages(Business_Enquiry_Enterprise_Model response) {
        Constants.StorebizEnterpriseQueries = new ArrayList<>();
        String formatted = "";
        String temp [] = new String[0];
        String dateString = null,dateTime = null ;

        try {
            ArrayList<Entity_model> entityArray = response.Entity;
            if(entityArray.size() >= 1 )
            {
                for(int i = 0 ; i < entityArray.size() ; i++)
                {
                    Entity_model data = entityArray.get(i);
                    dateString = data.CreatedDate.replace("/Date(", "").replace("+0530)/", "");

                    Long epochTime = Long.parseLong(dateString);

                    Date date = new Date(epochTime);
//                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy HH:mm:ss
                    DateFormat format = new SimpleDateFormat("MMMM dd, yyyy  |  HH:mm aa");
                    format.setTimeZone(TimeZone.getDefault());
                    if(date != null)
                        dateTime = format.format(date);
                    formatted = dateTime;
//                    if(!Util.isNullOrEmpty(dateTime)){
//                        temp = dateTime.split(" ");
//                        temp = temp[0].split("-");
//                    }
//                    if(temp.length >0){
//                        int month = Integer.parseInt(temp[1]);
//                        switch (month) {
//                            case 01:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" January, "+temp[2];
//                                break;
//                            case 2:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" February, "+temp[2];
//                                break;
//                            case 3:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" March, "+temp[2];
//                                break;
//                            case 4:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" April, "+temp[2];
//                                break;
//                            case 5:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" May, "+temp[2];
//                                break;
//                            case 6:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" June, "+temp[2];
//                                break;
//                            case 7:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" July, "+temp[2];
//                                break;
//                            case 8:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" August, "+temp[2];
//                                break;
//                            case 9:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" September, "+temp[2];
//                                break;
//                            case 10:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" October, "+temp[2];
//                                break;
//                            case 11:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" November, "+temp[2];
//                                break;
//                            case 12:
//                                temp[0] = Util.AddSuffixForDay(temp[0]);
//                                formatted = temp[0]+" December, "+temp[2];
//                                break;
//                        }
//                    }

                    data.CreatedDate = formatted.replace("am","AM").replace("pm","PM");
                    Constants.StorebizEnterpriseQueries.add(data);
                }
                }
                if (bus!=null)
                    bus.post(new BzQueryEvent(null,Constants.StorebizEnterpriseQueries));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseMessages(ArrayList<Business_Enquiry_Model> response){
        Constants.StorebizQueries = new ArrayList<>();
        String dateString = null,dateTime = null;
        String formatted = "";
        String temp [] = new String[0];
        if (response.size() >= 1)
        {
            for(int i = 0 ; i < response.size() ;i++)
            {
                Business_Enquiry_Model data = response.get(i);
                dateString = data.createdOn.replace("/Date(", "").replace("+0530)/", "");

                Long epochTime = Long.parseLong(dateString);

                Date date = new Date(epochTime);
//                DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy HH:mm:ss
                DateFormat format = new SimpleDateFormat("MMMM dd, yyyy  |  HH:mm aa");
                format.setTimeZone(TimeZone.getDefault());
                if(date != null)
                    dateTime = format.format(date);
                formatted = dateTime;
//                if(!Util.isNullOrEmpty(dateTime)){
//                    temp = dateTime.split(" ");
//                    temp = temp[0].split("-");
//                }
//                if(temp.length >0){
//                    int month = Integer.parseInt(temp[1]);
//                    switch (month) {
//                        case 01:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" January, "+temp[2];
//                            break;
//                        case 2:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" February, "+temp[2];
//                            break;
//                        case 3:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" March, "+temp[2];
//                            break;
//                        case 4:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" April, "+temp[2];
//                            break;
//                        case 5:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" May, "+temp[2];
//                            break;
//                        case 6:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" June, "+temp[2];
//                            break;
//                        case 7:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" July, "+temp[2];
//                            break;
//                        case 8:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" August, "+temp[2];
//                            break;
//                        case 9:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" September, "+temp[2];
//                            break;
//                        case 10:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" October, "+temp[2];
//                            break;
//                        case 11:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" November, "+temp[2];
//                            break;
//                        case 12:
//                            temp[0] = Util.AddSuffixForDay(temp[0]);
//                            formatted = temp[0]+" December, "+temp[2];
//                            break;
//                    }
//                }
                data.createdOn = formatted.replace("am","AM").replace("pm","PM");
                Constants.StorebizQueries.add(data);
            }
        }
        if (bus!=null)
            bus.post(new BzQueryEvent(Constants.StorebizQueries,null));
    }
}