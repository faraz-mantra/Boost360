package com.festive.poster.ui.festivePoster

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
import com.framework.models.BaseViewModel
import com.framework.utils.convertStringToObj
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
        val jsonList = convertStringToObj<ArrayList<PosterPackModel>>(arguments?.getString(
            BK_PACK_LIST_JSON
        )?:"")
        convertToPurchasedList(jsonList)
        setupList()
    }

    private fun convertToPurchasedList(jsonList: ArrayList<PosterPackModel>?) {

        dataList = ArrayList()
        jsonList?.forEachIndexed { index, posterPackModel ->
            var iconPos= index
            if (iconPos>8){
                iconPos=0
            }
            posterPackModel.tagsModel.drawableIcon=getIcon(iconPos)
            dataList?.add(PurchasedPosterPackModel(posterPackModel.tagsModel,posterPackModel.posterList,posterPackModel.price))
        }
    }

    fun getIcon(position: Int): Int {
        when(position){
            0->{
                return R.drawable.frame_0
            }
            1->{
                return R.drawable.frame_1
            }
            2->{
                return R.drawable.frame_2
            }
            3->{
                return R.drawable.frame_3
            }
            4->{
                return R.drawable.frame_4
            }
            5->{
                return R.drawable.frame_5
            }
            6->{
                return R.drawable.frame_6
            }
            7->{
                return R.drawable.frame_7
            }
            8->{
                return R.drawable.frame_8
            }

        }
        return R.drawable.frame_0
    }

    private fun setupList() {
        dataList?.let {
            binding?.emptyLayout?.isVisible = dataList.isNullOrEmpty()
            val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,it,this)
            binding?.rvPosters?.adapter = adapter
            binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
        }

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.POSTER_PACK_PURCHASED_CLICK.ordinal->{
                sharedViewModel?.selectedPosterPack = item as PosterPackModel
                sharedViewModel?.keyValueSaved?.value=null
                addFragment(R.id.container,
                    PosterListFragment.newInstance(item.tagsModel.tag),true,true)
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