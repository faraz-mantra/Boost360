package com.onboarding.nowfloats.ui.features

import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.feature.FeatureDetailsModel
import com.onboarding.nowfloats.model.feature.FeatureModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogFeatureDetailsBottomSheetBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import java.util.*

class FeaturesBottomSheetDialog : BaseDialogFragment<DialogFeatureDetailsBottomSheetBinding, BaseViewModel>() {

    private var adapter: AppBaseRecyclerViewAdapter<FeatureDetailsModel>? = null
    private var feature: FeatureModel? = null

    override fun getLayout(): Int {
        return R.layout.dialog_feature_details_bottom_sheet
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getTheme(): Int {
        return R.style.MaterialDialogThemeFull
    }

    override fun onViewCreated() {
        feature?.let { it1 ->
            binding?.title?.text = it1.title
            val drawable = it1.getDrawable(baseActivity) ?: return
            binding?.image?.setImageDrawable(drawable)
            binding?.image?.post {
                binding?.image?.fadeIn(300L)?.andThen(binding?.title?.fadeIn(200L))
                    ?.andThen(binding?.shimmerView?.parentShimmerLayout?.fadeIn())?.andThen {
                        binding?.shimmerView?.parentShimmerLayout?.visibility = View.VISIBLE
                        binding?.shimmerView?.parentShimmerLayout?.showShimmer(true)
                        setAdapter(it1.details)
                    }?.subscribe()
            }
        }
        setOnClickListener(binding?.okay)
    }

    fun setFeature(model: FeatureModel?) {
        feature = model
    }

    private fun setAdapter(details: ArrayList<FeatureDetailsModel>) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                binding?.shimmerView?.parentShimmerLayout?.post {
                    binding?.shimmerView?.parentShimmerLayout?.visibility = View.GONE
                    binding?.shimmerView?.parentShimmerLayout?.hideShimmer()
                    binding?.recyclerView?.visibility = View.VISIBLE
                    adapter = AppBaseRecyclerViewAdapter(baseActivity, details)
                    binding?.recyclerView?.adapter = adapter
                    binding?.okay?.visibility = View.VISIBLE
                }
            }
        }, 1000)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.okay -> dismiss()
        }
    }
}