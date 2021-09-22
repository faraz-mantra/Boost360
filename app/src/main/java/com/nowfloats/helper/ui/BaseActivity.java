package com.nowfloats.helper.ui;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.dispatchTouchEvent(ev);
    }
}
