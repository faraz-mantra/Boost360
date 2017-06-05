package com.nowfloats.riachatsdk.animators;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
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
            ((RvChatAdapter.TypingViewHolder) oldHolder).rlLoadingDots.
                    startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right));
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
