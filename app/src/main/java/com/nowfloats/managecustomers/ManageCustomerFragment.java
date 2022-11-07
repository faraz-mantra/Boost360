package com.nowfloats.managecustomers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appservice.ui.calltracking.VmnCallCardsActivityV2;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Analytics_Screen.VmnNumberRequestActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.customerassistant.ThirdPartyQueriesActivity;
import com.nowfloats.manageinventory.SellerAnalyticsActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageCustomerFragment extends Fragment implements View.OnClickListener {
    UserSessionManager session;
    SharedPreferences.Editor prefsEditor;
    private SharedPreferences pref = null;
    private Activity activity;
    private RevealFrameLayout overLayout1, overLayout2;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_manage_customers, container, false);
        pref = activity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(final View mainView, Bundle savedInstanceState) {
        super.onViewCreated(mainView, savedInstanceState);
        if (!isAdded() || isDetached()) return;
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.manage_customers));
        }
        mainView.findViewById(R.id.img_info1).setOnClickListener(this);
        mainView.findViewById(R.id.img_info2).setOnClickListener(this);
        String subscriberCount = session.getSubcribersCount();
        String enquiryCount = session.getEnquiryCount();
        String callCount = session.getVmnCallsCount();
        overLayout1 = mainView.findViewById(R.id.rfl_overlay1);
        overLayout2 = mainView.findViewById(R.id.rfl_overlay2);
        ((TextView) mainView.findViewById(R.id.tv_subscriber_count)).setText(subscriberCount);
        ((TextView) mainView.findViewById(R.id.tv_enquiries_count)).setText(enquiryCount);
        ((TextView) mainView.findViewById(R.id.tv_calls_count)).setText(callCount);
        mainView.findViewById(R.id.ll_order).setOnClickListener(this);
        mainView.findViewById(R.id.ll_enquiry).setOnClickListener(this);
        mainView.findViewById(R.id.ll_calls).setOnClickListener(this);
        mainView.findViewById(R.id.ll_subscriber).setOnClickListener(this);
        mainView.findViewById(R.id.ll_facebook_chat).setOnClickListener(this);
        mainView.findViewById(R.id.ll_third_party_query).setOnClickListener(this);
    }


    private void showOverlay(final RevealFrameLayout overLayout, String title, String msg) {
        RelativeLayout revealLayout = overLayout.findViewById(R.id.ll_reveal_layout);
        revealLayout.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOverlay(overLayout);
            }
        });
        ((TextView) revealLayout.findViewById(R.id.tvInfoTitle)).setText(title);
        ((TextView) revealLayout.findViewById(R.id.tvInfo)).setText(msg);

        int cx = (revealLayout.getLeft() + revealLayout.getRight());
        int cy = revealLayout.getTop();
        int radius = Math.max(revealLayout.getWidth(), revealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            revealLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, 0, radius);
            revealLayout.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void closeOverlay(final RevealFrameLayout overLayout) {

        final RelativeLayout revealLayout = overLayout.findViewById(R.id.ll_reveal_layout);
        int cx = (revealLayout.getLeft() + revealLayout.getRight());
        int cy = revealLayout.getTop();
        int radius = Math.max(revealLayout.getWidth(), revealLayout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Animator anim = ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    revealLayout.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

        } else {
            Animator anim = android.view.ViewAnimationUtils.
                    createCircularReveal(revealLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    revealLayout.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

        }

    }


    private void openCallLog() {

        final boolean isVmnEnable = "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME));
        final String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);

        if (isVmnEnable) {
            Intent i = new Intent(getActivity(), VmnCallCardsActivityV2.class);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if ((TextUtils.isDigitsOnly(paymentState) && "1".equalsIgnoreCase(paymentState))) {
            Intent i = new Intent(getActivity(), VmnNumberRequestActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Methods.showFeatureNotAvailDialog(getActivity());
            // show first buy lighthouse
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.manage_customers));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_info1:
                MixPanelController.track(EventKeysWL.MERCHANT_EDUCATION_MANAGE_CUSTOMERS, null);
                showOverlay(overLayout1, getString(R.string.website_customers), getString(R.string.manage_website_customers));
                return;
            case R.id.img_info2:
                MixPanelController.track(EventKeysWL.MERCHANT_EDUCATION_MANAGE_CUSTOMERS, null);
                showOverlay(overLayout2, getString(R.string.cross_platform), getString(R.string.manage_multichannel_customers));
                return;
            case R.id.ll_calls:
                openCallLog();
                return;
            case R.id.ll_subscriber:
                MixPanelController.track(EventKeysWL.SIDE_PANEL_SUBSCRIBERS, null);
                startActivity(new Intent(getActivity(), SubscribersActivity.class));
                break;
            case R.id.ll_enquiry:
                MixPanelController.track(EventKeysWL.SIDE_PANEL_BUSINESS_ENQUIRIES, null);
                startActivity(new Intent(getActivity(), BusinessEnquiryActivity.class));
                break;
            case R.id.ll_facebook_chat:
                startActivity(new Intent(getActivity(), FacebookChatActivity.class));
                break;
            case R.id.ll_third_party_query:
                startActivity(new Intent(getActivity(), ThirdPartyQueriesActivity.class));
                break;
            case R.id.ll_order:
                MixPanelController.track(EventKeysWL.SIDE_PANEL_SELLER_ANALYTICS, null);
                startActivity(new Intent(getActivity(), SellerAnalyticsActivity.class));
                break;
        }
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
