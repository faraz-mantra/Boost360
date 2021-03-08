package com.appservice.appointment.ui

import android.view.View
import com.appservice.R
import com.appservice.appointment.widgets.BottomSheetCatalogDisplayName
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentCatalogSettingBinding
import com.framework.models.BaseViewModel

class FragmentCatalogSettings : AppBaseFragment<FragmentCatalogSettingBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_catalog_setting
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentCatalogSettings {
            return FragmentCatalogSettings()
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