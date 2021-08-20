package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetBusinessCategoryAsyncTask extends AsyncTask<Void, String, String> {

    ProgressDialog pd = null;
    Boolean success = false;
    String response = null;
    String responseMessage = null;
    String category = null;
    int requestCode;
    Boolean flag4upateprofile = false;
    String fpID;
    private Activity appContext = null;

    public SetBusinessCategoryAsyncTask(Activity context, String category) {
        this.appContext = context;
        this.category = category;

    }

    public SetBusinessCategoryAsyncTask(Activity context, String category, Boolean flag4category, String fpID) {
        // TODO Auto-generated constructor stub
        this.appContext = context;
        this.category = category;
        this.flag4upateprofile = flag4category;
        this.fpID = fpID;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(appContext, null, "Setting business category");
        pd.setCancelable(true);

//		pd = new ProgressDialog(appContext,R.style.MyTheme);
//		pd.setCancelable(true);
//		pd.setMessage("Setting business category");
//		pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//		pd.show();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (!(pd == null)) {
                pd.dismiss();

                Edit_Profile_Activity.category.setText(category);
                Business_Profile_Fragment_V2.category.setText(category);
                //NewBusinessDetailsFragment.businessCategory.setText(category);

                if (flag4upateprofile) {
                    Edit_Profile_Activity.saveButton.setVisibility(View.GONE);
                    Util.changeDefaultBackgroundImage(category.toString());
                }

                // pd.dismiss();
                //  Util.toast("Data Saved", appContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            String content = Constants.clientId;
            String EncodedCategory = Uri.encode(category);
            //response = getDataFromServer(content, Constants.HTTP_POST,Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingPoint/UpdateFloatingPointCategory/"+Constants.StoreTag+"?category="+EncodedCategory);
            //  v1/floatingPoint/UpdateFloatingPointCategory/{fpTag}?category={category}
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.NOW_FLOATS_API_URL +
                    "/Discover/v1/floatingPoint/UpdateFloatingPointCategory/"
                    + fpID.toUpperCase().trim().replace("\"", "") +
                    "?category=" + EncodedCategory.toUpperCase().trim().replace("\"", ""));

            try {
                httppost.setEntity(new StringEntity(Constants.clientId));
                httppost.addHeader("Content-Type", "application/json");
                httppost.addHeader("Authorization", Utils.getAuthToken());

                HttpResponse response = httpclient.execute(httppost);
                requestCode = response.getStatusLine().getStatusCode();
                responseMessage = EntityUtils.toString(response.getEntity());
                if (requestCode == 200) {
                }


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }


//			if (!Util.isNullOrEmpty(response)) {
//
//
//			}// end of if condition

            // end of
        } catch (Exception e) {

        }
        return null;
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
            //connection.setRequestProperty("Connection", "Keep-Alive");
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
