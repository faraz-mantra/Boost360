package com.nowfloats.signup.UI.UI;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.riachatsdk.activities.ChatWebViewActivity;
import com.nowfloats.riachatsdk.utils.NFGeoCoder;
import com.nowfloats.signup.UI.API.API_Layer;
import com.nowfloats.signup.UI.API.API_Layer_Signup;
import com.nowfloats.signup.UI.API.Download_Facebook_Image;
import com.nowfloats.signup.UI.API.LoadCountryData;
import com.nowfloats.signup.UI.API.Signup_Descriptinon;
import com.nowfloats.signup.UI.API.Valid_Email;
import com.nowfloats.signup.UI.DomainAvailabilityCheck;
import com.nowfloats.signup.UI.Model.Create_Store_Event;
import com.nowfloats.signup.UI.Model.Email_Validation_Model;
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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PreSignUpActivityRia extends AppCompatActivity implements
        PreSignUpDialog.Dialog_Activity_Interface,
        API_Layer_Signup.SignUp_Interface, View.OnClickListener,
        LoadCountryData.LoadCountryData_Interface,
        Valid_Email.Valid_Email_Interface
        , GoogleApiClient.OnConnectionFailedListener, DomainAvailabilityCheck.CallBack {
    UserSessionManager sessionManager;
    private static final String TAG = "PreSignUp";
    public String[] cat = null;
    ListView l;
    HeaderText title;
    ImageView ivWebsiteStatus, ivPhoneStatus;
    Bus bus;
    public static ProgressBar cityProgress;
    private static EditText businessNameEditText, businessCategoryEditText, countryEditText, emailEditText,
            phoneEditText, etStreetAddress, edtLocality, etWebsiteAddress, etPinCode, etOTP;

    public static AutoCompleteTextView cityEditText;
    private static TextView countryPhoneCode, tvtermAndPolicy;
    String data_businessName, data_businessCategory, data_city, data_country, data_email, data_phone, data_country_code,
            data_lat = "0", data_lng = "0";

    EditText otpEditText;
    private MaterialDialog progressDialog, progressbar;
    private boolean businessCategory_Selected = false;
    SharedPreferences sharedpreferences;
    private boolean country_Selected = false;
    //  private String[] businessCategoryList;

    boolean allFieldsValid = true;
    private boolean goToNextScreen = false;
    private String tagName;
    String fpTag;
    private double lng, lat;
    private LatLng latlong;

    public static final String MyPREFERENCES = "PrefsSignUp";
    public static final String Save_Name = "nameKey";
    public static final String Save_Cat = "categoryKey";
    public static final String Save_Street_Address = "streetAddressKey";
    public static final String Save_Locality = "localityKey";
    public static final String Save_Phone = "phoneNumberKey";
    public static final String Save_Phone_Code = "phoneCodeKey";
    public static final String Save_Email = "emailKey";
    public static final String Save_Website_Address = "websiteAdressKey";
    public static final String Save_Otp = "otpKey";
    public static final String Save_Pin_Code = "pincodeKey";
    public static final String Save_Country = "countryKey";
    public static final String Save_City = "cityKey";
    public static final String Save_Lat = "ria_latKey";
    public static final String Save_Lng = "ria_lngKey";
    public static final String Save_IS_FP_AVAILABLE = "isFPAvailable";

    public static Email_Validation_Model emailModel;
    String facebookPageURL;

    HashMap<String, String> Country_CodeMap, Code_PhoneMap;
    private String Validate_Email_API_KEY = "e5f5fb5a-8e1f-422e-9d25-a67a16018d47";
    public Activity activity;


    AutocompleteFilter filter;
    private List<String> citys = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> signUpCountryList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<String> categories;
    private LinearLayout verify_button;
    private DomainAvailabilityCheck mDomainAvailabilityCheck;
    private DataBase dataBase;
    private String citytext = "";
    public ProgressDialog pd;
    private PopupWindow popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_sign_up_trial_4);
        if(!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            findViewById(R.id.layout_ria).setVisibility(View.GONE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if(getSupportActionBar() != null){
                setTitle(getString(R.string.create_my_website));
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
        businessCategoryEditText = (EditText) findViewById(R.id.editText_businessCategory);
        cityEditText = (AutoCompleteTextView) findViewById(R.id.editText_city);
        countryEditText = (EditText) findViewById(R.id.editText_Country);
        emailEditText = (EditText) findViewById(R.id.editText_Email);
        phoneEditText = (EditText) findViewById(R.id.editText_Phone);
        ivWebsiteStatus = (ImageView) findViewById(R.id.ivWebsiteStatus);
        ivPhoneStatus = (ImageView) findViewById(R.id.ivPhoneStatus);

        etStreetAddress = (EditText) findViewById(R.id.et_street_address);
        edtLocality = (EditText) findViewById(R.id.etLocality);
        etPinCode = (EditText) findViewById(R.id.editText_pinCode);
        etWebsiteAddress = (EditText) findViewById(R.id.etWebsiteAddress);
        etOTP = (EditText) findViewById(R.id.etOTP);

        verify_button = (LinearLayout) findViewById(R.id.verify_button);
        countryPhoneCode = (TextView) findViewById(R.id.countrycode_signupscreen);
        tvtermAndPolicy = (TextView) findViewById(R.id.tvtermAndPolicy);
        cityProgress = (ProgressBar) findViewById(R.id.city_progressbar);
        cityProgress.setVisibility(View.GONE);

        businessCategoryEditText.setFocusable(false);
        businessCategoryEditText.setFocusableInTouchMode(false);
        makeAutoCompleteFilter(null);
        CharSequence charSequence = Methods.fromHtml("By clicking on 'CREATE MY SITE' you agree to our " +
                "<a href=\"" + getString(R.string.settings_tou_url) + "\"><u>Terms</u></a> and <a href=\"" + getString(R.string.settings_privacy_url) + "\"><u>Privacy Policy</u></a>.");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        makeLinkClickable(spannableStringBuilder, charSequence);

        tvtermAndPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvtermAndPolicy.setText(spannableStringBuilder);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, citys);
        cityEditText.setAdapter(adapter);


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
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        verify_button.setOnClickListener(this);

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cityEditText.setText(citytext);
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if (s.toString().trim().length() >= 3) {
                    cityProgress.setVisibility(View.VISIBLE);
                    PlacesTask placesTask = new PlacesTask(activity);
                    placesTask.execute(s.toString());
                }*/

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //ArrayList<String> citys=new ArrayList<String>();
                //cityEditText.setAdapter(null);
                if (cityEditText.getTag() != null && !(boolean) cityEditText.getTag()) {

                } else {
                    try {

                        final PendingResult<AutocompletePredictionBuffer> result =
                                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, cityEditText.getText().toString().trim(),
                                        null, filter);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AutocompletePredictionBuffer a = result.await();
                                //Log.v("ggg","ok");
                                citys.clear();
                                cityEditText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                for (int i = 0; i < a.getCount(); i++) {
                                    //Log.v("ggg",a.get(i).getFullText(new StyleSpan(Typeface.NORMAL)).toString()+" length "+citys.size());
//                                citys.add(a.get(i).getPrimaryText(new StyleSpan(Typeface.NORMAL)).toString());
                                    String city = a.get(i).getPrimaryText(new StyleSpan(Typeface.NORMAL)).toString() + "," + a.get(i).getSecondaryText(new StyleSpan(Typeface.NORMAL)).toString();
                                    if (city.contains(",")) {
                                        String country[] = city.split(",");
                                        city = country[0];
                                        if (country.length>1){
                                            city += ","+(country[country.length -1].trim());
                                        }
                                    }
                                    citys.add(city);
                                }

                                a.release();

                                cityEditText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new ArrayAdapter<>(PreSignUpActivityRia.this,
                                                android.R.layout.simple_dropdown_item_1line, citys);
                                        if (!isFinishing()) {
                                            cityEditText.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }).start();
                    } catch (Exception e) {

                    }
                }
            }
        });

        cityEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String city = ((TextView) view).getText().toString();
                if (city.contains(",")) {
                    String country[] = city.split(",");
                    city = country[0];
                    if (country.length>1){
                        countryEditText.setText(country[country.length -1].trim());
                    }
//                  countryEditText.setFocusable(true);
                }
                cityEditText.setTag(false);
                cityEditText.setText(city);
                cityEditText.setTag(true);
                citytext = city;
                updateCountry();

                //Log.v("ggg",country_code);
