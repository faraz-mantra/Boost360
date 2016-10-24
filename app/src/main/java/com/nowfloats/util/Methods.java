package com.nowfloats.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
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
import java.util.TimeZone;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Guru on 21-04-2015.
 */
public class Methods {
    public static SimpleDateFormat dateFormatDefault = new SimpleDateFormat("dd/MM/yyyy");
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

    public static void showSnackBar(Activity context,String msg){
        SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.GRAY) // change the background color
                ,context); // activity where it is displayed
    }

    public static void showSnackBarPositive(Activity context,String msg){
        SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#5DAC01")) // change the background color
                ,context); // activity where it is displayed
    }

    public static void showSnackBarNegative(Activity context,String msg){
        SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#E02200")) // change the background color
                ,context); // activity where it is displayed
    }

    public static void snackbarNoInternet(Activity context){
        SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(context.getString(R.string.noInternet)) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#E02200")) // change the background color
                ,context); // activity where it is displayed
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

    public static void materialDialog(Activity activity,String title,String msg){
        try{
            new MaterialDialog.Builder(activity)
                    .title(title)
                    .content(msg)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.primaryColor)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            dialog.dismiss();
                        }
                    }).show();
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
        if(Sdate.contains("/Date")){
            Sdate = Sdate.replace("/Date(", "").replace(")/", "");
        }

        Long epochTime = Long.parseLong(Sdate);
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy HH:mm:ss
        format.setTimeZone(TimeZone.getDefault());
        if (date != null)
            dateTime = format.format(date);
        if (!Util.isNullOrEmpty(dateTime)) {
            String[] temp = dateTime.split(" ");
            temp = temp[0].split("-");
            if (temp.length > 0) {
                int month = Integer.parseInt(temp[1]);
                switch (month) {
                    case 01:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " January, " + temp[2];
                        break;
                    case 2:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " February, " + temp[2];
                        break;
                    case 3:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " March, " + temp[2];
                        break;
                    case 4:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " April, " + temp[2];
                        break;
                    case 5:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " May, " + temp[2];
                        break;
                    case 6:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " June, " + temp[2];
                        break;
                    case 7:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " July, " + temp[2];
                        break;
                    case 8:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " August, " + temp[2];
                        break;
                    case 9:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " September, " + temp[2];
                        break;
                    case 10:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " October, " + temp[2];
                        break;
                    case 11:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " November, " + temp[2];
                        break;
                    case 12:
                        temp[0] = Util.AddSuffixForDay(temp[0]);
                        formatted = temp[0] + " December, " + temp[2];
                        break;
                }
            }
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
            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");//dd/MM/yyyy HH:mm:ss
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

}