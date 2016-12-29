package com.nowfloats.signup.UI.API;

/**
 * Created by NowFloatsDev on 10/06/2015.
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by NowFloatsDev on 29/05/2015.
 */
public class uploadFeaturedImage_Facebook extends AsyncTask<Void,String, String> {

   // Activity appContext;
    Bitmap path;
    String fpID ;
    ProgressDialog pd = null;

    boolean isUploadingSuccess = false ;

    public uploadFeaturedImage_Facebook(Bitmap path,String fpID) {
        //this.appContext	=  	context;
        this.path	= 	path;
        this.fpID = fpID;
        Constants.IMAGEURIUPLOADED = false ;
    }



    @Override
    protected void onPreExecute() {

//        pd= ProgressDialog.show(appContext, "", "Uploading image...");
//        pd.setCancelable(false);
    }


    @Override
    protected void onPostExecute(String result) {

       // pd.dismiss();
        if (isUploadingSuccess) {
        //    Methods.showSnackBarPositive(appContext, "Image updated successfully");
//            Constants.IMAGEURIUPLOADED = true ;
//            try {
//                Bitmap bmp = Util.getBitmap(path, appContext);
//                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
//                Business_Profile_Fragment_V2.businessProfileImageView.setImageBitmap(bmp);
//                Edit_Profile_Activity.editProfileImageView.setImageBitmap(bmp);
//                SidePanelFragment.iconImage.setImageBitmap(bmp);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else {

        }
    }





    @Override
    protected String doInBackground(Void... params)
    {
        String response = "";

            uploadImage(path);

        return response ;
    }


    public void uploadImage(Bitmap imagePath){
//        FileInputStream fileInputStream = null;
//        File img = new File(imagePath);
        if(imagePath==null){
            return;
        }
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] buf = new byte[1024];
//        File f = new File(img.getAbsolutePath() + File.separator );
//        try {
//            f.createNewFile();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp =  imagePath;//BitmapFactory.decodeFile(imagePath,options);
//        if((f.length()/1024)>100){
//            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
//        }else{
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
       // }
//
        byte[] bitmapdata = bos.toByteArray();
//
//        try {
//            if (!Util.isNullOrEmpty(imagePath)) {
//                fileInputStream = new FileInputStream(img);
//            }
//
//            int bytesAvailable = fileInputStream.available();
//
//        } catch (Exception e) {
//
//
//        } finally {
//            try {
//                fileInputStream.close();
//            } catch (Exception e) {
//            }
//        }

        UUID uuid;

        uuid = UUID.randomUUID();
        String s_uuid = uuid.toString();
        s_uuid = s_uuid.replace("-", "");
        String uri;
        String param = "createImage";
        uri = Constants.LoadStoreURI+
                param+"?clientId="+
                Constants.clientId+
                "&fpId="+fpID+
                "&reqType=sequential&reqtId=" +
                s_uuid + "&";

        String temp = uri + "totalChunks=1&currentChunkNumber=1" ;
        sendDataToServer(temp,bitmapdata);





    }


    public void sendDataToServer(String url, byte[] BytesToBeSent){
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

            if (responseCode	== 200  || responseCode	== 202)
            {
                isUploadingSuccess = true;
            }

            InputStreamReader inputStreamReader = null;
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

                String response = responseContent.toString();
                if(!Util.isNullOrEmpty(response))
                    Constants.serviceResponse = response;
                else
                    Constants.serviceResponse = "";
            }
            catch(Exception e){}
            finally
            {
                try{
                    inputStreamReader.close();
                }catch (Exception e) {}
                try{
                    bufferedReader.close();
                }catch (Exception e) {}

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

}

