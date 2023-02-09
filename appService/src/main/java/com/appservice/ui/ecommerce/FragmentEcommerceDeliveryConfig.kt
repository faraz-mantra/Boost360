package com.appservice.ui.ecommerce

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentDeliveryConfigurationBinding
import com.appservice.model.aptsetting.DeliveryDetailsResponse
import com.appservice.model.aptsetting.DeliverySetup
import com.appservice.model.aptsetting.GetWareHouseResponse
import com.appservice.ui.ecommerce.bottomsheets.BottomSheetAddCartSlab
import com.appservice.ui.ecommerce.bottomsheets.BottomSheetAddWareHouse
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.fromHtml
import com.framework.views.customViews.CustomTextView
import com.framework.webengageconstant.DELIVERY_SETUP_PAGE_LOAD
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import java.util.*
import kotlin.concurrent.schedule

class FragmentEcommerceDeliveryConfig : AppBaseFragment<FragmentDeliveryConfigurationBinding, AppointmentSettingsViewModel>() {

  private var wareHouseAddress: GetWareHouseResponse? = null

  companion object {
    fun newInstance(): FragmentEcommerceDeliveryConfig {
      return FragmentEcommerceDeliveryConfig()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_delivery_configuration
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(DELIVERY_SETUP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setupView()
    getWareHouseAddress()

    binding.etdFlatCharges.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
          if (p0?.contains('-') == true) {
            binding.etdFlatCharges.setText(p0.toString().replace("-", ""))
            binding.etdFlatCharges.setSelection(binding.etdFlatCharges.length())
        }
      }

      override fun afterTextChanged(s: Editable?) {}

    })
    binding?.toggleHomeDelivery?.setOnToggledListener { _, isOn ->
      updateDeliveryStatus(binding?.toggleAllowPickup?.isOn ?: true, isOn, getflatCharges())
    }
    binding?.toggleAllowPickup?.setOnToggledListener { _, isOn ->
      updateDeliveryStatus(isOn, binding?.toggleHomeDelivery?.isOn ?: true, getflatCharges())
    }
  }

  private fun getDeliveryDetails() {
    showProgress(getString(R.string.loading_))
    viewModel?.getDeliveryDetails(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        val data = it as? DeliveryDetailsResponse
        binding?.toggleAllowPickup?.isOn = data?.result?.isPickupAllowed ?: true
        binding?.toggleHomeDelivery?.isOn = data?.result?.isHomeDeliveryAllowed ?: true
        binding.ccbBusinessLocation.isChecked = data?.result?.isBusinessLocationPickupAllowed ?: false
        binding.ccbWarehouseAddress.isChecked = data?.result?.isWarehousePickupAllowed ?: false
        binding?.etdFlatCharges?.setText((data?.result?.getFlatDeliveryCharge() ?: 0).toString())
      }

    })
  }

  private fun getWareHouseAddress() {
    showProgress(getString(R.string.loading_))
    viewModel?.getWareHouseAddress(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner, { baseResponse ->
      hideProgress()
      getDeliveryDetails()
      if (baseResponse.isSuccess()) {
        this.wareHouseAddress = baseResponse as? GetWareHouseResponse
        if (wareHouseAddress == null) {
          binding?.ccbWarehouseAddress?.isChecked = false
          binding?.addWareHouseAddress?.gone()
          binding?.toggleAllowPickup?.isOn = true
        } else {
          wareHouseAddress?.result?.data?.forEachIndexed { index, dataItem ->
            binding?.ccbWarehouseAddress?.isChecked = true
            binding?.addWareHouseAddress?.visible()
            binding?.containerWareHouseAddress?.addView(inflateWareHouseView(index, dataItem?.name, dataItem?.fullAddress, dataItem?.contactNumberWith91()))
          }
        }
      }
    })
  }

  private fun inflateWareHouseView(index: Int, name: String?, fullAddress: String?, contactNumber: String?): View {
    val itemView = LayoutInflater.from(context).inflate(R.layout.item_warehouse_address, null, false)
    val warehouseName = itemView.findViewById<CustomTextView>(R.id.ctv_warehouse_name)
    val wareHouseAddressPhone = itemView.findViewById<CustomTextView>(R.id.ctv_warehouse_address_phone)
    warehouseName.text = "#${index + 1}. $name"
    wareHouseAddressPhone.text = fromHtml("$fullAddress <u>$contactNumber</u>")
    return itemView
  }

  private fun setupView() {
    binding?.crbFlatCharges?.isChecked = true
    binding?.btnFlatCharges?.visible()
    binding?.btnAddAnotherSlab?.gone()
    binding?.llContainerCartSlab?.gone()
    binding?.crbCartSlab?.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        binding?.btnFlatCharges?.gone()
        binding?.btnAddAnotherSlab?.visible()
        binding?.llContainerCartSlab?.visible()
      } else {
        binding?.btnFlatCharges?.visible()
        binding?.btnAddAnotherSlab?.gone()
        binding?.llContainerCartSlab?.gone()

      }
    }
    binding?.crbFlatCharges?.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        binding?.btnFlatCharges?.visible()
        binding?.btnAddAnotherSlab?.gone()
        binding?.llContainerCartSlab?.gone()
      } else {
        binding?.btnFlatCharges?.gone()
        binding?.btnAddAnotherSlab?.visible()
        binding?.llContainerCartSlab?.visible()


      }
    }
    Timer().schedule(1000) {
//    binding?.ccbBusinessLocation?.isChecked = true
      binding?.ccbWarehouseAddress?.setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
          binding?.containerWareHouseAddress?.visible()
          if (wareHouseAddress?.result?.data.isNullOrEmpty())
            showAddWareHouse()
        }
        updateDeliveryStatus(
          binding?.toggleAllowPickup?.isOn,
          binding?.toggleHomeDelivery?.isOn,
          getflatCharges()
        )
      }
      binding?.ccbBusinessLocation.setOnCheckedChangeListener { buttonView, isChecked ->
        updateDeliveryStatus(
          binding?.toggleAllowPickup?.isOn,
          binding?.toggleHomeDelivery?.isOn,
          getflatCharges()
        )
      }
    }
    setOnClickListener(binding?.btnAddAnotherSlab, binding?.btnFlatCharges, binding?.addWareHouseAddress, binding?.btnSaveCharges)

  }

  fun getflatCharges(): Int{
    return if(binding?.etdFlatCharges?.text.toString().isNotEmpty()){
      binding?.etdFlatCharges?.text.toString().toInt()
    }else{
      0
    }
  }

  private fun showAddWareHouse() {
    val bottomSheetAddWareHouse = BottomSheetAddWareHouse()
    bottomSheetAddWareHouse.onClicked = { requestAddWareHouseAddress ->
      showProgress(getString(R.string.updating))
      requestAddWareHouseAddress.floatingPointId = sessionLocal.fPID
      requestAddWareHouseAddress.clientId = clientId
      viewModel?.addWareHouseAddress(requestAddWareHouseAddress)?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) {
          showShortToast(getString(R.string.updated))
          binding?.containerWareHouseAddress?.removeAllViews()
          getWareHouseAddress()
        } else
          showShortToast(getString(R.string.something_went_wrong))
      })
    }
    bottomSheetAddWareHouse.show(parentFragmentManager, BottomSheetAddWareHouse::class.java.name)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAddAnotherSlab -> {
        showAddCartSlab()
      }
      binding?.btnFlatCharges -> {
        showAddWareHouse()
      }
      binding?.addWareHouseAddress -> {
        showAddWareHouse()
      }
      binding?.btnSaveCharges -> {
        updateDeliveryStatus(binding?.toggleAllowPickup?.isOn ?: true, binding?.toggleHomeDelivery?.isOn ?: true, getflatCharges())
      }

    }
  }

  private fun showAddCartSlab() {
    val bottomSheetAddCartSlab = BottomSheetAddCartSlab()
    bottomSheetAddCartSlab.show(parentFragmentManager, BottomSheetAddCartSlab::class.java.name)
  }

  private fun updateDeliveryStatus(isPickup: Boolean, isHomePickup: Boolean, flatDeliverCharge: Int) {
    viewModel?.setupDelivery(
      DeliverySetup(
        isPickupAllowed = isPickup, isBusinessLocationPickupAllowed = binding.ccbBusinessLocation.isChecked, isWarehousePickupAllowed = binding.ccbWarehouseAddress.isChecked,
        isHomeDeliveryAllowed = isHomePickup, flatDeliveryCharge = flatDeliverCharge, clientId = clientId, floatingPointId = sessionLocal.fPID
      )
    )?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) showShortToast(getString(R.string.updated))
    })
  }
}