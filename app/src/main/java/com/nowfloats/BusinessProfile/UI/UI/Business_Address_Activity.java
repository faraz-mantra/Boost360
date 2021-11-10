package com.nowfloats.BusinessProfile.UI.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressUpdateApi;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.helper.locationAsync.GetAddressFromLatLng;
import com.nowfloats.helper.locationAsync.GetLatLngFromAddress;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.framework.webengageconstant.EventLabelKt.BUSINESS_DESCRIPTION;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_ADDRESS;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_CITY;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_COUNTRY;
import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_PINCODE;
import static com.framework.webengageconstant.EventNameKt.BUSINESS_ADDRESS_ADDED;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_ADDRESS;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_CITY;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_COUNTRY;
import static com.framework.webengageconstant.EventNameKt.EVENT_NAME_PINCODE;
import static com.framework.webengageconstant.EventValueKt.NULL;


public class Business_Address_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int PLACE_PICKER_REQUEST = 23;
    private static final String TAG = "Business_Address";
    //public static GoogleMap googleMap;
    public static TextView saveTextView;
    public static String adresslinetext, citytext, pincodetext, countrytext, statetext;
    public static EditText businessAddress, state, areaCode, country;
    public static AutoCompleteTextView cityAutoText;
    public static String text1, text2, text3, text4;
    public static ImageView ivMap;
    String responseMessage = "", address_line, address_town, address_pin, latlongAddress;
    boolean streetaddressflag = false, cityflag = false, pincodeflag = false, geolocationflag = false, stateflag = false;
    String[] profilesattr = new String[20];
    UserSessionManager session;
    ArrayAdapter<String> adapter;
    private Toolbar toolbar;
    private TextView titleTextView;
    private TextView addressText, cityText, provinceText, pincodeText, stateText, countryText;
    private boolean mUpdatingPositionFromMap = false, saveAddressFlag = false;
    private List<String> citys = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private com.google.android.gms.location.places.AutocompleteFilter filter;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_address);
        Methods.isOnline(Business_Address_Activity.this);
        //loadData();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .addApi(com.google.android.gms.location.places.Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        session = new UserSessionManager(getApplicationContext(), Business_Address_Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.business__address));
        ivMap = (ImageView) findViewById(R.id.iv_map);

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mapViewDialog();
                if (!Places.isInitialized())
                    Places.initialize(getApplicationContext(), getResources().getString(R.string.google_map_key));
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setTypeFilter(TypeFilter.REGIONS)
                        .setCountry("IN")
                        .build(getApplicationContext());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(BUSINESS_ADDRESS_ADDED, BUSINESS_DESCRIPTION, session.getFpTag());
                MixPanelController.track(EventKeysWL.SAVE_BUSINESS_ADDRESS, null);
                saveAddressFlag = true;
                uploadBussinessAddress();
            }
        });

        initilizeMap();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        businessAddress = (EditText) findViewById(R.id.addressLine);
        cityAutoText = (AutoCompleteTextView) findViewById(R.id.businessAddress_cityEditText);
        state = (EditText) findViewById(R.id.businessAddress_stateEditText);
        areaCode = (EditText) findViewById(R.id.businessAddress_pinCodeEditText);
        country = (EditText) findViewById(R.id.businessAddress_countryEditText);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, citys);
        cityAutoText.setAdapter(adapter);
        country.setKeyListener(null);


        addressText = (TextView) findViewById(R.id.business_address_textline);
        cityText = (TextView) findViewById(R.id.business_address_citytext);
        provinceText = (TextView) findViewById(R.id.business_address_statetext);
        pincodeText = (TextView) findViewById(R.id.business_address_pincodetext);
        countryText = (TextView) findViewById(R.id.business_address_countrytext);

        initializeData();
        country.setOnClickListener(v -> {
            WebEngageController.trackEvent(EVENT_NAME_COUNTRY, EVENT_LABEL_COUNTRY, NULL);
        });
        cityText.setOnClickListener(v -> {
            WebEngageController.trackEvent(EVENT_NAME_CITY, EVENT_LABEL_CITY, NULL);
        });
        pincodeText.setOnClickListener(v -> {
            WebEngageController.trackEvent(EVENT_NAME_PINCODE, EVENT_LABEL_PINCODE, NULL);
        });
        addressText.setOnClickListener(v -> {
            WebEngageController.trackEvent(EVENT_NAME_ADDRESS, EVENT_LABEL_ADDRESS, NULL);
        });
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
                    citytext = cityAutoText.getText().toString().trim();

                    int len = citytext.length();
                    if (len > 0) {
                        cityflag = true;
                        saveTextView.setVisibility(View.VISIBLE);
                    } else {
                        saveTextView.setVisibility(View.GONE);

                    }

                    final PendingResult<com.google.android.gms.location.places.AutocompletePredictionBuffer> result =
                            com.google.android.gms.location.places.Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, cityAutoText.getText().toString().trim(),
                                    null, filter);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            com.google.android.gms.location.places.AutocompletePredictionBuffer a = result.await();
                            //Log.v("ggg","ok");
                            citys.clear();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            for (int i = 0; i < a.getCount(); i++) {
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

    private void makeAutoCompleteFilter(String country_code) {
        //Log.v("filter",country_code);
        filter = null;
        com.google.android.gms.location.places.AutocompleteFilter.Builder builder = new com.google.android.gms.location.places.AutocompleteFilter.Builder()
                .setTypeFilter(com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_CITIES);

        if (country_code != null) {
            builder.setCountry(country_code.toUpperCase());
        }
        filter = builder.build();

    }

    private void initializeData() {

        businessAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS));
        cityAutoText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
        citytext = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
        country.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));
        areaCode.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE));
        saveTextView.setVisibility(View.GONE);
    }

    private String countryCode() {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            if (obj.getDisplayCountry().trim().toLowerCase().equals(country.getText().toString().toLowerCase().trim()))
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
//        mapViewDialog();
        getLatLongUsingAddress();
    }

    private void getLatLongUsingAddress() {
        String countryN = "India";
        if (TextUtils.isEmpty(country.getText().toString()))
            countryN = country.getText().toString();
        String fullAddress = businessAddress.getText().toString() + ", " + citytext + ", " + countryN + " " + areaCode.getText().toString();
        GetLatLngFromAddress getLatLngFromAddress = new GetLatLngFromAddress(this, latLng -> {
            if (latLng != null) {
                Constants.latitude = latLng.latitude;
                Constants.longitude = latLng.longitude;
            } else {
                Constants.latitude = 0.0;
                Constants.longitude = 0.0;
            }
            BusinessAddressUpdateApi Task = new BusinessAddressUpdateApi(Constants.latitude,
                    Constants.longitude, Business_Address_Activity.this, citytext
                    , areaCode.getText().toString(), businessAddress.getText().toString(),
                    saveAddressFlag, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), this::updateSuccess);
            Task.update();
            saveTextView.setVisibility(View.GONE);
            saveAddressFlag = false;
        });
        getLatLngFromAddress.execute(fullAddress);
    }

    private void updateSuccess(boolean success) {
        initializeData();
        initilizeMap();
    }


    private void initilizeMap() {
        try {
            String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" + getString(R.string.google_map_key); //AIzaSyBl66AnJ4_icH3gxI_ATc8031pveSTGWcg
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.default_product_image)
                    .error(R.drawable.default_product_image)
                    .into(ivMap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Map Urlggg:", e.getLocalizedMessage());
        }
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

        if (id == android.R.id.home) {

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
        try {
            com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder builder = new com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder();

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PLACE_PICKER_REQUEST) {
            com.google.android.gms.location.places.Place place = com.google.android.gms.location.places.ui.PlacePicker.getPlace(this, data);
            if (place != null) {
                LatLng latLng = place.getLatLng();
                Constants.latitude = latLng.latitude;
                Constants.longitude = latLng.longitude;

                GetAddressFromLatLng getAddressFromLatLng = new GetAddressFromLatLng(this, bundle -> {
                    if (bundle != null) {
                        saveAddressFlag = true;
                        String address = bundle.getString("addressline1");
                        String city = bundle.getString("city");
                        String country = bundle.getString("country");
                        String pincode = bundle.getString("postalcode");
                        BusinessAddressUpdateApi Task = new BusinessAddressUpdateApi(Constants.latitude,
                                Constants.longitude, Business_Address_Activity.this, city, pincode, address,
                                saveAddressFlag, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), this::updateSuccess);
                        Task.update();
                    }
                    saveTextView.setVisibility(View.GONE);
                    saveAddressFlag = false;
                });
                getAddressFromLatLng.execute(Constants.latitude, Constants.longitude);
            }
        } else if (resultCode == RESULT_OK && requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            Constants.latitude = place.getLatLng().latitude;
            Constants.longitude = place.getLatLng().longitude;
            GetAddressFromLatLng getAddressFromLatLng = new GetAddressFromLatLng(this, bundle -> {
                if (bundle != null) {
                    saveAddressFlag = true;
                    String address = bundle.getString("addressline1");
                    String city = bundle.getString("city");
                    String country = bundle.getString("country");
                    String pincode = bundle.getString("postalcode");
                    BusinessAddressUpdateApi Task = new BusinessAddressUpdateApi(Constants.latitude,
                            Constants.longitude, Business_Address_Activity.this, city, pincode, address,
                            saveAddressFlag, session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), this::updateSuccess);
                    Task.update();
                }
                saveTextView.setVisibility(View.GONE);
                saveAddressFlag = false;
            });
            getAddressFromLatLng.execute(Constants.latitude, Constants.longitude);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setTitle(getResources().getString(R.string.business__address));

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
