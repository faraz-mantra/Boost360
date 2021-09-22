package com.nowfloats.signup.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.signup.UI.API.API_Layer_Signup;
import com.nowfloats.signup.UI.API.Download_Facebook_Image;
import com.nowfloats.signup.UI.API.LoadCountryData;
import com.nowfloats.signup.UI.API.Signup_Descriptinon;
import com.nowfloats.signup.UI.API.Valid_Email;
import com.nowfloats.signup.UI.DomainAvailabilityCheck;
import com.nowfloats.signup.UI.Model.Create_Store_Event;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Model.Primary_Number_Event;
import com.nowfloats.signup.UI.Model.Suggest_Tag_Event;
import com.nowfloats.signup.UI.Service.Create_Tag_Service;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.signup.UI.Service.Suggest_Tag_Service;
import com.nowfloats.signup.UI.Validation.Signup_Validation;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.HeaderText;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.SmsVerifyModel;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_PROFILE_CREATION_FAILED;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_PROFILE_CREATION_INITIATED;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_BUSINESS_PROFILE_CREATION_SUCCESSFUL;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_PROFILE_CREATION_FAILED;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_PROFILE_CREATION_INITIATED;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_PROFILE_CREATION_SUCCESSFUL;
import static com.framework.webengageconstant.EventValueKt.NULL;

public class PreSignUpActivityRia extends AppCompatActivity implements
        PreSignUpDialog.Dialog_Activity_Interface,
        API_Layer_Signup.SignUp_Interface, View.OnClickListener,
        LoadCountryData.LoadCountryData_Interface,
        Valid_Email.Valid_Email_Interface
        , DomainAvailabilityCheck.CallBack {
    private static final String TAG = "PreSignUp";
    private static EditText businessNameEditText, emailEditText,
            phoneEditText, etStreetAddress, etOTP;
    private static TextView countryPhoneCode;
    public String[] cat = null;
    //    HashMap<String, String> Country_CodeMap, Code_PhoneMap;
//    private String Validate_Email_API_KEY = "e5f5fb5a-8e1f-422e-9d25-a67a16018d47";
    public Activity activity;
    public ProgressDialog pd;
    UserSessionManager sessionManager;
    ListView l;
    HeaderText title;
    ImageView ivPhoneStatus;
    Bus bus;
    String data_businessName, data_businessCategory, data_city, data_country, data_email, data_phone, data_country_code,
            data_lat = "0", data_lng = "0";
    EditText otpEditText;
    boolean allFieldsValid = true;
    //  private String[] businessCategoryList;
    //    private boolean goToNextScreen = false;
//    private String tagName;
    String fpTag;
    ArrayList<String> signUpCountryList = new ArrayList<>();
//    private double lng, lat;
//    private LatLng latlong;


//    public static final String Save_Name = "nameKey";
//    public static final String Save_Cat = "categoryKey";
//    public static final String Save_Street_Address = "streetAddressKey";
//    public static final String Save_Phone = "phoneNumberKey";
//    public static final String Save_Phone_Code = "phoneCodeKey";
//    public static final String Save_Email = "emailKey";
//    public static final String Save_Website_Address = "websiteAdressKey";
//    public static final String Save_Otp = "otpKey";
//    public static final String Save_Lat = "ria_latKey";
//    public static final String Save_Lng = "ria_lngKey";
//    public static final String Save_IS_FP_AVAILABLE = "isFPAvailable";

//    public static Email_Validation_Model emailModel;
//    String facebookPageURL;
    private MaterialDialog progressDialog, progressbar;
    private boolean businessCategory_Selected = false;
    //SharedPreferences sharedpreferences;
    private boolean country_Selected = false;
    private ArrayList<String> categories;
    private LinearLayout verify_button;
    private DataBase dataBase;
    private PopupWindow popup;
    private String existing_profile_id;
    private MaterialDialog otpDialog, numberDialog;
    private String contactName = "";
    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String otp = intent.getStringExtra("OTP_CODE");
            otp = otp.replaceAll("[^0-9]", "");
            if (otpEditText != null) {
                otpEditText.setText(otp);
            }
            stopProgressDialog();
        }
    };

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pre_sign_up_trial_4);
        setContentView(R.layout.activity_pre_sign_up);
        if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                setTitle("Create My Website");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        dataBase = new DataBase(PreSignUpActivityRia.this);
        activity = PreSignUpActivityRia.this;
        Methods.isOnline(activity);
        progressbar = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                /*.backgroundColorRes(R.color.transparent)*/
                .content("Verifying OTP")
                .progress(true, 0)
                .build();
        progressDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                /*.backgroundColorRes(R.color.transparent)*/
                .content("Fetching OTP from SMS inbox")
                .progress(true, 0)
                .build();
        bus = BusProvider.getInstance().getBus();
        businessNameEditText = (EditText) findViewById(R.id.editText_businessName);
        emailEditText = (EditText) findViewById(R.id.editText_Email);
        phoneEditText = (EditText) findViewById(R.id.editText_Phone);

        ivPhoneStatus = (ImageView) findViewById(R.id.ivPhoneStatus);

        etStreetAddress = findViewById(R.id.et_street_address);
        etOTP = findViewById(R.id.etOTP);

        verify_button = findViewById(R.id.verify_button);
        countryPhoneCode = findViewById(R.id.countrycode_signupscreen);

        CharSequence charSequence = Methods.fromHtml(getString(R.string.create_my_business_profile) +
                "<a href=\"" + getString(R.string.settings_tou_url) + "\"><u>Terms</u></a> and <a href=\"" + getString(R.string.settings_privacy_url) + "\"><u>Privacy Policy</u></a>.");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        makeLinkClickable(spannableStringBuilder, charSequence);

        sessionManager = new UserSessionManager(activity, activity);
