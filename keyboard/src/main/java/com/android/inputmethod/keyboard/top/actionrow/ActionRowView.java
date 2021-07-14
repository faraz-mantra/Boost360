package com.android.inputmethod.keyboard.top.actionrow;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.inputmethod.keyboard.KeyboardActionListener;
import com.android.inputmethod.keyboard.emojifast.EmojiView;
import com.android.inputmethod.keyboard.emojifast.RecentEmojiPageModel;
import com.android.inputmethod.keyboard.top.ShowSuggestionsEventAnimated;
import com.android.inputmethod.keyboard.top.UpdateActionBarEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.separ.neural.inputmethod.colors.ColorManager;
import io.separ.neural.inputmethod.colors.ColorProfile;
import io.separ.neural.inputmethod.indic.AudioAndHapticFeedbackManager;
import io.separ.neural.inputmethod.indic.Constants;
import io.separ.neural.inputmethod.indic.R;
import io.separ.neural.inputmethod.slash.EventBusExt;

import static io.separ.neural.inputmethod.indic.Constants.NOT_A_COORDINATE;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.DETAILS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PHOTOS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PRODUCTS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.UPDATES;

/**
 * Created by sepehr on 2/24/17.
 */

public class ActionRowView extends ViewPager implements ColorManager.OnColorChange, View.OnTouchListener {
    public static final String[] DEFAULT_SUGGESTED_EMOJI;
    private static final int[] SERVICE_IMAGE_IDS;
    private static final String[] DEFAULT_SERVICES;

    static {
        DEFAULT_SUGGESTED_EMOJI = "\u2764,\ud83d\ude15,\ud83d\ude18,\ud83d\ude22,\ud83d\ude3b,\ud83d\ude0a,\ud83d\ude09,\ud83d\ude0d".split("\\s*,\\s*");
        //SERVICE_IMAGE_IDS = new int[] {R.id.gif_service_action_button, R.id.maps_service_action_button, R.id.google_service_action_button,
        SERVICE_IMAGE_IDS = new int[]{R.id.maps_service_action_button, R.id.google_service_action_button,
                R.id.contacts_service_action_button, R.id.foursquare_service_action_button, R.id.customization_service_action_button, R.id.go_to_emoji};
        //DEFAULT_SERVICES = new String[] {"giphy","maps","google","contacts","foursquare","customization","emoji"};
        // DEFAULT_SERVICES = new String[]{"maps", "google", "contacts", "foursquare", "customization", "emoji"};
        DEFAULT_SERVICES = new String[]{UPDATES.name(), PRODUCTS.name(), PHOTOS.name(), DETAILS.name()};
    }

    private EventBusHandler mEventHandler;
    private ActionRowAdapter adapter;
    private String[] layoutToShow;
    private Listener mListener;
    private HashMap<String, LinearLayout> layouts;
    private KeyboardActionListener keyboardActionListener;
    private TextView tvUpdates;
    private TextView tvProducts;
    private TextView tvPhotos;
    private TextView tvDetails;

    public ActionRowView(Context context) {
        super(context);
        init();
    }

    public ActionRowView(Context context, AttributeSet attrs) {
        super(context, attrs);


        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();
        setLocale(getContext(), locale);
        init();
        this.mEventHandler = new EventBusHandler();
        this.mEventHandler.register();
    }

