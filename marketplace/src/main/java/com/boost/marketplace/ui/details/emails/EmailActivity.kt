package com.boost.marketplace.ui.details.emails

import android.view.View
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityEmailBinding
import com.boost.marketplace.ui.popup.emailselection.EmailConfirmatinBottomSheet
import com.boost.marketplace.ui.popup.emailselection.EmailHelpBottomSheet
import com.boost.marketplace.ui.popup.emailselection.ZohoBottomSheet

class EmailActivity : AppBaseActivity<ActivityEmailBinding, EmailViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_email
    }

    override fun getViewModelClass(): Class<EmailViewModel> {
        return EmailViewModel::class.java
    }
    override fun onCreateView() {
        super.onCreateView()
        binding?.create?.setOnClickListener {

            binding?.create?.visibility=View.GONE
            binding?.editlayout?.visibility=View.VISIBLE

        }
        binding?.zohodesc?.setOnClickListener {
            val dialogCard = ZohoBottomSheet()
            dialogCard.show(this.supportFragmentManager, ZohoBottomSheet::class.java.name)
        }

        binding?.cartContinueButton?.setOnClickListener {
            val dialogCard = EmailConfirmatinBottomSheet()
            dialogCard.show(this.supportFragmentManager, EmailConfirmatinBottomSheet::class.java.name)
        }

        binding?.help1?.setOnClickListener {
            val dialogCard = EmailHelpBottomSheet()
            dialogCard.show(this.supportFragmentManager, EmailHelpBottomSheet::class.java.name)
        }

        binding?.backButton?.setOnClickListener {
            finish()
        }
    }
}