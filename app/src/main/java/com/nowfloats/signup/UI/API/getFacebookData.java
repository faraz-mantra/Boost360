package com.nowfloats.signup.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by NowFloatsDev on 08/06/2015.
 */
public class getFacebookData extends AsyncTask<String, Void, String> {

    ProgressDialog pd = null;
    Boolean Connectiontimeout = false;
    Activity facebookActivity;
    String accessToken;

    public getFacebookData(Activity appContext, String accessToken) {
        facebookActivity = appContext;
        this.accessToken = accessToken;
    }

    @Override
    protected void onPreExecute() {
//            pd = ProgressDialog.show(facebookActivity, "Please wait",
//                    "Loading please wait..", true);
//            pd.setCancelable(true);

    }

    @Override
    protected String doInBackground(String... params) {
        fbUserProfile();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        pd.dismiss();
        if (Connectiontimeout != true) {
//                textName.setText(name);
//                textUserName.setText(userName);
//                textGender.setText(gender);
//                userImage.setImageBitmap(profilePic);
        } else {
            Toast.makeText(facebookActivity, "Connection Time out",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * getting user facebook data from facebook server
     */
    public void fbUserProfile() {

        try {
            // access_token = mPrefs.getString("access_token", null);
            JSONObject jsonObj = null;
            JSONObject jsonObjData = null;
            JSONObject jsonObjUrl = null;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 50000);
            HttpConnectionParams.setSoTimeout(httpParameters, 50000);

            HttpClient client = new DefaultHttpClient(httpParameters);

            String requestURL = "https://graph.facebook.com/me?fields=picture,id,name,email&access_token="
                    + accessToken;
            Log.i("Request URL:", "---" + requestURL);
            HttpGet request = new HttpGet(requestURL);

            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String webServiceInfo = "";

            while ((webServiceInfo = rd.readLine()) != null) {
                Log.i("Service Response:", "---" + webServiceInfo);
                jsonObj = new JSONObject(webServiceInfo);
                jsonObjData = jsonObj.getJSONObject("picture");
                jsonObjUrl = jsonObjData.getJSONObject("data");
//                name = jsonObj.getString("name");
//                userName = jsonObj.getString("username");
//                gender = jsonObj.getString("gender");
//                imageURL = jsonObjUrl.getString("url");
//                profilePic = BitmapFactory.decodeStream((InputStream) new URL(
//                        imageURL).getContent());
            }

        } catch (Exception e) {
            Log.d("E", "E:" + e);
            // Connectiontimeout = true;
        }
    }
}

