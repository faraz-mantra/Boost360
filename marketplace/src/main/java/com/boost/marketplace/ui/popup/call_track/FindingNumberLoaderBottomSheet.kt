package com.boost.marketplace.ui.popup.call_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.boost.marketplace.R
import kotlinx.android.synthetic.main.find_number_bottomsheet.*
import kotlinx.android.synthetic.main.layout_speak_expert_bottomsheet.back_btn

class FindingNumberLoaderBottomSheet : DialogFragment() {
    lateinit var root: View


    companion object {
        fun newInstance() = CallTrackingHelpBottomSheet()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.find_number_bottomsheet, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lotty_progress.pauseAnimation()
        lotty_progress.playAnimation()
        back_btn.setOnClickListener {
            dismiss()
        }

    }
}