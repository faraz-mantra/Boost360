package com.nowfloats.signup.UI.API;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by NowFloatsDev on 16/04/2015.
 */
public class Valid_Email {

    public static String validateEmail(Context context, String email) {
        RequestQueue queue = Volley.newRequestQueue(context);

        final Valid_Email_Interface valid_email_interface = (Valid_Email_Interface) context;

        String url = "https://bpi.briteverify.com/emails.json?" +
                "address=" + email +
                "&" +
                "apikey=e5f5fb5a-8e1f-422e-9d25-a67a16018d47";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                try {
                    valid_email_interface.emailValidated(response.getString("status"));
                    Log.d("Email Valid : ", "Email : " + response.getString("status"));
                    Log.d("Email Valid : ", "Email : " + response.getString("status"));

                    //  Log.d("Valid Email","Valid Email Response: "+response);
                    //  Log.d("Valid Email","Valid Email Response: "+response);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub


                Log.d("Error", "Error : " + error);
                // signUp.tagStatus("Success",tag);

            }
        });

        queue.add(jsObjRequest);
        return "";


    }

    public interface Valid_Email_Interface {

        public void emailValidated(String emailResult);
    }
}
