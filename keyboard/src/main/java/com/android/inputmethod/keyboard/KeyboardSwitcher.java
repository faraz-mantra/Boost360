/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyboard.KeyboardLayoutSet.KeyboardLayoutSetException;
import com.android.inputmethod.keyboard.internal.KeyboardState;
import com.android.inputmethod.keyboard.internal.KeyboardTextsSet;
import com.android.inputmethod.keyboard.top.ShowActionRowEvent;
import com.android.inputmethod.keyboard.top.UpdateActionBarEvent;
import com.android.inputmethod.keyboard.top.actionrow.ActionRowView;
import com.android.inputmethod.latin.utils.ResourceUtils;
import com.android.inputmethod.latin.utils.ScriptUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.separ.neural.inputmethod.Utils.FontUtils;
import io.separ.neural.inputmethod.compat.InputMethodServiceCompatUtils;
import io.separ.neural.inputmethod.indic.InputView;
import io.separ.neural.inputmethod.indic.LatinIME;
import io.separ.neural.inputmethod.indic.R;
import io.separ.neural.inputmethod.indic.RichInputMethodManager;
import io.separ.neural.inputmethod.indic.SubtypeSwitcher;
import io.separ.neural.inputmethod.indic.WordComposer;
import io.separ.neural.inputmethod.indic.settings.Settings;
import io.separ.neural.inputmethod.indic.settings.SettingsValues;
import io.separ.neural.inputmethod.slash.EventBusExt;
import nfkeyboard.adapter.BaseAdapterManager;
import nfkeyboard.adapter.MainAdapter;
import nfkeyboard.interface_contracts.ApiCallToKeyboardViewInterface;
import nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nfkeyboard.interface_contracts.GetGalleryImagesAsyncTask_Interface;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.interface_contracts.UrlToBitmapInterface;
import nfkeyboard.keyboards.ImePresenterImpl;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.models.networkmodels.Photo;
import nfkeyboard.network.ApiCallPresenter;
import nfkeyboard.util.Constants;
import nfkeyboard.util.MethodUtils;
import nfkeyboard.util.MixPanelUtils;
import nfkeyboard.util.SharedPrefUtil;

import static com.android.inputmethod.keyboard.internal.AlphabetShiftState.IS_SHIFTED;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.DETAILS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.KEYBOARD;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PHOTOS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.PRODUCTS;
import static nfkeyboard.keyboards.ImePresenterImpl.TabType.UPDATES;

public final class KeyboardSwitcher implements KeyboardState.SwitchActions, ItemClickListener, ApiCallToKeyboardViewInterface, GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface {
    private static final String TAG = KeyboardSwitcher.class.getSimpleName();
    private EventBusHandler mEventHandler;

    private SubtypeSwitcher mSubtypeSwitcher;
    private SharedPreferences mPrefs;

    private InputView mCurrentInputView;
    private View mMainKeyboardFrame;
    private MainKeyboardView mKeyboardView;
    private RichMediaView mRichMediaView;
    //private LinearLayout mSettingsViewPager;
    private LatinIME mLatinIME;
    private boolean mIsHardwareAcceleratedDrawingEnabled;
    private ActionRowView mActionRowView;

    private KeyboardState mState;
    private RecyclerView mRecyclerView;
    private MainAdapter shareAdapter;
    private ApiCallPresenter apiCallPresenter;
    private CandidateToPresenterInterface presenterListener;
    private ImePresenterImpl.TabType mTabType;
    private ConstraintLayout shareLayout, selectionLayout;
    private RecyclerView recyclerViewPhotos;
    private MainAdapter shareAdapter1;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private TextView totalImagesTv, imagesNotSupportedTv;
    private Button shareBtn, deselectBtn;
    private UrlToBitmapInterface urlToBitmapInterface;
    private GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface galleryImagesListener;
    boolean isProductCompleted, isUpdatesCompleted, isPhotosCompleted, isDetailsCompleted;
    private ProgressBar pbOffers;
    private TextView tvImageNotSupported;
    public static int MAIN_KEYBOARD_HEIGHT;
    private TextView tvPhotos;

    public LatinIME getmLatinIME() {
        return mLatinIME;
    }

    private KeyboardLayoutSet mKeyboardLayoutSet;
    // TODO: The following {@link KeyboardTextsSet} should be in {@link KeyboardLayoutSet}.
    private final KeyboardTextsSet mKeyboardTextsSet = new KeyboardTextsSet();

