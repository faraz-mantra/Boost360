package nfkeyboard.network;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nfkeyboard.interface_contracts.GetGalleryImagesAsyncTask_Interface;
import nfkeyboard.models.AllSuggestionModel;
import nfkeyboard.models.networkmodels.Product;
import nfkeyboard.util.Constants;
import nfkeyboard.util.SharedPrefUtil;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by NowFloats on 27-02-2018.
 */

public class NetworkAdapter {
    private INowFloatsApi mNfApi;
    private INowFloatsApi mCreateOfferApi;

    public NetworkAdapter() {

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NOW_FLOATS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                .build();

        mNfApi = retrofit.create(INowFloatsApi.class);

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("https://assuredpurchase.withfloats.com")
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mCreateOfferApi = retrofit1.create(INowFloatsApi.class);
    }

    public void getAllDetails(String fpTag , String clientId , final CallBack<CustomerDetails> callBack) {
        Map<String,String> queries = new HashMap<>();
        queries.put("clientId" , clientId);
        mNfApi.getAllDetails(fpTag ,queries).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<CustomerDetails>() {
                            @Override
                            public void accept(CustomerDetails customerDetails) throws Exception {
                            callBack.onSuccess(customerDetails);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                callBack.onError(throwable);
                                Log.d(NetworkAdapter.class.getSimpleName() , throwable.getMessage());
                            }
                        });
    }

    public void getAllProducts(String fpTag, String clientId, int skipBy, String identifierType,
                               final CallBack<List<Product>> callBack) {
        Map<String, String> queries = new HashMap<>();
        queries.put("fpTag", fpTag);
        queries.put("clientId", clientId);
        queries.put("skipBy", skipBy + "");
        queries.put("identifierType", identifierType);
        mNfApi.getAllProducts(queries).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> products) throws Exception {
                        callBack.onSuccess(products);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callBack.onError(throwable);
                    }
                });
    }

    public void getAllUpdates(String fpId, String clientId, int skipBy, int limit,
                              final CallBack<Updates> callBack) {
        Map<String, String> queries = new HashMap<>();
        queries.put("fpId", fpId);
        queries.put("clientId", clientId);
        queries.put("skipBy", skipBy + "");
        queries.put("limit", limit + "");
        mNfApi.getAllUpdates(queries).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Updates>() {
                    @Override
                    public void accept(Updates updates) throws Exception {
                        callBack.onSuccess(updates);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callBack.onError(throwable);
                    }
                });
    }

    public void createProductOffer(AllSuggestionModel model, final CallBack<CreatedOffer> createOfferCallback) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProductId(model.getP_id());
        Log.d("here", "Product id " + model.getP_id());
        request.setSalePrice(model.getAmount());
        Log.d("here", "price " + model.getAmount());
        request.setQuantity(model.getQuantity());
        Log.d("here", "quantity " + model.getQuantity());
        request.setExpiresOn(model.getLinkExpiryDateTime());
        Log.d("here", "expiry " + model.getLinkExpiryDateTime());
        request.setMaxQuantityPerOrder(model.getMaxUsage());
        Log.d("here", "maxu " + model.getMaxUsage());
        CreateOrderRequest.Seller seller = new CreateOrderRequest.Seller();
        seller.setIdentifier(model.getFpTag());
        Log.d("here", "id " + model.getFpTag());
        CreateOrderRequest.Seller.ContactDetails contactDetails = new CreateOrderRequest.Seller.ContactDetails();
        contactDetails.setFullName(SharedPrefUtil.fromBoostPref().getName());
        Log.d("here", "name " + contactDetails.getFullName());
        contactDetails.setEmailId(SharedPrefUtil.fromBoostPref().getEmail());
        Log.d("here", "email " + contactDetails.getEmailId());
        contactDetails.setSecondaryContactNumber(null);
        Log.d("here", "numbersec " + contactDetails.getSecondaryContactNumber());
        contactDetails.setPrimaryContactNumber(SharedPrefUtil.fromBoostPref().getPrimaryContactNumber());
        Log.d("here", "number " + contactDetails.getPrimaryContactNumber());
        CreateOrderRequest.Seller.Address address = new CreateOrderRequest.Seller.Address();
        address.setCity(SharedPrefUtil.fromBoostPref().getCity());
        Log.d("here", "city " + address.getCity());
        address.setCountry(SharedPrefUtil.fromBoostPref().getCountry());
        Log.d("here", "country " + address.getCountry());
        address.setAddressLine1(SharedPrefUtil.fromBoostPref().getAddress());
        Log.d("here", "line1 " + address.getAddressLine1());
        address.setZipcode(SharedPrefUtil.fromBoostPref().getZipcode());
        Log.d("here", "zip " + address.getZipcode());
        address.setAddressLine2("");
        address.setRegion("");
        seller.setAddresses(address);
        seller.setContactDetails(contactDetails);
        request.setSellers(seller);
        mCreateOfferApi.createProductOffers(request).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CreatedOffer>() {
                    @Override
                    public void accept(CreatedOffer response) throws Exception {
                        createOfferCallback.onSuccess(response);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("here", "errored");
                        Log.d("here", throwable.getStackTrace() + " " + throwable.getMessage() + " " + throwable.getCause());
                        createOfferCallback.onError(throwable);
                    }
                });

    }

    public void getAllImageList(GetGalleryImagesAsyncTask_Interface.getGalleryImagesInterface galleryImagesListener, String fpId) {
        GetGalleryImagesAsyncTask_Interface gallery = new GetGalleryImagesAsyncTask_Interface();
        gallery.setGalleryInterfaceListener(galleryImagesListener, fpId);
        gallery.execute();
    }
}
