package com.inventoryorder.ui.appointmentspa.bottomsheet

import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.databinding.*
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.model.spaAppointment.bookingslot.response.Date
import com.inventoryorder.model.spaAppointment.bookingslot.response.Slots
import com.inventoryorder.model.spaAppointment.bookingslot.response.Staff
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import java.util.*

class SelectDateTimeBottomSheetDialog(val bookingSlotResponse: BookingSlotResponse, val selectedService : ServiceItem) :
        BaseBottomSheetDialog<BottomSheetSelectDateTimeBinding, BaseViewModel>(),
        RecyclerItemClickListener{

  private var staffAdapter : AppBaseRecyclerViewAdapter<Staff>? = null
  private var timeSlotsAdapter : AppBaseRecyclerViewAdapter<Slots>? = null
  private var selectedStaffPosition = -1
  private var calender : Calendar ?= null

  var onClicked: (deliveryFeeValue: Double) -> Unit = { value : Double -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_select_date_time
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel, binding?.imageAdd, binding?.imageSub)
    binding?.textServiceName?.text = selectedService?.Name
    binding?.textDuration?.text = "${selectedService?.Duration} min"
    calender = Calendar.getInstance()
    setStaffAdapter()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v) {
      binding?.imageAdd -> {
        calender?.add(Calendar.DAY_OF_YEAR, 1)

      }

      binding?.imageSub -> {
        calender?.add(Calendar.DAY_OF_YEAR, -1)
      }

      binding?.tvCancel -> {

      }

      binding?.buttonDone -> {

      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.STAFF_CLICKED.ordinal) {
      selectedStaffPosition = position
      for ((index, value) in bookingSlotResponse?.Result?.get(0)?.Staff?.withIndex()!!) {
        value.isSelected = position == index
      }
      setTimeSlotsAdapter(position)
      staffAdapter?.notifyDataSetChanged()
    }

    if (actionType == RecyclerViewActionType.TIME_SLOT_CLICKED.ordinal) {

      for ((index, value) in bookingSlotResponse?.Result?.get(0)?.Staff?.get(selectedStaffPosition)?.AppointmentSlots?.get(0)?.Slots?.withIndex()!!) {
        value.isSelected = position == index
      }
      timeSlotsAdapter?.notifyDataSetChanged()
    }
  }

  private fun setStaffAdapter() {

    if (bookingSlotResponse?.Result?.get(0)?.Staff != null && bookingSlotResponse?.Result?.get(0)?.Staff?.size!! > 0) {

      binding?.textNoStaffAvailable?.visibility = View.GONE
      binding?.recyclerStaff?.visibility = View.VISIBLE

      staffAdapter = AppBaseRecyclerViewAdapter(baseActivity, bookingSlotResponse?.Result?.get(0)?.Staff!!, this)
      binding?.recyclerStaff?.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.recyclerStaff?.adapter = staffAdapter
      binding?.recyclerStaff?.let { staffAdapter?.runLayoutAnimation(it) }
    } else {
      binding?.textNoStaffAvailable?.visibility = View.VISIBLE
      binding?.recyclerStaff?.visibility = View.GONE
    }
  }

  private fun setTimeSlotsAdapter(position: Int) {

    if (bookingSlotResponse?.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots != null &&
            bookingSlotResponse?.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots?.size!! > 0) {
      binding?.textNoSlotsAvailable?.visibility = View.GONE
      binding?.recyclerTimeSlots?.visibility = View.VISIBLE

      timeSlotsAdapter = AppBaseRecyclerViewAdapter(baseActivity, bookingSlotResponse?.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots!!, this)
      binding?.recyclerTimeSlots?.layoutManager = GridLayoutManager(baseActivity, 4)
      binding?.recyclerTimeSlots?.adapter = timeSlotsAdapter
      binding?.recyclerTimeSlots?.let { staffAdapter?.runLayoutAnimation(it) }
    } else {
      binding?.textNoSlotsAvailable?.visibility = View.VISIBLE
      binding?.recyclerTimeSlots?.visibility = View.GONE
    }
  }
}