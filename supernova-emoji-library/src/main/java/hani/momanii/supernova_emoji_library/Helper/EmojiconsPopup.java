/*
 * Copyright 2016 Hani Al Momani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hani.momanii.supernova_emoji_library.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Arrays;
import java.util.List;

import hani.momanii.supernova_emoji_library.R;
import hani.momanii.supernova_emoji_library.emoji.Cars;
import hani.momanii.supernova_emoji_library.emoji.Electr;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;
import hani.momanii.supernova_emoji_library.emoji.Food;
import hani.momanii.supernova_emoji_library.emoji.Nature;
import hani.momanii.supernova_emoji_library.emoji.People;
import hani.momanii.supernova_emoji_library.emoji.Sport;
import hani.momanii.supernova_emoji_library.emoji.Symbols;

import static android.widget.ListPopupWindow.MATCH_PARENT;


/**
 * @author Hani Al Momani (hani.momanii@gmail.com)
 */

public class EmojiconsPopup extends PopupWindow implements ViewPager.OnPageChangeListener, EmojiconRecents {
    public EmojiconGridView.OnEmojiconClickedListener onEmojiconClickedListener;
    OnEmojiconBackspaceClickedListener onEmojiconBackspaceClickedListener;
    OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    Context mContext;
    boolean mUseSystemDefault = false;
    View view;
    int positionPager = 0;
    boolean setColor = false;
    String iconPressedColor = "#495C66";
    String tabsColor = "#DCE1E2";
    String backgroundColor = "#E6EBEF";
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiTabs;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    private Boolean isOpened = false;
    private ViewPager emojisPager;

    /**
     * Constructor
     *
     * @param mContext         The context of current activity.
     * @param useSystemDefault .
     * @param iconPressedColor .
     * @param tabsColor        .
     * @param backgroundColor  .
     */
    public EmojiconsPopup(Context mContext, boolean useSystemDefault, String iconPressedColor, String tabsColor, String backgroundColor) {
        super(mContext);
        this.setColor = true;
        this.backgroundColor = backgroundColor;
        this.iconPressedColor = iconPressedColor;
        this.tabsColor = tabsColor;
        this.mUseSystemDefault = useSystemDefault;
        this.mContext = mContext;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(LayoutParams.MATCH_PARENT, 255);
        setBackgroundDrawable(null);


    }

    /**
     * Constructor
     *
     * @param mContext         The context of current activity.
     * @param useSystemDefault .
     */
    public EmojiconsPopup(Context mContext, boolean useSystemDefault) {
        super(mContext);
        this.mUseSystemDefault = useSystemDefault;
        this.mContext = mContext;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize(LayoutParams.MATCH_PARENT, 255);
        setBackgroundDrawable(null);

    }

    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener) {
        this.onSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    public void setOnEmojiconClickedListener(EmojiconGridView.OnEmojiconClickedListener listener) {
        this.onEmojiconClickedListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener) {
        this.onEmojiconBackspaceClickedListener = listener;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     *
     */


    /**
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen() {
        return isOpened;
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        EmojiconRecentsManager
                .getInstance(mContext).saveRecents();
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void setSizeForSoftKeyboard() {
        keyBoardHeight = 263;
        setSize(MATCH_PARENT, keyBoardHeight);
    }

    /**
     * Manually set the popup window size
     *
     * @param width  Width of the popup
     * @param height Height of the popup
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void updateUseSystemDefault(boolean mUseSystemDefault) {
        if (view != null) {
            mEmojisAdapter = null;
            positionPager = emojisPager.getCurrentItem();
            dismiss();

            this.mUseSystemDefault = mUseSystemDefault;
            setContentView(createCustomView());
            //mEmojisAdapter.notifyDataSetChanged();
            mEmojiTabs[positionPager].setSelected(true);
            emojisPager.setCurrentItem(positionPager);
            onPageSelected(positionPager);

        }
    }


    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.emojicons, null, false);
        emojisPager = (ViewPager) view.findViewById(R.id.emojis_pager);
        LinearLayout tabs = (LinearLayout) view.findViewById(R.id.emojis_tab);

        emojisPager.setOnPageChangeListener(this);
        EmojiconRecents recents = this;
        mEmojisAdapter = new EmojisPagerAdapter(
                Arrays.asList(
                        new EmojiconRecentsGridView(mContext, null, null, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, People.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Nature.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Food.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Sport.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Cars.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Electr.DATA, recents, onEmojiconClickedListener, mUseSystemDefault),
                        new EmojiconGridView(mContext, Symbols.DATA, recents, onEmojiconClickedListener, mUseSystemDefault)

                )
        );
        emojisPager.setAdapter(mEmojisAdapter);
        mEmojiTabs = new View[8];

        mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_recents);
        mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_people);
        mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_nature);
        mEmojiTabs[3] = view.findViewById(R.id.emojis_tab_3_food);
        mEmojiTabs[4] = view.findViewById(R.id.emojis_tab_4_sport);
        mEmojiTabs[5] = view.findViewById(R.id.emojis_tab_5_cars);
        mEmojiTabs[6] = view.findViewById(R.id.emojis_tab_6_elec);
        mEmojiTabs[7] = view.findViewById(R.id.emojis_tab_7_sym);
        for (int i = 0; i < mEmojiTabs.length; i++) {
            final int position = i;
            mEmojiTabs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojisPager.setCurrentItem(position);
                }
            });
        }


        emojisPager.setBackgroundColor(Color.parseColor(backgroundColor));
        tabs.setBackgroundColor(Color.parseColor(tabsColor));
        for (int x = 0; x < mEmojiTabs.length; x++) {
            ImageButton btn = (ImageButton) mEmojiTabs[x];
            btn.setColorFilter(Color.parseColor(iconPressedColor));
        }

        ImageButton imgBtn = (ImageButton) view.findViewById(R.id.emojis_backspace);
        imgBtn.setColorFilter(Color.parseColor(iconPressedColor));
        imgBtn.setBackgroundColor(Color.parseColor(backgroundColor));


        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(500, 50, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onEmojiconBackspaceClickedListener != null)
                    onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
            }
        }));

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view.getContext());
        int page = mRecentsManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager.size() == 0) {
            page = 1;
        }

        if (page == 0) {
            onPageSelected(page);
        } else {
            emojisPager.setCurrentItem(page, false);
        }
        return view;
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsGridView fragment = ((EmojisPagerAdapter) emojisPager.getAdapter()).getRecentFragment();
        fragment.addRecentEmoji(context, emojicon);
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
                }
                mEmojiTabs[i].setSelected(true);
                mEmojiTabLastSelectedIndex = i;
                mRecentsManager.setRecentPage(i);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    public interface OnSoftKeyboardOpenCloseListener {
        void onKeyboardOpen(int keyBoardHeight);

        void onKeyboardClose();
    }

    public static class EmojisPagerAdapter extends PagerAdapter {
        private List<EmojiconGridView> views;

        public EmojisPagerAdapter(List<EmojiconGridView> views) {
            super();
            this.views = views;
        }

        public EmojiconRecentsGridView getRecentFragment() {
            for (EmojiconGridView it : views) {
                if (it instanceof EmojiconRecentsGridView)
                    return (EmojiconRecentsGridView) it;
            }
            return null;
        }

        @Override
        public int getCount() {
            return views.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position).rootView;
            ((ViewPager) container).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            ((ViewPager) container).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object key) {
            return key == view;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private final int normalInterval;
        private final OnClickListener clickListener;
        private Handler handler = new Handler();
        private int initialInterval;
        private View downView;
        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }


}