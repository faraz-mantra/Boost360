package nfkeyboard.network;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import nfkeyboard.models.networkmodels.Product;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by NowFloats on 27-02-2018.
 */

public interface INowFloatsApi {
    @GET("/Product/v1/GetListings")
    Observable<List<Product>> getAllProducts(@QueryMap Map<String, String> queries);

    @GET("/Discover/v3/floatingPoint/bizFloats")
    Observable<Updates> getAllUpdates(@QueryMap Map<String, String> queries);

    @POST("/api/Offers/CreateOffer")
    Observable<CreatedOffer> createProductOffers(@Body CreateOrderRequest request);

    @GET("/discover/v2/floatingPoint/nf-web/{fpTag}")
    Observable<CustomerDetails> getAllDetails(@Path("fpTag") String fpTag , @QueryMap Map<String,String> queries);

}
