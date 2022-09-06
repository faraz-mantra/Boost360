package com.appservice.ui.aptsetting.ui

import android.os.Handler
import android.os.Looper
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentConsultationAptSettingBinding
import com.appservice.model.generalApt.GeneralAptResponse
import com.appservice.model.generalApt.GeneralAptResult
import com.appservice.model.generalApt.UpdateRequestGeneralApt
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.extensions.observeOnce

class FragmentConsultationAptSettings : AppBaseFragment<FragmentConsultationAptSettingBinding, AppointmentSettingsViewModel>() {

  private val request= UpdateRequestGeneralApt()

  companion object {
    fun newInstance(): FragmentConsultationAptSettings {
      return FragmentConsultationAptSettings()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_consultation_apt_setting
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnConfirm)
    binding?.toggleHome?.setOnToggledListener { _, isOn ->
      toggleView(isOn)
    }
    getAPIGeneralService()
  }

  private fun toggleView(isOn: Boolean) {
    binding?.viewData?.visibility = if (isOn) View.VISIBLE else View.GONE
    binding?.noteTxt?.visibility = if (isOn) View.VISIBLE else View.GONE
  }

  private fun getAPIGeneralService(isShow: Boolean = true) {
    if (isShow) showProgress()
    viewModel?.getGeneralService(sessionLocal.fPID, sessionLocal.fpTag)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        val response = (it as? GeneralAptResponse)?.Result
        updateUiData(response)
      } else showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      hideProgress()
    }
  }

  private fun updateUiData(response: GeneralAptResult?) {
    binding?.toggleHome?.isOn = response?.IsAvailable ?: false
    toggleView(response?.IsAvailable ?: false)
    binding?.etdFlatCharges?.setText("${response?.Price ?: 0}")
    binding?.etdSlotDuration?.setText("${response?.Duration ?: 0}")
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnConfirm -> {
        if (isValid()) saveUpdateGeneralAppointment()
      }
    }
  }

  private fun isValid(): Boolean {
    val flatCharges = (binding?.etdFlatCharges?.text?.toString() ?: "").toDoubleOrNull()
    val slotDuration = (binding?.etdSlotDuration?.text?.toString() ?: "").toIntOrNull()
    if (flatCharges == null) {
      showShortToast("Appointment fee charges can not empty!")
      return false
    }
    if (slotDuration == null) {
      showShortToast("Appointment slot duration can not empty!")
      return false
    }
    request.price = flatCharges
    request.duration = slotDuration
    request.floatingPointTag = sessionLocal.fpTag
    request.isAvailable = binding?.toggleHome?.isOn ?: false
    return true
  }

  private fun saveUpdateGeneralAppointment() {
    showProgress()
    viewModel?.updateGeneralService(request)?.observeOnce(viewLifecycleOwner) {
      hideProgress()
      if (it.isSuccess().not()) {
        showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      } else{
        showSnackBarPositive(getString(R.string.data_updated_successfully))
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
          requireActivity().finish()
        },1500)
      }
    }
  }
}