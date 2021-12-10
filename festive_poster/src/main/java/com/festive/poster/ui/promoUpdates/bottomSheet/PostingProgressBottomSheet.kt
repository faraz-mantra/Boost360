package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPostingProgressBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.utils.SvgUtils
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.convertStringToObj
import com.google.gson.Gson
import java.util.*
import kotlin.concurrent.schedule

class PostingProgressBottomSheet :
    BaseBottomSheetDialog<BsheetPostingProgressBinding, BaseViewModel>() {

    private var posterModel:PosterModel?=null
    companion object {

        val IK_POSTER="IK_POSTER"

        @JvmStatic
        fun newInstance(posterModel: PosterModel?): PostingProgressBottomSheet {
            val bundle = Bundle()
            bundle.putString(IK_POSTER,Gson().toJson(posterModel))
            val fragment = PostingProgressBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_posting_progress
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        posterModel = convertStringToObj<PosterModel?>(arguments?.getString(PostPreviewSocialActivity.IK_POSTER))
        SvgUtils.loadImage(posterModel?.variants?.firstOrNull()?.svgUrl,binding?.ivPostIcon!!,
        posterModel?.keys,posterModel?.isPurchased)
    }
}