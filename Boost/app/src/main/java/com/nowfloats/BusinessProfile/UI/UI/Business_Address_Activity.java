package com.nowfloats.BusinessProfile.UI.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressLatLongUpdateAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.BusinessAddressUpdateAsyncTask;
import com.nowfloats.BusinessProfile.UI.API.NewMapViewDialogBusinessAddress;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

public class Business_Address_Activity extends ActionBarActivity {

    private Toolbar toolbar;
    public static GoogleMap googleMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_address);
        Methods.isOnline(Business_Address_Activity.this);
        loadData();

        session = new UserSessionManager(getApplicationContext(),Business_Address_Activity.this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        saveTextView = (TextView) toolbar.findViewById(R.id.saveTextView);
        titleTextView = (TextView) toolbar.findViewById(R.id.titleTextView);
        titleTextView.setText("Address");




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
            prefsEditor.commit();
            stateflag=false;
//			if(streetaddressflag && pincodeflag==false && cityflag==false)
//			Util.toast("Your business address has been updated successfully", this);
        }

        SharedPreferences sharedPreferences =getSharedPreferences("Saving State Value", Activity.MODE_PRIVATE);
        latlongAddress=text1.replaceAll(" ", "+")+","+text2.replaceAll(" ", "+")+","+","+text3;
        latlongAddress.replaceAll(" ", "+");

       // if(!stateflag){

//		UploadProfileAsyncTask upa = new UploadProfileAsyncTask(this,offerObj,profilesattr);;
//		upa.execute();
            String stringafteredit=text1+","+text2+","+","+ session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)+","+text3;
            BusinessAddressLatLongUpdateAsyncTask save =new BusinessAddressLatLongUpdateAsyncTask(stringafteredit, profilesattr, this,latlongAddress,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            save.execute();
       // }


    }

    private void loadData() {


    }

    private void initilizeMap() {

        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();
        LatLng latlong = new LatLng(Constants.latitude , Constants.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapViewDialog();
            }
        });

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


    private void mapViewDialog() {

        Intent it = new Intent(this, NewMapViewDialogBusinessAddress.class);
        startActivity(it);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.setTitle("Business Address");
        if(NewMapViewDialogBusinessAddress.updatingPostionFromMap)
        {

            BusinessAddressUpdateAsyncTask Task = new BusinessAddressUpdateAsyncTask( Constants.latitude, Constants.longitude, Business_Address_Activity.this,null,true,session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            Task.execute();
            saveTextView.setVisibility(View.GONE);
        }

    }


}
