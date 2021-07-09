package com.nowfloats.signup.UI;

import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.app.Activity;

import com.thinksity.R;


/**
 * Created by admin on 5/23/2017.
 */

public class AnimationTool {

    private AnimationListener animationListener;
    private AnimationComposer animationComposer;
    private Activity mContext;
    private boolean isVisible;

    public interface AnimationListener {
        void onAnimationEnd(AnimationType animationType);
        void onAnimationStart(AnimationType animationType);
    }

    public AnimationTool(Activity mContext) {
        this.mContext = mContext;
    }

    public AnimationComposer setAnimationType
            (AnimationType animationType) {
        if (animationComposer == null)
            animationComposer = new AnimationComposer(animationType);
        animationComposer.setAnimationType(animationType);
        return animationComposer;
    }

    public void setVisbilityStatus(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setListener(AnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    public class AnimationComposer implements Animation.AnimationListener {

        private AnimationType animationType;
        private int duration, repeatTimes;
        private Interpolator interpolator;
        private View view;

        public AnimationComposer(AnimationType animationType) {
            this.animationType = animationType;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (animationListener != null) {
                animationListener.onAnimationStart(animationType);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isVisible) {
                view.setVisibility(View.INVISIBLE);
            }

            if (animationListener != null) {
                animationListener.onAnimationEnd(animationType);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        public void setAnimationType(AnimationType animationType) {
            this.animationType = animationType;
        }

        public AnimationComposer duration(int duration) {
            this.duration = duration;
            return this;
        }

        public AnimationComposer interpolate(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public AnimationComposer repeat(int repeatTimes) {
            this.repeatTimes = repeatTimes;
            return this;
        }

        public void playOn(View view) {
            this.view = view;
            startAnimation();
        }

        private void startAnimation() {

            Animation animation = null;
            switch (animationType) {

                case ZOOM_IN:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
                    break;
                case RIA_ZOOM_IN:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.ria_circle_left_out);
                    break;
                case RIA_ZOOM_OUT:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.ria_circle_left_in);
                    break;
                case SLIDE_DOWN:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
                    break;
                case SLIDE_UP:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
                    break;
                case SLIDE_LEFT:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slidein_left_fast);
                    break;
                case SLIDE_RIGHT:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slidein_right_fast);
                    break;
                case SLIDE_RIA_LEFT:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slidein_ria_left);
                    break;
                case SLIDE_RIA_RIGHT:
                    animation = AnimationUtils.loadAnimation(mContext, R.anim.slidein_ria_right);
                    break;

            }

            if (!isVisible) {
                view.setVisibility(View.VISIBLE);
                animation.setInterpolator(interpolator);
            }

            view.setAnimation(animation);
            animation.setAnimationListener(animationComposer);
        }
    }


}
