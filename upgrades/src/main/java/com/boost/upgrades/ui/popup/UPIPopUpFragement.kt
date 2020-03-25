package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.upgrades.R
import com.boost.upgrades.ui.payment.PaymentViewModel
import kotlinx.android.synthetic.main.add_upi_popup.*
import org.json.JSONObject

class UPIPopUpFragement : DialogFragment() {

    lateinit var root: View
    private lateinit var viewModel: PaymentViewModel

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.add_upi_popup, container, false)

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        upi_popup_outer_layout.setOnClickListener {
            dialog!!.dismiss()
        }
        upi_popup_container_layout.setOnClickListener {}

        upi_popup_submit.setOnClickListener {
            upiPaymentRazorpay()
        }
    }

    fun upiPaymentRazorpay(){
        val data = JSONObject()
        data.put("method", "upi")
        data.put("vpa", upi_popup_value.text.toString())
        viewModel.UpdateUPIPaymentData(data)
        dialog!!.dismiss()
        clearData()
    }

    fun clearData(){
        upi_popup_value.text.clear()
    }
}