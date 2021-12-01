package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentBrowseTabBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class BrowseTabFragment: AppBaseFragment<FragmentBrowseTabBinding, BaseViewModel>(),RecyclerItemClickListener {

    var categoryList:ArrayList<PosterPackModel>?=null

    override fun getLayout(): Int {
        return R.layout.fragment_browse_tab
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): BrowseTabFragment {
            val fragment = BrowseTabFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        super.onCreateView()
        categoryList = ArrayList<PosterPackModel>()

        //dummy data
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()))

        val adapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!,this)
        binding?.rvCat?.adapter = adapter
        binding?.rvCat?.layoutManager = GridLayoutManager(requireActivity(),2)

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.BROWSE_TAB_POSTER_CAT_CLICKED.ordinal->{
                addFragmentReplace(R.id.container,BrowseAllFragment.newInstance(),true)
            }
        }
    }
}