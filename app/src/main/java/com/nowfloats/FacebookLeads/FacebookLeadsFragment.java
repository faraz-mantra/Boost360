package com.nowfloats.FacebookLeads;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boost.upgrades.UpgradeActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.enablekeyboard.BoostKeyboardActivity;
import com.nowfloats.enablekeyboard.KeyboardThemesAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import io.separ.neural.inputmethod.indic.LatinIME;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 02-03-2018.
 */

public class FacebookLeadsFragment extends Fragment {

    SharedPreferences sharedPreferences;

    private Context mContext;
    private LinearLayout secondaryLayout;
    private TextView buyItemButton;
    private UserSessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_facebook_leads, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new UserSessionManager(requireActivity().getApplicationContext(), (HomeActivity) requireActivity());

        //show or hide if feature is available to user
        secondaryLayout= (LinearLayout) view.findViewById(R.id.secondary_layout);
        buyItemButton = (TextView) view.findViewById(R.id.buy_item);
        if(Constants.StoreWidgets.contains("WILDFIRE_FB_LEAD_ADS")) {
            secondaryLayout.setVisibility(View.GONE);
        }else{
            secondaryLayout.setVisibility(View.VISIBLE);
        }
        buyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBuyFromMarketplace();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null && mContext instanceof HomeActivity)
            headerText.setText(getString(R.string.facebook_leads));
    }

    private void initiateBuyFromMarketplace() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        String status = "Loading. Please wait...";
        progressDialog.setMessage(status);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = new Intent((HomeActivity) requireActivity(), UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("loginid", session.getUserProfileId());
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
        intent.putExtra("buyItemKey", "WILDFIRE_FB_LEAD_ADS");
        startActivity(intent);
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
        },1000);
    }
}
