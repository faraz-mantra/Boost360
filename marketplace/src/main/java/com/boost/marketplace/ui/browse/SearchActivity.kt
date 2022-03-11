package com.boost.marketplace.ui.browse

import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.CompareItemAdapter
import com.boost.marketplace.R
import com.boost.marketplace.adapter.PackageRecyclerAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivitySearchBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.popup.PackagePopUpFragement
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.FEATURE_PACKS_CLICKED
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppBaseActivity<ActivitySearchBinding, SearchViewModel>(), HomeListener,AddonsListener {

    lateinit var featureAdaptor: CompareItemAdapter
    lateinit var packageAdaptor: PackageRecyclerAdapter
    lateinit var progressDialog: ProgressDialog

    var allFeatures: ArrayList<FeaturesModel> = arrayListOf()
    var allBundles: ArrayList<Bundles> = arrayListOf()
    var userPurchsedWidgets = ArrayList<String>()

    override fun getLayout(): Int {
        return R.layout.activity_search
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()
        featureAdaptor = CompareItemAdapter(allFeatures,this)
        progressDialog = ProgressDialog(this)
        packageAdaptor = PackageRecyclerAdapter(allBundles, this, this)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        initView()
        initMvvm()

    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        viewModel.setApplicationLifecycle(application, this)
        viewModel.loadAllPackagesFromDB()
        viewModel.loadAddonsFromDB()

        back_arrow.setOnClickListener {
            finish()
        }

        clear_text.setOnClickListener {
            search_value.setText("")
        }

        val tempString = SpannableString("Pack(s) having add-on")
        tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
        package_title.setText(tempString)

        search_value.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!=null && p0.length>3){
                    val tempString = SpannableString("Pack(s) having add-on named `"+p0.toString()+"`")
                    tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                    package_title.setText(tempString)
                    updateAllItemBySearchValue(p0.toString())
                } else{
                    val tempString = SpannableString("Pack(s) having add-on")
                    tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                    package_title.setText(tempString)
                    updateRecyclerViewItems(allFeatures,allBundles)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun initMvvm() {
        viewModel.addonsResult().observe(this, Observer {
            if(it!=null) {
                allFeatures = it as ArrayList<FeaturesModel>
                updateRecyclerViewItems(allFeatures, null)
            }
        })
        viewModel.bundleResult().observe(this, Observer {

            for(item in it){
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                allBundles.add(Bundles(
                    item.bundle_id,
                    temp,
                    item.min_purchase_months,
                    item.name,
                    item.overall_discount_percent,
                    PrimaryImage(item.primary_image),
                    item.target_business_usecase,
                    Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                    null,item.desc
                ))
                Log.e("loop",Gson().toJson(allBundles))
            }
            updateRecyclerViewItems(null, allBundles)
        })
        viewModel.addonsError().observe(this, Observer {

        })
        viewModel.addonsLoader().observe(this, Observer {
            if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
    }

    fun updateAllItemBySearchValue(searchValue: String){
        var searchFeatures: ArrayList<FeaturesModel> = arrayListOf()
        var searchBundles: ArrayList<Bundles> = arrayListOf()

        for(singleFeature in allFeatures){
            if(singleFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                searchFeatures.add(singleFeature)
            }
        }
        for(singleBundle in allBundles){
            var addedState = false
            for(singleFeatureBundle in singleBundle.included_features){
                for(singleFeature in allFeatures){
                    if((singleFeatureBundle.feature_code.equals(singleFeature.boost_widget_key) && singleFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ) ||(singleBundle.name?.lowercase()?.indexOf(searchValue.lowercase())!=-1)){
                        searchBundles.add(singleBundle)
                        addedState = true
                        break
                    }
                }
                //skip bundle loop if already added
                if(addedState){
                    break
                }
            }
        }
        updateRecyclerViewItems(searchFeatures, searchBundles)
    }

    fun updateRecyclerViewItems(featuresList: ArrayList<FeaturesModel>?, bundleList: ArrayList<Bundles>?){
        if(featuresList!=null) {
            if (featuresList.size > 0) {
                featureAdaptor.addupdates(featuresList, 1)
                feature_recyclerview.adapter = featureAdaptor
                featureAdaptor.notifyDataSetChanged()
                feature_recyclerview.visibility = View.VISIBLE
                addons_title.visibility = View.VISIBLE
            } else {
                feature_recyclerview.visibility = View.GONE
                addons_title.visibility = View.GONE
            }
        }
        if(bundleList != null) {
            if (bundleList.size > 0) {
                packageAdaptor.addupdates(allBundles)
                package_recyclerview.adapter = packageAdaptor
                packageAdaptor.notifyDataSetChanged()
                package_recyclerview.visibility = View.VISIBLE
                package_title.visibility = View.VISIBLE
            } else {
                package_recyclerview.visibility = View.GONE
                package_title.visibility = View.GONE
            }
        }
    }

    override fun onPackageClicked(item: Bundles?) {

        val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
        item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
        item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }

        event_attributes.put("Discount %", item!!.overall_discount_percent)
        event_attributes.put("Package Identifier", item!!._kid)
        item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, ADDONS_MARKETPLACE, event_attributes)

        val packagePopup = PackagePopUpFragement()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packagePopup.arguments = args
        packagePopup.show(supportFragmentManager,"PACKAGE_POPUP")

//        val intent = Intent(applicationContext, ComparePacksActivity::class.java)
//        intent.putExtra("bundleData", Gson().toJson(item))
//        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
//        startActivity(intent)



    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
        //not in use
    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
        //not in use
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {
        //not in use
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
        //not in use
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        //not in use
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
        //not in use
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        //not in use
    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
        //not in use
    }

    override fun onAddonsClicked(item: FeaturesModel) {
        TODO("Not yet implemented")
    }
}