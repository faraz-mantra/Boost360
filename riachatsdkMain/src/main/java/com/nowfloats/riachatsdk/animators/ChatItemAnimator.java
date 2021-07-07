package com.nowfloats.riachatsdk.animators;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.adapters.RvChatAdapter;

/**
 * Created by Miroslaw Stanek on 06.01.2016.
 */
public class ChatItemAnimator extends DefaultItemAnimator {

    private Context mContext;

    public ChatItemAnimator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {


        if (oldHolder instanceof RvChatAdapter.TypingViewHolder) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);
            ((RvChatAdapter.TypingViewHolder) oldHolder).rlLoadingDots.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {

        /*if (holder instanceof RvChatAdapter.TextViewHolder) {
            ((RvChatAdapter.TextViewHolder) holder).llBubbleContainer
                    .startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
        } else*/
        if (holder instanceof RvChatAdapter.TypingViewHolder) {
            ((RvChatAdapter.TypingViewHolder) holder).rlLoadingDots
                    .startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
        }

        return super.animateAdd(holder);
    }
}
