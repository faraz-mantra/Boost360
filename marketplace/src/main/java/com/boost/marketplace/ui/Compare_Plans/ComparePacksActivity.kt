package com.boost.marketplace.ui.Compare_Plans

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding
import com.boost.marketplace.infra.api.models.test.ViewpagerData
import com.boost.marketplace.infra.api.models.test.getDatas
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.google.android.material.snackbar.Snackbar

class ComparePacksActivity: AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>(),
    RecyclerItemClickListener {

    private var adaptercomparepacks: AppBaseRecyclerViewAdapter<ViewpagerData>? = null
    private var adapterPager: AppBaseRecyclerViewAdapter<Bundles>? = null

    override fun getLayout(): Int {
        return R.layout.activity_compare_packs
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }
    override fun onCreateView() {
        super.onCreateView()
       viewpagerdata()

    }

    private fun viewpagerdata() {
        binding?.packageViewpager?.apply {
         //   adaptercomparepacks.map { it1 -> it1.recyclerViewItemType = RecyclerViewItemType.PACKS}
            if (adaptercomparepacks == null) {
                adaptercomparepacks = AppBaseRecyclerViewAdapter(
                    this@ComparePacksActivity,
                    getDatas(RecyclerViewItemType.PACKS_BUNDLES.ordinal), this@ComparePacksActivity)
                offscreenPageLimit = 3
                adapter = adaptercomparepacks
                binding?.packageIndicator2?.setViewPager2(this)
                setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position)
                }
                val itemDecoration = HorizontalMarginItemDecoration(applicationContext, R.dimen.viewpager_current_item_horizontal_margin)
                binding?.packageViewpager!!.addItemDecoration(itemDecoration)
            }
        }
    }



    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {


    }

}





