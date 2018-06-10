package nfkeyboard.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nfkeyboard.models.networkmodels.Product;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by NowFloats on 27-02-2018.
 */

public class NetworkAdapter {
    private INowFloatsApi mNfApi;

    public NetworkAdapter() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.withfloats.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mNfApi = retrofit.create(INowFloatsApi.class);
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

}
