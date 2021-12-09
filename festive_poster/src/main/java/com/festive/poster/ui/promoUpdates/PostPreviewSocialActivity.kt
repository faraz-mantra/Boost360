package com.festive.poster.ui.promoUpdates

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import com.festive.poster.R
import com.festive.poster.databinding.ActivityPostPreviewSocialBinding
import com.festive.poster.models.promoModele.SocialPlatformModel
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.promoUpdates.bottomSheet.PostingProgressBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class PostPreviewSocialActivity : BaseActivity<ActivityPostPreviewSocialBinding, BaseViewModel>(), RecyclerItemClickListener {

    override fun getLayout(): Int {
        return R.layout.activity_post_preview_social
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        initUI()
    }

    private fun initUI() {
        binding?.tvChooseAPromoPack?.setOnClickListener {
            SubscribePlanBottomSheet().show(supportFragmentManager, SubscribePlanBottomSheet::class.java.name)
        }

        binding?.tvPostUpdate?.setOnClickListener {
            PostingProgressBottomSheet().show(supportFragmentManager, PostingProgressBottomSheet::class.java.name)
        }

        binding?.ivClosePreview?.setOnClickListener {
            onBackPressed()
        }

        val socialPlatformModel = SocialPlatformModel().getData(this@PostPreviewSocialActivity)
        binding?.rvSocialPlatforms?.apply {
            adapter = AppBaseRecyclerViewAdapter(
                this@PostPreviewSocialActivity,
                socialPlatformModel,
                this@PostPreviewSocialActivity
            )
        }

        val socialPreviewModel = SocialPreviewModel().getData(this@PostPreviewSocialActivity)
        binding?.rvPostPreview?.apply {
            adapter  = AppBaseRecyclerViewAdapter(this@PostPreviewSocialActivity, socialPreviewModel, this@PostPreviewSocialActivity)
        }

        isUserPremium(true)
    }

    private fun isUserPremium(isUserPremium:Boolean = false){
        if (isUserPremium.not()){
            binding?.tvChooseAPromoPack?.visible()
            binding?.tvPostUpdate?.gone()
            enableGrayScale(binding?.tvPreviewTitle, binding?.tvSelected, binding?.rvSocialPlatforms)
        }else{
            binding?.tvChooseAPromoPack?.gone()
            binding?.tvPostUpdate?.visible()
        }
    }

    private fun enableGrayScale(vararg views: View?){
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val greyscalePaint = Paint()
        greyscalePaint.colorFilter = ColorMatrixColorFilter(cm)
        for (view in views) view?.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint)
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
}