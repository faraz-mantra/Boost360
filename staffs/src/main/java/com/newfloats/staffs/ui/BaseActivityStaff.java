package com.newfloats.staffs.ui;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.framework.views.customViews.CustomImageView;
import com.framework.views.customViews.CustomTextView;
import com.newfloats.staffs.R;
public abstract class BaseActivityStaff extends AppCompatActivity {
    private CustomTextView tv_title;
    private ToolBarAction toolBarAction;

    protected void setUpToolBar(Toolbar toolBar, String title, ToolBarAction toolBarAction) {
        this.toolBarAction = toolBarAction;
        initViews();
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        tv_title.setText(title);
    }

    private void initViews() {
        this.tv_title = findViewById(R.id.tv_toolbar_title);
        CustomImageView iv_back_button = findViewById(R.id.iv_back);
        CustomImageView iv_add_button = findViewById(R.id.iv_add);
        iv_add_button.setOnClickListener(view -> toolBarAction.onAddButtonClick());
        iv_back_button.setOnClickListener(view -> toolBarAction.onBackButtonClick());
    }

    public interface ToolBarAction {
        void onBackButtonClick();
        void onAddButtonClick();
    }
}
