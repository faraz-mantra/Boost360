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
import com.boost.upgrades.ui.cart.CartViewModel
import com.boost.upgrades.utils.Utils.isValidGSTIN
import com.boost.upgrades.utils.Utils.isValidMail
import com.boost.upgrades.utils.Utils.isValidMobile
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.checkoutkyc_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class CheckoutKycFragment : DialogFragment() {

  var createCustomerInfoRequest: Result? = null

  var customerInfoState = false


  companion object {
    fun newInstance() = CheckoutKycFragment()
  }

  //    private lateinit var viewModel: CheckoutKycViewModel
  private lateinit var viewModel: CartViewModel

  override fun onStart() {
    super.onStart()
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.MATCH_PARENT
    dialog!!.window!!.setLayout(width, height)
    dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.checkoutkyc_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(requireActivity()).get(CheckoutKycViewModel::class.java)
    viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)


    loadCustomerInfo()
    initMvvm()
    viewModel.getCitiesFromAssetJson(activity!!)
    business_gst_number.setFilters(business_gst_number.filters + InputFilter.AllCaps())

    confirm_btn.setOnClickListener {
      if (validateAgreement()) {
        if (!customerInfoState) { //no customer available
          //create customer payment profile
          viewModel.createCustomerInfo(
            CreateCustomerInfoRequest(
              AddressDetails(
                if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                "india",
                null,
                null,
                null,
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
              null,
              TaxDetails(
                if (business_gst_number.text.isEmpty()) null else business_gst_number.text.toString(),
                null,
                null,
                null
              )

            )
          )
        } else {
          //update customer payment profile
          viewModel.updateCustomerInfo(
            CreateCustomerInfoRequest(
              AddressDetails(
                if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                "india",
                null,
                null,
                null,
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
              "",
//                            if (user_contact_number.text.isEmpty()) null else user_contact_number.text.toString(),
              createCustomerInfoRequest!!.Name,
              TaxDetails(
                if (business_gst_number.text.isEmpty()) null else business_gst_number.text.toString(),
                null,
                null,
                null
              )

            )
          )
        }
      }
    }

    close_popup.setOnClickListener {
      viewModel.updateCheckoutKycClose(true)
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

  private fun validateAgreement(): Boolean {
    if (business_contact_number.text.isEmpty() || business_email_address.text.isEmpty() || business_city_name.text.isEmpty()
    /*|| user_contact_number.text.isEmpty()|| user_email_address.text.isEmpty() */) {
      Toasty.error(requireContext(), "Fields are Empty!!", Toast.LENGTH_LONG).show();
      return false
    }
    if (!isValidMobile(business_contact_number.text.toString()) /*|| !isValidMobile(user_contact_number.text.toString())*/) {
      Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
        .show()
      return false
    }
    if (!isValidMail(business_email_address.text.toString()) /*|| !isValidMail(user_email_address.text.toString())*/) {
      Toasty.error(requireContext(), "Entered EmailId is not valid!!", Toast.LENGTH_LONG).show()
      return false
    }
    if (!business_gst_number.text.isEmpty() && !isValidGSTIN(business_gst_number.text.toString())) {
      Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
      return false
    }
    if (!confirm_checkbox.isChecked) {
      Toasty.error(requireContext(), "Accept the Agreement!!", Toast.LENGTH_LONG).show()
      return false
    }
    return true
  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun initMvvm() {
    viewModel.getCustomerInfoResult().observe(this, Observer {
      createCustomerInfoRequest = it.Result
      if (createCustomerInfoRequest != null) {
        if (createCustomerInfoRequest!!.BusinessDetails != null) {
          business_contact_number.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
          business_email_address.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
        }
        if (createCustomerInfoRequest!!.AddressDetails != null) {
          business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.City)
        }
        if (createCustomerInfoRequest!!.TaxDetails != null) {
          business_gst_number.setText(createCustomerInfoRequest!!.TaxDetails!!.GSTIN)
        }
//                user_contact_number.setText(createCustomerInfoRequest!!.MobileNumber)
//                user_email_address.setText(createCustomerInfoRequest!!.Email)
      }
    })
    viewModel.getCustomerInfoStateResult().observe(this, Observer {
      customerInfoState = it
    })

    viewModel.getUpdatedCustomerResult().observe(this, Observer {
      if (it.Result != null) {
        Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
        (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
      } else {
        Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
          .show()
        (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
      }
      dismiss()
    })
    viewModel.cityResult().observe(this, androidx.lifecycle.Observer {
      if (it != null) {
        val adapter =
          ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
        val adapter1 =
          ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
        business_city_name.setAdapter(adapter);
      }

    })

  }

  private fun loadCustomerInfo() {
    viewModel.getCustomerInfo(
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }


}
