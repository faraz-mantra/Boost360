package com.nowfloats.NavigationDrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Admin on 11-12-2017.
 */

public class WildFireAnalyticsActivity extends AppCompatActivity {

    public final static String TYPE="wildfire_type",VALUE = "value";
    public enum WildFireType{
        GOOGLE,FACEBOOK;
    }
    int[] googleAdAnalyticsImages = {R.drawable.ic_eye,R.drawable.ic_avg_position,R.drawable.ic_first_page_cost,R.drawable.ic_top_page_cost};
    String[] googleAdAnalyticsTitles = {"Impressions","Avg. Position","First Page CPC","Top Page CPC"};
    private WildFireKeyStatsModel model;
    private String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildfire_analytics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Keyword Analytics");
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        type = getIntent().getStringExtra(TYPE);
        String value = getIntent().getStringExtra(VALUE);

        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(value)){

            switch ( WildFireType.valueOf(type)){
                case GOOGLE:
                    model = new Gson().fromJson(value,WildFireKeyStatsModel.class);
                    break;
                case FACEBOOK:
                    break;
                default:
                   return;
            }
        }
        if (model == null){
            return;
        }
        TextView cpcTv = findViewById(R.id.tv_cpc);
        TextView clickTv = findViewById(R.id.tv_clicks);
        TextView keywordTv = findViewById(R.id.tv_keyword);
        keywordTv.setText(model.getKeyword().replace("+",""));
        if (TextUtils.isDigitsOnly(model.getClicks())) {
            clickTv.setText(NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(model.getClicks())));
        }else{
            clickTv.setText(model.getClicks());
        }
        cpcTv.setText(String.format("INR %s",NumberFormat.getNumberInstance().format(Double.valueOf(model.getAvgCPC())/1000000)));
        RecyclerView mRecyclerView = findViewById(R.id.recyclerview1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.hasFixedSize();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new Adapter());
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

//        Adapter( String[]titles, int[] images){
//
//        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (WildFireType.valueOf(type)){
                case GOOGLE:
                   v = LayoutInflater.from(WildFireAnalyticsActivity.this).inflate(MyGoogleViewHolder.id,parent,false);
                   return new MyGoogleViewHolder(v);
                case FACEBOOK:
                    v = LayoutInflater.from(WildFireAnalyticsActivity.this).inflate(MyGoogleViewHolder.id,parent,false);
                    return new MyGoogleViewHolder(v);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyGoogleViewHolder){
                MyGoogleViewHolder myGoogleViewHolder  = (MyGoogleViewHolder) holder;
                myGoogleViewHolder.titleTv.setText(googleAdAnalyticsTitles[position]);
                myGoogleViewHolder.image.setImageResource(googleAdAnalyticsImages[position]);
                switch (position){
                    case 0:
                        myGoogleViewHolder.countTv.setText(NumberFormat.getInstance(Locale.US).format(Long.valueOf(model.getImpressions())));
                        break;

                    case 1:
                        myGoogleViewHolder.countTv.setText(model.getAvgPosition());
                        break;

                    case 2:
                        myGoogleViewHolder.countTv.setText(String.format("INR %s",NumberFormat.getNumberInstance().format(Double.valueOf(model.getFirstPageCpc())/1000000)));
                        break;

                    case 3:
                        myGoogleViewHolder.countTv.setText(String.format("INR %s",NumberFormat.getInstance().format(Double.valueOf(model.getTopPageCPC())/1000000)));
                        break;
                    case 4:
                        break;
                }
            }else{

            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    class MyGoogleViewHolder extends RecyclerView.ViewHolder{
        public final static int id = R.layout.adapter_wildfire_google_analytics;
        TextView titleTv,countTv;
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
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAnalyticsActivity.this, R.color.light_gray));
                            break;
                        case MotionEvent.ACTION_UP:
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAnalyticsActivity.this, R.color.gray));
                            v.performClick();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            infoImage.setColorFilter(ContextCompat.getColor(WildFireAnalyticsActivity.this, R.color.gray));
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
                    switch (WildFireType.valueOf(type)){
                        case GOOGLE:
                            showDialog(googleAdAnalyticsImages[getAdapterPosition()],googleAdAnalyticsTitles[getAdapterPosition()],"hello");
                            break;
                        case FACEBOOK:

                            break;
                    }
                }
            });
        }
    }
    private void showDialog(int imgId, String title, String content){
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_wildfire_analytics,false)
                .build();

        View v = dialog.getCustomView();
        if (v ==null) {
            return;
        }
        TextView contentTv = v.findViewById(R.id.tv_content);
        TextView titleTv = v.findViewById(R.id.tv_title);
        ImageView contentImg = v.findViewById(R.id.img_content);
        contentImg.setColorFilter(ContextCompat.getColor(WildFireAnalyticsActivity.this, R.color.light_gray));
        v.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //contentTv.setText(Methods.fromHtml(content));
        titleTv.setText(Methods.fromHtml(title));
        contentImg.setImageResource(imgId);
        dialog.show();
    }
}
