package com.nowfloats.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Image_Gallery.OtherImagesAdapter;
import com.nowfloats.util.Constants;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetGalleryImagesAsyncTask_Interface extends AsyncTask<Void,String, String>{

	ProgressDialog pd 				= null;
	private Activity appContext 	= null;
	OtherImagesAdapter adapter = null;
	String clientIdConcatedWithQoutes = "\"" + Constants.clientId +"\"";
    UserSessionManager session;
    public interface getGalleryImagesInterface {
        public void imagesReceived();
    }

    private getGalleryImagesInterface galleryInterface ;

	public GetGalleryImagesAsyncTask_Interface(Activity context,UserSessionManager session) {
		this.appContext	=  	context;
        this.session = session;
		this.adapter = adapter;
       // galleryInterface = (getGalleryImagesInterface) appContext ;
	}

 public void setGalleryInterfaceListener(getGalleryImagesInterface galleryInterface)
 {
     this.galleryInterface = galleryInterface ;
 }

	@Override
	protected void onPreExecute() {
		//pd= ProgressDialog.show(appContext, "", "Retrieving  Images...");
		//pd.setCancelable(false);
	}

	@Override
	protected void onPostExecute(String result) {
      // pd.dismiss();
       // Log.d("Gallery Images", " Result : "+result);
        galleryInterface.imagesReceived();
//        Log.d("Images REceived","Images Received : Count "+Constants.storeSecondaryImages.size());
		if(result.length()>1){
        //  Log.d("Gallery Images", " Result : "+result);
//            adapter.setList(Constants.storeSecondaryImages);
//            adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		String response = "";
       // Log.d("GetGalleryImagesAsyncTask","doInBackground");
		try{
			response = Util.getDataFromServer(clientIdConcatedWithQoutes,
                    Constants.HTTP_POST,
                    Constants.LoadStoreURI + session.getFPID(),
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON);
         //   Log.d("GetGalleryImagesAsyncTask","Response : "+response);
			if (response.length() >= 1) {
				Constants.hasStoreData = true;
				JSONObject store 				= new JSONObject(response);
				if(response.contains("TileImageUri"))
					Constants.storePrimaryImage 	= store.getString("TileImageUri");

				if (response.contains("SecondaryTileImages")) {
					try {
                        if (store!=null && store.getJSONArray("SecondaryTileImages")!=null){
                            JSONArray array = store.getJSONArray("SecondaryTileImages");
                            if(array != null){
                                int len = array.length();
                                Log.d("Gallery","Length : "+len);
                                Constants.storeSecondaryImages = new ArrayList<String>();
                                if (len != 0) {
                                    for(int i =0 ; i < len ; i++){
                                        Constants.storeSecondaryImages.add(array.getString(i));
                                    }
                                    Constants.storeActualSecondaryImages = Constants.storeSecondaryImages;
                                }
                            }
                        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

                /*if (response.contains("SecondaryImages")) {
                    try {
                        if (store!=null && store.getJSONArray("SecondaryImages")!=null){
                            JSONArray array = store.getJSONArray("SecondaryImages");
                            if(array != null){
                                int len = array.length();
                                Log.d("Gallery","Length : "+len);
                                Constants.storeActualSecondaryImages = new ArrayList<String>();
                                if (len != 0) {
                                    for(int i =0 ; i < len ; i++){
                                        Constants.storeActualSecondaryImages.add(array.getString(i));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}