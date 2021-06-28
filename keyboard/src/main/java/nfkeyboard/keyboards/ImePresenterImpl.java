package nfkeyboard.keyboards;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.inputmethod.keyboard.KeyboardSwitcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.database.DatabaseTable;
import nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nfkeyboard.interface_contracts.CandidateViewItemClickListener;
import nfkeyboard.interface_contracts.GetGalleryImagesAsyncTask_Interface;
import nfkeyboard.interface_contracts.ImeToPresenterInterface;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.interface_contracts.PresenterToImeInterface;
import nfkeyboard.interface_contracts.UrlToBitmapInterface;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.models.KeywordModel;
import nfkeyboard.util.KeyboardUtils;
import nfkeyboard.util.MethodUtils;
import nfkeyboard.util.MixPanelUtils;

import static android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;

/**
 * Created by Admin on 26-02-2018.
 */

public class ImePresenterImpl implements ItemClickListener,
        ImeToPresenterInterface,
        CandidateToPresenterInterface,
        CandidateViewItemClickListener,
        UrlToBitmapInterface,
        GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface {
    private final KeyboardSwitcher mKeyboardSwitcher;
    private DatabaseTable mDatabaseTable;
    private SpellCorrector corrector;
    private ExecutorService mExecutorService;
    private CandidateViewBaseImpl mCandidateView;
    private KeyboardViewBaseImpl mKeyboardView;
    private ManageKeyboardView manageKeyboardView;
    private static final String UTM_SOURCE = "utm_source", UTM_MEDIUM = "utm_medium";
    public final static int KEY_EMOJI = -2005, KEY_IME_OPTION = -2006, KEY_SPACE = -2007, KEY_NUMBER = -2000,
            KEY_SYM = -2001, KEY_SYM_SHIFT = -2002, KEY_QWRTY = -2003, KEY_LANGUAGE_CHANGE = -2004;
    private Context mContext;
    private String packageName = "";
    private KeyboardUtils.CandidateType currentCandidateType = KeyboardUtils.CandidateType.TEXT_LIST;
    private KeyboardUtils.KeyboardType mKeyboardTypeCurrent = KeyboardUtils.KeyboardType.QWERTY_LETTERS;
    private KeyboardBaseImpl mCurrentKeyboard;
    private boolean caps;
    private int imeOptionId;
    private InputMethodManager mInputMethodManager;
    private PresenterToImeInterface imeListener;
    private AudioManager mAudioManager;
    private TabType mTabType = TabType.NO_TAB;
    private ShiftType mShiftType = ShiftType.CAPITAL;
    private int mPrimaryCode;
    private String text;
    private static String previousText;
    private ArrayList<KeywordModel> mSuggestions;
    private static boolean isSelectedKeyboardItem = false;
    private static boolean isSelectedKeyboardEdited = false;
    private static boolean isPreviousWordEdited = false;
    private Map<String, String> mSuggestedWordlist;
    private Handler mHandler;
    private Runnable runnable;
    private Future longRunningTaskFuture;
    private String enteredText;
    private SpellCorrectorAsyncTask spellCorrectorAsyncTask;
    private ArrayList<Uri> uris = new ArrayList<>();
    int numberOfSelected = 0;

    private int flag;
    private InputContentInfoCompat inputContentInfoCompat;

    @Override
    public TabType getTabType() {
        return mTabType;
    }

    @Override
    public void onSpeechResult(String speech) {
        if (!TextUtils.isEmpty(speech)) {
            if (imeListener.getImeCurrentInputConnection() != null)
                imeListener.getImeCurrentInputConnection().commitText(speech, 1);
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SPEECH_RESULT, null);
        }
        if (mTabType != TabType.KEYBOARD) {
            mTabType = TabType.NO_TAB;
            manageKeyboardView.showKeyboardLayout();
            addCandidateTypeView(currentCandidateType, mTabType);
        }
    }

    @Override
    public boolean imagesSupported() {
        final EditorInfo editorInfo = imeListener.getImeCurrentEditorInfo();
        if (!validatePackageName(editorInfo)) {
            return false;
        }

        if (isCommitContentSupported(editorInfo, "image/png")) {
            return true;
        }
        return false;
    }

    @Override
    public String packageName() {
        EditorInfo editorInfo = imeListener.getImeCurrentEditorInfo();
        return editorInfo.packageName;
    }


