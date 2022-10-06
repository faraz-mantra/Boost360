package com.appservice.ui.updatesBusiness

import android.os.Bundle
import android.util.Log
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseBottomSheetFragment
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.framework.models.UpdateDraftBody
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.toBase64
import java.io.File

class UpdateDraftBSheet:AppBaseBottomSheetFragment<BsheetUpdateDraftBinding,UpdatesViewModel>() {

    private val TAG = "UpdateDraftBSheet"

    private var text: String?=null

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
        text = arguments?.getString(BK_TEXT)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.rivCloseBottomSheet->{
                dismiss()
            }

            binding!!.btnDiscard->{
                text =""
                showProgress()
                updateState("")
            }
            binding!!.btnSaveDraft->{


                uploadDraftImage()
            }
        }
    }

    private fun uploadDraftImage() {
        showProgress()
        if (arguments?.getString(BK_IMAGE_PATH)!=null){
            val imgFile = File(arguments?.getString(BK_IMAGE_PATH)!!)
            viewModel?.putBizImageUpdateV2("draft",null,imgFile.toBase64())?.observe(viewLifecycleOwner
            ) {
                if (it.isSuccess()){
                    Log.i(TAG, "uploadDraftImage: "+it.stringResponse)
                    updateState(it.stringResponse)
                }else{
                    hideProgress()
                }
            }
        }else{
            updateState("")
        }

    }

    fun updateState(imageUrl: String?) {
//        viewModel?.updateFirebaseState(arguments?.getString(BK_TEXT))?.observe(viewLifecycleOwner,{
//
//        })

             val updateDraftBody= UpdateDraftBody(clientId,
                   text,sessionManager?.fpTag!!,
                 imageUrl
               )

        if (sessionManager?.fpTag!=null){

            viewModel?.updateDraft(updateDraftBody)?.observe(viewLifecycleOwner,{
                if (updateDraftBody.content.isNullOrEmpty().not()){
                    showLongToast(getString(R.string.your_post_was_saved_as_draft))
                }else{
                    showLongToast(getString(R.string.your_discarded_post))
                }
                hideProgress()
                requireActivity().finish()
            })
        }

    }



}