package com.nowfloats.riachatsdk.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jakewharton.retrofit.Ok3Client;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.adapters.RvButtonsAdapter;
import com.nowfloats.riachatsdk.adapters.RvChatAdapter;
import com.nowfloats.riachatsdk.animators.ChatItemAnimator;
import com.nowfloats.riachatsdk.fragments.CustomDialogFragment;
import com.nowfloats.riachatsdk.fragments.PickAddressFragment;
import com.nowfloats.riachatsdk.helpers.ChatLogger;
import com.nowfloats.riachatsdk.helpers.DeviceDetails;
import com.nowfloats.riachatsdk.interfaces.ChatJsonInterface;
import com.nowfloats.riachatsdk.interfaces.IChatAnimCallback;
import com.nowfloats.riachatsdk.interfaces.IConfirmationCallback;
import com.nowfloats.riachatsdk.models.Button;
import com.nowfloats.riachatsdk.models.RiaCardModel;
import com.nowfloats.riachatsdk.models.Section;
import com.nowfloats.riachatsdk.services.FileUploadService;
import com.nowfloats.riachatsdk.utils.Constants;
import com.nowfloats.riachatsdk.utils.Utils;
import com.squareup.picasso.Picasso;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.INVISIBLE;

public class ChatViewActivity extends AppCompatActivity implements RvButtonsAdapter.OnItemClickListener,
        IConfirmationCallback, IChatAnimCallback {

    private Toolbar toolbar;

    private RecyclerView rvChatData, rvButtonsContainer;

    private LinearLayout cvChatInput;

    private AutoCompleteTextView etChatInput;

    private ImageView ivSendMessage, ivScrollDown, ivBack;

    private TextView tvPrefix, tvPostfix, tvSkip;

    private Handler mHandler;

    private Button mCurrButton, mDefaultButton;

    private List<RiaCardModel> mAllNodes = null;

    private Map<String, String> mDataMap = new HashMap<>();

    private List<Section> mSectionList = new ArrayList<>();

    private List<Button> mButtonList = new ArrayList<>();

    private String mCurrVarName = null, mNextNodeId = "-1", appVersion = "", mCurrNodeId, mCurrentDeepLink;

    private final String KEY_NEXT_NODE_ID = "NextNodeId";

    private final String KEY_FP_CREATION_STATUSCODE = "FPCREATION_STATUSCODE";

    private Map<String, String> mAutoComplDataHash;

    private RvChatAdapter mAdapter;

    private RvButtonsAdapter mButtonsAdapter;

    private RequestQueue mRequestQueue;

    private PickAddressFragment pickAddressFragment;


    private FileUploadResultReceiver mReceiver;

    private TextView tvRiaTyping;

    private ProgressBar progressBar;

    private DisplayMetrics displayMetrics;

    private int FP_STATUS_CODE = -100;

    private static final int FP_CREATED = 1;

    private static final int FP_NOT_CREATED = -100;

    private static final int AUDIO_REQUEST_CODE = 56;

    /*
     **************************** CONSTANTS *******************************
     */

    public static final String Save_Name = "nameKey";
    public static final String Save_Cat = "categoryKey";
    public static final String Save_Phone = "phoneNumberKey";
    public static final String Save_Phone_code = "phoneCodeKey";
    public static final String Save_Email = "emailKey";
    public static final String Save_Country = "countryKey";
    public static final String Save_City = "cityKey";
    public static final String Save_Lat = "ria_latKey";
    public static final String Save_Lng = "ria_lngKey";

    public static final String Save_Website_Address = "websiteAdressKey";
    public static final String Save_Otp = "otpKey";
    public static final String Save_IS_FP_AVAILABLE = "isFPAvailable";
    public static final String Save_Pin_Code = "pincodeKey";
    public static final String Save_Street_Address = "streetAddressKey";
    public static final String Save_Locality = "localityKey";

    /*
     **************************** CONSTANTS *******************************
    */

    private Runnable mAutoCallRunnable = new Runnable() {
        @Override
        public void run() {
            onItemClick(mDefaultButton);
        }
    };

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            switch (mCurrentDeepLink) {
                case Constants.DeepLinkUrl.ASK_LOC_PERM:
                    showNextNode(mNextNodeId);
                    break;
            }
            Toast.makeText(ChatViewActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            switch (mCurrentDeepLink) {
                case Constants.DeepLinkUrl.ASK_LOC_PERM:
                    showNextNode(mNextNodeId);
                    break;
            }
            Toast.makeText(ChatViewActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initializeControls();

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        setListeners();

        if (isNetworkStatusAvialable(ChatViewActivity.this)) {
            fetchChatJson();
        } else {
            showCustomDialog(CustomDialogFragment.DialogFrom.NO_INTERNET);
        }
    }


    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    private void initializeControls() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary));
        }

        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.doodle_bg));

        displayMetrics = getResources().getDisplayMetrics();
        rvChatData = (RecyclerView) findViewById(R.id.rv_chat_data);
        rvButtonsContainer = (RecyclerView) findViewById(R.id.rv_reply_button_container);
        cvChatInput = (LinearLayout) findViewById(R.id.cv_chat_input);
        etChatInput = (AutoCompleteTextView) findViewById(R.id.et_chat_input);
        ivSendMessage = (ImageView) findViewById(R.id.iv_send_msg);
        ivScrollDown = (ImageView) findViewById(R.id.iv_scroll_down);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvPrefix = (TextView) findViewById(R.id.tv_prefix);
        tvSkip = (TextView) findViewById(R.id.tv_skip);
        tvPostfix = (TextView) findViewById(R.id.tv_postfix);
        tvRiaTyping = (TextView) findViewById(R.id.tv_ria_typing);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        progressBar.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.white),
                        PorterDuff.Mode.SRC_IN);

    }

    private void setListeners() {

        ivScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvSkip.setVisibility(View.VISIBLE);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(CustomDialogFragment.DialogFrom.SKIP);
            }
        });

        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etChatInput.getText().toString().trim().equals("") && mNextNodeId != null && !mNextNodeId.equals("-1")
                        && isValidInput(etChatInput.getText().toString().trim())) {
                    hideSoftKeyboard();
                    StringBuffer chatText = new StringBuffer("");
                    if (mCurrButton.getPrefixText() != null) {
                        chatText.append(getParsedPrefixPostfixText(mCurrButton.getPrefixText()));
                    }
                    chatText.append(etChatInput.getText().toString().trim());
                    if (mCurrButton.getPostfixText() != null) {
                        chatText.append(getParsedPrefixPostfixText(mCurrButton.getPrefixText()));
                    }

                    if (!mCurrButton.isPostToChat()) {
                        rvButtonsContainer.setVisibility(View.INVISIBLE);
                        cvChatInput.setVisibility(View.INVISIBLE);
                        if (mCurrVarName != null) {
                            mDataMap.put("[~" + mCurrVarName + "]", etChatInput.getText().toString().trim());
                            ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                    mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), mCurrVarName.replace("[~", "").replace("]", ""),
                                    etChatInput.getText().toString().trim(), mCurrButton.getButtonType(), appVersion);
                        } else {
                            ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                    mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), null,
                                    null, mCurrButton.getButtonType(), appVersion);
                        }
                        showNextNode(mCurrButton.getNextNodeId());
                        mAutoComplDataHash = null;
                        mButtonList.clear();
                        mButtonsAdapter.notifyDataSetChanged();
                    } else {
                        replyToRia(Constants.SectionType.TYPE_TEXT, chatText.toString());
                        if (mCurrVarName != null) {
                            if (mAutoComplDataHash == null || mAutoComplDataHash.get(etChatInput.getText().toString().trim()) == null) {
                                mDataMap.put("[~" + mCurrVarName + "]", etChatInput.getText().toString().trim());
                                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                        mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), mCurrVarName.replace("[~", "").replace("]", ""),
                                        etChatInput.getText().toString().trim(), mCurrButton.getButtonType(), appVersion);
                            } else {
                                mDataMap.put("[~" + mCurrVarName + "]", mAutoComplDataHash.get(etChatInput.getText().toString().trim()));
                                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                        mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), mCurrVarName.replace("[~", "").replace("]", ""),
                                        mAutoComplDataHash.get(etChatInput.getText().toString().trim()), mCurrButton.getButtonType(), appVersion);
                            }
                        }
                        mAutoComplDataHash = null;
                        //TODO:sent_check ButtonType and do the action accordingly
                        etChatInput.setText("");
                        mButtonList.clear();
                        mButtonsAdapter.notifyDataSetChanged();
                        rvButtonsContainer.setVisibility(View.INVISIBLE);
                        cvChatInput.setVisibility(View.INVISIBLE);
                        showNextNode(mNextNodeId);
                    }
                }
            }
        });

        etChatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    ivSendMessage.getBackground().setColorFilter(Color.parseColor("#157EFB"), PorterDuff.Mode.DARKEN);
                } else {
                    ivSendMessage.getBackground().setColorFilter(Color.parseColor("#40157EFB"), PorterDuff.Mode.DARKEN);
                }
            }
        });

        rvChatData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (position < (mSectionList.size() - 2)) {
                    ivScrollDown.setVisibility(View.VISIBLE);
                } else {
                    ivScrollDown.setVisibility(View.INVISIBLE);
                }
            }
        });

        mReceiver = new
                FileUploadResultReceiver(new Handler());

    }

    public static void showSnackBarNegative(Activity context, String msg) {
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.parseColor("#E02200"));
        snackBar.show();
    }

    private boolean isValidInput(String input) {
        if (mCurrButton.getButtonType() == null)
            return false;
        switch (mCurrButton.getButtonType()) {
            case Constants.ButtonType.TYPE_GET_EMAIL:
                String EMAIL_PATTERN =
                        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(input);
                if (!matcher.matches())
                    showErrorMessage("Please Enter a valid E-mail");
                return matcher.matches();
            case Constants.ButtonType.TYPE_GET_PHONE_NUMBER:
                if (!input.matches("\\(?([0-9]{3})\\)?([ .-]?)([0-9]{4,9})$"))
                    showErrorMessage("Please Enter a valid Phone Number");
                return input.matches("\\(?([0-9]{3})\\)?([ .-]?)([0-9]{4,9})$");
            case Constants.ButtonType.TYPE_GET_ITEM_FROM_SOURCE:
                if (mAutoComplDataHash.get(input) == null)
                    showErrorMessage("This Category is not available");
                return mAutoComplDataHash.get(input) != null;
            default:
                return true;
        }
    }

    private void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {
            imagePicker.submit(data);
        } else if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && (data == null || data.getData() == null)) {
            showNextNode(mCurrNodeId);
        } else if (requestCode == Picker.PICK_IMAGE_CAMERA && (data == null || data.getData() == null)) {
            showNextNode(mCurrNodeId);
        } else if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Intent i = new Intent(this, FileUploadService.class);
            i.putExtra(Constants.FILE_PATH, mPaths.get(0));
            i.putExtra(Constants.RECEIVER, mReceiver);
            startService(i);
            replyToRia(Constants.SectionType.TYPE_IMAGE, mPaths.get(0));
        } else if (requestCode == AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            Intent i = new Intent(this, FileUploadService.class);
            i.putExtra(Constants.FILE_PATH, file.getAbsolutePath());
            i.putExtra(Constants.RECEIVER, mReceiver);
            startService(i);
            replyToRia(Constants.SectionType.TYPE_AUDIO, file.getAbsolutePath());
        } else if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);
            Intent i = new Intent(this, FileUploadService.class);
            i.putExtra(Constants.FILE_PATH, mPaths.get(0));
            i.putExtra(Constants.RECEIVER, mReceiver);
            startService(i);
            replyToRia(Constants.SectionType.TYPE_IMAGE, mPaths.get(0));
        }
    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        if (FP_STATUS_CODE == FP_CREATED)
            showCustomDialog(CustomDialogFragment.DialogFrom.BACK_PRESS_LOGIN);
        else if (FP_STATUS_CODE == FP_NOT_CREATED)
            showCustomDialog(CustomDialogFragment.DialogFrom.BACK_PRESS);
        else {
            goBack();
        }
    }

    private void goBack() {

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        hideSoftKeyboard();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);

    }

    private void replyToRia(String type, String... msg) {

        mHandler.removeCallbacks(mAutoCallRunnable);
        Section section = new Section();
        section.setDateTime(Utils.getFormattedDate(new Date()));

        switch (type) {
            case Constants.SectionType.TYPE_ADDRESS_CARD:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_ADDRESS_CARD);
                section.setText(getParsedPrefixPostfixText(msg[0]));
                section.setUrl(msg[1]);
                mSectionList.add(section);
                mAdapter.notifyItemInserted(mSectionList.size() - 1);
                rvChatData.scrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_SUBMIT_FORM:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_SUBMIT_FORM);
                section.setText(msg[0]);
                mSectionList.set(mSectionList.size() - 1, section);
                mAdapter.notifyItemChanged(mSectionList.size() - 1);
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_TEXT:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_TEXT);
                section.setText(getParsedPrefixPostfixText(msg[0]));
                mSectionList.add(section);
                mAdapter.notifyItemInserted(mSectionList.size() - 1);
                rvChatData.scrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_IMAGE:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_IMAGE);
                section.setUrl(msg[0]);
                section.setLoading(true);
                mSectionList.add(section);
                mAdapter.notifyItemInserted(mSectionList.size() - 1);
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_AUDIO:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_AUDIO);
                section.setUrl(msg[0]);
                section.setLoading(true);
                mSectionList.add(section);
                mAdapter.notifyItemInserted(mSectionList.size() - 1);
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_VIDEO:
                section.setFromRia(false);
                section.setSectionType(Constants.SectionType.TYPE_VIDEO);
                section.setUrl(msg[0]);
                section.setLoading(true);
                mSectionList.add(section);
                mAdapter.notifyItemInserted(mSectionList.size() - 1);
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
                break;
            case Constants.SectionType.TYPE_TYPING:
                rvChatData.smoothScrollToPosition(mSectionList.size() - 1);
        }
        section.setShowDate(true);
    }

    private int cardCount = 0;

    private void replyToRia(String type, final RiaCardModel riaCardModel, boolean isReplace) {

        mHandler.removeCallbacks(mAutoCallRunnable);
        Section section = new Section();
        section.setDateTime(Utils.getFormattedDate(new Date()));

        if (type.equalsIgnoreCase(Constants.SectionType.TYPE_CARD)
                || type.equalsIgnoreCase(Constants.SectionType.TYPE_ADDRESS_CARD)) {
            section.setCardPos(++cardCount);
        }
        section.setFromRia(false);
        section.setSectionType(type);
        section.setCardModel(riaCardModel);

        if (isReplace) {
            mSectionList.set(mSectionList.size() - 1, section);
            mAdapter.notifyItemChanged(mSectionList.size() - 1);
            rvChatData.scrollToPosition(mSectionList.size() - 1);
            if (riaCardModel.getNextNodeId() != null) {
                showNextNode(riaCardModel.getNextNodeId());
                return;
            }
            for (Button btn : riaCardModel.getButtons()) {
                if (btn.getButtonType().equals(Constants.ButtonType.TYPE_NEXT_NODE) && btn.isDefaultButton()) {
                    final Button button = btn;
                    ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                            mCurrNodeId, button.getId(), button.getButtonText(), null,
                            null, button.getButtonType(), appVersion);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showNextNode(button.getNextNodeId());
                        }
                    }, btn.getBounceTimeout());
                    break;
                }
            }
        } else {
            mSectionList.add(section);
            mAdapter.notifyItemInserted(mSectionList.size() - 1);
            rvChatData.scrollToPosition(mSectionList.size() - 1);
        }

    }

    @Override
    public void onItemClick(int position) {
        onItemClick(mButtonList.get(position));
    }

    private void onItemClick(final Button button) {
        if (button == null)
            return;
        //TODO:sent_check ButtonType and do the action accordingly
        hideSoftKeyboard();
        mButtonList.clear();
        mButtonsAdapter.notifyDataSetChanged();
        rvButtonsContainer.setVisibility(View.INVISIBLE);
        cvChatInput.setVisibility(View.INVISIBLE);
        switch (button.getButtonType()) {
            case Constants.ButtonType.TYPE_NEXT_NODE:
                if (mCurrVarName != null && button.getVariableValue() != null && !button.getVariableValue().isEmpty()) {
                    String str = null;
                    if (button.getVariableValue() != null) {
                        str = button.getVariableValue();
                        Matcher m = Pattern.compile("\\[~(.*?)\\]").matcher(str);
                        //Log.d("DataMap", mDataMap.toString());
                        while (m.find()) {
                            if (mDataMap.get(m.group()) != null) {
                                str = str.replace(m.group(), mDataMap.get(m.group()));
                            }
                        }
                    }
                    mDataMap.put("[~" + mCurrVarName + "]", str);
                    ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                            mCurrNodeId, button.getId(), button.getButtonText(), mCurrVarName.replace("[~", "").replace("]", ""),
                            button.getVariableValue(), button.getButtonType(), appVersion);
                } else {
                    ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                            mCurrNodeId, button.getId(), button.getButtonText(), null,
                            null, button.getButtonType(), appVersion);
                }
                showNextNode(button.getNextNodeId());
                break;
            case Constants.ButtonType.TYPE_GET_ADDR:
                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, button.getId(), button.getButtonText(), null,
                        null, button.getButtonType(), appVersion);
                getUserAddress(button);
                break;
            case Constants.ButtonType.TYPE_SHOW_CONFIRMATION:
                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, button.getId(), button.getButtonText(), null,
                        null, button.getButtonType(), appVersion);