//        Typeface robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");
//
//        businessNameEditText.setTypeface(robotoLight);
//        businessCategoryEditText.setTypeface(robotoLight);
//        cityEditText.setTypeface(robotoLight);
//        countryEditText.setTypeface(robotoLight);
//        emailEditText.setTypeface(robotoLight);
//        phoneEditText.setTypeface(robotoLight);
//        countryPhoneCode.setTypeface(robotoLight);

        Util.addBackgroundImages();
        /*LoadCountryData countryData = new LoadCountryData(activity);
        countryData.LoadCountryData_Listener(this);
        countryData.execute();*/
        //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        verify_button.setOnClickListener(this);
        setEnableCreateWebsiteButton(true);

        Bundle extras = getIntent().getExtras();
        existing_profile_id = extras.getString("profile_id", "");
        if (existing_profile_id != null && existing_profile_id.length() > 0) {

        }
//        if (sessionManager.getIsSignUpFromFacebook().contains("true")) {
//            if (extras != null) {
//                businessNameEditText.setText(extras.getString("facebook_businessName"));
//                emailEditText.setText(extras.getString("facebook_email"));
//                businessCategoryEditText.setText("GENERAL SERVICES");
//                facebookPageURL = extras.getString("facebook_page_url");
//            }
//        }

        ivPhoneStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneEditText.getText().toString().trim().length() == 0) {
                    // show message
                    initiatePopupWindow(v);
                }
            }
        });
//        emailEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String result = s.toString().replaceAll(" ", "");
//                if (!s.toString().equals(result)) {
//                    emailEditText.setText(result);
//                    emailEditText.setSelection(result.length());
//                    // validateEmail(activity,result,Validate_Email_API_KEY,bus);
//                    // Valid_Email.validateEmail(activity,result);
//                }
//            }
//        });

//        phoneEditText.setFocusable(false);
//        phoneEditText.setFocusableInTouchMode(false);

//        countryEditText.setFocusable(false);
//        countryEditText.setFocusableInTouchMode(false);

