package com.onboarding.nowfloats.ui.channel

import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import com.framework.utils.ConversionUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.databinding.ActivityChannelPickerBinding
import com.onboarding.nowfloats.extensions.getParcelable
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel

class ChannelPickerActivity : AppBaseActivity<ActivityChannelPickerBinding, ChannelPlanViewModel>(), ChannelSelectorAnimator.OnAnimationCompleteListener, MotionLayout.TransitionListener {

    private var requestFloatsModel: RequestFloatsModel? = null
    private val animations = ChannelSelectorAnimator()
    val fragment: ChannelPickerFragment?
        get() = supportFragmentManager.findFragmentById(R.id.channelPickerFragment) as? ChannelPickerFragment

    override fun getLayout(): Int {
        return R.layout.activity_channel_picker
    }

    override fun getViewModelClass(): Class<ChannelPlanViewModel> {
        return ChannelPlanViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.imageRiya?.post {
            animations.setViews(
                motionLayout = binding?.motionLayout, imageView = binding?.imageView,
                titleForeground = binding?.titleForeground, subTitleForeground = binding?.subTitleForeground
            )
            animations.listener = this
            animations.startAnimation()
        }
        requestFloatsModel = NavigatorManager.getRequest()
        fragment?.updateBundleArguments(intent.extras)
        setCategoryImage()
        setOnClickListener(binding?.home)
        binding?.motionLayout?.setTransitionListener(this)
    }

    private fun setCategoryImage() {
        binding?.categoryImage?.setImageDrawable(requestFloatsModel?.categoryDataModel?.getImage(this))
        binding?.categoryImage?.setTintColor(ResourcesCompat.getColor(resources, R.color.white, theme))
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.home -> onBackPressed()
        }
    }

    override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {

    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

    }

    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
        binding?.categoryView?.cardElevation = ConversionUtils.dp2px(4f) * (1 - progress)
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

    }

    override fun onAnimationComplete() {
        super.onAnimationComplete()
        fragment?.startAnimationChannelFragment()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        requestFloatsModel?.channels = null
        NavigatorManager.popCurrentScreen(ScreenModel.Screen.CHANNEL_SELECT)
    }
}
