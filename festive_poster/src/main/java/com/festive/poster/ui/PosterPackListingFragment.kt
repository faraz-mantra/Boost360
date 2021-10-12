package com.festive.poster.ui

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterPackListingBinding
import com.festive.poster.databinding.SheetEditShareMessageBinding
import com.festive.poster.models.*
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList
import com.framework.webengageconstant.GET_FESTIVAL_POSTER_PACK
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class PosterPackListingFragment:
    AppBaseFragment<FragmentPosterPackListingBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private  val TAG = "PosterPackListingFragme"
    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var sharedViewModel:FestivePosterSharedViewModel?=null
    private var dataList:ArrayList<PosterPackModel>?=null
    private var session:UserSessionManager?=null

    companion object {
        @JvmStatic
        fun newInstance(): PosterPackListingFragment {
            return PosterPackListingFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_listing
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        setObserver()
        getTemplateViewConfig()
    }

    private fun setObserver() {
        sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner,{

           /*val posterPack = dataList?.find { posterPackModel -> posterPackModel.tagsModel.Tag==it.tag }
            Log.i(TAG, "poster pack: ${Gson().toJson(posterPack?.tagsModel)}")
            posterPack?.posterList?.forEach {posterModel ->
                Log.i(TAG, "poster model: ${Gson().toJson(posterModel)}")
                posterModel.Keys.forEach { posterKeyModel ->
                    if (posterKeyModel.Name=="Title"){
                        posterKeyModel.Custom = it.name
                    }
                }

            }

            Log.i(TAG, "result: ${Gson().toJson(dataList?.get(1))}")
            adapter?.notifyDataSetChanged()*/

        })
    }


    private fun getTemplateViewConfig() {

        showProgress()
        viewModel?.getTemplateConfig(session?.fPID,session?.fpTag)
            ?.observe(viewLifecycleOwner,{
                Log.i(TAG, "template config: ${Gson().toJson(it)}")
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.TodaysPicks.Tags)
                    fetchTemplates(tagArray,response)
                }
            })




    }

    private fun fetchTemplates(tagArray: ArrayList<String>, response: GetTemplateViewConfigResponse) {
        viewModel?.getTemplates(session?.fPID,session?.fpTag,tagArray)
            ?.observe(viewLifecycleOwner,{
                hideProgress()
                dataList = ArrayList()
                val templates_response = it as? GetTemplatesResponse

                templates_response?.let {
                    response.Result.TodaysPicks.Tags.forEach {pack_tag->

                        val templateList = ArrayList<PosterModel>()

                        templates_response.Result.Templates.forEach { template->
                            if (template.Tags.find { posterTag-> posterTag ==pack_tag.Tag }!=null){
                                templateList.add(
                                    template.clone()!!)
                            }
                        }


                        dataList?.add(PosterPackModel(pack_tag,templateList.toArrayList()))
                    }

                    adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
                        dataList!!,this)
                    binding?.rvPosters?.adapter = adapter
                    binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())


                }
            })
    }


    private fun prepareTagForApi(tags: List<PosterPackTagModel>): ArrayList<String> {

        val list = ArrayList<String>()
        tags.forEach {
            list.add(it.Tag)
        }
        return list
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

        when(actionType){
            RecyclerViewActionType.GET_POSTER_PACK_CLICK.ordinal->{
                WebEngageController.trackEvent(GET_FESTIVAL_POSTER_PACK)
                item as PosterPackModel
                CustomizePosterSheet.newInstance(item.tagsModel.Tag,true).show(requireActivity().supportFragmentManager,CustomizePosterSheet::class.java.name)
            }


        }
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
                WebEngageController.trackEvent(GET_FESTIVAL_POSTER_PACK)
                parentItem as PosterPackModel
                CustomizePosterSheet.newInstance(parentItem.tagsModel.Tag,true).show(requireActivity().supportFragmentManager,CustomizePosterSheet::class.java.name)
            }


        }
    }


}
