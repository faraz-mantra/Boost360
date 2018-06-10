package nfkeyboard.network;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import nfkeyboard.models.networkmodels.Product;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by NowFloats on 27-02-2018.
 */

public interface INowFloatsApi {
    @GET("/Product/v1/GetListings")
    Observable<List<Product>> getAllProducts(@QueryMap Map<String, String> queries);

    @GET("/Discover/v3/floatingPoint/bizFloats")
    Observable<Updates> getAllUpdates(@QueryMap Map<String, String> queries);
}
