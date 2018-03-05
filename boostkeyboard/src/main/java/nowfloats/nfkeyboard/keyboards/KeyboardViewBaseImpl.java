package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.graphics.PorterDuff;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.PopupWindow;

import java.util.HashMap;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.KeyboardViewInterface;
import nowfloats.nfkeyboard.util.KeyboardUtils;

/**
 * Created by Admin on 21-02-2018.
 */

public class KeyboardViewBaseImpl extends KeyboardView implements KeyboardViewInterface {
    private Context mContext;
    private HashMap<KeyboardUtils.KeyboardType, KeyboardBaseImpl> mKeyboardMaps = new HashMap<>(KeyboardUtils.KeyboardType.values().length);
    private PopupWindow popup;
    private KeyboardBaseImpl mCurrentKeyboard;

    public KeyboardViewBaseImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public KeyboardViewBaseImpl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setKeyboard(KeyboardBaseImpl keyboard) {
        mCurrentKeyboard = keyboard;
        super.setKeyboard(keyboard);
    }

    public KeyboardBaseImpl getKeyboard(KeyboardUtils.KeyboardType type) {
        // add keyboard mode to get with different key keyboard
        if (!mKeyboardMaps.containsKey(type)) {
            mKeyboardMaps.put(type, new KeyboardBaseImpl(mContext,
                    KeyboardUtils.KeyboardType.getXml(type)));
        }
        return mKeyboardMaps.get(type);
    }

    @Override
    public boolean setShifted(boolean bol) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP  && mCurrentKeyboard.getShiftKeyIndex() != -1) {
            Keyboard.Key key = mCurrentKeyboard.getKeys().get(mCurrentKeyboard.getShiftKeyIndex());
            key.icon.setColorFilter(ContextCompat.getColor(mContext, bol ? R.color.yellow : R.color.white), PorterDuff.Mode.SRC_IN);
        }
        return super.setShifted(bol);
    }
}

