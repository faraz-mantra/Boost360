package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.dashboard.R
import com.dashboard.databinding.SheetChangeUsernameBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class EditChangeUserNameSheet : BaseBottomSheetDialog<SheetChangeUsernameBinding, UserProfileViewModel>() {

  private var userName:String?=null
  companion object{
    val IK_NAME:String="IK_NAME"
  }
  override fun getLayout(): Int {
    return R.layout.sheet_change_username
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    userName = arguments?.getString(IK_NAME)
    binding?.cetBusinessName?.setText(userName)
    setOnClickListener(binding?.btnPublish)
    viewListeners()
  }

  private fun viewListeners() {
    binding?.cetBusinessName?.addTextChangedListener {
      binding?.ctvBusinessNameCount?.text="${it?.length}/40"
      binding?.btnPublish?.isEnabled = it?.length?:0<40
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnPublish->{
        updateUserNameApi()
      }
    }
  }

  private fun updateUserNameApi() {
    binding?.progressBar?.visible()
    viewModel?.updateUserName(binding?.cetBusinessName?.text.toString(),
      UserSessionManager(requireContext()).userProfileId)?.observe(viewLifecycleOwner,{
      binding?.progressBar?.gone()

      if (it.status==200){
        dismiss()
      }
    })

  }
}