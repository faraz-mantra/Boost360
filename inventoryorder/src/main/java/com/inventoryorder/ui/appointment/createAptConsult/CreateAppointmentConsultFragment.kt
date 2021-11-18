package com.inventoryorder.ui.appointment.createAptConsult

import GetStaffListingRequest
import StaffFilterBy
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.utils.DateUtils.FORMAT_YYYY_MM_DD
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.ValidationUtils.isEmailValid
import com.framework.utils.ValidationUtils.isMobileNumberValid
import com.framework.webengageconstant.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentAppointmentConsultBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.doctorsData.DataItem
import com.inventoryorder.model.doctorsData.GetStaffListingResponse
import com.inventoryorder.model.doctorsData.getDoctorStaffList
import com.inventoryorder.model.doctorsData.saveDoctorList
import com.inventoryorder.model.orderRequest.*
import com.inventoryorder.model.ordersdetails.ExtraPropertiesN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersdetails.ProductN
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.services.*
import com.inventoryorder.model.services.general.GeneralServiceResponse
import com.inventoryorder.model.spaAppointment.bookingslot.request.AppointmentRequestModel
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.request.DateRange
import com.inventoryorder.model.spaAppointment.bookingslot.response.ResultSlot
import com.inventoryorder.model.spaAppointment.bookingslot.response.Slots
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.bottomsheet.TimeSlotBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.item_unavailable_calendar.view.*
import java.util.*
import java.util.regex.Pattern

class CreateAppointmentConsultFragment : BaseInventoryFragment<FragmentAppointmentConsultBinding>(), PopupMenu.OnMenuItemClickListener {

  private lateinit var session: UserSessionManager

  private var orderItem: OrderItem? = null
  private val product: ProductN?
    get() {
      return orderItem?.firstItemForAptConsult()?.product()
    }
  private val extraItemConsult: ExtraPropertiesN?
    get() {
      return product?.extraItemProductConsultation()
    }

  private val isUpdate: Boolean
    get() {
      return (orderItem != null)
    }
  private var isVideoConsult: Boolean = false

  private var singleRowCalendar: SingleRowCalendar? = null
  private val calendar = Calendar.getInstance()
  private var currentMonth = 0
  private var scheduledDateTime: String = ""

  var doctorDataList: ArrayList<DataItem>? = null
  var doctorData: DataItem? = null
  private var selectPositionService: Int = -1
  private var selectPositionDoctor: Int = -1

  private var serviceList: ArrayList<ItemsItemService>? = null
  private var serviceData: ItemsItemService? = null
  private val serviceListFilter: List<ItemsItemService>?
    get() {
      return this.serviceList?.findByIds(doctorData?.serviceIds ?: arrayListOf())
    }

  private var timeSlots: ArrayList<Slots>? = null
  private var timeSlotData: Slots? = null

  private var orderInitiateRequest = OrderInitiateRequest()
  private var updateExtraPropertyRequest: UpdateExtraPropertyRequest? = null

  companion object {
    fun newInstance(bundle: Bundle? = null): CreateAppointmentConsultFragment {
      val fragment = CreateAppointmentConsultFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    orderItem = arguments?.getSerializable(IntentConstant.ORDER_ITEM.name) as? OrderItem
    isVideoConsult = arguments?.getBoolean(IntentConstant.IS_VIDEO.name) ?: false
    setToolbarTitle(getString(if (isUpdate) R.string.update_apt_consult else R.string.new_apppointment_camel_case))
    if (session.fP_AppExperienceCode == "DOC" || session.fP_AppExperienceCode == "HOS") binding?.radioVideoConsultation?.isVisible = true
    if (isVideoConsult) binding?.radioVideoConsultation?.isChecked = true else binding?.radioInClinic?.isChecked = true

    binding?.radioGroup?.setOnCheckedChangeListener { _, checkedId ->
      if (checkedId == binding?.radioInClinic?.id) isVideoConsult = false
      else if (checkedId == binding?.radioVideoConsultation?.id) isVideoConsult = true
    }
    calendarView()
    setOnClickListener(binding?.edtConsultingService, binding?.edtDoctor, binding?.edtStartTime, binding?.btnCreate, binding?.edtGender)
    getDoctorList()
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
        if (date.before(todayDate)) return R.layout.item_unavailable_calendar
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
          timeSlotData = null
          timeSlots = null
          binding?.edtStartTime?.setText("")
          setServiceDataAndSlot()
        }
      }

