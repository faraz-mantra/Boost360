package com.nowfloats.NavigationDrawer;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.Adapter.GoogleWildFireAdapter;
import com.nowfloats.NavigationDrawer.model.FacebookWildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.Locale;

import static com.nowfloats.NavigationDrawer.Adapter.GoogleWildFireAdapter.ChannelType.GOOGLE;
import static com.nowfloats.NavigationDrawer.WildFireAdAnalyticsActivity.MyGoogleViewHolder.id1;

/**
 * Created by Admin on 11-12-2017.
 */

public class WildFireAdAnalyticsActivity extends AppCompatActivity {

    public final static String TYPE = "wildfire_type", VALUE = "value";
    private final int[] googleAdAnalyticsImages = {R.drawable.ic_eye, R.drawable.ic_avg_position, R.drawable.ic_first_page_cost, R.drawable.ic_top_page_cost};
    private final int[] googleAdAnalyticsImagesGray = {R.drawable.ic_eye_gray, R.drawable.ic_avg_position_gray, R.drawable.ic_first_page_cost_gray, R.drawable.ic_top_page_cost_gray};
    private final int[] facebookAdAnalyticsImages = {R.drawable.ic_eye, R.drawable.ic_first_page_cost, R.drawable.ic_wildfire_reach};
    private final int[] facebookAdAnalyticsImagesGray = {R.drawable.ic_eye_gray, R.drawable.ic_first_page_cost_gray, R.drawable.ic_wildfire_reach_gray};
    private final String[] googleAdAnalyticsTitles = {"Impressions", "Avg. Position", "First Page CPC", "Top Page CPC"};
    private final String[] facebookAdAnalyticsTitles = {"Impressions", "CTR", "Reach"};
    private final String[] facebookContents = {"Number of times your ad was shown", "Click through rate", "Number of people who saw your ad"};
    private final String[] googleContents = {"Number of times your ad was shown", "Average position of ad on Google", "First page click per cost", "Top page click per cost"};
    private GoogleWildFireAdapter.ChannelType channelType;
    private WildFireKeyStatsModel googleModel;
    private FacebookWildFireDataModel facebookModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildfire_analytics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.text1);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        title.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        title.setTypeface(tf);
        ImageView channelImage = toolbar.findViewById(R.id.image1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String type = getIntent().getStringExtra(TYPE);
        if (TextUtils.isEmpty(type)) {
            finish();
        }
        channelType = GoogleWildFireAdapter.ChannelType.valueOf(type);
        String value = getIntent().getStringExtra(VALUE);
        TextView cpcTv = findViewById(R.id.tv_cpc);
        TextView clickTv = findViewById(R.id.tv_clicks);
        TextView keywordTv = findViewById(R.id.tv_keyword);
        AppCompatTextView adCostTv = findViewById(R.id.tv_ad_cost);
        adCostTv.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(WildFireAdAnalyticsActivity.this, R.drawable.ic_stack_cost), null, null, null);
        switch (channelType) {
            case GOOGLE:
                channelImage.setImageResource(R.drawable.ic_google_glass_logo);
                title.setText(R.string.keyboard_analytics);
                googleModel = new Gson().fromJson(value, WildFireKeyStatsModel.class);
                if (TextUtils.isDigitsOnly(googleModel.getClicks())) {
                    clickTv.setText(NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(googleModel.getClicks())));
                } else {
                    clickTv.setText(googleModel.getClicks());
                }
                keywordTv.setText(googleModel.getKeyword().replace("+", ""));
                cpcTv.setText(String.format(Locale.ENGLISH, "INR %.2f", Double.valueOf(googleModel.getAvgCPC()) / 1000000));

                break;
            case FACEBOOK:
                facebookModel = new Gson().fromJson(value, FacebookWildFireDataModel.class);
                channelImage.setImageResource(R.drawable.ic_facebook_app_logo);
                title.setText("Ad Analytics");
                if (TextUtils.isDigitsOnly(facebookModel.getClicks())) {
                    clickTv.setText(NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(facebookModel.getClicks())));
                } else {
                    clickTv.setText(facebookModel.getClicks());
                }
                keywordTv.setText(facebookModel.getAdName());
                cpcTv.setText(String.format(Locale.ENGLISH, "INR %.2f", Double.valueOf(facebookModel.getCpc())));

                TextView previewTv = findViewById(R.id.tv_preview);
                previewTv.setVisibility(View.VISIBLE);
                previewTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // show image preview businessapp
                        Intent i = new Intent(WildFireAdAnalyticsActivity.this, Mobile_Load_Html_Activity.class);
                        i.putExtra("WEBSITE_DATA", facebookModel.getPreviewAd());
                        startActivity(i);
                    }
                });
                break;
            default:
                return;
        }

        adCostTv.setText(Methods.fromHtml(String.format(Locale.ENGLISH, getString(R.string.wildfire_ad_cost_text)
                , channelType == GOOGLE ? Double.valueOf(googleModel.getCost()) / 1000000 : Double.valueOf(facebookModel.getCpm()))));
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.hasFixedSize();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new Adapter());
    }

    private void showDialog(int imgId, String title, String content) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_wildfire_analytics, false)
                .build();

        View v = dialog.getCustomView();
        if (v == null) {
            return;
        }
        TextView contentTv = v.findViewById(R.id.tv_content);
        TextView titleTv = v.findViewById(R.id.tv_title);
        ImageView contentImg = v.findViewById(R.id.img_content);
        v.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        contentTv.setText(Methods.fromHtml(content));
        titleTv.setText(Methods.fromHtml(title));
        contentImg.setImageResource(imgId);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case id1:
                    v = LayoutInflater.from(WildFireAdAnalyticsActivity.this).inflate(id1, parent, false);
                    return new MyGoogleViewHolder(v);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyGoogleViewHolder) {
                MyGoogleViewHolder myGoogleViewHolder = (MyGoogleViewHolder) holder;
                switch (channelType) {
                    case GOOGLE:
                        myGoogleViewHolder.titleTv.setText(googleAdAnalyticsTitles[position]);
                        myGoogleViewHolder.image.setImageResource(googleAdAnalyticsImages[position]);
                        switch (position) {
                            case 0:
                                myGoogleViewHolder.countTv.setText(NumberFormat.getInstance(Locale.US).format(Long.valueOf(googleModel.getImpressions())));
                                break;
                            case 1:
                                myGoogleViewHolder.countTv.setText(googleModel.getAvgPosition());
                                break;
                            case 2:
                                myGoogleViewHolder.countTv.setText(String.format(Locale.ENGLISH, "INR %.2f", Double.valueOf(googleModel.getFirstPageCpc()) / 1000000));
                                break;
                            case 3:
                                myGoogleViewHolder.countTv.setText(String.format(Locale.ENGLISH, "INR %.2f", Double.valueOf(googleModel.getTopPageCPC()) / 1000000));
                                break;
                            case 4:
                                break;
                        }
                        break;
                    case FACEBOOK:
                        myGoogleViewHolder.titleTv.setText(facebookAdAnalyticsTitles[position]);
                        myGoogleViewHolder.image.setImageResource(facebookAdAnalyticsImages[position]);
                        switch (position) {
                            case 0:
                                myGoogleViewHolder.countTv.setText(NumberFormat.getInstance(Locale.US).format(Long.valueOf(facebookModel.getImpressions())));
                                break;
                            case 1:
                                myGoogleViewHolder.countTv.setText(String.format(Locale.ENGLISH, "%.2f %s", Double.valueOf(facebookModel.getCtr()), "%"));
                                break;
                            case 2:
                                myGoogleViewHolder.countTv.setText(String.format("%s", NumberFormat.getNumberInstance().format(Double.valueOf(facebookModel.getReach()))));
                                break;
                            case 3:
                                break;
                        }
                        break;
                }

            }
        }

        @Override
        public int getItemCount() {
            return channelType == GOOGLE ?
                    googleAdAnalyticsTitles.length : facebookAdAnalyticsTitles.length;
        }

        @Override
        public int getItemViewType(int position) {
            return id1;
        }
    }

    class MyGoogleViewHolder extends RecyclerView.ViewHolder {
        public final static int id1 = R.layout.adapter_wildfire_google_analytics;
        TextView titleTv, countTv;
        ImageView image, infoImage;

        public MyGoogleViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image1);
            countTv = itemView.findViewById(R.id.tv_count);
            titleTv = itemView.findViewById(R.id.tv_title);
            infoImage = itemView.findViewById(R.id.img_info);
            infoImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAdAnalyticsActivity.this, R.color.light_gray));
                            break;
                        case MotionEvent.ACTION_UP:
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAdAnalyticsActivity.this, R.color.gray));
                            v.performClick();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAdAnalyticsActivity.this, R.color.gray));
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            infoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (channelType) {
                        case GOOGLE:
                            showDialog(googleAdAnalyticsImagesGray[getAdapterPosition()], googleAdAnalyticsTitles[getAdapterPosition()], googleContents[getAdapterPosition()]);
                            break;
                        case FACEBOOK:
                            showDialog(facebookAdAnalyticsImagesGray[getAdapterPosition()], facebookAdAnalyticsTitles[getAdapterPosition()], facebookContents[getAdapterPosition()]);
                            break;
                    }
                }
            });
        }
    }
}
