package com.inventoryorder.ui.appointmentSpa.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.extensions.observeOnce
import com.framework.utils.DateUtils
import com.framework.utils.MathUtils
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.AppConstant.Companion.GST_PERCENTAGE
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentReviewAndConfirmBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.PaymentDetails
import com.inventoryorder.model.orderRequest.ShippingDetails
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ReviewAndConfirmFragment : BaseInventoryFragment<FragmentReviewAndConfirmBinding>() {

  private var serviceFee = 0.0
  private var totalPrice = 0.0
  private var totalGst= 0.0
  private var discountedPrice = 0.0
  private var orderInitiateRequest: OrderInitiateRequest? = null
  private var orderBottomSheet = OrderBottomSheet()
  private var paymentStatus: String = PaymentDetailsN.STATUS.PENDING.name
  private var selectedService: ServiceItem? = null
  private var shouldReInitiate = false
  private var shouldRefresh = false
  private var prefData: PreferenceData? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): ReviewAndConfirmFragment {
      val fragment = ReviewAndConfirmFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  fun getBundleData(): Bundle {
    return Bundle().apply {
      putBoolean(IntentConstant.IS_REFRESH.name, shouldRefresh)
      putBoolean(IntentConstant.SHOULD_RE_INITIATE.name, shouldReInitiate)
    }
  }

  override fun onCreateView() {
    setOnClickListener(
      binding?.textAdd,
      binding?.buttonReviewDetails,
      binding?.tvPaymentStatus,
      binding?.textEdit
    )
    orderInitiateRequest =
      arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
    totalPrice = arguments?.getDouble(IntentConstant.TOTAL_PRICE.name) ?: 0.0
    totalGst = MathUtils.calculateGST(totalPrice,orderInitiateRequest?.items?.firstOrNull()?.productDetails?.gstSlab?:0)
    discountedPrice = arguments?.getDouble(IntentConstant.DISCOUNTED_PRICE.name) ?: 0.0
    selectedService =
      arguments?.getSerializable(IntentConstant.SELECTED_SERVICE.name) as ServiceItem
    prefData = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as PreferenceData
    // orderInitiateRequest?.sellerID = preferenceData?.fpTag.toString()

    preparePaymentStatusOptions()
    setData()
  }

  private fun setData() {
    binding?.serviceName?.text =
      orderInitiateRequest?.items?.get(0)?.productDetails?.name?.capitalizeUtil() ?: ""
    binding?.textServiceDuration?.text =
      "${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.duration ?: 0} minutes"
    binding?.textStaffName?.text =
      "by ${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.staffName}"
    binding?.textDateTimeSlot?.text = "${
      parseDate(
        orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.scheduledDateTime!!
      )
    }, ${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.startTime}"

    binding?.tvName?.text =
      orderInitiateRequest?.buyerDetails?.contactDetails?.fullName?.capitalizeUtil()
    binding?.tvEmail?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.emailId
    binding?.textAmount?.text = "${selectedService?.Currency} $totalPrice"
    binding?.textActualAmount?.text = "${selectedService?.Currency} $discountedPrice"
    binding?.textGstAmount?.text =
      "${selectedService?.Currency} ${totalGst}"
    // binding?.textTotalPayableValue?.text = "${selectedService?.Currency} ${(discountedPrice + calculateGST(discountedPrice))}"
    binding?.textTotalPayableValue?.text = "${selectedService?.Currency} $discountedPrice"


    if (orderInitiateRequest?.buyerDetails?.GSTIN != null && orderInitiateRequest?.buyerDetails?.GSTIN?.isNotEmpty() == true) {
      binding?.tvGstin?.text = "GSTIN : ${orderInitiateRequest?.buyerDetails?.GSTIN}"
      binding?.tvGstin?.visibility = View.VISIBLE
    }

    orderInitiateRequest?.gstCharges = totalGst
    val paymentDetails =
      PaymentDetails(method = PaymentDetailsN.METHOD.COD.type, status = paymentStatus)
    orderInitiateRequest?.paymentDetails = paymentDetails

    if (selectedService?.Image != null && selectedService?.Image?.isNotEmpty() == true)
      Picasso.get().load(selectedService?.Image).into(binding?.imageService)

    if (orderInitiateRequest?.buyerDetails?.contactDetails?.emailId.isNullOrBlank()) {
      binding?.tvEmail?.visibility = View.GONE
    } else {
      binding?.tvEmail?.visibility = View.VISIBLE
    }

    if (totalPrice == discountedPrice) {
      binding?.textAmount?.visibility = View.GONE
    }

    binding?.tvPhone?.text =
      orderInitiateRequest?.buyerDetails?.contactDetails?.primaryContactNumber.toString()
    binding?.tvAddress?.text = setUpAddress()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.textAdd -> showAddServiceFeeDialog()
      binding?.buttonReviewDetails -> {
        val shippingDetails = ShippingDetails(
          shippedBy = ShippingDetails.ShippedBy.SELLER.name,
          deliveryMode = OrderSummaryRequest.DeliveryMode.OFFLINE.name, shippingCost = serviceFee,
          currencyCode = orderInitiateRequest?.items?.get(0)?.productDetails?.getCurrencyCodeValue()
            ?: "INR"
        )
        orderInitiateRequest?.shippingDetails = shippingDetails
        createAppointment()
      }

      binding?.textEdit -> showAddServiceFeeDialog()

      binding?.tvPaymentStatus -> {
        val createOrderBottomSheetDialog = CreateOrderBottomSheetDialog(orderBottomSheet)
        createOrderBottomSheetDialog.onClicked = this::onPaymentStatusSelected
        createOrderBottomSheetDialog.show(
          this.parentFragmentManager,
          CreateOrderBottomSheetDialog::class.java.name
        )
      }
    }
  }

  private fun onPaymentStatusSelected(bottomSheetOptionsItem: BottomSheetOptionsItem, orderBottomSheet: OrderBottomSheet) {
    binding?.tvPaymentStatus?.text = bottomSheetOptionsItem?.displayValue
    orderInitiateRequest?.paymentDetails?.status = bottomSheetOptionsItem?.serverValue
    paymentStatus = bottomSheetOptionsItem?.serverValue!!
    this.orderBottomSheet = orderBottomSheet
  }

  private fun showAddServiceFeeDialog() {
    val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(serviceFee, AppConstant.TYPE_APPOINTMENT)
    addDeliveryFeeBottomSheetDialog.onClicked = { onServiceFeeAdded(it) }
    addDeliveryFeeBottomSheetDialog.show(
      this.parentFragmentManager,
      AddDeliveryFeeBottomSheetDialog::class.java.name
    )
  }

  private fun onServiceFeeAdded(fee: Double) {
    serviceFee = fee
    binding?.textGstAmount?.text =
      "${selectedService?.Currency} ${totalGst}"

    if (fee > 0.0) {
      binding?.textTotalPayableValue?.text = "${selectedService?.Currency} ${discountedPrice + fee}"
      binding?.textAdd?.text = "${selectedService?.Currency} $fee"
      binding?.textEdit?.visibility = View.VISIBLE
    } else {
      binding?.textAdd?.text = getString(R.string.add)
      binding?.textTotalPayableValue?.text = "${selectedService?.Currency} ${discountedPrice + fee}"
      binding?.textEdit?.visibility = View.INVISIBLE
    }
  }

  private fun createAppointment() {
    showProgress()
    viewModel?.postAppointment(AppConstant.CLIENT_ID_ORDER, orderInitiateRequest)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          hideProgress()
          val orderInitiateResponse = (it as? OrderInitiateResponse)
          apiConfirmOrder(orderInitiateResponse?.data)
        } else {
          hideProgress()
          showLongToast(
            if (it.message()
                .isNotEmpty()
            ) it.message() else getString(R.string.unable_to_create_order)
          )
        }
      })
  }

  private fun apiConfirmOrder(order: OrderItem?) {
    showProgress()
    viewModel?.confirmOrder(prefData?.clientId, order?._id)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        val bundle = Bundle()
        bundle.putString(IntentConstant.TYPE_APPOINTMENT.name, AppConstant.TYPE_APPOINTMENT)
        bundle.putSerializable(IntentConstant.ORDER_ID.name, order?._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, prefData)
        startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)
      } else {
        showLongToast(
          if (it.message()
              .isNotEmpty()
          ) it.message() else getString(R.string.unable_to_create_order)
        )
      }
    })
  }

  private fun setUpAddress(): String {
    val addSlr = StringBuilder()
    addSlr.append(orderInitiateRequest?.buyerDetails?.address?.addressLine)
    if (orderInitiateRequest?.buyerDetails?.address?.city.isNullOrEmpty()
        .not()
    ) addSlr.append(", ${orderInitiateRequest?.buyerDetails?.address?.city}")
    if (orderInitiateRequest?.buyerDetails?.address?.region.isNullOrEmpty()
        .not()
    ) addSlr.append(", ${orderInitiateRequest?.buyerDetails?.address?.region}")
    if (orderInitiateRequest?.buyerDetails?.address?.zipcode.isNullOrEmpty()
        .not()
    ) addSlr.append(", ${orderInitiateRequest?.buyerDetails?.address?.zipcode}")

    return addSlr.toString()
  }

  private fun parseDate(date: String): String? {
    try {
      val df1 = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
      val date = df1.parse(date)
      val df2 = SimpleDateFormat(DateUtils.SPA_REVIEW_DATE_FORMAT, Locale.getDefault())
      return df2.format(date)
    } catch (e: Exception) {
    }
    return ""
  }



  private fun preparePaymentStatusOptions() {
    orderBottomSheet.title = getString(R.string.str_payment_status)

    val optionsList = ArrayList<BottomSheetOptionsItem>()

    val bottomSheetOptionsItem2 = BottomSheetOptionsItem()
    bottomSheetOptionsItem2.title = getString(R.string.playment_already_received)
    bottomSheetOptionsItem2.description = getString(R.string.customer_paid_via_cash_card_upi)
    bottomSheetOptionsItem2.displayValue = getString(R.string.payment_received)
    bottomSheetOptionsItem2.serverValue = PaymentDetailsN.STATUS.INITIATED.name

    val bottomSheetOptionsItem1 = BottomSheetOptionsItem()
    bottomSheetOptionsItem1.title = getString(R.string.collect_later)
    bottomSheetOptionsItem1.description = getString(R.string.send_payment_to_customer)
    bottomSheetOptionsItem1.displayValue = getString(R.string.collect_later)
    bottomSheetOptionsItem1.isChecked = true
    bottomSheetOptionsItem1.serverValue = PaymentDetailsN.STATUS.PENDING.name

    optionsList.add(bottomSheetOptionsItem1)
    optionsList.add(bottomSheetOptionsItem2)
    orderBottomSheet.items = optionsList
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