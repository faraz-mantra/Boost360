package com.boost.marketplace.ui.Invoice

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityInvoiceBinding
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanViewModel

class InvoiceActivity : AppBaseActivity<ActivityInvoiceBinding, InvoiceViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_invoice
    }

    override fun getViewModelClass(): Class<InvoiceViewModel> {
      return InvoiceViewModel::class.java
    }
}