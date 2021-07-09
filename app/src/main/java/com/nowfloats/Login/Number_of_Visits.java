package com.nowfloats.Login;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NowFloatsDev on 27/04/2015.
 */
public class Number_of_Visits {

    Activity activity ;

    private RequestQueue queue ;

    public Number_of_Visits(Activity activity)
    {
        this.activity = activity ;
        queue = AppController.getInstance().getRequestQueue();
    }

    public void getNumberOfVisits(String websiteName)
    {
        String visitorsUri = Constants.LoadStoreURI+websiteName+"/visitorCount?clientId="+Constants.clientId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (
                        Request.Method.GET,
                visitorsUri,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                         Log.d("Response", "Response : " + response);
                        Log.d("Response", "Response : " + response);
                    }
                },
                        new Response.ErrorListener()
                        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
               Log.d("Error","error : "+error);
            }
         }
         )

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json");

                return headers;
            }
        };


        queue.add(jsonObjectRequest);


    }


}
