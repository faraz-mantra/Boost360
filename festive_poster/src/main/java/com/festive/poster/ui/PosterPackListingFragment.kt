package com.festive.poster.ui

import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterPackListingBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class PosterPackListingFragment:
    AppBaseFragment<FragmentPosterPackListingBinding, BaseViewModel>(),RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(): PosterPackListingFragment {
            return PosterPackListingFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_listing
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
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null),PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList,this)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }

    override fun onChildClick(
        childPosition: Int,
        parentPosition: Int,
        childItem: BaseRecyclerViewItem?,
        parentItem: BaseRecyclerViewItem?,
        actionType: Int
    ) {
        when(actionType){
            RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal->{
                CustomizePosterSheet().show(requireActivity().supportFragmentManager,CustomizePosterSheet::class.java.name)
            }
        }
    }


}
