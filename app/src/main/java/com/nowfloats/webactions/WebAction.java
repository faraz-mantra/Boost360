package com.nowfloats.webactions;

import android.os.Handler;
import android.text.TextUtils;

import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.nowfloats.ProductGallery.Model.ProductKeywordResponseModel;
import com.nowfloats.util.Constants;
import com.nowfloats.webactions.models.WebActionAddDataModel;
import com.nowfloats.webactions.models.WebActionDataResponse;
import com.nowfloats.webactions.models.WebActionError;
import com.nowfloats.webactions.models.WebActionList;
import com.nowfloats.webactions.models.WebActionUpdateRequestModel;
import com.nowfloats.webactions.models.WebActionVisibility;
import com.nowfloats.webactions.webactioninterfaces.IFilter;
import com.nowfloats.webactions.webactioninterfaces.IUpdate;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NowFloats on 09-04-2018.
 */

public class WebAction {

    private String mAuthHeader;
    private String mWebActionName;
    private WebActionNetworkModule mNetworkModule;

    private WebAction(WebActionNetworkModule networkModule, String authHeader, String webActionName) {
        this.mAuthHeader = authHeader;
        this.mNetworkModule = networkModule;
        this.mWebActionName = webActionName;
    }

    public String getWebActionName() {
        return this.mWebActionName;
    }

    public WebAction setWebActionName(String webActionName) {
        this.mWebActionName = webActionName;
        return this;
    }

