package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.WildFireApis;
import com.nowfloats.NavigationDrawer.Adapter.TextExpandableAdapter;
import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.Store.Model.MailModel;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 20-12-2017.
 */

public class WildFireFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private ProgressDialog progressDialog;
    private String wildfireId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wildfire,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded())  return;
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        if (Methods.isOnline(getActivity())){
            getWildFireData(view,manager.getFPID());
        }else{
            showDefaultPage(view);
        }
    }
    private void getWildFireChannels(final View view, final String accountId){
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getWildFireChannels(Constants.clientId, accountId, new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> strings, Response response) {
                if (strings != null &&strings.size()>0){
                    showWildFireCard(view,strings);
                  // google and facebook
                }else{

                    showDefaultPage(view);
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showDefaultPage(view);
            }
        });
    }

    private void showWildFireCard(View view,ArrayList<String> activeChannels) {
        hideProgress();
        RecyclerView channelList = view.findViewById(R.id.rv_channels);
        channelList.setVisibility(View.VISIBLE);
        channelList.setLayoutManager(new LinearLayoutManager(mContext));
        channelList.setHasFixedSize(true);
        ArrayList<String> notActiveChannels = new ArrayList<>(1);
        if (!activeChannels.contains("google")){
            notActiveChannels.add("google");
        }
        if (!activeChannels.contains("facebook")){
            notActiveChannels.add("facebook");
        }
        channelList.setAdapter(new WildFireChannelAdapter(activeChannels,notActiveChannels));
    }

    private void showDefaultPage(View view){

        ConstraintLayout defaultLayout = view.findViewById(R.id.constraintLayout_wildfire);
        defaultLayout.setVisibility(View.VISIBLE);
        TextView wildfireDefinitionTv = view.findViewById(R.id.wildfire_definition);
        view.findViewById(R.id.llayout_wildfire).setOnClickListener(this);
        view.findViewById(R.id.llayout_know_more).setOnClickListener(this);
        wildfireDefinitionTv.setText(Methods.fromHtml(getString(R.string.wildfire_definition)));
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<String> parentList =new ArrayList<>(Arrays.asList( getResources().getStringArray(R.array.wildfire_parents)));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_0))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_1))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_2))));

        ExpandableListView expandableListView = view.findViewById(R.id.info_exlv);
        expandableListView.setAdapter(new TextExpandableAdapter(mContext,childList,parentList));
        expandableListView.expandGroup(0);
        hideProgress();
    }

    private void getWildFireData(final View view, String sourceId){
        showProgress();
        WildFireApis apis = Constants.restAdapter.create(WildFireApis.class);
        apis.getWildFireData(sourceId, Constants.clientId, new Callback<WildFireDataModel>() {
            @Override
            public void success(WildFireDataModel wildFireDataModel, Response response) {
                if (wildFireDataModel != null && !TextUtils.isEmpty(wildFireDataModel.getId())){
                    wildfireId = wildFireDataModel.getId();
                    getWildFireChannels(view, wildFireDataModel.getId());
                }else{
                    showDefaultPage(view);
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showDefaultPage(view);
            }
        });
    }


    private void showProgress(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
    private void hideProgress(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null){
            headerText.setText("WildFire");
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llayout_wildfire:
                startActivity(new Intent(mContext, NewPricingPlansActivity.class));
                break;
            case R.id.llayout_know_more:
                sendEmailForWildFire();
                break;
        }
    }
    private void sendEmailForWildFire(){
        showProgress();
        MixPanelController.track(MixPanelController.REQUEST_FOR_WILDFIRE_PLAN,null);
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        ArrayList<String> emailsList = new ArrayList<String>(2);
        emailsList.add("pranav.venuturumilli@nowfloats.com");
        emailsList.add("wildfire.team@nowfloats.com");
        MailModel model = new MailModel(Constants.clientId,
               "Hi, <br>The client with FP Tag <b>\" "+manager.getFpTag()+" \"</b> has requested a meeting to understand the WildFire plan. Please take it up on priority.",
                 "Important: WildFire meeting is requested by"+manager.getFpTag(),
                 emailsList);
        StoreInterface anInterface = Constants.restAdapter.create(StoreInterface.class);
        anInterface.mail(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                hideProgress();
                if (response.getStatus() == 200 && !TextUtils.isEmpty(s)){
                    Methods.materialDialog(getActivity(),"Request For WildFire Plan","Your meeting request has been sent successfully.");
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

    }
    private class WildFireChannelAdapter extends RecyclerView.Adapter<WildFireChannelAdapter.ChannelViewHolder> {

        ArrayList<String> actives, unActives;
        WildFireChannelAdapter(ArrayList<String> actives, ArrayList<String> unActives){
            this.actives = actives;
            this.unActives = unActives;
        }
        @Override
        public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(ChannelViewHolder.id,parent,false);
            return new ChannelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChannelViewHolder holder, int position) {
            if (position<actives.size()){
                Intent intent = null;
                switch (actives.get(position)){
                    case "google":
                        intent = new Intent(mContext,GoogleWildFireActivity.class);
                        holder.nameTv.setText("Google Adwords");
                        holder.channelImage.setImageResource(R.drawable.ic_google_colored);
                        break;
                    case "facebook":
                        intent = new Intent(mContext,FacebookWildFireActivity.class);
                        holder.nameTv.setText("Facebook Ads");
                        holder.channelImage.setImageResource(R.drawable.com_facebook_favicon_blue);
                        break;
                    default:
                        return;
                }
                holder.descriptionTv.setText("Active");
                holder.arrowImage.setVisibility(View.VISIBLE);
                final Intent finalIntent = intent;
                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalIntent.putExtra("WILDFIRE_ID",wildfireId);
                        startActivity(finalIntent);

                    }
                });

            }else{
                //unactive
                String mainText = "";
                switch (unActives.get(position-actives.size())){
                    case "google":
                       // mainText = getString(R.string.wildfire_facebook_text);
                        holder.nameTv.setText("Google Adwords");
                        holder.channelImage.setImageResource(R.drawable.ic_google_gray);
                        break;
                    case "facebook":
                        //mainText = getString(R.string.wildfire_google_text);
                        holder.nameTv.setText("Facebook Ads");
                        holder.channelImage.setImageResource(R.drawable.ic_facebook_logo);
                        break;
                }

                holder.descriptionTv.setText("Inactive");
                holder.arrowImage.setVisibility(View.GONE);
                holder.parentView.setAlpha(.8f);
                final String finalMainText = mainText;
                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //showDialog(finalMainText);
                    }
                });
            }

        }

        private void showDialog(String mainText){
            final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                    .customView(R.layout.dialog_help_support,true)
                    .build();
            View view = dialog.getCustomView();
            if (view == null){
               return;
            }
            dialog.show();
            view.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            ((TextView)view.findViewById(R.id.tv_main_content)).setText(mainText);
            view.findViewById(R.id.tv_cta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SidePanelFragment.OnItemClickListener)mContext).onClick(getString(R.string.call));
                }
            });

        }
        @Override
        public int getItemCount() {
            return actives.size()+unActives.size();
        }

        class ChannelViewHolder extends RecyclerView.ViewHolder{
            static final int id = R.layout.layout_wildfire_channel;
            TextView nameTv, descriptionTv;
            ImageView arrowImage, channelImage;
            View parentView;
            public ChannelViewHolder(View itemView) {
                super(itemView);
                parentView = itemView;
                descriptionTv = itemView.findViewById(R.id.tv_channel_leads);
                nameTv = itemView.findViewById(R.id.tv_channel_title);
                channelImage = itemView.findViewById(R.id.img_channel);
//                knowMore = itemView.findViewById(R.id.tv_channel_know_more);
//                knowMore.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // show about page
//                    }
//                });
                arrowImage = itemView.findViewById(R.id.img_channel_arrow);
            }
        }
    }
}
