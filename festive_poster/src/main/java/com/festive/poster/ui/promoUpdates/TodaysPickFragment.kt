package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.*
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.models.promoModele.TodaysPickModel
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.festive.poster.utils.*
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.constants.IntentConstants
import com.framework.constants.PackageNames
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.convertListObjToString
import com.framework.utils.saveAsImageToAppFolder
import com.framework.utils.toArrayList
import com.framework.webengageconstant.Promotional_Update_Browse_All_Click
import com.framework.webengageconstant.Promotional_Update_View_More_Click
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var sharedViewModel: FestivePosterSharedViewModel? = null
    private var callbacks:TodaysPickFragment.Callbacks?=null
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
        fun newInstance(bundle: Bundle = Bundle(),callbacks: Callbacks): TodaysPickFragment {
            val fragment = TodaysPickFragment()
            fragment.arguments = bundle
            fragment.callbacks = callbacks
            return fragment
        }


    }

    interface Callbacks{
        fun onDataLoaded(data:ArrayList<PosterPackModel>)
    }

    override fun onCreateView() {
        session = UserSessionManager(requireActivity())
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)


        getTemplateViewConfig()
        setOnClickListener(binding?.cardBrowseAllTemplate)


    }

    override fun onResume() {
        super.onResume()

        refreshUserWidgets()

    }

    private fun refreshUserWidgets() {
        viewModel?.getUserDetails(session?.fpTag, clientId)?.observe(this) {
            if (it.isSuccess()) {
                val detail = it as? CustomerDetails
                detail?.FPWebWidgets?.let { list ->
                    session?.storeFPDetails(
                        Key_Preferences.STORE_WIDGETS,
                        convertListObjToString(list)
                    )

                }
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.cardBrowseAllTemplate->{
                WebEngageController.trackEvent(Promotional_Update_View_More_Click)
                val dataList = sharedViewModel?.browseAllPosterPackList
                if (dataList!=null){
                    addFragment(R.id.container,BrowseAllFragment.newInstance(dataList,0),
                        true,true)
                }

            }
        }
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
        startShimmer()
        viewModel?.getTemplateConfig(Constants.PROMO_FEATURE_CODE,session?.fPID, session?.fpTag)
            ?.observeOnce(viewLifecycleOwner) {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.todayPick.tags)
                    fetchTemplates(tagArray, response)
                }

            }
    }

    private fun startShimmer() {
        binding!!.shimmerLayout.visible()
        binding!!.shimmerLayout.startShimmer()
        binding!!.rvTemplates.gone()
        binding!!.cardBrowseAllTemplate.gone()
    }

    private fun stopShimmer() {
        binding!!.shimmerLayout.gone()
        binding!!.shimmerLayout.stopShimmer()
        binding!!.rvTemplates.visible()
        binding!!.cardBrowseAllTemplate.visible()
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
            ?.observeOnce(viewLifecycleOwner) {
                dataList = ArrayList()
                val templates_response = it as? GetTemplatesResponse
                templates_response?.let {
                    response.Result.todayPick.tags.forEach { pack_tag ->
                        val templateList = ArrayList<PosterModel>()
                        templates_response.Result.templates.forEach { template ->
                            var posterTag =
                                template.tags?.find { posterTag -> posterTag == pack_tag.tag }
                            if (posterTag != null && template?.active == true) {
                                template.greeting_message = pack_tag.description
                                template.layout_id =
                                    RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout()
                                templateList.add(template.clone()!!)
                            }
                        }
                        val filterdList= ArrayList<PosterModel>()
                        if (templateList.size>=4){
                            filterdList.addAll(
                                templateList.take(4))
                            filterdList.add(PosterModel(layout_id = RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()))
                        }else{
                            filterdList.addAll(templateList)
                        }
                        if (templateList.isNotEmpty()){
                            dataList?.add(
                                PosterPackModel(
                                    pack_tag,
                                    filterdList,
                                    isPurchased = pack_tag.isPurchased,
                                    list_layout = RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()
                                )
                            )
                        }


                    }


                    // getPriceOfPosterPacks()
                    callbacks?.onDataLoaded(dataList!!)
                    // rearrangeList()
                    adapter = AppBaseRecyclerViewAdapter(baseActivity, dataList!!, this)
                    binding?.rvTemplates?.adapter = adapter
                    binding?.rvTemplates?.layoutManager = LinearLayoutManager(requireActivity())
                    stopShimmer()
                }
            }
    }

   /* private fun getPriceOfPosterPacks() {
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





            }
        })
    }*/

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
            RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal->{
                posterWhatsappShareClicked(childItem as PosterModel,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal->{
                callFavApi(childItem as PosterModel,childPosition)
            }
            RecyclerViewActionType.POSTER_VIEW_MORE_CLICKED.ordinal->{
                parentItem as PosterPackModel

                addFragment(R.id.container,
                    BrowseAllFragment.newInstance(dataList!!,parentPosition),
                    true,true)

            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                Log.i(TAG, "onItemClick: ")
                posterPostClicked(childItem as PosterModel, requireActivity() as BaseActivity<*, *>)
            }
        }
    }

    private fun callFavApi(posterModel: PosterModel,position: Int) {
        viewModel?.favPoster(session?.fPID,session?.fpTag,posterModel.id)?.observe(viewLifecycleOwner){
            if (it.isSuccess()){
                posterModel.details?.Favourite= posterModel.details?.Favourite?.not() == true
                adapter?.notifyItemChanged(position)
            }
        }

    }
}