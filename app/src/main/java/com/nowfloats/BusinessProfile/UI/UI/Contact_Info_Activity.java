package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nowfloats.BusinessProfile.UI.API.Business_Info_Upload_Service;
import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contact_Info_Activity extends ActionBarActivity implements View.OnTouchListener {
    private Toolbar toolbar;
    public static TextView saveTextView;
    UserSessionManager session;
    Bus bus;
    ImageView ivProtoColSpinner;

    private String[] mProtoCols = {"http://", "https://"};

    public static EditText primaryNumber, alternateNumber_1,alternateNumber_2, alternateNumber_3, emailAddress,websiteAddress,facebookPage ;
    Boolean flag4emailaddress = false, flag4websiteaddress = false, flag4fbagename = false,flag4alternate1 = false, flag4alternate3=false, flag4primaryno = false,flag4alternate2 = false,flag4digitlimit0=false,flag4digitlimit1=false,flag4digitlimit=false,flag4digitlimit2=false;
    public static String msgtxt4_email = null, msgtxt4website = null,msgtxt4fbpage = null,msgtxt4primaryno = null, msgtxt4alternateno1 = null,msgtxtalternate2 = null, msgtxtalternate3=null;
    String[] profilesattr =new String[20];
    private TextView titleTextView;
    public static String primary="",alternate1="",alternate2="", alternate3="";
    private boolean allBoundaryCondtn = true;
    Spinner protocolSpinner;
    private boolean VMN_Dialog;
    private int mSelectionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Methods.isOnline(Contact_Info_Activity.this);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);

        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(getApplicationContext(),Contact_Info_Activity.this);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.contact__info));

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.SAVE_CONTACT_INFO,null);
                uploadContactInfo();
            }
        });

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        primaryNumber = (EditText) findViewById(R.id.primaryNumber);
        primaryNumber.setInputType(InputType.TYPE_NULL);
        alternateNumber_1 = (EditText) findViewById(R.id.alternateNumber_1);
        alternateNumber_2 = (EditText) findViewById(R.id.alternateNumber_2);
        alternateNumber_3 = (EditText) findViewById(R.id.alternateNumber_3);
        emailAddress = (EditText) findViewById(R.id.contactInfo_emailId);
        websiteAddress = (EditText) findViewById(R.id.websiteAddress);
        facebookPage = (EditText) findViewById(R.id.facebookPage);

        protocolSpinner = (Spinner) findViewById(R.id.sp_web_address);
        protocolSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.protocol_spinner_bg, mProtoCols));
        initializeData();
        facebookPage.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FBPAGENAME));

        ivProtoColSpinner = (ImageView) findViewById(R.id.iv_protocol_spinner);
        ivProtoColSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                protocolSpinner.performClick();
            }
        });

        protocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSelectionCounter+=1;
                    msgtxt4website = websiteAddress.getText().toString().trim();

                    int len = msgtxt4website.length();
                    if (len > 0 && mSelectionCounter>1) {

                        flag4websiteaddress= true;
                        saveTextView.setVisibility(View.VISIBLE);

                    } else {

                        saveTextView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        emailAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {

                    msgtxt4_email = emailAddress.getText().toString().trim();

                    int len = msgtxt4_email.length();
                    if (len > 0) {

                        flag4emailaddress = true;
                        saveTextView.setVisibility(View.VISIBLE);


                    } else {

                        saveTextView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    emailAddress.setText(result);
                    emailAddress.setSelection(result.length());
                    // alert the user
                }


            }
        });

        websiteAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                try {

                    msgtxt4website = websiteAddress.getText().toString().trim();

                    int len = msgtxt4website.length();
                    if (len > 0) {

                        flag4websiteaddress= true;
                        saveTextView.setVisibility(View.VISIBLE);

                    } else {

                        saveTextView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    websiteAddress.setText(result);
                    websiteAddress.setSelection(result.length());
                    // alert the user
                }
                if(s.length()==0){
                    saveTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        facebookPage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {

                    msgtxt4fbpage = facebookPage.getText().toString()
                            .trim();
                    int len = msgtxt4fbpage.length();
                    if (len > 0) {

                        flag4fbagename = true;

                        saveTextView.setVisibility(View.VISIBLE);


                    } else {

                        saveTextView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        primaryNumber.setOnTouchListener(this);
       /* primaryNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {

                    msgtxt4primaryno = primaryNumber.getText()
                            .toString().trim();
                    int len = msgtxt4primaryno.length();
                    if (len >= 0) {

                        flag4primaryno = true;
                        saveTextView.setVisibility(View.VISIBLE);

                    } else {

                        saveTextView.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    websiteAddress.setText(result);
                    websiteAddress.setSelection(result.length());
                    // alert the user
                }

            }
        });*/


        alternateNumber_1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String msg_txt = "";

                try {


                    msgtxt4alternateno1 = alternateNumber_1.getText()
                            .toString().trim();
                    int len = msgtxt4alternateno1.length();
                    if (len >= 0) {
                        if(len==0){

                        saveTextView.setVisibility(View.GONE);

                        }else{

                            saveTextView.setVisibility(View.VISIBLE);

                        }

                        flag4alternate1 = true;
                        saveTextView.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        alternateNumber_2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String msg_txt = "";

                try {


                    msgtxtalternate2 = alternateNumber_2.getText()
                            .toString().trim();
                    int len = msgtxtalternate2.length();
                    if (len >= 0) {
                        if(len==0){

                            saveTextView.setVisibility(View.GONE);
                        }else {

                            saveTextView.setVisibility(View.VISIBLE);
                        }
                         flag4alternate2 = true;

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        alternateNumber_3.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String msg_txt = "";

                try {


                    msgtxtalternate3 = alternateNumber_3.getText()
                            .toString().trim();
                    int len = msgtxtalternate3.length();
                    if (len >= 0) {
                        if(len==0){

                            saveTextView.setVisibility(View.GONE);
                        }else {

                            saveTextView.setVisibility(View.VISIBLE);
                        }
                        flag4alternate3 = true;

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }



    public void uploadContactDetails_retrofit(Contact_Info_Activity activity, JSONObject jsonObject, Bus bus)
    {
        new Business_Info_Upload_Service(activity,jsonObject,bus);
    }

    @Subscribe
    public void post_updateBusinessDetails(ArrayList<String> response)
    {
        ArrayList<String> value = response ;
    }

    public HashMap<String,String> getUploadDetails_PartOne()
    {
        HashMap<String, String> uploadInfo = new HashMap<String, String>();

        uploadInfo.put("clientId",Constants.clientId);
        uploadInfo.put("fpTag",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));

        return uploadInfo;
    }

    public HashMap<String,ArrayList> getUploadDetails_PartTwo()
    {
        HashMap<String, ArrayList> uploadInfo = new HashMap<String, ArrayList>();

        HashMap<String,String> details = new HashMap<String,String>();

        ArrayList<HashMap<String,String>> list = new ArrayList<>();

        if (flag4websiteaddress) {
                details.put("URL",msgtxt4website);
        }
        if (flag4fbagename) {
            details.put("FB",msgtxt4fbpage);
        }
        if (flag4emailaddress) {
           details.put("EMAIL",msgtxt4_email);
        }
        if(flag4primaryno)
        {
            details.put("CONTACTS",primary+"#"+alternate1+"#"+alternate2);
        }

        list.add(details);

        uploadInfo.put("updates",list);

        return uploadInfo;
    }

    public JSONObject dataToUpload()
    {
        JSONObject offerObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject  obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();
        JSONObject obj4 = new JSONObject();
        JSONObject obj5 = new JSONObject();

        if (flag4websiteaddress) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(websiteAddress.getWindowToken(), 0);

            if(isValidWebsite(msgtxt4website)){
                try {
                    obj1.put("key", "URL");
                    obj1.put("value", msgtxt4website + protocolSpinner.getSelectedItem());
                } catch (Exception ex) {
                    System.out.println();
                }
                ja.put(obj1);
               }
            else
            {
                Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_valid_website));
                // Util.("Enter Valid Website", this);
                allBoundaryCondtn=false;
            }
        }

        if (flag4fbagename) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(facebookPage.getWindowToken(), 0);

            try {
                obj2.put("key", "FB");
                obj2.put("value", msgtxt4fbpage);

                String webWidgets = null;
                for(int i = 0 ; i < Constants.StoreWidgets.size();i++)
                {
                    webWidgets = Constants.StoreWidgets.get(i).toString();
                    webWidgets += "#";
                }
                webWidgets += "#FBLIKEBOX";

                obj5.put("key","WEBWIDGETS");
                obj5.put("value",webWidgets);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj2);
            ja.put(obj5);
         }

        if (flag4emailaddress) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(emailAddress.getWindowToken(), 0);

            boolean emailValid=isValidEmail(msgtxt4_email);
            if(emailValid){
                try {
                    obj3.put("key", "EMAIL");
                    obj3.put("value", msgtxt4_email);
                } catch (Exception ex) {
                    System.out.println();
                }
                ja.put(obj3);


            }
            else{
                Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_valid_email));
                allBoundaryCondtn=false;
            }
        }

        if(flag4primaryno || flag4alternate2 || flag4alternate1 || flag4alternate3){
            try {

                if (flag4primaryno) {
                    primary = msgtxt4primaryno;
                    if(primary.length()==0 )
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.primary_num_can_not_empty));

                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3 = false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                    if(msgtxt4primaryno.length()>0 && msgtxt4primaryno.length()<=6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3 = false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String primaryNumber = session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM);
                    if (primaryNumber != null) {
                        int size = primaryNumber.length();
                        if (size != 0) {
                            if (!Util.isNullOrEmpty(primaryNumber))
                                primary = primaryNumber;
                        }
                    }
                }

                if (flag4alternate1) {
                    alternate1 = (msgtxt4alternateno1.length() == 0) ? ""
                            : msgtxt4alternateno1;
                    if(msgtxt4alternateno1.length()>0 && msgtxt4alternateno1.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));

                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3 = false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate1Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER);
                    if (alternate1Number != null) {
                        int size = alternate1Number.length();
                        if (size > 1) {
                            if (!Util.isNullOrEmpty(alternate1Number)) {
                                alternate1 = alternate1Number;
                            }
                        }
                    }
                }

                if (flag4alternate2) {
                    alternate2 = (msgtxtalternate2.length() == 0) ? ""
                            : msgtxtalternate2;
                    if(msgtxtalternate2.length()>0 && msgtxtalternate2.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3 = false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate2Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1);
                    if (alternate2Number != null) {
                        int size = alternate2Number.length();
                        if (size > 2) {
                            if (!Util.isNullOrEmpty(alternate2Number))
                                alternate2 = alternate2Number;
                        }
                    }
                }

                if (flag4alternate3) {
                    alternate3 = (msgtxtalternate2.length() == 0) ? ""
                            : msgtxtalternate3;
                    if(msgtxtalternate3.length()>0 && msgtxtalternate3.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        allBoundaryCondtn=false;
                        flag4alternate3 = false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate3Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3);
                    if (alternate3Number != null) {
                        int size = alternate3Number.length();
                        if (size > 2) {
                            if (!Util.isNullOrEmpty(alternate3Number))
                                alternate3 = alternate3Number;
                        }
                    }
                }

                obj4.put("key", "CONTACTS");
                obj4.put("value", alternate1+"#"+alternate2+"#"+alternate3);

            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj4);


        }

        try {

            offerObj.put("clientId", Constants.clientId);
            offerObj.put("fpTag", (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)).toUpperCase());
            offerObj.put("updates", ja);
        } catch (Exception ex) {

        }

        return offerObj;
    }


    public void uploadContactInfo_version2(){

    }

    public void uploadContactInfo() {
        Constants.StoreFbPage = msgtxt4fbpage ;
        int i=0;
        JSONObject offerObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject  obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();
        JSONObject obj4 = new JSONObject();
        JSONObject obj5 = new JSONObject();

        if (flag4websiteaddress) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(websiteAddress.getWindowToken(), 0);

            if(isValidWebsite(msgtxt4website)){
                try {
                    obj1.put("key", "URL");
                    obj1.put("value", protocolSpinner.getSelectedItem() + msgtxt4website);
                      session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE,protocolSpinner.getSelectedItem() + msgtxt4website);

                } catch (Exception ex) {
                    System.out.println();
                }
                ja.put(obj1);
                profilesattr[i] = "URL";
                i++;}
            else
            {
                YoYo.with(Techniques.Shake).playOn(websiteAddress);
                Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_valid_website));
                // Util.("Enter Valid Website", this);
                allBoundaryCondtn=false;
            }
        }

        if (flag4fbagename) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(facebookPage.getWindowToken(), 0);

            try {
                obj2.put("key", "FB");
                obj2.put("value", session.getFacebookPageID());

                String webWidgets = "";
                for(int j = 0 ; j < Constants.StoreWidgets.size();j++)
                {
                    webWidgets += Constants.StoreWidgets.get(j).toString()+"#";

                }
                webWidgets += "FBLIKEBOX";

                obj5.put("key","WEBWIDGETS");
                obj5.put("value",webWidgets);
                //                    primaryNumber.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
//                    alternateNumber_1.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1));
//                    alternateNumber_2.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_2));
//                    emailAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
//                    websiteAddress.setText(session.getFPDetails(""));
//                    facebookPage.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_FACEBOOK_PAGE));

         session.storeFacebookPage(msgtxt4fbpage);
            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj2);
            ja.put(obj5);

            profilesattr[i] = "FB";
            i++;
        }

        if (flag4emailaddress) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(emailAddress.getWindowToken(), 0);

            boolean emailValid=isValidEmail(msgtxt4_email);
            if(emailValid){
                try {
                    obj3.put("key", "EMAIL");
                    obj3.put("value", msgtxt4_email);
                    session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL,msgtxt4_email);

                } catch (Exception ex) {
                    System.out.println();
                }
                ja.put(obj3);
                profilesattr[i] = "EMAIL";
                i++;

            }
            else{
                YoYo.with(Techniques.Shake).playOn(emailAddress);
                Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_valid_email));
                allBoundaryCondtn=false;
            }
        }

        if(flag4primaryno || flag4alternate2 || flag4alternate1 || flag4alternate3){
            try {

                if (flag4primaryno) {
                    primary = msgtxt4primaryno;
                    if(primary.length()==0 )
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.primary_num_can_not_empty));

                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate3 = false;
                        flag4alternate2=false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                    if(msgtxt4primaryno.length()>0 && msgtxt4primaryno.length()<=6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3 = false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String primaryNumber = session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM);
                    if (primaryNumber != null) {
                        int size = primaryNumber.length();
                        if (size != 0) {
                            if (!Util.isNullOrEmpty(primaryNumber))
                                primary = primaryNumber;
                        }
                    }
                }

                if (flag4alternate1) {
                    alternate1 = (msgtxt4alternateno1.length() == 0) ? ""
                            : msgtxt4alternateno1;
                    if(msgtxt4alternateno1.length()>0 && msgtxt4alternateno1.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));

                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3=false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate1Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER);
                    if (alternate1Number != null) {
                        int size = alternate1Number.length();
                        if (size > 1) {
                            if (!Util.isNullOrEmpty(alternate1Number)) {
                                alternate1 = alternate1Number;
                            }
                        }
                    }
                }

                if (flag4alternate2) {
                    alternate2 = (msgtxtalternate2.length() == 0) ? ""
                            : msgtxtalternate2;
                    if(msgtxtalternate2.length()>0 && msgtxtalternate2.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        flag4alternate3=false;
                        allBoundaryCondtn=false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate2Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1);
                    if (alternate2Number != null) {
                        int size = alternate2Number.length();
                        if (size > 2) {
                            if (!Util.isNullOrEmpty(alternate2Number))
                                alternate2 = alternate2Number;
                        }
                    }
                }

                if (flag4alternate3) {
                    alternate3 = (msgtxtalternate3.length() == 0) ? ""
                            : msgtxtalternate3;
                    if(msgtxtalternate3.length()>0 && msgtxtalternate3.length()<6)
                    {
                        Methods.showSnackBarNegative(this,getResources().getString(R.string.enter_password_6to12_char));
                        flag4primaryno=false;
                        flag4alternate1=false;
                        flag4alternate2=false;
                        allBoundaryCondtn=false;
                        flag4alternate3 = false;
//                        floatSubmit.setVisibility(View.GONE);
//                        findViewById(
//                                R.id.business_contactInfo_save_text)
//                                .setVisibility(View.GONE);
                        saveTextView.setVisibility(View.GONE);
                    }
                } else {
                    String alternate3Number = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3);
                    if (alternate3Number != null) {
                        int size = alternate3Number.length();
                        if (size > 2) {
                            if (!Util.isNullOrEmpty(alternate3Number))
                                alternate3 = alternate3Number;
                        }
                    }
                }

                obj4.put("key", "CONTACTS");
                obj4.put("value", alternate1+"#"+alternate2+"#"+alternate3);

            } catch (Exception ex) {
                System.out.println();
            }
            ja.put(obj4);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER, alternate1);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1, alternate2);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3, alternate3);

        }

        try {
            offerObj.put("clientId", Constants.clientId);
            offerObj.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase());
            offerObj.put("updates", ja);
        } catch (Exception ex) {

        }

        if(allBoundaryCondtn)
        {
            UploadProfileAsyncTask upa = new UploadProfileAsyncTask(Contact_Info_Activity.this,offerObj,profilesattr);
            upa.execute();
        }
        else
        {
            allBoundaryCondtn = true;
        }

        // upa = new UploadPictureMessageAsyncTask(this, path, offerObj,
        // postUser, postPage);
        // upa.execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if("VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)) ||
                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME))){
            alternateNumber_2.setInputType(InputType.TYPE_NULL);
            alternateNumber_3.setInputType(InputType.TYPE_NULL);
            alternateNumber_1.setInputType(InputType.TYPE_NULL);
            alternateNumber_1.setOnTouchListener(this);
            alternateNumber_2.setOnTouchListener(this);
            alternateNumber_3.setOnTouchListener(this);
            VMN_Dialog = true;
    }
        bus.register(this);
        this.setTitle("Contact Information");
    }
    private MaterialDialog dialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_link_layout,null,false);
        TextView message = (TextView) dialogView.findViewById(R.id.toast_message_to_contact);
        if(VMN_Dialog) {
            message.setText("Call tracker is enabled. You will receive the call on your primary number."+getString(R.string.primary_contact_number_message));
        }else{
            message.setText(getString(R.string.primary_contact_number_message));
        }
            return new MaterialDialog.Builder(Contact_Info_Activity.this)
                    .title("Change number")
                    .customView(dialogView,false)
                    .positiveText(getString(R.string.ok))
                    .positiveColorRes(R.color.primaryColor)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                        }

                    })
                    .build();
    }
    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void initializeData() {

//        primaryNumber = (EditText) findViewById(R.id.primaryNumber);
//        alternateNumber_1 = (EditText) findViewById(R.id.alternateNumber_1);
//        alternateNumber_2 = (EditText) findViewById(R.id.alternateNumber_2);
//        emailAddress = (EditText) findViewById(R.id.contactInfo_emailId);
//        websiteAddress = (EditText) findViewById(R.id.websiteAddress);
//        facebookPage = (EditText) findViewById(R.id.facebookPage);


        primaryNumber.setText(session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        alternateNumber_1.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        alternateNumber_2.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_1));
        alternateNumber_3.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NUMBER_3));
        emailAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        String websiteAddr= session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE);
        if(websiteAddr!=null && !websiteAddr.equals("")) {
            if (websiteAddr.split("://")[0].equals("http") && websiteAddr.split("://").length==2) {
                protocolSpinner.setSelection(0);
                websiteAddress.setText(websiteAddr.split("://")[1]);
            } else if (websiteAddr.split("://")[0].equals("https") && websiteAddr.split("://").length==2) {
                protocolSpinner.setSelection(1);
                websiteAddress.setText(websiteAddr.split("://")[1]);
            }
        }
        facebookPage.setText(session.getFacebookPage());


