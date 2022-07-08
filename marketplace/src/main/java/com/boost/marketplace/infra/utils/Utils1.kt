package com.boost.marketplace.infra.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.boost.cart.utils.Constants
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.GetAllWidgets.GetAllWidgets
import com.framework.analytics.SentryController
import com.framework.rest.TokenAuthenticator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils1 {


    //getting retrofit instance
    fun getRetrofit(
        isAuthRemove: Boolean = false
    ): Retrofit {
        val client = Retrofit.Builder()
        client.baseUrl(Constants.BASE_URL)
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
        return httpClient.build()
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

    fun priceCalculatorForYear(price: Double,widgetType: String? = "",activity: Activity): Double{
        val prefs = SharedPrefs(activity)
        if(prefs.getYearPricing() && !(widgetType.equals("RECHARGE") || widgetType.equals("ONE_TIME"))){
            return price * 12
        }
        return  price
    }

    fun monthCalculatorForAddons(months: Int,widgetType: String? = ""): Int{
        if(!(widgetType.equals("RECHARGE") || widgetType.equals("ONE_TIME"))){
            return months
        }
        return 1
    }

    fun expiryCalculator(months: Int, widgetType: String? = "", activity: Activity): Int{
        val prefs = SharedPrefs(activity)
        if(prefs.getYearPricing() && !(widgetType.equals("RECHARGE") || widgetType.equals("ONE_TIME"))){
            return months * 12
        }
        return 1
    }

    fun yearlyOrMonthlyOrEmptyValidity(widgetType: String? = "", activity: Activity): String{
        val prefs = SharedPrefs(activity)
        if(widgetType.equals("RECHARGE") || widgetType.equals("ONE_TIME")){
            return ""
        } else if (prefs.getYearPricing()){
            return "  \n/year"
        } else {
            return "  \n/month"
        }
    }

    fun yearOrMonthText(months: Int, activity: Activity, capital: Boolean): String{
        val prefs = SharedPrefs(activity)
        if(capital) {
            return if (months > 1) {
                if (prefs.getYearPricing())
                    " Years"
                else
                    " Months"
            } else {
                if (prefs.getYearPricing())
                    " Year"
                else
                    " Month"
            }
        } else {
            return if (months > 1) {
                if (prefs.getYearPricing())
                    " years"
                else
                    " months"
            } else {
                if (prefs.getYearPricing())
                    " year"
                else
                    " month"
            }
        }

    }
}