package com.android.inputmethod.keyboard.top;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.inputmethod.keyboard.top.actionrow.ActionRowView;
import com.android.inputmethod.keyboard.top.services.ServiceResultsView;

import java.util.List;

import io.separ.neural.inputmethod.colors.ColorManager;
import io.separ.neural.inputmethod.indic.R;
import io.separ.neural.inputmethod.indic.suggestions.SuggestionStripView;
import io.separ.neural.inputmethod.slash.RSearchItem;
import io.separ.neural.inputmethod.slash.ServiceRequestManager;

import static android.view.View.GONE;

/**
 * Created by sepehr on 3/2/17.
 */

//TODO when latin ime stops us, we have to cancel old queries

public class TopDisplayController {
    final Runnable hideSuggestionAfter;
    private final LinearLayout holderLayout; //contains SStrip, ActionRow(nums,emojis,serviceList), ServiceResultsView
    private final ActionRowView mActionRowView;
    private final View mSuggestionsStripHackyContainer;
    private final SuggestionStripView mSuggestionsStripView;
    private final ImageButton mVoiceKey;
    private final ImageView mServicesKey;
    private ServiceResultsView mServiceResultsView;
    private final View mDimOtherView;
    //private final View mActionRowContainer;

    public int getHeight() {
        return holderLayout.getHeight();
    }

    public void setVisualState(ServiceResultsView.VisualSate visualState) {
        if (mServiceResultsView != null) {
            mServiceResultsView.setVisualState(visualState);
        }
    }

    public void showRetryErrorMessage(boolean network) {
        if (mServiceResultsView != null) {
            this.mServiceResultsView.showRetryErrorMessage(network);
        }
    }

    public void setSearchItems(String slash, List<RSearchItem> items, String authorizedStatus) {
        setVisualState(ServiceResultsView.VisualSate.Results);
        this.mActionRowView.setVisibility(GONE);
        if (mServiceResultsView != null) {
            this.mServiceResultsView.setSearchItems(slash, items, authorizedStatus);
        }
    }

    public void runSearch(String query) {
        if (mServiceResultsView != null) {
            mServiceResultsView.runSearch(query, false);
        }
    }

    public void runSearch(String serviceId, String context) {
        mActionRowView.removeCallbacks(this.hideSuggestionAfter);
        this.mActionRowView.setVisibility(GONE);
        mDimOtherView.setVisibility(View.VISIBLE);
        if (mServiceResultsView != null) {
            this.mServiceResultsView.startSearch(serviceId, context);
        }
        if (this.mSuggestionsStripHackyContainer.getVisibility() != GONE) {
            this.mSuggestionsStripHackyContainer.setVisibility(GONE);
        }
    }

    class C04611 implements Runnable {
        C04611() {
        }

        public void run() {
            mActionRowView.setVisibility(View.VISIBLE);
            mSuggestionsStripHackyContainer.setVisibility(GONE);
        }
    }

    public void updateBarVisibility() {
        mActionRowView.setVisibility(View.GONE);
        mSuggestionsStripHackyContainer.setVisibility(View.VISIBLE);
    }

    public void drop() {

        if (mServiceResultsView != null) {
            this.mServiceResultsView.drop();
        }
    }

    public void hideAll() {
        ServiceRequestManager.getInstance().cancelLastRequest();
        mDimOtherView.setVisibility(View.GONE);
        if (mServiceResultsView != null) {
            mServiceResultsView.setVisibility(GONE);
        }
        updateBarVisibility();
        if (mServiceResultsView != null) {
            mServiceResultsView.reset();
        }
    }

    public TopDisplayController(View parent) {
        hideSuggestionAfter = new C04611();
        //mActionRowContainer = parent.findViewById(R.id.action_row_container);
        holderLayout = (LinearLayout) parent.findViewById(R.id.keyboard_top_area);
        mActionRowView = (ActionRowView) parent.findViewById(R.id.action_row);
        mSuggestionsStripView = (SuggestionStripView) parent.findViewById(R.id.suggestion_strip_view);
        mVoiceKey = (ImageButton) mSuggestionsStripView.findViewById(R.id.suggestions_strip_voice_key);
        mServicesKey = (ImageView) mSuggestionsStripView.findViewById(R.id.suggestions_strip_services_key);
        ColorManager.addObserverAndCall(mSuggestionsStripView);
        mSuggestionsStripHackyContainer = parent.findViewById(R.id.suggestion_strip_hacky_container);
        mSuggestionsStripHackyContainer.setVisibility(GONE);
       /* mServiceResultsView = (ServiceResultsView) parent.findViewById(R.id.suggestion_source_results);
        mServiceResultsView.setVisibility(GONE);*/
        mDimOtherView = (View) parent.findViewById(R.id.dim_other_view);
    }

