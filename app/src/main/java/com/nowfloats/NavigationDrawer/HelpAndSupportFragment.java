package com.nowfloats.NavigationDrawer;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.RiaNetworkInterface;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.HelpAndSupportFragment.MemberType.CHC;
import static com.nowfloats.NavigationDrawer.HelpAndSupportFragment.MemberType.WEB;


public class HelpAndSupportFragment extends Fragment implements View.OnClickListener {

    AppCompatTextView tvConsultantName,tvConsultantNumber, tvEmail, tvNextMember,tvFaq,
            tvTextRia, tvConsultantDes;
    ImageView ivHelpAvatar;
    ProgressDialog pd;
    UserSessionManager sessionManager;
    List<RiaSupportModel> mRiaSupportModelList;
    private Context mContext;
    private MemberType nextMember = CHC;
    enum MemberType{
       CHC,WEB;
    }

    private static final String CHAT_INTENT_URI = "com.biz2.nowfloats://com.riachat.start";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_help_and_support, container, false);
        tvConsultantName =  view.findViewById(R.id.tv_consultant_name);
        tvConsultantNumber = view.findViewById(R.id.tv_contact_number);
        tvConsultantDes = view.findViewById(R.id.tv_member_description);
        tvEmail =  view.findViewById(R.id.tv_consultant_email);
        tvTextRia =  view.findViewById(R.id.tv_ria_text);
        tvFaq =  view.findViewById(R.id.tv_faq);
        tvNextMember =  view.findViewById(R.id.tv_next_member);
        ivHelpAvatar = (ImageView) view.findViewById(R.id.iv_help_avatar);
        tvNextMember.setOnClickListener(this);
        tvFaq.setOnClickListener(this);
        sessionManager = new UserSessionManager(getActivity().getApplicationContext(), getActivity());
        tvConsultantNumber.setPaintFlags(tvConsultantNumber.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tvConsultantNumber.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext,R.drawable.ic_call),null, null, null );
        tvEmail.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext,R.drawable.ic_email),null, null, null );
        final RiaNetworkInterface riaNetworkInterface = Constants.riaRestAdapter.create(RiaNetworkInterface.class);
        Map<String, String> param = new HashMap<>();
        param.put("clientId", Constants.clientId);
        param.put("fpTag", sessionManager.getFpTag());
        riaNetworkInterface.getAllMemberForFp(param, new Callback<List<RiaSupportModel>>() {
            @Override
            public void success(List<RiaSupportModel> list, Response response) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                mRiaSupportModelList = list;
                if (list == null){
                    tvNextMember.setVisibility(View.GONE);
                }else if(list.size() == 1){
                    tvNextMember.setVisibility(View.GONE);
                    setMember(list.get(0), WEB);
                }else if(list.size() > 1) {
                    for (RiaSupportModel riaSupportModel:list) {
                         if (!TextUtils.isEmpty(riaSupportModel.getType()) && MemberType.valueOf(riaSupportModel.getType()) == CHC) {
                             setMember(riaSupportModel, CHC);
                             break;
                        }
                    }

                }
//                    finish();
//                    Intent call = new Intent(Intent.ACTION_DIAL);
//                    String callString = "tel:" + getString(R.string.contact_us_number);
//                    call.setData(Uri.parse(callString));
//                    startActivity(call);

            }

            @Override
            public void failure(RetrofitError error) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                tvNextMember.setVisibility(View.GONE);
                //tvTextRia.setVisibility(View.GONE);
                Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong));

            }
        });

        tvConsultantNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("value",nextMember == CHC?"WEB":"CHC");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MixPanelController.track("CallToConsultant",json);
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + tvConsultantNumber.getText().toString()));
                startActivity(i);
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("value",nextMember == CHC?"WEB":"CHC");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MixPanelController.track("EmailToConsultant",json);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{tvEmail.getText().toString()});
                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
            }
        });
