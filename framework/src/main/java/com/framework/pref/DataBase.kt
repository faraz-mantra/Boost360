package com.framework.pref

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import java.util.*

class DataBase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
  var context: Context = context.applicationContext
  var createLoginTable = ("CREATE TABLE IF NOT EXISTS " + tableLogin
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
      + colsourceClientId + " TEXT PAYMENT_OPTIONS '0')")
  var createFPIDsTable = "CREATE TABLE IF NOT EXISTS $tableFPIds ( $colFPID TEXT PRIMARY KEY )"
  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(createLoginTable)
    db.execSQL(createFPIDsTable)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS $tableLogin")
    db.execSQL("DROP TABLE IF EXISTS $tableFPIds")
    onCreate(db)
  }

  //Login Methods
  val loginStatus: Cursor
    get() {
      val qdb = this.writableDatabase
      return qdb.rawQuery("SELECT * FROM $tableLogin ", null)
    }

  fun insertLoginStatus(value: Bundle, fpid: String?) {
    val qdb = this.writableDatabase
    val cursor = qdb.rawQuery("SELECT * FROM $tableLogin", null)
    if (cursor.count != 0) {
      qdb.execSQL("DELETE FROM $tableLogin WHERE $colLoginStatus = 'true'")
    }
    cursor.close()
    val cv = ContentValues()
    cv.put(colLoginStatus, "true")
    cv.put(colFPID, fpid)
    cv.put(colaccessType, value.getString("accessType"))
    cv.put(colisEnterprise, value.getString("isEnterprise"))
    cv.put(colisRestricted, value.getString("isRestricted"))
    cv.put(colsocialShareTokens, value.getString("socialShareTokens"))
    cv.put(colsourceClientId, value.getString("sourceClientId"))
    cv.put(colloginId, value.getString("loginId"))
    val chk = qdb.insertWithOnConflict(tableLogin, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
    Log.i("insertLoginStatus----", "" + chk)
    qdb.close()
  }

  fun insertLoginStatus(fpid: String?) {
    val qdb = this.writableDatabase
    val cv = ContentValues()
    cv.put(colLoginStatus, "true")
    cv.put(colFPID, fpid)
    val chk = qdb.insertWithOnConflict(tableLogin, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
    Log.i("insertLoginStatus-fp", "" + chk)
    qdb.close()
  }

  fun updateFacebookPage(value: String?, id: String?, token: String?) {
    val qdb = this.writableDatabase
    val cv = ContentValues()
    cv.put(colFacebookPage, value)
    cv.put(colFacebookPageId, id)
    cv.put(colFacebookPageAccessToken, token)
    qdb.update(tableLogin, cv, "$colLoginStatus = 'true'", null)
    qdb.close()
  }

  fun updateFacebookNameandToken(value: String?, Token: String?) {
    val qdb = this.writableDatabase
    val cv = ContentValues()
    cv.put(colFacebookName, value)
    cv.put(colFacebookAccessToken, Token)
    qdb.update(tableLogin, cv, "$colLoginStatus = 'true'", null)
    qdb.close()
  }

  fun deleteLoginStatus() {
    val qdb = this.writableDatabase
    qdb.execSQL("DELETE FROM $tableLogin WHERE $colLoginStatus = 'true'")
    qdb.close()
  }

  fun insertFPIDs(fpList: ArrayList<String?>) {
    val qdb = this.writableDatabase
    for (i in fpList.indices) {
      val cv = ContentValues()
      cv.put(colFPID, fpList[i])
      qdb.insertWithOnConflict(tableFPIds, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
      qdb.close()
    }
  }

  companion object {
    const val DB_VERSION = 3
    const val DB_NAME = "nowfloats.s3db"
    const val tableLogin = "Login_Details"
    const val tableFPIds = "ValidFPIds"

    //Login table Column names
    const val colLoginStatus = "login_status"
    const val colFPID = "fpid"
    const val colFacebookName = "facebook_name"
    const val colFacebookPage = "facebook_page"
    const val colFacebookAccessToken = "facebook_token"
    const val colFacebookPageAccessToken = "facebook_page_token"
    const val colFacebookPageId = "facebook_page_id"
    const val colaccessType = "accessType"
    const val colisEnterprise = "isEnterprise"
    const val colisRestricted = "isRestricted"
    const val colloginId = "loginId"
    const val colsocialShareTokens = "socialShareTokens"
    const val colsourceClientId = "sourceClientId"
  }

}