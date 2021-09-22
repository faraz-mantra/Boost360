package com.nowfloats.signup.UI.API;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 12-01-2015.
 */
public class API_Layer_Signup {

    static String tag = null;

    public static String getTag(final Context context, String name, String country, String city, String category) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject obj = new JSONObject();
        final SignUp_Interface signUp = (SignUp_Interface) context;
        try {
            obj.put("name", name);
            obj.put("city", city);
            obj.put("country", country);
            obj.put("category", category);
            obj.put("clientId", Constants.clientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/suggestTag";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        try {

                            //signUp.tagStatus("Success");
                            tag = response.toString();
                            signUp.tagStatus("Success", tag);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                // pd.cancel();
                try {
                    String mesg = error.getMessage();
                    //signUp.tagStatus(mesg);
                    if (mesg.contains("of type java.lang.String cannot be converted to JSONObject")) {
                        //org.json.JSONException: Value SAI of type java.lang.String cannot be converted to JSONObject
                        mesg = mesg.replace("org.json.JSONException: Value ", "");
                        mesg = mesg.replace(" of type java.lang.String cannot be converted to JSONObject", "");
                        mesg = mesg.trim();
                        tag = mesg;
                        signUp.tagStatus("Success", tag);
                    }
                } catch (Exception e) {

                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", Utils.getAuthToken());
                return headers;
            }
        };

        queue.add(jsObjRequest);
        return tag;

    }

    public static void checkUniqueNumber(Context context, final String mobileNumber, final String countryCode) {

        final SignUp_Interface checkUniqueNumberInterface = (SignUp_Interface) context;


        new AsyncTask<Void, String, String>() {

            @Override
            protected void onPreExecute() {
//                pd = ProgressDialog.show(BDetailsFrag.this.getActivity(), null,
//                        "Checking contact number..");
//                pd.setCancelable(true);
                checkUniqueNumberInterface.CheckUniqueNumber_preExecute("PreExecute");
            }

            @Override
            protected void onPostExecute(String result) {

                if (!Util.isNullOrEmpty(result)) {
                    if (result.equals("false")) {
                        checkUniqueNumberInterface.CheckUniqueNumber_postExecute("Success");
                        checkUniqueNumberInterface.CheckUniqueNumber_postExecute("Success", mobileNumber);

                    } else {
                        checkUniqueNumberInterface.CheckUniqueNumber_postExecute("Failure");
                        checkUniqueNumberInterface.CheckUniqueNumber_postExecute(result, mobileNumber);
                    }

                } else {
                    checkUniqueNumberInterface.CheckUniqueNumber_postExecute("Failure");
                    checkUniqueNumberInterface.CheckUniqueNumber_postExecute("Error", mobileNumber);
                }
            }

            @Override
            protected String doInBackground(Void... params) {

                String response = "";

                JSONObject obj = new JSONObject();
                try {
                    obj.put("clientId", Constants.clientId);
                    obj.put("mobile", mobileNumber);
                    obj.put("countryCode", countryCode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String content = obj.toString();

                response = Util.getDataFromServer(content, Constants.HTTP_POST,
                        Constants.uniqueNumber);

                return response;
            }

        }.execute((Void) null);


    }


    public interface SignUp_Interface {

        public void tagStatus(String status, String tag);

        public void CheckUniqueNumber_preExecute(String value);

        public void CheckUniqueNumber_postExecute(String value);

        public void CheckUniqueNumber_postExecute(String value, String phoneNumber);

    }


}


