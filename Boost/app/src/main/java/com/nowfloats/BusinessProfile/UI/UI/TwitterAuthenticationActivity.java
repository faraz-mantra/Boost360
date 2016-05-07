package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.thinksity.R;
import com.twitter.sdk.android.Twitter;

public class TwitterAuthenticationActivity extends AppCompatActivity {
    public final static String EXTRA_URL = "extra_url";
    private static final String TAG = TwitterAuthenticationActivity.class
            .getSimpleName();
    private WebView mWebView = null;
    private ProgressDialog mDialog = null;
    private Activity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Rahul","Twitter service onCreate()");
        mActivity = this;
        setContentView(R.layout.activity_twitter_authentication);
        mWebView = (WebView) findViewById(R.id.webview);
        final String url = this.getIntent().getStringExtra(EXTRA_URL);
        if (null == url) {
            finish();
        }
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(url);
    }

    @Override
    protected void onStop() {
        cancelProgressDialog();
        super.onStop();
    }

    @Override
    protected void onPause() {
        cancelProgressDialog();
        super.onPause();
    }

    private void cancelProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }

    @Override
    protected void onResume() {
        this.onRestart();
    }

    private class MyWebViewClient extends WebViewClient {
        boolean isLoaded = false;

        @Override
        public void onPageFinished(WebView view, String url) {
            try {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            } catch (Exception exception) {
            }
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(TwitterAuthenticationActivity.this);
            }
            cookieManager.setAcceptCookie(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mDialog == null)
                mDialog = new ProgressDialog(TwitterAuthenticationActivity.this);
            mDialog.setMessage("Loading..");

            if (!(mActivity.isFinishing())) {
                mDialog.show();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.i(TAG, "Loading Resources");
            Log.i(TAG,
                    "Resource Loading Progress : " + view.getProgress());
            if (view.getProgress() >= 70) {
                cancelProgressDialog();
                isLoaded = true;
            }
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String verifier = uri.getQueryParameter("oauth_verifier");
            Log.d("Rahul",verifier);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("oauth_verifier", verifier);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;

        }
    }
}
