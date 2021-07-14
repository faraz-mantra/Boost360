package com.nowfloats.BusinessProfile.UI.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.FeaturedImageActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by guru on 29/05/2015.
 */
public class uploadIMAGEURI extends AsyncTask<Void, String, String> {

    Activity appContext;
    String path;
    String fpID;
    ProgressDialog pd = null;
    boolean isUploadingSuccess = false;
    private Listener listener;

    public uploadIMAGEURI(Activity context, String path, String fpID) {
        this.appContext = context;
        this.path = path;
        this.fpID = fpID;
        Constants.IMAGEURIUPLOADED = false;
    }


    public uploadIMAGEURI(Activity context, String path, String fpID, Listener listener) {
        this.appContext = context;
        this.path = path;
        this.fpID = fpID;
        this.listener = listener;
        Constants.IMAGEURIUPLOADED = false;
    }

    @Override
    protected void onPreExecute() {
        appContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = ProgressDialog.show(appContext, "", "Uploading image...");
                pd.setCancelable(false);
            }
        });

    }


    @Override
    protected void onPostExecute(String result) {
        appContext.runOnUiThread(() -> {
            try {
                if (pd != null)
                    pd.dismiss();
                if (isUploadingSuccess) {
                    Methods.showSnackBarPositive(appContext, appContext.getString(R.string.image_updated_successfully));
                    Constants.IMAGEURIUPLOADED = true;
                    try {
                        Bitmap bmp = Util.getBitmap(path, appContext);
                        bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);

                        if (Edit_Profile_Activity.editProfileImageView != null) {
                            Edit_Profile_Activity.editProfileImageView.setImageBitmap(bmp);
                        }

                        if (SidePanelFragment.iconImage != null) {
                            SidePanelFragment.iconImage.setImageBitmap(bmp);
                        }

                        if (Business_Profile_Fragment_V2.businessProfileImageView != null) {
                            Business_Profile_Fragment_V2.businessProfileImageView.setImageBitmap(bmp);
                        }

                        if (FeaturedImageActivity.logoimageView != null) {
                            FeaturedImageActivity.logoimageView.setImageBitmap(bmp);
                        }
                        if (this.listener != null) this.listener.success(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (this.listener != null) this.listener.success(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        if (imagePath == null) {
            return;
        }
        FileInputStream fileInputStream = null;
        File img = new File(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        File f = new File(img.getAbsolutePath() + File.separator);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
        byte[] bitmapdata = bos.toByteArray();

        try {
            if (!Util.isNullOrEmpty(imagePath)) {
                fileInputStream = new FileInputStream(img);
            }

            int bytesAvailable = fileInputStream.available();

        } catch (Exception e) {


        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }

        UUID uuid;

        uuid = UUID.randomUUID();
        String s_uuid = uuid.toString();
        s_uuid = s_uuid.replace("-", "");
        String uri;
        String param = "createImage";
        uri = Constants.LoadStoreURI +
                param + "?clientId=" +
                Constants.clientId +
                "&fpId=" + fpID +
                "&reqType=sequential&reqtId=" +
                s_uuid + "&";

        String temp = uri + "totalChunks=1&currentChunkNumber=1";
        sendDataToServer(temp, bitmapdata);


    }


    public void sendDataToServer(String url, byte[] BytesToBeSent) {

        Log.d("IMAGE_URI", "" + url);
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
                BoostLog.d("response: ", response);
                if (!Util.isNullOrEmpty(response))
                    Constants.serviceResponse = response;
                else
                    Constants.serviceResponse = "";
                UserSessionManager manager = new UserSessionManager(appContext, appContext);
                manager.storeFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI, response.replace("\\", "").replace("\"", ""));
                //Constants.IMAGEURIUPLOADED = false;
            } catch (Exception e) {
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }


        } catch (Exception ex) {
            isUploadingSuccess = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public interface Listener {
        void success(boolean isSuccess);
    }
}
