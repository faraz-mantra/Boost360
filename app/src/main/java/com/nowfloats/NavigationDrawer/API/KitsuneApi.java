package com.nowfloats.NavigationDrawer.API;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.nowfloats.Volley.AppController;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NowFloats on 12-09-2016.
 */
public class KitsuneApi {
    private String mFptag;
    private ResultListener mListener;

    public KitsuneApi(String fpTag) {
        this.mFptag = fpTag;
    }

    public KitsuneApi setResultListener(ResultListener listener) {
        mListener = listener;
        return this;
    }

    public void enableKitsune() {
        String url = Constants.NOW_FLOATS_API_URL + "/kitsune/v1/enableKitsune?clientId=" +
                Constants.clientId + "&fpTag=" + mFptag;
        Request request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mListener.onResult(response, false);
                        BoostLog.d("KitsuneApi", response);
                        BoostLog.d("KitsuneApi", mFptag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onResult("Error", true);
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
        AppController.getInstance().getRequestQueue().add(request);
    }

    public void disablekitsune(String body) {
        String url = Constants.NOW_FLOATS_API_URL + "/kitsune/v1/disableKitsune?clientId=" +
                Constants.clientId + "&fpTag=" + mFptag;
        /*Request request = new JsonArrayRequest(Request.Method.POST, url, jsonReuest, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    mListener.onResult("false", false);
                }catch (JSONException e){
                    //mListener.onResult("Error", true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onResult("Error", true);
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray("[{\"response\":\"" + jsonString + "\"}]"),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };*/
        Request request = new JsonRequest<String>(Request.Method.POST, url, body, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mListener.onResult(response, false);
                BoostLog.d("Kitsune Api:", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onResult("Error", true);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers)), HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", Utils.getAuthToken());
                return headers;
            }
        };
        AppController.getInstance().getRequestQueue().add(request);

    }

    public interface ResultListener {
        void onResult(String response, boolean isError);
    }
}
