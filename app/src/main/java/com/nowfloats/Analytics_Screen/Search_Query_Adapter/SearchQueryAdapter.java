package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SearchAnalytics;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.MyViewHolder> {

    private ArrayList<SearchAnalytics> queryList;
    private Activity activity;


    public SearchQueryAdapter(Activity activity, ArrayList<SearchAnalytics> list) {
        this.activity = activity;
        queryList = list;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_query_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(activity.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        SearchAnalytics response = queryList.get(position);

        /*try
        {
            String sDate = queryList.get(position).getCreatedOn().replace("/Date(", "").replace("+0530)/", "");;
            holder.search_query_date_text.setText(Methods.getFormattedDate(sDate));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        try {
            String dateTime = Methods.getISO8601FormattedDate(response.getDate());
            holder.search_query_date_text.setText(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.search_query_header_text.setText(response.getKeyword());
        //holder.search_query_date_text.setTypeface(null, Typeface.ITALIC);
        holder.search_query_image_icon.setColorFilter(porterDuffColorFilter);
    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView search_query_header_text;
        TextView search_query_date_text;
        ImageView search_query_image_icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.search_query_header_text = itemView.findViewById(R.id.search_query_header_text_1);
            this.search_query_date_text = itemView.findViewById(R.id.search_query_header_text_2);
            this.search_query_image_icon = itemView.findViewById(R.id.search_query_image);
        }
    }
}