package com.nowfloats.AccrossVerticals.Testimonials;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.AccrossVerticals.API.model.testimonials.Data;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by NowFloats on 02-08-2016.
 */
public class TestimonialsAdapter extends RecyclerView.Adapter<TestimonialsAdapter.ViewHolder> {

    private List<Data> itemList;
    private TestimonialsListener listener;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;

    public TestimonialsAdapter(List<Data> itemList, TestimonialsListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_testimonials, null);
        this.context = v.getContext();
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    public void updateList(List<Data> itemList){
        this.itemList = itemList;
        menuPosition = -1; //reset menu
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

        Glide.with(context)
                .load(itemList.get(position).getProfileimage().getUrl())
                .into(holder.userProfileImage);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton,userProfileImage;
        LinearLayout menuOptionLayout;
        ConstraintLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            userProfileImage = (ImageView) itemView.findViewById(R.id.user_profile_image);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.main_layout);
        }
    }
}
