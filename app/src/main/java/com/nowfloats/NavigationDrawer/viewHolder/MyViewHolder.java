package com.nowfloats.NavigationDrawer.viewHolder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView textView, welcomeTextView, dateText, congratsTitleTextView;
    public ImageView imageView, cancelCardImageView, welcomeScreenCreateWebsiteImage, shareImageView, share_facebook, share_whatsapp;
    public CardView initialCardView;
    public Button showWebSiteButton;


    public MyViewHolder(View itemView) {
        super(itemView);
    }


    public static class WelcomeViewHolder extends MyViewHolder {

        public WelcomeViewHolder(View itemView) {
            super(itemView);

            welcomeTextView = itemView.findViewById(R.id.card_websiteTextView);
            cancelCardImageView = itemView.findViewById(R.id.cancelCardImageView);
            initialCardView = itemView.findViewById(R.id.initial_card_view);
            showWebSiteButton = itemView.findViewById(R.id.showWebsiteButton);
            welcomeScreenCreateWebsiteImage = itemView.findViewById(R.id.welcomeScreenShowWebsiteImage);
            congratsTitleTextView = itemView.findViewById(R.id.card_titleTextView);
        }
    }

    public static class Image_Text_ViewHolder extends MyViewHolder {

        public Image_Text_ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView);
            dateText = itemView.findViewById(R.id.textViewEmail);
            shareImageView = itemView.findViewById(R.id.shareData);
            share_facebook = itemView.findViewById(R.id.share_facebook);
            share_whatsapp = itemView.findViewById(R.id.share_whatsapp);
        }
    }
}