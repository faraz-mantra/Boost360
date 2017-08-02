package com.nowfloats.riachatsdk.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private TextInputEditText etStreetAddr, etPin, etLocality, etHousePlotNum, etLandmark;

    private Button btnSave;

    private AutoCompleteTextView etCity, etCountry;

    private TextView tvAddress;

    private ImageView tvTip;

    private LinearLayout llManual, llUseGPS;

    private GoogleMap mGoogleMap;

    private RelativeLayout rlMapContainer;


    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private LocationManager mLocationManager;

    private LatLng mCenterLatLong;

    private OnResultReceive mResultListener;

    private static final String ARG_MAP_TYPE = "map_type";
    private static final String ARG_MAP_DATA = "map_data";

    private PICK_TYPE pick_type;

    private HashMap<String, String> Country_CodeMap = new HashMap<>();

    private AutocompleteFilter filter;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> signUpCountryList = new ArrayList<>();

    private GoogleApiClient mGoogleApiClient;

    private Map<String, String> mDataMap = new HashMap<>();
    private NFGeoCoder nfGeoCoder;

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

    public enum PICK_TYPE {
        USE_GPS,
        MANUAL
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pick_type = (PICK_TYPE) getArguments().get(ARG_MAP_TYPE);
            mDataMap = (Map<String, String>) getArguments().get(ARG_MAP_DATA);
            showKeyBoard();
        }

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

//        tiHouseNo = (TextInputLayout) v.findViewById(R.id.tiHouseNo);

        llManual = (LinearLayout) v.findViewById(R.id.llManual);
        llUseGPS = (LinearLayout) v.findViewById(R.id.llUseGPS);
        btnSave = (Button) v.findViewById(R.id.btn_save);

        llManual.setVisibility(View.VISIBLE);
        btnSave.setText(getActivity().getResources().getString(R.string.locate_on_map));

        etCity = (AutoCompleteTextView) v.findViewById(R.id.et_city);
        etStreetAddr = (TextInputEditText) v.findViewById(R.id.et_street_address);
        etCountry = (AutoCompleteTextView) v.findViewById(R.id.et_country);
        etPin = (TextInputEditText) v.findViewById(R.id.et_pincode);
        etLocality = (TextInputEditText) v.findViewById(R.id.et_locality);
        etHousePlotNum = (TextInputEditText) v.findViewById(R.id.et_house_plot_num);
        etLandmark = (TextInputEditText) v.findViewById(R.id.et_landmark);
        tvAddress = (TextView) v.findViewById(R.id.tvAddress);
        tvTip = (ImageView) v.findViewById(R.id.tvTip);
        rlMapContainer = (RelativeLayout) v.findViewById(R.id.mapFragment);

        populateData();

        btnSave = (Button) v.findViewById(R.id.btn_save);

        llManual.bringToFront();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyData()) {
                    btnSave.setClickable(false);
                    btnSave.setEnabled(false);
                    btnSave.setVisibility(View.INVISIBLE);
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

//                                Toast.makeText(getActivity(),getString(R.string.you_can_move_pinch_zoom_map_to_exact_location),Toast.LENGTH_LONG).show();
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
                                    etLocality.getText().toString(), etCity.getText().toString(), "",
                                    etCountry.getText().toString(), lat, lng,
                                    etPin.getText().toString(),
                                    etHousePlotNum.getText().toString(), etLandmark.getText().toString());

                            Fragment fragment = ((AppCompatActivity) getActivity())
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.map);

                            if (fragment != null) {
                                ((AppCompatActivity) getActivity())
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
                        .enableAutoManage((FragmentActivity) getActivity(), this)
                        .build();
            }

