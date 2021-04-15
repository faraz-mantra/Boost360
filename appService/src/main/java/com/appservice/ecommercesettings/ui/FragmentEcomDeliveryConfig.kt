package com.appservice.ecommercesettings.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentDeliveryConfigurationBinding
import com.appservice.ecommercesettings.ui.bottomsheets.BottomSheetAddCartSlab
import com.appservice.ecommercesettings.ui.bottomsheets.BottomSheetAddWareHouse
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.gone
import com.framework.extensions.visible

class FragmentEcomDeliveryConfig : AppBaseFragment<FragmentDeliveryConfigurationBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_delivery_configuration
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentEcomDeliveryConfig {
            return FragmentEcomDeliveryConfig()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setupView()
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
                showAddWareHouse()
            }
        }
        setOnClickListener(binding?.btnAddAnotherSlab, binding?.btnFlatCharges)
    }

    private fun showAddWareHouse() {
        val bottomSheetAddWareHouse = BottomSheetAddWareHouse()
        bottomSheetAddWareHouse.bottomSheetAddWareHouse.show(parentFragmentManager, BottomSheetAddWareHouse::class.java.name)
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

        }
    }

    private fun showAddCartSlab() {
        val bottomSheetAddCartSlab = BottomSheetAddCartSlab()
        bottomSheetAddCartSlab.show(parentFragmentManager, BottomSheetAddCartSlab::class.java.name)
    }
}