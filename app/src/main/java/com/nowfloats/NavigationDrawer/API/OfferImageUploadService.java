package com.nowfloats.NavigationDrawer.API;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NowFloats on 04-11-2016.
 */

public class OfferImageUploadService extends AsyncTask<String, Void, String> {



    private OfferImageUploadComplete offerImageUploadComplete;

    public OfferImageUploadService(OfferImageUploadComplete offerImageUploadComplete){
        this.offerImageUploadComplete = offerImageUploadComplete;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = startUpload(params[0], params[1]);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        offerImageUploadComplete.onUploadCompletion(s);
    }

    private String startUpload(String filename, String targetUrl){
        String response = "error";

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String pathToOurFile = filename;
        String urlServer = targetUrl;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            connection.setRequestMethod("PUT");

            connection.setRequestProperty("Connection", "Keep-Alive");

            outputStream = new DataOutputStream(connection.getOutputStream());

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.e("Image length", bytesAvailable + "");
            try {
                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        response = "outofmemoryerror";
                        return response;
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
                return response;
            }
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.i("Server Response Code ", "" + serverResponseCode);
            Log.i("Server Response Message", serverResponseMessage);

            if (serverResponseCode == 200) {
                response = "true";
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception ex) {
            response = "error";
            Log.e("Send file Exception", ex.getMessage() + "");
            ex.printStackTrace();
        }
        return response;


    }

    public interface OfferImageUploadComplete{
        void onUploadCompletion(String response);
    }

}
