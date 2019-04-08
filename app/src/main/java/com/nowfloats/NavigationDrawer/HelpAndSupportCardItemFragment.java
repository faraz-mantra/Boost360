package com.nowfloats.NavigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.thinksity.R;

/**
 * Created by Admin on 28-12-2017.
 */
public class HelpAndSupportCardItemFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private RiaSupportModel riaSupportModel;

    public static String RIA_MODEL_DATA = "ria_model_data";
    public static String CHAT_SUPPORT_ENABLED = "chat_support_enabled";
    public static String PHONE_SUPPORT_ENABLED = "phone_support_enabled";

    private boolean isChatEnabled;
    private boolean isPhoneEnabled;

    public static Fragment getInstance(Bundle b)
    {
        HelpAndSupportCardItemFragment frag = new HelpAndSupportCardItemFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            riaSupportModel = new Gson().fromJson(getArguments().getString(RIA_MODEL_DATA), RiaSupportModel.class);
            isChatEnabled = getArguments().getBoolean(CHAT_SUPPORT_ENABLED, true);
            isPhoneEnabled = getArguments().getBoolean(PHONE_SUPPORT_ENABLED, true);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_card_help_and_support,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (!isAdded()) return;

        ImageView personImage =  view.findViewById(R.id.img_person);
        EditText emailTv = view.findViewById(R.id.tv_person_email);
        EditText numberTv = view.findViewById(R.id.tv_person_number);
        EditText nameTv = view.findViewById(R.id.tv_person_name);
        TextView descriptionTv = view.findViewById(R.id.tv_person_description);
        TextView callActionBtn = view.findViewById(R.id.btn_call_action);

        TextView labelNumber = view.findViewById(R.id.textView12);

        callActionBtn.setOnClickListener(this);
        emailTv.setOnClickListener(this);
        numberTv.setOnClickListener(this);
        TextView requestActionBtn = view.findViewById(R.id.btn_request_callback);
        requestActionBtn.setOnClickListener(this);

        switch (HelpAndSupportFragment.MemberType.valueOf(riaSupportModel.getType()))
        {
            case CHC:

                emailTv.setVisibility(View.GONE);
                view.findViewById(R.id.tv_email).setVisibility(View.GONE);
                callActionBtn.setText("CALL NOW");
                requestActionBtn.setVisibility(View.GONE);
                descriptionTv.setText("Your Local City Consultant");
                personImage.setImageResource(riaSupportModel.getGender() == 1?R.drawable.ic_consultant_female:R.drawable.ic_consultant_male);
                break;

            case WEB:

                callActionBtn.setText("CHAT NOW");
                personImage.setImageResource(riaSupportModel.getGender() == 1?R.drawable.ic_support_female:R.drawable.ic_support_male);
                descriptionTv.setText("Your Web Consultant");
                break;

            case DEFAULT:

                callActionBtn.setText("CALL NOW");
                requestActionBtn.setVisibility(View.GONE);
                personImage.setImageResource(R.drawable.ria_circle_image);
                descriptionTv.setText("Customer Support");
                break;

            default:

                break;
        }

        nameTv.setText(riaSupportModel.getName());
        numberTv.setText(riaSupportModel.getPhoneNumber());
        emailTv.setText(riaSupportModel.getEmail());

        if(!HelpAndSupportFragment.MemberType.valueOf(riaSupportModel.getType()).equals(HelpAndSupportFragment.MemberType.CHC))
        {
            if(isPhoneEnabled)
            {
                numberTv.setVisibility(View.VISIBLE);
                labelNumber.setVisibility(View.VISIBLE);

                callActionBtn.setVisibility(View.VISIBLE);
                requestActionBtn.setVisibility(View.VISIBLE);
            }

            else
            {
                numberTv.setVisibility(View.GONE);
                labelNumber.setVisibility(View.GONE);

                callActionBtn.setVisibility(View.GONE);
                requestActionBtn.setVisibility(View.GONE);
            }

            if(isChatEnabled)
            {
                callActionBtn.setVisibility(View.VISIBLE);
            }

            else
            {
                callActionBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_call_action:

                if (HelpAndSupportFragment.MemberType.valueOf(riaSupportModel.getType()) == HelpAndSupportFragment.MemberType.WEB)
                {
                    ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.chat));
                }

                else
                {
                    Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                    MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                }

                break;

            case R.id.tv_person_email:

                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_EMAIL,null);
                Methods.sendEmail(mContext,new String[]{riaSupportModel.getEmail()});
                break;

            case R.id.tv_person_number:

                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                break;

            case R.id.btn_request_callback:

                MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CALL,null);
                Methods.makeCall(mContext,riaSupportModel.getPhoneNumber());
                break;
        }
    }
}