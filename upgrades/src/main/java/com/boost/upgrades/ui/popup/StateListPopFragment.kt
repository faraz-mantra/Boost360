package com.boost.upgrades.ui.popup

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.StateListAdapter
import com.boost.upgrades.data.api_model.customerId.StateModel
import com.boost.upgrades.data.api_model.customerId.customerInfo.AddressDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.BusinessDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.customerInfo.TaxDetails
import com.boost.upgrades.data.api_model.customerId.get.Result
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.StateListener
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.utils.Utils.isValidGSTIN
import com.boost.upgrades.utils.Utils.isValidMail
import com.boost.upgrades.utils.Utils.isValidMobile
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.businessdetails_fragment.*
import kotlinx.android.synthetic.main.businessdetails_fragment.close
import kotlinx.android.synthetic.main.checkoutkyc_fragment.*
import kotlinx.android.synthetic.main.checkoutkyc_fragment.business_city_name
import kotlinx.android.synthetic.main.checkoutkyc_fragment.business_contact_number
import kotlinx.android.synthetic.main.checkoutkyc_fragment.business_email_address
import kotlinx.android.synthetic.main.checkoutkyc_fragment.confirm_btn
import kotlinx.android.synthetic.main.free_addons_fragment.*
import kotlinx.android.synthetic.main.statelist_fragment.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class StateListPopFragment : DialogFragment() ,StateListener{

    var createCustomerInfoRequest: Result? = null

    var customerInfoState = false

    lateinit var stateListAdapter: StateListAdapter


    companion object {
        fun newInstance() = StateListPopFragment()
    }

//    private lateinit var viewModel: CheckoutKycViewModel
    private lateinit var viewModel: PaymentViewModel

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statelist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(requireActivity()).get(CheckoutKycViewModel::class.java)
        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)
        stateListAdapter = StateListAdapter((activity as UpgradeActivity),ArrayList(),this)
        initializeFreeAddonsRecyclerView()
        initMvvm()
        viewModel.getStatesFromAssetJson(requireActivity())


        close.setOnClickListener {

            dismiss()
        }

        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.ACTION_UP) {
//                Toasty.info(requireContext(), "Accept Any One Condition...", Toast.LENGTH_LONG).show()
                return@setOnKeyListener true
            }
            false
        }
    }



    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {

        viewModel.stateResult().observe(this, androidx.lifecycle.Observer {
            if(it != null){
                var data = arrayListOf<StateModel>()
                it.forEach {
                    if(!data.contains(StateModel(it,"",0))){
                        data.add(StateModel(it,"",0))
                    }
//                    data.add(StateModel(it,"",0))
                }


                stateListAdapter.addupdates(data)
                stateListAdapter.notifyDataSetChanged()
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().viewModelStore.clear()
    }

    fun initializeFreeAddonsRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_state.apply {
            layoutManager = gridLayoutManager

        }
        recycler_state.adapter = stateListAdapter
    }

    override fun stateSelected(data: StateModel) {
viewModel.selectedStateResult(data.state)
        Handler().postDelayed({
            dismiss()
        }, 300)
    }
}
