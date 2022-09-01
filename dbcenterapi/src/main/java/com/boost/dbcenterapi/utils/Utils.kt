package com.boost.dbcenterapi.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.boost.dbcenterapi.BuildConfig
import com.boost.dbcenterapi.R
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllWidgets.GetAllWidgets
import com.boost.dbcenterapi.utils.Constants.Companion.BASE_URL
import com.framework.analytics.SentryController
import com.framework.rest.TokenAuthenticator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {


  //getting retrofit instance
  fun getRetrofit(
    isAuthRemove: Boolean = false
  ): Retrofit {
    val client = Retrofit.Builder()
    client.baseUrl(BASE_URL)
    client.addConverterFactory(ScalarsConverterFactory.create())
    client.addConverterFactory(GsonConverterFactory.create())
    client.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    client.client(httpClient(isAuthRemove))
    return client.build()
  }

  fun httpClient(isAuthRemove: Boolean): OkHttpClient {
    val tokenAuthenticator = TokenAuthenticator(isAuthRemove)
    val httpClient = OkHttpClient.Builder()
    httpClient.readTimeout(2, TimeUnit.MINUTES)
      .connectTimeout(2, TimeUnit.MINUTES)
      .writeTimeout(2, TimeUnit.MINUTES)
    httpClient.authenticator(tokenAuthenticator)
    httpClient.addInterceptor(getHttpLoggingInterceptor())
    return httpClient.build()
  }

  private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
    }
    return loggingInterceptor
  }

  fun hideSoftKeyboard(activity: Activity) {
    try {
      val inputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.e(Utils::class.java.name, e?.localizedMessage ?: "")
    }
  }

  fun isConnectedToInternet(context: Context): Boolean {
    val connectivity = context.getSystemService(
      Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val info = connectivity.allNetworkInfo
    for (i in info.indices)
      if (info[i].state == NetworkInfo.State.CONNECTED) return true

    longToast(context, "No Internet Connection")
    return false
  }

  fun toast(context: Context, msg: Any) {
    Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
  }

  fun longToast(context: Context, msg: Any) {
    Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show()
  }

  fun readJSONFromAsset(context: Context): List<GetAllWidgets>? {
    val data: List<GetAllWidgets>
    var jsonString: String? = null
    try {
      val inputStream: InputStream = context.assets.open("initialLoad.json")
      jsonString = inputStream.bufferedReader().use { it.readText() }
      val gson = Gson()
      val listPersonType = object : TypeToken<List<GetAllWidgets>>() {}.type
      data = gson.fromJson(jsonString, listPersonType)
    } catch (ex: Exception) {
      SentryController.captureException(ex)
      Log.e(Utils::class.java.name, ex?.localizedMessage ?: "")
      return null
    }
    return data
  }

  fun debitCreditCardPatterns(): ArrayList<String> {
    val listOfPattern = ArrayList<String>();

    val ptVisa = "^4[0-9]{6,}$";
    listOfPattern.add(ptVisa);
    val ptMasterCard = "^5[1-5][0-9]{5,}$";
    listOfPattern.add(ptMasterCard);
    val ptAmeExp = "^3[47][0-9]{5,}$";
    listOfPattern.add(ptAmeExp);
    val ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
    listOfPattern.add(ptDinClb);
    val ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
    listOfPattern.add(ptDiscover);
    val ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
    listOfPattern.add(ptJcb);

    return listOfPattern
  }

  fun isValidGSTIN(value: String): Boolean {

    val pattern: Pattern
    val matcher: Matcher
    val GSTIN_PATTERN = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}\$"
    pattern = Pattern.compile(GSTIN_PATTERN)
    matcher = pattern.matcher(value)

    return matcher.matches()

  }

  fun isValidMail(email: String): Boolean {
    return Pattern.compile(
      "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
          "\\@" +
          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
          "(" +
          "\\." +
          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
          ")+"
    ).matcher(email).matches()
  }

  fun isValidMobile(phone: String): Boolean {
    return Pattern.compile(
      "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}\$"
    )
      .matcher(phone).matches()
  }

  fun monthRange(): ArrayList<String> {
    var start = 1
    val stop = 12
    val list: ArrayList<String> = ArrayList()
    while (start <= stop) {
      if (start < 10) {
        list.add("0" + start++.toString())
      } else {
        list.add(start++.toString())
      }
    }
    return list
  }

  fun yearRange(
    startYear: Int,
    endYear: Int
  ): ArrayList<String> {
    var cur = startYear
    val stop = endYear
    val list: ArrayList<String> = ArrayList()
    while (cur <= stop) {
      list.add(cur++.toString())
    }
    return list
  }

  fun getDayOfMonthSuffix(n: Int): String? {
    return if (n >= 11 && n <= 13) {
      "th"
    } else when (n % 10) {
      1 -> "st"
      2 -> "nd"
      3 -> "rd"
      else -> "th"
    }
  }

  fun getAssetJsonData(context: Context): String? {
    val json: String
    try {
      val inputStream = context.getAssets().open("category_model_v3.json")
      val size = inputStream.available()
      val buffer = ByteArray(size)
      inputStream.use { it.read(buffer) }
      json = String(buffer)
    } catch (ioException: IOException) {
      ioException.printStackTrace()
      SentryController.captureException(ioException)
      return null
    }
    return json
  }

  fun getCityFromAssetJsonData(context: Context): String? {
    val json: String
    try {
      val inputStream = context.getAssets().open("cities.json")
      val size = inputStream.available()
      val buffer = ByteArray(size)
      inputStream.use { it.read(buffer) }
      json = String(buffer)
    } catch (ioException: IOException) {
      ioException.printStackTrace()
      SentryController.captureException(ioException)
      return null
    }
    return json
  }

  fun getStatesFromAssetJsonData(context: Context): String? {
    val json: String
    try {
      val inputStream = context.getAssets().open("states.json")
      val size = inputStream.available()
      val buffer = ByteArray(size)
      inputStream.use { it.read(buffer) }
      json = String(buffer)
    } catch (ioException: IOException) {
      ioException.printStackTrace()
      SentryController.captureException(ioException)
      return null
    }
    return json
  }

  fun filterBraces(cartids: String): String {
    var cartid = cartids.replace("[", "").replace("]", "")
    return cartid
  }

  fun filterQuotes(coupon: String): String {
    var couponid = coupon.replace("\"", "")
    return couponid
  }

  fun getBundlesFromJsonFile(context: Context):List<Bundles> {
    val `is`: InputStream = context.getResources().openRawResource(R.raw.localbundel)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    try {
      val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
      var n: Int
      while (reader.read(buffer).also { n = it } != -1) {
        writer.write(buffer, 0, n)
      }
    } finally {
      `is`.close()
    }

    return Gson().fromJson(writer.toString(),object : TypeToken<List<Bundles>>() {}.type)
  }

}