//            etCountry.setFocusable(false);

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
                    if (etCity.getTag() != null && !(boolean) etCity.getTag()) {

                    } else {
                        try {
//                            String country_code = null;
//                            if (Country_CodeMap != null) {
//                                country_code = Country_CodeMap.get(etCity.getText().toString());
//                            }
//                            makeAutoCompleteFilter(country_code);

                            final PendingResult<AutocompletePredictionBuffer> result =
                                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, etCity.getText().toString().trim(),
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

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (getActivity() != null &&
                                                    !getActivity().isFinishing()) {
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

                        }
                    }

                }
            });

            etCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String city = ((TextView) view).getText().toString();
                    if (city.contains(",")) {
                        String country[] = city.split(",");
                        city = country[0];
                        if (country.length == 3) {
                            etCountry.setText(country[2].trim());
                        } else if (country.length == 2)
                            etCountry.setText(country[1].trim());
                        etCountry.setFocusable(true);
                    }
                    etCity.setTag(false);
                    etCity.setText(city);
                    etCity.setTag(true);

                }
            });

            loadCountryCodeandCountryNameMap();
        } else {
            btnSave.setVisibility(View.GONE);
        }

        updateErrorList();
        addTextChangeListners();

        return v;
    }

    private void initializeCountryControls() {

        etCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateErrorList();
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                if (etCountry.getTag() != null && !(boolean) etCountry.getTag()) {

                } else {
                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final List<String> countrys = new ArrayList<>();
                                if (signUpCountryList != null) {

                                    if (!TextUtils.isEmpty(editable)) {

                                        for (int i = 0; i < signUpCountryList.size(); i++) {
                                            String country = signUpCountryList.get(i);
                                            if (country.contains(editable.toString()))
                                                countrys.add(signUpCountryList.get(i));
                                        }
                                    }
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (getActivity() != null &&
                                                !getActivity().isFinishing()) {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                                    android.R.layout.simple_dropdown_item_1line, countrys);
                                            etCountry.setAdapter(adapter);
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

        etCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String country = ((TextView) view).getText().toString();
                etCountry.setTag(false);
                etCountry.setText(country);
                etCountry.setTag(true);

            }
        });

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
            etCountry.setText(mDataMap.get("[~" + "COUNTRY" + "]"));
            etPin.setText(mDataMap.get("[~" + "PINCODE" + "]"));
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
            etStreetAddr.setSupportBackgroundTintList(errorColorStateList);
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
            etPin.setSupportBackgroundTintList(errorColorStateList);
        }
        if (etPin.getText().toString().trim().length() > 6) {
            isAllFieldsValid = false;
            Toast.makeText(getActivity(), getString(R.string.pin_code_length_error), Toast.LENGTH_SHORT).show();
            etPin.setSupportBackgroundTintList(errorColorStateList);
        }
        if (etHousePlotNum.getText().toString().trim().equals("")) {
            isAllFieldsValid = false;
            etHousePlotNum.setSupportBackgroundTintList(errorColorStateList);
        }

        if (!isAllFieldsValid) {
            Toast.makeText(getActivity(), "Please enter mandatory fields.", Toast.LENGTH_SHORT).show();
        }
        return isAllFieldsValid;
    }

    private void initMap() {
        if (mGoogleMap == null) {
            ((SupportMapFragment) ((AppCompatActivity) getActivity())
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
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
                                Toast.makeText(getActivity(),
                                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            setCameraChangeListener();
                        }
                    });


        }

    }

    ColorStateList errorColorStateList = ColorStateList.valueOf(Color.RED);
    ColorStateList initialColorStateList = ColorStateList.valueOf(Color.LTGRAY);

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

    private void updateErrorList() {
        etStreetAddr.setSupportBackgroundTintList(initialColorStateList);
        etLocality.setSupportBackgroundTintList(initialColorStateList);
        etLandmark.setSupportBackgroundTintList(initialColorStateList);
        etPin.setSupportBackgroundTintList(initialColorStateList);
        etCity.getBackground().mutate().setColorFilter(getResources().getColor(R.color.lt_gray), PorterDuff.Mode.SRC_ATOP);
        etHousePlotNum.setSupportBackgroundTintList(initialColorStateList);
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
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
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
                etCity.getText().toString() + ", " + etCountry.getText().toString() + ", " +
                etPin.getText().toString();

        tvAddress.setText(address);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setClickable(true);
        btnSave.setEnabled(true);
    }

    public interface OnResultReceive {
        void OnResult(String address, String area, String city, String state, String country, double lat, double lon, String pin, String housePlotNum, String landmark);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
            mGoogleApiClient.disconnect();
        }
    }

}
