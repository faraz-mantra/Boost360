package com.festive.poster.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterPackPurchasedBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PurchasedPosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.convertStringToList
import com.framework.utils.convertStringToObj
import com.framework.utils.toArrayList
import com.google.gson.Gson

class PosterPackPurchasedListingFragment: AppBaseFragment<FragmentPosterPackPurchasedBinding, BaseViewModel>(),RecyclerItemClickListener {

    var dataList:ArrayList<PurchasedPosterPackModel>?=null
    private var sharedViewModel: FestivePosterSharedViewModel? = null

    companion object {
        val BK_PACK_LIST_JSON="BK_PACK_LIST_JSON"
        @JvmStatic
        fun newInstance(packList:ArrayList<PosterPackModel>?): PosterPackPurchasedListingFragment {
            val bundle = Bundle().apply {
                putString(BK_PACK_LIST_JSON, Gson().toJson(packList))
            }
            val fragment = PosterPackPurchasedListingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_purchased
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)

        setOnClickListener(binding?.buyTemplates)
        val jsonList = convertStringToObj<ArrayList<PosterPackModel>>(arguments?.getString(BK_PACK_LIST_JSON)?:"")
        convertToPurchasedList(jsonList)
        setupList()
    }

    private fun convertToPurchasedList(jsonList: ArrayList<PosterPackModel>?) {

        dataList = ArrayList()
        jsonList?.forEach {
            dataList?.add(PurchasedPosterPackModel(it.tagsModel,it.posterList,it.price))
        }
    }

    private fun setupList() {
        binding?.emptyLayout?.isVisible = dataList.isNullOrEmpty()
        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList!!,this)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.POSTER_PACK_PURCHASED_CLICK.ordinal->{
                sharedViewModel?.selectedPosterPack = item as PosterPackModel
                sharedViewModel?.keyValueSaved?.value=null
                addFragmentReplace(R.id.container,PosterListFragment.newInstance(item.tagsModel.tag),true)
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.buyTemplates->{
                requireActivity().onBackPressed()
            }
        }
    }

}