package com.inventoryorder.ui.appointment.createAptConsult

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.firestore.FirestoreManager
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_1
import com.framework.utils.DateUtils.FORMAT_YYYY_MM_DD
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.DateUtils.toCalendar
import com.framework.utils.ValidationUtils.isMobileNumberValid
import com.framework.views.customViews.CustomEditText
import com.framework.webengageconstant.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentNewAppointmentBinding
import com.inventoryorder.model.AUTHORIZATION_3
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.apointmentData.AptData
import com.inventoryorder.model.apointmentData.DoctorAppointmentResponse
import com.inventoryorder.model.apointmentData.addRequest.ActionData
import com.inventoryorder.model.apointmentData.addRequest.AddAptConsultRequest
import com.inventoryorder.model.apointmentData.addRequest.CustomerInfo
import com.inventoryorder.model.apointmentData.updateRequest.SetField
import com.inventoryorder.model.apointmentData.updateRequest.UpdateConsultField
import com.inventoryorder.model.apointmentData.updateRequest.UpdateConsultRequest
import com.inventoryorder.model.doctorsData.DoctorDataResponse
import com.inventoryorder.model.orderRequest.*
import com.inventoryorder.model.ordersdetails.ExtraPropertiesN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersdetails.ProductN
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.services.InventoryServicesResponse
import com.inventoryorder.model.services.InventoryServicesResponseItem
import com.inventoryorder.model.timeSlot.TimeSlotData
import com.inventoryorder.model.weeklySchedule.DataItem
import com.inventoryorder.model.weeklySchedule.GetDoctorWeeklySchedule
import com.inventoryorder.model.weeklySchedule.isTimeBetweenTwoHours
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.bottomsheet.TimeSlotBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.item_unavailable_calendar.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class CreateAppointmentFragment : BaseInventoryFragment<FragmentNewAppointmentBinding>(), PopupMenu.OnMenuItemClickListener {

  //TODO update value
  private var orderItem: OrderItem? = null
  private val product: ProductN?
    get() {
      return orderItem?.firstItemForAptConsult()?.product()
    }
  private val extraItemConsult: ExtraPropertiesN?
    get() {
      return product?.extraItemProductConsultation()
    }
  private var aptData: AptData? = null
  private var updateExtraPropertyRequest: UpdateExtraPropertyRequest? = null
  private var isUpdate: Boolean = false
  //TODO update value

  private var selectPositionService: Int = -1
  private var selectPositionDoctor: Int = 0
  private var serviceList: ArrayList<InventoryServicesResponseItem>? = null
  private var serviceData: InventoryServicesResponseItem? = null
  private var scheduledDateTime: String = ""
  private var orderInitiateRequest = OrderInitiateRequest()
  private val calendar = Calendar.getInstance()
  private var currentMonth = 0

  var session: PreferenceData? = null
  var doctorDataList: ArrayList<DoctorDataResponse>? = null
  var doctorData: DoctorDataResponse? = null
  var isVideoConsult: Boolean = false
  var patientName: String? = null

  //  var startTime: String? = null
  var patientEmail: String? = null
  var patientMobile: String? = null
  var duration: String? = null
  var doctorWeeklySchedule: ArrayList<DataItem>? = null
  var timeSlotList = ArrayList<TimeSlotData>()
  var timeSlotData: TimeSlotData? = null

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
    session = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as? PreferenceData
    orderItem = arguments?.getSerializable(IntentConstant.ORDER_ITEM.name) as? OrderItem
    isUpdate = (orderItem != null)
    setToolbarTitle(getString(if (isUpdate) R.string.update_apt_consult else R.string.new_apppointment_camel_case))

    // Remove video consultation based on experience code.
    if (session?.experienceCode == "DOC" || session?.experienceCode == "HOS") binding?.radioVideoConsultation?.isVisible = true
    isVideoConsult = arguments?.getBoolean(IntentConstant.IS_VIDEO.name) ?: false
    if (isVideoConsult) binding?.radioVideoConsultation?.isChecked = true else binding?.radioInClinic?.isChecked = true

    binding?.radioGroup?.setOnCheckedChangeListener { _, checkedId ->
      if (checkedId == binding?.radioInClinic?.id) isVideoConsult = false
      else if (checkedId == binding?.radioVideoConsultation?.id) isVideoConsult = true
    }
    setOnClickListener(binding?.edtConsultingService, binding?.edtDoctor, binding?.edtStartTime, binding?.btnCreate, binding?.edtGender)
    getDoctorDetailApi()
  }

  private fun getDoctorDetailApi() {
    showProgress()
    viewModel?.getDoctorData(session?.fpTag)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it.anyResponse as? ArrayList<DoctorDataResponse>
        if (response.isNullOrEmpty().not()) {
          doctorDataList = response
          doctorData = if (isUpdate) response?.firstOrNull { data -> data.Id == extraItemConsult?.doctorId } else response?.get(0)
          setDatDoctor()
          getServiceList()
        } else errorUi(getErrorMessage())
      } else errorUi(getErrorMessage())
    })
  }

  private fun setDatDoctor() {
    val mobile = doctorData?.mobile?.replace("+91", "")?.trim()
    if (isMobileNumberValid(mobile ?: "")) session?.userPrimaryMobile = mobile
    binding?.edtDuration?.setText(doctorData?.duration)
    binding?.edtDoctor?.setText(doctorData?.username)
  }

  private fun getErrorMessage(): String {
    return when (session?.experienceCode) {
      "DOC",
      "HOS",
      -> resources.getString(R.string.please_add_doctor_first)
      "EDU" -> getString(R.string.please_add_teacher_first)
      "SPA" -> getString(R.string.masseur_masseuse_not_added)
      "HOT" -> getString(R.string.please_add_hotel_room_first)
      "CAF" -> getString(R.string.please_add_table_first)
      "SAL" -> getString(R.string.please_add_barber_first)
      "MFG" -> getString(R.string.please_add_commodity_first)
      "RTL" -> getString(R.string.please_add_retail_commodity_first)
      "SVC" -> getString(R.string.please_add_service_first)
      else -> ""
    }
  }

  private fun getServiceList() {
    viewModel?.getAllServiceList(AppConstant.CLIENT_ID, 0, session?.fpTag, InventoryServicesResponse.IdentifierType.SINGLE.name)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val resp = (it.arrayResponse as? Array<InventoryServicesResponseItem>)
        serviceList = if (resp.isNullOrEmpty().not()) resp?.toCollection(ArrayList()) else ArrayList()
        serviceList?.add(InventoryServicesResponseItem().getGeneralData())
        if (isUpdate) {
          serviceData = this.serviceList?.firstOrNull { service -> service.id == product?._id }
          selectDatServiceDataSet()
          updateUiConsult()
        }
        getWeeklyScheduleList(doctorData?.Id ?: "")
      } else errorUi(it.message())
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
        scheduledDateTime = date.parseDate(FORMAT_SERVER_DATE) ?: ""
        super.whenSelectionChanged(isSelected, position, date)
        if (isSelected) {
          showProgress()
          getAllAptConsultDoctor(doctorData?.Id, date.parseDate(FORMAT_YYYY_MM_DD), DateUtils.getDayName(date))
        }
      }

      override fun whenWeekMonthYearChanged(weekNumber: String, monthNumber: String, monthName: String, year: String, date: Date) {
//        scheduledDateTime = ""
        binding?.tvMonthDateRange?.text = "$monthName, $year"
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
      init()
//      setDates(getFutureDatesOfCurrentMonth())
    }
