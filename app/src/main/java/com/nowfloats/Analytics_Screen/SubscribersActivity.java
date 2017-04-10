package com.nowfloats.Analytics_Screen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SubscribersActivity extends AppCompatActivity {

    private final String SUBSCRIBER_URL = Constants.NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/";
    private UserSessionManager mSessionManager;
    private int mOffset = 0;
    private ProgressBar mPbSubscriber;
    private ListView mLvSubscribers;
    private Toolbar toolbar;
    LinearLayout emptyLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribers);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        emptyLayout = (LinearLayout) findViewById(R.id.emplty_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().   getString(R.string.subscribers));

        mPbSubscriber = (ProgressBar)findViewById(R.id.pb_subscriber);
        mLvSubscribers = (ListView)findViewById(R.id.lv_subscribers);

        mSessionManager = new UserSessionManager(getApplicationContext(), SubscribersActivity.this);
        if(mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)!=null) {
            new SubscribersAsyncTask().execute(SUBSCRIBER_URL +
                    mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) +
                    "/subscribers/" + Constants.clientId + "?offset=" + mOffset);
        }else{
            Methods.showSnackBarNegative(this,getResources().getString(R.string.could_not_find_fb_tag));
        }

        BoostLog.d("Test for Fp Tag: ",  mSessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home ){

            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //getSupportFragmentManager().popBackStack();
            //  NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SubscribersAsyncTask extends AsyncTask<String, Void, String>{

        List<String> subscribers;
        @Override
        protected void onPreExecute() {
            mPbSubscriber.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String resp = getResponse(params[0]);
            subscribers = new ArrayList<>();
            if(resp!=null){
                parseJson(resp);
            }else {
                return null;
            }

            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            mPbSubscriber.setVisibility(View.GONE);
            if(!subscribers.isEmpty()){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubscribersActivity.this, R.layout.subscriber_listview_row, subscribers);
                mLvSubscribers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else{
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }

        private void parseJson(String resp){

            try{
                JSONArray arr = new JSONArray(resp);
                for(int i=0; i<arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);
                    if(obj.getString("SubscriptionStatus").equals("20")) {
                        subscribers.add(obj.getString("UserMobile"));
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        private String getResponse(String param){
            StringBuilder response = new StringBuilder();
            URL url = null;
            try {
                url = new URL(param);
                BoostLog.d("ILUD Subscribers", url.toString());
                HttpURLConnection conn = null;
                if(url!=null){
                    conn = (HttpURLConnection) url.openConnection();
                }
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if(responseCode!=200){
                    return null;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line="";
                while ((line=br.readLine())!=null){
                    response.append(line);
                }
                br.close();
                conn.disconnect();
                BoostLog.d("Another Test:", response.toString());

            }
            catch (MalformedURLException e){
                e.printStackTrace();
                return null;
            }
            catch (IOException e){
                e.printStackTrace();
                return null;
            }
            return  response.toString();
        }

    }
}