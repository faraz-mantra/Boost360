package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.boost.upgrades.UpgradeActivity;
import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;
import com.webengage.sdk.android.WebEngage;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.requestlist.RequestListActivity;

import static com.framework.webengageconstant.EventLabelKt.CALL_SUPPORT_OPTION_IN_ACCOUNT;
import static com.framework.webengageconstant.EventLabelKt.CHAT_OPTION_IN_ACCOUNT;
import static com.framework.webengageconstant.EventLabelKt.DIRECT_AGENT_CALL_OPTION_IN_ACCOUNT;
import static com.framework.webengageconstant.EventLabelKt.EMAIL_OPTION_IN_ACCOUNT;
import static com.framework.webengageconstant.EventLabelKt.LEARN_HOW_TO_USE;
import static com.framework.webengageconstant.EventLabelKt.SUPPORT_SCREEN_LOADED;
import static com.framework.webengageconstant.EventLabelKt.VIEW_MY_SUPPORT_TICKETS;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_CALL;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_CHAT;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_DIRECT_AGENT_CALL;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_EMAIL;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_LEARN;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_VIEWED;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_VIEWED_PREMIUM;
import static com.framework.webengageconstant.EventNameKt.SUPPORT_VIEW_TICKETS;
import static com.framework.webengageconstant.EventValueKt.NULL;
import static com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_CATEGORY;

/**
 * Created by Admin on 28-12-2017.
 */

public class HelpAndSupportCardItemFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private RiaSupportModel riaSupportModel;
    private UserSessionManager sessionManager;
    public static String RIA_MODEL_DATA = "ria_model_data";
    private boolean is_premium_support = false;

    public static Fragment getInstance(Bundle b){
        HelpAndSupportCardItemFragment frag = new HelpAndSupportCardItemFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            riaSupportModel = new Gson().fromJson(getArguments().getString(RIA_MODEL_DATA), RiaSupportModel.class);
        }
        sessionManager = new UserSessionManager(mContext, getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_help_and_support,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;

        if (Constants.StoreWidgets.contains("CUSTOMERSUPPORT")){
            is_premium_support = true;
        }

        ImageView personImage =  view.findViewById(R.id.img_person);
        EditText emailTv = view.findViewById(R.id.tv_person_email);
        EditText numberTv = view.findViewById(R.id.tv_person_number);
        EditText nameTv = view.findViewById(R.id.tv_person_name);
        TextView slaTv = view.findViewById(R.id.sla_text);
        RelativeLayout chatActionBtn = view.findViewById(R.id.btn_chat_action);
        RelativeLayout callActionBtn = view.findViewById(R.id.btn_call_option);

        emailTv.setOnClickListener(this);
        numberTv.setOnClickListener(this);

        view.findViewById(R.id.btn_faqs).setOnClickListener(this);
        view.findViewById(R.id.btn_my_tickets).setOnClickListener(this);

        WebEngageController.trackEvent(is_premium_support ? SUPPORT_VIEWED_PREMIUM : SUPPORT_VIEWED, SUPPORT_SCREEN_LOADED, NULL);

        nameTv.setText(riaSupportModel.getName());
        if(is_premium_support)
            numberTv.setText("1860-123-1233");
        else
            numberTv.setText("xxx-xxx-xxxx");

        if(is_premium_support)
            emailTv.setText("ria@boost360.app");
        else
            emailTv.setText(riaSupportModel.getEmail());

        chatActionBtn.setOnClickListener(this);
        callActionBtn.setOnClickListener(this);

        if(is_premium_support){
            (view.findViewById(R.id.chat_option_lock)).setVisibility(View.GONE);
            (view.findViewById(R.id.call_option_lock)).setVisibility(View.GONE);
            slaTv.setText("* Response time SLA - 1 hour *");
        } else
            slaTv.setText("* Response time SLA - 72 hours *");
    }

    private void showPremiumAddOnDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.upgrade_to_premium_support)
                .content(R.string.you_are_currently_on_the_default_support_plan)
                .positiveText(getString(R.string.save_data))
                .negativeText(R.string.later)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.gray_40)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        String status = getString(R.string.loading_please_wait);
                        progressDialog.setMessage(status);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        UserSessionManager session = new UserSessionManager(getContext(), getActivity());
                        Intent intent = new Intent(getActivity(), UpgradeActivity.class);
                        intent.putExtra("expCode", session.getFP_AppExperienceCode());
                        intent.putExtra("fpName", session.getFPName());
                        intent.putExtra("fpid", session.getFPID());
                        intent.putExtra("fpTag", session.getFpTag());
                        intent.putExtra("accountType", session.getFPDetails(GET_FP_DETAILS_CATEGORY));
                        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets);
                        if (session.getUserProfileEmail() != null) {
                            intent.putExtra("email", session.getUserProfileEmail());
                        } else {
                            intent.putExtra("email", "ria@nowfloats.com");
                        }
                        if (session.getFPPrimaryContactNumber() != null) {
                            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
                        } else {
                            intent.putExtra("mobileNo", "9160004303");
                        }
                        intent.putExtra("profileUrl", session.getFPLogo());
                        intent.putExtra("buyItemKey", "CUSTOMERSUPPORT");
                        startActivity(intent);
                        new Handler().postDelayed(() -> {
                            progressDialog.dismiss();
                        },1000);
                    }
                }).build();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_chat_action:
                WebEngageController.trackEvent(SUPPORT_CHAT, CHAT_OPTION_IN_ACCOUNT, NULL);
                if(is_premium_support) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateobj = new Date();

                    if (sessionManager != null) {
                        VisitorInfo visitorInfo = new VisitorInfo.Builder()
                                .name(sessionManager.getFPName())
                                .email(sessionManager.getFPEmail())
                                .phoneNumber(sessionManager.getFPPrimaryContactNumber())
                                .note("FPTag: " + sessionManager.getFpTag() + "\n\nUserId: " + sessionManager.getUserProfileId() + "\nUserContact: " + sessionManager.getUserProfileMobile())
                                .build();
                        ZopimChat.setVisitorInfo(visitorInfo);
                    }

                    startActivity(new Intent(WebEngage.getApplicationContext(), ZopimChatActivity.class));
                } else
                    showPremiumAddOnDialog();
                break;
            case R.id.tv_person_email:
                WebEngageController.trackEvent(SUPPORT_EMAIL, EMAIL_OPTION_IN_ACCOUNT, NULL);
                Methods.sendEmail(mContext, new String[]{riaSupportModel.getEmail()}, getString(R.string.need_help_with_boost) + sessionManager.getFpTag() + " , " + sessionManager.getFP_AppExperienceCode() + "]");
                break;
            case R.id.tv_person_number:
                if(is_premium_support) {
                    WebEngageController.trackEvent(SUPPORT_DIRECT_AGENT_CALL, DIRECT_AGENT_CALL_OPTION_IN_ACCOUNT, NULL);
                    Methods.makeCall(mContext, riaSupportModel.getPhoneNumber());
                } else {
                    showPremiumAddOnDialog();
                }
                break;
            case R.id.btn_call_option:
                if(is_premium_support) {
                    WebEngageController.trackEvent(SUPPORT_CALL, CALL_SUPPORT_OPTION_IN_ACCOUNT, NULL);
                    Methods.makeCall(mContext, riaSupportModel.getPhoneNumber());
                } else {
                    showPremiumAddOnDialog();
                }
                break;
            case R.id.btn_my_tickets:
                WebEngageController.trackEvent(SUPPORT_VIEW_TICKETS, VIEW_MY_SUPPORT_TICKETS, NULL);
                RequestListActivity.builder().show(mContext);
                break;
            case R.id.btn_faqs:
                WebEngageController.trackEvent(SUPPORT_LEARN, LEARN_HOW_TO_USE, NULL);
                HelpCenterActivity.builder().show(mContext);
                break;
        }
    }
}