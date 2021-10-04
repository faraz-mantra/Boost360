package com.festive.poster.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentPosterDownloadsBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackDownloadsModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class PosterDownloadsFragment: AppBaseFragment<FragmentPosterDownloadsBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): PosterDownloadsFragment {
            return PosterDownloadsFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_downloads
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setupList()
    }

    private fun setupList() {


        val dataList = arrayListOf(
            PosterPackDownloadsModel("Navratri",10, arrayListOf(PosterModel(null), PosterModel(null))),
            PosterPackDownloadsModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackDownloadsModel("Navratri",10, arrayListOf(PosterModel(null))),
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

}