package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.WildFireApis;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 29-01-2018.
 */

public class WildFireAdsActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    private String wildfireId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_wildfire);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            setTitle("Inorganic");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        UserSessionManager manager = new UserSessionManager(this,this);
        if (Methods.isOnline(this)){
            getWildFireData(manager.getFPID());
        }else{
            showDefaultPage();
        }
    }

    private void showDefaultPage()
    {
        findViewById(R.id.empty_screen).setVisibility(View.VISIBLE);
        makeLinkClickable(findViewById(R.id.message_text3));
    }

    protected void makeLinkClickable(TextView view)
    {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder("Visit ");
        spanTxt.append(Methods.fromHtml("<u>" + getString(R.string.link_marketplace)+"</u>"));

        /*spanTxt.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget)
            {
                //Intent i = new Intent(WildFireAdsActivity.this, Mobile_Site_Activity.class);
                //i.putExtra("WEBSITE_NAME", getString(R.string.setting_faq_url));
                //startActivity(i);
            }
        }, spanTxt.length() - Methods.fromHtml("<b>" + getString(R.string.link_marketplace)+"</b>").length(), spanTxt.length(), 0);*/

        spanTxt.append(getString(R.string.from_a_desktop_to_explore_and_install_related_add_ons));

        //spanTxt.append(" from a desktop to explore and install related add-ons to see inorganic traffic/leads");
        //spanTxt.append(Methods.fromHtml(" or contact us at <a href=\"mailto:" + getString(R.string.settings_feedback_link) + "\"><b>" + getString(R.string.settings_feedback_link) + "</b></a> "));
        //spanTxt.append(Methods.fromHtml(" or call our toll-free number <a href=\"tel:" + getString(R.string.contact_us_number) + "\"><b>" + getString(R.string.contact_us_number) + "</b></a>."));

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }


//    private void showDefaultPage() {
//        findViewById(R.id.empty_screen).setVisibility(View.VISIBLE);
//        ImageView image = findViewById(R.id.image1);
//        image.setImageResource(R.drawable.wildfire_gray);
//        TextView mainText = findViewById(R.id.main_text1);
//        mainText.setText("No analytics!");
//        TextView messageText = findViewById(R.id.message_text2);
//        messageText.setText("Your wildfire is not enabled.");
//        TextView actionButton = findViewById(R.id.btn_action);
//        actionButton.setText("Start Wildfire");
//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(WildFireAdsActivity.this,FragmentsFactoryActivity.class);
//                intent.putExtra("fragmentName","WildFireFragment");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//
//    }

    private void getWildFireData(String sourceId){
        showProgress();
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        //WildFireApis apis = Constants.restAdapter.create(WildFireApis.class);
        apis.getWildFireData(sourceId, Constants.clientId, new Callback<WildFireDataModel>() {
            @Override
            public void success(WildFireDataModel wildFireDataModel, Response response) {
                if (wildFireDataModel != null && !TextUtils.isEmpty(wildFireDataModel.getId())){
                    wildfireId = wildFireDataModel.getId();
                    getWildFireChannels(wildFireDataModel.getId());
                }else{
                    showDefaultPage();
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showDefaultPage();
            }
        });
    }

    private void getWildFireChannels(final String accountId){
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getWildFireChannels(Constants.clientId, accountId, new Callback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> strings, Response response) {
                if (strings != null &&strings.size()>0){
                    showWildFireCard(strings);
                    // google and facebook
                }else{

                    showDefaultPage();
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showDefaultPage();
            }
        });
    }

    private void showWildFireCard(ArrayList<String> activeChannels) {
        hideProgress();
        RecyclerView channelList = findViewById(R.id.rv_wildfire_channels);
        channelList.setVisibility(View.VISIBLE);
        channelList.setLayoutManager(new LinearLayoutManager(this));
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

    private void showProgress(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.please_wait));
        }else if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
    private void hideProgress(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private class WildFireChannelAdapter extends RecyclerView.Adapter<WildFireChannelAdapter.ChannelViewHolder> {

        ArrayList<String> actives, unActives;
        private Context mContext;
        WildFireChannelAdapter(ArrayList<String> actives, ArrayList<String> unActives){
            this.actives = actives;
            this.unActives = unActives;
            mContext = WildFireAdsActivity.this;
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
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }
                });

            }else{
                //unactive
                String mainText = "";
                switch (unActives.get(position-actives.size())){
                    case "google":
                        mainText = getString(R.string.to_enable_google_wildfire_with_us);
                        holder.nameTv.setText(R.string.google_adwords);
                        holder.channelImage.setImageResource(R.drawable.ic_google_gray);
                        break;
                    case "facebook":
                        mainText = getString(R.string.to_enable_facebook_wildfire_with_us);
                        holder.nameTv.setText(R.string.facebook_ads);
                        holder.channelImage.setImageResource(R.drawable.ic_facebook_logo);
                        break;
                }

                holder.descriptionTv.setText(R.string.inactive);
                holder.arrowImage.setVisibility(View.GONE);
                holder.parentView.setAlpha(.8f);
                final String finalMainText = mainText;
                holder.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showDialog(finalMainText);
                    }
                });
            }

        }

        private void showDialog(String mainText){
            final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                    .customView(R.layout.dialog_help_support,false)
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
                    Intent intent = new Intent(WildFireAdsActivity.this,FragmentsFactoryActivity.class);
                    intent.putExtra("fragmentName","HelpAndSupportFragment");
                    startActivity(intent);
                    dialog.dismiss();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

                arrowImage = itemView.findViewById(R.id.img_channel_arrow);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
