package nfkeyboard.keyboards;

import android.content.Context;
import android.inputmethodservice.Keyboard;

import nowfloats.nfkeyboard.interface_contracts.KeyboardInterface;

/**
 * Created by Admin on 21-02-2018.
 */

public class KeyboardBaseImpl extends Keyboard implements KeyboardInterface {
    public KeyboardBaseImpl(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public KeyboardBaseImpl(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }


}
