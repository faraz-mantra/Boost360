package com.boost.presignin.ui.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentBusinessDetailsBinding
import com.boost.presignin.model.category.CategoryDataModel
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


class BusinessDetailsFragment : BaseFragment<FragmentBusinessDetailsBinding, BaseViewModel>() {


    companion object {

        @JvmStatic
        fun newInstance(category: CategoryDataModel) =
                BusinessDetailsFragment().apply {

                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.confirmButton?.setOnClickListener {
            addFragmentReplace(com.framework.R.id.container, BusinessWebsiteFragment.newInstance(),true);
        }

    }
}