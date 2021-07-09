package com.nowfloats.riachatsdk.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


/**
 * Created by RAJA on 20-06-2016.
 */
public class ChatContentProvider extends ContentProvider {

    private final String TAG = ChatContentProvider.class.getName();


    private static final int CHAT_HISTORY = 1;
    private static final int CHAT_VALUES = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ChatDbConstants.AUTHORITY, ChatDbConstants.IChatHistory.tableName, CHAT_HISTORY);
        sUriMatcher.addURI(ChatDbConstants.AUTHORITY, ChatDbConstants.IChatValues.tableName, CHAT_VALUES);
    }


    private ChatDatabaseOpenHelper boostDatabaseHelper;
    private SQLiteDatabase db;
    private Context context;
    public static String dbName = "chatbot.db";


    @Override
    public boolean onCreate() {
        context = getContext();
        /*
         *This dbName needs to be edited according to the Constants value(e.g  Constants.PREF_NAME).
         * Remove the default db name on porting to BOOST app
         */

        boostDatabaseHelper = new ChatDatabaseOpenHelper(context, dbName, null, 1);
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        db = boostDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CHAT_HISTORY:
                builder.setTables(ChatDbConstants.IChatHistory.tableName);
                break;
            case CHAT_VALUES:
                builder.setTables(ChatDbConstants.IChatValues.tableName);
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
            case CHAT_HISTORY:
                id = db.insert(ChatDbConstants.IChatHistory.tableName, null, values);
                break;
            case CHAT_VALUES:
                id = db.insert(ChatDbConstants.IChatValues.tableName, null, values);
                break;
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
            case CHAT_HISTORY:
                id = db.delete(ChatDbConstants.IChatHistory.tableName, selection, selectionArgs);
                break;
            case CHAT_VALUES:
                id = db.delete(ChatDbConstants.IChatValues.tableName, selection, selectionArgs);
                break;
        }
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int updateCount = 0;
        db = boostDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CHAT_HISTORY:
                updateCount = db.update(ChatDbConstants.IChatHistory.tableName, values, selection, selectionArgs);
                break;
            case CHAT_VALUES:
                updateCount = db.update(ChatDbConstants.IChatValues.tableName, values, selection, selectionArgs);
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


    protected static final class ChatDatabaseOpenHelper extends SQLiteOpenHelper {

        public ChatDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ChatDbConstants.IChatHistory.CREATE_CHAT_HISTORY_TABLE);
            db.execSQL(ChatDbConstants.IChatValues.CREATE_CHAT_VALUES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        }
    }


}
