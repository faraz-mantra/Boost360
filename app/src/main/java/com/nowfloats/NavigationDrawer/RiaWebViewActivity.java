package com.nowfloats.NavigationDrawer;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.util.RiaEventLogger;
import com.thinksity.R;

public class RiaWebViewActivity extends AppCompatActivity {

    public static String RIA_WEB_CONTENT_URL = "contentUrl";
    public static String RIA_NODE_DATA = "riaNodeData";

    Toolbar tbWebView;
    WebView wbRiaContent;

    private RiaNodeDataModel mNodeDataModel;
    private UserSessionManager mSession;
    private boolean mRiaTimeOut = false;

    Handler mHandler = new Handler();
    Runnable mRiaPostRunnable = new Runnable() {
        @Override
        public void run() {
            RiaEventLogger.getInstance().logPostEvent(mSession.getFpTag(), mNodeDataModel.getNodeId(),
                    mNodeDataModel.getButtonId(), mNodeDataModel.getButtonLabel(),
                    RiaEventLogger.EventStatus.COMPLETED.getValue());
            mRiaTimeOut = true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ria_web_view);

        tbWebView = (Toolbar) findViewById(R.id.tb_web_view);
        setSupportActionBar(tbWebView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wbRiaContent = (WebView) findViewById(R.id.webview);

        String url = getIntent().getStringExtra(RIA_WEB_CONTENT_URL);
        mNodeDataModel = getIntent().getParcelableExtra(RIA_NODE_DATA);

        mSession = new UserSessionManager(getApplicationContext(), this);

        WebSettings webSettings = wbRiaContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wbRiaContent.setWebViewClient(new WebViewClient());
        wbRiaContent.loadUrl(url);
        mHandler.postDelayed(mRiaPostRunnable, 15000);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mRiaTimeOut){
            RiaEventLogger.getInstance().logPostEvent(mSession.getFpTag(), mNodeDataModel.getNodeId(),
                    mNodeDataModel.getButtonId(), mNodeDataModel.getButtonLabel(),
                    RiaEventLogger.EventStatus.DROPPED.getValue());
        }

        mHandler.removeCallbacks(mRiaPostRunnable);
    }
}
