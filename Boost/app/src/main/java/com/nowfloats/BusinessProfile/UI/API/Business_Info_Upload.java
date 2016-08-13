package com.nowfloats.BusinessProfile.UI.API;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;

import org.json.JSONObject;

//import com.android.volley.toolbox.Volley.AppController;

/**
 * Created by Dell on 16-01-2015.
 */
public class Business_Info_Upload {

    private Context appContext;

    private JSONObject uploadObject ;

    private RequestQueue queue;

    public Business_Info_Upload(Context context,JSONObject obj) {
        appContext = context;
        queue = AppController.getInstance().getRequestQueue();
        uploadObject = obj ;
    }

    public interface Business_Info_Interface {
        // public class


    }

    public void postToServer() {

        //JSONObject obj = new JSONObject();

        BoostLog.d("Upload Object",uploadObject.toString());

        String url = Constants.FpsUpdate;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, uploadObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                try {

                    BoostLog.d("Business_Info"," Response : "+response.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                // pd.cancel();
                String mesg = error.getMessage();

                if (mesg.contains("of type java.lang.String cannot be converted to JSONObject")) {
                    //org.json.JSONException: Value SAI of type java.lang.String cannot be converted to JSONObject
                    mesg = mesg.replace("org.json.JSONException: Value ", "");
                    mesg = mesg.replace(" of type java.lang.String cannot be converted to JSONObject", "");
                    mesg = mesg.trim();
                  }
            }
        });

        queue.add(jsObjRequest);


    }


}