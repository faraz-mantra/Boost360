package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Analytics_Fragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class GetSearchQueryCountAsyncTask extends AsyncTask<Void, String, String> {
    private int count;
    private int responseCode;
    private UserSessionManager session;

    GetSearchQueryCountAsyncTask(Activity context, UserSessionManager session) {
        this.session = session;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String string) {
        try {
            if (Analytics_Fragment.searchQueriesCount != null && Analytics_Fragment.search_query_progress != null) {
                Analytics_Fragment.search_query_progress.setVisibility(View.GONE);

                if (responseCode == 200) {
                    session.setSearchCount(count + "");

                    Analytics_Fragment.searchQueriesCount.setVisibility(View.VISIBLE);
                    Analytics_Fragment.searchQueriesCount.setText(Analytics_Fragment.getNumberFormat(session.getSearchCount()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        URI website;

        try {
            HttpClient client = new DefaultHttpClient();

            if (session.getISEnterprise().equals("true")) {
                String queryURL = Constants.NOW_FLOATS_API_URL + "/Dashboard/v1/" +
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID) + "/summary?clientId=" + Constants.clientId +
                        "&fpId=" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID) + "&scope=1";
                website = new URI(queryURL);
            } else {
                website = new URI(Constants.SearchQueryCount +
                        Constants.clientId + "&fpTag=" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            }

            HttpGet httpRequest = new HttpGet();
            httpRequest.setURI(website);
            httpRequest.addHeader("Authorization", Utils.getAuthToken());
            ;
            HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity = responseOfSite.getEntity();

            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                count = Integer.parseInt(responseString);
                responseCode = responseOfSite.getStatusLine().getStatusCode();
            }
        } catch (Exception ex) {
            String message = "Parsing error.";
            if (ex.getLocalizedMessage() != null) message = ex.getLocalizedMessage();
            Log.e(GetSearchQueryCountAsyncTask.class.getName(), message);
        }

        return null;
    }
}