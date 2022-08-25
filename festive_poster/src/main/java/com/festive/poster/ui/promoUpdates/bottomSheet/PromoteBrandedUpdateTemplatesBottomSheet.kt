package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.facebook.appevents.suggestedevents.ViewOnClickListener
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPromoteUsingBrandedUpdateTemplatesBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class PromoteBrandedUpdateTemplatesBottomSheet : BaseBottomSheetDialog<BsheetPromoteUsingBrandedUpdateTemplatesBinding, BaseViewModel>() {

    private var color888888: Int = 0
    private var color4a4a4a: Int = 0

    companion object {

        @JvmStatic
        fun newInstance(): PromoteBrandedUpdateTemplatesBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = PromoteBrandedUpdateTemplatesBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_promote_using_branded_update_templates
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        color888888 = ContextCompat.getColor(baseActivity, R.color.color_888888)
        color4a4a4a = ContextCompat.getColor(baseActivity, R.color.black_4a4a4a)
        setOnClickListener(binding?.linearAdvancedWrapper, binding?.linearClassicWrapper, binding?.linearBuyThisWrapper)
        setRadioButtonListeners()
        binding?.rivCloseBottomSheet?.setOnClickListener { dismiss() }
    }

    private fun setRadioButtonListeners() {
        binding?.radioAdvanced?.setOnClickListener {

        }
        binding?.radioClassic?.setOnClickListener {

        }
        binding?.radioBuyThisOnly?.setOnClickListener {

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.linearAdvancedWrapper -> {
                binding?.radioAdvanced?.isChecked = true
                binding?.radioClassic?.isChecked = false
                binding?.radioBuyThisOnly?.isChecked = false
                binding?.tvAdvanceTitle?.setTextColor(color4a4a4a)
                binding?.tvClassicTitle?.setTextColor(color888888)
                binding?.tvBuyOnlyTitle?.setTextColor(color888888)
                binding?.btnViewPackDetails?.text = getString(R.string.view_pack_details)
            }
            binding?.linearClassicWrapper -> {
                binding?.radioAdvanced?.isChecked = false
                binding?.radioClassic?.isChecked = true
                binding?.radioBuyThisOnly?.isChecked = false
                binding?.tvAdvanceTitle?.setTextColor(color888888)
                binding?.tvClassicTitle?.setTextColor(color4a4a4a)
                binding?.tvBuyOnlyTitle?.setTextColor(color888888)
                binding?.btnViewPackDetails?.text = getString(R.string.view_pack_details)
            }
            binding?.linearBuyThisWrapper -> {
                binding?.radioAdvanced?.isChecked = false
                binding?.radioClassic?.isChecked = false
                binding?.radioBuyThisOnly?.isChecked = true
                binding?.tvAdvanceTitle?.setTextColor(color888888)
                binding?.tvClassicTitle?.setTextColor(color888888)
                binding?.tvBuyOnlyTitle?.setTextColor(color4a4a4a)
                binding?.btnViewPackDetails?.text = getString(R.string.add_and_go_to_cart)
            }
        }
    }
}