package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

//import java.util.logging.Handler;


public final class UploadPictureAsyncTask extends AsyncTask<Void,String, String> {

    public interface UploadPictureInterface {
        public void uploadedPictureListener(String imageURL) ;
    }


    private static final String TAG = "Image_Gallery";
    public static int uploadTypePrimary	=	1;
    public static int uploadTypeGallery	=	2;
    public static int uploadTypeLogo	=	3;
    public static int uploadTypeBackground = 4;

    private Activity appContext 	= null;
    ProgressDialog pd 				= null;
    String path 					= null;
    private SharedPreferences pref 	= null;
    private String backImg = null;
    private Boolean IsbackImgDeleted = false;
    private Boolean delImg = false;

    String responseMessage			= "";
    Boolean success 				= false,isPrimary = false, isGallery = false,isLogo = false, isParallel, isGalleryImage=false,isBackgroundImage=false;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId +"\"";
    int size = 0;
    SharedPreferences.Editor prefsEditor;
    static int chunkLength = 30000;
    UploadPictureInterface uploadInterface ;
    UserSessionManager session;

    boolean isSilent = false;
    public UploadPictureAsyncTask(Activity context ,String path ,Boolean isPrimary, Boolean isGallery) {
        this.appContext	=  	context;
        this.path	= 	path;
        this.isPrimary = isPrimary;
        isParallel = true;
        isGalleryImage = isGallery;
        session = new UserSessionManager(context.getApplicationContext(),context);
    }

    public UploadPictureAsyncTask(Activity context ,String path)
    {
        this.appContext = context ;
        this.path = path ;
        isParallel = true ;
        session = new UserSessionManager(context.getApplicationContext(),context);
        // uploadInterface = (UploadPictureInterface) context ;

    }

    public void setOnUploadListener(UploadPictureInterface uploadInterface){
        // Log.d("UploadPictureInterface","uploadInterface setOnUploadListener : "+uploadInterface);
        this.uploadInterface = uploadInterface;
    }

    public UploadPictureAsyncTask(String path ,Boolean isPrimary,boolean isSilent) {
        this.path	= 	path;
        this.isPrimary = isPrimary;
        isParallel = true;
        this.isSilent	=	isSilent;
    }

    public UploadPictureAsyncTask(Activity context,Boolean isbackgrndImg,Boolean delimg){
        this.appContext =context;
        this.isBackgroundImage = isbackgrndImg;
        this.delImg = delimg;
        session = new UserSessionManager(context.getApplicationContext(),context);

    }

    public UploadPictureAsyncTask(Activity context ,String path ,int type,boolean isParallel) {
        this.appContext	=  	context;
        this.path	= 	path;
        this.isParallel	=	isParallel;
        session = new UserSessionManager(context.getApplicationContext(),context);
        switch (type) {
            case 1:
                isPrimary = true;
                break;
            case 2:
                isGallery = true;
                break;
            case 3:
                isLogo = true;
                break;
            case 4:
                isBackgroundImage =  true;
                isParallel = false;
            default:
                break;
        }

    }

    @Override
    protected void onPreExecute() {

        if(!isSilent && (delImg==false))
        {
            pd= ProgressDialog.show(appContext, "", "Uploading image...");
            //	pd.setCancelable(true);
        }
        else
        {
            //pd= ProgressDialog.show(appContext, "Please wait ...", "Downloading Image ...", true);
            //pd.setCancelable(false);
            pd = new ProgressDialog(appContext);
            this.pd.setMessage("Deleting...");
            this.pd.show();
        }

    }

    @Override
    protected void onPostExecute(String result) {
//        Log.d("UploadPicAsyncTask","onPostExecute : "+Constants.storeSecondaryImages.size());
//        Toast.makeText(appContext,"Success  "+result,Toast.LENGTH_SHORT).show();
        Methods.showSnackBarPositive(appContext,"Image successfully uploaded");
        //  Log.d("UploadPictureInterface","uploadInterface : "+uploadInterface);
        uploadInterface.uploadedPictureListener(path);
        onPost();

    }


