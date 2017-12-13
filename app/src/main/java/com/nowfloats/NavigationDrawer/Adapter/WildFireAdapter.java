package com.nowfloats.NavigationDrawer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nowfloats.NavigationDrawer.WildFireAnalyticsActivity;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 01-12-2017.
 */

public class WildFireAdapter extends RecyclerView.Adapter<WildFireAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<WildFireKeyStatsModel> modelList, originalList;

    public WildFireAdapter(Context context){
        mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(mContext).inflate(MyViewHolder.layout_id,parent,false);
        return new MyViewHolder(v);
    }

    public void setModelList(ArrayList<WildFireKeyStatsModel> modelList){
        this.modelList = modelList;
        originalList = modelList;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public Filter getFilter() {
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
        View view;
        MyViewHolder(View itemView) {
            super(itemView);

            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            keywordTv = (TextView) itemView.findViewById(R.id.tv_keyword);
            clickTv = (TextView) itemView.findViewById(R.id.tv_clicks);
            statusTv = (TextView) itemView.findViewById(R.id.tv_status);
            view = itemView.findViewById(R.id.divider);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WildFireAnalyticsActivity.class);
                    intent.putExtra(WildFireAnalyticsActivity.TYPE,WildFireAnalyticsActivity.WildFireType.GOOGLE.name());
                    intent.putExtra(WildFireAnalyticsActivity.VALUE,new Gson().toJson(modelList.get(getAdapterPosition())));
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
