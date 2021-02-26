package com.appservice.appointment

import com.appservice.R
import com.appservice.databinding.BottomSheetCatalogDisplayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetCatalogDisplayName: BaseBottomSheetDialog<BottomSheetCatalogDisplayBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_catalog_display
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}