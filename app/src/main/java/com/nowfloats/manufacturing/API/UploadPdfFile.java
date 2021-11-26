package com.nowfloats.manufacturing.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.nowfloats.manufacturing.digitalbrochures.Interfaces.DigitalBrochuresDetailsListener;
import com.nowfloats.manufacturing.projectandteams.Interfaces.TeamsDetailsListener;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by NowFloatsDev on 29/05/2015.
 */
public class UploadPdfFile extends AsyncTask<Void, String, String> {

    private final InputStream path;
    Activity appContext;
    String fileName;
    ProgressDialog pd = null;
    DigitalBrochuresDetailsListener listener;
    boolean isUploadingSuccess = false;

    public UploadPdfFile(Activity context, DigitalBrochuresDetailsListener listener, InputStream path, String fileName) {
        this.appContext = context;
        this.path = path;
        this.fileName = fileName;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        appContext.runOnUiThread(() -> {
            pd = ProgressDialog.show(appContext, "", "Uploading Logo...");
            pd.setCancelable(false);
        });
    }


    @Override
    protected void onPostExecute(String result) {
        appContext.runOnUiThread(() -> {
            pd.dismiss();
            try {
                listener.UploadedPdfURL(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    protected String doInBackground(Void... params) {
        return uploadFileToServer(path, fileName);
    }

    public String uploadFileToServer(InputStream path, String fileName) {
        try {
            OkHttpClient client = new OkHttpClient();
            InputStream in = path;
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("application/pdf"), buf))
                    .build();

            //https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=screenshot-assuredpurchase.withfloats.com-2020.07.17-14_38_42.png
            Request request = new Request.Builder()
                    .url("https://webaction.api.boostkit.dev/api/v1/placesaround/upload-file?assetFileName=" + fileName + ".pdf")
                    .post(requestBody)
                    .addHeader("Authorization", "59c8add5dd304111404e7f04")
                    .build();

            Response response = client.newCall(request).execute();
            if (response != null && response.code() == 200) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                Methods.showSnackBarNegative(appContext, appContext.getString(R.string.uploading_image_failed));
            }

            in.close();
            buf = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
