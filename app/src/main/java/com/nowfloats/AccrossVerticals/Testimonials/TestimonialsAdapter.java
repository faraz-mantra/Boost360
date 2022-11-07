package com.nowfloats.AccrossVerticals.Testimonials;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.TestimonialData;
import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Profileimage;
import com.nowfloats.Login.UserSessionManager;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.nowfloats.AccrossVerticals.Testimonials.TestimonialUtils.*;

@Deprecated
public class TestimonialsAdapter extends RecyclerView.Adapter<TestimonialsAdapter.ViewHolder> {

    private List<TestimonialData> itemList;
    private TestimonialsListener listener;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private UserSessionManager userSession;
    private Activity context;

    public TestimonialsAdapter(List<TestimonialData> itemList, TestimonialsListener listener, UserSessionManager session, Activity context) {
        this.itemList = itemList;
        this.listener = listener;
        this.userSession = session;
        this.context = context;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_testimonials, null);
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    public void updateList(List<TestimonialData> itemList) {
        this.itemList = itemList;
        menuPosition = -1; //reset menu
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        TestimonialData data = itemList.get(position);
        Profileimage profileImage = data.getProfileimage();
        String imageUrl = "";
        if (profileImage != null) imageUrl = profileImage.getUrl();
        Glide.with(context).load(imageUrl).into(holder.userProfileImage);
        holder.userName.setText(data.getUsername());

        holder.reviewTitle.setVisibility(isReviewSecondValue(userSession.getFP_AppExperienceCode()));
        holder.reviewTitle.setText(getReviewSecondValue(data, userSession.getFP_AppExperienceCode()));
        holder.reviewDescription.setText(data.getDescription());
        holder.imgDesc.setVisibility(isProfileDescShow(userSession.getFP_AppExperienceCode()));
        holder.imgDesc.setText(getProfileDescValue(data, userSession.getFP_AppExperienceCode()));


        holder.menuOptionLayout.setVisibility(View.GONE);
        if (menuPosition == position) {
            if (menuStatus) {
                holder.menuOptionLayout.setVisibility(View.VISIBLE);
            } else {
                holder.menuOptionLayout.setVisibility(View.GONE);
            }
        }
        holder.mainLayout.setOnClickListener(v -> listener.itemMenuOptionStatus(position, false));

        holder.menuButton.setOnClickListener(v -> {
            if (holder.menuOptionLayout.getVisibility() == View.GONE) {
                listener.itemMenuOptionStatus(position, true);
            } else {
                listener.itemMenuOptionStatus(position, false);
            }
        });

        holder.editOption.setOnClickListener(v -> listener.editOptionClicked(data));

        holder.deleteOption.setOnClickListener(v -> listener.deleteOptionClicked(data));
        holder.shareOption.setOnClickListener(v -> listener.shareOptionClicked(data));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, userProfileImage;
        LinearLayout menuOptionLayout;
        ConstraintLayout mainLayout;
        TextView userName, reviewTitle, reviewDescription, editOption, deleteOption, imgDesc, shareOption;

        public ViewHolder(View itemView) {
            super(itemView);

            menuButton = itemView.findViewById(R.id.single_item_menu_button);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            menuOptionLayout = itemView.findViewById(R.id.menu_options);
            mainLayout = itemView.findViewById(R.id.main_layout);
            userName = itemView.findViewById(R.id.username);
            reviewTitle = itemView.findViewById(R.id.review_title);
            reviewDescription = itemView.findViewById(R.id.review_description);
            editOption = itemView.findViewById(R.id.edit_option);
            deleteOption = itemView.findViewById(R.id.delete_option);
            shareOption = itemView.findViewById(R.id.share_option);
            imgDesc = itemView.findViewById(R.id.img_desc);
        }
    }
}
