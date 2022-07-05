package com.dashboard.controller.ui.website

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface.*
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.appservice.constant.FragmentType
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.websiteTheme.bottomsheet.RepublishProgressBottomSheet
import com.dashboard.controller.ui.websiteTheme.bottomsheet.TypeSuccess
import com.dashboard.controller.ui.websiteTheme.bottomsheet.WebSiteThemeResetBottomSheet
import com.dashboard.controller.ui.websiteTheme.bottomsheet.WebsiteThemeUpdatedSuccessfullySheet
import com.dashboard.controller.ui.websiteTheme.dialog.WebViewDialog
import com.dashboard.databinding.FragmentWebsiteV2Binding
import com.dashboard.model.live.websiteItem.WebsiteActionItem
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.glide.util.glideLoad
import com.framework.pref.BASE_IMAGE_URL
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.ContentSharing
import com.framework.utils.changeLayersColor
import com.framework.utils.fromHtml
import com.framework.views.customViews.CustomImageView
import com.framework.webengageconstant.DASHBOARD_WEBSITE_PAGE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.android.material.tabs.TabLayout


private val TAB_TITLES = arrayOf("Content", "Features")

class WebsiteV2Fragment : AppBaseFragment<FragmentWebsiteV2Binding, DashboardViewModel>() {

    private var session: UserSessionManager? = null
    private var websiteLink: String? = null
    private var businessName: String? = null
    private var businessContact: String? = null
    private var popupWindow: PopupWindow? = null
    private val TAG = "WebsiteFragment"
    override fun getLayout(): Int {
        return R.layout.fragment_website_v2
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        Log.i(TAG, "onCreateView: ")
        baseActivity.window.statusBarColor = getColor(R.color.website_toolbar_color)
        session = UserSessionManager(baseActivity)
        setOnClickListener(
            binding?.txtDomainName, binding?.btnProfileLogo, binding?.editProfileWrapper,
            binding?.websiteThemeCustomization, binding?.businessTiming, binding?.layoutContent, binding?.layoutSettings)
        WebEngageController.trackEvent(DASHBOARD_WEBSITE_PAGE, PAGE_VIEW, session?.fpTag)
        setupViewPager()
        this.websiteLink = fromHtml("<u>${session?.getDomainName()}</u>").toString()
        businessName = session?.fPName!!
        businessContact = session?.fPPrimaryContactNumber ?: ""
        setSpannable()
    }

