package com.boost.marketplace.ui.details.domain
import com.boost.marketplace.R
import com.boost.marketplace.adapter.CustomDomainListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCustomDomainBinding


class CustomDomainActivity : AppBaseActivity<ActivityCustomDomainBinding, CustomDomainViewModel>() {
    lateinit var customDomainListAdapter: CustomDomainListAdapter

    override fun getLayout(): Int {
        return R.layout.activity_custom_domain
    }

    override fun getViewModelClass(): Class<CustomDomainViewModel> {
        return CustomDomainViewModel::class.java
    }
    override fun onCreateView() {
        super.onCreateView()


        binding?.help?.setOnClickListener {
            val dialogCard = CustomDomainHelpBottomSheet()
            dialogCard.show(this.supportFragmentManager, CustomDomainHelpBottomSheet::class.java.name)
        }
        binding?.btnSelect?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.btnSelectDomain?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.tv3?.setOnClickListener {
            val dialogCard = SSLCertificateBottomSheet()
            dialogCard.show(this.supportFragmentManager, SSLCertificateBottomSheet::class.java.name)
        }

    }
}