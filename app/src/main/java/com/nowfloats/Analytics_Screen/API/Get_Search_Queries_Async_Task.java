package com.nowfloats.Analytics_Screen.API;

/**
 * Created by Kamal on 17-02-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nowfloats.Analytics_Screen.DataMap;
import com.nowfloats.Analytics_Screen.Search_Query_Adapter.SearchQueryAdapter;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Get_Search_Queries_Async_Task extends AsyncTask<Void, String, String> {
    public String url;
    String formatted = "", msg = "", date = "", dateTime = "", existingData = "";
    String temp[];
    ProgressDialog pd = null;
    LinearLayout websiteScreen, detailsScreen;
    SearchQueryAdapter Adap;
    int skipBy = 0;
    String responseMessage = "";
    Boolean success = false;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
    String fpTag = "RIYATEST";
    JSONObject obj;
    ListView listView;
    boolean isrefresh = false;
    View footerView = null;
    View zerothScreen;
    UserSessionManager sessionManager;
    private Activity appContext = null;
    private String response = "";
    private String websiteName = "";
    private String callInitiatedFrom = "";

    public Get_Search_Queries_Async_Task(Activity context, SearchQueryAdapter Adap) {
        this.appContext = context;
        this.websiteName = websiteName;
        this.Adap = Adap;
        sessionManager = new UserSessionManager(appContext, appContext);
    }


    @Override
    protected void onPreExecute() {
        skipBy = HomeActivity.StorebizFloats.size();
        if (listView != null && skipBy < 3) {
            pd = ProgressDialog.show(appContext, null, appContext.getResources().getString(R.string.feching_search_detail));
            //pd = new ProgressDialog(appContext, R.style.MyTheme);
            pd.setCancelable(true);
            // pd.setMessage("Fetching Search details...");
            //pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pd.show();
        }


    }

    @Override
    protected void onPostExecute(String response) {
        if (pd != null)
            pd.dismiss();

        if (footerView != null) {
            listView.removeFooterView(footerView);
            footerView = null;
        }
        if (!Util.isNullOrEmpty(response)) {

            try {

                if (Adap != null) {
                  /*  Adap.setList(Constants.StoreUserSearch);
                    Adap.notifyDataSetChanged();

                    if(Constants.StoreUserSearch!=null){
                        if(Constants.StoreUserSearch.size() == 0 ){
                            SearchQueriesActivity.emptySearchLayout.setVisibility(View.VISIBLE);
                            //listView.setVisibility(View.GONE);
                        }else {
                            SearchQueriesActivity.emptySearchLayout.setVisibility(View.GONE);
                        }
                    }
                    else{
                        SearchQueriesActivity.emptySearchLayout.setVisibility(View.GONE);
                       // listView.setVisibility(View.GONE);
                    }*/
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected String doInBackground(Void... params) {

        obj = new JSONObject();
        try {
            obj.put("clientId", Constants.clientId);
            if (sessionManager.getISEnterprise().equals("true")) {
                obj.put("fpIdentifierType", "MULTI");
                // obj.put("fpTag", Constants.parentID);
                obj.put("fpTag", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            } else {
                obj.put("fpIdentifierType", "SINGLE");
                obj.put("fpTag", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int l = 0;
        if (Adap != null && !isrefresh) {
            //  l = Adap.getCount();
        }
        response = getDataFromServer(Constants.HTTP_POST, Constants.NOW_FLOATS_API_URL + "/Search/v1/queries/report?offset=" + l, obj.toString());

        try {
            JSONArray array = new JSONArray(response);
            int len = array.length();
            boolean flag = isrefresh;
            for (int i = 0; i < len; i++) {
                JSONObject query = new JSONObject();
                query = (JSONObject) array.get(i);
                String key = (String) query.get("createdOn");
                query.put("key", key);
                query.put("createdOn", Util.processDate((String) query.get("createdOn")));
                if (Constants.StoreUserSearch == null || flag) {

                    Constants.StoreUserSearch = new DataMap();
                    flag = false;

                }
                if (!Constants.StoreUserSearch.containsKey(key))
                    Constants.StoreUserSearch.put(key, query);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    public String getDataFromServer(String requestMethod, String serverUrl, String content) {
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

            outputStream.write(content.getBytes());

            //        output.write(query.getBytes(charset));
            int responseCode = connection.getResponseCode();

            responseMessage = connection.getResponseMessage();

            if (responseCode == 200 || responseCode == 202) {
                success = true;

            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(connection.getInputStream());
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

