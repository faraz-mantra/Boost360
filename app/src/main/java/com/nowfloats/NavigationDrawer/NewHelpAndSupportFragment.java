package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.RiaNetworkInterface;
import com.nowfloats.NavigationDrawer.model.RiaSupportModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.rd.PageIndicatorView;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 28-12-2017.
 */

public class NewHelpAndSupportFragment extends Fragment {
    private Context mContext;
    private List<RiaSupportModel> mRiaSupportModelList;
    private ProgressDialog dialog;
    enum MemberType{
        CHC,WEB,DEFAULT;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_new_help_and_support,container,false);
    }

    private void showProgress(){
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(true);
            dialog.setMessage(getString(R.string.please_wait));
        }
        if (!dialog.isShowing())
            dialog.show();
    }
    private void hideProgress(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;
        mRiaSupportModelList = new ArrayList<>(2);
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        HashMap<String, String> param = new HashMap<>();
        param.put("clientId", Constants.clientId);
        param.put("fpTag", manager.getFpTag());
        getRiaMembers(param,view);

//        CharSequence charSequence = Methods.fromHtml("If your query is still unanswered, please contact us at <a href=\"mailto:" + getString(R.string.settings_feedback_link) + "\">" + getString(R.string.settings_feedback_link) + "</a> " +
//                "or call at <a href=\"tel:"+ getString(R.string.contact_us_number)+"\">"+getString(R.string.contact_us_number)+"</a>."+
//                "or <a href=\"" + CHAT_INTENT_URI + "\"><u>CHAT</u></a>.");
//
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
//        makeLinkClickable(spannableStringBuilder, charSequence);
//        tvTextRia.setText(spannableStringBuilder);
//        tvTextRia.setMovementMethod(LinkMovementMethod.getInstance());

    }
    protected void makeLinkClickable(SpannableStringBuilder sp, CharSequence charSequence) {

//        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);
//
//        for (final URLSpan urlSpan : spans) {
//
//            if (urlSpan.getURL().equalsIgnoreCase(FAQ_INTENT_URI)) {
//
//                ClickableSpan clickableSpan = new ClickableSpan() {
//                    public void onClick(View view) {
//
//                    }
//                };
//                sp.setSpan(clickableSpan, sp.getSpanStart(urlSpan),
//                        sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            }
//
//        }
        //Linkify.add
    }
    private void getRiaMembers(HashMap<String,String> map, final View view){
        showProgress();
        RiaNetworkInterface riaNetworkInterface = Constants.riaRestAdapter.create(RiaNetworkInterface.class);
        riaNetworkInterface.getAllMemberForFp(map, new Callback<List<RiaSupportModel>>() {
            @Override
            public void success(List<RiaSupportModel> list, Response response) {
                if (list == null || list.size() == 0 ||
                        response.getStatus() <200 || response.getStatus()>300 ){
                    addDefaultRiaData();
                }else {
                    for (RiaSupportModel model : list){
                        if (TextUtils.isEmpty(model.getType())){
                            model.setType(MemberType.WEB.toString());
                            mRiaSupportModelList.add(model);
                        }else if( MemberType.CHC.name().equals(model.getType())){
                            mRiaSupportModelList.add(0,model);
                        }
                    }

                }
                setAdapterWithPager(view);
            }

            @Override
            public void failure(RetrofitError error) {
                addDefaultRiaData();
                setAdapterWithPager(view);
                Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong));

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null){
            headerText.setText("Help and Support");
        }
    }

    private void setAdapterWithPager(View view){
        hideProgress();
        ViewPager mPager = view.findViewById(R.id.ps_pager);
        mPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        int padding = Methods.dpToPx(20,mContext);
        mPager.setPadding(padding,padding,padding,padding/3);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mPager.setPageMargin(padding/3);
        mPager.setAdapter(new viewPagerAdapter(getChildFragmentManager()));
        PageIndicatorView pageIndicatorView = view.findViewById(R.id.ps_indicator);
        pageIndicatorView.setCount(mRiaSupportModelList.size());
        pageIndicatorView.setViewPager(mPager);
    }
    private void addDefaultRiaData(){
        RiaSupportModel model = new RiaSupportModel();
        model.setName("Ria");
        model.setGender(1);
        model.setEmail(getString(R.string.settings_feedback_link));
        model.setType(MemberType.DEFAULT.toString());
        model.setPhoneNumber(getString(R.string.contact_us_number));
        mRiaSupportModelList.add(model);
    }
    private class viewPagerAdapter extends FragmentStatePagerAdapter {

        viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putString(HelpAndSupportCardFragment.RIA_MODEL_DATA,new Gson().toJson(mRiaSupportModelList.get(position)));
            return HelpAndSupportCardFragment.getInstance(b);
        }

        @Override
        public int getCount() {
            return mRiaSupportModelList.size();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }
    }

}
