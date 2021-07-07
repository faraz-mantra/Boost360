package nfkeyboard.keyboards;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.CandidateViewItemClickListener;
import nfkeyboard.util.SharedPrefUtil;

/**
 * Created by Admin on 02-04-2018.
 */

public class BoostShareCandidateView extends BaseCandidateView {
    private int currentView;
    private View tabHorizontal;

    public BoostShareCandidateView(Context context) {
        super(context);
    }

    public BoostShareCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoostShareCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType) {
        String productTab = SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getProductVerb();
        tabHorizontal = findViewById(R.id.tab_horizontal);
        tabHorizontal.setX(findViewById(R.id.tv_updates).getX() + findViewById(R.id.tv_updates).getWidth() / 2 - tabHorizontal.getWidth() / 2);
        if (!TextUtils.isEmpty(productTab)) {
            ((TextView) findViewById(R.id.tv_products)).setText(productTab.trim());
        }
        switch (tabType) {
            case UPDATES:
                currentView = R.id.tv_updates;
                tabHorizontal.setX(findViewById(R.id.tv_updates).getX() + findViewById(R.id.tv_updates).getWidth() / 2 - tabHorizontal.getWidth() / 2);
                tabHorizontal.setVisibility(VISIBLE);
                break;
            case BACK:
                currentView = R.id.img_back;
                findViewById(R.id.tab_horizontal).setVisibility(INVISIBLE);
                break;
            case PRODUCTS:
                currentView = R.id.tv_products;
                tabHorizontal.setX(findViewById(R.id.tv_products).getX() + findViewById(R.id.tv_products).getWidth() / 2 - tabHorizontal.getWidth() / 2);
                tabHorizontal.setVisibility(VISIBLE);
                break;
            case PHOTOS:
                currentView = R.id.tv_photos;
                tabHorizontal.setX(findViewById(R.id.tv_photos).getX() + findViewById(R.id.tv_photos).getWidth() / 2 - tabHorizontal.getWidth() / 2);
                tabHorizontal.setVisibility(VISIBLE);
                break;
            case DETAILS:
                currentView = R.id.tv_details;
                tabHorizontal.setX(findViewById(R.id.tv_details).getX() + findViewById(R.id.tv_details).getWidth() / 2 - tabHorizontal.getWidth() / 2);
                tabHorizontal.setVisibility(VISIBLE);
            default:
                currentView = View.NO_ID;
                break;
        }

        parent.addView(this);

        ((TextView) findViewById(R.id.tv_updates)).setTextColor(mContext.getResources().getColor(R.color.white_70));
        ((TextView) findViewById(R.id.tv_products)).setTextColor(mContext.getResources().getColor(R.color.white_70));
        ((TextView) findViewById(R.id.tv_photos)).setTextColor(mContext.getResources().getColor(R.color.white_70));
        ((TextView) findViewById(R.id.tv_details)).setTextColor(mContext.getResources().getColor(R.color.white_70));
        findViewById(R.id.tab_horizontal).setVisibility(INVISIBLE);
        if (currentView != View.NO_ID) {
            ((TextView) findViewById(currentView)).setTextColor(mContext.getResources().getColor(R.color.white));
            tabHorizontal.setX(findViewById(currentView).getX() + findViewById(currentView).getWidth() / 2 - tabHorizontal.getWidth() / 2);
            tabHorizontal.setVisibility(VISIBLE);
        }

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_updates).setOnClickListener(this);
        findViewById(R.id.tv_products).setOnClickListener(this);
        findViewById(R.id.tv_photos).setOnClickListener(this);
        findViewById(R.id.tv_details).setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

//        Animation anim  = AnimationUtils.loadAnimation(mContext,R.anim.slide_in_left);
//        startAnimation(anim);
        tabHorizontal = findViewById(R.id.tab_horizontal);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
        tabHorizontal.setMinimumWidth(px);
        tabHorizontal.setVisibility(INVISIBLE);
        tabHorizontal.setX(findViewById(R.id.tv_updates).getX() + findViewById(R.id.tv_updates).getWidth() / 2 - tabHorizontal.getWidth() / 2);
        ScaleAnimation anim1 = new ScaleAnimation(0, 1, 1, 1);
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());
        anim1.setDuration(100);
        findViewById(R.id.tv_updates).startAnimation(anim1);
        findViewById(R.id.tv_products).startAnimation(anim1);
        findViewById(R.id.tv_photos).startAnimation(anim1);
        findViewById(R.id.tv_details).startAnimation(anim1);
    }

    @Override
    void setCandidateData(Bundle bundle) {

    }

    public void setItemClickListener(CandidateViewItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (currentView == view.getId()) {
            return;
        }
        currentView = view.getId();
        ((TextView) findViewById(R.id.tv_updates)).setTextColor(mContext.getResources().getColor(view.getId() == R.id.tv_updates ? R.color.white : R.color.white_70));
        ((TextView) findViewById(R.id.tv_products)).setTextColor(mContext.getResources().getColor(view.getId() == R.id.tv_products ? R.color.white : R.color.white_70));
        ((TextView) findViewById(R.id.tv_photos)).setTextColor(mContext.getResources().getColor(view.getId() == R.id.tv_photos ? R.color.white : R.color.white_70));
        ((TextView) findViewById(R.id.tv_details)).setTextColor(mContext.getResources().getColor(view.getId() == R.id.tv_details ? R.color.white : R.color.white_70));
        tabHorizontal = findViewById(R.id.tab_horizontal);
        if (currentView == R.id.img_back) {
            tabHorizontal.setVisibility(INVISIBLE);
            tabHorizontal.setX(findViewById(R.id.tv_updates).getX() + findViewById(R.id.tv_updates).getWidth() / 2 - tabHorizontal.getWidth() / 2);
        } else {
            //tabHorizontal.setVisibility(VISIBLE);
            tabHorizontal.setMinimumWidth(70);
            int[] x = {0, 0}, y = {0, 0};
            tabHorizontal.getLocationOnScreen(x);
            findViewById(currentView).getLocationOnScreen(y);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tabHorizontal, View.TRANSLATION_X, x[0], y[0] + findViewById(currentView).getWidth() / 2 - tabHorizontal.getWidth() / 2);
            objectAnimator.setDuration(200);
            objectAnimator.start();
        }
        listener.onKeyboardTabClick(view);
    }
}
