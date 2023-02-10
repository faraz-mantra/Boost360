package com.inventoryorder.ui.order.createorder

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.utils.ValidationUtils
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentAddCustomerBinding
import com.inventoryorder.model.orderRequest.Address
import com.inventoryorder.model.orderRequest.BuyerDetails
import com.inventoryorder.model.orderRequest.ContactDetails
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import com.onboarding.nowfloats.model.CityDataModel
import com.onboarding.nowfloats.ui.CitySearchDialog
import java.util.regex.Pattern
import com.framework.webengageconstant.CLICKED_ON_ADD_CUSTOMER
import com.framework.webengageconstant.ORDERS
import com.onboarding.nowfloats.extensions.capitalizeWords

class AddCustomerFragment : BaseInventoryFragment<FragmentAddCustomerBinding>() {

  private var createOrderRequest = OrderInitiateRequest()
  private var shouldReInitiate: Boolean = false
  private var shouldFinish: Boolean = false
  private var addMore: Boolean = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddCustomerFragment {
      val fragment = AddCustomerFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent(CLICKED_ON_ADD_CUSTOMER, ORDERS, it) }
    setOnClickListener(
      binding?.vwNext,
      binding?.textAddCustomerGstin,
      binding?.tvRemove,
      binding?.textGoBack,
      binding?.layoutBillingAddr?.editCity
    )
    createOrderRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
    setUpData()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwNext -> onNextTapped()
      binding?.textAddCustomerGstin -> {
        gstViewVisibility(true)
      }
      binding?.tvRemove -> {
        gstViewVisibility()
      }
      binding.textGoBack -> (context as? FragmentContainerOrderActivity)?.onBackPressed()
      binding.layoutBillingAddr.editCity -> {
        val dialog = CitySearchDialog()
        dialog.onClicked = { setCityState(it) }
        dialog.show(parentFragmentManager, dialog.javaClass.name)
      }
    }
  }

  fun gstViewVisibility(isShow : Boolean = false){
    if (isShow) {
      binding.textAddCustomerGstin.visibility = View.GONE
      binding.lytCustomerGstn.visibility = View.VISIBLE
    } else {
      binding.editGstin.setText("")
      binding.textAddCustomerGstin.visibility = View.VISIBLE
      binding.lytCustomerGstn.visibility = View.GONE
    }
  }

  fun getBundleData(): Bundle {
    return Bundle().apply {
      putBoolean(IntentConstant.SHOULD_RE_INITIATE.name, shouldReInitiate)
      putBoolean(IntentConstant.SHOULD_FINISH.name, shouldFinish)
      putBoolean(IntentConstant.ADD_MORE_ITEM.name, addMore)
      putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    }
  }

  private fun onNextTapped() {

    val name = binding?.editCustomerName?.text?.toString() ?: ""
    val email = binding?.editCustomerEmail?.text?.toString() ?: ""
    val phone = binding?.editCustomerPhone?.text?.toString() ?: ""
    val address = binding?.layoutBillingAddr?.editAddress?.text?.toString() ?: ""
    val city = binding?.layoutBillingAddr?.editCity?.text?.toString() ?: ""
    val state = binding?.layoutBillingAddr?.editState?.text?.toString() ?: ""
    val pinCode = binding?.layoutBillingAddr?.editPin?.text?.toString() ?: ""
    val gstNo = binding?.editGstin?.text?.toString() ?: ""

    if (name.isEmpty()) {
      showShortToast(getString(R.string.customer_name_cannot_be_empty))
      return
    }

    if (!ValidationUtils.isValidName(name)) {
      showShortToast(getString(R.string.please_enter_valid_customer_name))
      return
    }

    if (phone.isEmpty()) {
      showShortToast(getString(R.string.customer_phone_cannot_be_empty))
      return
    }

    if (!ValidationUtils.isMobileNumberValid(phone ?: "")) {
      showShortToast(getString(R.string.please_enter_valid_phone))
      return
    }

    if (email.isEmpty().not() && !ValidationUtils.isEmailValid(email)) {
      showShortToast(getString(R.string.please_enter_valid_email))
      return
    }

    if (gstNo.isEmpty().not() && Pattern.compile(AppConstant.GST_VALIDATION_REGEX).matcher(gstNo).matches().not()) {
      showShortToast(getString(R.string.enter_valid_gstin_number))
      return
    }

    if (address.isEmpty()) {
      showShortToast(getString(R.string.customer_address_cannot_be_empty))
      return
    }

    if (city.isEmpty()) {
      showShortToast(getString(R.string.customer_city_cannot_be_empty))
      return
    }

    if (state.isEmpty()) {
      showShortToast(getString(R.string.customer_state_cannot_be_empty))
      return
    }

    if (pinCode.isEmpty()) {
      showShortToast(getString(R.string.customer_pincode_cannot_be_empty))
      return
    }

    if (pinCode.length < 6) {
      showShortToast(getString(R.string.enter_valid_pincode))
      return
    }

    val contactDetails = ContactDetails(fullName = name, emailId = email, primaryContactNumber = phone)

    val billingAddress = Address(address, city = city, region = state, zipcode = pinCode)
    val buyerDetails = BuyerDetails(contactDetails = contactDetails, address = billingAddress, GSTIN = gstNo)
    createOrderRequest.buyerDetails = buyerDetails
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    bundle.putSerializable(
      IntentConstant.PREFERENCE_DATA.name,
      arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name)
    )
    startFragmentOrderActivity(FragmentType.BILLING_DETAIL, bundle, isResult = true)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val req = bundle?.getSerializable(IntentConstant.ORDER_REQUEST.name) as? OrderInitiateRequest
      if (req != null) {
        createOrderRequest = req
        setUpData()
      }
      addMore = bundle?.getBoolean(IntentConstant.ADD_MORE_ITEM.name) ?: false
      shouldFinish = bundle?.getBoolean(IntentConstant.SHOULD_FINISH.name) ?: false
      shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_RE_INITIATE.name) ?: false
      if (shouldFinish || shouldReInitiate || addMore) (context as? FragmentContainerOrderActivity)?.onBackPressed()
    }
  }

  private fun setUpData() {
    binding.editCustomerName.setText(
      createOrderRequest.buyerDetails?.contactDetails?.fullName ?: ""
    )
    binding.editCustomerEmail.setText(
      createOrderRequest.buyerDetails?.contactDetails?.emailId ?: ""
    )
    binding.editCustomerPhone.setText(
      createOrderRequest.buyerDetails?.contactDetails?.primaryContactNumber ?: ""
    )
    binding.layoutBillingAddr.editAddress.setText(
      createOrderRequest.buyerDetails?.address?.addressLine ?: ""
    )
    binding.layoutBillingAddr.editCity.setText(
      createOrderRequest.buyerDetails?.address?.city ?: ""
    )
    binding.layoutBillingAddr.editState.setText(
      createOrderRequest.buyerDetails?.address?.region ?: ""
    )
    binding.layoutBillingAddr.editPin.setText(
      createOrderRequest.buyerDetails?.address?.zipcode ?: ""
    )
    val gstInNumber = createOrderRequest.buyerDetails?.GSTIN ?: ""
    if (gstInNumber.isNullOrEmpty().not()){
      gstViewVisibility(true)
      binding.editGstin.setText(
        createOrderRequest.buyerDetails?.GSTIN ?: ""
      )
    }else{
      gstViewVisibility()
    }
  }

  private fun setCityState(cityDataModel: CityDataModel) {
    binding.layoutBillingAddr.editCity.setText(cityDataModel.getOriginalCityName())
    binding.layoutBillingAddr.editState.setText(cityDataModel.getOriginalStateName())
  }
}