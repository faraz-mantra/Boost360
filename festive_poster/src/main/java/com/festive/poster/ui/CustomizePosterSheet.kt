package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.databinding.BsheetCustomizePosterBinding
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUtil

class CustomizePosterSheet: BaseBottomSheetDialog<BsheetCustomizePosterBinding, BaseViewModel>() {

    private val TAG = "CustomizePosterSheet"
    private var path: String?=null
    private val RC_IMAGE_PCIKER=422
    private var sharedViewModel:FestivePosterSharedViewModel?=null
    private var packTag:String?=null

    companion object{
        val BK_TAG="BK_TITLE"
        @JvmStatic
        fun newInstance(tag:String): CustomizePosterSheet {
            val bundle = Bundle().apply {
                putString(BK_TAG,tag)
            }
            val fragment =CustomizePosterSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_customize_poster
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        packTag = arguments?.getString(BK_TAG)
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)

        setOnClickListener(binding?.ivCancel,binding?.uploadSelfie,binding?.tvUpdateInfo)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.ivCancel->{
                dismiss()
            }
            binding?.uploadSelfie->{

                ImagePicker.with(this).start(RC_IMAGE_PCIKER)
            }
            binding?.tvUpdateInfo->{
                Log.i(TAG, "path: $path")
                sharedViewModel?.customizationDetails?.value  = PosterCustomizationModel(
                    packTag!!,
                    binding?.etName?.text.toString(),
                    binding?.etEmail?.text.toString(),
                    binding?.etWhatsapp?.text.toString(),
                    binding?.etDesc?.text.toString(),
                    binding?.etWebsite?.text.toString(),
                    path
                    )
                PosterPaymentSheet().show(parentFragmentManager,PosterPaymentSheet::class.java.name)
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK&&requestCode==RC_IMAGE_PCIKER){
            openCropFragment(data)
        }
    }

    private fun openCropFragment(data: Intent?) {
        val uri = data?.data!!
        path = FileUtil.getTempFile(requireContext(),uri)?.path
        binding?.ivUserImg?.setImageURI(uri)
        showUserImage()

    }

    private fun showUserImage() {
        binding?.layoutImage?.visible()
        binding?.layoutNoImage?.gone()

    }
}