//                countryPhoneCode.setText("+" + phone_code);
//
//                if (!data_country_code.equalsIgnoreCase(country_code)) {
//                    phoneEditText.setText("");
//                    ivPhoneStatus.setBackgroundResource(R.drawable.warning);
//                }
//
//                data_country_code = country_code;
//                sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, phone_code);
            }
        });

        if (sessionManager.getIsSignUpFromFacebook().contains("true")) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                businessNameEditText.setText(extras.getString("facebook_businessName"));
                cityEditText.setText(extras.getString("facebook_city"));
                countryEditText.setText(extras.getString("facebook_country"));
                emailEditText.setText(extras.getString("facebook_email"));
                businessCategoryEditText.setText("GENERAL SERVICES");
                facebookPageURL = extras.getString("facebook_page_url");
            }
        }
        countryEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Methods.showSnackBarNegative(PreSignUpActivityRia.this,"Please select city first");
                }
                return true;

            }
        });
        ivPhoneStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneEditText.getText().toString().trim().length()==0){
                    // show message
                    initiatePopupWindow(v);
                }
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    emailEditText.setText(result);
                    emailEditText.setSelection(result.length());
                    // validateEmail(activity,result,Validate_Email_API_KEY,bus);
                    // Valid_Email.validateEmail(activity,result);
                }
            }
        });

        businessCategoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessCategory_Selected = true;
                country_Selected = false;

                if (categories == null) {
                    final ProgressDialog pd = ProgressDialog.show(PreSignUpActivityRia.this, "", getResources().getString(R.string.wait_while_loading_category));
                    API_Layer api = Constants.restAdapter.create(API_Layer.class);
                    api.getCategories(new Callback<ArrayList<String>>() {
                        @Override
                        public void success(ArrayList<String> strings, Response response) {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            categories = strings;
                            showCategoryDialog(categories);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }
                            showCategoryDialog(new ArrayList<String>(Arrays.asList(Constants.storeBusinessCategories)));
                            Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showCategoryDialog(categories);
                }
            }
        });


        phoneEditText.setFocusable(false);
        phoneEditText.setFocusableInTouchMode(false);

