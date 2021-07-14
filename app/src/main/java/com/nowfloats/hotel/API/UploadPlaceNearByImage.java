package com.nowfloats.hotel.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.nowfloats.hotel.Interfaces.PlaceNearByDetailsListener;
import com.nowfloats.util.Methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by NowFloatsDev on 29/05/2015.
 */
public class UploadPlaceNearByImage extends AsyncTask<Void, String, String> {

    Activity appContext;
    String path, fileName;
    ProgressDialog pd = null;
    PlaceNearByDetailsListener listener;
    boolean isUploadingSuccess = false;

    public UploadPlaceNearByImage(Activity context, PlaceNearByDetailsListener listener, String path, String fileName) {
        this.appContext = context;
        this.path = path;
        this.fileName = fileName;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        appContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = ProgressDialog.show(appContext, "", "Uploading Logo...");
                pd.setCancelable(false);
            }
        });
    }


    @Override
    protected void onPostExecute(String result) {
        appContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                try {
                    listener.uploadImageURL(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    protected String doInBackground(Void... params) {
        return uploadFileToServer(path, fileName);
    }

    public String uploadFileToServer(String path, String fileName) {
        File file = new File(path);
        try {
            OkHttpClient client = new OkHttpClient();
            InputStream in = new FileInputStream(file);
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/*"), buf))
                    .build();

            //https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=screenshot-assuredpurchase.withfloats.com-2020.07.17-14_38_42.png
            Request request = new Request.Builder()
                    .url("https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=screenshot-assuredpurchase.withfloats.com-" + fileName + ".jpg")
                    .post(requestBody)
                    .addHeader("Authorization", "59c8add5dd304111404e7f04")
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (response != null && response.code() == 200) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                Methods.showSnackBarNegative(appContext, "Uploading Image Failed");
            }

            in.close();
            buf = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
