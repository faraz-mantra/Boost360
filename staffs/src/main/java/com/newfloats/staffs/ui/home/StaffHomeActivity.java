package com.newfloats.staffs.ui.home;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.newfloats.staffs.R;
import com.newfloats.staffs.ui.BaseActivityStaff;
public class StaffHomeActivity extends BaseActivityStaff implements BaseActivityStaff.ToolBarAction {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_staff);
        setUpToolBar(toolbar, getString(R.string.staff_listing),this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, StaffHomeFragment.newInstance())
                    .commitNow();
        }
    }
    @Override
    public void onBackButtonClick() {
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddButtonClick() {
        Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
    }
}