    private KeyboardTheme mKeyboardTheme;
    private Context mThemeContext;
    private ArrayList<AllSuggestionModel> updatesList = new ArrayList<>(),
            productList = new ArrayList<>(),
            imagesList = new ArrayList<>(),
            selectedImages = new ArrayList<>(),
            detailsList = new ArrayList<>();

    private static final KeyboardSwitcher sInstance = new KeyboardSwitcher();

    public static KeyboardSwitcher getInstance() {
        return sInstance;
    }

    private KeyboardSwitcher() {
        // Intentional empty constructor for singleton.

        mEventHandler = new EventBusHandler();
    }

    public static void init(final LatinIME latinIme) {
        final SharedPreferences prefs = latinIme.getApplicationContext().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
        sInstance.initInternal(latinIme, prefs);
    }

    private void initInternal(final LatinIME latinIme, final SharedPreferences prefs) {
        mLatinIME = latinIme;
        mPrefs = prefs;
        mSubtypeSwitcher = SubtypeSwitcher.getInstance();
        mState = new KeyboardState(this);
        mIsHardwareAcceleratedDrawingEnabled =
                InputMethodServiceCompatUtils.enableHardwareAcceleration(mLatinIME);
    }

    public void updateKeyboardTheme() {
        final boolean themeUpdated = updateKeyboardThemeAndContextThemeWrapper(
                mLatinIME, KeyboardTheme.getKeyboardTheme(mPrefs));
        if (themeUpdated && mKeyboardView != null) {
            mLatinIME.setInputView(onCreateInputView(mIsHardwareAcceleratedDrawingEnabled));
        }
    }

    private boolean updateKeyboardThemeAndContextThemeWrapper(final Context context,
                                                              final KeyboardTheme keyboardTheme) {
        if (mThemeContext == null || !keyboardTheme.equals(mKeyboardTheme)) {
            mKeyboardTheme = keyboardTheme;
            mThemeContext = new ContextThemeWrapper(context, keyboardTheme.mStyleId);
            KeyboardLayoutSet.onKeyboardThemeChanged();
            return true;
        }
        return false;
    }

    public void loadKeyboard(final EditorInfo editorInfo, final SettingsValues settingsValues,
                             final int currentAutoCapsState, final int currentRecapitalizeState) {
        clearResources();
        mEventHandler.register();
        FontUtils.setCurrentLocale(mSubtypeSwitcher.getCurrentSubtypeLocale().getLanguage());
        FontUtils.setIsEmoji(false);
        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(
                mThemeContext, editorInfo);
        final Resources res = mThemeContext.getResources();
        final int keyboardWidth = ResourceUtils.getDefaultKeyboardWidth(res);
        final int keyboardHeight = ResourceUtils.getDefaultKeyboardHeight(res);
        MAIN_KEYBOARD_HEIGHT = keyboardHeight;
        builder.setKeyboardGeometry(keyboardWidth, keyboardHeight);
        builder.setSubtype(mSubtypeSwitcher.getCurrentSubtype());
        builder.setVoiceInputKeyEnabled(settingsValues.mShowsVoiceInputKey);
        builder.setLanguageSwitchKeyEnabled(mLatinIME.shouldShowLanguageSwitchKey());
        mKeyboardLayoutSet = builder.build();
        try {
            mState.onLoadKeyboard(currentAutoCapsState, currentRecapitalizeState);
            mKeyboardTextsSet.setLocale(mSubtypeSwitcher.getCurrentSubtypeLocale(), mThemeContext);
        } catch (KeyboardLayoutSetException e) {
            Log.w(TAG, "loading keyboard failed: " + e.mKeyboardId, e.getCause());
        }
    }


    public void clearResources() {
        updatesList.clear();
        productList.clear();
        imagesList.clear();
        detailsList.clear();
        isDetailsCompleted =  isProductCompleted = isUpdatesCompleted = isPhotosCompleted = false;
        if (shareAdapter != null)
            shareAdapter.setSuggestionModels(null);
    }

    public void saveKeyboardState() {
        if (getKeyboard() != null || isShowingEmojiPalettes()) {
            mState.onSaveKeyboardState();
        }
    }

    public void onHideWindow() {
        if (mKeyboardView != null) {
            mKeyboardView.onHideWindow();
        }
        // mEventHandler.unregister();
    }