//        phoneEditText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                    if (TextUtils.isEmpty(countryEditText.getText().toString())) {
//                        Methods.showSnackBarNegative(PreSignUpActivityRia.this, "Please select city first");
//                    } else {
//                        showOtpDialog();
//                    }
//                }
//
//                return true;
//            }
//        });
        setDetails();
    }

    protected void makeLinkClickable(SpannableStringBuilder sp, CharSequence charSequence) {

        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);

        for (final URLSpan urlSpan : spans) {

            ClickableSpan clickableSpan = new ClickableSpan() {
                public void onClick(View view) {
                    Log.e("urlSpan", urlSpan.getURL());
                    Intent intent;
                    intent = new Intent(PreSignUpActivityRia.this, Mobile_Site_Activity.class);
                    intent.putExtra("WEBSITE_NAME", urlSpan.getURL());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            };

            sp.setSpan(clickableSpan, sp.getSpanStart(urlSpan),
                    sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

    }

    private void suggestTag(PreSignUpActivityRia preSignUpActivity, String data_businessName, String data_country, String data_city, String data_businessCategory, Bus bus) {

        new Suggest_Tag_Service(preSignUpActivity, data_businessName, data_country, data_city, data_businessCategory, Constants.clientId, bus);
    }

    @Subscribe
    public void post_verifyUniqueNumber(Primary_Number_Event response) {

        boolean isNumberUnique = response.model;

        if (!isNumberUnique) {
            suggestTag(PreSignUpActivityRia.this, data_businessName, data_country, data_city, data_businessCategory, bus);
        } else {
            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.number_already_registered))
                    .content(getString(R.string.number_already_registered_enter_different))
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE)
                    .input(getString(R.string.enter_mobile_number), null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
//                            phoneEditText.setText(input.toString());
                        }
                    })
                    .positiveText(getString(R.string.ok))
                    .negativeText(getString(R.string.cancel_in_capital))
                    .show();

            MaterialProgressBar.dismissProgressBar();
            //PreSignUpDialog.showDialog_WebSiteCreation(activity,"Number Already Registered. . .","Try Again . . .");
            //goToNextScreen = false;

        }

    }

//    private void loadCountryCodeandCountryNameMap() {
//        Country_CodeMap = new HashMap<>();
//        Code_PhoneMap = new HashMap<>();
//        String[] locales = Locale.getISOCountries();
//        for (String countryCode : locales) {
//            Locale obj = new Locale("", countryCode);
//            signUpCountryList.add(obj.getDisplayCountry());
//            Country_CodeMap.put(obj.getDisplayCountry(), obj.getCountry());
//            //Log.v("ggg",obj.getCountry());
//        }
//        Country_CodeMap.put("USA", "US");
//        Collections.sort(signUpCountryList);
//        if (isFinishing()) {
//            return;
//        }
//        hideProgressbar();
//        String[] string_array = getResources().getStringArray(R.array.CountryCodes);
//        for (int i = 0; i < string_array.length; i++) {
//            String phoneCode = string_array[i].split(",")[0];
//            String countryCode = string_array[i].split(",")[1];
//            Code_PhoneMap.put(countryCode, phoneCode);
//        }
//
//    }

    private void setDetails() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                showProgressbar();