//
//        CharSequence charSequence = Methods.fromHtml("If your query is still unanswered, please contact us at <a href=\"mailto:" + getString(R.string.settings_feedback_link) + "\">" + getString(R.string.settings_feedback_link) + "</a> " +
//                "or call at <a href=\"tel:"+ getString(R.string.contact_us_number)+"\">"+getString(R.string.contact_us_number)+"</a>."
//                /*"or <a href=\"" + CHAT_INTENT_URI + "\"><u>CHAT</u></a>."*/);

//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
//        makeLinkClickable(spannableStringBuilder, charSequence);
//        tvTextRia.setText(spannableStringBuilder);
        //tvTextRia.setMovementMethod(LinkMovementMethod.getInstance());

        /*tvTextFaq2.setText(Methods.fromHtml("Product related queries, please refer to our <a href=\"" + getString(R.string.faqs_url) + "\">FAQs</a>"));
        tvTextFaq2.setMovementMethod(LinkMovementMethod.getInstance());*/

        pd = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait));

        return view;
    }

//    protected void makeLinkClickable(SpannableStringBuilder sp, CharSequence charSequence) {
//
//        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);
//
//        for (final URLSpan urlSpan : spans) {
//
//            if (urlSpan.getURL().equalsIgnoreCase(CHAT_INTENT_URI)) {
//
//                ClickableSpan clickableSpan = new ClickableSpan() {
//                    public void onClick(View view) {
//                        Hotline.showConversations(getActivity());
//                    }
//                };
//                sp.setSpan(clickableSpan, sp.getSpanStart(urlSpan),
//                        sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            }
//
//        }
//
//    }
    @Override
    public void onClick(View view){
            switch (view.getId()){
                case R.id.tv_next_member:
                    for (RiaSupportModel model:mRiaSupportModelList){
                        if (TextUtils.isEmpty(model.getType()) && nextMember == WEB){
                            setMember(model,nextMember);
                            break;
                        }
                        else if (!TextUtils.isEmpty(model.getType()) && nextMember == MemberType.valueOf(model.getType())){
                            setMember(model,nextMember);
                            break;
                        }
                    }

                    break;
                case R.id.tv_faq:
                    Intent i = new Intent(getActivity(), Mobile_Site_Activity.class);
                    i.putExtra("WEBSITE_NAME",getString(R.string.faqs_url));
                    startActivity(i);
                    break;
            }
    }

    private void setMember(final RiaSupportModel riaSupportModel, MemberType memberType){
        switch (memberType){
            case CHC:
                nextMember = WEB;
                tvNextMember.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext,R.drawable.right_arrow), null );
                tvNextMember.setText("Instant Web Support");
                tvConsultantDes.setText("Your Personal Account Manager");
                break;
            case WEB:
                nextMember = CHC;
                tvNextMember.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext,R.drawable.left_arrow), null, null, null );
                tvNextMember.setText("Personal Account Manager");
                tvConsultantDes.setText("Instant Web Support");
                break;
            default:
                break;
        }

        // check if next member exist add arrow if not hide it
        if (riaSupportModel.getGender() == 1) {
            tvConsultantName.setText("Ms. " + riaSupportModel.getName());
            if(getActivity()!=null)
                ivHelpAvatar.setImageDrawable(ContextCompat.getDrawable(getActivity(), memberType == WEB?R.drawable.help_web_support:R.drawable.help_female_avatar));
        } else {
            tvConsultantName.setText("Mr. " + riaSupportModel.getName());
            if(getActivity()!=null)
                ivHelpAvatar.setImageDrawable(ContextCompat.getDrawable(getActivity(), memberType == CHC?R.drawable.help_product_support:R.drawable.help_male_avatar));
        }
        tvConsultantNumber.setText(riaSupportModel.getPhoneNumber());
        tvEmail.setText(riaSupportModel.getEmail());
    }
    @Override
    public void onResume() {
        super.onResume();
        if (HomeActivity.headerText != null)
            HomeActivity.headerText.setText("Help And Support");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
