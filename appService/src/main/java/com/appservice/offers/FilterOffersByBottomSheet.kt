package com.appservice.offers

import android.view.View
import android.widget.RadioButton
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.databinding.BottomSheetSortOffersListingBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class FilterOffersByBottomSheet : BaseBottomSheetDialog<BottomSheetSortOffersListingBinding, BaseViewModel>() {

    var onClicked: (value:  Int) -> Unit = { }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_sort_offers_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        private const val LATEST_ON_TOP = 0
        private const val OLDEST_ON_TOP = 1
        private const val ACTIVE_FIRST = 0
        private const val DE_ACTIVE_FIRST = 1
        val filters: Map<String, Int> = mapOf(
                Pair(AppServiceApplication.instance.getString(R.string.latest_on_top), LATEST_ON_TOP),
                Pair(AppServiceApplication.instance.getString(R.string.oldest_on_top), OLDEST_ON_TOP),
                Pair(AppServiceApplication.instance.getString(R.string.active_first), ACTIVE_FIRST),
                Pair(AppServiceApplication.instance.getString(R.string.deactive_first), DE_ACTIVE_FIRST)
        )
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.btnApply, binding?.radioGroupSorting, binding?.ctvReset)
        markDefaultSelection()
    }

    private fun markDefaultSelection() {
        binding?.rbLatestOnTop?.isChecked = true

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnApply -> {
                if (binding?.radioGroupSorting != null) {
                    val radioButton: RadioButton = binding?.radioGroupSorting!!.findViewById(binding?.radioGroupSorting!!.checkedRadioButtonId)
                    onClicked(filters.getValue(radioButton.text.toString()))
                }
                dismiss()
            }
            binding?.btnCancel -> {
                dismiss()
            }
            binding?.ctvReset -> {
                binding?.radioGroupSorting?.clearCheck()
                markDefaultSelection()
            }
        }
    }
}