package com.nowfloats.Store;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Store.Adapters.PurchasedPlanAdapter;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.AllPackage;
import com.nowfloats.Store.Model.InvoiceDetailsModel;
import com.nowfloats.Store.Model.InvoiceDetailsResult;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 31-01-2018.
 */

public class YourPurchasedPlansActivity extends AppCompatActivity implements PurchasePlanFragment.AdapterCallback {

    UserSessionManager mSession;
    Toolbar toolbar;
    List<PackageDetails> mTopUps = Collections.emptyList();

    private static final int NUM_OF_FEATURES = 5;
    MaterialDialog materialProgress;

    ViewPager mViewPager;

    @Override
    public PurchasedPlanAdapter getAdapter(PlansType planType) {
        PurchasedPlanAdapter adapter = new PurchasedPlanAdapter(this, planType);
        switch (planType) {
            case ACTIVE_PLANS:
                adapter.setPlansList(activePlans);
                break;
            case YOUR_ORDERS:
                adapter.setPlansList(yourOrdersItems);
                break;
            case TO_BE_ACTIVATED_PLANS:
                adapter.setPlansList(toBeActivatedPlans);
                break;
            case EXPIRED_PLANS:
                adapter.setPlansList(expiredPlans);
                break;
            default:
                throw new RuntimeException(getString(R.string.you_can_use_other_type_of_plans));
        }
        return adapter;
    }

    public enum PlansType {
        ACTIVE_PLANS,
        TO_BE_ACTIVATED_PLANS,
        EXPIRED_PLANS,
        YOUR_ORDERS;

        public static String getName(int pos) {
            switch (pos) {
                case 0:
                    return "ACTIVE";
                case 3:
                    return "YOUR INVOICES";
                case 1:
                    return "TO BE ACTIVATED";
                case 2:
                    return "EXPIRED";
                default:
                    return getName(0);
            }
        }

        public static PlansType getPlanType(int pos) {
            switch (pos) {
                case 0:
                    return ACTIVE_PLANS;
                case 3:
                    return YOUR_ORDERS;
                case 1:
                    return TO_BE_ACTIVATED_PLANS;
                case 2:
                    return EXPIRED_PLANS;
                default:
                    return ACTIVE_PLANS;
            }
        }
    }

