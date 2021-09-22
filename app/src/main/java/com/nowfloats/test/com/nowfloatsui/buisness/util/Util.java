package com.nowfloats.test.com.nowfloatsui.buisness.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dell on 12-01-2015.
 */
public class Util {


    public static boolean toastFlag = true;
    static UserSessionManager session;
    private static HashMap<String, Integer> backgroundImages = new HashMap<String, Integer>();


    public static String getDataFromServer(String content,
                                           String requestMethod, String serverUrl) {
        String response = "";
        DataOutputStream outputStream = null;
        try {


            Thread.sleep(1000);
            URL new_url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());


            connection.setRequestProperty("Content-Type",
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            outputStream = new DataOutputStream(connection.getOutputStream());

            byte[] BytesToBeSent = content.getBytes();

            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            int responseCode = connection.getResponseCode();

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(
                        connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder responseContent = new StringBuilder();

                String temp = null;

                boolean isFirst = true;

                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append(Constants.NEW_LINE);
                    responseContent.append(temp);
                    isFirst = false;
                }

                response = responseContent.toString();

            } catch (Exception e) {
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }

        } catch (Exception ex) {

        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }

        return response;
    }


    public static String getDataFromServer(String content,
                                           String requestMethod, String serverUrl, String contentType) {
        String response = "", responseMessage = "";
        Boolean success = false;
        DataOutputStream outputStream = null;
        try {

            URL new_url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());

            connection.setRequestProperty("Content-Type", contentType);

            outputStream = new DataOutputStream(connection.getOutputStream());

            byte[] BytesToBeSent = content.getBytes();
            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            int responseCode = connection.getResponseCode();

            responseMessage = connection.getResponseMessage();

            if (responseCode == 200 || responseCode == 202) {
                success = true;

            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(
                        connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder responseContent = new StringBuilder();

                String temp = null;

                boolean isFirst = true;

                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append(Constants.NEW_LINE);
                    responseContent.append(temp);
                    isFirst = false;
                }

                response = responseContent.toString();

            } catch (Exception e) {
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }

        } catch (Exception ex) {
            success = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }

        return response;
    }


    public static String shortUrl(String serverDataFetchUri) {
//        Urlshortener.Builder builder = new Urlshortener.Builder (AndroidHttp.newCompatibleTransport(),
//                AndroidJsonFactory.getDefaultInstance(), null);
//        Urlshortener urlshortener = builder.build();
//
//        com.google.api.services.urlshortener.model.Url url = new com.google.api.services.urlshortener.model.Url();
//        url.setLongUrl(serverDataFetchUri);
//        try {
//            Urlshortener.Url.Insert insert=urlshortener.url().insert(url);
//            insert.setKey("AIzaSyCNXhsp0uvfEyS7RVXpDws4cjkpSWbo8iE ");
//            url = insert.execute();
//            return url.getId();
//        } catch (IOException e) {
//            //LogUtil.e(TAG, Log.getStackTraceString(e));
//            return serverDataFetchUri;
//        }
        String shortUrl = "";
        String serverResponse = "";
        try {
            // Set connection timeout to 5 secs and socket timeout to 10 secs
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpClient hc = new DefaultHttpClient(httpParameters);

//            HttpPost request = new HttpPost(
//                    "https://www.googleapis.com/urlshortener/v1/url");

            HttpPost request = new HttpPost(
                    "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyABcphoQlcCQnpMTSMhf1vNMWznZbHqwG8");
            request.setHeader("Content-type", "application/json");
            //request.setHeader("Content-type", "application/json");
            request.setHeader("Accept", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("longUrl", serverDataFetchUri);
            request.setEntity(new StringEntity(obj.toString(), "UTF-8"));

            HttpResponse response = hc.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                serverResponse = out.toString();
            } else {
                return serverDataFetchUri;
            }

            if (!Util.isNullOrEmpty(serverResponse)) {
                JSONObject data = new JSONObject(serverResponse);
                if (data != null) {
                    Constants.shortUrl = shortUrl = data.getString("id");
                }
            } else {
                Constants.shortUrl = shortUrl = serverDataFetchUri;
            }
        } catch (Exception e) {
            Constants.shortUrl = shortUrl = serverDataFetchUri;
        }
        return shortUrl;

    }


    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException,
            JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    public static String processDate(String tDate) {
        String formatted = "";
        String dateTime = "";
        String[] temp = null;
        String sDate = tDate.replace("/Date(", "").replace(")/", "");
        String[] splitDate = sDate.split("\\+");

        Long epochTime = Long.parseLong(splitDate[0]);

        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// dd/MM/yyyy
        // HH:mm:ss
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC+05:30"));
        if (date != null)
            dateTime = format.format(date);
        if (!Util.isNullOrEmpty(dateTime)) {
            temp = dateTime.split(" ");
            temp = temp[0].split("-");
        }
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

        return formatted;
    }


    public static String getFloatsInfoByAPICall(String apiURL) throws Exception {
        String response = "";
        HttpGet httpGet = null;
        InputStream content = null;
        DefaultHttpClient defaultHttpClient = null;
        BufferedReader buffer = null;

        try {
            defaultHttpClient = new DefaultHttpClient();
            httpGet = new HttpGet(apiURL);
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("Authorization", Utils.getAuthToken());
            HttpResponse execute = defaultHttpClient.execute(httpGet);
            content = execute.getEntity().getContent();
            buffer = new BufferedReader(new InputStreamReader(content));
            String json = "";
            while ((json = buffer.readLine()) != null) {
                response += json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                content.close();
            } catch (Exception e) {
            }
            try {
                buffer.close();
            } catch (Exception e) {
            }

        }
        return response;
    }


    public static boolean isNullOrEmpty(String value) {
        if (null == value) {
            return true;
        }

        value = value.trim();

        if ("null".equalsIgnoreCase(value) || value.length() == 0) {
            return true;
        }

        return false;
    }


    public static void toast(String mesg, Context ctx) {
        if (toastFlag) {
            Toast.makeText(ctx, mesg, Toast.LENGTH_SHORT).show();
            toastFlag = false;
            (new Thread() {
                public void run() {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    toastFlag = true;
                }
            }).start();
        }
    }

    public static void addBackgroundImages() {
        if (backgroundImages.size() == 0) {
            backgroundImages.put("GENERALSERVICES", R.drawable.general_services_background_img);
            /*backgroundImages.put("ARCHITECTURE",R.drawable.architecture_background_img);
            backgroundImages.put("AUTOMOTIVE",R.drawable.automotive_background_img);
            backgroundImages.put("BLOGS",R.drawable.blogs_background_img);
            backgroundImages.put("EDUCATION",R.drawable.education_background_img);
            backgroundImages.put("ELECTRONICS", R.drawable.electronics_background_img);
            backgroundImages.put("ENTERTAINMENT",R.drawable.entertainment_background_img);
            backgroundImages.put("EVENTS",R.drawable.events_background_img);
            backgroundImages.put("F&B-BAKERY",R.drawable.fb_bakery_background_img);
            backgroundImages.put("F&B-BARS",R.drawable.fb_bars_background_img);
            backgroundImages.put("F&B-CAFE",R.drawable.fb_cafe_background_img);
            backgroundImages.put("F&B-RESTAURANTS", R.drawable.fb_restaurants_background_img);
            backgroundImages.put("FASHION-APPAREL",R.drawable.fashion_apparel_background_img);
            backgroundImages.put("FASHION-FOOTWEAR",R.drawable.fashion_footwear_background_img);
            backgroundImages.put("FLOWERSSHOP",R.drawable.flower_shop_background_img);
            backgroundImages.put("FURNITURE",R.drawable.furniture_background_img);
            backgroundImages.put("GIFTS&NOVELTIES", R.drawable.gifts_novelties_background_img);
            backgroundImages.put("HEALTH&FITNESS",R.drawable.health_fitness_background_img);
            backgroundImages.put("HOMEAPPLICANCES",R.drawable.home_appliances_background_img);
            backgroundImages.put("HOMECARE",R.drawable.home_care_background_img);
            backgroundImages.put("HOMEMAINTENANCE",R.drawable.home_maintenance_background_img);
            backgroundImages.put("HOTEL&MOTELS",R.drawable.hotel_motels_background_img);
            backgroundImages.put("INTERIORDESIGN",R.drawable.interior_design_background_img);
            backgroundImages.put("MEDICAL-DENTAL",R.drawable.medical_dental_background_img);
            backgroundImages.put("MEDICAL-GENERAL",R.drawable.medical_general_background_img);
            backgroundImages.put("NATAURAL&AYURVEDA",R.drawable.natural_ayurveda_background_img);
            backgroundImages.put("KINDERGARTEN",R.drawable.kinder_garten_background_img);
            backgroundImages.put("PETS",R.drawable.pets_background_img);
            backgroundImages.put("PHOTOGRAPHY", R.drawable.photography_background_img);
            backgroundImages.put("REALESTATE&CONSTRUCTION",R.drawable.real_estate_construction_background_img);
            backgroundImages.put("SPA",R.drawable.spa_background_img);
            backgroundImages.put("SPORTS",R.drawable.sports_background_img);
            backgroundImages.put("TOURISM",R.drawable.tourism_background_img);
            backgroundImages.put("WATCHES&JEWELRY",R.drawable.watches_jewelry_background_img);
            backgroundImages.put("OTHERRETAIL", R.drawable.other_retail_background_img);*/
        }
    }

    public static void changeDefaultBackgroundImage(String category) {

        try {
            Constants.DefaultBackgroundImage = backgroundImages.get(category.replaceAll(" ", ""));
            //Constants.storedBackgroundImage = Constants.DefaultBackgroundImage ;
            if (SidePanelFragment.containerImage != null)
                SidePanelFragment.containerImage.setImageResource(Constants.DefaultBackgroundImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void GettingBackGroundId(final UserSessionManager session) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request

                String serverUri = Constants.GetBackgroundImage + "?clientId=" + Constants.clientId + "&fpId=" + session.getFPID();
                try {

                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpRequest = new HttpGet(serverUri);
                    httpRequest.setHeader("Authorization", Utils.getAuthToken());
                    HttpResponse responseOfSite = client.execute(httpRequest);
                    HttpEntity entity = (HttpEntity) ((HttpResponse) responseOfSite).getEntity();
                    if (entity != null) {
                        String str = (EntityUtils.toString(entity));
                        JSONArray bgjsonarray = new JSONArray(str);
                        if ((bgjsonarray) != null) {
                            int len = bgjsonarray.length();
                            String storedBackgroundImage = bgjsonarray.getString(len - 1);
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, storedBackgroundImage);
                        } else {
                            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, "");
                        }
                    } else {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, "");
                    }
                } catch (Exception e) {
                    System.out.println();

                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public static boolean deleteWithBody(String url, String content) {
        boolean flag = false;
        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        del.setHeader("Authorization", Utils.getAuthToken());
        StringEntity se;
        try {
            se = new StringEntity(content, HTTP.UTF_8);
            se.setContentType("application/json");

            del.setEntity(se);

            HttpResponse response = httpclient.execute(del);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            StatusLine status = response.getStatusLine();

            if (status.getStatusCode() == 200) {
                flag = true;
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return flag;
    }


    public static void storeImageInTemp(String storeImageUri, Context ctx) throws IOException {

        File outputFile = null;
        String baseName = Constants.NOW_FLOATS_API_URL + "" + storeImageUri;
        URL wallpaperURL = null;
        try {
            wallpaperURL = new URL(baseName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            URLConnection connection = wallpaperURL.openConnection();
            connection.setRequestProperty("Authorization", Utils.getAuthToken());
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
        File cacheDir = new File(getAppPicCacheDir((Activity) ctx));//  getCacheFolder(ctx);
        File cacheFile = new File(cacheDir, "localFileName.jpg");
        FileOutputStream outputStream = new FileOutputStream(cacheFile);
        Log.d("Cache Dir", "Base Name : " + baseName + " wallpaper : " + wallpaperURL.getPath());
        Log.d("Cache Dir", "######### Cache Dir #######" + cacheFile.getPath());
        byte buffer[] = new byte[1024];
        int dataSize;
        int loadedSize = 0;
        while ((dataSize = inputStream.read(buffer)) != -1) {
            loadedSize += dataSize;
            //publishProgress(loadedSize);
            outputStream.write(buffer, 0, dataSize);
        }
        Log.d("Cache Dir", "Data Size : " + dataSize + " " + outputStream);
        outputStream.close();
    }


    public static File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }

        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }

        return cacheDir;
    }

    public static String AddSuffixForDay(String originalDay) {
        String day = "";
        if (originalDay.startsWith("0"))
            originalDay = originalDay.replace("0", "");

//        if (originalDay.equals("01") || originalDay.equals("1")
//                || originalDay.equals("21") || originalDay.equals("31"))
//            day = originalDay + "";
//        else if (originalDay.equals("02") || originalDay.equals("2")
//                || originalDay.equals("22"))
//            day = originalDay + "";
//        else if (originalDay.equals("03") || originalDay.equals("3")
//                || originalDay.equals("23"))
//            day = originalDay + "";
//
//        else
        day = originalDay + "";

        return day;
    }

    public static JSONArray extractUrls(String value) throws Exception {
        if (value == null)
            throw new Exception("urls to extract");
        JSONArray result = new JSONArray();
        String urlPattern = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = null;
        while (value.length() > 0) {
            m = p.matcher(value);
            if (m.find()) {
                String pre = value.substring(0, m.start(0));
                String url = value.substring(m.start(0), m.end(0));
                value = value.substring(m.end(0));
                result.put(pre.trim());
                result.put(url.trim());
            } else {
                result.put(value.trim());
                break;
            }
        }

        return result;
    }

    public static String getPicCacheDir(Activity app) {
        String tempDir = null;
        String dir = Constants.dataDirectory;
        File sdcard = app.getExternalCacheDir();
        Log.d("sdCard", "sdCard : " + sdcard);
        File pictureDir = new File(sdcard, dir);
        tempDir = pictureDir.getAbsolutePath();
        return tempDir;
    }

    public static String getAppPicCacheDir(Activity app) {

        String tempDir = null;
        File sdcard = new File(getPicCacheDir(app));
        tempDir = sdcard.getAbsolutePath();
        return tempDir;

    }

    public static String saveBitmap(Bitmap bitmap, Activity app, String name) {
        String imgpath = "fail";
        try {

            String baseName = name + ".jpg";
            File pictureDir = new File(getPicCacheDir(app));
            Log.d("pic Dir ", "pictureDir : " + pictureDir);
            pictureDir.mkdirs();
            Log.d("Util", "pictureDir : " + pictureDir);
            File f = new File(pictureDir, baseName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            imgpath = f.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError E) {
            //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
            E.printStackTrace();
            System.gc();
            // toast("Uh oh. Something went wrong. Please try again", app);
        }
        Log.d("Util", "imagePath : " + imgpath);
        return imgpath;
    }

    public static String saveCameraBitmap(Bitmap bitmap, Activity app, String name) {
        String imgpath = "fail";
        try {
            String baseName = name + ".jpg";
            File pictureDir = new File(getPicCacheDir(app));
            pictureDir.mkdirs();
            File f = new File(pictureDir, baseName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
            fos.flush();
            fos.close();
            imgpath = f.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError E) {
            //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
            E.printStackTrace();
            System.gc();
            // toast("Uh oh. Something went wrong. Please try again", app);
        }
        return imgpath;
    }

    public static String saveBitmap(String path, Activity app, String fname) {
        return saveBitmap(path, app, 720, fname);
    }

    public static String saveBitmap(String path, Activity app, int mWidth,
                                    String fname) {
        Log.d("saveBitmap", "saveBitmap : " + path);
        String imgpath = "fail";

        try {

            Bitmap bitmap = getBitmap(path, app, mWidth);
            imgpath = saveBitmap(bitmap, app, fname);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgpath;
    }

    public static String getAppPicDir() {
        String tempDir = null;
        String dir = Constants.dataDirectory;
        File sdcard = Environment.getExternalStorageDirectory();
        File pictureDir = new File(sdcard, dir);
        tempDir = pictureDir.getAbsolutePath();
        return tempDir;
    }

    public static String saveCameraBitmap(String path, Activity app, int mWidth,
                                          String fname) {
        String imgpath = "fail";

        try {

            Bitmap bitmap = getBitmap(path, app, mWidth);
            imgpath = saveCameraBitmap(bitmap, app, fname);

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError E) {
            //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
            E.printStackTrace();
            System.gc();
            // toast("Uh oh. Something went wrong. Please try again", app);
        }
        return imgpath;
    }

    public static Bitmap getBitmap(String path, Activity app) {
        return getBitmap(path, app, 720);
    }

    public static float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(uri, projection,
                    null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(exif
                        .getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                // Util.log("Error checking exif");
            }
        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // Read bitmap
    public static Bitmap readBitmap(Uri selectedImage, Activity act) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = act.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getBitmap(String path, Activity app, int mWidth) {
        if (path == null) {
            return null;
        }
        Uri uri = Uri.fromFile(new File(path));
        float rotation = Util.rotationForImage(app, uri);
        Matrix matrix = new Matrix();

        String TAG = "GetBitmap";
        InputStream in = null;
        try {
            final int IMAGE_MAX_WIDTH = mWidth;
            ContentResolver mContentResolver = app.getContentResolver();
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;

            if (o.outWidth > mWidth) {
                scale = Math.round((float) o.outWidth / (float) mWidth);
            }
            BitmapFactory.Options optionsOut = new BitmapFactory.Options();

            optionsOut.inSampleSize = scale;
            optionsOut.inPurgeable = true;//

            Bitmap b = null;
            in = mContentResolver.openInputStream(uri);
            b = BitmapFactory.decodeStream(in, null, optionsOut);
            if (rotation != 0f) {
                matrix.preRotate(rotation);
            }
            if (b != null) {
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }
            System.gc();
            in.close();
            return b;
        } catch (IOException e) {
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPicture(Uri uri, Activity ctx) {
        String path = "";
        Bitmap bmp;
        path = getPath(uri, ctx);
        path = Util.saveBitmap(path, ctx, 720,
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + System.currentTimeMillis());
        if (!Util.isNullOrEmpty(path)) {
            bmp = Util.getBitmap(path, ctx);
            if (bmp != null) {
                // bmp = Bitmap.createScaledBitmap(bmp, 300, 300,true);
                //RoundCorners_image roundCorner = new RoundCorners_image();
                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 30);

                // msg.setHint("Add some text to give context to the picture.");
                String eol = System.getProperty("line.separator");
                // updateHint.setText("CHANGE" + eol + "PHOTO");
            }
        }
    }

    public String getPath(Uri uri, Activity ctx) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ctx.managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
        }
        return null;
    }

}
