package com.inventoryorder.ui.createAptOld

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inventoryorder.databinding.FragmentBookingSuccessfulBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.ui.BaseInventoryFragment

class BookingSuccessfulFragment : BaseInventoryFragment<FragmentBookingSuccessfulBinding>(){

    var orderId: String? = null
    var name: String? = null
    var startTimeDate: String? = null
    var number: String?= null
    var email: String? = null
    var serviceName: String? = null

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
        serviceName = arguments?.getString("SERVICE_NAME")
        setData()
        setOnClickListener(binding?.tvHome)
    }

    private fun setData() {
        binding?.tvOrderId?.text = "#$orderId"
        binding?.tvName?.text = name
        binding?.tvStartTimeDate?.text = startTimeDate
        binding?.tvNumber?.text = "+91-$number"
        binding?.tvEmail?.text = email
        binding?.serviceName?.text = serviceName
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