    public void getAllWebActions(String webActionType, WebActionVisibility visibility,
                                 final WebActionCallback<List<com.nowfloats.webactions.models.WebAction>> webActionCallback) {
        Map<String, String> queryMap = new HashMap<>();
        if (!TextUtils.isEmpty(webActionType)) {
            queryMap.put("Type", webActionType);
        }

        if (visibility != WebActionVisibility.NONE) {
            queryMap.put("Visibility", visibility.getValue() + "");
        }
        mNetworkModule.getWebActionService().getWebActionList(mAuthHeader, queryMap, new Callback<WebActionList>() {
            @Override
            public void success(WebActionList webActions, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(webActions.getWebActions());
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });

    }

    public <T> void findById(String id, final WebActionCallback<T> webActionCallback) {
        mNetworkModule.getWebActionService().getDataById(mAuthHeader, mWebActionName, id, new Callback<WebActionDataResponse<T>>() {
            @Override
            public void success(WebActionDataResponse<T> data, Response response) {
                if (webActionCallback == null)
                    return;
                if (data != null && data.getData() != null && data.getData().size() > 0) {
                    webActionCallback.onSuccess(data.getData().get(0));
                } else {
                    webActionCallback.onSuccess(null);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });
    }

    public <T> void findOne(IFilter filter, final WebActionCallback<T> webActionCallback) {
        Map<String, String> query = new HashMap<>();
        query.put("query", filter.toString());
        query.put("limit", "1");
        mNetworkModule.getWebActionService().getData(mAuthHeader, mWebActionName, query, new Callback<WebActionDataResponse<T>>() {
            @Override
            public void success(WebActionDataResponse<T> data, Response response) {
                if (webActionCallback == null)
                    return;
                if (data != null && data.getData() != null && data.getData().size() > 0) {
                    webActionCallback.onSuccess(data.getData().get(0));
                } else {
                    webActionCallback.onSuccess(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });
    }

    public void findProductKeywords(IFilter filter, final WebActionCallback<List<ProductKeywordResponseModel>> webActionCallback) {
        Map<String, String> query = new HashMap<>();
        query.put("query", filter.toQuery().toString());
        mNetworkModule.getWebActionService().getProductKeywordsData(mAuthHeader, mWebActionName, query, new Callback<WebActionDataResponse<ProductKeywordResponseModel>>() {
            @Override
            public void success(WebActionDataResponse<ProductKeywordResponseModel> data, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(data.getData());
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });
    }

    public void findProductImages(IFilter filter, final WebActionCallback<List<ProductImageResponseModel>> webActionCallback) {
        Map<String, String> query = new HashMap<>();
        query.put("query", filter.toQuery().toString());
        mNetworkModule.getWebActionService().getProductImagesData(mAuthHeader, mWebActionName, query, new Callback<WebActionDataResponse<ProductImageResponseModel>>() {
            @Override
            public void success(WebActionDataResponse<ProductImageResponseModel> data, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(data.getData());
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });
    }

    public <T> void insert(String websiteId, T data, final WebActionCallback<String> webActionCallback) {
        WebActionAddDataModel<T> webActionData = new WebActionAddDataModel<>(websiteId, data);
        mNetworkModule.getWebActionService().addData(mAuthHeader, mWebActionName, webActionData, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(s);
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });
    }

    public void update(IFilter filter, IUpdate update, final WebActionCallback<Boolean> webActionCallback) {
        WebActionUpdateRequestModel updateRequestModel = new WebActionUpdateRequestModel(filter, update);
        mNetworkModule.getWebActionService().updateData(mAuthHeader, mWebActionName, updateRequestModel, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(true);
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(true);
            }
        });
    }

    public void delete(IFilter filter, boolean isMultiple, final WebActionCallback<Boolean> webActionCallback) {
        WebActionUpdateRequestModel updateRequestModel = new WebActionUpdateRequestModel(filter, null);
        updateRequestModel.setMulti(isMultiple);
        mNetworkModule.getWebActionService().deleteData(mAuthHeader, mWebActionName, updateRequestModel, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if (webActionCallback == null)
                    return;
                webActionCallback.onSuccess(true);
            }

            @Override
            public void failure(RetrofitError error) {
                if (webActionCallback == null)
                    return;
                if (error.getResponse().getStatus() == 200) {
                    webActionCallback.onSuccess(true);
                } else {
                    webActionCallback.onFailure(new WebActionError(error.getMessage()));
                }

            }
        });
    }

    public void uploadFile(String filePath, final WebActionCallback<String> webActionCallback, final Handler handler) {
        File file = new File(filePath);
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), okhttp3.RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        Request request = new Request.Builder().url(Constants.WA_BASE_URL + this.mWebActionName + "/upload-file?assetFileName=" + file.getName())
                .header("Authorization", mAuthHeader)
                .post(body).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webActionCallback.onFailure(new WebActionError("Uploading Failed"));
                    }
                });

            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            webActionCallback.onSuccess(response.body().string());
                        } catch (IOException ex) {
                            webActionCallback.onFailure(new WebActionError("Uploading Failed"));
                        }
                    }
                });
            }
        });

        /*mNetworkModule.getWebActionService().uploadFile(mAuthHeader, file.getName(), mWebActionName, new TypedFile("multipart/form-data", file), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if(webActionCallback == null)
                    return;
                webActionCallback.onSuccess(s);
            }

            @Override
            public void failure(RetrofitError error) {
                error.getResponse().
                if(webActionCallback == null)
                    return;
                try {
                    InputStreamReader reader = new InputStreamReader(error.getResponse().getBody().in());
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    if(sb.length() > 0) {
                        webActionCallback.onSuccess(sb.toString());
                        return;
                    }

                } catch (IOException ex) {

                }

                webActionCallback.onFailure(new WebActionError(error.getMessage()));
            }
        });*/
    }

    public interface WebActionCallback<T> {
        public void onSuccess(T result);

        public void onFailure(WebActionError error);
    }

    public static class WebActionBuilder {

        private String authHeader;
        private String webActionName;


        public WebActionBuilder setAuthHeader(String authHeader) {
            this.authHeader = authHeader;
            return this;
        }

        public WebActionBuilder setWebActionName(String webActionName) {
            this.webActionName = webActionName;
            return this;
        }

        public WebAction build() throws RuntimeException {
            if (TextUtils.isEmpty(authHeader)) {
                throw new RuntimeException("AuthHeader for the webaction is not set");
            }
            WebActionNetworkModule networkModule = WebActionNetworkModule.init(Constants.WA_BASE_URL);
            return new WebAction(networkModule, authHeader, webActionName);
        }
    }


}
