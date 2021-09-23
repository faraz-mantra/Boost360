package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.appservice.R
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainSearchBinding
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
            val sheetBinding = DataBindingUtil.inflate<BsheetConfirmDomainSearchBinding>(layoutInflater,R.layout.bsheet_confirm_domain_search,null,false)
            bSheet.setContentView(sheetBinding.root)
            sheetBinding.btnConfirm.setOnClickListener{
                startFragmentDomainBookingActivity(
                    activity = this,
                    type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                    bundle = Bundle(),
                    clearTop = false
                )
            }
            bSheet.show()

        }

        binding?.include?.customImageView4?.setOnClickListener{
            this.onNavPressed()
        }
    }
}