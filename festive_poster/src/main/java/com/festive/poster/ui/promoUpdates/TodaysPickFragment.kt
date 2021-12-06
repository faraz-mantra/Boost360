package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.PosterDetailsModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.models.promoModele.TodaysPickModel
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.models.response.UpgradeGetDataResponse
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
import com.google.gson.Gson

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var sharedViewModel: FestivePosterSharedViewModel? = null

    private var session: UserSessionManager? = null
    var dataList: ArrayList<PosterPackModel>? = null
    private  val TAG = "TodaysPickFragment"
    override fun getLayout(): Int {
        return R.layout.fragment_todays_pick
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): TodaysPickFragment {
            val fragment = TodaysPickFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)

        session = UserSessionManager(requireActivity())

        getTemplateViewConfig()



    }

    private fun setDummyData() {
        val dataList = arrayListOf(
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),PosterPackModel(PosterPackTagModel("","","","",false,-1),ArrayList(),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),PosterPackModel(PosterPackTagModel("","","","",false,-1),ArrayList(),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),

            )


        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvTemplates?.adapter = adapter
        binding?.rvTemplates?.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun getTemplateViewConfig() {
        showProgress()
        viewModel?.getTemplateConfig(session?.fPID, session?.fpTag)
            ?.observeOnce(viewLifecycleOwner, {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.templatePacks.tags)
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
                    response.Result.templatePacks.tags.forEach { pack_tag ->
                        val templateList = ArrayList<PosterModel>()
                        templates_response.Result.templates.forEach { template ->
                            var posterTag = template.tags.find { posterTag -> posterTag == pack_tag.tag }
                            if ( posterTag != null && template.active) {
                                template.greeting_message = pack_tag.description
                                template.layout_id = RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout()
                                templateList.add(template.clone()!!)
                            }
                        }
                        dataList?.add(PosterPackModel(pack_tag, templateList.toArrayList(),isPurchased = pack_tag.isPurchased,list_layout = RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()))

                    }
                    getPriceOfPosterPacks()
                }
            })
    }

    private fun getPriceOfPosterPacks() {
        viewModel?.getUpgradeData()?.observeOnce(viewLifecycleOwner, {
            val response = it as? UpgradeGetDataResponse
            response?.let {
                dataList?.forEach { pack ->
                    val feature_festive = response.Data.firstOrNull()?.features?.find { feature ->
                        feature.feature_code == pack.tagsModel.tag
                    }
                    pack.price = feature_festive?.price ?: 0.0

                    Log.i(TAG, "festive price: ${feature_festive?.price}")
                }




                sharedViewModel?.posterPackList?.postValue(dataList)
                // rearrangeList()
                adapter = AppBaseRecyclerViewAdapter(baseActivity, dataList!!, this)
                binding?.rvTemplates?.adapter = adapter
                binding?.rvTemplates?.layoutManager = LinearLayoutManager(requireActivity())
                hideProgress()
            }
        })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
}