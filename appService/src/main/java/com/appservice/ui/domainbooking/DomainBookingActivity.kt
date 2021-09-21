package com.appservice.ui.domainbooking

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.databinding.*
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

class DomainBookingActivity : BaseActivity<ActivityDomainBookingBinding, BaseViewModel>(),
    RecyclerItemClickListener {

    var session: UserSessionManager? = null
    private lateinit var baseActivity: BaseActivity<*, *>

    /**
     * Bottom Sheet : function "showBsheetIntegrationOption"
     * 0: sheetBinding.radioAsBusinessWebsite is selected
     * 1: sheetBinding.radioCreateASubdomain is selected
     * */
    var domainIntegrationUserSelection:Int = 0

    override fun getLayout(): Int {
        return R.layout.activity_domain_booking
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        baseActivity = this
        session = UserSessionManager(this)
        binding?.tvDomain?.text = session?.getDomainName(true)
        setupUI()
        onClickListeners()
    }

    private fun onClickListeners() {
        binding?.btnBuyAddon?.setOnClickListener {
            premiumMode()
        }

        binding?.btnBookOldDomain?.setOnClickListener {
            showBsheetInputOwnDomain()
        }

        binding?.btnBookNewDomain?.setOnClickListener {
            startFragmentDomainBookingActivity(
                activity = this,
                type = com.appservice.constant.FragmentType.BOOK_A_DOMAIN_SSL_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    private fun showBsheetInputOwnDomain() {
        val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = DataBindingUtil.inflate<BsheetInputOwnDomainBinding>(
            layoutInflater,
            R.layout.bsheet_input_own_domain,
            null,
            false
        )
        bSheet.setContentView(sheetBinding.root)
        bSheet.show()
        sheetBinding.btnContinue.setOnClickListener {
            showBsheetIntegrationOption()
        }
    }

    private fun showBsheetIntegrationOption() {
        val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = DataBindingUtil.inflate<BsheetDomainIntegrationOptionsBinding>(
            layoutInflater,
            R.layout.bsheet_domain_integration_options, null, false
        )
        bSheet.setContentView(sheetBinding.root)
        domainIntegrationUserSelection = 0
        sheetBinding.radioAsBusinessWebsite.isChecked = true
        sheetBinding.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    sheetBinding.radioAsBusinessWebsite.id -> {
                        domainIntegrationUserSelection = 0
                    }
                    sheetBinding.radioCreateASubdomain.id -> {
                        domainIntegrationUserSelection = 1
                    }
                }
        }

        sheetBinding.btnContinue.setOnClickListener{
            if(domainIntegrationUserSelection == 0)
                startFragmentDomainBookingActivity(
                    activity = this,
                    type = com.appservice.constant.FragmentType.ADDING_EXISTING_DOMAIN_FRAGMENT,
                    bundle = Bundle(),
                    clearTop = false
                )

        }
        bSheet.show()

    }

    private fun setupUI() {
        if (isPremium()) {
            premiumMode()
        } else {
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
        return session?.getStoreWidgets()?.contains("DOMAINPURCHASE") ?: false
    }

    private fun setupSteps() {
        val secondStep =
            "In case you have a different website (for same business) connected to the domain that you own, you can integrate this website also as a sub-domain on that domain. (what’s subdomain?)"
        val whatsSubdomain = "(what’s subdomain?)"
        val whatsSubdomainIndex = secondStep.indexOf(whatsSubdomain)

        val stepsList = arrayListOf(
            DomainStepsModel(SpannableString("Since search engines recognize a domain that’s already in use, we recommend integrating any relatable domain name that you currently own")),
            DomainStepsModel(SpannableString(secondStep)
                .apply {
                    setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {

                            }

                        },
                        whatsSubdomainIndex,
                        whatsSubdomainIndex + whatsSubdomain.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }),
            DomainStepsModel(SpannableString("If you don’t have any other domain, click on ‘book a new domain’ and choose a domain you like for your business website."))
        )

        val adapter = AppBaseRecyclerViewAdapter(this, stepsList, this)
        binding?.rvSteps?.adapter = adapter
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