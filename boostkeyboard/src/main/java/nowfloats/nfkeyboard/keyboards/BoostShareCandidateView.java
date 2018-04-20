package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.CandidateViewItemClickListener;
import nowfloats.nfkeyboard.util.SharedPrefUtil;

/**
 * Created by Admin on 02-04-2018.
 */

public class BoostShareCandidateView extends BaseCandidateView {
    private int currentView;
    public BoostShareCandidateView(Context context) {
        super(context);
    }

    public BoostShareCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoostShareCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType){
        String productTab = SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getProductVerb();
        if (!TextUtils.isEmpty(productTab)){
            ((TextView)findViewById(R.id.tv_products)).setText(productTab.trim());
        }
        switch (tabType){
            case UPDATES:
                currentView = R.id.tv_updates;
                break;
            case BACK:
                currentView = R.id.img_back;
                break;
            case PRODUCTS:
                currentView = R.id.tv_products;
                break;
            default:
                currentView = View.NO_ID;
                break;
        }

        parent.addView(this);

        findViewById(R.id.tv_updates).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.tv_products).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_back).setBackgroundResource(android.R.color.transparent);
        if (currentView != View.NO_ID) {
            findViewById(currentView).setBackgroundResource(R.drawable.round_414141);
        }

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_updates).setOnClickListener(this);
        findViewById(R.id.tv_products).setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

//        Animation anim  = AnimationUtils.loadAnimation(mContext,R.anim.slide_in_left);
//        startAnimation(anim);
        ScaleAnimation anim1  = new ScaleAnimation(0,1,1,1);
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());
        anim1.setDuration(100);
        findViewById(R.id.tv_updates).startAnimation(anim1);
        findViewById(R.id.tv_products).startAnimation(anim1);
    }

    @Override
    void setCandidateData(Bundle bundle) {

    }

    public void setItemClickListener(CandidateViewItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        if (currentView == view.getId()){
            return;
        }
        currentView = view.getId();
        findViewById(R.id.tv_updates).setBackgroundResource(view.getId() == R.id.tv_updates ? R.drawable.round_414141:android.R.color.transparent);
        findViewById(R.id.tv_products).setBackgroundResource(view.getId() == R.id.tv_products ? R.drawable.round_414141:android.R.color.transparent);
        listener.onKeyboardTabClick(view);
    }
}
