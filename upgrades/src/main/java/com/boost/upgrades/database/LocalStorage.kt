package com.boost.upgrades.database

import android.content.Context
import android.content.SharedPreferences
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.utils.Constants.Companion.USER_PREFERENCES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalStorage(context: Context) {
  private val sharedPreferences: SharedPreferences
  private var editor: SharedPreferences.Editor? = null
  private val INITIAL_LOAD_DATA = "INITIAL_LOAD_DATA"
  private val CART_ITEMS = "CART_ITEMS"

  fun setInitialLoad(updatesModel: List<WidgetModel>) {
    editor = sharedPreferences!!.edit()
    editor!!.putString(INITIAL_LOAD_DATA, Gson().toJson(updatesModel))
    editor!!.apply()
  }

  fun getInitialLoad(): List<WidgetModel>? {
    val gson = Gson()
    var initDataModel: List<WidgetModel>?
    val data = sharedPreferences.getString(INITIAL_LOAD_DATA, null)

    initDataModel =
      gson.fromJson(data, object : TypeToken<List<WidgetModel>?>() {}.type)
    return initDataModel
  }

  fun addCartItem(updatesModel: WidgetModel) {
    var items = getCartItems() as MutableList<WidgetModel>?
    if (items == null) {
      var item: MutableList<WidgetModel> = ArrayList()
      item.add(updatesModel)
      editor = sharedPreferences!!.edit()
      editor!!.putString(CART_ITEMS, Gson().toJson(item))
      editor!!.apply()
    } else {
      items.add(updatesModel)
      editor = sharedPreferences!!.edit()
      editor!!.putString(CART_ITEMS, Gson().toJson(items))
      editor!!.apply()
    }
  }

  fun setCartItem(list: List<WidgetModel>) {
    editor = sharedPreferences!!.edit()
    editor!!.putString(CART_ITEMS, Gson().toJson(list))
    editor!!.apply()
  }

  fun getCartItems(): List<WidgetModel>? {
    val gson = Gson()
    var initDataModel: List<WidgetModel>?
    val data = sharedPreferences.getString(CART_ITEMS, null)

    initDataModel =
      gson.fromJson(data, object : TypeToken<List<WidgetModel>?>() {}.type)
    return initDataModel
  }

//    var userToken: String?
//        get() = sharedPreferences.getString(USER_TOKEN_KEY, null)
//        set(value) {
//            editor = sharedPreferences.edit()
//            editor.putString(USER_TOKEN_KEY, value)
//            editor.apply()
//        }
//
//    var mobileNumber: String?
//        get() = sharedPreferences.getString(USER_MOBILE_NO, null)
//        set(value) {
//            editor = sharedPreferences.edit()
//            editor.putString(USER_MOBILE_NO, value)
//            editor.apply()
//        }
//
//    fun setMultilingualData(multilingualData: HashMap<String?, String?>?) {
//        editor = sharedPreferences.edit()
//        editor.putString(MULTI_LINGUAL_DATA, Gson().toJson(multilingualData))
//        editor.apply()
//    }
//
//    val multilingualData: HashMap<String, String>
//        get() {
//            val type =
//                object : TypeToken<HashMap<String?, String?>?>() {}.type
//            return Gson().fromJson(
//                sharedPreferences.getString(
//                    MULTI_LINGUAL_DATA,
//                    null
//                ), type
//            )
//        }
//      public List<MapItem> getMapMarkersList() {
//        Gson gson = new Gson();
//        List<MapItem> sideMenuItems = new ArrayList<>();
//        String data = sharedPreferences.getString(BOOTH_LIST, null);
//
//        sideMenuItems = gson.fromJson(data, new TypeToken<List<MapItem>>() {
//        }.getType());
//        return sideMenuItems;
//    }
//
//    public void setMapMarkersList(List<MapItem> list) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(BOOTH_LIST, new Gson().toJson(list));
//        editor.apply();
//    }
//    var mapMarkersList: List<Any?>?
//        get() {
//            val gson = Gson()
//            var sideMenuItems: List<Booths?> = ArrayList<Booths?>()
//            val data = sharedPreferences.getString(BOOTH_LIST, null)
//            sideMenuItems = gson.fromJson<List<Booths?>>(
//                data,
//                object : TypeToken<List<Booths?>?>() {}.type
//            )
//            return sideMenuItems
//        }
//
//    fun setMapMarkersList(list: List<Booths?>?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(BOOTH_LIST, Gson().toJson(list))
//        editor.apply()
//    }
//
//    fun getBoothListData(): BoothListDataModel {
//        val gson = Gson()
//        var boothListDataModel = BoothListDataModel()
//        val data = sharedPreferences.getString(BOOTH_PC_AC_LIST, null)
//        boothListDataModel =
//            gson.fromJson(data, object : TypeToken<BoothListDataModel?>() {}.type)
//        return boothListDataModel
//    }
//
//    fun setBoothListData(boothListDataModel: BoothListDataModel?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(BOOTH_PC_AC_LIST, Gson().toJson(boothListDataModel))
//        editor.apply()
//    }
//
//    fun getBoothLevelInfoData(): BoothLevelInfo {
//        val gson = Gson()
//        var boothLevelInfoDataModel = BoothLevelInfo()
//        val data = sharedPreferences.getString(BOOTH_Level_INFO_DATA, null)
//        boothLevelInfoDataModel =
//            gson.fromJson(data, object : TypeToken<BoothLevelInfo?>() {}.type)
//        return boothLevelInfoDataModel
//    }
//
//    fun setBoothLevelInfoData(boothLevelInfoDataModel: BoothLevelInfo?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(BOOTH_Level_INFO_DATA, Gson().toJson(boothLevelInfoDataModel))
//        editor.apply()
//    }
//
//    fun getPcAcData(): PcAcDataModel {
//        val gson = Gson()
//        var pcAcDataModel = PcAcDataModel()
//        val data = sharedPreferences.getString(PC_AC_DATA, null)
//        pcAcDataModel = gson.fromJson(data, object : TypeToken<PcAcDataModel?>() {}.type)
//        return pcAcDataModel
//    }
//
//    fun setPcAcData(pcAcDataModel: PcAcDataModel?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(PC_AC_DATA, Gson().toJson(pcAcDataModel))
//        editor.apply()
//    }
//
//    fun getDashboardData(): DashboardDataModel {
//        val gson = Gson()
//        var dashboardDataModel = DashboardDataModel()
//        val data = sharedPreferences.getString(DASHBOARD_DATA, null)
//        dashboardDataModel =
//            gson.fromJson(data, object : TypeToken<DashboardDataModel?>() {}.type)
//        return dashboardDataModel
//    }
//
//    fun setDashboardData(dashboardDataModel: DashboardDataModel?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(DASHBOARD_DATA, Gson().toJson(dashboardDataModel))
//        editor.apply()
//    }
//
//    fun getGuidlinesData(): GuidlinesDataModel {
//        val gson = Gson()
//        var guidlinesDataModel = GuidlinesDataModel()
//        val data = sharedPreferences.getString(GUIDELINES_DATA, null)
//        guidlinesDataModel =
//            gson.fromJson(data, object : TypeToken<GuidlinesDataModel?>() {}.type)
//        return guidlinesDataModel
//    }
//
//    fun setGuidlinesData(guidlinesDataModel: GuidlinesDataModel?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(GUIDELINES_DATA, Gson().toJson(guidlinesDataModel))
//        editor.apply()
//    }
//
//    fun getHouseCoverage(): HouseCoverageModel {
//        val gson = Gson()
//        var houseCoverageModel = HouseCoverageModel()
//        val data = sharedPreferences.getString(HOUSECOURAGE_DATA, null)
//        houseCoverageModel =
//            gson.fromJson(data, object : TypeToken<HouseCoverageModel?>() {}.type)
//        return houseCoverageModel
//    }
//
//    fun setHouseCoverage(houseCoverageModel: HouseCoverageModel?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(HOUSECOURAGE_DATA, Gson().toJson(houseCoverageModel))
//        editor.apply()
//    }
//
//    fun getAreaStatus(): AreaStatusModel {
//        val gson = Gson()
//        var areaStatusModel = AreaStatusModel()
//        val data = sharedPreferences.getString(AREA_STATUS, null)
//        areaStatusModel = gson.fromJson(data, object : TypeToken<AreaStatusModel?>() {}.type)
//        return areaStatusModel
//    }
//
//    fun setAreaStatus(areaStatusModel: String?) {
//        val editor = sharedPreferences.edit()
//        editor.putString(AREA_STATUS, areaStatusModel)
//        editor.apply()
//    }
//
//    fun logout(): Boolean {
//        editor = sharedPreferences.edit()
//        editor.clear()
//        return editor.commit()
//    }

  companion object {
    private var instance: LocalStorage? = null
    fun getInstance(context: Context): LocalStorage? {
      if (instance == null) {
        synchronized(LocalStorage::class.java) {
          if (instance == null) {
            instance = LocalStorage(context)
          }
        }
      }
      return instance
    }
  }

  init {
    sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
  }
}