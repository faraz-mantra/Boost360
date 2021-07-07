package com.nowfloats.GMB;

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
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GMBHandler {


    public static int REQUEST_CODE = 332;
    private final String content_type = "application/json";
    private final String Pwd = "78234i249123102398";
    private final String Key = "JYUYTJH*(*&BKJ787686876bbbhl";
    private Context context;
    private String TAG = "android23235616";
    private int pollingCount = 0;

    private int showLocations = 2323, showAccounts = 345345;

    private String locationId, locationName, accountId, accountName, refreshToken,
            authToken, tokenExpiry;

    private UserSessionManager sessionManager;

    public GMBHandler(Context context, UserSessionManager sessionManager) {

        this.context = context;

        this.sessionManager = sessionManager;

    }


    public void sendDetailsToGMB(final boolean display) throws JSONException {

     /*   DisplayLog(session.getFPDetails(Key_Preferences.LATITUDE)+"\n"+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER)+"\n"
                +session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME)+"\n"
                +session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)+"\n"+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
                + session.getFPDetails(GET_FP_DETAILS_CATEGORY)+"\n"+session.getFPDetails(GET_FP_DETAILS_CITY));*/
        JSONObject parent = new JSONObject();


        parent.put("nowfloats_client_id", Constants.clientId);

        parent.put("nowfloats_id", sessionManager.getFPID());

        parent.put("operation", "create");

        parent.put("filter", "updatepage");

        parent.put("boost_priority", 9);

        parent.put("callback_url", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE));

        JSONArray identifiers = new JSONArray();

        identifiers.put(0, "googlemybusiness");

        JSONObject social_data = new JSONObject();

        social_data.put("name", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

        social_data.put("phone", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));

        social_data.put("description", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

        social_data.put("website", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE));

        parent.put("identifiers", identifiers);

        parent.put("social_data", social_data);


        BoostLog.i(Constants.LogTag, parent.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXProcessUrl, parent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BoostLog.i(Constants.LogTag, response.toString());

//                        if (display)
//                            Toast.makeText(context, context.getString(R.string.gmb_upload_data), Toast.LENGTH_LONG).show();

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


    public void updateAccessToken(final SocialSharingFragment socialSharingFragment) {

        socialSharingFragment.showLoader("Finishing up.");

        JSONObject parent = new JSONObject();

        JSONObject accessTokenJson = new JSONObject();

        try {
            accessTokenJson.put("Type", "googlemybusiness");

            accessTokenJson.put("UserAccountId", "accounts/" + accountId);

            accessTokenJson.put("UserAccountName", accountName);

            accessTokenJson.put("LocationId", "accounts/" + accountId + "/locations/" + locationId);

            accessTokenJson.put("LocationName", locationName);

            accessTokenJson.put("token_expiry", tokenExpiry);

            accessTokenJson.put("invalid", false);

            JSONObject token_response = new JSONObject();

            token_response.put("access_token", authToken);

            token_response.put("token_type", "Bearer");

            token_response.put("expires_in", 3600);

            token_response.put("refresh_token", refreshToken);

            accessTokenJson.put("token_response", token_response);

            accessTokenJson.put("refresh_token", refreshToken);

            accessTokenJson.put("UserAccessTokenKey", authToken);

            parent.put("floatingPointId", sessionManager.getFPID());

            parent.put("clientId", Constants.clientId);

            parent.put("accessToken", accessTokenJson);

            BoostLog.i(TAG, parent.toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXUpdateAcessToken, parent,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            BoostLog.i(Constants.LogTag, response.toString());

                            socialSharingFragment.hideLoader();

                            Toast.makeText(context, context.getString(R.string.gmb_integration_Successful), Toast.LENGTH_LONG).show();

                            socialSharingFragment.handleGMBCheckbox(true);


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    BoostLog.i(Constants.LogTag, error.toString());
                    socialSharingFragment.hideLoader();
                    socialSharingFragment.handleGMBCheckbox(false);

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
            socialSharingFragment.handleGMBCheckbox(false);
        }

    }

    public void isSynced(final SocialSharingFragment socialSharingFragment) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , Constants.NFXgetAcessToken + "?nowfloats_id=" + sessionManager.getFPID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObject = new JSONObject(response);

                    JSONArray arr = responseObject.getJSONArray("NFXAccessTokens");

                    JSONObject childObject = arr.getJSONObject(0);

                    String refresh_token = childObject.getString("refresh_token"); //checking the validity of response

                    socialSharingFragment.handleGMBCheckbox(true);

                    BoostLog.i(Constants.LogTag, "its synced");


                } catch (JSONException e) {
                    e.printStackTrace();
                    BoostLog.i(Constants.LogTag, "its not synced");

                    socialSharingFragment.handleGMBCheckbox(false);

                    socialSharingFragment.gmbSignOutUserfromGoogle(false);


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, "its not synced " + error.toString());

                socialSharingFragment.handleGMBCheckbox(false);

                socialSharingFragment.gmbSignOutUserfromGoogle(false);

            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public int getShowLocations() {
        return showLocations;
    }

    public int getShowAccounts() {
        return showAccounts;
    }


    public void getLocations(final SocialSharingFragment socialSharingFragment) {


        StringRequest sr = new StringRequest(Request.Method.GET, Constants.GMBgetLocationUrl + accountId
                + "/locations?access_token=" + authToken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                BoostLog.i(Constants.LogTag, response);

                try {
                    JSONArray arr = new JSONObject(response).getJSONArray("locations");
                    socialSharingFragment.showBuilder(arr, showLocations);
                    socialSharingFragment.hideLoader();

                } catch (JSONException e) {
                    Toast.makeText(context, context.getString(R.string.gmb_error_message), Toast.LENGTH_LONG).show();
                    socialSharingFragment.closeDialog();
                    e.printStackTrace();
                    socialSharingFragment.hideLoader();
                    socialSharingFragment.handleGMBCheckbox(false);
                    removeUser(socialSharingFragment);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, "here 3" + error.toString() + " " + authToken);


                socialSharingFragment.hideLoader();

                socialSharingFragment.handleGMBCheckbox(false);


            }
        });
        Volley.newRequestQueue(context).add(sr);

    }


    public void removeUser(final SocialSharingFragment socialSharingFragment) {

        JSONObject mainObject = new JSONObject();

        try {
            mainObject.put("floatingPointId", sessionManager.getFPID());

            mainObject.put("clientId", Constants.clientId);

            JSONObject accessToken = new JSONObject();

            accessToken.put("Type", "googlemybusiness");

            accessToken.put("UserAccountId", "");

            accessToken.put("UserAccountName", "");

            accessToken.put("LocationId", "");

            accessToken.put("LocationName", "");

            accessToken.put("token_expiry", "");

            accessToken.put("invalid", false);

            JSONObject tokenResponse = new JSONObject();

            tokenResponse.put("access_token", "");

            tokenResponse.put("token_type", "");

            tokenResponse.put("expires_in", 3600);

            tokenResponse.put("refresh_token", "");

            accessToken.put("token_response", tokenResponse);

            accessToken.put("token_type", "");

            accessToken.put("UserAccessTokenKey", "");

            mainObject.put("accessToken", accessToken);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXUpdateAcessToken, mainObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BoostLog.i(Constants.LogTag, response.toString());

                        socialSharingFragment.hideLoader();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, error.toString());
                socialSharingFragment.hideLoader();

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


    public void postAuthCodeFromServer(final String authCode, final SocialSharingFragment socialSharingFragment) {

        JSONObject child = new JSONObject();

        try {
            child.put("nowfloats_client_id", Constants.clientId);
            child.put("nowfloats_id", sessionManager.getFPID());
            child.put("operation", "create");
            child.put("filter", "access_token");
            child.put("boost_priority", 9);
            child.put("callback_url", "https://bookshaukeen.nowfloats.com/");
            JSONArray arr = new JSONArray();
            arr.put(0, "googlemybusiness");
            JSONObject social_data = new JSONObject();
            social_data.put("authorization_code", authCode);
            child.put("social_data", social_data);
            child.put("identifiers", arr);

        } catch (JSONException e) {
            e.printStackTrace();
            socialSharingFragment.hideLoader();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.NFXProcessUrl, child,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BoostLog.i(Constants.LogTag, response.toString());

                        continueProcess(authCode, socialSharingFragment);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BoostLog.i(Constants.LogTag, error.toString());

                socialSharingFragment.hideLoader();

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

    public void continueProcess(final String authCode, final SocialSharingFragment socialSharingFragment) {


        BoostLog.i(Constants.LogTag, sessionManager.getFPID());

        StringRequest stringRequest = new StringRequest(Request.Method.GET
                , Constants.NFXgetAcessToken + "?nowfloats_id=" + sessionManager.getFPID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                BoostLog.i(Constants.LogTag, response);

                BoostLog.i(Constants.LogTag, authCode);


                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray arr = object.getJSONArray("NFXAccessTokens");

                    //JSONObject childObject = arr.getJSONObject(0);

                    JSONObject childObject = new JSONObject();


                    for (int i = 0; i < arr.length(); i++) {
                        childObject = arr.getJSONObject(i);
                        if (childObject.getString("Type").equals("googlemybusiness")) {

                            break;
                        } else {
                            if (pollingCount > 15) {
                                Toast.makeText(context, context.getString(R.string.gmb_error_message), Toast.LENGTH_LONG).show();
                                socialSharingFragment.hideLoader();
                            }
                        }
                    }
                    //String refresh_token = childObject.getString("refresh_token");

                    //efreshToken = refresh_token;

                    String auth_token = childObject.getJSONObject("token_response").getString("access_token");

                    String refresh_token = childObject.getJSONObject("token_response").getString("refresh_token");

                    refreshToken = refresh_token;

                    tokenExpiry = childObject.getString("token_expiry");

                    authToken = auth_token;

                    BoostLog.i(Constants.LogTag, "refresh_token: " + refresh_token + "\n" + "auth_code: " + auth_token);

                    socialSharingFragment.hideLoader();

                    getAccountNumber(auth_token, socialSharingFragment);


                } catch (JSONException e) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            pollingCount++;


                            if (pollingCount < 15) {

                                continueProcess(authCode, socialSharingFragment);

                                BoostLog.i(Constants.LogTag, "Polling Count : " + pollingCount + " , trying again");

                            } else {
                                socialSharingFragment.hideLoader();
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
                socialSharingFragment.hideLoader();
            }
        });

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void refreshGMB() {
        accountName = "";
        accountId = "";
        authToken = "";
        refreshToken = "";
        pollingCount = 0;
    }

    public void getAccountNumber(String at, final SocialSharingFragment socialSharingFragment) {

        if (at.length() > 0) {

            JsonObjectRequest sr = new JsonObjectRequest(Constants.GMBCallbackUrl + "?access_token=" + at, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            BoostLog.i(Constants.LogTag, response.toString());

                            try {
                                JSONArray arr = response.getJSONArray("accounts");

                                socialSharingFragment.hideLoader();

                                socialSharingFragment.showBuilder(arr, showAccounts);

                            } catch (JSONException e) {
                                BoostLog.i(Constants.LogTag, "Accounts Not Found");
                                socialSharingFragment.handleGMBCheckbox(false);
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    BoostLog.i(Constants.LogTag, error.toString());
                    socialSharingFragment.hideLoader();
                    socialSharingFragment.handleGMBCheckbox(false);
                }
            }


            );


            Volley.newRequestQueue(context).add(sr);
        } else {
            socialSharingFragment.hideLoader();
            BoostLog.i(Constants.LogTag, "invalid token");
        }
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
