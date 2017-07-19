package com.nowfloats.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.accessbility.DataAccessibilityServiceV7;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.squareup.okhttp.OkHttpClient;
import com.thinksity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by Guru on 21-04-2015.
 */
public class Methods {
    public static SimpleDateFormat dateFormatDefault = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
    public static boolean isOnline(Activity context) {
        boolean status = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = connectivityManager.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
            if (!status){
                snackbarNoInternet(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            snackbarNoInternet(context);
            return false;
        }
        return status;
    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        { result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    public static void likeUsFacebook(Context context,String review){
        MixPanelController.track("LikeUsOnFacebook", null);
        Intent facebookIntent;
        //if(review.trim().length() == 0) {
            try {
                context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FACEBOOK_PAGE_WITH_ID));
            } catch (Exception e) {
                facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FACEBOOK_URL + review));
            }
       /* }else{
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FACEBOOK_URL + review));
        }*/

        facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            context.startActivity(facebookIntent);
        }catch (Exception e){
            Toast.makeText(context, "unable to open facebook", Toast.LENGTH_SHORT).show();
        }

    }
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + DataAccessibilityServiceV7.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            //Log.v("ggg", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("ggg", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            //Log.v("ggg", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    //Log.v("ggg", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        //Log.v("ggg", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            //Log.v("ggg", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }
    public static void showSnackBar(View view,String message,int color){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }
    public static void showSnackBar(Activity context,String msg){
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.GRAY);
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.GRAY) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void showSnackBarPositive(Activity context,String msg){
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.parseColor("#5DAC01"));
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#5DAC01")) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void showSnackBarNegative(Activity context,String msg){
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.parseColor("#E02200"));
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#E02200")) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void showSnackBarNegative(View mView,String msg){
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(mView, msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.parseColor("#E02200"));
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#E02200")) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void snackbarNoInternet(Activity context){
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), context.getString(R.string.noInternet), android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(Color.parseColor("#E02200"));
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(context.getString(R.string.noInternet)) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#E02200")) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
       ListAdapter listAdapter = listView.getAdapter();
       if (listAdapter == null)
           return;

       int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
       int totalHeight = 0;
       View view = null;
       for (int i = 0; i < listAdapter.getCount(); i++) {
           view = listAdapter.getView(i, view, listView);
           if (i == 0)
               view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ListView.LayoutParams.WRAP_CONTENT));

           view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
           totalHeight += view.getMeasuredHeight();
       }
       ViewGroup.LayoutParams params = listView.getLayoutParams();
       params.height = 120 + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
       listView.setLayoutParams(params);
       listView.requestLayout();
   }

    public static void launch(AppCompatActivity activity, View transitionView, Intent intent) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, "imageKey");
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
    public static void launchFromFragment(Activity activity, View transitionView, Intent intent) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, "imageKey");
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
    public static void hideKeyboard(EditText editText,Context context){
        try {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context) {
        try {

            if (context != null) {

                View view = ((Activity) context).getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void materialDialog(Activity activity, String title, String msg) {
        try {
            new MaterialDialog.Builder(activity)
                    .title(title)
                    .content(Methods.fromHtml(msg))
                    .positiveText("Ok")
                    .positiveColorRes(R.color.primaryColor)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            dialog.dismiss();
                        }
                    })
                    .build()
                    .show();
        }catch(Exception e){e.printStackTrace();}
    }

    public static String setDateFormat(String value,String formatType){
        try{
            Log.i("formatValue--",value);
            Long epochTime = Long.parseLong(value);
            Date date = new Date(epochTime);
            DateFormat dateFormat = new SimpleDateFormat(formatType);//dd/MM/yyyy HH:mm:ss
            dateFormat.setTimeZone(TimeZone.getDefault());
            value = dateFormat.format(date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static boolean compareDate(Date one,Date cur_date){
        try {
//            Date purchaseDate = dateFormatDefault.parse(one);
//            Date expiryDate = dateFormatDefault.parse(two);

            if (one.after(cur_date)) return true;
            else if(one.equals(cur_date)) return true;
            else return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFormattedDate(String Sdate) {
        String formatted = "",dateTime = "";
        if(TextUtils.isEmpty(Sdate)){
            return "";
        }
        if(Sdate.contains("/Date")){
            Sdate = Sdate.replace("/Date(", "").replace(")/", "");
        }

        Long epochTime = Long.parseLong(Sdate);
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);//dd/MM/yyyy HH:mm:ss
        format.setTimeZone(TimeZone.getDefault());
        dateTime = format.format(date);

        if (!Util.isNullOrEmpty(dateTime)) {
            String[] dateTemp;
            String hrsTemp;
            String amMarker;
            dateTemp = dateTime.split(" ");
            hrsTemp=dateTemp[1];
            amMarker = dateTemp[2];
            dateTemp = dateTemp[0].split("-");

            if (dateTemp.length > 0) {
                int month = Integer.parseInt(dateTemp[1]);
                switch (month) {
                    case 01:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Jan, " + dateTemp[2];
                        break;
                    case 2:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Feb, " + dateTemp[2];
                        break;
                    case 3:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Mar, " + dateTemp[2];
                        break;
                    case 4:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Apr, " + dateTemp[2];
                        break;
                    case 5:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " May, " + dateTemp[2];
                        break;
                    case 6:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " June, " + dateTemp[2];
                        break;
                    case 7:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " July, " + dateTemp[2];
                        break;
                    case 8:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Aug, " + dateTemp[2];
                        break;
                    case 9:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Sept, " + dateTemp[2];
                        break;
                    case 10:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Oct, " + dateTemp[2];
                        break;
                    case 11:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Nov, " + dateTemp[2];
                        break;
                    case 12:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " Dec, " + dateTemp[2];
                        break;
                }
            }
            formatted+=" at "+hrsTemp+" "+amMarker;
        }
        return formatted;
    }
    public static byte[] compressTobyte(String path,Activity act) {
        File img = new File(path);
        File f = new File(img.getAbsolutePath() + File.separator );
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bmp = Util.getBitmap(path,act);
        if((f.length()/1024)>1024){
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        } else{
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        }
        return bos.toByteArray();
    }

    public static String getCurrentTime() {
        String result = "";
        try{
            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);//dd/MM/yyyy HH:mm:ss
            dateFormat.setTimeZone(TimeZone.getDefault());
            result = dateFormat.format(Calendar.getInstance().getTime());
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static RestAdapter createAdapter(Context context,String url) throws IOException {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            // loading CAs from an InputStream
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.my_cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally {
                cert.close();
            }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());

            // creating a RestAdapter using the custom client
            return new RestAdapter.Builder()
                    .setEndpoint(url)
                    .setClient(new OkClient(okHttpClient))
                    .build();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dpToPx(int dp,Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public interface SmsApi{

        @Headers({"X-Authy-API-Key:"+Constants.TWILIO_AUTHY_API_KEY})
        @POST("/protected/json/phones/verification/start")
        void sendSms(@QueryMap Map hashMap, Callback<SmsVerifyModel> response);

        @Headers({"X-Authy-API-Key:"+Constants.TWILIO_AUTHY_API_KEY})
        @GET("/protected/json/phones/verification/check")
        void verifySmsCode(@QueryMap Map hashMap, Callback<SmsVerifyModel> response);
    }


    public static String getFormattedDate(long milliseconds) {

        Date date = new Date(milliseconds);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);//dd/MM/yyyy HH:mm:ss
        format.setTimeZone(TimeZone.getDefault());
        String dateTime = format.format(date);
        String formatted = "";
        if (!Util.isNullOrEmpty(dateTime)) {
            String[] dateTemp;
            dateTemp = dateTime.split(" ");
            dateTemp = dateTemp[0].split("-");

            if (dateTemp.length > 0) {
                int month = Integer.parseInt(dateTemp[1]);
                switch (month) {
                    case 01:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " January " + dateTemp[2];
                        break;
                    case 2:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " February " + dateTemp[2];
                        break;
                    case 3:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " March " + dateTemp[2];
                        break;
                    case 4:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " April " + dateTemp[2];
                        break;
                    case 5:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " May " + dateTemp[2];
                        break;
                    case 6:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " June " + dateTemp[2];
                        break;
                    case 7:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " July " + dateTemp[2];
                        break;
                    case 8:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " August " + dateTemp[2];
                        break;
                    case 9:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " September " + dateTemp[2];
                        break;
                    case 10:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " October " + dateTemp[2];
                        break;
                    case 11:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " November " + dateTemp[2];
                        break;
                    case 12:
                        dateTemp[0] = Util.AddSuffixForDay(dateTemp[0]);
                        formatted = dateTemp[0] + " December " + dateTemp[2];
                        break;
                }
            }
//            formatted += " at " + hrsTemp + " " + amMarker;
        }
        return "THURSDAY, " + formatted;
    }
}