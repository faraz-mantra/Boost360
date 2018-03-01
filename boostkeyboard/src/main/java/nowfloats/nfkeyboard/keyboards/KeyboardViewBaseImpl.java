package nowfloats.nfkeyboard.keyboards;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.PopupWindow;

import java.util.HashMap;

import nowfloats.nfkeyboard.interface_contracts.KeyboardViewInterface;
import nowfloats.nfkeyboard.util.KeyboardUtils;

/**
 * Created by Admin on 21-02-2018.
 */

public class KeyboardViewBaseImpl extends KeyboardView implements KeyboardViewInterface {
    private Context mContext;
    private HashMap<KeyboardUtils.KeyboardType, KeyboardBaseImpl> mKeyboardMaps = new HashMap<>(KeyboardUtils.KeyboardType.values().length);
    private PopupWindow popup;

    public KeyboardViewBaseImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    public KeyboardViewBaseImpl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setKeyboard(KeyboardBaseImpl keyboard) {
        super.setKeyboard(keyboard);
    }

    public KeyboardBaseImpl getKeyboard(KeyboardUtils.KeyboardType type){
        // add keyboard mode to get with different key keyboard
        if (!mKeyboardMaps.containsKey(type)){
            mKeyboardMaps.put(type, new KeyboardBaseImpl(mContext,
                    KeyboardUtils.KeyboardType.getXml(type)));
        }
        return mKeyboardMaps.get(type);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }
}
