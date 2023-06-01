package com.boost.marketplace.ui.popup

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.boost.marketplace.R
import kotlinx.android.synthetic.main.find_number_bottomsheet.*
import kotlinx.android.synthetic.main.find_number_bottomsheet.lotty_progress
import kotlinx.android.synthetic.main.find_number_bottomsheet.upi_popup_outer_layout
import kotlinx.android.synthetic.main.layout_speak_expert_bottomsheet.back_btn
import kotlinx.android.synthetic.main.lottie_loader_bottomsheet.*

class ComparePacksLottieLoaderBottomSheet : DialogFragment() {
    lateinit var root: View

    val handler = Handler()
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
        root = inflater.inflate(R.layout.lottie_loader_bottomsheet, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lotty_progress.pauseAnimation()
        lotty_progress.playAnimation()
        handler.postDelayed({
            dismiss()
            }, 5000)

        upi_popup_outer_layout1.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        System.gc()
        super.onDestroy()
    }

}