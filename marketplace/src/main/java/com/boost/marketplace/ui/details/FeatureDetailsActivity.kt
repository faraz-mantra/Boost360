package com.boost.marketplace.ui.details

import android.view.View
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityFeatureDetailsBinding
import com.boost.marketplace.infra.api.models.test.TestData
import com.boost.marketplace.infra.api.models.test.getData
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter

class FeatureDetailsActivity :
    AppBaseActivity<ActivityFeatureDetailsBinding, FeatureDetailsViewModel>(),
    RecyclerItemClickListener {

    private var featureDetailsAdapter: AppBaseRecyclerViewAdapter<TestData>? = null

    override fun getLayout(): Int {
        return R.layout.activity_feature_details
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.addItemToCart)
        initializeFeatureDetailsAdapter()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.addItemToCart -> {
                val dialog = FeaturesDetailsDialog()
                dialog.show(this.supportFragmentManager, "FeatureDialog")
            }
        }
    }


    fun initializeFeatureDetailsAdapter() {
        binding?.packRecycler?.apply {
            featureDetailsAdapter =
                AppBaseRecyclerViewAdapter(this@FeatureDetailsActivity, getData(RecyclerViewItemType.FEATURE_DETAILS.ordinal), this@FeatureDetailsActivity)
            adapter = featureDetailsAdapter
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }

}