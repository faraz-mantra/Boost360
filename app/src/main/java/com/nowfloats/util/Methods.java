package com.nowfloats.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.squareup.okhttp.OkHttpClient;
import com.thinksity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
import java.util.List;
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
    public static SimpleDateFormat dateFormatDefault = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

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
            if (!status) {
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
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void likeUsFacebook(Context context, String review) {
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

        facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            context.startActivity(facebookIntent);
        } catch (Exception e) {
            Toast.makeText(context, "unable to open facebook", Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isAccessibilitySettingsOn(Context mContext) {
//        int accessibilityEnabled = 0;
//        final String service = mContext.getPackageName() + "/" + DataAccessibilityServiceV8.class.getCanonicalName();
//        try {
//            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
//                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
//            //Log.v("ggg", "accessibilityEnabled = " + accessibilityEnabled);
//        } catch (Settings.SettingNotFoundException e) {
//            Log.e("ggg", "Error finding setting, default accessibility to not found: "
//                    + e.getMessage());
//        }
//        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
//
//        if (accessibilityEnabled == 1) {
//            //Log.v("ggg", "***ACCESSIBILITY IS ENABLED*** -----------------");
//            String settingValue = Settings.Secure.getString(
//                    mContext.getApplicationContext().getContentResolver(),
//                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//            if (settingValue != null) {
//                mStringColonSplitter.setString(settingValue);
//                while (mStringColonSplitter.hasNext()) {
//                    String accessibilityService = mStringColonSplitter.next();
//
//                    //Log.v("ggg", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
//                    if (accessibilityService.equalsIgnoreCase(service)) {
//                        //Log.v("ggg", "We've found the correct setting - accessibility is switched on!");
//                        return true;
//                    }
//                }
//            }
//        } else {
//            //Log.v("ggg", "***ACCESSIBILITY IS DISABLED***");
//        }
//
//        return false;

        boolean accessibilityServiceEnabled = false;
        AccessibilityManager am = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo service : runningServices) {
            if (service.getResolveInfo().serviceInfo.packageName.equals(mContext.getPackageName())) {
                accessibilityServiceEnabled = true;
            }
        }
        return accessibilityServiceEnabled;
    }
    public static boolean isMyAppOpen(Context mContext){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if(taskInfo != null && taskInfo.size()>0) {
            //Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return mContext.getPackageName().equalsIgnoreCase(componentInfo.getPackageName());
        }
        return false;
    }

    public static void showDialog(Context mContext, String title, String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle(title).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static boolean isMyActivityAtTop(Context mContext){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if(taskInfo != null && taskInfo.size()>0) {
            //Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            return  mContext.getClass().getName().equalsIgnoreCase(taskInfo.get(0).topActivity.getClassName());
//            return mContext.getPackageName().equalsIgnoreCase(componentInfo.getPackageName());
        }
        return false;
    }

    public static boolean isMyActivityInStack(Context mContext){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if(taskInfo != null && taskInfo.size()>0) {
            //Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName())
            for (ActivityManager.RunningTaskInfo info :taskInfo)
            return  mContext.getClass().getName().equalsIgnoreCase(info.topActivity.getClassName());
//            return mContext.getPackageName().equalsIgnoreCase(componentInfo.getPackageName());
        }
        return false;
    }

    public static void showSnackBar(View view, String message, int color) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }

    public static void showSnackBar(Activity context, String msg) {
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

    public static void showSnackBarPositive(Activity context, String msg) {
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_positive_color));
        snackBar.show();
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(msg) // text to be displayed
                        .textColor(Color.WHITE) // change the text color
                        .color(Color.parseColor("#5DAC01")) // change the background color
                ,context); // activity where it is displayed*/
    }

    public static void showSnackBarNegative(Activity context, String msg) {
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_negative_color));
        snackBar.show();
    }

    public static void showFeatureNotAvailDialog(final Context context){
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.features_not_available))
                .content(context.getString(R.string.buy_light_house_plan))
                .positiveText(context.getString(R.string.buy))
                .negativeText(context.getString(R.string.later))
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.light_gray)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        context.startActivity(new Intent(context,NewPricingPlansActivity.class));
                    }
                }).show();
    }
    public static void showSnackBarNegative(View mView, String msg) {
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(mView, msg, android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(mView.getContext(), R.color.snackbar_negative_color));
        snackBar.show();
    }

    public static void snackbarNoInternet(Activity context) {
        android.support.design.widget.Snackbar snackBar = android.support.design.widget.Snackbar.make(context.findViewById(android.R.id.content), context.getString(R.string.noInternet), android.support.design.widget.Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_negative_color));
        snackBar.show();
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

    public static void hideKeyboard(EditText editText, Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasOverlayPerm(Context mContext) {

        boolean isOverlayApplicable = false;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            isOverlayApplicable = Settings.canDrawOverlays(mContext);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isOverlayApplicable = canDrawOverlaysUsingReflection(mContext);
        }

        return isOverlayApplicable;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean canDrawOverlaysUsingReflection(Context context) {

        try {

            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
//AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {
            return false;
        }

    }

    public static String setDateFormat(String value, String formatType) {
        try {
            Log.i("formatValue--", value);
            Long epochTime = Long.parseLong(value);
            Date date = new Date(epochTime);
            DateFormat dateFormat = new SimpleDateFormat(formatType);//dd/MM/yyyy HH:mm:ss
            dateFormat.setTimeZone(TimeZone.getDefault());
            value = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
        private int margin;
        private int lines;

        public MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        /* Возвращает значение, на которе должен быть добавлен отступ */
        @Override
        public int getLeadingMargin(boolean first) {
            if (first) {
            /*
             * Данный отступ будет применен к количеству строк
             * возвращаемых getLeadingMarginLineCount()
             */
                return margin;
            } else {
                // Отступ для всех остальных строк
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {}

        /*
         * Возвращает количество строк, к которым должен быть
         * применен отступ возвращаемый методом getLeadingMargin(true)
         * Замечание:
         * Отступ применяется только к N строкам первого параграфа.
         */
        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    }

    public static boolean compareDate(Date next, Date prev) {
        try {
//            Date purchaseDate = dateFormatDefault.parse(one);
//            Date expiryDate = dateFormatDefault.parse(two);

            if (next.after(prev)) return true;
            else if (next.equals(prev)) return true;
            else return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFormattedDate(String date, String format){
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        return getFormattedDate(getDateMillSecond(date),format);
    }
    public static Long getDateMillSecond(String date){
        String[]dateTime = null;
        long dateMilliseconds = 0;
        if (date.contains("/Date")) {
            date = date.replace("/Date(", "").replace(")/","");
        }

        if(date.contains("+")) {
            dateTime = date.split("\\+");
            if (dateTime[1].length() > 1) {
                dateMilliseconds += Integer.parseInt(dateTime[1].substring(0, 2)) * 60 * 60 * 1000;
            }
            if (dateTime[1].length() > 3) {
                dateMilliseconds += Integer.parseInt(dateTime[1].substring(2, 4)) * 60 * 1000;
            }
            dateMilliseconds += Long.valueOf(dateTime[0]);
        }else{
            dateMilliseconds += Long.valueOf(date);
        }
        return dateMilliseconds;
    }
    public static String getFormattedDate(long epochTime, String format){
        Date date1 = new Date(epochTime);
        DateFormat format1 = new SimpleDateFormat(format, Locale.ENGLISH);//dd/MM/yyyy HH:mm:ss
        format1.setTimeZone(TimeZone.getDefault());
        return format1.format(date1);
    }
    public static void showApplicationPermissions(String title, String content, final Context appContext){

            new MaterialDialog.Builder(appContext)
                    .content(content)
                    .title(title)
                    .positiveColorRes(R.color.primaryColor)
                    .negativeColorRes(R.color.light_gray)
                    .negativeText("Cancel")
                    .positiveText("Take Me There")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", appContext.getPackageName(), null);
                            intent.setData(uri);
                            appContext.startActivity(intent);
                        }
                    })
                    .build().show();
    }
    public static String getFormattedDate(String Sdate) {
        String formatted = "", dateTime = "";
        if (TextUtils.isEmpty(Sdate)) {
            return "";
        }
        if (Sdate.contains("/Date")) {
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
            hrsTemp = dateTemp[1];
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
            formatted += " at " + hrsTemp + " " + amMarker;
        }
        return formatted;
    }

    public static byte[] compressToByte(String path, Activity act) {
        File img = new File(path);
        File f = new File(img.getAbsolutePath() + File.separator);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bmp = Util.getBitmap(path, act);
        if ((f.length() / 1024) > 1024) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        } else {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        }
        return bos.toByteArray();
    }

    public static String getCurrentTime() {
        String result = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);//dd/MM/yyyy HH:mm:ss
            dateFormat.setTimeZone(TimeZone.getDefault());
            result = dateFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        boolean isMyServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        if (list != null) {
            for (ActivityManager.RunningServiceInfo service : list) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    isMyServiceRunning = true;
                }
            }
        }
        return isMyServiceRunning;
    }

    public static boolean isUserLoggedIn(Context context){
        DataBase db =new DataBase(context);
        Cursor cursor = db.getLoginStatus();
        if (cursor!=null && cursor.getCount()>0){
            if (cursor.moveToNext()){
                String LoginStatus = cursor.getString(0);
                return LoginStatus != null && LoginStatus.equals("true");
            }
        }
        return false;
    }
    public static RestAdapter createAdapter(Context context, String url) throws IOException {
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

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public interface SmsApi {

        @Headers({"X-Authy-API-Key:" + Constants.TWILIO_AUTHY_API_KEY})
        @POST("/protected/json/phones/verification/start")
        void sendSms(@QueryMap Map hashMap, Callback<SmsVerifyModel> response);

        @Headers({"X-Authy-API-Key:" + Constants.TWILIO_AUTHY_API_KEY})
        @GET("/protected/json/phones/verification/check")
        void verifySmsCode(@QueryMap Map hashMap, Callback<SmsVerifyModel> response);
    }

    public static void makeCall(Context mContext,String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            mContext.startActivity(Intent.createChooser(callIntent, "Call by:"));
        }catch(Exception e){
            Toast.makeText(mContext, "Unable to make call", Toast.LENGTH_SHORT).show();
        }

    }
    public static void sendEmail( Context context, String[] email){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }else {
            Toast.makeText(context, "Unable to send email", Toast.LENGTH_SHORT).show();
        }


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
        return formatted;
    }

    public static Bitmap decodeSampledBitmap(String uri, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri, options);
    }
    public static Bitmap decodeSampledBitmap(Resources res,int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;
        options.inJustDecodeBounds = false;
        return  BitmapFactory.decodeResource(res,resId,options);
    }
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}