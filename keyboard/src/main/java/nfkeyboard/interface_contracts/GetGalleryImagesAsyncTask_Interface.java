package nfkeyboard.interface_contracts;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nfkeyboard.util.Constants;
import nfkeyboard.util.Util;

/**
 * Created by Shimona on 01-06-2018.
 */

public class GetGalleryImagesAsyncTask_Interface extends AsyncTask<Void, String, String> {

    String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
    private String fpId;
    private getGalleryImagesInterface galleryInterface;

    public GetGalleryImagesAsyncTask_Interface() {
    }

    public void setGalleryInterfaceListener(getGalleryImagesInterface galleryInterface, String fpId) {
        this.galleryInterface = galleryInterface;
        this.fpId = fpId;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {
        galleryInterface.imagesReceived();
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        try {
            response = Util.getDataFromServer(clientIdConcatedWithQoutes,
                    Constants.HTTP_POST,
                    Constants.LoadStoreURI + fpId,
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
            if (response.length() >= 1) {
                Constants.hasStoreData = true;
                JSONObject store = new JSONObject(response);
                if (response.contains("ImageUri"))
                    Constants.storePrimaryImage = store.getString("ImageUri");

                if (response.contains("SecondaryImages")) {
                    try {
                        if (store != null && store.getJSONArray("SecondaryImages") != null) {
                            JSONArray array = store.getJSONArray("SecondaryImages");
                            if (array != null) {
                                int len = array.length();
                                Constants.storeSecondaryImages = new ArrayList<>();
                                if (len != 0) {
                                    for (int i = 0; i < len; i++) {
                                        Constants.storeSecondaryImages.add(0, array.getString(i));
                                    }
                                    Constants.storeActualSecondaryImages = Constants.storeSecondaryImages;
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

    public interface getGalleryImagesInterface {
        void imagesReceived();
    }

}