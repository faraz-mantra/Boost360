package com.boost.upgrades.ui.details

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.ReviewViewPagerAdapter
import com.boost.upgrades.adapter.ZoomOutPageTransformer
import com.boost.upgrades.data.api_model.GetAllWidgets.FeatureDetails
import com.boost.upgrades.data.api_model.GetAllWidgets.Review
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.webview.WebViewFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.WEB_VIEW_FRAGMENT
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import com.boost.upgrades.utils.Utils.longToast
import com.bumptech.glide.Glide
//import com.devs.readmoreoption.ReadMoreOption
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.details_fragment.*
import retrofit2.Retrofit


class DetailsFragment : BaseFragment() {

    lateinit var root: View
    lateinit var viewModel: DetailsViewModel
    lateinit var detailsViewModelFactory: DetailsViewModelFactory

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    lateinit var localStorage: LocalStorage
    var singleItemId: String? = null
    var badgeNumber = 0
    var addons_list: List<WidgetModel>? = null
    var cart_list: List<CartModel>? = null
    var itemInCartStatus = false
    var widgetLearnMoreLink: String? = null

    lateinit var reviewAdaptor: ReviewViewPagerAdapter
//    private var detailsAdapter = DetailsAdapter(ArrayList())

    companion object {
        fun newInstance() = DetailsFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.details_fragment, container, false)


        detailsViewModelFactory =
                DetailsViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), detailsViewModelFactory)
                .get(DetailsViewModel::class.java)

        reviewAdaptor = ReviewViewPagerAdapter(ArrayList())
        localStorage = LocalStorage.getInstance(context!!)!!
        singleItemId = arguments!!.getString("itemId")

//        addons_list = localStorage.getInitialLoad()

//        cart_list = localStorage.getCartItems()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spannableString()
        loadData()
        initializeViewPager()

        viewModel.addonsResult().observe(this, Observer {
            viewModel.getCartItems()
            addons_list = it
            if (addons_list != null) {
                for (item in addons_list!!) {
                    if (item.id == singleItemId) {
                        val featureType = object : TypeToken<FeatureDetails>() {}.type
                        val featureDetails: FeatureDetails = Gson().fromJson(item.featureDetails, featureType)
                        Glide.with(this).load(item.image)
                                .into(image1222)

                        Glide.with(this).load(item.image)
                                .into(title_image)

                        Glide.with(this).load(featureDetails.backgroundImage)
                                .into(details_image_bg)

                        title_top_1.text = item.title
                        title_top.text = item.name
                        title_appbar.text = item.name
                        details_discount.text = item.discount.toString()+"% OFF"
                        title_bottom2.text = featureDetails.noOfbusinessUsed.toString() + " businesses have added this"
                        money.text = "₹" + item.price + "/month"
                        orig_cost.text = "Original cost ₹" + item.MRPPrice + "/month"
                        widgetLearnMore.text = featureDetails.learnMore.title
                        widgetLearnMoreLink = featureDetails.learnMore.link
                        xheader.text = featureDetails.subTitle
                        abcText.text = featureDetails.subDesc
                        updateReview(featureDetails.review)
                        break
                    }
                }
            }
        })

        viewModel.cartResult().observe(this, Observer {
            cart_list = it
            itemInCartStatus = false
            if (cart_list != null && cart_list!!.size > 0) {
                badge121.visibility = View.VISIBLE
                for (item in cart_list!!) {
                    if (item.id == singleItemId) {
                        add_item_to_cart.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.added_to_cart_grey
                        )
                        add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                        add_item_to_cart.setText(getString(R.string.added_to_cart))
                        havent_bought_the_feature.visibility = View.INVISIBLE
                        itemInCartStatus = true
                        break
                    }
                }
                badgeNumber = cart_list!!.size
                badge121.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
                if (!itemInCartStatus) {
                    add_item_to_cart.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.orange_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.WHITE)
                    add_item_to_cart.setText(getString(R.string.add_for_99_month))
                    havent_bought_the_feature.visibility = View.VISIBLE
                }
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                itemInCartStatus = false
                add_item_to_cart.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.orange_button_click_effect
                )
                add_item_to_cart.setTextColor(Color.WHITE)
                add_item_to_cart.setText(getString(R.string.add_for_99_month))
                havent_bought_the_feature.visibility = View.VISIBLE
            }
        })

        viewModel.addonsError().observe(this, Observer {
            longToast(requireContext(), "onFailure: " + it)
        })

        viewModel.addonsLoader().observe(this, Observer {
            if (!it) {

            }
        })


        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                title_appbar.visibility = View.VISIBLE
                title_image.visibility = View.VISIBLE
                // Collapsed
            } else if (verticalOffset == 0) {
                title_appbar.visibility = View.INVISIBLE
                title_image.visibility = View.INVISIBLE

                // Expanded
            } else {
                title_appbar.visibility = View.INVISIBLE
                title_image.visibility = View.INVISIBLE
            }
        }
        )

        abcText.setText(getString(R.string.addons_description))
        var readmoreState = false
        readmore.setOnClickListener {
            if (readmoreState) {
                abcText.maxLines = 10
                readmoreState = false
                readmore.setText(getString(R.string.read_more))
            } else {
                abcText.maxLines = 50
                readmoreState = true
                readmore.setText(getString(R.string.read_less))
            }
        }


        add_item_to_cart.setOnClickListener {
            if (!itemInCartStatus) {
                if (addons_list != null) {
                    for (item in addons_list!!) {
                        if (item.id == singleItemId) {
                            viewModel.addItemToCart(item)
                            break
                        }
                    }
                    badgeNumber = badgeNumber + 1
                    badge121.setText(badgeNumber.toString())
                    badge121.visibility = View.VISIBLE
                    Constants.CART_VALUE = badgeNumber

//                    localStorage.addCartItem(addons_list!!.get(itemId))

                    add_item_to_cart.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                    add_item_to_cart.setText(getString(R.string.added_to_cart))
                    havent_bought_the_feature.visibility = View.INVISIBLE
                    itemInCartStatus = true
                }
            }
        }

        imageView121.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        imageViewCart121.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    CartFragment.newInstance(),
                    CART_FRAGMENT
            )
        }

        fb_layout.setOnClickListener {
            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("link", widgetLearnMoreLink)
            webViewFragment.arguments = args
            (activity as UpgradeActivity).addFragment(
                    webViewFragment,
                    WEB_VIEW_FRAGMENT
            )
        }

    }

    override fun onResume() {
        super.onResume()
        val pos = 2
        reviewViewpager.postDelayed(Runnable { reviewViewpager.setCurrentItem(pos) }, 100)
    }

    fun spannableString() {
        val origCost = SpannableString("Original cost ₹799/month")

        origCost.setSpan(
                StrikethroughSpan(),
                15,
                origCost.length,
                0
        )
        orig_cost.setText(origCost)
    }

    fun loadData() {
        viewModel.loadAddonsFromDB()
    }

    fun updateReview(list: List<Review>){
        reviewAdaptor.addupdates(list)
    }

    private fun initializeViewPager() {
        reviewViewpager.adapter = reviewAdaptor
        dots_indicator.setViewPager2(reviewViewpager)
        reviewViewpager.offscreenPageLimit = 1

        reviewViewpager.setPageTransformer(ZoomOutPageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
                requireContext(),
                R.dimen.viewpager_current_item_horizontal_margin
        )
        reviewViewpager.addItemDecoration(itemDecoration)
//        viewpager.setPageTransformer(ZoomOutPageTransformer())
    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
            viewModel.getCartItems()
        }
    }

}
