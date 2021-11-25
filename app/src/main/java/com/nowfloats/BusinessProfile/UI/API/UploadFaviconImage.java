package com.nowfloats.BusinessProfile.UI.API;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.framework.analytics.SentryController;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class UploadFaviconImage extends AsyncTask<Void, String, String> {
    boolean isUploadingSuccess = false;
    private String path;
    private String url;
    private OnImageUpload listener;
    private String response;

    public UploadFaviconImage(String path, String url) {
        this.path = path;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {

        listener.onPreUpload();
    }

    @Override
    protected void onPostExecute(String result) {

        listener.onPostUpload(isUploadingSuccess, response);
    }

    @Override
    protected String doInBackground(Void... params) {
        uploadImage(path);
        return null;
    }


    private void uploadImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }

        File img = new File(imagePath);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        File f = new File(img.getAbsolutePath() + File.separator);

        try {
            f.createNewFile();
        } catch (IOException e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        if (bmp != null) {
            if ((f.length() / 1024) > 100) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
        }

        sendDataToServer(url, bos.toByteArray());
    }


    private void sendDataToServer(String url, byte[] BytesToBeSent) {
        DataOutputStream outputStream = null;

        try {

            URL new_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(Constants.HTTP_PUT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());

            connection.setRequestProperty("Content-Type", Constants.BG_SERVICE_CONTENT_TYPE_OCTET_STREAM);

            if (BytesToBeSent != null) {
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200 || responseCode == 202) {
                isUploadingSuccess = true;
            }

            InputStreamReader inputStreamReader;
            BufferedReader bufferedReader;

            inputStreamReader = new InputStreamReader(connection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder responseContent = new StringBuilder();

            String temp;

            boolean isFirst = true;

            while ((temp = bufferedReader.readLine()) != null) {
                if (!isFirst) {
                    responseContent.append(Constants.NEW_LINE);
                }

                responseContent.append(temp);
                isFirst = false;
            }

            response = TextUtils.isEmpty(responseContent.toString()) ? "" : responseContent.toString();

            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            SentryController.INSTANCE.captureException(ex);
            ex.printStackTrace();
        }
    }

    public void setUploadListener(OnImageUpload listener) {
        this.listener = listener;
    }

    public interface OnImageUpload {
        void onPreUpload();

        void onPostUpload(boolean isSuccess, String response);
    }
}
