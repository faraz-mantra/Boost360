package com.nowfloats.NavigationDrawer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.RiaEventLogger;
import com.thinksity.R;

public class RiaWebViewActivity extends AppCompatActivity {

    public static String RIA_WEB_CONTENT_URL = "contentUrl";
    public static String RIA_NODE_DATA = "riaNodeData";

    Toolbar tbWebView;
    WebView wbRiaContent;
    ProgressBar progressBar;
    Handler mHandler = new Handler();
    private RiaNodeDataModel mNodeDataModel;
    private UserSessionManager mSession;
    private boolean mRiaTimeOut = false;
    Runnable mRiaPostRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mRiaTimeOut) {
                RiaEventLogger.getInstance().logPostEvent(mSession.getFpTag(), mNodeDataModel.getNodeId(),
                        mNodeDataModel.getButtonId(), mNodeDataModel.getButtonLabel(),
                        RiaEventLogger.EventStatus.COMPLETED.getValue());
                mRiaTimeOut = true;
            }


        }
    };
    private boolean mIsCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ria_web_view);

        tbWebView = (Toolbar) findViewById(R.id.tb_web_view);
        setSupportActionBar(tbWebView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wbRiaContent = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setMax(100);

        final String url = getIntent().getStringExtra(RIA_WEB_CONTENT_URL);
        mNodeDataModel = getIntent().getParcelableExtra(RIA_NODE_DATA);

        mSession = new UserSessionManager(getApplicationContext(), this);

        findViewById(R.id.tv_open_in_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                if (!mRiaTimeOut) {
                    RiaEventLogger.getInstance().logPostEvent(mSession.getFpTag(), mNodeDataModel.getNodeId(),
                            mNodeDataModel.getButtonId(), mNodeDataModel.getButtonLabel(),
                            RiaEventLogger.EventStatus.COMPLETED.getValue());
                    mHandler.removeCallbacksAndMessages(null);
                    mRiaTimeOut = true;
                }

            }
        });

        WebSettings webSettings = wbRiaContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wbRiaContent.setWebChromeClient(new MyWebChromeClient());
        wbRiaContent.setWebViewClient(new MyWebViewClient());
        if (TextUtils.isEmpty(url)) {
            finish();
        } else if (url.endsWith(".pdf")) {
            wbRiaContent.loadUrl(Constants.PDF_LOADER_URL + url);
        } else {
            wbRiaContent.loadUrl(url);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mRiaTimeOut) {
            RiaEventLogger.getInstance().logPostEvent(mSession.getFpTag(), mNodeDataModel.getNodeId(),
                    mNodeDataModel.getButtonId(), mNodeDataModel.getButtonLabel(),
                    RiaEventLogger.EventStatus.DROPPED.getValue());
        }
        //Set WebViewClient null to not to cache the view and not to trigger completed event
        wbRiaContent.setWebViewClient(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setValue(int progress) {
        progressBar.setProgress(progress);
        if (progress == 100) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            RiaWebViewActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (!mIsCalled) {
                mHandler.postDelayed(mRiaPostRunnable, 10000);
                mIsCalled = true;
            }
        }
    }
}
