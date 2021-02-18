package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.AccrossVerticals.domain.DomainDetailsActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.util.HashMap;

/**
 * Created by Admin on 05-10-2017.
 */

public class Business_Profile_Fragment_V3 extends Fragment implements View.OnClickListener {
    @Nullable
    Context mContext;
    UserSessionManager session;
    private DomainApiService domainApiService;
    boolean isAlreadyCalled;
    private SharedPreferences pref = null;
    private final static int LIGHT_HOUSE_EXPIRED =-1,DEMO =0,DEMO_EXPIRED=-2;
    private Bus mBus;
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor prefsEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_profile_v2,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pref = mContext.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        prefsEditor = pref.edit();
        mBus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(mContext.getApplicationContext(), getActivity());
        //domainApiService = new DomainApiService(mBus);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isAdded()){
            return;
        }
        session = new UserSessionManager(mContext,getActivity());
        ImageView profileImage = (ImageView) view.findViewById(R.id.img_profile);
        ImageView editImage = (ImageView) view.findViewById(R.id.img_edit);
        TextView description = (TextView) view.findViewById(R.id.tv_business_description);
        TextView businessName = (TextView) view.findViewById(R.id.tv_business_name);
        TextView category = (TextView) view.findViewById(R.id.tv_business_category);
        view.findViewById(R.id.cv_business_details).setOnClickListener(this);
        view.findViewById(R.id.cv_business_images).setOnClickListener(this);
        view.findViewById(R.id.cv_site_appearance).setOnClickListener(this);
        view.findViewById(R.id.cv_custom_pages).setOnClickListener(this);
        view.findViewById(R.id.cv_pricing_plans).setOnClickListener(this);
        view.findViewById(R.id.cv_domain_details).setOnClickListener(this);
        editImage.setOnClickListener(this);
        String iconUrl = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI);
        if (iconUrl.length() > 0 && iconUrl.contains("BizImages") && !iconUrl.contains("http")) {

            Picasso.get()
                    .load(Constants.BASE_IMAGE_URL + "" + iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(profileImage);
        } else if ( iconUrl.length() > 0) {
                Picasso.get()
                        .load(iconUrl).placeholder(R.drawable.business_edit_profile_icon).into(profileImage);
        }

        if (session.getIsSignUpFromFacebook().contains("true") && !Util.isNullOrEmpty(session.getFacebookPageURL())) {
            Picasso.get()
                    .load(session.getFacebookPageURL()).placeholder(R.drawable.business_edit_profile_icon)
                    .rotate(90)
                    .into(profileImage);
        }

        Typeface robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        category.setTypeface(robotoLight);
        category.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY));
        businessName.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        description.setTypeface(robotoLight);
        description.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION));

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.cv_business_details:
                intent = new Intent(mContext,BusinessDetailsActivity.class);
                break;
            case R.id.cv_business_images:
                break;
            case R.id.cv_site_appearance:
                intent = new Intent(mContext,SiteAppearanceActivity.class);
                break;
            case R.id.cv_custom_pages:
                intent = new Intent(mContext,CustomPageActivity.class);
                break;
            case R.id.cv_domain_details:
                callDomainDetails();
                break;
            case R.id.cv_pricing_plans:
                intent = new Intent(mContext,NewPricingPlansActivity.class);
                startActivity(intent);
                break;
            case R.id.img_edit:
                intent = new Intent(mContext,Edit_Profile_Activity.class);
                break;
        }
        if(intent != null){
            startActivity(intent);
            if(mContext instanceof Activity) {
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    private void callDomainDetails() {
        isAlreadyCalled = false;
        MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY, null);
        if (!BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                    .title("Get A Domain")
                    .customView(R.layout.dialog_link_layout, false)
                    .positiveText(getString(R.string.ok))
                    .positiveColorRes(R.color.primaryColor)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                        }

                    });
            if (!getActivity().isFinishing()) {
                builder.show();
            }
        } else if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("0")){
            showExpiryDialog(DEMO);
        }else if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("-1") &&
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL).equalsIgnoreCase("0")){
            showExpiryDialog(DEMO_EXPIRED);
        } else if (Utils.isNetworkConnected(getActivity())) {
            showLoader(getString(R.string.please_wait));
            domainApiService.getDomainDetails(getActivity(),session.getFpTag(), getDomainDetailsParam());
        } else {
            Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }


    /*
     * Domain Details Param
     */
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
    }
    private void showLoader(final String message) {

        if (!isAdded()) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
    private void showCustomDialog(String title, String message, String postiveBtn, String negativeBtn,
                                  final DialogFrom dialogFrom) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(title)
                .customView(R.layout.dialog_link_layout, false)
                .positiveText(postiveBtn)
                .negativeText(negativeBtn)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (dialogFrom) {

                           /* case CONTACTS_AND_EMAIL_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.contact__info));
                                break;
                            case CATEGORY_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.basic_info));
                                break;
                            case ADDRESS_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.business__address));*/
                            case DEFAULT:

                                break;
                        }
                    }
                    /*
                    ((SidePanelFragment.OnItemClickListener) activity).
                onClick(getResources().getString(R.string.business_profile));
                     */

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:
                                MixPanelController.track(MixPanelController.DOMAIN_SEARCH, null);
                                break;
                            case DEFAULT:

                                break;
                        }
                    }
                });

        final MaterialDialog materialDialog = builder.show();
        View maView = materialDialog.getCustomView();

        TextView tvMessage = (TextView) maView.findViewById(R.id.toast_message_to_contact);
        tvMessage.setText(message);
    }
    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }

    @Subscribe
    public void getDomainDetails(DomainDetails domainDetails) {
//        domainDetails = null;
        hideLoader();

        if(!isAlreadyCalled) {
            if (domainDetails == null){
                Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong));
            } else if(domainDetails.isFailed()){
                showCustomDialog(getString(R.string.domain_booking_failed),
                        Methods.fromHtml(TextUtils.isEmpty(domainDetails.getErrorMessage())?
                                getString(R.string.drop_us_contact):domainDetails.getErrorMessage()).toString(),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            }else if(domainDetails.isPending()){

                showCustomDialog(getString(R.string.domain_booking_process),
                        getString(R.string.domain_booking_process_message),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            }else{
                showDomainDetails();
            }
        }
    }

    private void showDomainDetails() {
        isAlreadyCalled = true;
        Intent domainIntent = new Intent(mContext, DomainDetailsActivity.class);
        startActivity(domainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showExpiryDialog(int showDialog) {

        String callUsButtonText, cancelButtonText, dialogTitle, dialogMessage;
        int dialogImage, dialogImageBgColor;

        switch (showDialog) {
            case LIGHT_HOUSE_EXPIRED:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.buy_light_house);
                break;
            case DEMO_EXPIRED:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            default:
                return;
        }

        MaterialDialog mExpireDailog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .positiveText(callUsButtonText)
                .negativeText(cancelButtonText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(mContext,NewPricingPlansActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();

        View view = mExpireDailog.getCustomView();

        roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(mContext, dialogImage));

        roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }
}