//        countryEditText.setFocusable(false);
//        countryEditText.setFocusableInTouchMode(false);

        phoneEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (TextUtils.isEmpty(countryEditText.getText().toString())) {
                        Methods.showSnackBarNegative(PreSignUpActivityRia.this, "Please select city first");
                    } else{
                        showOtpDialog();
                    }
                }

                return true;
            }
        });

        mDomainAvailabilityCheck = new DomainAvailabilityCheck(PreSignUpActivityRia.this);
        etWebsiteAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {

                if (!isFirstCheck) {

                    if (etWebsiteAddress.getText().toString().trim().length() == 0){
                        ivWebsiteStatus.setVisibility(View.GONE);
                    }else if (Util.isNetworkStatusAvialable(PreSignUpActivityRia.this)) {
                        mDomainAvailabilityCheck.domainCheck(s.toString());
                    } else {
                        Toast.makeText(PreSignUpActivityRia.this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        setDetails();
    }

    private void updateCountry() {
        String selectedCountry = countryEditText.getText().toString();

        if (Country_CodeMap != null && Country_CodeMap.containsKey(selectedCountry)) {
            String country_code = Country_CodeMap.get(selectedCountry);
            if (Code_PhoneMap.containsKey(country_code))
                data_country_code = Code_PhoneMap.get(country_code);
        }

        if (phoneEditText.getText().toString().trim().length() != 0) {
            phoneEditText.setText("");
            ivPhoneStatus.setImageResource(R.drawable.icon_info);
        }
    }

    private boolean isFirstCheck = true;
    private MaterialDialog otpDialog, numberDialog;

    private void showOtpDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp, null);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("Please verify your mobile number");
        tvTitle.setVisibility(View.GONE);
        final EditText number = (EditText) view.findViewById(R.id.editText);
        numberDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .titleColorRes(R.color.primary_color)
                .title("Enter Mobile Number")
                .negativeText("Cancel")
//                .positiveText("Verify & Send OTP")
                .positiveText("Confirm")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .negativeColorRes(R.color.gray_transparent)
                .positiveColorRes(R.color.primary_color)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideKeyBoard();
                        if (Utils.isNetworkConnected(PreSignUpActivityRia.this)) {
                            int length = number.getText().toString().trim().length();
                            if (length == 0) {
                                Toast.makeText(PreSignUpActivityRia.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
                            } else if (length<4 || length>13){
                                Toast.makeText(PreSignUpActivityRia.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                            }else {
                                API_Layer_Signup.checkUniqueNumber(activity, number.getText().toString().trim());
                            }
                        } else {
                            Toast.makeText(PreSignUpActivityRia.this, getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideKeyBoard();
                        dialog.dismiss();
                    }
                }).show();

        final TextView positive = numberDialog.getActionButton(DialogAction.POSITIVE);
        positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (number.getText().toString().trim().length() > 0) {
                    positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.primary));
                } else {
                    positive.setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
                }
            }
        });
    }

    private void showCategoryDialog(ArrayList<String> categories) {
        new MaterialDialog.Builder(activity)
                .title("Select a Category")
                .items(categories)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        businessCategoryEditText.setText(text);
                        return false;
                    }
                })
                .show();
    }

    private void showCountryDialog(ArrayList<String> countries) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(PreSignUpActivityRia.this,
                R.layout.search_list_item_layout, countries);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PreSignUpActivityRia.this);
        builderSingle.setTitle("Select a Country");

        View view = LayoutInflater.from(PreSignUpActivityRia.this).inflate(R.layout.search_list_layout, null);
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
                countryEditText.setText(strVal);
                updateCountry();
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

        dialog.setCanceledOnTouchOutside(false);
    }

    protected void makeLinkClickable(SpannableStringBuilder sp, CharSequence charSequence) {

        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);

        for (final URLSpan urlSpan : spans) {

            ClickableSpan clickableSpan = new ClickableSpan() {
                public void onClick(View view) {
                    Log.e("urlSpan", urlSpan.getURL());
                    Intent intent;
                    if (BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
                        intent = new Intent(PreSignUpActivityRia.this, ChatWebViewActivity.class);
                        intent.putExtra(ChatWebViewActivity.KEY_URL, urlSpan.getURL());
                    }else{
                        intent = new Intent(PreSignUpActivityRia.this, Mobile_Site_Activity.class);
                        intent.putExtra("WEBSITE_NAME",urlSpan.getURL());
                    }
                    startActivity(intent);

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            };

            sp.setSpan(clickableSpan, sp.getSpanStart(urlSpan),
                    sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

    }

    private void makeAutoCompleteFilter(String country_code) {

        filter = null;
        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);

        if (country_code != null) {
            builder.setCountry(country_code.toUpperCase());
        }
        filter = builder.build();

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
            goToNextScreen = false;

        }

    }

    private void setDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showProgressbar();
                loadCountryCodeandCountryNameMap();
            }
        }).start();
        Bundle mBundle = getIntent().getExtras();

        if (mBundle != null &&
                mBundle.containsKey("mBundle")) {

            mBundle = mBundle.getBundle("mBundle");

            if (!TextUtils.isEmpty(mBundle.getString(Save_Name))) {
                businessNameEditText.setText(mBundle.getString(Save_Name, ""));

            }
            if (!TextUtils.isEmpty(mBundle.getString(Save_Cat))) {
                businessCategoryEditText.setText(mBundle.getString(Save_Cat, ""));

            }
            if (!TextUtils.isEmpty(mBundle.getString(Save_Country))) {
                countryEditText.setText(mBundle.getString(Save_Country, ""));
            } else {
                updateBasedOnMostRecentLocation(Constants.lastKnownAddress);
            }

            if (!TextUtils.isEmpty(mBundle.getString(Save_City))) {
                cityEditText.setText(mBundle.getString(Save_City, ""));
                citytext = cityEditText.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(mBundle.getString(Save_Email))) {
                emailEditText.setText(mBundle.getString(Save_Email, ""));
            }

            if (TextUtils.isEmpty(mBundle.getString(Save_Phone)) || TextUtils.isEmpty(mBundle.getString(Save_Otp))){

            } else if (mBundle.getString(Save_Otp).equalsIgnoreCase("true")) {

                ivPhoneStatus.setImageResource(R.drawable.green_check);
                data_country_code = mBundle.getString(Save_Phone_Code, "").replace("+", "");
                phoneEditText.setText(mBundle.getString(Save_Phone_Code, "") + " - " +
                        mBundle.getString(Save_Phone, ""));
                data_phone = mBundle.getString(Save_Phone, "");

                phoneEditText.setFocusable(false);
                phoneEditText.setFocusableInTouchMode(false);

                etOTP.setFocusable(false);
                etOTP.setFocusableInTouchMode(false);

                phoneEditText.setEnabled(false);
                phoneEditText.setClickable(false);

                countryPhoneCode.setTextColor(getResources().getColor(R.color.light_gray));
                phoneEditText.setTextColor(getResources().getColor(R.color.light_gray));

            } else {

                ivPhoneStatus.setImageResource(R.drawable.icon_info);
                phoneEditText.setEnabled(true);
                phoneEditText.setClickable(true);

                phoneEditText.setTextColor(getResources().getColor(R.color.black));
            }

            if (!TextUtils.isEmpty(mBundle.getString(Save_Street_Address))) {
                etStreetAddress.setText(mBundle.getString(Save_Street_Address, ""));

            }
            if (!TextUtils.isEmpty(mBundle.getString(Save_Locality))) {
                edtLocality.setText(mBundle.getString(Save_Locality, ""));
            }
            if (!TextUtils.isEmpty(mBundle.getString(Save_Pin_Code))) {
                etPinCode.setText(mBundle.getString(Save_Pin_Code, ""));
            }
            if (TextUtils.isEmpty(mBundle.getString(Save_Website_Address)) || TextUtils.isEmpty(mBundle.getString(Save_IS_FP_AVAILABLE))){
                if (ivWebsiteStatus.getVisibility() == View.VISIBLE){
                    ivWebsiteStatus.setVisibility(View.GONE);
                }
            } else if (mBundle.getString(Save_IS_FP_AVAILABLE).equalsIgnoreCase("true")) {
                if (ivWebsiteStatus.getVisibility() != View.VISIBLE){
                    ivWebsiteStatus.setVisibility(View.VISIBLE);
                }
                ivWebsiteStatus.setBackgroundResource(R.drawable.green_check);
                fpTag = mBundle.getString(Save_Website_Address, "");
                etWebsiteAddress.setText(mBundle.getString(Save_Website_Address, ""));

            } else {
                if (ivWebsiteStatus.getVisibility() != View.VISIBLE){
                    ivWebsiteStatus.setVisibility(View.VISIBLE);
                }
                ivWebsiteStatus.setBackgroundResource(R.drawable.warning);
                API_Layer_Signup.getTag(activity, businessNameEditText.getText().toString(),
                        countryEditText.getText().toString(), cityEditText.getText().toString(),
                        businessCategoryEditText.getText().toString());


            }

            etWebsiteAddress.setClickable(true);
            etWebsiteAddress.setEnabled(true);
//            etWebsiteAddress.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
            etWebsiteAddress.setTextColor(getResources().getColor(R.color.black));


            if (!TextUtils.isEmpty(mBundle.getString(Save_Lat))) {
                data_lat = mBundle.getString(Save_Lat);
            }

            if (!TextUtils.isEmpty(mBundle.getString(Save_Lng))) {
                data_lng = mBundle.getString(Save_Lng);
            }


            isFirstCheck = false;
            if (!TextUtils.isEmpty(mBundle.getString(Save_Otp))) {
                etOTP.setText(mBundle.getString(Save_Otp, ""));
            }
        }

    }

    @Subscribe
    public void post_SuggestTag(Suggest_Tag_Event response) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void loadCountryCodeandCountryNameMap() {
        Country_CodeMap = new HashMap<>();
        Code_PhoneMap = new HashMap<>();
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            signUpCountryList.add(obj.getDisplayCountry());
            Country_CodeMap.put(obj.getDisplayCountry(), obj.getCountry());
            //Log.v("ggg",obj.getCountry());
        }
        Country_CodeMap.put("USA","US");
        Collections.sort(signUpCountryList);
        if (isFinishing()) {
            return;
        }
        hideProgressbar();
        String[] string_array = getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < string_array.length; i++) {
            String phoneCode = string_array[i].split(",")[0];
            String countryCode = string_array[i].split(",")[1];
            Code_PhoneMap.put(countryCode, phoneCode);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (TextUtils.isEmpty(data_country_code)) {
                    String selectedCountry = countryEditText.getText().toString();

                    if (Country_CodeMap != null && Country_CodeMap.containsKey(selectedCountry)) {
                        String country_code = Country_CodeMap.get(selectedCountry);
                        if (Code_PhoneMap.containsKey(country_code))
                            data_country_code = Code_PhoneMap.get(country_code);
                    }
                }
                hideProgressbar();
            }
        });

    }

    private void initiatePopupWindow(View image) {
        Rect location = locateView(image);
        if (location == null) return;
        int position_x = location.centerX() - Methods.dpToPx(300,PreSignUpActivityRia.this);
        int position_y = location.bottom-location.height() - Methods.dpToPx(120,PreSignUpActivityRia.this);
        if (popup == null) {
            try {

                popup = new PopupWindow(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.layout_popup_dialog, null);
                popup.setBackgroundDrawable(ContextCompat.getDrawable(PreSignUpActivityRia.this,R.color.transparent));
                popup.setContentView(layout);
                popup.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);

                popup.showAtLocation(image.getRootView(), Gravity.NO_GRAVITY, position_x,position_y);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (popup.isShowing()){
            popup.dismiss();
        }else
        {
            popup.showAtLocation(image.getRootView(), Gravity.NO_GRAVITY, position_x,position_y);
        }
    }
    public static Rect locateView(View v)
    {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
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
    protected void onResume() {
        super.onResume();

        bus.register(this);

    }

    private boolean getEditTextData() {
        try {
            MixPanelController.track("EnterDetailsNext", null);
            data_businessName = businessNameEditText.getText().toString().trim();
            data_businessCategory = businessCategoryEditText.getText().toString().trim();
            data_city = cityEditText.getText().toString().trim();
            data_country = countryEditText.getText().toString().trim();
            data_email = emailEditText.getText().toString().trim();
//            data_phone = phoneEditText.getText().toString();
//            data_country_code = countryPhoneCode.getText().toString();
            allFieldsValid = true;
            if (data_businessName.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(businessNameEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_business_name));
            } else if (data_businessCategory.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(businessCategoryEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.select_business_category));
            } else if (data_city.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(cityEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_city));
            }else if(!citytext.trim().equals(data_city)){
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(cityEditText);
                Methods.showSnackBarNegative(activity, "Enter valid city");
            }else if (data_country.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(countryEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.select_country));
            } else if (etStreetAddress.getText().toString().trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(etStreetAddress);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_street_address));
            } else if (etPinCode.getText().toString().trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(etPinCode);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_pin_code));
            } else if (!(Signup_Validation.isValidEmail(emailEditText.getText().toString()))) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(emailEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_email));
            } else if (etWebsiteAddress.getText().toString().trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(etWebsiteAddress);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_website_name));
            } else if (TextUtils.isEmpty(fpTag)) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(etWebsiteAddress);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_website_name));
            } else if (phoneEditText.getText().toString().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(phoneEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_mobile_number));
            }
