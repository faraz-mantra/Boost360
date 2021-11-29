package com.nowfloats.NavigationDrawer.popup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.thinksity.R;

import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

public class PurchaseFeaturesPopup extends DialogFragment {

    LinearLayout mainLayout;
    RelativeLayout secondaryLayout;
    TextView buyItemButton, featureName;

    UserSessionManager session;

    String itemName = "";
    String buyItemKey = "";


    @Override
    public void onStart() {
        super.onStart();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.fullscreen_color);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.purchase_feature_popup, container, false);
        session = new UserSessionManager(getActivity().getApplicationContext(), (HomeActivity) requireActivity());
        itemName = getArguments().getString("itemName");
        buyItemKey = getArguments().getString("buyItemKey");
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        secondaryLayout = (RelativeLayout) view.findViewById(R.id.secondary_layout);
        buyItemButton = (TextView) view.findViewById(R.id.buy_item);
        featureName = (TextView) view.findViewById(R.id.feature_name);

        featureName.setText(itemName);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        secondaryLayout.setOnClickListener(null);

        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
                dismiss();
            }
        });


    }

    private void initiateBuyFromMarketplace() {
        Intent intent = new Intent((HomeActivity) getActivity(), UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("fpTag", session.getFpTag());
        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getUserProfileEmail());
        } else {
            intent.putExtra("email", getString(R.string.ria_customer_mail));
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile());
        } else {
            intent.putExtra("mobileNo", getString(R.string.ria_customer_number));
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        intent.putExtra("buyItemKey", buyItemKey);
        startActivity(intent);
    }
}
