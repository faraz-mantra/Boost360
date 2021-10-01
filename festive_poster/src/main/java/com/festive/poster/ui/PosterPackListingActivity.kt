package com.festive.poster.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityPosterPackListingBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class PosterPackListingActivity:
    AppBaseActivity<ActivityPosterPackListingBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_poster_pack_listing
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
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            )

        val adapter = AppBaseRecyclerViewAdapter(this as BaseActivity<*, *>,dataList)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(this)
    }
}
