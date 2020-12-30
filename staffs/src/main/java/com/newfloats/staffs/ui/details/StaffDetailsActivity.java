package com.newfloats.staffs.ui.details;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.newfloats.staffs.R;


public class StaffDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_details_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, StaffDetailsFragment.newInstance())
                    .commitNow();
        }
    }
}