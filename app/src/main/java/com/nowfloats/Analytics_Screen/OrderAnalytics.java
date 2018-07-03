package com.nowfloats.Analytics_Screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nowfloats.Analytics_Screen.Fragments.ProcessFacebookDataFragment;
import com.thinksity.R;

public class OrderAnalytics extends AppCompatActivity {

    private ImageView spinner;
    private LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (LinearLayout) findViewById(R.id.linearlayout);
        spinner = (ImageView) findViewById(R.id.toolbar_spinner);
    }

    private void addFragment() {
        Bundle b = new Bundle();
        if (layout.getVisibility() != View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment frag = ProcessFacebookDataFragment.getInstance(b);
        transaction.replace(R.id.linearlayout, frag, "FetchFacebookData").commit();
    }
}
