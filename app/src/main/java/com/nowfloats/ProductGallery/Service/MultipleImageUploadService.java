package com.nowfloats.ProductGallery.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.nowfloats.ProductGallery.Product_Detail_Activity;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NowFloats on 06-09-2016.
 */
public class MultipleImageUploadService extends IntentService{

    public static final String REQUEST_PI = "productId";
    public static final String REQUEST_FILE_NAME = "fileName";
    private String productId;
    private String fileName;
    private NotificationManager mNotificationManager;
    private boolean flag =false;
    private int a = 0;
    NotificationCompat.Builder mBuilder;
    int mTotalCount = 0;
    int mSuccessCount = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MultipleImageUploadService(String name) {
        super(name);
    }

    public MultipleImageUploadService(){
        super("MultipleImageUploadService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        a++;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        a--;
        mTotalCount++;
        productId = intent.getExtras().getString(REQUEST_PI, null);
        fileName = intent.getExtras().getString(REQUEST_FILE_NAME, null);
        if(productId!=null && fileName!=null){
            String valuesStr = "clientId="+Constants.clientId
                    +"&requestType=sequential&requestId="+Constants.deviceId
                    +"&totalChunks=1&currentChunkNumber=1&productId="+productId;
            String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/AddImage?" +valuesStr;
            String response = startUpload(fileName, url);
            if(response.equals("true")){
                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(getString(R.string.uploading))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getString(R.string.upload_successful)))
                                .setContentText(getString(R.string.upload_successful)+": " + fileName)
                                .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.app_launcher)).getBitmap())
                                .setSmallIcon(R.drawable.app_launcher2);

                mNotificationManager.notify(16, mBuilder.build());
                mSuccessCount++;
            }else {
                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(getString(R.string.uploading))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getString(R.string.upload_failed)))
                                .setContentText(getString(R.string.error_in_uploading)+": " + fileName)
                                .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.app_launcher)).getBitmap())
                                .setSmallIcon(R.drawable.app_launcher2);

                mNotificationManager.notify(16, mBuilder.build());
            }
        }
        if(a==0){
            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle(getString(R.string.uploading))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(mSuccessCount + getString(R.string.uploaded_and) + (mTotalCount-mSuccessCount) +getString(R.string.failed)))
                            .setContentText(mSuccessCount + getString(R.string.uploaded_and) + (mTotalCount-mSuccessCount) + getString(R.string.failed))
                            .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.app_launcher)).getBitmap())
                            .setSmallIcon(R.drawable.app_launcher2);

            mNotificationManager.notify(16, mBuilder.build());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Product_Detail_Activity.ACTION);
            sendBroadcast(broadcastIntent);
        }
    }

    private String startUpload(String filename, String targetUrl){
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.uploading))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.uploading)))
                .setContentText(getString(R.string.uploading_image)+": " + filename)
                .setSmallIcon(R.drawable.app_launcher2);

        mNotificationManager.notify(16, mBuilder.build());

        String response = "error";
        Log.e("Image filename", filename);
        Log.e("url", targetUrl);

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

}
