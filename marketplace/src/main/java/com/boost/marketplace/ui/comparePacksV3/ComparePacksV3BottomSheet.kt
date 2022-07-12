package com.boost.marketplace.ui.comparePacksV3

import com.boost.marketplace.R
import com.boost.marketplace.databinding.Comparepacksv3PopupBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ComparePacksV3BottomSheet: BaseBottomSheetDialog<Comparepacksv3PopupBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.comparepacksv3_popup
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }
    }


}