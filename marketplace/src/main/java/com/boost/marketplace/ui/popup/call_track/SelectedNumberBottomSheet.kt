package com.boost.marketplace.ui.popup.call_track

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.boost.marketplace.R
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import kotlinx.android.synthetic.main.layout_selected_number_bottomsheet.*
import kotlinx.android.synthetic.main.layout_speak_expert_bottomsheet.back_btn


class SelectedNumberBottomSheet : DialogFragment() {
    lateinit var root: View

    companion object {
        fun newInstance() = SelectedNumberBottomSheet()
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
        root = inflater.inflate(R.layout.layout_selected_number_bottomsheet, container, false)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_title_call_track.text = requireArguments().getString("phone_number")
        back_btn.setOnClickListener {
            dismiss()
        }
        tv_add_to_cart.setOnClickListener {
            dismiss()
        }
        val select_another_no: String =
            "We have selected the above virtual phone number for you. You can also select another number."
        val ss =
            SpannableString("We have selected the above virtual phone number for you. You can also select another number.")
        val string = SpannableString("select another number.")

        ss.setSpan(
            UnderlineSpan(),
            select_another_no.indexOf(string.toString()),
            select_another_no.length,
            0
        )
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
            select_another_no.indexOf(string.toString()),
            select_another_no.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tv_select_number.text = ss
        tv_select_number.setOnClickListener {
            val list = requireArguments().getStringArrayList("numberList")
            val intent = Intent(requireContext(), CallTrackingActivity::class.java)
            intent.putExtra("list", list)
            startActivity(intent)
        }

    }


}