    private fun setupViewPager() {
        binding?.pager?.adapter = CategoriesPagerV2Adapter(baseActivity, childFragmentManager)
        binding?.tabLayout?.setupWithViewPager(binding?.pager)
        for (i in 0..binding?.tabLayout?.tabCount!!) {
            val tab: TabLayout.Tab? = binding?.tabLayout?.getTabAt(i)
            if (tab != null) {
                val tabTextView = TextView(baseActivity)
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = tab.text
                if (i == 0) {
                    // This set the font style of the first tab
                    tabTextView.setTypeface(SANS_SERIF, BOLD)
                    tabTextView.setTextColor(getColor(R.color.colorAccent))

                }
                if (i == 1) {
                    // This set the font style of the first tab
                    tabTextView.setTypeface(null, NORMAL)
                }
            }
        }
        binding?.tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, BOLD)
                text.setTextColor(getColor(R.color.colorAccent))

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, NORMAL)
                text.setTextColor(
                    getColor(R.color.black_4a4a4a)
                )

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding?.pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                binding?.pager?.reMeasureCurrentPage(binding?.pager?.currentItem?:0)
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
    }

    override fun onResume() {
        super.onResume()
        setUserData()
    }

    private fun setUserData() {
        val desc = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)
        binding?.txtDesc?.apply {
            //if (desc.isNullOrEmpty().not()) visible() else gone()
            text = desc
        }
        binding?.txtBusinessName?.text =
            session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
        binding?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName()}</u>")
        var imageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
        if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("BizImages") && imageUri.contains(
                "http"
            ).not()
        ) {
            imageUri = BASE_IMAGE_URL + imageUri
        }
        binding?.imgProfileLogo?.apply {
            if (imageUri.isNullOrEmpty().not()) {
                baseActivity.glideLoad(
                    mImageView = this,
                    url = imageUri!!,
                    placeholder = R.drawable.gradient_white,
                    isLoadBitmap = true
                )
            } else setImageResource(R.drawable.ic_add_logo_d)
        }
        updateTimings()
    }

    private fun clickActionButton(type: WebsiteActionItem.IconType) {
        when (type) {
            WebsiteActionItem.IconType.service_product_catalogue -> baseActivity.startListServiceProduct(session)
            WebsiteActionItem.IconType.latest_update_tips -> session?.let { baseActivity.startUpdateLatestStory(it) }
            WebsiteActionItem.IconType.all_images -> startBackgroundActivity(session, FragmentType.BACKGROUND_IMAGE_FRAGMENT)
            WebsiteActionItem.IconType.business_profile -> baseActivity.startFragmentsFactory(session, fragmentType = "Business_Profile_Fragment_V2")
            WebsiteActionItem.IconType.testimonials -> baseActivity.startTestimonial(session)
            WebsiteActionItem.IconType.custom_page -> baseActivity.startCustomPage(session)
            WebsiteActionItem.IconType.project_teams -> baseActivity.startListProjectAndTeams(session)
            WebsiteActionItem.IconType.unlimited_digital_brochures -> baseActivity.startListDigitalBrochure(session)
            WebsiteActionItem.IconType.toppers_institute -> baseActivity.startListToppers(session)
            WebsiteActionItem.IconType.upcoming_batches -> baseActivity.startListBatches(session)
            WebsiteActionItem.IconType.faculty_management -> baseActivity.startFacultyMember(session)
            WebsiteActionItem.IconType.places_look_around -> baseActivity.startNearByView(session)
            WebsiteActionItem.IconType.trip_adviser_ratings -> baseActivity.startListTripAdvisor(session)
            WebsiteActionItem.IconType.seasonal_offers -> baseActivity.startListSeasonalOffer(session)
            WebsiteActionItem.IconType.website_theme -> baseActivity.startWebsiteTheme(session)
            WebsiteActionItem.IconType.doctor_e_profile_listing -> baseActivity.startListDoctors(session)
        }
    }


    private fun updateTimings() {
        session?.getCurrentTimingsData { isOpen, day, timing ->
            binding?.txtOpenClose?.text =
                resources.getString(if (isOpen) R.string.open_now else R.string.close_now)
            binding?.txtOpenClose?.setTextColor(
                if (isOpen) getColor(R.color.green_light) else getColor(
                    R.color.red_E39595
                )
            )
            binding?.ellipseOpenClose?.changeLayersColor(if (isOpen) R.color.green_light else R.color.red_E39595)
        }
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.txtDomainName -> baseActivity.startWebViewPageLoad(
                session,
                session!!.getDomainName(false)
            )
            binding?.btnProfileLogo -> baseActivity.startBusinessLogo(session)
            binding?.editProfileWrapper -> baseActivity.startBusinessProfileDetailEdit(session)
            binding?.websiteThemeCustomization -> baseActivity.startWebsiteNav(session)
//      binding?.contactDetail -> baseActivity.startBusinessInfoEmail(session)
            binding?.businessTiming -> baseActivity.startBusinessHours(session)
            binding?.layoutContent -> setUpButtonSelectedUI()
            binding?.layoutSettings -> setUpButtonSelectedUI(false)
        }

    }

    private fun openPopUp(v: View) {
        if (this.popupWindow?.isShowing == true) this.popupWindow?.dismiss()
        else showPopupWindow(v)
    }

    private fun shareWhatsAppText() {
        ContentSharing.shareWebsiteTheme(requireActivity(), businessName!!, websiteLink!!, businessContact!!, isWhatsApp = true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_website_theme, menu)
        val item = menu.findItem(R.id.menu_whatsapp_share)?.actionView
        item?.findViewById<LinearLayoutCompat>(R.id.share_whatsapp)?.setOnClickListener { shareWhatsAppText() }
        item?.findViewById<CustomImageView>(R.id.more_share)?.setOnClickListener { openPopUp(it) }
    }

    private fun showPopupWindow(anchor: View) {
        val view = LayoutInflater.from(baseActivity)
            .inflate(R.layout.popup_window_website_menu_share, null)
        this.popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val more = this.popupWindow?.contentView?.findViewById<LinearLayoutCompat>(R.id.ll_share)
        more?.setOnClickListener {
            shareMore()
            this.popupWindow?.dismiss()
        }
        val republishWebsiteBtn =
            this.popupWindow?.contentView?.findViewById<LinearLayoutCompat>(R.id.ll_republish)
        republishWebsiteBtn?.setOnClickListener {
            RepublishProgressBottomSheet().apply {
                onRepublishSuccess = { openSuccessDialog() }
                show(baseActivity.supportFragmentManager, RepublishProgressBottomSheet::javaClass.name)
            }
            this.popupWindow?.dismiss()
        }
        this.popupWindow?.elevation = 5.0F
        this.popupWindow?.showAsDropDown(anchor, 0, 20)
    }

    private fun shareMore() {
        ContentSharing.shareWebsiteTheme(baseActivity, businessName ?: "", websiteLink ?: "", businessContact ?: "")
    }

    private fun openSuccessDialog() {
        WebsiteThemeUpdatedSuccessfullySheet(isRepublishFlow = true).apply {
            onClicked = {
                when (it) {
                    TypeSuccess.VISIT_WEBSITE.name -> {
                        openWebViewDialog(
                            binding?.txtDomainName?.text.toString(),
                            session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
                                ?: ""
                        )
                    }
                    TypeSuccess.CLOSE.name -> dismiss()
                }
            }
            show(baseActivity.supportFragmentManager, WebSiteThemeResetBottomSheet::javaClass.name)
        }
    }

    private fun openWebViewDialog(url: String, title: String) {
        WebViewDialog().apply {
            setData(url, title)
            show(baseActivity.supportFragmentManager, WebViewDialog::javaClass.name)
        }
    }

    private fun setUpButtonSelectedUI(isContentSelected: Boolean = true) {
        if (isContentSelected) {
            binding?.layoutContent?.setBackgroundResource(R.drawable.rounded_rect_grey_18)
            binding?.layoutSettings?.setBackgroundResource(R.drawable.rounded_rect_white_18)
            binding?.titleContent?.setTextColor(getColor(R.color.white))
            binding?.titleSettings?.setTextColor(getColor(R.color.black_4a4a4a))
            binding?.ivContent?.setImageResource(R.drawable.ic_note_white)
            binding?.ivSettings?.setImageResource(R.drawable.ic_slider_grey)
            binding?.pager?.currentItem = 0
            setSpannable(true)

        } else {
            binding?.layoutContent?.setBackgroundResource(R.drawable.rounded_rect_white_18)
            binding?.layoutSettings?.setBackgroundResource(R.drawable.rounded_rect_grey_18)
            binding?.titleContent?.setTextColor(getColor(R.color.black_4a4a4a))
            binding?.titleSettings?.setTextColor(getColor(R.color.white))
            binding?.ivContent?.setImageResource(R.drawable.ic_note_grey)
            binding?.ivSettings?.setImageResource(R.drawable.ic_slider_white)
            binding?.pager?.currentItem = 1
            setSpannable(false)
        }
    }

    private fun setSpannable(isContentSelected: Boolean = true) {
        val spannableString: SpannableString = if (isContentSelected) {
            SpannableString(getString(R.string.content_click_here_text))
        } else {
            SpannableString(getString(R.string.setting_click_here_text))
        }

        spannableString.setSpan(
            object : ClickableSpan() {
                override fun onClick(p0: View) {
                    if (isContentSelected) binding?.layoutSettings?.performClick() else binding?.layoutContent?.performClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(baseActivity, R.color.color_747474)
                    ds.typeface = DEFAULT_BOLD
                }
            },
            spannableString.indexOf(getString(R.string.click_here)), spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.tvClickHere?.text = spannableString
        binding?.tvClickHere?.movementMethod = LinkMovementMethod.getInstance()
    }
}


@SuppressLint("WrongConstant")
class CategoriesPagerV2Adapter(private var context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return FragmentCategoryV2.newInstance(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return 2
    }
}