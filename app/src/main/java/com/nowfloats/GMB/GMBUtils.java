package com.nowfloats.GMB;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;




public class GMBUtils {


    Context context;

    private int requestCode = 332;

    private final String content_type = "application/json";

    private final String Pwd = "78234i249123102398";

    private String TAG = "android23235616";
    
    private final String Key = "JYUYTJH*(*&BKJ787686876bbbhl";

    private int showLocations = 2323, showAccounts = 345345;

    private int GMBPollinCount = 0;

    private String locationId, locationName, GMBAccountId, GMBUserAccountName,GMBRefreshtoken,
    GMBAuthToken,GMBTokenExpiry;

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

                        BoostLog.i(Constants.LogTag, response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("Content-type", content_type);
                map.put("key", Key);
                map.put("pwd", Pwd);

                return map;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }




    public void GMBUpdateAccessToken(final SocialSharingFragment socialSharingFragment, UserSessionManager session) {

        socialSharingFragment.showLoader("Finishing up.");

        JSONObject parent = new JSONObject();

        JSONObject accessTokenJson = new JSONObject();

        try {
            accessTokenJson.put("Type", "googlemybusiness");

            accessTokenJson.put("UserAccountId", "accounts/"+GMBAccountId);

            accessTokenJson.put("UserAccountName", GMBUserAccountName);

            accessTokenJson.put("LocationId", "accounts/"+GMBAccountId+"/locations/"+locationId);

            accessTokenJson.put("LocationName", locationName);

            accessTokenJson.put("token_expiry", GMBTokenExpiry);

            accessTokenJson.put("invalid", false);

            JSONObject token_response = new JSONObject();

            token_response.put("access_token", GMBAuthToken);

            token_response.put("token_type", "Bearer");

            token_response.put("expires_in", 3600);

            token_response.put("refresh_token", GMBRefreshtoken);

            accessTokenJson.put("token_response", token_response);

            accessTokenJson.put("refresh_token", GMBRefreshtoken);

            accessTokenJson.put("UserAccessTokenKey", GMBAuthToken);

            parent.put("floatingPointId", session.getFPID());

            parent.put("clientId", Constants.clientId);

            parent.put("accessToken", accessTokenJson);

            BoostLog.i(TAG, parent.toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXUpdateAcessToken, parent,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            BoostLog.i(Constants.LogTag, response.toString());

                            socialSharingFragment.CloseDialogBoxes();


                            Toast.makeText(context, "Your business " + "has been synced with Google My Business", Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    BoostLog.i(Constants.LogTag, error.toString());
                  socialSharingFragment.CloseDialogBoxes();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();

                    map.put("Content-type", content_type);


                    return map;
                }
            };

            Volley.newRequestQueue(context).add(jsonObjectRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void checkIfGMBisSynced(Context context,String fp_id,final SocialSharingFragment socialSharingFragment){

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , Constants.NFXgetAcessToken + "?nowfloats_id=" + fp_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObject = new JSONObject(response);

                    JSONArray arr = responseObject.getJSONArray("NFXAccessTokens");

                    JSONObject childObject = arr.getJSONObject(0);

                    String refresh_token = childObject.getString("refresh_token"); //checking the validity of response

                    socialSharingFragment.handleGMBCheckbox(true);

                    BoostLog.i(Constants.LogTag,"its synced");



                } catch (JSONException e) {
                    e.printStackTrace();
                    BoostLog.i(Constants.LogTag,"its not synced");

                    socialSharingFragment.handleGMBCheckbox(false);

                    socialSharingFragment.GMBSignOutUserfromGoogle(false);


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag,"its not synced "+error.toString());

                socialSharingFragment.handleGMBCheckbox(false);

                socialSharingFragment.GMBSignOutUserfromGoogle(false);

            }
        });

