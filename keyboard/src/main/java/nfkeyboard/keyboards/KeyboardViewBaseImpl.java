package nfkeyboard.keyboards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;

import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;

import android.util.AttributeSet;
import android.widget.PopupWindow;

import java.util.HashMap;
import java.util.List;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.KeyboardViewInterface;
import nfkeyboard.util.KeyboardUtils;

import static nfkeyboard.keyboards.ImePresenterImpl.KEY_EMOJI;
import static nfkeyboard.util.KeyboardUtils.KeyboardType.NUMBERS;

/**
 * Created by Admin on 21-02-2018.
 */

public class KeyboardViewBaseImpl extends KeyboardView implements KeyboardViewInterface {
    private Context mContext;
    private HashMap<KeyboardUtils.KeyboardType, KeyboardBaseImpl> mKeyboardMaps = new HashMap<>(KeyboardUtils.KeyboardType.values().length);
    private PopupWindow popup;
    private KeyboardBaseImpl mCurrentKeyboard;
    private boolean isShifted;
    private KeyboardUtils.KeyboardType mKeyboardTypeCurrent;

    public KeyboardViewBaseImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public KeyboardViewBaseImpl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        Drawable dr;
        if (mKeyboardTypeCurrent != NUMBERS) {
            for (Keyboard.Key key : keys) {
                switch (key.codes[0]) {
                    case Keyboard.KEYCODE_DELETE:
                        dr = (Drawable) mContext.getResources().getDrawable(R.drawable.round_light_grey);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_backspace_arrow);

                        key.icon.setBounds(key.x + key.width / 4, key.y + key.height / 4, key.x + 3 * key.width / 4, key.y + 3 * key.height / 4);
                        key.icon.draw(canvas);
                        break;
                    case -1:
                        dr = (Drawable) mContext.getResources().getDrawable(R.drawable.round_light_grey);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_arrow_up);
                        key.icon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, isShifted ? R.color.primaryColor : R.color.white), PorterDuff.Mode.SRC_IN));
                        key.icon.setBounds(key.x + key.width / 4, key.y + key.height / 4, key.x + 3 * key.width / 4, key.y + 3 * key.height / 4);
                        key.icon.draw(canvas);
                        break;
                    case -2001:
                    case -2003:
                    case -2002:
                        dr = (Drawable) mContext.getResources().getDrawable(R.drawable.round_light_grey);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        Paint paintBackground = new Paint();
                        paintBackground.setTextAlign(Paint.Align.CENTER);
                        paintBackground.setColor(Color.WHITE);
                        paintBackground.setTypeface(Typeface.create("Arial", Typeface.BOLD));
                        paintBackground.setTextSize(45);
                        canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                                key.y + (int) (key.height / 1.5), paintBackground);
                        break;
                    case KEY_EMOJI:
                        dr = (Drawable) mContext.getResources().getDrawable(R.drawable.round_light_grey);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        key.icon = AppCompatResources.getDrawable(mContext, R.drawable.emoji_happiness);
                        key.icon.setBounds(key.x + key.width / 8, key.y + key.height / 4, key.x + 7 * key.width / 8, key.y + 3 * key.height / 4);
                        key.icon.draw(canvas);
                        break;
                    case -2006:
                        dr = (Drawable) mContext.getResources().getDrawable(R.drawable.round_light_grey);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
//                        key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_enter_arrow);
                        key.icon.setBounds(key.x + key.width / 4, key.y + key.height / 4, key.x + 3 * key.width / 4, key.y + 3 * key.height / 4);
                        key.icon.draw(canvas);
                        break;
                }
            }
        }
    }

    public void setKeyboard(KeyboardBaseImpl keyboard) {
        mCurrentKeyboard = keyboard;
        super.setKeyboard(keyboard);
    }

    public KeyboardBaseImpl getKeyboard(KeyboardUtils.KeyboardType type) {
        this.mKeyboardTypeCurrent = type;
        // add keyboard mode to get with different key keyboard
        if (!mKeyboardMaps.containsKey(type)) {
            mKeyboardMaps.put(type, new KeyboardBaseImpl(mContext,
                    KeyboardUtils.KeyboardType.getXml(type)));
        }
        return mKeyboardMaps.get(type);
    }

    @Override
    public boolean setShifted(boolean bol) {
        this.isShifted = bol;
        if (mCurrentKeyboard.getShiftKeyIndex() != -1) {
            Keyboard.Key key = mCurrentKeyboard.getKeys().get(mCurrentKeyboard.getShiftKeyIndex());
            key.icon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, bol ? R.color.primaryColor : R.color.white), PorterDuff.Mode.SRC_IN));
        }

        return super.setShifted(bol);
    }

    public void setCurrentKeyBoardType(KeyboardUtils.KeyboardType mKeyboardTypeCurrent) {
        this.mKeyboardTypeCurrent = mKeyboardTypeCurrent;
    }
}

