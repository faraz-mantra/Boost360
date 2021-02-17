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
import com.inventoryorder.ui.order.OrderInvoiceFragment
import com.inventoryorder.ui.startFragmentOrderActivity

class OrderPlacedFragment : BaseInventoryFragment<FragmentOrderPlacedBinding>() {

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
        if (orderInitiateResponse != null) setData()

        setOnClickListener(binding?.buttonInitiateNewOrder)
    }

    private fun setData() {
        binding?.textOrderIdValue?.text = orderInitiateResponse?.data?.ReferenceNumber ?: ""
        binding?.textName?.text = orderInitiateResponse?.data?.BuyerDetails?.ContactDetails?.FullName ?: ""
        binding?.textCount?.text = orderInitiateResponse?.data?.Items?.size.toString() ?: ""
        binding?.textPaymentLink?.text = orderInitiateResponse?.data?.PaymentDetails?.OnlinePaymentProvider ?: ""
        binding?.textDeliveryStatus?.text = orderInitiateResponse?.data?.LogisticsDetails?.Status ?: ""
        binding?.textDeliveryType?.text = orderInitiateResponse?.data?.LogisticsDetails?.DeliveryMode ?: ""
        binding?.textTotalAmount?.text = "${orderInitiateResponse?.data?.BillingDetails?.CurrencyCode} ${orderInitiateResponse?.data?.BillingDetails?.GrossAmount}"
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v) {
            binding?.buttonInitiateNewOrder -> {
                startFragmentOrderActivity(FragmentType.CREATE_NEW_ORDER, Bundle(),)
            }
        }
    }
}