package com.nowfloats.Store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class UpgradesFragment extends Fragment {

    private Context mContext;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container, false);
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

        MixPanelController.track(MixPanelController.SUBSCRIPTIONS, null);
        final String[] adapterTexts = getResources().getStringArray(R.array.upgrade_tab_items);
        final int[] adapterImages = {R.drawable.ic_plan_details, R.drawable.ic_subscription_history};

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch (adapterTexts[pos]) {
                    case "Plan Details":
                        MixPanelController.track(MixPanelController.BUY_AND_RENEW, null);
                        intent = new Intent(mContext, NewPricingPlansActivity.class);
                        break;
                    case "Subscription History":
                        MixPanelController.track(MixPanelController.SUBSCRIPTION_HISTORY, null);
                        intent = new Intent(mContext, YourPurchasedPlansActivity.class);
                        break;
                }

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new

                LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.subscriptions));
        }
    }

//    @Override
//    public void onItemClick(int pos) {
//        Intent intent = null;
//        switch (pos) {
//            case 0:
//                intent = new Intent(mContext, NewPricingPlansActivity.class);
//                break;
//            case 1:
//                intent = new Intent(mContext, TopUpPlansActivity.class);
//                break;
//            default:
//                return;
//        }
//        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }
}
