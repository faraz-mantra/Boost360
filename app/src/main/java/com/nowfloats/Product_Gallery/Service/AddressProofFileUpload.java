package com.nowfloats.Product_Gallery.Service;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.Product_Gallery.Model.ProductImageRequestModel;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.models.ProductImage;
import com.nowfloats.webactions.models.WebActionError;

public class AddressProofFileUpload extends AsyncTask<Void, String, String>
{
    private String TAG = AddressProofFileUpload.class.getSimpleName();

    private String productId;
    private UserSessionManager session;
    private WebAction mWebAction;
    private String path;

    public AddressProofFileUpload(UserSessionManager session, WebAction webAction, String path)
    {
        this.session = session;
        this.mWebAction = webAction;
        this.path = path;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected String doInBackground(Void... strings)
    {
        uploadFile(path);
        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute");
    }

    private void uploadFile(final String path)
    {
        mWebAction.uploadFile(path, new WebAction.WebActionCallback<String>() {

            @Override
            public void onSuccess(String result)
            {
                Log.d(TAG, "Result: " + result);
                //addImageData(result, image);
            }

            @Override
            public void onFailure(WebActionError error)
            {
                Log.d(TAG, "Fail: Image Upload Fail");
            }
        }, new Handler(Looper.getMainLooper()));
    }


    private void addImageData(final String result, final ProductImage image)
    {
        if (!TextUtils.isEmpty(result))
        {
            final ProductImageRequestModel productImageRequestModel = new ProductImageRequestModel();
            productImageRequestModel._pid = productId;
            productImageRequestModel.image = new ProductImage(result, image.description != null ? image.description : "");

            mWebAction.insert(session.getFpTag(), productImageRequestModel, new WebAction.WebActionCallback<String>() {

                @Override
                public void onSuccess(String id) {

                    Log.d(TAG, "Id: " + id);
                }

                @Override
                public void onFailure(WebActionError error)
                {
                    Log.d(TAG, "IMAGE RESPONSE FAIL");
                }
            });
        }
    }
}