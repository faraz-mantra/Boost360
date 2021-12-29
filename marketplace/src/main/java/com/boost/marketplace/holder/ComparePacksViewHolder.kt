package com.boost.marketplace.holder

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.databinding.ItemComparePacksBinding
import com.boost.marketplace.infra.api.models.test.Packs_Data
import com.boost.marketplace.infra.api.models.test.ViewpagerData
import com.boost.marketplace.infra.api.models.test.getDatas
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter

class ComparePacksViewHolder(binding: ItemComparePacksBinding)  :
    AppBaseRecyclerViewHolder<ItemComparePacksBinding>(binding), RecyclerItemClickListener {

    private var adapterPacks: AppBaseRecyclerViewAdapter<ViewpagerData>? = null
    // var list: ArrayList<Packs_Data>? = null


    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
    ) {
        super.bind(position, item)
    //    val data = item as? Bundles ?: return
      //  binding.packageTitle.text = data.type?.title

        if (adapterPacks == null) {
            binding.childRecyclerview.apply {
                adapterPacks = activity?.let {
                    AppBaseRecyclerViewAdapter(
                        it,
                        getDatas(RecyclerViewItemType.TOP_FEATURES.ordinal),
                        this@ComparePacksViewHolder
                    )

                }
                adapter = adapterPacks
            }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        listener?.onItemClick(position, item, actionType)
    }
}



