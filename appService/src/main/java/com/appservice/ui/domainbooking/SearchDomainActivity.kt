package com.appservice.ui.domainbooking

import androidx.databinding.DataBindingUtil
import com.appservice.R
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchDomainActivity: BaseActivity<ActivitySearchDomainBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_search_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnContinue?.setOnClickListener {
            val bSheet = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
            val sheetBinding = DataBindingUtil.inflate<BsheetConfirmDomainBinding>(layoutInflater,R.layout.bsheet_confirm_domain,null,false)
            bSheet.setContentView(sheetBinding.root)
            bSheet.show()

        }
    }
}