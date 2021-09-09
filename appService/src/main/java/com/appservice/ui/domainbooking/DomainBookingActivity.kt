package com.appservice.ui.domainbooking

import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.databinding.ActivityDomainBookingBinding
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainBinding
import com.appservice.databinding.BsheetInputOwnDomainBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainStepsModel
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.google.android.material.bottomsheet.BottomSheetDialog

class DomainBookingActivity: BaseActivity<ActivityDomainBookingBinding, BaseViewModel>(),RecyclerItemClickListener {

    var session:UserSessionManager?=null

    override fun getLayout(): Int {
        return R.layout.activity_domain_booking
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        session = UserSessionManager(this)
        binding?.tvDomain?.text = session?.getDomainName(true)
        setupUI()
        onClickListeners()
    }

    private fun onClickListeners() {
        binding?.btnBuyAddon?.setOnClickListener {

        }

        binding?.btnBookOldDomain?.setOnClickListener {
            val bSheet = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
            val sheetBinding = DataBindingUtil.inflate<BsheetInputOwnDomainBinding>(layoutInflater,R.layout.bsheet_input_own_domain,null,false)
            bSheet.setContentView(sheetBinding.root)
            bSheet.show()
        }
    }

    private fun setupUI() {
        if (isPremium()){
            premiumMode()
        }else{
            nonPremiumMode()
        }
    }

    private fun nonPremiumMode() {
        binding?.layoutBuyAddon?.visible()
        binding?.ivRiaSteps?.gone()
        binding?.btnBookNewDomain?.gone()
        binding?.btnBookOldDomain?.gone()
        binding?.rvSteps?.gone()
    }

    private fun premiumMode() {
        setupSteps()
        binding?.layoutBuyAddon?.gone()
        binding?.ivRiaSteps?.visible()
        binding?.btnBookNewDomain?.visible()
        binding?.btnBookOldDomain?.visible()
        binding?.rvSteps?.visible()

    }


    private fun isPremium(): Boolean {
       return session?.getStoreWidgets()?.contains("DOMAINPURCHASE")?:false
    }

    private fun setupSteps() {
        val secondStep = "In case you have a different website (for same business) connected to the domain that you own, you can integrate this website also as a sub-domain on that domain. (what’s subdomain?)"
        val whatsSubdomain ="(what’s subdomain?)"
        val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

        val stepsList = arrayListOf(
            DomainStepsModel(SpannableString("Since search engines recognize a domain that’s already in use, we recommend integrating any relatable domain name that you currently own")),
            DomainStepsModel(SpannableString(secondStep)
                .apply {
                    setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {

                        }

                    },whatsSubdomainIndex,whatsSubdomainIndex+whatsSubdomain.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }),
            DomainStepsModel(SpannableString("If you don’t have any other domain, click on ‘book a new domain’ and choose a domain you like for your business website."))
            )

        val adapter = AppBaseRecyclerViewAdapter(this,stepsList,this)
        binding?.rvSteps?.adapter= adapter
        binding?.rvSteps?.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }

   /* private fun initiateBuyFromMarketplace() {

        val progressDialog = ProgressDialog(this)
        val status = getString(R.string.please_wait)
        progressDialog.setMessage(status)
        progressDialog.setCancelable(false)
        progressDialog.show()
        val intent = Intent(this, UpgradeActivity::class.java)
        intent.putExtra("expCode", session.getFP_AppExperienceCode())
        intent.putExtra("fpName", session.getFPName())
        intent.putExtra("fpid", session.getFPID())
        intent.putExtra("fpTag", session.getFpTag())
        intent.putExtra("accountType", session!!.getFPDetails(GET_FP_DETAILS_CATEGORY))
        intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets)
        if (session.getUserProfileEmail() != null) {
            intent.putExtra("email", session.getUserProfileEmail())
        } else {
            intent.putExtra("email", "ria@nowfloats.com")
        }
        if (session.getUserPrimaryMobile() != null) {
            intent.putExtra("mobileNo", session.getUserPrimaryMobile())
        } else {
            intent.putExtra("mobileNo", "9160004303")
        }
        intent.putExtra("profileUrl", session.getFPLogo())
        intent.putExtra("buyItemKey", "DOMAINPURCHASE")
        startActivity(intent)
        Handler().postDelayed({ progressDialog.dismiss() }, 1000)
    }*/
}