//            if (!Signup_Validation.isValidPhoneNumber(phoneEditText.getText().toString())) {
//                allFieldsValid = false;
//                YoYo.with(Techniques.Shake).playOn(phoneEditText);
//                Methods.showSnackBarNegative(activity, getString(R.string.enter_password_6to12_char));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allFieldsValid;
    }

    @Override
    public void onItemClick(String itemName) {

        if (businessCategory_Selected) {
            businessCategoryEditText.setText(itemName);
            businessCategory_Selected = false;
        } else if (country_Selected) {
            countryEditText.setText(itemName);
            country_Selected = false;
        }

    }

    @Override
    public void tagStatus(String status, String tag) {

        if (TextUtils.isEmpty(tag)) {
            ivWebsiteStatus.setBackgroundResource(R.drawable.warning);
        } else {
            fpTag = tag.toLowerCase();
            etWebsiteAddress.setText(fpTag);
            ivWebsiteStatus.setBackgroundResource(R.drawable.green_check);
        }
    }


    @Override
    public void CheckUniqueNumber_preExecute(String value) {
        showLoader(getString(R.string.checking_contact_number));
    }

    @Override
    public void CheckUniqueNumber_postExecute(String value) {

//        pd.dismiss();
//        if (value.equals("Success")) {
//
//            otpVerifyDialog(value);
//            numberDialog.dismiss();
//
//        } else if (value.equals("Failure")){
//            Methods.showSnackBarNegative(numberDialog.getView(), "Please enter another number");
//            goToNextScreen = false;
//        }

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

            numberDialog.dismiss();

            phoneEditText.setText("+" + data_country_code + " - " + phoneNumber);
            ivPhoneStatus.setImageResource(R.drawable.green_check);
            data_phone = phoneNumber;
        } else if (value.equalsIgnoreCase("Error")) {
            Methods.showSnackBarNegative(numberDialog.getView(), getString(R.string.something_went_wrong_try_again));
            goToNextScreen = false;
        } else {
            Methods.showSnackBarNegative(numberDialog.getView(), getString(R.string.number_already_exists));
            goToNextScreen = false;
        }
    }

    private String contactName = "";

    private HashMap<String, String> getJSONData() {
        HashMap<String, String> store = new HashMap<String, String>();
        try {
            store.put("clientId", Constants.clientId);
            store.put("tag", fpTag);
            store.put("contactName", contactName);
            store.put("name", data_businessName);
            store.put("desc", "");
            store.put("address", etStreetAddress.getText().toString());
            store.put("city", data_city);
            store.put("pincode", etPinCode.getText().toString());
            store.put("country", data_country);
            store.put("primaryNumber", data_phone);
            store.put("email", data_email);
            store.put("primaryNumberCountryCode", data_country_code);
            store.put("Uri", "");
            store.put("fbPageName", "");
            store.put("primaryCategory", data_businessCategory);
            store.put("lat", data_lat);
            store.put("lng", data_lng);
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

//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.clear();
//        editor.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, "91");
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void selectC() {
        final List<String> stringList = signUpCountryList;
        String[] countryList = new String[stringList.size()];
        countryList = stringList.toArray(countryList);

        //Another way showing the material dialog using custom Adapter

//        final CustomFilterableAdapter adapter = new CustomFilterableAdapter(
//                stringList, activity);
//        final MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .title("Choose Country")
//                .adapter(adapter)
//                .build();
//
//        ListView listView = dialog.getListView();
//        if (listView != null) {
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    //Toast.makeText(activity, "Clicked item " + position, Toast.LENGTH_SHORT).show();
//                    String strName = adapter.getItem(position);
//                    countryEditText.setText(strName);
//                    dialog.dismiss();
//                }
//            });
//        }
//
//        dialog.show();

        new MaterialDialog.Builder(activity)
                .title("Select Country")
                .items(countryList)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        countryEditText.setText(text);
                        //Log.v("ggg",text.toString());
                        try {
                            String country_code = Country_CodeMap.get(text.toString());
                            //Log.v("ggg",country_code);
                            String phone_code = Code_PhoneMap.get(country_code);
                            countryPhoneCode.setText("+" + phone_code);
                            sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, phone_code);
                        } catch (Exception e) {
                            //Log.v("ggg ",e.toString());
                        }
                        return false;
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.verify_button) {
            try {
                if (getEditTextData()) {
                    if (data_lat.equalsIgnoreCase("0")) {

                        LatLng latLng = new NFGeoCoder(PreSignUpActivityRia.this).reverseGeoCode(
                                etStreetAddress.getText().toString(), cityEditText.getText().toString(), countryEditText.getText().toString(),
                                etPinCode.getText().toString());
                        if(latLng != null) {
                            data_lat = latLng.latitude + "";
                            data_lng = latLng.longitude + "";
                        }
                    }
                    hideKeyBoard();
                    MixPanelController.track("CreateMyWebsite", null);
                    createStore_retrofit(PreSignUpActivityRia.this, getJSONData(), bus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(verify_button.getWindowToken(), 0);

    }

    public boolean updateBasedOnMostRecentLocation(Address address) {
        if (address != null) {
            String countryPhoneCodeString = sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE);
            String lastKnownCountry = address.getCountryName();
            String lastKnownState = address.getAdminArea();
            String lastKnownCity = address.getLocality();
            String lastKnownPin = address.getPostalCode();

            if (!Util.isNullOrEmpty(lastKnownCountry)) {
                countryEditText.setText(lastKnownCountry);
            }

            if (!Util.isNullOrEmpty(lastKnownCity)) {
                cityEditText.setText(lastKnownCity);
            }

            if (!Util.isNullOrEmpty(countryPhoneCodeString)) {
                data_country_code = countryPhoneCodeString;
                countryPhoneCode.setText("+" + countryPhoneCodeString);
                //countryPhoneCode.setTextColor((new PreSignUpActivity()).getResources().getColor(R.color.accentColor));
            }

            return true;
        } else {
            return false;
        }
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
//                    //Util.hideInput(getActivity());
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
            API_Layer_Signup.checkUniqueNumber(activity, data_phone);
        } else {
            Toast.makeText(activity, "Invalid Email. Please enter Again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void otpVerifyDialog(final String number) {
        //call send otp api
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_otp_verify, null);
        final EditText otp = (EditText) view.findViewById(R.id.editText);
        otpEditText = otp;
        TextView resend = (TextView) view.findViewById(R.id.resend_tv);
        resend.setPaintFlags(resend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend.setText(Methods.fromHtml(getString(R.string.resend)));
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) v).setTextColor(ContextCompat.getColor(PreSignUpActivityRia.this, R.color.gray_transparent));
                sendSms(number);
            }
        });
        otpDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .negativeText("Cancel")
                .autoDismiss(false)
                .titleColorRes(R.color.primary_color)
                .positiveText("Submit")
                .title("One Time Password")
                .canceledOnTouchOutside(false)
                .negativeColorRes(R.color.gray_transparent)
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
                }).show();

        final TextView positive = otpDialog.getActionButton(DialogAction.POSITIVE);
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
        sendSms(number);
    }

    private void sendSms(String number) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            startProgressDialog();
        }
        Methods.SmsApi smsApi = Constants.smsVerifyAdapter.create(Methods.SmsApi.class);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("via", "sms");
        hashMap.put("locale", sessionManager.getFPDetails(Key_Preferences.LANGUAGE_CODE));
        hashMap.put("phone_number", number);
        hashMap.put("country_code", sessionManager.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE));
        smsApi.sendSms(hashMap, new Callback<SmsVerifyModel>() {
            @Override
            public void success(SmsVerifyModel model, Response response) {
                if (model == null) {
                    stopProgressDialog();
                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getSuccess()) {

                } else {
                    stopProgressDialog();
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
        hashMap.put("verification_code", otpCode);
        hashMap.put("phone_number", number);
        hashMap.put("country_code", "+91");
        smsApi.verifySmsCode(hashMap, new Callback<SmsVerifyModel>() {
            @Override
            public void success(SmsVerifyModel model, Response response) {
                hideProgressbar();
                if (model == null) {
                    Toast.makeText(PreSignUpActivityRia.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getSuccess()) {
                    otpDialogDismiss();
                    phoneEditText.setText("+" + data_country_code + " - " + number);
                    ivPhoneStatus.setImageResource(R.drawable.green_check);
                    data_phone = number;
                } else {
                    hideProgressbar();
                    Toast.makeText(PreSignUpActivityRia.this, model.getMessage(), Toast.LENGTH_SHORT).show();
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
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

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
        if (ivWebsiteStatus.getVisibility() != View.VISIBLE){
            ivWebsiteStatus.setVisibility(View.VISIBLE);
        }
        ivWebsiteStatus.setBackgroundResource(R.drawable.green_check);
    }

    @Override
    public void onDomainNotAvailable() {
        this.fpTag = "";
        if(etWebsiteAddress.getText().toString().trim().length() == 0){
            ivWebsiteStatus.setVisibility(View.GONE);
        }else if (ivWebsiteStatus.getVisibility() != View.VISIBLE){
            ivWebsiteStatus.setVisibility(View.VISIBLE);
        }
        ivWebsiteStatus.setBackgroundResource(R.drawable.warning);
    }

    private void createStore_retrofit(PreSignUpActivityRia webSiteAddressActivity, HashMap<String, String> jsonData, Bus bus) {

        showLoader(getString(R.string.creating_website));
        new Create_Tag_Service(webSiteAddressActivity, jsonData, bus);
    }

    @Subscribe
    public void put_createStore(Create_Store_Event response) {
        final String fpId = response.fpId;
        hideLoader();
        if (TextUtils.isEmpty(fpId)){
            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
            return;
        }
        dataBase.insertLoginStatus(fpId);
        sessionManager = new UserSessionManager(getApplicationContext(), PreSignUpActivityRia.this);
        sessionManager.storeFPID(fpId);
        sessionManager.storeSourceClientId(Constants.clientId);
        //thinksity check
        if (Constants.clientId.equals(Constants.clientIdThinksity)) {
            sessionManager.storeIsThinksity("true");
        }

        // Give a Delay of 4 Seconds and Call this method
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over Start your app main activity
                getFPDetails(PreSignUpActivityRia.this, fpId, Constants.clientId, bus);
            }
        }, 5000);

        // Store it in Database
        // Store it in Shared pref
    }

    private void getFPDetails(PreSignUpActivityRia activity, String fpId, String clientId, Bus bus) {
        showLoader(getString(R.string.please_wait));
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
