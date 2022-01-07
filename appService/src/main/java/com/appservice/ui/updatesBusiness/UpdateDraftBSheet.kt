package com.appservice.ui.updatesBusiness

import android.view.View
import com.appservice.R
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class UpdateDraftBSheet:BaseBottomSheetDialog<BsheetUpdateDraftBinding,BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.bsheet_update_draft
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding!!.btnSaveDraft,binding!!.btnDiscard,binding!!.rivCloseBottomSheet)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.rivCloseBottomSheet->{
                dismiss()
            }

            binding!!.btnDiscard->{
                val sessionLocal = UserSessionManager(requireActivity())
                sessionLocal.storeFPDetails(msgPost, "")
                sessionLocal.storeFPDetails(imagePost, "")
                dismiss()
                requireActivity().finish()
            }
            binding!!.btnSaveDraft->{
                dismiss()
                requireActivity().finish()
            }
        }
    }
}