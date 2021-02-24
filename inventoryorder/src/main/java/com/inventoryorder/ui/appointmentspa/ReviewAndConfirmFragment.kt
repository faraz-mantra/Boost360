package com.inventoryorder.ui.appointmentspa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.utils.DateUtils
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentReviewAndConfirmBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.orderRequest.PaymentDetails
import com.inventoryorder.model.orderRequest.ShippingDetails
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.order.sheetOrder.AddDeliveryFeeBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.CreateOrderBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ReviewAndConfirmFragment : BaseInventoryFragment<FragmentReviewAndConfirmBinding>() {

    private val GST_PERCENTAGE = 1.18
    private var serviceFee = 0.0
    private var totalPrice = 0.0
    private var discountedPrice = 0.0
    private var orderInitiateRequest : OrderInitiateRequest?= null
    var orderBottomSheet = OrderBottomSheet()
    private var paymentStatus : String = PaymentDetailsN.STATUS.PENDING.name
    private var selectedService : ServiceItem ?= null
    var shouldReInitiate = false
    var shouldRefresh = false
    var prefData : PreferenceData ?= null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ReviewAndConfirmFragment {
            val fragment = ReviewAndConfirmFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun getBundleData(): Bundle? {
        val bundle = Bundle()
        shouldReInitiate?.let {
            bundle.putBoolean(IntentConstant.SHOULD_REINITIATE.name, shouldReInitiate)
            bundle.putBoolean(IntentConstant.IS_REFRESH.name, shouldRefresh)
        }
        return bundle
    }

    override fun onCreateView() {
        setOnClickListener(binding?.textAdd, binding?.buttonReviewDetails, binding?.tvPaymentStatus, binding?.textEdit)
        orderInitiateRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
        totalPrice = arguments?.getDouble(IntentConstant.TOTAL_PRICE.name) ?: 0.0
        discountedPrice = arguments?.getDouble(IntentConstant.DISCOUNTED_PRICE.name) ?: 0.0
        selectedService = arguments?.getSerializable(IntentConstant.SELECTED_SERVICE.name) as ServiceItem
        prefData = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as PreferenceData
       // orderInitiateRequest?.sellerID = preferenceData?.fpTag.toString()

        preparePaymentStatusOptions()
        setData()
    }

    private fun setData() {
        binding?.serviceName?.text = orderInitiateRequest?.items?.get(0)?.productDetails?.name?.capitalizeUtil() ?: ""
        binding?.textServiceDuration?.text = "${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.duration ?: 0} minutes"
        binding?.textStaffName?.text = "by ${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.staffName}"
        binding?.textDateTimeSlot?.text = "${parseDate(orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.scheduledDateTime!!)}, ${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.get(0)?.startTime}"

        binding?.tvName?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.fullName?.capitalizeUtil()
        binding?.tvEmail?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.emailId
        binding?.textAmount?.text = "${selectedService?.Currency} $totalPrice"
        binding?.textActualAmount?.text =  "${selectedService?.Currency} $discountedPrice"
        binding?.textGstAmount?.text = "${selectedService?.Currency} ${calculateGST(discountedPrice + serviceFee)}"
       // binding?.textTotalPayableValue?.text = "${selectedService?.Currency} ${(discountedPrice + calculateGST(discountedPrice))}"
        binding?.textTotalPayableValue?.text = "${selectedService?.Currency} $discountedPrice"


        if (orderInitiateRequest?.buyerDetails?.GSTIN != null && orderInitiateRequest?.buyerDetails?.GSTIN?.isNotEmpty()==true) {
            binding?.tvGstin?.text = "GSTIN : ${orderInitiateRequest?.buyerDetails?.GSTIN}"
            binding?.tvGstin?.visibility = View.VISIBLE
        }

        orderInitiateRequest?.gstCharges = calculateGST(discountedPrice)
        var paymentDetails = PaymentDetails(method = PaymentDetailsN.METHOD.COD.type, status = paymentStatus)
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

        binding?.tvPhone?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.primaryContactNumber.toString()
        binding?.tvAddress?.text = setUpAddress()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            binding?.textAdd -> {
                showAddServiceFeeDialog()
            }

            binding?.buttonReviewDetails -> {

                var shippingDetails = ShippingDetails(shippedBy = ShippingDetails.ShippedBy.SELLER.name,
                        deliveryMode = OrderSummaryRequest.DeliveryMode.OFFLINE.name, shippingCost = serviceFee,
                        currencyCode = orderInitiateRequest?.items?.get(0)?.productDetails?.currencyCode)
                orderInitiateRequest?.shippingDetails = shippingDetails


                createAppointment()

            /*    //TODO : to be removed after dry run
                var orderInitiateResponse = OrderInitiateResponse(statusN = "SUCCESS", data = OrderItem(), messageN = "")
                var bundle = Bundle()
                bundle.putString(IntentConstant.TYPE_APPOINTMENT.name, "appt")
                bundle.putSerializable(IntentConstant.CREATE_ORDER_RESPONSE.name, orderInitiateResponse)
                startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)*/
            }

            binding?.textEdit -> {
                showAddServiceFeeDialog()
            }

            binding?.tvPaymentStatus -> {
                val createOrderBottomSheetDialog = CreateOrderBottomSheetDialog(orderBottomSheet)
                createOrderBottomSheetDialog.onClicked = this::onPaymentStatusSelected
                createOrderBottomSheetDialog.show(this.parentFragmentManager, CreateOrderBottomSheetDialog::class.java.name)
            }
        }
    }

    private fun onPaymentStatusSelected(bottomSheetOptionsItem : BottomSheetOptionsItem, orderBottomSheet : OrderBottomSheet) {
        binding?.tvPaymentStatus?.text = bottomSheetOptionsItem?.displayValue
        orderInitiateRequest?.paymentDetails?.status = bottomSheetOptionsItem?.serverValue
        paymentStatus = bottomSheetOptionsItem?.serverValue!!
        this.orderBottomSheet = orderBottomSheet
    }

    private fun showAddServiceFeeDialog() {
        val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(serviceFee, AppConstant.TYPE_APPOINTMENT)
        addDeliveryFeeBottomSheetDialog.onClicked = { onServiceFeeAdded(it) }
        addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
    }

    private fun onServiceFeeAdded(fee : Double) {
        serviceFee = fee
        binding?.textGstAmount?.text = "${selectedService?.Currency} ${calculateGST(discountedPrice + serviceFee)}"

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
        viewModel?.postAppointment(AppConstant.CLIENT_ID_2, orderInitiateRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.error is NoNetworkException) {
                hideProgress()
                showLongToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.isSuccess()) {
                hideProgress()

                /*showShortToast(getString(R.string.appointment_created_successfully))
                shouldReInitiate = true
                shouldRefresh = true
                (activity as FragmentContainerOrderActivity).onBackPressed()*/

               /* var orderInitiateResponse = (it as? OrderInitiateResponse)
                var bundle = Bundle()
                bundle.putString(IntentConstant.TYPE_APPOINTMENT.name, "appt")
                bundle.putSerializable(IntentConstant.CREATE_ORDER_RESPONSE.name, orderInitiateResponse)
                startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)*/

                var orderInitiateResponse = (it as? OrderInitiateResponse)
                apiConfirmOrder(orderInitiateResponse?.data!!)
            } else {
                hideProgress()
                showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
            }
        })
    }

    private fun apiConfirmOrder(order: OrderItem?) {
        showProgress()
        viewModel?.confirmOrder(prefData?.clientId, order?._id)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            hideProgress()
            if (it.error is NoNetworkException) {
                showShortToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.isSuccess()) {

                var bundle = Bundle()
                bundle.putString(IntentConstant.TYPE_APPOINTMENT.name, "appt")
                bundle.putSerializable(IntentConstant.ORDER_ID.name, order?._id)
                bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, prefData)
                startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)

             /*   showShortToast(getString(R.string.appointment_created_successfully))
                shouldReInitiate = true
                shouldRefresh = true
                (activity as FragmentContainerOrderActivity).onBackPressed()*/
            } else {
                showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
            }
        })
    }

    private fun setUpAddress() : String {
        var addrStr = StringBuilder()
        addrStr.append(orderInitiateRequest?.buyerDetails?.address?.addressLine)
        if (orderInitiateRequest?.buyerDetails?.address?.city.isNullOrEmpty().not()) addrStr.append(", ${orderInitiateRequest?.buyerDetails?.address?.city}")
        if (orderInitiateRequest?.buyerDetails?.address?.region.isNullOrEmpty().not()) addrStr.append(", ${orderInitiateRequest?.buyerDetails?.address?.region}")
        if (orderInitiateRequest?.buyerDetails?.address?.zipcode.isNullOrEmpty().not()) addrStr.append(", ${orderInitiateRequest?.buyerDetails?.address?.zipcode}")

        return addrStr.toString()
    }

    private fun parseDate(date : String) : String? {
        try {
            val df1 = SimpleDateFormat(DateUtils.FORMAT_YYYY_MM_DD, Locale.getDefault())
            var date = df1.parse(date)
            val df2 = SimpleDateFormat(DateUtils.SPA_REVIEW_DATE_FORMAT, Locale.getDefault())
            return df2.format(date)
        } catch(e: Exception) {}

        return ""
    }

    private fun calculateGST(amount : Double) : Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format((amount - (df.format(amount / GST_PERCENTAGE).toDouble()))).toDouble()
    }

    private fun preparePaymentStatusOptions() {
        orderBottomSheet.title = getString(R.string.str_payment_status)

        var optionsList = ArrayList<BottomSheetOptionsItem>()

        var bottomSheetOptionsItem2 = BottomSheetOptionsItem()
        bottomSheetOptionsItem2.title = getString(R.string.playment_already_received)
        bottomSheetOptionsItem2.description = getString(R.string.customer_paid_via_cash_card_upi)
        bottomSheetOptionsItem2.displayValue = getString(R.string.payment_received)
        bottomSheetOptionsItem2.serverValue = PaymentDetailsN.STATUS.INITIATED.name

        var bottomSheetOptionsItem1 = BottomSheetOptionsItem()
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
            shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_REINITIATE.name)!!
            if (shouldReInitiate != null && shouldReInitiate) {
                (context as FragmentContainerOrderActivity).onBackPressed()
            }
        }
    }
}