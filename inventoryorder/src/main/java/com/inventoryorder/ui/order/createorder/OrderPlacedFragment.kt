package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentOrderDetailBinding
import com.inventoryorder.databinding.FragmentOrderPlacedBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.order.INVOICE_URL
import com.inventoryorder.ui.order.OrderInvoiceFragment
import com.inventoryorder.ui.startFragmentOrderActivity

class OrderPlacedFragment : BaseInventoryFragment<FragmentOrderPlacedBinding>() {

    var shouldReInitiate = false
    var shouldRefresh = false
    var type : String ?= null
    var orderId : String ?= null
    var orderResponse : OrderItem ?= null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): OrderPlacedFragment {
            val fragment = OrderPlacedFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

       // orderInitiateResponse = arguments?.getSerializable(IntentConstant.CREATE_ORDER_RESPONSE.name) as OrderInitiateResponse?
        type = arguments?.getString(IntentConstant.TYPE_APPOINTMENT.name)
        orderId = arguments?.getString(IntentConstant.ORDER_ID.name)

        getOrderDetails()

       // if (orderInitiateResponse != null) setData()

        setOnClickListener(binding?.buttonInitiateNewOrder, binding?.textInvoice)
    }

    private fun setData(orderItem: OrderItem) {

        if (type.equals(AppConstant.TYPE_APPOINTMENT, true)) {
            binding?.tvName?.text = getString(R.string.appointment_booked_and_confirmed_successfully)

            binding?.linearPaymentStatus?.visibility = View.VISIBLE
            binding?.textPaymentStatus?.text = orderItem?.PaymentDetails?.Status ?: ""
            binding?.textOrderIdValue?.text = orderItem?.ReferenceNumber ?: ""
            binding?.textName?.text = orderItem?.BuyerDetails?.ContactDetails?.FullName ?: ""
            binding?.textTotalAmount?.text = "${orderItem?.BillingDetails?.CurrencyCode} ${orderItem?.BillingDetails?.GrossAmount}"

            binding?.linearItemQty?.visibility = View.GONE
            binding?.linearPaymentMode?.visibility = View.GONE
            binding?.linearDeliveryStatus?.visibility = View.GONE
            binding?.linearDeliveryType?.visibility = View.GONE
            binding?.appointmentText?.visibility = View.VISIBLE

            binding?.buttonConfirmOrder?.text = getString(R.string.view_appointment_details)
            binding?.buttonInitiateNewOrder?.text = getString(R.string.view_appointment_dashboard)
        } else {
            binding?.textOrderIdValue?.text = orderItem?.ReferenceNumber ?: ""
            binding?.textName?.text = orderItem?.BuyerDetails?.ContactDetails?.FullName ?: ""
            binding?.textCount?.text = orderItem?.Items?.size.toString() ?: ""
            binding?.textPaymentLink?.text = orderItem?.PaymentDetails?.Method ?: ""
            binding?.textDeliveryStatus?.text = orderItem?.LogisticsDetails?.Status ?: ""
            binding?.textDeliveryType?.text = orderItem?.LogisticsDetails?.DeliveryMode ?: ""
            binding?.textTotalAmount?.text = "${orderItem?.BillingDetails?.CurrencyCode} ${orderItem?.BillingDetails?.GrossAmount}"
        }

    }

    fun getBundleData(): Bundle? {
        val bundle = Bundle()
        if (type.equals(AppConstant.TYPE_APPOINTMENT, true)) bundle.putBoolean(IntentConstant.IS_REFRESH.name, shouldRefresh)
        shouldReInitiate?.let {
            bundle.putBoolean(IntentConstant.SHOULD_REINITIATE.name, shouldReInitiate)
        }
        return bundle
    }

    private fun getOrderDetails() {
        showProgress()
        viewModel?.assuredPurchaseGetOrderDetails(preferenceData?.clientId, orderId)?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()
            if (it.error is NoNetworkException) {
                showShortToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }
            if (it.isSuccess()) {
                orderResponse = (it as? OrderDetailResponse)?.Data
                setData(orderItem = orderResponse!!)
              /*  var bundle = Bundle()
                bundle.putSerializable(IntentConstant.ORDER_ID.name, orderId)
                bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
                startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)*/
            } else {
                showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
            }
        })
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v) {
            binding?.buttonInitiateNewOrder -> {
                shouldReInitiate = true
                if (type.equals(AppConstant.TYPE_APPOINTMENT, true)) shouldRefresh = true
                (activity as FragmentContainerOrderActivity).onBackPressed()
            }

            binding?.textInvoice -> {
                var bundle = Bundle()
                bundle.putSerializable(INVOICE_URL, orderResponse?.BillingDetails?.InvoiceUrl)
                startFragmentOrderActivity(FragmentType.ORDER_INVOICE_VIEW, bundle)
            }
        }
    }
}