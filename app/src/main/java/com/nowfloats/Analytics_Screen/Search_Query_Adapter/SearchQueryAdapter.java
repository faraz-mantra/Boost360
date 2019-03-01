package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.AnalyticsResponse;
import com.nowfloats.Analytics_Screen.model.SearchQueryModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.MyViewHolder>{

    private ArrayList<AnalyticsResponse> queryList;
    Activity activity;
    PorterDuffColorFilter porterDuffColorFilter;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView search_query_header_text;
        TextView search_query_date_text;
        ImageView search_query_image_icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.search_query_header_text = (TextView) itemView.findViewById(R.id.search_query_header_text_1);
            this.search_query_date_text = (TextView) itemView.findViewById(R.id.search_query_header_text_2);
            this.search_query_image_icon = (ImageView) itemView.findViewById(R.id.search_query_image);
        }
    }
    public SearchQueryAdapter(Activity activity, ArrayList<AnalyticsResponse> list){
        this.activity = activity;
        queryList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.search_query_adapter, parent, false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        porterDuffColorFilter = new PorterDuffColorFilter(activity.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        /*try {
            String sDate = queryList.get(position).getCreatedOn().replace("/Date(", "").replace("+0530)/", "");;
            holder.search_query_date_text.setText(Methods.getFormattedDate(sDate));
        }catch (Exception e){
            e.printStackTrace();
        }*/
        holder.search_query_header_text.setText(queryList.get(position).getKeyword());
        holder.search_query_date_text.setTypeface(null, Typeface.ITALIC);
        holder.search_query_image_icon.setColorFilter(porterDuffColorFilter);
    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }

}