    public void onPost()
    {
        if (success && !isPrimary && !isBackgroundImage ) {
            Constants.uploadedImg = path;
            pd.dismiss();
            if(!isGalleryImage){
                //  Util.toast("Logo image updated.", appContext);
                Constants.isImgUploaded = true;
                Constants.uploadedImg = path;

                // appContext.finish();
            }
            else{
                //  Util.toast("Gallery image updated.", appContext);
                // Toast.makeText(appContext,"Gallery Image Update ",Toast.LENGTH_SHORT).show();

                //   Log.d(TAG,"Gallery Updated");
                Constants.isImgUploaded = true;
                Constants.uploadedImg = path;
                uploadInterface.uploadedPictureListener(path);
                appContext.finish();
            }
        }
//        else if (success && isPrimary) {
//            Constants.uploadedImg = path;
//            pd.dismiss();
//
//            Util.toast("Featured image updated.", appContext);
//            Constants.isImgUploaded = true;
//            Constants.uploadedImg = path;
//            try{
//                Bitmap bmp = Util.getBitmap(path, appContext);
//                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
//                MainFragment.homeFeaturedImage.setImageBitmap(bmp);
//                BusinessDetailsFragment.featuredImage.setImageDrawable(null);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            appContext.finish();
//
//        }
////        else if(success && isBackgroundImage )
//        {
//            Constants.uploadedImg = path;
//            pd.dismiss();
//
//            Util.toast("Background image updated.", appContext);
//            Constants.isImgUploaded = true;
//
//            try{
//                Bitmap bmp = Util.getBitmap(path, appContext);
//                bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
//                MainFragment.coverImg.setImageBitmap(Bitmap.createScaledBitmap(bmp, 500,500, false));
//                //BusinessDetailsFragment.featuredImage.setImageDrawable(null);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            appContext.finish();
//        }


//        else if(delImg && IsbackImgDeleted){
//            {
//                Constants.uploadedImg = path;
//                pd.dismiss();
//
//                Util.toast("Background image Deleted.", appContext);
//                Constants.isImgUploaded = true;
//                Constants.uploadedImg = path;
//
//                try{
//                    //Bitmap bmp = Util.getBitmap(path, appContext);
//                    //bmp = RoundCorners_image.getRoundedCornerBitmap(bmp, 15);
//                    //MainFragment.coverImg.setImageBitmap(Bitmap.createScaledBitmap(bmp, 500,500, false));
//                    //BusinessDetailsFragment.featuredImage.setImageDrawable(null);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                appContext.finish();
//            }
//        }
//
//        else if(delImg && (Util.isNullOrEmpty(Constants.storedBackgroundImage))){
//
//            pd.dismiss();
//
//            CharSequence text = "No images to Delete.";
//            int duration = Toast.LENGTH_LONG;
//            Toast toast = Toast.makeText(appContext, text, duration);
//            toast.show();
//
//            //Util.toast("No images to Delete.", appContext);
//
//            appContext.finish();
//        }
//
//        else if(Util.isNetworkStatusAvialable(appContext)){
//            pd.dismiss();
//            MixPanelController.track(EventKeys.ImageUploadFailed, null);
//            final Dialog dialog = new Dialog(appContext);
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//            dialog.setContentView(R.layout.basic_dialog_layout);
//
//            TextView text = (TextView) dialog
//                    .findViewById(R.id.textview_heading);
//            text.setText("Uh Oh!");
//            ImageView image = (ImageView) dialog
//                    .findViewById(R.id.alert_imageView);
//            image.setImageResource(R.drawable.error_user_message_icon);
//
//            TextView description = (TextView) dialog
//                    .findViewById(R.id.alert_message_text);
//            description.setText("There's something wrong. Please try again!");
//            dialog.show();
//
//            Button okbutton = (Button) dialog.findViewById(R.id.okButton);
//
//            okbutton.setText("OKAY!");
//
//            Button cancelButton = (Button) dialog
//                    .findViewById(R.id.cancelButton);
//
//            cancelButton.setVisibility(View.GONE);
//
//            okbutton.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    dialog.dismiss();
//                }
//            });
//        }
//        else {
//            pd.dismiss();
//            Toast.makeText(appContext, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
//        }
    }



    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        if(!Util.isNullOrEmpty(path)){
            uploadImage(path);
        }
        if(delImg && isBackgroundImage)
        {
            removebackgroundImg();
        }
        if(success){
            //getFpData();
            if(isBackgroundImage){
                try{
                    Thread.sleep(2000);
                }
                catch(Exception e)
                {
                    System.out.println();

                }
                Fp_bakgrnd_img_after_deletion();
            }
        }
        if(delImg && IsbackImgDeleted)
        {
            //getFpData();
            if(isBackgroundImage){
                Fp_bakgrnd_img_after_deletion();
            }
        }
        return response ;
    }


    public void uploadImage(String imagePath){
        // Toast.makeText(appContext,"Image Path : "+imagePath,Toast.LENGTH_SHORT).show();
        //  Log.d(TAG,"Image Path : "+imagePath);
//        Handler handler =  new Handler(appContext.getMainLooper());
//        handler.post( new Runnable(){
//            public void run(){
//                // Toast.makeText(appContext, "Image Path : "+imagePath,Toast.LENGTH_LONG).show();
//            }
//        });
//        FileInputStream fileInputStream = null;
//        File img = new File(imagePath);
//        int _totalChunks = 0;
//
//        ArrayList<byte[]> _chunks = new ArrayList<byte[]>();
//
//        try {
//            if (!Util.isNullOrEmpty(imagePath)) {
//                fileInputStream = new FileInputStream(img);
//            }
//
//            int bytesAvailable = fileInputStream.available();
//            if(!isParallel)
//            {
//                chunkLength = bytesAvailable;
//            }
//            _totalChunks = (int) Math
//                    .ceil(((double) bytesAvailable / (double) chunkLength));
//
//            for (int i = 0; i < _totalChunks; i++) {
//                int startPosition = i * chunkLength;
//                int dataLength = (int) (Math.min(bytesAvailable
//                        - startPosition, chunkLength));
//                byte[] buffer = new byte[dataLength];
//
//                fileInputStream.read(buffer, 0, dataLength);
//                _chunks.add(buffer);
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG,"E : "+e.getMessage());
//
//        } finally {
//            try {
//                fileInputStream.close();
//            } catch (Exception e) {
//            }
//        }
//
//        int _totalCallsMade = _chunks.size();
        //Path path = Paths.get("path/to/file");
        //String filepath = "/sdcard/temp.png";
        File imagefile = new File(imagePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
        byte[] bitmapdata = baos.toByteArray();

        UUID uuid = null;

        uuid = UUID.randomUUID();
        String s_uuid = uuid.toString();
        s_uuid = s_uuid.replace("-", "");
        String uri = null;
        String param = "createSecondaryImage/";
        if(isPrimary){
            param = "createImage";
        }
        else if(isLogo)
        {
            param = "createLogoImage/";
        }
        else if(isBackgroundImage)
        {
            param= "createBackgroundImage/";
        }

        //Constants con = new Constants(appContext);
        if(isParallel)
        {
//            uri = Constants.LoadStoreURI+param+
//                    "?clientId="+ Constants.clientId+
//                    "&fpId="+ session.getFPID()+"&reqType=sequential&reqtId=" + s_uuid + "&";
            uri = Constants.LoadStoreURI +
                    param + "?clientId=" +
                    Constants.clientId +
                    "&fpId=" + session.getFPID() +
                    "&reqType=sequential&reqtId=" +
                    s_uuid + "&";

            String temp = uri + "totalChunks=1&currentChunkNumber=1";
            sendDataToServer(temp, bitmapdata);

                //sendDataToServer(uri,  bitmapdata);
        }
        else
        {
            if(!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)))
            {
                //removebackgroundImg();
                String backgroundimgid = (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)).replace("/Backgrounds/", "");
                uri = Constants.ReplaceBackImg+
                        "?clientId="+
                        Constants.clientId+"&fpId="+ session.getFPID()+
                        "&existingBackgroundImageUri="+backgroundimgid+"&identifierType=SINGLE";
                sendDataToServer(uri,  bitmapdata);
            }
            else
            {
                uri = Constants.LoadStoreURI+param+"/?clientId="+
                        Constants.clientId+"&fpId="+ session.getFPID();
                sendDataToServer(uri,  bitmapdata);
            }



        }
    }

    public void removebackgroundImg(){
        //if(!Util.isNullOrEmpty(Constants.storedBackgroundImage))
        //IsbackImgDeleted=Util.deletebackgroundImg(Constants.storedBackgroundImage);
    }

