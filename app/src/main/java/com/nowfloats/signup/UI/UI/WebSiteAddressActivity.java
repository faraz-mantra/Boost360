package com.nowfloats.signup.UI.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.signup.UI.API.Download_Facebook_Image;
import com.nowfloats.signup.UI.API.Signup_Descriptinon;
import com.nowfloats.signup.UI.Model.Create_Store_Event;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Create_Tag_Service;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.signup.UI.Service.Verify_Tag_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DataBase;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class WebSiteAddressActivity extends AppCompatActivity {
    public static ProgressDialog pd;
    Button createButton;
    boolean domainCheck, firstCheck = true;
    String data_businessName, data_businessCategory, data_city, data_country, data_email, data_phone, websiteTag;
    JSONObject businessDetails_jsonData;
    EditText webSiteTextView;
    CardView webSiteCardView;
    TextView label;
    Set<String> rTags, xTags;
    String aTag, mtag, aName, val, beforeEdit, intialValue;
    ImageView domainCheckStatus;
    CustomRunnable r;
    Handler handler;
    int regex;
    boolean addressTagValid = false;
    Bus bus;
    UserSessionManager session;
    AppCompatCheckBox termAndPolicyCheckbox;
    TextView termAndPolicyTextView;
    private Toolbar toolbar;
    private String contactName = "contact";
    private String countrycodeTag;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site_address);
        dataBase = new DataBase(WebSiteAddressActivity.this);
        createButton = findViewById(R.id.createButton);
        termAndPolicyTextView = findViewById(R.id.term_policy_textview);
        termAndPolicyCheckbox = findViewById(R.id.checkbox);
        webSiteTextView = findViewById(R.id.websiteTitleTextView);
        termAndPolicyCheckbox = findViewById(R.id.checkbox);
        webSiteCardView = findViewById(R.id.websiteTitleCardView);

        session = new UserSessionManager(getApplicationContext(), WebSiteAddressActivity.this);

        bus = BusProvider.getInstance().getBus();

