package nowfloats.nfkeyboard.keyboards;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;

import nowfloats.nfkeyboard.interface_contracts.PresenterToImeInterface;

/**
 * Created by Admin on 20-02-2018.
 */

public class ImeKeyboardService extends InputMethodService implements PresenterToImeInterface {

    private ImePresenterImpl mPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        mPresenter = new ImePresenterImpl(this,this);
    }

    @Override
    public View onCreateInputView() {
        return mPresenter.onCreateInputView();
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mPresenter.onStartInput(attribute,restarting);
    }

    @Override
    public void setCandidatesViewShown(boolean shown) {
        super.setCandidatesViewShown(shown);
    }

    @Override
    public void setCandidatesView(View view) {
        if (view.getParent() != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        super.setCandidatesView(view);
    }
    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }
    @Override
    public View onCreateCandidatesView() {
        return mPresenter.getCandidateView();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        mPresenter.setCurrentKeyboard();
        //mPresenter.setSuggestions(KeyboardUtils.CandidateType.TEXT_LIST,modelArrayList);
        //setExtractViewShown(true);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mPresenter.onKeyLongPress(keyCode, event);
    }

    @Override
    public InputConnection getImeCurrentInputConnection() {
        return getCurrentInputConnection();
    }

    @Override
    public InputBinding getImeCurrentInputBinding() {
        return getCurrentInputBinding();
    }

    @Override
    public EditorInfo getImeCurrentEditorInfo() {
        return getCurrentInputEditorInfo();
    }

//    @Override
//    public void onUpdateExtractingVisibility(EditorInfo ei) {
//        ei.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN;
//    }

}