//	public void getFpData(){
//		String response = "";
//		try{
//		response = Util.getDataFromServer(clientIdConcatedWithQoutes, Constants.HTTP_POST, Constants.LoadStoreURI+Constants.Store_id,Constants.BG_SERVICE_CONTENT_TYPE_JSON);
//		if (response.length() > 1) {
//			Constants.hasStoreData = true;
//			 JSONObject store 				= new JSONObject(response);
//
//			 if(response.contains("TileImageUri"))
//					Constants.storePrimaryImage 	= store.getString("TileImageUri");
//			if (response.contains("SecondaryTileImages")) {
//				try {
//
//					JSONArray array = store.getJSONArray("SecondaryTileImages");
//					if(array != null){
//					int len = array.length();
//					Constants.storeSecondaryImages = new ArrayList<String>();
//					if (len != 0) {
//						for(int i =0 ; i < len ; i++){
//							Constants.storeSecondaryImages.add(array.getString(i));
//						}
//					}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	}
//

    public void Fp_bakgrnd_img_after_deletion(){
        String serverUri = Constants.GetBackgroundImage+
                "?clientId="+ Constants.clientId+"&fpId="+ session.getFPID();
        String backgroundimageurl="";
        String backgroundimgid="";
        try {
            Thread.sleep(5000);
            HttpClient client = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(serverUri);
            org.apache.http.HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity =(HttpEntity) ((org.apache.http.HttpResponse) responseOfSite).getEntity();
            if (entity != null) {
                String str = (EntityUtils.toString(entity));
                JSONArray bgjsonarray = new JSONArray(str);
                if((bgjsonarray)!=null){
                    int len=bgjsonarray.length();
                    if(len!=0){
                        String storedBackgroundImage = bgjsonarray.getString(len-1);
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE,storedBackgroundImage);
                    }
                    else
                    {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE,"");

                    }
                }
            }



        }
        catch(Exception e)
        {
            System.out.println();

        }


    }





    public void sendDataToServer(String url, byte[] BytesToBeSent){
        //  Log.d("UploadPic","URL : "+url);
        DataOutputStream outputStream = null;
        success = false;
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
                if(!Util.isNullOrEmpty(response)){
                    Constants.serviceResponse = response;
//                    if (success)
//                    else MixPanelController.track("AddImageFailure",null);
                }
                else{
                    Constants.serviceResponse = "";
//                    MixPanelController.track("AddImageFailure",null);
                }
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