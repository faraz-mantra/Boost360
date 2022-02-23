/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.inputmethod.keyboard.internal.AlphabetShiftState;
import com.android.inputmethod.keyboard.internal.KeyDrawParams;
import com.android.inputmethod.keyboard.internal.KeySpecParser;
import com.android.inputmethod.keyboard.internal.KeyVisualAttributes;
import com.android.inputmethod.keyboard.internal.MoreKeySpec;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.android.inputmethod.latin.utils.StringUtils;
import com.android.inputmethod.latin.utils.TypefaceUtils;

import java.util.HashSet;
import java.util.Locale;

import io.separ.neural.inputmethod.indic.Constants;
import io.separ.neural.inputmethod.indic.R;

import static com.android.inputmethod.keyboard.Key.BACKGROUND_TYPE_ACTION;
import static com.android.inputmethod.keyboard.Key.BACKGROUND_TYPE_ENTERKEY;
import static com.android.inputmethod.keyboard.Key.BACKGROUND_TYPE_SPACEBAR;
import static com.android.inputmethod.keyboard.internal.KeyboardIconsSet.ICON_UNDEFINED;
import static io.separ.neural.inputmethod.Utils.ColorUtils.colorProfile;
import static io.separ.neural.inputmethod.indic.Constants.CODE_OUTPUT_TEXT;
import static io.separ.neural.inputmethod.indic.Constants.CODE_UNSPECIFIED;

/**
 * A view that renders a virtual {@link Keyboard}.
 *
 * @attr ref R.styleable#KeyboardView_keyBackground
 * @attr ref R.styleable#KeyboardView_functionalKeyBackground
 * @attr ref R.styleable#KeyboardView_spacebarBackground
 * @attr ref R.styleable#KeyboardView_spacebarIconWidthRatio
 * @attr ref R.styleable#Keyboard_Key_keyLabelFlags
 * @attr ref R.styleable#KeyboardView_keyHintLetterPadding
 * @attr ref R.styleable#KeyboardView_keyPopupHintLetter
 * @attr ref R.styleable#KeyboardView_keyPopupHintLetterPadding
 * @attr ref R.styleable#KeyboardView_keyShiftedLetterHintPadding
 * @attr ref R.styleable#KeyboardView_keyTextShadowRadius
 * @attr ref R.styleable#KeyboardView_verticalCorrection
 * @attr ref R.styleable#Keyboard_Key_keyTypeface
 * @attr ref R.styleable#Keyboard_Key_keyLetterSize
 * @attr ref R.styleable#Keyboard_Key_keyLabelSize
 * @attr ref R.styleable#Keyboard_Key_keyLargeLetterRatio
 * @attr ref R.styleable#Keyboard_Key_keyLargeLabelRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLetterRatio
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelRatio
 * @attr ref R.styleable#Keyboard_Key_keyLabelOffCenterRatio
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelOffCenterRatio
 * @attr ref R.styleable#Keyboard_Key_keyPreviewTextRatio
 * @attr ref R.styleable#Keyboard_Key_keyTextColor
 * @attr ref R.styleable#Keyboard_Key_keyTextColorDisabled
 * @attr ref R.styleable#Keyboard_Key_keyTextShadowColor
 * @attr ref R.styleable#Keyboard_Key_keyHintLetterColor
 * @attr ref R.styleable#Keyboard_Key_keyHintLabelColor
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintInactivatedColor
 * @attr ref R.styleable#Keyboard_Key_keyShiftedLetterHintActivatedColor
 * @attr ref R.styleable#Keyboard_Key_keyPreviewTextColor
 */
