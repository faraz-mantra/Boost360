package com.appservice.ui.ecommerce

import android.view.LayoutInflater
import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.DeliveryDetailsResponse
import com.appservice.model.aptsetting.DeliverySetup
import com.appservice.model.aptsetting.GetWareHouseResponse
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentDeliveryConfigurationBinding
import com.appservice.ui.ecommerce.bottomsheets.BottomSheetAddCartSlab
import com.appservice.ui.ecommerce.bottomsheets.BottomSheetAddWareHouse
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.fromHtml
import com.framework.views.customViews.CustomTextView

class FragmentEcomDeliveryConfig : AppBaseFragment<FragmentDeliveryConfigurationBinding, AppointmentSettingsViewModel>() {

  private var wareHouseAddress: GetWareHouseResponse? = null

  companion object {
    fun newInstance(): FragmentEcomDeliveryConfig {
      return FragmentEcomDeliveryConfig()
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
    setupView()
    getWareHouseAddress()
    binding?.toggleHomeDelivery?.setOnToggledListener { _, isOn ->
      updateDeliveryStatus(binding?.toggleAllowPickup?.isOn ?: true, isOn, binding?.etdFlatCharges?.text.toString())
    }
    binding?.toggleAllowPickup?.setOnToggledListener { _, isOn ->
      updateDeliveryStatus(isOn, binding?.toggleHomeDelivery?.isOn ?: true, binding?.etdFlatCharges?.text.toString())
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
        binding?.etdFlatCharges?.setText(data?.result?.flatDeliveryCharge.toString())
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
            binding?.containerWareHouseAddress?.addView(inflateWareHouseView(index, dataItem?.name, dataItem?.fullAddress, dataItem?.contactNumber))
          }
        }
      }
    })
  }

  private fun inflateWareHouseView(index: Int, name: String?, fullAddress: String?, contactNumber: String?): View {
    val itemView = LayoutInflater.from(context).inflate(R.layout.item_warehouse_address, null, false)
    val warehouseName = itemView.findViewById<CustomTextView>(R.id.ctv_warehouse_name)
    val wareHouseAddressPhone = itemView.findViewById<CustomTextView>(R.id.ctv_warehouse_address_phone)
    warehouseName.text = "#${index + 1}. ${name}"
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
    binding?.ccbBusinessLocation?.isChecked = true
    binding?.ccbWarehouseAddress?.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        binding?.containerWareHouseAddress?.visible()
        if (wareHouseAddress?.result?.data.isNullOrEmpty())
          showAddWareHouse()
      }
    }
    setOnClickListener(binding?.btnAddAnotherSlab, binding?.btnFlatCharges, binding?.addWareHouseAddress, binding?.btnSaveCharges)

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
        updateDeliveryStatus(
          binding?.toggleAllowPickup?.isOn
            ?: true, binding?.toggleHomeDelivery?.isOn
            ?: true, binding?.etdFlatCharges?.text.toString()
        )
      }

    }
  }

  private fun showAddCartSlab() {
    val bottomSheetAddCartSlab = BottomSheetAddCartSlab()
    bottomSheetAddCartSlab.show(parentFragmentManager, BottomSheetAddCartSlab::class.java.name)
  }

  private fun updateDeliveryStatus(isPickup: Boolean, isHomePickup: Boolean, flatDeliverCharge: String) {
    viewModel?.setupDelivery(
      DeliverySetup(
        isPickupAllowed = isPickup,
        isBusinessLocationPickupAllowed = false,
        isWarehousePickupAllowed = false,
        isHomeDeliveryAllowed = isHomePickup,
        flatDeliveryCharge = flatDeliverCharge,
        clientId = clientId,
        floatingPointId = sessionLocal.fPID
      )
    )?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        showShortToast(getString(R.string.updated))
      }
    })
  }
}