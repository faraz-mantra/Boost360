package com.appservice.ui.updatesBusiness

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UpdateDraftBSheet:BaseBottomSheetDialog<BsheetUpdateDraftBinding,UpdatesViewModel>() {


    companion object{
        val BK_TEXT="BK_TEXT"
        val BK_IMAGE_PATH="BK_IMAGE_PATH"
        fun newInstance(text:String,imagePath:String?): UpdateDraftBSheet {
            val draftSheet= UpdateDraftBSheet()
            val bundle = Bundle().apply {
                putString(BK_TEXT,text)
                putString(BK_IMAGE_PATH,imagePath)
            }
            draftSheet.arguments=bundle
            return draftSheet

        }
    }
    override fun getLayout(): Int {
        return R.layout.bsheet_update_draft
    }

    override fun getViewModelClass(): Class<UpdatesViewModel> {
        return UpdatesViewModel::class.java
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
                uploadDraftImage()
                requireActivity().finish()
            }
        }
    }

    private fun uploadDraftImage() {
//        if (arguments?.getString(BK_IMAGE_PATH)!=null){
//            val imgFile = File(arguments?.getString(BK_IMAGE_PATH)!!)
//            viewModel?.uploadDraftImage(imgFile.asRequestBody("image/*".toMediaTypeOrNull())).observe(
//                viewLifecycleOwner,{
//                    updateState()
//                }
//            )
//        }else{
//            updateState()
//        }

    }

    fun updateState(){
//        viewModel?.updateFirebaseState(arguments?.getString(BK_TEXT))?.observe(viewLifecycleOwner,{
//
//        })
    }



}