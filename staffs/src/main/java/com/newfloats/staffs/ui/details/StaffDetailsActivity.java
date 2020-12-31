package com.newfloats.staffs.ui.details;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseStaffActivity;


public class StaffDetailsActivity extends BaseStaffActivity implements BaseStaffActivity.ToolBarAction {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_details_activity);
        this.toolbar = findViewById(R.id.toolbar_staff);
        setUpToolBar(toolbar, "Staff Details", this, false);
        setStatusBarColor("#61605F");
        setToolBarColor("#747474");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, StaffDetailsFragment.newInstance())
                    .commitNow();
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onBackButtonClick() {
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public void onAddButtonClick() {
//not required
    }
}