    public static Context setLocale(Context context, String language) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        final Object tag = v.getTag();
        if (!(tag instanceof Integer)) {
            return false;
        }
        final int code = (Integer) tag;
        keyboardActionListener.onPressKey(
                code, 0 /* repeatCount */, true /* isSinglePointer */);
        // It's important to return false here. Otherwise, {@link #onClick} and touch-down visual
        // feedback stop working.
        return false;
    }

    public void setKeyboardActionListener(KeyboardActionListener keyboardActionListener) {
        this.keyboardActionListener = keyboardActionListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mEventHandler.unregister();
    }

    public void setAdapter(ActionRowAdapter adapter) {
        super.setAdapter(adapter);
    }

    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
    }

    public void onColorChange(ColorProfile newProfile) {
        //setBackgroundColor(newProfile.getSecondary());
        setupLayouts();
        adapter = new ActionRowAdapter();
        setAdapter(adapter);
        invalidate();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ColorManager.addObserverAndCall(this);
    }

    private void init() {
        //setBackgroundColor(Color.parseColor("#eceff1"));
        //layoutToShow = ActionRowSettingsActivity.DEFAULT_LAYOUTS.split("\\s*,\\s*");
        layoutToShow = ActionRowSettingsActivity.SERVCICE_ID.split("\\s*,\\s*");
        ColorManager.addObserverAndCall(this);
    }

    private void setupLayouts() {
        layouts = new HashMap<>();
        layouts.put(ActionRowSettingsActivity.SERVCICE_ID, addServices());
       /* layouts.put(ActionRowSettingsActivity.EMOJI_ID, addEmojis()); //TODO update this on change
        layouts.put(ActionRowSettingsActivity.CLIP_ID, addButtons());*/
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private LinearLayout addButtons() {
        int textColor = ColorManager.getLastProfile().getTextColor();
        LinearLayout layout = (LinearLayout) View.inflate(getContext(), R.layout.clipboard_action_layout, null);
        ImageView selectAll = (ImageView) layout.findViewById(R.id.select);
        selectAll.setColorFilter(textColor);
        selectAll.setSoundEffectsEnabled(false);
        selectAll.setOnClickListener(new C02297());
        selectAll.setBackgroundResource(R.drawable.action_row_bg);
        ImageView cut = (ImageView) layout.findViewById(R.id.cut);
        cut.setColorFilter(textColor);
        cut.setSoundEffectsEnabled(false);
        cut.setOnClickListener(new C02308());
        cut.setBackgroundResource(R.drawable.action_row_bg);
        ImageView copy = (ImageView) layout.findViewById(R.id.copy);
        copy.setColorFilter(textColor);
        copy.setSoundEffectsEnabled(false);
        copy.setOnClickListener(new C02319());
        copy.setBackgroundResource(R.drawable.action_row_bg);
        ImageView paste = (ImageView) layout.findViewById(R.id.paste);
        paste.setColorFilter(textColor);
        paste.setSoundEffectsEnabled(false);
        paste.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mListener.onPaste();
                AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);
            }
        });
        paste.setBackgroundResource(R.drawable.action_row_bg);
        return layout;
    }

    private LinearLayout addServices() {
        //LinearLayout layout = (LinearLayout) View.inflate(getContext(), R.layout.services_action_layout, null);
        LinearLayout layout = (LinearLayout) View.inflate(getContext(), R.layout.boost_share_candidate_view1, null);
        int i = 0;
       /* for(int currentServiceViewId : SERVICE_IMAGE_IDS) {
            ImageView imageView = (ImageView) layout.findViewById(currentServiceViewId);
            imageView.setColorFilter(ColorManager.getLastProfile().getTextColor());
            imageView.setSoundEffectsEnabled(false);
            imageView.setOnClickListener(new serviceClickListener(i));
            imageView.setBackgroundResource(R.drawable.action_row_bg);
            i++;
        }*/
        final ImageView ivBack = layout.findViewById(R.id.img_back);
        ivBack.setTag(Constants.CODE_ALPHA_FROM_EMOJI);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                ivBack.setClickable(false);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, ivBack.getX() + ivBack.getWidth() / 2, ivBack.getY() + ivBack.getHeight() / 2);
                scaleAnimation.setDuration(200);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivBack.setScaleX(1f);
                        ivBack.setScaleX(1f);
                        ivBack.setClickable(true);
                        final Object tag = v.getTag();
                        if (!(tag instanceof Integer)) {
                            return;
                        }
                        final int code = (Integer) tag;
                        keyboardActionListener.onCodeInput(code, NOT_A_COORDINATE, NOT_A_COORDINATE,
                                false /* isKeyRepeat */);
                        keyboardActionListener.onReleaseKey(code, false /* withSliding */);
                        EventBusExt.getDefault().post(new ShowSuggestionsEventAnimated());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                ivBack.startAnimation(scaleAnimation);
            }
        });
        tvUpdates = layout.findViewById(R.id.tv_updates);
        tvUpdates.setText("UPDATES");
        tvUpdates.setOnClickListener(new serviceClickListener(0));
        tvProducts = layout.findViewById(R.id.tv_products);
        tvProducts.setText("INVENTORY");
        tvProducts.setOnClickListener(new serviceClickListener(1));
        tvPhotos = layout.findViewById(R.id.tv_photos);
        tvPhotos.setText("PHOTOS");
        tvPhotos.setOnClickListener(new serviceClickListener(2));
        tvDetails = layout.findViewById(R.id.tv_details);
        tvDetails.setText("DETAILS");
        tvDetails.setOnClickListener(new serviceClickListener(3));
        return layout;
    }

    private LinearLayout addEmojis() {
        LinearLayout emojiLayout = new LinearLayout(getContext());
        emojiLayout.setGravity(17);
        emojiLayout.setWeightSum(1.f);
        fillEmojiLayout(emojiLayout);
        return emojiLayout;
    }

    private void fillEmojiLayout(LinearLayout emojiLayout) {
        String[] emojiArray = RecentEmojiPageModel.toReversePrimitiveArray(RecentEmojiPageModel.getPersistedCache(PreferenceManager.getDefaultSharedPreferences(getContext())));
        //String[] emojiArray = FrequentEmojiHandler.getInstance(getContext()).getMostFrequentEmojis(8).toArray(new String[0]); //TODO use this
        int i = 0;
        for (String emoji : emojiArray) {
            i++;
            if (i > DEFAULT_SUGGESTED_EMOJI.length)
                break;
            emojiLayout.addView(addSingleEmoji(emoji));
        }
        for (int j = 0; i < DEFAULT_SUGGESTED_EMOJI.length; i++, j++)
            emojiLayout.addView(addSingleEmoji(DEFAULT_SUGGESTED_EMOJI[j]));
    }

    private View addSingleEmoji(String emoji) {
        EmojiView view = new EmojiView(getContext(), true);
        view.setEmoji(emoji);
        view.setSoundEffectsEnabled(false);
        view.setAlpha(1.f);
        view.setOnClickListener(new AnonymousClass11(view));
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.f / ((float) DEFAULT_SUGGESTED_EMOJI.length)));
        view.setBackgroundResource(R.drawable.action_row_bg);
        return view;
    }

    public interface Listener {
        void onCopy();

        void onCut();

        void onEmojiClicked(String str, boolean z);

        void onNumberClicked(String str);

        void onPaste();

        void onSelectAll();

        void onServiceClicked(String id);
    }

    /*service click handler*/
    class serviceClickListener implements OnClickListener {
        final int serviceId;

        serviceClickListener(int serviceId) {
            this.serviceId = serviceId;
        }

        public void onClick(View v) {
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);

            findViewById(R.id.tv_updates).setBackgroundResource(v.getId()
                    == R.id.tv_updates ? R.drawable.round_414141 : android.R.color.transparent);
            findViewById(R.id.tv_products).setBackgroundResource(v.getId()
                    == R.id.tv_products ? R.drawable.round_414141 : android.R.color.transparent);
            findViewById(R.id.tv_photos).setBackgroundResource(v.getId()
                    == R.id.tv_photos ? R.drawable.round_414141 : android.R.color.transparent);
            findViewById(R.id.tv_details).setBackgroundResource(v.getId()
                    == R.id.tv_details ? R.drawable.round_414141 : android.R.color.transparent);
            mListener.onServiceClicked(DEFAULT_SERVICES[serviceId]);
        }
    }

    /* onSelectAll */
    class C02297 implements OnClickListener {
        C02297() {
        }

        public void onClick(View v) {
            mListener.onSelectAll();
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);
        }
    }

    /* onCut */
    class C02308 implements OnClickListener {
        C02308() {
        }

        public void onClick(View v) {
            mListener.onCut();
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);
        }
    }

    /* onCopy */
    class C02319 implements OnClickListener {
        C02319() {
        }

        public void onClick(View v) {
            mListener.onCopy();
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);
        }
    }

    private class ActionRowAdapter extends PagerAdapter {
        private ArrayList<View> views;

        private ActionRowAdapter() {
            this.views = new ArrayList();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = this.views.indexOf(object);
            if (index == -1) {
                return -2;
            }
            return index;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            //View v = ActionRowView.this.createViewFromID(ActionRowView.this.layoutToShow[position % ActionRowView.this.layoutToShow.length]);
            View v = layouts.get(ActionRowView.this.layoutToShow[position % ActionRowView.this.layoutToShow.length]);
            this.views.add(v);
            container.addView(v);
            return v;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getCount() {
            return ActionRowView.this.layoutToShow.length;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public View getView(int position) {
            return (View) this.views.get(position);
        }
    }

    public class EventBusHandler {
        public void register() {
            if (!EventBusExt.getDefault().isRegistered(this)) {
                EventBusExt.getDefault().register(this);
            }
        }

        public void unregister() {
            if (EventBusExt.getDefault().isRegistered(this)) {
                EventBusExt.getDefault().unregister(this);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(UpdateActionBarEvent event) {
            if (tvUpdates != null) {
                tvUpdates.setText(R.string.tv_updates);
                tvProducts.setText(R.string.tv_products);
                tvPhotos.setText(R.string.tv_photos);
                tvDetails.setText(R.string.tv_details);

                tvUpdates.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvProducts.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvPhotos.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvDetails.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }

    class AnonymousClass11 implements OnClickListener {
        final EmojiView val$view;

        AnonymousClass11(EmojiView emojiconTextView) {
            this.val$view = emojiconTextView;
        }

        public void onClick(View tview) {
            AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(-15, ActionRowView.this);
            mListener.onEmojiClicked(this.val$view.getEmoji(), false);
        }
    }
}