public class KeyboardView extends View {
    private static final float KET_TEXT_SHADOW_RADIUS_DISABLED = -1.0f;
    // The maximum key label width in the proportion to the key width.
    private static final float MAX_LABEL_RATIO = 0.90f;
    public final String mKeyPopupHintLetter;
    protected final Drawable mKeyBackground;
    protected final KeyDrawParams mKeyDrawParams = new KeyDrawParams();
    final TypedArray keyboardViewAttr;
    // XML attributes
    private final KeyVisualAttributes mKeyVisualAttributes;
    // Default keyLabelFlags from {@link KeyboardTheme}.
    // Currently only "alignHintLabelToBottom" is supported.
    private final int mDefaultKeyLabelFlags;
    private final float mKeyHintLetterPadding;
    private final float mKeyPopupHintLetterPadding;
    private final float mKeyShiftedLetterHintPadding;
    private final float mKeyTextShadowRadius;
    private final float mVerticalCorrection;
    private final Drawable mFunctionalKeyBackground;
    private final Drawable mSpacebarBackground;
    private final Drawable mEnterKeyBackground;
    private final float mSpacebarIconWidthRatio;
    private final Rect mKeyBackgroundPadding = new Rect();

    // Drawing
    /**
     * The keys that should be drawn
     */
    private final HashSet<Key> mInvalidatedKeys = new HashSet<>();
    /**
     * The working rectangle variable
     */
    private final Rect mClipRect = new Rect();
    /**
     * The canvas for the above mutable keyboard bitmap
     */
    private final Canvas mOffscreenCanvas = new Canvas();
    private final Paint mPaint = new Paint();
    private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
    // Main keyboard
    private Keyboard mKeyboard;
    /**
     * True if all keys should be drawn
     */
    private boolean mInvalidateAllKeys;
    /**
     * The keyboard bitmap buffer for faster updates
     */
    private Bitmap mOffscreenBuffer;

    public KeyboardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public KeyboardView(final Context context, final AttributeSet attrs, final int defStyle) {
        this(context, attrs, defStyle, false);
    }

    public KeyboardView(final Context context, final AttributeSet attrs, final int defStyle, boolean isEmoji) {
        super(context, attrs, defStyle);

        keyboardViewAttr = context.obtainStyledAttributes(attrs,
                R.styleable.KeyboardView, defStyle, R.style.KeyboardView);
        mKeyBackground = keyboardViewAttr.getDrawable(R.styleable.KeyboardView_keyBackground);
        if (!isInEditMode())
            mKeyBackground.getPadding(mKeyBackgroundPadding);
        final Drawable functionalKeyBackground = keyboardViewAttr.getDrawable(
                R.styleable.KeyboardView_functionalKeyBackground);
        mFunctionalKeyBackground = (functionalKeyBackground != null) ? functionalKeyBackground
                : mKeyBackground;
        mEnterKeyBackground = keyboardViewAttr.getDrawable(R.styleable.KeyboardView_enterKeyBackground);
        final Drawable spacebarBackground = keyboardViewAttr.getDrawable(
                R.styleable.KeyboardView_spacebarBackground);
        mSpacebarBackground = (spacebarBackground != null) ? spacebarBackground : mKeyBackground;
        mSpacebarIconWidthRatio = keyboardViewAttr.getFloat(
                R.styleable.KeyboardView_spacebarIconWidthRatio, 1.0f);
        mKeyHintLetterPadding = keyboardViewAttr.getDimension(
                R.styleable.KeyboardView_keyHintLetterPadding, 0.0f);
        mKeyPopupHintLetter = keyboardViewAttr.getString(
                R.styleable.KeyboardView_keyPopupHintLetter);
        mKeyPopupHintLetterPadding = keyboardViewAttr.getDimension(
                R.styleable.KeyboardView_keyPopupHintLetterPadding, 0.0f);
        mKeyShiftedLetterHintPadding = keyboardViewAttr.getDimension(
                R.styleable.KeyboardView_keyShiftedLetterHintPadding, 0.0f);
        mKeyTextShadowRadius = keyboardViewAttr.getFloat(
                R.styleable.KeyboardView_keyTextShadowRadius, KET_TEXT_SHADOW_RADIUS_DISABLED);
        mVerticalCorrection = keyboardViewAttr.getDimension(
                R.styleable.KeyboardView_verticalCorrection, 0.0f);
        keyboardViewAttr.recycle();

        final TypedArray keyAttr = context.obtainStyledAttributes(attrs,
                R.styleable.Keyboard_Key, defStyle, R.style.KeyboardView);
        mDefaultKeyLabelFlags = keyAttr.getInt(R.styleable.Keyboard_Key_keyLabelFlags, 0);
        if (isEmoji)
            mKeyVisualAttributes = KeyVisualAttributes.newInstance(keyAttr, null);
        else
            mKeyVisualAttributes = KeyVisualAttributes.newInstance(keyAttr, null);
        keyAttr.recycle();

        mPaint.setAntiAlias(true);
    }

