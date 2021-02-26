package com.appservice.appointment

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAppointmentSettingsBinding
import com.appservice.ui.catalog.catalogService.ServiceCatalogHomeFragment
import com.framework.models.BaseViewModel

class FragmentAppointmentSettings: AppBaseFragment<FragmentAppointmentSettingsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_appointment_settings    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(): FragmentAppointmentSettings {
            return FragmentAppointmentSettings()
        }
    }
}