package com.nowfloats.Store;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.util.Constants;
import com.thinksity.R;

/**
 * Created by Admin on 29-01-2018.
 */

public class SimpleImageTextListAdapter extends RecyclerView.Adapter<SimpleImageTextListAdapter.MyListItemHolder> {

    private Context mContext;
    private String[] myTextStrings;
    private int[] myImagesIds;
    private OnItemClickCallback onItemClickCallback;

    public SimpleImageTextListAdapter(Context context, OnItemClickCallback itemClickCallback) {
        mContext = context;
        onItemClickCallback = itemClickCallback;
    }

    @Override
    public MyListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_simple_image_text, parent, false);
        return new MyListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListItemHolder holder, int position) {
        holder.image1.setImageResource(myImagesIds[position]);
        holder.text1.setText(myTextStrings[position]);
        if (myTextStrings[position].equals("Domain and Email") && !Constants.StoreWidgets.contains("DOMAINPURCHASE")
                || myTextStrings[position].equals("Testimonials") && !Constants.StoreWidgets.contains("TESTIMONIALS")
                || myTextStrings[position].equals("Projects & teams") && !Constants.StoreWidgets.contains("PROJECTTEAM")
                || myTextStrings[position].equals("Places to look around") && !Constants.StoreWidgets.contains("PLACES-TO-LOOK-AROUND")
                || myTextStrings[position].equals("Trip Advisors") && !Constants.StoreWidgets.contains("TRIPADVISOR-REVIEWS")) {
            holder.featureLock.setVisibility(View.VISIBLE);
        } else {
            holder.featureLock.setVisibility(View.GONE);
        }
    }

    public void setItems(int[] myImagesIds, String[] strings) {
        myTextStrings = strings;
        this.myImagesIds = myImagesIds;
    }

    @Override
    public int getItemCount() {
        return myTextStrings.length;
    }

    class MyListItemHolder extends RecyclerView.ViewHolder {

        TextView text1;
        ImageView image1, featureLock;

        public MyListItemHolder(View itemView) {
            super(itemView);

            image1 = itemView.findViewById(R.id.image1);
            text1 = itemView.findViewById(R.id.text1);
            featureLock = itemView.findViewById(R.id.feature_lock);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickCallback != null) {
                        onItemClickCallback.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
