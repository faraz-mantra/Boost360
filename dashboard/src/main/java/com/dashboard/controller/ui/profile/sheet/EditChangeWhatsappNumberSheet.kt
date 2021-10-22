package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.dashboard.R
import com.dashboard.databinding.SheetChangeWhatsappNumberBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class EditChangeWhatsappNumberSheet : BaseBottomSheetDialog<SheetChangeWhatsappNumberBinding, UserProfileViewModel>() {

  private var mobile: String? = null

  companion object {
    val IK_MOB: String = "IK_MOB"
  }

  override fun getLayout(): Int {
    return R.layout.sheet_change_whatsapp_number
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    mobile = arguments?.getString(EditChangeMobileNumberSheet.IK_MOB)
    binding?.cetWaNo?.setText(mobile)
    setOnClickListener(binding?.btnPublish, binding?.rivCloseBottomSheet, binding?.btnRemove)
    viewListeners()

  }

  private fun viewListeners() {
    binding?.checkbox?.setOnCheckedChangeListener { compoundButton, b ->
      if (b) {
        binding?.btnPublish?.text = getString(R.string.save_amp_opt_in_for_updates)
      } else {
        binding?.btnPublish?.text = getString(R.string.save)
      }
    }

    binding?.cetWaNo?.addTextChangedListener {
      binding?.btnPublish?.isEnabled = it?.length == 10
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        binding?.progressBar?.visible()
        viewModel?.updateWhatsappNo(binding?.cetWaNo?.text.toString(), binding?.checkbox?.isChecked, UserSessionManager(baseActivity).userProfileId)?.observeOnce(viewLifecycleOwner, {
          if (it.isSuccess()) {
            dismiss()
          }else showShortToast(it.message())
          binding?.progressBar?.gone()
        })
      }
      binding?.btnRemove -> {
        RemoveWhatsappNumberSheet().show(parentFragmentManager, RemoveWhatsappNumberSheet::class.java.name)
        dismiss()
      }
      binding?.rivCloseBottomSheet -> {
        dismiss()
      }
    }
  }
}