//        btnRight.setOnClickListener { singleRowCalendar?.setDates(getDatesOfNextMonth()) }
//        btnLeft.setOnClickListener { singleRowCalendar?.setDates(getDatesOfPreviousMonth()) }
  }

  private fun consultingOnService() {
    val singleItems = this.serviceList?.map { it.name }?.toTypedArray()
    MaterialAlertDialogBuilder(baseActivity).setTitle(getString(R.string.consult_service)).setPositiveButton(getString(R.string.ok)) { d, _ ->
      serviceData = this.serviceList?.firstOrNull { it.name == singleItems?.get(selectPositionService) }
      selectDatServiceDataSet()
      d.dismiss()
    }.setNeutralButton(getString(R.string.cancel)) { d, _ ->
      d.dismiss()
    }.setSingleChoiceItems(singleItems, selectPositionService) { _, pos ->
      selectPositionService = pos
    }.show()
  }

  private fun selectDatServiceDataSet() {
    serviceData?.let {
      binding?.edtConsultingService?.setText(it.name)
      binding?.edtFees?.setText(it.discountedPrice().toString())
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtDoctor -> if (isUpdate.not()) doctorListDialog()
      binding?.edtConsultingService -> {
        if ((isUpdate && serviceData != null).not()) {
          if (serviceList.isNullOrEmpty().not()) consultingOnService()
          else showShortToast(getString(R.string.cosulting_service_not_available))
        }
      }
      binding?.edtStartTime -> {
        //setTime(binding?.edtStartTime!!)
        when {
          scheduledDateTime.isEmpty() -> showLongToast(getString(R.string.first_select_consultation_date))
          timeSlotList.isEmpty() -> showLongToast(getString(R.string.time_slot_not_available))
          else -> timeSlotBottomSheet()
        }
      }
      binding?.edtGender -> if (isUpdate.not()) menuItemView(v, R.menu.popup_menu_gender_selection)
      binding?.btnCreate -> {
        if (validateAndCreateRequest()) {
          if (isUpdate) updateBooking() else createBooking()
        }
      }
    }
  }

  private fun doctorListDialog() {
    val singleItems = this.doctorDataList?.map { it.username }?.toTypedArray()
    MaterialAlertDialogBuilder(baseActivity).setTitle(getString(R.string.select_doctor)).setPositiveButton(getString(R.string.ok)) { d, _ ->
      doctorData = this.doctorDataList?.firstOrNull { it.username == singleItems?.get(selectPositionDoctor) }
      changeDoctor()
      d.dismiss()
    }.setNeutralButton(getString(R.string.cancel)) { d, _ ->
      d.dismiss()
    }.setSingleChoiceItems(singleItems, selectPositionDoctor) { _, pos ->
      selectPositionDoctor = pos
    }.show()
  }

  private fun changeDoctor() {
    serviceData = null
    timeSlotData = null
    binding?.edtConsultingService?.setText("")
    binding?.edtFees?.setText("")
    binding?.edtStartTime?.setText("")
    setDatDoctor()
    getServiceList()
  }

  private fun validateAndCreateRequest(): Boolean {
    duration = binding?.edtDuration?.text?.toString()
    val consultingService = binding?.edtConsultingService?.text?.toString()
    val consultFee = binding?.edtFees?.text?.toString()
    patientMobile = binding?.edtPatientPhone?.text?.toString()
    patientName = binding?.edtPatientName?.text?.toString()
    val gender = binding?.edtGender?.text?.toString()
    val age = binding?.edtAge?.text?.toString()
    patientEmail = binding?.edtPatientEmail?.text?.toString() ?: ""

    when {
      scheduledDateTime.isEmpty() -> {
        showLongToast(getString(R.string.please_select_consultation_date))
        return false
      }
      timeSlotList.isEmpty() -> {
        showLongToast(getString(R.string.time_slot_not_available))
        return false
      }
      timeSlotData == null -> {
        showLongToast(resources.getString(R.string.please_select_time_slot))
        return false
      }
      consultingService.isNullOrEmpty() -> {
        showLongToast(getString(R.string.please_select_consulting_on))
        return false
      }
      patientName.isNullOrEmpty() -> {
        showLongToast(getString(R.string.patient_name_field_must_be_empty))
        return false
      }
      gender.isNullOrEmpty() -> {
        showLongToast(resources.getString(R.string.please_select_gender))
        return false
      }
      age.isNullOrEmpty() -> {
        showLongToast(getString(R.string.age_field_must_not_be_empty))
        return false
      }
      patientMobile.isNullOrEmpty() -> {
        showLongToast(getString(R.string.patient_phone_number_field_must_not_be_empty))
        return false
      }
      checkStringContainsDigits(patientName!!) -> {
        showLongToast(getString(R.string.please_enter_valid_patient_name))
        return false
      }

      patientMobile!!.length != 10 -> {
        showLongToast(getString(R.string.please_enter_valid_phone_number))
        return false
      }

      patientEmail!!.isNotEmpty() && !checkValidEmail(patientEmail!!) -> {
        showLongToast(resources.getString(R.string.please_enter_valid_email))
        return false
      }
      else -> {
        var startTime24 = ""
        var endTime24 = ""
        try {
          startTime24 = parseDate(timeSlotData?.startTime, com.framework.utils.DateUtils.FORMAT_HH_MM_A, com.framework.utils.DateUtils.FORMAT_HH_MM) ?: ""
          endTime24 = parseDate(timeSlotData?.endTime, com.framework.utils.DateUtils.FORMAT_HH_MM_A, com.framework.utils.DateUtils.FORMAT_HH_MM) ?: ""
        } catch (e: Exception) {
          e.printStackTrace()
        }
        val extra = ExtraProperties(patientName = patientName!!, gender = gender, age = age, patientMobileNumber = patientMobile!!,
            patientEmailId = patientEmail!!, startTime = startTime24, endTime = endTime24, scheduledDateTime = scheduledDateTime, consultationFor = serviceData?.name ?: "",
            doctorName = doctorData?.username ?: "", doctorId = doctorData?.Id ?: "", doctorQualification = doctorData?.degrees ?: "", doctorSpeciality = doctorData?.speciality ?: "",
            duration = duration?.toIntOrNull() ?: 0, businessLicense = doctorData?.businessLicense ?: "", doctorSignature = doctorData?.doctorsignature?.url ?: "",
            referenceId = serviceData?.pickupAddressReferenceId() ?: "", businessLogo = "")

        if (isUpdate) {
          updateExtraPropertyRequest = UpdateExtraPropertyRequest(extraProperties = extra, orderId = orderItem?._id, updateExtraPropertyType = UpdateExtraPropertyRequest.PropertyType.ITEM.name)
        } else {
          val method = if (serviceData?.discountedPrice() == 0.0) PaymentDetailsN.METHOD.FREE.type else PaymentDetailsN.METHOD.COD.type
          val paymentDetails = PaymentDetails(method)
          val buyerDetail = BuyerDetails(address = Address(), contactDetails = ContactDetails(emailId = patientEmail!!, fullName = patientName!!, primaryContactNumber = patientMobile!!))
          val delMode = if (isVideoConsult) OrderItem.DeliveryMode.ONLINE.name else OrderSummaryRequest.DeliveryMode.OFFLINE.name
          val delProvider = if (isVideoConsult) ShippingDetails.DeliveryProvider.NF_VIDEO_CONSULATION.name else ""
          val shippingDetails = ShippingDetails(shippedBy = ShippingDetails.ShippedBy.SELLER.name, deliveryMode = delMode, deliveryProvider = delProvider, shippingCost = 0.0, currencyCode = "INR")
          val items = ArrayList<ItemsItem>()

          val productDetails = ProductDetails(id = serviceData?.id ?: "NO_ITEM", name = serviceData?.name ?: "NO_ITEM", description = serviceData?.description ?: "NO_ITEM",
              currencyCode = "INR", isAvailable = serviceData?.isAvailable(), price = serviceData?.discountedPrice(), shippingCost = 0.0, discountAmount = serviceData?.discountAmount(),
              extraProperties = extra, imageUri = serviceData?.imageUri ?: "")

          items.add(ItemsItem(type = serviceData?.getType() ?: "NO_ITEM", productOrOfferId = serviceData?.id ?: "NO_ITEM", quantity = 1, productDetails = productDetails))

          orderInitiateRequest.paymentDetails = paymentDetails
          orderInitiateRequest.sellerID = session?.fpTag
          orderInitiateRequest.buyerDetails = buyerDetail
          orderInitiateRequest.mode = OrderItem.OrderMode.APPOINTMENT.name
          orderInitiateRequest.shippingDetails = shippingDetails
          orderInitiateRequest.transactionCharges = 0.0
          orderInitiateRequest.gstCharges = 0.0
          orderInitiateRequest.items = items
          orderInitiateRequest.isVideoConsult = isVideoConsult
        }
        return true
      }
    }
  }

  private fun updateBooking() {
    showProgress()
    viewModel?.updateExtraPropertyOrder(AppConstant.CLIENT_ID_2, request = updateExtraPropertyRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.isSuccess()) {
        WebEngageController.trackEvent(if (isVideoConsult) CONSULATION_UPDATED else APPOINTMENT_UPDATED, ADDED, TO_BE_ADDED)
        hitApiUpdateAptConsult(updateExtraPropertyRequest?.extraProperties)
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.can_not_reshedule_your_booking_at_this_time))
      }
    })
  }

  private fun hitApiUpdateAptConsult(updateExtra: ExtraProperties?) {
    val request = UpdateConsultRequest()
    request.setQueryData(aptData?.id)
    val dateTimeSlot = "#${updateExtra?.getScheduledDateN()}#${updateExtra?.startTime()},${updateExtra?.endTime()}#"
    val setField = SetField(bookingRef = orderItem?._id, dateTimeSlot = dateTimeSlot, doctorId = doctorData?.Id, serviceId = serviceData?.id ?: "NO_ITEM")
    setField.setCustomerInfo(CustomerInfo(emailId = updateExtra?.patientEmailId, name = updateExtra?.patientName, mobileNumber = updateExtra?.patientMobileNumber))
    request.setUpdateValueAll(UpdateConsultField(setField))
    viewModel?.updateAptConsultData(AUTHORIZATION_3,request)?.observeOnce(viewLifecycleOwner, {
      showLongToast("Your booking is rescheduled successfully.")
      val intent = Intent()
      intent.putExtra(IntentConstant.ORDER_ID.name, orderItem?._id)
      baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
      baseActivity.finish()
      hideProgress()
    })
  }

  private fun createBooking() {
    showProgress()
    viewModel?.postOrderInitiate(AppConstant.CLIENT_ID_2, orderInitiateRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.isSuccess()) {
        WebEngageController.trackEvent(if (isVideoConsult) CONSULATION_CREATE else APPOINTMENT_CREATE, ADDED, TO_BE_ADDED)
        onInClinicAptConsultAddedOrUpdated(true);
        hitApiAddAptConsult((it as? OrderInitiateResponse)?.data)
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.can_not_reshedule_your_booking_at_this_time))
      }
    })
  }

  private fun hitApiAddAptConsult(response: OrderItem?) {
    val item = response?.firstItemForAptConsult()
    val itemExtra = item?.product()?.extraItemProductConsultation()

    val customerInfo = CustomerInfo(emailId = response?.BuyerDetails?.ContactDetails?.EmailId, name = response?.BuyerDetails?.ContactDetails?.FullName,
        mobileNumber = response?.BuyerDetails?.ContactDetails?.PrimaryContactNumber)

    val dateTimeSlot = "#${item?.getScheduledDate()}#${itemExtra?.startTime()},${itemExtra?.endTime()}#"

    val actionData = ActionData(bookingRef = response?._id, doctorId = doctorData?.Id, serviceId = serviceData?.id,
        category = "General", status = "booked", notes = "", dateTimeSlot = dateTimeSlot)

    actionData.setCustomerInfo(customerInfo)

    val request = AddAptConsultRequest(actionData = actionData, websiteId = session?.fpTag)

    viewModel?.addAptConsultData(AUTHORIZATION_3,request)?.observeOnce(viewLifecycleOwner, {
      val scheduleDate = item?.scheduledStartDate()
      val dateApt = parseDate(scheduleDate, FORMAT_SERVER_DATE, com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2)
      startSuccessScreen(response, dateApt)
      //TODO SMS and Email is coming from backend
//      val dur = item?.Product?.extraItemProductConsultation()?.durationTxt() ?: "0 Minute"
//
//      val messageForDoctor = if (isVideoConsult) "New appointment confirmed:\n \n${itemExtra?.patientName ?: "New User"} has been booked for $dateApt with test. video, ${itemExtra?.patientMobileNumber}.\nClick here ${response?.consultationWindowUrlForDoctor()}"
//      else "Hi ${itemExtra?.doctorName}, you have a new appointment scheduled on $dateApt. You will receive a mail with the booking details shortly. For any queries, contact 1860-123-1233."
//
//      val messageForClient = if (isVideoConsult) "Your appointment details:\n\n${itemExtra?.consultationFor} with ${itemExtra?.doctorName ?: ""} for $dateApt. Click here ${response?.consultationWindowUrlForPatient()}\n\nContact: ${session?.userPrimaryMobile}"
//      else "Dear ${itemExtra?.patientName ?: "sir"}, your appointment with ${itemExtra?.doctorName ?: ""} is confirmed for $dateApt. You will receive a mail with the booking details shortly. Please reach the centre $dur before the scheduled time. For any queries, contact ${session?.userPrimaryMobile}."
//
//      val numberPatient = itemExtra?.patientMobileNumber?.replace("+91", "")?.trim()
//      val numberDoctor = session?.userPrimaryMobile?.replace("+91", "")?.trim()
//      when {
//        isMobileNumberValid(numberPatient ?: "") -> {
//          viewModel?.sendSMS(itemExtra?.patientMobileNumber, messageForClient, CLIENT_ID_3)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//            if (isMobileNumberValid(numberDoctor ?: "")) {
//              viewModel?.sendSMS(numberDoctor, messageForDoctor, CLIENT_ID_3)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//                sendMailUserAndDoctor(response, dateApt)
//              })
//            } else sendMailUserAndDoctor(response, dateApt)
//          })
//        }
//        isMobileNumberValid(numberDoctor ?: "") -> {
//          viewModel?.sendSMS(numberDoctor, messageForDoctor, CLIENT_ID_3)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//            sendMailUserAndDoctor(response, dateApt)
//          })
//        }
//        else -> sendMailUserAndDoctor(response, dateApt)
//      }
    })
  }

  //TODO SMS and Email is coming from backend
