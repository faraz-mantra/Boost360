package com.nowfloats.Store;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;

/**
 * Created by Admin on 13-02-2018.
 */

public class TopUpPlansActivity extends AppCompatActivity{

    private TopUpPlansAdapter adapter;
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
        adapter = new TopUpPlansAdapter();
        mRecyclerView.setAdapter(adapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
                    holder.setCardHolderData(R.drawable.ic_dictate_plan,"Dictate",getString(R.string.dictate_definition));
                    break;
                case 2:
                    holder.setCardHolderData(R.drawable.ic_business_apps,"My Business App",getString(R.string.dictate_definition));
                    break;
            }
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data){
            if (topUpDialog != null)
            topUpDialog.onPaymentResultReceived(requestCode,resultCode,data);
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
            TopUpCardHolder(View itemView) {
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
                        topUpDialog = new TopUpDialog(TopUpPlansActivity.this);

                        switch (getAdapterPosition()){
                            case 0:
                                topUpDialog.getTopUpPricing(TopUpDialog.TopUpType.WildFire.name());
                                break;
                            case 1:
                                topUpDialog.getTopUpPricing(TopUpDialog.TopUpType.Dictate.name());
                                break;
                            case 2:
                                topUpDialog.getTopUpPricing(TopUpDialog.TopUpType.App.name());
                                break;
                        }
                        break;
                }
            }
        }
    }


}