//                createmySite(button);
                mNextNodeId = button.getNextNodeId();
                mCurrButton = button;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        replyToRia(Constants.SectionType.TYPE_SUBMIT_FORM, button.getButtonText());
                    }
                }, 1000);

                break;
            case Constants.ButtonType.TYPE_FETCH_CHAT:
                fetchNewChatData(button);
                break;
            case Constants.ButtonType.TYPE_GET_IMAGE:
                getImage(button);
                break;
            case Constants.ButtonType.TYPE_GET_AUDIO:
                getAudio(button);
                break;
            case Constants.ButtonType.TYPE_GET_VIDEO:
                getVideo(button);
                break;
            case Constants.ButtonType.TYPE_GET_ITEM_FROM_SOURCE:
                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, button.getId(), button.getButtonText(), null,
                        null, button.getButtonType(), appVersion);
                handleAutoComplete(button);
                break;
            case Constants.ButtonType.TYPE_DEEP_LINK:
                handleDeepLink(button);
                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, button.getId(), button.getButtonText(), null,
                        null, button.getButtonType(), appVersion);
                break;
            default:
                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, button.getId(), button.getButtonText(), null,
                        null, button.getButtonType(), appVersion);
                break;

        }

        if (!button.isHidden() && button.getButtonType() != null && !button.getButtonType().equals(Constants.ButtonType.TYPE_GET_ITEM_FROM_SOURCE) && button.isPostToChat()) {
            replyToRia(Constants.SectionType.TYPE_TEXT, button.getButtonText());
        }

    }

    private void fetchNewChatData(final Button button) {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(button.getUrl())
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNextNode(button.getNextNodeId());
                    }
                });

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();

                    List<RiaCardModel> newNodes = gson.fromJson(response.body().string(), new TypeToken<List<RiaCardModel>>() {
                    }.getType());
                    mAllNodes.addAll(newNodes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNextNode(button.getNextNodeId());
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNextNode(button.getNextNodeId());
                        }
                    });
                }
            }
        });
    }


    private void getUserAddress(final Button btn) {
        pickAddressFragment = PickAddressFragment.newInstance(PickAddressFragment.PICK_TYPE.MANUAL, (HashMap<String, String>) mDataMap);

        pickAddressFragment.setResultListener(new PickAddressFragment.OnResultReceive() {
            @Override
            public void OnResult(String address, String area, String city, String state, String country, double lat, double lon, String pin, String housePlotNum, String landmark) {
                //TODO: saveREsult
                //replyToRia(address + "\nCity: " + city + "\nCountry: " + country + "\nPin: " + pin, Constants.SectionType.TYPE_TEXT);

                String landmarkMap = TextUtils.isEmpty(landmark) ? "" : ", " + landmark;
                String locality = TextUtils.isEmpty(area) ? "" : ", " + area;

                mDataMap.put("[~" + "CITY" + "]", city);
                mDataMap.put("[~" + "COUNTRY" + "]", country);
                mDataMap.put("[~" + "PICK_HOUSEPLOTNO" + "]", housePlotNum);
                mDataMap.put("[~" + "PICK_AREA" + "]", area);
                mDataMap.put("[~" + "PICK_ADDRESS" + "]", address);
                mDataMap.put("[~" + "PICK_LANDMARK" + "]", landmark);
                mDataMap.put("[~" + "STREET_ADDRESS" + "]",
                        housePlotNum + locality + ", " + address + landmarkMap);
                mDataMap.put("[~" + "PINCODE" + "]", pin);
                mDataMap.put("[~" + "LAT" + "]", lat + "");
                mDataMap.put("[~" + "LNG" + "]", lon + "");
                mDataMap.put("[~" + "ADDRESSMAP_IMAGE" + "]", Utils.getMapUrlFromLocation(lat + "", lon + ""));

                HashMap<String, String> userData = new HashMap<String, String>();
                userData.put("CITY", city);
                userData.put("COUNTRY", country);
                userData.put("PICK_HOUSEPLOTNO", housePlotNum);
                userData.put("PICK_AREA", area);
                userData.put("PICK_ADDRESS", address);
                userData.put("PICK_LANDMARK", landmark);
                userData.put("STREET_ADDRESS",
                        housePlotNum + locality + ", " + address + landmarkMap);
                userData.put("PINCODE", pin);
                userData.put("LAT", lat + "");
                userData.put("LNG", lon + "");
                userData.put("ADDRESSMAP_IMAGE", Utils.getMapUrlFromLocation(lat + "", lon + ""));


                pickAddressFragment.setResultListener(null);
                pickAddressFragment.dismiss();
                pickAddressFragment = null;
                mNextNodeId = btn.getNextNodeId();
                mCurrButton = btn;

                ChatLogger.getInstance().logPostEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, btn.getId(), btn.getButtonText(), ChatLogger.EventStatus.COMPLETED.getValue(),
                        btn.getButtonType(), userData, appVersion);

                showNextNode(mCurrButton.getNextNodeId());
                //showConfirmation(Constants.ConfirmationType.ADDRESS_ENTRY, housePlotNum + ", " + address + ", " + city + ", " + country + ", " + pin + ", " + landmark, lat + "", lon + "");
            }
        });
        pickAddressFragment.show(getFragmentManager(), "Test");
    }


    public void showImageDilaog(String url) {
        final Dialog dialog = new Dialog(ChatViewActivity.this);
        dialog.setContentView(R.layout.image_dialog_layout);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = displayMetrics.widthPixels;
        params.height = displayMetrics.heightPixels;

        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final ImageViewTouch ivContent = (ImageViewTouch) dialog.findViewById(R.id.ivContent);

        if (!url.contains("http")) {

            Glide.with(ChatViewActivity.this)
                    .load(getParsedPrefixPostfixText(url))
                    .fitCenter()
                    .placeholder(R.drawable.site_sc_default)
                    .into(ivContent);
        } else {

            Picasso.with(ChatViewActivity.this).load(url)
                    .placeholder(R.drawable.site_sc_default)
                    .into(ivContent);
        }


        LinearLayout llClose = (LinearLayout) dialog.findViewById(R.id.llClose);
        llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private CameraImagePicker imagePicker;
    private static final int RC_CAMERA = 3000;

    private void getImage(Button btn) {
        mNextNodeId = btn.getNextNodeId();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.media_picker_select_from))
                .setPositiveButton(getString(R.string.media_picker_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final Activity activity = ChatViewActivity.this;
                        final String[] permissions = new String[]{Manifest.permission.CAMERA};
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, permissions, RC_CAMERA);
                        } else {
                            captureImage();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.media_picker_gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new ImagePicker.Builder(ChatViewActivity.this)
                                .mode(ImagePicker.Mode.GALLERY)
                                .directory(ImagePicker.Directory.DEFAULT)
                                .allowMultipleImages(false)
                                .enableDebuggingMode(true)
                                .build();

                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void captureImage() {
        imagePicker = new CameraImagePicker(ChatViewActivity.this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {
                                                   Intent i = new Intent(ChatViewActivity.this, FileUploadService.class);
                                                   i.putExtra(Constants.FILE_PATH, images.get(0).getOriginalPath());
                                                   i.putExtra(Constants.RECEIVER, mReceiver);
                                                   startService(i);
                                                   replyToRia(Constants.SectionType.TYPE_IMAGE, images.get(0).getOriginalPath());
                                               }

                                               @Override
                                               public void onError(String message) {
                                                   // Do error handling
                                               }
                                           }
        );
        String outputPath = imagePicker.pickImage();
    }

    private void getAudio(Button btn) {
        mNextNodeId = btn.getNextNodeId();
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, AUDIO_REQUEST_CODE);
    }

    private void getVideo(Button btn) {
        mNextNodeId = btn.getNextNodeId();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.media_picker_select_from))
                .setPositiveButton(getString(R.string.media_picker_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new VideoPicker.Builder(ChatViewActivity.this)
                                .mode(VideoPicker.Mode.CAMERA)
                                .directory(VideoPicker.Directory.DEFAULT)
                                .enableDebuggingMode(true)
                                .build();
                    }
                })
                .setNegativeButton(getString(R.string.media_picker_gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new VideoPicker.Builder(ChatViewActivity.this)
                                .mode(VideoPicker.Mode.GALLERY)
                                .directory(VideoPicker.Directory.DEFAULT)
                                .enableDebuggingMode(true)
                                .build();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void handleDeepLink(Button btn) {
        switch (btn.getDeepLinkUrl()) {
            case Constants.DeepLinkUrl.ASK_LOC_PERM:
                mCurrentDeepLink = btn.getDeepLinkUrl();
                askLocationPermission();
                mNextNodeId = btn.getNextNodeId();
                break;
            case Constants.DeepLinkUrl.LOGIN:
                login();
                break;
        }

    }

    private void skip() {

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);

        if (mCurrButton != null) {

            HashMap<String, String> userData = new HashMap<String, String>();
            userData.put("BUSINESS_NAME", mDataMap.get("[~BUSINESS_NAME]") + "");
            userData.put("COUNTRY_CODE", mDataMap.get("[~COUNTRYCODE]") + "");
            userData.put("PHONE", mDataMap.get("[~PHONE]") + "");
            userData.put("BUSINESS_CATEGORY", mDataMap.get("[~BUSINESS_CATEGORY]") + "");
            userData.put("ADDRESS", mDataMap.get("[~STREET_ADDRESS]") + ", " + mDataMap.get("[~CITY]") + ", " + mDataMap.get("[~COUNTRY]") + "");
            userData.put("EMAIL", mDataMap.get("[~EMAIL]") + "");
            userData.put("TAG", mDataMap.get("[~TAG]") + "");

            ChatLogger.getInstance().logPostEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                    mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), ChatLogger.EventStatus.DROPPED.getValue(),
                    mCurrButton.getButtonType(), userData, appVersion);
        }


        Bundle mBundle = new Bundle();

        mBundle.putString(Save_Name, mDataMap.get("[~BUSINESS_NAME]"));
        mBundle.putString(Save_Cat, mDataMap.get("[~BUSINESS_CATEGORY]"));
        mBundle.putString(Save_City, mDataMap.get("[~CITY]"));
        mBundle.putString(Save_Country, mDataMap.get("[~COUNTRY]"));
        mBundle.putString(Save_Email, mDataMap.get("[~EMAIL]"));

        if (mDataMap.containsKey("[~PHONE]")) {
            mBundle.putString(Save_Phone, mDataMap.get("[~PHONE]"));
            mBundle.putString(Save_Phone_code, mDataMap.get("[~COUNTRYCODE]"));
        }

        mBundle.putString(Save_Website_Address, mDataMap.get("[~TAG]"));
        mBundle.putString(Save_Pin_Code, mDataMap.get("[~PINCODE]"));
        mBundle.putString(Save_Street_Address, mDataMap.get("[~STREET_ADDRESS]"));
        mBundle.putString(Save_Locality, mDataMap.get("[~PICK_LANDMARK]"));
        mBundle.putString(Save_Otp, mDataMap.get("[~IsOTPValid]"));
        mBundle.putString(Save_IS_FP_AVAILABLE, mDataMap.get("[~IsFPTagAvailable]"));
        mBundle.putString(Save_Lat, mDataMap.get("[~LAT]"));
        mBundle.putString(Save_Lng, mDataMap.get("[~LNG]"));

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("nowfloats://com.riasdk.skip/riachat"));
        intent.putExtra("mBundle", mBundle);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setAction(Intent.ACTION_VIEW);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }


    private void login() {

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("com.biz2.nowfloats://com.riasdk.skip/riachat"));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setAction(Intent.ACTION_VIEW);

        String userKey = "USERNAME";
        String passwordKey = "PASSWORD";

        intent.putExtra("Username", mDataMap.get("[~" + userKey + "]").toLowerCase());
        intent.putExtra("Password", mDataMap.get("[~" + passwordKey + "]"));
        intent.putExtra("fromLogin", true);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        finish();
    }

    private void askLocationPermission() {
        new TedPermission(this)
                .setPermissionListener(mPermissionListener)
                .setDeniedMessage("If you reject permission, you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private RiaCardModel getNextNode(String currNode) {
        RiaCardModel nextNode = null;
        for (RiaCardModel node : mAllNodes) {
            if (node.getId().equals(currNode)) {
                nextNode = node;
                break;
            }
        }
        return nextNode;
    }

    private void showNextNode(String nodeId) {
        startChat(getNextNode(nodeId));
    }

    private void startChat(final RiaCardModel node) {
        if (node == null)
            return;
        mDefaultButton = null;
        tvPrefix.setText("");
        tvPostfix.setText("");
        mCurrNodeId = node.getId();
        etChatInput.setHint("");
        ChatLogger.getInstance().logViewEvent(DeviceDetails.getDeviceId(this), mCurrNodeId, appVersion);
        mCurrVarName = null;
        mHandler = new Handler(Looper.getMainLooper());
        if (node.getVariableName() != null) {
            mCurrVarName = node.getVariableName();
        }


        if (node.getNodeType() != null && node.getNodeType().equals(Constants.NodeType.TYPE_CARD) && node.getPlacement().equals("Center")) {

            if (node.getSections().size() == 2 && node.getSections().get(0).getSectionType().equals(Constants.SectionType.TYPE_IMAGE)) {
                if (mSectionList.get(mSectionList.size() - 1).getCardModel() != null &&
                        mSectionList.get(mSectionList.size() - 1).getCardModel().getPlacement().equals("Center")) {
                    replyToRia(Constants.SectionType.TYPE_UNCONFIRMED_ADDR_CARD, node, true);
                } else {
                    replyToRia(Constants.SectionType.TYPE_UNCONFIRMED_ADDR_CARD, node, false);
                }
            } else if (node.getSections().size() == 1 && node.getSections().get(0).getSectionType().equals(Constants.SectionType.TYPE_TEXT)) {
                if (mSectionList.get(mSectionList.size() - 1).getCardModel() != null &&
                        mSectionList.get(mSectionList.size() - 1).getCardModel().getPlacement().equals("Center")) {
                    replyToRia(Constants.SectionType.TYPE_UNCONFIRMED_CARD, node, true);
                } else {
                    replyToRia(Constants.SectionType.TYPE_UNCONFIRMED_CARD, node, false);
                }
            } else if (node.getSections().size() == 1 && node.getSections().get(0).getSectionType().equals(Constants.SectionType.TYPE_PRINT_OTP)) {
                if (mSectionList.get(mSectionList.size() - 1).getCardModel() != null &&
                        mSectionList.get(mSectionList.size() - 1).getCardModel().getPlacement().equals("Center")) {
                    replyToRia(Constants.SectionType.TYPE_PRINT_OTP, node, true);
                } else {
                    replyToRia(Constants.SectionType.TYPE_PRINT_OTP, node, false);
                }
                showKeyBoard();
            }
            return;
        }
        if (node.getNodeType() != null && node.getNodeType().equals(Constants.NodeType.TYPE_CARD) && node.getPlacement().equals("Outgoing")) {
            if (node.getSections().size() == 2 && node.getSections().get(0).getSectionType().equals(Constants.SectionType.TYPE_IMAGE)) {
                replyToRia(Constants.SectionType.TYPE_ADDRESS_CARD, node, true);
            } else if (node.getSections().size() == 1 && node.getSections().get(0).getSectionType().equals(Constants.SectionType.TYPE_TEXT)) {
                replyToRia(Constants.SectionType.TYPE_CARD, node, true);
            }
            return;
        }


        if (node.getNodeType() != null && node.getNodeType().equals(Constants.NodeType.TYPE_API_CALL)
                && node.getApiMethod() != null) {
            switch (node.getApiMethod()) {
                case Constants.ApiType.TYPE_GET:
                    handleGetRequest(node);
                    break;
                case Constants.ApiType.TYPE_POST:
                    handlePostRequest(node);
                    break;
            }
            return;
        }
        final List<Section> sectionList = node.getSections();
        int time = 0;
        final Section typingSection = new Section();
        typingSection.setSectionType(Constants.SectionType.TYPE_TYPING);

        if (!TextUtils.isEmpty(node.getVariableName())
                && node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_ADD_DELAY)) {

            time += 1000;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSectionList.add(typingSection);
                    tvRiaTyping.setVisibility(View.VISIBLE);
                    mAdapter.notifyItemInserted(mSectionList.size() - 1);
                    rvChatData.scrollToPosition(mSectionList.size() - 1);
                }
            }, time);
        } else {

            for (final Section section : sectionList) {
                time += 1000;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSectionList.add(typingSection);
                        tvRiaTyping.setVisibility(View.VISIBLE);
                        mAdapter.notifyItemInserted(mSectionList.size() - 1);
                        rvChatData.scrollToPosition(mSectionList.size() - 1);
                    }
                }, time);

                if (section.getDelayInMs() < getResources().getInteger(android.R.integer.config_longAnimTime)) {
                    Log.e("section", section.getText() + 500);
                    section.setDelayInMs(getResources().getInteger(android.R.integer.config_longAnimTime));
                } else {
                    Log.e("section", section.getText() + section.getDelayInMs());
                }

                time += section.getDelayInMs();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String str = null;
                        if (section.getText() != null) {
                            str = section.getText();
                            str = getParsedPrefixPostfixText(str);
                        }
                        if (null != str) {
                            section.setText(str);
                        }
                        section.setFromRia(true);
                        section.setDateTime(Utils.getFormattedDate(new Date()));
                        mSectionList.set(mSectionList.size() - 1, section);
                        tvRiaTyping.setVisibility(View.INVISIBLE);
                        mAdapter.notifyItemChanged(mSectionList.size() - 1);
                        rvChatData.scrollToPosition(mSectionList.size() - 1);
                    }
                }, time);
            }
        }

        if (time > 0)
            time += 1200;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                etChatInput.setAdapter(null);

                boolean isTextInput = true;

                for (Button btn : node.getButtons()) {
                    if (btn.isDefaultButton()) {
                        mDefaultButton = btn;
                    }
                    if (btn.getButtonType().equals(Constants.ButtonType.TYPE_GET_TEXT) && !btn.isHidden()) {
                        if (btn.getPrefixText() != null) {
                            tvPrefix.setText(getParsedPrefixPostfixText(btn.getPrefixText().trim()));
                        }
                        if (btn.getPostfixText() != null) {
                            tvPostfix.setText(getParsedPrefixPostfixText(btn.getPostfixText().trim()));
                        }
                        cvChatInput.setVisibility(View.INVISIBLE);
                        etChatInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        etChatInput.setHint(btn.getPlaceholderText());
                        mNextNodeId = btn.getNextNodeId();
                        mCurrButton = btn;
                    } else if (btn.getButtonType().equals(Constants.ButtonType.TYPE_GET_NUMBER) && !btn.isHidden()) {
                        if (btn.getPrefixText() != null) {
                            tvPrefix.setText(getParsedPrefixPostfixText(btn.getPrefixText().trim()));
                        }
                        if (btn.getPostfixText() != null) {
                            tvPostfix.setText(getParsedPrefixPostfixText(btn.getPostfixText().trim()));
                        }
                        cvChatInput.setVisibility(View.INVISIBLE);
                        etChatInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etChatInput.setHint(btn.getPlaceholderText());
                        mNextNodeId = btn.getNextNodeId();
                        mCurrButton = btn;

                    } else if (btn.getButtonType().equals(Constants.ButtonType.TYPE_GET_EMAIL) && !btn.isHidden()) {
                        if (btn.getPrefixText() != null) {
                            tvPrefix.setText(getParsedPrefixPostfixText(btn.getPrefixText().trim()));
                        }
                        if (btn.getPostfixText() != null) {
                            tvPostfix.setText(getParsedPrefixPostfixText(btn.getPostfixText().trim()));
                        }
                        cvChatInput.setVisibility(View.INVISIBLE);
                        etChatInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        etChatInput.setHint(btn.getPlaceholderText());
                        mNextNodeId = btn.getNextNodeId();
                        mCurrButton = btn;
                    } else if (btn.getButtonType().equals(Constants.ButtonType.TYPE_GET_PHONE_NUMBER) && !btn.isHidden()) {
                        if (btn.getPrefixText() != null) {
                            tvPrefix.setText(getParsedPrefixPostfixText(btn.getPrefixText().trim()));
                        }
                        if (btn.getPostfixText() != null) {
                            tvPostfix.setText(getParsedPrefixPostfixText(btn.getPostfixText().trim()));
                        }
                        cvChatInput.setVisibility(View.INVISIBLE);
                        etChatInput.setInputType(InputType.TYPE_CLASS_PHONE);
                        etChatInput.setHint(btn.getPlaceholderText());
                        mNextNodeId = btn.getNextNodeId();
                        mCurrButton = btn;
                    } /*else if (btn.getButtonType().equals(Constants.ButtonType.TYPE_GET_ITEM_FROM_SOURCE) && !btn.isHidden()) {
                        if (btn.getPrefixText() != null) {
                            tvPrefix.setText(getParsedPrefixPostfixText(btn.getPrefixText().trim()));
                        }
                        if (btn.getPostfixText() != null) {
                            tvPostfix.setText(getParsedPrefixPostfixText(btn.getPostfixText().trim()));
                        }
                        handleAutoComplete(btn);
                        mCurrButton = btn;
                    }*/ else {
                        isTextInput = false;
                        if (!btn.isHidden()) {
                            String str = null;
                            if (btn.getButtonText() != null) {
                                str = btn.getButtonText();
                                Matcher m = Pattern.compile("\\[~(.*?)\\]").matcher(str);
                                while (m.find()) {
                                    if (mDataMap.get(m.group()) != null) {
                                        str = str.replace(m.group(), mDataMap.get(m.group()));
                                    }
                                }
                            }
                            if (str != null) {
                                btn.setButtonText(str);
                            }
                            mButtonList.add(btn);
                        }
                        node.setToShowInput(false);
                    }
                }

