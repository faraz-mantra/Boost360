package com.inventoryorder.ui.appointmentSpa.create

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.DateUtils
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.BottomSheetSelectDateTimeBinding
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.model.spaAppointment.bookingslot.request.AppointmentRequestModel
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.request.DateRange
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.model.spaAppointment.bookingslot.response.Slots
import com.inventoryorder.model.spaAppointment.bookingslot.response.Staff
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class SelectDateTimeBottomSheetDialog(
    private var bookingSlotResponse: BookingSlotResponse,
    private var selectedService: ServiceItem,
    private var dateCounter: Int,
    private var dateChange: DateChangedListener,
) : BaseBottomSheetDialog<BottomSheetSelectDateTimeBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var staffAdapter: AppBaseRecyclerViewAdapter<Staff>? = null
  private var timeSlotsAdapter: AppBaseRecyclerViewAdapter<Slots>? = null
  private var selectedStaffPosition = 0
  private var dateChangedListener: DateChangedListener? = null
  private var selectedTimeSlot: Slots? = null

  var onClicked: (appointmentRequestModel: AppointmentRequestModel, dateCounter: Int) -> Unit = { _: AppointmentRequestModel, _: Int -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_select_date_time
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel, binding?.imageNext, binding?.imagePrev)
    binding?.textServiceName?.text = selectedService.Name
    binding?.textDuration?.text = "${selectedService.Duration} min"
    binding?.textSelectedDate?.text = getDisplayDate(getDateTime())
    dateChangedListener = dateChange

    setStaffAdapter()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.imageNext -> {
        dateCounter++
        binding?.textSelectedDate?.text = getDisplayDate(getDateTime())
        dateChangedListener?.onDateChanged(getSlotsAndStaff())
        binding?.imagePrev?.setImageResource(R.drawable.ic_filled_arrow_left)
      }

      binding?.imagePrev -> {
        if (dateCounter == 0) {
          binding?.imagePrev?.setImageResource(R.drawable.ic_filled_arrow_left_gray)
        } else {
          binding?.imagePrev?.setImageResource(R.drawable.ic_filled_arrow_left)
          if (dateCounter == 1) binding?.imagePrev?.setImageResource(R.drawable.ic_filled_arrow_left_gray)
          dateCounter--
          binding?.textSelectedDate?.text = getDisplayDate(getDateTime())
          dateChangedListener?.onDateChanged(getSlotsAndStaff())
        }
      }

      binding?.tvCancel -> {
        dismiss()
      }

      binding?.buttonDone -> {

        if (selectedTimeSlot == null) {
          showShortToast(getString(R.string.please_select_time_slot))
          return
        }

        dismiss()
        prepareModelAndClose()
      }
    }
  }

  private fun prepareModelAndClose() {
    val appointmentRequestModel = AppointmentRequestModel()
    appointmentRequestModel._id = selectedTimeSlot?._id
    appointmentRequestModel.startTime = selectedTimeSlot?.StartTime
    appointmentRequestModel.endTime = selectedTimeSlot?.EndTime
    appointmentRequestModel.duration = selectedService.Duration?.toString()
    appointmentRequestModel.scheduledDateTime = getDateTime()
    appointmentRequestModel.staffId = if (bookingSlotResponse.Result?.get(0)?.Staff?.get(selectedStaffPosition)?.Name.equals("anybody", true)) null
    else bookingSlotResponse.Result?.get(0)?.Staff?.get(selectedStaffPosition)?._id

    appointmentRequestModel.staffName = bookingSlotResponse.Result?.get(0)?.Staff?.get(selectedStaffPosition)?.Name

    onClicked(appointmentRequestModel, dateCounter)
  }

  private fun getSlotsAndStaff(): BookingSlotsRequest {
    return BookingSlotsRequest(BatchType = "DAILY", ServiceId = selectedService._id ?: "", DateRange = DateRange(StartDate = getDateTime(), EndDate = getDateTime()))
  }

  fun setData(
      bookingSlotResp: BookingSlotResponse,
      selected: ServiceItem,
  ) {
    bookingSlotResponse = bookingSlotResp
    selectedService = selected
    setStaffAdapter()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.STAFF_CLICKED.ordinal) {
      selectedStaffPosition = position
      selectedTimeSlot = null
      for ((index, value) in bookingSlotResponse.Result?.get(0)?.Staff?.withIndex()!!) {
        value.isSelected = position == index
      }
      setTimeSlotsAdapter(position)
      staffAdapter?.notifyDataSetChanged()
    }

    if (actionType == RecyclerViewActionType.TIME_SLOT_CLICKED.ordinal) {
      for ((index, value) in bookingSlotResponse.Result?.get(0)?.Staff?.get(selectedStaffPosition)?.AppointmentSlots?.get(0)?.Slots?.withIndex()!!) {
        value.isSelected = position == index
        if (position == index) selectedTimeSlot = value
      }
      timeSlotsAdapter?.notifyDataSetChanged()
    }
  }

  private fun setStaffAdapter() {

    if (bookingSlotResponse.Result?.get(0)?.Staff != null && bookingSlotResponse.Result?.get(0)?.Staff?.size!! > 0) {

      var isPopulated = false
      binding?.textNoStaffAvailable?.visibility = View.GONE
      binding?.recyclerStaff?.visibility = View.VISIBLE

      staffAdapter = AppBaseRecyclerViewAdapter(baseActivity, bookingSlotResponse.Result?.get(0)?.Staff!!, this)
      binding?.recyclerStaff?.layoutManager = LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.recyclerStaff?.adapter = staffAdapter
      binding?.recyclerStaff?.let { staffAdapter?.runLayoutAnimation(it) }

      /*for ((index, value) in bookingSlotResponse?.Result?.get(0)?.Staff?.withIndex()!!) {
        value.isSelected = selectedStaffPosition == index
      }*/

      staffAdapter?.notifyDataSetChanged()
      for ((index, value) in bookingSlotResponse.Result?.get(0)?.Staff!!.withIndex()) {
        if (value.isSelected) {
          isPopulated = true
          setTimeSlotsAdapter(index)
        }
      }

      if (!isPopulated) setTimeSlotsAdapter(0)

    } else {
      binding?.textNoStaffAvailable?.visibility = View.VISIBLE
      binding?.recyclerStaff?.visibility = View.GONE
    }
  }

  private fun setTimeSlotsAdapter(position: Int) {
    if (bookingSlotResponse.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots != null &&
        bookingSlotResponse.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots?.size!! > 0) {
      binding?.textNoSlotsAvailable?.visibility = View.GONE
      binding?.recyclerTimeSlots?.visibility = View.VISIBLE

      timeSlotsAdapter = AppBaseRecyclerViewAdapter(baseActivity, bookingSlotResponse.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots!!, this)
      binding?.recyclerTimeSlots?.layoutManager = GridLayoutManager(baseActivity, 4)
      binding?.recyclerTimeSlots?.adapter = timeSlotsAdapter
      binding?.recyclerTimeSlots?.let { staffAdapter?.runLayoutAnimation(it) }

      for (item in bookingSlotResponse.Result?.get(0)?.Staff?.get(position)?.AppointmentSlots?.get(0)?.Slots!!) {
        if (item.isSelected) selectedTimeSlot = item
      }
    } else {
      binding?.textNoSlotsAvailable?.visibility = View.VISIBLE
      binding?.recyclerTimeSlots?.visibility = View.GONE
    }
  }

  private fun getDateTime(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, dateCounter)
    val df = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
    return df.format(cal.time)
  }

  private fun getDisplayDate(date: String): String {
    val currentDateFormat = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
    val displayDateFormat = SimpleDateFormat(DateUtils.SPA_DISPLAY_DATE, Locale.getDefault())

    val currentDate = currentDateFormat.parse(date)
    return displayDateFormat.format(currentDate!!)
  }

  interface DateChangedListener {
    fun onDateChanged(bookingSlotRequest: BookingSlotsRequest)
  }
}