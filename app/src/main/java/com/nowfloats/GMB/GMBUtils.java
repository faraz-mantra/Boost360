package com.nowfloats.GMB;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class GMBUtils {


    Context context;

    UserSessionManager sessionManager;

    public GMBUtils(Context context, UserSessionManager sessionManager){

        this.context = context;

        this.sessionManager = sessionManager;

    }


    public void sendDetailsToGMB() throws JSONException {

     /*   DisplayLog(session.getFPDetails(Key_Preferences.LATITUDE)+"\n"+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER)+"\n"
                +session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME)+"\n"
                +session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)+"\n"+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
                + session.getFPDetails(GET_FP_DETAILS_CATEGORY)+"\n"+session.getFPDetails(GET_FP_DETAILS_CITY));*/



        JSONObject parent = new JSONObject();

        parent.put("nowfloats_client_id", Constants.clientId);

        parent.put("nowfloats_id",sessionManager.getFPID());

        parent.put("operation","create");

        parent.put("filter","updatepage");

        parent.put("boost_priority",9);

        parent.put("callback_url",sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE));

        JSONArray identifiers = new JSONArray();

        identifiers.put(0,"googlemybusiness");

        JSONObject social_data = new JSONObject();

        social_data.put("name",sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

        social_data.put("phone",sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));

        social_data.put("description",sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

        social_data.put("website",sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE));

        parent.put("identifiers",identifiers);

        parent.put("social_data",social_data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXProcessUrl, parent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(Constants.LogTag, response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(Constants.LogTag, error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("Content-type", "application/json");
                map.put("key", "78234i249123102398");
                map.put("pwd", "JYUYTJH*(*&BKJ787686876bbbhl");

                return map;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

}
