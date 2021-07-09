package com.nowfloats.ProductGallery.Service;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Model.ProductImageRequestModel;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.models.ProductImage;
import com.nowfloats.webactions.models.WebActionError;

public class MultipleFileUpload extends AsyncTask<Void, String, String>
{
    private String TAG = MultipleFileUpload.class.getSimpleName();

    private String productId;
    private UserSessionManager session;
    private WebAction mWebAction;
    private ProductImage image;

    public MultipleFileUpload(String productId, UserSessionManager session, WebAction webAction, ProductImage image)
    {
        this.productId = productId;
        this.session = session;
        this.mWebAction = webAction;
        this.image = image;
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
        uploadFile(image);
        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute");
    }

    private void uploadFile(final ProductImage image)
    {
        mWebAction.uploadFile(image.url, new WebAction.WebActionCallback<String>() {

            @Override
            public void onSuccess(String result)
            {
                Log.d(TAG, "Result: " + result);
                addImageData(result, image);
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