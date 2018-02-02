package com.nowfloats.Store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;

/**
 * Created by Admin on 29-01-2018.
 */

public class UpgradesFragment extends Fragment {

    private Context mContext;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container,false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || isDetached()) return;

        String[] adapterTexts = getResources().getStringArray(R.array.upgrade_tab_items);
        int[] adapterImages = {R.drawable.sidepanel_store, R.drawable.wildfire_gray, R.drawable.dictate_gray, R.drawable.new_business_app};
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new SimpleImageTextListAdapter.onItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(pos){
                    case 0:
                        intent = new Intent(mContext, NewPricingPlansActivity.class);
                        break;
                    case 1:
                        intent = new Intent(mContext,FragmentsFactoryActivity.class);
                        intent.putExtra("fragmentName","WildFireFragment");
                        break;
                    case 2:
                        intent = new Intent(mContext,FragmentsFactoryActivity.class);
                        intent.putExtra("fragmentName","DictateFragment");
                        break;
                    case 3:
                        startBusinessApp();
                        return;
                    default:
                        return;
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        adapter.setItems(adapterImages,adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }
    private void startBusinessApp() {
        Intent i;
        int businessAppStatus = pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO);
        if (businessAppStatus == BIZ_APP_DEMO) {
           i = new Intent(mContext, FragmentsFactoryActivity.class);
            i.putExtra("fragmentName","BusinessAppsFragment");
        } else {
            if (businessAppStatus == BIZ_APP_PAID) {
                pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO_REMOVE).apply();
            }
            i = new Intent(mContext, BusinessAppsDetailsActivity.class);
        }
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
        {
            HomeActivity.headerText.setText(getString(R.string.upgrades));
        }
    }
}
