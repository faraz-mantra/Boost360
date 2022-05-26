package com.boost.marketplace.ui.details.call_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.boost.marketplace.R
import kotlinx.android.synthetic.main.popup_request_callback_custom_domain.*


class RequestCallbackBottomSheet : DialogFragment() {
    lateinit var root: View

    companion object {
        fun newInstance() = RequestCallbackBottomSheet()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(com.boost.cart.R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.layout_request_callback_bottomsheet_call_track, container, false)

        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        back_btn.setOnClickListener {
            dismiss()
        }
        tv_call_expert.setOnClickListener {
            dismiss()
        }

    }

}