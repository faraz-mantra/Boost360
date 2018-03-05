package nowfloats.nfkeyboard.keyboards;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Helper.EmojiconGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;
import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nowfloats.nfkeyboard.interface_contracts.ImeToPresenterInterface;
import nowfloats.nfkeyboard.interface_contracts.ItemClickListener;
import nowfloats.nfkeyboard.interface_contracts.PresenterToImeInterface;
import nowfloats.nfkeyboard.interface_contracts.UrlToBitmapInterface;
import nowfloats.nfkeyboard.models.AllSuggestionModel;
import nowfloats.nfkeyboard.models.networkmodels.Product;
import nowfloats.nfkeyboard.network.CallBack;
import nowfloats.nfkeyboard.network.Float;
import nowfloats.nfkeyboard.network.NetworkAdapter;
import nowfloats.nfkeyboard.network.Updates;
import nowfloats.nfkeyboard.util.KeyboardUtils;
import nowfloats.nfkeyboard.util.MethodUtils;
import nowfloats.nfkeyboard.util.SharedPrefUtil;
import timber.log.Timber;

/**
 * Created by Admin on 26-02-2018.
 */

public class ImePresenterImpl implements ItemClickListener,
        ImeToPresenterInterface,
        CandidateToPresenterInterface,
        View.OnClickListener,
        UrlToBitmapInterface {
    private CandidateViewBaseImpl mCandidateView;
    private KeyboardViewBaseImpl mKeyboardView;
    private ManageKeyboardView manageKeyboardView;
    public final static int KEY_EMOJI = -2005,KEY_IME_OPTION = -2006, KEY_SPACE = -2007, KEY_NUMBER = -2000, KEY_SYM = -2001, KEY_SYM_SHIFT = -2002, KEY_QWRTY = -2003
            ,KEY_LANGUAGE_CHANGE= -2004;
    private Context mContext;
    private KeyboardUtils.CandidateType currentCandidateType = KeyboardUtils.CandidateType.BOOST_SHARE;
    private KeyboardUtils.KeyboardType mKeyboardTypeCurrent = KeyboardUtils.KeyboardType.QWERTY_LETTERS;
    private KeyboardBaseImpl mCurrentKeyboard;
    private boolean caps;
    private int imeOptionId;
    private ArrayList<AllSuggestionModel> updatesList, productList;
    private InputMethodManager mInputMethodManager;
    private PresenterToImeInterface imeListener;
    private AudioManager mAudioManager;
    private TabType mTabType;
    private ShiftType mShiftType = ShiftType.CAPITAL;
    private KeyAction KeyActionType;

    @Override
    public void onSpeechResult(String speech) {
        if (!TextUtils.isEmpty(speech))
            imeListener.getImeCurrentInputConnection().commitText(speech,1);
        if (mTabType != TabType.KEYBOARD) {
            mTabType = TabType.KEYBOARD;
            manageKeyboardView.showKeyboardLayout();
            mCandidateView.addCandidateTypeView(currentCandidateType);
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        imeListener.getImeCurrentInputConnection().commitText(emojicon.getEmoji(), 1);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {

    }

    private enum KeyAction{
        KEY_DOWN, KEY_UP;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onResourcesReady(Bitmap bitmap, String text, String imageId) {
        doCommitContent(text, "image/png",
                MethodUtils.getImageUri(mContext,bitmap, TextUtils.isEmpty(imageId)?
                        UUID.randomUUID().toString():imageId));
    }

    public enum TabType {
        PRODUCTS, UPDATES,KEYBOARD,SETTINGS;
    }
    private enum ShiftType{
        LOCKED,CAPITAL,NORMAL;
    }

    ImePresenterImpl(Context context, PresenterToImeInterface imeListener){
        mCandidateView = new CandidateViewBaseImpl(context);
        mCandidateView.setItemClickListener(this);
        mContext = context;
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        this.imeListener = imeListener;
    }

    @Override
    public View onCreateInputView(){
        manageKeyboardView = (ManageKeyboardView) LayoutInflater.from(mContext).inflate(R.layout.keyboard_view, null);
        manageKeyboardView.setPresenterListener(this);
        mKeyboardView = manageKeyboardView.getKeyboard();
        setCurrentKeyboard(mKeyboardTypeCurrent);
        mKeyboardView.setOnKeyboardActionListener(new KeyboardListener());
        return manageKeyboardView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS){
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                setKeyboardType(KeyboardUtils.KeyboardType.NUMBERS);
                currentCandidateType = KeyboardUtils.CandidateType.NULL;
                break;
            case InputType.TYPE_CLASS_TEXT:
            default:
                currentCandidateType = KeyboardUtils.CandidateType.BOOST_SHARE;
                setKeyboardType(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
        }
        imeOptionId = attribute.imeOptions;
        initializeValues();
    }

    private void initializeValues() {
        updatesList = null; productList = null;
        mTabType = TabType.KEYBOARD;
    }

    private void setImeOptions(Resources res, int options) {
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
                KeyActionType = KeyAction.KEY_UP;
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = "Go";
                break;
            case EditorInfo.IME_ACTION_NEXT:
                KeyActionType = KeyAction.KEY_UP;
                mEnterKey.icon = res.getDrawable(R.drawable.ic_next);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                KeyActionType = KeyAction.KEY_DOWN;
                mEnterKey.icon = res.getDrawable(R.drawable.ic_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                KeyActionType = KeyAction.KEY_DOWN;
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = "send";
                break;
            default:
                KeyActionType = KeyAction.KEY_DOWN;
                mEnterKey.icon = res.getDrawable(R.drawable.ic_enter_arrow);
                mEnterKey.label = null;
                break;
        }
        //mKeyboardView.invalidateKey(mCurrentKeyboard.getKeys().size()-1);
    }

    public void setKeyboardType(KeyboardUtils.KeyboardType type){
        mKeyboardTypeCurrent = type;
    }

    public void setCurrentKeyboard(){
        setCurrentKeyboard(mKeyboardTypeCurrent);
        addCandidateTypeView(currentCandidateType);
    }

    public void setCurrentKeyboard(KeyboardUtils.KeyboardType type){
        if (mKeyboardTypeCurrent != type){
            mKeyboardTypeCurrent = type;
        }
        mCurrentKeyboard = mKeyboardView.getKeyboard(type);
        setImeOptions(mContext.getResources(), imeOptionId);
        mKeyboardView.setKeyboard(mCurrentKeyboard);
        mKeyboardView.setShifted(mShiftType != ShiftType.NORMAL);
        manageKeyboardView.showKeyboardLayout();
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

    private void addCandidateTypeView(KeyboardUtils.CandidateType type){
        imeListener.setCandidatesViewShown(mCandidateView.addCandidateTypeView(type));
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
            Timber.e("inputBinding should not be null here. "
                    + "You are likely to be hitting b.android.com/225029");
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
            } catch (Exception e){
                Timber.e("grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + uri);
            }
        }

        imeListener.getImeCurrentInputConnection().commitText(description,1);
        if(isCommitContentSupported(editorInfo, mimeType)) {
            final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                    uri,
                    new ClipDescription(description, new String[]{mimeType}),
                    null /* linkUrl */);
            InputConnectionCompat.commitContent(
                    imeListener.getImeCurrentInputConnection(),
                    imeListener.getImeCurrentEditorInfo(), inputContentInfoCompat,
                    flag, null);
        }

    }

    @Override
    public void onItemClick(final AllSuggestionModel model) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Please grant external storage permission", Toast.LENGTH_SHORT).show();
            MethodUtils.getPermissions(mContext);
            return;
        }
        if (model == null) {
            Toast.makeText(mContext, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (model.getTypeEnum()) {

            case ImageAndText:
                MethodUtils.onGlideBitmapReady(this, model.getText(), model.getImageUrl(), model.getId());
                break;
            case Product:
                MethodUtils.onGlideBitmapReady(this, "Name: " + model.getText() + "\nPrice: " +
                        model.getPrice() + "\nDiscount: " + model.getDiscount() +
                        "\nDescription: " + model.getDiscount(), model.getImageUrl(), model.getId());
                break;
            case Text:
                doCommitContent(model.getText(), "text/plain", null);
                break;
            default:
                break;
        }
    }


    @Override
    public View getCandidateView() {
        return currentCandidateType == KeyboardUtils.CandidateType.NULL? null : mCandidateView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() ==  R.id.img_nowfloats) {
            if (mTabType != TabType.KEYBOARD) {
                mTabType = TabType.KEYBOARD;
                manageKeyboardView.showKeyboardLayout();
            }
        }else if(view.getId() ==  R.id.tv_updates) {
            if (mTabType != TabType.UPDATES) {
                mTabType = TabType.UPDATES;
                manageKeyboardView.showShareLayout(updatesList == null ?
                        createModelList(TabType.UPDATES) : updatesList);
            }
        }else if(view.getId() ==  R.id.tv_products) {
            if (mTabType != TabType.PRODUCTS) {
                mTabType = TabType.PRODUCTS;
                manageKeyboardView.showShareLayout(productList == null ?
                        createModelList(TabType.PRODUCTS) : productList);
            }
        }else if(view.getId() ==  R.id.img_settings) {
            if (mTabType != TabType.SETTINGS) {
                mTabType = TabType.SETTINGS;
            }
            manageKeyboardView.showSpeechInput();
        }
    }
    public ArrayList<AllSuggestionModel> createModelList(TabType suggestionType) {
        if(suggestionType == TabType.PRODUCTS) {
            productList = new ArrayList<>();
            NetworkAdapter adapter = new NetworkAdapter();
            SharedPrefUtil boostPref = SharedPrefUtil.fromBoostPref().getsBoostPref(mContext);
            adapter.getAllProducts(boostPref.getFpTag(), mContext.getString(R.string.client_id),
                    0, "SINGLE", new CallBack<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> data) {
                        for(Product product : data) {
                            productList.add(product.toAllSuggestion());
                        }
                        if (mTabType == TabType.PRODUCTS) {
                            manageKeyboardView.onSetSuggestions(productList);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
            return productList;
        } else if(suggestionType == TabType.UPDATES) {
            updatesList = new ArrayList<>();
            NetworkAdapter adapter = new NetworkAdapter();
            SharedPrefUtil boostPref = SharedPrefUtil.fromBoostPref().getsBoostPref(mContext);
            adapter.getAllUpdates(boostPref.getFpId(), mContext.getString(R.string.client_id),
                    0, 0, new CallBack<Updates>() {
                        @Override
                        public void onSuccess(Updates data) {
                            for(Float update : data.getFloats()) {
                                updatesList.add(update.toAllSuggestion());
                            }
                            if(mTabType == TabType.UPDATES) {
                                manageKeyboardView.onSetSuggestions(updatesList);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
            return updatesList;
        }
      return null;
    }
    public boolean onKeyLongPress(int keyCode, KeyEvent event){
        switch (keyCode){
            case KEY_SYM_SHIFT:
                mShiftType = ShiftType.LOCKED;
                mKeyboardView.setShifted(true);
                return true;
        }
        return false;
    }
    class KeyboardListener extends AbstractKeyboardListener {
        PopUpView mPopUpView;
        KeyboardListener(){
            mPopUpView = new PopUpView(mContext);
        }

        @Override
        public void onPress(int primaryCode) {
            mKeyboardView.setPreviewEnabled(primaryCode>=0);

            //mPopUpView.showAtLocation(mCurrentKeyboard.getKeys().get(15),mKeyboardView);
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
            switch(primaryCode){
                case Keyboard.KEYCODE_DELETE :
                    inputConnection.deleteSurroundingText(1, 0);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    onShiftPressed();
                    break;
                case KEY_IME_OPTION:
                    inputConnection.sendKeyEvent(new KeyEvent(KeyActionType == KeyAction.KEY_DOWN ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                    break;
                case KEY_NUMBER:
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.NUMBERS);
                    break;
                case KEY_EMOJI:
                    manageKeyboardView.showEmojiLayout();
                    break;
                case KEY_QWRTY:
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.QWERTY_LETTERS);
                    break;
                case KEY_SYM:
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.SYMBOLS);
                    break;
                case KEY_SYM_SHIFT:
                    setCurrentKeyboard(KeyboardUtils.KeyboardType.SYMBOLS_SHIFT);
                    break;
                case KEY_LANGUAGE_CHANGE:
                    showLanguageMethods();
                    break;
                case KEY_SPACE:
                    primaryCode = 32;
                default:
                    char code = (char)primaryCode;
                    if(Character.isLetter(code) && mShiftType != ShiftType.NORMAL){
                        code = Character.toUpperCase(code);
                    }
                    inputConnection.commitText(String.valueOf(code),1);
            }
            if (primaryCode != Keyboard.KEYCODE_SHIFT && mShiftType == ShiftType.CAPITAL){
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mInputMethodManager != null) {
                    // if true show keyboard language change button by calling  switchToNextInputMethod()
                    boolean isLanguageVisible = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
                    if (isLanguageVisible){
                        mInputMethodManager.showInputMethodPicker();
                    }else{
                        Toast.makeText(mContext, "Unable to show other language", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        private void onShiftPressed() {
            mShiftType = mShiftType == ShiftType.NORMAL ? ShiftType.CAPITAL : ShiftType.NORMAL;
            caps = !caps;
            mCurrentKeyboard.setShifted(mShiftType != ShiftType.NORMAL);
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
}
