package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.os.AsyncTask;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;

public class GetVisitorsAndSubscribersCountAsyncTask extends AsyncTask<Void, String, String> {

    private Activity mContext;
    private int responseCode;
    private UserSessionManager sessionManager;
    private String numberOfViews, numberOfSubscribers, numberOfVisitors, numberOfEnquries;

    public GetVisitorsAndSubscribersCountAsyncTask(Activity context, UserSessionManager session) {
        mContext = context;
        sessionManager = session;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String string) {
        try {
            if (responseCode == 200) {
                try {
                    if (!numberOfViews.contains(",")) {
                        numberOfViews = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfViews));
                    }

                    if (!numberOfVisitors.contains(",")) {
                        numberOfVisitors = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfVisitors));
                    }

                    if (!numberOfSubscribers.contains(",")) {
                        numberOfSubscribers = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfSubscribers));
                    }

                    if (!numberOfEnquries.contains(",")) {
                        numberOfEnquries = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberOfEnquries));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sessionManager.setVisitsCount(numberOfViews);
                sessionManager.setVisitorsCount(numberOfVisitors);
                sessionManager.setSubcribersCount(numberOfSubscribers);
                sessionManager.setEnquiryCount(numberOfEnquries);

                mContext.runOnUiThread(() -> {

                    if (Analytics_Fragment.subscriberCount != null && Analytics_Fragment.subscriber_progress != null) {
                        Analytics_Fragment.subscriberCount.setVisibility(View.VISIBLE);
                        Analytics_Fragment.subscriber_progress.setVisibility(View.GONE);
                        Analytics_Fragment.subscriberCount.setText(numberOfSubscribers);
                    }

                    if (Analytics_Fragment.visitCount != null && Analytics_Fragment.visits_progressBar != null) {
                        Analytics_Fragment.visitCount.setVisibility(View.VISIBLE);
                        Analytics_Fragment.visits_progressBar.setVisibility(View.GONE);
                        Analytics_Fragment.visitCount.setText(Analytics_Fragment.getNumberFormat(numberOfViews));
                    }

                    if (Analytics_Fragment.visitorsCount != null && Analytics_Fragment.visitors_progressBar != null) {
                        Analytics_Fragment.visitorsCount.setVisibility(View.VISIBLE);
                        Analytics_Fragment.visitors_progressBar.setVisibility(View.GONE);
                        Analytics_Fragment.visitorsCount.setText(Analytics_Fragment.getNumberFormat(numberOfVisitors));
                    }

                    if (Analytics_Fragment.businessEnqCount != null && Analytics_Fragment.businessEnqProgress != null) {
                        Analytics_Fragment.businessEnqCount.setVisibility(View.VISIBLE);
                        Analytics_Fragment.businessEnqProgress.setVisibility(View.GONE);
                        Analytics_Fragment.businessEnqCount.setText(Analytics_Fragment.getNumberFormat(numberOfEnquries));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new GetSearchQueryCountAsyncTask(mContext, sessionManager).execute();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        URI website;

        try {
            HttpClient client = new DefaultHttpClient();

            if (sessionManager.getISEnterprise().equals("true")) {
                String queryURL = Constants.NOW_FLOATS_API_URL + "/Dashboard/v1/" +
                        sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + "/summary?clientId=" + Constants.clientId +
                        "&fpId=" + sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PARENTID) + "&scope=1";
                website = new URI(queryURL);
            } else {
                website = new URI(Constants.NOW_FLOATS_API_URL + "/Dashboard/v1/" + sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + "/summary?clientId="
                        + Constants.clientId + "&fpId=" + sessionManager.getFPID() + "&scope=0");
            }

            HttpGet httpRequest = new HttpGet();
            httpRequest.setURI(website);
            httpRequest.addHeader("Authorization", Utils.getAuthToken());

            HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity = responseOfSite.getEntity();

            responseCode = responseOfSite.getStatusLine().getStatusCode();

            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                JSONObject responseJson = new JSONObject(responseString);
                JSONArray entityArray = responseJson.getJSONArray("Entity");

                for (int i = 0; i < entityArray.length(); i++) {
                    JSONObject data = (JSONObject) entityArray.get(i);

                    numberOfViews = data.getString("NoOfViews");
                    numberOfVisitors = data.getString("NoOfUniqueViews");
                    numberOfEnquries = data.getString("NoOfMessages");
                    numberOfSubscribers = data.getString("NoOfSubscribers");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}