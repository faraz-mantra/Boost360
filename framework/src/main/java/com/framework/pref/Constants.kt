package com.framework.pref

import android.util.DisplayMetrics
import com.framework.BuildConfig
import java.util.*

var APPLICATION_JIO_ID = "com.jio.online"
var APPLICATION_HEALTHGRO_ID = "com.healthgro.nowfloats"
var APPLICATION_ARANTOO_ID = "com.arantoo.nowfloats"
var APPLICATION_ARDHIM_ID = "com.ardhim.nowfloats"
var APPLICATION_CHECKKINN_ID = "com.checkkinn.nowfloats"

val REFERRAL_CAMPAIGN_CODE = 26277

var PREF_NAME = "nowfloatsPrefs"

//val clientId: String get() = clientId2
val clientId: String get() = BuildConfig.CLIENT_ID

var clientIdThinksity = "217FF5B9CE214CDDAC4985C853AE7F75AAFA11AF2C4B47CB877BCA26EC217E6D"

var clientId2 = BuildConfig.CLIENT_ID

var clientId4 = "731EBAB8596648E79882096E4733E7173B314BECC649423DB67897BF8CC596EC"

var clientId3 = "A816E08AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F51770E46AD86"

var clientId1 = "39EB5FD120DC4394A10301B108030CB70FA553E91F984C829AB6ADE23B6767B7"

var teleCountry = "in"
var DISPLAY_METRICS: DisplayMetrics? = null
var hasStoreData = false
var hasSearchData = false
var FontSizeT = 13
var FontSizeE = 16

var prevDataDirectory = "NowFloatsBiz"
var dataDirectory = "NowFloatsBoost"

var days = arrayOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")
var Currency_Country_Map: Map<String, String> = HashMap()
var currencyArray = ArrayList<String>()

const val WA_KEY = "58ede4d4ee786c1604f6c535"

val BASE_IMAGE_URL = "https://content.withfloats.com"

var StoreWidgets = ArrayList<String>()

var ANA_CHAT_API_URL =  /*"http://chat-dev.nowfloatsdev.com";// */"https://gateway.api.ana.chat"
var ANA_BUSINESS_ID =  /*"Boost-Web";// */"boost-agent-chat"