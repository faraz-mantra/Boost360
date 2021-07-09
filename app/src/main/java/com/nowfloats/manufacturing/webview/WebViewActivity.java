package com.nowfloats.manufacturing.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    TextView title;
    LinearLayout backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        title.setText(R.string.browser);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadData();
    }


    private void loadData(){
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            title.setText(extra.getString("url"));
            webView.loadUrl(extra.getString("url"));
        }
    }
}