package com.nowfloats.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.AboutFragment;
import com.nowfloats.NavigationDrawer.AccountSettingsFragment;
import com.nowfloats.NavigationDrawer.HelpAndSupportFragment;
import com.nowfloats.NavigationDrawer.Home_Main_Fragment;
import com.nowfloats.NavigationDrawer.ManageContentFragment;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.NotificationCenter.NotificationFragment;
import com.nowfloats.enablekeyboard.KeyboardFragment;
import com.nowfloats.managecustomers.ManageCustomerFragment;
import com.nowfloats.manageinventory.ManageInventoryFragment;
import com.nowfloats.util.Utils;
import com.thinksity.R;
import com.thinksity.databinding.ActivityFragmentContainerAppBinding;

public class AppFragmentContainerActivity extends AppCompatActivity {

    public static String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    private FragmentType type = null;
    private UserSessionManager session;

    private ActivityFragmentContainerAppBinding binding;

    public static void startFragmentAppActivity(Activity activity, FragmentType type, Bundle bundle, Boolean clearTop) {
        Intent intent = new Intent(activity, AppFragmentContainerActivity.class);
        intent.putExtras(bundle);
        setFragmentType(intent, type);
        if (clearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(intent);
    }

    private static void setFragmentType(Intent intent, FragmentType type) {
        intent.putExtra(FRAGMENT_TYPE, type.name());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !TextUtils.isEmpty(bundle.getString(FRAGMENT_TYPE))) {
            type = FragmentType.valueOf(bundle.getString(FRAGMENT_TYPE));
        }
        if (getThemeN() != null) setTheme(getThemeN());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fragment_container_app);
        session = new UserSessionManager(this.getApplicationContext(), this);
        setToolbar();
        onCreateView();
    }

    private void onCreateView() {
        setFragment();
    }

    private Integer getThemeN() {
        return null;
    }

    private boolean isVisibleBackButton() {
        return true;
    }

    private Boolean isHideToolbar() {
        return false;
    }

    private String getTitleTextView() {
        switch (type) {
            case ACCOUNT_SETTING:
                return getString(R.string.account_settings);
            case ACCOUNT_KEYBOARD:
                return getString(R.string.boost_keyboard);
            case MANAGE_CONTENT:
                return getString(R.string.manage_content);
            case MANAGE_INVENTORY:
                return Utils.getDefaultTrasactionsTaxonomyFromServiceCode(session.getFP_AppExperienceCode());
            case HELP_AND_SUPPORT:
                return getString(R.string.help_and_support);
            case ABOUT_BOOST:
                return getString(R.string.about);
            case NOTIFICATION_VIEW:
                return getString(R.string.notification);
            case UPDATE_LATEST_STORY_VIEW:
                return Utils.getLatestUpdatesTaxonomyFromServiceCode(session.getFP_AppExperienceCode());
            case MANAGE_CUSTOMER_VIEW:
                return getString(R.string.manage_customers);
            case SITE_METER_OLD_VIEW:
                return "Site Meter";
            default:
                return "";
        }
    }

    private int getToolbarTitleColor() {
        switch (type) {
            case ACCOUNT_SETTING:
            case ACCOUNT_KEYBOARD:
            case MANAGE_CONTENT:
            case MANAGE_INVENTORY:
            case HELP_AND_SUPPORT:
            case ABOUT_BOOST:
            case NOTIFICATION_VIEW:
            case UPDATE_LATEST_STORY_VIEW:
            case MANAGE_CUSTOMER_VIEW:
            case SITE_METER_OLD_VIEW:
            default:
                return R.color.white;
        }
    }

    private Drawable getNavigationIcon() {
        switch (type) {
            case ACCOUNT_SETTING:
            case ACCOUNT_KEYBOARD:
            case MANAGE_CONTENT:
            case MANAGE_INVENTORY:
            case HELP_AND_SUPPORT:
            case ABOUT_BOOST:
            case NOTIFICATION_VIEW:
            case UPDATE_LATEST_STORY_VIEW:
            case MANAGE_CUSTOMER_VIEW:
            case SITE_METER_OLD_VIEW:
            default:
                return ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        }
    }

    private Boolean shouldAddToBackStack() {
        return false;
    }

    private Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else return false;
    }

    private void setFragment() {
        Fragment fragment = getFragmentInstance(type);
        if (fragment != null) {
            fragment.setArguments(getIntent().getExtras());
            addFragmentReplace(binding.container.getId(), fragment, shouldAddToBackStack());
        } else throw new IllegalArgumentException();
    }

    private int getToolbarBackgroundColor() {
        switch (type) {
            case ACCOUNT_SETTING:
            case ACCOUNT_KEYBOARD:
            case MANAGE_CONTENT:
            case MANAGE_INVENTORY:
            case HELP_AND_SUPPORT:
            case ABOUT_BOOST:
            case UPDATE_LATEST_STORY_VIEW:
            case NOTIFICATION_VIEW:
            case MANAGE_CUSTOMER_VIEW:
            case SITE_METER_OLD_VIEW:
            default:
                return ContextCompat.getColor(this, R.color.colorAccent);

        }
    }

    private void addFragmentReplace(Integer containerId, Fragment fragment, Boolean addToBackStack) {
        if (getSupportFragmentManager().isDestroyed()) return;
        if (containerId == null || fragment == null) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) fragmentTransaction.addToBackStack(fragment.getClass().getName());
        try {
            fragmentTransaction.replace(containerId, fragment).commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        if (!isHideToolbar()) {
            getToolbar().setNavigationOnClickListener(v -> onBackPressed());
            setToolbarTitle(getTitleTextView());
            getToolbar().setNavigationIcon(getNavigationIcon());
            getToolbar().setBackgroundColor(getToolbarBackgroundColor());
            setSupportActionBar(getToolbar());
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(isVisibleBackButton());
        } else getToolbar().setVisibility(View.GONE);
    }

    private void setToolbarTitle(String title) {
        getToolbar().setTitle(title);
        getToolbar().setTitleTextColor(ContextCompat.getColor(this, getToolbarTitleColor()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Fragment getFragmentInstance(FragmentType type) {
        switch (type) {
            case ACCOUNT_SETTING:
                return new AccountSettingsFragment();
            case ACCOUNT_KEYBOARD:
                return new KeyboardFragment();
            case MANAGE_CONTENT:
                return new ManageContentFragment();
            case MANAGE_INVENTORY:
                return new ManageInventoryFragment();
            case HELP_AND_SUPPORT:
                return new HelpAndSupportFragment();
            case ABOUT_BOOST:
                return new AboutFragment();
            case NOTIFICATION_VIEW:
                return new NotificationFragment();
            case UPDATE_LATEST_STORY_VIEW:
                return new Home_Main_Fragment();
            case MANAGE_CUSTOMER_VIEW:
                return new ManageCustomerFragment();
            case SITE_METER_OLD_VIEW:
                return new Site_Meter_Fragment();
            default:
                return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}