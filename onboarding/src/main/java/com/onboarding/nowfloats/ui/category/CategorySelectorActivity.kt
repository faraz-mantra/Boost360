package com.onboarding.nowfloats.ui.category

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.framework.extensions.refreshLayout
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.ActivityCategorySelectorBinding
import com.onboarding.nowfloats.viewmodel.category.CategoryViewModel
import io.reactivex.disposables.Disposable

class CategorySelectorActivity : AppBaseActivity<ActivityCategorySelectorBinding, CategoryViewModel>(), CategorySelectorAnimator.OnAnimationCompleteListener {

    private val animations = CategorySelectorAnimator()
    private val categorySelectorFragment = CategorySelectorFragment.newInstance()

    override fun getLayout(): Int {
        return R.layout.activity_category_selector
    }

    override fun getViewModelClass(): Class<CategoryViewModel> {
        return CategoryViewModel::class.java
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.home -> onBackPressed()
        }
    }

    override fun getObservables(): List<Disposable?> {
        return animations.getObservables()
    }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.home)
    binding?.imageRiya?.post {
      resizeLargeLogo()

      binding?.imageRiyaLarge?.post {
        binding?.imageRiyaLarge?.alpha = 1f
        animations.setViews(
                imageRiyaLarge = binding?.imageRiyaLarge,
                imageRiya = binding?.imageRiya,
                motionLayout = binding?.motionLayout,
                toolbarTitle = binding?.title,
                subTitleForeground = binding?.subTitleForeground
        )
        animations.listener = this
        animations.startAnimation()
      }
    }
  }

  private fun resizeLargeLogo() {
    val params = binding?.imageRiyaLarge?.layoutParams as? FrameLayout.LayoutParams
    params?.height = ScreenUtils.instance.getWidth(this)
    params?.width = params?.height
    params?.topMargin = params?.height?.div(2)
    binding?.imageRiyaLarge?.layoutParams = params
    binding?.imageRiyaLarge?.refreshLayout()
  }

  override fun onSubTitleAnimationComplete() {
    super.onSubTitleAnimationComplete()
      addFragmentReplace(binding?.fragmentContainer?.id, categorySelectorFragment, false)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    return if (animations.isAnimating()) true
    else super.dispatchTouchEvent(ev)
  }
}
