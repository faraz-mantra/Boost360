package com.nowfloats.Analytics_Screen.Graph.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.nowfloats.Analytics_Screen.Graph.model.DatabaseModel;
import com.nowfloats.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Abhi on 11/8/2016.
 */

public class SaveDataCounts {

    public static final String DATABASE_NAME = "Analytics.db";
    private static final String DATE_ID = "id";
    private static final String DATE = "DATE";
    private static final String DATA_COUNT = "dataCount";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String WEEK = "week";
    private static final int DATABASE_VERSION = 2;
    private static final String VISITS_TABLE = "AnalyticsTable";
    private static final String VISITORS_TABLE = "VisitorsAnalyticsTable";
    private static final String DROP_VISITORS_TABLE = "DROP TABLE IF EXISTS " + VISITORS_TABLE;
    private static final String DROP_VISITS_TABLE = "DROP TABLE IF EXISTS " + VISITS_TABLE;
    private static final String WEEK_COUNT = "weekCount";
    private static final String MONTH_COUNT = "monthCount";
    private static final String YEAR_COUNT = "yearCount";
    private static final String CREATE_VISITORS_TABLE = "CREATE TABLE " + VISITORS_TABLE +
            " ( " + DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " INTEGER, " + YEAR_COUNT + " INTEGER, "
            + MONTH_COUNT + " INTEGER, " + WEEK_COUNT + " INTEGER, " + DATA_COUNT + " VARCHAR(255))";
    private static final String CREATE_VISITS_TABLE = "CREATE TABLE " + VISITS_TABLE +
            " ( " + DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " INTEGER, " + YEAR_COUNT + " INTEGER, "
            + MONTH_COUNT + " INTEGER, " + WEEK_COUNT + " INTEGER, " + DATA_COUNT + " VARCHAR(255))";
    private final Context mContext;
    private Holder mHolder;
    private SQLiteDatabase mDb;
    private String ANALYTICS_TABLE;

    public SaveDataCounts(Context context, int visitors) {
        if (visitors == Constants.VISITS_TABLE) {
            ANALYTICS_TABLE = VISITS_TABLE;
        } else {
            ANALYTICS_TABLE = VISITORS_TABLE;
        }
        mContext = context;
        mHolder = new Holder(context);
    }

    public void addDataCount(DatabaseModel data) {
        mDb = mHolder.getWritableDatabase();
        ContentValues values = makeContentValues(data);
        long i = mDb.insert(ANALYTICS_TABLE, null, values);
        //Log.v("ggg",i+" insert row "+ANALYTICS_TABLE);
        mDb.close();
    }

    public void updateDataCount(DatabaseModel data) {
        mDb = mHolder.getWritableDatabase();
        ContentValues values = makeContentValues(data);
        long i = mDb.update(ANALYTICS_TABLE, values, null, null);
        //Log.v("ggg",i+" update row"+ANALYTICS_TABLE);
        mDb.close();
    }

    public boolean rowExist() {
        mDb = mHolder.getReadableDatabase();
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + ANALYTICS_TABLE, null);
        boolean isExist = mCursor.moveToFirst();
        mCursor.close();
        return isExist;
    }

    private ContentValues makeContentValues(DatabaseModel data) {
        ContentValues values = new ContentValues();
        JSONObject json = new JSONObject();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json.put(YEAR, new JSONArray(data.getYear()));
                json.put(MONTH, new JSONArray(data.getMonth()));
                json.put(WEEK, new JSONArray(data.getWeek()));
            }
        } catch (JSONException e) {
            Log.v("ggg", e.getMessage() + " colums");
        }
        values.put(DATA_COUNT, json.toString());
        values.put(DATE, data.getDate());
        values.put(YEAR_COUNT, data.getYearCount());
        values.put(MONTH_COUNT, data.getMonthCount());
        values.put(WEEK_COUNT, data.getWeekCount());
        return values;
    }

    public long deleteAll() {
        mDb = mHolder.getWritableDatabase();
        return mDb.delete(ANALYTICS_TABLE, null, null);
        //Log.v("ggg",i+"delete row");
    }

    public DatabaseModel getDataArrays() {
        DatabaseModel modeldata = null;
        try {
            mDb = mHolder.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + ANALYTICS_TABLE;
            Cursor cursor = mDb.rawQuery(selectQuery, null);

            if (cursor == null) return null;
            if (cursor.moveToFirst()) {
                modeldata = new DatabaseModel();
                do {

                    String arrayJSON = cursor.getString(5);
                    if (arrayJSON == null || arrayJSON.equals("null")) return null;
                    JSONObject json = new JSONObject(cursor.getString(5));
                    modeldata.setDate(cursor.getString(1));
                    modeldata.setYear(jsonArrayToIntArray(json.getJSONArray(YEAR)));
                    modeldata.setMonth(jsonArrayToIntArray(json.getJSONArray(MONTH)));
                    modeldata.setWeek(jsonArrayToIntArray(json.getJSONArray(WEEK)));
                    modeldata.setYearCount(cursor.getInt(2));
                    modeldata.setMonthCount(cursor.getInt(3));
                    modeldata.setWeekCount(cursor.getInt(4));
                    //Log.v("ggg","one times run getArray database");
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            mDb.close();
        }
        return modeldata;
    }

    private int[] jsonArrayToIntArray(JSONArray json) {
        if (json == null) return null;
        int length = json.length();
        int[] intAarry = new int[length];
        for (int i = 0; i < length; i++)
            intAarry[i] = json.optInt(i);
        return intAarry;
    }

    class Holder extends SQLiteOpenHelper {

        Holder(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_VISITORS_TABLE);
                db.execSQL(CREATE_VISITS_TABLE);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                Log.v("ggg", e.getMessage() + "");
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Toast.makeText(mContext, "onupgrade", Toast.LENGTH_SHORT).show();
            try {
                db.execSQL(DROP_VISITORS_TABLE);
                db.execSQL(DROP_VISITS_TABLE);
                onCreate(db);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                Log.v("ggg", e.getMessage() + "");
            }
        }
    }
}