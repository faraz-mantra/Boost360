package com.nowfloats.NFXApi;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RAJA on 12-07-2016.
 */
public class NfxRequestClient {
    private static final String MERCHANT_DATA = "merchant_data";
    private static final int ONE_MINUTE = 60000;
    /*
     * Request params
     */
    private final String APPACCESSTOKENKEY = "AppAccessTokenKey";
    private final String APPACCESSTOKENSECRET = "AppAccessTokenSecret";
    private final String TYPE = "Type";
    private final String NOWFLOATS_CLIENTID = "nowfloats_client_id";
    private final String USERACCESSTOKENKEY = "UserAccessTokenKey";
    private final String USERACCOUNTNAME = "UserAccountName";
    private final String USERACCESSTOKENSECRET = "UserAccessTokenSecret";
    private final String USERACCOUNTID = "UserAccountId";
    private final String ACCESSTOKEN = "accessToken";
    private final String CLIENTID = "clientId";
    private final String FLOATINGPOINTID = "floatingPointId";
    private final String NOWFLOATS_ID = "nowfloats_id";
    private final String OPERATION = "operation";
    private final String FILTER = "filter";
    private final String IDENTIFIERS = "identifiers";
    private final String FPIDENTIFIERTYPE = "fpIdentifierType";
    private final String SOCIAL_DATA = "social_data";
    private final String ABOUT = "about";
    private final String CATEGORY = "category";
    private final String DESCRIPTION = "description";
    private final String NAME = "name";
    private final String PICTURE = "picture";
    private final String COVER_PHOTO = "cover_photo";
    private final String ADDRESS = "address";
    private final String CITY = "city";
    private final String COUNTRY = "country";
    private final String MOBILE = "mobile_number";
    private final String WEBSITE = "website";
    private String mType;
    private String mUserAccessTokenKey;
    private String mAppAccessTokenKey;
    private String mUserAccessTokenSecret;
    private String mAppAccessTokenSecret;
    private String mUserAccountId;
    private String mFpId;
    private String mName;
    private NfxCallBackListener mCallBackListener;
    private int mCallType;

    private NfxRequestClient() {

    }


    public NfxRequestClient(NfxCallBackListener callBackListener) {
        this.mCallBackListener = callBackListener;
    }

    public String getmUserAccessTokenKey() {
        return mUserAccessTokenKey;
    }

    public NfxRequestClient setmUserAccessTokenKey(String mUserAccessTokenKey) {
        this.mUserAccessTokenKey = mUserAccessTokenKey;
        return this;
    }

    public String getmAppAccessTokenKey() {
        return mAppAccessTokenKey;
    }

    public NfxRequestClient setmAppAccessTokenKey(String mAppAccessTokenKey) {
        this.mAppAccessTokenKey = mAppAccessTokenKey;
        return this;
    }

    public String getmType() {
        return mType;
    }

    public NfxRequestClient setmType(String mType) {
        this.mType = mType;
        return this;
    }

    public String getmAppAccessTokenSecret() {
        return mAppAccessTokenSecret;
    }

    public NfxRequestClient setmAppAccessTokenSecret(String mAppAccessTokenSecret) {
        this.mAppAccessTokenSecret = mAppAccessTokenSecret;
        return this;
    }

    public String getmUserAccessTokenSecret() {
        return mUserAccessTokenSecret;
    }

    public NfxRequestClient setmUserAccessTokenSecret(String mUserAccessTokenSecret) {
        this.mUserAccessTokenSecret = mUserAccessTokenSecret;
        return this;
    }

    public String getmUserAccountId() {
        return mUserAccountId;
    }

    public NfxRequestClient setmUserAccountId(String mUserAccountId) {
        this.mUserAccountId = mUserAccountId;
        return this;
    }

    public String getmFpId() {
        return mFpId;
    }

    public NfxRequestClient setmFpId(String mFpId) {
        this.mFpId = mFpId;
        return this;
    }

    public int getmCallType() {
        return mCallType;
    }

    public NfxRequestClient setmCallType(int mCallType) {
        this.mCallType = mCallType;
        return this;
    }

    public String getmName() {
        return mName;
    }

    public NfxRequestClient setmName(String mName) {
        this.mName = mName;
        return this;

    }

