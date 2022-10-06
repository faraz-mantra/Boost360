package com.boost.marketplace.ui.comparePacksV3

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.AllFrequentlyAskedQuestion
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
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
import com.framework.analytics.SentryController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ComparePacksV3AddonsPopUpFragment : DialogFragment() {

    protected var viewModel: ComparePacksViewModel? = null
    lateinit var binding: PackDetailsBottomSheetBinding;

    //    protected var binding: Binding? = null
    lateinit var features: FeaturesModel
    var addonDetails: FeaturesModel? = null
    lateinit var features1: String
    lateinit var featuresList: List<FeaturesModel>
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
    lateinit var root: View
    lateinit var singleBundle: Bundles
    lateinit var cartItems: List<CartModel>

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.pack_details_bottom_sheet,
            container,
            false
        )

//        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding?.lifecycleOwner = this
//        root = inflater.inflate(R.layout.pack_details_bottom_sheet, container, false)
//        singleBundle = Gson().fromJson(
//            requireArguments().getString("bundleData"),
//            object : TypeToken<Bundles>() {}.type
//        )

        howToUseAdapter = HowToUseAdapter(requireActivity(), java.util.ArrayList())
        faqAdapter = FAQAdapter(requireActivity(), java.util.ArrayList())

//        featuresList =
//            Gson().fromJson<List<FeaturesModel>>(
//                requireArguments().getString("addons"),
//                object : TypeToken<List<FeaturesModel>>() {}.type
//            )

        features1 = requireArguments().getString("featureCode").toString()


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
        prefs = activity?.let { SharedPrefs(it) }!!
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)



        binding?.learnMoreBtn?.setOnClickListener {
            binding?.learnMoreBtn?.visibility = View.GONE
            binding?.learnLessBtn?.visibility = View.VISIBLE
            binding?.titleBottom3?.maxLines = 20
        }

        binding.learnLessBtn.setOnClickListener {
            binding.learnMoreBtn.visibility = View.VISIBLE
            binding.learnLessBtn.visibility = View.GONE
            binding.titleBottom3.maxLines = 2
            binding.titleBottom3.ellipsize = TextUtils.TruncateAt.END
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        loadData()
        initMvvm()
        return binding.root
    }

    fun loadData() {
        try {
            viewModel?.loadAddonsFromDB(features1)
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsViewpager?.let { binding?.benefitsIndicator?.setViewPager2(it) }
        binding?.benefitsViewpager?.offscreenPageLimit = 1
        binding?.benefitsViewpager?.setPageTransformer(BenifitsPageTransformer(requireActivity()))

        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_current_item_horizontal_margin,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.howToUseRecycler.apply {
            layoutManager = gridLayoutManager
            binding.howToUseRecycler.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.faqRecycler.apply {
            layoutManager = gridLayoutManager
            binding.faqRecycler.adapter = faqAdapter
        }
    }

    private fun initMvvm() {

        viewModel?.addonsResult()?.observe(this, Observer {

            addonDetails = it

            if (it != null) {

                Glide.with(this).load(it.primary_image!!)
                    .into(binding.packageProfileImage)

                binding.packageTitle.text = it.name
                binding.titleBottom3.text = it.description
            }

            if (it.benefits != null) {
                binding.benefitContainerItem.visibility = View.VISIBLE
                val benefits = Gson().fromJson<List<String>>(
                    it.benefits,
                    object : TypeToken<List<String>>() {}.type
                )
                benefitAdaptor.addupdates(benefits)
                benefitAdaptor.notifyDataSetChanged()
            }
            if (it.how_to_use_steps != null) {
                binding.howToUseContainer.visibility = View.VISIBLE
                binding.howToUseTitleLayout.visibility = View.VISIBLE
                binding.howToUseTitleLayout.setOnClickListener {
                    if (binding.howToUseRecycler.visibility == View.VISIBLE) {
                        binding.howToUseArrow.setImageResource(R.drawable.ic_arrow_down_gray)
                        binding.howToUseRecycler.visibility = View.GONE
                    } else {
                        binding.howToUseArrow.setImageResource(R.drawable.ic_arrow_up_gray)
                        binding.howToUseRecycler.visibility = View.VISIBLE
                    }
                }
                if (it.target_business_usecase != null) {
                    binding.tvHowToUseTitle.text =
                        "How To Use " + it!!.target_business_usecase
                }
                val steps = Gson().fromJson<List<HowToUseStep>>(
                    it.how_to_use_steps,
                    object : TypeToken<List<HowToUseStep>>() {}.type
                )
                howToUseAdapter.addupdates(steps)
                howToUseAdapter.notifyDataSetChanged()
            }

            if (it.all_frequently_asked_questions != null) {
                binding.faqContainer.visibility = View.VISIBLE
                val faq = Gson().fromJson<List<AllFrequentlyAskedQuestion>>(
                    it?.all_frequently_asked_questions,
                    object : TypeToken<List<AllFrequentlyAskedQuestion>>() {}.type
                )
                faqAdapter.addupdates(faq)
                faqAdapter.notifyDataSetChanged()
            }

        })

    }
}