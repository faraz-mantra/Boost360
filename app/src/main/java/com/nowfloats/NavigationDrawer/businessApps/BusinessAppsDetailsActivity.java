package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appservice.ui.business_logo.BusinessLogoActivityV2;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Abhi on 12/12/2016.
 */

public class BusinessAppsDetailsActivity extends AppCompatActivity {

    public final static int SHOW_PREVIEW = 0, SHOW_ABOUT_APP = 1, SHOW_SITE_HEALTH = 2;
    private UserSessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new UserSessionManager(this, this);
        if (getSupportActionBar() != null) {
            setTitle(getString(R.string.my_business_apps));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        /*if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))){
            ShowDialogLogo();
        }*/
        addFragments(SHOW_PREVIEW);

    }

    private void ShowDialogLogo() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("Business Logo Missing!")
                .content(getString(R.string.add_logo_message))
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        finish();
                        dialog.dismiss();
                        Intent i = new Intent(BusinessAppsDetailsActivity.this, BusinessLogoActivityV2.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        dialog.dismiss();
                    }
                })
                .positiveText("Add Now")
                .negativeText("Back");
        if (!isFinishing()) {
            builder.show();
        }
    }

    public void addFragments(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragA = getSupportFragmentManager().findFragmentByTag("BusinessAppPreview");
        Fragment siteHealthFrag = getSupportFragmentManager().findFragmentByTag("siteHealth");
        switch (id) {
            case SHOW_PREVIEW:
                if (fragA == null) {
                    fragA = new BusinessAppPreview();
                    transaction.add(R.id.linearlayout_business_app, fragA, "BusinessAppPreview");
                } else if (fragA.isHidden()) {
                    transaction.show(fragA);
                }
                break;
            case SHOW_ABOUT_APP:
                Fragment frag = new BusinessAppsFragment();
                if (fragA != null && fragA.isVisible()) {
                    transaction.hide(fragA);
                }
                if (siteHealthFrag != null && siteHealthFrag.isVisible()) {
                    transaction.hide(siteHealthFrag);
                }
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.add(R.id.linearlayout_business_app, frag, "BusinessAppFragment");
                transaction.addToBackStack(null);
                break;
            case SHOW_SITE_HEALTH:
                if (siteHealthFrag == null) {
                    siteHealthFrag = new Site_Meter_Fragment();
                }
                if (fragA != null && fragA.isVisible()) {
                    transaction.hide(fragA);
                }
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.add(R.id.linearlayout_business_app, siteHealthFrag, "siteHealth");
                transaction.addToBackStack(null);
                break;
            default:
                break;
        }
        transaction.commit();
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_app,menu);
        return true;
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
               /* FragmentManager manager = getSupportFragmentManager();
                BusinessAppsFragment frag = (BusinessAppsFragment) manager.findFragmentByTag("BusinessAppFragment");
                if(frag != null && frag.isVisible()){
                    manager.beginTransaction().remove(frag).commit();
                }*/
                onBackPressed();
                return true;
            case R.id.action_notif:
                Methods.materialDialog(this, "Send Push Notification", "Inform your app users about your latest product offerings via push notifications. This feature is coming soon.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
