package com.nowfloats.NavigationDrawer;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Admin on 26-12-2017.
 */

public class Mobile_Load_Html_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile__site);
        Methods.isOnline(this);
        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        String data;

        if (getIntent().hasExtra("WEBSITE_DATA")) {
            data = getIntent().getStringExtra("WEBSITE_DATA");
        } else {
            Toast.makeText(this, "Something is wrong with data", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.close_web).setVisibility(View.GONE);
        ImageView back = (ImageView) findViewById(R.id.back_web);
        back.setColorFilter(whiteLabelFilter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebView webView = (WebView) findViewById(R.id.webView1);
        //        startWebView(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(data, "text/html", null);
        Toast.makeText(this, getString(R.string.loading_data), Toast.LENGTH_LONG).show();
    }
}
