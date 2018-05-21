package nowfloats.nfkeyboard.network;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import nowfloats.nfkeyboard.R;
import nowfloats.nfkeyboard.interface_contracts.ApiCallToKeyboardViewInterface;
import nowfloats.nfkeyboard.keyboards.ImePresenterImpl;
import nowfloats.nfkeyboard.models.AllSuggestionModel;
import nowfloats.nfkeyboard.models.networkmodels.Product;
import nowfloats.nfkeyboard.util.SharedPrefUtil;

/**
 * Created by Admin on 28-03-2018.
 */

public class ApiCallPresenter {
    private NetworkAdapter adapter = new NetworkAdapter();
    private Context mContext;
    private ApiCallToKeyboardViewInterface apiCallListener;

    public ApiCallPresenter(Context context, ApiCallToKeyboardViewInterface apiCallListener){
        mContext = context;
        this.apiCallListener = apiCallListener;
    }
    public void loadMore(int skipBy, ImePresenterImpl.TabType tabType){
        switch (tabType){
            case PRODUCTS:
                adapter.getAllProducts(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpTag(),mContext.getString(R.string.client_id),skipBy,"SINGLE",productCallback);
                break;
            case UPDATES:
                adapter.getAllUpdates(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpId(),mContext.getString(R.string.client_id),skipBy,10,updateCallback);
                break;
            default:
                break;
        }

    }


    private CallBack<List<Product>> productCallback = new CallBack<List<Product>>() {
        ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
        @Override
        public void onSuccess(List<Product> data) {
            modelList.clear();
            if (data != null) {
                for (Product product : data) {
                    modelList.add(product.toAllSuggestion());
                }
            }
            if (modelList.size()<10){
                apiCallListener.onCompleted(ImePresenterImpl.TabType.PRODUCTS);
            }
            apiCallListener.onLoadMore(ImePresenterImpl.TabType.PRODUCTS,modelList);
        }

        @Override
        public void onError(Throwable t) {
            apiCallListener.onError(ImePresenterImpl.TabType.PRODUCTS);
        }
    };
    private CallBack<Updates> updateCallback = new CallBack<Updates>() {
        ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
        @Override
        public void onSuccess(Updates data) {
            modelList.clear();
            if (data != null && data.getFloats()!= null) {
                for (Float update : data.getFloats()) {
                    modelList.add(update.toAllSuggestion());
                }
            }
            if (modelList.size()<10){
                apiCallListener.onCompleted(ImePresenterImpl.TabType.UPDATES);
            }
            apiCallListener.onLoadMore(ImePresenterImpl.TabType.UPDATES,modelList);
        }

        @Override
        public void onError(Throwable t) {
            apiCallListener.onError(ImePresenterImpl.TabType.UPDATES);
        }
    };
}
