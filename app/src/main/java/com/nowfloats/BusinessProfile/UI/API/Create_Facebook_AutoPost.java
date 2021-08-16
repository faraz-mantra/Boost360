package com.nowfloats.BusinessProfile.UI.API;

/**
 * Created by NowFloatsDev on 29/05/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Create_Facebook_AutoPost extends AsyncTask<Void, String, String> {

    SharedPreferences.Editor prefsEditor;
    ProgressDialog pd = null;
    Boolean success = false;
    String response = null;
    String responseMessage = null;
    JSONObject obj;
    TextView fromPage;
    CheckBox checkBox;
    private SharedPreferences pref = null;
    private Activity appContext = null;


    public Create_Facebook_AutoPost(Activity context, JSONObject obj, TextView FromPage, CheckBox Checkbox) {
        this.appContext = context;
        this.obj = obj;
        this.fromPage = FromPage;
        this.checkBox = Checkbox;
    }

    @Override
    protected void onPreExecute() {

        pd = ProgressDialog.show(appContext, null, "Please wait");
        pd.setCancelable(true);
        pd.show();
        pref = appContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
    }

    @Override
    protected void onPostExecute(String result) {

        if (pd != null) {
            pd.dismiss();
        }
        if (!Util.isNullOrEmpty(result)) {
            //Util.toast("success", appContext);
            prefsEditor.putBoolean("FacebookFeedRegd", true);
            prefsEditor.commit();
            fromPage.setText("From " + Constants.fbFromWhichPage);
        } else {
            Util.toast(appContext.getString(R.string.uh_oh_somthing_went_wrong_please_try_again), appContext);
            checkBox.setChecked(false);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";

        try {
            String content = obj.toString();
            response = getDataFromServer(content,
                    Constants.HTTP_POST,
                    Constants.NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/AutoPublishMessages");
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

