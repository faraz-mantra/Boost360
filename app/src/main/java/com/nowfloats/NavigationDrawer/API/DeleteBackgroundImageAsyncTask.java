package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Dell on 03-03-2015.
 */
public class DeleteBackgroundImageAsyncTask extends AsyncTask<Void, String, String> {
    ProgressDialog pd = null;
    Activity appContext;
    UserSessionManager session;
    private String category;

    public DeleteBackgroundImageAsyncTask(Activity appContext, UserSessionManager session) {
        this.appContext = appContext;
        this.session = session;
    }

    @Override
    protected void onPreExecute() {
        try {
            pd = ProgressDialog.show(appContext, "", "Deleting image...");
            pd.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE))) {
            String backgroundimgid = (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)).replace("/Backgrounds/", "");

            JSONObject obj = new JSONObject();
            //?clientId=%@&existingBackgroundImageUri=%@&fpId=%@&identifierType=%@
            String serverUri = Constants.DelBackgroundImage;

            try {
                obj.put("ClientId", Constants.clientId);
                obj.put("FpId", session.getFPID());
                obj.put("ExistingBackgroundImageUri", backgroundimgid);
                //obj.put("identifierType", "SINGLE");
                String serveruri = Constants.DelBackImg + "/" + "?ClientId=" + Constants.clientId + "&ExistingBackgroundImageUri=" + backgroundimgid + "&FpId=" + session.getFPID() + "&identifierType=SINGLE";
                //https://api.withfloats.com/discover/v1/floatingpoint/backgroundImage/delete/?ClientId=Constants.clientId&ExistingBackgroundImageUri=backgroundimgid&FpId=Constants.Store_id&&identifierType=SINGLE"
                // add header
                HttpPost httpPost = new HttpPost(serveruri);
                StringEntity entity = new StringEntity(obj.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("Authorization", Utils.getAuthToken());
                httpPost.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(httpPost);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    try {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, "");
                        appContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SidePanelFragment.containerImage.setImageDrawable(appContext.getResources().getDrawable(SidePanelFragment.getCategoryBackgroundImage(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClientProtocolException | UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