    private void setKeyboard(final Keyboard keyboard) {
        // Make {@link MainKeyboardView} visible and hide {@link EmojiPalettesView}.
        final SettingsValues currentSettingsValues = Settings.getInstance().getCurrent();
        setMainKeyboardFrame(currentSettingsValues);
        // TODO: pass this object to setKeyboard instead of getting the current values.
        final MainKeyboardView keyboardView = mKeyboardView;
        final Keyboard oldKeyboard = keyboardView.getKeyboard();
        keyboardView.setKeyboard(keyboard);
        mCurrentInputView.setKeyboardTopPadding(keyboard.mTopPadding);
        keyboardView.setKeyPreviewPopupEnabled(
                currentSettingsValues.mKeyPreviewPopupOn,
                currentSettingsValues.mKeyPreviewPopupDismissDelay);
        keyboardView.setKeyPreviewAnimationParams(
                currentSettingsValues.mHasCustomKeyPreviewAnimationParams,
                currentSettingsValues.mKeyPreviewShowUpStartXScale,
                currentSettingsValues.mKeyPreviewShowUpStartYScale,
                currentSettingsValues.mKeyPreviewShowUpDuration,
                currentSettingsValues.mKeyPreviewDismissEndXScale,
                currentSettingsValues.mKeyPreviewDismissEndYScale,
                currentSettingsValues.mKeyPreviewDismissDuration);
        keyboardView.updateShortcutKey(mSubtypeSwitcher.isShortcutImeReady());
        final boolean subtypeChanged = (oldKeyboard == null)
                || !keyboard.mId.mLocale.equals(oldKeyboard.mId.mLocale);
        final int languageOnSpacebarFormatType = mSubtypeSwitcher.getLanguageOnSpacebarFormatType(
                keyboard.mId.mSubtype);
        final boolean hasMultipleEnabledIMEsOrSubtypes = RichInputMethodManager.getInstance()
                .hasMultipleEnabledIMEsOrSubtypes(true /* shouldIncludeAuxiliarySubtypes */);
        keyboardView.startDisplayLanguageOnSpacebar(subtypeChanged, languageOnSpacebarFormatType,
                hasMultipleEnabledIMEsOrSubtypes);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("keyboard_height", getMainKeyboardView().getHeight());
        editor.commit();
    }

    public Keyboard getKeyboard() {
        if (mKeyboardView != null) {
            return mKeyboardView.getKeyboard();
        }
        return null;
    }

    // TODO: Remove this method. Come up with a more comprehensive way to reset the keyboard layout
    // when a keyboard layout set doesn't get reloaded in LatinIME.onStartInputViewInternal().
    public void resetKeyboardStateToAlphabet(final int currentAutoCapsState,
                                             final int currentRecapitalizeState) {
        mState.onResetKeyboardStateToAlphabet(currentAutoCapsState, currentRecapitalizeState);
    }

    public void onPressKey(final int code, final boolean isSinglePointer,
                           final int currentAutoCapsState, final int currentRecapitalizeState) {
        mState.onPressKey(code, isSinglePointer, currentAutoCapsState, currentRecapitalizeState);
    }

    public void onReleaseKey(final int code, final boolean withSliding,
                             final int currentAutoCapsState, final int currentRecapitalizeState) {
        mState.onReleaseKey(code, withSliding, currentAutoCapsState, currentRecapitalizeState);
    }

