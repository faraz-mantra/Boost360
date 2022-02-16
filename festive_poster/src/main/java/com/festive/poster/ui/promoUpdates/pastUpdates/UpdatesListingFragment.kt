package com.festive.poster.ui.promoUpdates.pastUpdates

import android.os.Bundle
import android.util.Log
import com.boost.dbcenterapi.utils.observeOnce
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentUpdatesListingBinding
import com.festive.poster.models.promoModele.PastUpdateModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.pref.clientId
import com.framework.pref.clientId3

class UpdatesListingFragment : AppBaseFragment<FragmentUpdatesListingBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): UpdatesListingFragment {
            val fragment = UpdatesListingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_updates_listing
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
        apiCallPastUpdates()
    }

    private fun apiCallPastUpdates() {
        viewModel?.getPastUpdatesList(clientId = clientId, fpId = sessionLocal.fPID)
            ?.observeOnce( viewLifecycleOwner, {it ->
            Log.i("pastUpdates", "PastUpdates: $it")
        })
    }

    private fun initUI() {
        val pastPostData = PastUpdateModel().getData(baseActivity)
        binding?.rvPostListing?.adapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostData, this)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}