package com.nowfloats.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class GetStoreFrontImageAsyncTask extends AsyncTask<Void, String, String> {
    public String url;
    ImageView iv;
    ProgressDialog pd;
    LinearLayout websiteScreen, detailsScreen;
    private Activity appContext = null;
    private String response = "";
    private String imageUrl = "";
    private String callInitiatedFrom = "";

    public GetStoreFrontImageAsyncTask(Activity context, String imageUrl, String callInitiatedFrom) {
        this.appContext = context;
        this.imageUrl = imageUrl;
        this.callInitiatedFrom = callInitiatedFrom;
    }

    public GetStoreFrontImageAsyncTask(Activity context, String imageUrl) {
        this.appContext = context;
        this.imageUrl = imageUrl;
    }

    public GetStoreFrontImageAsyncTask(Activity context, String imageUrl, ImageView iv) {
        this.appContext = context;
        this.imageUrl = imageUrl;
        this.iv = iv;
    }

    public static String getAppPicDir() {
        String tempDir = null;
        String dir = Constants.dataDirectory;
        File sdcard = Environment.getExternalStorageDirectory();
        File pictureDir = new File(sdcard, dir);
        tempDir = pictureDir.getAbsolutePath();
        return tempDir;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(appContext, null, "getting your store image...");
        pd.setCancelable(true);
    }

    @Override
    protected void onPostExecute(String abc) {
        if (pd != null) {
            pd.dismiss();
        }
        if (!Util.isNullOrEmpty(abc)) {
            if (iv != null) {
                Bitmap bmp = BitmapFactory.decodeFile(abc);
                iv.setImageBitmap(bmp);
                iv.invalidate();
            }
        } else {

        }

    }

    @Override
    protected String doInBackground(Void... params) {
        String img_path = "";
        try {
            String serverUri = Constants.NOW_FLOATS_API_URL + "/" + imageUrl;
            Bitmap response = null;
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(serverUri);
            httpGet.setHeader("Authorization", Utils.getAuthToken());
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();
            response = BitmapFactory.decodeStream(content);
            Log.d("Downloaded Image", " Image Saved : " + response);
            if (response != null) {
                img_path = saveBitmap(response);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img_path;
    }

    public JSONArray getJsonArray(JSONObject store, String key) {
        JSONArray val = new JSONArray();
        if (checkForNull(store, key)) {
            try {
                val = store.getJSONArray(key);
                if (val.length() != 0) {

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        return val;

    }

    public boolean checkForNull(JSONObject store, String key) {
        boolean flag = true;

        try {
            String str = store.getString(key);
            if (str.equals("null") || str.equals("NULL") || str.length() == 0) {
                flag = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            flag = false;
        }

        return flag;
    }

    public String saveBitmap(Bitmap bitmap) {
        String imgpath = "";
        try {
            String baseName = imageUrl;
            if (baseName.contains("FP/Logos/Actual/")) {
                baseName = baseName.replace("FP/Logos/Actual/", "");
            } else {
                baseName = baseName.replace("FP/Tile/", "");
            }

            File pictureDir = new File(Util.getAppPicDir());
            pictureDir.mkdirs();
            Log.d("Dir Picture ", "pictureDir : " + pictureDir);
            File f = new File(pictureDir, baseName);
            Log.d("Dir Picture ", "pictureDir : " + f.getPath() + " , " + f);
            if (!f.exists()) {
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
                imgpath = f.getAbsolutePath();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Download Image", "downloaded Image path : " + imgpath);
        return imgpath;
    }

}
