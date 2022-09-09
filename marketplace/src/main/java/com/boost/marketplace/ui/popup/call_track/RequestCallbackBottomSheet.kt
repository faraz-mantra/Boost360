package com.boost.marketplace.ui.popup.call_track

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.boost.cart.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.ui.pack_details.PackDetailsActivity
import kotlinx.android.synthetic.main.popup_request_callback_custom_domain.*


class RequestCallbackBottomSheet : DialogFragment() {
    lateinit var root: View
    lateinit var prefs: SharedPrefs


    companion object {
        fun newInstance() = RequestCallbackBottomSheet()
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
        root = inflater.inflate(R.layout.layout_request_callback_bottomsheet_call_track, container, false)
        prefs = SharedPrefs(activity as PackDetailsActivity)
        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        back_btn.setOnClickListener {
            dismiss()
        }
        tv_call_expert.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + prefs.getExpertContact())
            startActivity(Intent.createChooser(callIntent, "Call by:"))
            dismiss()
        }

    }

}