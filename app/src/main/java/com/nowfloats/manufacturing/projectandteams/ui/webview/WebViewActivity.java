package com.nowfloats.manufacturing.projectandteams.ui.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.hotel.placesnearby.PlacesNearByDetailsActivity;
import com.nowfloats.util.Constants;
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

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        title.setText("Browser");

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