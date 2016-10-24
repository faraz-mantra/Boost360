/*
 * Copyright 2014 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nowfloats.NavigationDrawer.viewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

	public TextView textView,welcomeTextView,dateText,congratsTitleTextView;
    public ImageView imageView,cancelCardImageView,welcomeScreenCreateWebsiteImage,shareImageView ;
    public CardView initialCardView ;
    public Button showWebSiteButton ;
    String imageURI ;


    public MyViewHolder(View itemView) {
        super(itemView);
    }


    public static class WelcomeViewHolder extends MyViewHolder {

        public WelcomeViewHolder(View itemView) {
            super(itemView);

            welcomeTextView = (TextView) itemView.findViewById(R.id.card_websiteTextView);
            cancelCardImageView = (ImageView) itemView.findViewById(R.id.cancelCardImageView);
            initialCardView = (CardView) itemView.findViewById(R.id.initial_card_view);
            showWebSiteButton = (Button) itemView.findViewById(R.id.showWebsiteButton);
            welcomeScreenCreateWebsiteImage = (ImageView) itemView.findViewById(R.id.welcomeScreenShowWebsiteImage);
            congratsTitleTextView = (TextView) itemView.findViewById(R.id.card_titleTextView);

        }
    }

    public static class Image_Text_ViewHolder extends MyViewHolder {

        public Image_Text_ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textViewName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            dateText = (TextView) itemView.findViewById(R.id.textViewEmail);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareData);
        }
    }

}