    public void connectNfx() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.nfxUpdateTokens, getNfxParams(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mCallBackListener.nfxCallBack(response.toString(), getmCallType(), getmName());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBackListener.nfxCallBack("error", getmCallType(), getmName());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "78234i249123102398");
                map.put("pwd", "JYUYTJH*(*&BKJ787686876bbbhl");
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String resp =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers)
                            );
                    String jsonString = "{\"resp\":" + resp + "}";
                    BoostLog.d("JSON Response:", resp);
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
                //return new JSONObject("FACEBOOK");
            }
        };
        AppController.getInstance().addToRequstQueue(jsonObjectRequest);

    }

    public void nfxNoPageFound() {
        String url = Constants.nfxUpdateTokens + "?not_found=true";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST,
                url, getNoPageNfxParams(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mCallBackListener.nfxCallBack(response.toString(), mCallType, getmName());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBackListener.nfxCallBack("error", mCallType, getmName());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "78234i249123102398");
                map.put("pwd", "JYUYTJH*(*&BKJ787686876bbbhl");
                map.put("Content-Type", "application/json");
                return map;
            }
        };
        AppController.getInstance().addToRequstQueue(jsonObjectRequest);
    }

    public void createFBPage(String businessName, String businessDesciption, String businessCategory, String mobileNumber,
                             String logoURL, String imageURI, String fpURI, String address, String city, String country) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.nfxFBPageCreation, getNfxParamsFBPage(businessName, businessDesciption,
                businessCategory, mobileNumber, logoURL, imageURI, fpURI, address, city, country),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            mCallBackListener.nfxCallBack("error", getmCallType(), getmName());
                            return;
                        }

                        String message = response.optString("message");
                        String extraParam = "";
                        if ("success".equals(message)) {
                            Boolean imageUsed = response.optBoolean("default_image_used");
                            extraParam = imageUsed ? "_fbDefaultImage" : "_logoImage";
                        }
                        mCallBackListener.nfxCallBack(message + extraParam, getmCallType(), getmName());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.statusCode == 400) {
                    JSONObject res = new JSONObject();
                    try {
                        res.put("message", "profile_incomplete");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCallBackListener.nfxCallBack("profile_incomplete", getmCallType(), getmName());
                } else if (response == null || response.statusCode != 200) {
                    mCallBackListener.nfxCallBack("error", getmCallType(), getmName());
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "78234i249123102398");
                map.put("pwd", "JYUYTJH*(*&BKJ787686876bbbhl");
                map.put("Content-Type", "application/json");
                return map;
            }


            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode != 200) {
                    return null;
                }
                try {
                    String resp = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    BoostLog.d("JSON Response:", resp);
                    return Response.success(new JSONObject(resp),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
                //return new JSONObject("FACEBOOK");
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(ONE_MINUTE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequstQueue(jsonObjectRequest);

    }

    private JSONObject getNfxParamsFBPage(String businessName, String businessDesciption,
                                          String businessCategory, String mobileNumber, String logoURL, String imageURL, String fpURI,
                                          String address, String city, String country) {

        try {
            JSONObject internalParams1 = new JSONObject();
            internalParams1.put(ABOUT, "details");
            internalParams1.put(CATEGORY, businessCategory);
            internalParams1.put(NAME, businessName);
            internalParams1.put(DESCRIPTION, businessDesciption);
            internalParams1.put(PICTURE, logoURL);
            internalParams1.put(COVER_PHOTO, imageURL);
            internalParams1.put(WEBSITE, fpURI);

            JSONObject internalParams2 = new JSONObject();
            internalParams2.put(ADDRESS, address);
            internalParams2.put(MOBILE, mobileNumber);
            internalParams2.put(CITY, city);
            internalParams2.put(COUNTRY, country);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, "facebookpage");

            JSONObject param = new JSONObject();
            param.put(NOWFLOATS_CLIENTID, Constants.clientId);
            param.put(NOWFLOATS_ID, getmFpId());
            param.put(OPERATION, "create");
            param.put(FILTER, "facebookpage");
            param.put(IDENTIFIERS, jsonArray);
            param.put(SOCIAL_DATA, internalParams1);
            param.put(MERCHANT_DATA, internalParams2);
            BoostLog.d("JSON String", param.toString());
            //Log.v("ggg",param.toString());
            return param;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getNfxParams() {

        try {
            JSONObject internalParams = new JSONObject();
            internalParams.put(TYPE, getmType());
            internalParams.put(USERACCESSTOKENKEY, getmUserAccessTokenKey());
            internalParams.put(APPACCESSTOKENKEY, getmAppAccessTokenKey());
            internalParams.put(USERACCESSTOKENSECRET, getmUserAccessTokenSecret());
            internalParams.put(APPACCESSTOKENSECRET, getmAppAccessTokenSecret());
            internalParams.put(USERACCOUNTID, getmUserAccountId());
            internalParams.put(USERACCOUNTNAME, getmName());
            JSONObject param = new JSONObject();
            param.put(ACCESSTOKEN, internalParams);
            param.put(CLIENTID, Constants.clientId);
            param.put(FLOATINGPOINTID, getmFpId());
            param.put(FPIDENTIFIERTYPE, 0);
            BoostLog.d("JSON String", param.toString());
            return param;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getNoPageNfxParams() {
        try {
            JSONObject internalParams = new JSONObject();
            internalParams.put(TYPE, getmType());

            JSONObject param = new JSONObject();
            param.put(ACCESSTOKEN, internalParams);
            param.put(CLIENTID, Constants.clientId);
            param.put(FLOATINGPOINTID, getmFpId());
            param.put(FPIDENTIFIERTYPE, 0);
            BoostLog.d("JSON String", param.toString());
            return param;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface NfxCallBackListener {
        void nfxCallBack(String response, int callType, String name);
    }


}
