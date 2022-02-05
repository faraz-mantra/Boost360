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

package io.separ.neural.inputmethod.indic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.inputmethod.keyboard.Key;
import com.android.inputmethod.keyboard.Keyboard;
import com.android.inputmethod.keyboard.KeyboardActionListener;
import com.android.inputmethod.keyboard.KeyboardId;
import com.android.inputmethod.keyboard.KeyboardSwitcher;
import com.android.inputmethod.keyboard.MainKeyboardView;
import com.android.inputmethod.keyboard.PointerTracker;
import com.android.inputmethod.keyboard.TextDecoratorUi;
import com.android.inputmethod.keyboard.sticker.InsertPngEvent;
import com.android.inputmethod.keyboard.top.ShowActionRowEvent;
import com.android.inputmethod.keyboard.top.ShowSuggestionsEvent;
import com.android.inputmethod.keyboard.top.ShowSuggestionsEventAnimated;
import com.android.inputmethod.keyboard.top.TopDisplayController;
import com.android.inputmethod.keyboard.top.actionrow.ActionRowView;
import com.android.inputmethod.keyboard.top.actionrow.FrequentEmojiHandler;
import com.android.inputmethod.keyboard.top.services.LaunchSettingsEvent;
import com.android.inputmethod.keyboard.top.services.SearchItemSelectedEvent;
import com.android.inputmethod.keyboard.top.services.ServiceExitEvent;
import com.android.inputmethod.latin.utils.ApplicationUtils;
import com.android.inputmethod.latin.utils.CapsModeUtils;
import com.android.inputmethod.latin.utils.CoordinateUtils;
import com.android.inputmethod.latin.utils.CursorAnchorInfoUtils;
import com.android.inputmethod.latin.utils.DialogUtils;
import com.android.inputmethod.latin.utils.DistracterFilterCheckingExactMatchesAndSuggestions;
import com.android.inputmethod.latin.utils.IntentUtils;
import com.android.inputmethod.latin.utils.JniUtils;
import com.android.inputmethod.latin.utils.LeakGuardHandlerWrapper;
import com.android.inputmethod.latin.utils.SubtypeLocaleUtils;
import com.android.inputmethod.latin.utils.ViewLayoutUtils;
import com.crashlytics.android.Crashlytics;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;
import com.permissioneverywhere.PermissionResultCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import io.separ.neural.inputmethod.Utils.FontUtils;
import io.separ.neural.inputmethod.Utils.ShareUtils;
import io.separ.neural.inputmethod.Utils.StatsUtils;
import io.separ.neural.inputmethod.Utils.SwipeUtils;
import io.separ.neural.inputmethod.accessibility.AccessibilityUtils;
import io.separ.neural.inputmethod.annotations.UsedForTesting;
import io.separ.neural.inputmethod.colors.ColorDatabase;
import io.separ.neural.inputmethod.colors.ColorManager;
import io.separ.neural.inputmethod.colors.NavManager;
import io.separ.neural.inputmethod.compat.CursorAnchorInfoCompatWrapper;
import io.separ.neural.inputmethod.compat.InputMethodServiceCompatUtils;
import io.separ.neural.inputmethod.dictionarypack.DictionaryPackConstants;
import io.separ.neural.inputmethod.event.Event;
import io.separ.neural.inputmethod.event.HardwareEventDecoder;
import io.separ.neural.inputmethod.event.HardwareKeyboardEventDecoder;
import io.separ.neural.inputmethod.event.InputTransaction;
import io.separ.neural.inputmethod.indic.Suggest.OnGetSuggestedWordsCallback;
import io.separ.neural.inputmethod.indic.SuggestedWords.SuggestedWordInfo;
import io.separ.neural.inputmethod.indic.define.DebugFlags;
import io.separ.neural.inputmethod.indic.define.JniLibName;
import io.separ.neural.inputmethod.indic.define.ProductionFlags;
import io.separ.neural.inputmethod.indic.inputlogic.InputLogic;
import io.separ.neural.inputmethod.indic.personalization.ContextualDictionaryUpdater;
import io.separ.neural.inputmethod.indic.personalization.DictionaryDecayBroadcastReciever;
import io.separ.neural.inputmethod.indic.personalization.PersonalizationDictionaryUpdater;
import io.separ.neural.inputmethod.indic.personalization.PersonalizationHelper;
import io.separ.neural.inputmethod.indic.settings.Settings;
import io.separ.neural.inputmethod.indic.settings.SettingsActivity;
import io.separ.neural.inputmethod.indic.settings.SettingsValues;
import io.separ.neural.inputmethod.indic.suggestions.SuggestionStripView;
import io.separ.neural.inputmethod.indic.suggestions.SuggestionStripViewAccessor;
import io.separ.neural.inputmethod.slash.EventBusExt;
import io.separ.neural.inputmethod.slash.SearchResultsEvent;
import io.separ.neural.inputmethod.slash.SearchRetryErrorEvent;
import io.separ.neural.inputmethod.slash.ServiceRequestEvent;
import nfkeyboard.interface_contracts.PresenterToImeInterface;
import nfkeyboard.keyboards.ImePresenterImpl;
import nfkeyboard.util.LocaleUtils;
import nfkeyboard.util.MixPanelUtils;
import nfkeyboard.util.SharedPrefUtil;

import static io.separ.neural.inputmethod.colors.ColorManager.addObserver;
import static io.separ.neural.inputmethod.indic.Constants.ImeOption.FORCE_ASCII;
import static io.separ.neural.inputmethod.indic.Constants.ImeOption.NO_MICROPHONE;
import static io.separ.neural.inputmethod.indic.Constants.ImeOption.NO_MICROPHONE_COMPAT;


/**
 * Input method implementation for Qwerty'ish keyboard.
 */
