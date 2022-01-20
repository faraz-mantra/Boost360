package dev.patrickgold.florisboard.customization.util;

import com.framework.BuildConfig;

/**
 * Created by Shimona on 01-06-2018.
 */

public class Constants {

  public final static String BASE_IMAGE_URL = "https://content.withfloats.com";


  public final static String clientId = BuildConfig.CLIENT_ID;

  public final static String HTTP_POST = "POST";

  public final static String NOW_FLOATS_API_URL = "https://api2.withfloats.com";

  public static final String BG_SERVICE_CONTENT_TYPE_JSON = "application/json";

  public static String LoadStoreURI = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/";

  public static String storePrimaryImage = null;

  public static int UNAUTHORIZED_STATUS_CODE = 401;

  public static String TOKEN_EXPIRED_MESSAGE = "Token Expired. Please Login Again";
  public static String NO_INTERNET_CONNECTION = "Internet not available";
  public static String FAILED_TO_CONNECT = "Failed to connect";
  public static String UNABLE_TO_RESOLVED_HOST = "Unable to resolve host";
}
