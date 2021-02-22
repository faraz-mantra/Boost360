package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentOrderDetailBinding
import com.inventoryorder.databinding.FragmentOrderPlacedBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.order.OrderInvoiceFragment
import com.inventoryorder.ui.startFragmentOrderActivity

class OrderPlacedFragment : BaseInventoryFragment<FragmentOrderPlacedBinding>() {

    var shouldReInitiate = false
    var type : String ?= null
    var orderInitiateResponse: OrderInitiateResponse? = null

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

        orderInitiateResponse = arguments?.getSerializable(IntentConstant.CREATE_ORDER_RESPONSE.name) as OrderInitiateResponse?
        type = arguments?.getString(IntentConstant.TYPE_APPOINTMENT.name)

        if (orderInitiateResponse != null) setData()

        setOnClickListener(binding?.buttonInitiateNewOrder)
    }

    private fun setData() {

        if (type.equals("appt", true)) {
            binding?.tvName?.text = getString(R.string.appointment_booked_and_confirmed_successfully)

            binding?.linearPaymentStatus?.visibility = View.VISIBLE
            binding?.textPaymentStatus?.text = orderInitiateResponse?.data?.PaymentDetails?.Status ?: ""
            binding?.textOrderIdValue?.text = orderInitiateResponse?.data?.ReferenceNumber ?: ""
            binding?.textName?.text = orderInitiateResponse?.data?.BuyerDetails?.ContactDetails?.FullName ?: ""
            binding?.textTotalAmount?.text = "${orderInitiateResponse?.data?.BillingDetails?.CurrencyCode} ${orderInitiateResponse?.data?.BillingDetails?.GrossAmount}"

            binding?.linearItemQty?.visibility = View.GONE
            binding?.linearPaymentMode?.visibility = View.GONE
            binding?.linearDeliveryStatus?.visibility = View.GONE
            binding?.linearDeliveryType?.visibility = View.GONE
            binding?.appointmentText?.visibility = View.VISIBLE

            binding?.buttonConfirmOrder?.text = getString(R.string.view_appointment_details)
            binding?.buttonInitiateNewOrder?.text = getString(R.string.view_appointment_dashboard)
        } else {
            binding?.textOrderIdValue?.text = orderInitiateResponse?.data?.ReferenceNumber ?: ""
            binding?.textName?.text = orderInitiateResponse?.data?.BuyerDetails?.ContactDetails?.FullName ?: ""
            binding?.textCount?.text = orderInitiateResponse?.data?.Items?.size.toString() ?: ""
            binding?.textPaymentLink?.text = orderInitiateResponse?.data?.PaymentDetails?.OnlinePaymentProvider ?: ""
            binding?.textDeliveryStatus?.text = orderInitiateResponse?.data?.LogisticsDetails?.Status ?: ""
            binding?.textDeliveryType?.text = orderInitiateResponse?.data?.LogisticsDetails?.DeliveryMode ?: ""
            binding?.textTotalAmount?.text = "${orderInitiateResponse?.data?.BillingDetails?.CurrencyCode} ${orderInitiateResponse?.data?.BillingDetails?.GrossAmount}"
        }

    }

    fun getBundleData(): Bundle? {
        val bundle = Bundle()
        shouldReInitiate?.let {
            bundle.putBoolean(IntentConstant.SHOULD_REINITIATE.name, shouldReInitiate)
        }
        return bundle
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v) {
            binding?.buttonInitiateNewOrder -> {
                shouldReInitiate = true
                (activity as FragmentContainerOrderActivity).onBackPressed()
            }
        }
    }
}