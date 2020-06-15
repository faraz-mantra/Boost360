package com.nowfloats.domain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.domain.ui.DomainNotPurchase.DomainNotPurchaseFragment;
import com.nowfloats.domain.ui.DomainPurchased.DomainPurchasedFragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DomainEmailActivity extends AppCompatActivity {

    private DomainNotPurchaseFragment domainNotPurchaseFragment;
    private ProgressDialog progressDialog;
    private HashMap<String, Integer> hmPrices = new HashMap<>();
    public UserSessionManager session;
    public static TextView headerText;
    private Toolbar toolbar;

    private Fragment currentFragment = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_email);
        initializeView();
        initView();
    }

    private void initView() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment =
                        getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                if (currentFragment != null) {
                    String tag = currentFragment.getTag();
                    Log.e("tag", ">>>$tag");
                } else {
                    finish();
                }
            }
        });
    }

    private void initializeView() {

        domainNotPurchaseFragment = new DomainNotPurchaseFragment();
        session = new UserSessionManager(this, this);

        toolbar = findViewById(R.id.app_bar);
        headerText = toolbar.findViewById(R.id.titleTextView);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        createView();

        //testingPurpos
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFrame, new DomainPurchasedFragment())
                // Add this transaction to the back stack
                .addToBackStack("Profile")
                .commit();

    }

    private void createView() {
        if (!Constants.StoreWidgets.contains("DOMAINPURCHASE")) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainFrame, domainNotPurchaseFragment)
                    // Add this transaction to the back stack
                    .addToBackStack("Profile")
                    .commit();
        }else{
            loadData();
        }
    }

    private void loadData() {
        showLoader(getString(R.string.please_wait));
        initializePrices();
    }

    private void initializePrices() {
        hmPrices.put(".COM", 680);
        hmPrices.put(".NET", 865);
        hmPrices.put(".CO.IN", 375);
        hmPrices.put(".IN", 490);
        hmPrices.put(".ORG", 500);
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

    private void showLoader(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getApplicationContext());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void addFragment(Fragment fragment, String fragmentTag) {
        currentFragment = fragment;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFrame, fragment, fragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, String fragmentTag) {
        popFragmentFromBackStack();
        addFragment(fragment, fragmentTag);
    }

    public void setHeaderText(String value){
        headerText.setText(value);
    }

    public void popFragmentFromBackStack() {
        fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    private void performBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                Fragment currentFragment =
                        getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                String tag = currentFragment.getTag();
                Log.e("back pressed tag", ">>>$tag");
                popFragmentFromBackStack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}