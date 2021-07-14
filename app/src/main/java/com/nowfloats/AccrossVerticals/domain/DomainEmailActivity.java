package com.nowfloats.AccrossVerticals.domain;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.framework.models.firestore.FirestoreManager;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.GetDomain.GetDomainData;
import com.nowfloats.AccrossVerticals.domain.ui.ActiveDomain.ActiveDomainFragment;
import com.nowfloats.AccrossVerticals.domain.ui.DomainNotPurchase.DomainNotPurchaseFragment;
import com.nowfloats.AccrossVerticals.domain.ui.DomainPurchased.DomainPurchasedFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class DomainEmailActivity extends AppCompatActivity {

    public UserSessionManager session;
    public String clientid = "2D5C6BB4F46457422DA36B4977BD12E37A92EEB13BB4423A548387BA54DCEBD5";
    private ProgressDialog vmnProgressBar;
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

        session = new UserSessionManager(this, this);
        vmnProgressBar = new ProgressDialog(this);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        createView();
    }

    private void createView() {
        if (session.getStoreWidgets().contains("DOMAINPURCHASE")) {
            loadData();
        } else {
            addFragment(new DomainNotPurchaseFragment(), "DOMAIN_NOT_PURCHASE_FRAGMENT");
            onDomainAddedOrUpdated(false);
        }
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

    private void showProgress() {
        if (!vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.show();
        }
    }

    private void hideProgress() {
        if (vmnProgressBar.isShowing() && !isFinishing()) {
            vmnProgressBar.dismiss();
        }
    }

    private void loadData() {
        try {
            showProgress();
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("http://plugin.withfloats.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.getDomainDetails(session.getFpTag(), Constants.clientId3, new Callback<GetDomainData>() {
                @Override
                public void success(GetDomainData domainData, Response response) {
                    hideProgress();
                    if (domainData == null || response.getStatus() != 200) {
                        Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (domainData.getDomainName() != null && !domainData.getDomainName().isEmpty()) {
                        addFragment(new ActiveDomainFragment(), "ACTIVE_DOMAIN_FRAGMENT");
                        onDomainAddedOrUpdated(true);
                    } else {
                        addFragment(new DomainPurchasedFragment(), "DOMAIN_PURCHASE_FRAGMENT");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    Methods.showSnackBarNegative(DomainEmailActivity.this, getString(R.string.something_went_wrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDomainAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData().getMetricdetail() == null) return;
        instance.getDrScoreData().getMetricdetail().setBoolean_add_custom_domain_name_and_ssl(isAdded);
        instance.updateDocument();
    }

}