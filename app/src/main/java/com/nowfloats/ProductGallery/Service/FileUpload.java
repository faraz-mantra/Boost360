package com.nowfloats.ProductGallery.Service;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FileUpload extends AsyncTask<String, String, String>
{
    private String TAG = FileUpload.class.getSimpleName();

    private File file;
    private OnFileUpload listener;
   // private String fileURL;

    public FileUpload(File file)
    {
        this.file = file;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
        listener.onPreUpload();
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String url = strings[0];

        try
        {
            Log.d(TAG, "URL " + url);

            OkHttpClient client = new OkHttpClient();
            okhttp3.RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileName", file.getPath(), okhttp3.RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    .build();

            Request request = new Request.Builder().url(url)
                    .put(body).build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    if(response != null)
                    {
                        try
                        {
                            JSONObject json = new JSONObject(response.body().string());
                            //setFileURL(json.getString("Data"));
                            //Log.d(TAG, "" + json.toString());
                            listener.onSuccess(json.getString("Data"));
                        }

                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            listener.onFailure();
                        }
                    }
                }
            } );
        }

        catch (Exception e)
        {
            Log.d(TAG, "FILE_UPLOAD: EXCEPTION" + e.getMessage());
            e.printStackTrace();
            listener.onFailure();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute " + result);
    }

    public void setFileUploadListener(OnFileUpload listener)
    {
        this.listener = listener;
    }

    public interface OnFileUpload
    {
        void onSuccess(String url);
        void onFailure();
        void onPreUpload();
    }
}