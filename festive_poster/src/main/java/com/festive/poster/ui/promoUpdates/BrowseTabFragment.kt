package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentBrowseTabBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList

class BrowseTabFragment: AppBaseFragment<FragmentBrowseTabBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var sharedViewModel: FestivePosterSharedViewModel?=null
   // var categoryList=ArrayList<PosterPackModel>()
    var dataList=ArrayList<PosterPackModel>()

    private var session: UserSessionManager? = null

    override fun getLayout(): Int {
        return R.layout.fragment_browse_tab
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): BrowseTabFragment {
            val fragment = BrowseTabFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onResume() {
        super.onResume()
    }
    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        getTemplateViewConfig()
       // setupDummyList()
     //   setRealData()

    }
/*
    private fun setupDummyList() {

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
    }*/

    /*fun setRealData(data:ArrayList<PosterPackModel>){
        categoryList.clear()
        data.forEach { categoryList.add(it.copy()!!) }
        categoryList.forEach { it.list_layout= RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()}

        if (isAdded){
            val adapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!,this)
            binding?.rvCat?.adapter = adapter
            binding?.rvCat?.layoutManager = GridLayoutManager(requireActivity(),2)
        }



    }*/


    private fun getTemplateViewConfig() {
        viewModel?.getTemplateConfig(Constants.PROMO_FEATURE_CODE,session?.fPID, session?.fpTag)
            ?.observeOnce(this, {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.allTemplates.tags)
                    fetchTemplates(tagArray, response)
                }

            })
    }

    private fun prepareTagForApi(tags: List<PosterPackTagModel>): ArrayList<String> {
        val list = ArrayList<String>()
        tags.forEach {
            list.add(it.tag)
        }
        return list
    }

    private fun fetchTemplates(tagArray: ArrayList<String>, response: GetTemplateViewConfigResponse) {
        viewModel?.getTemplates(session?.fPID, session?.fpTag, tagArray)
            ?.observeOnce(viewLifecycleOwner, {
                dataList = ArrayList()
                val templates_response = it as? GetTemplatesResponse
                templates_response?.let {
                    response.Result.allTemplates.tags.forEach { pack_tag ->
                        val templateList = ArrayList<PosterModel>()
                        templates_response.Result.templates.forEach { template ->
                            var posterTag = template.tags.find { posterTag -> posterTag == pack_tag.tag }
                            if ( posterTag != null && template.active) {
                                template.greeting_message = pack_tag.description
                                template.layout_id = RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout()
                                templateList.add(template.clone()!!)
                            }
                        }
                        dataList?.add(PosterPackModel(pack_tag,
                            templateList.toArrayList(),
                            isPurchased = pack_tag.isPurchased,
                            list_layout = RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout()))

                    }
                    sharedViewModel?.browseAllPosterPackList = dataList
                    // getPriceOfPosterPacks()
                    // rearrangeList()
                    val adapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList,this)
                    binding?.rvCat?.adapter = adapter
                    binding?.rvCat?.layoutManager = GridLayoutManager(requireActivity(),2)
                    hideProgress()
                }
            })
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.BROWSE_TAB_POSTER_CAT_CLICKED.ordinal->{
                addFragment(R.id.container,BrowseAllFragment.newInstance(dataList,position),true,true)
            }
        }
    }
}