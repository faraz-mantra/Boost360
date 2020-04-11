package com.boost.upgrades.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.PackageViewPagerAdapter
import com.boost.upgrades.adapter.SimplePageTransformer
import com.boost.upgrades.adapter.UpgradeAdapter
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.HomeListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.PACKAGE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import com.boost.upgrades.utils.Utils.getRetrofit
import com.boost.upgrades.utils.Utils.longToast
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.dots_indicator
import retrofit2.Retrofit
import java.util.*

class HomeFragment : BaseFragment(), HomeListener {

    lateinit var root: View
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    lateinit var localStorage: LocalStorage

    lateinit var upgradeAdapter: UpgradeAdapter

    lateinit var progressDialog: ProgressDialog

//    lateinit var packageViewPagerAdapter: PackageViewPagerAdapter

    var cart_list: List<WidgetModel>? = null
    var badgeNumber = 0
//    val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)

        homeViewModelFactory = HomeViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), homeViewModelFactory)
                .get(HomeViewModel::class.java)

        upgradeAdapter = UpgradeAdapter((activity as UpgradeActivity), ArrayList())
//        packageViewPagerAdapter = PackageViewPagerAdapter(ArrayList(), this)

        //request retrofit instance
        ApiService = getRetrofit()
                .create(ApiInterface::class.java)

        progressDialog = ProgressDialog(requireContext())

        localStorage = LocalStorage.getInstance(context!!)!!

        cart_list = localStorage.getCartItems()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        spannableString()
        loadData()
        initMvvm()

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Loaded", "ADDONS_MARKETPLACE", "")

        shimmer_view_container.duration = 600
        shimmer_view_container.startShimmerAnimation()


        Glide.with(this).load(R.drawable.back_beau)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(back_image)

        imageView21.setOnClickListener {
            (activity as UpgradeActivity).finish()
        }



        imageViewCart1.setOnClickListener {
            (activity as UpgradeActivity).addFragment(CartFragment.newInstance(), CART_FRAGMENT)
        }

//        initializeViewPager()
        initializeRecycler()

        share_fb_1.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent
                    .putExtra(
                            Intent.EXTRA_TEXT,
                            "<---YOUR TEXT HERE--->."
                    )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.facebook.orca")
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {

                Toasty.error(
                        requireContext(),
                        "Please Install Facebook Messenger",
                        Toast.LENGTH_SHORT,
                        true
                ).show();

            }
        }
        share_whatsapp_1.setOnClickListener {
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Application of social rating share with your friend"
            )
            try {
                Objects.requireNonNull(this).startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                        )
                )
            }

        }
        share_referal.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Referral Code : daidvadnvandvoiw121"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

//        if(::localStorage.isInitialized) {
//            cart_list = localStorage.getCartItems()
//            if (cart_list != null) {
//                badgeNumber = cart_list!!.size
//                badge.setText(badgeNumber.toString())
//                Constants.CART_VALUE = badgeNumber
//            }
//        }


        view_my_addons.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    MyAddonsFragment.newInstance(),
                    MYADDONS_FRAGMENT
            )
        }

        all_recommended_addons.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    ViewAllFeaturesFragment.newInstance(),
                    VIEW_ALL_FEATURE
            )
        }


    }

    override fun onResume() {
        super.onResume()
//        val pos = 2
//        package_viewpager.postDelayed(Runnable { package_viewpager.setCurrentItem(pos) }, 100)
    }

    fun spannableString() {
        val referralText = SpannableString("Read Boost referral TnC")
        val termsOfUseClicked: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                // navigate to sign up fragment
                Toast.makeText(
                        requireContext(),
                        "Read Boost referral TnC is clicked...",
                        Toast.LENGTH_LONG
                ).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        //By creating Boost account, you agree to our Terms of use and Privacy Policy
        referralText.setSpan(termsOfUseClicked, 0, referralText.length, 0)
        referralText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
                0,
                referralText.length,
                0
        )
        referralText.setSpan(UnderlineSpan(), 0, referralText.length, 0)
        bottom_referral.setText(referralText)

        val refText =
                SpannableString("and use SAM Chat Bot on your site for a month absolutely FREE")
//        val boldSpan = StyleSpan(Typeface.BOLD);
        refText.setSpan(StyleSpan(Typeface.BOLD), 8, 20, 0)
        refText.setSpan(StyleSpan(Typeface.BOLD), refText.length - 4, refText.length, 0)
        refText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
                0,
                refText.length,
                0
        )
        ref_txt.setText(refText)
    }

    fun loadData() {
        viewModel.loadUpdates()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm(){

        viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
            //            Snackbar.make(root, viewModel.errorMessage, Snackbar.LENGTH_LONG).show()
//            if (shimmer_view_container.isAnimationStarted) {
//                shimmer_view_container.stopShimmerAnimation()
//                shimmer_view_container.visibility = View.GONE
//            }
            longToast(requireContext(), "onFailure: " + it)
        })

//        viewModel.upgradeResult().observe(this, androidx.lifecycle.Observer {
//            updateRecycler(it)
//        })

        viewModel.initialResult().observe(this, androidx.lifecycle.Observer {
            updateRecycler(it)
        })

        viewModel.getFreeAddonsCount().observe(this, androidx.lifecycle.Observer {
            free_accouns_count.setText(it.toString())
        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            if (it != null && it.size > 0) {
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
            }
        })
    }

    fun updateRecycler(list: List<FeaturesModel>) {
        if (shimmer_view_container.isAnimationStarted) {
            shimmer_view_container.stopShimmerAnimation()
            shimmer_view_container.visibility = View.GONE
        }
        upgradeAdapter.addupdates(list)
        recycler.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        recycler.setFocusable(false)
        back_image.setFocusable(true)
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler.apply {
            layoutManager = gridLayoutManager
        }
    }

//    private fun initializeViewPager() {
//        package_viewpager.adapter = packageViewPagerAdapter
//        dots_indicator.setViewPager2(package_viewpager)
//        package_viewpager.offscreenPageLimit = 1
//
//        package_viewpager.setPageTransformer(SimplePageTransformer())
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        package_viewpager.addItemDecoration(itemDecoration)
//
//    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
            viewModel.getCartItems()
        }
    }

    override fun onPackageClicked(v: View?) {
        (activity as UpgradeActivity).addFragment(PackageFragment.newInstance(), PACKAGE_FRAGMENT)
    }

    override fun onAddonsClicked(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
