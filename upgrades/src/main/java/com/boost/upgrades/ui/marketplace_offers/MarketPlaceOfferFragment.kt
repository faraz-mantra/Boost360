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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.UpgradeAdapter
import com.boost.upgrades.adapter.VideosListAdapter
import com.boost.upgrades.data.api_model.GetAllFeatures.response.MarketPlaceOfferDetail
import com.boost.upgrades.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.upgrades.data.api_model.GetPurchaseOrder.Result
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.HistoryFragmentListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.freeaddons.FreeAddonsFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.framework.webengageconstant.ADDONS_MARKETPLACE_OFFERS_LOADED
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.compare_all_packages_new.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.*
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.back_image
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.package_back
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MarketPlaceOfferFragment : BaseFragment(), HistoryFragmentListener {

    lateinit var root: View

    var marketOffersData: MarketPlaceOffers? = null
    lateinit var marketOfferDetailAdapter: MarketOfferDetailAdapter
    lateinit var marketOfferTermsAdapter: MarketOfferTermsAdapter

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
        marketOfferDetailAdapter = MarketOfferDetailAdapter(ArrayList(), this)
        marketOfferTermsAdapter = MarketOfferTermsAdapter(ArrayList(), this)
        WebEngageController.trackEvent(ADDONS_MARKETPLACE_OFFERS_LOADED, PAGE_VIEW, NO_EVENT_VALUE)
        prefs = SharedPrefs(activity as UpgradeActivity)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketPlaceOfferViewModel::class.java)

        initializeRecycler()
        initializeTermsRecycler()
        loadData()
        initMvvm()

        offer_title.setText(marketOffersData!!.title)
        avail_coupon_txt.setText(marketOffersData!!.coupon_code)
        offer_validity_txt.setText(getConvertedExpiryDateFormat(marketOffersData!!.expiry_date))
        if(marketOffersData?.extra_information != null){
            var list: ArrayList<String> = arrayListOf()
            var listTerms: ArrayList<String> = arrayListOf()

//            val extraInfoList = marketOffersData?.extra_information?.split("s1")?.toTypedArray()
//            val extraInfoList = marketOffersData?.extra_information?.split("s1")?.toTypedArray()
            var extra = marketOffersData?.extra_information?.trim()
            extra = extra?.replace("S2coupon_code",marketOffersData!!.coupon_code)
            extra = extra?.replace("S1coupon_code",marketOffersData!!.coupon_code)
            extra = extra?.replace("S2discount_amt","null")
            extra = extra!!.replace("S1discount_amt","null")
            extra = extra?.replace("S2from_date",getConvertedDateFormat(marketOffersData!!.createdon))
            extra = extra?.replace("S2to_date",getConvertedExpiryDateFormat(marketOffersData!!.expiry_date))
            Log.v("marketOffersDataValue", " " + extra)
//            var extraInfoTermsNCond = marketOffersData?.extra_information?.trim()?.split("Terms and Conditions")?.toTypedArray()
            var extraInfoTermsNCond = extra.trim()?.split("Terms and Conditions")?.toTypedArray()

            Log.v("extraInfoTermsNCond", " " + extraInfoTermsNCond?.get(1))
            var termsContent = extraInfoTermsNCond?.get(1)?.split("s2")?.toTypedArray()
            var offerContent = extraInfoTermsNCond?.get(0)?.split("s1")?.toTypedArray()
            for(terms in termsContent!!){
                if(terms.isNotEmpty()) {
                    Log.v("listTermsBoolean", " " + terms.contains("S2coupon_code")  + terms.contains("S2from_date") + marketOffersData!!.coupon_code)

                    listTerms.add(terms)
                    Log.v("listTerms", " " + terms)
                }


            }
            updateTermsRecycler(listTerms)

            for(extraInfo in offerContent!!){
                if(extraInfo.isNotEmpty() && !extraInfo.equals("") && extraInfo.length > 1){
                    extraInfo.replace("@coupon_code",marketOffersData!!.coupon_code)
                    extraInfo.replace("@discount_amt","null")
                    list.add(extraInfo)
                    Log.v("extraInfoList", " " + extraInfo)
                }


            }
            updateRecycler(list)
//            marketOfferDetailAdapter = MarketOfferDetailAdapter(list, this)
        }
//        howto_pointTwo.setText(resources.getString(R.string.marketoffer_howtoTwo, marketOffersData!!.coupon_code))
//        howto_pointThree_layout.visibility = View.GONE
//        term_one.setText(resources.getString(R.string.marketoffer_termOne, marketOffersData!!.coupon_code))

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
//        ss.setSpan(clickableSpan, 86, 96, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        term_six.setText(ss)
//        term_six.setMovementMethod(LinkMovementMethod.getInstance())



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
        date_from_to_layout.setOnClickListener {
            val clipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(avail_coupon_txt.text, avail_coupon_txt.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(activity, "Coupon " + avail_coupon_txt.text + " copied!!" , Toast.LENGTH_SHORT).show()
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
        Log.d("marketOffersCoupon", " " + marketOffersData!!.coupon_code)
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
//        val output = SimpleDateFormat("dd'th' MMM yyyy ")
        val output = SimpleDateFormat("dd-MMM-yy ")

        var d: Date? = null
        try {
            d = input.parse(textDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return output.format(d)
    }

    override fun viewHistoryItem(item: Result) {
        TODO("Not yet implemented")
    }

    fun updateRecycler(list: List<String>) {
        Log.v("updateRecycler", " "+ list)
        marketOfferDetailAdapter.addupdates(list)
        recyclerOfferDetails.adapter = marketOfferDetailAdapter
//        marketOfferDetailAdapter.notifyDataSetChanged()
    }

    private fun initializeRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerOfferDetails.apply {
            layoutManager = gridLayoutManager
        }
    }
    private fun initializeTermsRecycler() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerTerms.apply {
            layoutManager = gridLayoutManager
        }
    }
    fun updateTermsRecycler(list: List<String>) {
        Log.v("updateRecycler", " "+ list)
        marketOfferTermsAdapter.addupdates(list)
        recyclerTerms.adapter = marketOfferTermsAdapter
//        marketOfferDetailAdapter.notifyDataSetChanged()
    }
}
