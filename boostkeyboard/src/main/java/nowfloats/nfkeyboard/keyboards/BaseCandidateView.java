package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Admin on 26-02-2018.
 */

public abstract class BaseCandidateView extends LinearLayout implements View.OnClickListener{
    OnClickListener listener;
    Context mContext;
    public BaseCandidateView(Context context) {
        super(context);
        mContext = context;
    }

    public BaseCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BaseCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseCandidateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }
    public void setItemClickListener(OnClickListener listener){
        this.listener = listener;
    }
    abstract void addCandidateView(ViewGroup parent, ImePresenterImpl.TabType tabType);

    abstract void setCandidateData(Bundle bundle);
}
