package com.boost.marketplace.ui.Marketplace_Offers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceoffersBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_marketplaceoffers.*
import kotlinx.android.synthetic.main.item_marketplaceoffers_info.view.*
import kotlinx.android.synthetic.main.item_packs_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MarketPlaceOffersActivity :
    AppBaseActivity<ActivityMarketplaceoffersBinding, MarketPlaceOffersViewModel>() {


    var marketOffersData: MarketPlaceOffers? = null
    lateinit var marketOfferDetailAdapter: AppBaseRecyclerViewAdapter<MarketPlaceOffers>
    lateinit var marketOfferTermsAdapter: AppBaseRecyclerViewAdapter<MarketPlaceOffers>
    lateinit var prefs: SharedPrefs


    override fun getLayout(): Int {
        return R.layout.activity_marketplaceoffers
    }

    override fun getViewModelClass(): Class<MarketPlaceOffersViewModel> {
        return MarketPlaceOffersViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()



        marketOffersData = intent.extras?.getSerializable("marketOffersData") as? MarketPlaceOffers
        //   marketOffersData = Gson().fromJson<MarketPlaceOffers>(jsonString, object : TypeToken<MarketPlaceOffers>() {}.type)
        prefs = SharedPrefs(this)

        initializeDetailsRecycler()
        initializeTermsRecycler()
        //  loadData()
        initMvvm()

        binding!!.offerTitle.text = marketOffersData!!.title
        binding!!.availCouponTxt.text = marketOffersData!!.coupon_code
        binding?.offerValidityTxt?.text =
            getConvertedExpiryDateFormat(marketOffersData!!.expiry_date)
        if (marketOffersData?.extra_information != null) {
            var list: ArrayList<String> = arrayListOf()
            var listTerms: ArrayList<String> = arrayListOf()

            var extra = marketOffersData?.extra_information?.trim()
            extra = extra?.replace("S2coupon_code", marketOffersData!!.coupon_code)
            extra = extra?.replace("S1coupon_code", marketOffersData!!.coupon_code)
            extra = extra?.replace("S2discount_amt", "null")
            extra = extra!!.replace("S1discount_amt", "null")
            extra =
                extra.replace("S2from_date", getConvertedDateFormat(marketOffersData!!.createdon))
            extra =
                extra.replace(
                    "S2to_date",
                    getConvertedExpiryDateFormat(marketOffersData!!.expiry_date)
                )
            Log.v("marketOffersDataValue", " " + extra)
            var extraInfoTermsNCond = extra.trim().split("Terms and Conditions").toTypedArray()

            Log.v("extraInfoTermsNCond", " " + extraInfoTermsNCond.get(1))
            var termsContent = extraInfoTermsNCond.get(1).split("s2").toTypedArray()
            var offerContent = extraInfoTermsNCond.get(0).split("s1").toTypedArray()
            for (terms in termsContent) {
                if (terms.isNotEmpty()) {
                    Log.v(
                        "listTermsBoolean",
                        " " + terms.contains("S2coupon_code") + terms.contains("S2from_date") + marketOffersData!!.coupon_code
                    )

                    listTerms.add(terms)
                    Log.v("listTerms", " " + terms)
                }


            }
            updateTermsRecycler(listTerms)

            for (extraInfo in offerContent) {
                if (extraInfo.isNotEmpty() && !extraInfo.equals("") && extraInfo.length > 1) {
                    extraInfo.replace("@coupon_code", marketOffersData!!.coupon_code)
                    extraInfo.replace("@discount_amt", "null")
                    list.add(extraInfo)
                    Log.v("extraInfoList", " " + extraInfo)
                }


            }
            updateRecycler(list)
//            marketOfferDetailAdapter = MarketOfferDetailAdapter(list, this)
        }

        val ss = SpannableString(
            resources.getString(
                R.string.marketoffer_termSix,
                getConvertedDateFormat(marketOffersData!!.createdon),
                getConvertedExpiryDateFormat(marketOffersData!!.expiry_date)
            )
        )
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(this@MarketPlaceOffersActivity, "On Clicked ", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.common_text_color)
                ds.isUnderlineText = true
            }
        }

        if (intent.extras?.containsKey("showCartIcon")!!) {
            offer_info_icon.visibility = View.INVISIBLE
            avail_coupon_submit.visibility = View.GONE
        }

        if (marketOffersData!!.image != null && !marketOffersData!!.image!!.url.isNullOrEmpty()) {
            Glide.with(MarketPlaceOffersActivity()).load(marketOffersData!!.image!!.url)
                .into(binding?.marketoffersImage!!)
        } else {
            binding?.marketoffersImage?.setImageResource(R.drawable.rectangle_copy_18)
        }

        binding!!.packageBack.setOnClickListener {
            TODO("Not yet implemented")
        }

        binding!!.help.setOnClickListener {
            TODO("Not yet implemented")
        }

        binding!!.dateFromToLayout.setOnClickListener {
            val clipboard: ClipboardManager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(avail_coupon_txt.text, avail_coupon_txt.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                this,
                "Coupon " + avail_coupon_txt.text + " copied!!",
                Toast.LENGTH_SHORT
            )
                .show()

        }
        binding!!.availCouponSubmit.setOnClickListener {

            val clipboard: ClipboardManager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(avail_coupon_txt.text, avail_coupon_txt.text)
            clipboard.setPrimaryClip(clip)
            super.onBackPressed()
            Toast.makeText(
                this,
                "Coupon " + avail_coupon_txt.text + " copied!!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

//    private fun loadData() {
//      //  Log.d("marketOffersCoupon", " " + marketOffersData!!.coupon_code)
//        viewModel.getOffersByCouponId(marketOffersData!!.coupon_code)
//    }

    private fun initMvvm() {
        viewModel.marketOffersCouponResult().observe(this, androidx.lifecycle.Observer {
            Log.v("marketOffersCoupon", " " + it.coupon_code)
        })
    }

    fun getConvertedDateFormat(textDate: String): String {
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

    fun getConvertedExpiryDateFormat(textDate: String): String {
        Log.v("getConvertedExpir", " " + textDate)
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

    fun updateRecycler(list: List<String>) {
        Log.v("updateRecycler", " " + list)
        //   marketOfferDetailAdapter?.addupdates(list)
        recyclerOfferDetails.adapter = marketOfferDetailAdapter
        marketOfferDetailAdapter?.notifyDataSetChanged()
    }

    fun initializeDetailsRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recyclerOfferDetails?.apply {
            layoutManager = gridLayoutManager
        }
    }

    private fun initializeTermsRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recyclerTerms?.apply {
            layoutManager = gridLayoutManager
        }
    }

    fun updateTermsRecycler(list: List<String>) {
        Log.v("updateRecycler", " " + list)
        //  marketOfferTermsAdapter?.addupdates(list)
        recyclerTerms.adapter = marketOfferTermsAdapter
        marketOfferDetailAdapter?.notifyDataSetChanged()
    }

}


