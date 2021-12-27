package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.Home_View_Card_Delete;
import com.nowfloats.NavigationDrawer.model.PostImageSuccessEvent;
import com.nowfloats.NavigationDrawer.model.PostTaskModel;
import com.nowfloats.NavigationDrawer.model.PostTextSuccessEvent;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.UploadLargeImage;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.QueryMap;

public final class UploadMessageTask implements UploadLargeImage.ImageCompressed {

    String path = null;
    Boolean success = false;
    ListenNew listenNew;
    String subscribers = "false";
    PostTaskModel obj;
    UserSessionManager session;
    String textId;
    private Activity appContext = null;

    public UploadMessageTask(Activity context, String path, PostTaskModel offerObj, UserSessionManager session) {
        this.appContext = context;
        this.obj = offerObj;
        this.path = path;
        this.session = session;
    }

    public UploadMessageTask(Activity context, String path, PostTaskModel offerObj, UserSessionManager session, ListenNew listenNew) {
        this.appContext = context;
        this.obj = offerObj;
        this.path = path;
        this.session = session;
        this.listenNew = listenNew;
    }

    @Override
    public void onImageCompressed(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        if (!Util.isNullOrEmpty(textId)) {
            uploadImage(b, textId);
        }
    }

    public void UploadPostService() {
        try {
            UploadPostInterface postInterface = Constants.restAdapter.create(UploadPostInterface.class);
            postInterface.postText(obj, new Callback<String>() {
                @Override
                public void success(String txtId, Response retroResponse) {
                    //Log.d("Response", "Text Response : " + txtId);
                    if (!Util.isNullOrEmpty(txtId) && !Util.isNullOrEmpty(path) && path.length() > 1) {
                        textId = txtId;
                        new UploadLargeImage(appContext, UploadMessageTask.this, path, 720, 720).execute();
                    } else if (txtId != null && txtId.length() > 1) {
                        success = true;
                        //Constants.createMsg = false;
                        /*if (Constants.twitterShareEnabled && Home_Main_Fragment.facebookPostCount == 0) {
                            Constants.twitterShareEnabled = false;
                            String tweetData = "";
                            try {
                                tweetData = obj.message;
                                *//*PostImageTweetInBackgroundAsyncTask tweet = new PostImageTweetInBackgroundAsyncTask(appContext,obj.message,txtId,path,session);
                                tweet.execute();*//*
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                Log.i("Text---wait", " for 2 SEconDS");
                                if (Home_Main_Fragment.bus != null)
                                    Home_Main_Fragment.bus.post(new PostTextSuccessEvent(true));
                                if (listenNew != null) listenNew.postUpdate(true, "Post Updated.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    success = false;
                    if (listenNew != null) listenNew.postUpdate(false, "Post updating error!");
                    //Constants.createMsg = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listenNew != null) listenNew.postUpdate(false, "Post updating error!");
        }
    }

    public void uploadImage(final byte[] bitmapdata, final String response) {
        try {
            UUID uuid = UUID.randomUUID();
            String s_uuid = uuid.toString();
            s_uuid = s_uuid.replace("-", "");
            try {
                boolean val = obj.sendToSubscribers;
                if (val) subscribers = "true";
                else subscribers = "false";
            } catch (Exception e1) {
                e1.printStackTrace();
            }
           /* HashMap<String,String> values = new HashMap<>();
            values.put("clientId",Constants.clientId);
            values.put("bizMessageId",response);
            values.put("requestType","sequential");
            values.put("requestId",s_uuid);
            values.put("sendToSubscribers",subscribers);
            values.put("totalChunks","1");
            values.put("currentChunkNumber","1");*/


            final String uri = Constants.PictureFloatImgCreationURI + "?clientId=" + Constants.clientId + "&bizMessageId=" + response + "&requestType=sequential&requestId="
                    + s_uuid + "&sendToSubscribers=" + Create_Message_Activity.tosubscribers + "&" + "totalChunks=1&currentChunkNumber=1&socialParmeters=" + obj.socialParameters;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendDataToServer(uri, bitmapdata, response);
                }
            }).start();
//            sendDataToServer(values,response,bitmapdata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataToServer(String url, byte[] imageData, final String txtId) {
        DataOutputStream outputStream = null;
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
            connection.setRequestProperty("Authorization", Utils.getAuthToken());
            if (!url.toLowerCase().contains("createbizimage"))
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            else
                connection.setRequestProperty("Content-Type",
                        "text/plain");

            if (imageData != null) {
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(imageData, 0, imageData.length);
                //Log.d("Test:::Test", ":::::Again:::1");
            }

            int responseCode = connection.getResponseCode();
            success = responseCode == 200 || responseCode == 202;

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
                //Log.d("Message Task","Image Response : "+response);
                if (success) {
                    /*if(Constants.twitterShareEnabled && obj.message!=null && Home_Main_Fragment.facebookPostCount == 0)
                    {
                        //PostImageTweetInBackgroundAsyncTask tweet = new PostImageTweetInBackgroundAsyncTask(appContext,obj.message,txtId,path,session);
                        //tweet.execute();

                    }*/
                    Create_Message_Activity.imageIconButtonSelected = false;
                    Constants.serviceResponse = response;
                    //Log.d("Test:::Test", ":::::Again:::  " + path + "    "  + url);
                    if (path != null && !path.equals("")) {
                        //Log.d("Test:::Test", ":::::");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(8000);
                                    //Log.i("IMAGE---wait"," for 8 SEconDS");
                                    if (Home_Main_Fragment.bus != null)
                                        Home_Main_Fragment.bus.post(new PostImageSuccessEvent(txtId));
                                    if (listenNew != null)
                                        listenNew.postUpdate(true, "Post Updated.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    Constants.serviceResponse = "";
                }
                if (!success) {
                    //Log.i("Image UPLOAD FAILED","");
                    Methods.showSnackBarNegative(appContext, appContext.getString(R.string.image_uploading_failed_try_again));
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("dealId", txtId);
                        obj2.put("clientId", Constants.clientId1);
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    Home_View_Card_Delete deleteCard = new Home_View_Card_Delete(appContext, Constants.DeleteCard, obj2, 0, null, 1);
                    deleteCard.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (listenNew != null)
                    listenNew.postUpdate(false, appContext.getString(R.string.post_image_uploading_error));
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
            success = false;
            if (listenNew != null)
                listenNew.postUpdate(false, appContext.getString(R.string.post_image_uploading_error));
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }


    public interface UploadPostInterface {
        @PUT("/Discover/v1/FloatingPoint/createBizMessage")
        void postText(@Body PostTaskModel task, Callback<String> callback);

        @Headers({"Content-Type: application/x-www-form-urlencoded", "Connection: Keep-Alive"})
        @PUT("/Discover/v1/FloatingPoint/createBizImage")
        void uploadPic(@Body byte[] file, @QueryMap HashMap<String, String> map, Callback<String> cb);
    }

    interface ListenNew {
        void postUpdate(Boolean isSuccess, String msg);
    }
}