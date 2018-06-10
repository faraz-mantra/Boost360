package nfkeyboard.keyboards;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconRecents;
import hani.momanii.supernova_emoji_library.Helper.EmojiconRecentsGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconRecentsManager;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import hani.momanii.supernova_emoji_library.emoji.Cars;
import hani.momanii.supernova_emoji_library.emoji.Electr;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;
import hani.momanii.supernova_emoji_library.emoji.Food;
import hani.momanii.supernova_emoji_library.emoji.Nature;
import hani.momanii.supernova_emoji_library.emoji.People;
import hani.momanii.supernova_emoji_library.emoji.Sport;
import hani.momanii.supernova_emoji_library.emoji.Symbols;
import nfkeyboard.activity.SpeechRecognitionManager;
import nfkeyboard.adapter.BaseAdapterManager;
import nfkeyboard.adapter.MainAdapter;
import nfkeyboard.interface_contracts.ApiCallToKeyboardViewInterface;
import nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.interface_contracts.SpeechRecognitionResultInterface;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.network.ApiCallPresenter;
import nfkeyboard.util.SharedPrefUtil;
import nowfloats.nfkeyboard.R;

/**
 * Created by Admin on 27-02-2018.
 */

public class ManageKeyboardView extends FrameLayout implements ItemClickListener, SpeechRecognitionResultInterface, ApiCallToKeyboardViewInterface {
    private KeyboardViewBaseImpl mKeyboardView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private MainAdapter shareAdapter;
    private SpeechRecognitionManager mSpeechRecognitionManager;
    private CandidateToPresenterInterface presenterListener;
    private TextView mSpeechMessageTv;
    private ConstraintLayout speechLayout, shareLayout;
    private RelativeLayout emojiLayout;
    private PagerAdapter mEmojisAdapter;
    private ApiCallPresenter apiCallPresenter;
    private View[] mEmojiTabs;
    private ArrayList<AllSuggestionModel> updatesList = new ArrayList<>(), productList = new ArrayList<>();
    private EmojiconRecentsManager mRecentsManager;
    private int mEmojiTabLastSelectedIndex = -1;
    boolean isProductCompleted, isUpdatesCompleted;

    String iconPressedColor = "#ffffff";
    String tabsColor = "#212121";
    String backgroundColor = "#212121";
    private ViewPager emojisPager;

    public ManageKeyboardView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public ManageKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ManageKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setPresenterListener(CandidateToPresenterInterface listener) {
        presenterListener = listener;
        init();
    }

    private void init() {
        speechLayout = findViewById(R.id.speech_layout);
        shareLayout = findViewById(R.id.sharelayout);
        emojiLayout = findViewById(R.id.emoji_layout);
        mSpeechMessageTv = findViewById(R.id.tv_message);
        apiCallPresenter = new ApiCallPresenter(mContext, this);
    }

    public void clearResources() {
        updatesList.clear();
        productList.clear();
        isProductCompleted = isUpdatesCompleted = false;
        if (shareAdapter != null)
            shareAdapter.setSuggestionModels(null);
    }

