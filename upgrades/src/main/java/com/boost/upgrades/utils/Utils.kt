package com.boost.upgrades.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.boost.upgrades.data.api_model.GetAllWidgets.GetAllWidgets
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.utils.Constants.Companion.BASE_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {

    //getting retrofit instance
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager =
                activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        if (info != null)
            for (i in info.indices)
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
        longToast(context,"No Internet Connection")
        return false
    }

    fun toast(context: Context ,msg: Any){
        Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()
    }

    fun longToast(context: Context ,msg: Any){
        Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show()
    }

    fun readJSONFromAsset(context: Context): List<GetAllWidgets>? {
        val data: List<GetAllWidgets>
        var jsonString: String? = null
        try {
            val  inputStream: InputStream = context.assets.open("initialLoad.json")
            jsonString = inputStream.bufferedReader().use{it.readText()}
            val gson = Gson()
            val listPersonType = object : TypeToken<List<GetAllWidgets>>() {}.type

            data = gson.fromJson(jsonString, listPersonType)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return data
    }

    fun debitCreditCardPatterns(): ArrayList<String>{
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

    fun monthRange(): ArrayList<String> {
        var start = 1
        val stop = 12
        val list: ArrayList<String> = ArrayList()
        while (start <= stop) {
            if(start<10){
                list.add("0"+start++.toString())
            }else {
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
}