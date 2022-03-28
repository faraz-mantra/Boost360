package com.boost.marketplace.ui.details.call_track

import com.boost.marketplace.R
import com.boost.marketplace.databinding.LayoutSpeakExpertBottomsheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CallTrackingHelpBottomSheet : BaseBottomSheetDialog<LayoutSpeakExpertBottomsheetBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.layout_speak_expert_bottomsheet
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }

}