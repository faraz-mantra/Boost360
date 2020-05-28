package com.nowfloats.util;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by admin on 7/18/2017.
 */

public class ExpandableTextView extends androidx.appcompat.widget.AppCompatTextView {


    private static final int DEFAULT_TRIM_LENGTH = 70;
    private static final String ELLIPSIS = " <html><body><font color='#ffb900'>View More...</font></body></html>";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private TextView.BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = DEFAULT_TRIM_LENGTH;
//        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }

    private void setText() {
        super.setText(Html.fromHtml(getDisplayableText().toString()), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (originalText != null && originalText.length() > trimLength) {
            return new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
        } else {
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }

    ///////////for applying font style
    private void init() {
    }
}
