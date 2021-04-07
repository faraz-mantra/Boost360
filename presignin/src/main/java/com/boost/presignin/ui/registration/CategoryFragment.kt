package com.boost.presignin.ui.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentCategoryBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


class CategoryFragment : BaseFragment<FragmentCategoryBinding, BaseViewModel>() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CategoryFragment()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_category
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}