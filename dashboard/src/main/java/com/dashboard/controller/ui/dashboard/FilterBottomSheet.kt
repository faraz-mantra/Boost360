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
import com.framework.utils.DateUtils.FORMAT_YYYY_MM_DD
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.DateUtils.toCalendar
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class FilterBottomSheet : BaseBottomSheetDialog<BottomSheetFilterDateBinding, BaseViewModel>(), RecyclerItemClickListener {

  var onClicked: (filterDateModel: FilterDateModel) -> Unit = { }
  var filterDateModel: FilterDateModel? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_filter_date
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnDone, binding?.btnReset)
    val listFilter = getFilterDate()
    if (filterDateModel != null) listFilter.firstOrNull { it.title.equals(filterDateModel!!.title) }?.isSelect = true
    else listFilter.last().isSelect = true
    binding?.recyclerView?.apply {
      val adapterFilter = AppBaseRecyclerViewAdapter(baseActivity, listFilter, this@FilterBottomSheet)
      adapter = adapterFilter
    }
  }

  private fun getFilterDate(): ArrayList<FilterDateModel> {
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
    listFilter.add(FilterDateModel("Till date", "", ""))
    return listFilter
  }

  private fun Date.getPreviousDate(amount: Int): String? {
    val c = toCalendar()
    c?.add(Calendar.DAY_OF_MONTH, -amount)
    return c?.time?.parseDate(FORMAT_YYYY_MM_DD)
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
      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val itemFilter = item as? FilterDateModel ?: return
    this.filterDateModel = itemFilter
  }
}

data class FilterDateModel(
    var title: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var isSelect: Boolean = false,
) : Serializable, AppBaseRecyclerViewItem {
  override fun getViewType(): Int {
    return RecyclerViewItemType.FILTER_DATE_VIEW.getLayout()
  }
}