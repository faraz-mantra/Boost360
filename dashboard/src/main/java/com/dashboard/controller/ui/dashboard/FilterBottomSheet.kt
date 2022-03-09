package com.dashboard.controller.ui.dashboard

import android.view.View
import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.databinding.BottomSheetFilterDateBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.*
import com.framework.utils.DateUtils.FORMAT_YYYY_MM_DD
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.DateUtils.toCalendar
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

const val BUSINESS_REPORT = "BUSINESS_REPORT"
const val WEBSITE_REPORT = "WEBSITE_REPORT"
const val MY_ENQUIRIES = "MY_ENQUIRIES"

class FilterBottomSheet : BaseBottomSheetDialog<BottomSheetFilterDateBinding, BaseViewModel>(), RecyclerItemClickListener {

  var onClicked: (filterDateModel: FilterDateModel) -> Unit = { }
  var filterDateModel: FilterDateModel? = null
  var listFilter: ArrayList<FilterDateModel>? = null
  var adapterFilter: AppBaseRecyclerViewAdapter<FilterDateModel>? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_filter_date
  }

  fun setData(filterDateModel: FilterDateModel?) {
    this.filterDateModel = filterDateModel
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnDone, binding?.btnReset)
    listFilter = FilterDateModel().getFilterDate()
    if (filterDateModel != null) listFilter?.map {
      it.isSelect = it.title.equals(filterDateModel!!.title)
    }
    else listFilter?.last()?.isSelect = true
    binding?.recyclerView?.apply {
      adapterFilter = AppBaseRecyclerViewAdapter(baseActivity, listFilter!!, this@FilterBottomSheet)
      adapter = adapterFilter
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        if (filterDateModel != null) {
          onClicked(filterDateModel!!)
          dismiss()
        }
      }
      binding?.btnReset -> {
        this.filterDateModel = listFilter?.lastOrNull()
        listFilter?.map { it.isSelect = (it.title.equals(this.filterDateModel?.title)) }
        if (adapterFilter != null) adapterFilter?.notifyDataSetChanged()
      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    this.filterDateModel = item as? FilterDateModel ?: return
    onClicked(filterDateModel!!)
    dismiss()
  }
}

const val FILTER_BUSINESS_REPORT = "FILTER_BUSINESS_REPORT"
const val FILTER_WEBSITE_REPORT = "FILTER_WEBSITE_REPORT"
const val FILTER_MY_ENQUIRIES = "FILTER_MY_ENQUIRIES"
const val TILL_DATE = "Till date"

data class FilterDateModel(
  var title: String? = null,
  var startDate: String? = null,
  var endDate: String? = null,
  var isSelect: Boolean = false,
) : Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.FILTER_DATE_VIEW.getLayout()
  }

  fun getFilterDate(): ArrayList<FilterDateModel> {
    val listFilter = ArrayList<FilterDateModel>()
    val date = getCurrentDate()
    val dateToday = date.parseDate(FORMAT_YYYY_MM_DD)
    val yesterdayDate = date.getPreviousDate(1)
    val oneWeek = date.getPreviousDate(7)
    val last30Days = date.getPreviousDate(30)
    val last90Days = date.getPreviousDate(90)
    val lastOneYear = date.getPreviousDate(365)
    listFilter.add(FilterDateModel("Yesterday", yesterdayDate, dateToday))
    listFilter.add(FilterDateModel("Last week", oneWeek, dateToday))
    listFilter.add(FilterDateModel("Last 30 days", last30Days, dateToday))
    listFilter.add(FilterDateModel("Last 90 days", last90Days, dateToday))
    listFilter.add(FilterDateModel("Last 12 months", lastOneYear, dateToday))
    listFilter.add(FilterDateModel(TILL_DATE, "", "", isSelect = true))
    return listFilter
  }

  fun getDateFilter(key: String): FilterDateModel? {
    val resp = PreferencesUtils.instance.getData(key, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData(key: String) {
    PreferencesUtils.instance.saveData(key, convertObjToString(this) ?: "")
  }
}

private fun Date.getPreviousDate(amount: Int): String? {
  val c = toCalendar()
  c?.add(Calendar.DAY_OF_MONTH, -amount)
  return c?.time?.parseDate(FORMAT_YYYY_MM_DD)
}