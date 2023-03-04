package com.boost.marketplace.ui.popup.dictate

import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomsheetDictateSuccessBinding
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanViewModel
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.framework.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.find_number_bottomsheet.*
import kotlinx.android.synthetic.main.find_number_bottomsheet.back_btn
import kotlinx.android.synthetic.main.layout_speak_expert_bottomsheet.*

class SuccessPreferencesBottomSheet: BaseBottomSheetDialog<BottomsheetDictateSuccessBinding, MyCurrentPlanViewModel>() {

    override fun getLayout(): Int {
        return  R.layout.bottomsheet_dictate_success
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }


    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreateView() {
        viewModel?.setApplicationLifecycle(Application(), this)
        viewModel = ViewModelProviders.of(this)
            .get(MyCurrentPlanViewModel::class.java)

        binding?.backBtn?.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        back_btn.setOnClickListener {
            dismiss()
        }

    }

}