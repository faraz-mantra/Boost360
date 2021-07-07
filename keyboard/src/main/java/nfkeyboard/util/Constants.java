package nfkeyboard.util;

import java.util.ArrayList;

/**
 * Created by Shimona on 01-06-2018.
 */

public class Constants {

    public final static String BASE_IMAGE_URL = "https://content.withfloats.com";
    public final static String clientId = "2D5C6BB4F46457422DA36B4977BD12E37A92EEB13BB4423A548387BA54DCEBD5";
    public final static String HTTP_POST = "POST";
    public final static String NOW_FLOATS_API_URL = "https://api2.withfloats.com";
    public static final String BG_SERVICE_CONTENT_TYPE_JSON = "application/json";
    public static ArrayList<String> storeSecondaryImages = null;
    public static String LoadStoreURI = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/";

    public static Boolean hasStoreData = false;

    public static String storePrimaryImage = null;

    public static ArrayList<String> storeActualSecondaryImages = new ArrayList<>();

}
