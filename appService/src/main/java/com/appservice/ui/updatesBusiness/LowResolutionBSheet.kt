package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.appservice.R
import com.appservice.base.AppBaseBottomSheetFragment
import com.appservice.databinding.BsheetLowResolutionBinding
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.framework.models.UpdateDraftBody
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.toBase64
import java.io.File

class LowResolutionBSheet(val path: String, val fragment: AddUpdateBusinessFragmentV2, val launcher: ActivityResultLauncher<Intent>?) :AppBaseBottomSheetFragment<BsheetLowResolutionBinding,UpdatesViewModel>() {

    private val TAG = "LowResolutionBSheet"

    override fun getLayout(): Int {
        return R.layout.bsheet_low_resolution
    }

    override fun getViewModelClass(): Class<UpdatesViewModel> {
        return UpdatesViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding!!.btnProceed,binding!!.btnReplaceImage,binding!!.rivCloseBottomSheet)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.rivCloseBottomSheet->{
                dismiss()
            }

            binding!!.btnReplaceImage->{
                fragment.chooseOption()
                dismiss()
            }
            binding!!.btnProceed->{
//                UpdateCropImageActivity.launchActivity(
//                    path,
//                    requireActivity(),
//                    launcher
//                )
                dismiss()
            }
        }
    }



}