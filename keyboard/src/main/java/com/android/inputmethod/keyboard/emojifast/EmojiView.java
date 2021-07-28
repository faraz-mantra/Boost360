package com.android.inputmethod.keyboard.emojifast;

/**
 * Created by sepehr on 2/1/17.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.keyboard.emoji.EmojiconHandler;

import io.separ.neural.inputmethod.indic.R;

public class EmojiView extends View implements Drawable.Callback {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private String emoji;
    private Drawable drawable;
    private boolean scaleDown = false;

    public EmojiView(Context context, boolean scaleDown) {
        this(context, null);
        this.scaleDown = scaleDown;
    }

    public EmojiView(Context context) {
        this(context, null);
    }

    public EmojiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
        SpannableStringBuilder builder = new SpannableStringBuilder(emoji);
        int resourceId = EmojiconHandler.addEmojis(getContext(), builder, 20, DynamicDrawableSpan.ALIGN_BOTTOM, 20);
        if (resourceId > 0) {
            this.drawable = getResources().getDrawable(resourceId);
        } else {
            this.drawable = getResources().getDrawable(R.drawable.ic_apps_black_24dp);
        }
//        this.drawable = EmojiProvider.getInstance(getContext())
//                .getEmojiDrawable(emoji);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawable != null) {
            if (!scaleDown)
                drawable.setBounds(getPaddingLeft(),
                        getPaddingTop(),
                        getWidth() - getPaddingRight(),
                        getHeight() - getPaddingBottom());
            else {
                final int width = getWidth();
                final int height = getHeight();
                if (width > height)
                    drawable.setBounds((width - height) / 2 + height * 15 / 100, height * 15 / 100, (width - height) / 2 + height * 85 / 100, height * 85 / 100);
                else
                    drawable.setBounds(width * 15 / 100, (height - width) / 2 + width * 15 / 100, width * 85 / 100, (height - width) / 2 + width * 85 / 100);
            }
            drawable.setCallback(this);
            drawable.draw(canvas);
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
