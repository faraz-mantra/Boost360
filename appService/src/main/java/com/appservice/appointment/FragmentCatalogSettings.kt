package com.appservice.appointment

import com.appservice.R
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
}