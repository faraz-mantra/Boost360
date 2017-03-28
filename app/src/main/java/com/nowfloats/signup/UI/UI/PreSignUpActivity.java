package com.nowfloats.signup.UI.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nowfloats.signup.UI.API.API_Layer;
import com.nowfloats.signup.UI.API.API_Layer_Signup;
import com.nowfloats.signup.UI.API.LoadCountryData;
import com.nowfloats.signup.UI.API.Valid_Email;
import com.nowfloats.signup.UI.Model.Email_Validation_Model;
import com.nowfloats.signup.UI.Model.Primary_Number_Event;
import com.nowfloats.signup.UI.Model.Suggest_Tag_Event;
import com.nowfloats.signup.UI.Model.ValidationEvent;
import com.nowfloats.signup.UI.Service.Primary_Number_Service;
import com.nowfloats.signup.UI.Service.Suggest_Tag_Service;
import com.nowfloats.signup.UI.Validation.Signup_Validation;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.HeaderText;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PreSignUpActivity extends AppCompatActivity implements
        PreSignUpDialog.Dialog_Activity_Interface,
        API_Layer_Signup.SignUp_Interface, View.OnClickListener,
        LoadCountryData.LoadCountryData_Interface,
        Valid_Email.Valid_Email_Interface
        , GoogleApiClient.OnConnectionFailedListener {
    static UserSessionManager sessionManager;
    private static final String TAG = "PreSignUp";
    public String[] cat = null;
    ListView l;
    // static List<String> countries;
    HeaderText title;
    ImageView forwardButton;
    Bus bus;
    public static ProgressBar cityProgress;
    private static EditText businessNameEditText, businessCategoryEditText, countryEditText, emailEditText, phoneEditText;
    private Toolbar toolbar;
    public static AutoCompleteTextView cityEditText;
    private static TextView countryPhoneCode;
    String data_businessName, data_businessCategory, data_city, data_country, data_email, data_phone, data_country_code;

    ImageView bizzName, bizzCat, bizCity, bizzCtry, bizzEmail, bizPh;


    private boolean businessCategory_Selected = false;
    SharedPreferences sharedpreferences;
    private boolean country_Selected = false;
    //  private String[] businessCategoryList;

    ProgressDialog pd;
    boolean allFieldsValid = true;
    private boolean goToNextScreen = false;
    private String tagName;
    String fpTag;
    private double lng, lat;
    private LatLng latlong;

    public static final String MyPREFERENCES = "PrefsSignUp";
    public static final String Save_Name = "nameKey";
    public static final String Save_Cat = "categoryKey";
    public static final String Save_Phone = "phoneKey";
    public static final String Save_Email = "emailKey";
    public static final String Save_Country = "countryKey";
    public static final String Save_City = "cityKey";

    public static Email_Validation_Model emailModel;
    String facebookPageURL;

    HashMap<String, String> Country_CodeMap, Code_PhoneMap;
    private String Validate_Email_API_KEY = "e5f5fb5a-8e1f-422e-9d25-a67a16018d47";
    public Activity activity;


    AutocompleteFilter filter;
    private List<String> citys =new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> signUpCountryList=new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_sign_up_trial_3);
        activity = PreSignUpActivity.this;
        Methods.isOnline(activity);
        bus = BusProvider.getInstance().getBus();
        businessNameEditText = (EditText) findViewById(R.id.editText_businessName);
        businessCategoryEditText = (EditText) findViewById(R.id.editText_businessCategory);
        cityEditText = (AutoCompleteTextView) findViewById(R.id.editText_city);
        countryEditText = (EditText) findViewById(R.id.editText_Country);
        emailEditText = (EditText) findViewById(R.id.editText_Email);
        phoneEditText = (EditText) findViewById(R.id.editText_Phone);
        forwardButton = (ImageView) findViewById(R.id.forward_button_signup_screen);
        countryPhoneCode = (TextView) findViewById(R.id.countrycode_signupscreen);
        cityProgress = (ProgressBar) findViewById(R.id.city_progressbar);
        cityProgress.setVisibility(View.GONE);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,citys);
        cityEditText.setAdapter(adapter);


        sessionManager = new UserSessionManager(activity, activity);
        Typeface robotoLight = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");

        businessNameEditText.setTypeface(robotoLight);
        businessCategoryEditText.setTypeface(robotoLight);
        cityEditText.setTypeface(robotoLight);
        countryEditText.setTypeface(robotoLight);
        emailEditText.setTypeface(robotoLight);
        phoneEditText.setTypeface(robotoLight);
        countryPhoneCode.setTypeface(robotoLight);

        Util.addBackgroundImages();
        /*LoadCountryData countryData = new LoadCountryData(activity);
        countryData.LoadCountryData_Listener(this);
        countryData.execute();*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadCountryCodeandCountryNameMap();
            }
        }).start();

        bizzName = (ImageView) findViewById(R.id.businessNameImageView);
        bizzCat = (ImageView) findViewById(R.id.businessCategoryImageView);
        bizzEmail = (ImageView) findViewById(R.id.emailImageView);
        bizCity = (ImageView) findViewById(R.id.cityImageView);
        bizPh = (ImageView) findViewById(R.id.phoneImageView);
        bizzCtry = (ImageView) findViewById(R.id.countryImageView);

        PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        bizzName.setColorFilter(whiteLabelFilter);
        bizzCat.setColorFilter(whiteLabelFilter);
        bizzEmail.setColorFilter(whiteLabelFilter);
        bizCity.setColorFilter(whiteLabelFilter);
        bizPh.setColorFilter(whiteLabelFilter);
        bizzCtry.setColorFilter(whiteLabelFilter);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        forwardButton.setOnClickListener(this);

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

                String country_code = null;
                if(Country_CodeMap!=null) {
                    country_code = Country_CodeMap.get(countryEditText.getText().toString());
                }
                makeAutoCompleteFilter(country_code);

                final PendingResult<AutocompletePredictionBuffer> result =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, cityEditText.getText().toString().trim(),
                                null, filter );

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AutocompletePredictionBuffer a=result.await();
                        //Log.v("ggg","ok");
                        citys.clear();
                        for (int i=0;i<a.getCount();i++){
                            //Log.v("ggg",a.get(i).getFullText(new StyleSpan(Typeface.NORMAL)).toString()+" length "+citys.size());
                            citys.add(a.get(i).getPrimaryText(new StyleSpan(Typeface.NORMAL)).toString());
                        }

                        a.release();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new ArrayAdapter<>(PreSignUpActivity.this,
                                        android.R.layout.simple_dropdown_item_1line, citys);
                                if (!isFinishing()) {
                                    cityEditText.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void afterTextChanged(Editable s) {
                //ArrayList<String> citys=new ArrayList<String>();
                //cityEditText.setAdapter(null);

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

        countryEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectC();
                //Util.hideInput(getActivity());
            }
        });

       /* cityEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = cityEditText.getText().toString().trim();
                String city = s.split("\\,")[0];
                String country = s.split("\\,")[1];
                cityEditText.setText(city);
                countryEditText.setText(country);
                try {
                    String country_code = Country_CodeMap.get(country.toString());
                    String phone_code = Code_PhoneMap.get(country_code);
                    countryPhoneCode.setText("+" + phone_code);
                    sessionManager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE, phone_code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        businessCategoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessCategory_Selected = true;
                country_Selected = false;

                new FetchCategory().execute();


                /*if (Constants.storeBusinessCategories == null) {
                    Constants.storeBusinessCategories = API_Layer.getBusinessCategories(activity);
                } else {

                    new MaterialDialog.Builder(activity)
                            .title("Select a Category")
                            .items(Constants.storeBusinessCategories)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    businessCategoryEditText.setText(text);

                                    Util.changeDefaultBackgroundImage(text.toString());
                                    return false;
                                }
                            })
                            .show();
                }*/
            }
        });
        updateBasedOnMostRecentLocation(Constants.lastKnownAddress);
    }
    private void makeAutoCompleteFilter(String country_code){

        filter =null;
        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);

        if(country_code!=null){
            builder.setCountry(country_code.toUpperCase());
        }
        filter= builder.build();

    }
    private void validateEmail(PreSignUpActivity preSignUpActivity, String email, String apiKey, final Bus bus) {
        // Open Progress Dialog
//        MaterialProgressBar.startProgressBar(preSignUpActivity,"Validating Data",false);
//        new Email_Service(preSignUpActivity,email,apiKey,bus);
//        verifyUniqueNumber(PreSignUpActivity.this,data_phone,bus);
        String tagName = API_Layer_Signup.getTag(activity, data_businessName, data_country, data_city, data_businessCategory);
        fpTag = tagName;
        API_Layer_Signup.checkUniqueNumber(activity, data_phone);
    }

    private void verifyUniqueNumber(PreSignUpActivity preSignUpActivity, String mobileNumber, final Bus bus) {
        new Primary_Number_Service(preSignUpActivity, Constants.clientId, mobileNumber, bus);
    }

    private void suggestTag(PreSignUpActivity preSignUpActivity, String data_businessName, String data_country, String data_city, String data_businessCategory, Bus bus) {

        new Suggest_Tag_Service(preSignUpActivity, data_businessName, data_country, data_city, data_businessCategory, Constants.clientId, bus);
    }


    @Subscribe
    public void get_IsValidEmail(ValidationEvent response) {
        emailModel = (Email_Validation_Model) response.model;

        String value = emailModel.status;
//         value = "valid";

        if (value.equals("valid")) {
            verifyUniqueNumber(PreSignUpActivity.this, data_phone, bus);


            //String tagName = API_Layer_Signup.getTag(activity, data_businessName, data_country, data_city, data_businessCategory);
            //API_Layer_Signup.checkUniqueNumber(activity, data_phone);

        } else {
            MaterialProgressBar.dismissProgressBar();
            Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_email));
            //Toast.makeText(activity, "Invalid Email. Please enter Again", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void post_verifyUniqueNumber(Primary_Number_Event response) {

        boolean isNumberUnique = response.model;

        if (!isNumberUnique) {


            suggestTag(PreSignUpActivity.this, data_businessName, data_country, data_city, data_businessCategory, bus);


        } else {
            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.number_already_registered))
                    .content(getString(R.string.number_already_registered_enter_different))
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE)
                    .input(getString(R.string.enter_mobile_number), null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            phoneEditText.setText(input.toString());
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


    @Subscribe
    public void post_SuggestTag(Suggest_Tag_Event response) {
        fpTag = response.fpTag;

        // Close Progress Dialog

        MaterialProgressBar.dismissProgressBar();

        Bundle editTextBundle = new Bundle();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Save_Name, data_businessName);
        editor.putString(Save_Cat, data_businessCategory);
        editor.putString(Save_City, data_city);
        editor.putString(Save_Country, data_country);
        editor.putString(Save_Email, data_email);
        editor.putString(Save_Phone, data_phone);


        editTextBundle.putString("signup_business_name", data_businessName);
        editTextBundle.putString("signup_business_category", data_businessCategory);
        editTextBundle.putString("signup_city", data_city);
        editTextBundle.putString("signup_country", data_country);
        editTextBundle.putString("signup_email", data_email);
        editTextBundle.putString("signup_phone", data_phone);
        editTextBundle.putString("signup_country_code", countryPhoneCode.getText().toString());
        editTextBundle.putString("tag", fpTag);
//            if(sessionManager.getIsSignUpFromFacebook().contains("true"))
//            {
//                editTextBundle.putS
//            }
        editor.apply();
        String setLatLong = data_city.replaceAll(" ", "+") + "," + data_country.replaceAll(" ", "+");
        setLatLong.replaceAll(" ", "+");

        setLatLong(setLatLong);
        Intent webSiteCreationIntent = new Intent(activity, WebSiteAddressActivity.class);
        webSiteCreationIntent.putExtras(editTextBundle);
        startActivity(webSiteCreationIntent);
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
        Collections.sort(signUpCountryList);
        if(isFinishing()) return;
        String[] string_array = getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < string_array.length; i++) {
            String phoneCode = string_array[i].split(",")[0];
            String countryCode = string_array[i].split(",")[1];
            Code_PhoneMap.put(countryCode, phoneCode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);

        if (sharedpreferences.contains(Save_Name)) {
            businessNameEditText.setText(sharedpreferences.getString(Save_Name, ""));

        }

        if (sharedpreferences.contains(Save_Cat)) {
            businessCategoryEditText.setText(sharedpreferences.getString(Save_Cat, ""));

        }
        if (sharedpreferences.contains(Save_Country)) {
            countryEditText.setText(sharedpreferences.getString(Save_Country, ""));
        }
        if (sharedpreferences.contains(Save_City)) {
            cityEditText.setText(sharedpreferences.getString(Save_City, ""));

        }
        if (sharedpreferences.contains(Save_Email)) {
            emailEditText.setText(sharedpreferences.getString(Save_Email, ""));

        }
        if (sharedpreferences.contains(Save_Phone)) {
            phoneEditText.setText(sharedpreferences.getString(Save_Phone, ""));
        }
    }

    private boolean getEditTextData() {
        try {
            MixPanelController.track("EnterDetailsNext", null);
            data_businessName = businessNameEditText.getText().toString();
            data_businessCategory = businessCategoryEditText.getText().toString();
            data_city = cityEditText.getText().toString();
            data_country = countryEditText.getText().toString();
            data_email = emailEditText.getText().toString();
            data_phone = phoneEditText.getText().toString();
            allFieldsValid = true;
            if (data_businessName.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(businessNameEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_business_name));
            }
            if (data_businessCategory.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(businessCategoryEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.select_business_category));
            }
            if (data_city.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(cityEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_city));
            }
            if (data_country.trim().length() == 0) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(countryEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.select_country));
            }
            if (!(Signup_Validation.isValidEmail(emailEditText.getText().toString()))) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(emailEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_valid_email));
            }
            if (!Signup_Validation.isValidPhoneNumber(data_phone)) {
                allFieldsValid = false;
                YoYo.with(Techniques.Shake).playOn(phoneEditText);
                Methods.showSnackBarNegative(activity, getString(R.string.enter_password_6to12_char));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allFieldsValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }
        else if (id == R.id.forward_button_signup_screen) {

            getEditTextData();

            Bundle editTextBundle = new Bundle();

//            String tagName = API_Layer_Signup.getTag(activity, data_businessName, data_country, data_city, data_businessCategory);
//            API_Layer_Signup.checkUniqueNumber(activity, data_phone);

            //   suggestTag(activity,data_businessName, data_country, data_city, data_businessCategory,bus);


            editTextBundle.putString("signup_business_name", data_businessName);
            editTextBundle.putString("signup_business_category", data_businessCategory);
            editTextBundle.putString("signup_city", data_city);
            editTextBundle.putString("signup_country", data_country);
            editTextBundle.putString("signup_email", data_email);
            editTextBundle.putString("signup_phone", data_phone);
            editTextBundle.putString("tag", fpTag);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
        fpTag = tag;
    }


    @Override
    public void CheckUniqueNumber_preExecute(String value) {
        pd = ProgressDialog.show(activity, null, getString(R.string.checking_contact_number));
        pd.setCancelable(true);
    }

    @Override
    public void CheckUniqueNumber_postExecute(String value) {

        pd.dismiss();

        if (value.equals("Success")) {
            // PreSignUpDialog.showDialog_WebSiteCreation(activity,"Valid Phone number. . .","Congrats . . .");
            //goToNextScreen = true ;

            Bundle editTextBundle = new Bundle();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Save_Name, data_businessName);
            editor.putString(Save_Cat, data_businessCategory);
            editor.putString(Save_City, data_city);
            editor.putString(Save_Country, data_country);
            editor.putString(Save_Email, data_email);
            editor.putString(Save_Phone, data_phone);

            editTextBundle.putString("signup_business_name", data_businessName);
            editTextBundle.putString("signup_business_category", data_businessCategory);
            editTextBundle.putString("signup_city", data_city);
            editTextBundle.putString("signup_country", data_country);
            editTextBundle.putString("signup_email", data_email);
            editTextBundle.putString("signup_phone", data_phone);
            editTextBundle.putString("signup_country_code", countryPhoneCode.getText().toString());
            editTextBundle.putString("tag", fpTag);
            editor.commit();
            String setLatLong = data_city.replaceAll(" ", "+") + "," + data_country.replaceAll(" ", "+");
            setLatLong.replaceAll(" ", "+");

            setLatLong(setLatLong);
            Intent webSiteCreationIntent = new Intent(activity, WebSiteAddressActivity.class);
            webSiteCreationIntent.putExtras(editTextBundle);
            startActivity(webSiteCreationIntent);


        } else if (value.equals("Failure")) {
            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(this)
                    .customView(R.layout.basic_dialog_layout, wrapInScrollView)
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            final EditText text = (EditText) dialog.findViewById(R.id.editContactNumber);
                            phoneEditText.setText(text.getText().toString());
                        }
                    })
                    .build()
                    .show();

            //PreSignUpDialog.showDialog_WebSiteCreation(activity,"Number Already Registered. . .","Try Again . . .");
            goToNextScreen = false;
        }
        // pd.dismiss();
        // PreSignUpDialog.showDialog_WebSiteCreation(activity,"Congrats . . . ","Web Site Creation");

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
        if (v.getId() == R.id.forward_button_signup_screen) {
            try {
                if (getEditTextData()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(forwardButton.getWindowToken(), 0);
                    validateEmail(PreSignUpActivity.this, emailEditText.getText().toString(), Validate_Email_API_KEY, bus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updateBasedOnMostRecentLocation(Address address) {
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

        if (result.equals("failure")) {
            countryEditText.setEnabled(true);
            countryEditText.setOnClickListener(null);
            countryEditText.setText("Canada");
        } else if (result.equals("success")) {
            countryEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectC();
                    //Util.hideInput(getActivity());

                }
            });
        }

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
            Toast.makeText(activity, "Invalid Email. Please enter again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class FetchCategory extends AsyncTask<String, Void, String>{

        ProgressDialog pd = null;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(PreSignUpActivity.this, "", "Wait While Loading Categories...");
            //return
        }

        @Override
        protected String doInBackground(String... params) {
            API_Layer.getBusinessCategories(PreSignUpActivity.this);
            return null;
        }



        @Override
        protected void onPostExecute(String result) {
        //Log.v("Test",Constants.storeBusinessCategories.length+" length");
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            new MaterialDialog.Builder(activity)
                    .title("Select a Category")
                    .items(Constants.storeBusinessCategories)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            businessCategoryEditText.setText(text);

                            Util.changeDefaultBackgroundImage(text.toString());
                            return false;
                        }
                    })
                    .show();
            //return
        }
    }

}
