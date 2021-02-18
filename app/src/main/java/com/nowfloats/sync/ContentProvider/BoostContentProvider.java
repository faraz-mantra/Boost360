package com.nowfloats.sync.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.nowfloats.sync.Constants;
import com.nowfloats.sync.DbConstants;


/**
 * Created by RAJA on 20-06-2016.
 */
public class BoostContentProvider extends ContentProvider {

    private final String TAG = BoostContentProvider.class.getName();


    private static final int UPDATES = 1;
    private static final int ALERTS = 2;
    private static final int CUSTOM_PAGES = 3;
    private static final int PHOTO_GALLERY = 4;
    private static final int PRODUCT_GALLERY = 5;
    private static final int SEARCH_QUERIES = 6;
    private static final int STORE_ACTIVE_PLANS = 7;
    private static final int ACTIVE_PLANS_IMAGES = 8;
    private static final int UPDATES_ID = 9;
    private static final int PHOTO_GALLERY_ID = 10;
    private static final int PRODUCT_GALLERY_ID = 11;
    private static final int ALERT_DATA_MODEL = 12;
    private static final int SAM_BUBBLE = 13;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.Iupdates.tableName, UPDATES);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.Ialerts.tableName, ALERTS);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IcustomPages.tableName, CUSTOM_PAGES);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IphotoGallery.tableName, PHOTO_GALLERY);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IproductGallery.tableName, PRODUCT_GALLERY);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.Isearch_queries.tableName, SEARCH_QUERIES);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IstoreActivePlans.tableName, STORE_ACTIVE_PLANS);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IstoreImages.tableName, ACTIVE_PLANS_IMAGES);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.Iupdates.tableName + "/#", UPDATES_ID);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IphotoGallery.tableName + "/#", PHOTO_GALLERY_ID);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IproductGallery.tableName + "/#", PRODUCT_GALLERY_ID);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.Ialerts.IalertData.tableDataName, ALERT_DATA_MODEL);
        sUriMatcher.addURI(Constants.AUTHORITY, DbConstants.IsamBubble.tableName, SAM_BUBBLE);
    }


    private BoostDatabaseOpenHelper boostDatabaseHelper;
    private SQLiteDatabase db;
    Context context;


    @Override
    public boolean onCreate() {
        context = getContext();
        /*
         *This dbName needs to be edited according to the Constants value(e.g  Constants.PREF_NAME).
         * Remove the default db name on porting to BOOST app
         */
        //UserSessionManager sessionManager = new UserSessionManager(context, (Activity)context);
        String dbName = "BoostDefault.db";
        /*String fpid = null;

        DataBase db =new DataBase(context);
        Cursor cursor = db.getLoginStatus();
        //boolean isLogin = false ;
        if (cursor!=null && cursor.getCount()>0) {
            if (cursor.moveToNext()) {
                fpid = cursor.getString(1);
            }
        }
        if(fpid!=null){
            dbName = fpid + ".db";
        }else{
            dbName = "BoostDefault.db";
        }*/
        boostDatabaseHelper = new BoostDatabaseOpenHelper(context, dbName, null, 3);
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        db = boostDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case UPDATES:
                builder.setTables(DbConstants.Iupdates.tableName);
                break;
            case ALERTS:
                builder.setTables(DbConstants.Ialerts.tableName);
                break;
            case CUSTOM_PAGES:
                builder.setTables(DbConstants.IcustomPages.tableName);
                break;
            case PHOTO_GALLERY:
                builder.setTables(DbConstants.IphotoGallery.tableName);
                break;
            case PRODUCT_GALLERY:
                builder.setTables(DbConstants.IproductGallery.tableName);
                break;
            case SEARCH_QUERIES:
                builder.setTables(DbConstants.Isearch_queries.tableName);
                break;
            case STORE_ACTIVE_PLANS:
                builder.setTables(DbConstants.IstoreActivePlans.tableName);
                break;
            case ACTIVE_PLANS_IMAGES:
                builder.setTables(DbConstants.IstoreActivePlans.tableName);
                break;
            case ALERT_DATA_MODEL:
                builder.setTables(DbConstants.Ialerts.IalertData.tableDataName);
                break;
            case SAM_BUBBLE:
                builder.setTables(DbConstants.IsamBubble.tableName);
                break;
            default:
                Log.d(TAG, "Unmatched URI, It may cause Exception");

        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //db.close();
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = boostDatabaseHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        long id = 0;
        switch (match) {
            case UPDATES:
                id = db.insert(DbConstants.Iupdates.tableName, null, values);
                break;
            case ALERTS:
                id = db.insert(DbConstants.Ialerts.tableName, null, values);
                break;
            case CUSTOM_PAGES:
                id = db.insert(DbConstants.IcustomPages.tableName, null, values);
                break;
            case PHOTO_GALLERY:
                id = db.insert(DbConstants.IphotoGallery.tableName, null, values);
                break;
            case PRODUCT_GALLERY:
                id = db.insert(DbConstants.IproductGallery.tableName, null, values);
                break;
            case SEARCH_QUERIES:
                id = db.insert(DbConstants.Isearch_queries.tableName, null, values);
                break;
            case STORE_ACTIVE_PLANS:
                id = db.insert(DbConstants.IstoreActivePlans.tableName, null, values);
                break;
            case ACTIVE_PLANS_IMAGES:
                id = db.insert(DbConstants.IstoreActivePlans.tableName, null, values);
                break;
            case ALERT_DATA_MODEL:
                id = db.insert(DbConstants.Ialerts.IalertData.tableDataName, null, values);
                break;
            case SAM_BUBBLE:
                id = db.insert(DbConstants.IsamBubble.tableName, null, values);
                break;
            //builder.setTables(DbConstants.Ialerts.IalertData.tableName);

        }

        try {
            return getUriForId(id, uri);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = boostDatabaseHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int id = 0;
        switch (match) {
            case UPDATES:
                id = db.delete(DbConstants.Iupdates.tableName, selection, selectionArgs);
                break;
            case ALERTS:
                id = db.delete(DbConstants.Ialerts.tableName, selection, selectionArgs);
                break;
            case CUSTOM_PAGES:
                id = db.delete(DbConstants.IcustomPages.tableName, selection, selectionArgs);
                break;
            case PHOTO_GALLERY:
                id = db.delete(DbConstants.IphotoGallery.tableName, selection, selectionArgs);
                break;
            case PRODUCT_GALLERY:
                id = db.delete(DbConstants.IproductGallery.tableName, selection, selectionArgs);
                break;
            case SEARCH_QUERIES:
                id = db.delete(DbConstants.Isearch_queries.tableName, selection, selectionArgs);
                break;
            case STORE_ACTIVE_PLANS:
                id = db.delete(DbConstants.IstoreActivePlans.tableName, selection, selectionArgs);
                break;
            case ACTIVE_PLANS_IMAGES:
                id = db.delete(DbConstants.IstoreImages.tableName, selection, selectionArgs);
                break;
            case ALERT_DATA_MODEL:
                id = db.delete(DbConstants.Isearch_queries.tableName, selection, selectionArgs);
                break;
            case SAM_BUBBLE:
                id = db.delete(DbConstants.IsamBubble.tableName, selection, selectionArgs);
                break;

        }
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int updateCount = 0;
        db = boostDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case UPDATES:
                updateCount = db.update(DbConstants.Iupdates.tableName, values, selection, selectionArgs);
                break;
            case PHOTO_GALLERY:
                updateCount = db.update(DbConstants.IphotoGallery.tableName, values, selection, selectionArgs);
                break;
            case PRODUCT_GALLERY:
                updateCount = db.update(DbConstants.IproductGallery.tableName, values, selection, selectionArgs);
                break;
            case SAM_BUBBLE:
                updateCount = db.update(DbConstants.IsamBubble.tableName, values, selection, selectionArgs);
                break;
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //db.close();
        return updateCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }


    protected final class BoostDatabaseOpenHelper extends SQLiteOpenHelper {

        public BoostDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(DbConstants.Iupdates.CREATE_UPDATES_TABLE);
                db.execSQL(DbConstants.Ialerts.CREATE_ALERT_TABLE);
                db.execSQL(DbConstants.Ialerts.IalertData.CREATE_ALERT_DATA_TABLE);
                db.execSQL(DbConstants.IproductGallery.CREATE_PRODUCT_GALLERY_TABLE);
                db.execSQL(DbConstants.IphotoGallery.CREATE_PHOTO_GALLERY_TABLE);
                db.execSQL(DbConstants.IcustomPages.CREATE_CUSTOM_PAGES_TABLE);
                db.execSQL(DbConstants.Isearch_queries.CREATE_SEARCH_QUERIES_TABLE);
                db.execSQL(DbConstants.IstoreActivePlans.CREATE_ACTIVE_PLANS_TABLE);
                db.execSQL(DbConstants.IstoreImages.CREATE_STORE_IMAGES_TABLE);
                db.execSQL(DbConstants.IsamBubble.CREATE_SAM_BUBBLE_TABLE);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DbConstants.Iupdates.DROP_UPDATES_TABLE);
                db.execSQL(DbConstants.IcustomPages.DROP_CUSTOM_PAGES_TABLE);
                db.execSQL(DbConstants.Ialerts.DROP_ALERT_TABLE);
                db.execSQL(DbConstants.Ialerts.IalertData.DROP_ALERT_DATA_TABLE);
                db.execSQL(DbConstants.IphotoGallery.DROP_PHOTO_GALLERY_TABLE);
                db.execSQL(DbConstants.IproductGallery.DROP_PRODUCT_GALLERY_TABLE);
                db.execSQL(DbConstants.IsamBubble.DROP_SAM_BUBBLE_TABLE);
                db.execSQL(DbConstants.Isearch_queries.DROP_SEARCH_QUERIES_TABLE);
                db.execSQL(DbConstants.IstoreActivePlans.DROP_ACTIVE_PLANS_TABLE);
                db.execSQL(DbConstants.IstoreImages.DROP_STORE_IMAGES_TABLE);
                onCreate(db);
               SharedPreferences pref =  context.getSharedPreferences(com.nowfloats.util.Constants.PREF_NAME,Context.MODE_PRIVATE);
                pref.edit().putBoolean(com.nowfloats.util.Constants.SYNCED,false).apply();
            }catch (SQLException e){
                e.printStackTrace();
            }

        }
    }

}
