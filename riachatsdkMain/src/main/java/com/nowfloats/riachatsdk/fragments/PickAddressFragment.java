package com.nowfloats.riachatsdk.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 *
 */

public class PickAddressFragment extends DialogFragment implements LocationListener{

    EditText etStreetAddr, etCity, etCountry, etPin;
    Button btnSave;
    private GoogleMap mGoogleMap;

    private AddressResultReceiver mResultReceiver;

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    protected String mCountryOutput;
    protected String mPin;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private LocationManager mLocationManager;


    private LatLng mCenterLatLong;

    private OnResultReceive mResultListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_address, container, false);
        setCancelable(false);
        etCity = (EditText) v.findViewById(R.id.et_city);
        etStreetAddr = (EditText) v.findViewById(R.id.et_street_address);
        etCountry = (EditText) v.findViewById(R.id.et_country);
        etPin = (EditText) v.findViewById(R.id.et_pincode);
        btnSave = (Button) v.findViewById(R.id.btn_save);

        btnSave = (Button) v.findViewById(R.id.btn_save);
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Callback Interface
                if(mResultListener!=null && verifyData()){
                    mResultListener.OnResult(etStreetAddr.getText().toString().trim(),
                            mAreaOutput, mCityOutput, mStateOutput, mCountryOutput, mCenterLatLong.latitude, mCenterLatLong.latitude, mPin);
                    Fragment fragment = ((AppCompatActivity)getActivity())
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    if (fragment != null){
                        ((AppCompatActivity)getActivity())
                            .getSupportFragmentManager().beginTransaction()
                                .remove(fragment)
                                .commit();
                    }
                }

            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressOutput ="";

        try {
            // Loading map
            initMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
        }catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }

    private boolean verifyData(){
        if(etStreetAddr.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), getString(R.string.empty_address_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etCity.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), getString(R.string.empty_city_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etCountry.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), getString(R.string.empty_country_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etPin.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(), getString(R.string.empty_pin_warn), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void initMap() {
        if (mGoogleMap == null) {
            ((SupportMapFragment)((AppCompatActivity)getActivity())
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
                        LatLng latLong;


                        latLong = new LatLng(28.5707228, 77.0218132);
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

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLong).zoom(18f).build();

                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getActivity(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                        PackageManager.PERMISSION_GRANTED) {
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                        mGoogleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

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

    public void setResultListener(OnResultReceive onResultReceive){
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

            displayAddressOutput();

        }

    }
    protected void displayAddressOutput() {
        etCity.setText(mCityOutput);
        etCountry.setText(mCountryOutput);
        etStreetAddr.setText(mAddressOutput.replaceAll("[\r\n]+", " "));
        etPin.setText(mPin);
        btnSave.setVisibility(View.VISIBLE);
    }

    public interface OnResultReceive{
        void OnResult(String address, String area, String city, String state, String country, double lat, double lon, String pin);
    }


}
