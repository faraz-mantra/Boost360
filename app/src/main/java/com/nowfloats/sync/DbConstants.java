package com.nowfloats.sync;

/**
 * Created by RAJA on 20-06-2016.
 */
public class DbConstants {
    public interface Iupdates {
        String tableName = "updates";
        String ID = "_ID";
        String serverId = "server_id";
        String imageUrl = "image_url";
        String updateText = "update_text";
        String date = "date";
        String type = "type";
        String url = "url";
        String synced = "synced";
        String tileImageUrl = "tileImageUrl";
        String localImagePath = "local_image_path";
        String CREATE_UPDATES_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + serverId + " TEXT NOT NULL UNIQUE, " + imageUrl +
                " TEXT, " + updateText + " TEXT, " + date + " TEXT , " + tileImageUrl + " TEXT, " + type + " TEXT, " + url + " TEXT, " + localImagePath + " TEXT, " + synced + " INTEGER)";

        String DROP_UPDATES_TABLE = "DROP TABLE IF EXISTS " + tableName;
    }

    public interface Ialerts {
        String tableName = "alerts";
        String ID = "ID";
        String channel = "channel";
        String createdOn = "created_on";
        String imageUrl = "image_url";
        String message = "message";
        String notificationImageUrl = "notificatn_img_url";
        String notificationStatus = "notificatn_status";
        String notificationType = "notificatn_type";
        String sendOn = "send_on";
        String isRead = "is_read";
        String isTargetAchieved = "is_achieved";
        String CREATE_ALERT_TABLE = "CREATE TABLE " + tableName + " (" + ID + " TEXT PRIMARY KEY, " + channel +
                " TEXT, " + createdOn + " TEXT, " + imageUrl + " TEXT, " + message + " TEXT, " + notificationImageUrl + " TEXT, " + notificationStatus + " TEXT, " + notificationType +
                " TEXT, " + sendOn + " TEXT, " + isRead + " TEXT, " + isTargetAchieved + " TEXT)";

        String DROP_ALERT_TABLE = "DROP TABLE IF EXISTS " + tableName;

        public interface IalertData {
            String tableDataName = "alert_data";
            String id = "_ID";
            String serverId = "server_id";
            String key = "key";
            String value = "value";
            String CREATE_ALERT_DATA_TABLE = "CREATE TABLE " + tableDataName + " ( " + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + serverId + " TEXT, " + key + " TEXT, " + value + " TEXT)";
            String DROP_ALERT_DATA_TABLE = "DROP TABLE IF EXISTS " + tableDataName;
        }
    }


    public interface IcustomPages {
        String tableName = "custom_pages";
        String ID = "_ID";
        String pageName = "page_name";
        String dateStamp = "date_stamp";
        String pageContent = "page_content";

        String CREATE_CUSTOM_PAGES_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + pageName +
                " TEXT, " + dateStamp + " TEXT, " + pageContent + " TEXT)";
        String DROP_CUSTOM_PAGES_TABLE = "DROP TABLE IF EXISTS " + tableName;

    }

    /*
     * variable count excluding table_name is 7
     */
    public interface IphotoGallery {
        String tableName = "photo_gallery";
        String ID = "_ID";
        String serverId = "server_id";
        String imageTag = "image_tag";
        String imageName = "image_name";
        String imageUrl = "image_url";
        String localImageUrl = "local_image_url";
        String tileImageUrl = "tile_image_url";
        String synced = "synced";
        String CREATE_PHOTO_GALLERY_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + serverId + " TEXT, " + imageName +
                " TEXT, " + imageTag + " TEXT, " + imageUrl + " TEXT, " + localImageUrl + " TEXT, " + tileImageUrl + " TEXT, " + synced + " INTEGER)";
        String DROP_PHOTO_GALLERY_TABLE = "DROP TABLE IF EXISTS " + tableName;

    }

    public interface IproductGallery {
        String tableName = "product_gallery";
        String ID = "_ID";
        String buyOnlineLink = "buy_online_link";
        String currencyCode = "currency_code";
        String description = "descr";
        String discountAmount = "disc_amnt";
        String externalSourceId = "ext_src_id";
        String isArchived = "is_archvd";
        String isAvailable = "is_avlbl";
        String name = "name";
        String priority = "priority";
        String shipmentDuration = "shipment_duratn";
        String applicationId = "app_id";
        String customWidgets = "custom_widgets";
        String fpTag = "fp_tag";
        String images = "images";
        String imageUrl = "img_url";
        String merchantName = "mer_name";
        String tileImageUri = "tile_img_uri";
        String serverId = "server_id";
        String gpId = "gp_id";
        String totalQueries = "total_queries";
        String createdOn = "created_on";
        String updatedOn = "update_on";
        String price = "price";
        String CREATE_PRODUCT_GALLERY_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + serverId +
                " TEXT UNIQUE, " + name + " TEXT, " + imageUrl + " TEXT, " + buyOnlineLink + " TEXT, " + price + " TEXT, " + currencyCode + " TEXT, " + description + " TEXT, " + discountAmount + " INTEGER, " +
                externalSourceId + " TEXT, " + isArchived + " TEXT, " + isAvailable + " TEXT, " + priority + " INTEGER, " + shipmentDuration + " INTEGER, " + applicationId + " TEXT, " +
                customWidgets + " TEXT, " + fpTag + " TEXT, " + images + " TEXT, " + merchantName + " TEXT, " + tileImageUri + " TEXT, " + gpId + " TEXT, " + totalQueries + " INTEGER, " +
                createdOn + " TEXT, " + updatedOn + " TEXT )";
        String DROP_PRODUCT_GALLERY_TABLE = "DROP TABLE IF EXISTS " + tableName;

    }

    public interface Isearch_queries {
        String tableName = "search_queries";
        String ID = "_ID";
        String SearchQueries = "queries";
        String date = "DATE";

        String CREATE_SEARCH_QUERIES_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SearchQueries +
                " TEXT, " + date + " TEXT)";
        String DROP_SEARCH_QUERIES_TABLE  = "DROP TABLE IF EXISTS " + tableName;

    }

    public interface IstoreActivePlans {
        String tableName = "active_plans";
        String ID = "_ID";
        String planName = "plan_name";
        String planDescr = "plan_descr";
        String price = "price";

        String CREATE_ACTIVE_PLANS_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + planName +
                " TEXT, " + planDescr + " TEXT, " + price + " REAL)";
        String DROP_ACTIVE_PLANS_TABLE  = "DROP TABLE IF EXISTS " + tableName;

    }

    public interface IstoreImages {
        String tableName = "store_images";
        String ID = "_ID";
        String foreignIdPlanName = "foreign_id";
        String imageUrl = "image_url";
        String CREATE_STORE_IMAGES_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + foreignIdPlanName +
                " TEXT, " + imageUrl + " TEXT)";
        String DROP_STORE_IMAGES_TABLE  = "DROP TABLE IF EXISTS " + tableName;

    }

    public interface IsamBubble {
        String tableName = "sam_bubble";
        String ID = "_ID";
        String SUGGESTIONS = "suggestions";
        String CREATE_SAM_BUBBLE_TABLE = "CREATE TABLE " + tableName + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SUGGESTIONS + " TEXT)";
        String DROP_SAM_BUBBLE_TABLE  = "DROP TABLE IF EXISTS " + tableName;

    }

}