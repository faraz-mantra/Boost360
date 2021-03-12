package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Store.Adapters.PurchasedPlanAdapter;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventLabelKt.LOGIN_ERROR;
import static com.framework.webengageconstant.EventNameKt.YOUR_PLANS_CURRENT;
import static com.framework.webengageconstant.EventNameKt.YOUR_PLANS_EXPIRED;
import static com.framework.webengageconstant.EventNameKt.YOUR_PLANS_TO_BE_ACTIVATED;
import static com.framework.webengageconstant.EventValueKt.NULL;

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
        LinearLayout orderHeader = view.findViewById(R.id.orderHeader);
        orderHeader.setVisibility(View.GONE);
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_plans);

        switch (planType) {
            case ACTIVE_PLANS:
                WebEngageController.trackEvent(YOUR_PLANS_CURRENT, LOGIN_ERROR, NULL);
                aboutPlan.setText(R.string.the_following_plan_in_use);
                break;
            case YOUR_ORDERS:
                orderHeader.setVisibility(View.VISIBLE);
                aboutPlan.setText("");
                int pixels = Methods.dpToPx(20, getActivity());
                mRecyclerView.setPadding(pixels, 0, pixels, pixels);
                break;
            case TO_BE_ACTIVATED_PLANS:
                WebEngageController.trackEvent(YOUR_PLANS_TO_BE_ACTIVATED, EVENT_LABEL_NULL, NULL);
                aboutPlan.setText(R.string.the_following_plans_are_not_in_use);
                break;
            case EXPIRED_PLANS:
                WebEngageController.trackEvent(YOUR_PLANS_EXPIRED, EVENT_LABEL_NULL, NULL);
                aboutPlan.setText(R.string.the_following_plan_is_are_expired);
                break;
        }

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