public class LatinIME extends InputMethodService implements KeyboardActionListener,
        SuggestionStripView.Listener, SuggestionStripViewAccessor,
        DictionaryFacilitator.DictionaryInitializationListener, ActionRowView.Listener,
        ImportantNoticeDialog.ImportantNoticeDialogListener, SwipeUtils.SelectionChanger, ColorManager.OnFinishCalculateProfile, PresenterToImeInterface {
    private static final String TAG = LatinIME.class.getSimpleName();
    private static final boolean TRACE = false;
    private static final int EXTENDED_TOUCHABLE_REGION_HEIGHT = 100;
    private static final int PENDING_IMS_CALLBACK_DURATION = 800;
    private static final int DELAY_WAIT_FOR_DICTIONARY_LOAD = 2000; // 2s
    private static final int PERIOD_FOR_AUDIO_AND_HAPTIC_FEEDBACK_IN_KEY_REPEAT = 2;
    /**
     * The name of the scheme used by the Package Manager to warn of a new package installation,
     * replacement or removal.
     */
    private static final String SCHEME_PACKAGE = "package";
    public static Context mResContext;
    public static InputConnection mInputCOnnection;
    public static Context mAppContext;
    private static boolean DEBUG = false;

    // Loading the native library eagerly to avoid unexpected UnsatisfiedLinkError at the initial
    // JNI call as much as possible.
    static {
        JniUtils.loadNativeLibrary();
    }

    public final UIHandler mHandler = new UIHandler(this);
    // We expect to have only one decoder in almost all cases, hence the default capacity of 1.
    // If it turns out we need several, it will get grown seamlessly.
    final SparseArray<HardwareEventDecoder> mHardwareEventDecoders = new SparseArray<>(1);
    @UsedForTesting
    final KeyboardSwitcher mKeyboardSwitcher;
    private final Settings mSettings;
    private final DictionaryFacilitator mDictionaryFacilitator =
            new DictionaryFacilitator(
                    new DistracterFilterCheckingExactMatchesAndSuggestions(this /* context */));
    // TODO: Move from LatinIME.
    private final PersonalizationDictionaryUpdater mPersonalizationDictionaryUpdater =
            new PersonalizationDictionaryUpdater(this /* context */, mDictionaryFacilitator);
    private final ContextualDictionaryUpdater mContextualDictionaryUpdater =
            new ContextualDictionaryUpdater(this /* context */, mDictionaryFacilitator,
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postUpdateSuggestionStrip(SuggestedWords.INPUT_STYLE_NONE);
                        }
                    });
    private final InputLogic mInputLogic = new InputLogic(this /* LatinIME */,
            this /* SuggestionStripViewAccessor */, mDictionaryFacilitator);
    private final ImePresenterImpl mImePresenter;
    private final SubtypeSwitcher mSubtypeSwitcher;
    private final SubtypeState mSubtypeState = new SubtypeState();

    private final SpecialKeyDetector mSpecialKeyDetector;
    // Object for reacting to adding/removing a dictionary pack.
    private final BroadcastReceiver mDictionaryPackInstallReceiver =
            new DictionaryPackInstallBroadcastReceiver(this);
    private final BroadcastReceiver mDictionaryDumpBroadcastReceiver =
            new DictionaryDumpBroadcastReceiver(this);
    private final boolean mIsHardwareAcceleratedDrawingEnabled;
    // receive ringer mode change and network state change.
    private final BroadcastReceiver mConnectivityAndRingerModeChangeReceiver =
            new MyBroadcastReceiver();
    // TODO: Move these {@link View}s to {@link KeyboardSwitcher}.
    private View mInputView;
    private SuggestionStripView mSuggestionStripView;
    private TextView mExtractEditText;
    private final ViewTreeObserver.OnPreDrawListener mExtractTextViewPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    onExtractTextViewPreDraw();
                    return true;
                }
            };
    private EventBusHandler mEventHandler;
    private RichInputMethodManager mRichImm;
    // Working variable for {@link #startShowingInputView()} and
    // {@link #onEvaluateInputViewShown()}.
    private boolean mIsExecutingStartShowingInputView;
    private AlertDialog mOptionsDialog;
    private NavManager navManager;
    private TopDisplayController mTopDisplayController;
    private ImePresenterImpl.TabType serviceTab;
    private boolean isWindowHidden = false;
    private int mLanguageIndex = 0;
    private ColorManager colorManager;
    private String currentPackageName;
    private long lastStickerInsertionTime = 0;
    private long lastEmojiInsertionTime = 0;

    public LatinIME() {
        super();
        mSettings = Settings.getInstance();
        mSubtypeSwitcher = SubtypeSwitcher.getInstance();
        mKeyboardSwitcher = KeyboardSwitcher.getInstance();
        mImePresenter = new ImePresenterImpl(this, this, mKeyboardSwitcher);
        mKeyboardSwitcher.setPresenterListener(mImePresenter);
        mKeyboardSwitcher.setUrlToBitmapInterface(mImePresenter);
        mKeyboardSwitcher.setGalleryImageListener(mImePresenter);
        mSpecialKeyDetector = new SpecialKeyDetector(this);
        mIsHardwareAcceleratedDrawingEnabled =
                InputMethodServiceCompatUtils.enableHardwareAcceleration(this);
        Log.i(TAG, "Hardware accelerated drawing: " + mIsHardwareAcceleratedDrawingEnabled);
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

    // A helper method to split the code point and the key code. Ultimately, they should not be
    // squashed into the same variable, and this method should be removed.
    private static Event createSoftwareKeypressEvent(final int keyCodeOrCodePoint, final int keyX,
                                                     final int keyY, final boolean isKeyRepeat) {
        final int keyCode;
        final int codePoint;
        if (keyCodeOrCodePoint <= 0) {
            keyCode = keyCodeOrCodePoint;
            codePoint = Event.NOT_A_CODE_POINT;
        } else {
            keyCode = Event.NOT_A_KEY_CODE;
            codePoint = keyCodeOrCodePoint;
        }
        return Event.createSoftwareKeypressEvent(codePoint, keyCode, keyX, keyY, isKeyRepeat);
    }

    private static boolean isBackWordStopChar(int c) {
        return !Character.isLetter(c);
    }

    @Override
    public void onCopy() {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(JniLibName.JNI_LIB_NAME, this.mInputLogic.mConnection.getSelectedText(0)));
        StatsUtils.getInstance().onSnippetToolSelected();
    }

    @Override
    public void onCut() {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(JniLibName.JNI_LIB_NAME, this.mInputLogic.mConnection.getSelectedText(0)));
        this.mInputLogic.mConnection.mIC.performContextMenuAction(16908320);
        StatsUtils.getInstance().onSnippetToolSelected();
    }

    @Override
    public void onEmojiClicked(String emoji, boolean z) {
        FrequentEmojiHandler.getInstance(this).onEmojiClicked(emoji);
        updateStateAfterInputTransaction(this.mInputLogic.onTextInput(this.mSettings.getCurrent(), Event.createSoftwareTextEvent(emoji, 1), this.mKeyboardSwitcher.getKeyboardShiftMode(), this.mHandler), false);
        this.mKeyboardSwitcher.onCodeInput(-4, getCurrentAutoCapsState(), getCurrentRecapitalizeState());
        StatsUtils.getInstance().onTopEmojiSelected();
    }

    @Override
    public void onNumberClicked(String number) {
        updateStateAfterInputTransaction(this.mInputLogic.onCodeInput(this.mSettings.getCurrent(), Event.createSoftwareKeypressEvent(number.codePointAt(0), 0, 0, 0, false), this.mKeyboardSwitcher.getKeyboardShiftMode(), this.mKeyboardSwitcher.getCurrentKeyboardScriptId(), this.mHandler), false);
        this.mKeyboardSwitcher.onCodeInput(-4, getCurrentAutoCapsState(), getCurrentRecapitalizeState());
    }

    @Override
    public void onPaste() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemAt(0) != null) {
            CharSequence text = clipboard.getPrimaryClip().getItemAt(0).getText();
            this.mInputLogic.finishInput();
            if (text != null)
                this.mInputLogic.mConnection.commitText(text, this.mInputLogic.mConnection.getExpectedSelectionEnd());
        }
        StatsUtils.getInstance().onSnippetToolSelected();
    }

    @Override
    public void onSelectAll() {
        this.mInputLogic.mConnection.mIC.performContextMenuAction(16908319);
        StatsUtils.getInstance().onSnippetToolSelected();
    }

    @Override
    public void onServiceClicked(String serviceId) {
        MixPanelUtils.reset(this);
        StatsUtils.getInstance().onServiceClicked(serviceId);
        if (serviceId.equals("customization")) {
            Context context = DialogUtils.getPlatformDialogThemeContext(this);
            try {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                final ColorPickerView colorPickerView = new ColorPickerView(context);
//                colorPickerView.showAlpha(false);
//                colorPickerView.showHex(false);
//                colorPickerView.setOriginalColor(getLastProfile().getPrimary());
//                colorPickerView.setCurrentColor(getLastProfile().getPrimary());
//                linearLayout.addView(colorPickerView);
                final CheckBox checkBox = new CheckBox(context);
                checkBox.setText(R.string.set_as_default_theme);
                linearLayout.addView(checkBox);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(linearLayout).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        ColorDatabase.addColors(LatinIME.this, currentPackageName, new String[]{convertColor(colorPickerView.getColor())});
//                        if (checkBox.isChecked()) {
//                            ColorDatabase.addTheme(LatinIME.this, colorPickerView.getColor());
//                            PreferenceManager.getDefaultSharedPreferences(LatinIME.this).edit().putString("KeyboardTheme", "my_theme").apply();
//                        }
//                        colorManager.calculateProfile(LatinIME.this, currentPackageName);
                    }
                }).setNegativeButton(android.R.string.cancel, null).setNeutralButton(R.string.default_coloring, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ColorDatabase.deletePackage(LatinIME.this, currentPackageName);
                        colorManager.calculateProfile(LatinIME.this, currentPackageName);
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                showOptionDialog(dialog);
            } catch (Exception exception) {
//                ColorChooserDialog dialog = new ColorChooserDialog(context);
//                dialog.setColorListener(new ColorListener() {
//                    @Override
//                    public void OnColorClick(View v, int color) {
//                        ColorDatabase.addColors(LatinIME.this, currentPackageName, new String[]{convertColor(color)});
//                        colorManager.calculateProfile(LatinIME.this, currentPackageName);
//                    }
//                });
//                dialog.setCancelable(true);
//                dialog.setCanceledOnTouchOutside(true);
//                showOldColorDialog(dialog);
            }
            return;
        }
        if (serviceId.equals("emoji")) {
            this.mKeyboardSwitcher.setEmojiKeyboard();
            return;
        }

        if (serviceId.equals(ImePresenterImpl.TabType.UPDATES.name())) {
            this.serviceTab = ImePresenterImpl.TabType.UPDATES;
            this.mKeyboardSwitcher.setProductShareKeyboardFrame(ImePresenterImpl.TabType.UPDATES);
            return;
        }

        if (serviceId.equals(ImePresenterImpl.TabType.PRODUCTS.name())) {
            this.serviceTab = ImePresenterImpl.TabType.PRODUCTS;
            this.mKeyboardSwitcher.setProductShareKeyboardFrame(ImePresenterImpl.TabType.PRODUCTS);
            return;
        }

        if (serviceId.equals(ImePresenterImpl.TabType.PHOTOS.name())) {
            this.serviceTab = ImePresenterImpl.TabType.PHOTOS;
            this.mKeyboardSwitcher.setProductShareKeyboardFrame(ImePresenterImpl.TabType.PHOTOS);
            return;
        }

        if (serviceId.equals(ImePresenterImpl.TabType.DETAILS.name())) {
            this.serviceTab = ImePresenterImpl.TabType.DETAILS;
            this.mKeyboardSwitcher.setProductShareKeyboardFrame(ImePresenterImpl.TabType.DETAILS);
            return;
        }
        this.mInputLogic.startSearchingResults();
        this.mTopDisplayController.runSearch(serviceId, mInputLogic.mConnection.getmComposingText().toString());
    }

    @Override
    public InputConnection getImeCurrentInputConnection() {
        mInputCOnnection = getCurrentInputConnection();
        return getCurrentInputConnection();
    }

    @Override
    public InputBinding getImeCurrentInputBinding() {
        return getCurrentInputBinding();
    }

    @Override
    public EditorInfo getImeCurrentEditorInfo() {
        return getCurrentInputEditorInfo();
    }

    @Override
    public void onCreate() {
        mAppContext = this;
//        Fabric.with(this, new Crashlytics());
        Settings.init(this);
        DebugFlags.init(PreferenceManager.getDefaultSharedPreferences(this));
        RichInputMethodManager.init(this);
        mRichImm = RichInputMethodManager.getInstance();
        PointerTracker.KEYBOARD_TYPED_KEY = null;


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(new Locale(locale));
        } else {
            configuration.locale = new Locale(locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        }
        if (locale.equalsIgnoreCase(LocaleUtils.ENGLISH)) {
            MixPanelUtils.getInstance().track(MixPanelUtils.SET_ENGLISH_KEYBOARD, null);
            mLanguageIndex = 0;
        } else {
            MixPanelUtils.getInstance().track(MixPanelUtils.SET_HINDI_KEYBOARD, null);
            mLanguageIndex = 1;
        }
        LocaleUtils.initialize(this, locale);

        SubtypeSwitcher.init(this);
        KeyboardSwitcher.init(this);
        AudioAndHapticFeedbackManager.init(this);
        AccessibilityUtils.init(this);
        super.onCreate();
        mHandler.onCreate();
        DEBUG = DebugFlags.DEBUG_ENABLED;
        setMixPanel(this);

        // TODO: Resolve mutual dependencies of {@link #loadSettings()} and {@link #initSuggest()}.
        loadSettings();
        resetSuggest();

        // Register to receive ringer mode change and network state change.
        // Also receive installation and removal of a dictionary pack.
        final IntentFilter filter = new IntentFilter();
        //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(mConnectivityAndRingerModeChangeReceiver, filter);

        final IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        packageFilter.addDataScheme(SCHEME_PACKAGE);
        registerReceiver(mDictionaryPackInstallReceiver, packageFilter);

        final IntentFilter newDictFilter = new IntentFilter();
        newDictFilter.addAction(DictionaryPackConstants.NEW_DICTIONARY_INTENT_ACTION);
        registerReceiver(mDictionaryPackInstallReceiver, newDictFilter);

        final IntentFilter dictDumpFilter = new IntentFilter();
        dictDumpFilter.addAction(DictionaryDumpBroadcastReceiver.DICTIONARY_DUMP_INTENT_ACTION);
        registerReceiver(mDictionaryDumpBroadcastReceiver, dictDumpFilter);

        DictionaryDecayBroadcastReciever.setUpIntervalAlarmForDictionaryDecaying(this);

        StatsUtils.getInstance().onCreate(this);
        FontUtils.initialize(this);
        SwipeUtils.init(this, this);
        this.colorManager = new ColorManager(this);
        this.navManager = new NavManager(this);
        //SpeechUtils.initialize(this);
        this.mEventHandler = new EventBusHandler();
        //StatSyncJob.scheduleJob();
    }

    public void finishCalculatingProfile() {
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        mainKeyboardView.startDisplayLanguageOnSpacebar(false, 2, true);

    }

    // Has to be package-visible for unit tests
    @UsedForTesting
    void loadSettings() {
        final Locale locale = mSubtypeSwitcher.getCurrentSubtypeLocale();
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        final InputAttributes inputAttributes = new InputAttributes(
                editorInfo, isFullscreenMode(), getPackageName());
        mSettings.loadSettings(this, locale, inputAttributes);
        final SettingsValues currentSettingsValues = mSettings.getCurrent();
        AudioAndHapticFeedbackManager.getInstance().onSettingsChanged(currentSettingsValues);
        // This method is called on startup and language switch, before the new layout has
        // been displayed. Opening dictionaries never affects responsivity as dictionaries are
        // asynchronously loaded.
        if (!mHandler.hasPendingReopenDictionaries()) {
            resetSuggestForLocale(locale);
        }
        mDictionaryFacilitator.updateEnabledSubtypes(mRichImm.getMyEnabledInputMethodSubtypeList(
                true /* allowsImplicitlySelectedSubtypes */));
        refreshPersonalizationDictionarySession(currentSettingsValues);
        //StatsUtils.onLoadSettings(currentSettingsValues);
    }

    private void refreshPersonalizationDictionarySession(
            final SettingsValues currentSettingsValues) {
        mPersonalizationDictionaryUpdater.onLoadSettings(
                currentSettingsValues.mUsePersonalizedDicts,
                mSubtypeSwitcher.isSystemLocaleSameAsLocaleOfAllEnabledSubtypesOfEnabledImes());
        mContextualDictionaryUpdater.onLoadSettings(currentSettingsValues.mUsePersonalizedDicts);
        final boolean shouldKeepUserHistoryDictionaries = currentSettingsValues.mUsePersonalizedDicts;
        if (!shouldKeepUserHistoryDictionaries) {
            // Remove user history dictionaries.
            PersonalizationHelper.removeAllUserHistoryDictionaries(this);
            mDictionaryFacilitator.clearUserHistoryDictionary();
        }
    }

    // Note that this method is called from a non-UI thread.
    @Override
    public void onUpdateMainDictionaryAvailability(final boolean isMainDictionaryAvailable) {
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        if (mainKeyboardView != null) {
            MainKeyboardView.setMainDictionaryAvailability(isMainDictionaryAvailable);
        }
        if (mHandler.hasPendingWaitForDictionaryLoad()) {
            mHandler.cancelWaitForDictionaryLoad();
            mHandler.postResumeSuggestions(true /* shouldIncludeResumedWordInSuggestions */,
                    false /* shouldDelay */);
        }
    }

    public void resetSuggest() {
        final Locale switcherSubtypeLocale = mSubtypeSwitcher.getCurrentSubtypeLocale();
        final String switcherLocaleStr = switcherSubtypeLocale.toString();
        final Locale subtypeLocale;
        if (TextUtils.isEmpty(switcherLocaleStr)) {
            // This happens in very rare corner cases - for example, immediately after a switch
            // to LatinIME has been requested, about a frame later another switch happens. In this
            // case, we are about to go down but we still don't know it, however the system tells
            // us there is no current subtype so the locale is the empty string. Take the best
            // possible guess instead -- it's bound to have no consequences, and we have no way
            // of knowing anyway.
            Log.e(TAG, "System is reporting no current subtype.");
            subtypeLocale = getResources().getConfiguration().locale;
        } else {
            subtypeLocale = switcherSubtypeLocale;
        }
        resetSuggestForLocale(subtypeLocale);
    }

    /**
     * Reset suggest by loading dictionaries for the locale and the current settings values.
     *
     * @param locale the locale
     */
    private void resetSuggestForLocale(final Locale locale) {
        final SettingsValues settingsValues = mSettings.getCurrent();
        mDictionaryFacilitator.resetDictionaries(this /* context */, locale,
                settingsValues.mUseContactsDict, settingsValues.mUsePersonalizedDicts,
                false /* forceReloadMainDictionary */, this);
        if (settingsValues.mAutoCorrectionEnabledPerUserSettings) {
            mInputLogic.mSuggest.setAutoCorrectionThreshold(
                    settingsValues.mAutoCorrectionThreshold);
        }
    }

    /**
     * Reset suggest by loading the main dictionary of the current locale.
     */
    /* package private */ void resetSuggestMainDict() {
        final SettingsValues settingsValues = mSettings.getCurrent();
        mDictionaryFacilitator.resetDictionaries(this /* context */,
                mDictionaryFacilitator.getLocale(), settingsValues.mUseContactsDict,
                settingsValues.mUsePersonalizedDicts, true /* forceReloadMainDictionary */, this);
    }

    @Override
    public void onDestroy() {
        if (this.mTopDisplayController != null) {
            this.mTopDisplayController.drop();
        }
        mDictionaryFacilitator.closeDictionaries();
        mPersonalizationDictionaryUpdater.onDestroy();
        mContextualDictionaryUpdater.onDestroy();
        mSettings.onDestroy();
        unregisterReceiver(mConnectivityAndRingerModeChangeReceiver);
        unregisterReceiver(mDictionaryPackInstallReceiver);
        unregisterReceiver(mDictionaryDumpBroadcastReceiver);
        StatsUtils.getInstance().onDestroy();
        this.navManager.killService();
        super.onDestroy();
    }

    @UsedForTesting
    public void recycle() {
        unregisterReceiver(mDictionaryPackInstallReceiver);
        unregisterReceiver(mDictionaryDumpBroadcastReceiver);
        unregisterReceiver(mConnectivityAndRingerModeChangeReceiver);
        mInputLogic.recycle();
    }

    @Override
    public void onConfigurationChanged(final Configuration conf) {

        //setInputView(onCreateInputView());
        LocaleUtils.handleConfigurationChange(this);

        SettingsValues settingsValues = mSettings.getCurrent();
        if (settingsValues.mDisplayOrientation != conf.orientation) {
            mHandler.startOrientationChanging();
            mInputLogic.onOrientationChange(mSettings.getCurrent());
        }
        if (settingsValues.mHasHardwareKeyboard != Settings.readHasHardwareKeyboard(conf)) {
            // If the state of having a hardware keyboard changed, then we want to reload the
            // settings to adjust for that.
            // TODO: we should probably do this unconditionally here, rather than only when we
            // have a change in hardware keyboard configuration.
            loadSettings();
            settingsValues = mSettings.getCurrent();
            if (settingsValues.mHasHardwareKeyboard) {
                // We call cleanupInternalStateForFinishInput() because it's the right thing to do;
                // however, it seems at the moment the framework is passing us a seemingly valid
                // but actually non-functional InputConnection object. So if this bug ever gets
                // fixed we'll be able to remove the composition, but until it is this code is
                // actually not doing much.
                cleanupInternalStateForFinishInput();
            }
        }
        // TODO: Remove this test.
        if (!conf.locale.equals(PersonalizationDictionaryUpdater.getLocale())) {
            refreshPersonalizationDictionarySession(settingsValues);
        }
        super.onConfigurationChanged(conf);
    }

    @Override
    public View onCreateInputView() {
        StatsUtils.getInstance().onCreateInputView();
        return mKeyboardSwitcher.onCreateInputView(mIsHardwareAcceleratedDrawingEnabled);
    }

    @Override
    public void setInputView(final View view) {
        super.setInputView(view);
        mInputView = view;
        mSuggestionStripView = (SuggestionStripView) view.findViewById(R.id.suggestion_strip_view);
        this.mTopDisplayController = new TopDisplayController(view);
        if (hasSuggestionStripView()) {
            mSuggestionStripView.setListener(this, view);
            addObserver(mSuggestionStripView);
        }
        mInputLogic.setTextDecoratorUi(new TextDecoratorUi(this, view));
    }

    @Override
    public void setExtractView(final View view) {
        final TextView prevExtractEditText = mExtractEditText;
        super.setExtractView(view);
        TextView nextExtractEditText = null;
        if (view != null) {
            final View extractEditText = view.findViewById(android.R.id.inputExtractEditText);
            if (extractEditText instanceof TextView) {
                nextExtractEditText = (TextView) extractEditText;
            }
        }
        if (prevExtractEditText == nextExtractEditText) {
            return;
        }
        if (ProductionFlags.ENABLE_CURSOR_ANCHOR_INFO_CALLBACK && prevExtractEditText != null) {
            prevExtractEditText.getViewTreeObserver().removeOnPreDrawListener(
                    mExtractTextViewPreDrawListener);
        }
        mExtractEditText = nextExtractEditText;
        if (ProductionFlags.ENABLE_CURSOR_ANCHOR_INFO_CALLBACK && mExtractEditText != null) {
            mExtractEditText.getViewTreeObserver().addOnPreDrawListener(
                    mExtractTextViewPreDrawListener);
        }
    }

    private void onExtractTextViewPreDraw() {
        if (!ProductionFlags.ENABLE_CURSOR_ANCHOR_INFO_CALLBACK || !isFullscreenMode()
                || mExtractEditText == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final CursorAnchorInfo info = CursorAnchorInfoUtils.getCursorAnchorInfo(mExtractEditText);
            mInputLogic.onUpdateCursorAnchorInfo(CursorAnchorInfoCompatWrapper.fromObject(info));
        }
    }

    @Override
    public void setCandidatesView(final View view) {
        // To ensure that CandidatesView will never be set.
    }

    @Override
    public void onStartInput(final EditorInfo editorInfo, final boolean restarting) {
        if (isInputViewShown())
            handleKeyboardColor(editorInfo);


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();
        mResContext = setLocale(getApplicationContext(), locale);
        mHandler.onStartInput(editorInfo, restarting);
        if (getImeCurrentInputConnection() == null) {
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_INPUT_CONNECTION_NULL, null);
        } else if (getImeCurrentInputConnection() != null && !locale.equalsIgnoreCase(LocaleUtils.ENGLISH)) {
            CharSequence inputSequence = getImeCurrentInputConnection().getTextBeforeCursor(1, 0);

            if (inputSequence == null) {
                PointerTracker.KEYBOARD_TYPED_KEY = null;
            }
            if (inputSequence != null) {
                if (!inputSequence.toString().trim().isEmpty()) {
                    if (Character.UnicodeBlock.of(inputSequence.charAt(0)) != Character.UnicodeBlock.DEVANAGARI) {
                        PointerTracker.KEYBOARD_TYPED_KEY = null;
                    } else if (LocaleUtils.isNormalKeyLabel(getResources(), inputSequence.toString())) {
                        PointerTracker.KEYBOARD_TYPED_KEY = new Key(inputSequence.toString(), 0, 0, inputSequence.toString(),
                                null, 0, 0, 0, 0, 0, 0, 0, 0, false);
                    }
                } else {
                    PointerTracker.KEYBOARD_TYPED_KEY = null;
                }
            }
        }
    }

    @Override
    public void onStartInputView(final EditorInfo editorInfo, final boolean restarting) {
        handleKeyboardColor(editorInfo);
        mHandler.onStartInputView(editorInfo, restarting);
        mInputLogic.stopSearchingResults();
        this.mEventHandler.register();
        if (this.mTopDisplayController != null)
            this.mTopDisplayController.hideAll();
    }

    @Override
    public void onFinishInputView(final boolean finishingInput) {
        mHandler.onFinishInputView(finishingInput);
        if (this.mTopDisplayController != null)
            this.mTopDisplayController.hideAll();
        mInputLogic.stopSearchingResults();
        this.mEventHandler.unregister();
    }

    @Override
    public void onFinishInput() {
        mHandler.onFinishInput();
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(final InputMethodSubtype subtype) {
        // Note that the calling sequence of onCreate() and onCurrentInputMethodSubtypeChanged()
        // is not guaranteed. It may even be called at the same time on a different thread.
        StatsUtils.getInstance().onSubtypeChanged(subtype);
        mSubtypeSwitcher.onSubtypeChanged(subtype);
        mInputLogic.onSubtypeChanged(SubtypeLocaleUtils.getCombiningRulesExtraValue(subtype),
                mSettings.getCurrent());
        loadKeyboard();
        LocaleUtils.handleConfigurationChange(this);
        if (getImeCurrentInputConnection() == null) {
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_INPUT_CONNECTION_NULL, null);
        } else if (getImeCurrentInputConnection() != null) {
            CharSequence inputSequence = getImeCurrentInputConnection().getTextBeforeCursor(1, 0);

            if (inputSequence == null) {
                PointerTracker.KEYBOARD_TYPED_KEY = null;
            }
            if (inputSequence != null) {
                if (!inputSequence.toString().trim().isEmpty()) {
                    if (Character.UnicodeBlock.of(inputSequence.charAt(0)) != Character.UnicodeBlock.DEVANAGARI) {
                        PointerTracker.KEYBOARD_TYPED_KEY = null;
                    } else if (LocaleUtils.isNormalKeyLabel(getResources(), inputSequence.toString())) {
                        PointerTracker.KEYBOARD_TYPED_KEY = new Key(inputSequence.toString(), 0, 0, inputSequence.toString(),
                                null, 0, 0, 0, 0, 0, 0, 0, 0, false);
                    }
                } else {
                    PointerTracker.KEYBOARD_TYPED_KEY = null;
                }
            }
        }
    }

    private void onStartInputInternal(final EditorInfo editorInfo, final boolean restarting) {
        super.onStartInput(editorInfo, restarting);
    }

    private void handleKeyboardColor(EditorInfo editorInfo) {
        currentPackageName = editorInfo.packageName;
        StatsUtils.getInstance().updatePackageName(currentPackageName);
        this.colorManager.calculateProfile(this, currentPackageName);
    }

    public void getOverlayPermission() {
        Intent permissionIntent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.getPackageName()));
        permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(permissionIntent);
    }

    @SuppressWarnings("deprecation")
    private void onStartInputViewInternal(final EditorInfo editorInfo, final boolean restarting) {
        super.onStartInputView(editorInfo, restarting);
        mRichImm.clearSubtypeCaches();
        final KeyboardSwitcher switcher = mKeyboardSwitcher;
        switcher.updateKeyboardTheme();
        final MainKeyboardView mainKeyboardView = switcher.getMainKeyboardView();
        // If we are starting input in a different text field from before, we'll have to reload
        // settings, so currentSettingsValues can't be final.
        SettingsValues currentSettingsValues = mSettings.getCurrent();

        if (editorInfo == null) {
            Log.e(TAG, "Null EditorInfo in onStartInputView()");
            if (DebugFlags.DEBUG_ENABLED) {
                throw new NullPointerException("Null EditorInfo in onStartInputView()");
            }
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "onStartInputView: editorInfo:"
                    + String.format("inputType=0x%08x imeOptions=0x%08x",
                    editorInfo.inputType, editorInfo.imeOptions));
            Log.d(TAG, "All caps = "
                    + ((editorInfo.inputType & InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) != 0)
                    + ", sentence caps = "
                    + ((editorInfo.inputType & InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0)
                    + ", word caps = "
                    + ((editorInfo.inputType & InputType.TYPE_TEXT_FLAG_CAP_WORDS) != 0));
        }
        Log.i(TAG, "Starting input. Cursor position = "
                + editorInfo.initialSelStart + ',' + editorInfo.initialSelEnd);
        // TODO: Consolidate these checks with {@link InputAttributes}.
        if (InputAttributes.inPrivateImeOptions(null, NO_MICROPHONE_COMPAT, editorInfo)) {
            Log.w(TAG, "Deprecated private IME option specified: " + editorInfo.privateImeOptions);
            Log.w(TAG, "Use " + getPackageName() + '.' + NO_MICROPHONE + " instead");
        }
        if (InputAttributes.inPrivateImeOptions(getPackageName(), FORCE_ASCII, editorInfo)) {
            Log.w(TAG, "Deprecated private IME option specified: " + editorInfo.privateImeOptions);
            Log.w(TAG, "Use EditorInfo.IME_FLAG_FORCE_ASCII flag instead");
        }

        // In landscape mode, this method gets called without the input view being created.
        if (mainKeyboardView == null) {
            return;
        }

        // Forward this event to the accessibility utilities, if enabled.
        final AccessibilityUtils accessUtils = AccessibilityUtils.getInstance();
        if (accessUtils.isTouchExplorationEnabled()) {
            accessUtils.onStartInputViewInternal(mainKeyboardView, editorInfo, restarting);
        }

        final boolean inputTypeChanged = !currentSettingsValues.isSameInputType(editorInfo);
        final boolean isDifferentTextField = !restarting || inputTypeChanged;
        if (isDifferentTextField) {
            mSubtypeSwitcher.updateParametersOnStartInputView();
        }

        StatsUtils.getInstance().onStartInputView(editorInfo.inputType,
                Settings.getInstance().getCurrent().mDisplayOrientation,
                !isDifferentTextField);

        // The EditorInfo might have a flag that affects fullscreen mode.
        // Note: This call should be done by InputMethodService?
        updateFullscreenMode();

        // ALERT: settings have not been reloaded and there is a chance they may be stale.
        // In the practice, if it is, we should have gotten onConfigurationChanged so it should
        // be fine, but this is horribly confusing and must be fixed AS SOON AS POSSIBLE.

        // In some cases the input connection has not been reset yet and we can't access it. In
        // this case we will need to call loadKeyboard() later, when it's accessible, so that we
        // can go into the correct mode, so we need to do some housekeeping here.
        final boolean needToCallLoadKeyboardLater;
        final Suggest suggest = mInputLogic.mSuggest;
        if (!currentSettingsValues.mHasHardwareKeyboard) {
            // The app calling setText() has the effect of clearing the composing
            // span, so we should reset our state unconditionally, even if restarting is true.
            // We also tell the input logic about the combining rules for the current subtype, so
            // it can adjust its combiners if needed.
            mInputLogic.startInput(mSubtypeSwitcher.getCombiningRulesExtraValueOfCurrentSubtype(),
                    currentSettingsValues);

            // Note: the following does a round-trip IPC on the main thread: be careful
            final Locale currentLocale = mSubtypeSwitcher.getCurrentSubtypeLocale();
            if (null != currentLocale && !currentLocale.equals(suggest.getLocale())) {
                // TODO: Do this automatically.
                resetSuggest();
            }

            // TODO[IL]: Can the following be moved to InputLogic#startInput?
            if (!mInputLogic.mConnection.resetCachesUponCursorMoveAndReturnSuccess(
                    editorInfo.initialSelStart, editorInfo.initialSelEnd,
                    false /* shouldFinishComposition */)) {
                // Sometimes, while rotating, for some reason the framework tells the app we are not
                // connected to it and that means we can't refresh the cache. In this case, schedule
                // a refresh later.
                // We try resetting the caches up to 5 times before giving up.
                mHandler.postResetCaches(isDifferentTextField, 5 /* remainingTries */);
                // mLastSelection{Start,End} are reset later in this method, no need to do it here
                needToCallLoadKeyboardLater = true;
            } else {
                // When rotating, initialSelStart and initialSelEnd sometimes are lying. Make a best
                // effort to work around this bug.
                mInputLogic.mConnection.tryFixLyingCursorPosition();
                mHandler.postResumeSuggestions(true /* shouldIncludeResumedWordInSuggestions */,
                        true /* shouldDelay */);
                needToCallLoadKeyboardLater = false;
            }
        } else {
            // If we have a hardware keyboard we don't need to call loadKeyboard later anyway.
            needToCallLoadKeyboardLater = false;
        }

        if (isDifferentTextField ||
                !currentSettingsValues.hasSameOrientation(getResources().getConfiguration())) {
            loadSettings();
        }
        if (isDifferentTextField) {
            mainKeyboardView.closing();
            currentSettingsValues = mSettings.getCurrent();

            if (currentSettingsValues.mAutoCorrectionEnabledPerUserSettings) {
                suggest.setAutoCorrectionThreshold(
                        currentSettingsValues.mAutoCorrectionThreshold);
            }

            switcher.loadKeyboard(editorInfo, currentSettingsValues, getCurrentAutoCapsState(),
                    getCurrentRecapitalizeState());
            if (needToCallLoadKeyboardLater) {
                // If we need to call loadKeyboard again later, we need to save its state now. The
                // later call will be done in #retryResetCaches.
                switcher.saveKeyboardState();
            }
        } else if (restarting) {
            // TODO: Come up with a more comprehensive way to reset the keyboard layout when
            // a keyboard layout set doesn't get reloaded in this method.
            switcher.resetKeyboardStateToAlphabet(getCurrentAutoCapsState(),
                    getCurrentRecapitalizeState());
            // In apps like Talk, we come here when the text is sent and the field gets emptied and
            // we need to re-evaluate the shift state, but not the whole layout which would be
            // disruptive.
            // Space state must be updated before calling updateShiftState
            switcher.requestUpdatingShiftState(getCurrentAutoCapsState(),
                    getCurrentRecapitalizeState());
        }
        // This will set the punctuation suggestions if next word suggestion is off;
        // otherwise it will clear the suggestion strip.
        setNeutralSuggestionStrip();

        mHandler.cancelUpdateSuggestionStrip();

        MainKeyboardView.setMainDictionaryAvailability(
                mDictionaryFacilitator.hasInitializedMainDictionary());
        mainKeyboardView.setKeyPreviewPopupEnabled(currentSettingsValues.mKeyPreviewPopupOn,
                currentSettingsValues.mKeyPreviewPopupDismissDelay);
        mainKeyboardView.setSlidingKeyInputPreviewEnabled(
                currentSettingsValues.mSlidingKeyInputPreviewEnabled);
        mainKeyboardView.setGestureHandlingEnabledByUser(
                currentSettingsValues.mGestureInputEnabled,
                currentSettingsValues.mGestureTrailEnabled,
                currentSettingsValues.mGestureFloatingPreviewTextEnabled);

        // Contextual dictionary should be updated for the current application.
        mContextualDictionaryUpdater.onStartInputView(editorInfo.packageName);
        if (TRACE) Debug.startMethodTracing("/data/trace/latinime");
    }

    public void onWindowShown() {
        super.onWindowShown();
        isWindowHidden = false;
        if (this.navManager != null) {
            this.navManager.show();
        }
        /*if (mTopDisplayController.isActionViewVisible() && serviceTab != null) {
            EventBusExt.getDefault().post(new ShowActionRowEvent());
            this.mKeyboardSwitcher.setProductShareKeyboardFrame(serviceTab);
        } else {
            EventBusExt.getDefault().post(new ShowSuggestionsEvent());
        }*/
        EventBusExt.getDefault().post(new ShowSuggestionsEvent());
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        if (this.navManager != null) {
            this.navManager.hide();
        }
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        if (mainKeyboardView != null) {
            mainKeyboardView.closing();
        }
    }

    private void onFinishInputInternal() {
        super.onFinishInput();

        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        if (mainKeyboardView != null) {
            mainKeyboardView.closing();
        }
    }

    private void onFinishInputViewInternal(final boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        cleanupInternalStateForFinishInput();
    }

    private void cleanupInternalStateForFinishInput() {
        mKeyboardSwitcher.deallocateMemory();
        // Remove pending messages related to update suggestions
        mHandler.cancelUpdateSuggestionStrip();
        // Should do the following in onFinishInputInternal but until JB MR2 it's not called :(
        mInputLogic.finishInput();
    }

    @Override
    public void onUpdateSelection(final int oldSelStart, final int oldSelEnd,
                                  final int newSelStart, final int newSelEnd,
                                  final int composingSpanStart, final int composingSpanEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                composingSpanStart, composingSpanEnd);
        if (DEBUG) {
            Log.i(TAG, "onUpdateSelection: oss=" + oldSelStart + ", ose=" + oldSelEnd
                    + ", nss=" + newSelStart + ", nse=" + newSelEnd
                    + ", cs=" + composingSpanStart + ", ce=" + composingSpanEnd);
        }

        // This call happens when we have a hardware keyboard as well as when we don't. While we
        // don't support hardware keyboards yet we should avoid doing the processing associated
        // with cursor movement when we have a hardware keyboard since we are not in charge.
        final SettingsValues settingsValues = mSettings.getCurrent();
        if ((!settingsValues.mHasHardwareKeyboard || ProductionFlags.IS_HARDWARE_KEYBOARD_SUPPORTED)
                && mInputLogic.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                settingsValues)) {
            mKeyboardSwitcher.requestUpdatingShiftState(getCurrentAutoCapsState(),
                    getCurrentRecapitalizeState());
        }
    }

    // We cannot mark this method as @Override until new SDK becomes publicly available.
    // @Override
    public void onUpdateCursorAnchorInfo(final CursorAnchorInfo info) {
        if (!ProductionFlags.ENABLE_CURSOR_ANCHOR_INFO_CALLBACK || isFullscreenMode()) {
            return;
        }
        mInputLogic.onUpdateCursorAnchorInfo(CursorAnchorInfoCompatWrapper.fromObject(info));
    }

    /**
     * This is called when the user has clicked on the extracted text view,
     * when running in fullscreen mode.  The default implementation hides
     * the suggestions view when this happens, but only if the extracted text
     * editor has a vertical scroll bar because its text doesn't fit.
     * Here we override the behavior due to the possibility that a re-correction could
     * cause the suggestions strip to disappear and re-appear.
     */
    @Override
    public void onExtractedTextClicked() {
        if (mSettings.getCurrent().needsToLookupSuggestions()) {
            return;
        }

        super.onExtractedTextClicked();
    }

    /**
     * This is called when the user has performed a cursor movement in the
     * extracted text view, when it is running in fullscreen mode.  The default
     * implementation hides the suggestions view when a vertical movement
     * happens, but only if the extracted text editor has a vertical scroll bar
     * because its text doesn't fit.
     * Here we override the behavior due to the possibility that a re-correction could
     * cause the suggestions strip to disappear and re-appear.
     */
    @Override
    public void onExtractedCursorMovement(final int dx, final int dy) {
        if (mSettings.getCurrent().needsToLookupSuggestions()) {
            return;
        }

        super.onExtractedCursorMovement(dx, dy);
    }

    @Override
    public void hideWindow() {
        mKeyboardSwitcher.onHideWindow();

        if (TRACE) Debug.stopMethodTracing();
        if (isShowingOptionDialog()) {
            mOptionsDialog.dismiss();
            mOptionsDialog = null;
        }
        super.hideWindow();
    }

    @Override
    public void onDisplayCompletions(final CompletionInfo[] applicationSpecifiedCompletions) {
        if (DEBUG) {
            Log.i(TAG, "Received completions:");
            if (applicationSpecifiedCompletions != null) {
                for (int i = 0; i < applicationSpecifiedCompletions.length; i++) {
                    Log.i(TAG, "  #" + i + ": " + applicationSpecifiedCompletions[i]);
                }
            }
        }
        if (!mSettings.getCurrent().isApplicationSpecifiedCompletionsOn()) {
            return;
        }
        // If we have an update request in flight, we need to cancel it so it does not override
        // these completions.
        mHandler.cancelUpdateSuggestionStrip();
        if (applicationSpecifiedCompletions == null) {
            setNeutralSuggestionStrip();
            return;
        }

        final ArrayList<SuggestedWords.SuggestedWordInfo> applicationSuggestedWords =
                SuggestedWords.getFromApplicationSpecifiedCompletions(
                        applicationSpecifiedCompletions);
        final SuggestedWords suggestedWords = new SuggestedWords(applicationSuggestedWords,
                null /* rawSuggestions */, false /* typedWordValid */, false /* willAutoCorrect */,
                false /* isObsoleteSuggestions */,
                SuggestedWords.INPUT_STYLE_APPLICATION_SPECIFIED /* inputStyle */);
        // When in fullscreen mode, show completions generated by the application forcibly
        setSuggestedWords(suggestedWords);
    }

    @Override
    public void onComputeInsets(final InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);
        final SettingsValues settingsValues = mSettings.getCurrent();
        final View visibleKeyboardView = mKeyboardSwitcher.getVisibleKeyboardView();
        if (visibleKeyboardView == null)//|| !hasSuggestionStripView()) {
            return;

        final int inputHeight = mInputView.getHeight();
        final boolean hasHardwareKeyboard = settingsValues.mHasHardwareKeyboard;
        if (hasHardwareKeyboard && visibleKeyboardView.getVisibility() == View.GONE) {
            // If there is a hardware keyboard and a visible software keyboard view has been hidden,
            // no visual element will be shown on the screen.
            outInsets.touchableInsets = inputHeight;
            outInsets.visibleTopInsets = inputHeight;
            return;
        }
        //SEPAR TODO::this is for the action row (if emoji => set to buttombar size + rich keyboard)
        /*final int suggestionsHeight = (!mKeyboardSwitcher.isShowingEmojiPalettes()) ?
                mTopDisplayController.getHeight() : mSuggestionStripView.getHeight();*/
        final int suggestionsHeight = (mTopDisplayController != null) ? mTopDisplayController.getHeight() : 0;
        final int visibleTopY = inputHeight - visibleKeyboardView.getHeight() - suggestionsHeight;
        // Need to set touchable region only if a keyboard view is being shown.
        if (visibleKeyboardView.isShown()) {
            final int touchTop = mKeyboardSwitcher.isShowingMoreKeysPanel() ? 0 : visibleTopY;
            final int touchRight = visibleKeyboardView.getWidth();
            final int touchBottom = inputHeight
                    // Extend touchable region below the keyboard.
                    + EXTENDED_TOUCHABLE_REGION_HEIGHT;
            outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_REGION;
            final int touchLeft = 0;
            outInsets.touchableRegion.set(touchLeft, touchTop, touchRight, touchBottom);
        }
        outInsets.contentTopInsets = visibleTopY;
        outInsets.visibleTopInsets = visibleTopY;
    }

    public void startShowingInputView() {
        mIsExecutingStartShowingInputView = true;
        // This {@link #showWindow(boolean)} will eventually call back
        // {@link #onEvaluateInputViewShown()}.
        showWindow(true /* showInput */);
        mIsExecutingStartShowingInputView = false;
    }

    public void stopShowingInputView() {
        showWindow(false /* showInput */);
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        return mIsExecutingStartShowingInputView || super.onEvaluateInputViewShown();
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        final SettingsValues settingsValues = mSettings.getCurrent();
        if (settingsValues.mHasHardwareKeyboard) {
            // If there is a hardware keyboard, disable full screen mode.
            return false;
        }
        // Reread resource value here, because this method is called by the framework as needed.
        final boolean isFullscreenModeAllowed = Settings.readUseFullscreenMode(getResources());
        if (super.onEvaluateFullscreenMode() && isFullscreenModeAllowed) {
            // TODO: Remove this hack. Actually we should not really assume NO_EXTRACT_UI
            // implies NO_FULLSCREEN. However, the framework mistakenly does.  i.e. NO_EXTRACT_UI
            // without NO_FULLSCREEN doesn't work as expected. Because of this we need this
            // hack for now.  Let's get rid of this once the framework gets fixed.
            final EditorInfo ei = getCurrentInputEditorInfo();
            return !(ei != null && ((ei.imeOptions & EditorInfo.IME_FLAG_NO_EXTRACT_UI) != 0));
        }
        return false;
    }

    @Override
    public void updateFullscreenMode() {
        // Override layout parameters to expand {@link SoftInputWindow} to the entire screen.
        // See {@link InputMethodService#setinputView(View) and
        // {@link SoftInputWindow#updateWidthHeight(WindowManager.LayoutParams)}.
        final Window window = getWindow().getWindow();
        ViewLayoutUtils.updateLayoutHeightOf(window, LayoutParams.MATCH_PARENT);
        // This method may be called before {@link #setInputView(View)}.
        if (mInputView != null) {
            // In non-fullscreen mode, {@link InputView} and its parent inputArea should expand to
            // the entire screen and be placed at the bottom of {@link SoftInputWindow}.
            // In fullscreen mode, these shouldn't expand to the entire screen and should be
            // coexistent with {@link #mExtractedArea} above.
            // See {@link InputMethodService#setInputView(View) and
            // com.android.internal.R.layout.input_method.xml.
            final int layoutHeight = isFullscreenMode()
                    ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT;
            final View inputArea = window.findViewById(android.R.id.inputArea);
            ViewLayoutUtils.updateLayoutHeightOf(inputArea, layoutHeight);
            ViewLayoutUtils.updateLayoutGravityOf(inputArea, Gravity.BOTTOM);
            ViewLayoutUtils.updateLayoutHeightOf(mInputView, layoutHeight);
        }
        super.updateFullscreenMode();
        mInputLogic.onUpdateFullscreenMode(isFullscreenMode());
    }

    private int getCurrentAutoCapsState() {
        return mInputLogic.getCurrentAutoCapsState(mSettings.getCurrent());
    }

    private int getCurrentRecapitalizeState() {
        return mInputLogic.getCurrentRecapitalizeState();
    }

    public Locale getCurrentSubtypeLocale() {
        return mSubtypeSwitcher.getCurrentSubtypeLocale();
    }

    /**
     * @param codePoints code points to get coordinates for.
     * @return x, y coordinates for this keyboard, as a flattened array.
     */
    public int[] getCoordinatesForCurrentKeyboard(final int[] codePoints) {
        final Keyboard keyboard = mKeyboardSwitcher.getKeyboard();
        if (null == keyboard) {
            return CoordinateUtils.newCoordinateArray(codePoints.length,
                    Constants.NOT_A_COORDINATE, Constants.NOT_A_COORDINATE);
        }
        return keyboard.getCoordinates(codePoints);
    }

    // Callback for the {@link SuggestionStripView}, to call when the "add to dictionary" hint is
    // pressed.
    @Override
    public void addWordToUserDictionary(final String word) {
        if (TextUtils.isEmpty(word)) {
            // Probably never supposed to happen, but just in case.
            return;
        }
        final String wordToEdit;
        if (CapsModeUtils.isAutoCapsMode(mInputLogic.mLastComposedWord.mCapitalizedMode)) {
            wordToEdit = word.toLowerCase(getCurrentSubtypeLocale());
        } else {
            wordToEdit = word;
        }
        mDictionaryFacilitator.addWordToUserDictionary(this /* context */, wordToEdit);
        mInputLogic.onAddWordToUserDictionary();
    }

    // Callback for the {@link SuggestionStripView}, to call when the important notice strip is
    // pressed.
    @Override
    public void showImportantNoticeContents() {
        showOptionDialog(new ImportantNoticeDialog(this /* context */, this /* listener */));
    }

    // Implement {@link ImportantNoticeDialog.ImportantNoticeDialogListener}
    @Override
    public void onClickSettingsOfImportantNoticeDialog(final int nextVersion) {
        launchSettings();
    }

    // Implement {@link ImportantNoticeDialog.ImportantNoticeDialogListener}
    @Override
    public void onUserAcknowledgmentOfImportantNoticeDialog(final int nextVersion) {
        setNeutralSuggestionStrip();
    }

    public void displaySettingsDialog() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("nowfloats://" + getApplicationContext().getPackageName() + ".keyboard.home/addproduct"));
        intent.putExtra("from", "notification");
        intent.putExtra("url", "keyboardSettings");
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (intent.resolveActivity(getPackageManager()) != null) {
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_THEME_NAVIGATION_THROUGH_KEYBOARD, null);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCustomRequest(final int requestCode) {
        if (isShowingOptionDialog()) return false;
        switch (requestCode) {
            case Constants.CUSTOM_CODE_SHOW_INPUT_METHOD_PICKER:
                if (mRichImm.hasMultipleEnabledIMEsOrSubtypes(true /* include aux subtypes */)) {
                    mRichImm.getInputMethodManager().showInputMethodPicker();
                    return true;
                }
        }
        return false;
    }

    private boolean isShowingOptionDialog() {
        return mOptionsDialog != null && mOptionsDialog.isShowing();
    }

    public void switchToNextSubtype() {
        final IBinder token = getWindow().getWindow().getAttributes().token;
        mSubtypeState.switchSubtype(token, mRichImm);
    }

    // Implementation of {@link KeyboardActionListener}.
    @Override
    public void onCodeInput(final int codePoint, final int x, final int y,
                            final boolean isKeyRepeat) {
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        // x and y include some padding, but everything down the line (especially native
        // code) needs the coordinates in the keyboard frame.
        // TODO: We should reconsider which coordinate system should be used to represent
        // keyboard event. Also we should pull this up -- LatinIME has no business doing
        // this transformation, it should be done already before calling onCodeInput.
        final int keyX = mainKeyboardView.getKeyX(x);
        final int keyY = mainKeyboardView.getKeyY(y);
        final int codeToSend;
        if (Constants.CODE_SHIFT == codePoint) {
            // TODO: Instead of checking for alphabetic keyboard here, separate keycodes for
            // alphabetic shift and shift while in symbol layout.
            final Keyboard currentKeyboard = mKeyboardSwitcher.getKeyboard();
            if (null != currentKeyboard && currentKeyboard.mId.isAlphabetKeyboard()) {
                codeToSend = codePoint;
            } else {
                codeToSend = Constants.CODE_SYMBOL_SHIFT;
            }
        } else {
            codeToSend = codePoint;
        }
        if (Constants.CODE_SHORTCUT == codePoint) {
            /*SpeechUtils.startListening();
            Log.e("SEPAR", "onCodeInput: "+SpeechUtils.data);*/
            mSubtypeSwitcher.switchToShortcutIME(this); /*SEPAR*/

        }
        if (Constants.CODE_INLINESETTINGS == codePoint) {
            //mKeyboardSwitcher.onToggleSettingsKeyboard();
        }
        if (Constants.CODE_LANGUAGE_SWITCH == codePoint)
            switchToNextSubtype();
        final Event event = createSoftwareKeypressEvent(codeToSend, keyX, keyY, isKeyRepeat);
        final InputTransaction completeInputTransaction =
                mInputLogic.onCodeInput(mSettings.getCurrent(), event,
                        mKeyboardSwitcher.getKeyboardShiftMode(),
                        mKeyboardSwitcher.getCurrentKeyboardScriptId(), mHandler);
        int newCodePoint = updateSearchAfterCodeInput(codePoint);
        updateStateAfterInputTransaction(completeInputTransaction);
        mKeyboardSwitcher.onCodeInput(newCodePoint, getCurrentAutoCapsState(),
                getCurrentRecapitalizeState());
    }

    private int updateSearchAfterCodeInput(int codeToSend) {
        if (codeToSend == Constants.CODE_EMOJI && mInputLogic.isSearchingResults()) {
            LatinIME.this.mInputLogic.stopSearchingResults();
            mTopDisplayController.hideAll();
            return Constants.CODE_UNSPECIFIED;
        }
        if (mInputLogic.isSearchingResults() && mInputLogic.getShouldReSearch())
            mTopDisplayController.runSearch(mInputLogic.getSearchText());
        return codeToSend;
    }

    public void SendRichContentSample(int id) {
        final File imagesDir = new File(getFilesDir(), "images");
        imagesDir.mkdirs();
        int resId = 0;
        String type;
        String name;
        if (id == 0) {
            //resId = R.raw.setup_welcome_image;
            type = RichInputConnection.MIME_TYPE_PNG;
            name = "image.png";
        } else if (id == 1) {
            //resId = R.raw.animated_gif;
            type = RichInputConnection.MIME_TYPE_GIF;
            name = "image.gif";
        } else {
            //resId = R.raw.animated_webp;
            type = RichInputConnection.MIME_TYPE_WEBP;
            name = "image.webp";
        }
        File mPngFile = RichInputConnection.getFileForResource(this, resId, imagesDir, name);
        Intent resInt = mInputLogic.mConnection.doCommitContent("Test", type, mPngFile);
        if (resInt != null) {
            resInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(resInt);
        }
    }

    @Override
    public void onEmojiInput(final String rawText) {
        long currentTime = System.currentTimeMillis();
        if ((lastEmojiInsertionTime + 200) > currentTime)
            return;
        lastEmojiInsertionTime = currentTime;
        FrequentEmojiHandler.getInstance(this).onEmojiClicked(rawText);
        onTextInput(rawText);
        StatsUtils.getInstance().onRichEmojiSelected();
    }

    // Called from PointerTracker through the KeyboardActionListener interface
    @Override
    public void onTextInput(final String rawText) {
        // TODO: have the keyboard pass the correct key code when we need it.
        final Event event = Event.createSoftwareTextEvent(rawText, Event.NOT_A_KEY_CODE);
        final InputTransaction completeInputTransaction =
                mInputLogic.onTextInput(mSettings.getCurrent(), event,
                        mKeyboardSwitcher.getKeyboardShiftMode(), mHandler);
        updateStateAfterInputTransaction(completeInputTransaction);
        mKeyboardSwitcher.onCodeInput(Constants.CODE_OUTPUT_TEXT, getCurrentAutoCapsState(),
                getCurrentRecapitalizeState());
    }

    @Override
    public void onStartBatchInput() {
        mInputLogic.onStartBatchInput(mSettings.getCurrent(), mKeyboardSwitcher, mHandler);
    }

    @Override
    public void onUpdateBatchInput(final InputPointers batchPointers) {
        mInputLogic.onUpdateBatchInput(mSettings.getCurrent(), batchPointers, mKeyboardSwitcher);
        if (mTopDisplayController != null)
            mTopDisplayController.showSuggestions(false);
    }

    @Override
    public void onEndBatchInput(final InputPointers batchPointers) {
        mInputLogic.onEndBatchInput(batchPointers);
    }

    @Override
    public void onCancelBatchInput() {
        mInputLogic.onCancelBatchInput(mHandler);
    }

    // This method must run on the UI Thread.
    private void showGesturePreviewAndSuggestionStrip(final SuggestedWords suggestedWords,
                                                      final boolean dismissGestureFloatingPreviewText) {
        showSuggestionStrip(suggestedWords);
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        mainKeyboardView.showGestureFloatingPreviewText(suggestedWords);
        if (dismissGestureFloatingPreviewText) {
            mainKeyboardView.dismissGestureFloatingPreviewText();
        }
    }

    // Called from PointerTracker through the KeyboardActionListener interface
    @Override
    public void onFinishSlidingInput() {
        // User finished sliding input.
        mKeyboardSwitcher.onFinishSlidingInput(getCurrentAutoCapsState(),
                getCurrentRecapitalizeState());
    }

    // Called from PointerTracker through the KeyboardActionListener interface
    @Override
    public void onCancelInput() {
        // User released a finger outside any key
        // Nothing to do so far.
    }

    public boolean hasSuggestionStripView() {
        return (null != mSuggestionStripView);
    }

    @Override
    public boolean isShowingAddToDictionaryHint() {
        return hasSuggestionStripView() && mSuggestionStripView.isShowingAddToDictionaryHint();
    }

    @Override
    public void dismissAddToDictionaryHint() {
        if (!hasSuggestionStripView()) {
            return;
        }
        mSuggestionStripView.dismissAddToDictionaryHint();
    }

    private void setSuggestedWords(final SuggestedWords suggestedWords) {
        final SettingsValues currentSettingsValues = mSettings.getCurrent();
        mInputLogic.setSuggestedWords(suggestedWords, currentSettingsValues, mHandler);
        // TODO: Modify this when we support suggestions with hard keyboard
        if (!hasSuggestionStripView()) {
            return;
        }
        if (!onEvaluateInputViewShown()) {
            return;
        }

        //final boolean shouldShowImportantNotice = ImportantNoticeUtils.shouldShowImportantNotice(this);
        final boolean shouldShowSuggestionCandidates = true;

        final boolean shouldShowSuggestionsStripUnlessPassword = /*shouldShowImportantNotice
                || currentSettingsValues.mShowsVoiceInputKey
                ||*/ shouldShowSuggestionCandidates
                || currentSettingsValues.isApplicationSpecifiedCompletionsOn();
        boolean shouldShowSuggestionsStrip = shouldShowSuggestionsStripUnlessPassword
                && !currentSettingsValues.mInputAttributes.mIsPasswordField
                && !mKeyboardSwitcher.isShowingEmojiPalettes();
        if (!shouldShowSuggestionsStrip) {
            if (!mTopDisplayController.isActionViewVisible()) {
                mTopDisplayController.showActionRow(getApplicationContext());
            }
            return;
        }
        //shouldShowSuggestionsStrip = true;
        //&& mKeyboardSwitcher.getKeyboard().mId.mElementId<=ELEMENT_ALPHABET_SHIFT_LOCK_SHIFTED;
        mSuggestionStripView.updateVisibility(shouldShowSuggestionsStrip, isFullscreenMode());


        final boolean isEmptyApplicationSpecifiedCompletions = currentSettingsValues.isApplicationSpecifiedCompletionsOn()
                && suggestedWords.isEmpty();
        final boolean noSuggestionsFromDictionaries = (SuggestedWords.EMPTY == suggestedWords)
                || suggestedWords.isPunctuationSuggestions()
                || isEmptyApplicationSpecifiedCompletions;
        final boolean isBeginningOfSentencePrediction = (suggestedWords.mInputStyle
                == SuggestedWords.INPUT_STYLE_BEGINNING_OF_SENTENCE_PREDICTION);
        /*final boolean noSuggestionsToOverrideImportantNotice = noSuggestionsFromDictionaries
                || isBeginningOfSentencePrediction;
        if (shouldShowImportantNotice && noSuggestionsToOverrideImportantNotice) {
            if (mSuggestionStripView.maybeShowImportantNoticeTitle()) {
                return;
            }
        }*/

        if (currentSettingsValues.isSuggestionsEnabledPerUserSettings()
                || currentSettingsValues.isApplicationSpecifiedCompletionsOn()
                // We should clear the contextual strip if there is no suggestion from dictionaries.
                || noSuggestionsFromDictionaries) {
            mSuggestionStripView.setSuggestions(suggestedWords,
                    SubtypeLocaleUtils.isRtlLanguage(mSubtypeSwitcher.getCurrentSubtype()));
        }
    }

    // TODO[IL]: Move this out of LatinIME.
    public void getSuggestedWords(final int inputStyle, final int sequenceNumber,
                                  final OnGetSuggestedWordsCallback callback) {
        final Keyboard keyboard = mKeyboardSwitcher.getKeyboard();
        if (keyboard == null) {
            callback.onGetSuggestedWords(SuggestedWords.EMPTY);
            return;
        }
        mInputLogic.getSuggestedWords(mSettings.getCurrent(), keyboard.getProximityInfo(),
                mKeyboardSwitcher.getKeyboardShiftMode(), inputStyle, sequenceNumber, callback);
    }

    @Override
    public void showSuggestionStrip(final SuggestedWords sourceSuggestedWords) {
        final SuggestedWords suggestedWords =
                sourceSuggestedWords.isEmpty() ? SuggestedWords.EMPTY : sourceSuggestedWords;
        if (SuggestedWords.EMPTY == suggestedWords) {
            setNeutralSuggestionStrip();
        } else {
            setSuggestedWords(suggestedWords);
        }
        // Cache the auto-correction in accessibility code so we can speak it if the user
        // touches a key that will insert it.
        AccessibilityUtils.getInstance().setAutoCorrection(suggestedWords,
                sourceSuggestedWords.mTypedWord);
    }

    // Called from {@link SuggestionStripView} through the {@link SuggestionStripView#Listener}
    // interface
    @Override
    public void pickSuggestionManually(final SuggestedWordInfo suggestionInfo) {
        final InputTransaction completeInputTransaction = mInputLogic.onPickSuggestionManually(
                mSettings.getCurrent(), suggestionInfo,
                mKeyboardSwitcher.getKeyboardShiftMode(),
                mKeyboardSwitcher.getCurrentKeyboardScriptId(),
                mHandler);
        updateStateAfterInputTransaction(completeInputTransaction);
    }

    @Override
    public void showAddToDictionaryHint(final String word) {
        if (!hasSuggestionStripView()) {
            return;
        }
        mSuggestionStripView.showAddToDictionaryHint(word);
    }

    // This will show either an empty suggestion strip (if prediction is enabled) or
    // punctuation suggestions (if it's disabled).
    @Override
    public void setNeutralSuggestionStrip() {
        final SettingsValues currentSettings = mSettings.getCurrent();
        final SuggestedWords neutralSuggestions = currentSettings.mBigramPredictionEnabled
                ? SuggestedWords.EMPTY : currentSettings.mSpacingAndPunctuations.mSuggestPuncList;
        setSuggestedWords(neutralSuggestions);
    }

    // TODO: Make this private
    // Outside LatinIME, only used by the {@link InputTestsBase} test suite.
    @UsedForTesting
    void loadKeyboard() {
        // Since we are switching languages, the most urgent thing is to let the keyboard graphics
        // update. LoadKeyboard does that, but we need to wait for buffer flip for it to be on
        // the screen. Anything we do right now will delay this, so wait until the next frame
        // before we do the rest, like reopening dictionaries and updating suggestions. So we
        // post a message.
        mHandler.postReopenDictionaries();
        loadSettings();
        if (mKeyboardSwitcher.getMainKeyboardView() != null) {
            // Reload keyboard because the current language has been changed.
            mKeyboardSwitcher.loadKeyboard(getCurrentInputEditorInfo(), mSettings.getCurrent(),
                    getCurrentAutoCapsState(), getCurrentRecapitalizeState());
        }
    }

    private void updateStateAfterInputTransaction(final InputTransaction inputTransaction, boolean showSuggest) {
        switch (inputTransaction.getRequiredShiftUpdate()) {
            case InputTransaction.SHIFT_UPDATE_LATER:
                mHandler.postUpdateShiftState();
                break;
            case InputTransaction.SHIFT_UPDATE_NOW:
                mKeyboardSwitcher.requestUpdatingShiftState(getCurrentAutoCapsState(),
                        getCurrentRecapitalizeState());
                break;
            default: // SHIFT_NO_UPDATE
        }
        if (inputTransaction.requiresUpdateSuggestions()) {
            final int inputStyle;
            if (inputTransaction.mEvent.isSuggestionStripPress()) {
                // Suggestion strip press: no input.
                inputStyle = SuggestedWords.INPUT_STYLE_NONE;
            } else if (inputTransaction.mEvent.isGesture()) {
                inputStyle = SuggestedWords.INPUT_STYLE_TAIL_BATCH;
            } else {
                inputStyle = SuggestedWords.INPUT_STYLE_TYPING;
            }
            mHandler.postUpdateSuggestionStrip(inputStyle);
        }
        if (inputTransaction.didAffectContents()) {
            mSubtypeState.setCurrentSubtypeHasBeenUsed();
        }
        if ((inputTransaction.mEvent.mKeyCode >= 0 || inputTransaction.mEvent.mKeyCode == Constants.CODE_DELETE) && mTopDisplayController != null && showSuggest) {
            SettingsValues currentSettingsValues = mSettings.getCurrent();

            final boolean shouldShowSuggestionsStripUnlessPassword =
                    currentSettingsValues.mShowsVoiceInputKey
                            || currentSettingsValues.isApplicationSpecifiedCompletionsOn();
            boolean shouldShowSuggestionsStrip = shouldShowSuggestionsStripUnlessPassword
                    && !currentSettingsValues.mInputAttributes.mIsPasswordField
                    && !mKeyboardSwitcher.isShowingEmojiPalettes();
            if (!shouldShowSuggestionsStrip) {
                return;
            }
            mTopDisplayController.showSuggestions(false);
        }
    }

    /**
     * After an input transaction has been executed, some state must be updated. This includes
     * the shift state of the keyboard and suggestions. This method looks at the finished
     * inputTransaction to find out what is necessary and updates the state accordingly.
     *
     * @param inputTransaction The transaction that has been executed.
     */
    private void updateStateAfterInputTransaction(final InputTransaction inputTransaction) {
        updateStateAfterInputTransaction(inputTransaction, true);
    }

    private void hapticAndAudioFeedback(final int code, final int repeatCount) {
        final MainKeyboardView keyboardView = mKeyboardSwitcher.getMainKeyboardView();
        if (keyboardView != null && keyboardView.isInDraggingFinger()) {
            // No need to feedback while finger is dragging.
            return;
        }
        if (repeatCount > 0) {
            if (code == Constants.CODE_DELETE && !mInputLogic.mConnection.canDeleteCharacters()) {
                // No need to feedback when repeat delete key will have no effect.
                return;
            }
            // TODO: Use event time that the last feedback has been generated instead of relying on
            // a repeat count to thin out feedback.
            if (repeatCount % PERIOD_FOR_AUDIO_AND_HAPTIC_FEEDBACK_IN_KEY_REPEAT == 0) {
                return;
            }
        }
        final AudioAndHapticFeedbackManager feedbackManager =
                AudioAndHapticFeedbackManager.getInstance();
        if (repeatCount == 0) {
            // TODO: Reconsider how to perform haptic feedback when repeating key.
            feedbackManager.performHapticFeedback(keyboardView);
        }
        feedbackManager.performAudioFeedback(code);
    }

    // Callback of the {@link KeyboardActionListener}. This is called when a key is depressed;
    // release matching call is {@link #onReleaseKey(int,boolean)} below.
    @Override
    public void onPressKey(final int primaryCode, final int repeatCount,
                           final boolean isSinglePointer) {
        mKeyboardSwitcher.onPressKey(primaryCode, isSinglePointer, getCurrentAutoCapsState(),
                getCurrentRecapitalizeState());
        hapticAndAudioFeedback(primaryCode, repeatCount);
    }

    // Callback of the {@link KeyboardActionListener}. This is called when a key is released;
    // press matching call is {@link #onPressKey(int,int,boolean)} above.
    @Override
    public void onReleaseKey(final int primaryCode, final boolean withSliding) {
        mKeyboardSwitcher.onReleaseKey(primaryCode, withSliding, getCurrentAutoCapsState(),
                getCurrentRecapitalizeState());
    }

    private HardwareEventDecoder getHardwareKeyEventDecoder(final int deviceId) {
        final HardwareEventDecoder decoder = mHardwareEventDecoders.get(deviceId);
        if (null != decoder) return decoder;
        // TODO: create the decoder according to the specification
        final HardwareEventDecoder newDecoder = new HardwareKeyboardEventDecoder(deviceId);
        mHardwareEventDecoders.put(deviceId, newDecoder);
        return newDecoder;
    }

    // onKeyDown and onKeyUp are the main events we are interested in. There are two more events
    // related to handling of hardware key events that we may want to implement in the future:
    // boolean onKeyLongPress(final int keyCode, final KeyEvent event);
    // boolean onKeyMultiple(final int keyCode, final int count, final KeyEvent event);

    // Hooks for hardware keyboard
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent keyEvent) {
        mSpecialKeyDetector.onKeyDown(keyEvent);
        if (!ProductionFlags.IS_HARDWARE_KEYBOARD_SUPPORTED) {
            return super.onKeyDown(keyCode, keyEvent);
        }
        final Event event = getHardwareKeyEventDecoder(
                keyEvent.getDeviceId()).decodeHardwareKey(keyEvent);
        // If the event is not handled by LatinIME, we just pass it to the parent implementation.
        // If it's handled, we return true because we did handle it.
        if (event.isHandled()) {
            mInputLogic.onCodeInput(mSettings.getCurrent(), event,
                    mKeyboardSwitcher.getKeyboardShiftMode(),
                    // TODO: this is not necessarily correct for a hardware keyboard right now
                    mKeyboardSwitcher.getCurrentKeyboardScriptId(),
                    mHandler);
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent keyEvent) {
        mSpecialKeyDetector.onKeyUp(keyEvent);
        if (!ProductionFlags.IS_HARDWARE_KEYBOARD_SUPPORTED) {
            return super.onKeyUp(keyCode, keyEvent);
        }
        final long keyIdentifier = keyEvent.getDeviceId() << 32 + keyEvent.getKeyCode();
        return mInputLogic.mCurrentlyPressedHardwareKeys.remove(keyIdentifier) || super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    private void launchSettings() {
        if (mInputLogic.isSearchingResults()) {
            LatinIME.this.mInputLogic.stopSearchingResults();
            mTopDisplayController.hideAll();
        }
        mInputLogic.commitTyped(mSettings.getCurrent(), LastComposedWord.NOT_A_SEPARATOR);
        requestHideSelf(0);
        final MainKeyboardView mainKeyboardView = mKeyboardSwitcher.getMainKeyboardView();
        if (mainKeyboardView != null) {
            mainKeyboardView.closing();
        }
        final Intent intent = new Intent();
        intent.setClass(LatinIME.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_HOME_AS_UP, false);
        startActivity(intent);
    }

    private void showSubtypeSelectorAndSettings() {
        final CharSequence title = getString(R.string.english_ime_input_options);
        // TODO: Should use new string "Select active input modes".
        final CharSequence languageSelectionTitle = getString(R.string.language_selection_title);
        final CharSequence[] items = new CharSequence[]{
                languageSelectionTitle,
                getString(ApplicationUtils.getActivityTitleResId(this, SettingsActivity.class))
        };
        final OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int position) {
                di.dismiss();
                switch (position) {
                    case 0:
                        final Intent intent = IntentUtils.getInputLanguageSelectionIntent(
                                mRichImm.getInputMethodIdOfThisIme(),
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Intent.EXTRA_TITLE, languageSelectionTitle);
                        startActivity(intent);
                        break;
                    case 1:
                        launchSettings();
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                DialogUtils.getPlatformDialogThemeContext(this));
        builder.setItems(items, listener).setTitle(title);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true /* cancelable */);
        dialog.setCanceledOnTouchOutside(true /* cancelable */);
        showOptionDialog(dialog);
    }

    // TODO: Move this method out of {@link LatinIME}.
    private void showOptionDialog(final AlertDialog dialog) {
        final IBinder windowToken = mKeyboardSwitcher.getMainKeyboardView().getWindowToken();
        if (windowToken == null) {
            return;
        }

        final Window window = dialog.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = windowToken;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        mOptionsDialog = dialog;
        dialog.show();
    }

    private void showOldColorDialog(final Dialog dialog) {
        final IBinder windowToken = mKeyboardSwitcher.getMainKeyboardView().getWindowToken();
        if (windowToken == null)
            return;
        final Window window = dialog.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = windowToken;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //mOptionsDialog = dialog;
        dialog.show();
    }

    // TODO: can this be removed somehow without breaking the tests?
    @UsedForTesting
    /* package for test */ SuggestedWords getSuggestedWordsForTest() {
        // You may not use this method for anything else than debug
        return DEBUG ? mInputLogic.mSuggestedWords : null;
    }

    // DO NOT USE THIS for any other purpose than testing. This is information private to LatinIME.
    @UsedForTesting
    /* package for test */ void waitForLoadingDictionaries(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        mDictionaryFacilitator.waitForLoadingDictionariesForTesting(timeout, unit);
    }

    // DO NOT USE THIS for any other purpose than testing. This can break the keyboard badly.
    @UsedForTesting
    /* package for test */ void replaceDictionariesForTest(final Locale locale) {
        final SettingsValues settingsValues = mSettings.getCurrent();
        mDictionaryFacilitator.resetDictionaries(this, locale,
                settingsValues.mUseContactsDict, settingsValues.mUsePersonalizedDicts,
                false /* forceReloadMainDictionary */, this /* listener */);
    }

    // DO NOT USE THIS for any other purpose than testing.
    @UsedForTesting
    /* package for test */ void clearPersonalizedDictionariesForTest() {
        mDictionaryFacilitator.clearUserHistoryDictionary();
        mDictionaryFacilitator.clearPersonalizationDictionary();
    }

    @UsedForTesting
        /* package for test */ List<InputMethodSubtype> getEnabledSubtypesForTest() {
        return (mRichImm != null) ? mRichImm.getMyEnabledInputMethodSubtypeList(
                true /* allowsImplicitlySelectedSubtypes */) : new ArrayList<InputMethodSubtype>();
    }

    public void dumpDictionaryForDebug(final String dictName) {
        if (mDictionaryFacilitator.getLocale() == null) {
            resetSuggest();
        }
        mDictionaryFacilitator.dumpDictionaryForDebug(dictName);
    }

    public void debugDumpStateAndCrashWithException(final String context) {
        final SettingsValues settingsValues = mSettings.getCurrent();
        String s = settingsValues.toString() + "\nAttributes : " + settingsValues.mInputAttributes +
                "\nContext : " + context;
        throw new RuntimeException(s);
    }

    @Override
    protected void dump(final FileDescriptor fd, final PrintWriter fout, final String[] args) {
        super.dump(fd, fout, args);

        final Printer p = new PrintWriterPrinter(fout);
        p.println("LatinIME state :");
        p.println("  VersionCode = " + ApplicationUtils.getVersionCode(this));
        p.println("  VersionName = " + ApplicationUtils.getVersionName(this));
        final Keyboard keyboard = mKeyboardSwitcher.getKeyboard();
        final int keyboardMode = keyboard != null ? keyboard.mId.mMode : -1;
        p.println("  Keyboard mode = " + keyboardMode);
        final SettingsValues settingsValues = mSettings.getCurrent();
        p.println(settingsValues.dump());
        // TODO: Dump all settings values
    }

    public boolean shouldSwitchToOtherInputMethods() {
        // TODO: Revisit here to reorganize the settings. Probably we can/should use different
        // strategy once the implementation of
        // {@link InputMethodManager#shouldOfferSwitchingToNextInputMethod} is defined well.
        final boolean fallbackValue = mSettings.getCurrent().mIncludesOtherImesInLanguageSwitchList;
        final IBinder token = getWindow().getWindow().getAttributes().token;
        if (token == null) {
            return fallbackValue;
        }
        return mRichImm.shouldOfferSwitchingToNextInputMethod(token, fallbackValue);
    }

    public boolean shouldShowLanguageSwitchKey() {
        // TODO: Revisit here to reorganize the settings. Probably we can/should use different
        // strategy once the implementation of
        // {@link InputMethodManager#shouldOfferSwitchingToNextInputMethod} is defined well.
        final boolean fallbackValue = mSettings.getCurrent().isLanguageSwitchKeyEnabled();
        final IBinder token = getWindow().getWindow().getAttributes().token;
        if (token == null) {
            return fallbackValue;
        }
        return mRichImm.shouldOfferSwitchingToNextInputMethod(token, fallbackValue);
    }

    public void moveCursorBack() {
        changeSelection(-1, -1);
    }

    public void moveCursorNext() {
        changeSelection(1, 1);
    }

    public synchronized void changeSelection(int startChange, int endChange) {
        RichInputConnection connection = this.mInputLogic.mConnection;
        int startSelection = connection.getExpectedSelectionStart() + startChange;
        int endSelection = connection.getExpectedSelectionEnd() + endChange;
        Log.i(TAG, "start : " + startSelection + " end : " + endSelection + " textlenght " + connection.getTextLenght());
        if (startSelection >= 0 && endSelection <= connection.getTextLenght()) {
            connection.setSelection(startSelection, endSelection);
        }
        this.mInputLogic.finishInput();
        connection.finishComposingText();
    }

    public boolean isSelectionEmpty() {
        return this.mInputLogic.mConnection.getExpectedSelectionEnd() == this.mInputLogic.mConnection.getExpectedSelectionStart();
    }

    public int getKeyboardHeight() {
        return mKeyboardSwitcher.getMainKeyboardView().getHeight();
    }

    public void restartInput(boolean resetAnimation) {
        this.navManager.setNotHide(!resetAnimation);
        restartInput();
    }

    public void restartInput() {
        this.mKeyboardSwitcher.onHideWindow();
        super.hideWindow();
        showWindow(true);
    }

    public void changeLanguageNext() {
        switchToNextSubtype();
    }

    public void changeLanguagePrev() {
        switchToNextSubtype();
    }

    public void forceSuggestedWord(String word) {
        SuggestedWordInfo info = new SuggestedWordInfo(word, 0, 0, null, 0, 0);
        ArrayList<SuggestedWordInfo> infoList = new ArrayList();
        infoList.add(info);
        forceSuggestedWord(new SuggestedWords(infoList, null, false, false, false, 0));
    }

    private void forceSuggestedWord(SuggestedWords suggestedWords) {
        setSuggestedWords(suggestedWords);
    }

    public void deleteLastWord() {
        RichInputConnection conn = this.mInputLogic.mConnection;
        conn.finishComposingText();
        CharSequence cs = conn.getTextBeforeCursor(128, 0);
        CharSequence as = conn.getTextAfterCursor(128, 0);
        if (!TextUtils.isEmpty(cs)) {
            int inputLength = cs.length();
            int adx = 0;
            int idx = inputLength - 1;
            while (adx < as.length() && !isBackWordStopChar(as.charAt(adx))) {
                adx++;
            }
            while (idx > 0 && isBackWordStopChar(cs.charAt(idx))) {
                idx--;
            }
            while (idx > 0 && !isBackWordStopChar(cs.charAt(idx))) {
                idx--;
            }
            CharSequence chars = conn.getTextBeforeCursor(inputLength - idx, 0);
            if (chars.charAt(0) == ' ') {
                idx++;
            }
            if (chars.charAt(chars.length() - 1) == ' ') {
                adx = -1;
            }
            CharSequence charsAfter = conn.getTextAfterCursor(Math.max(adx, 0), 0);
            if (charsAfter != null) {
                forceSuggestedWord(chars.toString().replaceAll("\\s+", LastComposedWord.NOT_A_SEPARATOR) + charsAfter.toString().replaceAll("\\s+", LastComposedWord.NOT_A_SEPARATOR) + (adx != 0 ? Constants.WORD_SEPARATOR : LastComposedWord.NOT_A_SEPARATOR));
            }
            conn.deleteSurroundingText(inputLength - idx, adx + 1);
            this.mInputLogic.finishInput();
            this.mKeyboardSwitcher.requestUpdatingShiftState(getCurrentAutoCapsState(), getCurrentRecapitalizeState());
        }
    }

    public void deleteAllWords() {
        RichInputConnection conn = this.mInputLogic.mConnection;
        conn.finishComposingText();
        CharSequence cs = conn.getTextBeforeCursor(Constants.EDITOR_CONTENTS_CACHE_SIZE, 0);
        if (!TextUtils.isEmpty(cs)) {
            int inputLength = cs.length();
            CharSequence chars = conn.getTextBeforeCursor(inputLength, 0);
            if (chars != null) {
                forceSuggestedWord(chars.toString());
            }
            conn.deleteSurroundingText(inputLength, 0);
            this.mInputLogic.finishInput();
            this.mKeyboardSwitcher.requestUpdatingShiftState(getCurrentAutoCapsState(), getCurrentRecapitalizeState());
        }
    }

    public void setMixPanel(Context mContext) {

        if (!TextUtils.isEmpty(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpTag())) {

            MixPanelUtils.setMixPanel(mContext);

            MixPanelUtils.sendMixPanelProperties(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getBusinessName(),
                    SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getEmail(),
                    SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpTag(),
                    SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFPDetailsCreatedOn());

            setMixPanelProperties();
        }
    }

    private void setMixPanelProperties() {

        try {
            JSONObject store = new JSONObject();
            store.put("Business Name", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getBusinessName());
            store.put("Tag", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getFpTag());
            store.put("Primary contact", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getPrimaryContactNumber());
            store.put("$phone", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getPrimaryContactNumber());
            store.put("$email", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getEmail());
            store.put("$city", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getCity());
            store.put("$country_code", getCountryCode());
            if (TextUtils.isEmpty(SharedPrefUtil.fromBoostPref().getsBoostPref(this).getRootAliasURI()) || SharedPrefUtil.fromBoostPref().getsBoostPref(this).getRootAliasURI().equals("null")) {
                store.put("Domain", "False");
            } else {
                store.put("Domain", "True");
            }
            store.put("FpId", SharedPrefUtil.fromBoostPref().getsBoostPref(this).getFpId());
            MixPanelUtils.identify(SharedPrefUtil.fromBoostPref().getsBoostPref(this).getFpTag(), store, SharedPrefUtil.fromBoostPref().getsBoostPref(this).getFpId());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCountryCode() {
        String[] string_array = this.getResources().getStringArray(R.array.CountryCodes);
        for (String country_phone : string_array) {
            String[] Codes = country_phone.split(",");
            if (Codes[0].equalsIgnoreCase(SharedPrefUtil.fromBoostPref().getsBoostPref(this).getCountryPhoneCode())) {
                return Codes[1];
            }
        }
        return "";
    }

    public static final class UIHandler extends LeakGuardHandlerWrapper<LatinIME> {
        private static final int MSG_UPDATE_SHIFT_STATE = 0;
        private static final int MSG_PENDING_IMS_CALLBACK = 1;
        private static final int MSG_UPDATE_SUGGESTION_STRIP = 2;
        private static final int MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP = 3;
        private static final int MSG_RESUME_SUGGESTIONS = 4;
        private static final int MSG_REOPEN_DICTIONARIES = 5;
        private static final int MSG_UPDATE_TAIL_BATCH_INPUT_COMPLETED = 6;
        private static final int MSG_RESET_CACHES = 7;
        private static final int MSG_WAIT_FOR_DICTIONARY_LOAD = 8;
        // Update this when adding new messages
        private static final int MSG_LAST = MSG_WAIT_FOR_DICTIONARY_LOAD;

        private static final int ARG1_NOT_GESTURE_INPUT = 0;
        private static final int ARG1_DISMISS_GESTURE_FLOATING_PREVIEW_TEXT = 1;
        private static final int ARG1_SHOW_GESTURE_FLOATING_PREVIEW_TEXT = 2;
        private static final int ARG2_UNUSED = 0;
        private static final int ARG1_FALSE = 0;
        private static final int ARG1_TRUE = 1;

        private int mDelayInMillisecondsToUpdateSuggestions;
        private int mDelayInMillisecondsToUpdateShiftState;
        // Working variables for the following methods.
        private boolean mIsOrientationChanging;
        private boolean mPendingSuccessiveImsCallback;
        private boolean mHasPendingStartInput;
        private boolean mHasPendingFinishInputView;
        private boolean mHasPendingFinishInput;
        private EditorInfo mAppliedEditorInfo;

        public UIHandler(final LatinIME ownerInstance) {
            super(ownerInstance);
        }

        public void onCreate() {
            final LatinIME latinIme = getOwnerInstance();
            if (latinIme == null) {
                return;
            }
            final Resources res = latinIme.getResources();
            mDelayInMillisecondsToUpdateSuggestions = res.getInteger(
                    R.integer.config_delay_in_milliseconds_to_update_suggestions);
            mDelayInMillisecondsToUpdateShiftState = res.getInteger(
                    R.integer.config_delay_in_milliseconds_to_update_shift_state);
        }

        @Override
        public void handleMessage(final Message msg) {
            final LatinIME latinIme = getOwnerInstance();
            if (latinIme == null) {
                return;
            }
            final KeyboardSwitcher switcher = latinIme.mKeyboardSwitcher;
            switch (msg.what) {
                case MSG_UPDATE_SUGGESTION_STRIP:
                    cancelUpdateSuggestionStrip();
                    latinIme.mInputLogic.performUpdateSuggestionStripSync(
                            latinIme.mSettings.getCurrent(), msg.arg1 /* inputStyle */);
                    break;
                case MSG_UPDATE_SHIFT_STATE:
                    switcher.requestUpdatingShiftState(latinIme.getCurrentAutoCapsState(),
                            latinIme.getCurrentRecapitalizeState());
                    break;
                case MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP:
                    if (msg.arg1 == ARG1_NOT_GESTURE_INPUT) {
                        final SuggestedWords suggestedWords = (SuggestedWords) msg.obj;
                        latinIme.showSuggestionStrip(suggestedWords);
                    } else {
                        latinIme.showGesturePreviewAndSuggestionStrip((SuggestedWords) msg.obj,
                                msg.arg1 == ARG1_DISMISS_GESTURE_FLOATING_PREVIEW_TEXT);
                    }
                    break;
                case MSG_RESUME_SUGGESTIONS:
                    latinIme.mInputLogic.restartSuggestionsOnWordTouchedByCursor(
                            latinIme.mSettings.getCurrent(),
                            msg.arg1 == ARG1_TRUE /* shouldIncludeResumedWordInSuggestions */,
                            latinIme.mKeyboardSwitcher.getCurrentKeyboardScriptId());
                    break;
                case MSG_REOPEN_DICTIONARIES:
                    // We need to re-evaluate the currently composing word in case the script has
                    // changed.
                    postWaitForDictionaryLoad();
                    latinIme.resetSuggest();
                    break;
                case MSG_UPDATE_TAIL_BATCH_INPUT_COMPLETED:
                    latinIme.mInputLogic.onUpdateTailBatchInputCompleted(
                            latinIme.mSettings.getCurrent(),
                            (SuggestedWords) msg.obj, latinIme.mKeyboardSwitcher);
                    if (latinIme.mInputLogic.isSearchingResults()) {
                        latinIme.mTopDisplayController.runSearch(latinIme.mInputLogic.getSearchText());
                    }
                    break;
                case MSG_RESET_CACHES:
                    final SettingsValues settingsValues = latinIme.mSettings.getCurrent();
                    if (latinIme.mInputLogic.retryResetCachesAndReturnSuccess(
                            msg.arg1 == ARG1_TRUE /* tryResumeSuggestions */,
                            msg.arg2 /* remainingTries */, this /* handler */)) {
                        // If we were able to reset the caches, then we can reload the keyboard.
                        // Otherwise, we'll do it when we can.
                        latinIme.mKeyboardSwitcher.loadKeyboard(latinIme.getCurrentInputEditorInfo(),
                                settingsValues, latinIme.getCurrentAutoCapsState(),
                                latinIme.getCurrentRecapitalizeState());
                    }
                    break;
                case MSG_WAIT_FOR_DICTIONARY_LOAD:
                    Log.i(TAG, "Timeout waiting for dictionary load");
                    break;
            }
        }

        public void postUpdateSuggestionStrip(final int inputStyle) {
            sendMessageDelayed(obtainMessage(MSG_UPDATE_SUGGESTION_STRIP, inputStyle,
                    0 /* ignored */), mDelayInMillisecondsToUpdateSuggestions);
        }

        public void postReopenDictionaries() {
            sendMessage(obtainMessage(MSG_REOPEN_DICTIONARIES));
        }

        public void postResumeSuggestions(final boolean shouldIncludeResumedWordInSuggestions,
                                          final boolean shouldDelay) {
            final LatinIME latinIme = getOwnerInstance();
            if (latinIme == null) {
                return;
            }
            if (!latinIme.mSettings.getCurrent().isSuggestionsEnabledPerUserSettings()) {
                return;
            }
            removeMessages(MSG_RESUME_SUGGESTIONS);
            if (shouldDelay) {
                sendMessageDelayed(obtainMessage(MSG_RESUME_SUGGESTIONS,
                        shouldIncludeResumedWordInSuggestions ? ARG1_TRUE : ARG1_FALSE,
                        0 /* ignored */), mDelayInMillisecondsToUpdateSuggestions);
            } else {
                sendMessage(obtainMessage(MSG_RESUME_SUGGESTIONS,
                        shouldIncludeResumedWordInSuggestions ? ARG1_TRUE : ARG1_FALSE,
                        0 /* ignored */));
            }
        }

        public void postResetCaches(final boolean tryResumeSuggestions, final int remainingTries) {
            removeMessages(MSG_RESET_CACHES);
            sendMessage(obtainMessage(MSG_RESET_CACHES, tryResumeSuggestions ? 1 : 0,
                    remainingTries, null));
        }

        public void postWaitForDictionaryLoad() {
            sendMessageDelayed(obtainMessage(MSG_WAIT_FOR_DICTIONARY_LOAD),
                    DELAY_WAIT_FOR_DICTIONARY_LOAD);
        }

        public void cancelWaitForDictionaryLoad() {
            removeMessages(MSG_WAIT_FOR_DICTIONARY_LOAD);
        }

        public boolean hasPendingWaitForDictionaryLoad() {
            return hasMessages(MSG_WAIT_FOR_DICTIONARY_LOAD);
        }

        public void cancelUpdateSuggestionStrip() {
            removeMessages(MSG_UPDATE_SUGGESTION_STRIP);
        }

        public boolean hasPendingUpdateSuggestions() {
            return hasMessages(MSG_UPDATE_SUGGESTION_STRIP);
        }

        public boolean hasPendingReopenDictionaries() {
            return hasMessages(MSG_REOPEN_DICTIONARIES);
        }

        public void postUpdateShiftState() {
            removeMessages(MSG_UPDATE_SHIFT_STATE);
            sendMessageDelayed(obtainMessage(MSG_UPDATE_SHIFT_STATE),
                    mDelayInMillisecondsToUpdateShiftState);
        }

        @UsedForTesting
        public void removeAllMessages() {
            for (int i = 0; i <= MSG_LAST; ++i) {
                removeMessages(i);
            }
        }

        public void showGesturePreviewAndSuggestionStrip(final SuggestedWords suggestedWords,
                                                         final boolean dismissGestureFloatingPreviewText) {
            removeMessages(MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP);
            final int arg1 = dismissGestureFloatingPreviewText
                    ? ARG1_DISMISS_GESTURE_FLOATING_PREVIEW_TEXT
                    : ARG1_SHOW_GESTURE_FLOATING_PREVIEW_TEXT;
            obtainMessage(MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP, arg1,
                    ARG2_UNUSED, suggestedWords).sendToTarget();
        }

        public void showSuggestionStrip(final SuggestedWords suggestedWords) {
            removeMessages(MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP);
            obtainMessage(MSG_SHOW_GESTURE_PREVIEW_AND_SUGGESTION_STRIP,
                    ARG1_NOT_GESTURE_INPUT, ARG2_UNUSED, suggestedWords).sendToTarget();
        }

        public void showTailBatchInputResult(final SuggestedWords suggestedWords) {
            obtainMessage(MSG_UPDATE_TAIL_BATCH_INPUT_COMPLETED, suggestedWords).sendToTarget();
        }

        public void startOrientationChanging() {
            removeMessages(MSG_PENDING_IMS_CALLBACK);
            resetPendingImsCallback();
            mIsOrientationChanging = true;
            final LatinIME latinIme = getOwnerInstance();
            if (latinIme == null) {
                return;
            }
            if (latinIme.isInputViewShown()) {
                latinIme.mKeyboardSwitcher.saveKeyboardState();
            }
        }

        private void resetPendingImsCallback() {
            mHasPendingFinishInputView = false;
            mHasPendingFinishInput = false;
            mHasPendingStartInput = false;
        }

        private void executePendingImsCallback(final LatinIME latinIme, final EditorInfo editorInfo,
                                               boolean restarting) {
            if (mHasPendingFinishInputView) {
                latinIme.onFinishInputViewInternal(mHasPendingFinishInput);
            }
            if (mHasPendingFinishInput) {
                latinIme.onFinishInputInternal();
            }
            if (mHasPendingStartInput) {
                latinIme.onStartInputInternal(editorInfo, restarting);
            }
            resetPendingImsCallback();
        }

        public void onStartInput(final EditorInfo editorInfo, final boolean restarting) {
            if (hasMessages(MSG_PENDING_IMS_CALLBACK)) {
                // Typically this is the second onStartInput after orientation changed.
                mHasPendingStartInput = true;
            } else {
                if (mIsOrientationChanging && restarting) {
                    // This is the first onStartInput after orientation changed.
                    mIsOrientationChanging = false;
                    mPendingSuccessiveImsCallback = true;
                }
                final LatinIME latinIme = getOwnerInstance();
                if (latinIme != null) {
                    executePendingImsCallback(latinIme, editorInfo, restarting);
                    latinIme.onStartInputInternal(editorInfo, restarting);
                }
            }
        }

        public void onStartInputView(final EditorInfo editorInfo, final boolean restarting) {
            if (hasMessages(MSG_PENDING_IMS_CALLBACK)
                    && KeyboardId.equivalentEditorInfoForKeyboard(editorInfo, mAppliedEditorInfo)) {
                // Typically this is the second onStartInputView after orientation changed.
                resetPendingImsCallback();
            } else {
                if (mPendingSuccessiveImsCallback) {
                    // This is the first onStartInputView after orientation changed.
                    mPendingSuccessiveImsCallback = false;
                    resetPendingImsCallback();
                    sendMessageDelayed(obtainMessage(MSG_PENDING_IMS_CALLBACK),
                            PENDING_IMS_CALLBACK_DURATION);
                }
                final LatinIME latinIme = getOwnerInstance();
                if (latinIme != null) {
                    executePendingImsCallback(latinIme, editorInfo, restarting);
                    latinIme.onStartInputViewInternal(editorInfo, restarting);
                    mAppliedEditorInfo = editorInfo;
                }
            }
        }

        public void onFinishInputView(final boolean finishingInput) {
            if (hasMessages(MSG_PENDING_IMS_CALLBACK)) {
                // Typically this is the first onFinishInputView after orientation changed.
                mHasPendingFinishInputView = true;
            } else {
                final LatinIME latinIme = getOwnerInstance();
                if (latinIme != null) {
                    latinIme.onFinishInputViewInternal(finishingInput);
                    mAppliedEditorInfo = null;
                }
            }
        }

        public void onFinishInput() {
            if (hasMessages(MSG_PENDING_IMS_CALLBACK)) {
                // Typically this is the first onFinishInput after orientation changed.
                mHasPendingFinishInput = true;
            } else {
                final LatinIME latinIme = getOwnerInstance();
                if (latinIme != null) {
                    executePendingImsCallback(latinIme, null, false);
                    latinIme.onFinishInputInternal();
                }
            }
        }
    }

    static final class SubtypeState {
        private InputMethodSubtype mLastActiveSubtype;
        private boolean mCurrentSubtypeHasBeenUsed;

        public void setCurrentSubtypeHasBeenUsed() {
            mCurrentSubtypeHasBeenUsed = true;
        }

        public void switchSubtype(final IBinder token, final RichInputMethodManager richImm) {
            final InputMethodSubtype currentSubtype = richImm.getInputMethodManager()
                    .getCurrentInputMethodSubtype();
            final InputMethodSubtype lastActiveSubtype = mLastActiveSubtype;
            final boolean currentSubtypeHasBeenUsed = mCurrentSubtypeHasBeenUsed;
            if (currentSubtypeHasBeenUsed) {
                mLastActiveSubtype = currentSubtype;
                mCurrentSubtypeHasBeenUsed = false;
            }
            if (currentSubtypeHasBeenUsed
                    && richImm.checkIfSubtypeBelongsToThisImeAndEnabled(lastActiveSubtype)
                    && !currentSubtype.equals(lastActiveSubtype)) {
                richImm.setInputMethodAndSubtype(token, lastActiveSubtype);
                return;
            }
            richImm.switchToNextInputMethod(token, true /* onlyCurrentIme */);
        }
    }

    private static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            //if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ////mSubtypeSwitcher.onNetworkStateChanged(intent);
            //} else if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
            if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                AudioAndHapticFeedbackManager.getInstance().onRingerModeChanged();
            }
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
        public void onEventMainThread(InsertPngEvent event) {
            try {
                if (event.isSticker) {
                    long currentTime = System.currentTimeMillis();
                    if ((lastStickerInsertionTime + 3000) > currentTime)
                        return;
                    lastStickerInsertionTime = currentTime;
                    Log.e("TWO_STICKER", "sending sticker");
                    AssetFileDescriptor fileDescriptor = getAssets().openFd("stickers/" + event.base + '/' + event.name);
                    FileInputStream stream = fileDescriptor.createInputStream();
                    final File imagesDir = new File(getFilesDir(), "images");
                    imagesDir.mkdirs();
                    final File mPngFile = new File(imagesDir, event.name);
                    mPngFile.delete();
                    OutputStream outputStream = new FileOutputStream(mPngFile);
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = stream.read(bytes)) != -1)
                        outputStream.write(bytes, 0, read);
                    Intent resInt = mInputLogic.mConnection.doCommitContent("Sticker from The Agah Keyboard", RichInputConnection.MIME_TYPE_PNG, mPngFile);
                    if (resInt != null) {
                        resInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(resInt);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SearchItemSelectedEvent event) {
            LatinIME.this.mInputLogic.stopSearchingResults();
            mTopDisplayController.hideAll();
            if (event.getItem().getService().equals("giphy")) {
                File fromFile = ShareUtils.getCachedImageOnDisk(LatinIME.this, Uri.parse(event.getItem().getImage().getUrl()));
                File toFile = new File(new File(getFilesDir(), "images"), System.currentTimeMillis() + ".gif");
                if (fromFile == null)
                    return;
                ShareUtils.copyFile(fromFile, toFile);
                Intent resInt = mInputLogic.mConnection.doCommitContent("GIF from The Agah Keyboard", RichInputConnection.MIME_TYPE_GIF, toFile);
                if (resInt != null) {
                    resInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(resInt);
                }
                return;
            }
            shareItemThroughText(event);
        }

        private void shareItemThroughText(SearchItemSelectedEvent event) {
            String output = event.getItem().getOutput();
            if (TextUtils.isEmpty(output))
                return;
            LatinIME.this.mInputLogic.mConnection.commitText(output, LatinIME.this.mInputLogic.mConnection.getExpectedSelectionEnd());
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ServiceRequestEvent event) {
            if (mInputLogic.isSearchingResults())
                LatinIME.this.mTopDisplayController.setVisualState(event.getState());
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SearchRetryErrorEvent event) {
            if (mInputLogic.isSearchingResults())
                LatinIME.this.mTopDisplayController.showRetryErrorMessage(event.isNetworkError());
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SearchResultsEvent event) {
            if (mInputLogic.isSearchingResults())
                LatinIME.this.mTopDisplayController.setSearchItems(event.getSource(), event.getItems(), event.getAuthorizedStatus());
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ServiceExitEvent event) {
            if (mInputLogic.isSearchingResults()) {
                LatinIME.this.mInputLogic.stopSearchingResults();
                mTopDisplayController.hideAll();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ShowActionRowEvent event) {
            mTopDisplayController.showActionRow(getApplicationContext());
            //mKeyboardSwitcher.setProductShareKeyboardFrame(ImePresenterImpl.TabType.UPDATES);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ShowSuggestionsEvent event) {
            mTopDisplayController.showSuggestions(false);
            mKeyboardSwitcher.showKeyboardFrame();
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(ShowSuggestionsEventAnimated event) {
            mTopDisplayController.showSuggestions(true);
            mKeyboardSwitcher.showKeyboardFrame();
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(LaunchSettingsEvent event) {
            if (mInputLogic.isSearchingResults()) {
                LatinIME.this.mInputLogic.stopSearchingResults();
                mTopDisplayController.hideAll();
            }
            Integer requestCode = 2;
            PermissionEverywhere.getPermission(getApplicationContext(), new String[]{Manifest.permission.READ_CONTACTS}, requestCode,
                    getResources().getString(R.string.contacts_permission), getResources().getString(R.string.contacts_permission),
                    R.drawable.ic_launcher_keyboard).enqueue(new PermissionResultCallback() {
                @Override
                public void onComplete(PermissionResponse permissionResponse) {

                }
            });
        }

    }


}