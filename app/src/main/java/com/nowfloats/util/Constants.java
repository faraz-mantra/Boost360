package com.nowfloats.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Business_Enquiries.Model.Entity_model;
import com.nowfloats.NotificationCenter.NotificationInterface;
import com.nowfloats.Store.iapUtils.Purchase;
import com.nowfloats.test.com.nowfloatsui.buisness.util.DataMap;
import com.thinksity.Specific;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

//import com.squareup.okhttp.OkHttpClient;


public class Constants {
    public static final String boostx_script = "<script type=\"text/javascript\" " +
            " data-nf-clientId=\"8DB87D953727422DA36B4977BD12E37A92EEB23119DC4152AAEB6B22BDB578EF\" " +
            " data-nf-fpTag=\"[[FPTAG]]\" " +
            " src=\"//ext.boost360.app/nf-widget.js\"" +
            " backgroundColor=\"#eaeaea\" " +
            " secondaryColor=\"#2f2f2f\" " +
            " activeColor=\"#FF5722\" " +
            " buttonBackgroundColor=\"#ffd55a\" " +
            " buttonTextColor=\"#790000\">" +
            " </script> ";

    public static final boolean APK_MODE_RELEASE = true;
    public static final String APP_TAG = "Boost App";
    public static final String RIA_NODE_DATA = "riaNodeDatas";
    public static final int VISITS_TABLE = 0;
    public static final int VISITORS_TABLE = 1;
    public static final String PDF_LOADER_URL = "https://drive.google.com/viewerng/viewer?embedded=true&url=";
    public static final String WA_KEY = "58ede4d4ee786c1604f6c535";
    public static final String ORDER_TYPE = "order_type";
    public static final int GALLERY_PHOTO = 100;
    public static final int CHOSEN_PHOTO = 1908;
    public static final int CAMERA_PHOTO = 1888;
    public static final String SMS_OTP_RECEIVER = "sms_otp_receiver";
    public static final String[] FACEBOOK_READ_PERMISSIONS = Specific.FACEBOOK_READ_PERMISSIONS;
    public static final String[] FACEBOOK_PUBLISH_PERMISSIONS = Specific.FACEBOOK_PUBLISH_PERMISSIONS;
    //public final static String NOW_FLOATS_API_URL		=	"http://api.nowfloatsdev.com";
    //public final static String NOW_FLOATS_API_URL		=	"http://api.nowfloatsdev.com";
    public final static String WEB_ACTION_BOOST_KIT_API_URL = "https://webaction.api.boostkit.dev";
    public final static String NOW_FLOATS_API_URL = "https://api2.withfloats.com";
    public final static String NOW_FLOATS_API_URL_SALESMAN = "http://52.66.59.196";
    public final static String WITH_FLOATS_API_URL = "https://api.withfloats.com/";
    public final static String NOW_FLOATS_API_URL_V2 = "https://api2.withfloats.com";
    public final static String TEST_API_URL = "http://api.nowfloatsdev.com";
    public final static String CREATE_MESSAGE_URL = "http://api.nowfloatsdev.com/akash";
    public final static String BASE_IMAGE_URL = "https://content.withfloats.com";
    public final static String RIA_MEMORY_API_URL = "http://riamemory.withfloats.com";
    public final static String PLUGIN_API_URL = "https://plugin.withfloats.com";
    public final static String RIA_MEMORY_TEST_API_URL = "http://ria.nowfloatsdev.com";
    public final static String RIA_API_URL = "https://ria.withfloats.com";
    public final static String SUGGESTIONS_API_URL = "http://boostapi.withfloats.com";
    public final static String SUGGESTIONS_TEXT_API_URL = "http://boosttest.nowfloatsdev.com";
    //public final static String WA_BASE_URL = "https://webactions.kitsune.tools/api/v1/";
    public final static String WA_BASE_URL = "https://kit-webaction-api.withfloats.com/api/v1/";
    public final static String AP_BASE_URL = "https://assuredpurchase.withfloats.com/api/AssuredPurchase/";
    public final static String AP_BOOST_BASE_URL = "https://boost.nowfloats.com/AssuredPurchase";
    public final static String BOOST_NOWFLOATS_BASE_URL = "https://boost.nowfloats.com/Home";
    public final static String ANA_CHAT_API_URL = /*"http://chat-dev.nowfloatsdev.com";//*/"https://gateway.api.ana.chat";
    public final static String ANA_BUSINESS_ID = /*"Boost-Web";//*/"boost-agent-chat";
    public final static String ANALYTICS_BASE_URL = "https://api2.kitsune.tools/api/WebAnalytics/v1";
    // public final static String NOW_FLOATS_API_URL		=	"https://nftestbed.azurewebsites.net";
    public final static String HTTP_PUT = "PUT";
    public final static String HTTP_POST = "POST";
    public final static String HTTP_DEL = "DELETE";
    public final static int REFERRAL_CAMPAIGN_CODE = 26277;
    //Retrofit Single Instance
    public static final RestAdapter chatRestAdapter = new RestAdapter.Builder().setEndpoint("http://dbapi.fostergem.com").build();
    public static final RestAdapter chatsendRestAdapter = new RestAdapter.Builder().setEndpoint("http://api.fostergem.com").build();
    //public static final RestAdapter smsVerifyAdapter = new RestAdapter.Builder().setEndpoint("https://api.authy.com")./*setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).*/build();
    public static final RestAdapter smsVerifyAdapter = new RestAdapter.Builder().setEndpoint("https://onboarding-boost.withfloats.com").setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    //    public static RestAdapter validEmailAdapter = null;
    public static final RestAdapter validEmailAdapter = new RestAdapter.Builder().setEndpoint("https://bpi.briteverify.com").build();
    //    public static RestAdapter restAdapter = null;
    public static final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.NOW_FLOATS_API_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter restAdapterV2 = new RestAdapter.Builder().setEndpoint(Constants.NOW_FLOATS_API_URL_V2)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("gggv2")).build();
    public static final RestAdapter restAdapterWithFloat = new RestAdapter.Builder().setEndpoint(Constants.WITH_FLOATS_API_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("with float")).build();
    public static final RestAdapter restAdapterBoostKit = new RestAdapter.Builder().setEndpoint(Constants.WEB_ACTION_BOOST_KIT_API_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Boost Kit")).build();
    public static final RestAdapter restAdapterSalesman = new RestAdapter.Builder().setEndpoint(Constants.NOW_FLOATS_API_URL_SALESMAN)/*.setClient(getClient())*//*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg"))*/.build();
    public static final RestAdapter movingFloatsDevAdapter = new RestAdapter.Builder().setEndpoint("http://movingfloats_nds.nowfloatsdev.com").setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter movingFloatsDev2Adapter = new RestAdapter.Builder().setEndpoint("http://withfloatsapi2-dev.ap-south-1.elasticbeanstalk.com").setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter pluginSuggestionsAdapter = new RestAdapter.Builder().setEndpoint(Constants.SUGGESTIONS_API_URL)./*setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit Response")).*/build();
    public static final RestAdapter testRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.TEST_API_URL).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit Response")).build();
    public static final RestAdapter createMessageAdapter = new RestAdapter.Builder().setEndpoint(Constants.CREATE_MESSAGE_URL)/*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit Response"))*/.build();
    public static final RestAdapter pluginRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.PLUGIN_API_URL)/*.setClient(getClient())*//*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg"))*/.build();
    public static final RestAdapter riaRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.RIA_API_URL)/*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit Response"))*/.build();
    public static final RestAdapter riaMemoryRestAdapter = new RestAdapter.Builder().setEndpoint(Constants.RIA_MEMORY_API_URL)/*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit Response"))*/.build();
    public static final RestAdapter webActionAdapter = new RestAdapter.Builder().setEndpoint(WA_BASE_URL).setConverter(new GsonConverter(new GsonBuilder().setLenient().create())).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("WebAction Response")).build();
    public static final RestAdapter apAdapter = new RestAdapter.Builder().setEndpoint(AP_BASE_URL).setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))/*.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Order Response"))*/.build();
    //public static final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.NOW_FLOATS_API_URL).setClient(getClient()).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter restAdapterAnalytics = new RestAdapter.Builder().setEndpoint(Constants.ANALYTICS_BASE_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter restAdapterAnalyticsWebApi = new RestAdapter.Builder().setEndpoint(Constants.BOOST_NOWFLOATS_BASE_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    /**
     * Added newly for boost assured purchase API adapter
     */
    public static final RestAdapter apApiAdapter = new RestAdapter.Builder().setEndpoint(AP_BOOST_BASE_URL).setConverter(new GsonConverter(new GsonBuilder().setLenient().create())).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Assured Purchase Response")).build();
    public static final String TWITTER_URL = Specific.TWITTER_URL;
    public static final String TWITTER_ID_URL = Specific.TWITTER_ID_URL;
    public static final String BG_SERVICE_CONTENT_TYPE_OCTET_STREAM = "binary/octet-stream";
    public static final String BG_SERVICE_CONTENT_TYPE_JSON = "application/json";
    public final static String BG_SERVICE_DATA_ROW_ID = "rowId_bizApp";
    public final static String NEW_LINE = "\n";
    public static final String CLIENT_ID_KEY = "clientId_bizAPP";
    public static final String CONSUMER_KEY = Specific.TWITTER_TOK;
    public static final String CONSUMER_SECRET = Specific.TWITTER_SEC;
    public static final String FACEBOOK_API_KEY = Specific.FACEBOOK_API_KEY;
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    public static final String PREF_USER_NAME = "twitter_user_name";
    public static final String PREF_NOTI_CALL_LOGS = "noti_call_logs";
    public static final String PREF_NOTI_ENQUIRIES = "noti_enquiries";
    public static final String PREF_NOTI_ORDERS = "noti_orders";
    public static final String SYNCED = "synced";
    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
    public static final String OAUTH_CALLBACK_HOST = "callback";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "NFBoostDB";
    // Table Names
    public static final String TABLE_DRAFTS_UPDATES = "drafts_updates";
    public static final String TABLE_CONTACTS = "contacts";
    //widgets
    public static final int WidgetDetailDialogRequestCode = 2222;
    public static final int WidgetDetailViewRequestCode = 2223;
    public static final int DomainPurchaseRequestCode = 2121;
    public static final String TWILIO_AUTHY_API_KEY = Specific.TWILIO_AUTHY_API_KEY;
    public static final String FACEBOOK_PAGE_WITH_ID = Specific.FACEBOOK_PAGE_ID;
    /**
     * Dev Adapter
     */
    public static final RestAdapter restAdapterDev = new RestAdapter.Builder().setEndpoint(/*"http://ec2-13-233-87-208.ap-south-1.compute.amazonaws.com/"*/ "https://api2.withfloats.com/")/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static final RestAdapter restAdapterDev1 = new RestAdapter.Builder().setEndpoint(/*"http://ec2-13-233-87-208.ap-south-1.compute.amazonaws.com"*/ "https://api2.withfloats.com")/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();
    public static boolean LOGOUPLOADED = false;
    public static boolean IS_SUNDAY_CHECKED = false;
    public static boolean IS_MONDAY_CHECKED = false;
    public static boolean IS_TUESDAY_CHECKED = false;
    public static boolean IS_WEDNESDAY_CHECKED = false;
    public static boolean IS_THURSDAY_CHECKED = false;
    public static boolean IS_FRIDAY_CHECKED = false;
    public static boolean IS_SATURDAY_CHECKED = false;
    public static String UserId = "";
    public static Typeface hel_bold, hel_roman;
    public static String PREF_NAME = "nowfloatsPrefs";
    public static String clientIdThinksity = "217FF5B9CE214CDDAC4985C853AE7F75AAFA11AF2C4B47CB877BCA26EC217E6D";
    public static String clientId = Specific.clientId2;
    public static String clientId_ORDER = "AC16E0892F2F45388F439FDE9F6F3FB5C31F0FAA628D40CD9814A79D8841397D";
    public static String GMBClientId = "534180772998-29dvjja8u2lnaklmrdnne776i2gosi4c.apps.googleusercontent.com";
    public static String GMBCallbackUrl = "https://mybusiness.googleapis.com/v4/accounts";
    public static String NFXUpdateAcessToken = "https://nfx.withfloats.com/dataexchange/v1/updateAccessTokens";
    public static String NFXProcessUrl = "https://nfx.withfloats.com/dataexchange/v1/process";
    public static String GMBScope = "https://www.googleapis.com/auth/plus.business.manage";
    public static String NFXgetAcessToken = "https://nfx.withfloats.com/dataexchange/v1/getAccessTokens";
    public static String GMBgetLocationUrl = "https://mybusiness.googleapis.com/v4/accounts/";
    public static String LogTag = "android23235616";
    public static String UPLOAD_TO_S3_ENDPOINT = "https://6psfkele8l.execute-api.ap-south-1.amazonaws.com";
    public static String deviceId = "123456789";
    public static String clientId4 = "731EBAB8596648E79882096E4733E7173B314BECC649423DB67897BF8CC596EC";
    public static String clientId3 = "A816E08AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F51770E46AD86";
    public static String clientId2 = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21";
    public static String clientId1 = "39EB5FD120DC4394A10301B108030CB70FA553E91F984C829AB6ADE23B6767B7";
    public static String license_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmtfDikZcbWSvq3f+pEsAL5KQCNy3X96j++Z1PnjvidpJCEe0/S1xdPs4CfT3JkeQl7SNps/9cPu3EuOOFdx76QPpAqlqXHMKMwM9H+ikx5iUWPSilRjwLfJNjbJZT2xpuI6k32VyHhqLyU4rR95nrAPTGrocyyV1jtTFSYt77YZew8YfeePlcB2STLP3Ookho37Ah1QovelfdaG5ZNWz5OPYKnificSPyYjioYkfUmpnUJvN0INYMGFKefhfPtWPx5UCiQp15A6ir4wH0wVL3/QksonIb0JMiXpuXvWuggNb1AqEtdiPuBTleU5GovKL+HToKjwQu8NSuJsb3EacIwIDAQAB";
    public static Purchase lastPurchase = null;
    public static String GOOGLE_API_KEY = "AIzaSyBl66AnJ4_icH3gxI_ATc8031pveSTGWcg";
    public static String teleCountry = "in";
    public static DisplayMetrics DISPLAY_METRICS = null;
    public static Boolean hasStoreData = false;
    public static Boolean hasSearchData = false;
    public static int FontSizeT = 13;
    public static int FontSizeE = 16;
    public static String prevDataDirectory = "NowFloatsBiz";
    public static String dataDirectory = "NowFloatsBoost";
    public static String[] days = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    public static Map<String, String> Currency_Country_Map = new HashMap<String, String>();
    public static ArrayList<String> currencyArray = new ArrayList<>();
    public static Activity app = null;
    public static Activity home = null;
    public static NotificationInterface alertInterface = Constants.restAdapter.create(NotificationInterface.class);
    //http://api.withfloats.com/Discover/v3/FloatingPoint/create
    public static String NFX_DEV_NOWFLOATS = "http://nfx.nowfloatsdev.com";
    public static String NFX_WITH_NOWFLOATS = "https://nfx.withfloats.com";
    //public static String NFX_WITH_NOWFLOATS="http://nfx.nowfloatsdev.com";
    public static String createStoreV3 = NOW_FLOATS_API_URL + "/Discover/v3/FloatingPoint/create";
    public static String createStoreV2 = NOW_FLOATS_API_URL + "/Discover/v2/FloatingPoint/create";
    public static String loginUrl = NOW_FLOATS_API_URL + "/discover/v1/login/";
    public static String verifyLoginUrl = NOW_FLOATS_API_URL + "/discover/v1/floatingPoint/verifyLogin";
    public static String FloatCreationURI = NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/create";
    public static String PictureFloatCreationURI = NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/createBizMessage";
    public static String PictureFloatImgCreationURI = NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/createBizImage";
    public static String EventCreationURI = NOW_FLOATS_API_URL + "/Discover/v1/float/createEvent";
    public static String OfferCreationURI = NOW_FLOATS_API_URL + "/Discover/v1/float/createDeal";
    public static String LoadStoreURI = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/";
    public static String LoadUserMessages = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/usermessages/";
    public static String LoadBizFloats = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/bizFloats?clientId=";
    public static String LoadBizFloatsSearch = NOW_FLOATS_API_URL + "/Search/v1/queries/report?offset=0";
    public static String FpsUpdate = NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/update/";
    public static String FpImageUri = NOW_FLOATS_API_URL + "/Discover/v1/FloatingPoint/createImage?";
    public static String UTagUri = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/verifyUniqueTag";
    public static String deleteGalleryImgs = NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/removeSecondaryImage";
    public static String addwidget = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/addwidget";
    public static String addFbLikeBoxwidget = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/addFpWidget";
    public static String GMBSharedAuthToken = ",jUSSHADHlasjdakGDJadhds";
    public static String GMBSharedReferenceToken = ".KAHGSDKAjdghKAJHgads";
    public static String GMBAccountNumber = "ajhdgakdsghaksdhgaksdhgaksjdh";

    //public static final String FACEBOOK_API_KEY 	= "193559690753525";
    //public static final String FACEBOOK_API_KEY 	= "851630411569235"; // Thinksity
    // public static final String FACEBOOK_API_KEY 	= "539836972828386";  // Boost
    public static String GMBAuthCode = "jalskdh9879a8sd7";
    public static String GMBAccountName = "jalskdh987.aksdjklakdsjasd9a8sd7";
    public static String GMBAccountId = "lkjasjdsaldalskjdslaksjd";
    public static String ChangePassword = NOW_FLOATS_API_URL + "/discover/v1/floatingpoint/changePassword";
    //public static String TWITTER_TOK 				= "";
    //public static String TWITTER_SEC 				= "";
    public static String domainSearch = NOW_FLOATS_API_URL + "/DomainService/v1/checkAvailability/";
    public static String domainPurchase = NOW_FLOATS_API_URL + "/domainservice/v1/requestdomainpurchase";
    public static String googlePlaces = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/CreateGooglePlaces/";
    public static String uniqueNumber = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/verifyPrimaryNumber";
    public static String SearchQueryCount = NOW_FLOATS_API_URL + "/Discover/v1/searchqueries/count?clientId=";
    public static String BackgroundImageUpload = NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/createBackgroundImage/";
    public static String GetBackgroundImage = NOW_FLOATS_API_URL + "/discover/v1/floatingpoint/getBackgroundImages/";
    public static String DelBackgroundImage = NOW_FLOATS_API_URL + "/discover/v1/floatingpoint/backgroundImage/delete";
    public static String DelBackImg = NOW_FLOATS_API_URL + "/discover/v1/floatingpoint/backgroundImage/delete";
    public static String ReplaceBackImg = NOW_FLOATS_API_URL + "/discover/v1/floatingpoint/replaceBackgroundImage/";
    public static String DeleteCard = NOW_FLOATS_API_URL + "/Discover/v1/floatingPoint/archiveMessage";
    public static String nfxApi = NOW_FLOATS_API_URL + "/Discover/v2/floatingPoint/updateSocialAccessToken";
    public static String beCountUrl = NOW_FLOATS_API_URL + "/Discover/v1/businessenquiries/count";
    public static String nfxUpdateTokens = NFX_WITH_NOWFLOATS + "/dataexchange/v1/updateAccessTokens";
    public static String nfxFBPageCreation = NFX_WITH_NOWFLOATS + "/dataexchange/v1/process";
    public static int enqCount = 0;
    public static String picType = "picType";
    public static String picTypeCamera = "picType_Camera";
    public static String picTypeGallery = "picType_Gallery";
    public static String callInitiatedFrom = null;
    public static String serviceResponse = "";
    public static int UPLOAD_TYPE_FILE = 123;
    public static int UPLOAD_TYPE_STRING = 120;
    // test facebook app id 544632525682164
    //public static final String FACEBOOK_API_KEY 	= "544632525682164";
    public static String FACEBOOK_USER_ID = "";
    public static String FACEBOOK_USER_ACCESS_ID = "";
    public static String FACEBOOK_PAGE_ACCESS_ID = "";
    public static String TWITTER_TOK = Specific.TWITTER_TOK;
    public static String TWITTER_SEC = Specific.TWITTER_SEC;
    // for referral
    public static String PREF_NAME_REFERRAL = "nowfloatsPrefsReferral";
    public static String IS_INSTALL_APP = "isInstallApp";
    public static String REFER_CODE_APP = "referCodeApp";
    public static String shortUrl = "";
    public static Boolean fbShareEnabled = false;
    public static boolean fbPageShareEnabled = false;
    public static Boolean twitterShareEnabled = false;
    public static Boolean fromLogin = false;
    public static String StoreResponse = null;
    public static String StoreAddress = null;
    public static String ContactName = null;
    public static String SignUpContactName = null;
    public static String StoreCategory = null;
    public static String StoreCity = null;
    public static String StoreFPCountry = null;
    public static String StorePinCode = null;
    public static String StorePrimaryNumber = null;
    public static String StoreName = null;
    public static String StoreLogoUri = null;
    public static int StoreCountryCode = 0;
    public static String StoreImageUri = null;
    public static String StoreDescription = null;
    public static String mondayStartTime = null;
    public static String mondayEndTime = null;
    public static String tuesdayStartTime = null;
    public static String tuesdayEndTime = null;
    public static String wednesdayStartTime = null;
    public static String wednesdayEndTime = null;
    public static String thursdayStartTime = null;
    public static String thursdayEndTime = null;
    public static String fridayStartTime = null;
    public static String fridayEndTime = null;
    public static String satdayStartTime = null;
    public static String satdayEndTime = null;
    public static String sundayStartTime = null;
    public static String sundayEndTime = null;
    public static String Store_search = null;
    public static String StoreErrorRadius = null;
    public static String StoreHeight = null;
    public static String StoreLat = null;
    public static String StoreLng = null;
    public static double latitude;
    public static double longitude;
    //	public static String [] StoreContact	= null;
    public static String StoreContactName = null;
    public static String StoreEmail = null;
    public static String StoreWebSite = null;
    public static String StoreFbPage = null;
    public static String StoreTimingOn = null;
    public static String StoreTimingOff = null;
    public static String StoreFromDay = "MONDAY";
    public static String StoreToDay = "SATURDAY";
    public static Map[] sfcontactsMap = null;
    public static Map[] sfRatings = null;
    public static String storePrimaryImage = null;
    public static String storePrimaryImageTemp = null;
    public static JSONArray storeTimming = null;
    public static ArrayList<String> storeSecondaryImages = null;
    public static String[] StoreAddresArray = {"Address Line1", "Address Line2", "Landmark", "City", "Pincode", "Andhra Pradesh"};
    public static ArrayList<Business_Enquiry_Model> StorebizQueries = new ArrayList<Business_Enquiry_Model>();
    public static ArrayList<Entity_model> StorebizEnterpriseQueries = new ArrayList<Entity_model>();
    public static ArrayList<JSONObject> StoreUserMessages = new ArrayList<JSONObject>();
    public static com.nowfloats.Analytics_Screen.DataMap StoreUserSearch = new DataMap();
    public static ArrayList<JSONObject> StoresInfo = new ArrayList<JSONObject>();
    public static ArrayList<Integer> visitorCountList = new ArrayList<Integer>();
    public static Double CurrentFloatingPointLatitude = 0.0;
    public static Double CurrentFloatingPointLongitude = 0.0;
    public static float CurrentFloatingPointAccuracy = 0f;
    public static Boolean moreStorebizFloatsAvailable = true;
    public static Boolean moreStoreSearchAvailable = true;
    public static JSONArray FbPageList;
    public static Boolean isInboxLoaded = true;
    public static Boolean isDomainSearchSuccess = false;
    public static Address lastKnownAddress;
    public static LatLng latlng;
    public static String geoAddress = null;
    public static Boolean isAddressLoaded = false;
    public static int getCurrentPosition;
    public static String requestedDomainName;
    public static String requestedDomainType;
    public static Boolean isDirectlyCameToMainScreen = false;
    //	public static Boolean websiteShared = false;
    public static String currentActivePackageId = "currentActivePackageId";
    // Update Matchers
    public static int update_drafts = 100000001;
    public static int update_contacts = 100000002;
    public static String[] storeBusinessCategories = {"GENERAL SERVICES",
            "ARCHITECTURE",
            "AUTOMOTIVE",
            "GROCERY",
            "BLOGS",
            "EDUCATION",
            "ELECTRONICS",
            "ENTERTAINMENT",
            "EVENTS",
            "F&B - BAKERY",
            "F&B - BARS",
            "F&B - CAFE",
            "F&B - RESTAURANTS",
            "FASHION - APPAREL",
            "FASHION - FOOTWEAR",
            "FLOWER SHOP",
            "HOME FURNISHINGS",
            "GIFTS & NOVELTIES",
            "HEALTH & FITNESS",
            "HOME APPLIANCES",
            "HOME CARE",
            "HOME MAINTENANCE",
            "HOTEL & MOTELS",
            "INTERIOR DESIGN",
            "MEDICAL - DENTAL",
            "MEDICAL - GENERAL",
            "NATURAL & AYURVEDA",
            "KINDER GARTEN",
            "PETS",
            "PHOTOGRAPHY",
            "REAL ESTATE & CONSTRUCTION",
            "SPA",
            "SPORTS",
            "TOURISM",
            "WATCHES & JEWELRY",
            "OTHER RETAIL",
            "TUITIONS & COACHING",
            "MANUFACTURERS",
            "CONSTRUCTION MATERIAL",
            "FREELANCER",
            "CONSULTANTS"};
    public static String PrimaryNumberClientId = "726F12B41F6242CC9A2B23BF101199B54449370AD7F44B069C9B5E7CC4A7A20D";
    public static ArrayList<String> signUpCountryList = new ArrayList<>();
    public static int DefaultBackgroundImage;
    public static boolean imageNotSet = false;
    public static boolean isWelcomScreenToBeShown = false;
    public static boolean fromSiteMeter_Share = false;
    public static boolean isFirstTimeSendToSubscriber = false;
    public static boolean isFBLikeBOXPresent = false;
    public static boolean ImageGalleryWidget = false;
    public static boolean BusinessTimingsWidget = false;
    public static boolean BusinessEnquiryWidget = false;
    // public static String parentID;
    public static String createdON;
    public static ArrayList<JSONObject> StorebizQueries_enterprise = new ArrayList<JSONObject>();
    public static int paymentLevel;
    public static String gcmRegistrationID;
    public static boolean deepLinkStore, deepLinkAnalytics;
    //    public static boolean showStoreScreen;
    public static int NumberOfUpdates;
    public static boolean IMAGEURIUPLOADED = false;
    public static String facebookPageURL;
    public static String facebookPageDescription;
    public static boolean createMsg = false;
    public static boolean gotoStore = false;
    public static boolean GCM_Msg = false;
    public static ArrayList<String> storeActualSecondaryImages = new ArrayList<>();
    public static ArrayList<String> storeActualGif = new ArrayList<>();
    public static boolean isImgUploaded = false;
    public static String uploadedImg = "";

    public static int featureImgReqCode = 6767;
    public static int secondaryImgReqCode = 50505;

    public static int logoImgReqCode = 4545;
    public static int BackgroundImgReqCode = 9898;
    public static String BackGoundImgReqCode = "Background";

    public static int imgUploadReqCode = 789;

    public static boolean isGalleryUpdated = false;
    public static Set<String> widgets = new HashSet<String>();
    public static ArrayList<String> StoreWidgets = new ArrayList<>();
    public static ArrayList<String> StorePackageIds = new ArrayList<>();
    public static String fbPageFullUrl;
    public static String fbFromWhichPage;
    public static boolean FbFeedPullAutoPublish;
    public static String FACEBOOK_URL = Specific.FACEBOOK_URL;
    public static String PACKAGE_NAME = Specific.PACKAGE_NAME;
    public static String DEFAULT_PACKAGE_NAME_WEB_ERROR = "com.biz.nowfloats";
    public static String SUPPORT_EMAIL_ID = Specific.CONTACT_EMAIL_ID;
    /**
     * Dev URL
     */
    //public static String DEV_ASSURED_PURCHASE_URL = "http://ec2-13-232-212-139.ap-south-1.compute.amazonaws.com";
    public static String DEV_ASSURED_PURCHASE_URL = "https://assuredpurchase.withfloats.com";
    public static final RestAdapter assuredPurchaseRestAdapterDev = new RestAdapter.Builder().setEndpoint(DEV_ASSURED_PURCHASE_URL)/*.setClient(getClient())*/.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("ggg")).build();

    public Constants(Activity app) {
        Constants.app = app;
    }

    public Constants(Context app) {
    }

    public static OkHttpClient getClient() {
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(1, TimeUnit.MINUTES);
        client.readTimeout(1, TimeUnit.MINUTES);
        return client.build();
    }

    public static void clearStore() {
        StoreAddress = null;
        StoreName = null;
        StoreImageUri = null;
        storePrimaryImage = null;
        StoreDescription = null;
        Store_search = null;
        StoreErrorRadius = null;
        StoreHeight = null;
        StoreLat = null;
        StoreLng = null;
//		StoreContact		= null;
        StoreContactName = null;
        StoreEmail = null;
        StoreWebSite = null;
        StoreFbPage = null;
        StoreTimingOn = null;
        StoreTimingOff = null;
        StoreFromDay = null;
        StoreToDay = null;
        storeSecondaryImages = null;
        StoreUserMessages = new ArrayList<JSONObject>();
        StoreUserSearch = null;
        StoresInfo = new ArrayList<JSONObject>();
        visitorCountList = new ArrayList<Integer>();
        Constants.isDirectlyCameToMainScreen = false;
    }


    public enum SubscriberStatus {
        UNSUBSCRIBED(30), SUBSCRIBED(20), REQUESTED(10);
        public int value;

        SubscriberStatus(int i) {
            value = i;
        }
    }

    public enum PaymentAndDeliveryMode {
        ASSURED_PURCHASE("AssuredPurchase"), MY_PAYMENT_GATEWAY("MyPaymentGateWay"), UNIQUE_PAYMENT_URL("UniquePaymentUrl"), DONT_WANT_TO_SELL("None");

        private String value;

        PaymentAndDeliveryMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum DeliveryMethod {

        ASSURED_PURCHASE("ASSUREDPURCHASE"), SELF("SELF"), NOT_AVAILABLE("NA");

        public String value;

        DeliveryMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public interface BROADCAST_RECEIVERS_INFO {
        String BACKGROUND_SERVICE_BROADCAST_RECEIVER_KEY = "BIZ_APP_REC_KEY";
        String FLOAT_CREATION_BACKGROUND_SERVICE_BROADCAST_RECEIVER_ACTION = "com.biz2.nowfloats.FLOAT_CREATION_ACTION";
        String BACKGROUND_SERVICE_RESPONSE = "response";
    }

}