//                loadCountryCodeandCountryNameMap();
//            }
//        }).start();
//        Bundle mBundle = getIntent().getExtras();
//
//        if (mBundle != null &&
//                mBundle.containsKey("mBundle")) {
//
//            mBundle = mBundle.getBundle("mBundle");
//
//            if(mBundle == null)
//            {
//                return;
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Name))) {
//                businessNameEditText.setText(mBundle.getString(Save_Name, ""));
//
//            }
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Cat))) {
//                businessCategoryEditText.setText(mBundle.getString(Save_Cat, ""));
//
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Email))) {
//                emailEditText.setText(mBundle.getString(Save_Email, ""));
//            }
//
//            /*if (TextUtils.isEmpty(mBundle.getString(Save_Phone)) || TextUtils.isEmpty(mBundle.getString(Save_Otp))) {
//
//            }*/
//
//            /**
//             * Modified above condition and added because OTP verification is removed from the flow
//             */
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Phone)))
//            {
//                data_country_code = mBundle.getString(Save_Phone_Code, "").replace("+", "");
//                phoneEditText.setText(String.valueOf(mBundle.getString(Save_Phone_Code, "") + " - " +
//                        mBundle.getString(Save_Phone, "")));
//                data_phone = mBundle.getString(Save_Phone, "");
//            }
//
//            else if (mBundle.getString(Save_Otp, "").equalsIgnoreCase("true")) {
//
//                ivPhoneStatus.setImageResource(R.drawable.green_check);
//                data_country_code = mBundle.getString(Save_Phone_Code, "").replace("+", "");
//                phoneEditText.setText(String.valueOf(mBundle.getString(Save_Phone_Code, "") + " - " +
//                        mBundle.getString(Save_Phone, "")));
//                data_phone = mBundle.getString(Save_Phone, "");
//
//                phoneEditText.setFocusable(false);
//                phoneEditText.setFocusableInTouchMode(false);
//
//                etOTP.setFocusable(false);
//                etOTP.setFocusableInTouchMode(false);
//
//                phoneEditText.setEnabled(false);
//                phoneEditText.setClickable(false);
//
//                countryPhoneCode.setTextColor(getResources().getColor(R.color.light_gray));
//                phoneEditText.setTextColor(getResources().getColor(R.color.light_gray));
//
//            } else {
//
//                ivPhoneStatus.setImageResource(R.drawable.icon_info);
//                phoneEditText.setEnabled(true);
//                phoneEditText.setClickable(true);
//
//                phoneEditText.setTextColor(getResources().getColor(R.color.black));
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Street_Address))) {
//                etStreetAddress.setText(mBundle.getString(Save_Street_Address, ""));
//
//            }
//
//            if (TextUtils.isEmpty(mBundle.getString(Save_Website_Address)) || TextUtils.isEmpty(mBundle.getString(Save_IS_FP_AVAILABLE)))
//            {
//                if (ivWebsiteStatus.getVisibility() == View.VISIBLE) {
//                    ivWebsiteStatus.setVisibility(View.GONE);
//                }
//            } else if (mBundle.getString(Save_IS_FP_AVAILABLE, "").equalsIgnoreCase("true")) {
//                if (ivWebsiteStatus.getVisibility() != View.VISIBLE) {
//                    ivWebsiteStatus.setVisibility(View.VISIBLE);
//                }
//                ivWebsiteStatus.setBackgroundResource(R.drawable.green_check);
//                fpTag = mBundle.getString(Save_Website_Address, "");
//            } else {
//                if (ivWebsiteStatus.getVisibility() != View.VISIBLE) {
//                    ivWebsiteStatus.setVisibility(View.VISIBLE);
//                }
//                ivWebsiteStatus.setBackgroundResource(R.drawable.warning);
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Lat))) {
//                data_lat = mBundle.getString(Save_Lat);
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Lng))) {
//                data_lng = mBundle.getString(Save_Lng);
//            }
//
//            if (!TextUtils.isEmpty(mBundle.getString(Save_Otp))) {
//                etOTP.setText(mBundle.getString(Save_Otp, ""));
//            }
//        }

    }

    @Subscribe
    public void post_SuggestTag(Suggest_Tag_Event response) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void initiatePopupWindow(View image) {
        Rect location = locateView(image);
        if (location == null) return;
        View layout1 = LayoutInflater.from(this).inflate(R.layout.layout_popup_dialog, null);
        layout1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int position_x = location.centerX() - layout1.getMeasuredWidth();
        int position_y = location.bottom - location.height() - layout1.getMeasuredHeight();
        if (popup == null) {
            try {

                popup = new PopupWindow(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.layout_popup_dialog, null);
                popup.setBackgroundDrawable(ContextCompat.getDrawable(PreSignUpActivityRia.this, R.color.transparent));
                popup.setContentView(layout);
                popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);

                popup.showAtLocation(image.getRootView(), Gravity.NO_GRAVITY, position_x, position_y);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (popup.isShowing()) {
            popup.dismiss();
        } else {
            popup.showAtLocation(image.getRootView(), Gravity.NO_GRAVITY, position_x, position_y);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);

    }

    private boolean getEditTextData() {
        try {
            MixPanelController.track("EnterDetailsNext", null);
            data_businessName = businessNameEditText.getText().toString().trim();
            data_email = emailEditText.getText().toString().trim();
            data_phone = phoneEditText.getText().toString().trim();
            fpTag = "";

            allFieldsValid = true;
            if (data_businessName.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(businessNameEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_business_name));
            } else if (etStreetAddress.getText().toString().trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(etStreetAddress);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_street_address));
            } else if (!(Signup_Validation.isValidEmail(emailEditText.getText().toString()))) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(emailEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_email));
            } else if (phoneEditText.getText().toString().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(phoneEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_mobile_number));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return allFieldsValid;
    }

    @Override
    public void onItemClick(String itemName) {
        if (businessCategory_Selected) {
//            businessCategoryEditText.setText(itemName);
            businessCategory_Selected = false;
        } else if (country_Selected) {
            country_Selected = false;
        }
    }

    @Override
    public void tagStatus(String status, String tag) {

    }

    @Override
    public void CheckUniqueNumber_preExecute(String value) {
        showLoader(getString(R.string.checking_contact_number));
    }

    @Override
    public void CheckUniqueNumber_postExecute(String value) {

       /* pd.dismiss();
        if (value.equals("Success")) {
            otpVerifyDialog(value);
            numberDialog.dismiss();

        } else if (value.equals("Failure")) {
            Methods.showSnackBarNegative(numberDialog.getView(), "Please enter another number");
            goToNextScreen = false;
        }*/

    }

    private void showLoader(final String message) {

        if (pd == null) {
            pd = new ProgressDialog(PreSignUpActivityRia.this);
            pd.setCancelable(false);
        }
        pd.setMessage(message);
        pd.show();
    }

    private void hideLoader() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    public void CheckUniqueNumber_postExecute(String value, String phoneNumber) {
        hideLoader();
        if (value.equals("Success")) {
            pd.dismiss();
            phoneEditText.setText("+" + data_country_code + " - " + phoneNumber);
            ivPhoneStatus.setImageResource(R.drawable.checkmark_icon);
            data_phone = phoneNumber;

            numberDialog.dismiss();

        } else if (value.equalsIgnoreCase("Error")) {
            Methods.showSnackBarNegative(numberDialog.getView(), getString(R.string.something_went_wrong_try_again));
//            goToNextScreen = false;
        } else {
            Methods.showSnackBarNegative(numberDialog.getView(), getString(R.string.number_already_exists));
//            goToNextScreen = false;
        }
    }

    private HashMap<String, String> getJSONData() {
        HashMap<String, String> store = new HashMap<String, String>();
        try {
            //            store.put("tag", fpTag);
            //            store.put("city", data_city);
            //            store.put("country", data_country);
            //            store.put("lat", data_lat);
            //            store.put("lng", data_lng);
            store.put("clientId", Constants.clientId);
            store.put("name", data_businessName);
            store.put("address", etStreetAddress.getText().toString());

            store.put("pincode", "------");
            store.put("country", "India");

            store.put("primaryNumber", data_phone);
            store.put("email", data_email);
            store.put("primaryNumberCountryCode", "91");
            store.put("Uri", "");
            store.put("fbPageName", "");
            store.put("primaryCategory", data_businessCategory);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return store;

        // {"clientId":"138440FB190A4CDA998274F52952B0657465B03A177C499DA495372764CE185F",
        //  "tag":"ANUPAMRETAIL",
        // "contactName":"Anupam Retail Ltd.",
        // "name":"Anupam Retail Ltd.",
        // "desc":"Signed for BSP with Infomedia",
        // "address":"Delhi",
        // "city":"Delhi",
        // "pincode":"110028",
        // "country":"India",
        // "primaryNumber":"1125893883",
        // "email":"kavita@anupamsinks.com",
        // "primaryNumberCountryCode":"+91",
        // "Uri":"",
        // "fbPageName":"",
        // "primaryCategory":"OTHER RETAIL",
        // "lat":0,
        // "lng":0}
    }

    @Override
    protected void onPause() {
        super.onPause();

        bus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, "91");
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.clear();
//        editor.apply();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.verify_button) {
            try {
                if (getEditTextData()) {
                    if (data_lat.equalsIgnoreCase("0")) {

//                        LatLng latLng = new NFGeoCoder(PreSignUpActivityRia.this).reverseGeoCode(
//                                etStreetAddress.getText().toString(), "India",
//                                "");
//                        if (latLng != null) {
//                            data_lat = latLng.latitude + "";
//                            data_lng = latLng.longitude + "";
//                        }
                    }
                    hideKeyBoard();
                    MixPanelController.track("CreateMyWebsite", null);
                    WebEngageController.trackEvent(BUSINESS_PROFILE_CREATION_INITIATED, EVENT_LABEL_BUSINESS_PROFILE_CREATION_INITIATED, NULL);
                    createStore_retrofit(PreSignUpActivityRia.this, getJSONData(), bus);
                } else {
                    Toast.makeText(activity, "All your inputs are not valid. Please cross check once.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                WebEngageController.trackEvent(BUSINESS_PROFILE_CREATION_FAILED, EVENT_LABEL_BUSINESS_PROFILE_CREATION_FAILED, NULL);
                e.printStackTrace();
            }
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(verify_button.getWindowToken(), 0);
    }

    @Override
    public void LoadCountry_onPostExecute_Completed(String result) {

//        if (result.equals("failure")) {
//            countryEditText.setEnabled(true);
//            countryEditText.setOnClickListener(null);
//            countryEditText.setText("Canada");
//        } else if (result.equals("success")) {
//            countryEditText.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectC();
//                    //Util.hideInput(requireActivity());
//
//                }
//            });
//        }

    }

    private void setLatLong(final String fpAddress) {


        new Thread(new Runnable() {
            public double lng, lat;
            public LatLng latlong;

            @Override
            public void run() {
                int requestCode;
                String responseMessage;
                HttpClient httpclient = new DefaultHttpClient();
                String fpAdcdress = fpAddress;
                HttpGet httpget = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?address=" + fpAdcdress + "&sensor=false");
                try {
                    httpget.addHeader("Content-Type", "application/json");
                    HttpResponse response = httpclient.execute(httpget);
                    requestCode = response.getStatusLine().getStatusCode();
                    responseMessage = EntityUtils.toString(response.getEntity());

                    if (requestCode == 200) {
                        try {
                            JSONObject store = new JSONObject(responseMessage);
                            this.lng = ((JSONArray) store.get("results"))
                                    .getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lng");

                            this.lat = ((JSONArray) store.get("results"))
                                    .getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lat");


                            Constants.latitude = this.lat;
                            Constants.longitude = this.lng;

                            if (lng != 0 && lat != 0) {
                                latlong = new LatLng(lng, lat);
                                this.latlong = latlong;

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();
                        }
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
        }).start();


    }

    @Override
    public void emailValidated(String emailResult) {

        if (emailResult.equals("valid")) {
            String tagName = API_Layer_Signup.getTag(activity, data_businessName, data_country, data_city, data_businessCategory);

            API_Layer_Signup.checkUniqueNumber(activity, data_phone, data_country_code);
        } else {
            Toast.makeText(activity, getString(R.string.invalid_email_please_enter_again), Toast.LENGTH_SHORT).show();
        }

    }

    private void otpVerifyDialog(final String number) {
        //call send otp api
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp_verify, null);
        final TextView tvNumber = view.findViewById(R.id.tv_number);
        tvNumber.setText("(" + number + ")");
        final EditText otp = (EditText) view.findViewById(R.id.editText);
        otpEditText = otp;
        TextView tvOTPOverCall = (TextView) view.findViewById(R.id.tv_get_otp_over_call);
        TextView resend = (TextView) view.findViewById(R.id.resend_tv);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((TextView) v).setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
                //sendSms(number);
            }
        });
        otpDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                //.negativeText("Cancel")
                .autoDismiss(false)
                //.titleColorRes(R.color.primary_color)
                //.positiveText("Submit")
                .title(R.string.one_time_password)
                .canceledOnTouchOutside(false)
                /* .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.primary_color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                     @Override
                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                         String numText = otp.getText().toString().trim();
                         hideKeyBoard();
                         if (numText.length() > 0) {
                             verifySms(number, numText);
                         } else {
                             Toast.makeText(PreSignUpActivityRia.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                         }
                     }
                 })
                 .onNegative(new MaterialDialog.SingleButtonCallback() {
                     @Override
                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                         hideKeyBoard();
                         dialog.dismiss();
                     }
                 })*/.show();

        final TextView positive = otpDialog.getActionButton(DialogAction.POSITIVE);
        TextView tvSubmit = view.findViewById(R.id.tv_submit);
        tvOTPOverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reSendOTPOverCall(number);
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numText = otp.getText().toString().trim();
                hideKeyBoard();
                if (numText.length() > 0) {
                    verifySms(number, numText);
                } else {
                    Toast.makeText(PreSignUpActivityRia.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp.getText().toString().trim().length() > 0) {
                    positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.primary));
                } else {
                    positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
                }
            }
        });
        // sendSms(number);
    }

