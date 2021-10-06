package com.festive.poster.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentPosterListBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackPurchasedModel
import com.festive.poster.models.PosterPurchasedModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.utils.SvgCaching
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class PosterListFragment: AppBaseFragment<FragmentPosterListBinding, BaseViewModel>() {

    companion object {
        val BK_TITLE="BK_TITLE"
        @JvmStatic
        fun newInstance(title:String): PosterListFragment {
            val bundle = Bundle().apply {
                putString(BK_TITLE,title)
            }
            val fragment =PosterListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    var title:String?=null
    override fun getLayout(): Int {
        return R.layout.fragment_poster_list
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        title = arguments?.getString(BK_TITLE)
        setupList()
    }

    private fun setupList() {


        val dataList = arrayListOf(
            PosterPurchasedModel(null), PosterPurchasedModel(null),
            PosterPurchasedModel(null)
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())


    }

}