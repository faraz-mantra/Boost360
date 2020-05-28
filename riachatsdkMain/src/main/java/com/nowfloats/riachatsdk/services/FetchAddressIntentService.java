package com.nowfloats.riachatsdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.nowfloats.riachatsdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link ResultReceiver} in * MainActivity to process content
     * sent from this service.
     * <p>
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.LocationConstants.RECEIVER);

        // sent_check if receiver was properly registered.
        if (mReceiver == null) {
            //Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(Constants.LocationConstants.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = "No Location Data Provided";
            //Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.LocationConstants.FAILURE_RESULT, errorMessage, null);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (Exception e) {
            // Catch network or other I/O problems.
            errorMessage = "Service Not Available";
            //Log.e(TAG, errorMessage, e);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Address Not Found";
                //Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.LocationConstants.FAILURE_RESULT, errorMessage, null);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));

            }
            //Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.LocationConstants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
            //TextUtils.split(TextUtils.join(System.getProperty("line.separator"), addressFragments), System.getProperty("line.separator"));

        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = null;
        try {
            bundle  = new Bundle();
            bundle.putString(Constants.LocationConstants.RESULT_DATA_KEY, message);
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_AREA, address.getSubLocality());
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_CITY, address.getLocality());
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_STREET, address.getAddressLine(0) + ", " + address.getAddressLine(1));
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_COUNTRY, address.getCountryName());
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_PIN, address.getPostalCode());
            bundle.putString(Constants.LocationConstants.LOCATION_DATA_LOCALITY, address.getLocality());
            mReceiver.send(resultCode, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            mReceiver.send(resultCode, bundle);
        }
    }

    /*private void getAddress() {
        UIController.getReverseGeoCode(this, new SignupResponse(), new IResultListener<SignupResponse>() {
            @Override
            public void onResult(SignupResponse result) {
                if (result.success) {
                    if (result.responseContent != null) {
                        try {
                            JSONObject content = new JSONObject(result.responseContent);
                            JSONArray dataarray = content.getJSONArray("results");
                            if (dataarray != null && dataarray.length() > 0) {
                                JSONObject oneObject = dataarray.getJSONObject(0);
                                String address = oneObject.getString("formatted_address");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }*/
}