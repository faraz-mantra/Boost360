package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetGalleryImagesAsyncTask extends AsyncTask<Void, String, String> {

    ProgressDialog pd = null;
    OtherImagesAdapter adapter = null;
    String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
    UserSessionManager session;
    private Activity appContext = null;

    public GetGalleryImagesAsyncTask(Activity context) {
        this.appContext = context;
        session = new UserSessionManager(appContext, appContext);
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(appContext, "", appContext.getString(R.string.updating_image));
        pd.setCancelable(false);
    }

    @Override
    protected void onPostExecute(String result) {
        pd.cancel();
        // Log.d("Gallery Images", " Result : "+result);
        if (result.length() > 1) {
            //  Log.d("Gallery Images", " Result : "+result);
//            adapter.setList(Constants.storeSecondaryImages);
//            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        // Log.d("GetGalleryImagesAsyncTask","doInBackground");
        try {
            response = Util.getDataFromServer(clientIdConcatedWithQoutes,
                    Constants.HTTP_POST,
                    Constants.LoadStoreURI + session.getFPID(),
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            //   Log.d("GetGalleryImagesAsyncTask","Response : "+response);
            if (response.length() > 1) {
                Constants.hasStoreData = true;
                JSONObject store = new JSONObject(response);

                if (response.contains("TileImageUri"))
                    Constants.storePrimaryImage = store.getString("TileImageUri");
                if (response.contains("SecondaryTileImages")) {
                    try {

                        JSONArray array = store.getJSONArray("SecondaryTileImages");
                        if (array != null) {
                            int len = array.length();
                            Log.d("Gallery", "");
                            Constants.storeSecondaryImages = new ArrayList<String>();
                            if (len != 0) {
                                for (int i = 0; i < len; i++) {
                                    Constants.storeSecondaryImages.add(array.getString(i));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}