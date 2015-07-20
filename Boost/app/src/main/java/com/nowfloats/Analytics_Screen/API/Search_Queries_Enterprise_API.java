package com.nowfloats.Analytics_Screen.API;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Volley.AppController;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.Specific;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by NowFloatsDev on 03/05/2015.
 */
public class Search_Queries_Enterprise_API {

    private Activity activityContext;
    private String clientID ;
    private String fpTag,fpIdentifierType ;
    private int offset ;
    private RequestQueue queue ;



    public Search_Queries_Enterprise_API(Activity activity)
    {
        activityContext = activity ;
        queue = AppController.getInstance().getRequestQueue();


    }

    public void getSearchQueries(String clientID,String fpTag,String fpIdentifierType,int offset)
    {
        this.clientID = clientID ;
        this.fpTag = fpTag ;
        this.fpIdentifierType = fpIdentifierType ;
        this.offset = offset ;

        JSONObject obj = new JSONObject();

        try
        {
            obj.put("clientId", clientID);
           // obj.put("fpTag", Constants.parentID);
            obj.put("fpTag", "TECHNEWS"); // TODO
            obj.put("fpIdentifierType", fpIdentifierType);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        String url = Constants.NOW_FLOATS_API_URL+"/Search/v1/queries/report?offset="+offset;

        JsonArrayRequest jsObjRequest = new JsonArrayRequest (Request.Method.POST, url, obj,
                new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray  response)
                    {
                        Log.d("Search_Queries"," Enterprise APP : "+response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                // pd.cancel();
                String mesg = error.getMessage();

                // Log.d(TAG,"Error : "+error.networkResponse);

            }
        });

//        int socketTimeout = 5000;//3 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsObjRequest.setRetryPolicy(policy);
        queue.add(jsObjRequest);



    }
}
