package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;

import nowfloats.nfkeyboard.interface_contracts.CandidateViewInterface;
import nowfloats.nfkeyboard.util.KeyboardUtils;

/**
 * Created by Admin on 22-02-2018.
 */

public class CandidateViewBaseImpl extends FrameLayout implements CandidateViewInterface,View.OnClickListener {
    private Context mContext;
    private HashMap<KeyboardUtils.CandidateType, View> mCandidateMaps = new HashMap<>(KeyboardUtils.CandidateType.values().length);
    private OnClickListener onClickListener;

    public CandidateViewBaseImpl(Context context) {
        super(context);
        mContext = context;
    }

    public CandidateViewBaseImpl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CandidateViewBaseImpl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setItemClickListener(OnClickListener listener){
        onClickListener = listener;
    }
    public boolean addCandidateTypeView(KeyboardUtils.CandidateType type) {
        View candidateParentLayout =  getCandidateView(type);
        if (candidateParentLayout == null) return false;
        if (candidateParentLayout.getParent() != null){
            ((ViewGroup)candidateParentLayout.getParent()).removeView(candidateParentLayout);
        }
        switch (type){
            case BOOST_SHARE:
                BoostCandidateView boostView = (BoostCandidateView) candidateParentLayout;
                boostView.setItemClickListener(this);
                boostView.addCandidateView(this);
                break;
        }
        return true;
    }
    public View getCandidateView(KeyboardUtils.CandidateType type) {
        if (!mCandidateMaps.containsKey(type)){
            mCandidateMaps.put(type, type == KeyboardUtils.CandidateType.NULL ? null :
                    LayoutInflater.from(mContext).inflate(KeyboardUtils.CandidateType.getXml(type), this,false));
        }
        return mCandidateMaps.get(type);
    }

    @Override
    public void onClick(View view) {
        onClickListener.onClick(view);
    }
}
