package com.nowfloats.riachatsdk.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by admin on 6/30/2017.
 */

public class NFGeoCoder {

    private Context mContext;

    public NFGeoCoder(Context mContext) {
        this.mContext = mContext;
    }

    public LatLng reverseGeoCode(String... param) {

        Geocoder gc = new Geocoder(mContext);
        LatLng latLong = null;
        if (gc.isPresent()) {
            List<Address> list = null;
            try {
                Address address = null;
                list = gc.getFromLocationName(param[0] + ","
                        + param[1] + "," + param[2], 10);

                double lat = 0, lng = 0;
                if (list != null && list.size() > 0) {

                    address = list.get(list.size() - 1);
                    lat = address.getLatitude();
                    lng = address.getLongitude();
                    latLong = new LatLng(lat, lng);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return latLong;
    }

//    public LatLng reverseGeoCodeApi(String... param) {
//
//        String address = param[0] + ","
//                + param[1] + "," + param[2] + "," + param[3];
//        try {
//            address = URLEncoder.encode(address, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String requesturl = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=" + address;
//
//        System.out.println("Request " + requesturl);
//        DefaultHttpClient client = new DefaultHttpClient();
//        System.out.println("hello");
//
//        HttpGet req = new HttpGet(requesturl);
//        System.out.println("hello");
//
//        try {
//            HttpResponse res = client.execute(req);
//            StatusLine status = res.getStatusLine();
//            int code = status.getStatusCode();
//            System.out.println(code);
//            if (code != 200) {
//                System.out.println("Request Has not succeeded");
//            }
//
//            HttpEntity jsonentity = res.getEntity();
//            InputStream in = jsonentity.getContent();
//
//            JSONObject jsonobj = new JSONObject(convertStreamToString(in));
//
//
//            JSONArray resarray = jsonobj.getJSONArray("results");
//
//            if (resarray.length() == 0) {
//            } else {
//                int len = resarray.length();
//
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String convertStreamToString(InputStream in) {
//        BufferedReader br = new BufferedReader(new InputStreamReader(in));
//        StringBuilder jsonstr = new StringBuilder();
//        String line;
//        try {
//            while ((line = br.readLine()) != null) {
//                String t = line + "\n";
//                jsonstr.append(t);
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return jsonstr.toString();
//    }
}
