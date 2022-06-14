package com.festive.poster.ui.promoUpdates.pastUpdates

import android.os.Bundle
import android.util.Log
import com.boost.dbcenterapi.utils.observeOnce
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.databinding.FragmentUpdatesListingBinding
import com.festive.poster.models.FavouriteTemplatesDetail
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.PastPostItem
import com.festive.poster.models.promoModele.PastUpdatesNewListingResponse
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.pref.clientId

class UpdatesListingFragment : AppBaseFragment<FragmentUpdatesListingBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {

    var pastPostListing = ArrayList<PastPostItem>()

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
    }

    private fun initUI() {
        getTemplateViewConfig()
        apiCallPastUpdates()
        //val pastPostData = PastUpdateModel().getData(baseActivity)
    }

    private fun getTemplateViewConfig() {
        viewModel?.getTemplateConfig(Constants.PROMO_FEATURE_CODE, sessionLocal.fPID, sessionLocal.fpTag)
            ?.observeOnce(this) {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.allTemplates.tags)

                }

            }
    }

    private fun prepareTagForApi(tags: List<PosterPackTagModel>): ArrayList<String> {
        val list = ArrayList<String>()
        tags.forEach {
            list.add(it.tag)
        }
        return list
    }

    private fun apiCallPastUpdates() {
        viewModel?.getPastUpdatesList(clientId = clientId, fpId = sessionLocal.fPID, postType = 0)
            ?.observeOnce( viewLifecycleOwner, {it ->
                if (it.isSuccess()){
                    it as PastUpdatesNewListingResponse

                    it.floats?.let { it1 ->
                        pastPostListing.addAll(it1)
                        val adapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostListing, this)
                        binding.rvPostListing.adapter = adapter
                    }

                }

                Log.i("pastUpdates", "PastUpdates: $it")
            })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}