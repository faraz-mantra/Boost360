package com.boost.upgrades.ui.checkoutkyc

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.customerId.customerInfo.AddressDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.BusinessDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.customerInfo.TaxDetails
import com.boost.upgrades.data.api_model.customerId.get.Result
import com.boost.upgrades.interfaces.BusinessDetailListener
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.ui.popup.StateListPopFragment
import com.boost.upgrades.utils.Constants.Companion.STATE_LIST_FRAGMENT
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils.isValidGSTIN
import com.boost.upgrades.utils.Utils.isValidMail
import com.boost.upgrades.utils.Utils.isValidMobile
import com.boost.upgrades.utils.observeOnce
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.businessdetails_fragment.*
import kotlinx.android.synthetic.main.businessdetails_fragment.business_address
import kotlinx.android.synthetic.main.businessdetails_fragment.business_name_value
import kotlinx.android.synthetic.main.payment_fragment.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class BusinessDetailsFragment : DialogFragment() {

    var createCustomerInfoRequest: Result? = null

    var customerInfoState = false
    val stateFragment = StateListPopFragment()
    lateinit var prefs: SharedPrefs


    companion object {
        lateinit var listener: BusinessDetailListener
        fun newInstance(businessDetailListener: BusinessDetailListener) = BusinessDetailsFragment().apply {
            listener = businessDetailListener
        }
    }

//    private lateinit var viewModel: CheckoutKycViewModel
    private lateinit var viewModel: PaymentViewModel
    var gstFlag = true

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.businessdetails_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(requireActivity()).get(CheckoutKycViewModel::class.java)
        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        prefs = SharedPrefs(activity as UpgradeActivity)
        loadCustomerInfo()
        initMvvm()
        viewModel.getCitiesFromAssetJson(requireActivity())
        viewModel.getStatesFromAssetJson(requireActivity())
//        business_gst_number.setFilters(business_gst_number.filters + InputFilter.AllCaps())

        confirm_btn.setOnClickListener {
            if (validateAgreement()) {
                if (!customerInfoState) { //no customer available
                    //create customer payment profile
                    viewModel.createCustomerInfo(CreateCustomerInfoRequest(
                            AddressDetails(
                                    if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                                    "india",
                                if (business_address.text.isEmpty()) null else business_address.text.toString(),
                                    null,
                                if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                                    null
                            ),
                            BusinessDetails(
                                    "+91",
                                    if (business_email_address.text.isEmpty()) null else business_email_address.text.toString(),
                                    if (business_contact_number.text.isEmpty()) null else business_contact_number.text.toString()
                            ),
                            (activity as UpgradeActivity).clientid,
                            "+91",
                            "ANDROID",
                            "",
                            (activity as UpgradeActivity).fpid!!,
                            if (business_contact_number.text.isEmpty()) null else business_contact_number.text.toString(),
                        if (business_name_value.text.isEmpty()) null else business_name_value.text.toString(),
                            TaxDetails(
                                if (business_gstin_number.text.isEmpty()) null else business_gstin_number.text.toString(),
                                    null,
                                    null,
                                    null
                            )

                    ))
                } else {
                    //update customer payment profile
                    viewModel.updateCustomerInfo(CreateCustomerInfoRequest(
                            AddressDetails(
                                    if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                                    "india",
                                if (business_address.text.isEmpty()) null else business_address.text.toString(),
                                    null,
                                if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                                    null
                            ),
                            BusinessDetails(
                                    "+91",
                                    if (business_email_address.text.isEmpty()) null else business_email_address.text.toString(),
                                    if (business_contact_number.text.isEmpty()) null else business_contact_number.text.toString()
                            ),
                            (activity as UpgradeActivity).clientid,
                            "+91",
                            "ANDROID",
                            "",
                            (activity as UpgradeActivity).fpid,
                            if (business_contact_number.text.isEmpty()) null else business_contact_number.text.toString(),
                        if (business_name_value.text.isEmpty()) null else business_name_value.text.toString(),
                            TaxDetails(
                                if (business_gstin_number.text.isEmpty()) null else business_gstin_number.text.toString(),
                                    null,
                                    null,
                                    null
                            )

                    ))
                }
            }
        }
        prefs.storeGstRegistered(true)
        close.setOnClickListener {
            dismiss()
//            viewModel.updatesBusinessPopup(true)
        }

        gstin_on.setOnClickListener {
            prefs.storeGstRegistered(false)
            gstFlag = false
            business_gstin_number.visibility = View.GONE
            gstin_on.visibility = View.GONE
            gstin_off.visibility = View.VISIBLE
        }

        gstin_off.setOnClickListener {
            prefs.storeGstRegistered(true)
            gstFlag = true
            business_gstin_number.visibility = View.VISIBLE
            gstin_off.visibility = View.GONE
            gstin_on.visibility = View.VISIBLE
        }

        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.ACTION_UP) {
//                Toasty.info(requireContext(), "Accept Any One Condition...", Toast.LENGTH_LONG).show()
                return@setOnKeyListener true
            }
            false
        }
        business_city_name.setOnClickListener{
            stateFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                STATE_LIST_FRAGMENT
            )
        }
    }

    private fun validateAgreement(): Boolean {
        if (business_contact_number.text.isEmpty() || business_email_address.text.isEmpty() || business_city_name.text.isEmpty()
        || business_name_value.text.isEmpty()|| business_address.text.isEmpty() || (gstFlag && business_gstin_number.text.isEmpty() ) ) {
            Log.v("business_name_value", " "+ business_name_value.text.toString())
//            Toasty.error(requireContext(), "Fields are Empty!!", Toast.LENGTH_LONG).show()
            if (business_gstin_number.text.isEmpty() && !isValidGSTIN(business_gstin_number.text.toString()) && gstFlag) {
                business_gstin_number.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
                return false
            }else{
                business_gstin_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }

            if (!isValidMobile(business_contact_number.text.toString()) /*|| !isValidMobile(user_contact_number.text.toString())*/) {
                business_contact_number.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG).show()
                return false
            }else{
                business_contact_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }
            if (!isValidMail(business_email_address.text.toString()) /*|| !isValidMail(user_email_address.text.toString())*/) {
                business_email_address.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Entered EmailId is not valid!!", Toast.LENGTH_LONG).show()
                return false
            }else{
                business_email_address.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }

            Log.v("business_name_value1", " "+ business_name_value.text.toString())
            if (business_name_value.text.isEmpty()) {
                business_name_value.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Entered Business name is not valid!!", Toast.LENGTH_LONG).show()
                return false
            }else{
                business_name_value.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }
            if (business_address.text.isEmpty()) {
                business_address.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Entered Business address is not valid!!", Toast.LENGTH_LONG).show()
                return false
            }else{
                business_address.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }
            return false
        }
        if (!business_gstin_number.text.isEmpty() && !isValidGSTIN(business_gstin_number.text.toString()) && gstFlag) {
            business_gstin_number.setBackgroundResource(R.drawable.et_validity_error)
            Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
            return false
        }

        /*if (!confirm_checkbox.isChecked) {
            Toasty.error(requireContext(), "Accept the Agreement!!", Toast.LENGTH_LONG).show()
            return false
        }*/
        return true
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.getCustomerInfoResult().observeOnce(viewLifecycleOwner, Observer {
            createCustomerInfoRequest = it.Result
            if (createCustomerInfoRequest != null) {
                if (createCustomerInfoRequest!!.BusinessDetails != null) {
                    business_contact_number.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
                    business_email_address.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
                }
                if (createCustomerInfoRequest!!.AddressDetails != null) {
                    business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.City)

                    if(createCustomerInfoRequest!!.AddressDetails!!.City != null){
                        viewModel.getStateFromCityAssetJson(requireActivity(),createCustomerInfoRequest!!.AddressDetails!!.City)
                    }
                    if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
                        business_address.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                    }
                }
                if (createCustomerInfoRequest!!.TaxDetails != null) {
                    business_gstin_number.setText(createCustomerInfoRequest!!.TaxDetails!!.GSTIN)
                }
                if (createCustomerInfoRequest!!.Name != null) {
                    business_name_value.setText(createCustomerInfoRequest!!.Name)
                }

//                user_contact_number.setText(createCustomerInfoRequest!!.MobileNumber)
//                user_email_address.setText(createCustomerInfoRequest!!.Email)
            }
        })
        viewModel.getCustomerInfoStateResult().observeOnce(this, Observer {
            customerInfoState = it
        })

        viewModel.getUpdatedCustomerBusinessResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG).show()
                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
            }
            dismiss()
        })
        viewModel.getUpdatedResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Created Profile.", Toast.LENGTH_LONG).show()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG).show()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
            }
            dismiss()
        })

        viewModel.cityValueResult().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                business_city_name.setText(it)
            }

        })

        viewModel.stateResult().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
//                business_city_name.setAdapter(adapter)
            }

        })

        viewModel.getSelectedStateResult().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                business_city_name.text = it
            }

        })

    }

    private fun loadCustomerInfo() {
        viewModel.getCustomerInfo((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().viewModelStore.clear()
//        this.viewModelStore.clear()
        listener.backListener(true)
    }
}
