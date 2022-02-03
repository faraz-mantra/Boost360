package com.appservice.ui.updatesBusiness

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseBottomSheetFragment
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.framework.models.UpdateDraftBody
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId

class UpdateDraftBSheet:AppBaseBottomSheetFragment<BsheetUpdateDraftBinding,UpdatesViewModel>() {


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
                val updateDraftBody= UpdateDraftBody(clientId,
                    "",sessionManager?.fpTag!!,""
                )
                updateState(updateDraftBody)
            }
            binding!!.btnSaveDraft->{
                val updateDraftBody= UpdateDraftBody(clientId,
                    "sds",sessionManager?.fpTag!!,"https://images.unsplash.com/photo-1643694941418-ab65214f34fd?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=465&q=80"
                )
                updateState(updateDraftBody)
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

    fun updateState(updateDraftBody: UpdateDraftBody) {
//        viewModel?.updateFirebaseState(arguments?.getString(BK_TEXT))?.observe(viewLifecycleOwner,{
//
//        })

        showProgress()
        if (sessionManager?.fpTag!=null){

            viewModel?.updateDraft(updateDraftBody)?.observe(viewLifecycleOwner,{
                hideProgress()
                requireActivity().finish()
            })
        }

    }



}