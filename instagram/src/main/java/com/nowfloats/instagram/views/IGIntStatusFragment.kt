package com.nowfloats.instagram.views

import android.view.View
import com.framework.base.BaseActivity
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewAdapter
import com.nowfloats.instagram.base.AppBaseFragment
import com.framework.models.BaseViewModel
import com.nowfloats.instagram.R
import com.nowfloats.instagram.databinding.FragmentIgIntStatusBinding
import com.nowfloats.instagram.databinding.FragmentIgIntroScreenBinding
import com.nowfloats.instagram.models.IGFeaturesModel

class IGIntStatusFragment: AppBaseFragment<FragmentIgIntStatusBinding, BaseViewModel>() {


    companion object{

        fun newInstance():IGIntStatusFragment{
            val fragment = IGIntStatusFragment()
            return fragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_ig_int_status
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.btnNext)
    }



    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.btnNext->{


            }
        }
    }
}