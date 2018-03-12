package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.UI.changePasswordAsyncTask;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.Store.YourPurchasedPlansActivity;
import com.nowfloats.domain.DomainDetailsActivity;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 29-01-2018.
 */

public class AccountSettingsFragment extends Fragment implements DomainApiService.DomainCallback{

    private Context mContext;
    private EditText old_pwd, new_pwd, confirm_pwd;
    Boolean confirmCheckerActive = false;
    private ImageView confirmChecker;
    UserSessionManager sessionManager;
    private ProgressDialog progressDialog;
    private DomainApiService domainApiService;
    private final static int LIGHT_HOUSE_EXPIRED =-1,DEMO =0,DEMO_EXPIRED=-2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || getActivity() == null){
            Methods.showSnackBar(view,getString(R.string.something_went_wrong_try_again), Color.RED);
            return;
        }

        domainApiService = new DomainApiService(this);
        sessionManager = new UserSessionManager(mContext, getActivity());
        String[] adapterTexts = getResources().getStringArray(R.array.account_setting_tab_items);
        int[] adapterImages = {R.drawable.ic_site_apperance, R.drawable.ic_domain_enad_emails,
                R.drawable.ic_your_plan, R.drawable.icon_change_password,R.drawable.icon_logout};
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch(pos){
                    case 0:
                        intent = new Intent(mContext, SiteAppearanceActivity.class);
                        break;
                    case 1:
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
                        } else if(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("0")){
                            showExpiryDialog(DEMO);
                        }else if(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("-1") &&
                                sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL).equalsIgnoreCase("0")){
                            showExpiryDialog(DEMO_EXPIRED);
                        } else if (Methods.isOnline(getActivity())) {
                            showLoader(getString(R.string.please_wait));
                            domainApiService.getDomainDetails(mContext,sessionManager.getFpTag(), getDomainDetailsParam());
                        } else {
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
                        }
                        return;
                    case 2:
                        intent = new Intent(mContext, YourPurchasedPlansActivity.class);
                        break;
                    case 3:
                        changePassword();
                        return;
                    case 4:
                        logoutAlertDialog_Material();
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


    public void logoutAlertDialog_Material() {

        new MaterialDialog.Builder(mContext)
                .customView(R.layout.exit_dialog, true)
                .positiveText(getString(R.string.setting_logout))
                .negativeText(getString(R.string.cancel))
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.light_gray)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sessionManager.logoutUser();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void changePassword() {
        if (getActivity() == null) return;
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.change_password, true)
                .positiveText(getString(R.string.ok))
                .negativeText(getString(R.string.cancel))
                .positiveColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String oldPass = old_pwd.getText().toString().trim();
                        String newPass = new_pwd.getText().toString().trim();
                        String confirmPass = confirm_pwd.getText().toString().trim();
                        Boolean confirm = newPass.equals(confirmPass);

                        if (newPass.length() > 5) {
                            if (confirm) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("clientId", Constants.clientId);
                                    obj.put("currentPassword", oldPass);
                                    obj.put("newPassword", newPass);
                                    obj.put("username", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (Util.isNetworkStatusAvialable(mContext)) {
                                    changePasswordAsyncTask task = new changePasswordAsyncTask(mContext, obj, getActivity());
                                    dialog.dismiss();
                                    task.execute();
                                } else {
                                    Methods.showSnackBarNegative(getActivity(), getString(R.string.check_internet_connection));
                                }

                            } else {
                                Methods.showSnackBarNegative(getActivity(), getString(R.string.both_password_not_matched));
                            }
                        } else {
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.min_6char_required));
                        }
                    }
                }).show();
        confirmDetails(dialog);
    }

    private void confirmDetails(MaterialDialog view) {

        old_pwd = (EditText) view.findViewById(R.id.change_pwd_old_pwd);
        new_pwd = (EditText) view.findViewById(R.id.change_pwd_new_pwd);
        confirm_pwd = (EditText) view.findViewById(R.id.change_pwd_confirm_pwd);
        confirmChecker = (ImageView) view.findViewById(R.id.confirm_pwd_status_img);
        confirm_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPwd = new_pwd.getText().toString().trim();
                String confirmPwd = confirm_pwd.getText().toString().trim();
                confirmCheckerActive = true;
                if (confirmPwd.equals(newPwd)) {
                    confirmChecker.setVisibility(View.VISIBLE);
                    confirmChecker.setImageResource(R.drawable.domain_available);
                } else {
                    confirmChecker.setVisibility(View.VISIBLE);
                    confirmChecker.setImageResource(R.drawable.domain_not_available);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });

        new_pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (confirmCheckerActive) {
                    String newPwd = new_pwd.getText().toString().trim();
                    String confirmPwd = confirm_pwd.getText().toString().trim();
                    if (confirmPwd.equals(newPwd)) {
                        confirmChecker.setImageResource(R.drawable.domain_available);
                    } else {
                        confirmChecker.setImageResource(R.drawable.domain_not_available);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });
    }
    private void showLoader(final String message) {

        if (getActivity() == null) return;

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
        if (getActivity() == null) return;
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

                            case CONTACTS_AND_EMAIL_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) mContext).
                                        onClick(getResources().getString(R.string.contact__info));
                                break;
                            case CATEGORY_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) mContext).
                                        onClick(getResources().getString(R.string.basic_info));
                                break;
                            case ADDRESS_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) mContext).
                                        onClick(getResources().getString(R.string.business__address));
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
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
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
                        Intent intent = new Intent(mContext, NewPricingPlansActivity.class);
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
    @Override
    public void getDomainDetails(DomainDetails domainDetails) {
//        domainDetails = null;
        hideLoader();

        if(!isAlreadyCalled) {
            if (domainDetails != null && domainDetails.response == DomainDetails.DOMAIN_RESPONSE.ERROR){
                Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong));
            } else if (domainDetails != null && domainDetails.response == DomainDetails.DOMAIN_RESPONSE.DATA) {
                isDomainDetailsAvali = true;
                if( !TextUtils.isEmpty(domainDetails.getErrorMessage()) && domainDetails.getIsProcessingFailed()){
                    showCustomDialog(getString(R.string.domain_booking_failed),
                            Methods.fromHtml(getString(R.string.drop_us_contact)).toString(),
                            getString(R.string.ok), null, DialogFrom.DEFAULT);
                }else if(TextUtils.isDigitsOnly(domainDetails.getProcessingStatus()) && Integer.parseInt(domainDetails.getProcessingStatus())<=16){

                    showCustomDialog(getString(R.string.domain_booking_process),
                            getString(R.string.domain_booking_process_message),
                            getString(R.string.ok), null, DialogFrom.DEFAULT);
                }else
                {
                    showLoader(getString(R.string.please_wait));
                    domainApiService.getDomainFPDetails(sessionManager.getFPID(), getDomainDetailsParam());
                }

            }
            else if (!TextUtils.isEmpty(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))) {
                showCustomDialog("Domain Details", "You have linked your domain to " +
                                sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI) + " successfully.",
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            }else if(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("-1")) {
                showExpiryDialog(LIGHT_HOUSE_EXPIRED);
            }else if (Methods.isOnline(getActivity())){
                showLoader(getString(R.string.please_wait));
                domainApiService.getDomainFPDetails(sessionManager.getFPID(), getDomainDetailsParam());
            }else{
                Methods.snackbarNoInternet(getActivity());
            }
        }
    }

    @Override
    public void emailBookingStatus(ArrayList<EmailBookingModel.EmailBookingStatus> bookingStatuses) {

    }

    @Override
    public void getEmailBookingList(ArrayList<String> ids, String error) {

    }

    @Override
    public void getDomainSupportedTypes(ArrayList<String> arrExtensions) {

    }

    @Override
    public void domainAvailabilityStatus(DomainApiService.DomainAPI domainAPI) {

    }

    @Override
    public void domainBookStatus(String response) {

    }

    private static final String PAYMENT_STATE_SUCCESS = "1";
    private static final String ROOT_ALIAS_URI = "nowfloats";
    private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";
    private Get_FP_Details_Model get_fp_details_model;
    private boolean isAlreadyCalled = false;
    private boolean isDomainDetailsAvali = false;

    @Override
    public void getFpDetails(Get_FP_Details_Model get_fp_details_model) {
        hideLoader();
        this.get_fp_details_model = get_fp_details_model;
        if (TextUtils.isEmpty(get_fp_details_model.response)) {

            if (isDomainDetailsAvali){
                showDomainDetails();
            }
            else if (TextUtils.isEmpty(get_fp_details_model.getEmail())
                    || get_fp_details_model.getContacts() == null) {
                showCustomDialog(getString(R.string.domain_detail_required),
                        Methods.fromHtml("Insufficient data to book domain. Please update your Email Address.").toString(),
                        "Update Email Address", null, DialogFrom.CONTACTS_AND_EMAIL_REQUIRED);

            } else if (get_fp_details_model.getCategory() == null || get_fp_details_model.getCategory().size() == 0) {
                showCustomDialog(getString(R.string.domain_detail_required),
                        Methods.fromHtml("Insufficient data to book domain. Please update your Business Category.").toString(),
                        "Update Business Category", null, DialogFrom.CATEGORY_REQUIRED);
            } else if (TextUtils.isEmpty(get_fp_details_model.getAddress())
                    || TextUtils.isEmpty(get_fp_details_model.getLat())
                    || TextUtils.isEmpty(get_fp_details_model.getLng())
                    || get_fp_details_model.getLat().equalsIgnoreCase("0")
                    || get_fp_details_model.getLng().equalsIgnoreCase("0")
                    ||TextUtils.isEmpty(get_fp_details_model.getPinCode())) {
                showCustomDialog(getString(R.string.domain_detail_required),
                        Methods.fromHtml("Insufficient data to book domain. Please update you Business Address.").toString(),
                        "Update Business Address", null, DialogFrom.ADDRESS_REQUIRED);
            } else {
                showDomainDetails();
            }

        } else {
            Methods.showSnackBarNegative(getActivity(), get_fp_details_model.response);
        }
    }

    private void showDomainDetails() {
        isAlreadyCalled = true;
        Intent domainIntent = new Intent(mContext, DomainDetailsActivity.class);
        domainIntent.putExtra("get_fp_details_model", get_fp_details_model);
        startActivity(domainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null)
        {
            HomeActivity.headerText.setText(getString(R.string.account_settings));
        }
    }
}
