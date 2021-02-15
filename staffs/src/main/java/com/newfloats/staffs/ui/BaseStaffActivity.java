package com.newfloats.staffs.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.framework.views.customViews.CustomImageView;
import com.framework.views.customViews.CustomTextView;
import com.newfloats.staffs.R;

public abstract class BaseStaffActivity extends AppCompatActivity {
    private CustomTextView tv_title;
    private ToolBarAction toolBarAction;
    private Toolbar toolbar;
    private CustomImageView iv_add_button;
    private CustomImageView iv_back_button;

    public void setToolBarTitle(String title, boolean allCaps) {
        tv_title.setText(title);
        tv_title.setAllCaps(allCaps);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        super.onCreate(savedInstanceState);
    }

    protected void setUpToolBar(Toolbar toolBar, String title, ToolBarAction toolBarAction, Boolean requireActionButton) {
        initViews(toolBarAction);
        if (!requireActionButton) {
            iv_add_button.setVisibility(View.INVISIBLE);
        }
        this.toolbar = toolBar;
        tv_title.setText(title);
    }

    protected void setToolBarColor(String color) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));

    }

    protected void setStatusBarColor(String color) {
        getWindow().setStatusBarColor(Color.parseColor(color));
    }


    private void initViews(ToolBarAction toolBarAction) {
        this.toolBarAction = toolBarAction;
        this.tv_title = findViewById(R.id.tv_toolbar_title);
        this.iv_back_button = findViewById(R.id.iv_back);
        this.iv_add_button = findViewById(R.id.iv_add);
        if (toolBarAction != null) {
            iv_add_button.setOnClickListener(view -> this.toolBarAction.onAddButtonClick());
            iv_back_button.setOnClickListener(view -> this.toolBarAction.onBackButtonClick());
        }
    }

    public interface ToolBarAction {
        void onBackButtonClick();
        void onAddButtonClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
