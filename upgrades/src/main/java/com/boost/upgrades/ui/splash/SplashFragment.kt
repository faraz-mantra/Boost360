package com.boost.upgrades.ui.splash

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.AddressDetails
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.BusinessDetails
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.TaxDetails
import com.boost.dbcenterapi.data.api_model.customerId.get.Result
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Utils.isValidGSTIN
import com.boost.upgrades.utils.Utils.isValidMail
import com.boost.upgrades.utils.Utils.isValidMobile
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.splash_fragment.*

class SplashFragment : DialogFragment() {

  var createCustomerInfoRequest: Result? = null

  var customerInfoState = false

  companion object {
    fun newInstance() = SplashFragment()
  }

  private lateinit var viewModel: SplashViewModel

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
    return inflater.inflate(R.layout.splash_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(SplashViewModel::class.java)

    loadCustomerInfo()
    initMvvm()

    business_gst_number.setFilters(business_gst_number.filters + InputFilter.AllCaps())

    confirm_btn.setOnClickListener {
      if (validateAgreement()) {
        if (!customerInfoState) { //no customer available
          //create customer payment profile
          viewModel.createCustomerInfo((activity as? UpgradeActivity)?.getAccessToken()?:"",
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
              user_email_address.text.toString(),
              (activity as UpgradeActivity).fpid!!,
              user_contact_number.text.toString(),
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
          viewModel.updateCustomerInfo((activity as? UpgradeActivity)?.getAccessToken()?:"",
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
              user_email_address.text.toString(),
              (activity as UpgradeActivity).fpid,
              if (user_contact_number.text.isEmpty()) null else user_contact_number.text.toString(),
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

    cancle_button.setOnClickListener {
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
      || user_contact_number.text.isEmpty() || user_email_address.text.isEmpty()
    ) {
      Toasty.error(requireContext(), "Fields are Empty!!", Toast.LENGTH_LONG).show();
      return false
    }
    if (!isValidMobile(business_contact_number.text.toString()) || !isValidMobile(
        user_contact_number.text.toString()
      )
    ) {
      Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
        .show()
      return false
    }
    if (!isValidMail(business_email_address.text.toString()) || !isValidMail(user_email_address.text.toString())) {
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
        user_contact_number.setText(createCustomerInfoRequest!!.MobileNumber)
        user_email_address.setText(createCustomerInfoRequest!!.Email)
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
  }

  private fun loadCustomerInfo() {
    viewModel.getCustomerInfo(
      (activity as? UpgradeActivity)?.getAccessToken()?:"",
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }

}
