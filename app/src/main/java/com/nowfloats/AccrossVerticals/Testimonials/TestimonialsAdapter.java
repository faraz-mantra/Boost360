package com.nowfloats.AccrossVerticals.Testimonials;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Data;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Profileimage;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestimonialsAdapter extends RecyclerView.Adapter<TestimonialsAdapter.ViewHolder> {

    private List<Data> itemList;
    private TestimonialsListener listener;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;
    private UserSessionManager userSession;
    Activity activity;

    public TestimonialsAdapter(List<Data> itemList, TestimonialsListener listener, UserSessionManager session) {
        this.itemList = itemList;
        this.listener = listener;
        this.userSession = session;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_testimonials, null);
        this.context = v.requireContext();
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    public void updateList(List<Data> itemList) {
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

        if (userSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("HOTEL & MOTELS")) {
            Profileimage profileImage = itemList.get(position).getProfileImage();
            String imageUrl = "";
            if (profileImage != null) imageUrl = profileImage.getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.userProfileImage);
            holder.userName.setText(itemList.get(position).getCustomerName());
            holder.reviewTitle.setText(itemList.get(position).getCity());
            holder.reviewDescription.setText(itemList.get(position).getTestimonial());
        } else if (userSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("MANUFACTURERS")) {
            Profileimage profileImage = itemList.get(position).getProfileimage();
            String imageUrl = "";
            if (profileImage != null) imageUrl = profileImage.getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.userProfileImage);
            holder.userName.setText(itemList.get(position).getUsername());
            holder.reviewTitle.setText(itemList.get(position).getTitle());
            holder.reviewDescription.setText(itemList.get(position).getDescription());
        } else if (userSession.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).equals("SALON")) {
            Profileimage profileImage = itemList.get(position).getProfileimage();
            String imageUrl = "";
            if (profileImage != null) imageUrl = profileImage.getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.userProfileImage);
            holder.userName.setText(itemList.get(position).getName());
            holder.reviewTitle.setText(itemList.get(position).getTitle());
            holder.reviewDescription.setText(itemList.get(position).getOurStory());
        } else {
            Profileimage profileImage = itemList.get(position).getProfileImage();
            String imageUrl = "";
            if (profileImage != null) imageUrl = profileImage.getUrl();
            Glide.with(context).load(imageUrl).into(holder.userProfileImage);
            holder.userName.setText(itemList.get(position).getUsername());
            holder.reviewTitle.setText(itemList.get(position).getTitle());
            holder.reviewDescription.setText(itemList.get(position).getDescription());
        }

/*            Glide.with(context)
                    .load(itemList.get(position).getProfileimage().getUrl())
                    .into(holder.userProfileImage);*/


/*        holder.userName.setText(itemList.get(position).getUsername());
        holder.reviewTitle.setText(itemList.get(position).getTitle());
        holder.reviewDescription.setText(itemList.get(position).getDescription());*/

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, userProfileImage;
        LinearLayout menuOptionLayout;
        ConstraintLayout mainLayout;
        TextView userName, reviewTitle, reviewDescription, editOption, deleteOption;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            userProfileImage = (ImageView) itemView.findViewById(R.id.user_profile_image);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.main_layout);
            userName = itemView.findViewById(R.id.username);
            reviewTitle = itemView.findViewById(R.id.review_title);
            reviewDescription = itemView.findViewById(R.id.review_description);
            editOption = itemView.findViewById(R.id.edit_option);
            deleteOption = itemView.findViewById(R.id.delete_option);
        }
    }
}
