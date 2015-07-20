package com.nowfloats.BusinessProfile.UI.API;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
	GoogleMap googleMap = null;
	String address;
	boolean flag=false, success=false;
    public static boolean updatingPostionFromMap=false,savebuttonhide=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        final PorterDuffColorFilter whiteLabelFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapview_dialog);
        Methods.isOnline(NewMapViewDialogBusinessAddress.this);
		googleMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.dialog_map)).getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		LatLng latlong = new LatLng(Constants.latitude , Constants.longitude);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16)); 
		
		cancelButton = (Button) findViewById(R.id.mapview_cancel);
		okButton 	 = (Button) findViewById(R.id.mapview_confirm);
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
				LatLng coordinates=googleMap.getCameraPosition().target;
				double latitude = coordinates.latitude;
				double longitude = coordinates.longitude;
				Constants.latlng = new LatLng(latitude, longitude);
				Constants.latitude = latitude;
				Constants.longitude = longitude;
				updatingPostionFromMap=true;
				savebuttonhide=true;
				
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

		// Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = null;
        try{
        location = locationManager.getLastKnownLocation(provider);
        }
        catch(Exception e){
        	e.printStackTrace();
        }

        if(location!=null)
        {
	        latlng = new LatLng(Constants.latitude , Constants.longitude);
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
        try{
        locationManager.requestLocationUpdates(provider, 20000, 0, (LocationListener) locationListener);
        }catch(Exception e){
        	e.printStackTrace();
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
