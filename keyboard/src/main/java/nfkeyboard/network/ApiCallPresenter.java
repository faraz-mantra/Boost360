package nfkeyboard.network;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ApiCallToKeyboardViewInterface;
import nfkeyboard.interface_contracts.CandidateToPresenterInterface;
import nfkeyboard.interface_contracts.GetGalleryImagesAsyncTask_Interface;
import nfkeyboard.keyboards.ImePresenterImpl;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.models.networkmodels.Details;
import nfkeyboard.models.networkmodels.Product;
import nfkeyboard.util.SharedPrefUtil;

/**
 * Created by Admin on 28-03-2018.
 */

public class ApiCallPresenter {
    private NetworkAdapter adapter = new NetworkAdapter();
    private Context mContext;
    private ApiCallToKeyboardViewInterface apiCallListener;
    private CandidateToPresenterInterface presenterListener;

    public ApiCallPresenter(Context context, ApiCallToKeyboardViewInterface apiCallListener) {
        mContext = context;
        this.apiCallListener = apiCallListener;
    }

    public void loadMore(int skipBy, ImePresenterImpl.TabType tabType, GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface getGalleryImagesInterface) {
        switch (tabType) {
            case PRODUCTS:
                adapter.getAllProducts(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpTag(), mContext.getString(R.string.client_id), skipBy, "SINGLE", productCallback);
                break;
            case UPDATES:
                adapter.getAllUpdates(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpId(), mContext.getString(R.string.client_id), skipBy, 10, updateCallback);
                break;
            case PHOTOS:
                adapter.getAllImageList(getGalleryImagesInterface, SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpId());
            default:
                break;
        }
    }

    private CallBack<CustomerDetails> customerDetailsCallBack = new CallBack<CustomerDetails>() {
        @Override
        public void onSuccess(CustomerDetails data) {
            Details details = new Details();
            details.setAddress(data.getAddress());

            if(SharedPrefUtil.fromBoostPref().getFpTag().equals(data.getTag()))
            {
                details.setName(SharedPrefUtil.fromBoostPref().getName() == null ? "" : (SharedPrefUtil.fromBoostPref().getName().equals("null") ? "" : SharedPrefUtil.fromBoostPref().getName()));
            }

            else
            {
                details.setName(data.getContactName() == null ? "" : (data.getContactName().toLowerCase().equals("null") ? "" : data.getContactName()));
            }

            details.setEmail(data.getEmail());
            details.setPhoneNumber(data.getPrimaryNumber());
            details.setWebsite(SharedPrefUtil.fromBoostPref().getWebsite());
            details.setBusinessName(data.getName());
            String location = "http://maps.google.com/maps?q=loc:" + data.lat+ "," + data.lng;
            details.setLocation(location);
            ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
            modelList.add(details.toAllSuggestion());
            apiCallListener.onDetailsLoaded(modelList);
        }

        @Override
        public void onError(Throwable t) {
            apiCallListener.onError(ImePresenterImpl.TabType.DETAILS);
        }
    };

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
            if (modelList.size() < 10) {
                apiCallListener.onCompleted(ImePresenterImpl.TabType.PRODUCTS, modelList);
            } else {
                apiCallListener.onLoadMore(ImePresenterImpl.TabType.PRODUCTS, modelList);
            }
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
            if (data != null && data.getFloats() != null) {
                for (Float update : data.getFloats()) {
                    modelList.add(update.toAllSuggestion());
                }
            }
            if (modelList.size() < 10) {
                apiCallListener.onCompleted(ImePresenterImpl.TabType.UPDATES, modelList);
            } else {
                apiCallListener.onLoadMore(ImePresenterImpl.TabType.UPDATES, modelList);
            }
        }

        @Override
        public void onError(Throwable t) {
            apiCallListener.onError(ImePresenterImpl.TabType.UPDATES);
        }
    };

    private CallBack<CreatedOffer> createOfferCallback = new CallBack<CreatedOffer>() {
        @Override
        public void onSuccess(CreatedOffer data) {
            presenterListener.onCreateProductOfferResponse(
                    data.getData().getProduct().getName(),
                    data.getData().getProduct().getPrice(),
                    data.getData().getPrice(),
                    data.getData().getCreatedOn(),
                    data.getData().getExpiresOn(),
                    data.getData().getUrl(),
                    data.getData().getProduct().getCurrencyCode()
            );
        }

        @Override
        public void onError(Throwable t) {
            Log.d("here", t.toString());
            presenterListener.onError();
        }
    };

    private String getName(String product) {
        int start = product.indexOf(" Name=");
        int end = product.indexOf(",", start);
        return product.substring(start + 6, end);
    }

    private String getPriceOriginal(String product) {
        int start = product.indexOf(" Price=");
        int end = product.indexOf(",", start);
        return product.substring(start + 7, end);
    }

    public void createProductOffers(AllSuggestionModel model, CandidateToPresenterInterface presenterListener) {
        adapter.createProductOffer(model, createOfferCallback);
        this.presenterListener = presenterListener;
    }

    public ArrayList<AllSuggestionModel> getAllDetails() {
        ArrayList<AllSuggestionModel> modelList = new ArrayList<>();
        modelList.clear();
        Details details = new Details();
        details.setName(SharedPrefUtil.fromBoostPref().getName());
        details.setBusinessName(SharedPrefUtil.fromBoostPref().getBusinessName());
        details.setPhoneNumber(SharedPrefUtil.fromBoostPref().getPrimaryContactNumber());
        details.setEmail(SharedPrefUtil.fromBoostPref().getEmail());
        details.setWebsite(SharedPrefUtil.fromBoostPref().getWebsite());
        details.setAddress(SharedPrefUtil.fromBoostPref().getAddress());
        String location = "http://maps.google.com/maps?q=loc:" + SharedPrefUtil.fromBoostPref().getLat() + "," + SharedPrefUtil.fromBoostPref().getLong();
        details.setLocation(location);
        modelList.add(details.toAllSuggestion());
        return modelList;
    }

    public void getAllDetailsFromApi() {
        adapter.getAllDetails(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).getFpTag(), mContext.getString(R.string.client_id) , customerDetailsCallBack);
    }
}
