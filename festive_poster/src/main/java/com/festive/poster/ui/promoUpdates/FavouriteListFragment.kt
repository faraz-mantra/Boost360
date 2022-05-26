package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentFavouriteListBinding
import com.festive.poster.models.FavouriteTemplatesDetail
import com.festive.poster.models.GetFavTemplateResponse
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.posterPostClicked
import com.festive.poster.utils.posterWhatsappShareClicked
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.Promotional_Update_Category_Click

class FavouriteListFragment: AppBaseFragment<FragmentFavouriteListBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {


    private var session: UserSessionManager?=null
    private var selectedPos: Int=0
    private var posterRvAdapter: AppBaseRecyclerViewAdapter<PosterModel>?=null
    private var categoryAdapter: AppBaseRecyclerViewAdapter<FavouriteTemplatesDetail>?=null
    var categoryList=ArrayList<FavouriteTemplatesDetail>()

    override fun getLayout(): Int {
        return R.layout.fragment_favourite_list
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }



    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        binding.rvCat.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
        getFavTemp()
    }

    private fun getFavTemp() {
        showProgress()
        viewModel?.getFavTemplates(session?.fPID,session?.fpTag,Constants.PROMO_WIDGET_KEY)
            ?.observe(viewLifecycleOwner) {
                hideProgress()
                if (it.isSuccess()){
                    it as GetFavTemplateResponse

                    it.Result?.FavouriteTemplatesDetails?.let { list ->
                        categoryList.addAll(list)
                        applyAttributesToTemplates()
                        groupTemplatesToAllTag()
                        if (categoryList.isEmpty()){
                            zerothCase(true)
                            return@let
                        }else{
                            zerothCase(false)
                        }
                        categoryAdapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList,this)
                        binding.rvCat.adapter =categoryAdapter
                        switchToSelectedItem()
                    }

                }

        }
    }

    private fun groupTemplatesToAllTag() {
        val allTemplates = ArrayList<PosterModel>()
        categoryList.forEach {
            if (it.tag!="All"&&it.templateDetails!=null){
                allTemplates.addAll(it.templateDetails!!)
            }
        }
        categoryList.find { it.tag=="All" }?.apply {
            templateDetails=allTemplates
            tagName="All"
        }

    }

    private fun applyAttributesToTemplates() {
        categoryList.forEach {
            it.templateDetails?.forEach { template->
                template.layout_id = RecyclerViewItemType.TEMPLATE_VIEW_FOR_FAV.getLayout()
            }

        }
    }

    private fun zerothCase(b: Boolean) {
        if (b){
            binding.layoutZeroth.isVisible=true
        }else{
            binding.layoutZeroth.isVisible=false
        }
    }

    companion object {

        val BK_POSTER_PACK_LIST="POSTER_PACK_LIST"
        val BK_SELECTED_POS="BK_SELECTED_POS"

        fun newInstance(): FavouriteListFragment {
            val bundle: Bundle = Bundle()
            val fragment = FavouriteListFragment()
            fragment.arguments = bundle
            return fragment
        }


    }




    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.FAV_CAT_CLICKED.ordinal->{
                WebEngageController.trackEvent(Promotional_Update_Category_Click)

                selectedPos=position
                switchToSelectedItem()
            }
            RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal->{
                posterWhatsappShareClicked(item as PosterModel,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                posterPostClicked(item as PosterModel, requireActivity() as BaseActivity<*, *>)
            }
        }

    }

    private fun switchToSelectedItem() {
        categoryList.forEach { it.isSelected =false }
        categoryList.get(selectedPos).isSelected=true
        categoryAdapter?.notifyDataSetChanged()
        categoryList.get(selectedPos).templateDetails?.let {
            val selectedItem = categoryList.get(selectedPos)
            selectedItem.isSelected =true
            binding.tvCatTitle.text = selectedItem.tag
            binding.tvCatSize.text = selectedItem.count.toString()
            posterRvAdapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
                it,this)
            binding.rvPosters.adapter = posterRvAdapter
            binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
        }

    }
}