    public void showShareLayout(final ImePresenterImpl.TabType type) {
        mKeyboardView.setVisibility(INVISIBLE);
        emojiLayout.setVisibility(GONE);
        stopListening();
        shareLayout.setVisibility(VISIBLE);
        if (mRecyclerView == null) {
            mRecyclerView = shareLayout.findViewById(R.id.rv_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            shareAdapter = new MainAdapter(mContext, this);
            mRecyclerView.setAdapter(shareAdapter);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (apiCallPresenter == null) {
                        Toast.makeText(mContext, "Please reopen this keyboard", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (lastVisibleItem >= totalItemCount - 2) {
                        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn()) {
                            shareAdapter.setLoginScreen(createSuggestionModel("Login", BaseAdapterManager.SectionTypeEnum.Login));
                        } else {
                            callLoadingApi(presenterListener.getTabType());
                        }
                    }
                }
            });
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
        }

        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn()) {
            shareAdapter.setLoginScreen(createSuggestionModel("Login", BaseAdapterManager.SectionTypeEnum.Login));
        } else {
            switch (type) {
                case UPDATES:
                    if (updatesList.size() > 0) {
                        shareAdapter.setSuggestionModels(updatesList);
                    } else {
                        callLoadingApi(ImePresenterImpl.TabType.UPDATES);
                    }
                    break;
                case PRODUCTS:
                    if (productList.size() > 0) {
                        shareAdapter.setSuggestionModels(productList);
                    } else {
                        callLoadingApi(ImePresenterImpl.TabType.PRODUCTS);
                    }
                    break;
            }
        }
    }

    private void callLoadingApi(final ImePresenterImpl.TabType type) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case PRODUCTS:
                        if (isProductCompleted) {
                            return;
                        }
                        if (productList.size() > 0 && productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                            return;
                        }
                        productList.add(createSuggestionModel("", BaseAdapterManager.SectionTypeEnum.loader));
                        shareAdapter.setSuggestionModels(productList);
                        apiCallPresenter.loadMore(productList.size() - 1, ImePresenterImpl.TabType.PRODUCTS);
                        break;
                    case UPDATES:
                        if (isUpdatesCompleted) {
                            return;
                        }
                        if (updatesList.size() > 0 && updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                            return;
                        }
                        updatesList.add(createSuggestionModel("", BaseAdapterManager.SectionTypeEnum.loader));
                        shareAdapter.setSuggestionModels(updatesList);
                        apiCallPresenter.loadMore(updatesList.size() - 1, ImePresenterImpl.TabType.UPDATES);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private AllSuggestionModel createSuggestionModel(String text, BaseAdapterManager.SectionTypeEnum type) {
        AllSuggestionModel model = new AllSuggestionModel(text, null);
        model.setTypeEnum(type);
        return model;
    }

    public void showKeyboardLayout() {
        shareLayout.setVisibility(GONE);
        emojiLayout.setVisibility(GONE);
        stopListening();
        mKeyboardView.setVisibility(VISIBLE);
    }

    public void showSpeechInput() {
        if (mSpeechRecognitionManager == null) {
            mSpeechRecognitionManager = new SpeechRecognitionManager(mContext, this);
        }
        emojiLayout.setVisibility(GONE);
        shareLayout.setVisibility(GONE);
        mKeyboardView.setVisibility(INVISIBLE);
        mSpeechMessageTv.setText("Connecting...");
        speechLayout.setVisibility(VISIBLE);
        mSpeechRecognitionManager.startListening();
    }

    public void stopListening() {
        speechLayout.setVisibility(GONE);
        if (mSpeechRecognitionManager != null) {
            mSpeechRecognitionManager.stopListening();
        }

    }

    public KeyboardViewBaseImpl getKeyboard() {
        return mKeyboardView = findViewById(R.id.keyboard_view);
    }

    @Override
    public void onItemClick(AllSuggestionModel model) {
        if (presenterListener != null) {
            presenterListener.onItemClick(model);
        }
    }

    @Override
    public void onResult(Bundle b) {
        ArrayList<String> matches = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) return;
        //Toast.makeText(mContext, matches.size()>0?matches.get(0):"null", Toast.LENGTH_SHORT).show();