//        webSiteCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Util.isNetworkStatusAvialable(WebSiteAddressActivity.this))
//                {
//                    domainCheck();
//
//                }
//                else{
//                    Toast.makeText(WebSiteAddressActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        label = findViewById(R.id.domainAvailable);
        domainCheckStatus = findViewById(R.id.domainCheckStatus);
        getEditTextBundle();
        if (Util.isNetworkStatusAvialable(WebSiteAddressActivity.this)) {
            domainCheck();
        } else {
            Toast.makeText(WebSiteAddressActivity.this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
        rTags = new HashSet<String>();
        xTags = new HashSet<String>();

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final float scale = this.getResources().getDisplayMetrics().density;
        termAndPolicyTextView.setText(Methods.fromHtml("By clicking Create My Website, you agree to our <a href=\"" + getString(R.string.settings_tou_url) + "\"><u>Terms and Conditions</u></a>  and that you have read our <a href=\"" + getString(R.string.settings_privacy_url) + "\"><u>Privacy Policy</u></a>."));
        termAndPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        termAndPolicyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createButton.setBackgroundColor(ContextCompat.getColor(WebSiteAddressActivity.this, R.color.primary_color));
                } else {
                    MixPanelController.track(MixPanelController.TERM_AND_POLICY_CHECKBOX, null);
                    createButton.setBackgroundColor(ContextCompat.getColor(WebSiteAddressActivity.this, R.color.gray_transparent));
                }
            }
        });
        webSiteTextView.addTextChangedListener(new TextWatcher() {
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
                domainCheck = true;
                if (Util.isNetworkStatusAvialable(WebSiteAddressActivity.this)) {
                    domainCheck();

                } else {
                    Toast.makeText(WebSiteAddressActivity.this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                }

                if (firstCheck) {
                    intialValue = webSiteTextView.getText().toString();
                }

            }
        });

        createButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (!termAndPolicyCheckbox.isChecked()) {
                        Toast.makeText(WebSiteAddressActivity.this, getString(R.string.you_must_agree_with_the_terms_and_conditions), Toast.LENGTH_LONG).show();
                    } else if (addressTagValid) {
                        MixPanelController.track("CreateMyWebsite", null);
                        createStore_retrofit(WebSiteAddressActivity.this, getJSONData(), bus);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void createStore_retrofit(WebSiteAddressActivity webSiteAddressActivity, HashMap<String, String> jsonData, Bus bus) {

        pd = ProgressDialog.show(WebSiteAddressActivity.this, "", getString(R.string.creating_website));
        pd.setCancelable(false);
        new Create_Tag_Service(webSiteAddressActivity, jsonData, bus);
    }

    @Subscribe
    public void put_createStore(Create_Store_Event response) {
        final String fpId = response.fpId;

        dataBase.insertLoginStatus(fpId);
        UserSessionManager session = new UserSessionManager(getApplicationContext(), WebSiteAddressActivity.this);
        session.storeFPID(fpId);
        session.storeSourceClientId(Constants.clientId);
        //thinksity check
        if (Constants.clientId.equals(Constants.clientIdThinksity)) {
            session.storeIsThinksity("true");
        }

        // Give a Delay of 4 Seconds and Call this method
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over Start your app main activity
                getFPDetails(WebSiteAddressActivity.this, fpId, Constants.clientId, bus);
            }
        }, 8000);

        // Store it in Database
        // Store it in Shared pref
    }

    private void getFPDetails(WebSiteAddressActivity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response) {
        // Close of Progress Bar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null) {
                    pd.dismiss();
                }
            }
        });
        //VISITOR and SUBSCRIBER COUNT API
        GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(WebSiteAddressActivity.this, session);
        visit_subcribersCountAsyncTask.execute();
        if (session.getIsSignUpFromFacebook().contains("true")) {
            if (session.getFacebookName() != null && session.getFacebookAccessToken() != null) {
                dataBase.updateFacebookNameandToken(session.getFacebookName(), session.getFacebookAccessToken());
            }
            if (session.getFacebookPage() != null && session.getFacebookPageID() != null && session.getPageAccessToken() != null) {
                dataBase.updateFacebookPage(session.getFacebookPage(), session.getFacebookPageID(), session.getPageAccessToken());
            }
            new Download_Facebook_Image().execute(session.getFacebookPageURL(), session.getFPID());
            Signup_Descriptinon descriptinon = new Signup_Descriptinon(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                    session.getFacebookProfileDescription(),
                    session.getFacebookPageID(),
                    response.model.FPWebWidgets
            );
            descriptinon.execute();
        }

        try {
            //        Intent webIntent = new Intent(WebSiteAddressActivity.this, HomeActivity.class);
            Intent webIntent = new Intent(WebSiteAddressActivity.this, Class.forName("com.dashboard.controller.DashboardActivity"));
            webIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle loginBundle = new Bundle();
            loginBundle.putBoolean("fromLogin", true);
            Constants.isWelcomScreenToBeShown = true;
            webIntent.putExtras(loginBundle);
            startActivity(webIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void getEditTextBundle() {
        try {
            Intent signUpIntent = getIntent();
            businessDetails_jsonData = new JSONObject();
            data_businessName = signUpIntent.getStringExtra("signup_business_name");
            data_businessCategory = signUpIntent.getStringExtra("signup_business_category");
            data_city = signUpIntent.getStringExtra("signup_city");
            data_country = signUpIntent.getStringExtra("signup_country");
            data_email = signUpIntent.getStringExtra("signup_email");
            data_phone = signUpIntent.getStringExtra("signup_phone");
            websiteTag = signUpIntent.getStringExtra("tag");
            countrycodeTag = signUpIntent.getStringExtra("signup_country_code");

            beforeEdit = websiteTag.toLowerCase();
            webSiteTextView.setText(websiteTag.toLowerCase());
            webSiteTextView.setSelection(webSiteTextView.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HashMap<String, String> getJSONData() {
        HashMap<String, String> store = new HashMap<String, String>();
        try {
            store.put("clientId", Constants.clientId);
            store.put("tag", websiteTag);
            store.put("contactName", contactName);
            store.put("name", data_businessName);
            store.put("desc", "");
            store.put("address", data_city);
            store.put("city", data_city);
            store.put("pincode", "");
            store.put("country", data_country);
            store.put("primaryNumber", data_phone);
            store.put("email", data_email);
            store.put("primaryNumberCountryCode", countrycodeTag);
            store.put("Uri", "");
            store.put("fbPageName", "");
            store.put("primaryCategory", data_businessCategory);
            store.put("lat", "0");
            store.put("lng", "0");
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

    public void createStore() {

        String error = uploadStore();
        if (error.equals("OK")) {
            //ok
        } else {
            AlertDialog.Builder builderInner = new AlertDialog.Builder(
                    this);
            builderInner.setMessage(getString(R.string.try_again) + error);
            builderInner.setTitle(getString(R.string.error));
            builderInner.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            dialog.dismiss();

                        }
                    });
            builderInner.show();
        }
        //	}

    }


    public void verifyTag(String tagname) {
        RequestQueue queue = Volley.newRequestQueue(WebSiteAddressActivity.this);
        String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/verifyUniqueTag";
        JSONObject obj = new JSONObject();
        try {
            obj.put("fpTag", tagname);
            obj.put("fpName", tagname);
            obj.put("clientId", Constants.clientId);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String mesg = error.getMessage();
                if (mesg != null && mesg.contains("org.json.JSONException: End of input at character 0 of")) {
                    // invalid tag
                    xTags.add(mtag);
                    label.setText(getString(R.string.chosen_website_not_available));
                    domainCheck = false;
                    addressTagValid = false;

                    domainCheckStatus.setVisibility(View.VISIBLE);
                    domainCheckStatus.clearColorFilter();
                    domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_not_available));
                } else if (mesg != null && mesg
                        .contains("type java.lang.String cannot be converted to JSONObject")) {
                    mesg = mesg.replace(
                            "org.json.JSONException: Value ", "");
                    mesg = mesg.replace(
                            " of type java.lang.String cannot be converted to JSONObject",
                            "");
                    mesg = mesg.trim();
                    rTags.add(mtag);
                    if (mtag.equals(mesg)) {
                        // valid tag
                        aTag = mesg;
                        websiteTag = mesg;
                        addressTagValid = true;

                        label.setText(getString(R.string.chosen_website_available));
                        domainCheck = false;
                        //domainCheckPD.setVisibility(View.GONE);
                        domainCheckStatus.setVisibility(View.VISIBLE);
                        domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_available));
                        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(WebSiteAddressActivity.this, R.color.primaryColor), PorterDuff.Mode.SRC_IN);
                        domainCheckStatus.setColorFilter(porterDuffColorFilter);

                    } else {
                        rTags.add(mesg);
                        aTag = mesg;
                        addressTagValid = false;
                        label.setText(getString(R.string.chosen_website_not_available));
                        domainCheck = false;
                        // domainCheckPD.setVisibility(View.GONE);
                        domainCheckStatus.setVisibility(View.VISIBLE);
                        domainCheckStatus.clearColorFilter();
                        domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_not_available));
                        //PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(WebSiteAddressActivity.this,R.color.red), PorterDuff.Mode.SRC_IN);

                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", Utils.getAuthToken());
                return headers;
            }
        };

        queue.add(jsObjRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public boolean validate(String text) {
        regex = R.string.signup_subd;
        return text.matches(WebSiteAddressActivity.this.getResources().getString(regex));
    }

    public void domainCheck() {
        if (firstCheck) {
            //domainCheckPD.setVisibility(View.VISIBLE);
            domainCheckStatus.setVisibility(View.GONE);
            label.setText("Checking if chosen website Address is Available.");
            if (handler == null) {
                handler = new Handler();
                r = new CustomRunnable(beforeEdit) {
                    public void run() {
                        val = webSiteTextView.getText().toString();
                        if (beforeEdit.equals(val)) {
                            if (validate(val)) {
                                String ttag = val.toUpperCase();
                                if (rTags.contains(ttag)) {
                                    aTag = ttag;
                                    label.setText(getString(R.string.chosen_website_available));
                                    domainCheck = false;
                                    // domainCheckPD.setVisibility(View.GONE);
                                    domainCheckStatus.setVisibility(View.VISIBLE);
                                    domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_available));
                                    PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(WebSiteAddressActivity.this, R.color.primaryColor), PorterDuff.Mode.SRC_IN);
                                    domainCheckStatus.setColorFilter(porterDuffColorFilter);

                                } else if (xTags.contains(ttag)) {
                                    // invalid tag
                                    mtag = ttag;
                                    label.setText(getString(R.string.chosen_website_not_available));
                                    domainCheck = false;
                                    //domainCheckPD.setVisibility(View.GONE);
                                    domainCheckStatus.setVisibility(View.VISIBLE);
                                    domainCheckStatus.clearColorFilter();
                                    domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_not_available));
                                } else {
                                    mtag = ttag;

                                    // verifyTag_RetroFit(WebSiteAddressActivity.this,mtag,bus);
                                    verifyTag(ttag);
                                }
                            } else {
                                label.setText(getString(R.string.enter_valid_website_name));
                                domainCheck = false;
                                //domainCheckPD.setVisibility(View.GONE);
                                domainCheckStatus.clearColorFilter();
                                domainCheckStatus.setVisibility(View.VISIBLE);
                                domainCheckStatus.setImageDrawable(ContextCompat.getDrawable(WebSiteAddressActivity.this, R.drawable.domain_not_available));
                            }
                        }

                    }
                };
                beforeEdit = webSiteTextView.getText().toString();
                handler.postDelayed(r, 1000);

            } else {
                handler.removeCallbacks(r);
                beforeEdit = webSiteTextView.getText().toString();
                handler.postDelayed(r, 1000);
            }
        }
    }

    private void verifyTag_RetroFit(WebSiteAddressActivity webSiteAddressActivity, String mtag, Bus bus) {
        new Verify_Tag_Service(webSiteAddressActivity, mtag, "", Constants.clientId, bus);
    }


//    @Subscribe
//    public void post_VerifyTag(Verifty_Unique_Tag_Event response)
//    {
//        fpTag = response.uniqueFpTag ;
//    }


    private String uploadStore() {

        try {
            String error = "";
            JSONObject store = new JSONObject();
            if (webSiteTextView != null) {
                store.put("tag", webSiteTextView.toString());
            }

            if (data_businessName != null) {
                store.put("name", data_businessName);
            }
            if (data_city != null) {
                store.put("city", data_city);
            }
            if (data_country != null) {
                store.put("country", data_country);
            }
            if (data_phone != null) {
                store.put("phone", data_phone);
            }
            if (data_email != null) {
                store.put("email", data_email);
            }

            if (data_businessCategory != null) {
                store.put("businessCategory", data_businessCategory);
            }

//            CreateStoreTask task = new CreateStoreTask(this,store);
//            //task.onObserverListener = this;
//              task.execute();


        } catch (Exception e) {
            return "error-csj001";
        }


        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_site_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }
}
