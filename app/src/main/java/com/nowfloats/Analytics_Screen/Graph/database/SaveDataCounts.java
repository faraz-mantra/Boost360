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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Abhi on 11/8/2016.
 */

public class SaveDataCounts {

    public static final String DATE_ID = "id";
    public static final String DATE = "date";
    public static final String DATA_COUNT = "dataCount";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String WEEK = "week";
    private Context mContext;

    private Holder mHolder;
    private SQLiteDatabase mDb;
    public static final String DATABASE_NAME = "Analytics.db";
    private static final int DATABASE_VERSION = 1;

    private static final String ANALYTICS_TABLE = "AnalyticsTable";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + ANALYTICS_TABLE;
    private static final String WEEK_COUNT ="weekCount" ;
    private static final String MONTH_COUNT ="monthCount" ;
    private static final String YEAR_COUNT ="yearCount" ;
    private static final String CREATE_ANALYTICS_TABLE = "CREATE TABLE " + ANALYTICS_TABLE +
            " ( " + DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE + " INTEGER, "+ YEAR_COUNT + " INTEGER, "
            + MONTH_COUNT + " INTEGER, "+ WEEK_COUNT + " INTEGER, "+ DATA_COUNT + " VARCHAR(255))";

    public SaveDataCounts(Context context) {
        mContext = context;
        mHolder = new Holder(mContext);
    }
    public void addDataCount(DatabaseModel data) {
        mDb = mHolder.getWritableDatabase();
        ContentValues values=makeContentValues(data);
        long i=mDb.insert(ANALYTICS_TABLE, null, values);
        Log.v("ggg",i+"insert row");
        mDb.close();
    }
    public void updateDataCount(DatabaseModel data){
        mDb = mHolder.getWritableDatabase();
        ContentValues values=makeContentValues(data);
        long i=mDb.update(ANALYTICS_TABLE,values,null,null);
        Log.v("ggg",i+"update row");
        mDb.close();
    }
    public boolean rowExist(){
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + ANALYTICS_TABLE, null);
        if (mCursor.moveToFirst())
           return true;
        else
            return false;
    }
    private ContentValues makeContentValues(DatabaseModel data){
        ContentValues values = new ContentValues();
        JSONObject json = new JSONObject();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json.put(YEAR, new JSONArray(data.getYear()));
                json.put(MONTH, new JSONArray(data.getMonth()));
                json.put(WEEK, new JSONArray(data.getWeek()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(DATA_COUNT,json.toString());
        values.put(DATE,data.getDate());
        values.put(YEAR_COUNT,data.getYearCount());
        values.put(MONTH_COUNT,data.getMonthCount());
        values.put(WEEK_COUNT,data.getWeekCount());
        return values;
    }
    public long deleteAll(){
        mDb = mHolder.getWritableDatabase();
        long i = mDb.delete(ANALYTICS_TABLE,null, null);
        Log.v("ggg",i+"delete row");
        return i;
    }
    public DatabaseModel getDataArrays() {
        DatabaseModel modeldata=null;
        try {
            mDb = mHolder.getWritableDatabase();
            String selectQuery = "SELECT * FROM "+ ANALYTICS_TABLE;
            Cursor cursor = mDb.rawQuery(selectQuery, null);
            Log.v("ggg","getdata");
            if(cursor==null) return null;
            if (cursor.moveToFirst()) {
                modeldata= new DatabaseModel();
                do {
                    Log.v("ggg","doloop");
                    String arrayJSON=cursor.getString(5);
                    if(arrayJSON==null||arrayJSON.equals("null")) return null;
                    JSONObject json = new JSONObject(cursor.getString(5));
                    modeldata.setDate(cursor.getString(1));
                    modeldata.setYear(jsonArrayToIntArray(json.getJSONArray(YEAR)));
                    modeldata.setMonth(jsonArrayToIntArray(json.getJSONArray(MONTH)));
                    modeldata.setWeek(jsonArrayToIntArray(json.getJSONArray(WEEK)));
                    modeldata.setYearCount(cursor.getInt(2));
                    modeldata.setMonthCount(cursor.getInt(3));
                    modeldata.setWeekCount(cursor.getInt(4));
                    Log.v("ggg","one times run getArray database");
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally
        {
            mDb.close();
        }
        return modeldata;
    }
    private int[] jsonArrayToIntArray(JSONArray json){
        if(json==null) return null;
        int length=json.length();
        int[] intAarry=new int[length];
        for(int i=0;i<length;i++)
            intAarry[i]=json.optInt(i);
        return intAarry;
    }
    class Holder extends SQLiteOpenHelper {

         Holder(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            //Toast.makeText(mContext, "oncreate", Toast.LENGTH_SHORT).show();
            try {
                db.execSQL(CREATE_ANALYTICS_TABLE);
            }catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Toast.makeText(mContext, "onupgrade", Toast.LENGTH_SHORT).show();
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}