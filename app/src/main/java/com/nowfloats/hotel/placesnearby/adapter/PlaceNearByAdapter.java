package com.nowfloats.hotel.placesnearby.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.hotel.API.model.GetPlacesAround.Data;
import com.nowfloats.hotel.Interfaces.PlaceNearByListener;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceNearByAdapter extends RecyclerView.Adapter<PlaceNearByAdapter.ViewHolder> {

    private List<Data> itemList;
    private PlaceNearByListener listener;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;

    public PlaceNearByAdapter(List<Data> data, PlaceNearByListener listener) {
        this.itemList = data;
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_nearby, null);
        context = v.getContext();
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    public void updateList(List<Data> list) {
        itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menuOptionLayout.setVisibility(View.GONE);
        if (menuPosition == position) {
            if (menuStatus) {
                holder.menuOptionLayout.setVisibility(View.VISIBLE);
            } else {
                holder.menuOptionLayout.setVisibility(View.GONE);
            }
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemMenuOptionStatus(position, false);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.menuOptionLayout.getVisibility() == View.GONE) {
                    listener.itemMenuOptionStatus(position, true);
//                    holder.menuOptionLayout.setVisibility(View.VISIBLE);
                } else {
                    listener.itemMenuOptionStatus(position, false);
//                    holder.menuOptionLayout.setVisibility(View.GONE);
                }
            }
        });

        holder.editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editOptionClicked(itemList.get(position));
            }
        });

        holder.deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteOptionClicked(itemList.get(position));
            }
        });

        holder.placeName.setText(itemList.get(position).getPlaceName());
        holder.placeAddress.setText(itemList.get(position).getPlaceAddress());
        holder.placeDescription.setText(itemList.get(position).getPlaceImage().getDescription());
        SpannableString content = new SpannableString(itemList.get(position).getDistance() + " from your place");
        content.setSpan(new StyleSpan(Typeface.BOLD), 0, itemList.get(position).getDistance().length(), 0);
        holder.placeDistance.setText(content);

        Glide.with(context)
                .load(itemList.get(position).getPlaceImage().getUrl())
                .into(holder.placeImage);

        holder.dummyView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        holder.dummyView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, placeImage;
        LinearLayout menuOptionLayout;
        ConstraintLayout mainLayout;
        TextView placeName, placeAddress, placeDescription, placeDistance, editOption, deleteOption;
        View dummyView, dummyView1;

        public ViewHolder(View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.place_name);
            placeAddress = itemView.findViewById(R.id.place_address);
            placeDescription = itemView.findViewById(R.id.place_description);
            placeDistance = itemView.findViewById(R.id.distance);
            placeImage = (ImageView) itemView.findViewById(R.id.place_profile_image);
            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.main_layout);
            editOption = itemView.findViewById(R.id.edit_option);
            deleteOption = itemView.findViewById(R.id.delete_option);
            dummyView = itemView.findViewById(R.id.dummy_view);
            dummyView1 = itemView.findViewById(R.id.dummy_view1);
        }
    }
}
