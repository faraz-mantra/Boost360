package com.nowfloats.managenotification;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.thinksity.R;

public class ExpandableCardView extends LinearLayout {

    public static final int DEFAULT_ANIM_DURATION = 350;
    private final static int COLLAPSING = 0;
    private final static int EXPANDING = 1;
    private String title, message;
    private View innerView;
    private ViewGroup containerView;
    private ImageButton arrowBtn;
    private ImageView headerIcon;
    private TextView textViewTitle, tvMsg;
    private TypedArray typedArray;
    private int innerViewRes, textColor, backgroundColor;
    private Drawable iconDrawable;
    private CardView card;
    private Space space;
    private long animDuration = DEFAULT_ANIM_DURATION;
    private boolean isExpanded = false;
    private boolean isExpanding = false;
    private boolean isCollapsing = false;
    private boolean expandOnClick = false;

    private int previousHeight = 0, initHeight = 0, totalHeight = 0;

    private OnExpandedListener listener;
    private Context mContext;
    private LinearLayout llHeader;


    private OnClickListener defaultClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isExpanded()) collapse();
            else expand();
        }
    };
    private boolean isInitCollapse = false;

    public ExpandableCardView(Context context) {
        super(context);
    }


    public ExpandableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttributes(context, attrs);
        initView(context);
    }

    public ExpandableCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttributes(context, attrs);
        initView(context);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixels(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private void initView(Context context) {
        //Inflating View
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_cardview, this);
        llHeader = findViewById(R.id.llHeader);
        card = findViewById(R.id.card);
        space = findViewById(R.id.spView);
        llHeader.post(new Runnable() {
            public void run() {
                previousHeight = initHeight = llHeader.getHeight() + 20;
                card.post(new Runnable() {
                    public void run() {
                        totalHeight = card.getHeight();
                        if (isInitCollapse)
                            collapse();
                    }
                });
            }
        });

    }

    public void setInitCollapse(boolean isInitCollapse) {
        this.isInitCollapse = isInitCollapse;
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        //Ottengo attributi
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView);
        title = typedArray.getString(R.styleable.ExpandableCardView_title);
        backgroundColor = typedArray.getInteger(R.styleable.ExpandableCardView_backgroundColor_custome, View.NO_ID);
        textColor = typedArray.getInteger(R.styleable.ExpandableCardView_cardTextColor, View.NO_ID);
        message = typedArray.getString(R.styleable.ExpandableCardView_message);
        iconDrawable = typedArray.getDrawable(R.styleable.ExpandableCardView_icon);
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableCardView_inner_view, View.NO_ID);
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableCardView_expandOnClick, false);
        animDuration = typedArray.getInteger(R.styleable.ExpandableCardView_animationDuration, DEFAULT_ANIM_DURATION);
        typedArray.recycle();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        arrowBtn = findViewById(R.id.arrow);
        textViewTitle = findViewById(R.id.title);
        tvMsg = findViewById(R.id.tvMsg);
        headerIcon = findViewById(R.id.icon);

        //Setting attributes
        if (!TextUtils.isEmpty(title)) textViewTitle.setText(title);

        //Setting attributes
        if (!TextUtils.isEmpty(message)) {
            tvMsg.setText(message);
            tvMsg.setVisibility(View.VISIBLE);
            space.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.GONE);
            space.setVisibility(View.VISIBLE);
        }

        if (iconDrawable != null) {
            headerIcon.setVisibility(VISIBLE);
            headerIcon.setImageDrawable(iconDrawable);
        }

        card = findViewById(R.id.card);


        if (textColor != -1) {
            textViewTitle.setTextColor(textColor);
        }

        if (backgroundColor != -1) {
            textViewTitle.setTextSize(convertDpToPixels(getContext(), 6));
            card.setBackgroundColor(backgroundColor);
            findViewById(R.id.vwDivider).setVisibility(View.GONE);
            card.setRadius(convertDpToPixels(getContext(), 5));
            setElevation(convertDpToPixels(getContext(), 5));
        } else {
            setElevation(convertDpToPixels(getContext(), 20));
        }

        setInnerView(innerViewRes);

        containerView = findViewById(R.id.viewContainer);


        if (expandOnClick) {
//            card.setOnClickListener(defaultClickListener);
            arrowBtn.setOnClickListener(defaultClickListener);
            llHeader.setOnClickListener(defaultClickListener);
        }

    }

    public void expand() {

        final int initialHeight = initHeight;//(int) convertDpToPixels(mContext, 100);

        if (!isMoving()) {
            previousHeight = initialHeight;
        }

        card.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = totalHeight;//(int) convertDpToPixels(mContext, 300);

        isExpanded = true;
        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING);
        }
    }

    public void collapse() {

        int initialHeight = card.getMeasuredHeight();

        isExpanded = false;
        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING);
        }

    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void animateViews(final int initialHeight, final int distance, final int animationType) {

        Animation expandAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 0) {
                    //Setting isExpanding/isCollapsing to false
                    isExpanding = false;
                    isCollapsing = false;

                    if (listener != null) {
                        if (animationType == EXPANDING) {
                            listener.onExpandChanged(getId(), card, true);
                        } else {
                            listener.onExpandChanged(getId(), card, false);
                        }
                    }
                }

                card.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));
                card.findViewById(R.id.viewContainer).requestLayout();

                containerView.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight - (distance * interpolatedTime));

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        RotateAnimation arrowAnimation = animationType == EXPANDING ?
                new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);


        arrowAnimation.setDuration(animDuration);
        expandAnimation.setDuration(animDuration);

        isExpanding = animationType == EXPANDING;
        isCollapsing = animationType == COLLAPSING;

        startAnimation(expandAnimation);
        Log.d("SO", "Started animation: " + (animationType == EXPANDING ? "Expanding" : "Collapsing"));
        arrowBtn.startAnimation(arrowAnimation);
        isExpanded = animationType == EXPANDING;

    }

    private boolean isExpanding() {
        return isExpanding;
    }

    private boolean isCollapsing() {
        return isCollapsing;
    }

    private boolean isMoving() {
        return isExpanding() || isCollapsing();
    }

    public void setOnExpandedListener(OnExpandedListener listener) {
        this.listener = listener;
    }

    public void removeOnExpandedListener() {
        this.listener = null;
    }

    public void setTitle(String title) {
        if (textViewTitle != null) textViewTitle.setText(title);
    }

    public void setTitle(int resId) {
        if (textViewTitle != null) textViewTitle.setText(resId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setIcon(Drawable drawable) {
//        if (headerIcon != null) {
//            headerIcon.setBackground(drawable);
        iconDrawable = drawable;
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setIcon(int resId) {
//        if (headerIcon != null) {
        iconDrawable = ContextCompat.getDrawable(getContext(), resId);
//            headerIcon.setBackground(iconDrawable);
//        }
    }

    private void setInnerView(int resId) {
        ViewStub stub = findViewById(R.id.viewStub);
        stub.setLayoutResource(resId);
        innerView = stub.inflate();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (arrowBtn != null) arrowBtn.setOnClickListener(l);
        if (llHeader != null) llHeader.setOnClickListener(l);
        super.setOnClickListener(l);
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
    }

    public void setMaxHeight(int maxHeight) {
        card.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (card.getMeasuredHeight() > maxHeight)
            card.getLayoutParams().height = maxHeight;
    }

    /**
     * Interfaces
     */

    public interface OnExpandedListener {

        void onExpandChanged(int id, View v, boolean isExpanded);

    }

}



