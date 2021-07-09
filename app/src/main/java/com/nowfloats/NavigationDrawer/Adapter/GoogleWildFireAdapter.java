package com.nowfloats.NavigationDrawer.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.WildFireAdAnalyticsActivity;
import com.nowfloats.NavigationDrawer.model.FacebookWildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 01-12-2017.
 */

public class GoogleWildFireAdapter extends RecyclerView.Adapter<GoogleWildFireAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<WildFireKeyStatsModel> modelList, originalList;
    private ArrayList<FacebookWildFireDataModel> facebookModelList, facebookOriginalList;
    private ChannelType dataChannelType;
    public enum ChannelType {
        GOOGLE,FACEBOOK;
    }

    public GoogleWildFireAdapter(Context context){
        mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(MyViewHolder.layout_id, parent, false);
        return new MyViewHolder(v);

    }

    public void setGoogleModelList(ArrayList<WildFireKeyStatsModel> modelList){
        this.modelList = modelList;
        originalList = modelList;
        dataChannelType = ChannelType.GOOGLE;
    }
    public void setFacebookModelList(ArrayList<FacebookWildFireDataModel> modelList){
        facebookModelList = modelList;
        facebookOriginalList = modelList;
        dataChannelType = ChannelType.FACEBOOK;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (dataChannelType){
            case GOOGLE:
                int padding1 = Methods.dpToPx(10,mContext);
                holder.parentView.setPadding(padding1,padding1,padding1,padding1);
                holder.keywordTv.setText(modelList.get(position).getKeyword().replace("+",""));
                if (TextUtils.isDigitsOnly(modelList.get(position).getClicks())) {
                    holder.clickTv.setText(NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(modelList.get(position).getClicks())));
                }else{
                    holder.clickTv.setText(modelList.get(position).getClicks());
                }
                holder.statusTv.setText(modelList.get(position).getKeywordState());
                if (modelList.get(position).getKeywordState().equalsIgnoreCase("enabled")){
                    holder.statusTv.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                }else{
                    holder.statusTv.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }
                break;

            case FACEBOOK:
                int padding2 = Methods.dpToPx(15,mContext);
                holder.parentView.setPadding(padding2,padding2,padding2,padding2);
                holder.keywordTv.setText(facebookModelList.get(position).getAdName());
                holder.clickTv.setText(NumberFormat.getIntegerInstance(Locale.US).format(Long.valueOf(facebookModelList.get(position).getClicks())));
                holder.statusTv.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        switch (dataChannelType){
            case GOOGLE:
                return modelList.size();
            case FACEBOOK:
                return facebookModelList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return dataChannelType.ordinal();
    }

    @Override
    public Filter getFilter() {
        switch (dataChannelType){
            case GOOGLE:
                return getGoogleListFilter();
            case FACEBOOK:
                return getFacebookListFilter();
        }
        return null;
    }
    private Filter getFacebookListFilter(){
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                ArrayList<FacebookWildFireDataModel> filteredList;
                if (charString.isEmpty()) {

                    filteredList = facebookOriginalList;
                } else {
                    filteredList =new ArrayList<>();
                    for (FacebookWildFireDataModel model : facebookOriginalList) {

                        if (model.getAdName().toLowerCase().contains(charString)) {

                            filteredList.add(model);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                facebookModelList = (ArrayList<FacebookWildFireDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    private Filter getGoogleListFilter(){
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                ArrayList<WildFireKeyStatsModel> filteredList;
                if (charString.isEmpty()) {

                    filteredList = originalList;
                } else {
                    filteredList =new ArrayList<>();
                    for (WildFireKeyStatsModel model : originalList) {

                        if (model.getKeyword().toLowerCase().contains(charString)) {

                            filteredList.add(model);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                modelList = (ArrayList<WildFireKeyStatsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        final static int layout_id = R.layout.adapter_wildfire_item;
        ImageView arrowImage;
        TextView keywordTv,clickTv,statusTv;
        View parentView;
        MyViewHolder(View itemView) {
            super(itemView);

            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            keywordTv = (TextView) itemView.findViewById(R.id.tv_keyword);
            clickTv = (TextView) itemView.findViewById(R.id.tv_clicks);
            statusTv = (TextView) itemView.findViewById(R.id.tv_status);
            parentView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WildFireAdAnalyticsActivity.class);
                    intent.putExtra(WildFireAdAnalyticsActivity.TYPE,dataChannelType.name());
                    intent.putExtra(WildFireAdAnalyticsActivity.VALUE,
                            new Gson().toJson(dataChannelType == ChannelType.GOOGLE?modelList.get(getAdapterPosition())
                                    :facebookModelList.get(getAdapterPosition())));
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
