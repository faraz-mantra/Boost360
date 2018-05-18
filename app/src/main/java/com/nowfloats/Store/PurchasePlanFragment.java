package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Store.Adapters.PurchasedPlanAdapter;
import com.thinksity.R;

/**
 * Created by Admin on 31-01-2018.
 */

public class PurchasePlanFragment extends Fragment {

    private Context mContext;
    private YourPurchasedPlansActivity.PlansType planType;

    public static PurchasePlanFragment getInstance(Bundle bundle) {
        PurchasePlanFragment frag = new PurchasePlanFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planType = YourPurchasedPlansActivity.PlansType.getPlanType(getArguments().getInt("pos"));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isAdded() || isDetached()) return;
        TextView aboutPlan = view.findViewById(R.id.tv_about_plan);
        switch (planType) {
            case ACTIVE_PLANS:
                aboutPlan.setText("The following plan(s) is/are currently in use");
                break;
            case YOUR_ORDERS:
                aboutPlan.setText("The following plan(s) is/are not verified to use");
                break;
            case TO_BE_ACTIVATED_PLANS:
                aboutPlan.setText("The following plan(s) is/are not in use");
                break;
            case EXPIRED_PLANS:
                aboutPlan.setText("The following plan(s) is/are expired");
                break;
        }

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_plans);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
       /* if (planType.equals(YourPurchasedPlansActivity.PlansType.YOUR_ORDERS)) {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }*/
        mRecyclerView.setLayoutManager(layoutManager);
        if (mContext instanceof AdapterCallback) {
            mRecyclerView.setAdapter(((AdapterCallback) mContext).getAdapter(planType));
        }

    }

    public interface AdapterCallback {
        PurchasedPlanAdapter getAdapter(YourPurchasedPlansActivity.PlansType pos);
    }
}
