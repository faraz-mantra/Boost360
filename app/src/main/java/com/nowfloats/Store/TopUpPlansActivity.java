package com.nowfloats.Store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Store.Model.PackageDetails;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;

/**
 * Created by Admin on 13-02-2018.
 */

public class TopUpPlansActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_plans);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Top Up Plans");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView mRecyclerView = findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TopUpPlansAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TopUpPlansAdapter extends RecyclerView.Adapter<TopUpPlansAdapter.TopUpCardHolder> {

        private TopUpDialog topUpDialog;

        @Override
        public TopUpCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TopUpPlansActivity.this).inflate(R.layout.adapter_item_top_up,parent,false);
            return new TopUpCardHolder(view);
        }

        @Override
        public void onBindViewHolder(TopUpCardHolder holder, int position) {
            switch (position){
                case 0:
                    holder.setCardHolderData(R.drawable.wild_fire_expire,"Wildfire",getString(R.string.wildfire_definition));
                    break;
                case 1:
                    holder.setCardHolderData(R.drawable.dictate_gray,"Dictate",getString(R.string.dictate_definition));
                    break;
                case 2:
                    holder.setCardHolderData(R.drawable.dictate_gray,"My Business App",getString(R.string.dictate_definition));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        private void startDetails(int pos){
            Intent intent = null;
            switch (pos){
                case 0:
                    intent = new Intent(TopUpPlansActivity.this,FragmentsFactoryActivity.class);
                    intent.putExtra("fragmentName","WildFireFragment");
                    break;
                case 1:
                    intent = new Intent(TopUpPlansActivity.this,FragmentsFactoryActivity.class);
                    intent.putExtra("fragmentName","DictateFragment");
                    break;
                case 2:
                    startBusinessApp();
                    return;
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        private void startBusinessApp() {
            Intent i;
            SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
            int businessAppStatus = pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO);
            if (businessAppStatus == BIZ_APP_DEMO) {
                i = new Intent(TopUpPlansActivity.this, FragmentsFactoryActivity.class);
                i.putExtra("fragmentName","BusinessAppsFragment");
            } else {
                if (businessAppStatus == BIZ_APP_PAID) {
                    pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO_REMOVE).apply();
                }
                i = new Intent(TopUpPlansActivity.this, BusinessAppsDetailsActivity.class);
            }
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        class TopUpCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private TextView descriptionTv,typeTv;
            private ImageView topUpImage;
            public TopUpCardHolder(View itemView) {
                super(itemView);
                topUpImage = itemView.findViewById(R.id.img_top_up);
                descriptionTv = itemView.findViewById(R.id.tv_top_up_description);
                itemView.findViewById(R.id.tv_top_up_detail).setOnClickListener(this);
                itemView.findViewById(R.id.tv_top_up_pricing).setOnClickListener(this);
                typeTv = itemView.findViewById(R.id.tv_top_up_type);
            }

            private void setCardHolderData(int resId, String title, String description){
             descriptionTv.setText(Methods.fromHtml(description));
             typeTv.setText(title);
             topUpImage.setImageResource(resId);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.tv_top_up_detail:
                        startDetails(getAdapterPosition());
                        break;
                    case R.id.tv_top_up_pricing:
                        if (topUpDialog == null)
                        topUpDialog = new TopUpDialog(TopUpPlansActivity.this,TopUpPlansActivity.this);

                        switch (getAdapterPosition()){
                            case 0:
                                topUpDialog.getTopUpPricing("WildFire");
                                break;
                            case 1:
                                topUpDialog.getTopUpPricing("Dictate");
                                break;
                            case 2:
                                topUpDialog.getTopUpPricing("App");
                                break;
                        }
                        break;
                }
            }
        }
    }

    class TopUpDialog implements TopUpPlansService.ServiceCallbackListener, View.OnClickListener, TopUpDialogAdapter.onItemClickListener {

        private static final int DIRECT_REQUEST_CODE = 2013;
        private List<PackageDetails> mTopUps;
        private  List<PackageDetails> visiblePackages;
        private MaterialDialog topUpDialog;
        private Context mContext;
        private Activity activity;
        private String planType;
        private String selectedPackage;
        private ProgressDialog progressDialog;

        private TopUpDialog(Context context, Activity activity){
            mContext = context;
            this.activity = activity;
        }
        private void getTopUpPricing(String planType){
            this.planType = planType;
            if (mTopUps == null) {
                UserSessionManager mSession = new UserSessionManager(mContext, activity);
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
                TopUpPlansService services = TopUpPlansService.getTopUpService(this);
                services.getTopUpPackages(params);
            }else{
                showTopUpDialog();
            }
        }

        private void showTopUpDialog() {
            if (visiblePackages == null)
                visiblePackages = new ArrayList<>();
            else{
                visiblePackages.clear();
            }

            for (PackageDetails details:mTopUps){
                if(details.getName() != null && details.getName().toLowerCase().contains(planType.toLowerCase())){
                    visiblePackages.add(details);
                }
            }
            if (visiblePackages.size() == 0){
                Toast.makeText(mContext, "Your account can't have App packages.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (topUpDialog == null) {
                topUpDialog = new MaterialDialog.Builder(mContext)
                        .customView(R.layout.dialog_top_up_plans, false)
                        .positiveText("Buy")
                        .cancelable(false)
                        .positiveColorRes(R.color.primaryColor)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                startBuy(selectedPackage);
                            }
                        })
                        .build();
            }
            View view = topUpDialog.getCustomView();
            if (view == null) return;
            view.findViewById(R.id.img_cancel).setOnClickListener(this);
            TextView title = view.findViewById(R.id.tv_title);
            TextView description = view.findViewById(R.id.tv_description);
            title.setText(String.format("%s Pricing",planType));
            description.setText(String.format("Select the duration of %s package that suits your requirements.",planType));
            RecyclerView mRecyclerView = view.findViewById(R.id.rv_plans);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(TopUpPlansActivity.this));
            TopUpDialogAdapter adapter = new TopUpDialogAdapter(visiblePackages);
            selectedPackage = visiblePackages.get(0).getId();
            adapter.setItemClickCallback(this);
            mRecyclerView.setAdapter(adapter);
            topUpDialog.show();
        }

        private void startBuy(String id){
            Intent i = new Intent(mContext, ProductCheckoutActivity.class);
            i.putExtra("package_ids", new String[]{id});
            startActivityForResult(i, DIRECT_REQUEST_CODE);
        }
        @Override
        public void onDataReceived(List<PackageDetails> packages) {
            mTopUps = packages;
            showTopUpDialog();
        }
        @Override
        public void showDialog(){

                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage(getString(R.string.loading_wait));
                }
                if (!progressDialog.isShowing())
                    progressDialog.show();
        }

        @Override
        public void hideDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_cancel:
                    topUpDialog.dismiss();
                    break;
            }
        }

        @Override
        public void onItemClick(String id) {
            selectedPackage = id;
        }
    }
}