      override fun whenWeekMonthYearChanged(weekNumber: String, monthNumber: String, monthName: String, year: String, date: Date) {
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
    singleRowCalendar = binding?.mainSingleRowCalendar?.apply {
      calendarViewManager = myCalendarViewManager
      calendarChangesObserver = myCalendarChangesObserver
      calendarSelectionManager = mySelectionManager
      includeCurrentDate = true
      pastDaysCount = 0
      futureDaysCount = 90
      init()
    }
  }

  private fun getDoctorList() {
    showProgress()
    val resp1 = getDoctorStaffList()
    if (resp1.isNullOrEmpty().not()) setDoctorView(resp1)
    viewModel?.getDoctorsListing(session.getFilterRequest(0, 20))?.observeOnce(viewLifecycleOwner, {
      val resultData = (it.anyResponse as? GetStaffListingResponse?)?.result?.data
      val resultDataFilter = resultData?.filter { it1 -> it1.isAvailable == true }
      if (it.isSuccess()) {
        if (resultData.isNullOrEmpty().not()) {
          if (resultDataFilter.isNullOrEmpty().not()) {
            resultDataFilter?.saveDoctorList()
            setDoctorView(resultDataFilter)
          } else errorUi(baseActivity.resources.getString(R.string.doctor_inactive))
        } else errorUi(baseActivity.resources.getString(R.string.please_add_doctor_first))
      } else errorUi(baseActivity.resources.getString(R.string.error_getting_doctor_data))
    })
  }

  private fun setDoctorView(response: List<DataItem>?) {
    doctorDataList = ArrayList(response ?: arrayListOf())
    doctorData = if (isUpdate) {
      selectPositionDoctor = doctorDataList?.indexOfFirst { data -> data.id == extraItemConsult?.doctorId } ?: 0
      if (selectPositionDoctor == -1) selectPositionDoctor = 0
      doctorDataList?.get(selectPositionDoctor)
    } else {
      selectPositionDoctor = 0
      response?.firstOrNull()
    }
    binding?.edtDoctor?.setText(doctorData?.name)
    getServiceList()
  }

  private fun changeDoctor() {
    this.timeSlotData = null
    this.timeSlots = null
    this.serviceData = null
    this.serviceData = serviceList?.firstOrNull()
    this.selectPositionService = 0
    binding?.edtConsultingService?.setText("")
    binding?.edtFees?.setText("")
    binding?.edtStartTime?.setText("")
    binding?.edtDoctor?.setText(doctorData?.name)
    setServiceDataAndSlot()
  }

