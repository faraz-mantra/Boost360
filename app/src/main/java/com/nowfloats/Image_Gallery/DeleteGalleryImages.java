package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_DELETE_GALLERY_IMAGE;
import static com.framework.webengageconstant.EventNameKt.DELETE_GALLERY_IMAGE;

public final class DeleteGalleryImages extends AsyncTask<Void, String, String> {


    boolean status = false;
    private Activity ctx = null;
    private OtherImagesAdapter adapter = null;
    private ImageAdapter imageAdapter;
    private ProgressDialog pd = null;
    private int selectedPosition;
    private UserSessionManager session;
    private ArrayList<Integer> arrSelectedImages;
    private boolean hasToFinish = true;

    public DeleteGalleryImages(Activity ctx, ArrayList<Integer> arrSelectedImages, OtherImagesAdapter adapter) {
        this.ctx = ctx;
        session = new UserSessionManager(ctx.getApplicationContext(), ctx);
        this.arrSelectedImages = arrSelectedImages;
        this.adapter = adapter;
        hasToFinish = false;
    }

    public interface DeleteGalleryInterface {
        public void galleryImageDeleted();
    }

    DeleteGalleryInterface deleteInterface;

    public DeleteGalleryImages(Activity context, ImageAdapter imageAdapter, int SelectedPosition) {
        this.ctx = context;
        hasToFinish = true;
        this.imageAdapter = imageAdapter;
        selectedPosition = SelectedPosition;
        session = new UserSessionManager(context.getApplicationContext(), context);
        //deleteInterface = (DeleteGalleryInterface) context ;
    }

    public void setOnDeleteListener(DeleteGalleryInterface deleteInterface) {
        this.deleteInterface = deleteInterface;
    }


    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(ctx, null, ctx.getString(R.string.deleting));
    }

    @Override
    protected void onPostExecute(final String result) {

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }

        String temp = null;
        if (status) {
            MixPanelController.track("ImageDeleted", null);
            WebEngageController.trackEvent(DELETE_GALLERY_IMAGE,EVENT_LABEL_DELETE_GALLERY_IMAGE,session.getFpTag());
            temp = "Deleted";
        } else {
            temp = "error";
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (deleteInterface != null)
            deleteInterface.galleryImageDeleted();

        if (hasToFinish)
            ctx.finish();
        //Toast.makeText(ctx, temp, Toas).show();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {

            if (arrSelectedImages != null && arrSelectedImages.size() > 0) {
                int count = 0;
                for (Integer selectedPos : arrSelectedImages) {
                    if (selectedPos > 0) {
                        selectedPos = selectedPos - count;
                    }
                    deleteSelectedImage(selectedPos);
                    Constants.storeSecondaryImages.remove((int) selectedPos);
                    count++;
                }
            } else {
                deleteSelectedImage(selectedPosition);
                Constants.storeSecondaryImages.remove(selectedPosition);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void deleteSelectedImage(int pos) {
        String url = Constants.deleteGalleryImgs;
        JSONObject obj = new JSONObject();
        try {
            obj.put("fpId", session.getFPID());
            obj.put("secondaryImageFilename", Constants.storeSecondaryImages.get(pos).replace("/FP/Tile/", ""));
            obj.put("clientId", Constants.clientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DeleteGalleryImages", "Path : " + url + " , " + obj.toString());
        status = Util.deleteWithBody(url, obj.toString());
        Log.d("DeleteGalleryImages", "Status : " + status);
    }
}
