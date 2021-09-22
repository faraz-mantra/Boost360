package com.nowfloats.ProductGallery.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImage extends AsyncTask<String, String, String> {
    private String url;
    private byte[] imageData;
    private String productId;
    private int responseCode;
    private ImageUploadListener listener;

    public UploadImage(String url, byte[] imageData, String productId) {
        this.url = url;
        this.imageData = imageData;
        this.productId = productId;
    }

    @Override
    protected void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        DataOutputStream outputStream;

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
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());
            if (imageData != null) {
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(imageData, 0, imageData.length);
            }

            responseCode = connection.getResponseCode();

                /*if (responseCode == 200 || responseCode == 202)
                {
                    Log.d("PRODUCT_RESPONSE", "" + 200);
                    //flag = true;
                }

                else
                {
                    Log.d("PRODUCT_RESPONSE", "IMAGE UPLOAD FAIL");
                    //flag = false;
                }*/

                /*InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader =  null;
                try
                {
                    inputStreamReader = new InputStreamReader(connection.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder responseContent = new StringBuilder();
                    String temp = null;
                    boolean isFirst = true;
                    while((temp = bufferedReader.readLine())!=null)
                    {
                        if(!isFirst)
                            responseContent.append(Constants.NEW_LINE);
                        responseContent.append(temp);
                        isFirst = false;
                    }
                    if (responseContent!=null || responseContent.length()==0){
                        String response = responseContent.toString();
                        Log.d("Product IMage", "Upload Response : " + response);
                        if (response==null || response.trim().length()==0) flag =false;
                    }else{
                        flag =false;
                    }
                }
                catch(Exception e){e.printStackTrace();flag = false;}
                finally
                {
                    try{
                        inputStreamReader.close();
                    }catch (Exception e) {}
                    try{
                        bufferedReader.close();
                    }catch (Exception e) {}

                }*/
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("UploadImage >>", "error --->>>" + ex.toString());
            //flag = false;
        } /*finally {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                }
            }*/

        return null;
    }

    @Override
    protected void onPostExecute(final String result) {

        listener.onPostExecute(result, responseCode);
        // Log.d("FILE_UPLOAD_RESPONSE", "RESPONSE: " + result);
    }

    public void setImageUploadListener(ImageUploadListener listener) {
        this.listener = listener;
    }

    public interface ImageUploadListener {
        void onPreExecute();

        void onPostExecute(String result, int responseCode);
    }
}