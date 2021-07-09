package com.nowfloats.BusinessProfile.UI.API;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.nowfloats.util.Methods;
import com.thinksity.R;
import com.nowfloats.util.Constants;

public class NewMapViewDialogBusinessAddress extends FragmentActivity{
    Context context = this;
	TextView tv;
	LatLng latlng;
	Button cancelButton, okButton;
	double lat, lon;
	static GoogleMap googleMap = null;
	String address;
	boolean flag=false, success=false;
    public static boolean updatingPostionFromMap=false,savebuttonhide=false;
    private final int LOC_REQ_CODE = 6577;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        //final PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapview_dialog);
        cancelButton = (Button) findViewById(R.id.mapview_cancel);
        okButton 	 = (Button) findViewById(R.id.mapview_confirm);
        Methods.isOnline(NewMapViewDialogBusinessAddress.this);
		((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.dialog_map))
                .getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        NewMapViewDialogBusinessAddress.googleMap = googleMap;
                        setLocation();
                    }
                });
        cancelButton.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        okButton.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
        okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try{
                    if(googleMap!=null) {
                        LatLng coordinates = googleMap.getCameraPosition().target;
                        double latitude = coordinates.latitude;
                        double longitude = coordinates.longitude;
                        Constants.latlng = new LatLng(latitude, longitude);
                        Constants.latitude = latitude;
                        Constants.longitude = longitude;
                        updatingPostionFromMap = true;
                        savebuttonhide = true;
                    }

                        //	geolocationflag=true;
                        //uploadBussinessAddress();
//					BusinessAddressUpdateAsyncTask Task = new BusinessAddressUpdateAsyncTask( latitude, longitude,  ,null,updatingPostionFromMap);
//					Task.execute(); 
                        finish();

				}
				catch(Exception e){
		        	e.printStackTrace();
		        }
			}
		});


	}
    private void setLocation(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
        }else {
            try {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                LatLng latlong = new LatLng(Constants.latitude, Constants.longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
                // Getting LocationManager object from System Service LOCATION_SERVICE
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);

                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    latlng = new LatLng(Constants.latitude, Constants.longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
                }

                LocationListener locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        // TODO Auto-generated method stub
                        drawMarker(location);
                    }

                    @Override
                    public void onProviderDisabled(String arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderEnabled(String arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                        // TODO Auto-generated method stub

                    }


                };
                locationManager.requestLocationUpdates(provider, 20000, 0, (LocationListener) locationListener);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOC_REQ_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setLocation();
                }else {
                    return;
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Methods.isOnline(NewMapViewDialogBusinessAddress.this);
    }

    private void drawMarker(Location location){
	    googleMap.clear();
	    LatLng currentPosition = new LatLng(Constants.latitude,Constants.longitude);
	    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
	}





}
