package com.nowfloats.riachatsdk.utils;

/**
 * Created by NowFloats on 27-03-2017.
 */

public class Constants {
    public static final String PARCEL_NAME = "config_parcel";
    public static final String CHAT_TYPE = "chat_type";
    public static final String SERVER_URL = "https://onboarding-boost.withfloats.com";
    public static final String FEEDBACK_SERVER_URL = "https://nfanabots.withfloats.com/nps";
    //    public static final String SERVER_URL = "http://ria.nowfloatsdev.com";
    public static final String FILE_PATH = "file_path";
    public static final String RECEIVER = "receiver";
    public static final String KEY_FILE_URL = "file_url";
    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCELLED = 1;

    public interface SectionType {
        String TYPE_TEXT = "Text";
        String TYPE_TYPING = "Typing";
        String TYPE_IMAGE = "Image";
        String TYPE_AUDIO = "Audio";
        String TYPE_VIDEO = "Video";
        String TYPE_HEADER = "Header";
        String TYPE_CARD = "Card";
        String TYPE_ADDRESS_CARD = "AddressCard";
        String TYPE_UNCONFIRMED_CARD = "UnConfirmedCard";
        String TYPE_CAROUSEL = "Carousel";
        String TYPE_UNCONFIRMED_ADDR_CARD = "UnConfirmedAddressCard";
        String TYPE_PRINT_OTP = "PrintOTP";
        String TYPE_SUBMIT_FORM = "SubmitForm";
    }

    public interface ApiType {
        String TYPE_GET = "GET";
        String TYPE_POST = "POST";
    }

    public interface NodeType {
        String TYPE_API_CALL = "ApiCall";
        String TYPE_CARD = "Card";
    }

    public interface ButtonType {
        String TYPE_GET_TEXT = "GetText";
        String TYPE_GET_NUMBER = "GetNumber";
        String TYPE_NEXT_NODE = "NextNode";
        String TYPE_GET_ADDR = "GetAddress";
        String TYPE_SHOW_CONFIRMATION = "ShowConfirmation";
        String TYPE_GET_IMAGE = "GetImage";
        String TYPE_DEEP_LINK = "DeepLink";
        String TYPE_GET_ITEM_FROM_SOURCE = "GetItemFromSource";
        String TYPE_GET_EMAIL = "GetEmail";
        String TYPE_GET_PHONE_NUMBER = "GetPhoneNumber";
        String TYPE_GET_AUDIO = "GetAudio";
        String TYPE_GET_VIDEO = "GetVideo";
        String TYPE_FETCH_CHAT = "FetchChatFlow";
    }

    public interface VariableName {
        String TYPE_TAG = "TAG";
        String TYPE_EXISTING_WEBSITE = "EXISTING_WEBSITE";
        String TYPE_NEW_PASSWORD = "NEW_PASSWORD";
        String TYPE_NEW_PASSWORD_REPEAT = "NEW_PASSWORD_REPEAT";
        String TYPE_ALTERNATE_PHONE = "ALTERNATE_PHONE";
        String TYPE_BUSINESS_DESCRIPTION = "BUSINESS_DESCRIPTION";
        String TYPE_MESSAGE_UPDATE = "MESSAGE_UPDATE";
        String TYPE_OTP = "OTP";
        String TYPE_ADD_DELAY = "ADD_DELAY";
    }

    public interface DeepLinkUrl {
        String ASK_LOC_PERM = "asklocationpermission";
        String FACEBOOK_AUTH = "facebookauth";
        String LOGIN = "login";
    }

    public interface LocationConstants {
        int SUCCESS_RESULT = 0;

        int FAILURE_RESULT = 1;

        String PACKAGE_NAME = "com.nowfloats.riachatsdk";

        String RECEIVER = PACKAGE_NAME + ".RECEIVER";

        String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

        String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

        String LOCATION_DATA_AREA = PACKAGE_NAME + ".LOCATION_DATA_AREA";
        String LOCATION_DATA_CITY = PACKAGE_NAME + ".LOCATION_DATA_CITY";
        String LOCATION_DATA_STREET = PACKAGE_NAME + ".LOCATION_DATA_STREET";
        java.lang.String LOCATION_DATA_COUNTRY = PACKAGE_NAME + ".LOCATION_DATA_COUNTRY";
        String LOCATION_DATA_PIN = PACKAGE_NAME + ".LOCATION_DATA_PIN";
        String LOCATION_DATA_LOCALITY = PACKAGE_NAME + ".LOCATION_DATA_LOCALITY";
        String LOCATION_DATA_HOUSE_NUMBER = PACKAGE_NAME + ".LOCATION_DATA_HOUSE_NUMBER";
        String LOCATION_DATA_LANDMARK = PACKAGE_NAME + ".LOCATION_DATA_LANDMARK";

    }

    public interface ConfirmationType {
        String BIZ_NAME = "biz_name";
        String OTP = "otp";
        String ADDRESS_ENTRY = "address_entry";
        String SUBMIT_FORM = "submit_form";
        String FB_PAGE = "fb_page";
        String FP_TAG_CONFIRM = "fp_tag_confirm";
        String FP_TAG_EDIT = "fp_tag_edit";
    }
}
