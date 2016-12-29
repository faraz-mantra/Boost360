package com.nowfloats.Twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.thinksity.R;

public class TwitterAuthenticationActivity extends AppCompatActivity {
    public final static String EXTRA_URL = "extra_url";
    private static final String TAG = TwitterAuthenticationActivity.class.getSimpleName();
    private WebView mWebView = null;
    private ProgressDialog mDialog = null;
    static ITwitterCallbacks  mcallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_authentication);
        mWebView = (WebView) findViewById(R.id.webview);
        final String url = this.getIntent().getStringExtra(EXTRA_URL);
        if (null == url) {
            finish();
        }
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(url);
    }
    public static void setListener(Context context){
        mcallback = (Social_Sharing_Activity)context;
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
        super.onResume();
        //this.onRestart();
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
           /* CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(TwitterAuthenticationActivity.this);
            }
            cookieManager.setAcceptCookie(true);*/
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mDialog == null)
                mDialog = new ProgressDialog(TwitterAuthenticationActivity.this);
            mDialog.setMessage("Loading..");

            if (!(isFinishing())) {
                mDialog.show();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (view.getProgress() >= 70) {
                cancelProgressDialog();
                isLoaded = true;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String verifier = uri.getQueryParameter("oauth_verifier");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("oauth_verifier", verifier);
            mcallback.returnToken(resultIntent);
            finish();
            return true;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView = null;
    }
}

