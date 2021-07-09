package nfkeyboard.interface_contracts;

import android.view.View;
import android.view.inputmethod.EditorInfo;

/**
 * Created by Admin on 26-02-2018.
 */

public interface ImeToPresenterInterface {

    View getCandidateView();
    View onCreateInputView();
    void onStartInputView(EditorInfo attribute, boolean restarting);
}
