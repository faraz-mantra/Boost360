package nfkeyboard.keyboards;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.activity.SpeechRecognitionManager;
import nfkeyboard.adapter.BaseAdapterManager;
import nfkeyboard.adapter.MainAdapter;
import nfkeyboard.interface_contracts.ApiCallToKeyboardViewInterface;
import nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nfkeyboard.interface_contracts.GetGalleryImagesAsyncTask_Interface;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.interface_contracts.SpeechRecognitionResultInterface;
import nfkeyboard.interface_contracts.UrlToBitmapInterface;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.network.ApiCallPresenter;
import nfkeyboard.util.MethodUtils;
import nfkeyboard.util.MixPanelUtils;
import nfkeyboard.util.SharedPrefUtil;

import static nfkeyboard.keyboards.ImePresenterImpl.TabType.DETAILS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PHOTOS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PRODUCTS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.UPDATES;

/**
 * Created by Admin on 27-02-2018.
 */

public class ManageKeyboardView extends FrameLayout implements ItemClickListener, SpeechRecognitionResultInterface, ApiCallToKeyboardViewInterface, GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface {
    private KeyboardViewBaseImpl mKeyboardView;
    private Context mContext;
    private RecyclerView mRecyclerView, recyclerViewPhotos;
    private MainAdapter shareAdapter, shareAdapter1;
    private SpeechRecognitionManager mSpeechRecognitionManager;
    private CandidateToPresenterInterface presenterListener;
    private GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface galleryImagesListener;
    private TextView mSpeechMessageTv, totalImagesTv;
    private ConstraintLayout speechLayout, shareLayout, selectionLayout;
    private RelativeLayout emojiLayout;
    private PagerAdapter mEmojisAdapter;
    private ApiCallPresenter apiCallPresenter;
    private View[] mEmojiTabs;
    private ArrayList<AllSuggestionModel> updatesList = new ArrayList<>(),
            productList = new ArrayList<>(),
            imagesList = new ArrayList<>(),
            selectedImages = new ArrayList<>(),
            detailsList = new ArrayList<>();
    private int mEmojiTabLastSelectedIndex = -1;
    boolean isProductCompleted, isUpdatesCompleted, isPhotosCompleted, isDetailsCompleted;
    private Button shareBtn, deselectBtn;
    private UrlToBitmapInterface urlToBitmapInterface;

