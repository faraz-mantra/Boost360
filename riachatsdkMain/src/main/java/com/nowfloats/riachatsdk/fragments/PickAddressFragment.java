package com.nowfloats.riachatsdk.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.services.FetchAddressIntentService;
import com.nowfloats.riachatsdk.utils.Constants;

import java.io.IOException;
import java.util.List;

/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 */

public class PickAddressFragment extends DialogFragment implements LocationListener {

    private TextInputEditText etStreetAddr, etCity, etCountry, etPin, etLocality, etHousePlotNum, etLandmark;

    private Button btnSave;

    private TextView tvAddress;

    private LinearLayout llManual, llUseGPS;

    private GoogleMap mGoogleMap;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    protected String mCountryOutput;
    protected String mPin;
    protected String mLocality;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private LocationManager mLocationManager;

    private LatLng mCenterLatLong;

    private OnResultReceive mResultListener;

    private static final String ARG_MAP_TYPE = "map_type";

    private PICK_TYPE pick_type;

    public static PickAddressFragment newInstance(PICK_TYPE pick_type) {

        PickAddressFragment fragment = new PickAddressFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MAP_TYPE, pick_type);
        fragment.setArguments(args);

        return fragment;
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
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            switch (pick_type) {
                case USE_GPS:
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    break;
                case MANUAL:
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_address, container, false);
        setCancelable(false);

        llManual = (LinearLayout) v.findViewById(R.id.llManual);
        llUseGPS = (LinearLayout) v.findViewById(R.id.llUseGPS);
        btnSave = (Button) v.findViewById(R.id.btn_save);

        if (pick_type == PICK_TYPE.MANUAL) {
            llManual.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setText(getActivity().getResources().getString(R.string.locate_on_map));
        } else {
            btnSave.setVisibility(View.INVISIBLE);
            llUseGPS.setVisibility(View.VISIBLE);
            btnSave.setText(getActivity().getResources().getString(R.string.done));
        }

        etCity = (TextInputEditText) v.findViewById(R.id.et_city);
        etStreetAddr = (TextInputEditText) v.findViewById(R.id.et_street_address);
        etCountry = (TextInputEditText) v.findViewById(R.id.et_country);
        etPin = (TextInputEditText) v.findViewById(R.id.et_pincode);
        etLocality = (TextInputEditText) v.findViewById(R.id.et_locality);
        etHousePlotNum = (TextInputEditText) v.findViewById(R.id.et_house_plot_num);
        etLandmark = (TextInputEditText) v.findViewById(R.id.et_landmark);
        tvAddress = (TextView) v.findViewById(R.id.tvAddress);

        btnSave = (Button) v.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Callback Interface
                if (mResultListener != null && verifyData()) {

                    mResultListener.OnResult(etStreetAddr.getText().toString().trim(),
                            mAreaOutput, mCityOutput, mStateOutput, mCountryOutput, mCenterLatLong.latitude, mCenterLatLong.longitude,
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
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressOutput = "";

        try {
            // Loading map
            initMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
        } catch (Exception e) {
            e.printStackTrace();
        }


        etStreetAddr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    reverseGeoCode();
                }
                return false;
            }
        });

        return v;
    }

    private void reverseGeoCode() {

        String content = "";
        Geocoder gc = new Geocoder(getActivity());
        if (gc.isPresent()) {
            List<Address> list = null;
            try {
                Address address = null;
                list = gc.getFromLocationName(etStreetAddr.getText().toString() + " " + etCity.getText().toString(), 1);
                double lat = 0, lng = 0;
                if (list != null && list.size() > 0) {

                    address = list.get(0);
                    lat = address.getLatitude();
                    lng = address.getLongitude();

                    /*for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        content += address.getAddressLine(i)+" ";
                    }*/
//                    if (!TextUtils.isEmpty(content)) {

//                    etStreetAddr.setText(content);
//                    etStreetAddr.setSelection(etStreetAddr.getText().toString().length());
//
//                    etPin.setText(list.get(0).getPostalCode());

                    LatLng latLong = new LatLng(lat, lng);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLong).zoom(18f).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
//                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean verifyData() {
        if (etStreetAddr.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.empty_address_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCity.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.empty_city_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCountry.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.empty_country_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPin.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getString(R.string.empty_pin_warn), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

                            // check if map is created successfully or not
                            if (mGoogleMap != null) {
                                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
//                                LatLng latLong;


//                                latLong = new LatLng(28.5707228, 77.0218132);
                                if (ActivityCompat.checkSelfPermission(getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                                        PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(getActivity(),
                                                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                                PackageManager.PERMISSION_GRANTED) {
                                    mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, PickAddressFragment.this);
                                    //latLong = new LatLng(location.getLatitude(), location.getLongitude());
                                }

//                                CameraPosition cameraPosition = new CameraPosition.Builder()
//                                        .target(latLong).zoom(18f).build();

                                if (ActivityCompat.checkSelfPermission(getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                                        PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(getActivity(),
                                                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                                PackageManager.PERMISSION_GRANTED) {
                                    mGoogleMap.setMyLocationEnabled(true);
                                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                }
//                                mGoogleMap.animateCamera(CameraUpdateFactory
//                                        .newCameraPosition(cameraPosition));

                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            setCameraChangeListener();
                        }
                    });


        } else {
            setCameraChangeListener();
        }

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

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        intent.putExtra(Constants.LocationConstants.RECEIVER, mResultReceiver);

        intent.putExtra(Constants.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        getActivity().startService(intent);
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


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(Constants.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(Constants.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(Constants.LocationConstants.LOCATION_DATA_STREET);
            mCountryOutput = resultData.getString(Constants.LocationConstants.LOCATION_DATA_COUNTRY);
            mPin = resultData.getString(Constants.LocationConstants.LOCATION_DATA_PIN);
            mLocality = resultData.getString(Constants.LocationConstants.LOCATION_DATA_LOCALITY);

            displayAddressOutput();

        }

    }

    protected void displayAddressOutput() {
        etCity.setText(mCityOutput);
        etCountry.setText(mCountryOutput);

        if(!TextUtils.isEmpty(mStateOutput)){
            etStreetAddr.setText(mStateOutput.replaceAll("[\r\n]+", " ")+"");
        }
        etStreetAddr.setSelection(etStreetAddr.getText().toString().length());

        etPin.setText(mPin);
        etLocality.setText(mLocality);
        btnSave.setVisibility(View.VISIBLE);

//        if(TextUtils.isEmpty(tvAddress.getText().toString())){

            String address = etHousePlotNum.getText().toString() + ", " + etLandmark.getText().toString() + ", " +
                    etStreetAddr.getText().toString().trim() + ", " +
                    mCityOutput + ", " + mCountryOutput + ", " +
                    etPin.getText().toString();

            tvAddress.setText(address);
//        }

    }

    public interface OnResultReceive {
        void OnResult(String address, String area, String city, String state, String country, double lat, double lon, String pin, String housePlotNum, String landmark);
    }
}
