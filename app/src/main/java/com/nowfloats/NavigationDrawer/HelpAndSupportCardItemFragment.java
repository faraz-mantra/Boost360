package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import zendesk.support.request.RequestActivity;
import zendesk.support.requestlist.RequestListActivity;

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

        WebEngageController.trackEvent(is_premium_support ? "SUPPORT - Viewed Premium" : "SUPPORT - Viewed","Support Screen Loaded",null);

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
                .title("Upgrade to Premium Support")
                .content("You are currently on the default support plan. This gives you access to Boost Care team via email only, with a promise of 72 hours SLA.\n\nYou can upgrade to premium support to get direct access to Boost Care team by phone, email and chat,\nwith a promise of 1 hour SLA.")
                .positiveText("View Details")
                .negativeText("Later")
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
                        String status = "Loading. Please wait...";
                        progressDialog.setMessage(status);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        UserSessionManager session = new UserSessionManager(getContext(), getActivity());
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
                WebEngageController.trackEvent("SUPPORT - CHAT","Chat option in Account",null);
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
                WebEngageController.trackEvent("SUPPORT - EMAIL","Email option in Account",null);
                Methods.sendEmail(mContext,new String[]{riaSupportModel.getEmail()}, "Need help with Boost360 [" + sessionManager.getFpTag() + " , " + sessionManager.getFP_AppExperienceCode()+ "]");
                break;
            case R.id.tv_person_number:
                if(is_premium_support) {
                    WebEngageController.trackEvent("SUPPORT - DIRECT_AGENT_CALL","Direct Agent Call option in Account",null);
                    Methods.makeCall(mContext, riaSupportModel.getPhoneNumber());
                } else {
                    showPremiumAddOnDialog();
                }
                break;
            case R.id.btn_call_option:
                if(is_premium_support) {
                    WebEngageController.trackEvent("SUPPORT - CALL", "Call Support option in Account", null);
                    Methods.makeCall(mContext, riaSupportModel.getPhoneNumber());
                } else {
                    showPremiumAddOnDialog();
                }
                break;
            case R.id.btn_my_tickets:
                WebEngageController.trackEvent("SUPPORT - VIEW_TICKETS","View My Support Tickets",null);
                RequestListActivity.builder()
                        .show(mContext);
                break;
            case R.id.btn_faqs:
                WebEngageController.trackEvent("SUPPORT - LEARN","Learn How to use",null);
                HelpCenterActivity.builder()
                        .show(mContext);
                break;
        }
    }
}