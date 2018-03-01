package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nowfloats.nfkeyboard.R;

/**
 * Created by Admin on 26-02-2018.
 */

public class BoostCandidateView extends LinearLayout implements View.OnClickListener {
    private OnClickListener listener;
    private int currentView;
    public BoostCandidateView(Context context) {
        super(context);
    }

    public BoostCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoostCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void addCandidateView(ViewGroup parent){
        currentView = R.id.img_nowfloats;
        parent.addView(this);
        findViewById(R.id.tv_updates).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.tv_products).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_settings).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.img_nowfloats).setBackgroundResource(R.drawable.round_414141);
        findViewById(R.id.img_nowfloats).setOnClickListener(this);
        findViewById(R.id.img_settings).setOnClickListener(this);
        findViewById(R.id.tv_updates).setOnClickListener(this);
        findViewById(R.id.tv_products).setOnClickListener(this);
    }
    public void setItemClickListener(OnClickListener listener){
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
        findViewById(R.id.img_settings).setBackgroundResource(view.getId() == R.id.img_settings ? R.drawable.round_414141:android.R.color.transparent);
        findViewById(R.id.img_nowfloats).setBackgroundResource(view.getId() == R.id.img_nowfloats ? R.drawable.round_414141:android.R.color.transparent);
        listener.onClick(view);
    }
}
