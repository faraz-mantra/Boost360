package com.boost.upgrades.ui.checkoutkyc

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
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
import com.boost.upgrades.utils.WebEngageController
import com.boost.upgrades.utils.observeOnce
import com.framework.extensions.isVisible
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.businessdetails_fragment.*
import kotlinx.android.synthetic.main.businessdetails_fragment.business_address
import kotlinx.android.synthetic.main.businessdetails_fragment.business_name_value
import kotlinx.android.synthetic.main.payment_fragment.*
import java.io.*
import java.util.*


class BusinessDetailsFragment : DialogFragment() {

  var createCustomerInfoRequest: Result? = null

  var customerInfoState = false
  private var setStates: String? = null
  val stateFragment = StateListPopFragment()
  lateinit var prefs: SharedPrefs
  private var session: UserSessionManager? = null
  private var gstInfoResult: com.boost.upgrades.data.api_model.gst.Result? = null
  private var isGstApiCalled :Boolean = false


  companion object {
    lateinit var listener: BusinessDetailListener
    fun newInstance(businessDetailListener: BusinessDetailListener) =
      BusinessDetailsFragment().apply {
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

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.businessdetails_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(requireActivity()).get(CheckoutKycViewModel::class.java)
    viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

    prefs = SharedPrefs(activity as UpgradeActivity)
    session = UserSessionManager(requireActivity())
    loadCustomerInfo()
    initMvvm()
    viewModel.getCitiesFromAssetJson(requireActivity())
    viewModel.getStatesFromAssetJson(requireActivity())

    if(gstin_on.visibility == View.VISIBLE){
      updateVisibility()
    }else if(gstin_off.visibility == View.VISIBLE){
      reverseVisibility()
    }
//        business_gst_number.setFilters(business_gst_number.filters + InputFilter.AllCaps())

    business_gstin_number.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(business_gstin_number.hasFocus()){
          var enteredText: String = s.toString()
          if (enteredText.length == 15) {
            showProgress()
            callGSTApi(enteredText)
          }
        }
      }
      override fun afterTextChanged(s: Editable?) {
      }

    })


    confirm_btn.setOnClickListener {
      if(progress_bar.visibility == View.GONE){
        if (validateAgreement()) {
          if (!customerInfoState) { //no customer available
            //create customer payment profile
            viewModel.createCustomerInfo(
              (activity as? UpgradeActivity)?.getAccessToken() ?: "",
              CreateCustomerInfoRequest(
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

              )
            )
          } else {
            //update customer payment profile
            if(gst_business_name_value.visibility == View.GONE && gst_business_address_value.visibility == View.GONE){
              viewModel.updateCustomerInfo(
                (activity as? UpgradeActivity)?.getAccessToken() ?: "",
                CreateCustomerInfoRequest(
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

                )
              )
            }else if(isGstApiCalled){
              viewModel.updateCustomerInfo(
                (activity as? UpgradeActivity)?.getAccessToken() ?: "",
                CreateCustomerInfoRequest(
                  AddressDetails(
                    gstInfoResult?.address?.city,
                    "india",
                    gst_business_address_value.text.toString(),
                    null,
                    gstInfoResult?.address?.state,
                    gstInfoResult?.address?.pincode
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
                  gst_business_name_value.text.toString(),
                  TaxDetails(
                    if (business_gstin_number.text.isEmpty()) null else business_gstin_number.text.toString(),
                    null,
                    null,
                    null
                  )

                )
              )
            }
            else{
              viewModel.updateCustomerInfo(
                (activity as? UpgradeActivity)?.getAccessToken() ?: "",
                CreateCustomerInfoRequest(
                  AddressDetails(
                    if (business_city_name.text.isEmpty()) null else business_city_name.text.toString(),
                    "india",
                    gst_business_address_value.text.toString(),
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
                  gst_business_name_value.text.toString(),
                  TaxDetails(
                    if (business_gstin_number.text.isEmpty()) null else business_gstin_number.text.toString(),
                    null,
                    null,
                    null
                  )

                )
              )
            }

          }
        }
      }else{
        Toast.makeText(requireActivity(),"Please wait while data is loading",Toast.LENGTH_SHORT).show()
      }
    }
//        prefs.storeGstRegistered(true)
    if (!prefs.getGstRegistered()) {
      gstFlag = false
      gstin_on.visibility = View.GONE
      gstin_off.visibility = View.VISIBLE
      gst_info_tv.visibility = View.GONE
      business_gstin_number.visibility = View.GONE
      reverseVisibility()
    } else {
      gstFlag = true
      gstin_on.visibility = View.VISIBLE
      gstin_off.visibility = View.GONE
      gst_info_tv.visibility = View.VISIBLE
      business_gstin_number.visibility = View.VISIBLE
      updateVisibility()
    }
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
      gst_info_tv.visibility = View.GONE
      reverseVisibility()
    }

    gstin_off.setOnClickListener {
      prefs.storeGstRegistered(true)
      gstFlag = true
      business_gstin_number.visibility = View.VISIBLE
      gstin_off.visibility = View.GONE
      gstin_on.visibility = View.VISIBLE
      gst_info_tv.visibility = View.VISIBLE
      updateVisibility()
    }

    dialog!!.setOnKeyListener { dialog, keyCode, event ->
      if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.ACTION_UP) {
//                Toasty.info(requireContext(), "Accept Any One Condition...", Toast.LENGTH_LONG).show()
        return@setOnKeyListener true
      }
      false
    }
    business_city_name.setOnClickListener {
      val args = Bundle()
      args.putString("state", setStates)
      stateFragment.arguments = args
      stateFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        STATE_LIST_FRAGMENT
      )
    }
    WebEngageController.trackEvent(ADDONS_MARKETPLACE_BUSINESS_DETAILS_LOAD, GSTIN, NO_EVENT_VALUE)
  }

  private fun saveGstResponse() {
    if (business_gstin_number.text.isNullOrEmpty().not()) {
      if (isValidGSTIN(business_gstin_number.text.toString())) {
        loadGSTInfo(business_gstin_number.text.toString())
        viewModel.getGstApiResult().observe(viewLifecycleOwner, Observer {
          gstInfoResult = it.result
          if (gstInfoResult != null) {
            prefs.storeGstApiResponse(gstInfoResult)
          } else {
            Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
          }
        })
      } else {
        Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun callGSTApi(gstNo:String){
    if(isValidGSTIN(gstNo)){
      loadGSTInfo(gstNo)
      viewModel.getGstApiResult().observe(viewLifecycleOwner,{
        gstInfoResult = it.result
        if(gstInfoResult!=null){
          gst_business_name_value.text = gstInfoResult!!.legalName
          gst_business_address_value.text = gstInfoResult!!.address!!.addressLine1 + gstInfoResult!!.address!!.addressLine2 + gstInfoResult!!.address!!.city + gstInfoResult!!.address!!.pincode + gstInfoResult!!.address!!.district + gstInfoResult!!.address!!.state
          isGstApiCalled = true
        } else {
          Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
        }
        hideProgress()
      })
    }else {
      Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
      hideProgress()
    }
  }

  private fun showProgress() {
   progress_bar.visibility = View.VISIBLE
  }

  private fun hideProgress() {
    progress_bar.visibility = View.GONE
  }

  private fun updateVisibility() {
    business_name_actual.visibility = View.GONE
    business_name_value.visibility = View.GONE
    complete_business_address.visibility = View.GONE
    business_address.visibility = View.GONE
    state_of_supply.visibility = View.GONE
    business_city_name.visibility = View.GONE
    gst_business_name_tv.visibility = View.VISIBLE
    gst_business_name_value.visibility = View.VISIBLE
    gst_business_address_tv.visibility = View.VISIBLE
    gst_business_address_value.visibility = View.VISIBLE
  }
  private fun reverseVisibility(){
    business_name_actual.visibility = View.VISIBLE
    business_name_value.visibility = View.VISIBLE
    complete_business_address.visibility = View.VISIBLE
    business_address.visibility = View.VISIBLE
    state_of_supply.visibility = View.VISIBLE
    business_city_name.visibility = View.VISIBLE
    gst_business_name_tv.visibility = View.GONE
    gst_business_name_value.visibility = View.GONE
    gst_business_address_tv.visibility = View.GONE
    gst_business_address_value.visibility = View.GONE
  }

  private fun validateAgreement(): Boolean {
    if (business_contact_number.text.isEmpty() || business_email_address.text.isEmpty() || business_city_name.text.isEmpty()
      || business_name_value.text.isEmpty() || business_address.text.isEmpty() || (gstFlag && business_gstin_number.text.isEmpty())
    ) {
      Log.v("business_name_value", " " + business_name_value.text.toString())
//            Toasty.error(requireContext(), "Fields are Empty!!", Toast.LENGTH_LONG).show()
      if (business_gstin_number.text.isEmpty() && !isValidGSTIN(business_gstin_number.text.toString()) && gstFlag) {
        business_gstin_number.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
        return false
      } else {
        business_gstin_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
      }

      if (business_contact_number.text.isEmpty()) {
        business_contact_number.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Please enter Mobile no.", Toast.LENGTH_LONG).show()
        return false
      } else {
        if (!isValidMobile(business_contact_number.text.toString())) {
          business_contact_number.setBackgroundResource(R.drawable.et_validity_error)
          Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
            .show()
          return false
        } else {
          business_contact_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
        }
      }

      Log.v("business_name_value1", " " + business_contact_number.text.toString())


      Log.v("business_name_value2", " " + business_email_address.text.toString())
      if (business_email_address.text.isEmpty()) {
        business_email_address.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Please enter Email ID", Toast.LENGTH_LONG).show()
        return false
      } else {
        if (!isValidMail(business_email_address.text.toString())) {
          business_email_address.setBackgroundResource(R.drawable.et_validity_error)
          Toasty.error(requireContext(), "Entered Email ID is not valid!!", Toast.LENGTH_LONG)
            .show()
          return false
        } else {
          business_email_address.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
        }
      }


      Log.v("business_name_value3", " " + business_name_value.text.toString())
      if (business_name_value.text.isEmpty()) {
        business_name_value.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Entered Business name is not valid!!", Toast.LENGTH_LONG)
          .show()
        return false
      } else {
        business_name_value.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
      }

      if (business_address.text.isEmpty()) {
        business_address.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Entered Business address is not valid!!", Toast.LENGTH_LONG)
          .show()
        return false
      } else {
        business_address.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
      }
      return false
    }
    if (!business_gstin_number.text.isEmpty() && !isValidGSTIN(business_gstin_number.text.toString()) && gstFlag) {
      business_gstin_number.setBackgroundResource(R.drawable.et_validity_error)
      Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
      return false
    }

    if (!isValidMail(business_email_address.text.toString())) {
      business_email_address.setBackgroundResource(R.drawable.et_validity_error)
      Toasty.error(requireContext(), "Entered Email ID is not valid!!", Toast.LENGTH_LONG).show()
      return false
    } else {
      business_email_address.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
    }

    if (!isValidMobile(business_contact_number.text.toString())) {
      business_contact_number.setBackgroundResource(R.drawable.et_validity_error)
      Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
        .show()
      return false
    } else {
      business_contact_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
    }

    /*if (!confirm_checkbox.isChecked) {
        Toasty.error(requireContext(), "Accept the Agreement!!", Toast.LENGTH_LONG).show()
        return false
    }*/
    return true
//        return false
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
//                    business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.City)

          if (createCustomerInfoRequest!!.AddressDetails!!.City != null) {
            viewModel.getStateFromCityAssetJson(
              requireActivity(),
              createCustomerInfoRequest!!.AddressDetails!!.City
            )
          }
          if (createCustomerInfoRequest!!.AddressDetails!!.State != null || !createCustomerInfoRequest!!.AddressDetails!!.State.equals(
              "string"
            )
          ) {
            business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
            setStates = createCustomerInfoRequest!!.AddressDetails!!.State
          }
          if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
            business_address.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
            gst_business_address_value.text = createCustomerInfoRequest!!.AddressDetails.Line1.toString()
          }
        }
        if (createCustomerInfoRequest!!.TaxDetails != null) {
          business_gstin_number.setText(createCustomerInfoRequest!!.TaxDetails!!.GSTIN)
        }
        if (createCustomerInfoRequest!!.Name != null) {
          business_name_value.setText(createCustomerInfoRequest!!.Name)
        }

        if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null) {
          business_contact_number.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
        } else {
          if (session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")) {

          } else {
            business_contact_number.setText(session?.userPrimaryMobile)
          }
        }


        if (createCustomerInfoRequest!!.BusinessDetails!!.Email != null) {
          business_email_address.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
        } else {
          if (session?.fPEmail == null || session?.fPEmail.equals("")) {

          } else {
            business_email_address.setText(session?.fPEmail)
          }
        }


        if (createCustomerInfoRequest!!.Name != null) {
          business_name_value.setText(createCustomerInfoRequest!!.Name)
          gst_business_name_value.text = createCustomerInfoRequest!!.Name
        } else {
          if (session?.fPName == null || session?.fPName.equals("")) {

          } else {
            business_name_value.setText(session?.fPName)
            gst_business_name_value.text = session?.fPName
          }
        }


        if (createCustomerInfoRequest!!.AddressDetails != null) {


          if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
            business_address.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
            gst_business_address_value.text = createCustomerInfoRequest!!.AddressDetails.Line1.toString()
          } else {
            if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(
                Key_Preferences.GET_FP_DETAILS_ADDRESS
              ).equals("")
            ) {

            } else {
              business_address.setText(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))
              gst_business_address_value.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
            }
          }
        }

      }
    })
    viewModel.getCustomerInfoStateResult().observeOnce(this, Observer {
      customerInfoState = it
      if (!customerInfoState) {
        if (session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")) {

        } else {
          business_contact_number.setText(session?.userPrimaryMobile)
        }

//                if(session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL) == null || session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL).equals("") ){
        if (session?.fPEmail == null || session?.fPEmail.equals("")) {
        } else {
          business_email_address.setText(session?.fPEmail)
        }

        if (session?.fPName == null || session?.fPName.equals("")) {

        } else {
          business_name_value.setText(session?.fPName)
        }

        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(
            Key_Preferences.GET_FP_DETAILS_ADDRESS
          ).equals("")
        ) {

        } else {
          business_address.setText(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))
        }
      }
    })

    viewModel.getUpdatedCustomerBusinessResult().observeOnce(viewLifecycleOwner, Observer {
      if (it.Result != null) {
        Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
        val event_attributes: HashMap<String, Any> = HashMap()
        event_attributes.put("", it.Result.CustomerId)
        WebEngageController.trackEvent(
          ADDONS_MARKETPLACE_BUSINESS_DETAILS_UPDATE_SUCCESS,
          ADDONS_MARKETPLACE,
          event_attributes
        )
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
      } else {
        Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
          .show()
        WebEngageController.trackEvent(
          ADDONS_MARKETPLACE_BUSINESS_DETAILS_FAILED,
          ADDONS_MARKETPLACE,
          NO_EVENT_VALUE
        )
        (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
      }
      dismiss()
    })
    viewModel.getUpdatedResult().observeOnce(viewLifecycleOwner, Observer {
      if (it.Result != null) {
        Toasty.success(requireContext(), "Successfully Created Profile.", Toast.LENGTH_LONG).show()
        val event_attributes: HashMap<String, Any> = HashMap()
        event_attributes.put("", it.Result.CustomerId)
        WebEngageController.trackEvent(
          ADDONS_MARKETPLACE_BUSINESS_DETAILS_CREATE_SUCCESS,
          ADDONS_MARKETPLACE,
          event_attributes
        )
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
      } else {
        Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
          .show()
        WebEngageController.trackEvent(
          ADDONS_MARKETPLACE_BUSINESS_DETAILS_FAILED,
          ADDONS_MARKETPLACE,
          NO_EVENT_VALUE
        )
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
      }
      dismiss()
    })


    viewModel.stateResult().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it != null) {
        val adapter =
          ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
//                business_city_name.setAdapter(adapter)
      }

    })

    viewModel.getSelectedStateResult().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it != null) {
        business_city_name.text = it
        setStates = it
      }

    })

  }

  private fun loadGSTInfo(gstIn: String) {
    viewModel.getGstApiInfo(
      (activity as? UpgradeActivity)?.getAccessToken() ?: "",
      gstIn,
      (activity as UpgradeActivity).clientid,
      progress_bar
    )
  }

  private fun loadCustomerInfo() {
    viewModel.getCustomerInfo(
      (activity as? UpgradeActivity)?.getAccessToken() ?: "",
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }


  override fun onDestroy() {
    super.onDestroy()
    requireActivity().viewModelStore.clear()
//        this.viewModelStore.clear()
    listener.backListener(true)

  }
}
