package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FacebookFeedPullAutoPublishAsyncTask extends AsyncTask<Void, String, String> {

    SharedPreferences.Editor prefsEditor;
    ProgressDialog pd = null;
    Boolean success = false;
    String response = null;
    String responseMessage = null;
    JSONObject obj;
    boolean subscription;
    TextView fromPage;
    UserSessionManager sessionManager;
    ImageView ivFbPageAutoPull;
    private SharedPreferences pref = null;
    private Activity appContext = null;

    public FacebookFeedPullAutoPublishAsyncTask(Activity context, JSONObject obj, boolean subscription, TextView FromPage, ImageView ivFbPageAutoPull, UserSessionManager sessionManager) {
        this.appContext = context;
        this.obj = obj;
        this.subscription = subscription;
        this.fromPage = FromPage;
        this.sessionManager = sessionManager;
        this.ivFbPageAutoPull = ivFbPageAutoPull;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(appContext, null, "Please wait");
        //pd.setCancelable(true);
        pref = appContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
    }

    @Override
    protected void onPostExecute(String result) {

        if (pd != null) {
            pd.dismiss();
        }
        BoostLog.d("FacebookAutoPublish", "FacebookFeedPullAutoPublish : " + result);
        if (!Util.isNullOrEmpty(result)) {
            //Util.toast("success", appContext);
            if (subscription == true) {
                prefsEditor.putBoolean("FBFeedPullAutoPublish", true);
                prefsEditor.commit();
                //fromPage.setText("From "+ Constants.fbFromWhichPage);
            } else {
                Toast.makeText(appContext, "Auto Pull for Updates will be turned OFF", Toast.LENGTH_SHORT).show();
                fromPage.setVisibility(View.GONE);
                ivFbPageAutoPull.setImageDrawable(appContext.getResources().getDrawable(R.drawable.facebookpage_icon_inactive));
                sessionManager.storeFPDetails(Key_Preferences.FB_PULL_PAGE_NAME, null);
                sessionManager.storeFPDetails(Key_Preferences.FB_PULL_ENABLED, "false");
                sessionManager.storeFPDetails(Key_Preferences.FB_PULL_COUNT, "0");
            }
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";

        try {
            String content = obj.toString();
            response = getDataFromServer(content,
                    Constants.HTTP_POST,
                    Constants.NOW_FLOATS_API_URL +
                            "/Discover/v1/FloatingPoint/UpdateFacebookPullRegistration/");
            if (!Util.isNullOrEmpty(response)) {


            }// end of if condition

            // end of
        } catch (Exception e) {

        }
        return response;
    }

    public String getDataFromServer(String content, String requestMethod,
                                    String serverUrl) {
        String response = "";
        DataOutputStream outputStream = null;
        try {

            URL new_url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            outputStream = new DataOutputStream(connection.getOutputStream());

            byte[] BytesToBeSent = content.getBytes();

            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            int responseCode = connection.getResponseCode();

            responseMessage = connection.getResponseMessage();

            if (responseCode == 200 || responseCode == 202) {
                success = true;

            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(
                        connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder responseContent = new StringBuilder();

                String temp = null;

                boolean isFirst = true;

                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append(Constants.NEW_LINE);
                    responseContent.append(temp);
                    isFirst = false;
                }

                response = responseContent.toString();

            } catch (Exception e) {
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }

        } catch (Exception ex) {
            success = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
        return response;
    }


}
