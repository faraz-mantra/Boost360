package com.onboarding.nowfloats.ui.registration.instagram

import android.util.Log
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.databinding.ActivityInstagramContainerBinding
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.ui.registration.BaseRegistrationFragment

class RegistrationBusinessInstagramFragment:
    AppBaseActivity<ActivityInstagramContainerBinding,BaseViewModel>() {
    
    override var TAG = "RegistrationBusinessIns"
    override fun getLayout(): Int {
        return R.layout.activity_instagram_container
    }


    override fun onCreateView() {
        super.onCreateView()
        observeBackStack()

        addFragmentReplace(binding?.container?.id,IGIntroScreenFragment.newInstance(),true)
        binding?.ivCross?.setOnClickListener {
            val topFragment = getTopFragment()

            if (topFragment is IGIntStatusFragment){
                topFragment as IGIntStatusFragment
                if (topFragment.status==IGIntStatusFragment.Status.SUCCESS.name){
                    finish()
                }else{
                    onBackPressed()
                }
            }
        }
    }

    private fun observeBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {
            val topFragment = getTopFragment()
            if (topFragment!=null){
                when(topFragment){
                    is IGIntStepsFragment->{
                        binding!!.layoutSteps.visible()
                        handleStepUI(topFragment)
                        Log.i(TAG, "observeBackStack: "+topFragment.currentStep)
                    }
                    else->{
                        binding!!.layoutSteps.gone()

                    }
                }
            }else{
                this.finish()
            }
        }
    }

    private fun handleStepUI(topFragment: IGIntStepsFragment) {
        binding!!.ivS1.setImageResource(R.drawable.ic_ig_step_not_selected)
        binding!!.tvS1.setTextColor(ContextCompat.getColor(this,R.color.color88888))
        binding!!.ivS2.setImageResource(R.drawable.ic_ig_step_not_selected)
        binding!!.tvS2.setTextColor(ContextCompat.getColor(this,R.color.color88888))
        binding!!.ivS3.setImageResource(R.drawable.ic_ig_step_not_selected)
        binding!!.tvS3.setTextColor(ContextCompat.getColor(this,R.color.color88888))
        binding!!.ivS4.setImageResource(R.drawable.ic_ig_step_not_selected)
        binding!!.tvS4.setTextColor(ContextCompat.getColor(this,R.color.color88888))
        when(topFragment.currentStep){
            IGIntStepsFragment.Step.STEP1.name->{
                binding!!.ivS1.setImageResource(R.drawable.ic_ig_step_selected)
                binding!!.tvS1.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
            IGIntStepsFragment.Step.STEP2.name->{
                binding!!.ivS2.setImageResource(R.drawable.ic_ig_step_selected)
                binding!!.tvS2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
            IGIntStepsFragment.Step.STEP3.name->{
                binding!!.ivS3.setImageResource(R.drawable.ic_ig_step_selected)
                binding!!.tvS3.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
            IGIntStepsFragment.Step.STEP4.name->{
                binding!!.ivS4.setImageResource(R.drawable.ic_ig_step_selected)
                binding!!.tvS4.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            }
        }
    }

   /* override fun updateInfo() {
        requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.instagram }
        super.updateInfo()
    }*/

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

}