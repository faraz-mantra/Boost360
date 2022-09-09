package com.boost.marketplace.ui.popup.removeItems

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.boost.cart.CartActivity
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.remove_package_bottomsheet.*
import java.text.NumberFormat
import java.util.*


class RemovePackageBottomSheet(val listener: DetailsFragmentListener) : DialogFragment() {
    lateinit var root: View
    lateinit var prefs: SharedPrefs

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
        root = inflater.inflate(R.layout.remove_package_bottomsheet, container, false)
        prefs = SharedPrefs(activity as FeatureDetailsActivity)
        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        spannableString()
        close_btn.setOnClickListener {
            dismiss()
        }
        cancel_button.setOnClickListener {
            dismiss()
        }
        goToCart_button.setOnClickListener {
            listener.goToCart()
            dismiss()
        }
    }

    fun spannableString() {
        val addonName = arguments!!.getString("addonName")
        val stringValue = SpannableString(
            "You are trying to add “$addonName“ but your cart contains a pack that includes this feature. Please go to cart and remove the pack if you want to add this feature."
        )

        stringValue.setSpan(
            StyleSpan(Typeface.BOLD),
            23,
            23 + addonName!!.length,
            0
        )
        desc.text = stringValue
    }

}