package com.inventoryorder.ui.appointment

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils.getAmountMinDate
import com.framework.utils.DateUtils.parseDate
import com.framework.views.customViews.CustomEditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentNewAppointmentBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.doctorsData.DoctorDataResponse
import com.inventoryorder.model.orderRequest.*
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.services.InventoryServicesResponse
import com.inventoryorder.model.services.InventoryServicesResponseItem
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.item_unavailable_calendar.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class CreateAppointmentFragment : BaseInventoryFragment<FragmentNewAppointmentBinding>(), PopupMenu.OnMenuItemClickListener {

  private var selectPositionService: Int = -1
  private var serviceList: ArrayList<InventoryServicesResponseItem>? = null
  private var serviceData: InventoryServicesResponseItem? = null
  private var scheduledDateTime: String = ""
  private var orderInitiateRequest = OrderInitiateRequest()
  private val calendar = Calendar.getInstance()
  private var currentMonth = 0

  var data: PreferenceData? = null
  var doctorData: DoctorDataResponse? = null
  var isVideoConsult: Boolean = false
  var patientName: String? = null
  var startTime: String? = null
  var patientEmail: String? = null
  var patientMobile: String? = null
  var duration: String? = null


  companion object {
    fun newInstance(bundle: Bundle? = null): CreateAppointmentFragment {
      val fragment = CreateAppointmentFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    calendarView()
    data = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as PreferenceData
    // Get data and check if user's mobile is registered.
    checkPhoneNumberValid()
    binding?.radioInClinic?.isChecked = true
    binding?.radioGroup?.setOnCheckedChangeListener { _, checkedId ->
      if (checkedId == binding?.radioInClinic?.id) {
        isVideoConsult = false
      } else if (checkedId == binding?.radioVideoConsultation?.id) {
        isVideoConsult = true
      }
    }
    setOnClickListener(binding?.edtConsultingService, binding?.edtStartTime, binding?.btnCreate, binding?.edtGender)
    getServiceList()
    binding?.edtDuration?.setText("15")
  }

  private fun getServiceList() {
    viewModel?.getAllServiceList(AppConstant.CLIENT_ID, 0, data?.fpTag, InventoryServicesResponse.IdentifierType.SINGLE.name)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        serviceList = (it.arrayResponse as? Array<InventoryServicesResponseItem>)?.toCollection(ArrayList())
      } else errorUi(it.message())
    })
  }

  private fun checkPhoneNumberValid() {
    showProgress()
    viewModel?.getDoctorData(data?.fpTag)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it.anyResponse as? ArrayList<DoctorDataResponse>
        if (response?.size!! > 0) {
          doctorData = response[0]
          hideProgress()
        } else {
          hideProgress()
          errorUi("Cannot create a booking at this time. Please try later.")
        }
      } else {
        hideProgress()
        errorUi("Cannot create a booking at this time. Please try later.")
      }
    })
  }

  private fun calendarView() {
    calendar.time = Date()
    currentMonth = calendar[Calendar.MONTH]

    // Keep a track of today's date time to prohibit selection before today.
    var todayDate = Date()
    val tempCal = Calendar.getInstance()
    tempCal.time = todayDate
    tempCal.set(Calendar.HOUR_OF_DAY, 0)
    tempCal.set(Calendar.MINUTE, 0)
    tempCal.set(Calendar.SECOND, 0)
    tempCal.set(Calendar.MILLISECOND, 0)
    todayDate = tempCal.time // Set to today at 00:00:00:00

    val myCalendarViewManager = object : CalendarViewManager {
      override fun setCalendarViewResourceId(position: Int, date: Date, isSelected: Boolean): Int {
        val cal = Calendar.getInstance()
        cal.time = date // This is the date for each day displayed on the calendar view.

        if (date.before(todayDate)) {
          return R.layout.item_unavailable_calendar
        }
        return if (isSelected) {
          when (cal[Calendar.DAY_OF_WEEK]) {
            else -> R.layout.selected_calendar_item
          }
        } else {
          when (cal[Calendar.DAY_OF_WEEK]) {
            else -> R.layout.calendar_item
          }
        }
      }

      override fun bindDataToCalendarView(holder: SingleRowCalendarAdapter.CalendarViewHolder, date: Date, position: Int, isSelected: Boolean) {
        holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
        holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)
      }
    }

    val myCalendarChangesObserver = object : CalendarChangesObserver {
      override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
        // Selected date is earlier than today's date (NOT ALLOWED)
        scheduledDateTime = date.parseDate(com.framework.utils.DateUtils.FORMAT_SERVER_DATE) ?: ""
        super.whenSelectionChanged(isSelected, position, date)
      }

      override fun whenWeekMonthYearChanged(weekNumber: String, monthNumber: String, monthName: String, year: String, date: Date) {
        scheduledDateTime = ""
//                tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getYear(date)} "
        super.whenWeekMonthYearChanged(weekNumber, monthNumber, monthName, year, date)
      }
    }

    val mySelectionManager = object : CalendarSelectionManager {
      override fun canBeItemSelected(position: Int, date: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.time = date
        if (date.before(todayDate)) {
          return false
        }

        return when (cal[Calendar.DAY_OF_WEEK]) {
          else -> true
        }
      }
    }
    val singleRowCalendar = binding?.mainSingleRowCalendar?.apply {
      calendarViewManager = myCalendarViewManager
      calendarChangesObserver = myCalendarChangesObserver
      calendarSelectionManager = mySelectionManager
      pastDaysCount = 0
      futureDaysCount = 90
//      setDates(getFutureDatesOfCurrentMonth())
      init()
    }