//      try {
//            if (Constants.StoreContact != null) {
//                int size = Constants.StoreContact.length;
//                if (size != 0) {
//                    if (!Util.isNullOrEmpty(Constants.StoreContact[0]))
//                        primaryNumber.setText(Constants.StoreContact[0]);
//                    primaryNumberText.setVisibility(View.VISIBLE);
//
//                }
//                if (size > 2) {
//                    if (!Util.isNullOrEmpty(Constants.StoreContact[1])) {
//                        alternateNumber_1.setText(Constants.StoreContact[1]);
//                        alternateNumberText1.setVisibility(View.VISIBLE);
//                    }
//                    if (size > 3) {
//                        if (!Util.isNullOrEmpty(Constants.StoreContact[2])) {
//                            alternateNumber_2.setText(Constants.StoreContact[2]);
//                            alternateNumberText2.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            }
//
//            if (Constants.StoreContact == null && Constants.StorePrimaryNumber != null) {
//                primaryNumber.setText("+" + Constants.StoreCountryCode + "-" + Constants.StorePrimaryNumber);
//
//            }
//
//
//            if (Constants.StoreContact == null && Constants.StoreContact[1] != null) {
//                alternateNumber_1.setText(Constants.StoreContact[1]);
//            }
//
//            if (Constants.StoreContact == null && Constants.StoreContact[2] != null) {
//                alternateNumber_1.setText(Constants.StoreContact[2]);
//            }
//
//            if (!Util.isNullOrEmpty(Constants.StoreEmail)) {
//                emailAddress.setText(Constants.StoreEmail);
//                emailText.setVisibility(View.VISIBLE);
//            }
//            if (!Util.isNullOrEmpty(Constants.StoreWebSite)) {
//                websiteAddress.setText(Constants.StoreWebSite);
//                websiteText.setVisibility(View.VISIBLE);
//            }
//            if (!Util.isNullOrEmpty(Constants.StoreFbPage)) {
//                facebookPage.setText(Constants.StoreFbPage);
//                fbpageText.setVisibility(View.VISIBLE);
//            }
//        } catch (Exception e)
//        {
//
//        }


        saveTextView.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact__info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){




            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //  NavUtils.navigateUpFromSameTask(this);
        }



        return super.onOptionsItemSelected(item);
    }


    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidWebsite(String website) {

        Pattern pattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
        //Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(website);
        return matcher.matches();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            MaterialDialog dialog = dialog();
            if(!isFinishing()){
                dialog.show();
                return true;
            }
        }
        return false;
    }
}
