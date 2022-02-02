package com.nowfloats.NavigationDrawer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.util.Methods;
import com.thinksity.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Mobile_Site_Activity extends AppCompatActivity {

  String url;
  private WebView webView;

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mobile__site);
    Methods.isOnline(Mobile_Site_Activity.this);
    PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      url = extras.getString("WEBSITE_NAME");

      if (!TextUtils.isEmpty(url) && !url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://".concat(url);
      }
    }

    TextView close = (TextView) findViewById(R.id.close_web);
    close.setOnClickListener(v -> {
      try {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
      } catch (Exception e) {
        Toast.makeText(getBaseContext(), "Browser not available!", Toast.LENGTH_SHORT).show();
      }
    });

    ImageView back = (ImageView) findViewById(R.id.back_web);
    back.setColorFilter(whiteLabelFilter);
    back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    webView = (WebView) findViewById(R.id.webView1);

    startWebView(url);
  }


  @SuppressLint("SetJavaScriptEnabled")
  private void startWebView(String url) {
    //Create new webview Client to show progress dialog
    //When opening a url or click on link
    webView.setWebViewClient(new WebViewClient() {
      ProgressDialog progressDialog;

      //If you will not use this method url links are opeen in new brower not in webview
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (
            url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("geo:")
                || url.startsWith("whatsapp:") || url.startsWith("spotify:")
        ) {
          try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
          } catch (Exception e) {
            e.printStackTrace();
            view.loadUrl(url);
            return false;
          }
          return true;
        } else {
          view.loadUrl(url);
          return false;
        }
      }

      //Show loader on url load
      @Override
      public void onLoadResource(WebView view, String url) {
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        try {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (progressDialog == null) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(Mobile_Site_Activity.this);
                progressDialog.setMessage(getString(R.string.loading));
              }
              progressDialog.show();
            }
          });
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        try {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
              }
            }
          });
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    });

    // Javascript inabled on webview
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setLoadWithOverviewMode(true);
    webView.getSettings().setUseWideViewPort(true);
    webView.getSettings().setAllowFileAccess(true);
    webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    webView.setWebChromeClient(new WebChromeClient());
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setSupportMultipleWindows(true);
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
    webSettings.setDomStorageEnabled(true);
    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView.HitTestResult result = view.getHitTestResult();
        String data = result.getExtra();
        if (data != null) {
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
          view.getContext().startActivity(browserIntent);
        }
        return false;
      }
    });

    //Load url in webview
    webView.loadUrl(url);
  }

  // Open previous opened link from history on webview when back button pressed

  @Override
  // Detect when the back button is pressed
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      // Let the system handle the back button
      super.onBackPressed();
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_mobile__site, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