    String iconPressedColor = "#ffffff";
    String tabsColor = "#212121";
    String backgroundColor = "#212121";
    private ViewPager emojisPager;
    private SnapHelper snapHelper = new PagerSnapHelper();

    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);

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

    void setUrlToBitmapInterface(UrlToBitmapInterface urlToBitmapInterface) {
        this.urlToBitmapInterface = urlToBitmapInterface;
    }

    public void setPresenterListener(CandidateToPresenterInterface listener) {
        presenterListener = listener;
        init();
    }

    public void setGalleryImageListener(GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface listener) {
        this.galleryImagesListener = listener;
    }

    private void init() {
        speechLayout = findViewById(R.id.speech_layout);
        shareLayout = findViewById(R.id.sharelayout);
//        emojiLayout = findViewById(R.id.emoji_layout);
        mSpeechMessageTv = findViewById(R.id.tv_message);
        selectionLayout = findViewById(R.id.cl_selection_layout);
        totalImagesTv = findViewById(R.id.tv_total);
        shareBtn = findViewById(R.id.btn_share);
        shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("here", "clicked");
                onShareClick();
                deselectImages();
            }
        });
        deselectBtn = findViewById(R.id.btn_deselect_all);
        deselectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectImages();
            }
        });
        apiCallPresenter = new ApiCallPresenter(mContext, this);
    }

    private void deselectImages() {
        selectedImages.clear();
        shareBtn.setText(getResources().getString(R.string.share));
        shareAdapter1.setSuggestionModels(imagesList);
        recyclerViewPhotos.setAdapter(shareAdapter1);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(shareBtn, View.ALPHA, 0, 1f);
        objectAnimator.setDuration(100);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                shareBtn.setEnabled(false);
                shareBtn.setBackgroundResource(R.drawable.rounded_button_stroke);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
       // objectAnimator.start();
    }

    public void clearResources() {
        updatesList.clear();
        productList.clear();
        imagesList.clear();
        isProductCompleted = isUpdatesCompleted = isPhotosCompleted = false;
        if (shareAdapter != null)
            shareAdapter.setSuggestionModels(null);
    }

    public void showShareLayout(final ImePresenterImpl.TabType type) {
        mKeyboardView.setVisibility(INVISIBLE);
        emojiLayout.setVisibility(GONE);
        stopListening();
        shareLayout.setVisibility(VISIBLE);
        if (mRecyclerView == null) {

            recyclerViewPhotos = shareLayout.findViewById(R.id.rv_list_photos);
//            mRecyclerView = shareLayout.findViewById(R.id.rv_list);
            mRecyclerView.setHasFixedSize(true);
            shareAdapter1 = new MainAdapter(mContext, this);
            shareAdapter = new MainAdapter(mContext, this);
            mRecyclerView.setAdapter(shareAdapter);
            switch (type) {
                case PRODUCTS:
                case UPDATES:
                case DETAILS:
                    mRecyclerView.setVisibility(VISIBLE);
                    recyclerViewPhotos.setVisibility(INVISIBLE);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {

                            int totalItemCount = linearLayoutManager.getItemCount();
                            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            loadMore(totalItemCount, lastVisibleItem);
                        }
                    });
                    snapHelper.attachToRecyclerView(mRecyclerView);
                    break;
                case PHOTOS:
                    mRecyclerView.setVisibility(GONE);
                    break;
            }
        }
        if (recyclerViewPhotos == null) {
            recyclerViewPhotos.setHasFixedSize(true);
            shareAdapter1 = new MainAdapter(mContext, this);
            recyclerViewPhotos.setAdapter(shareAdapter1);
            recyclerViewPhotos.setLayoutManager(gridLayoutManager);
            selectionLayout.setVisibility(VISIBLE);
            if (type == PHOTOS)
                recyclerViewPhotos.setVisibility(VISIBLE);
        }

        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn()) {
            shareAdapter.setLoginScreen(createSuggestionModel("Login", BaseAdapterManager.SectionTypeEnum.Login));
        } else {
            switch (type) {
                case UPDATES:
                    snapHelper.attachToRecyclerView(mRecyclerView);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    selectionLayout.setVisibility(GONE);
                    mRecyclerView.setVisibility(VISIBLE);
                    recyclerViewPhotos.setVisibility(GONE);
                    if (updatesList.size() > 0) {
                        shareAdapter.setSuggestionModels(updatesList);
                    } else {
                        callLoadingApi(UPDATES);
                    }
                    break;
                case PRODUCTS:
                    snapHelper.attachToRecyclerView(mRecyclerView);
                    selectionLayout.setVisibility(GONE);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setVisibility(VISIBLE);
                    recyclerViewPhotos.setVisibility(GONE);
                    if (productList.size() > 0) {
                        shareAdapter.setSuggestionModels(productList);
                    } else {
                        callLoadingApi(PRODUCTS);
                    }
                    break;
                case PHOTOS:
                    deselectImages();
                    mRecyclerView.setVisibility(GONE);
                    recyclerViewPhotos.setVisibility(VISIBLE);
                    selectionLayout.setVisibility(VISIBLE);
                    if (imagesList.size() > 0 && imagesList.get(imagesList.size() - 1).getTypeEnum() != BaseAdapterManager.SectionTypeEnum.loader) {
                        shareAdapter1.setSuggestionModels(imagesList);
                    } else {
                        callLoadingApi(PHOTOS);
                    }
                    break;
                case DETAILS:
                    snapHelper.attachToRecyclerView(mRecyclerView);
                    selectionLayout.setVisibility(GONE);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setVisibility(VISIBLE);
                    recyclerViewPhotos.setVisibility(GONE);
                    if (detailsList.size() > 0) {
                        shareAdapter.setSuggestionModels(detailsList);
                    } else {
                        callLoadingApi(DETAILS);
                    }
                    break;
            }
        }
    }

    private void loadMore(int totalItemCount, int lastVisibleItem) {
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
                        apiCallPresenter.loadMore(productList.size() - 1, PRODUCTS, null);
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
                        apiCallPresenter.loadMore(updatesList.size() - 1, UPDATES, null);
                        break;
                    case PHOTOS:
                        if (isPhotosCompleted) {
                            return;
                        }
                        apiCallPresenter.loadMore(imagesList.size() - 1, PHOTOS, galleryImagesListener);
                        selectionLayout.setVisibility(VISIBLE);
                    case DETAILS:
                        if (isDetailsCompleted) {
                            return;
                        }
                        if (detailsList.size() > 0 && detailsList.get(detailsList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                            return;
                        }
                        detailsList.add(createSuggestionModel("", BaseAdapterManager.SectionTypeEnum.loader));
                        shareAdapter.setSuggestionModels(detailsList);
                        detailsList = apiCallPresenter.getAllDetails();
                        shareAdapter.setSuggestionModels(detailsList);
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
        selectionLayout.setVisibility(GONE);
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
    public String onCopyClick(AllSuggestionModel model) {
        if (presenterListener != null) {
            return presenterListener.onCopyClick(model);
        } else
            return "";
    }

    @Override
    public void onCreateProductOfferClick(AllSuggestionModel model) {
        createProductOffers(model);
    }

    @Override
    public String onCreateProductOfferResponse(String name, double oldPrice, double newPrice, String createdOn, String expiresOn, String Url, String currency) {
        return null;
    }

    @Override
    public boolean onClick(AllSuggestionModel model, boolean selected) {
        if (selected) {
            selectedImages.add(model);
        } else {
            for (int i = 0; i < selectedImages.size(); i++) {
                if (selectedImages.get(i).getImageUri().equalsIgnoreCase(model.getImageUri())) {
                    selectedImages.remove(i);
                }
            }
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(shareBtn, View.ALPHA, 0, 1f);
        objectAnimator.setDuration(100);
        if (selectedImages.size() == 0) {
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    shareBtn.setEnabled(false);
                    shareBtn.setText(getResources().getString(R.string.share));
                    shareBtn.setBackgroundResource(R.drawable.rounded_button_stroke);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            objectAnimator.start();
        } else {
            shareBtn.setText(getResources().getString(R.string.share) + " " + Integer.toString(selectedImages.size()));
            if (selectedImages.size() == 1 && selected) {
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        shareBtn.setEnabled(true);
                        shareBtn.setBackgroundResource(R.drawable.rounded_button_filled_primarycolor);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                objectAnimator.start();
            }
        }
        return true;
    }

    @Override
    public void onDetailsClick(AllSuggestionModel model) {
        if (presenterListener != null) {
            presenterListener.onDetailsClick(model);
        }
    }

    @Override
    public void onError() {

    }

    void onShareClick() {

        AllSuggestionModel model;

        for (int i = 0; i < selectedImages.size(); i++) {
            model = selectedImages.get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("id", model.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (model.getTypeEnum() == BaseAdapterManager.SectionTypeEnum.ImageShare) {
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_UPDATE_IMAGE_SHARE, object);
                MethodUtils.onGlideBitmapMultipleReady(urlToBitmapInterface, model.getImageUri(), model.getId(), selectedImages.size(), i);
            }

        }
    }

    public void createProductOffers(AllSuggestionModel model) {
        apiCallPresenter.createProductOffers(model, presenterListener);
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
//            emojisPager = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_pager);
//            LinearLayout tabs = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab);
//
//            ViewPager.OnPageChangeListener pagerPageChangeListener = new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int i) {
//                    if (mEmojiTabLastSelectedIndex == i) {
//                        return;
//                    }
//                    switch (i) {
//                        case 0:
//                        case 1:
//                        case 2:
//                        case 3:
//                        case 4:
//                        case 5:
//                        case 6:
//                        case 7:
//
//                            if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
//                                mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
//                            }
//                            mEmojiTabs[i].setSelected(true);
//                            mEmojiTabLastSelectedIndex = i;
//                            mRecentsManager.setRecentPage(i);
//                            break;
//                    }
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            };
//
//            emojisPager.setOnPageChangeListener(pagerPageChangeListener);
//
//            EmojiconRecents recents = new EmojiconRecents() {
//                @Override
//                public void addRecentEmoji(Context context, Emojicon emojicon) {
//                    EmojiconRecentsGridView fragment = ((EmojiconsPopup.EmojisPagerAdapter) emojisPager.getAdapter()).getRecentFragment();
//                    fragment.addRecentEmoji(context, emojicon);
//                }
//            };
//
//
//            mEmojisAdapter = new EmojiconsPopup.EmojisPagerAdapter(
//                    Arrays.asList(
//                            new EmojiconRecentsGridView(mContext, null, null, presenterListener, true),
//                            new EmojiconGridView(mContext, People.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Nature.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Food.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Sport.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Cars.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Electr.DATA, recents, presenterListener, true),
//                            new EmojiconGridView(mContext, Symbols.DATA, recents, presenterListener, true)
//
//                    )
//            );
//            emojisPager.setAdapter(mEmojisAdapter);
//            mEmojiTabs = new View[8];
//
//            mEmojiTabs[0] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_0_recents);
//            mEmojiTabs[1] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_1_people);
//            mEmojiTabs[2] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_2_nature);
//            mEmojiTabs[3] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_3_food);
//            mEmojiTabs[4] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_4_sport);
//            mEmojiTabs[5] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_5_cars);
//            mEmojiTabs[6] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_6_elec);
//            mEmojiTabs[7] = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_tab_7_sym);
//            for (int i = 0; i < mEmojiTabs.length; i++) {
//                final int position = i;
//                mEmojiTabs[i].setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        emojisPager.setCurrentItem(position);
//                    }
//                });
//            }
//
//
//            emojisPager.setBackgroundColor(Color.parseColor(backgroundColor));
//            tabs.setBackgroundColor(Color.parseColor(tabsColor));
//            for (int x = 0; x < mEmojiTabs.length; x++) {
//                ImageButton btn = (ImageButton) mEmojiTabs[x];
//                btn.setColorFilter(Color.parseColor(iconPressedColor));
//            }
//
//            ImageButton imgBtn = findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_backspace);
//            imgBtn.setColorFilter(Color.parseColor(iconPressedColor));
//            imgBtn.setBackgroundColor(Color.parseColor(backgroundColor));
//
//
//            findViewById(hani.momanii.supernova_emoji_library.R.id.emojis_backspace).setOnTouchListener(new EmojiconsPopup.RepeatListener(500, 50, new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                /*if(onEmojiconBackspaceClickedListener != null)
//                    onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);*/
//                    if (presenterListener != null)
//                        presenterListener.onEmojiconBackspaceClicked(v);
//                }
//            }));
//
//            // get last selected page
//            mRecentsManager = EmojiconRecentsManager.getInstance(getContext());
//            int page = mRecentsManager.getRecentPage();
//            // last page was recents, check if there are recents to use
//            // if none was found, go to page 1
//            if (page == 0 && mRecentsManager.size() == 0) {
//                page = 1;
//            }
//
//            if (page == 0) {
//                pagerPageChangeListener.onPageSelected(page);
//            } else {
//                emojisPager.setCurrentItem(page, false);
//            }
//        }
//        emojiLayout.setVisibility(VISIBLE);
        }

//    @Override
//    public void onLoadMore(final ImePresenterImpl.TabType type, List<AllSuggestionModel> models) {
//
//        switch (type) {
//            case UPDATES:
//                selectionLayout.setVisibility(GONE);
//                if (updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
//                    updatesList.remove(updatesList.size() - 1);
//                }
//                updatesList.addAll(models);
//                if (updatesList.size() == 0) {
//                    updatesList.add(createSuggestionModel("Data not found", BaseAdapterManager.SectionTypeEnum.EmptyList));
//                }
//                break;
//            case PRODUCTS:
//                selectionLayout.setVisibility(GONE);
//                if (productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
//                    productList.remove(productList.size() - 1);
//                }
//                productList.addAll(models);
//                if (productList.size() == 0) {
//                    productList.add(createSuggestionModel("Data not found", BaseAdapterManager.SectionTypeEnum.EmptyList));
//                }
//                break;
//            case PHOTOS:
//                imagesList.addAll(models);
//                mRecyclerView.setLayoutManager(linearLayoutManager);
//                selectionLayout.setVisibility(VISIBLE);
//                if (imagesList.size() == 0) {
//                    imagesList.add(createSuggestionModel("Data not found", BaseAdapterManager.SectionTypeEnum.EmptyList));
//                }
//        }
//        if (type == presenterListener.getTabType()) {
//            shareAdapter.setSuggestionModels(type == UPDATES ? updatesList : productList);
//        }
//    }
//
//    @Override
//    public void onError(final ImePresenterImpl.TabType type) {
//        switch (type) {
//            case UPDATES:
//                selectionLayout.setVisibility(GONE);
//                if (updatesList.size() > 0 && updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
//                    updatesList.remove(updatesList.size() - 1);
//                }
//                Toast.makeText(mContext, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
//                break;
//            case PRODUCTS:
//                selectionLayout.setVisibility(GONE);
//                if (productList.size() > 0 && productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
//                    productList.remove(productList.size() - 1);
//                }
//                Toast.makeText(mContext, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
//                break;
//            case PHOTOS:
//                selectionLayout.setVisibility(VISIBLE);
//                mRecyclerView.setLayoutManager(linearLayoutManager);
//                selectionLayout.setVisibility(GONE);
//                Toast.makeText(mContext, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
//                break;
//        }
//        if (type == presenterListener.getTabType()) {
//            shareAdapter.setSuggestionModels(type == UPDATES ? updatesList : productList);
//            mRecyclerView.setLayoutManager(type == UPDATES ? linearLayoutManager : (type == PRODUCTS ? linearLayoutManager : linearLayoutManager));
//
//        }
//    }
//
//    @Override
//    public void onCompleted(ImePresenterImpl.TabType type) {
//        switch (type) {
//            case UPDATES:
//                isUpdatesCompleted = true;
//                break;
//            case PRODUCTS:
//                isProductCompleted = true;
//                break;
//            case PHOTOS:
//                isPhotosCompleted = true;
//                break;
//        }
//    }
//
//    @Override
//    public void imagesReceived() {
//        onCompleted(PHOTOS);
//        ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
//        modelList.clear();
//        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//        int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, metrics);
//        int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, metrics);
//        int windowWidth = (int) metrics.widthPixels - margins;
//        int lengthOfItems = 2 * (windowWidth / viewWidth);
//        if (Constants.storeActualSecondaryImages != null) {
//            for (int i = 0; i < Constants.storeSecondaryImages.size(); i++) {
//                Photo photo = new Photo();
//                photo.setImageUri(Constants.storeActualSecondaryImages.get(i));
//                modelList.add(photo.toAllSuggestion());
//            }
//            if (lengthOfItems > Constants.storeSecondaryImages.size()) {
//                for (int i = 0; i < lengthOfItems - Constants.storeSecondaryImages.size(); i++) {
//                    Photo photo = new Photo();
//                    photo.setImageUri(null);
//                    modelList.add(photo.toAllSuggestion());
//                }
//            }
//            totalImagesTv.setText(Integer.toString(Constants.storeActualSecondaryImages.size()));
//        }
//        if (modelList.size() < 10) {
//            onCompleted(PHOTOS);
//        }
//        shareAdapter1.setSuggestionModels(modelList);
//        recyclerViewPhotos.setLayoutManager(gridLayoutManager);
//        selectionLayout.setVisibility(VISIBLE);
//        onLoadMore(PHOTOS, modelList);
//    }
    }

    @Override
    public void onLoadMore(ImePresenterImpl.TabType type, List<AllSuggestionModel> models) {

    }

    @Override
    public void onError(ImePresenterImpl.TabType type) {

    }

    @Override
    public void onCompleted(ImePresenterImpl.TabType type, ArrayList<AllSuggestionModel> modelList) {

    }

    @Override
    public void onDetailsLoaded(ArrayList<AllSuggestionModel> details) {

    }

    @Override
    public void imagesReceived() {

    }
}
