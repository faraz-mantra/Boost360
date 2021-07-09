package com.nowfloats.NavigationDrawer.SiteMeter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru on 27-04-2015.
 */
public class SiteMeterAdapter extends RecyclerView.Adapter<SiteMeterAdapter.ViewHolder>{
    Activity appContext;
    View displayView;
    ArrayList<SiteMeterModel> siteData;
    private LayoutInflater mInflater;
    public Site_Meter_Fragment fragment;
    public SiteMeterAdapter(Activity appContext, Site_Meter_Fragment fragment, ArrayList<SiteMeterModel> siteData) {
        this.appContext = appContext;
        this.siteData = siteData;
        this.fragment = fragment;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleText,descText,value;
        public LinearLayout sitemeter_layout;
        public ViewHolder(View v) {
            super(v);
            titleText = (TextView)itemView.findViewById(R.id.titleText);
            descText = (TextView)itemView.findViewById(R.id.descText);
            value = (TextView)itemView.findViewById(R.id.valueText);
            sitemeter_layout = (LinearLayout)itemView.findViewById(R.id.sitemeter_layout);
        }
    }

    @Override
    public SiteMeterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.sitemeter_design_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        holder.sitemeter_layout.setTag(position+"");
        holder.titleText.setText(siteData.get(position).Title);
        holder.descText.setText(siteData.get(position).Desc);
        holder.value.setText(siteData.get(position).Percentage);

        if(siteData.get(position).status){
            holder.titleText.setTextColor(appContext.getResources().getColor(R.color.percentage_text_color));
            holder.descText.setTextColor(appContext.getResources().getColor(R.color.inactive_text));
            holder.value.setTextColor(appContext.getResources().getColor(R.color.inactive_text));
        }else {
            holder.value.setTextColor(appContext.getResources().getColor(R.color.primaryColor));
            holder.titleText.setTextColor(appContext.getResources().getColor(R.color.dark_black_color));
            holder.descText.setTextColor(appContext.getResources().getColor(R.color.light_black_color));
        }
        holder.sitemeter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.parseInt(v.getTag().toString());
                //Log.v("ggg",siteData.get(pos).position+" "+siteData.get(pos).status);
            if (!siteData.get(pos).status)
                fragment.SiteMeterOnClick(siteData.get(pos).position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return siteData.size();
    }
}