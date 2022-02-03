package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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


    private var caption: String?=null
    private var posterImgPath:String?=null

    companion object {
        val IK_POSTER="IK_POSTER"
        val IK_CAPTION="IK_CAPTION"

        @JvmStatic
        fun newInstance(posterImgPath: String?,caption:String?): PostSuccessBottomSheet {
            val bundle = Bundle().apply {}
            bundle.putString(IK_POSTER, posterImgPath)
            bundle.putString(IK_CAPTION, caption)

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
        posterImgPath = arguments?.getString(IK_POSTER)
        caption = arguments?.getString(IK_CAPTION)

        setOnClickListener(binding?.ivWhatsapp,binding?.ivInstagram,binding?.ivOther)

        binding!!.ivPosterIcon.loadUsingGlide(posterImgPath,false)
        binding?.ivClosePostSuccess?.setOnClickListener {
            dismiss()
        }
        binding!!.cardBigAnim.isVisible = posterImgPath==null
        binding!!.cardSmallAnim.isVisible = posterImgPath!=null

    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (posterImgPath!=null){
            val imgFile = File(posterImgPath!!)

            when(v){
                binding?.ivWhatsapp->{

                    imgFile.shareAsImage(requireActivity(),PackageNames.WHATSAPP,caption)

                }
                binding?.ivInstagram->{
                    imgFile.shareAsImage(requireActivity(),PackageNames.INSTAGRAM,caption)

                }
                binding?.ivOther->{
                    imgFile.shareAsImage(requireActivity(),null,caption)

                }
            }
        }

    }
}