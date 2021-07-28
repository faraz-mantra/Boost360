package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

//import java.com.thinksity.logging.Handler;


public final class UploadPictureAsyncTask extends AsyncTask<Void, String, String> {

    private static final String TAG = "Image_Gallery";
    public static int uploadTypePrimary = 1;
    public static int uploadTypeGallery = 2;
    public static int uploadTypeLogo = 3;
    public static int uploadTypeBackground = 4;
    static int chunkLength = 30000;
    ProgressDialog pd = null;
    String path = null;
    ArrayList<String> arrPath = null;
    String responseMessage = "";
    Boolean success = false, isPrimary = false, isGallery = false, isLogo = false, isParallel, isGalleryImage = false, isBackgroundImage = false;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
    int size = 0;
    SharedPreferences.Editor prefsEditor;
    UploadPictureInterface uploadInterface;
    UserSessionManager session;
    boolean isSilent = false;
    private ArrayList<String> arrImagePaths;
    private Activity appContext = null;
    private SharedPreferences pref = null;
    private String backImg = null;
    private Boolean IsbackImgDeleted = false;
    private Boolean delImg = false;

    public UploadPictureAsyncTask(Activity context, String path, Boolean isPrimary, Boolean isGallery) {
        this.appContext = context;
        this.path = path;
        this.isPrimary = isPrimary;
        isParallel = true;
        isGalleryImage = isGallery;
        session = new UserSessionManager(context.getApplicationContext(), context);
    }

    public UploadPictureAsyncTask(Activity context, String path) {
        this.appContext = context;
        this.path = path;
        isParallel = true;
        session = new UserSessionManager(context.getApplicationContext(), context);
        // uploadInterface = (UploadPictureInterface) context ;

    }

    public UploadPictureAsyncTask(Activity context, ArrayList<String> arrImagePaths) {
        this.appContext = context;
        this.arrImagePaths = arrImagePaths;
        isParallel = true;
        session = new UserSessionManager(context.getApplicationContext(), context);
    }

    public UploadPictureAsyncTask(String path, Boolean isPrimary, boolean isSilent) {
        this.path = path;
        this.isPrimary = isPrimary;
        isParallel = true;
        this.isSilent = isSilent;
    }


    public UploadPictureAsyncTask(Activity context, Boolean isbackgrndImg, Boolean delimg) {
        this.appContext = context;
        this.isBackgroundImage = isbackgrndImg;
        this.delImg = delimg;
        session = new UserSessionManager(context.getApplicationContext(), context);

    }

