package com.newfloats.staffs.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseStaffActivity;
import com.newfloats.staffs.ui.details.StaffDetailsActivity;

public class StaffHomeActivity extends BaseStaffActivity implements BaseStaffActivity.ToolBarAction {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setUpToolBar(toolbar, getString(R.string.staff_listing), this, true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, StaffHomeFragment.newInstance())
                    .commitNow();
        }
    }
    @Override
    public void onBackButtonClick() {
    }

    @Override
    public void onAddButtonClick() {
        startActivity(new Intent(this, StaffDetailsActivity.class));

    }
}