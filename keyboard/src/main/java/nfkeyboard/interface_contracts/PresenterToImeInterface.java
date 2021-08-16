package nfkeyboard.interface_contracts;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;

/**
 * Created by Admin on 26-02-2018.
 */

public interface PresenterToImeInterface {
    InputConnection getImeCurrentInputConnection();

    InputBinding getImeCurrentInputBinding();

    EditorInfo getImeCurrentEditorInfo();

    Dialog getWindow();

    void setCandidatesViewShown(boolean p);
}
