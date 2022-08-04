package com.boost.marketplace.ui.pack_details

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.adapter.SimplePageTransformerSmall
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.AllFrequentlyAskedQuestion
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.HowToUseStep
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.Adapters.PacksV3BenefitsViewPagerAdapter
import com.boost.marketplace.R
import com.boost.marketplace.adapter.FAQAdapter
import com.boost.marketplace.adapter.HowToUseAdapter
import com.boost.marketplace.databinding.PackDetailsBottomSheetBinding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.pack_details_bottom_sheet.*

class PackDetailsBottomSheet :
    BaseBottomSheetDialog<PackDetailsBottomSheetBinding, ComparePacksViewModel>() {

    lateinit var features: FeaturesModel
    var experienceCode: String? = null
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = java.util.ArrayList<String>()
    var cartList: List<CartModel>? = null
    var packageInCartStatus = false
    var itemInCart = false
    lateinit var prefs: SharedPrefs
    var offeredBundlePrice: Double = 0.0
    var originalBundlePrice: Double = 0.0
    var addonsSize: Int = 0
    var cartCount = 0
    lateinit var benefitAdaptor: PacksV3BenefitsViewPagerAdapter
    lateinit var howToUseAdapter: HowToUseAdapter
    lateinit var faqAdapter: FAQAdapter

    override fun getLayout(): Int {
        return R.layout.pack_details_bottom_sheet
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {

        howToUseAdapter = HowToUseAdapter(requireActivity(), java.util.ArrayList())
        faqAdapter = FAQAdapter(requireActivity(), java.util.ArrayList())
        dialog.behavior.isDraggable = true

        features = Gson().fromJson<FeaturesModel>(
            requireArguments().getString("features"),
            object : TypeToken<FeaturesModel>() {}.type
        )

        experienceCode = requireArguments().getString("expCode")
        fpid = requireArguments().getString("fpid")
        isDeepLink = requireArguments().getBoolean("isDeepLink", false)
        deepLinkViewType = requireArguments().getString("deepLinkViewType") ?: ""
        deepLinkDay = requireArguments().getString("deepLinkDay")?.toIntOrNull() ?: 7
        email = requireArguments().getString("email")
        mobileNo = requireArguments().getString("mobileNo")
        profileUrl = requireArguments().getString("profileUrl")
        accountType = requireArguments().getString("accountType")
        isOpenCardFragment = requireArguments().getBoolean("isOpenCardFragment", false)
        userPurchsedWidgets =
            requireArguments().getStringArrayList("userPurchsedWidgets") ?: java.util.ArrayList()
        addonsSize = requireArguments().getInt("addons")
        offeredBundlePrice = requireArguments().getDouble("price")
        originalBundlePrice = requireArguments().getDouble("price")
        benefitAdaptor = PacksV3BenefitsViewPagerAdapter(ArrayList())
        initializeViewPager()
        initializeFAQRecycler()
        initializeHowToUseRecycler()
        prefs = SharedPrefs(baseActivity)
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        binding?.titleBottom3?.text = features.description
        binding?.packageTitle?.text = features.name

        binding?.packageProfileImage?.let {
            Glide.with(this).load(features.primary_image!!)
                .into(it)
        }
        binding?.learnMoreBtn?.setOnClickListener {

            binding?.learnMoreBtn?.visibility = View.GONE
            binding?.learnLessBtn?.visibility = View.VISIBLE
            binding?.titleBottom3?.maxLines = 20

        }

        binding?.learnLessBtn?.setOnClickListener {
            binding?.learnMoreBtn?.visibility = View.VISIBLE
            binding?.learnLessBtn?.visibility = View.GONE
            binding?.titleBottom3?.maxLines = 2
            binding?.titleBottom3?.ellipsize = TextUtils.TruncateAt.END

        }


        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }




        initMvvm()

    }

    private fun initMvvm() {

        if (features?.benefits != null) {
            binding?.benefitContainerItem?.visibility = View.VISIBLE
            val benefits = Gson().fromJson<List<String>>(
                features?.benefits!!,
                object : TypeToken<List<String>>() {}.type
            )
            benefitAdaptor.addupdates(benefits)
            benefitAdaptor.notifyDataSetChanged()
        }
        if (features.how_to_use_steps != null) {
            binding?.howToUseContainer?.visibility = View.VISIBLE
            how_to_use_title_layout.setOnClickListener {
                if (how_to_use_recycler.visibility == View.VISIBLE) {
                    how_to_use_arrow.setImageResource(R.drawable.ic_arrow_down_gray)
                    how_to_use_recycler.visibility = View.GONE
                } else {
                    how_to_use_arrow.setImageResource(R.drawable.ic_arrow_up_gray)
                    how_to_use_recycler.visibility = View.VISIBLE
                }
            }
            if (features.target_business_usecase != null) {
                tv_how_to_use_title.text =
                    "How To Use " + features!!.target_business_usecase
            }
            val steps = Gson().fromJson<List<HowToUseStep>>(
                features.how_to_use_steps,
                object : TypeToken<List<HowToUseStep>>() {}.type
            )
            howToUseAdapter.addupdates(steps)
            howToUseAdapter.notifyDataSetChanged()
        }

        if (features.all_frequently_asked_questions != null) {
            faq_container.visibility = View.VISIBLE
            val faq = Gson().fromJson<List<AllFrequentlyAskedQuestion>>(
                features?.all_frequently_asked_questions,
                object : TypeToken<List<AllFrequentlyAskedQuestion>>() {}.type
            )
            faqAdapter.addupdates(faq)
            faqAdapter.notifyDataSetChanged()
        }

    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsViewpager?.let { binding?.benefitsIndicator?.setViewPager2(it) }
        binding?.benefitsViewpager?.offscreenPageLimit = 1
        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())

        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_next_item_visible,
            R.dimen.viewpager_next_item_visible
        )
        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        how_to_use_recycler.apply {
            layoutManager = gridLayoutManager
            how_to_use_recycler.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        faq_recycler.apply {
            layoutManager = gridLayoutManager
            faq_recycler.adapter = faqAdapter
        }
    }

}