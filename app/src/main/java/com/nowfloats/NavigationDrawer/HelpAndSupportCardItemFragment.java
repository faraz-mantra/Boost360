package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

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
    public static String RIA_MODEL_DATA = "ria_model_data";
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
        ImageView personImage =  view.findViewById(R.id.img_person);
        EditText emailTv = view.findViewById(R.id.tv_person_email);
        EditText numberTv = view.findViewById(R.id.tv_person_number);
        EditText nameTv = view.findViewById(R.id.tv_person_name);
        TextView descriptionTv = view.findViewById(R.id.tv_person_description);
        TextView callActionBtn = view.findViewById(R.id.btn_call_action);
        callActionBtn.setOnClickListener(this);
        emailTv.setOnClickListener(this);
        numberTv.setOnClickListener(this);
        TextView requestActionBtn = view.findViewById(R.id.btn_request_callback);
        requestActionBtn.setOnClickListener(this);

        view.findViewById(R.id.btn_faqs).setOnClickListener(this);
        view.findViewById(R.id.btn_my_tickets).setOnClickListener(this);

        WebEngageController.trackEvent("ACCOUNT MANAGER","Support Screen Loaded",null);


        switch (HelpAndSupportFragment.MemberType.valueOf(riaSupportModel.getType())){
            case CHC:
                emailTv.setVisibility(View.GONE);
                view.findViewById(R.id.tv_email).setVisibility(View.GONE);
                //callActionBtn.setText("CALL NOW");
                requestActionBtn.setVisibility(View.VISIBLE);
                callActionBtn.setVisibility(View.GONE);
                descriptionTv.setText("Your Digital Consultant");
                personImage.setImageResource(riaSupportModel.getGender() == 1?R.drawable.ic_consultant_female:R.drawable.ic_consultant_male);
                break;
//            case WEB:
//                //callActionBtn.setText("CHAT NOW");
//                //personImage.setImageResource(riaSupportModel.getGender() == 1?R.drawable.ic_support_female:R.drawable.ic_support_male);
//                //descriptionTv.setText("Your Web Consultant");
//                descriptionTv.setText("Customer Support");
//                personImage.setImageResource(R.drawable.ria_circle_image);
//                break;
//            case DEFAULT:
//                //callActionBtn.setText("CALL NOW");
//                //requestActionBtn.setVisibility(View.GONE);
//                requestActionBtn.setVisibility(View.VISIBLE);
//                callActionBtn.setVisibility(View.GONE);
//                personImage.setImageResource(R.drawable.ria_circle_image);
//                descriptionTv.setText("Customer Support");
            default:
                descriptionTv.setText("Your Digital Assistant");
                personImage.setImageResource(R.drawable.ria_circle_image);
                break;
        }
        nameTv.setText(riaSupportModel.getName());
        numberTv.setText(riaSupportModel.getPhoneNumber());
        emailTv.setText(riaSupportModel.getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_call_action:
                /*if (HelpAndSupportFragment.MemberType.valueOf(riaSupportModel.getType()) == HelpAndSupportFragment.MemberType.WEB){
                    ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.chat));
                }else{
                    Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                    MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                }*/

                WebEngageController.trackEvent("ACCOUNT MANAGER","Chat option in Account",null);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date dateobj = new Date();
                RequestActivity.builder()
                            .withRequestSubject("Support required: " + df.format(dateobj))
                            .withTags("sdk", "android")
                            .show(mContext);

                //Old Code - to trigger RIA chat sdk powered by ANA
//                ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.chat));
                break;
            case R.id.tv_person_email:
                WebEngageController.trackEvent("ACCOUNT MANAGER","Email option in Account",null);
//                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_EMAIL,null);
                Methods.sendEmail(mContext,new String[]{riaSupportModel.getEmail()});
                break;
            case R.id.tv_person_number:
                WebEngageController.trackEvent("ACCOUNT MANAGER","Direct Agent Call option in Account",null);
//                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                break;
            case R.id.btn_request_callback:
                WebEngageController.trackEvent("ACCOUNT MANAGER","Call Support option in Account",null);
//                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                break;
            case R.id.btn_my_tickets:
                WebEngageController.trackEvent("ACCOUNT MANAGER","View My Support Tickets",null);
                RequestListActivity.builder()
                        .show(mContext);
                break;
            case R.id.btn_faqs:
                WebEngageController.trackEvent("ACCOUNT MANAGER","Learn How to use",null);
                HelpCenterActivity.builder()
                        .show(mContext);
                break;
        }
    }
}