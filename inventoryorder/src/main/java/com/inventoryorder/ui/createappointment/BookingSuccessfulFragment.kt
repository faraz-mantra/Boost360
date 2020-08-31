package com.inventoryorder.ui.createappointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentBookingSuccessfulBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.ui.BaseInventoryFragment

class BookingSuccessfulFragment : BaseInventoryFragment<FragmentBookingSuccessfulBinding>(){

    var orderId: String? = null
    var name: String? = null
    var startTimeDate: String? = null
    var number: String?= null
    var email: String? = null

    private var orderItem: OrderItem? = null

    companion object{
        fun newInstance(bundle: Bundle? = null) : BookingSuccessfulFragment{
            val fragment = BookingSuccessfulFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        orderId = arguments?.getString("ORDER_ID")
        name = arguments?.getString("NAME")
        startTimeDate = arguments?.getString("START_TIME_DATE")
        number = arguments?.getString("NUMBER")
        email = arguments?.getString("EMAIL")

        setData()

        setOnClickListener(binding?.tvHome)
    }

    private fun setData() {
        binding?.tvOrderId?.text = "#$orderId"
        binding?.tvName?.text = name
        binding?.tvStartTimeDate?.text = startTimeDate
        binding?.tvNumber?.text = "+91-$number"
        binding?.tvEmail?.text = email
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.tvHome -> {
                baseActivity.setResult(AppCompatActivity.RESULT_OK)
                baseActivity.finish()
            }
        }
    }

    fun getBundleData(): Bundle {
        return Bundle()
    }
}