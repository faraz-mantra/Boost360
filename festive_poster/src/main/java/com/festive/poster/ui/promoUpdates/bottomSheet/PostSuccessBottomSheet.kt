package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPostSuccessBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.utils.SvgUtils
import com.framework.base.BaseBottomSheetDialog
import com.framework.constants.PackageNames
import com.framework.models.BaseViewModel
import com.framework.utils.convertStringToObj
import com.framework.utils.loadUsingGlide
import com.framework.utils.shareAsImage
import com.google.gson.Gson
import java.io.File

class PostSuccessBottomSheet : BaseBottomSheetDialog<BsheetPostSuccessBinding, BaseViewModel>() {


    private var posterImgPath:String?=null

    companion object {
        val IK_POSTER="IK_POSTER"

        @JvmStatic
        fun newInstance(posterImgPath: String?): PostSuccessBottomSheet {
            val bundle = Bundle().apply {}
            bundle.putString(PostingProgressBottomSheet.IK_POSTER, posterImgPath)

            val fragment = PostSuccessBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_post_success
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        posterImgPath = arguments?.getString(PostPreviewSocialActivity.IK_POSTER)

        setOnClickListener(binding?.ivWhatsapp,binding?.ivInstagram,binding?.ivOther)

        binding!!.ivPosterIcon.loadUsingGlide(posterImgPath,false)
        binding?.ivClosePostSuccess?.setOnClickListener {
            dismiss()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val imgFile = File(posterImgPath)

        when(v){
            binding?.ivWhatsapp->{

                imgFile.shareAsImage(requireActivity(),PackageNames.WHATSAPP)

            }
            binding?.ivInstagram->{
                imgFile.shareAsImage(requireActivity(),PackageNames.INSTAGRAM)

            }
            binding?.ivOther->{
                imgFile.shareAsImage(requireActivity(),null)

            }
        }
    }
}