  private fun getServiceList() {
    serviceList = getDoctorServiceList()
    if (serviceList.isNullOrEmpty().not()) serviceListView()
    viewModel?.getGeneralService(session.fpTag, session.fPID)?.observeOnce(viewLifecycleOwner, { it0 ->
      val data = it0 as? GeneralServiceResponse
      var generalService: ItemsItemService? = null
      if (data?.Result != null && data.Result.IsAvailable == true) {
        generalService = ItemsItemService(
          name = data.Result.Name, id = data.Result._id, currency = data.Result.Currency, price = data.Result.Price, discountAmount = data.Result.DiscountAmount,
          discountedPrice = data.Result.getDiscountedPrice(), duration = data.Result.Duration, description = data.Result.Description, isGeneralService = true
        )
      }
      viewModel?.getServiceListing(ServiceListingRequest(floatingPointTag = session.fpTag))?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          val resp = (it as? ServiceListingResponse)?.result?.flatMap { it1 -> it1.services?.items!! }?.reversed()?.toMutableList()
          generalService?.let { it1 -> resp?.add(it1) }
          val resp1 = resp?.reversed()
          serviceList = resp1?.toCollection(arrayListOf()) ?: ArrayList()
          serviceList?.saveDoctorServiceList()
          serviceListView()
        } else {
          hideProgress()
          showLongToast(getString(R.string.service_not_available))
        }
      })
    })
  }

  private fun serviceListView() {
    hideProgress()
    serviceData = when {
      isUpdate -> {
        selectPositionService = this.serviceListFilter?.indexOfFirst { it.id == product?._id } ?: 0
        if (selectPositionService == -1) selectPositionService = 0
        this.serviceListFilter?.get(selectPositionService)
      }
      else -> {
        selectPositionService = 0
        serviceListFilter?.firstOrNull()
      }
    }
    if (isUpdate) updateUiConsult()
    singleRowCalendar?.select(0)
  }

  private fun setServiceDataAndSlot() {
    serviceData?.let {
      binding?.edtConsultingService?.setText(it.name)
      binding?.edtDuration?.setText((it.duration ?: 0).toString())
      binding?.edtFees?.setText((it.discountedPrice ?: 0.0).toString())
    }
    val dateRange = DateRange(StartDate = scheduledDateTime, EndDate = scheduledDateTime)
    BookingSlotsRequest(ServiceId = serviceData?.id ?: "", DateRange = dateRange).apply {
      getBookingSlots(this)
    }
  }

  private fun getBookingSlots(slotsRequest: BookingSlotsRequest) {
    showProgress()
    this.timeSlots = arrayListOf()
    viewModel?.getBookingSlotsStaff(doctorData?.id, slotsRequest)?.observeOnce(viewLifecycleOwner, {
      val response = it.arrayResponse as? Array<ResultSlot>
      if (it.isSuccess() && response.isNullOrEmpty().not()) {
        val resultSlot = response?.firstOrNull()
        if (resultSlot != null && resultSlot.Staff.isNullOrEmpty().not()) {
          this.timeSlots = resultSlot.Staff?.firstOrNull()?.AppointmentSlots?.firstOrNull()?.Slots
        }
      } else showShortToast(getString(R.string.doctor_weekly_schedule_not_available))
      hideProgress()
    })
  }

  private fun updateUiConsult() {
    binding?.radioInClinic?.isClickable = false
    binding?.radioVideoConsultation?.isClickable = false
    binding?.edtAge?.isFocusable = false
    binding?.edtPatientName?.isFocusable = false
    binding?.edtPatientPhone?.isFocusable = false
    binding?.edtPatientEmail?.isFocusable = false
    binding?.edtGender?.setText(extraItemConsult?.gender ?: "")
    binding?.edtAge?.setText(extraItemConsult?.age ?: "")
    binding?.edtPatientName?.setText(extraItemConsult?.patientName ?: "")
    binding?.edtPatientPhone?.setText(extraItemConsult?.getNumberPatient() ?: "")
    binding?.edtPatientEmail?.setText(extraItemConsult?.patientEmailId ?: "")
  }

  private fun errorUi(message: String) {
    hideProgress()
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtDoctor -> doctorListDialog()
      binding?.edtConsultingService -> consultingOnService()
      binding?.edtStartTime -> {
        when {
          scheduledDateTime.isEmpty() -> showLongToast(getString(R.string.first_select_consultation_date))
          doctorData == null -> showLongToast(getString(R.string.first_select_doctor))
          serviceData == null -> showLongToast(getString(R.string.first_select_service))
          timeSlots.isNullOrEmpty() -> showLongToast(getString(R.string.time_slot_not_available))
          else -> timeSlotBottomSheet()
        }
      }
      binding?.edtGender -> menuItemView(v, R.menu.popup_menu_gender_selection)
      binding?.btnCreate -> {
        if (validateAndCreateRequest()) {
          if (isUpdate) updateBooking() else createBooking()
        }
      }
    }
  }

  private fun createBooking() {
    showProgress()
    viewModel?.postAppointment(clientId, orderInitiateRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        WebEngageController.trackEvent(if (isVideoConsult) CONSULATION_CREATE else APPOINTMENT_CREATE, ADDED, TO_BE_ADDED)
        onInClinicAptConsultAddedOrUpdated(true)
        startSuccessScreen((it as? OrderInitiateResponse)?.data)
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.can_not_reshedule_your_booking_at_this_time))
      }
    })
  }

  private fun updateBooking() {
    showProgress()
    viewModel?.updateExtraPropertyOrder(AppConstant.CLIENT_ID_2, request = updateExtraPropertyRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        WebEngageController.trackEvent(if (isVideoConsult) CONSULATION_UPDATED else APPOINTMENT_UPDATED, ADDED, TO_BE_ADDED)
        showLongToast(getString(R.string.apt_resceduled_success))
        val intent = Intent()
        intent.putExtra(IntentConstant.ORDER_ID.name, orderItem?._id)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
        baseActivity.finish()
        hideProgress()
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.can_not_reshedule_your_booking_at_this_time))
      }
    })

  }

  private fun startSuccessScreen(response: OrderItem?) {
    val extra = response?.Items?.firstOrNull()?.product()?.extraItemProductConsultation()
    val scheduledDate = parseDate(extra?.scheduledDateTime, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL_2)
    showLongToast(getString(R.string.booking_created))
    val bundle = Bundle()
    bundle.putString("ORDER_ID", response?.ReferenceNumber)
    bundle.putString("NAME", extra?.patientName)
    bundle.putString("SERVICE_NAME", serviceData?.name)
    bundle.putString("START_TIME_DATE", scheduledDate)
    bundle.putString("NUMBER", extra?.patientMobileNumber)
    bundle.putString("EMAIL", extra?.patientEmailId)
    startFragmentOrderActivity(FragmentType.BOOKING_SUCCESSFUL, bundle, isResult = true)
    hideProgress()
  }

  private fun validateAndCreateRequest(): Boolean {
    val duration = binding?.edtDuration?.text?.toString()
    val consultingService = binding?.edtConsultingService?.text?.toString()
    val patientMobile = binding?.edtPatientPhone?.text?.toString()
    val patientName = binding?.edtPatientName?.text?.toString()
    val gender = binding?.edtGender?.text?.toString()
    val age = binding?.edtAge?.text?.toString()
    val patientEmail = binding?.edtPatientEmail?.text?.toString() ?: ""
    when {
      scheduledDateTime.isEmpty() -> {
        showLongToast(getString(R.string.please_select_consultation_date))
        return false
      }
      timeSlots?.isEmpty() == true -> {
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
      (age.toIntOrNull())?:0>150->{
        showLongToast(getString(R.string.enter_valid_age))
        return false
      }
      patientMobile.isNullOrEmpty() -> {
        showLongToast(getString(R.string.patient_phone_number_field_must_not_be_empty))
        return false
      }
      !isMobileNumberValid(patientMobile.toString()) -> {
        showLongToast(getString(R.string.phone_number_invalid))
        return false
      }
      checkStringContainsDigits(patientName) -> {
        showLongToast(getString(R.string.please_enter_valid_patient_name))
        return false
      }

      patientMobile.length != 10 -> {
        showLongToast(getString(R.string.please_enter_valid_phone_number))
        return false
      }

      patientEmail.isNotEmpty() && !isEmailValid(patientEmail) -> {
        showLongToast(resources.getString(R.string.please_enter_valid_email))
        return false
      }
      else -> {
//        var startTime24 = ""
//        var endTime24 = ""
//        try {
//          startTime24 = parseDate(timeSlotData?.StartTime, FORMAT_HH_MM_A, FORMAT_HH_MM) ?: ""
//          endTime24 = parseDate(timeSlotData?.EndTime, FORMAT_HH_MM_A, FORMAT_HH_MM) ?: ""
//        } catch (e: Exception) {
//          e.printStackTrace()
//        }
        val scheduledDateTimeN = parseDate(scheduledDateTime, FORMAT_SERVER_DATE, FORMAT_YYYY_MM_DD)
        val aptModel = AppointmentRequestModel(
          _id = timeSlotData?._id, duration = duration, startTime = timeSlotData?.StartTime, endTime = timeSlotData?.EndTime,
          scheduledDateTime = scheduledDateTimeN, staffId = doctorData?.id ?: "", staffName = doctorData?.name ?: ""
        )
        val extra = ExtraProperties(
          patientName = patientName, gender = gender, age = (age.toIntOrNull() ?: 0).toString(),
          patientMobileNumber = patientMobile, patientEmailId = patientEmail,
          startTime = timeSlotData?.StartTime ?: "", endTime = timeSlotData?.EndTime ?: "", scheduledDateTime = scheduledDateTime,
          consultationFor = serviceData?.name ?: "", doctorName = doctorData?.name ?: "",
          doctorId = doctorData?.id ?: "", doctorQualification = doctorData?.education ?: "",
          doctorSpeciality = doctorData?.speciality ?: "", duration = duration?.toIntOrNull() ?: 0,
          businessLicense = doctorData?.businessLicence ?: "", doctorSignature = doctorData?.signature?.toString() ?: "",
          referenceId = serviceData?.id ?: "", businessLogo = "", appointment = arrayListOf(aptModel)
        )

        if (isUpdate) {
          updateExtraPropertyRequest = UpdateExtraPropertyRequest(
            extraProperties = extra, orderId = orderItem?._id, updateExtraPropertyType = UpdateExtraPropertyRequest.PropertyType.ITEM.name
          )
        } else {
          val method = if (serviceData?.discountedPrice == 0.0) PaymentDetailsN.METHOD.FREE.type else PaymentDetailsN.METHOD.COD.type
          val paymentDetails = PaymentDetails(method)
          val buyerDetail = BuyerDetails(
            address = Address(),
            contactDetails = ContactDetails(emailId = patientEmail, fullName = patientName, primaryContactNumber = patientMobile)
          )
          val delMode = if (isVideoConsult) OrderItem.DeliveryMode.ONLINE.name else OrderSummaryRequest.DeliveryMode.OFFLINE.name
          val delProvider = if (isVideoConsult) ShippingDetails.DeliveryProvider.NF_VIDEO_CONSULATION.name else ""
          val shippingDetails = ShippingDetails(
            shippedBy = ShippingDetails.ShippedBy.SELLER.name, deliveryMode = delMode,
            shippingCost = 0.0, currencyCode = "INR", deliveryProvider = delProvider
          )
          val items = ArrayList<ItemsItem>()

          val productDetails = ProductDetails(
            id = serviceData?.id ?: "NO_ITEM", name = serviceData?.name ?: "NO_ITEM", description = serviceData?.description ?: "NO_ITEM",
            currencyCode = "INR", isAvailable = true, price = serviceData?.discountedPrice, shippingCost = 0.0,
            discountAmount = serviceData?.discountAmount, extraProperties = extra, imageUri = serviceData?.image ?: ""
          )

          items.add(
            ItemsItem(
              type = serviceData?.getCategoryValue() ?: "NO_ITEM", productDetails = productDetails,
              productOrOfferId = serviceData?.id ?: "NO_ITEM", quantity = 1
            )
          )

          orderInitiateRequest.paymentDetails = paymentDetails
          orderInitiateRequest.sellerID = session.fpTag
          orderInitiateRequest.buyerDetails = buyerDetail
          orderInitiateRequest.mode = OrderItem.OrderMode.APPOINTMENT.name
          orderInitiateRequest.shippingDetails = shippingDetails
          orderInitiateRequest.transactionCharges = 0.0
          orderInitiateRequest.items = items
          orderInitiateRequest.isVideoConsult = isVideoConsult
        }
        return true
      }
    }
  }

  private fun menuItemView(v: View, menuView: Int) {
    val popup = PopupMenu(baseActivity, v)
    popup.setOnMenuItemClickListener(this@CreateAppointmentConsultFragment)
    popup.inflate(menuView)
    popup.show()
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.male -> binding?.edtGender?.setText(item.title.toString())
      R.id.female -> binding?.edtGender?.setText(item.title.toString())
      else -> false
    }
    return false
  }

  private fun timeSlotBottomSheet() {
    val timeSlotBottom = TimeSlotBottomSheetDialog()
    timeSlotBottom.onDoneClicked = {
      this.timeSlotData = it
      binding?.edtStartTime?.setText(this.timeSlotData?.getTimeSlotText())
    }
    timeSlotBottom.setList(timeSlots ?: arrayListOf())
    timeSlotBottom.show(this.parentFragmentManager, TimeSlotBottomSheetDialog::class.java.name)
  }

  private fun consultingOnService() {
    var selectPS = selectPositionService
    val singleItems = serviceListFilter?.map { it.name }?.toTypedArray()
    if (singleItems.isNullOrEmpty().not()) {
      if (singleItems!!.size == 1) return
      MaterialAlertDialogBuilder(baseActivity).setTitle(getString(R.string.consult_service))
        .setPositiveButton(getString(R.string.ok)) { d, _ ->
          selectPositionService = selectPS
          serviceData = this.serviceListFilter?.firstOrNull { it.name == singleItems[selectPositionService] }
          this.timeSlotData = null
          this.timeSlots = null
          binding?.edtStartTime?.setText("")
          setServiceDataAndSlot()
          d.dismiss()
        }.setNeutralButton(getString(R.string.cancel)) { d, _ ->
          d.dismiss()
        }.setSingleChoiceItems(singleItems, selectPositionService) { _, pos ->
          selectPS = pos
        }.show()
    } else showShortToast(getString(R.string.cosulting_service_not_available))
  }

  private fun doctorListDialog() {
    var selectPD = selectPositionDoctor
    val singleItems = this.doctorDataList?.map { it.name }?.toTypedArray()
    if (singleItems.isNullOrEmpty().not()) {
      if (singleItems!!.size == 1) return
      MaterialAlertDialogBuilder(baseActivity).setTitle(getString(R.string.select_doctor)).setPositiveButton(getString(R.string.ok)) { d, _ ->
        selectPositionDoctor = selectPD
        doctorData = this.doctorDataList?.firstOrNull { it.name == singleItems[selectPositionDoctor] }
        changeDoctor()
        d.dismiss()
      }.setNeutralButton(getString(R.string.cancel)) { d, _ ->
        d.dismiss()
      }.setSingleChoiceItems(singleItems, selectPositionDoctor) { _, pos ->
        selectPD = pos
      }.show()
    } else showShortToast(getString(R.string.doctor_staff_not_available))
  }

  private fun onInClinicAptConsultAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    if (isVideoConsult) instance.getDrScoreData()?.metricdetail?.boolean_create_sample_video_consultation = isAdded
    else instance.getDrScoreData()?.metricdetail?.boolean_create_sample_in_clinic_appointment = isAdded
    instance.updateDocument()
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
}

fun UserSessionManager.getFilterRequest(offSet: Int, limit: Int): GetStaffListingRequest {
  return GetStaffListingRequest(StaffFilterBy(offset = offSet, limit = limit), this.fpTag, "")
}

fun List<ItemsItemService>.findByIds(fooApiList: List<String>) = filter { fooApiList.any { v -> (v == it.id || it.isGeneralService) } }


fun checkStringContainsDigits(input: String): Boolean {
  return Pattern.compile("[0-9]").matcher(input).find()
}