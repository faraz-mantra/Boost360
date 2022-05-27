package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.*
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.*
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.convertJsonToObj
import com.framework.utils.convertListObjToString
import com.framework.webengageconstant.Promotional_Update_View_More_Click
import com.google.gson.Gson

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var session: UserSessionManager? = null
    var promoUpdatesViewModel:PromoUpdatesViewModel?=null
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

    interface Callbacks{
        fun onDataLoaded(data:ArrayList<PosterPackModel>)
    }

    override fun onCreateView() {
        session = UserSessionManager(requireActivity())
        promoUpdatesViewModel = ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
        fetchDataFromServer()
        setOnClickListener(binding.cardBrowseAllTemplate)


    }

    private fun fetchDataFromServer() {
        startShimmer()
        promoUpdatesViewModel?.todaysPickLData?.observe(viewLifecycleOwner){
            stopShimmer()
            it?.let {list->

                list.forEach {item->
                    if ((item.posterList?.size ?: 0) >= 4){
                        item.posterList?.add(PosterModel(layout_id = RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()))
                    }
                }
                adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
                binding.rvTemplates.adapter = adapter
                binding.rvTemplates.layoutManager = LinearLayoutManager(requireActivity())
            }

        }
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

                    addFragment(R.id.container,BrowseAllFragment.newInstance(),
                        true,true)


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
                        BrowseAllFragment.newInstance(parentItem.tagsModel?.tag),
                        true,true)

            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                Log.i(TAG, "onItemClick: ")
                posterPostClicked(childItem as PosterModel, requireActivity() as AppBaseActivity<*, *>)
            }
        }
    }

    private fun callFavApi(posterModel: PosterModel,position: Int) {
        viewModel?.makeTemplateFav(session?.fPID,session?.fpTag,posterModel.id)?.observe(viewLifecycleOwner){
            if (it.isSuccess()){
                posterModel.details?.Favourite= posterModel.details?.Favourite?.not() == true
                adapter?.notifyItemChanged(position)
            }
        }

    }
}