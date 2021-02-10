package com.appservice.ui.catalog.listing

import com.appservice.R
import com.appservice.databinding.BottomSheetSortServiceListingBinding
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class SortAndFilterBottomSheet : BaseBottomSheetDialog<BottomSheetSortServiceListingBinding, BaseViewModel>(), RecyclerItemClickListener {

    var onClicked: (value: String) -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_sort_service_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }

}