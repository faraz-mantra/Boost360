package com.nowfloats.riachatsdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.nowfloats.riachatsdk.interfaces.FileUploadInterface;
import com.nowfloats.riachatsdk.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit.RestAdapter;

/**
 * Created by NowFloats on 28-03-2017.
 */

public class FileUploadService extends IntentService {

    private static final String TAG = "FileUploadService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private RestAdapter mAdapter;
    private FileUploadInterface mFileUploadInterface;
    private ResultReceiver mReceiver;

    public FileUploadService() {
        super(TAG);
    }

    public FileUploadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAdapter = new RestAdapter.Builder().setEndpoint(Constants.SERVER_URL).build();
        mFileUploadInterface = mAdapter.create(FileUploadInterface.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("FileUploadService", "This is reaching1");
        if (intent == null)
            return;
        Log.d("FileUploadService", "This is reaching2");

        String filePath = intent.getStringExtra(Constants.FILE_PATH);

        File file = new File(filePath);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        try {

            OkHttpClient client = new OkHttpClient();

            InputStream in = new FileInputStream(file);
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            RequestBody requestBody = RequestBody
                    .create(MediaType.parse("image/jpeg"), buf);


            Request request = new Request.Builder()
                    .url(Constants.SERVER_URL + "/plugin/api/Service/ReceiveFile?filename=" + file.getName())
                    .post(requestBody)
                    .addHeader("content-type", "multipart/form-data")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "9afff121-51ec-7b4e-88f6-fb3a0374a16c")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Bundle bundle = new Bundle();
                    if (mReceiver != null) {
                        bundle.putString(Constants.KEY_FILE_URL, null);
                        mReceiver.send(Constants.RESULT_CANCELLED, bundle);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Bundle bundle = new Bundle();
                    if (mReceiver != null) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            Log.d("FileUploadService", "Url: " + obj.optString("Url"));
                            bundle.putString(Constants.KEY_FILE_URL, obj.optString("Url"));
                            mReceiver.send(Constants.RESULT_OK, bundle);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            if (mReceiver != null) {
                                bundle.putString(Constants.KEY_FILE_URL, null);
                                mReceiver.send(Constants.RESULT_CANCELLED, bundle);
                            }
                        }
                    }
                }
            });

//            Log.d("POST", response.body().string());


        } catch (Exception e) {
            e.printStackTrace();
        }

//        try{
//
//            InputStream in = new FileInputStream(file);
//            byte[] buf;
//            buf = new byte[in.available()];
//            while (in.read(buf) != -1);
//            RequestBody requestBody = RequestBody
//                    .create(MediaType.parse("image/jpeg"), buf);
//
//            mFileUploadInterface.uploadBinary(file.getName(),requestBody, new Callback<FileResultModel>() {
//                @Override
//                public void success(FileResultModel fileResultModel, Response response) {
//                    Bundle bundle = new Bundle();
//                    if(mReceiver!=null){
//                        Log.d("FileUploadService", "Url: " + fileResultModel.getUrl());
//                        bundle.putString(Constants.KEY_FILE_URL, fileResultModel.getUrl());
//                        mReceiver.send(Constants.RESULT_OK, bundle);
//                    }
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    error.printStackTrace();
//                    Bundle bundle = new Bundle();
//                    if(mReceiver!=null){
//                        bundle.putString(Constants.KEY_FILE_URL, null);
//                        mReceiver.send(Constants.RESULT_CANCELLED, bundle);
//                    }
//                }
//            });
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }

//        TypedFile typedFile = new TypedFile("multipart/form-data", new File(filePath));
//        mFileUploadInterface.upload(typedFile.fileName(), typedFile, "My Description", new Callback<FileResultModel>() {
//            @Override
//            public void success(FileResultModel fileResultModel, Response response) {
//                Bundle bundle = new Bundle();
//                if(mReceiver!=null){
//                    Log.d("FileUploadService", "Url: " + fileResultModel.getUrl());
//                    bundle.putString(Constants.KEY_FILE_URL, fileResultModel.getUrl());
//                    mReceiver.send(Constants.RESULT_OK, bundle);
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                error.printStackTrace();
//                Bundle bundle = new Bundle();
//                if(mReceiver!=null){
//                    bundle.putString(Constants.KEY_FILE_URL, null);
//                    mReceiver.send(Constants.RESULT_CANCELLED, bundle);
//                }
//            }
//        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}
