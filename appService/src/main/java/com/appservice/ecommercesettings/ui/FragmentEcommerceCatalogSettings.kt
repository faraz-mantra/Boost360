package com.appservice.ecommercesettings.ui

import android.view.View
import com.appservice.R
import com.appservice.appointment.widgets.BottomSheetCatalogDisplayName
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentEcomCatalogSettingBinding
import com.framework.models.BaseViewModel

class FragmentEcommerceCatalogSettings : AppBaseFragment<FragmentEcomCatalogSettingBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_ecom_catalog_setting
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentEcommerceCatalogSettings {
            return FragmentEcommerceCatalogSettings()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.ctvChangeServices)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.ctvChangeServices -> {
                showCatalogDisplayName()
            }
        }
    }

    private fun showCatalogDisplayName() {
        val bottomSheetCatalogDisplayName = BottomSheetCatalogDisplayName()
        bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetCatalogDisplayName::class.java.name)
    }
}