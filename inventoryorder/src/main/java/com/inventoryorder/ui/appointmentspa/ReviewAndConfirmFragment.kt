package com.inventoryorder.ui.appointmentspa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.models.firestore.FirestoreManager.convert
import com.framework.utils.DateUtils
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentReviewAndConfirmBinding
import com.inventoryorder.databinding.FragmentSpaAppointmentBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.sheetOrder.AddDeliveryFeeBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.CreateOrderBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReviewAndConfirmFragment : BaseInventoryFragment<FragmentReviewAndConfirmBinding>() {

    private val GST_PERCENTAGE = 0.05
    private var serviceFee = 0.0
    private var totalPrice = 0.0
    private var discountedPrice = 0.0
    private var orderInitiateRequest : OrderInitiateRequest?= null
    var orderBottomSheet = OrderBottomSheet()
    private var paymentStatus : String = PaymentDetailsN.STATUS.PENDING.name

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ReviewAndConfirmFragment {
            val fragment = ReviewAndConfirmFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        setOnClickListener(binding?.textAdd, binding?.buttonReviewDetails, binding?.tvPaymentStatus)
        orderInitiateRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
        totalPrice = arguments?.getDouble(IntentConstant.TOTAL_PRICE.name) ?: 0.0
        discountedPrice = arguments?.getDouble(IntentConstant.DISCOUNTED_PRICE.name) ?: 0.0
       // orderInitiateRequest?.sellerID = preferenceData?.fpTag.toString()

        preparePaymentStatusOptions()
        setData()
    }

    private fun setData() {
        binding?.serviceName?.text = orderInitiateRequest?.items?.get(0)?.productDetails?.name ?: ""
        binding?.textServiceDuration?.text = "${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.duration ?: 0} minutes"
        binding?.textStaffName?.text = orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.staffName
        binding?.textDateTimeSlot?.text = "${parseDate(orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.scheduledDateTime!!)}, ${orderInitiateRequest?.items?.get(0)?.productDetails?.extraProperties?.appointment?.startTime}"

        binding?.tvName?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.fullName
        binding?.tvEmail?.text = orderInitiateRequest?.buyerDetails?.contactDetails?.emailId
        binding?.textAmount?.text = totalPrice.toString()
        binding?.textActualAmount?.text = discountedPrice.toString()
        binding?.textGstAmount?.text = calculateGST(discountedPrice).toString()
        binding?.textTotalPayableValue?.text = (discountedPrice - calculateGST(discountedPrice)).toString()

        if (orderInitiateRequest?.buyerDetails?.contactDetails?.emailId.isNullOrBlank()) {
            binding?.tvEmail?.visibility = View.GONE
        } else {
            binding?.tvEmail?.visibility = View.VISIBLE
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
                createAppointment()
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
        val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(serviceFee)
        addDeliveryFeeBottomSheetDialog.onClicked = { onServiceFeeAdded(it) }
        addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
    }

    private fun onServiceFeeAdded(fee : Double) {

    }

    private fun createAppointment() {
        showProgress()
        viewModel?.postOrderInitiate(AppConstant.CLIENT_ID_2, orderInitiateRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.error is NoNetworkException) {
                hideProgress()
                showLongToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.isSuccess()) {
                hideProgress()
                var orderInitiateResponse = (it as? OrderInitiateResponse)
                var bundle = Bundle()
                bundle.putSerializable(IntentConstant.CREATE_ORDER_RESPONSE.name, orderInitiateResponse)
                startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)
            } else {
                hideProgress()
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
        return amount * GST_PERCENTAGE
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
}