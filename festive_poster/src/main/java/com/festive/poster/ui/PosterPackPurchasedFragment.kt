package com.festive.poster.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterPackPurchasedBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackPurchasedModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class PosterPackPurchasedFragment: AppBaseFragment<FragmentPosterPackPurchasedBinding, BaseViewModel>(),RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(): PosterPackPurchasedFragment {
            return PosterPackPurchasedFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_purchased
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
            PosterPackPurchasedModel("Navratri",10, arrayListOf(PosterModel(null), PosterModel(null))),
            PosterPackPurchasedModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackPurchasedModel("Navratri",10, arrayListOf(PosterModel(null))),
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList,this)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.POSTER_PACK_PURCHASED_CLICK.ordinal->{
                addFragmentReplace(R.id.container,PosterListFragment.newInstance("Navratri"),true)
            }
        }
    }

}