//    @Override
//    public void onEmojiconClicked(Emojicon emojicon) {
//        if (imeListener.getImeCurrentInputConnection() != null)
//            imeListener.getImeCurrentInputConnection().commitText(emojicon.getEmoji(), 1);
//    }
//
//    @Override
//    public void onEmojiconBackspaceClicked(View v) {
//        sendKeyEvent(KeyEvent.KEYCODE_DEL);
//    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onResourcesReady(Bitmap bitmap, String text, String imageId) {
        Uri uri = MethodUtils.getImageUri(mContext, bitmap, TextUtils.isEmpty(imageId) ?
                UUID.randomUUID().toString() : imageId);
        if (uri == null) {
            if (imeListener.getImeCurrentInputConnection() != null)
                imeListener.getImeCurrentInputConnection().commitText(text, 1);
        } else {
            doCommitContent(text, "image/png", uri);
        }
    }

    @Override
    public void onResorceMultipleReady(Bitmap bitmap, String imageId, int size, int current) {

        if (current == 0) {
            uris.clear();
        }
        uris.add(MethodUtils.getImageUri(mContext, bitmap, TextUtils.isEmpty(imageId) ?
                UUID.randomUUID().toString() : imageId));

        if (size == uris.size()) {
            doCommitContentMultiple("image/png", uris);
        }
    }

    @Override
    public void onItemClick(KeywordModel word) {
        isSelectedKeyboardItem = true;
        if (word.getType().equalsIgnoreCase(KeywordModel.NEW_WORD)) {
            mDatabaseTable.saveWordToDatabase(word.getWord().trim());
        }
        ExtractedText et = imeListener.getImeCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
        int selectionStart = et.selectionStart;
        CharSequence inputSequence = imeListener.getImeCurrentInputConnection().getTextBeforeCursor(1000, 0);
        if (inputSequence != null) {
            int index = inputSequence.toString().lastIndexOf(" ");
            String oldText = inputSequence.toString().substring(index > 0 ? index : 0, selectionStart);
            imeListener.getImeCurrentInputConnection().deleteSurroundingText(!inputSequence.toString().contains(" ") ?
                    oldText.length() - 1 : oldText.length(), 0);
        }
        imeListener.getImeCurrentInputConnection().finishComposingText();
        imeListener.getImeCurrentInputConnection().commitText(word.getWord().trim(), 1);
        imeListener.getImeCurrentInputConnection().finishComposingText();
    }

    @Override
    public void onKeyboardTabClick(View view) {

        if (view.getId() == R.id.img_nowfloats) {
            if (mTabType != TabType.KEYBOARD) {
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_ICON_CLICKED, null);
                // mTabType = TabType.KEYBOARD;
                addCandidateTypeView(KeyboardUtils.CandidateType.BOOST_SHARE1, TabType.NO_TAB);
                manageKeyboardView.showKeyboardLayout();
            }

        } else if (view.getId() == R.id.tv_updates) {
            if (mTabType != TabType.UPDATES) {
                mTabType = TabType.UPDATES;
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SHOW_UPDATES, null);
                manageKeyboardView.showShareLayout(mTabType);
            }
        } else if (view.getId() == R.id.tv_products) {
            if (mTabType != TabType.PRODUCTS) {
                mTabType = TabType.PRODUCTS;
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SHOW_PRODUCT, null);
                manageKeyboardView.showShareLayout(mTabType);
            }
        } else if (view.getId() == R.id.img_settings) {
            if (mTabType != TabType.SETTINGS) {
                mTabType = TabType.SETTINGS;
                manageKeyboardView.showSpeechInput();
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_VOICE_INPUT, null);
            }

        } else if (view.getId() == R.id.tv_photos) {
            if (mTabType != TabType.PHOTOS) {
                mTabType = TabType.PHOTOS;
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SHOW_PHOTOS, null);
                manageKeyboardView.showShareLayout(mTabType);
            }
        } else if (view.getId() == R.id.tv_details) {
            if (mTabType != TabType.DETAILS) {
                mTabType = TabType.DETAILS;
                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_SHOW_DETAILS, null);
                manageKeyboardView.showShareLayout(mTabType);
            }
        } else if (view.getId() == R.id.img_back) {
            mTabType = TabType.KEYBOARD;
            setCurrentKeyboard();
        }

    }

    public KeyboardViewBaseImpl getKeyBoardview() {
        return mKeyboardView;
    }

    @Override
    public String onCopyClick(AllSuggestionModel model)
    {

        permissions();
        return onClickRegister(model);
    }

    @Override
    public void onCreateProductOfferClick(AllSuggestionModel model) {
    }

    @Override
    public String onCreateProductOfferResponse(String name, double oldPrice, double newPrice, String createdOn, String expiresOn, String Url, String currency) {

        mKeyboardSwitcher.hideProgressbar();

        /*if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(mContext, "Please grant external storage permission", Toast.LENGTH_SHORT).show();
            return null;
        }*/

        if (Url == null)
        {
            Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
            return null;
        }

        long diffHours = 24;
        DecimalFormat df = new DecimalFormat("#,##,##,##,##,##,##,###.##");

        try
        {
            createdOn = createdOn.replace("T", " ");
            expiresOn = expiresOn.replace("T", " ");
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createdOn);
            Date expireDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiresOn);

            long diff = expireDate.getTime() - createdDate.getTime();
            diffHours = (diff / (60 * 60 * 1000));
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            doCommitContent("Offer on: " + name /*+ "\nPrice: " + currency + " " + df.format(oldPrice)*/ + "\n\nOffer Price: "
                    + currency + " " + df.format(newPrice) + "\nExpires in \"" + diffHours + "\" Hours!\n\n" +
                    "Click to Buy: " + appendedUrl(Url), "text/plain", null);
        }

        return Url;
    }

    @Override
    public boolean onClick(AllSuggestionModel model, boolean selected) {
        return true;
    }

    @Override
    public void onDetailsClick(AllSuggestionModel model) {

        permissions();
        onClickRegister(model);
    }

    @Override
    public void onError() {
        mKeyboardSwitcher.hideProgressbar();
    }

    @Override
    public void imagesReceived() {
        Log.d("here", "hello");
        mKeyboardSwitcher.imagesReceived();

    }

    public enum TabType {
        PRODUCTS, UPDATES, KEYBOARD, SETTINGS, BACK, NO_TAB, PHOTOS, DETAILS;
    }

    private enum ShiftType {
        LOCKED, CAPITAL, NORMAL;
    }

     /* public ImePresenterImpl(Context context, PresenterToImeInterface imeListener) {
        mHandler = new Handler(context.getMainLooper());
        mDatabaseTable = new DatabaseTable(context);
        corrector = new SpellCorrector(context);
        mCandidateView = new CandidateViewBaseImpl(context);
        MixPanelUtils.getInstance().setMixPanel(context);
        mCandidateView.setcandidateItemClickListener(this);
        mContext = context;
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        this.imeListener = imeListener;
        if (imeListener.getImeCurrentEditorInfo() != null) {
            imeListener.getImeCurrentEditorInfo().inputType = TYPE_TEXT_FLAG_AUTO_CORRECT;
        }
        mExecutorService = Executors.newSingleThreadExecutor();
        mSuggestions = new ArrayList<>();
    }*/


    public ImePresenterImpl(Context context, PresenterToImeInterface mImeListener, KeyboardSwitcher mKeyboardSwitcher) {
        mContext = context;
        this.imeListener = mImeListener;
        this.mKeyboardSwitcher = mKeyboardSwitcher;


    }


    @Override
    public View onCreateInputView() {
        manageKeyboardView = (ManageKeyboardView) LayoutInflater.from(mContext).inflate(R.layout.keyboard_view, null);
        manageKeyboardView.setPresenterListener(this);
        manageKeyboardView.setUrlToBitmapInterface(this);
        manageKeyboardView.setGalleryImageListener(this);
        mKeyboardView = manageKeyboardView.getKeyboard();
        setCurrentKeyboard(mKeyboardTypeCurrent);
        mKeyboardView.setOnKeyboardActionListener(new KeyboardListener());
        return manageKeyboardView;
    }

    public void onStartInputView(EditorInfo attribute, boolean restarting) {

        packageName = attribute.packageName;
        mShiftType = ShiftType.CAPITAL;
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                setKeyboardType(KeyboardUtils.KeyboardType.NUMBERS);
                currentCandidateType = KeyboardUtils.CandidateType.NULL;
                break;
            case InputType.TYPE_CLASS_TEXT:
                final int variation = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
                switch (variation) {
                    case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                    case EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS:
                        currentCandidateType = KeyboardUtils.CandidateType.NULL;
                        mShiftType = ShiftType.NORMAL;
                        setKeyboardType(KeyboardUtils.KeyboardType.EMAIL_ADDRESS);
                        break;
                    case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD:
                    case EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                    case EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD:
                        mShiftType = ShiftType.NORMAL;
                        currentCandidateType = KeyboardUtils.CandidateType.NULL;
                        setKeyboardType(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
                        break;
                    default:
                        //currentCandidateType = KeyboardUtils.CandidateType.BOOST_SHARE;
                        currentCandidateType = KeyboardUtils.CandidateType.TEXT_LIST;
                        setKeyboardType(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
                        showWordSuggestions(imeListener.getImeCurrentInputConnection());
                        break;

                }

                break;
            default:
                //currentCandidateType = KeyboardUtils.CandidateType.BOOST_SHARE;
                currentCandidateType = KeyboardUtils.CandidateType.TEXT_LIST;
                setKeyboardType(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
        }
        imeOptionId = attribute.imeOptions;
        setCurrentKeyboard();
    }
    private void initializeValues() {
        mTabType = TabType.NO_TAB;
        manageKeyboardView.clearResources();
    }

    private void setImeOptions(int options) {
        int keySize = mCurrentKeyboard.getKeys().size();
        Keyboard.Key mEnterKey = null;
        if (keySize > 0) {
            mEnterKey = mCurrentKeyboard.getKeys().get(keySize - 1);
        }
        if (mEnterKey == null) {
            return;
        }
        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = "Go";
                break;
//            case EditorInfo.IME_ACTION_NEXT:
//                mEnterKey.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_next_kbd);
//                mEnterKey.label = null;
//                break;
//            case EditorInfo.IME_ACTION_SEARCH:
//                mEnterKey.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_search_kbd);
//                mEnterKey.label = null;
//                break;
//            case EditorInfo.IME_ACTION_SEND:
//                mEnterKey.iconPreview = null;
//                mEnterKey.icon = null;
//                mEnterKey.label = "send";
//                break;
//            case EditorInfo.IME_ACTION_DONE:
//                mEnterKey.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_check_white_24dp);
//                mEnterKey.label = null;
//                break;
//            default:
//                mEnterKey.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_enter_arrow);
//                mEnterKey.label = null;
//                break;
        }
        //mKeyboardView.invalidateKey(mCurrentKeyboard.getKeys().size()-1);
    }

    public void setKeyboardType(KeyboardUtils.KeyboardType type) {
        mKeyboardTypeCurrent = type;
    }

    public void setCurrentKeyboard() {
        initializeValues();
        setCurrentKeyboard(mKeyboardTypeCurrent);
        addCandidateTypeView(currentCandidateType, mTabType);
    }

    public void setCurrentKeyboard(KeyboardUtils.KeyboardType type) {
        if (mKeyboardTypeCurrent != type) {
            mKeyboardTypeCurrent = type;
        }
        mCurrentKeyboard = mKeyboardView.getKeyboard(type);
        addSvgImages();
        setImeOptions(imeOptionId);
        mKeyboardView.setKeyboard(mCurrentKeyboard);
        mKeyboardView.setCurrentKeyBoardType(mKeyboardTypeCurrent);
        mKeyboardView.setShifted(mShiftType != ShiftType.NORMAL);
        manageKeyboardView.showKeyboardLayout();
    }

    private void addSvgImages() {
        for (Keyboard.Key key : mCurrentKeyboard.getKeys()) {
            switch (key.codes[0]) {
                case Keyboard.KEYCODE_DELETE:
                    key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_backspace_arrow);
                    //inputConnection.deleteSurroundingText(1, 0);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_arrow_up);
                    break;
                case KEY_EMOJI:
                    key.icon = AppCompatResources.getDrawable(mContext, R.drawable.emoji_happiness);
                    break;
                case KEY_LANGUAGE_CHANGE:
                    key.icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_language_change);
                    break;
            }
        }
    }


    private IBinder getToken() {
        final Dialog dialog = imeListener.getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    public void onDestroy() {
        MixPanelUtils.flushMixPanel();
    }

    private void addCandidateTypeView(KeyboardUtils.CandidateType type, TabType tabType) {
        imeListener.setCandidatesViewShown(mCandidateView.addCandidateTypeView(type, tabType));
    }

    private boolean isCommitContentSupported(
            @Nullable EditorInfo editorInfo, @NonNull String mimeType) {
        if (editorInfo == null) {
            return false;
        }

        final InputConnection ic = imeListener.getImeCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        if (!validatePackageName(editorInfo)) {
            return false;
        }

        final String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePackageName(@Nullable EditorInfo editorInfo) {
        if (editorInfo == null) {
            return false;
        }
        final String packageName = editorInfo.packageName;
        if (packageName == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        final InputBinding inputBinding = imeListener.getImeCurrentInputBinding();
        if (inputBinding == null) {
            return false;
        }
        final int packageUid = inputBinding.getUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final AppOpsManager appOpsManager =
                    (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            try {
                appOpsManager.checkPackage(packageUid, packageName);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        final PackageManager packageManager = mContext.getPackageManager();
        final String possiblePackageNames[] = packageManager.getPackagesForUid(packageUid);
        for (final String possiblePackageName : possiblePackageNames) {
            if (packageName.equals(possiblePackageName)) {
                return true;
            }
        }
        return false;
    }

    private void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull Uri uri) {

        final EditorInfo editorInfo = imeListener.getImeCurrentEditorInfo();

        if (!validatePackageName(editorInfo)) {
            return;
        }
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        } else {
            flag = 0;
            try {
                mContext.grantUriPermission(
                        editorInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
            }
        }

        if (imeListener.getImeCurrentInputConnection() != null) {
            imeListener.getImeCurrentInputConnection().commitText(description + (description.equals("") ? "" : "\n"), 1);
        }
        if (isCommitContentSupported(editorInfo, mimeType)) {
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_IMAGE_SHARING, null);
            inputContentInfoCompat = new InputContentInfoCompat(
                    uri,
                    new ClipDescription(description, new String[]{mimeType}),
                    null /* linkUrl */);

            InputConnectionCompat.commitContent(
                    imeListener.getImeCurrentInputConnection(),
                    imeListener.getImeCurrentEditorInfo(), inputContentInfoCompat,
                    flag, null);

        } else if (mimeType.equalsIgnoreCase("image/png")) {

            Toast.makeText(mContext, "Image not supported", Toast.LENGTH_SHORT).show();

        }

    }

    private void doCommitContentMultiple(@NonNull String mimeType,
                                         @NonNull ArrayList<Uri> uris) {

        final EditorInfo editorInfo = imeListener.getImeCurrentEditorInfo();

        if (!validatePackageName(editorInfo)) {
            return;
        }

        if (isCommitContentSupported(editorInfo, mimeType)) {

            Log.d("here", "here200");
            MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_IMAGE_SHARING, null);

            imeListener.getImeCurrentInputConnection().beginBatchEdit();
            for (int i = 0; i < uris.size(); i++) {
                if (Build.VERSION.SDK_INT >= 25) {
                    flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
                } else {
                    flag = 0;
                    try {
                        mContext.grantUriPermission(
                                editorInfo.packageName, uris.get(i), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception e) {
                    }
                }
                if (uris.get(i) != null) {
                    inputContentInfoCompat = new InputContentInfoCompat(
                            uris.get(i),
                            new ClipDescription("", new String[]{mimeType}),
                            null /* linkUrl */);
                    InputConnectionCompat.commitContent(
                            imeListener.getImeCurrentInputConnection(),
                            imeListener.getImeCurrentEditorInfo(), inputContentInfoCompat,
                            flag, null);
                }

            }
            imeListener.getImeCurrentInputConnection().endBatchEdit();

        }

        if (!isCommitContentSupported(editorInfo, mimeType) && mimeType.equalsIgnoreCase("image/png")) {

            Toast.makeText(mContext, "Image not supported", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(final AllSuggestionModel model) {

        permissions();

        onClickRegister(model);

    }


    @Override
    public View getCandidateView() {
        return mCandidateView;
    }

    private void sendKeyEvent(int keyEventCode) {
        if (imeListener.getImeCurrentInputConnection() != null) {
            imeListener.getImeCurrentInputConnection().sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
            imeListener.getImeCurrentInputConnection().sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEY_SYM_SHIFT:
                mShiftType = ShiftType.LOCKED;
                mKeyboardView.setShifted(true);
                return true;
        }
        return false;
    }


    public void showWordSuggestions(InputConnection inputConnection) {
        if (mPrimaryCode != 32 && mPrimaryCode != -2007 && currentCandidateType.equals(KeyboardUtils.CandidateType.TEXT_LIST)) {

            //if (currentCandidateType.equals(KeyboardUtils.CandidateType.TEXT_LIST)) {
            CharSequence inputSequence = inputConnection.getTextBeforeCursor(1000, 0);
            if (inputSequence != null && inputSequence.length() > 0) {
                text = inputSequence.toString();
                if (inputSequence.toString().lastIndexOf(" ") > 0) {
                    text = inputSequence.toString().substring(inputSequence.toString().lastIndexOf(" "), inputSequence.toString().length());
                }
                if (spellCorrectorAsyncTask != null) {
                    spellCorrectorAsyncTask.cancel(true);
                }
                spellCorrectorAsyncTask = new SpellCorrectorAsyncTask();
                spellCorrectorAsyncTask.execute(text);
                //mExecutorService.execute(new Thread(searchKeywordRunnable));
            }
        }
    }

    Runnable updateKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) mSuggestions);
            mCandidateView.setDataToCandidateType(currentCandidateType, bundle);
        }
    };

    Runnable searchKeywordRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(updateKeyboardRunnable);
            //mSuggestions = new ArrayList<>();
            mSuggestions = MethodUtils.fetchWordsFromDatabase(mDatabaseTable, text.trim().length() > 0 ? text.trim() : " ");
            if (mSuggestions != null && mSuggestions.size() < 3) {
                corrector.setSuggestedWordListLimit(3 - mSuggestions.size());
                mSuggestions.addAll(corrector.correct(text.trim()));
            }
            mSuggestedWordlist = new HashMap<>();
            List<Map<String, String>> mSuggestedWordmap = new ArrayList<>();
            boolean isWordPresent = false;
            if (text.trim().length() > 0) {
                Map<String, String> map = new HashMap<>();
                for (KeywordModel model : mSuggestions) {
                    if (model.getWord().trim().equalsIgnoreCase(text.trim())) {
                        isWordPresent = true;
                    }
                    mSuggestedWordlist.put(model.getWord().trim().toLowerCase(), model.getType());
                }

                if (!isWordPresent) {
                    KeywordModel model = new KeywordModel();
                    model.setWord(text);
                    model.setType(KeywordModel.NEW_WORD);
                    mSuggestions.add(0, model);
                }
            }
            mHandler.post(updateKeyboardRunnable);
            //mHandler.post(updateKeyboardRunnable);

        }
    };

    class KeyboardListener extends AbstractKeyboardListener {
        @Override
        public void onPress(int primaryCode) {
            mKeyboardView.setPreviewEnabled(mKeyboardTypeCurrent != KeyboardUtils.KeyboardType.NUMBERS && primaryCode >= 0);
            //mPopUpView.showAtLocation(mCurrentKeyboard.getKeys().get(primaryCode), mKeyboardView);
        }

        @Override
        public void onRelease(int primaryCode) {
            //mPopUpView.onRelease();
            mKeyboardView.setPreviewEnabled(false);
        }

        @Override
        public void onKey(int primaryCode, int[] keys) {
            InputConnection inputConnection = imeListener.getImeCurrentInputConnection();
            playClick(primaryCode);
            mPrimaryCode = primaryCode;

            if (imeListener.getImeCurrentEditorInfo() != null) {
                imeListener.getImeCurrentEditorInfo().inputType = TYPE_TEXT_FLAG_AUTO_CORRECT;
            }

            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    //sendKeyEvent(KeyEvent.KEYCODE_DEL);
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = true;
                    inputConnection.deleteSurroundingText(1, 0);
                    //inputConnection.commitText("", 1);
                    //showWordSuggestions(primaryCode, inputConnection);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    onShiftPressed();
                    break;
                case KEY_IME_OPTION:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    final EditorInfo editorInfo = imeListener.getImeCurrentEditorInfo();
                    final int imeOptionsActionId = KeyboardUtils.getImeOptionsActionIdFromEditorInfo(editorInfo);
                    if (inputConnection != null && KeyboardUtils.IME_ACTION_CUSTOM_LABEL == imeOptionsActionId) {
                        // Either we have an actionLabel and we should performEditorAction with
                        // actionId regardless of its value.
                        inputConnection.performEditorAction(editorInfo.actionId);
                    } else if (inputConnection != null && EditorInfo.IME_ACTION_NONE != imeOptionsActionId) {
                        // We didn't have an actionLabel, but we had another action to execute.
                        // EditorInfo.IME_ACTION_NONE explicitly means no action. In contrast,
                        // EditorInfo.IME_ACTION_UNSPECIFIED is the default value for an action, so it
                        // means there should be an action and the app didn't bother to set a specific
                        // code for it - presumably it only handles one. It does not have to be treated
                        // in any specific way: anything that is not IME_ACTION_NONE should be sent to
                        // performEditorAction.
                        inputConnection.performEditorAction(imeOptionsActionId);
                    } else {
                        sendKeyEvent(KeyEvent.KEYCODE_ENTER);
                    }

                    break;
                case KEY_NUMBER:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.NUMBERS);
                    break;
                case KEY_EMOJI:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    mTabType = TabType.NO_TAB;
                    addCandidateTypeView(KeyboardUtils.CandidateType.BOOST_SHARE, TabType.NO_TAB);
                    manageKeyboardView.showEmojiLayout();
                    break;
                case KEY_QWRTY:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
                    break;
                case KEY_SYM:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.SYMBOLS);
                    break;
                case KEY_SYM_SHIFT:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.SYMBOLS_SHIFT);
                    break;
                case KEY_LANGUAGE_CHANGE:
                    isSelectedKeyboardItem = false;
                    isSelectedKeyboardEdited = false;
                    showLanguageMethods();
                    break;
                case KEY_SPACE:
                    primaryCode = 32;

                default:
                    if (isSelectedKeyboardEdited && primaryCode == KEY_SPACE) {
                        isSelectedKeyboardEdited = false;
                    }
                    if (primaryCode != 32) {
                        isSelectedKeyboardItem = false;
                    }
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && mShiftType != ShiftType.NORMAL) {
                        code = Character.toUpperCase(code);
                    }
                    if (inputConnection != null) {
                        inputConnection.commitText(String.valueOf(code), 1);
                    }
                    //showWordSuggestions(primaryCode, inputConnection);

            }
            if (primaryCode != Keyboard.KEYCODE_SHIFT && mShiftType == ShiftType.CAPITAL) {
                mShiftType = ShiftType.NORMAL;
                mKeyboardView.setShifted(false);
            }
        }

        protected void playClick(int keyCode) {

            mKeyboardView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            if (mAudioManager == null) {
                return;
            }
            switch (keyCode) {
                case KEY_SPACE:
                    mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;
                case Keyboard.KEYCODE_DONE:
                case 10:
                    mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;
                case Keyboard.KEYCODE_DELETE:
                    mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;
                default:
                    mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        }

        private void showLanguageMethods() {
            boolean isLanguageVisible = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mInputMethodManager != null) {
                    // if true show keyboard language change button by calling  switchToNextInputMethod()
                    isLanguageVisible = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
                }
            }
            if (isLanguageVisible) {
                boolean isLastIMESwitched = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    isLastIMESwitched = mInputMethodManager.switchToNextInputMethod(getToken(), false);
                }
                if (!isLastIMESwitched) {
                    mInputMethodManager.switchToLastInputMethod(getToken());
                }
            } else {
                Toast.makeText(mContext, "Unable to show other language keyboard", Toast.LENGTH_SHORT).show();
            }
        }

        private void onShiftPressed() {
            mShiftType = mShiftType == ShiftType.NORMAL ? ShiftType.CAPITAL : ShiftType.NORMAL;
            caps = !caps;
            mKeyboardView.setShifted(mShiftType != ShiftType.NORMAL);
            mKeyboardView.invalidateAllKeys();
        }

        @Override
        public void onText(CharSequence charSequence) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }

    }

    private class SpellCorrectorAsyncTask extends AsyncTask<String, Void, ArrayList<KeywordModel>> {
        @Override
        protected ArrayList<KeywordModel> doInBackground(String... strings) {
            mSuggestions.clear();
            //mSuggestions = MethodUtils.fetchWordsFromDatabase(mDatabaseTable, text.trim().length() > 0 ? text.trim() : " ");
            if (mSuggestions != null && mSuggestions.size() < 3) {
                corrector.setSuggestedWordListLimit(3 - mSuggestions.size());
                mSuggestions.addAll(corrector.correct(text.trim()));
            }
            mSuggestedWordlist = new HashMap<>();
            List<Map<String, String>> mSuggestedWordmap = new ArrayList<>();
            boolean isWordPresent = false;
            if (text.trim().length() > 0) {
                Map<String, String> map = new HashMap<>();
                for (KeywordModel model : mSuggestions) {
                    if (model.getWord().trim().equalsIgnoreCase(text.trim())) {
                        isWordPresent = true;
                    }
                    mSuggestedWordlist.put(model.getWord().trim().toLowerCase(), model.getType());
                }

                if (!isWordPresent) {
                    KeywordModel model = new KeywordModel();
                    model.setWord(text);
                    model.setType(KeywordModel.NEW_WORD);
                    mSuggestions.add(0, model);
                }
            }
            return mSuggestions;
        }

        @Override
        protected void onPostExecute(ArrayList<KeywordModel> strings) {

            super.onPostExecute(strings);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) mSuggestions);
            mCandidateView.setDataToCandidateType(currentCandidateType, bundle);
        }
    }

    public void permissions()
    {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(mContext, "Please grant external storage permission", Toast.LENGTH_SHORT).show();
            //MethodUtils.getPermissions(mContext);
            return;
        }
    }

    private String onClickRegister(AllSuggestionModel model)
    {
        if (model == null)
        {
            Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
            return null;
        }

        JSONObject object = new JSONObject();

        try
        {
            object.put("id", model.getId());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String shareUrl = appendedUrl(model.getUrl());

        switch (model.getTypeEnum())
        {
            case ImageAndText:

                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_UPDATE_IMAGE_SHARE, object);
                MethodUtils.onGlideBitmapReady(this, model.getText() + "\nUrl: " + shareUrl, model.getImageUrl(), model.getId());
                break;

            case Product:

                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_PRODUCT_SHARE, object);
                StringBuilder builder = new StringBuilder("Product: " + model.getText());

                try
                {
                    DecimalFormat df = new DecimalFormat("#,##,##,##,##,##,##,###.##");

                    double price = TextUtils.isEmpty(model.getPrice()) ? 0 : Double.valueOf(model.getPrice());
                    double discount = TextUtils.isEmpty(model.getDiscount()) ? 0 : Double.valueOf(model.getDiscount());

                    String formatted = df.format(price);

                    if(discount > 0)
                    {
                        String _mrp = (price == 0) ? "" : "\nMRP: " + (model.getCurrencyCode() + " " + formatted);
                        builder.append(_mrp);
                    }

                    formatted = df.format(price - discount);
                    String _price = (price - discount <= 0) ? "" : "\nPrice: " + (model.getCurrencyCode() + " " + formatted);
                    builder.append(_price);

                    formatted = df.format(discount);
                    String _discount = (discount == 0) ? "" : "\nYou Save: " + (model.getCurrencyCode() + " " + formatted);
                    builder.append(_discount);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    builder.append("\n\nClick to Buy: ");
                    builder.append(shareUrl);
                }

                /*MethodUtils.onGlideBitmapReady(this, "Product: " + model.getText() +
                        "\nPrice: " + model.getCurrencyCode() + " " + model.getPrice() + "\n\nClick to Buy: " + shareUrl, model.getImageUrl(), model.getId());*/

                MethodUtils.onGlideBitmapReady(this, builder.toString(), model.getImageUrl(), model.getId());
                break;

            case DetailsShare:

                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_UPDATE_SHARE, object);
                String description = model.getName() + "\n" + model.getBusinessName() + "\n"
                        + model.getPhoneNumber() + "\n\nWebsite: " + model.getWebsite() + "\nEmail: " + model.getEmail() +
                        "\n\nAddress: " + model.getAddress() + "\nLocation: " + model.getLocation();
                doCommitContent(description, "text/plain", null);
                break;

            case Text:

                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_UPDATE_SHARE, object);
                doCommitContent(model.getText() + "\nUrl: " + shareUrl, "text/plain", null);
                break;

            case ImageShare:

                MixPanelUtils.getInstance().track(MixPanelUtils.KEYBOARD_UPDATE_IMAGE_SHARE, object);
                MethodUtils.onGlideBitmapReady(this, "", model.getImageUri(), model.getId());

            default:

                break;
        }

        return shareUrl;
    }

    String appendedUrl(String url)
    {
        Uri uri = null;
        String shareUrl = null;

        try
        {
            if (!TextUtils.isEmpty(url))
            {
                uri = Uri.parse(url).buildUpon().appendQueryParameter(UTM_SOURCE, "bk_android")
                        .appendQueryParameter(UTM_MEDIUM, TextUtils.isEmpty(packageName) ? "share" : packageName).build();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (uri != null)
        {
            shareUrl = uri.toString();
        }

        return shareUrl;
    }
}