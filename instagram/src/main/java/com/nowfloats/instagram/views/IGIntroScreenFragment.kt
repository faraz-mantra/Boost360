package com.nowfloats.instagram.views

import android.view.View
import com.framework.base.BaseActivity
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewAdapter
import com.nowfloats.instagram.base.AppBaseFragment
import com.framework.models.BaseViewModel
import com.nowfloats.instagram.R
import com.nowfloats.instagram.databinding.FragmentIgIntroScreenBinding
import com.nowfloats.instagram.models.IGFeaturesModel

class IGIntroScreenFragment: AppBaseFragment<FragmentIgIntroScreenBinding, BaseViewModel>() {


    companion object{

        fun newInstance():IGIntroScreenFragment{
            val fragment = IGIntroScreenFragment()
            return fragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_ig_intro_screen
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupSlider()
        setOnClickListener(binding?.btnNext)
    }

    private fun setupSlider() {
        val featureList = arrayListOf(
            IGFeaturesModel("Easily publish your business updates to Instagram and bring more customers to your website.",
            R.drawable.ig_feature1),
            IGFeaturesModel("Get more leads and engagements by using Instagram to expand your customer base.",
                R.drawable.ig_feature_2),
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,featureList)
        binding!!.vpFeatures.adapter = adapter
        binding!!.dotIndicator.setViewPager2(binding!!.vpFeatures)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.btnNext->{
                addFragmentReplace(R.id.container,IGIntStepsFragment.newInstance(
                    IGIntStepsFragment.Step.STEP1
                ),true)

            }
        }
    }
}