package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.*
import com.festive.poster.models.response.TemplateSaveActionBody
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.CategoryDiffUtil
import com.festive.poster.ui.TemplateDiffUtil
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
import com.framework.rest.NetworkResult
import com.framework.utils.convertJsonToObj
import com.framework.utils.convertListObjToString
import com.framework.utils.showToast
import com.framework.utils.toArrayList
import com.framework.webengageconstant.Promotional_Update_View_More_Click
import com.google.gson.Gson

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<TodaysPickCategory>?=null
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

        adapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(), this)
        binding.rvTemplates.adapter = adapter
        binding.rvTemplates.layoutManager = LinearLayoutManager(requireActivity())

    }

    private fun fetchDataFromServer() {

        promoUpdatesViewModel?.todayPickData?.observe(viewLifecycleOwner){


            when(it){
                is NetworkResult.Loading->{
                    if (adapter?.isEmpty() == true)
                        startShimmer()
                }
                is NetworkResult.Success->{
                    stopShimmer()
                    val data = it.data?:return@observe
                    val uiList = data.asTodaysPickModels().toArrayList()
                    addViewMoreInEachList(uiList)
                    adapter?.setUpUsingDiffUtil(
                        uiList
                    )
                }
                is NetworkResult.Error->{
                    stopShimmer()
                    showToast(it.msg)
                }
            }


           /*     list.forEach {item->
                    if ((item.posterList?.size ?: 0) >= 4){
                        item.posterList?.add(PosterModel(layout_id = RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()))
                    }
                }*/






        }
    }

    private fun addViewMoreInEachList(uiList: java.util.ArrayList<TodaysPickCategory>) {
        val itemsToShow = 4
        uiList.forEach {cat->
            if (itemsToShow < (cat._templates?.size ?: 0)){
                val templates:List<TodayPickTemplate>? =cat._templates?.take(itemsToShow)?.toArrayList()?.apply {
                    add(
                        ViewMoreTodayPickTemplate((cat.getParentTemplates()?.size ?: 0 ) - itemsToShow)
                    )
                }
                cat._templates = templates
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
                posterWhatsappShareClicked(childItem as TemplateUi,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal->{
                callFavApi(childItem as TemplateUi)
            }
            RecyclerViewActionType.POSTER_VIEW_MORE_CLICKED.ordinal->{
                parentItem as CategoryUi

                    addFragment(R.id.container,
                        BrowseAllFragment.newInstance(parentItem.id),
                        true,true)

            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                Log.i(TAG, "onItemClick: ")
                posterPostClicked(childItem as TemplateUi, requireActivity() as AppBaseActivity<*, *>)
            }
        }
    }

    private fun callFavApi(posterModel: TemplateUi) {
        promoUpdatesViewModel?.markAsFav(posterModel.isFavourite.not(),posterModel.id)
        promoUpdatesViewModel?.favStatus?.observe(viewLifecycleOwner){

            when(it){
                is NetworkResult.Loading->{
                    showProgress()
                }
                else->{
                    hideProgress()
                }
            }
        }

    }

    fun AppBaseRecyclerViewAdapter<TodaysPickCategory>.setUpUsingDiffUtil(
        newList: ArrayList<TodaysPickCategory>
    ){
        val catDiffUtil = CategoryDiffUtil(
            this.list,
            newList
        )

        val diffResult = DiffUtil.calculateDiff(catDiffUtil)

        this.list.clear()
        this.list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}