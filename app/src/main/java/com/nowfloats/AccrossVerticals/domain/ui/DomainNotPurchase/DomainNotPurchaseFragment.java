package com.nowfloats.AccrossVerticals.domain.ui.DomainNotPurchase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

public class DomainNotPurchaseFragment extends Fragment {

    private DomainNotPurchaseViewModel mViewModel;
    private TextView headerText, buyItem;
    private UserSessionManager session;

    public static DomainNotPurchaseFragment newInstance() {
        return new DomainNotPurchaseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        session = new UserSessionManager(getContext(), requireActivity());
        mViewModel = ViewModelProviders.of(this).get(DomainNotPurchaseViewModel.class);

        return inflater.inflate(R.layout.domain_not_purchase_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setheader
        setHeader(view);

        buyItem = (TextView) view.findViewById(R.id.buy_item);
        buyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });
    }

    public void setHeader(View view) {
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText("Website Domain");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        String status = getString(R.string.loading_please_wait);
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent(getContext(), UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getFPPrimaryContactNumber() != null) {
            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", "DOMAINPURCHASE");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
        }, 1000);
    }

}