    public UploadPictureAsyncTask(Activity context, String path, int type, boolean isParallel) {
        this.appContext = context;
        this.path = path;
        this.isParallel = isParallel;
        session = new UserSessionManager(context.getApplicationContext(), context);
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
                isBackgroundImage = true;
                isParallel = false;
            default:
                break;
        }

    }

    public void setOnUploadListener(UploadPictureInterface uploadInterface) {
        // BoostLog.d("UploadPictureInterface","uploadInterface setOnUploadListener : "+uploadInterface);
        this.uploadInterface = uploadInterface;
    }

    @Override
    protected void onPreExecute() {

        if (!isSilent && (delImg == false)) {
            pd = ProgressDialog.show(appContext, "", appContext.getString(R.string.uploadingimage));
            //	pd.setCancelable(true);
            BoostLog.d("ILUD Upload Asynctask", appContext.getString(R.string.uploading_image));
        } else {
            //pd= ProgressDialog.show(appContext, "Please wait ...", "Downloading Image ...", true);
            //pd.setCancelable(false);
            pd = new ProgressDialog(appContext);
            this.pd.setMessage("Deleting...");
            this.pd.show();
        }

    }

    @Override
    protected void onPostExecute(String result) {
//        BoostLog.d("UploadPicAsyncTask","onPostExecute : "+Constants.storeSecondaryImages.size());
//        Toast.makeText(appContext,"Success  "+result,Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(result) && result.equals("true")) {
            Methods.showSnackBarPositive(appContext, appContext.getString(R.string.image_successfully_apdated));
            if (appContext != null && !appContext.isFinishing() && pd.isShowing()) {
                pd.dismiss();
            }
        } else {
            Methods.showSnackBarNegative(appContext, appContext.getString(R.string.can_not_upload_image));
            if (appContext != null && !appContext.isFinishing() && pd.isShowing()) {
                pd.dismiss();
            }
        }
        //  BoostLog.d("UploadPictureInterface","uploadInterface : "+uploadInterface);
        uploadInterface.uploadedPictureListener(path);
        onPost();

    }

    public void onPost() {
        if (success && !isPrimary && !isBackgroundImage) {
            Constants.uploadedImg = path;
            pd.dismiss();
            if (!isGalleryImage) {
                //  Util.toast("Logo image updated.", appContext);
                Constants.isImgUploaded = true;
                Constants.uploadedImg = path;

                // appContext.finish();
            } else {
                //  Util.toast("Gallery image updated.", appContext);
                // Toast.makeText(appContext,"Gallery Image Update ",Toast.LENGTH_SHORT).show();

                //   BoostLog.d(TAG,"Gallery Updated");
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
        if (!Util.isNullOrEmpty(path)) {
            response = uploadImage(path);
            return response;
        }
        if (arrImagePaths != null && arrImagePaths.size() > 0) {
            for (String path : arrImagePaths)
                response = uploadImage(path);
            return response;
        }
        if (delImg && isBackgroundImage) {
            removebackgroundImg();
        }
        if (success) {
            //getFpData();
            if (isBackgroundImage) {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    System.out.println();

                }
                Fp_bakgrnd_img_after_deletion();
            }
        }
        if (delImg && IsbackImgDeleted) {
            //getFpData();
            if (isBackgroundImage) {
                Fp_bakgrnd_img_after_deletion();
            }
        }
        return response;
    }

    public String uploadImage(String imagePath) {

        File imagefile = new File(imagePath);
        FileInputStream fis = null;
        String response = null;
        try {
            fis = new FileInputStream(imagefile);
            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if ((imagefile.length() / 1024) > 100) {
                bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            } else {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            byte[] bitmapdata = baos.toByteArray();

            UUID uuid = null;

            uuid = UUID.randomUUID();
            String s_uuid = uuid.toString();
            s_uuid = s_uuid.replace("-", "");
            String uri = null;
            String param = "createSecondaryImage/";
            if (isPrimary) {
                param = "createImage";
            } else if (isLogo) {
                param = "createLogoImage/";
            } else if (isBackgroundImage) {
                param = "createBackgroundImage/";
            }

            //Constants con = new Constants(appContext);
            if (isParallel) {
//            uri = Constants.LoadStoreURI+param+
//                    "?clientId="+ Constants.clientId+
//                    "&fpId="+ session.getFPID()+"&reqType=sequential&reqtId=" + s_uuid + "&";
                uri = Constants.NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/createSecondaryImage/?clientId=" +
                        Constants.clientId +
                        "&fpId=" + session.getFPID() +
                        "&reqType=sequential&reqtId=" +
                        s_uuid + "&";

                String temp = uri + "totalChunks=1&currentChunkNumber=1";
                response = sendDataToServer(imagePath, uri);

                //sendDataToServer(uri,  bitmapdata);
            } else {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE))) {
                    //removebackgroundImg();
                    String backgroundimgid = (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)).replace("/Backgrounds/", "");
                    uri = Constants.ReplaceBackImg +
                            "?clientId=" +
                            Constants.clientId + "&fpId=" + session.getFPID() +
                            "&existingBackgroundImageUri=" + backgroundimgid + "&identifierType=SINGLE";
                    response = sendDataToServer(imagePath, uri);
                } else {
                    uri = Constants.LoadStoreURI + param + "/?clientId=" +
                            Constants.clientId + "&fpId=" + session.getFPID();
                    response = sendDataToServer(imagePath, uri);
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            response = null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Methods.showSnackBar(appContext, "Image is too large");
        }


        return response;
    }

    public void removebackgroundImg() {
        //if(!Util.isNullOrEmpty(Constants.storedBackgroundImage))
        //IsbackImgDeleted=Util.deletebackgroundImg(Constants.storedBackgroundImage);
    }

    public void Fp_bakgrnd_img_after_deletion() {
        String serverUri = Constants.GetBackgroundImage +
                "?clientId=" + Constants.clientId + "&fpId=" + session.getFPID();
        String backgroundimageurl = "";
        String backgroundimgid = "";
        try {
            Thread.sleep(5000);
            HttpClient client = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(serverUri);
            org.apache.http.HttpResponse responseOfSite = client.execute(httpRequest);
            HttpEntity entity = (HttpEntity) ((org.apache.http.HttpResponse) responseOfSite).getEntity();
            if (entity != null) {
                String str = (EntityUtils.toString(entity));
                JSONArray bgjsonarray = new JSONArray(str);
                if ((bgjsonarray) != null) {
                    int len = bgjsonarray.length();
                    if (len != 0) {
                        String storedBackgroundImage = bgjsonarray.getString(len - 1);
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, storedBackgroundImage);
                    } else {
                        session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, "");

                    }
                }
            }


        } catch (Exception e) {
            System.out.println();

        }


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

    public String sendDataToServer(String filename, String targetUrl) {
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
            connection.setRequestProperty("Content-Type", "application/octet-stream");

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


    public interface UploadPictureInterface {
        public void uploadedPictureListener(String imageURL);
    }
}