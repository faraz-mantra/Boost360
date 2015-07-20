package com.nowfloats.BusinessProfile.UI.API;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by NowFloatsDev on 16/04/2015.
 */
public class Facebook_Auto_Publish_API {

    public static String autoPublish(Context context,String fpID)
    {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject obj = new JSONObject();

        String url = "https://api.withfloats.com/Discover/v1/FloatingPoint/" +
                "GetFacebookPullRegistrations/" +
                fpID+"/" +
                Constants.clientId;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response)
            {
                // TODO Auto-generated method stub
                try
                {
                    //Log.d("Email Valid : ", "Email : " + response.getString("status"));
                    //Log.d("Email Valid : ","Email : "+response.getString("status"));

                    MixPanelController.setProperties("FacebookAutoPublish", response.getString("AutoPublish"));
                      //Log.d("PullRegistrations","GetFacebookPullRegistrations "+response.getString("AutoPublish"));
                      //Log.d("Valid Email","Valid Email Response: "+response);


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub


                Log.d("Error","Error : "+error);
                // signUp.tagStatus("Success",tag);

            }
        });

        queue.add(jsObjRequest);
        return "";


    }
}
