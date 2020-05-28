package nfkeyboard.keyboards;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.CandidateViewInterface;
import nfkeyboard.interface_contracts.CandidateViewItemClickListener;
import nfkeyboard.models.KeywordModel;
import nfkeyboard.util.KeyboardUtils;

/**
 * Created by Admin on 22-02-2018.
 */

public class CandidateViewBaseImpl extends FrameLayout implements CandidateViewInterface, CandidateViewItemClickListener {
    private Context mContext;
    private HashMap<KeyboardUtils.CandidateType, View> mCandidateMaps = new HashMap<>(KeyboardUtils.CandidateType.values().length);
    private CandidateViewItemClickListener onClickListener;

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

    public void setcandidateItemClickListener(CandidateViewItemClickListener listener) {
        setBackgroundResource(R.color.kbd_key_color);
        onClickListener = listener;
    }

    public boolean addCandidateTypeView(KeyboardUtils.CandidateType type, ImePresenterImpl.TabType id) {
        View candidateParentLayout = getCandidateView(type);
        if (candidateParentLayout == null) return false;
        if (candidateParentLayout.getParent() != null) {
            ((ViewGroup) candidateParentLayout.getParent()).removeAllViews();
        }

        /*switch (type){
            case BOOST_SHARE:
                BaseCandidateView v1 = (BaseCandidateView) candidateParentLayout;
                v1.setItemClickListener(this);
                v1.addCandidateView(this,id);
                break;
            case BOOST_SHARE1:
                BoostShareCandidateView v2 = (BoostShareCandidateView) candidateParentLayout;
                v2.setItemClickListener(this);
                v2.addCandidateView(this,id);
                break;
            case TEXT_LIST:
                TextSuggestionsCandidateView v3 = (TextSuggestionsCandidateView) candidateParentLayout;
                v3.setItemClickListener(this);
                v3.addCandidateView(this,id);
                break;
        }*/
        BaseCandidateView v1 = (BaseCandidateView) candidateParentLayout;
        v1.setItemClickListener(this);
        v1.addCandidateView(this, id);
        return true;
    }

    public View getCandidateView(KeyboardUtils.CandidateType type) {
        if (!mCandidateMaps.containsKey(type)) {
            mCandidateMaps.put(type, type == KeyboardUtils.CandidateType.NULL ? null :
                    LayoutInflater.from(mContext).inflate(KeyboardUtils.CandidateType.getXml(type), this, false));
        }
        return mCandidateMaps.get(type);
    }

    public void setDataToCandidateType(KeyboardUtils.CandidateType type, Bundle bundle) {
        BaseCandidateView candidateParentLayout = (BaseCandidateView) getCandidateView(type);
        if (candidateParentLayout == null) return;
        candidateParentLayout.setCandidateData(bundle);
    }

    @Override
    public void onItemClick(KeywordModel word) {
        onClickListener.onItemClick(word);

    }

    @Override
    public void onKeyboardTabClick(View view) {
        onClickListener.onKeyboardTabClick(view);

    }
}
