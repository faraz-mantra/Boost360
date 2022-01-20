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
import com.google.gson.Gson

class PostSuccessBottomSheet : BaseBottomSheetDialog<BsheetPostSuccessBinding, BaseViewModel>() {


    private var posterModel:PosterModel?=null

    companion object {
        val IK_POSTER="IK_POSTER"

        @JvmStatic
        fun newInstance(posterModel: PosterModel?): PostSuccessBottomSheet {
            val bundle = Bundle().apply {}
            bundle.putString(PostingProgressBottomSheet.IK_POSTER, Gson().toJson(posterModel))

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
        posterModel = convertStringToObj<PosterModel?>(arguments?.getString(PostPreviewSocialActivity.IK_POSTER))

        setOnClickListener(binding?.ivWhatsapp,binding?.ivInstagram,binding?.ivOther,binding?.ivClosePostSuccess)
        SvgUtils.loadImage(posterModel?.variants?.firstOrNull()?.svgUrl,binding?.ivPosterIcon!!,
            posterModel?.keys,posterModel?.isPurchased)
        binding?.ivClosePostSuccess?.setOnClickListener {
            dismiss()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.ivWhatsapp->{
                SvgUtils.shareUncompressedSvg(posterModel?.variants?.firstOrNull()?.svgUrl,
                posterModel,requireActivity(),PackageNames.WHATSAPP)
            }
            binding?.ivInstagram->{
                SvgUtils.shareUncompressedSvg(posterModel?.variants?.firstOrNull()?.svgUrl,
                    posterModel,requireActivity(),PackageNames.INSTAGRAM)
            }
            binding?.ivOther->{
                SvgUtils.shareUncompressedSvg(posterModel?.variants?.firstOrNull()?.svgUrl,
                    posterModel,requireActivity(),"")
            }
            binding?.ivClosePostSuccess->{
                dismiss()
            }
        }
    }
}