    public void onFinishSlidingInput(final int currentAutoCapsState,
                                     final int currentRecapitalizeState) {
        mState.onFinishSlidingInput(currentAutoCapsState, currentRecapitalizeState);
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setAlphabetKeyboard() {
        IS_SHIFTED = false;
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET));
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setAlphabetManualShiftedKeyboard() {
        IS_SHIFTED = true;
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET_MANUAL_SHIFTED));
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setAlphabetAutomaticShiftedKeyboard() {
        IS_SHIFTED = true;
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED));
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setAlphabetShiftLockedKeyboard() {
        IS_SHIFTED = true;
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCKED));
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setAlphabetShiftLockShiftedKeyboard() {
        IS_SHIFTED = true;
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCK_SHIFTED));
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setSymbolsKeyboard() {
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_SYMBOLS));
    }

    private void setMainKeyboardFrame(final SettingsValues settingsValues) {
        mMainKeyboardFrame.setVisibility(
                settingsValues.mHasHardwareKeyboard ? View.GONE : View.VISIBLE);
        mKeyboardView.setVisibility(View.VISIBLE);
        //EventBusExt.getDefault().post(new ShowSuggestionsEvent());
        shareLayout.setVisibility(View.GONE);
        mRichMediaView.setGone();
    }

    public void showKeyboardFrame() {
        mTabType = KEYBOARD;
        mKeyboardView.setVisibility(View.VISIBLE);
        mRichMediaView.setGone();
        imagesNotSupportedTv.setVisibility(View.GONE);
        if (shareLayout != null) {
            shareLayout.setVisibility(View.INVISIBLE);
        }
        mState.setAlphabetMode(true);
    }

    public void setProductShareKeyboardFrame(final ImePresenterImpl.TabType tabType) {
        if (tvPhotos != null) {
            tvPhotos.setText(R.string.tv_photos);
            deselectBtn.setText(R.string.deselect_all);
        }
        this.mTabType = tabType;
        shareLayout.setVisibility(View.VISIBLE);
        mKeyboardView.setVisibility(View.INVISIBLE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("keyboard_height", getMainKeyboardView().getHeight());
        editor.commit();
        mRichMediaView.setGone();
        //EventBusExt.getDefault().post(new ShowActionRowEvent());
        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mThemeContext).isLoggedIn()) {
            shareAdapter.setLoginScreen(createSuggestionModel("Login", BaseAdapterManager.SectionTypeEnum.Login));
        } else {

            if (MethodUtils.isOnline(mThemeContext)) {
                refreshRecyclerView();
                switch (tabType) {
                    case UPDATES:
                        mActionRowView.findViewById(R.id.tv_updates).setBackground(mThemeContext.getResources().getDrawable(R.drawable.round_414141));
                        imagesNotSupportedTv.setVisibility(View.GONE);
                        snapHelper.attachToRecyclerView(mRecyclerView);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        selectionLayout.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        recyclerViewPhotos.setVisibility(View.GONE);
                        if (updatesList.size() > 0) {
                            shareAdapter.setSuggestionModels(updatesList);
                        } else {
                            callLoadingApi(UPDATES);
                        }
                        break;
                    case PRODUCTS:
                        mActionRowView.findViewById(R.id.tv_products).setBackground(mThemeContext.getResources().getDrawable(R.drawable.round_414141));
                        imagesNotSupportedTv.setVisibility(View.GONE);
                        snapHelper.attachToRecyclerView(mRecyclerView);
                        selectionLayout.setVisibility(View.GONE);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        recyclerViewPhotos.setVisibility(View.GONE);
                        if (productList.size() > 0) {
                            shareAdapter.setSuggestionModels(productList);
                        } else {
                            callLoadingApi(PRODUCTS);
                        }
                        break;
                    case PHOTOS:
                        mActionRowView.findViewById(R.id.tv_photos).setBackground(mThemeContext.getResources().getDrawable(R.drawable.round_414141));
                        if (presenterListener.imagesSupported()) {
                            imagesNotSupportedTv.setVisibility(View.GONE);
                            deselectImages();
                            mRecyclerView.setVisibility(View.GONE);
                            recyclerViewPhotos.setVisibility(View.VISIBLE);
                            selectionLayout.setVisibility(View.VISIBLE);
                            if (imagesList.size() > 0 && imagesList.get(imagesList.size() - 1).getTypeEnum()
                                    != BaseAdapterManager.SectionTypeEnum.loader) {
                                shareAdapter1.setSuggestionModels(imagesList);
                            } else {
                                callLoadingApi(PHOTOS);
                            }
                        } else {
                            imagesNotSupportedTv.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            selectionLayout.setVisibility(View.GONE);
                            recyclerViewPhotos.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case DETAILS:
                        mActionRowView.findViewById(R.id.tv_details).setBackground(mThemeContext.getResources().getDrawable(R.drawable.round_414141));
                        imagesNotSupportedTv.setVisibility(View.GONE);
                        snapHelper.attachToRecyclerView(mRecyclerView);
                        selectionLayout.setVisibility(View.GONE);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        recyclerViewPhotos.setVisibility(View.GONE);
//                        if (detailsList.size() > 0) {
//                            shareAdapter.setSuggestionModels(detailsList);
//                        } else {
//                            callLoadingApi(DETAILS);
//                        }
                        callLoadingApi(DETAILS);
                        break;
                }
            } else {
                Toast.makeText(mThemeContext, "Check your Network Connection", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void deselectImages() {
        selectedImages.clear();
        shareBtn.setText(mThemeContext.getResources().getString(R.string.share));
        recyclerViewPhotos.removeAllViews();
        for (int i = 0; i < imagesList.size(); i++) {
            if (imagesList.get(i).getSelected() && imagesList.get(i).getImageUri() != null) {
                imagesList.get(i).setSelected(false);
            }
        }
        shareAdapter1.setSuggestionModels(imagesList);
        shareAdapter1.notifyDataSetChanged();
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
        objectAnimator.start();
    }

    private void callLoadingApi(final ImePresenterImpl.TabType type) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case PRODUCTS:
                        if (isProductCompleted) {
                            return;
                        }
                        if (productList.size() > 0 && productList.get(productList.size() - 1).getTypeEnum()
                                == BaseAdapterManager.SectionTypeEnum.loader) {
                            return;
                        }
                        productList.add(createSuggestionModel("", BaseAdapterManager.SectionTypeEnum.loader));
                        shareAdapter.setSuggestionModels(productList);
                        apiCallPresenter.loadMore(productList.size() - 1, ImePresenterImpl.TabType.PRODUCTS, null);
                        break;
                    case UPDATES:
                        if (isUpdatesCompleted) {
                            return;
                        }
                        if (updatesList.size() > 0 && updatesList.get(updatesList.size() - 1).getTypeEnum() ==
                                BaseAdapterManager.SectionTypeEnum.loader) {
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
                    case DETAILS:
                        if (isDetailsCompleted) {
                            shareAdapter.setSuggestionModels(detailsList);
                            return;
                        }
                        if (detailsList.size() > 0 || detailsList.size() > 0 && detailsList.get(detailsList.size() - 1).getTypeEnum() ==
                                BaseAdapterManager.SectionTypeEnum.loader) {
                            return;
                        }
                        detailsList.add(createSuggestionModel("", BaseAdapterManager.SectionTypeEnum.loader));
                        shareAdapter.setSuggestionModels(detailsList);
                        apiCallPresenter.getAllDetailsFromApi();
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

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setEmojiKeyboard() {
        final Keyboard keyboard = mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET);
        mKeyboardView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        EventBusExt.getDefault().post(new ShowActionRowEvent());
        mRichMediaView.setEmojiKeyboard(mKeyboardTextsSet.getText(KeyboardTextsSet.SWITCH_TO_ALPHA_KEY_LABEL), mKeyboardView.getKeyVisualAttribute(), keyboard.mIconsSet);
    }

    /*public void onToggleSettingsKeyboard() {
        if(mSettingsViewPager.getVisibility() == View.VISIBLE){
            mSettingsViewPager.setVisibility(View.GONE);
            mMainKeyboardFrame.setVisibility(View.VISIBLE);
            mEmojiPalettesView.setVisibility(View.GONE);
            mMediaBottomBar.setVisibility(View.GONE);
        }else {
            mSettingsViewPager.setVisibility(View.VISIBLE);
            mMainKeyboardFrame.setVisibility(View.GONE);
            mEmojiPalettesView.setVisibility(View.GONE);
            mMediaBottomBar.setVisibility(View.GONE);
        }
    }*/

    public void onToggleEmojiKeyboard() {
        if (mKeyboardLayoutSet == null || !isShowingEmojiPalettes()) {
            mLatinIME.startShowingInputView();
            setEmojiKeyboard();
        } else {
            mLatinIME.stopShowingInputView();
            setAlphabetKeyboard();
        }
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void setSymbolsShiftedKeyboard() {
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_SYMBOLS_SHIFTED));
    }

    @Override
    public void setMainLayoutTwo() {
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.KEYBOARD_MAIN_LAYOUT_TWO));
    }

    @Override
    public void setMainLayoutThree() {
        setKeyboard(mKeyboardLayoutSet.getKeyboard(KeyboardId.KEYBOARD_MAIN_LAYOUT_THREE));
    }

    // Future method for requesting an updating to the shift state.
    public void requestUpdatingShiftState(final int currentAutoCapsState,
                                          final int currentRecapitalizeState) {
        mState.onUpdateShiftState(currentAutoCapsState, currentRecapitalizeState);
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void startDoubleTapShiftKeyTimer() {
        final MainKeyboardView keyboardView = mKeyboardView;
        if (keyboardView != null) {
            keyboardView.startDoubleTapShiftKeyTimer();
        }
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public void cancelDoubleTapShiftKeyTimer() {
        final MainKeyboardView keyboardView = mKeyboardView;
        if (keyboardView != null) {
            keyboardView.cancelDoubleTapShiftKeyTimer();
        }
    }

    // Implements {@link KeyboardState.SwitchActions}.
    @Override
    public boolean isInDoubleTapShiftKeyTimeout() {
        final MainKeyboardView keyboardView = mKeyboardView;
        return keyboardView != null && keyboardView.isInDoubleTapShiftKeyTimeout();
    }

    /**
     * Updates state machine to figure out when to automatically switch back to the previous mode.
     */
    public void onCodeInput(final int code, final int currentAutoCapsState,
                            final int currentRecapitalizeState) {
        mState.onCodeInput(code, currentAutoCapsState, currentRecapitalizeState);
    }

    //TODO rename this
    public boolean isShowingEmojiPalettes() {
        return mRichMediaView != null && mRichMediaView.isShowingEmojiPalettes();
    }

    public boolean isShowingMoreKeysPanel() {
        return !isShowingEmojiPalettes() && mKeyboardView.isShowingMoreKeysPanel();
    }

    public View getVisibleKeyboardView() {
        if (isShowingEmojiPalettes()) {
            return mRichMediaView.getVisibleKeyboardView();
        }
        return mKeyboardView;
    }

    public MainKeyboardView getMainKeyboardView() {
        return mKeyboardView;
    }

    public void deallocateMemory() {
        if (mKeyboardView != null) {
            mKeyboardView.cancelAllOngoingEvents();
            mKeyboardView.deallocateMemory();
        }
        if (mRichMediaView != null) {
            mRichMediaView.deallocateMemory();
        }
    }

    public View onCreateInputView(final boolean isHardwareAcceleratedDrawingEnabled) {
        if (mKeyboardView != null) {
            mKeyboardView.closing();
        }

        updateKeyboardThemeAndContextThemeWrapper(
                mLatinIME, KeyboardTheme.getKeyboardTheme(mPrefs));
        mCurrentInputView = (InputView) LayoutInflater.from(mThemeContext).inflate(
                R.layout.input_view, null);
        apiCallPresenter = new ApiCallPresenter(mThemeContext, this);
        mMainKeyboardFrame = mCurrentInputView.findViewById(R.id.main_keyboard_frame);
        mRichMediaView = (RichMediaView) mCurrentInputView.findViewById(R.id.rich_media_view);
        mRichMediaView.setUp(mCurrentInputView, isHardwareAcceleratedDrawingEnabled, mLatinIME);

        mKeyboardView = mCurrentInputView.findViewById(R.id.keyboard_view);
        mKeyboardView.setHardwareAcceleratedDrawingEnabled(isHardwareAcceleratedDrawingEnabled);
        mKeyboardView.setKeyboardActionListener(mLatinIME);
        mActionRowView = mCurrentInputView.findViewById(R.id.action_row);
        mActionRowView.setListener(this.mLatinIME);
        mActionRowView.setKeyboardActionListener(mLatinIME);
        shareLayout = mCurrentInputView.findViewById(R.id.sharelayout);
        pbOffers = (ProgressBar) mCurrentInputView.findViewById(R.id.pb_offer);
        imagesNotSupportedTv = (TextView) mCurrentInputView.findViewById(R.id.tv_images_not_supported);
        shareLayout.setMinimumHeight(mKeyboardView.getHeight());
        //mRichMediaView.setMinimumHeight(mKeyboardView.getHeight());
        selectionLayout = shareLayout.findViewById(R.id.cl_selection_layout);

        totalImagesTv = shareLayout.findViewById(R.id.tv_total);
        tvPhotos = shareLayout.findViewById(R.id.photos);
        shareBtn = shareLayout.findViewById(R.id.btn_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("here", "clicked");
                onShareClick();
                deselectImages();
            }
        });
        deselectBtn = shareLayout.findViewById(R.id.btn_deselect_all);
        deselectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectImages();
            }
        });
        refreshRecyclerView();
        return mCurrentInputView;
    }

    public void onNetworkStateChanged() {
        if (mKeyboardView != null) {
            mKeyboardView.updateShortcutKey(mSubtypeSwitcher.isShortcutImeReady());
        }
    }

    public ActionRowView getActionRowView() {
        return this.mActionRowView;
    }

    public int getKeyboardShiftMode() {
        final Keyboard keyboard = getKeyboard();
        if (keyboard == null) {
            return WordComposer.CAPS_MODE_OFF;
        }
        switch (keyboard.mId.mElementId) {
            case KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCKED:
            case KeyboardId.ELEMENT_ALPHABET_SHIFT_LOCK_SHIFTED:
                return WordComposer.CAPS_MODE_MANUAL_SHIFT_LOCKED;
            case KeyboardId.ELEMENT_ALPHABET_MANUAL_SHIFTED:
                return WordComposer.CAPS_MODE_MANUAL_SHIFTED;
            case KeyboardId.ELEMENT_ALPHABET_AUTOMATIC_SHIFTED:
                IS_SHIFTED = true;
                return WordComposer.CAPS_MODE_AUTO_SHIFTED;
            default:
                IS_SHIFTED = false;
                return WordComposer.CAPS_MODE_OFF;
        }
    }


    void onShareClick() {

        AllSuggestionModel model;

        Toast.makeText(mThemeContext, "Photos are being posted...", Toast.LENGTH_SHORT).show();

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

    public int getCurrentKeyboardScriptId() {
        if (null == mKeyboardLayoutSet) {
            return ScriptUtils.SCRIPT_UNKNOWN;
        }
        return mKeyboardLayoutSet.getScriptId();
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
        pbOffers.setVisibility(View.VISIBLE);
        apiCallPresenter.createProductOffers(model, presenterListener);
    }


    @Override
    public String onCreateProductOfferResponse(String name, double oldPrice, double newPrice, String createdOn, String expiresOn, String Url, String currency) {
        return null;
    }

    @Override
    public boolean onClick(AllSuggestionModel model, boolean selected) {
        if (presenterListener.packageName().equalsIgnoreCase(mThemeContext.getString(R.string.whatsapp_package))) {
            if (selectedImages.size() < 5) {
                if (selected) {
                    selectedImages.add(model);
                } else {
                    for (int i = 0; i < selectedImages.size(); i++) {
                        if (selectedImages.get(i).getImageUri().equalsIgnoreCase(model.getImageUri())) {
                            selectedImages.remove(i);
                        }
                    }
                }
            } else {
                if (!selected) {
                    for (int i = 0; i < selectedImages.size(); i++) {
                        if (selectedImages.get(i).getImageUri().equalsIgnoreCase(model.getImageUri())) {
                            selectedImages.remove(i);
                        }
                    }
                } else {
                    Toast.makeText(mThemeContext, mThemeContext.getString(R.string.can_not_select_more_than_5_images), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            if (selected) {
                selectedImages.add(model);
            } else {
                for (int i = 0; i < selectedImages.size(); i++) {
                    if (selectedImages.get(i).getImageUri().equalsIgnoreCase(model.getImageUri())) {
                        selectedImages.remove(i);
                    }
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
                    shareBtn.setText(mThemeContext.getResources().getString(R.string.share));
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
            shareBtn.setText(mThemeContext.getResources().getString(R.string.share) + " " + Integer.toString(selectedImages.size()));
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
        Toast.makeText(mThemeContext, mThemeContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
    }

    public void setPresenterListener(CandidateToPresenterInterface mImePresenter) {
        this.presenterListener = mImePresenter;
    }


    public void setUrlToBitmapInterface(UrlToBitmapInterface urlToBitmapInterface) {
        this.urlToBitmapInterface = urlToBitmapInterface;
    }

    public void setGalleryImageListener(GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface listener) {
        this.galleryImagesListener = listener;
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
                break;
        }
        if (type == mTabType) {
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
                Toast.makeText(mThemeContext, mThemeContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
                break;
            case PRODUCTS:
                if (productList.size() > 0 && productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    productList.remove(productList.size() - 1);
                }
                Toast.makeText(mThemeContext, mThemeContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mThemeContext, mThemeContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
        }
        if (type == mTabType) {
            shareAdapter.setSuggestionModels(type == ImePresenterImpl.TabType.UPDATES ? updatesList : productList);
        }
    }

    private void refreshRecyclerView() {
        mRecyclerView = null;
        recyclerViewPhotos = null;
        if (mRecyclerView == null) {
            mRecyclerView = shareLayout.findViewById(R.id.product_share_rv_list);
            mRecyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(mThemeContext, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            shareAdapter = new MainAdapter(mThemeContext, this);
            mRecyclerView.setAdapter(shareAdapter);
            mRecyclerView.setOnFlingListener(null);
            snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (apiCallPresenter == null) {
                        Toast.makeText(mThemeContext, mThemeContext.getString(R.string.please_reopen_the_keyboard), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (totalItemCount > 0 && lastVisibleItem >= totalItemCount - 2) {
                        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mThemeContext).isLoggedIn()) {
                            shareAdapter.setLoginScreen(createSuggestionModel("Login", BaseAdapterManager.SectionTypeEnum.Login));
                        } else {
                            callLoadingApi(mTabType);
                        }
                    }
                }
            });
        }
        if (recyclerViewPhotos == null) {
            recyclerViewPhotos = shareLayout.findViewById(R.id.rv_list_photos);
            recyclerViewPhotos.setHasFixedSize(true);
            shareAdapter1 = new MainAdapter(mThemeContext, this);
            recyclerViewPhotos.setAdapter(shareAdapter1);
            gridLayoutManager = new GridLayoutManager(mThemeContext, 2, GridLayoutManager.HORIZONTAL, false);
            recyclerViewPhotos.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public void onCompleted(ImePresenterImpl.TabType type, ArrayList<AllSuggestionModel> modelList) {
        switch (type) {
            case UPDATES:
                isUpdatesCompleted = true;
                if (updatesList.get(updatesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    updatesList.remove(updatesList.size() - 1);
                }
                updatesList.addAll(modelList);
                if (updatesList.size() == 0) {
                    updatesList.add(createSuggestionModel(mThemeContext.getString(R.string.no_updates_available), BaseAdapterManager.SectionTypeEnum.EmptyList));
                }
                shareAdapter.setSuggestionModels(updatesList);
                break;
            case PRODUCTS:
                isProductCompleted = true;
                if (productList.get(productList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    productList.remove(productList.size() - 1);
                }
                productList.addAll(modelList);
                if (productList.size() == 0) {
                    productList.add(createSuggestionModel(mThemeContext.getString(R.string.no_product_available), BaseAdapterManager.SectionTypeEnum.EmptyList));
                }
                shareAdapter.setSuggestionModels(productList);
                mEventHandler.unregister();
                break;
            case PHOTOS:
                isPhotosCompleted = true;
               /* if (imagesList != null && !imagesList.isEmpty() && imagesList.get(imagesList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
                    imagesList.remove(imagesList.size() - 1);
                }
                if (imagesList.size() == 0) {
                    imagesList.add(createSuggestionModel("No photos available.", BaseAdapterManager.SectionTypeEnum.EmptyList));
                }
                shareAdapter1.setSuggestionModels(imagesList);*/
                shareAdapter.unRegisterEventBus();
                break;

        }
    }

    @Override
    public void onDetailsLoaded(ArrayList<AllSuggestionModel> details) {
       isDetailsCompleted = true;
        if (detailsList.size() > 0|| detailsList.get(detailsList.size() - 1).getTypeEnum() == BaseAdapterManager.SectionTypeEnum.loader) {
            detailsList.remove(detailsList.size() - 1);
            //detailsList.clear();
        }
        detailsList.addAll(details);

        if (detailsList.size() == 0) {
            detailsList.add(createSuggestionModel(mThemeContext.getString(R.string.no_details_available), BaseAdapterManager.SectionTypeEnum.EmptyList));
        }
        shareAdapter.setSuggestionModels(detailsList);
    }

    @Override
    public void imagesReceived() {
        //onCompleted(PHOTOS);
        ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
        modelList.clear();
        DisplayMetrics metrics = mThemeContext.getResources().getDisplayMetrics();
        int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, metrics);
        int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, metrics);
        int windowWidth = (int) metrics.widthPixels - margins;
        int lengthOfItems = 2 * (windowWidth / viewWidth);
        if (Constants.storeActualSecondaryImages != null && !Constants.storeActualSecondaryImages.isEmpty()) {
            for (int i = 0; i < Constants.storeSecondaryImages.size(); i++) {
                Photo photo = new Photo();
                photo.setImageUri(Constants.storeActualSecondaryImages.get(i));
                photo.setSelected(false);
                modelList.add(photo.toAllSuggestion());
            }
        }
        if (Constants.storeSecondaryImages != null) {
            if (lengthOfItems > Constants.storeActualSecondaryImages.size()) {
                for (int i = 0; i < lengthOfItems - Constants.storeSecondaryImages.size(); i++) {
                    Photo photo = new Photo();
                    photo.setImageUri(null);
                    if (i == 0) {
                        photo.setSelected(true);
                    } else {
                        photo.setSelected(false);
                    }
                    modelList.add(photo.toAllSuggestion());
                }
            } else {
                Photo photo = new Photo();
                photo.setImageUri(null);
                photo.setSelected(true);
                modelList.add(photo.toAllSuggestion());
            }
        } else {
            for (int i = 0; i < lengthOfItems; i++) {
                Photo photo = new Photo();
                photo.setImageUri(null);
                if (i == 0) {
                    photo.setSelected(true);
                } else {
                    photo.setSelected(false);
                }
                modelList.add(photo.toAllSuggestion());
            }
        }
        totalImagesTv.setText(Integer.toString(Constants.storeActualSecondaryImages.size()));
        imagesList.addAll(modelList);
        shareAdapter1.setSuggestionModels(modelList);
        recyclerViewPhotos.setLayoutManager(gridLayoutManager);
        selectionLayout.setVisibility(View.VISIBLE);

        if (modelList.size() < 10) {
            onCompleted(PHOTOS, null);
        } else {
            onLoadMore(PHOTOS, modelList);
        }
    }

    public void hideProgressbar() {
        pbOffers.setVisibility(View.GONE);
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
            if (tvPhotos != null) {
                tvPhotos.setText(R.string.tv_photos);
                deselectBtn.setText(R.string.deselect_all);
            }
        }
    }
}