//    private void sendSms(String number) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
//            startProgressDialog();
//        }
//        Methods.SmsApi smsApi = Constants.smsVerifyAdapter.create(Methods.SmsApi.class);
//        Map<String, String> hashMap = new HashMap<>();
//        hashMap.put("PHONE", number);
//        hashMap.put("COUNTRY", countryEditText.getText().toString().trim());
//        smsApi.reSendOTP(hashMap, new Callback<SmsVerifyModel>() {
//            @Override
//            public void success(SmsVerifyModel model, Response response) {
//                if (model == null) {
//                    stopProgressDialog();
//                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (model.isOTPSent()) {
//                    stopProgressDialog();
//
//                } else {
//                    stopProgressDialog();
//                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                stopProgressDialog();
//                Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void reSendOTPOverCall(String number) {
        // showProgressbar();
        Toast.makeText(PreSignUpActivityRia.this, getString(R.string.you_will_receive_a_call_shortly), Toast.LENGTH_SHORT).show();
        Methods.SmsApi smsApi = Constants.smsVerifyAdapter.create(Methods.SmsApi.class);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("PHONE", number);
        hashMap.put("COUNTRY", "India");
        smsApi.resendOTPOverCall(hashMap, new Callback<SmsVerifyModel>() {
            @Override
            public void success(SmsVerifyModel model, Response response) {
                if (model == null) {
                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.isOTPSent()) {

                } else {
                    Toast.makeText(PreSignUpActivityRia.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                stopProgressDialog();
                Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifySms(final String number, String otpCode) {
        showProgressbar();
        Methods.SmsApi smsApi = Constants.smsVerifyAdapter.create(Methods.SmsApi.class);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("OTP", otpCode);
        hashMap.put("PHONE", number);
        hashMap.put("COUNTRY", "India");
        smsApi.verifyOTPCode(hashMap, new Callback<SmsVerifyModel>() {
            @Override
            public void success(SmsVerifyModel model, Response response) {
                hideProgressbar();
                if (model == null) {
                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.isOTPValid()) {
                    otpDialogDismiss();
                    phoneEditText.setText("+" + data_country_code + " - " + number);
                    ivPhoneStatus.setImageResource(R.drawable.checkmark_icon);
                    data_phone = number;

                } else {
                    hideProgressbar();
                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.please_enter_valid_otp), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressbar();
                Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressbar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressbar != null) {
                    progressbar.setCancelable(false);
                    progressbar.show();
                }
            }
        });

    }

    private void hideProgressbar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressbar != null && progressbar.isShowing()) {
                    progressbar.dismiss();
                }
            }
        });

    }

    private void otpDialogDismiss() {
        if (otpDialog != null && otpDialog.isShowing()) {
            otpDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mSmsReceiver, new IntentFilter(Constants.SMS_OTP_RECEIVER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSmsReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void startProgressDialog() {
        if (progressDialog != null)
            progressDialog.show();
    }

    private void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDomainAvailable(String websiteTag) {
        this.fpTag = websiteTag;
//        if (ivWebsiteStatus.getVisibility() != View.VISIBLE) {
//            ivWebsiteStatus.setVisibility(View.VISIBLE);
//        }
//        ivWebsiteStatus.setBackgroundResource(R.drawable.green_check);
    }

    @Override
    public void onDomainNotAvailable() {
        this.fpTag = "";
//        if (etWebsiteAddress.getText().toString().trim().length() == 0) {
//            ivWebsiteStatus.setVisibility(View.GONE);
//        } else if (ivWebsiteStatus.getVisibility() != View.VISIBLE) {
//            ivWebsiteStatus.setVisibility(View.VISIBLE);
//        }
//        ivWebsiteStatus.setBackgroundResource(R.drawable.warning);
    }

    private void createStore_retrofit(PreSignUpActivityRia webSiteAddressActivity, HashMap<String, String> jsonData, Bus bus) {
        setEnableCreateWebsiteButton(false);
        showLoader(getString(R.string.creating_website));
        new Create_Tag_Service(webSiteAddressActivity, existing_profile_id, jsonData, bus);
    }

    private void setEnableCreateWebsiteButton(boolean bool) {
        verify_button.setTag(bool);
        verify_button.setBackgroundResource(bool ? R.drawable.rounded_corner_pre_signup : R.drawable.rounded_gray_padded);
    }

    @Subscribe
    public void put_createStore(Create_Store_Event response) {
        Log.d("Store_Event_Response", "" + response.fpId);

        final String fpId = response.fpId;
        if (TextUtils.isEmpty(fpId)) {
            hideLoader();
            Toast.makeText(PreSignUpActivityRia.this, getString(R.string.failed_to_create_business_profile), Toast.LENGTH_SHORT).show();
            setEnableCreateWebsiteButton(true);
            return;
        }
        //Log the Conversion Goal for Boost OnBoarding Complete
        Utils.logOnBoardingCompleteConversionGoals(activity, fpId);

        dataBase.insertLoginStatus(fpId);
        sessionManager = new UserSessionManager(getApplicationContext(), PreSignUpActivityRia.this);
        sessionManager.storeFPID(fpId);
        sessionManager.storeSourceClientId(Constants.clientId);

        //thinksity check
        if (Constants.clientId.equals(Constants.clientIdThinksity)) {
            sessionManager.storeIsThinksity("true");
        }


        // Give a Delay of 2 Seconds and Call this method
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader();
                downloadBusinessProfileDetails(PreSignUpActivityRia.this, fpId, Constants.clientId, bus);
            }
        }, 2000);
    }

    private void downloadBusinessProfileDetails(PreSignUpActivityRia activity, String fpId, String clientId, Bus bus) {
        showLoader(getString(R.string.please_wait_for_dashboard));
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response) {
        // Close of Progress Bar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoader();
            }
        });
        //VISITOR and SUBSCRIBER COUNT API
        GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(PreSignUpActivityRia.this, sessionManager);
        visit_subcribersCountAsyncTask.execute();
        if (sessionManager.getIsSignUpFromFacebook().contains("true")) {
            if (sessionManager.getFacebookName() != null && sessionManager.getFacebookAccessToken() != null) {
                dataBase.updateFacebookNameandToken(sessionManager.getFacebookName(), sessionManager.getFacebookAccessToken());
            }
            if (sessionManager.getFacebookPage() != null && sessionManager.getFacebookPageID() != null && sessionManager.getPageAccessToken() != null) {
                dataBase.updateFacebookPage(sessionManager.getFacebookPage(), sessionManager.getFacebookPageID(), sessionManager.getPageAccessToken());
            }
            new Download_Facebook_Image().execute(sessionManager.getFacebookPageURL(), sessionManager.getFPID());
            Signup_Descriptinon descriptinon = new Signup_Descriptinon(sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                    sessionManager.getFacebookProfileDescription(),
                    sessionManager.getFacebookPageID(),
                    response.model.FPWebWidgets
            );
            descriptinon.execute();
        }
        if (response != null && response.model != null) {
            WebEngageController.trackEvent(BUSINESS_PROFILE_CREATION_SUCCESSFUL,
                    EVENT_LABEL_BUSINESS_PROFILE_CREATION_SUCCESSFUL, response.model.Tag);
        }
        Intent webIntent = new Intent(PreSignUpActivityRia.this, HomeActivity.class);
        webIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle loginBundle = new Bundle();
        loginBundle.putBoolean("fromLogin", true);
        Constants.isWelcomScreenToBeShown = true;
        webIntent.putExtras(loginBundle);
        startActivity(webIntent);
        finish();
    }

}
