package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentBrowseAllBinding
import com.festive.poster.databinding.FragmentFavouriteListBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.pref.UserSessionManager
import com.google.gson.Gson

class FavouriteListFragment: AppBaseFragment<FragmentFavouriteListBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {
    private var session: UserSessionManager?=null
    private var selectedPos: Int=0
    private var posterRvAdapter: AppBaseRecyclerViewAdapter<PosterModel>?=null
    private var categoryAdapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    var categoryList:ArrayList<PosterPackModel>?=null

    override fun getLayout(): Int {
        return R.layout.fragment_favourite_list
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        TODO("Not yet implemented")
    }


}