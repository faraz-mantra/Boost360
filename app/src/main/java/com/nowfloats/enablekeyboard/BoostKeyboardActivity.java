package com.nowfloats.enablekeyboard;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thinksity.R;

public class BoostKeyboardActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView headerText;
    private EnableBoostKeyboardFragment enableBoostKeyboardFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_keyboard);
        toolbar = (Toolbar) findViewById(R.id.app_bar_enable_keyboard);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.enable_keyboard_n));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enableBoostKeyboardFragment = new EnableBoostKeyboardFragment();

        findViewById(R.id.fm_enable_boost_keyboard).setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fm_enable_boost_keyboard, enableBoostKeyboardFragment).
                commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
