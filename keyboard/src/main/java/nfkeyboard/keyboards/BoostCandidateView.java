package nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.util.SharedPrefUtil;

/**
 * Created by Admin on 26-02-2018.
 */

public class BoostCandidateView extends BaseCandidateView {
    private int currentView;
    private Context mContext;

    public BoostCandidateView(Context context) {
        super(context);
        mContext = context;
    }

    public BoostCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BoostCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType) {
        String productTab = SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getProductVerb();
        if (!TextUtils.isEmpty(productTab)) {
            ((TextView) findViewById(R.id.tv_products)).setText(productTab.trim());
        }
        switch (tabType) {
            case UPDATES:
                currentView = R.id.tv_updates;
                break;
            case KEYBOARD:
                currentView = R.id.img_nowfloats;
                break;
            case PRODUCTS:
                currentView = R.id.tv_products;
                break;
            case SETTINGS:
                currentView = R.id.img_settings;
                break;
            default:
                currentView = View.NO_ID;
                break;
        }
        parent.addView(this);
        findViewById(R.id.tv_updates).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.tv_products).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_settings).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_nowfloats).setBackgroundResource(android.R.color.transparent);
//        if (currentView != View.NO_ID) {
//            findViewById(currentView).setBackgroundResource(R.drawable.round_414141);
//        }

        findViewById(R.id.img_nowfloats).setOnClickListener(this);
        findViewById(R.id.img_settings).setOnClickListener(this);
        findViewById(R.id.tv_updates).setOnClickListener(this);
        findViewById(R.id.tv_products).setOnClickListener(this);
    }

    @Override
    void setCandidateData(Bundle bundle) {

    }

    /* public void setItemClickListener(OnClickListener listener){
         this.listener = listener;
     }*/
    @Override
    public void onClick(View view) {
        if (currentView == view.getId()) {
            return;
        }
        currentView = view.getId();
//        findViewById(R.id.tv_updates).setBackgroundResource(view.getId() == R.id.tv_updates ? R.drawable.round_414141 : android.R.color.transparent);
//        findViewById(R.id.tv_products).setBackgroundResource(view.getId() == R.id.tv_products ? R.drawable.round_414141 : android.R.color.transparent);
//        findViewById(R.id.img_settings).setBackgroundResource(view.getId() == R.id.img_settings ? R.drawable.round_414141 : android.R.color.transparent);
//        findViewById(R.id.img_nowfloats).setBackgroundResource(view.getId() == R.id.img_nowfloats ? R.drawable.round_414141 : android.R.color.transparent);
        listener.onKeyboardTabClick(view);
    }
}
