package com.nowfloats.riachatsdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.nowfloats.riachatsdk.interfaces.FileUploadInterface;
import com.nowfloats.riachatsdk.models.FileResultModel;
import com.nowfloats.riachatsdk.utils.Constants;

import java.io.File;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

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

    public FileUploadService(){
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
        if(intent==null)
            return ;
        Log.d("FileUploadService", "This is reaching2");
        String filePath = intent.getStringExtra(Constants.FILE_PATH);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(filePath));
        mFileUploadInterface.upload(typedFile.fileName(), typedFile, "My Description", new Callback<FileResultModel>() {
            @Override
            public void success(FileResultModel fileResultModel, Response response) {
                Bundle bundle = new Bundle();
                if(mReceiver!=null){
                    Log.d("FileUploadService", "Url: " + fileResultModel.getUrl());
                    bundle.putString(Constants.KEY_FILE_URL, fileResultModel.getUrl());
                    mReceiver.send(Constants.RESULT_OK, bundle);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Bundle bundle = new Bundle();
                if(mReceiver!=null){
                    bundle.putString(Constants.KEY_FILE_URL, null);
                    mReceiver.send(Constants.RESULT_CANCELLED, bundle);
                }
            }
        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}
