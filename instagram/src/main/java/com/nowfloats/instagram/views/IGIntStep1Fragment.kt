package com.nowfloats.instagram.views

import com.framework.base.BaseActivity
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewAdapter
import com.nowfloats.instagram.base.AppBaseFragment
import com.framework.models.BaseViewModel
import com.nowfloats.instagram.R
import com.nowfloats.instagram.databinding.FragmentIgIntStep1Binding
import com.nowfloats.instagram.databinding.FragmentIgIntroScreenBinding
import com.nowfloats.instagram.models.IGFeaturesModel

class IGIntStep1Fragment: AppBaseFragment<FragmentIgIntStep1Binding, BaseViewModel>() {


    companion object{

        fun newInstance():IGIntStep1Fragment{
            val fragment = IGIntStep1Fragment()
            return fragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_ig_int_step1
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupSlider()

    }

    private fun setupSlider() {
        val featureList = arrayListOf(
            IGFeaturesModel("Easily publish your business updates to Instagram and bring more customers to your website.",
            R.drawable.ig_feature1),
            IGFeaturesModel("Get more leads and engagements by using Instagram to expand your customer base.",
                R.drawable.ig_feature_2),
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,featureList)

    }
}