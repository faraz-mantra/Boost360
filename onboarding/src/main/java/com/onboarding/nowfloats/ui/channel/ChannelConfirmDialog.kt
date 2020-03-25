package com.onboarding.nowfloats.ui.channel

import android.view.View
import android.widget.FrameLayout
import com.framework.base.BaseDialogFragment
import com.framework.extensions.refreshLayout
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogCategorySelectorConfirmBinding

class ChannelConfirmDialog : BaseDialogFragment<DialogCategorySelectorConfirmBinding, BaseViewModel>() {

    private var count: Int = 0
    private var onConfirmClicked: () -> Unit = { }
    private var animator = ChannelConfirmDialogAnimator()

    override fun getLayout(): Int {
        return R.layout.dialog_category_selector_confirm
    }

    override fun onViewCreated() {
        if (count > 0) {
            val title = StringBuilder(
                resources.getString(R.string.you_selected) + "\n$count " + resources.getString(R.string.channel)
            )
            if (count > 1) title.append(resources.getString(R.string.more_than_one_add_s))

            binding?.title?.text = title.toString()
        } else {
            binding?.title?.text = ""
        }
        setClickListeners(binding?.confirm)

        binding?.dialogRoot?.post {
            val imageParams = binding?.imageRiyaLarge?.layoutParams as? FrameLayout.LayoutParams
            imageParams?.height = binding?.dialogRoot?.measuredHeight?.div(2)?.toInt()
            imageParams?.width = imageParams?.height
            imageParams?.topMargin = imageParams?.height?.div(2)
            binding?.imageRiyaLarge?.layoutParams = imageParams
            binding?.imageRiyaLarge?.refreshLayout()

            binding?.imageRiyaLarge?.post {
                animator.setViews(
                    imageRiyaLarge = binding?.imageRiyaLarge,
                    imageRiya = binding?.imageRiya,
                    title = binding?.title,
                    desc = binding?.desc,
                    confirm = binding?.confirm
                )
                animator.startAnimation()
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.confirm -> {
                this.onConfirmClicked()
                this.dismiss()
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.MaterialDialogTheme
    }

    override fun getWidth(): Int? {
        return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(32f)
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun setOnConfirmClick(closure: () -> Unit) {
        this.onConfirmClicked = closure
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}