        Volley.newRequestQueue(context).add(stringRequest);


    }

    public int getShowLocations(){
        return showLocations;
    }

    public int getShowAccounts(){
        return showAccounts;
    }


    public void getLocations(Context context,final SocialSharingFragment socialSharingFragment,final int showLocations) {



        StringRequest sr = new StringRequest(Request.Method.GET, Constants.GMBgetLocationUrl + GMBAccountId
                + "/locations?access_token=" + GMBAuthToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                BoostLog.i(Constants.LogTag, response);

                try {
                    JSONArray arr = new JSONObject(response).getJSONArray("locations");
                    socialSharingFragment.showBuilder(arr,showLocations);
                    socialSharingFragment.CloseDialogBoxes();

                } catch (JSONException e) {

                    AlertDialog builder = socialSharingFragment.getBuilder();
                    builder.cancel();
                    e.printStackTrace();
                    socialSharingFragment.CloseDialogBoxes();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, "here 3"+ error.toString()+" "+GMBAuthToken);



                socialSharingFragment.CloseDialogBoxes();


            }
        });
        Volley.newRequestQueue(context).add(sr);

    }


    public void GMBRemoveUser(Context context, UserSessionManager session, final SocialSharingFragment socialSharingFragment) {

        JSONObject mainObject = new JSONObject();

        try {
            mainObject.put("floatingPointId",session.getFPID());

            mainObject.put("clientId",Constants.clientId);

            JSONObject accessToken = new JSONObject();

            accessToken.put("Type", "googlemybusiness");

            accessToken.put("UserAccountId","");

            accessToken.put("UserAccountName","");

            accessToken.put("LocationId","");

            accessToken.put("LocationName","");

            accessToken.put("token_expiry","");

            accessToken.put("invalid",false);

            JSONObject tokenResponse = new JSONObject();

            tokenResponse.put("access_token","");

            tokenResponse.put("token_type","");

            tokenResponse.put("expires_in",3600);

            tokenResponse.put("refresh_token","");

            accessToken.put("token_response",tokenResponse);

            accessToken.put("token_type","");

            accessToken.put("UserAccessTokenKey","");

            mainObject.put("accessToken",accessToken);





        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXUpdateAcessToken, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BoostLog.i(Constants.LogTag, response.toString());

                        socialSharingFragment.CloseDialogBoxes();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, error.toString());
                socialSharingFragment.CloseDialogBoxes();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("Content-type", content_type);


                return map;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);


    }


    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setGMBAuthToken(String GMBAuthToken) {
        this.GMBAuthToken = GMBAuthToken;
    }

    public void setGMBTokenExpiry(String GMBTokenExpiry) {
        this.GMBTokenExpiry = GMBTokenExpiry;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setGMBAccountId(String GMBAccountId) {
        this.GMBAccountId = GMBAccountId;
    }

    public void setGMBUserAccountName(String GMBUserAccountName) {
        this.GMBUserAccountName = GMBUserAccountName;
    }

    public void setGMBRefreshtoken(String GMBRefreshtoken) {
        this.GMBRefreshtoken = GMBRefreshtoken;
    }

    public void getAuthCodeFromServer(final Context context, final String np_id, final String auth_code, final SocialSharingFragment socialSharingFragment) {

        JSONObject child = new JSONObject();

        try {
            child.put("nowfloats_client_id", Constants.clientId);
            child.put("nowfloats_id", np_id);
            child.put("operation", "create");
            child.put("filter", "access_token");
            child.put("boost_priority", 9);
            child.put("callback_url", "https://bookshaukeen.nowfloats.com/");
            JSONArray arr = new JSONArray();
            arr.put(0, "googlemybusiness");
            JSONObject social_data = new JSONObject();
            social_data.put("authorization_code", auth_code);
            child.put("social_data", social_data);
            child.put("identifiers", arr);


        } catch (JSONException e) {
            e.printStackTrace();

            socialSharingFragment.CloseDialogBoxes();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXProcessUrl, child,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BoostLog.i(Constants.LogTag, response.toString());

                        continueProcessForGMB(context,np_id, auth_code,socialSharingFragment);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, error.toString());

                socialSharingFragment.CloseDialogBoxes();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("Content-type", content_type);
                map.put("key", Key);
                map.put("pwd", Pwd);

                return map;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void continueProcessForGMB(final Context context, final String np_id, final String auth_code, final SocialSharingFragment socialSharingFragment) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , Constants.NFXgetAcessToken+"?nowfloats_id=" + np_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                BoostLog.i(Constants.LogTag, response);

                BoostLog.i(Constants.LogTag, auth_code);


                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray arr = object.getJSONArray("NFXAccessTokens");

                    JSONObject childObject = arr.getJSONObject(0);

                    String refresh_token = childObject.getString("refresh_token");

                    GMBRefreshtoken = refresh_token;

                    String auth_token = childObject.getJSONObject("token_response").getString("access_token");

                    GMBTokenExpiry = childObject.getString("token_expiry");

                    GMBAuthToken = auth_token;

                    BoostLog.i(Constants.LogTag, "refresh_token: " + refresh_token + "\n" + "auth_code: " + auth_token);



                    GMBGetAccountNumber(context,auth_token,socialSharingFragment);

                    socialSharingFragment.CloseDialogBoxes();


                } catch (JSONException e) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {



                            if (GMBPollinCount < 15) {

                                continueProcessForGMB(context,np_id, auth_code,socialSharingFragment);

                                BoostLog.i(Constants.LogTag, "Polling Count : " + GMBPollinCount + " , trying again");

                            } else {
                                socialSharingFragment.CloseDialogBoxes();
                            }
                        }
                    }, 2000);

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              

                BoostLog.i(Constants.LogTag, error.toString());
                socialSharingFragment.CloseDialogBoxes();
            }
        });

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void refreshGMB() {
        GMBUserAccountName = "";
        GMBAccountId = "";
        GMBAuthToken = "";
        GMBRefreshtoken = "";
        GMBPollinCount=0;
    }

    public void GMBGetAccountNumber(Context context, String at, final SocialSharingFragment socialSharingFragment) {

        if (at.length() > 0) {


            JsonObjectRequest sr = new JsonObjectRequest(Constants.GMBCallbackUrl + "?access_token=" + at, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            BoostLog.i(Constants.LogTag, response.toString());

                            try {
                                JSONArray arr = response.getJSONArray("accounts");

                                socialSharingFragment.CloseDialogBoxes();

                                socialSharingFragment.showBuilder(arr, showAccounts);

                            } catch (JSONException e) {
                                BoostLog.i(Constants.LogTag, "Accounts Not Found");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    BoostLog.i(Constants.LogTag, error.toString());
                    socialSharingFragment.CloseDialogBoxes();
                }
            }


            );


            Volley.newRequestQueue(context).add(sr);
        } else {
            socialSharingFragment.CloseDialogBoxes();
            BoostLog.i(Constants.LogTag, "invalid token");
        }
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }
}