//        for (String speech : matches){
//            result.append(speech+"\n");
//        }
        //result.delete(result.lastIndexOf("\n"),result.length());
        presenterListener.onSpeechResult(matches.get(0));

    }

    @Override
    public void onReadyToSpeech(Bundle b) {
        mSpeechMessageTv.setText("Listening...");
        //Toast.makeText(mContext, "ready to speech", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int i) {
        String message = null;
        switch (i) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                mSpeechMessageTv.setText(message);
                return;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                if (speechLayout.getVisibility() == VISIBLE) {
                    presenterListener.onSpeechResult(null);
                }
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            case SpeechRecognizer.ERROR_SERVER:
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "Recognizer Busy";
                //showSpeechInput();
                //mSpeechRecognitionManager.startListening();
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        mSpeechMessageTv.setText(message);
        //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showEmojiLayout() {
        mKeyboardView.setVisibility(INVISIBLE);
        stopListening();
        shareLayout.setVisibility(GONE);
        if (emojisPager == null) {
            emojiLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mKeyboardView.getMeasuredHeight()));
            emojisPager = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_pager);
            LinearLayout tabs = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab);

            ViewPager.OnPageChangeListener pagerPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
                public void onPageScrollStateChanged(int state) {

                }
            };

            emojisPager.setOnPageChangeListener(pagerPageChangeListener);

            EmojiconRecents recents = new EmojiconRecents() {
                @Override
                public void addRecentEmoji(Context context, Emojicon emojicon) {
                    EmojiconRecentsGridView fragment = ((EmojiconsPopup.EmojisPagerAdapter) emojisPager.getAdapter()).getRecentFragment();
                    fragment.addRecentEmoji(context, emojicon);
                }
            };


            mEmojisAdapter = new EmojiconsPopup.EmojisPagerAdapter(
                    Arrays.asList(
                            new EmojiconRecentsGridView(mContext, null, null, presenterListener, true),
                            new EmojiconGridView(mContext, People.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Nature.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Food.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Sport.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Cars.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Electr.DATA, recents, presenterListener, true),
                            new EmojiconGridView(mContext, Symbols.DATA, recents, presenterListener, true)

                    )
            );
            emojisPager.setAdapter(mEmojisAdapter);
            mEmojiTabs = new View[8];

            mEmojiTabs[0] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_0_recents);
            mEmojiTabs[1] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_1_people);
            mEmojiTabs[2] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_2_nature);
            mEmojiTabs[3] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_3_food);
            mEmojiTabs[4] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_4_sport);
            mEmojiTabs[5] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_5_cars);
            mEmojiTabs[6] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_6_elec);
            mEmojiTabs[7] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_7_sym);
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

            ImageButton imgBtn = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_backspace);
            imgBtn.setColorFilter(Color.parseColor(iconPressedColor));
            imgBtn.setBackgroundColor(Color.parseColor(backgroundColor));


            findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_backspace).setOnTouchListener(new EmojiconsPopup.RepeatListener(500, 50, new OnClickListener() {

                @Override
                public void onClick(View v) {
                /*if(onEmojiconBackspaceClickedListener != null)
                    onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);*/
                    if (presenterListener != null)
                        presenterListener.onEmojiconBackspaceClicked(v);
                }
            }));

            // get last selected page
            mRecentsManager = EmojiconRecentsManager.getInstance(getContext());
            int page = mRecentsManager.getRecentPage();
            // last page was recents, check if there are recents to use
            // if none was found, go to page 1
            if (page == 0 && mRecentsManager.size() == 0) {
                page = 1;
            }

            if (page == 0) {
                pagerPageChangeListener.onPageSelected(page);
            } else {
                emojisPager.setCurrentItem(page, false);
            }
        }
        emojiLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onLoadMore(final ImePresenterImpl.TabType type, List<AllSuggestionModel> models) {

        switch (type) {
            case UPDATES:
                if (updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    updatesList.remove(updatesList.size() - 1);
                }
                updatesList.addAll(models);
                if (updatesList.size() == 0) {
                    updatesList.add(createSuggestionModel("Data not found", BaseAdapterManager.SectionTypeEnum.EmptyList));
                }
                break;
            case PRODUCTS:
                if (productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    productList.remove(productList.size() - 1);
                }
                productList.addAll(models);
                if (productList.size() == 0) {
                    productList.add(createSuggestionModel("Data not found", BaseAdapterManager.SectionTypeEnum.EmptyList));
                }
                break;
        }
        if (type == presenterListener.getTabType()) {
            shareAdapter.setSuggestionModels(type == ImePresenterImpl.TabType.UPDATES ? updatesList : productList);
        }
    }

    @Override
    public void onError(final ImePresenterImpl.TabType type) {
        switch (type) {
            case UPDATES:
                if (updatesList.size() > 0 && updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    updatesList.remove(updatesList.size() - 1);
                }
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
            case PRODUCTS:
                if (productList.size() > 0 && productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    productList.remove(productList.size() - 1);
                }
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
        }
        if (type == presenterListener.getTabType()) {
            shareAdapter.setSuggestionModels(type == ImePresenterImpl.TabType.UPDATES ? updatesList : productList);
        }
    }

    @Override
    public void onCompleted(ImePresenterImpl.TabType type) {
        switch (type) {
            case UPDATES:
                isUpdatesCompleted = true;
                break;
            case PRODUCTS:
                isProductCompleted = true;
                break;
        }
    }
}
