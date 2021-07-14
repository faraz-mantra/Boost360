package nfkeyboard.keyboards;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Build;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.separ.neural.inputmethod.indic.R;

import static android.view.Gravity.NO_GRAVITY;

/**
 * Created by Admin on 26-02-2018.
 */

public class PopUpView {
    private Context mContext;
    private PopupWindow mPopUpWindow;
    private TextView mTextView;

    PopUpView(Context context) {
        mContext = context;
        mPopUpWindow = new PopupWindow(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_key_pop_up, null);
        mPopUpWindow.setContentView(layout);
        mTextView = layout.findViewById(R.id.text2);
        setPopUpWindow();
    }

    public void showAtLocation(Keyboard.Key key, View parent) {
        if (mPopUpWindow.isShowing()) {
            mPopUpWindow.dismiss();
        }
        mTextView.setWidth(key.width);
        mTextView.setHeight(key.height);
        mTextView.setText(key.label);
        mPopUpWindow.setClippingEnabled(false);
        mPopUpWindow.showAtLocation(parent, NO_GRAVITY, key.x, key.y);
    }

    private void setPopUpWindow() {

        try {
            mPopUpWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopUpWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopUpWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.key_pop_up_bg));
            mPopUpWindow.setOutsideTouchable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPopUpWindow.setElevation(2f);
            }
            //popup.setAnimationStyle(R.anim.slide_out_up1);
            mPopUpWindow.setFocusable(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRelease() {
        if (mPopUpWindow.isShowing()) {
            mPopUpWindow.dismiss();
        }
    }
}
