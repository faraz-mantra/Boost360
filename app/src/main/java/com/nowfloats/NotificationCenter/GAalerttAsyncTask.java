package com.nowfloats.NotificationCenter;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class GAalerttAsyncTask extends AsyncTask<Void, String, String> {
    Activity mContext;
    String response = "";
    int count;
    String url;

    public GAalerttAsyncTask(Activity context, String url) {
        super();
        mContext = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String string) {
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet();
            httpRequest.setURI(new URI(url));
            HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity = responseOfSite.getEntity();
            if (entity != null) {
                int code = responseOfSite.getStatusLine().getStatusCode();
                if (code == 200) {
                    response = "Ok";
                } else {
                    response = "Failed";
                }
                Log.i("GA ", "response--" + response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = "Failed";
        }
        return response;
    }
}
