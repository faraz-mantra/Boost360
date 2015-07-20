package com.nowfloats.Store.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.StoreDataActivity;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by guru on 27-04-2015.
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder>{
    Activity appContext;
    View displayView;
    ArrayList<StoreModel> storeData;
    private LayoutInflater mInflater;
    public StoreAdapter(Activity appContext, ArrayList<StoreModel> storeData) {
        this.appContext = appContext;
        this.storeData = storeData;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView titleText,descText,readmore;
        public LinearLayout store_product_layout;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)itemView.findViewById(R.id.storeDataIcon);
            titleText = (TextView)itemView.findViewById(R.id.titleText);
            descText = (TextView)itemView.findViewById(R.id.descText);
            readmore = (TextView)itemView.findViewById(R.id.readMore);
            store_product_layout = (LinearLayout)itemView.findViewById(R.id.store_product_layout);
        }
    }

    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.store_list_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        try{
            if (storeData.get(position)!=null){
                if(storeData.get(position).PrimaryImageUri!=null)
                    if (storeData.get(position).PrimaryImageUri.contains("http")){
                        Picasso.with(appContext).load(storeData.get(position).PrimaryImageUri).into(holder.imageView);
                    }else{
                        String url = Constants.NOW_FLOATS_API_URL+storeData.get(position).PrimaryImageUri;
                        Picasso.with(appContext).load(url).into(holder.imageView);
                    }
                if (storeData.get(position).Name!=null)
                    holder.titleText.setText(storeData.get(position).Name);
                if (storeData.get(position).Desc!=null)
                    holder.descText.setText(storeData.get(position).Desc);
                holder.readmore.setPaintFlags(holder.readmore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.readmore.setText("Know more");

                holder.store_product_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent intent = new Intent(appContext,StoreDataActivity.class);
                    intent.putExtra("key",position+"");
                    appContext.startActivity(intent);
                    appContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
            });
            }
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return storeData.size();
    }
}
