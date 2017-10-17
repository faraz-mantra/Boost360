package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressUpdateApi;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Business_Address_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    //public static GoogleMap googleMap;
    public static TextView saveTextView;
    private TextView titleTextView;
    String responseMessage= "", address_line, address_town, address_pin,latlongAddress;
    private TextView addressText,cityText,provinceText,pincodeText,stateText,countryText;
    public static String adresslinetext , citytext , pincodetext , countrytext,statetext ;
    boolean streetaddressflag = false, cityflag = false, pincodeflag = false, geolocationflag = false,stateflag=false;
    public static EditText businessAddress,state,areaCode,country;
    public static AutoCompleteTextView cityAutoText;
    public static String text1,text2,text3,text4;
    String[] profilesattr =new String[20];
    UserSessionManager session;
    public static ImageView ivMap;
    public static final int PLACE_PICKER_REQUEST = 23;
    private boolean mUpdatingPositionFromMap = false,saveAddressFlag=false;
    ArrayAdapter<String> adapter;
    private List<String> citys =new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    AutocompleteFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_address);
        Methods.isOnline(Business_Address_Activity.this);
        //loadData();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        session = new UserSessionManager(getApplicationContext(),Business_Address_Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.business__address));
        ivMap = (ImageView) findViewById(R.id.iv_map);

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapViewDialog();
            }
        });


        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.SAVE_BUSINESS_ADDRESS,null);
                saveAddressFlag=true;
                uploadBussinessAddress();
            }
        });


        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        businessAddress = (EditText) findViewById(R.id.addressLine);
        cityAutoText = (AutoCompleteTextView) findViewById(R.id.businessAddress_cityEditText);
        state = (EditText) findViewById(R.id.businessAddress_stateEditText);
        areaCode = (EditText) findViewById(R.id.businessAddress_pinCodeEditText);
        country = (EditText) findViewById(R.id.businessAddress_countryEditText);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,citys);
        cityAutoText.setAdapter(adapter);
        country.setKeyListener(null);


        addressText = (TextView) findViewById(R.id.business_address_textline);
        cityText = (TextView) findViewById(R.id.business_address_citytext);
        provinceText = (TextView) findViewById(R.id.business_address_statetext);
        pincodeText = (TextView) findViewById(R.id.business_address_pincodetext);
        countryText = (TextView) findViewById(R.id.business_address_countrytext);

        initializeData();
        businessAddress.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {
                    adresslinetext = businessAddress.getText().toString().trim();

                    int len = adresslinetext.length();
                    if (len > 0) {
                        streetaddressflag = true;
                        saveTextView.setVisibility(View.VISIBLE);
                    }
                    else {
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


        state.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {
                    statetext = state.getText().toString().trim();

                    int len = statetext.length();
                    if (len > 0) {
                        stateflag = true;
                        saveTextView.setVisibility(View.VISIBLE);
                    }
                    else {
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



        cityAutoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                citytext = adapter.getItem(position);
            }
        });
        cityAutoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cityAutoText.setText(citytext);
            }
        });
        cityAutoText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String citytext = cityAutoText.getText().toString().trim();

                    int len = citytext.length();
                    if (len > 0) {
                        cityflag= true;
                        saveTextView.setVisibility(View.VISIBLE);
                    } else {
                        saveTextView.setVisibility(View.GONE);

                    }

                    final PendingResult<AutocompletePredictionBuffer> result =
                            Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, cityAutoText.getText().toString().trim(),
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
                                    adapter = new ArrayAdapter<>(Business_Address_Activity.this,
                                            android.R.layout.simple_dropdown_item_1line, citys);
                                    if (!isFinishing()) {
                                        cityAutoText.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        areaCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    pincodetext = areaCode.getText()
                            .toString().trim();
                    int len = pincodetext.length();
                    if (len > 0) {
                        pincodeflag = true;
                        saveTextView.setVisibility(View.VISIBLE);
                    } else {
                        saveTextView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }
        });
        makeAutoCompleteFilter(countryCode());
    }
    private void makeAutoCompleteFilter(String country_code){
        //Log.v("filter",country_code);
        filter =null;
        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);

        if(country_code!=null){
            builder.setCountry(country_code.toUpperCase());
        }
        filter= builder.build();

    }
    private void initializeData() {

        businessAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS));
        cityAutoText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
        citytext = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
        country.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));
        areaCode.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE));
        saveTextView.setVisibility(View.GONE);
    }
    private String countryCode(){
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
           if(obj.getDisplayCountry().trim().toLowerCase().equals(country.getText().toString().toLowerCase().trim()))
             return obj.getCountry();
        }
        return null;
    }
    private void uploadBussinessAddress() {
        int i = 0;
        if (streetaddressflag) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(state.getWindowToken(), 0);
            text1 = adresslinetext;
            profilesattr[i] = "ADDRESS";
            //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS, text1);
            i++;
        } else {
            text1 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS);
        }
        if (cityflag) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(cityAutoText.getWindowToken(), 0);
            text2 = citytext;
            profilesattr[i] = "CITY";
            //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY, text2);
            i++;
        } else {
            text2 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
        }
        if (pincodeflag) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(areaCode.getWindowToken(), 0);
            text3 = pincodetext;
            //session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE, text3);
            i++;
        } else {
            text3 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE);
        }

        if (stateflag) {
            saveTextView.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(state.getWindowToken(), 0);

            SharedPreferences sharedpreferences = this.getSharedPreferences("Saving State Value", 0);
            SharedPreferences.Editor prefsEditor;
            prefsEditor = sharedpreferences.edit();
            prefsEditor.putString("State", statetext);
            prefsEditor.apply();
            stateflag = false;

        }
        mapViewDialog();
    }



    private void initilizeMap() {

        String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" +getString(R.string.google_map_key); //AIzaSyBl66AnJ4_icH3gxI_ATc8031pveSTGWcg
        //holderItem.chatImage.setVisibility(View.VISIBLE);
       // Log.d("Map Urlggg:", url);
        try {
            Picasso.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_product_image)
                    .error(R.drawable.default_product_image)
                    .into(ivMap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // check if map is created successfully or not
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business__address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home ){

            /*Business_Profile_Fragment_V2 f1 = (Business_Profile_Fragment_V2) getSupportFragmentManager().findFragmentByTag("A");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(f1 != null) {
                transaction.attach(f1);
                transaction.addToBackStack("Profile");
                transaction.commit();
            }*/
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
            //getSupportFragmentManager().popBackStack();

            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    private void mapViewDialog() {

        /*Intent it = new Intent(this, NewMapViewDialogBusinessAddress.class);
        startActivity(it);*/
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            Intent placePickerIntent = builder.build(this);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                placePickerIntent.putExtra("primary_color", ContextCompat.getColor(this, R.color.primaryColor));
                placePickerIntent.putExtra("primary_color_dark", ContextCompat.getColor(this, R.color.primaryColorDark));
            }

            startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(this, data);
            if(place!=null){
                LatLng latLng = place.getLatLng();
                Constants.latitude = latLng.latitude;
                Constants.longitude = latLng.longitude;
                String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" + getString(R.string.google_map_key);
                //holderItem.chatImage.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(this)
                            .load(url)
                            .placeholder(R.drawable.default_product_image)
                            .into(ivMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BusinessAddressUpdateApi Task = new BusinessAddressUpdateApi( Constants.latitude,
                        Constants.longitude, Business_Address_Activity.this,citytext
                        ,areaCode.getText().toString(),businessAddress.getText().toString(),
                        saveAddressFlag,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
                Task.update();
                saveTextView.setVisibility(View.GONE);
                saveAddressFlag=false;
            }
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.setTitle(getResources().getString(R.string.business__address));

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