//  private fun sendMailUserAndDoctor(response: OrderItem?, dateApt: String?) {
//    val item = response?.firstItemForConsultation()
//    val itemExtra = item?.product()?.extraItemProductConsultation()
//
//    val requestDoctor = if (isEmailValid(session?.emailDoctor ?: "")) {
//      if (isVideoConsult) {
//        val htmLBody = getConsultHtmlTextForDoctor(response?.SellerDetails?.ContactDetails?.FullName, itemExtra, dateApt, response?.consultationWindowUrlForDoctor())
//        val subject = "\uD83C\uDF10 New Online Video Consultation ID ${response?.ReferenceNumber} received on $dateApt • ${itemExtra?.patientName ?: ""}"
//        SendMailRequest(session?.emailDoctor, htmLBody, subject, "0")
//      } else {
//        val htmLBody = getAptHtmlTextForDoctor(response?.SellerDetails?.ContactDetails?.FullName, itemExtra, dateApt)
//        val subject = "\uD83D\uDDD3️ Appointment Consultation ID ${response?.ReferenceNumber} received on $dateApt • ${itemExtra?.patientName}"
//        SendMailRequest(session?.emailDoctor, htmLBody, subject, "0")
//      }
//    } else null
//
//    val emailUser = itemExtra?.patientEmailId ?: ""
//    val requestUser = if (isEmailValid(emailUser)) {
//      if (isVideoConsult) {
//        val htmLBody = getConsultHtmlTextForUser(session?.userPrimaryMobile, emailUser, session?.webSiteUrl, itemExtra, dateApt, response?.consultationWindowUrlForPatient(), session?.emailDoctor)
//        val subject = "\uD83C\uDF10 Online Video Consultation ID ${response?.ReferenceNumber} Confirmation: $dateApt • ${response?.SellerDetails?.ContactDetails?.FullName}"
//        SendMailRequest(emailUser, htmLBody, subject, "0")
//      } else {
//        val htmLBody = getAptHtmlForUser(response?.SellerDetails?.Address?.addressLine1(), session?.latitude, session?.longitude,
//            session?.userPrimaryMobile, emailUser, session?.webSiteUrl, itemExtra, dateApt, session?.emailDoctor)
//        val subject = "\uD83D\uDDD3️ New Appointment Consultation ID ${response?.ReferenceNumber} Confirmation: $dateApt • ${response?.SellerDetails?.ContactDetails?.FullName ?: ""}"
//        SendMailRequest(emailUser, htmLBody, subject, "0")
//      }
//    } else null
//
//    when {
//      requestDoctor != null -> {
//        viewModel?.sendMail(requestDoctor)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//          if (requestUser != null) {
//            viewModel?.sendMail(requestUser)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//              startSuccessScreen(response, dateApt)
//            })
//          } else startSuccessScreen(response, dateApt)
//        })
//      }
//      requestUser != null -> {
//        viewModel?.sendMail(requestUser)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
//          startSuccessScreen(response, dateApt)
//        })
//      }
//      else -> startSuccessScreen(response, dateApt)
//    }
//  }

  private fun startSuccessScreen(response: OrderItem?, dateApt: String?) {
    hideProgress()
    showLongToast(getString(R.string.booking_created))
    val bundle = Bundle()
    bundle.putString("ORDER_ID", response?.ReferenceNumber)
    bundle.putString("NAME", patientName)
    bundle.putString("SERVICE_NAME", serviceData?.name)
    bundle.putString("START_TIME_DATE", dateApt)
    bundle.putString("NUMBER", patientMobile)
    bundle.putString("EMAIL", patientEmail)
    startFragmentOrderActivity(FragmentType.BOOKING_SUCCESSFUL, bundle, isResult = true)
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val intent = Intent()
      intent.putExtra(IntentConstant.RESULT_DATA.name, Bundle().apply { putBoolean(IntentConstant.IS_REFRESH.name, true) })
      baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
      baseActivity.finish()
    }
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
      showShortToast(getString(R.string.please_select_date_first))
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

  private fun getWeeklyScheduleList(doctorId: String) {
    val requestQuery = "{\$and:[{WebsiteId: \'${preferenceData?.fpTag}\'}, {doctorId: \'${doctorId}\'}]}"
    viewModel?.getWeeklyScheduleList(AUTHORIZATION_3, requestQuery)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val resp = it as? GetDoctorWeeklySchedule
        if (resp?.data.isNullOrEmpty().not()) {
          doctorWeeklySchedule = resp?.data
          if (isUpdate) getAptConsultDoctor() else hideProgress()
        } else errorUi(getString(R.string.doctor_weekly_schedule_not_available))
      } else errorUi(it.message())
    })
  }

  private fun getAptConsultDoctor() {
    val dateTimeSlot = orderItem?.firstItemForAptConsult()?.getScheduledDate()
    val requestQuery = "{\$and:[{WebsiteId: \'${preferenceData?.fpTag}\'}, {doctorId: \'${doctorData?.Id}\'}, {status: {\$ne: 'cancelled'}}, {dateTimeSlot: /$dateTimeSlot/}]}"
    viewModel?.getAllAptConsultDoctor(AUTHORIZATION_3,requestQuery)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        aptData = (it as? DoctorAppointmentResponse)?.data?.firstOrNull { apt -> apt.bookingRef == orderItem?._id }
        hideProgress()
      } else errorUi(it.message())
    })
  }

  private fun getAllAptConsultDoctor(doctorId: String?, dateTimeSlot: String?, day: String) {
    timeSlotList = ArrayList()
    binding?.edtStartTime?.hint = resources.getString(R.string.please_select_time_slot)
    this.timeSlotData = null
    val requestQuery = "{\$and:[{WebsiteId: \'${preferenceData?.fpTag}\'}, {doctorId: \'${doctorId}\'}, {status: {\$ne: 'cancelled'}}, {dateTimeSlot: /$dateTimeSlot/}]}"
    viewModel?.getAllAptConsultDoctor(AUTHORIZATION_3, requestQuery)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val resp = it as? DoctorAppointmentResponse
        val allPreviousAptConsult = resp?.data ?: ArrayList()
        setTimeSlot(allPreviousAptConsult, day)
      } else showLongToast(it.message())
    })
  }

  private fun setTimeSlot(allPreviousAptConsult: ArrayList<AptData>, day: String) {
    val dataWeekDay = doctorWeeklySchedule?.firstOrNull {
      DataItem.DayName.fromValue(it.day)?.value == DataItem.DayName.fromValue(day)?.value
    }
    val durMin = doctorData?.duration?.toLongOrNull() ?: 0
    if (durMin >= 0) {
      val durMillis = TimeUnit.MINUTES.toMillis(durMin)
      timeSlotList = dataWeekDay?.getTimeSlot(durMillis) ?: ArrayList()
    }
    removeAfterTime(Calendar.getInstance(), startTime = true, b = true)
    allPreviousAptConsult.forEach {
      if (it.dateTimeSlot.isNullOrEmpty().not()) {
        val dataTimeDate = it.dateTimeSlot?.split("#") ?: ArrayList()
        if (dataTimeDate.size >= 4) {
          val timeSlot = dataTimeDate[2].trim().split(",")
          if (timeSlot.size >= 2) {
            val date = parseDate(scheduledDateTime, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY)
            val startTime = "$date ${timeSlot[0]}".parseDate(FORMAT_SERVER_TO_LOCAL_1)?.toCalendar()
            val endTime = "$date ${timeSlot[1]}".parseDate(FORMAT_SERVER_TO_LOCAL_1)?.toCalendar()
            startTime?.let { s -> removeAfterTime(s, startTime = true, b = false) }
            endTime?.let { e -> removeAfterTime(e, startTime = false, b = false) }
          }
        }
      }
    }
  }

  private fun removeAfterTime(now: Calendar, startTime: Boolean, b: Boolean) {
    val newList = ArrayList<TimeSlotData>()
    timeSlotList.forEach {
      val date = parseDate(scheduledDateTime, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY)
      val startTime1 = "$date ${it.startTime}".parseDate(FORMAT_SERVER_TO_LOCAL)?.toCalendar()
      val endTime1 = "$date ${it.endTime}".parseDate(FORMAT_SERVER_TO_LOCAL)?.toCalendar()
      if (isTimeBetweenTwoHours(startTime1, endTime1, now, startTime, b).not()) newList.add(it)
    }
    timeSlotList = newList
  }

  private fun timeSlotBottomSheet() {
    val timeSlotBottom = TimeSlotBottomSheetDialog()
    timeSlotBottom.onDoneClicked = { clickTimeItem(it) }
    timeSlotBottom.setList(timeSlotList)
    timeSlotBottom.show(this.parentFragmentManager, TimeSlotBottomSheetDialog::class.java.name)
  }

  private fun clickTimeItem(timeSlotData: TimeSlotData?) {
    this.timeSlotData = timeSlotData
    binding?.edtStartTime?.setText(this.timeSlotData?.getTimeSlotText())
  }

  private fun updateUiConsult() {
    binding?.radioInClinic?.isClickable = false
    binding?.radioVideoConsultation?.isClickable = false
    binding?.edtAge?.isFocusable = false
    binding?.edtPatientName?.isFocusable = false
    binding?.edtPatientPhone?.isFocusable = false
    binding?.edtPatientEmail?.isFocusable = false
    binding?.edtGender?.text = extraItemConsult?.gender ?: ""
    binding?.edtAge?.setText(extraItemConsult?.age ?: "")
    binding?.edtPatientName?.setText(extraItemConsult?.patientName ?: "")
    binding?.edtPatientPhone?.setText(extraItemConsult?.getNumberPatient() ?: "")
    binding?.edtPatientEmail?.setText(extraItemConsult?.patientEmailId ?: "")
  }

  private fun onInClinicAptConsultAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    if (instance.getDrScoreData()?.metricdetail == null) return
    if (isVideoConsult) instance.getDrScoreData()?.metricdetail?.boolean_create_sample_video_consultation = isAdded
    else instance.getDrScoreData()?.metricdetail?.boolean_create_sample_in_clinic_appointment = isAdded
    instance.updateDocument()
  }
}
