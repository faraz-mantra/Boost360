package com.nowfloats.BusinessProfile.UI.API;

/**
 * Created by Prashant on 2/6/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.RoundCorners_image;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;


public final class UploadPictureAsyncTask extends AsyncTask<Void,String, String> {

    public static int uploadTypePrimary	=	1;
    public static int uploadTypeGallery	=	2;
    public static int uploadTypeLogo	=	3;
    public static int uploadTypeBackground = 4;

    public Activity appContext 	= null;
    ProgressDialog pd 				= null;
    String path 					= null;
    private SharedPreferences pref 	= null;
    private String backImg = null;
    private Boolean IsbackImgDeleted = false;
    private Boolean delImg = false;

    String responseMessage			= "";
    Boolean success 				= false,isPrimary = false, isGallery = false,isLogo = false, isParallel, isGalleryImage=false;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId +"\"";
    int size = 0;
    SharedPreferences.Editor prefsEditor;
    static int chunkLength = 30000;
    String backImgPath = "";
    UserSessionManager session ;
    Boolean isBackgroundImage = false;

    boolean isSilent = false;
    private String category;
    String fpID ;

    public UploadPictureAsyncTask(Activity context ,String path ,Boolean isPrimary, Boolean isLogo,String fpID) {
        this.appContext	=  	context;
        this.path	= 	path;
        this.isPrimary = isPrimary;
        this.isLogo = isLogo;
        this.fpID = fpID;
        session = new UserSessionManager(context.getApplicationContext(),context);
    }

    public UploadPictureAsyncTask(Activity context,String path,Boolean IsUpdateBackgrundImage,String fpID){
        this.appContext = context;
        this.path = path;
        this.fpID = fpID;
        this.isBackgroundImage = IsUpdateBackgrundImage;
        session = new UserSessionManager(context.getApplicationContext(), context);
    }

//    public UploadPictureAsyncTask(Activity context,boolean IsbackgroundImageDelete){
//        this.appContext = context;
//        this.IsbackImgDeleted = IsbackgroundImageDelete;
//    }

    @Override
    protected void onPreExecute() {
        if(!isSilent && (delImg==false))
        {
            pd= ProgressDialog.show(appContext, "", appContext.getString(R.string.uploading_image));
            pd.setCancelable(false);

        }


    }

    @Override
    protected void onPostExecute(String result) {

        if(!isSilent)
        {
            onPost();
        }

    }


    public void onPost()
    {
        if (success) Methods.showSnackBarPositive(appContext,"Image updated successfully");
        if (success && isPrimary) {
            Constants.uploadedImg = path;
            pd.dismiss();

            //  Util.toast("Featured image updated.", appContext);
            Constants.isImgUploaded = true;
            Constants.uploadedImg = path;
            try {
                Bitmap bmp = Util.getBitmap(path, appContext);
                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
                Business_Profile_Fragment_V2.businessProfileImageView.setImageBitmap(bmp);
                Edit_Profile_Activity.editProfileImageView.setImageBitmap(bmp);
                SidePanelFragment.iconImage.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else if(success && isLogo){
            pd.dismiss();
            Constants.isImgUploaded = true;
            Constants.uploadedImg = path;
            Bitmap bmp = Util.getBitmap(path, appContext);
            Business_Logo_Activity.logoimageView.setImageBitmap(bmp);
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl,path);
            session.storeFPLogo(Constants.uploadedImg);


        }
        else if(success && isBackgroundImage){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                Util.GettingBackGroundId(session);
            } catch(Exception e){e.printStackTrace();}
            pd.dismiss();
            Bitmap bmp = Util.getBitmap(path, appContext);
            SidePanelFragment.containerImage.setImageBitmap(bmp);
        }
        else if (IsbackImgDeleted){
            pd.dismiss();
        }
        else if(Util.isNetworkStatusAvialable(appContext)){
            pd.dismiss();
        }

    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        if(!Util.isNullOrEmpty(path)){
            uploadImage(path);}
        if(success){
            getFpData();
        }
        if(IsbackImgDeleted)
        {
           // removebackgroundImg();
        }
        return response ;
    }


    public void uploadImage(String imagePath){
        try {
            FileInputStream fileInputStream = null;
            File img = new File(imagePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //byte[] buf = new byte[1024];
            File f = new File(img.getAbsolutePath() + File.separator);
            try {
                f.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
            if(bmp != null) {
                if ((f.length() / 1024) > 100) {
                    bmp.compress(CompressFormat.JPEG, 70, bos);
                } else {
                    bmp.compress(CompressFormat.JPEG, 100, bos);
                }
            }
            byte[] bitmapdata = bos.toByteArray();

            try {
                if (!Util.isNullOrEmpty(imagePath)) {
                    fileInputStream = new FileInputStream(img);
                }

                int bytesAvailable = fileInputStream.available();
                if (!isParallel) {
                    chunkLength = bytesAvailable;
                }
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
            String param = "createSecondaryImage/";
            if (isPrimary) {
                param = "createImage";
            } else if (isLogo) {
                param = "createLogoImage/";
            } else if (isBackgroundImage) {
                param = "createBackgroundImage/";
            }

            if (isPrimary || isLogo)    // change for other cases
            {
                uri = Constants.LoadStoreURI +
                        param + "?clientId=" +
                        Constants.clientId +
                        "&fpId=" + fpID +
                        "&reqType=sequential&reqtId=" +
                        s_uuid + "&";

                String temp = uri + "totalChunks=1&currentChunkNumber=1";
                sendDataToServer(temp, bitmapdata);

            } else {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)) && isLogo == false) {
                    //removebackgroundImg();
                    String backgroundimgid = (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)).replace("/Backgrounds/", "");
                    uri = Constants.ReplaceBackImg + "?clientId=" + Constants.clientId + "&fpId=" + fpID + "&existingBackgroundImageUri=" + backgroundimgid + "&identifierType=SINGLE";
                    sendDataToServer(uri, bitmapdata);
                } else {
                    uri = Constants.LoadStoreURI + param + "/?clientId=" + Constants.clientId + "&fpId=" + fpID;
                    sendDataToServer(uri, bitmapdata);
                }
            }
        }catch (Exception e){e.printStackTrace(); System.gc();}
    }
    public void getFpData(){

        if(isBackgroundImage){

          //  try {
                Util.GettingBackGroundId(session);
                //Thread.sleep(3000);
          //  } catch (InterruptedException e) {
          //      e.printStackTrace();
          //  }
        }else{
            String response = "";
            try{
                String url = Constants.NOW_FLOATS_API_URL+"/Discover/v1/floatingPoint/"
                        + "nf-app/" + session.getFPID();
                String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";

                response = Util.getDataFromServer(
                        clientIdConcatedWithQoutes,
                        Constants.HTTP_POST, url);
                if (response.length() > 1) {
                    Constants.hasStoreData = true;
                    JSONObject store 				= new JSONObject(response);

                    if(response.contains("ImageUri"))
                        Constants.storePrimaryImage 	= store.getString("ImageUri");
                    if (response.contains("LogoUrl")) {
                        Constants.StoreLogoUri = store.getString("LogoUrl");
                        Constants.StoreLogoUri = Constants.StoreLogoUri.replaceFirst(
                                "/", "");
                        if (!Util.isNullOrEmpty(Constants.StoreLogoUri)){
                            //MixPanelController.setProperties("Logo", "True");
                        }
                        else{
                            // MixPanelController.setProperties("Logo", "False");
                        }
                        //session.storeLogoURI(Constants.StoreLogoUri);
                        session.storeFPLogo(Constants.StoreLogoUri);
                        prefsEditor.putString("LogoUrl", Constants.StoreLogoUri);
                        prefsEditor.commit();
                    }
                    if (response.contains("SecondaryTileImages")) {
                        try {
                            JSONArray array = store.getJSONArray("SecondaryTileImages");
                            if(array != null){
                                int len = array.length();
                                Constants.storeSecondaryImages = new ArrayList<String>();
                                if (len != 0) {
                                    for(int i = 0 ; i < len ; i++){
                                        Constants.storeSecondaryImages.add(array.getString(i));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //For side panel
                    (new Site_Meter_Fragment()).siteMeterCalculation();
                    (new SidePanelFragment()).siteMeterCalculation();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public void removebackgroundImg(){
//        if(!Util.isNullOrEmpty(Constants.storedBackgroundImage)){
//
//            DeleteBackgroundImageAsyncTask deleteBackgroundImageAsyncTask = new DeleteBackgroundImageAsyncTask(appContext);
//            deleteBackgroundImageAsyncTask.execute();

//            Thread thread = new Thread(new Runnable(){
//                @Override
//                public void run(){
//                    //code to do the HTTP request
//                    String backgroundimgid = (Constants.storedBackgroundImage).replace("/Backgrounds/", "");
//                    JSONObject obj = new JSONObject();
//                    //?clientId=%@&existingBackgroundImageUri=%@&fpId=%@&identifierType=%@
//                    String serverUri = Constants.DelBackgroundImage;
//
//
//
//                    try {
//
//                        obj.put("ClientId", Constants.clientId);
//                        obj.put("FpId", Constants.Store_id);
//                        obj.put("ExistingBackgroundImageUri", backgroundimgid);
//                        //obj.put("identifierType", "SINGLE");
//                        String serveruri=Constants.DelBackImg+"/"+"?ClientId="+Constants.clientId+"&ExistingBackgroundImageUri="+backgroundimgid+"&FpId="+Constants.Store_id+"&identifierType=SINGLE";
//                        //https://api.withfloats.com/discover/v1/floatingpoint/backgroundImage/delete/?ClientId=Constants.clientId&ExistingBackgroundImageUri=backgroundimgid&FpId=Constants.Store_id&&identifierType=SINGLE"
//                        // add header
//                        HttpPost httpPost = new HttpPost(serveruri);
//                        StringEntity entity = new StringEntity(obj.toString(), HTTP.UTF_8);
//                        entity.setContentType("application/json");
//                        httpPost.setHeader("Content-type","application/json");
//                        httpPost.setEntity(entity);
//                        HttpClient client = new DefaultHttpClient();
//                        HttpResponse response = client.execute(httpPost);
//
//                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                            Constants.storedBackgroundImage = null;
//                            if (!Util.isNullOrEmpty(Constants.StoreCategory)) {
//                                String categorystore = Constants.StoreCategory.toUpperCase()
//                                        .replace("[", "").replace("]", "").replace("\"", "");
//
//                                if (categorystore.contains("BAKERY")) {
//                                    category = "F&B-BAKERY";
//
//                                } else if (categorystore.contains("BARS")) {
//                                    category = "F&B - BARS";
//
//                                } else if (categorystore.contains("CAFE")) {
//                                    category = "F&B-CAFE";
//
//                                } else if (categorystore.contains("RESTAURANTS")) {
//                                    category = "F&B-RESTAURANTS";
//
//                                } else if (categorystore.contains("APPAREL")) {
//                                    category = "FASHION-APPAREL";
//
//                                } else if (categorystore.contains("FOOTWEAR")) {
//                                    category = "FASHION-FOOTWEAR";
//
//                                } else if (categorystore.contains("GIFTS")) {
//                                    category = "GIFTS&NOVELTIES";
//                                } else if (categorystore.contains("HEALTH")) {
//                                    category = "HEALTH&FITNESS";
//
//                                } else if (categorystore.contains("HOTEL")) {
//                                    category = "HOTEL&MOTELS";
//
//                                } else if (categorystore.contains("NATURALAYURVEDA")) {
//                                    category = "NATURAL&AYURVEDA";
//
//                                } else {
//                                    category = categorystore;
//
//                                }
//                            }
//                            Util.changeDefaultBackgroundImage(category);
//                        }
//
//                    }
//                    catch (Exception e) {
//
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//            thread.setPriority(Thread.MIN_PRIORITY);
//            thread.start();
//        }
//
//    }

    public void sendDataToServer(String url, byte[] BytesToBeSent){
        DataOutputStream outputStream = null;

        BoostLog.d("BackGroundImage Url", url);

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
                success = true;
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
            success = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

}

