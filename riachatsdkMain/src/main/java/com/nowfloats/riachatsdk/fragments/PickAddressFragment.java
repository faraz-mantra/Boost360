package com.nowfloats.riachatsdk.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.utils.NFGeoCoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 */

public class PickAddressFragment extends DialogFragment implements LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private static final String ARG_MAP_TYPE = "map_type";
    private static final String ARG_MAP_DATA = "map_data";
    ColorStateList errorColorStateList = ColorStateList.valueOf(Color.RED);
    ColorStateList initialColorStateList = ColorStateList.valueOf(Color.LTGRAY);
    private TextInputEditText etStreetAddr, etPin, etLocality, etHousePlotNum, etLandmark, etState, etCountry;
    private Button btnSave, btnCancel;
    private AutoCompleteTextView etCity;
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateErrorList();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView tvAddress;
    private ImageView tvTip;
    private LinearLayout llManual, llUseGPS;
    private RelativeLayout rlMapContainer;
    private SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private LatLng mCenterLatLong;
    private OnResultReceive mResultListener;
    private PICK_TYPE pick_type;
    private AutocompleteFilter filter;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> signUpCountryList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Map<String, String> mDataMap = new HashMap<>();
    private NFGeoCoder nfGeoCoder;
    private boolean hasCitySelected = true;

    public static PickAddressFragment newInstance(PICK_TYPE pick_type) {

        PickAddressFragment fragment = new PickAddressFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MAP_TYPE, pick_type);
        fragment.setArguments(args);

        return fragment;
    }

    public static PickAddressFragment newInstance(PICK_TYPE pick_type, HashMap<String, String> mDataMap) {

        PickAddressFragment fragment = new PickAddressFragment();

        Bundle args = new Bundle();

        args.putSerializable(ARG_MAP_TYPE, pick_type);
        fragment.setArguments(args);

        args.putSerializable(ARG_MAP_DATA, mDataMap);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*hasCitySelected = false;*/

        if (getArguments() != null) {
            pick_type = (PICK_TYPE) getArguments().get(ARG_MAP_TYPE);
            mDataMap = (Map<String, String>) getArguments().get(ARG_MAP_DATA);

            //showKeyBoard();
        }

        makeAutoCompleteFilter(null);
        nfGeoCoder = new NFGeoCoder(getActivity());
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_address, container, false);
        setCancelable(false);

        llManual = v.findViewById(R.id.llManual);
        llUseGPS = v.findViewById(R.id.llUseGPS);
        btnSave = v.findViewById(R.id.btn_save);
        btnCancel = v.findViewById(R.id.btn_cancel);

        llManual.setVisibility(View.VISIBLE);
        btnSave.setText(getActivity().getResources().getString(R.string.locate_on_map));

        etCity = v.findViewById(R.id.et_city);
        etState = v.findViewById(R.id.et_state);
        etStreetAddr = v.findViewById(R.id.et_street_address);
        etCountry = v.findViewById(R.id.et_country);
        etPin = v.findViewById(R.id.et_pincode);
        etLocality = v.findViewById(R.id.et_locality);
        etHousePlotNum = v.findViewById(R.id.et_house_plot_num);
        etLandmark = v.findViewById(R.id.et_landmark);
        tvAddress = v.findViewById(R.id.tvAddress);
        tvTip = v.findViewById(R.id.tvTip);
        rlMapContainer = v.findViewById(R.id.mapFragment);

        btnSave = v.findViewById(R.id.btn_save);

        llManual.bringToFront();

        /*btnSave.setClickable(false);*/
        /*btnSave.setEnabled(false);*/

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (verifyData()) {
                    //btnSave.setClickable(false);
                    //btnSave.setEnabled(false);
                    //btnSave.setVisibility(View.INVISIBLE);

                    if (btnSave.getText().toString().equalsIgnoreCase(getResources().getString(R.string.locate_on_map))) {

                        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                getResources().getDisplayMetrics().heightPixels - 100);

                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.map_form_scale_down);
                        llManual.setAnimation(animation);
                        animation.start();
                        rlMapContainer.setVisibility(View.VISIBLE);
                        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.map_scale_up);
                        rlMapContainer.setAnimation(animation1);
                        animation1.start();

                        if (pick_type == PICK_TYPE.MANUAL) {
                            reverseGeoCode();
                        }

                        animation.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                llManual.setVisibility(View.GONE);
                                llUseGPS.setVisibility(View.VISIBLE);
                                tvAddress.setVisibility(View.VISIBLE);
                                btnSave.setText(getResources().getString(R.string.done));

                                tvTip.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        tvTip.setVisibility(View.VISIBLE);

                                        tvTip.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                tvTip.setVisibility(View.GONE);
                                            }
                                        }, 10000);


                                        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

                                            @Override
                                            public void onCameraMove() {
                                                tvTip.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }, 3000);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        if (mResultListener != null) {
                            double lat = 0, lng = 0;

                            if (mCenterLatLong != null) {
                                lat = mCenterLatLong.latitude;
                                lng = mCenterLatLong.longitude;
                            }

                            mResultListener.OnResult(etStreetAddr.getText().toString().trim(),
                                    etLocality.getText().toString(), etCity.getText().toString(), etState.getText().toString(),
                                    etCountry.getText().toString(), lat, lng,
                                    etPin.getText().toString(),
                                    etHousePlotNum.getText().toString(), etLandmark.getText().toString());

                            Fragment fragment = (getActivity())
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.map);

                            if (fragment != null) {
                                (getActivity())
                                        .getSupportFragmentManager().beginTransaction()
                                        .remove(fragment)
                                        .commit();
                            }
                        }
                    }
                }
            }
        });


        try {
            initMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pick_type == PICK_TYPE.MANUAL) {

            btnSave.setVisibility(View.VISIBLE);
            btnSave.setClickable(true);
            btnSave.setEnabled(true);

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(getActivity())
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(getActivity(), this)
                        .build();
            }

            etCity.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateErrorList();
                }

                @Override
                public void afterTextChanged(Editable s) {

                    loadCountryCodeandCountryNameMap();

                    if (getActivity() == null || (etCity.getTag() != null && !(boolean) etCity.getTag())) {

                    } else {
                        try {
                            /*hasCitySelected = false;*/
                            final PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, etCity.getText().toString().trim(),
                                    null, filter);

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    AutocompletePredictionBuffer a = result.await();
                                    final List<String> citys = new ArrayList<>();

                                    for (int i = 0; i < a.getCount(); i++) {
                                        citys.add(a.get(i).getPrimaryText(new StyleSpan(Typeface.NORMAL)).toString() + "," + a.get(i).getSecondaryText(new StyleSpan(Typeface.NORMAL)).toString());
                                    }

                                    a.release();

                                    etCity.post(new Runnable() {

                                        @Override
                                        public void run() {

                                            if (getActivity() != null && !getActivity().isFinishing()) {
                                                adapter = new ArrayAdapter<>(getActivity(),
                                                        android.R.layout.simple_dropdown_item_1line, citys);
                                                etCity.setAdapter(adapter);
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

                }
            });

            etCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String city = ((TextView) view).getText().toString();
                    String state = "";
                    String country = "";

                    try {
                        if (city.contains(",")) {
                            String result[] = city.split(",");
                            city = result[0];
                            state = result.length > 1 ? result[result.length - 2].replaceFirst("^ *", "") : "";
                            country = result[result.length - 1].replaceFirst("^ *", "");
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    etCity.setTag(false);
                    etCity.setText(city);
                    etState.setText(state);
                    etCity.setTag(true);
                    /*hasCitySelected = true;*/

                    etCountry.setText(country);
                    etPin.requestFocus();
                }
            });

            loadCountryCodeandCountryNameMap();
        } else {
            btnSave.setVisibility(View.GONE);
        }

        updateErrorList();
        addTextChangeListners();
        addListener();
        addTextChangeListener();
        populateData();

        return v;
    }

    private void addTextChangeListener() {
        etHousePlotNum.addTextChangedListener(new GenericTextWatcher(etHousePlotNum));
        etStreetAddr.addTextChangedListener(new GenericTextWatcher(etStreetAddr));
        etCity.addTextChangedListener(new GenericTextWatcher(etCity));
        etCountry.addTextChangedListener(new GenericTextWatcher(etCountry));
        etPin.addTextChangedListener(new GenericTextWatcher(etPin));
    }

    private void addListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().remove(PickAddressFragment.this).commit();
            }
        });
    }

    private void initializeCountryControls() {
       /* etCountry.setFocusable(false);
        etCountry.setFocusableInTouchMode(false);*//*
        etCountry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                *//*showCountryDialog(signUpCountryList);*//*
            }
        });*/
    }

    private void showCountryDialog(ArrayList<String> countries) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.search_list_item_layout, countries);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select a Country");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_list_layout, null);
        builderSingle.setView(view);

        EditText edtSearch = view.findViewById(R.id.edtSearch);
        ListView lvItems = view.findViewById(R.id.lvItems);

        lvItems.setAdapter(adapter);


        final Dialog dialog = builderSingle.show();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String strVal = adapter.getItem(position);
                dialog.dismiss();
                etCountry.setText(strVal);
                etPin.requestFocus();
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

    private void loadCountryCodeandCountryNameMap() {
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            signUpCountryList.add(obj.getDisplayCountry());
        }

        Collections.sort(signUpCountryList);
        initializeCountryControls();
    }

    private void populateData() {
        if (mDataMap != null && mDataMap.size() > 0) {
            etHousePlotNum.setText(mDataMap.get("[~" + "PICK_HOUSEPLOTNO" + "]"));
            etLocality.setText(mDataMap.get("[~" + "PICK_AREA" + "]"));
            etStreetAddr.setText(mDataMap.get("[~" + "PICK_ADDRESS" + "]"));
            etLandmark.setText(mDataMap.get("[~" + "PICK_LANDMARK" + "]"));
            etCity.setText(mDataMap.get("[~" + "CITY" + "]"));
            etState.setText(mDataMap.get("[~" + "STATE" + "]"));
            etCountry.setText(mDataMap.get("[~" + "COUNTRY" + "]"));
            etPin.setText(mDataMap.get("[~" + "PINCODE" + "]"));

            if (!etCity.getText().toString().trim().isEmpty()) {
                /*hasCitySelected = true;*/
            }

            if (checkFields()) {
                btnSave.setClickable(true);
                btnSave.setEnabled(true);
                btnSave.setBackgroundResource(R.drawable.done_button_enabled);
            }
        }
    }

    private void makeAutoCompleteFilter(String country_code) {
        try {
            filter = null;
            AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);

            if (country_code != null) {
                builder.setCountry(country_code.toUpperCase());
            }

            filter = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reverseGeoCode() {

        LatLng latLong = nfGeoCoder.reverseGeoCode(etStreetAddr.getText().toString(),
                etCity.getText().toString(), etCountry.getText().toString(), etPin.getText().toString());

        if (latLong != null) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(18f).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition), 1000, null);
        } else {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, PickAddressFragment.this);
            }

            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

        displayAddressOutput();
    }

    private boolean verifyData() {

        boolean isAllFieldsValid = true;

        if (etStreetAddr.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etStreetAddr.setBackgroundTintList(errorColorStateList);
            }
        }

        if (etCity.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;
            etCity.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }

        if (etCountry.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;
            etCountry.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }

        if (etPin.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etPin.setBackgroundTintList(errorColorStateList);
            }
        }

        if (etPin.getText().toString().trim().length() > 6) {
            isAllFieldsValid = false;

            Toast.makeText(getActivity(), getString(R.string.pin_code_length_error), Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etPin.setBackgroundTintList(errorColorStateList);
            }
        }

        if (etHousePlotNum.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etHousePlotNum.setBackgroundTintList(errorColorStateList);
            }
        }

        if (!isAllFieldsValid) {
            Toast.makeText(getActivity(), "Please enter mandatory fields.", Toast.LENGTH_SHORT).show();
        }

        if (!hasCitySelected) {
            isAllFieldsValid = false;
            Toast.makeText(getActivity(), "Please enter valid city.", Toast.LENGTH_SHORT).show();
        }

        return isAllFieldsValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mapFragment != null) {
            Fragment fragment = (getActivity())
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            if (fragment != null) {
                (getActivity())
                        .getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private void initMap() {

        if (mGoogleMap == null) {

            mapFragment = ((SupportMapFragment) (getActivity()).getSupportFragmentManager()
                    .findFragmentById(R.id.map));

            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {

                    mGoogleMap = googleMap;

                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED) {

                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }

                    if (mGoogleMap != null) {
                        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                    } else {
                        Toast.makeText(getActivity(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                    }

                    setCameraChangeListener();
                }
            });
        }
    }

    private void updateErrorList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            etStreetAddr.setBackgroundTintList(initialColorStateList);
            etLocality.setBackgroundTintList(initialColorStateList);
            etLandmark.setBackgroundTintList(initialColorStateList);
            etPin.setBackgroundTintList(initialColorStateList);
            etHousePlotNum.setBackgroundTintList(initialColorStateList);
        }

        etCity.getBackground().mutate().setColorFilter(getResources().getColor(R.color.lt_gray), PorterDuff.Mode.SRC_ATOP);
    }

    private void addTextChangeListners() {
        etStreetAddr.addTextChangedListener(textWatcher);
        etLocality.addTextChangedListener(textWatcher);
        etLandmark.addTextChangedListener(textWatcher);
        etPin.addTextChangedListener(textWatcher);
        etCountry.addTextChangedListener(textWatcher);
        etHousePlotNum.addTextChangedListener(textWatcher);
    }

    public void setResultListener(OnResultReceive onResultReceive) {
        mResultListener = onResultReceive;
    }

    private void setCameraChangeListener() {

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                mCenterLatLong = mGoogleMap.getCameraPosition().target;
                mGoogleMap.clear();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(18f).build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    protected void displayAddressOutput() {
        String landmark = TextUtils.isEmpty(etLandmark.getText().toString()) ? "" : etLandmark.getText().toString() + ", ";
        String locality = TextUtils.isEmpty(etLocality.getText().toString()) ? "" : etLocality.getText().toString() + ", ";
        String address = etHousePlotNum.getText().toString() + ", " + etStreetAddr.getText().toString() + ", " +
                locality +
                landmark +
                etCity.getText().toString() + ", " + etState.getText().toString() + ", " + etCountry.getText().toString() + ", " +
                etPin.getText().toString();

        tvAddress.setText(address);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setClickable(true);
        btnSave.setEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkFields() {

        if (etStreetAddr.getText().toString().trim().equals("")) {
            return false;
        }

        if (etCity.getText().toString().trim().equals("")) {
            return false;
        }

        if (etCountry.getText().toString().trim().equals("")) {
            return false;
        }

        if (etPin.getText().toString().trim().equals("")) {
            return false;
        }

        if (etPin.getText().toString().trim().length() > 6) {
            return false;
        }

        if (etHousePlotNum.getText().toString().trim().equals("")) {
            return false;
        }

        return hasCitySelected;
    }


    public enum PICK_TYPE {
        USE_GPS,
        MANUAL
    }


    public interface OnResultReceive {
        void OnResult(String address, String area, String city, String state, String country, double lat, double lon, String pin, String housePlotNum, String landmark);
    }

    class GenericTextWatcher implements TextWatcher {
        private GenericTextWatcher(View view) {

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

            if (checkFields()) {
                btnSave.setClickable(true);
                btnSave.setEnabled(true);
                btnSave.setBackgroundResource(R.drawable.done_button_enabled);
            } else {
                /*btnSave.setClickable(false);
                btnSave.setEnabled(false);
                btnSave.setBackgroundResource(R.drawable.done_button_disabled);*/
            }
        }
    }
}