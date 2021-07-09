package com.nowfloats.Store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Store.Model.OnItemClickCallback;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import static com.nowfloats.education.helper.Constants.FACULTY_MANAGEMENT_FEATURE;
import static com.nowfloats.education.helper.Constants.TOPPER_FEATURE;

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
                || myTextStrings[position].equals("Projects & Teams") && !Constants.StoreWidgets.contains("PROJECTTEAM")
                || myTextStrings[position].equals("Places to look around") && !Constants.StoreWidgets.contains("PLACES-TO-LOOK-AROUND")
                || myTextStrings[position].equals("Unlimited digital brochures") && !Constants.StoreWidgets.contains("BROCHURE")
                || myTextStrings[position].equals("Tripadvisor Ratings") && !Constants.StoreWidgets.contains("TRIPADVISOR-REVIEWS")
                || myTextStrings[position].equals("Toppers of Institute") && !Constants.StoreWidgets.contains(TOPPER_FEATURE)
                || myTextStrings[position].equals("Faculty Management") && !Constants.StoreWidgets.contains(FACULTY_MANAGEMENT_FEATURE)
                /*|| myTextStrings[position].equals("Seasonal Offers") && !Constants.StoreWidgets.contains("OFFERS")*/ //this is free widget
        ) {
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
