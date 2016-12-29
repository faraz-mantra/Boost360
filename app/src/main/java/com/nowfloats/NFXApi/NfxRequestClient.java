package com.nowfloats.NFXApi;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by RAJA on 12-07-2016.
 */
public class NfxRequestClient {
    private String mType;
    private String mUserAccessTokenKey;
    private String mUserAccessTokenSecret;
    private String mUserAccountId;
    private String mFpId;
    private String mName;
    private NfxCallBackListener mCallBackListener;
    private int mCallType;

    /*
     * Request params
     */
    private final String TYPE = "Type";
    private final String USERACCESSTOKENKEY = "UserAccessTokenKey";
    private final String USERACCOUNTNAME = "UserAccountName";
    private final String USERACCESSTOKENSECRET = "UserAccessTokenSecret";
    private final String USERACCOUNTID = "UserAccountId";
    private final String ACCESSTOKEN = "accessToken";
    private final String CLIENTID = "clientId";
    private final String FLOATINGPOINTID = "floatingPointId";
    private final String FPIDENTIFIERTYPE = "fpIdentifierType";


    public interface NfxCallBackListener{
        void nfxCallBack(String response, int callType, String name);
    }


    private NfxRequestClient(){

    }
    public NfxRequestClient(NfxCallBackListener callBackListener){
        this.mCallBackListener = callBackListener;
    }

    public String getmUserAccessTokenKey() {
        return mUserAccessTokenKey;
    }

    public NfxRequestClient setmUserAccessTokenKey(String mUserAccessTokenKey) {
        this.mUserAccessTokenKey = mUserAccessTokenKey;
        return this;
    }

    public String getmType() {
        return mType;
    }

    public NfxRequestClient setmType(String mType) {
        this.mType = mType;
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

    public void connectNfx(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.nfxFacebookAnalytics, getNfxParams() , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mCallBackListener.nfxCallBack(response.toString(), getmCallType(), getmName());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCallBackListener.nfxCallBack("error", getmCallType(), getmName());
            }
        }){
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
                }catch (JSONException e){
                    return Response.error(new ParseError(e));
                }
                //return new JSONObject("FACEBOOK");
            }
        };
        AppController.getInstance().addToRequstQueue(jsonObjectRequest);

    }



    private JSONObject getNfxParams(){

        try {
            JSONObject internalParams = new JSONObject();
            internalParams.put(TYPE, getmType());
            internalParams.put(USERACCESSTOKENKEY,getmUserAccessTokenKey());
            internalParams.put(USERACCESSTOKENSECRET, getmUserAccessTokenSecret());
            internalParams.put(USERACCOUNTID, getmUserAccountId());
            internalParams.put(USERACCOUNTNAME, getmName());
            JSONObject param = new JSONObject();
            param.put(ACCESSTOKEN , internalParams);
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


}
