package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressLatLongUpdateAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressUpdateAsyncTask;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.picasso.Picasso;
import com.thinksity.R;


public class Business_Address_Activity extends ActionBarActivity implements BusinessAddressLatLongUpdateAsyncTask.MapCallback{

    private Toolbar toolbar;
    //public static GoogleMap googleMap;
    public static TextView saveTextView;
    private TextView titleTextView;
    String responseMessage= "", address_line, address_town, address_pin,latlongAddress;
    private TextView addressText,cityText,provinceText,pincodeText,stateText,countryText;
    public static String adresslinetext , citytext , pincodetext , countrytext,statetext ;
    boolean streetaddressflag = false, cityflag = false, pincodeflag = false, geolocationflag = false,stateflag=false;
    public static EditText businessAddress,city,state,areaCode,country;
    public static String text1,text2,text3,text4;
    String[] profilesattr =new String[20];
    UserSessionManager session;
    public static ImageView ivMap;
    public static final int PLACE_PICKER_REQUEST = 23;
    private boolean mUpdatingPositionFromMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_address);
        Methods.isOnline(Business_Address_Activity.this);
        //loadData();

        session = new UserSessionManager(getApplicationContext(),Business_Address_Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText(getResources().getString(R.string.business__address));
        ivMap = (ImageView) findViewById(R.id.iv_map);

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapViewDialog(null);
            }
        });


        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.SAVE_BUSINESS_ADDRESS,null);
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        businessAddress = (EditText) findViewById(R.id.addressLine);
        city = (EditText) findViewById(R.id.businessAddress_cityEditText);
        state = (EditText) findViewById(R.id.businessAddress_stateEditText);
        areaCode = (EditText) findViewById(R.id.businessAddress_pinCodeEditText);
        country = (EditText) findViewById(R.id.businessAddress_countryEditText);

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




        city.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                try {
                    citytext = city.getText().toString().trim();

                    int len = citytext.length();
                    if (len > 0) {
                        cityflag= true;
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


        areaCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
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

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });






    }



    private void initializeData() {

        businessAddress.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS));
        city.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
        country.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));
        areaCode.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE));

        saveTextView.setVisibility(View.GONE);
    }

    private void uploadBussinessAddress() {
        int i=0;
        if(streetaddressflag)
        {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( state.getWindowToken(), 0);
            text1=adresslinetext;
            profilesattr[i] = "ADDRESS";
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS,text1);
            i++;
        }
        else
        {
            text1=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS);
        }
        if(cityflag){
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( city.getWindowToken(), 0);
            text2=citytext;
            profilesattr[i] = "CITY";
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_CITY,text2);
            i++;
        }
        else
        {
            text2=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY);
        }
        if(pincodeflag){
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( areaCode.getWindowToken(), 0);
            text3=pincodetext;
            profilesattr[i] = "PINCODE";
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE,text3);
            i++;
        }
        else
        {
            text3=session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PINCODE);
        }

        if(stateflag)
        {
            saveTextView.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( state.getWindowToken(), 0);

            SharedPreferences sharedpreferences = this.getSharedPreferences("Saving State Value", 0);
            SharedPreferences.Editor prefsEditor;
            prefsEditor = sharedpreferences.edit();
            prefsEditor.putString("State", statetext);
            prefsEditor.apply();
            stateflag=false;
//			if(streetaddressflag && pincodeflag==false && cityflag==false)
//			Util.toast("Your business address has been updated successfully", this);
        }

        SharedPreferences sharedPreferences =getSharedPreferences("Saving State Value", Activity.MODE_PRIVATE);
        latlongAddress=text1.replaceAll(" ", "+")+","+text2.replaceAll(" ", "+")+","+text3;
        latlongAddress.replaceAll(" ", "+");

        // if(!stateflag){

//		UploadProfileAsyncTask upa = new UploadProfileAsyncTask(this,offerObj,profilesattr);;
//		upa.execute();
        String stringafteredit=text1+","+text2+","+","+ session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)+","+text3;
        BusinessAddressLatLongUpdateAsyncTask save =new BusinessAddressLatLongUpdateAsyncTask(stringafteredit, profilesattr, this,latlongAddress,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        save.execute();
        // }


    }



    private void initilizeMap() {

        String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" + "AIzaSyBl66AnJ4_icH3gxI_ATc8031pveSTGWcg";
        //holderItem.chatImage.setVisibility(View.VISIBLE);
        Log.d("Map Urlggg:", url);
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


//    @Override
//    protected void onResume() {
//        super.onResume();
//        initilizeMap();
//        this.setTitle("Business Address");
//
//    }




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


            // Log.d("Back","Back Pressed");

            Business_Profile_Fragment_V2 f1 = (Business_Profile_Fragment_V2) getSupportFragmentManager().findFragmentByTag("A");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(f1 != null) {
                transaction.attach(f1);
                transaction.addToBackStack("Profile");
                transaction.commit();
            }
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //getSupportFragmentManager().popBackStack();

            //  NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    private void mapViewDialog(LatLngBounds latLngBounds) {

        /*Intent it = new Intent(this, NewMapViewDialogBusinessAddress.class);
        startActivity(it);*/
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            if(latLngBounds!=null){
                builder.setLatLngBounds(latLngBounds);
                Log.v("gggpp",latLngBounds.toString());
            }
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
                changeAddress(place);
                String url = "http://maps.google.com/maps/api/staticmap?center=" + Constants.latitude + "," + Constants.longitude + "&zoom=14&size=400x400&sensor=false" + "&markers=color:red%7Clabel:C%7C" + Constants.latitude + "," + Constants.longitude + "&key=" + "AIzaSyBl66AnJ4_icH3gxI_ATc8031pveSTGWcg";
                //holderItem.chatImage.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(this)
                            .load(url)
                            .placeholder(R.drawable.default_product_image)
                            .into(ivMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BusinessAddressUpdateAsyncTask Task = new BusinessAddressUpdateAsyncTask( Constants.latitude, Constants.longitude, Business_Address_Activity.this,businessAddress.getText().toString(),true,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
                Task.execute();
                saveTextView.setVisibility(View.GONE);
            }
        }
    }
    private void changeAddress(Place place){
        Log.v("ggg",place.toString());
        CharSequence address=place.getAddress();
        if(address!=null)
            businessAddress.setText(address);
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.setTitle(getResources().getString(R.string.business__address));

    }


    @Override
    public void openMap(LatLngBounds latLngBounds) {
        mapViewDialog(latLngBounds);
    }
}
