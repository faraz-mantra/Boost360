package com.nowfloats.Analytics_Screen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nowfloats.Analytics_Screen.API.NfxFacebbokAnalytics;
import com.nowfloats.Analytics_Screen.Fragments.FetchFacebookDataFragment;
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
 * Created by Abhi on 11/28/2016.
 */

public class ShowWebView extends AppCompatActivity {
    private final static int FETCH_DATA=20,POST_UPDATE=10;
    WebView web;
    ProgressDialog progress;
    LinearLayout layout;
    Toolbar toolbar;
    final String mType="facebook";
    private String mAnalyticsUrl="http://nfx.withfloats.com/dataexchange/v1/fetch/analytics?" +
            "identifier="+mType+"&nowfloats_id=";
    UserSessionManager session;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        web= (WebView) findViewById(R.id.webview);
        layout= (LinearLayout) findViewById(R.id.linearlayout);

        setSupportActionBar(toolbar);
        progress=new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.please_wait));
        progress.setCanceledOnTouchOutside(false);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new UserSessionManager(getApplicationContext(), ShowWebView.this);
        checkForMessage();
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
                Toast.makeText(ShowWebView.this,error.getMessage(), Toast.LENGTH_SHORT).show();
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
        web.setWebChromeClient(new MyWebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        Map<String,String> mp=new HashMap<>();
        mp.put("key","78234i249123102398");
        mp.put("pwd","JYUYTJH*(*&BKJ787686876bbbhl)");
        web.loadUrl(makeUrl(session.getFPID()),mp);
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment frag=null;
        switch(i){
            case FETCH_DATA:
                //getting info about message
                frag = new FetchFacebookDataFragment();
                break;
            case POST_UPDATE:
                frag = new PostFacebookUpdateFragment();
                break;
            default:
                break;
        }
        transaction.add(R.id.linearlayout, frag);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home ){
            BoostLog.d("Back", "Back Pressed");
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    private String makeUrl(String fpId){
        return mAnalyticsUrl + fpId;
    }
}
