package com.nowfloats.Store.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
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
    String modelKey = "";
    private UserSessionManager mSessionManager;
    public StoreAdapter(Activity appContext, ArrayList<StoreModel> storeData, String key, UserSessionManager sessionManager) {
        this.appContext = appContext;
        this.storeData = storeData;
        this.modelKey = key;
        this.mSessionManager = sessionManager;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        CardView cardView;
        public TextView titleText,descText,readmore,priceText,knowMoreText;
        public LinearLayout store_product_layout;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)itemView.findViewById(R.id.storeDataIcon);
            titleText = (TextView)itemView.findViewById(R.id.titleText);
            descText = (TextView)itemView.findViewById(R.id.descText);
            readmore = (TextView)itemView.findViewById(R.id.readMore);
            priceText = (TextView)itemView.findViewById(R.id.priceText);
            cardView = (CardView) itemView.findViewById(R.id.new_card);
            knowMoreText = (TextView)itemView.findViewById(R.id.knowMoreText);
            store_product_layout = (LinearLayout)itemView.findViewById(R.id.store_product_layout);
        }
    }

    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.store_list_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        //class str = StoreAdapter.class;
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
                if (storeData.get(position).Name!=null) {
                    holder.titleText.setText(storeData.get(position).Name);
                    if(holder.titleText.getText().toString().toLowerCase().contains("mini")){
                        holder.cardView.setVisibility(View.VISIBLE);
                    }
                }
                if (storeData.get(position).Desc!=null)
                    holder.descText.setText(storeData.get(position).Desc);
                holder.readmore.setPaintFlags(holder.readmore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.readmore.setText(appContext.getString(R.string.read_more));
                if(modelKey.equals("active")){
                    holder.priceText.setVisibility(View.GONE);
                    holder.readmore.setVisibility(View.GONE);
                    holder.knowMoreText.setVisibility(View.VISIBLE);
                    holder.knowMoreText.setText(appContext.getString(R.string.know_more));
//                    holder.validityText.setVisibility(View.VISIBLE);
//                    holder.validityText.setText("("+storeData.get(position).ExpiryInMths+" months plan,"
//                            +storeData.get(position).ValiditiyInMths+" months left"+")");
                }else{
                    holder.priceText.setVisibility(View.VISIBLE);
                    holder.readmore.setVisibility(View.VISIBLE);
                    holder.knowMoreText.setVisibility(View.GONE);
//                    holder.validityText.setVisibility(View.GONE);
                    String currency = " ";
                    if (storeData.get(position).CurrencyCode!=null && !storeData.get(position).CurrencyCode.equals("null") && storeData.get(position).CurrencyCode.length()>0)
                        currency = " "+storeData.get(position).CurrencyCode;

                    holder.priceText.setText(""+storeData.get(position).Price+ currency
                    /*+"/"+storeData.get(position).ExpiryInMths+" months"*/);
                }
                holder.store_product_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent intent = new Intent(appContext,StoreDataActivity.class);
                    intent.putExtra("key",position+"");
                    intent.putExtra("type",modelKey+"");
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