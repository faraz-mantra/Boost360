package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framework.analytics.SentryController;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.FileUtils;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by NowFloatsDev on 29/05/2015.
 */
public class Upload_Logo extends AsyncTask<Void, String, String> {

    Activity appContext;
    String path;
    String fpID;
    ProgressDialog pd = null;
    UserSessionManager mSession;
    boolean isUploadingSuccess = false;
    private int imageSize = 4194304;
    private Listener listener;

    public Upload_Logo(Activity context, String path, String fpID, UserSessionManager sessionManager, Listener listener) {
        this.appContext = context;
        this.path = path;
        this.fpID = fpID;
        Constants.LOGOUPLOADED = false;
        mSession = sessionManager;
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
            }
        });

        if (isUploadingSuccess) {
            if (!Util.isNullOrEmpty(Constants.serviceResponse)) {
                BoostLog.d("Logo Image", Constants.serviceResponse);
                mSession.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl, Constants.serviceResponse.replace("\\", "").replace("\"", ""));
            }
            appContext.runOnUiThread(() -> {
                try {
                    listener.onSuccess(true);
                    Methods.showSnackBarPositive(appContext, appContext.getString(R.string.logo_updated_successfully));
                    Constants.LOGOUPLOADED = true;
                    if(path.toLowerCase().contains(".gif")){
                        Glide.with(appContext).asGif().load(path).apply(new RequestOptions().placeholder(R.drawable.logo_default_image)).into(Business_Logo_Activity.logoimageView);
                    }else {
                        Bitmap bmp = Methods.decodeSampledBitmap(path, 720, 720);
//                        bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
                        Business_Logo_Activity.logoimageView.setImageBitmap(bmp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SentryController.INSTANCE.captureException(e);
                }
            });

        } else {

        }
    }


    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        if (!Util.isNullOrEmpty(path)) {
            uploadImage(path);
        }

        return response;
    }


    public void uploadImage(String imagePath) {
        try {
            UUID uuid;
            String minType = FileUtils.getExtension(imagePath);
            uuid = UUID.randomUUID();
            String s_uuid = uuid.toString();
            s_uuid = s_uuid.replace("-", "");
            String uri;
            String param = "createLogoImage/";
            uri = Constants.LoadStoreURI +
                    param + "?clientId=" +
                    Constants.clientId +
                    "&fpId=" + fpID +
                    "&reqType=sequential&reqtId=" +
                    s_uuid + "&fileName=ImageFloat" + System.currentTimeMillis()+minType+"&";

            String temp = uri + "totalChunks=1&currentChunkNumber=1";
//            if(imagePath.contains(".gif"))
//                sendGifToServer(temp, imagePath);
//            else
                sendDataToServer(temp, Methods.compressToByte(imagePath, appContext));
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            Methods.showSnackBarNegative(appContext, e.getMessage());
            e.printStackTrace();
            System.gc();
        }


    }

    public void sendGifToServer(String url, String ImagePath){
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileName", ImagePath, okhttp3.RequestBody.create(MediaType.parse("multipart/form-data"), new File(ImagePath)))
                .build();

        Request request = new Request.Builder().url(url)
                .put(body)
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Authorization", Utils.getAuthToken())
                .addHeader("Content-Type", Constants.BG_SERVICE_CONTENT_TYPE_OCTET_STREAM)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GIF upload", e.toString());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (response != null) {
                    if(response.code() == 200 || response.code() == 202){
                        isUploadingSuccess = true;
                    }
                }
            }
        });
    }


    public void sendDataToServer(String url, byte[] BytesToBeSent) {
        DataOutputStream outputStream = null;

        try {

            URL new_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(Constants.HTTP_PUT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());


            connection.setRequestProperty("Content-Type",
                    Constants.BG_SERVICE_CONTENT_TYPE_OCTET_STREAM);

            if (BytesToBeSent != null) {
                outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }


            int responseCode = connection.getResponseCode();

            String responseMessage = connection.getResponseMessage();

            if (responseCode == 200 || responseCode == 202) {
                isUploadingSuccess = true;
            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder responseContent = new StringBuilder();

                String temp = null;

                boolean isFirst = true;

                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append(Constants.NEW_LINE);
                    responseContent.append(temp);
                    isFirst = false;
                }

                String response = responseContent.toString();
                if (!Util.isNullOrEmpty(response))
                    Constants.serviceResponse = response;
                else
                    Constants.serviceResponse = "";
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    SentryController.INSTANCE.captureException(e);
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    SentryController.INSTANCE.captureException(e);
                }

            }


        } catch (Exception ex) {
            SentryController.INSTANCE.captureException(ex);
            isUploadingSuccess = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                SentryController.INSTANCE.captureException(e);
            }
        }
    }


    public interface Listener {
        void onSuccess(Boolean isSuccess);
    }
}
