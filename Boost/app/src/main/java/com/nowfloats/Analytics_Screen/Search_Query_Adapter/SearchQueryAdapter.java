package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.API.Get_Search_Queries_Async_Task;
import com.nowfloats.Analytics_Screen.DataMap;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kamal on 17-02-2015.
 */
public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.MyViewHolder>{

    Activity activity;
    DataMap list;
    int size;
    String msg = "mesg",date = "date", time = "time", timef = "timef";
    PorterDuffColorFilter porterDuffColorFilter;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView search_query_header_text;
        TextView search_query_date_text;
        ImageView search_query_image_icon;


        public MyViewHolder(View itemView) {
            super(itemView);
            //  Log.d("CardAdapter_v2","MyViewHolder");
            this.search_query_header_text = (TextView) itemView.findViewById(R.id.search_query_header_text_1);
            this.search_query_date_text = (TextView) itemView.findViewById(R.id.search_query_header_text_2);
            this.search_query_image_icon = (ImageView) itemView.findViewById(R.id.search_query_image);
        }
    }
    public SearchQueryAdapter(Activity activity){
        this.activity = activity;
        list = Constants.StoreUserSearch;

        if(list != null)
            size = list.size();
        if(size == 0)
            initialiseList();
        this.notifyDataSetChanged();

    }

    public void initialiseList() {
        Get_Search_Queries_Async_Task gsf = new Get_Search_Queries_Async_Task(activity,this);
        gsf.execute();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.search_query_adapter, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TextView query = holder.search_query_header_text;
        TextView date1 = holder.search_query_date_text;
        ImageView imageView = holder.search_query_image_icon;

        query.setTypeface(null, Typeface.ITALIC);


        porterDuffColorFilter = new PorterDuffColorFilter(activity.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);


//        Typeface myCustomFont = Typeface.createFromAsset(activity.getAssets(),"Roboto-Light.ttf");
//        textViewName.setTypeface(myCustomFont);
//        textViewEmail.setTypeface(myCustomFont);

        // textViewName.setTextSize(24);
        // textViewEmail.setTextSize(18);

//        textViewName.setTextColor(appContext.getResources().getColor(R.color.home_view_card_main_text_color));
//        textViewEmail.setTextColor(appContext.getResources().getColor(R.color.home_view_card_date_text_color));

         Log.d("Search Query Adapter ", "Constants. : " + list.get(position));

        JSONObject obj = (JSONObject) list.get(position);
        try
        {
            msg =  obj.getString("keyword");
            date = obj.getString("createdOn");
            time = obj.getString("key");
            String sDate = time.replace("/Date(", "").replace(")/", "");
            String[] splitDate = sDate.split("\\+");
            Long epochTime = Long.parseLong(splitDate[0]);
            Date date = new Date(epochTime);
            String strDateFormat = "hh:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            timef = sdf.format(epochTime);

        }
        catch(Exception ex)
        {
            ex.getMessage();
        }


        holder.search_query_header_text.setText(msg);
        holder.search_query_date_text.setText(date);
        holder.search_query_image_icon.setColorFilter(porterDuffColorFilter);
        //holder.search_query_image_icon.setImageBitmap(timef);




    }

    @Override
    public int getItemCount() {
        return Constants.StoreUserSearch.size();
    }





    public void setList(DataMap list){
        try {
            this.list = list;
            if(list != null)
                size = list.size();
            this.notifyDataSetChanged();
            //this.notifyDataSetInvalidated();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}