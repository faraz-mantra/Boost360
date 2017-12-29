package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.Adapter.TextExpandableAdapter;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 01-12-2017.
 */

public class DictateFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wildfire_dictate,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()){
            return;
        }
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        showDefaultPage(view);
    }

    private void showDefaultPage(View view){
        TextView wildfireDefinitionTv = view.findViewById(R.id.wildfire_definition);
        TextView titleTv = view.findViewById(R.id.title_tv);
        view.findViewById(R.id.llayout_know_more).setVisibility(View.INVISIBLE);
        ImageView image1 = view.findViewById(R.id.image1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image1.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.primaryColor)));
        }
        image1.setImageResource(R.drawable.dictate_gray);
        //image1.setColorFilter(new PorterDuffColorFilter(R.color.primaryColor, PorterDuff.Mode.ADD));
        titleTv.setText("Why choose\nDictate plan?");
       /* SpannableString ss =new SpannableString(Methods.fromHtml(getString(R.string.dictate_definition)));
        Drawable dIcon = getResources().getDrawable(R.drawable.wild_fire_expire);
        int leftMargin = dIcon.getIntrinsicWidth() + 10;


        ss.setSpan(new Methods.MyLeadingMarginSpan2(3,leftMargin), 0, ss.length(), 0);*/

        wildfireDefinitionTv.setText(Methods.fromHtml(getString(R.string.dictate_definition)));
        LinearLayout dictateLayout = view.findViewById(R.id.llayout_wildfire);
        TextView dictateTv = dictateLayout.findViewById(R.id.tv_wildfire);
        dictateTv.setText("Start Dictate");
        dictateLayout.setOnClickListener(this);
        view.findViewById(R.id.llayout_know_more).setOnClickListener(this);
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<String> parentList =new ArrayList<>(Arrays.asList( mContext.getResources().getStringArray(R.array.wildfire_parents)));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.dictate_parent_0))));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.dictate_parent_1))));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.dictate_parent_2))));

        ExpandableListView expandableListView = view.findViewById(R.id.info_exlv);
        expandableListView.expandGroup(0);
        expandableListView.setAdapter(new TextExpandableAdapter(mContext,childList,parentList));
    }
//    private void showProgress(){
//        if (!progressDialog.isShowing()){
//            progressDialog.show();
//        }
//    }
//    private void hideProgress(){
//        if (progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null){
            headerText.setText("Dictate");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llayout_wildfire:
                startActivity(new Intent(mContext, NewPricingPlansActivity.class));
                break;
            case R.id.llayout_know_more:

                break;
        }
    }

    /*private void sendEmailForDictate(){
        showProgress();
        MixPanelController.track(MixPanelController.REQUEST_FOR_DICTATE_PLAN,null);
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        ArrayList<String> emailsList = new ArrayList<String>(2);
        emailsList.add("pranav.venuturumilli@nowfloats.com");
        emailsList.add("wildfire.team@nowfloats.com");
        MailModel model = new MailModel(Constants.clientId,"Important: Dictate meeting is requested by"+manager.getFpTag()
        , TextUtils.htmlEncode("Hi, <br>The client with FP Tag <b>\" "+manager.getFpTag()+" \"</b> has requested a meeting to understand the Dictate plan. Please take it up on priority."),
                emailsList);
        StoreInterface anInterface = Constants.restAdapter.create(StoreInterface.class);
        anInterface.mail(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                hideProgress();
                if (response.getStatus() == 200){
                    Methods.materialDialog(getActivity(),"Request For Dictate Plan","your meeting request has been sent successfully.");
                }else{
                    Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong_try_again));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(getActivity(),"Server error");
            }
        });
    }*/
}
