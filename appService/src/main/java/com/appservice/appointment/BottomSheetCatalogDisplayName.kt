package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetCatalogDisplayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class BottomSheetCatalogDisplayName: BaseBottomSheetDialog<BottomSheetCatalogDisplayBinding, BaseViewModel>() {
    var customText: String? = null
    var onDoneClicked: () -> Unit = {}
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_catalog_display
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.radioGroup?.check(R.id.radio_service)
        initRadioService()
        setOnClickListener(binding?.btnCancel, binding?.btnProceed)
        binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_service -> {
                    initRadioService()
                }
                R.id.radio_custom_text -> {
                    binding?.ctvCustomDisplayHint?.visible()
                    binding?.btnCancel?.visible()
                    binding?.btnProceed?.text = getString(R.string.proceed)

                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun initRadioService() {
        binding?.ctvCustomDisplayHint?.gone()
        binding?.btnCancel?.gone()
        binding?.btnProceed?.text = getString(R.string.close)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnCancel -> {
                dismiss()
            }
            binding?.btnProceed -> {
                if (binding?.radioGroup?.checkedRadioButtonId != R.id.radio_service) {
                    customText = binding?.ctvCustomDisplayHint?.text.toString()
                    showConfirmingChange()
                }
                dismiss()
            }
        }
    }

    private fun showConfirmingChange() {
        val bottomSheetCatalogDisplayName =BottomSheetConfirmingChange()
        bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetConfirmingChange::class.java.name)
    }
}