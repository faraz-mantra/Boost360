package com.nowfloats.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nowfloats.Login.Model.Login_Data_Model;

import java.util.ArrayList;

/**
 * Created by guru on 18/5/15.
 */
public class DataBase extends SQLiteOpenHelper {
    //Login table Column names
    public static final String colLoginStatus = "login_status";
    public static final String colFPID = "fpid";
    public static final String colFacebookName = "facebook_name";
    public static final String colFacebookPage = "facebook_page";
    public static final String colFacebookAccessToken = "facebook_token";
    public static final String colFacebookPageAccessToken = "facebook_page_token";
    public static final String colFacebookPageId = "facebook_page_id";
    public static final String colaccessType = "accessType";
    public static final String colisEnterprise = "isEnterprise";
    public static final String colisRestricted = "isRestricted";
    public static final String colloginId = "loginId";
    public static final String colsocialShareTokens = "socialShareTokens";
    public static final String colsourceClientId = "sourceClientId";
    final static int DB_VERSION = 3;
    final static String DB_NAME = "nowfloats.s3db";
    final static String tableLogin = "Login_Details";
    final static String tableFPIds = "ValidFPIds";
    Context context;
    String createLoginTable = "CREATE TABLE IF NOT EXISTS " + tableLogin
            + " ( " + colLoginStatus + " TEXT PRIMARY KEY , "
            + colFPID + " TEXT PAYMENT_OPTIONS '0', "
            + colFacebookName + " TEXT PAYMENT_OPTIONS ' ', "
            + colFacebookPage + " TEXT PAYMENT_OPTIONS ' ', "
            + colFacebookAccessToken + " TEXT PAYMENT_OPTIONS '0', "
            + colFacebookPageAccessToken + " TEXT PAYMENT_OPTIONS '0', "
            + colFacebookPageId + " TEXT PAYMENT_OPTIONS '0' , "
            + colaccessType + " TEXT PAYMENT_OPTIONS '0', "
            + colisEnterprise + " TEXT PAYMENT_OPTIONS '0', "
            + colisRestricted + " TEXT PAYMENT_OPTIONS 'false', "
            + colloginId + " TEXT PAYMENT_OPTIONS '0', "
            + colsocialShareTokens + " TEXT PAYMENT_OPTIONS '0', "
            + colsourceClientId + " TEXT PAYMENT_OPTIONS '0')";
    String createFPIDsTable = "CREATE TABLE IF NOT EXISTS " + tableFPIds + " ( " + colFPID + " TEXT PRIMARY KEY )";

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createLoginTable);
        db.execSQL(createFPIDsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableLogin);
        db.execSQL("DROP TABLE IF EXISTS " + tableFPIds);
        onCreate(db);
    }

    //Login Methods
    public Cursor getLoginStatus() {
        SQLiteDatabase qdb = this.getWritableDatabase();
        Cursor cursor = qdb.rawQuery("SELECT * FROM " + tableLogin + " ", null);
        return cursor;
    }

    public void insertLoginStatus(Login_Data_Model value, String fpid) {
        SQLiteDatabase qdb = this.getWritableDatabase();
        Cursor cursor = qdb.rawQuery("SELECT * FROM " + tableLogin, null);
        if (cursor.getCount() != 0) {
            qdb.execSQL("DELETE FROM " + tableLogin + " WHERE " + colLoginStatus + " = 'true'");
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(colLoginStatus, "true");
        cv.put(colFPID, fpid);
        cv.put(colaccessType, value.accessType);
        cv.put(colisEnterprise, value.isEnterprise);
        cv.put(colisRestricted, value.isRestricted);
        cv.put(colsocialShareTokens, value.socialShareTokens);
        cv.put(colsourceClientId, value.sourceClientId);
        cv.put(colloginId, value.loginId);
        long chk = qdb.insertWithOnConflict(tableLogin, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        Log.i("insertLoginStatus----", "" + chk);
        qdb.close();
    }

    public void insertLoginStatus(String fpid) {
        SQLiteDatabase qdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colLoginStatus, "true");
        cv.put(colFPID, fpid);
        long chk = qdb.insertWithOnConflict(tableLogin, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        Log.i("insertLoginStatus-fp", "" + chk);
        qdb.close();
    }

    public void updateFacebookPage(String value, String id, String token) {
        SQLiteDatabase qdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colFacebookPage, value);
        cv.put(colFacebookPageId, id);
        cv.put(colFacebookPageAccessToken, token);
        qdb.update(tableLogin, cv, colLoginStatus + " = 'true'", null);
        qdb.close();
    }

    public void updateFacebookNameandToken(String value, String Token) {
        SQLiteDatabase qdb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(colFacebookName, value);
        cv.put(colFacebookAccessToken, Token);
        qdb.update(tableLogin, cv, colLoginStatus + " = 'true'", null);
        qdb.close();
    }

    public void deleteLoginStatus() {
        SQLiteDatabase qdb = this.getWritableDatabase();
        qdb.execSQL("DELETE FROM " + tableLogin + " WHERE " + colLoginStatus + " = 'true'");
        qdb.close();
    }

    public void insertFPIDs(ArrayList<String> fpList) {
        SQLiteDatabase qdb = this.getWritableDatabase();
        for (int i = 0; i < fpList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(colFPID, fpList.get(i));
            qdb.insertWithOnConflict(tableFPIds, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            qdb.close();
        }

    }
}