//                if (mCurrButton != null && !TextUtils.isEmpty(mCurrButton.getPrefixText())
//                        && mCurrButton.getPrefixText().equalsIgnoreCase(PREFIX_COUNTRY_CODE)) {
//                    tvPrefix.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_ALTERNATE_PHONE))
//                                showCustomDialog(CustomDialogFragment.DialogFrom.COUNTRY_CODE);
////                            else
////                                showCustomDialog(CustomDialogFragment.DialogFrom.COUNTRY_CODE);
//                        }
//                    });
//                } else {
//                    tvPrefix.setOnClickListener(null);
//                }

                mButtonsAdapter.notifyDataSetChanged();
                rvButtonsContainer.setVisibility(View.INVISIBLE);

                mSectionList.get(mSectionList.size() - 1).setShowDate(true);
                mAdapter.notifyItemChanged(mSectionList.size() - 1);
                rvChatData.scrollToPosition(mSectionList.size() - 1);

                if (mDefaultButton != null && node.getTimeoutInMs() != -1L) {

//                    ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
//                            mCurrNodeId, mDefaultButton.getId(), mDefaultButton.getButtonText(), null,
//                            null, mDefaultButton.getButtonType());

                    tvRiaTyping.setVisibility(View.INVISIBLE);
                    if (!isTextInput)
                        mHandler.postDelayed(mAutoCallRunnable, node.getTimeoutInMs());
                }

            }
        }, time);

        if (time > 0)
            time += 800;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mButtonList.size() > 0) {
                    if (rvButtonsContainer.getVisibility() == INVISIBLE) {
                        rvButtonsContainer.setVisibility(View.VISIBLE);
                    }
                } else if (!node.isToShowInput()) {

                } else {
                    if (cvChatInput.getVisibility() == INVISIBLE) {

                        /*
                         * setting input types to edit text based on
                         * variable names
                         */

                        if (!TextUtils.isEmpty(node.getVariableName())) {
                            if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_TAG)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_EXISTING_WEBSITE)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_NEW_PASSWORD)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_NEW_PASSWORD_REPEAT)) {
                                etChatInput.setInputType(InputType.TYPE_CLASS_TEXT);

                                /*
                                 * hardcoded for password repeat
                                 */
                                if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_NEW_PASSWORD_REPEAT))
                                    etChatInput.setText("");

                            } else if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_MESSAGE_UPDATE)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_BUSINESS_DESCRIPTION)) {
                                etChatInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                            }

                             /*
                         * setting input filters to edit text based on
                         * variable name
                         */

                            if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_TAG)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_EXISTING_WEBSITE)) {
                                etChatInput.setFilters(new InputFilter[]
                                        {
                                                new InputFilter() {
                                                    public CharSequence filter(CharSequence source, int start, int end, Spanned dst, int dstart, int dend) {
                                                        StringBuilder builder = new StringBuilder();
                                                        for (int i = start; i < end; i++) {
                                                            char currentChar = source.charAt(i);
                                                            if (Character.isLetterOrDigit(currentChar) || currentChar == '-' || currentChar == '.') {
                                                                builder.append(currentChar);
                                                            }
                                                        }
                                                        return builder.toString();
                                                    }
                                                }
                                        });
                            } else if (node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_NEW_PASSWORD)
                                    || node.getVariableName().equalsIgnoreCase(Constants.VariableName.TYPE_NEW_PASSWORD_REPEAT)) {
                                etChatInput.setFilters(new InputFilter[]
                                        {
                                                new InputFilter() {
                                                    public CharSequence filter(CharSequence source, int start, int end, Spanned dst, int dstart, int dend) {
                                                        StringBuilder builder = new StringBuilder();
                                                        for (int i = start; i < end; i++) {
                                                            char currentChar = source.charAt(i);
                                                            if (!Character.isWhitespace(currentChar)) {
                                                                builder.append(currentChar);
                                                            }
                                                        }
                                                        return builder.toString();
                                                    }
                                                }
                                        });
                            } else {
                                etChatInput.setFilters(new InputFilter[]{});
                            }

                        } else {
                            etChatInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            etChatInput.setFilters(new InputFilter[]{});
                        }

                        if (TextUtils.isEmpty(tvPostfix.getText().toString())) {
                            tvPostfix.setVisibility(View.GONE);
                        } else {
                            tvPostfix.setVisibility(View.VISIBLE);
                        }


                        cvChatInput.setVisibility(View.VISIBLE);
                    }
                }

            }
        }, time);

        time += 300;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cvChatInput.getVisibility() == View.VISIBLE) {
                    etChatInput.requestFocus();
                    showKeyBoard();
                }

                rvChatData.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v,
                                               int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (bottom < oldBottom) {
                            rvChatData.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rvChatData.smoothScrollToPosition(
                                            rvChatData.getAdapter().getItemCount() - 1);
                                }
                            }, 50);
                        }
                    }
                });

            }
        }, time);

    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private String getParsedPrefixPostfixText(String text) {
        if (text == null)
            return null;
        Matcher m = Pattern.compile("\\[~(.*?)\\]").matcher(text);
        while (m.find()) {
            if (mDataMap.get(m.group()) != null) {
                text = text.replace(m.group(), mDataMap.get(m.group()));
            }
        }
        return text;
    }

    private void handleAutoComplete(final Button btn) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, btn.getUrl(), null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        Iterator<?> keys = response.keys();
                        List<String> mAutoComplRes = new ArrayList<>();
                        mAutoComplDataHash = new HashMap<>();
                        while (keys.hasNext()) {
                            try {
                                String key = (String) keys.next();
                                mAutoComplRes.add(response.getString(key));
                                mAutoComplDataHash.put(response.getString(key), key);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        showListDialog(btn, mAutoComplRes);

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: shopw unable to process
                progressBar.setVisibility(View.GONE);
                showCustomDialog(CustomDialogFragment.DialogFrom.SKIP);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(40000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(request);
        mNextNodeId = btn.getNextNodeId();


    }

    private void showListDialog(final Button btn, List<String> mAutoComplRes) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatViewActivity.this,
                R.layout.search_list_item_layout, mAutoComplRes);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChatViewActivity.this);
        builderSingle.setTitle(btn.getPlaceholderText());

        View view = LayoutInflater.from(ChatViewActivity.this).inflate(R.layout.search_list_layout, null);
        builderSingle.setView(view);

        EditText edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        ListView lvItems = (ListView) view.findViewById(R.id.lvItems);

        lvItems.setAdapter(adapter);


        final Dialog dialog = builderSingle.show();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strVal = adapter.getItem(position);
                dialog.dismiss();
                //replyToRia(Constants.SectionType.TYPE_TEXT, strVal);
                if (mCurrVarName != null) {
                    mDataMap.put("[~" + mCurrVarName + "]", strVal);

                    HashMap<String, String> userData = new HashMap<String, String>();
                    userData.put(mCurrVarName, strVal);

                    ChatLogger.getInstance().logPostEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                            mCurrNodeId, btn.getId(), btn.getButtonText(), ChatLogger.EventStatus.COMPLETED.getValue(),
                            btn.getButtonType(), userData, appVersion);
                }

                showNextNode(btn.getNextNodeId());
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString().toLowerCase());
            }
        });

        dialog.setCancelable(false);
    }

    private void showTyping() {
        final Section typingSection = new Section();
        typingSection.setSectionType(Constants.SectionType.TYPE_TYPING);
        mSectionList.add(typingSection);
        mAdapter.notifyItemInserted(mSectionList.size() - 1);
        Log.d("Chat View Activity", "This is Executing");
    }

    private void removeTyping() {
        mSectionList.remove(mSectionList.size() - 1);
        mAdapter.notifyItemRemoved(mSectionList.size());
    }


    private void fetchChatJson() {
        /*if(null!=pg && !pg.isShowing())
            pg.show();*/
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        RestAdapter adapter = new RestAdapter.Builder().setClient(new Ok3Client(client)).setEndpoint(Constants.SERVER_URL).build();
        ChatJsonInterface chatJsonInterface = adapter.create(ChatJsonInterface.class);
        Map<String, String> query = new HashMap<>();
        query.put("deviceId", DeviceDetails.getDeviceId(this));
        query.put("libVersion", DeviceDetails.getLibVersionName());
        query.put("osVersion", DeviceDetails.getAndroidVersion());
        query.put("osTimeZone", DeviceDetails.getTimeZone());
        query.put("osCountry", DeviceDetails.getCountry());
        query.put("osLanguage", DeviceDetails.getLanguage());
        query.put("deviceBrand", DeviceDetails.getBrand());
        query.put("deviceModel", DeviceDetails.getDeviceModel());
        query.put("screenWidth", DeviceDetails.getScreenWidth(this) + "");
        query.put("screenHeight", DeviceDetails.getScreenHeight(this) + "");
        chatJsonInterface.getChatJson(query, new Callback<List<RiaCardModel>>() {
            @Override
            public void success(List<RiaCardModel> riaCardModels, Response response) {
                //pg.dismiss();
                if (riaCardModels != null && riaCardModels.size() > 0) {
                    initChat(riaCardModels);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //pg.dismiss();
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);

                if (isNetworkStatusAvialable(ChatViewActivity.this)) {
                    showCustomDialog(CustomDialogFragment.DialogFrom.SKIP);
                } else {
                    showCustomDialog(CustomDialogFragment.DialogFrom.NO_INTERNET);
                }
                //(RiaOnBoardingActivity.this, getString(R.string.something_went_wrong));
            }
        });
//
//        List<RiaCardModel> posts = new ArrayList<RiaCardModel>();
//        Gson mGson = new Gson();
//        posts = Arrays.asList(mGson.fromJson(loadJSONFromAsset(), RiaCardModel[].class));
//        initChat(posts);
//        progressBar.setVisibility(View.GONE);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("json.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void initChat(List<RiaCardModel> riaCardModels) {
        mAllNodes = riaCardModels;
        //TODO:Run on Another thread
        mAdapter = new RvChatAdapter(mSectionList, mDataMap, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(false);

        rvChatData.setItemAnimator(new ChatItemAnimator(ChatViewActivity.this));
        rvChatData.setLayoutManager(layoutManager);
        rvChatData.setAdapter(mAdapter);

        mButtonsAdapter = new RvButtonsAdapter(mButtonList);
        mButtonsAdapter.setOnCItemClickListener(this);
        LinearLayoutManager buttonsLayoutManager = new LinearLayoutManager(ChatViewActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        rvButtonsContainer.setLayoutManager(buttonsLayoutManager);
        rvButtonsContainer.setAdapter(mButtonsAdapter);

        startChat(mAllNodes.get(0));
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void handleGetRequest(final RiaCardModel node) {

        progressBar.setVisibility(View.VISIBLE);

        StringBuilder urlBuilder = new StringBuilder(node.getApiUrl());
        urlBuilder.append("?");

        for (String key : node.getRequiredVariables()) {
            try {
                urlBuilder.append(key + "=" + URLEncoder.encode(mDataMap.get("[~" + key + "]"), "UTF-8") + "&");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        urlBuilder.append("deviceId=" + DeviceDetails.getDeviceId(this));
        Log.e("urlBuilder", urlBuilder.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlBuilder.toString(), null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ChatView", response.toString());
                        Iterator<?> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();

                            if (key.equalsIgnoreCase(KEY_FP_CREATION_STATUSCODE)) {
                                FP_STATUS_CODE = response.optInt(KEY_FP_CREATION_STATUSCODE);
                            } else if (!key.equals(KEY_NEXT_NODE_ID)) {
                                try {
                                    mDataMap.put("[~" + key + "]", response.getString(key));
                                    //Log.d("Hello", mDataMap.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //TODO:Add unable to process in the sectionList
                                }
                            }
                        }
                        if (node.getButtons() != null && node.getButtons().size() > 0) {
                            DocumentContext context = JsonPath.parse(response.toString());
                            String nextNodeId = null;
                            for (Button btn : node.getButtons()) {
                                if (TextUtils.isEmpty(btn.getApiResponseMatchKey()))
                                    continue;
                                String keyPath = "$." + btn.getApiResponseMatchKey();
                                String val = context.read(keyPath, String.class);
                                if (val != null && val.equalsIgnoreCase(btn.getApiResponseMatchValue())) {
                                    nextNodeId = btn.getNextNodeId();
                                    break;
                                }
                            }
                            if (nextNodeId != null) {
                                showNextNode(nextNodeId);
                            }
                        } else {
                            try {
                                if (TextUtils.isEmpty(response.optString(KEY_NEXT_NODE_ID))) {
                                    showNextNode(node.getNextNodeId());
                                } else {
                                    showNextNode(response.getString(KEY_NEXT_NODE_ID));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                //TODO:Add unable to process in the sectionList
                            }
                        }

                        if (FP_STATUS_CODE == FP_CREATED) {
                            tvSkip.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.GONE);

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Hello", "Error");
                progressBar.setVisibility(View.GONE);
                showCustomDialog(CustomDialogFragment.DialogFrom.SKIP);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void handlePostRequest(final RiaCardModel node) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, node.getApiUrl() +
                "?deviceId=" + DeviceDetails.getDeviceId(this),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            Log.d("ChatView", response.toString());
                            Iterator<?> keys = response.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (!key.equals(KEY_NEXT_NODE_ID)) {
                                    try {
                                        mDataMap.put("[~" + key + "]", response.getString(key));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //TODO:Add unable to process in the sectionList
                                    }
                                }

                            }
                            if (node.getButtons() != null && node.getButtons().size() > 0) {
                                DocumentContext context = JsonPath.parse(response);
                                String nextNodeId = null;
                                for (Button btn : node.getButtons()) {
                                    if (TextUtils.isEmpty(btn.getApiResponseMatchKey()))

                                        continue;
                                    String keyPath = "$." + btn.getApiResponseMatchKey();
                                    String val = context.read(keyPath, String.class);
                                    if (val != null && val.equals(btn.getApiResponseMatchValue())) {
                                        nextNodeId = btn.getNextNodeId();
                                        break;
                                    }
                                }
                                if (nextNodeId != null) {
                                    showNextNode(nextNodeId);
                                }
                            } else {
                                try {
                                    if (TextUtils.isEmpty(response.optString(KEY_NEXT_NODE_ID))) {
                                        showNextNode(node.getNextNodeId());
                                    } else {
                                        showNextNode(response.getString(KEY_NEXT_NODE_ID));
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //TODO:Add unable to process in the sectionList
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showCustomDialog(CustomDialogFragment.DialogFrom.SKIP);
                progressBar.setVisibility(View.GONE);
                //TODO:Add unable to process in the sectionList
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                for (String key : node.getRequiredVariables()) {
                    param.put(key, mDataMap.get("[~" + key + "]"));
                }
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onCardResponse(String confirmationType, Button mButton, String... data) {

        if (mButton != null) {

            if (confirmationType.equalsIgnoreCase(Constants.ConfirmationType.OTP)) {

                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, mButton.getId(), mButton.getButtonText(), mCurrVarName,
                        data[0], mButton.getButtonType(), appVersion);
            } else {

                ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                        mCurrNodeId, mButton.getId(), mButton.getButtonText(), null,
                        null, mButton.getButtonType(), appVersion);
            }
        }

        if (TextUtils.isEmpty(data[1])) {
            switch (confirmationType) {
                case Constants.ConfirmationType.SUBMIT_FORM:

                    HashMap<String, String> userData = new HashMap<String, String>();
                    userData.put("BUSINESS_NAME", mDataMap.get("[~BUSINESS_NAME]") + "");
                    userData.put("COUNTRY_CODE", mDataMap.get("[~COUNTRYCODE]") + "");
                    userData.put("PHONE", mDataMap.get("[~PHONE]") + "");
                    userData.put("BUSINESS_CATEGORY", mDataMap.get("[~BUSINESS_CATEGORY]") + "");
                    userData.put("ADDRESS", mDataMap.get("[~STREET_ADDRESS]") + ", " + mDataMap.get("[~CITY]") + ", " + mDataMap.get("[~COUNTRY]") + "");
                    userData.put("EMAIL", mDataMap.get("[~EMAIL]") + "");
                    userData.put("TAG", mDataMap.get("[~TAG]") + "");


                    ChatLogger.getInstance().logPostEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                            mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), ChatLogger.EventStatus.COMPLETED.getValue(),
                            mCurrButton.getButtonType(), userData, appVersion);

                    showNextNode(mNextNodeId);
                    break;
            }
        } else {
            RiaCardModel nextNode = getNextNode(data[1]);
            if (nextNode != null && (nextNode.getNodeType().equals(Constants.NodeType.TYPE_CARD) && nextNode.getPlacement().equals("Outgoing")) ||
                    nextNode.getNodeType().equals(Constants.NodeType.TYPE_API_CALL)) {
                if (mCurrVarName != null && mCurrVarName.trim().length() > 0) {
                /*if (mAutoComplDataHash == null || !TextUtils.isEmpty(mAutoComplDataHash.get(etChatInput.getText().toString().trim()))) {
                    mDataMap.put("[~" + mCurrVarName + "]", etChatInput.getText().toString().trim());
                } else {
                    mDataMap.put("[~" + mCurrVarName + "]", mAutoComplDataHash.get(etChatInput.getText().toString().trim()));
                }*/
                    mDataMap.put("[~" + mCurrVarName + "]", data[0]);
                }
                etChatInput.setText("");
                hideSoftKeyboard();
                cvChatInput.setVisibility(View.INVISIBLE);
//                switch (confirmationType) {
//                    case Constants.ConfirmationType.BIZ_NAME:
//                        showNextNode(data[1]);
//                        break;
//                    case Constants.ConfirmationType.ADDRESS_ENTRY:
//                        showNextNode(data[1]);
//                        break;
//                }
            } else {
                mSectionList.remove(mSectionList.size() - 1);
                mAdapter.notifyItemRemoved(mSectionList.size());
//                switch (confirmationType) {
//                    case Constants.ConfirmationType.BIZ_NAME:
//                        showNextNode(data[1]);
//                        break;
//                    case Constants.ConfirmationType.ADDRESS_ENTRY:
//                        showNextNode(data[1]);
//                        break;
//                }
            }

            switch (confirmationType) {
                case Constants.ConfirmationType.BIZ_NAME:
                case Constants.ConfirmationType.OTP:
                    showNextNode(data[1]);
                    break;
                case Constants.ConfirmationType.ADDRESS_ENTRY:
                    showNextNode(data[1]);
                    break;
            }
        }

    }

    @Override
    public void onAnimationend() {

    }


    private class FileUploadResultReceiver extends ResultReceiver {

        /**
         *
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public FileUploadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData != null && resultCode == Constants.RESULT_OK && mCurrVarName != null) {
                mDataMap.put("[~" + mCurrVarName + "]", resultData.getString(Constants.KEY_FILE_URL));
            }
            mSectionList.get(mSectionList.size() - 1).setLoading(false);
            mAdapter.notifyItemChanged(mSectionList.size() - 1);
            //TODO: Start Next Node;
            if (mNextNodeId != null) {
                showNextNode(mNextNodeId);
            }
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void showCustomDialog(
            final CustomDialogFragment.DialogFrom dialogFrom) {

        if ((dialogFrom == CustomDialogFragment.DialogFrom.SKIP ||
                dialogFrom == CustomDialogFragment.DialogFrom.CREATE_MY_SITE)
                && FP_STATUS_CODE != FP_NOT_CREATED) {

        } else {

            if (isDestroyed()) {
                return;
            }

            final CustomDialogFragment customDialogFragment = CustomDialogFragment.newInstance(dialogFrom);
            customDialogFragment.show(getFragmentManager(), "Test");
            customDialogFragment.setResultListener(new CustomDialogFragment.OnResultReceive() {
                @Override
                public void createmysite() {
                    customDialogFragment.dismiss();
                    skip();
                }

                @Override
                public void navigateToHome() {
                    customDialogFragment.dismiss();
                    login();
                }

                @Override
                public void navigateToSignup() {
                    customDialogFragment.dismiss();
                }

                @Override
                public void dismissPopup() {
                    customDialogFragment.dismiss();
                }

                @Override
                public void skipNode() {

                    customDialogFragment.dismiss();

                    if (mCurrButton != null) {

                        rvButtonsContainer.setVisibility(View.INVISIBLE);
                        cvChatInput.setVisibility(View.INVISIBLE);
                        if (mCurrVarName != null) {
                            mDataMap.put("[~" + mCurrVarName + "]", etChatInput.getText().toString().trim());
                            ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                    mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), mCurrVarName.replace("[~", "").replace("]", ""),
                                    etChatInput.getText().toString().trim(), mCurrButton.getButtonType(), appVersion);
                        } else {
                            ChatLogger.getInstance().logClickEvent(DeviceDetails.getDeviceId(ChatViewActivity.this),
                                    mCurrNodeId, mCurrButton.getId(), mCurrButton.getButtonText(), null,
                                    null, mCurrButton.getButtonType(), appVersion);
                        }
                        showNextNode(mCurrButton.getNextNodeId());

                        mAutoComplDataHash = null;
                        mButtonList.clear();
                        mButtonsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void finishActivity() {
                    customDialogFragment.dismiss();
                    goBack();
                }
            });
        }
    }
}