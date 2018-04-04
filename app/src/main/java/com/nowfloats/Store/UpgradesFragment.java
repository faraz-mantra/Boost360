package com.nowfloats.Store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Store.Adapters.UpgradeAdapter;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.util.Constants;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class UpgradesFragment extends Fragment implements OnItemClickCallback{

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

//        String[] adapterTexts = getResources().getStringArray(R.array.upgrade_tab_items);
//        int[] adapterImages = {R.drawable.sidepanel_store, R.drawable.wildfire_gray, R.drawable.dictate_gray, R.drawable.new_business_app};
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
//        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
//            @Override
//            public void onItemClick(int pos) {
//
//            }
//        });
//        adapter.setItems(adapterImages,adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter( new UpgradeAdapter(mContext,this));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
        {
            HomeActivity.headerText.setText(getString(R.string.upgrades));
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent = null;
        switch(pos){
            case 0:
                intent = new Intent(mContext, NewPricingPlansActivity.class);
                break;
            case 1:
                intent = new Intent(mContext, TopUpPlansActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
