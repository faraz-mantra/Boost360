package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONObject;

public final class DeleteGalleryImages extends AsyncTask<Void,String, String> {


	boolean status	=	false;
	Activity	ctx = null;
	int pos = 0;
	String responseMessage = null;
	boolean[] selected = null;
	OtherImagesAdapter adapter = null;
    ImageAdapter imageAdapter ;
	ProgressDialog pd 	= null;
    private int selectedPosition;
    UserSessionManager session;

//    public DeleteGalleryImages(Activity	ctx,boolean[] selected,OtherImagesAdapter adapter)
//	{
//		this.ctx		=	ctx;
//		this.selected = selected;
//		this.adapter = adapter;
//	}

    public interface DeleteGalleryInterface {
        public void galleryImageDeleted();
    }

    DeleteGalleryInterface deleteInterface ;

    public DeleteGalleryImages(Activity context, ImageAdapter imageAdapter,int SelectedPosition) {
        this.ctx = context ;
        this.imageAdapter = imageAdapter ;
        selectedPosition = SelectedPosition ;
        session = new UserSessionManager(context.getApplicationContext(),context);
        //deleteInterface = (DeleteGalleryInterface) context ;
    }

    public void setOnDeleteListener(DeleteGalleryInterface deleteInterface){
        this.deleteInterface = deleteInterface;
    }


    @Override
	protected void onPreExecute() 
	{
		pd= ProgressDialog.show(ctx, null, ctx.getString(R.string.deleting));
	}
	@Override
	protected void onPostExecute(final String result) 
	{

		if(pd!=null && pd.isShowing()){
			pd.dismiss();
		}
		
		String temp = null;
		if(status)
		{
            MixPanelController.track("ImageDeleted", null);
			temp	=	"Deleted";
		}
		else
		{
			temp	=	"error";
		}
		if(adapter!=null)
		{
		    adapter.setList(Constants.storeSecondaryImages);
			adapter.notifyDataSetChanged();
		}
        if(deleteInterface != null)
        deleteInterface.galleryImageDeleted();

        ctx.finish();
		//Toast.makeText(ctx, temp, Toas).show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
		try
		{
		//	for(int i=0;i<selected.length;i++)
	//		{
	//			if(selected[i])
//				{
                   int i = selectedPosition;
					String url	=	Constants.deleteGalleryImgs;
					//fpId, secondaryImageFilename, clientId
					JSONObject obj = new JSONObject();
					obj.put("fpId",  session.getFPID());
					obj.put("secondaryImageFilename", Constants.storeSecondaryImages.get(i).replace("/FP/Tile/", ""));
					obj.put("clientId", Constants.clientId);
                    Log.d("DeleteGalleryImages","Path : "+url+" , "+obj.toString());
					status = Util.deleteWithBody(url, obj.toString());
                    Log.d("DeleteGalleryImages", "Status : "+status);
				//	if(status)
				//	{
						Constants.storeSecondaryImages.remove(i);
                       // session.
				//	}
		//		}
	//		}
				
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

}
