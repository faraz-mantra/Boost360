package com.nowfloats.Analytics_Screen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.API.NfxFacebbokAnalytics;
import com.nowfloats.Analytics_Screen.Fragments.FetchFacebookDataFragment;
import com.nowfloats.Analytics_Screen.Fragments.LoginFragment;
import com.nowfloats.Analytics_Screen.Fragments.PostFacebookUpdateFragment;
import com.nowfloats.Analytics_Screen.model.GetFacebookAnalyticsData;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.thinksity.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Abhi on 12/1/2016.
 */

public class SocialAnalytics extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener,LoginFragment.OpenOtherFacebookScreen {

    private int status = 0;
    private final static int FETCH_DATA = 20,POST_UPDATE = 10,LOGIN_FACEBOOK = -100;
    WebView web;
    ProgressDialog progress;
    LinearLayout layout;
    Toolbar toolbar;
    final String mType="facebook";
    private String mAnalyticsUrl="http://nfx.withfloats.com/dataexchange/v1/fetch/analytics?" +
            "identifier="+mType+"&nowfloats_id=";
    String[] socialArray;
    UserSessionManager session;
    AppCompatSpinner spinner;
    ImageView toolbarImage;
    TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_analytics);
        session = new UserSessionManager(getApplicationContext(), this);
        socialArray=getResources().getStringArray(R.array.social_array);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            title.setText("Social Analytics");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarImage = (ImageView) findViewById(R.id.spinner_img);
        web = (WebView) findViewById(R.id.webview);
        layout = (LinearLayout) findViewById(R.id.linearlayout);
        spinner = (AppCompatSpinner) findViewById(R.id.toolbar_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,socialArray);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        status = intent.getIntExtra("GetStatus",0);

        progress=new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.please_wait));
        progress.setCanceledOnTouchOutside(false);

        if(status == 1){
            checkForMessage();
        }else{
            addFragment(LOGIN_FACEBOOK);
        }

    }

    private void showDialog(){
        if(!isFinishing())
            progress.show();
    }
    private void hideDialog(){
        if(progress.isShowing())
            progress.hide();
    }

    private void checkForMessage(){
        //Log.v("ggg","checkformessage");
        showDialog();
        NfxFacebbokAnalytics.nfxFacebookApis facebookApis=NfxFacebbokAnalytics.getAdapter();
        facebookApis.nfxFetchFacebookData(session.getFPID(), mType, new Callback<GetFacebookAnalyticsData>() {
            @Override
            public void success(GetFacebookAnalyticsData facebookAnalyticsData, Response response) {
                hideDialog();
                if(facebookAnalyticsData==null){
                    return;
                }
                String status=facebookAnalyticsData.getStatus();
                String message=facebookAnalyticsData.getMessage();
                if(message!=null && message.equalsIgnoreCase("success")){
                    startWebView();
                    setImpressionValue(facebookAnalyticsData.getData());
                }else if(status!=null && message!=null){
                    addFragment(Integer.parseInt(status));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
                Toast.makeText(SocialAnalytics.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.v("ggg", error + "");
            }
        });
    }

    private void setImpressionValue(List<GetFacebookAnalyticsData.Datum> list) {
        for (GetFacebookAnalyticsData.Datum data :list) {
            if("facebook".equalsIgnoreCase(data.getIdentifier())){
                session.storeFacebookImpressions(String.valueOf(data.getValues().getPostImpressions()));
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView() {
        //Log.v("ggg","webview");
        layout.setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);
        showDialog();
        web.setWebChromeClient(new SocialAnalytics.MyWebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        Map<String,String> mp=new HashMap<>();
        mp.put("key","78234i249123102398");
        mp.put("pwd","JYUYTJH*(*&BKJ787686876bbbhl)");
        web.loadUrl(makeUrl(session.getFPID()),mp);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v("ggg",position+" selected "+id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void showFragment() {
        checkForMessage();
    }

    private class MyWebViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100){
                hideDialog();
            }
            super.onProgressChanged(view, newProgress);
        }
    }
    private void addFragment(int i){
        if(layout.getVisibility() != View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment frag=null;
        switch(i){
            case FETCH_DATA:
                //getting info about message
                frag = manager.findFragmentByTag("FetchFacebookData");
                if(frag == null)
                    frag = new FetchFacebookDataFragment();

                transaction.replace(R.id.linearlayout,frag,"FetchFacebookData").commit();
                break;
            case POST_UPDATE:
                frag = manager.findFragmentByTag("PostFacebookUpdate");
                if(frag == null)
                    frag = new PostFacebookUpdateFragment();

                transaction.replace(R.id.linearlayout,frag,"PostFacebookUpdate").commit();
                break;
            case LOGIN_FACEBOOK:
                frag = manager.findFragmentByTag("LoginFragment");
                if(frag == null)
                    frag = LoginFragment.getInstance(status);

                transaction.replace(R.id.linearlayout,frag,"LoginFragment").commit();
                break;
            default:
                break;
        }
    }

    private String makeUrl(String fpId){
        return mAnalyticsUrl + fpId;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home ){
            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