//        btnRight.setOnClickListener { singleRowCalendar?.setDates(getDatesOfNextMonth()) }
//        btnLeft.setOnClickListener { singleRowCalendar?.setDates(getDatesOfPreviousMonth()) }
  }

  private fun consultingOnService() {
    val singleItems = this.serviceList?.map { it.name }?.toTypedArray()
    MaterialAlertDialogBuilder(baseActivity).setTitle(getString(R.string.consult_service)).setPositiveButton(getString(R.string.ok)) { d, _ ->
      serviceData = this.serviceList?.firstOrNull { it.name == singleItems?.get(selectPositionService) }
      binding?.edtConsultingService?.setText(serviceData?.name)
      binding?.edtFees?.setText(serviceData?.price()?.toString())
      d.dismiss()
    }.setNeutralButton(getString(R.string.cancel)) { d, _ ->
      d.dismiss()
    }.setSingleChoiceItems(singleItems, selectPositionService) { _, pos ->
      selectPositionService = pos
    }.show()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtConsultingService -> {
        if (serviceList.isNullOrEmpty().not()) consultingOnService()
        else showShortToast(getString(R.string.cosulting_service_not_available))
      }
      binding?.edtStartTime -> setTime(binding?.edtStartTime!!)
      binding?.edtGender -> menuItemView(v, R.menu.popup_menu_gender_selection)
      binding?.btnCreate -> if (validateAndCreateRequest()) createBooking()
    }
  }

  private fun validateAndCreateRequest(): Boolean {
    startTime = binding?.edtStartTime?.text?.toString()
    duration = binding?.edtDuration?.text?.toString()
    val consultingService = binding?.edtConsultingService?.text?.toString()
    val consultFee = binding?.edtFees?.text?.toString()
    patientMobile = binding?.edtPatientPhone?.text?.toString()
    patientName = binding?.edtPatientName?.text?.toString()
    val gender = binding?.edtGender?.text?.toString()
    val age = binding?.edtAge?.text?.toString()
    patientEmail = binding?.edtPatientEmail?.text?.toString() ?: ""


    when {
      scheduledDateTime.isNullOrEmpty() -> {
        showLongToast("Please select consultation date")
        return false
      }
      startTime.isNullOrEmpty() -> {
        showLongToast("Please select start time.")
        return false
      }
      duration.isNullOrEmpty() -> {
        showLongToast("Please select duration.")
        return false
      }
      consultingService.isNullOrEmpty() -> {
        showLongToast("Please select consulting on.")
        return false
      }
      consultFee.isNullOrEmpty() -> {
        showLongToast("Consulting fee field must not be empty.")
        return false
      }
      patientName.isNullOrEmpty() -> {
        showLongToast("Patient name field must not be empty.")
        return false
      }
      gender.isNullOrEmpty() -> {
        showLongToast("Please select gender.")
        return false
      }
      age.isNullOrEmpty() -> {
        showLongToast("Age field must not be empty.")
        return false
      }
      patientMobile.isNullOrEmpty() -> {
        showLongToast("Patient phone number field must not be empty.")
        return false
      }
      checkStringContainsDigits(patientName!!) -> {
        showLongToast("Please enter a valid patient name.")
        return false
      }

      patientMobile!!.length != 10 -> {
        showLongToast("Please enter a valid Phone Number.")
        return false
      }

      patientEmail!!.isNotEmpty() && !checkValidEmail(patientEmail!!) -> {
        showLongToast("Please enter a valid email.")
        return false
      }
      else -> {
        val paymentDetails = PaymentDetails(PaymentDetails.MethodType.FREE.name)
        val buyerDetail = BuyerDetails(address = Address(), contactDetails = ContactDetails(emailId = patientEmail!!, fullName = patientName!!, primaryContactNumber = patientMobile!!))
        val delMode = if (isVideoConsult) OrderItem.DeliveryMode.ONLINE.name else OrderSummaryRequest.DeliveryMode.OFFLINE.name
        val delProvider = if (isVideoConsult) ShippingDetails.DeliveryProvider.NF_VIDEO_CONSULATION.name else ""
        val shippingDetails = ShippingDetails(
            shippedBy = ShippingDetails.ShippedBy.SELLER.name,
            deliveryMode = delMode, deliveryProvider = delProvider, shippingCost = 0.0, currencyCode = "INR"
        )
        val items = ArrayList<ItemsItem>()
        var startTime24 = ""
        var endTime24 = ""
        try {
          startTime24 = parseDate(startTime, com.framework.utils.DateUtils.FORMAT_HH_MM_A, com.framework.utils.DateUtils.FORMAT_HH_MM) ?: ""
          val startTimeDate = startTime!!.parseDate(com.framework.utils.DateUtils.FORMAT_HH_MM_A)?.getAmountMinDate(duration!!.toInt())
          endTime24 = startTimeDate?.parseDate(com.framework.utils.DateUtils.FORMAT_HH_MM) ?: ""
        } catch (e: Exception) {
          e.printStackTrace()
        }

        val extra = doctorData?.username?.let {
          doctorData?.Id?.let { it1 ->
            doctorData?.degrees?.let { it2 ->
              doctorData?.speciality?.let { it3 ->
                doctorData?.businessLicense?.let { it4 ->
                  doctorData?.doctorsignature?.url?.let { it5 ->
                    serviceData?.pickupAddressReferenceId()?.let { it6 ->
                      serviceData?.name?.let { it7 ->
                        ExtraProperties(
                            patientName = patientName!!, gender = gender, age = age, patientMobileNumber = patientMobile!!,
                            patientEmailId = patientEmail!!, startTime = startTime24, endTime = endTime24, scheduledDateTime = scheduledDateTime, consultationFor = it7,
                            doctorName = it, doctorId = it1, doctorQualification = it2, doctorSpeciality = it3,
                            duration = duration!!.toInt(), businessLicense = it4, doctorSignature = it5,
                            referenceId = it6, businessLogo = ""
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }


        val productDetails = ProductDetails(
            id = "NO_ITEM", name = serviceData?.name, description = "NO_ITEM", currencyCode = "INR", isAvailable = serviceData?.isAvailable(),
            price = serviceData?.price(), shippingCost = 0.0, discountAmount = serviceData?.discountAmount(), extraProperties = extra
        )

        items.add(ItemsItem(type = "NO_ITEM", productOrOfferId = "NO_ITEM", quantity = 1, productDetails = productDetails))

        orderInitiateRequest.paymentDetails = paymentDetails
        orderInitiateRequest.sellerID = data?.fpTag.toString()
        orderInitiateRequest.buyerDetails = buyerDetail
        orderInitiateRequest.mode = OrderItem.OrderMode.APPOINTMENT.name
        orderInitiateRequest.shippingDetails = shippingDetails
        orderInitiateRequest.transactionCharges = 0.0
        orderInitiateRequest.gstCharges = 0.0
        orderInitiateRequest.items = items
        orderInitiateRequest.isVideoConsult = isVideoConsult
        return true
      }

    }
  }

  private fun createBooking() {
    showProgress()
    viewModel?.postOrderInitiate(AppConstant.CLIENT_ID_2, orderInitiateRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        hideProgress()
        showLongToast(getString(R.string.booking_created))
        val response = it.anyResponse as OrderInitiateResponse
        val bundle = Bundle()
        val format: DateFormat = SimpleDateFormat(com.framework.utils.DateUtils.FORMAT_SERVER_DATE, Locale.ENGLISH)
        val date: Date = format.parse(scheduledDateTime)
        bundle.putString("ORDER_ID", response.data.ReferenceNumber)
        bundle.putString("NAME", patientName)
        bundle.putString("START_TIME_DATE", date.parseDate(com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL).toString())
        bundle.putString("NUMBER", patientMobile)
        bundle.putString("EMAIL", patientEmail)
        startFragmentActivity(FragmentType.BOOKING_SUCCESSFUL, bundle)
        baseActivity.onBackPressed()
      } else {
        hideProgress()
        errorUi("Cannot create a booking at this time. Please try later.")
      }
    })
  }

  private fun menuItemView(v: View, @MenuRes menu: Int) {
    val popup = PopupMenu(baseActivity, v)
    popup.setOnMenuItemClickListener(this@CreateAppointmentFragment)
    popup.inflate(menu)
    popup.show()
  }

  private fun setTime(timePickerText: CustomEditText) {
    val calender = Calendar.getInstance()
    if (scheduledDateTime.isNullOrEmpty()) {
      showShortToast("Please select a date first")
    } else {
      val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        // Get current date and time
        val currentTime = Calendar.getInstance()
        // Get user selected date
        val selectedTime = Calendar.getInstance()
        selectedTime.time = scheduledDateTime.parseDate(com.framework.utils.DateUtils.FORMAT_SERVER_DATE)
        selectedTime.set(Calendar.HOUR_OF_DAY, hour) // Set user selected hour
        selectedTime.set(Calendar.MINUTE, minute) // Set user selected minute

        if (selectedTime.timeInMillis < currentTime.timeInMillis) {
          // User selected a time in the past
          showShortToast(getString(R.string.select_later_time_toast_string))
        } else {
          calender.set(Calendar.HOUR_OF_DAY, hour) // Set user selected hour
          calender.set(Calendar.MINUTE, minute) // Set user selected minute
          timePickerText.setText(calender.time.parseDate(com.framework.utils.DateUtils.FORMAT_HH_MM_A))
        }
      }
      TimePickerDialog(baseActivity, timeSetListener, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()
    }
  }

  private fun errorUi(message: String) {
    hideProgress()
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.male -> binding?.edtGender?.text = item.title.toString()
      R.id.female -> binding?.edtGender?.text = item.title.toString()
      else -> false
    }
    return false
  }

  private fun checkStringContainsDigits(input: String): Boolean {
    return Pattern.compile("[0-9]").matcher(input).find()
  }

  private fun checkValidEmail(email: String): Boolean {
    return Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matcher(email).find()
  }

}
