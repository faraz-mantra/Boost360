package com.inventoryorder.ui.appointmentSpa.create
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.utils.DateUtils
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentSpaAppointmentBinding
import com.inventoryorder.model.orderRequest.*
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.spaAppointment.GetServiceListingResponse
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.model.spaAppointment.bookingslot.request.AppointmentRequestModel
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.request.DateRange
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.recyclerView.CustomArrayAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.model.CityDataModel
import com.onboarding.nowfloats.ui.CitySearchDialog
import kotlinx.android.synthetic.main.item_date_view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class SpaAppointmentFragment : BaseInventoryFragment<FragmentSpaAppointmentBinding>(), SelectDateTimeBottomSheetDialog.DateChangedListener {

  private var serviceList: ArrayList<ServiceItem>? = null
  private var serviceAdapter: ArrayAdapter<ServiceItem>? = null
  private var startDate = ""
  private var bookingSlotResponse: BookingSlotResponse? = null
  private var selectedService: ServiceItem? = null
  private var selectedDateTimeBottomSheetDialog: SelectDateTimeBottomSheetDialog? = null
  private var orderInitiateRequest = OrderInitiateRequest()
  private var appointmentRequestModel = AppointmentRequestModel()
  private var totalPrice = 0.0
  private var discountedPrice = 0.0
  private var currency = ""
  private var shouldReInitiate = false
  private var shouldRefresh = false
  private var dateCounter = 1

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SpaAppointmentFragment {
      val fragment = SpaAppointmentFragment()
      fragment.setTargetFragment(this.newInstance(), 0)
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    getSearchListing()
    serviceList = ArrayList()
    startDate = getDateTime()
    setOnClickListener(binding?.buttonReviewDetails, binding?.textAddApptDateTime, binding?.buttonReviewDetails, binding?.imageEdit,
        binding?.layoutCustomer?.textAddCustomerGstin, binding?.layoutCustomer?.tvRemove, binding?.layoutBillingAddr?.editCity)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonReviewDetails -> validateForm()
      binding?.layoutBillingAddr?.editCity -> {
        val dialog = CitySearchDialog()
        dialog.onClicked = { setCityState(it) }
        dialog.show(parentFragmentManager, dialog.javaClass.name)
      }
      binding?.textAddApptDateTime -> {
        selectedDateTimeBottomSheetDialog = SelectDateTimeBottomSheetDialog(bookingSlotResponse!!, selectedService!!, dateCounter, this)
        selectedDateTimeBottomSheetDialog?.onClicked = this::onDialogDoneClicked
        selectedDateTimeBottomSheetDialog?.show(this.parentFragmentManager, SelectDateTimeBottomSheetDialog::class.java.name)
      }

      binding?.layoutCustomer?.textAddCustomerGstin -> {
        if (binding?.layoutCustomer?.lytCustomerGstn?.visibility == View.GONE) {
          binding?.layoutCustomer?.textAddCustomerGstin?.visibility = View.GONE
          binding?.layoutCustomer?.lytCustomerGstn?.visibility = View.VISIBLE
        }
      }

      binding?.layoutCustomer?.tvRemove -> {
        binding?.layoutCustomer?.textAddCustomerGstin?.visibility = View.VISIBLE
        binding?.layoutCustomer?.lytCustomerGstn?.visibility = View.GONE
        binding?.layoutCustomer?.editGstin?.text?.clear()

      }

      binding?.imageEdit -> {
        appointmentRequestModel.staffId?.let {
          for ((_, value) in bookingSlotResponse?.Result?.get(0)?.Staff?.withIndex()!!) {
            value.isSelected = value._id == it
          }
        }

        selectedDateTimeBottomSheetDialog = SelectDateTimeBottomSheetDialog(bookingSlotResponse!!, selectedService!!, dateCounter, this)
        selectedDateTimeBottomSheetDialog?.onClicked = this::onDialogDoneClicked
        selectedDateTimeBottomSheetDialog?.show(this.parentFragmentManager, SelectDateTimeBottomSheetDialog::class.java.name)
      }
    }
  }

  private fun setCityState(cityDataModel: CityDataModel) {
    binding?.layoutBillingAddr?.editCity?.setText(cityDataModel.getCityName().capitalizeWords())
    binding?.layoutBillingAddr?.editState?.setText(cityDataModel.getStateName().capitalizeWords())
  }

  fun getBundleData(): Bundle? {
    return Bundle().apply {
      putBoolean(IntentConstant.SHOULD_RE_INITIATE.name, shouldReInitiate)
      putBoolean(IntentConstant.IS_REFRESH.name, shouldRefresh)
    }
  }

  private fun validateForm() {

    val name = binding?.layoutCustomer?.editCustomerName?.text ?: ""
    val email = binding?.layoutCustomer?.editCustomerEmail?.text ?: ""
    val phone = binding?.layoutCustomer?.editCustomerPhone?.text ?: ""
    val address = binding?.layoutBillingAddr?.editAddress?.text ?: ""
    val city = binding?.layoutBillingAddr?.editCity?.text ?: ""
    val state = binding?.layoutBillingAddr?.editState?.text ?: ""
    val pinCode = binding?.layoutBillingAddr?.editPin?.text ?: ""
    val gstNo = binding?.layoutCustomer?.editGstin?.text ?: ""

    if (appointmentRequestModel._id == null || appointmentRequestModel?._id?.isEmpty() == true) {
      showShortToast(getString(R.string.please_select_staff_and_time_slot))
      return
    }

    if (name.isEmpty()) {
      showShortToast(getString(R.string.customer_name_cannot_be_empty))
      return
    }

    if (phone.isEmpty()) {
      showShortToast(getString(R.string.customer_phone_cannot_be_empty))
      return
    }

    if (phone.length < 10) {
      showShortToast(getString(R.string.please_enter_valid_phone))
      return
    }

    if (email.isNullOrEmpty().not() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
      showShortToast(getString(R.string.please_enter_valid_email))
      return
    }
    if (binding?.layoutCustomer?.lytCustomerGstn?.visibility == View.VISIBLE)
      if (gstNo.isNullOrEmpty().not() && Pattern.compile(AppConstant.GST_VALIDATION_REGEX).matcher(gstNo).matches().not()) {
        showShortToast(getString(R.string.enter_valid_gstin_number))
        return
      }

    if (address.isEmpty()) {
      showShortToast(getString(R.string.customer_address_cannot_be_empty))
      return
    }

    if (city.isEmpty()) {
      showShortToast(getString(R.string.customer_city_cannot_be_empty))
      return
    }

    if (state.isEmpty()) {
      showShortToast(getString(R.string.customer_state_cannot_be_empty))
      return
    }

    if (pinCode.isEmpty()) {
      showShortToast(getString(R.string.customer_pincode_cannot_be_empty))
      return
    }

    if (pinCode.length < 6) {
      showShortToast(getString(R.string.enter_valid_pincode))
      return
    }

    val contactDetails = ContactDetails(fullName = name.toString(),
        emailId = email.toString(),
        primaryContactNumber = phone.toString())

    val billingAddress = Address(address.toString(), city = city.toString(), region = state.toString(), zipcode = pinCode.toString())
    val buyerDetails = BuyerDetails(contactDetails = contactDetails, address = billingAddress, GSTIN = gstNo.toString())

    val appointmentsList = ArrayList<AppointmentRequestModel>()
    appointmentsList.add(appointmentRequestModel)

    val productDetails = ProductDetails(extraProperties = ExtraProperties(appointment = appointmentsList))

    val items = ArrayList<ItemsItem>()
    val item = ItemsItem(productOrOfferId = selectedService?._id, quantity = 1, type = "SERVICE", productDetails = productDetails)

    items.add(item)

    orderInitiateRequest.buyerDetails = buyerDetails
    orderInitiateRequest.mode = OrderItem.OrderMode.APPOINTMENT.name
    orderInitiateRequest.items = items
    orderInitiateRequest.items?.get(0)?.productDetails?.name = selectedService?.Name
    orderInitiateRequest.sellerID = preferenceData?.fpTag

    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, orderInitiateRequest)
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
    bundle.putDouble(IntentConstant.TOTAL_PRICE.name, totalPrice)
    bundle.putDouble(IntentConstant.DISCOUNTED_PRICE.name, discountedPrice)
    bundle.putString(IntentConstant.CURRENCY.name, currency)
    bundle.putSerializable(IntentConstant.SELECTED_SERVICE.name, selectedService)
    startFragmentOrderActivity(FragmentType.REVIEW_SPA_DETAILS, bundle, isResult = true)
  }

  private fun onDialogDoneClicked(appointmentRequestModel: AppointmentRequestModel, dateCounter: Int) {
    binding?.layoutShowSelectedSlot?.visibility = View.VISIBLE
    binding?.groupTiming?.visibility = View.GONE

    binding?.textDate?.text = getDisplayDate(appointmentRequestModel?.scheduledDateTime ?: "")
    binding?.textTime?.text = appointmentRequestModel?.startTime
    binding?.textBy?.text = appointmentRequestModel?.staffName

    this.appointmentRequestModel = appointmentRequestModel
    this.dateCounter = dateCounter
  }

  private fun getSearchListing() {
    showProgress(getString(R.string.loading))
    viewModel?.getSearchListing(preferenceData?.fpTag!!, "", "", 0, 100)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()

      if (it.error is NoNetworkException) {
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }

      if (it.isSuccess()) {
        val serviceListingResponse = (it as? GetServiceListingResponse)
        if (serviceListingResponse != null) {
          serviceList?.addAll(serviceListingResponse.Result?.Data!!)
          setArrayAdapter()
        }
      } else {
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.not_able_to_get_services_list))
      }
    })
  }


  private fun setArrayAdapter() {
    binding?.editServiceName?.apply {
      serviceAdapter = CustomArrayAdapter(baseActivity, R.layout.layout_service_item, serviceList!!)
      threshold = 1
      (serviceAdapter as? CustomArrayAdapter)?.initList()
      setAdapter(serviceAdapter)
      onItemClickListener = AdapterView.OnItemClickListener { p0, view, pos, id ->
        selectedService = serviceList?.get(pos)
        binding?.editServiceName?.setText(fromHtml("<b><color='#2a2a2a'>${selectedService?.Name}</color></b> <color='#adadad'>(${selectedService?.Currency}${selectedService?.DiscountedPrice} for ${selectedService?.Duration}min)</color>"))
        totalPrice = selectedService?.Price ?: 0.0
        discountedPrice = selectedService?.DiscountedPrice ?: 0.0
        currency = selectedService?.Currency ?: ""
        val bookingSlotsRequest = BookingSlotsRequest(BatchType = "DAILY",
                ServiceId = serviceList?.get(pos)?._id!!,
                DateRange = DateRange(StartDate = startDate, EndDate = startDate))
        getBookingSlots(bookingSlotsRequest)
      }
    }
    binding?.editServiceName?.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (s.toString().isBlank() || s.toString().isEmpty()) {
          binding?.groupTiming?.visibility = View.VISIBLE
          binding?.layoutShowSelectedSlot?.visibility = View.GONE
        }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }
    })
  }

  private fun getBookingSlots(bookingSlotsRequest: BookingSlotsRequest) {
    viewModel?.getBookingSlots(bookingSlotsRequest)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.isSuccess()) {
        bookingSlotResponse = (it as? BookingSlotResponse)
        if (bookingSlotResponse?.Result.isNullOrEmpty().not()) {
          if (bookingSlotResponse?.Result?.get(0)?.Staff.isNullOrEmpty().not()) {
            bookingSlotResponse?.Result?.get(0)?.Staff?.get(0)?.isSelected = true
          }
        }
        selectedDateTimeBottomSheetDialog?.setData(bookingSlotResponse!!, selectedService!!)
        binding?.groupTiming?.visibility = View.VISIBLE
        binding?.layoutShowSelectedSlot?.visibility = View.GONE
      } else {
        showLongToast(getString(R.string.not_able_to_get_booking_slots))
        binding?.groupTiming?.visibility = View.GONE
      }
    })
  }

  private fun getDateTime(): String {
    val c = Calendar.getInstance().time
    val df = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
    return df.format(c)
  }

  private fun getDisplayDate(date: String): String {
    try {
      val currentDateFormat = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
      val displayDateFormat = SimpleDateFormat(DateUtils.SPA_DISPLAY_DATE, Locale.getDefault())
      val currentDate = currentDateFormat.parse(date)
      return displayDateFormat.format(currentDate!!)
    } catch (ex: Exception) {
    }
    return ""
  }

  override fun onDateChanged(request: BookingSlotsRequest) {
    getBookingSlots(bookingSlotsRequest = request)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_RE_INITIATE.name) ?: false
      shouldRefresh = bundle?.getBoolean(IntentConstant.IS_REFRESH.name) ?: false
      if (shouldReInitiate || shouldRefresh) (context as? FragmentContainerOrderActivity)?.onBackPressed()
    }
  }
}