    ArrayList<ActivePackage> activePlans = new ArrayList<>(), toBeActivatedPlans = new ArrayList<>(),
            expiredPlans = new ArrayList<>();
    ArrayList<ActivePackage> yourOrdersItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_plans);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            setTitle("Subscription History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mSession = new UserSessionManager(this, this);
        getPricingPlanDetails();
        getInvoiceDetailsByFPTag();
    }

    private void getInvoiceDetailsByFPTag() {
        Map<String, String> params = new HashMap<>();
        params.put("clientId", Constants.clientId);
        params.put("FpTag", mSession.getFpTag());
        params.put("offset", "0");
        params.put("pagesize", "1000");
        params.put("saleType", "-1");

        Constants.restAdapter.create(StoreInterface.class).getInvoiceDetailsByFPTag(params, new Callback<InvoiceDetailsModel>() {
            @Override
            public void success(InvoiceDetailsModel invoiceDetailsModel, Response response) {
                if (invoiceDetailsModel != null && invoiceDetailsModel.results != null
                        && invoiceDetailsModel.results.size() > 0) {
//                    ActivePackage header = new ActivePackage();
//                    yourOrdersItems.add(header);
                    for (InvoiceDetailsResult result : invoiceDetailsModel.results) {
                        ActivePackage activePackage = new ActivePackage();
                        activePackage.setId(result.getOrderConfirmationId());
                        activePackage.setPaymentDate(result.getPaymentDate());
                        activePackage.setCurrencyCode(result.getCurrencyCode());
                        if (result.getPackageDetails() != null && !result.getPackageDetails().isEmpty()) {
                            List<PackageDetails> packageDetailsList = new ArrayList<>();
                            for (PackageDetails packageDetail : result.getPackageDetails()) {
                                PackageDetails packageDetails = new PackageDetails();
                                packageDetails.setPackageName(packageDetail.getPackageName());
                                packageDetails.setNetPackagePrice(packageDetail.getNetPackagePrice());
                                packageDetailsList.add(packageDetails);
                            }
                            activePackage.setPackageDetails(packageDetailsList);
                        }

                        activePackage.setClaimid(result.getClaimid());
                        yourOrdersItems.add(activePackage);
                    }
                } else {
                    Toast.makeText(YourPurchasedPlansActivity.this, getString(R.string.oops_invoice_details_not_found), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
                // zeroth screen
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
                Log.d("Test", error.getMessage());
            }
        });
    }

    private void getPricingPlanDetails() {
        showDialog();
        String accId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID);
        String appId = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_APPLICATION_ID);
        String country = mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY);
        Map<String, String> params = new HashMap<>();
        if (accId.length() > 0) {
            params.put("identifier", accId);
        } else {
            params.put("identifier", appId);
        }
        params.put("clientId", Constants.clientId);
        params.put("fpId", mSession.getFPID());
        params.put("country", country.toLowerCase());
        params.put("fpCategory", mSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).toUpperCase());

        Constants.restAdapter.create(StoreInterface.class).getStoreList(params, new Callback<PricingPlansModel>() {
            @Override
            public void success(PricingPlansModel storeMainModel, Response response) {
                if (storeMainModel != null) {
                    preProcessAndDispatchPlans(storeMainModel);
                } else {
                    Toast.makeText(YourPurchasedPlansActivity.this, getString(R.string.oops_pricing_plans_not_found), Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
                // zeroth screen
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
                Log.d("Test", error.getMessage());
            }
        });

    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            return PurchasePlanFragment.getInstance(bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PlansType.getName(position);
        }

        @Override
        public int getCount() {
            return PlansType.values().length;
        }
    }

    private void preProcessAndDispatchPlans(final PricingPlansModel storeMainModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ActivePackage activePackage : storeMainModel.activePackages) {
                    int featuresCount = 0;
                    StringBuilder featuresBuilder = new StringBuilder("");
                    if (activePackage.getWidgetPacks() != null) {
                        for (WidgetPacks widget : activePackage.getWidgetPacks()) {
                            if (widget.Name != null) {
                                featuresBuilder.append("• " + widget.Name + "\n");
                                featuresCount++;
                                if (featuresCount >= NUM_OF_FEATURES) {
                                    break;
                                }
                            }
                        }
                        if (featuresCount > 0) {
                            featuresBuilder.delete(featuresBuilder.lastIndexOf("\n"), featuresBuilder.length());
                        }
                    }
                    activePackage.setFeatures(featuresBuilder.toString());
                    if (Calendar.getInstance().getTimeInMillis() < getMilliseconds(activePackage.getToBeActivatedOn())) {
                        toBeActivatedPlans.add(activePackage);
                        activePackage.setActiveStatus("Not Active");
                    } else if (!isPackageExpired(activePackage)) {
                        activePlans.add(activePackage);
                        activePackage.setActiveStatus("Active");
                    } else {
                        expiredPlans.add(activePackage);
                        activePackage.setActiveStatus("Expired");
                    }
                }
                for (AllPackage allPackage : storeMainModel.allPackages) {
                    if (allPackage.getKey().equals("TopUp")) {
                        mTopUps = allPackage.getValue();
                        for (PackageDetails topUp : mTopUps) {
                            List<String> featuresList = new ArrayList<>();
                            int count = 0;
                            if (topUp.getWidgetPacks() != null && topUp.getWidgetPacks().size() > 0) {

                                for (WidgetPacks widget : topUp.getWidgetPacks()) {
                                    if (widget.Name != null) {
                                        featuresList.add(widget.Name);
                                        count++;
                                        if (count >= 8) {
                                            break;
                                        }
                                    }
                                }
                            }
                            topUp.setFeatureList(featuresList);
                        }
                    }
                }

                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                    }
                });
            }
        }).start();
    }

    private long getMilliseconds(String date) {
        if (date.contains("/Date")) {
            date = date.replace("/Date(", "").replace(")/", "");
        }
        return Long.valueOf(date);
    }

    private void hideDialog() {
        if (materialProgress.isShowing())
            materialProgress.dismiss();
    }

    private void showDialog() {
        if (materialProgress == null) {
            materialProgress = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.accentColor)
                    .content(getString(R.string.please_wait_))
                    .progress(true, 0)
                    .cancelable(false)
                    .build();

        }
        if (!materialProgress.isShowing()) {
            materialProgress.show();
        }
    }

    private boolean isPackageExpired(ActivePackage activePackage) {
        long time = Long.parseLong(activePackage.getToBeActivatedOn().replaceAll("[^\\d]", ""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        double totalMonthsValidity = activePackage.getTotalMonthsValidity();
        calendar.add(Calendar.MONTH, (int) Math.floor(totalMonthsValidity));
        calendar.add(Calendar.DATE, (int) ((totalMonthsValidity - Math.floor(totalMonthsValidity)) * 30));
        return calendar.getTime().before(new Date());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