    public void showSuggestions(boolean shouldAnimate) {
        /*  if (this.mSuggestionsStripView.getVisibility() == View.VISIBLE) {
            if (mServiceResultsView != null && this.mServiceResultsView.getVisibility() == View.VISIBLE) {
                this.mSuggestionsStripHackyContainer.setVisibility(GONE);
                return;
            }
            mActionRowView.removeCallbacks(this.hideSuggestionAfter);
            mActionRowView.setVisibility(GONE);
            this.mSuggestionsStripHackyContainer.setVisibility(View.VISIBLE);
            //mActionRowView.postDelayed(this.hideSuggestionAfter, 5000);
            mSuggestionsStripView.setVisibility(View.VISIBLE);
        }*/
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mServicesKey.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (shouldAnimate) {
            mServicesKey.startAnimation(scaleAnimation);
            mServicesKey.setClickable(false);
        } else {
            mServicesKey.clearAnimation();
            mServicesKey.setClickable(true);
        }
        mActionRowView.removeCallbacks(this.hideSuggestionAfter);
        if (mActionRowView.findViewById(R.id.tv_products) != null) {
            mActionRowView.findViewById(R.id.tv_photos).setVisibility(GONE);
            mActionRowView.findViewById(R.id.tv_updates).setVisibility(GONE);
            mActionRowView.findViewById(R.id.tv_details).setVisibility(GONE);
            mActionRowView.findViewById(R.id.tv_products).setVisibility(GONE);
        }
        mVoiceKey.setVisibility(View.VISIBLE);
        mSuggestionsStripView.setVisibility(View.VISIBLE);
        mActionRowView.setVisibility(GONE);
        mServicesKey.setVisibility(View.VISIBLE);
        mSuggestionsStripHackyContainer.setVisibility(View.VISIBLE);
        //mActionRowView.postDelayed(this.hideSuggestionAfter, 5000);
    }

    public void showActionRow(Context context) {
        mActionRowView.setVisibility(View.VISIBLE);
        mSuggestionsStripHackyContainer.setVisibility(GONE);
        mActionRowView.removeCallbacks(this.hideSuggestionAfter);
        //mActionRowView.postDelayed(this.hideSuggestionAfter, 20000);
        mSuggestionsStripView.setVisibility(GONE);
        if (mActionRowView.findViewById(R.id.tv_products) != null) {

            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(200);
            scaleAnimation.setInterpolator(new OvershootInterpolator(1.2f));
            mActionRowView.findViewById(R.id.img_back).startAnimation(scaleAnimation);
            ScaleAnimation anim1 = new ScaleAnimation(0, 1, 1, 1);
            anim1.setInterpolator(new AccelerateDecelerateInterpolator());
            mActionRowView.findViewById(R.id.tv_photos).setVisibility(View.VISIBLE);
            mActionRowView.findViewById(R.id.tv_updates).setVisibility(View.VISIBLE);
            mActionRowView.findViewById(R.id.tv_details).setVisibility(View.VISIBLE);
            mActionRowView.findViewById(R.id.tv_products).setVisibility(View.VISIBLE);
            anim1.setDuration(100);
            mActionRowView.findViewById(R.id.tv_updates).startAnimation(anim1);
            mActionRowView.findViewById(R.id.tv_products).startAnimation(anim1);
            mActionRowView.findViewById(R.id.tv_photos).startAnimation(anim1);
            mActionRowView.findViewById(R.id.tv_details).startAnimation(anim1);
            mActionRowView.findViewById(R.id.tv_products).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            mActionRowView.findViewById(R.id.tv_updates).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            mActionRowView.findViewById(R.id.tv_photos).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            mActionRowView.findViewById(R.id.tv_details).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }

    public boolean isActionViewVisible() {
        return mActionRowView.getVisibility() == View.VISIBLE;
    }
}