    private static void blendAlpha(final Paint paint, final int alpha) {
        final int color = paint.getColor();
        paint.setARGB((paint.getAlpha() * alpha) / Constants.Color.ALPHA_OPAQUE,
                Color.red(color), Color.green(color), Color.blue(color));
    }

    public KeyVisualAttributes getKeyVisualAttribute() {
        return mKeyVisualAttributes;
    }

    public void setHardwareAcceleratedDrawingEnabled(final boolean enabled) {
        if (!enabled) return;
        // TODO: Should use LAYER_TYPE_SOFTWARE when hardware acceleration is off?
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public void setKeyboard(final Keyboard keyboard, boolean isEmoji) {
        mKeyboard = keyboard;
        final int keyHeight = keyboard.mMostCommonKeyHeight - keyboard.mVerticalGap;
        mKeyDrawParams.updateParams(keyHeight, mKeyVisualAttributes);
        mKeyDrawParams.updateParams(keyHeight, keyboard.mKeyVisualAttributes);
        mKeyDrawParams.mTypeface = keyboard.mTypeface;
        invalidateAllKeys();
        requestLayout();
    }

    /**
     * Returns the current keyboard being displayed by this view.
     *
     * @return the currently attached keyboard
     * @see #setKeyboard(Keyboard)
     */
    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the
     * view will re-layout itself to accommodate the keyboard.
     *
     * @param keyboard the keyboard to display in this view
     * @see Keyboard
     * @see #getKeyboard()
     */
    public void setKeyboard(final Keyboard keyboard) {
        setKeyboard(keyboard, false);
    }

    protected float getVerticalCorrection() {
        return mVerticalCorrection;
    }

    protected void updateKeyDrawParams(final int keyHeight) {
        mKeyDrawParams.updateParams(keyHeight, mKeyVisualAttributes);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (mKeyboard == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // The main keyboard expands to the entire this {@link KeyboardView}.
        final int width = mKeyboard.mOccupiedWidth + getPaddingLeft() + getPaddingRight();
        final int height = mKeyboard.mOccupiedHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (canvas.isHardwareAccelerated()) {
            onDrawKeyboard(canvas);
            return;
        }

        final boolean bufferNeedsUpdates = mInvalidateAllKeys || !mInvalidatedKeys.isEmpty();
        if (bufferNeedsUpdates || mOffscreenBuffer == null) {
            if (maybeAllocateOffscreenBuffer()) {
                mInvalidateAllKeys = true;
                // TODO: Stop using the offscreen canvas even when in software rendering
                mOffscreenCanvas.setBitmap(mOffscreenBuffer);
            }
            onDrawKeyboard(mOffscreenCanvas);
        }
        canvas.drawBitmap(mOffscreenBuffer, 0.0f, 0.0f, null);
    }

    private boolean maybeAllocateOffscreenBuffer() {
        final int width = getWidth();
        final int height = getHeight();
        if (width == 0 || height == 0) {
            return false;
        }
        if (mOffscreenBuffer != null && mOffscreenBuffer.getWidth() == width
                && mOffscreenBuffer.getHeight() == height) {
            return false;
        }
        freeOffscreenBuffer();
        mOffscreenBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        return true;
    }

    private void freeOffscreenBuffer() {
        mOffscreenCanvas.setBitmap(null);
        mOffscreenCanvas.setMatrix(null);
        if (mOffscreenBuffer != null) {
            mOffscreenBuffer.recycle();
            mOffscreenBuffer = null;
        }
    }

    private void onDrawKeyboard(final Canvas canvas) {
        final Keyboard keyboard = getKeyboard();
        if (keyboard == null) {
            return;
        }

        // Sets color according to package primary color
        /*if (this instanceof MoreKeysKeyboardView) {
            getBackground().setColorFilter(keyboardViewAttr.getColor(R.styleable.KeyboardView_background, Color.parseColor("#212121")), PorterDuff.Mode.MULTIPLY);
        } else {
            //getBackground().setAlpha(0);
            ColorUtils.drawBackground(canvas, keyboardViewAttr.getColor(R.styleable.KeyboardView_background, Color.parseColor("#212121")));
            //ColorUtils.drawBackground(canvas, Color.parseColor("#212121"));
        }*/

        final Paint paint = mPaint;
        final Drawable background = getBackground();
        // Calculate clip region and set.
        final boolean drawAllKeys = mInvalidateAllKeys || mInvalidatedKeys.isEmpty();
        final boolean isHardwareAccelerated = canvas.isHardwareAccelerated();
        // TODO: Confirm if it's really required to draw all keys when hardware acceleration is on.
        if (drawAllKeys || isHardwareAccelerated) {
            if (!isHardwareAccelerated && background != null) {
                // Need to draw keyboard background on {@link #mOffscreenBuffer}.
                canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                background.draw(canvas);
            }
            // Draw all keys.
            for (final Key key : keyboard.getSortedKeys()) {
                onDrawKey(key, canvas, paint);
            }
        } else {
            for (final Key key : mInvalidatedKeys) {
                if (!keyboard.hasKey(key)) {
                    continue;
                }
                if (background != null) {
                    // Need to redraw key's background on {@link #mOffscreenBuffer}.
                    final int x = key.getX() + getPaddingLeft();
                    final int y = key.getY() + getPaddingTop();
                    mClipRect.set(x, y, x + key.getWidth(), y + key.getHeight());
                    canvas.save();
                    canvas.clipRect(mClipRect);
                    canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                    background.draw(canvas);
                    canvas.restore();
                }
                onDrawKey(key, canvas, paint);
            }
        }

        mInvalidatedKeys.clear();
        mInvalidateAllKeys = false;
    }

    private void onDrawKey(final Key key, final Canvas canvas, final Paint paint) {
        final int keyDrawX = key.getDrawX() + getPaddingLeft();
        final int keyDrawY = key.getY() + getPaddingTop();
        canvas.translate(keyDrawX, keyDrawY);

        final int keyHeight = mKeyboard.mMostCommonKeyHeight - mKeyboard.mVerticalGap;
        final KeyVisualAttributes attr = key.getVisualAttributes();
        final KeyDrawParams params = mKeyDrawParams.mayCloneAndUpdateParams(keyHeight, attr);
        params.mAnimAlpha = Constants.Color.ALPHA_OPAQUE;

        if (!key.isSpacer()) {
            if (key.isMoreKey()) {
                Drawable tmp = key.selectBackgroundDrawable(this.mKeyBackground, this.mFunctionalKeyBackground, this.mSpacebarBackground, getResources().getColor(R.color.primaryColor), getResources().getColor(R.color.primaryColorDark));
                onDrawKeyBackground(key, canvas, tmp, 1);
                //Log.e("SEPAR", "here :(");
            } else {
                switch (key.getType()) {
                    case BACKGROUND_TYPE_ENTERKEY:
                        key.setBackgroundState(mEnterKeyBackground);
                        onDrawKeyBackground(key, canvas, key.getEnterKeyBackground(mEnterKeyBackground), ResourceUtils.getFraction(keyboardViewAttr, R.styleable.KeyboardView_heightFractionSpaceEnter, 1));
                        break;
                    case BACKGROUND_TYPE_ACTION:
                        key.setBackgroundState(mFunctionalKeyBackground);
                        onDrawKeyBackground(key, canvas, key.getActionBackground(mFunctionalKeyBackground), 1);
                        break;
                    case BACKGROUND_TYPE_SPACEBAR:
                        key.setBackgroundState(mSpacebarBackground);
                        onDrawKeyBackground(key, canvas, key.getSpaceBarBackground(mSpacebarBackground), ResourceUtils.getFraction(keyboardViewAttr, R.styleable.KeyboardView_heightFractionSpaceEnter, 1));
                        break;
                    default:
                        key.setBackgroundState(mKeyBackground);
                        onDrawKeyBackground(key, canvas, key.getNormalBackground(mKeyBackground, false), 1);
                }
            }
            /*switch (key.getType()) {
                case 1:
                    Drawable drawable = this.mKeyBackground;
                    onDrawKeyBackground(key, canvas, key.getNormalBackground(drawable, true, colorProfile.getSecondary()));
                    break;
                case 2:
                    onDrawKeyBackground(key, canvas, key.getNormalBackground(this.mKeyBackground, true, colorProfile.getSecondary()));
                    break;
                case 3:
                    onDrawKeyBackground(key, canvas, key.getNormalBackground(this.mKeyBackground, true, colorProfile.getSecondary()));
                    onDrawKeyBackground(key, canvas, key.getStickyBackground(this.mKeyBackground, true, colorProfile.getAccent()));
                    break;
                case 4:
                    onDrawKeyBackground(key, canvas, key.getNormalBackground(this.mKeyBackground, true, colorProfile.getSecondary()));
                    onDrawKeyBackground(key, canvas, key.getStickyBackground(this.mKeyBackground, true, colorProfile.getAccent()));
                    break;
                case 5:
                    onDrawKeyBackground(key, canvas, key.getActionBackground(this.mKeyBackground, colorProfile.getSecondary()));
                    break;
                case 6:
                    onDrawKeyBackground(key, canvas, key.getSpaceBarBackground(this.mSpacebarBackground, colorProfile.getPrimary(), PorterDuff.Mode.MULTIPLY));
                    break;
            }*/
        }
        onDrawKeyTopVisuals(key, canvas, paint, params);
        canvas.translate(-keyDrawX, -keyDrawY);
    }

    // Draw key background.
    protected void onDrawKeyBackground(final Key key, final Canvas canvas,
                                       Drawable background, float heightFraction) {
        final int keyWidth = key.getDrawWidth();
        final int keyHeight = key.getHeight();
        final int bgWidth, bgHeight, bgX, bgY;
        // Used for expanding keys to keep background ratio & for Landscape view
        if ((key.needsToKeepBackgroundAspectRatio(mDefaultKeyLabelFlags)
                // HACK: To disable expanding normal/functional key background.
                && !key.hasCustomActionLabel())) {
            final int intrinsicWidth = background.getIntrinsicWidth();
            final int intrinsicHeight = background.getIntrinsicHeight();
            final float minScale = Math.min(
                    keyWidth / (float) intrinsicWidth, keyHeight / (float) intrinsicHeight);
            bgWidth = (int) (intrinsicWidth * minScale);
            bgHeight = (int) (intrinsicHeight * minScale);
            bgX = (keyWidth - bgWidth) / 2;
            bgY = (keyHeight - bgHeight) / 2;
        } else {
            final Rect padding = mKeyBackgroundPadding;
            bgWidth = keyWidth + padding.left + padding.right;
            bgHeight = (int) ((keyHeight + padding.top + padding.bottom) * heightFraction);
            bgY = (int) ((keyHeight + padding.top + padding.bottom) * (1 - heightFraction) / 2);
            bgX = -padding.left;
        }
        final Rect bounds = background.getBounds();
        if (bgWidth != bounds.right || bgHeight != bounds.bottom) {
            background.setBounds(0, 0, bgWidth, bgHeight);
        }
        canvas.translate(bgX, bgY);
        background.draw(canvas);
        canvas.translate(-bgX, -bgY);
    }

    // Draw key top visuals.
    protected void onDrawKeyTopVisuals(final Key key, final Canvas canvas, final Paint paint,
                                       final KeyDrawParams params) {
        final int keyWidth = key.getDrawWidth();
        final int keyHeight = key.getHeight();
        final float centerX = keyWidth * 0.5f;
        final float centerY = keyHeight * 0.5f;

        // Draw key label.
        final Drawable icon = key.getIcon(mKeyboard.mIconsSet, params.mAnimAlpha);
        float labelX = centerX;
        float labelBaseline = centerY;
        String label = key.getLabel();
        if (label != null) {
            paint.setTypeface(Key.selectTypeface(params));
            paint.setTextSize(key.selectTextSize(params));
            final float labelCharHeight = TypefaceUtils.getReferenceCharHeight(paint);
            final float labelCharWidth = TypefaceUtils.getReferenceCharWidth(paint);
            if (key.isFontResizeKey()) {
                paint.setTextSize(paint.getTextSize() * 1.6f);
            }

            // Vertical label text alignment.
            labelBaseline = centerY + labelCharHeight / 2.0f;
            if (key.isFontResizeKey()) {
                labelBaseline = centerY + labelCharHeight / 1.3f;
            }

            // Horizontal label text alignment
            if (key.isAlignLabelOffCenter()) {
                // The label is placed off center of the key. Used mainly on "phone number" layout.
                labelX = centerX + params.mLabelOffCenterRatio * labelCharWidth;
                paint.setTextAlign(Align.LEFT);
            } else {
                labelX = centerX;
                paint.setTextAlign(Align.CENTER);
            }
            if (key.needsAutoXScale()) {
                final float ratio = Math.min(1.0f, (keyWidth * MAX_LABEL_RATIO) /
                        TypefaceUtils.getStringWidth(label, paint));
                if (key.needsAutoScale()) {
                    final float autoSize = paint.getTextSize() * ratio;
                    paint.setTextSize(autoSize);
                } else {
                    paint.setTextScaleX(ratio);
                }
            }

            if (key.isEnabled()) {
                paint.setColor(key.selectTextColor(params));
                //paint.setColor(colorProfile.getText());
                // Set a drop shadow for the text if the shadow radius is positive value.
                if (mKeyTextShadowRadius > 0.0f) {
                    paint.setShadowLayer(mKeyTextShadowRadius, 0.0f, 0.0f, params.mTextShadowColor);
                } else {
                    paint.clearShadowLayer();
                }
            } else {
                // Make label invisible
                paint.setColor(Color.TRANSPARENT);
                paint.clearShadowLayer();
            }
            blendAlpha(paint, params.mAnimAlpha);
           /* Drawable dr = (Drawable) getContext().getResources().getDrawable(R.drawable.round_light_grey);
            dr.setBounds(key.getX(), key.getY(), key.getX() + key.getWidth(), key.getY() + key.getHeight());
            dr.draw(canvas);*/
            if ((key.isHeaderKey() || key.getHeaderKeySpec() != null && key.getHeaderKeySpec() != null) &&
                    PointerTracker.KEYBOARD_TYPED_KEY != null && PointerTracker.KEYBOARD_TYPED_KEY.getLabel() != null &&
                    !PointerTracker.KEYBOARD_TYPED_KEY.isPeriodKey(getContext())) {
                key.setLabelModified(true);
                String[] moreKeys = new String[]{label};
                MoreKeySpec[] mMoreKeys = new MoreKeySpec[moreKeys.length];
                mMoreKeys[0] = new MoreKeySpec(moreKeys[0], false, Locale.getDefault());
                // key.mMoreKeys = mMoreKeys;
                label = PointerTracker.KEYBOARD_TYPED_KEY.getLabel() + (key.getHeaderKeySpec() != null ? key.getHeaderKeySpec() : "");
                String mLabel = label;
                String outputText = key.getOutputText();
                int mCode;
                final int code = KeySpecParser.getCode(label);
                if (code == CODE_UNSPECIFIED && TextUtils.isEmpty(key.getOutputText())
                        && !TextUtils.isEmpty(key.getLabel())) {
                    if (StringUtils.codePointCount(label) == 1) {
                        // Use the first letter of the hint label if shiftedLetterActivated flag is
                        // specified.
                        mCode = mLabel.codePointAt(0);
                    } else {
                        // In some locale and case, the character might be represented by multiple code
                        // points, such as upper case Eszett of German alphabet.
                        outputText = mLabel;
                        mCode = CODE_OUTPUT_TEXT;
                    }
                } else if (code == CODE_UNSPECIFIED && key.getOutputText() != null) {
                    if (StringUtils.codePointCount(key.getOutputText()) == 1) {
                        mCode = key.getOutputText().codePointAt(0);
                        outputText = null;
                    } else {
                        mCode = CODE_OUTPUT_TEXT;
                    }
                } else {
                    mCode = StringUtils.toUpperCaseOfCodeForLocale(code, false, Locale.getDefault());
                }
                key.mOptionalAttributes = Key.OptionalAttributes.newInstance(label, CODE_UNSPECIFIED,
                        ICON_UNDEFINED, 0 /* visualInsetsLeft */, 0 /* visualInsetsRight */);
                key.mCode = mCode;
            } else {
                key.setLabelModified(false);
                String mLabel = label;
                key.mOptionalAttributes = Key.OptionalAttributes.newInstance(label, CODE_UNSPECIFIED,
                        ICON_UNDEFINED, 0 /* visualInsetsLeft */, 0 /* visualInsetsRight */);
            }
            canvas.drawText(label, 0, label.length(), labelX, labelBaseline, paint);
            // Turn off drop shadow and reset x-scale.
            paint.clearShadowLayer();
            paint.setTextScaleX(1.0f);
        }

        // Draw hint label.
        final String hintLabel = key.getHintLabel();
        if (hintLabel != null) {
            paint.setTextSize(key.selectHintTextSize(params));
            paint.setColor(key.selectHintTextColor(params));
            // TODO: Should add a way to specify type face for hint letters
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            blendAlpha(paint, params.mAnimAlpha);
            final float labelCharHeight = TypefaceUtils.getReferenceCharHeight(paint);
            final float labelCharWidth = TypefaceUtils.getReferenceCharWidth(paint);
            final float hintX, hintBaseline;
            if (key.hasHintLabel()) {
                // The hint label is placed just right of the key label. Used mainly on
                // "phone number" layout.
                hintX = labelX + params.mHintLabelOffCenterRatio * labelCharWidth * 0.7f;
                if (key.isAlignHintLabelToBottom(mDefaultKeyLabelFlags)) {
                    hintBaseline = labelBaseline;
                } else {
                    hintBaseline = centerY + labelCharHeight / 2.0f;
                }
                paint.setTextAlign(Align.LEFT);
            } else if (key.hasShiftedLetterHint()) {
                // The hint label is placed at top-right corner of the key. Used mainly on tablet.
                hintX = keyWidth - mKeyShiftedLetterHintPadding - labelCharWidth / 2.0f;
                paint.getFontMetrics(mFontMetrics);
                hintBaseline = -mFontMetrics.top;
                paint.setTextAlign(Align.CENTER);
            } else { // key.hasHintLetter()
                // The hint letter is placed at top-right corner of the key. Used mainly on phone.
                final float hintDigitWidth = TypefaceUtils.getReferenceDigitWidth(paint);
                final float hintLabelWidth = TypefaceUtils.getStringWidth(hintLabel, paint);
                hintX = keyWidth - mKeyHintLetterPadding
                        - Math.max(hintDigitWidth, hintLabelWidth) / 1.3f;
                hintBaseline = -paint.ascent() + 2;
                paint.setTextAlign(Align.CENTER);
            }
            final float adjustmentY = params.mHintLabelVerticalAdjustment * labelCharHeight;
            canvas.drawText(
                    hintLabel, 0, hintLabel.length(), hintX, hintBaseline + adjustmentY, paint);
        }

        // Draw key icon.
        if (label == null && icon != null) {
            final int iconWidth;
            if (key.getCode() == Constants.CODE_SPACE && icon instanceof NinePatchDrawable) {
                iconWidth = (int) (keyWidth * mSpacebarIconWidthRatio);
            } else {
                iconWidth = Math.min(icon.getIntrinsicWidth(), keyWidth);
            }
            final int iconHeight = icon.getIntrinsicHeight();
            final int iconY;
            if (key.isAlignIconToBottom()) {
                iconY = keyHeight - iconHeight;
            } else {
                iconY = (keyHeight - iconHeight) / 2; // Align vertically center.
            }
            final int iconX = (keyWidth - iconWidth) / 2; // Align horizontally center.
            drawIcon(key.getCode(), canvas, icon, iconX, iconY, iconWidth, iconHeight);
        }
        if (key.hasPopupHint() && key.getMoreKeys() != null) {
            drawKeyPopupHint(key, canvas, paint, params);
        }
    }

    // Draw popup hint "..." at the bottom right corner of the key.
    protected void drawKeyPopupHint(final Key key, final Canvas canvas, final Paint paint,
                                    final KeyDrawParams params) {
        if (TextUtils.isEmpty(mKeyPopupHintLetter)) {
            return;
        }
        final int keyWidth = key.getDrawWidth();
        final int keyHeight = key.getHeight();

        paint.setTypeface(params.mTypeface);
        paint.setTextSize(params.mHintLetterSize);
        //paint.setColor(params.mHintLabelColor);
        paint.setColor(colorProfile.getText());
        paint.setTextAlign(Align.CENTER);
        final float hintX = keyWidth - mKeyHintLetterPadding
                - TypefaceUtils.getReferenceCharWidth(paint) / 2.0f;
        final float hintY = keyHeight - mKeyPopupHintLetterPadding;
        canvas.drawText(mKeyPopupHintLetter, hintX, hintY, paint);
    }

    protected void drawIcon(int code, final Canvas canvas, final Drawable icon, final int x,
                            final int y, final int width, final int height) {
        icon.clearColorFilter();
        icon.setColorFilter(null);
        if (code == -1) {
            icon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), AlphabetShiftState.IS_SHIFTED ?
                    R.color.primaryColor : R.color.white), PorterDuff.Mode.SRC_IN));
        } else {
            icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
        //icon.setColorFilter(colorProfile.getIcon(), PorterDuff.Mode.SRC_IN);
        canvas.translate((float) x, (float) y);
        icon.setBounds(0, 0, width, height);
        icon.draw(canvas);
        canvas.translate(-x, -y);
    }

    public Paint newLabelPaint(final Key key) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (key == null) {
            paint.setTypeface(mKeyDrawParams.mTypeface);
            paint.setTextSize(mKeyDrawParams.mLabelSize);
        } else {
            paint.setColor(colorProfile.getText());
            paint.setTypeface(Key.selectTypeface(mKeyDrawParams));
            paint.setTextSize(key.selectTextSize(mKeyDrawParams));
        }
        return paint;
    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient
     * because the keyboard renders the keys to an off-screen buffer and an invalidate() only
     * draws the cached buffer.
     *
     * @see #invalidateKey(Key)
     */
    public void invalidateAllKeys() {
        mInvalidatedKeys.clear();
        mInvalidateAllKeys = true;
        invalidate();
    }

    /**
     * Invalidates a key so that it will be redrawn on the next repaint. Use this method if only
     * one key is changing it's content. Any changes that affect the position or size of the key
     * may not be honored.
     *
     * @param key key in the attached {@link Keyboard}.
     * @see #invalidateAllKeys
     */
    public void invalidateKey(final Key key) {
        if (mInvalidateAllKeys) return;
        if (key == null) return;
        mInvalidatedKeys.add(key);
        final int x = key.getX() + getPaddingLeft();
        final int y = key.getY() + getPaddingTop();
        invalidate(x, y, x + key.getWidth(), y + key.getHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        freeOffscreenBuffer();
    }

    public void deallocateMemory() {
        freeOffscreenBuffer();
    }
}