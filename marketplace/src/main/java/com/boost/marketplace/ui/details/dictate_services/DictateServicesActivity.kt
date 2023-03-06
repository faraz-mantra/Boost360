package com.boost.marketplace.ui.details.dictate_services

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.boost.cart.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityDictateServicesBinding
import com.boost.marketplace.ui.details.domain.CustomDomainViewModel
import com.boost.marketplace.ui.popup.dictate.SuccessPreferencesBottomSheet
import com.boost.marketplace.ui.popup.myplan.MyPlanBottomSheet
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.layout_acceptance.*
import kotlinx.android.synthetic.main.layout_details_popup.view.*
import kotlinx.android.synthetic.main.layout_target_customers.*
import kotlinx.android.synthetic.main.layout_target_customers.btn1
import kotlinx.android.synthetic.main.layout_target_customers.img1
import kotlinx.android.synthetic.main.layout_target_customers.img2
import kotlinx.android.synthetic.main.layout_word_count.*

class DictateServicesActivity :
    AppBaseActivity<ActivityDictateServicesBinding, CustomDomainViewModel>() {

    override fun getLayout(): Int {
        return com.boost.marketplace.R.layout.activity_dictate_services
    }

    override fun getViewModelClass(): Class<CustomDomainViewModel> {
        return CustomDomainViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            WindowInsetsControllerCompat(window, window.decorView).setAppearanceLightStatusBars(false)
            window.statusBarColor = ResourcesCompat.getColor(resources, com.boost.cart.R.color.common_text_color, null)
        }

        binding?.addonsBack?.setOnClickListener{
            super.onBackPressed()
        }

        binding?.layoutCustomers?.setOnClickListener {
            dialogCustomers()
        }

        binding?.layoutCount?.setOnClickListener {
            dialogCount()
        }

        binding?.layoutHashtag?.setOnClickListener {
            dialogAcceptance()
        }

        binding?.layoutAddress?.setOnClickListener {
            dialogAcceptance()
        }

        binding?.layoutLogo?.setOnClickListener {
            dialogAcceptance()
        }

        binding?.mainBtn1?.setOnClickListener {
            val dialogCard = SuccessPreferencesBottomSheet()
            val args = Bundle()
            dialogCard.arguments = args
            dialogCard.show(
                this.supportFragmentManager,
                SuccessPreferencesBottomSheet::class.java.name
            )
        }

    }

    fun dialogCustomers(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_target_customers)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.radio_button1?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }

        dialog.radio_button2?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }

        dialog.radio_button3?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }

        dialog.radio_button4?.setOnClickListener {
            dialog.hide_layut.visibility=View.VISIBLE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            doneButton(dialog)
        }

    }

    fun dialogCount(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_word_count)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
        dialog.radio_button5.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }
        dialog.radio_button6.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }
    }

    private fun dialogAcceptance() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_acceptance)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.radio_button7.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }
        dialog.radio_button8.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            doneButton(dialog)
        }
    }

    private fun doneButton(dialog: Dialog) {
        dialog.btn1?.setOnClickListener {
            dialog.btn1.background = ContextCompat.getDrawable(
                applicationContext,
                com.boost.marketplace.R.drawable.grey_button_click_effect)
            dialog.btn1.setTextColor(getResources().getColor(com.boost.marketplace.R.color.tv_color_BB))
            dialog.btn1.text = "Done"
            Handler().postDelayed({
                dialog.dismiss()
            }, 600)
        }
    }

}