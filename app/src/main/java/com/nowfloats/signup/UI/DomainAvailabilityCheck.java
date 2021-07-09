package com.nowfloats.signup.UI;

import android.content.Context;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.signup.UI.UI.CustomRunnable;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 6/29/2017.
 */

public class DomainAvailabilityCheck {

    private Context mContext;
    private boolean domainCheck, firstCheck = true;
    private CustomRunnable r;
    private Handler handler;
    private String aTag, mtag, aName, val, beforeEdit, intialValue, websiteTag;
    private Set<String> rTags, xTags;
    private CallBack callBack;
    private String websiteAddress;

    public DomainAvailabilityCheck(Context mContext) {
        this.mContext = mContext;
        this.callBack = (CallBack) mContext;
        rTags = new HashSet<String>();
        xTags = new HashSet<String>();
    }

    public void domainCheck(String webAdress) {
        this.websiteAddress = webAdress;
        if (callBack != null) {
            if (firstCheck) {
                if (handler == null) {
                    handler = new Handler();
                    r = new CustomRunnable(beforeEdit) {
                        public void run() {
                            val = websiteAddress;
                            if (beforeEdit.equals(val)) {
                                if (validate(val)) {
                                    String ttag = val.toUpperCase();
                                    if (rTags.contains(ttag)) {
                                        aTag = ttag;
//                                    label.setText(getString(R.string.chosen_website_available));
                                        domainCheck = false;
                                        callBack.onDomainAvailable(websiteTag);

                                    } else if (xTags.contains(ttag)) {
                                        // invalid tag
                                        mtag = ttag;
//                                    label.setText(getString(R.string.chosen_website_not_available));
                                        domainCheck = false;
                                        callBack.onDomainNotAvailable();
                                    } else {
                                        mtag = ttag;
                                        verifyTag(ttag);
                                    }
                                } else {
//                                label.setText(getString(R.string.enter_valid_website_name));
                                    callBack.onDomainNotAvailable();
                                    domainCheck = false;
                                }
                            }

                        }
                    };
                    beforeEdit = websiteAddress;
                    handler.postDelayed(r, 1000);

                } else {
                    handler.removeCallbacks(r);
                    beforeEdit = websiteAddress;
                    handler.postDelayed(r, 1000);
                }
            }
        }

    }

    int regex;

    public boolean validate(String text) {
        regex = R.string.signup_subd;
        if (text.matches(mContext.getResources().getString(regex))) {
            return true;
        } else {
            return false;
        }
    }


    public void verifyTag(String tagname) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/verifyUniqueTag";
        JSONObject obj = new JSONObject();
        try {
            obj.put("fpTag", tagname);
            obj.put("fpName", tagname);
            obj.put("clientId", Constants.clientId);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String mesg = error.getMessage();
                if (mesg != null && mesg.contains("org.json.JSONException: End of input at character 0 of")) {
                    // invalid tag
                    xTags.add(mtag);
//                    label.setText(getString(R.string.chosen_website_not_available));
                    domainCheck = false;
//                    addressTagValid = false;
                    callBack.onDomainNotAvailable();
                } else if (mesg != null && mesg
                        .contains("type java.lang.String cannot be converted to JSONObject")) {
                    mesg = mesg.replace(
                            "org.json.JSONException: Value ", "");
                    mesg = mesg.replace(
                            " of type java.lang.String cannot be converted to JSONObject",
                            "");
                    mesg = mesg.trim();
                    rTags.add(mtag);
                    if (mtag.equals(mesg)) {
                        // valid tag
                        aTag = mesg;
                        websiteTag = mesg;
//                        addressTagValid = true;

//                        label.setText(getString(R.string.chosen_website_available));
                        domainCheck = false;
                        callBack.onDomainAvailable(websiteTag);
                    } else {
                        rTags.add(mesg);
                        aTag = mesg;
//                        addressTagValid = false;
//                        label.setText(getString(R.string.chosen_website_not_available));
                        domainCheck = false;
                        callBack.onDomainNotAvailable();
                    }
                }
            }
        });

        queue.add(jsObjRequest);
    }

    public interface CallBack {

        void onDomainAvailable(String websiteTag);

        void onDomainNotAvailable();
    }
}
