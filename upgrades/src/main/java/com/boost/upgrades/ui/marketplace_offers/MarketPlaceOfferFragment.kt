package com.boost.upgrades.ui.marketplace_offers

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.freeaddons.FreeAddonsFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.SharedPrefs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.compare_all_packages_new.*
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.*
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.package_back
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MarketPlaceOfferFragment : BaseFragment() {

    lateinit var root: View

    var marketOffersData: MarketPlaceOffers? = null

    lateinit var prefs: SharedPrefs

    private lateinit var viewModel: MarketPlaceOfferViewModel

    companion object {
        fun newInstance() = MarketPlaceOfferFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.marketplaceoffer_fragment, container, false)

        val jsonString = arguments!!.getString("marketOffersData")
        marketOffersData = Gson().fromJson<MarketPlaceOffers>(jsonString, object : TypeToken<MarketPlaceOffers>() {}.type)
        Log.v("bundleData", " " + marketOffersData?.expiry_date  + " "+ marketOffersData?.createdon)
        prefs = SharedPrefs(activity as UpgradeActivity)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketPlaceOfferViewModel::class.java)

        loadData()
        initMvvm()

        offer_title.setText(marketOffersData!!.title)
        avail_coupon_txt.setText(marketOffersData!!.coupon_code)
        howto_pointTwo.setText(resources.getString(R.string.marketoffer_howtoTwo, marketOffersData!!.coupon_code))
        howto_pointThree_layout.visibility = View.GONE
        term_one.setText(resources.getString(R.string.marketoffer_termOne, marketOffersData!!.coupon_code))

        val ss = SpannableString(resources.getString(R.string.marketoffer_termSix, getConvertedDateFormat(marketOffersData!!.createdon),getConvertedExpiryDateFormat(marketOffersData!!.expiry_date)  ))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(activity, "On Clicked " , Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.common_text_color)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan, 86, 96, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        term_six.setText(ss)
        term_six.setMovementMethod(LinkMovementMethod.getInstance())



        if (arguments!!.containsKey("showCartIcon")) {
            offer_info_icon.visibility = View.INVISIBLE
            avail_coupon_submit.visibility = View.GONE
        }

        if (marketOffersData!!.image != null && !marketOffersData!!.image!!.url.isNullOrEmpty()) {
            Glide.with(this).load(marketOffersData!!.image!!.url).into(marketoffers_image)
        } else {
            marketoffers_image.setImageResource(R.drawable.rectangle_copy_18)
        }

        package_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        offer_info_icon.setOnClickListener {
//            (activity as UpgradeActivity).addFragment(
//                    CartFragment.newInstance(),
//                    Constants.CART_FRAGMENT
//            )
        }
        avail_coupon_submit.setOnClickListener {
            val clipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(avail_coupon_txt.text, avail_coupon_txt.text)
            clipboard.setPrimaryClip(clip)
            (activity as UpgradeActivity).popFragmentFromBackStack()
            Toast.makeText(activity, "Coupon " + avail_coupon_txt.text + " copied!!" , Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadData() {
        Log.v("marketOffersCoupon", " " + marketOffersData!!.coupon_code)
        viewModel.getOffersByCouponId(marketOffersData!!.coupon_code)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.marketOffersCouponResult().observe(this, Observer {
            Log.v("marketOffersCoupon", " " + it.coupon_code)
        })
    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
//            viewModel.getCartItems()
        }
    }

    fun getConvertedDateFormat(textDate: String) : String{
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
       val output = SimpleDateFormat("dd'th' MMM yyyy ")

       var d: Date? = null
       try {
           d = input.parse(textDate)
       } catch (e: ParseException) {
           e.printStackTrace()
       }
        return output.format(d)
    }
    fun getConvertedExpiryDateFormat(textDate: String) : String{
        Log.v("getConvertedExpir", " "+ textDate)
//        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val output = SimpleDateFormat("dd'th' MMM yyyy ")

        var d: Date? = null
        try {
            d = input.parse(textDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return output.format(d)
    }
}
