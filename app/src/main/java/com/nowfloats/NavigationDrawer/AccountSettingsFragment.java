package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appservice.constant.FragmentType;
import com.appservice.constant.IntentConstant;
import com.appservice.model.SessionData;
import com.appservice.model.StatusKyc;
import com.dashboard.controller.DashboardFragmentContainerActivityKt;
import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.nowfloats.BusinessProfile.UI.UI.changePasswordAsyncTask;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.SimpleImageTextListAdapter;
import com.nowfloats.Store.YourPurchasedPlansActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.appservice.ui.bankaccount.AccountFragmentContainerActivityKt.startFragmentAccountActivityNew;
import static com.appservice.ui.paymentgateway.PaymentGatewayContainerActivityKt.startFragmentPaymentActivityNew;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_CHANGEPASSWORD;
import static com.framework.webengageconstant.EventLabelKt.NO_EVENT_LABLE;
import static com.framework.webengageconstant.EventNameKt.DOMAIN_EMAIL;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_CHANGEPASSWORD;
import static com.framework.webengageconstant.EventValueKt.NULL;

/**
 * Created by Admin on 29-01-2018.
 */

public class AccountSettingsFragment extends Fragment implements DomainApiService.DomainCallback {

    private final static int LIGHT_HOUSE_EXPIRED = -1, DEMO = 0, DEMO_EXPIRED = -2;
    Boolean confirmCheckerActive = false;
    UserSessionManager sessionManager;
    private Context mContext;
    private EditText old_pwd, new_pwd, confirm_pwd;
    private ImageView confirmChecker;
    private ProgressDialog progressDialog;
    private DomainApiService domainApiService;
    private boolean isAlreadyCalled = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade, container, false);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || getActivity() == null) {
            Methods.showSnackBar(view, getString(R.string.something_went_wrong_try_again), Color.RED);
            return;
        }
        domainApiService = new DomainApiService(this);
        sessionManager = new UserSessionManager(mContext, getActivity());
        final String[] adapterTexts = getResources().getStringArray(R.array.account_setting_tab_items);
        final TypedArray imagesArray = getResources().obtainTypedArray(R.array.account_settings);
        int[] adapterImages = new int[adapterTexts.length];
        for (int i = 0; i < adapterTexts.length; i++) {
            adapterImages[i] = imagesArray.getResourceId(i, -1);
        }
        imagesArray.recycle();
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_upgrade);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        SimpleImageTextListAdapter adapter = new SimpleImageTextListAdapter(mContext, new OnItemClickCallback() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = null;
                switch (adapterTexts[pos]) {
                    case "My Business Profile":
//                        intent = new Intent(mContext, FragmentsFactoryActivity.class);
//                        intent.putExtra("fragmentName", "Business_Profile_Fragment_V2");
//                        startActivity(intent);
                        DashboardFragmentContainerActivityKt.startFragmentDashboardActivity(((AppCompatActivity) requireActivity())
                                , com.dashboard.constant.FragmentType.FRAGMENT_BUSINESS_PROFILE, new Bundle(), false);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case "My Bank Account":
                        Bundle bundle = new Bundle();
                        bundle.putString(IntentConstant.CLIENT_ID.name(), Constants.clientId);
                        bundle.putString(IntentConstant.USER_PROFILE_ID.name(), sessionManager.getUserProfileId());
                        bundle.putString(IntentConstant.FP_ID.name(), sessionManager.getFPID());
                        if (sessionManager.getAccountSave()) {
                            startFragmentAccountActivityNew(getActivity(), FragmentType.BANK_ACCOUNT_DETAILS, bundle, false);
                        } else {
                            startFragmentAccountActivityNew(getActivity(), FragmentType.ADD_BANK_ACCOUNT_START, bundle, false);
                        }
//                        intent = new Intent(mContext, AccountInfoActivity.class);
                        break;
                    case "Self Branded Payment Gateway":
                        Bundle b = getBundleDataKyc();
                        startFragmentPaymentActivityNew(getActivity(), com.appservice.constant.FragmentType.PAYMENT_GATEWAY, b, false);
                        break;
                    case "My Business KYC":
                        Bundle b1 = getBundleDataKyc();
                        if (sessionManager.isSelfBrandedKycAdd()) {
                            startFragmentPaymentActivityNew(getActivity(), com.appservice.constant.FragmentType.KYC_STATUS, b1, false);
                        } else
                            startFragmentPaymentActivityNew(getActivity(), FragmentType.BUSINESS_KYC_VIEW, b1, false);
                        break;
                    case "Boost Extensions":
                        intent = new Intent(mContext, Boost360ExtensionsActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
//                    case "Site Appearance":
//                        intent = new Intent(mContext, SiteAppearanceActivity.class);
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        break;
                    case "Domain and Email":
                        isAlreadyCalled = false;
                        MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY, null);
                        WebEngageController.trackEvent(DOMAIN_EMAIL, NO_EVENT_LABLE, sessionManager.getFpTag());
                        if (Methods.isOnline(getActivity())) {
                            showLoader(getString(R.string.please_wait));
                            domainApiService.getDomainDetails(mContext, sessionManager.getFpTag(), getDomainDetailsParam());
                        } else {
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
                        }
                        return;
                    case "Subscription History":
                        intent = new Intent(mContext, YourPurchasedPlansActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case "Change Password":
                        changePassword();
                        WebEngageController.trackEvent(EVENT_NAME_CHANGEPASSWORD, EVENT_LABEL_CHANGEPASSWORD, NULL);
                        return;
                    case "Log out":
                        logoutAlertDialog_Material();
                        return;
                    default:
                        return;
                }
            }
        });
        adapter.setItems(adapterImages, adapterTexts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);

    }

    private Bundle getBundleDataKyc() {
        SessionData session = new SessionData();
        session.setClientId(Constants.clientId);
        session.setUserProfileId(sessionManager.getUserProfileId());
        session.setFpId(sessionManager.getFPID());
        session.setFpTag(sessionManager.getFpTag());
        session.setExperienceCode(sessionManager.getFP_AppExperienceCode());
        session.setFpLogo(sessionManager.getFPLogo());
        session.setFpEmail(sessionManager.getFPEmail());
        session.setFpNumber(sessionManager.getFPPrimaryContactNumber());
        session.setSelfBrandedAdd(sessionManager.isSelfBrandedKycAdd());
        session.setPaymentGateway(sessionManager.getStoreWidgets().contains(StatusKyc.CUSTOM_PAYMENTGATEWAY.name()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(com.appservice.constant.IntentConstant.SESSION_DATA.name(), session);
        return bundle;
    }

    public void logoutAlertDialog_Material() {

        new MaterialDialog.Builder(mContext)
                .customView(R.layout.exit_dialog, true)
                .positiveText(getString(R.string.setting_logout))
                .negativeText(getString(R.string.cancel))
                .negativeColorRes(R.color.black_4a4a4a)
                .positiveColorRes(R.color.colorAccentLight)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sessionManager.logoutUser();
//                        dialog.dismiss();
//                        WebEngageController.logout();
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
                .negativeColorRes(R.color.black_4a4a4a)
                .positiveColorRes(R.color.colorAccentLight)
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
                    confirmChecker.setImageResource(R.drawable.checkmark_icon);
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

        getActivity().runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(message);
            progressDialog.show();
        });
    }

    private void hideLoader() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
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
                .negativeColorRes(R.color.black_4a4a4a)
                .positiveColorRes(R.color.colorAccentLight)
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

        TextView tvMessage = maView.findViewById(R.id.toast_message_to_contact);
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

        roboto_md_60_212121 title = view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(mContext, dialogImage));

        roboto_lt_24_212121 message = view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }

    @Override
    public void getDomainDetails(DomainDetails domainDetails) {
        hideLoader();
        if (!isAdded() || getActivity() == null) return;
        if (!isAlreadyCalled) {
            showDomainDetails();
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
    public void domainAvailabilityStatus(String domainName, String domainType, DomainApiService.DomainAPI domainAPI) {

    }

    @Override
    public void domainBookStatus(String response) {

    }

    private void showDomainDetails() {
        isAlreadyCalled = true;
        //old design
//        Intent domainIntent = new Intent(mContext, DomainDetailsActivity.class);
        //new design implementation work in progress
        Intent domainIntent = new Intent(mContext, DomainEmailActivity.class);
        startActivity(domainIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText(getString(R.string.account_settings));
        }
    }

    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }
}
