package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentBrowseAllBinding
import com.festive.poster.models.PosterDetailsModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.models.promoModele.TodaysPickModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class BrowseAllFragment: AppBaseFragment<FragmentBrowseAllBinding, BaseViewModel>() {

    var categoryList:ArrayList<PosterPackModel>?=null

    override fun getLayout(): Int {
        return R.layout.fragment_browse_all
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): BrowseAllFragment {
            val fragment = BrowseAllFragment()
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
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))


        val adapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!)
        binding?.rvCat?.adapter = adapter
        binding?.rvCat?.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        setupPosterList()
    }

    fun setupPosterList(){
        val dataList = arrayListOf(
            PosterModel(true,"", PosterDetailsModel("",false,0.0,"",
                false),"",ArrayList(),ArrayList(),"",ArrayList(),"",R.layout.list_item_template,
            )
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())

    }
}