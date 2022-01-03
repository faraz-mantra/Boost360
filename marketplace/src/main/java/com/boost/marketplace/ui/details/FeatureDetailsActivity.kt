package com.boost.marketplace.ui.details

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import com.boost.dbcenterapi.data.remote.ApiInterface
import com.boost.dbcenterapi.infra.api.models.test.StringData
import com.boost.dbcenterapi.recycleritem.*
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.adapter.ReviewViewPagerAdapter
import com.boost.marketplace.adapter.SecondaryImagesAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityFeatureDetailsBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerStringAdapter
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.infra.utils.Constants.Companion.IMAGE_PREVIEW_POPUP_FRAGMENT
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.popup.ImagePreviewPopUpFragement
import retrofit2.Retrofit

class FeatureDetailsActivity :
    AppBaseActivity<ActivityFeatureDetailsBinding, FeatureDetailsViewModel>(),
    RecyclerStringItemClickListener, DetailsFragmentListener {

    private var featureDetailsAdapter: AppBaseRecyclerStringAdapter<StringData>? = null

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    //  lateinit var localStorage: LocalStorage
    var singleWidgetKey: String? = null
    var badgeNumber = 0
    var addonDetails: FeaturesModel? = null
    var cart_list: List<CartModel>? = null
    var itemInCartStatus = false
    var widgetLearnMoreLink: String? = null

    lateinit var progressDialog: ProgressDialog

    lateinit var reviewAdaptor: ReviewViewPagerAdapter
    lateinit var secondaryImagesAdapter: SecondaryImagesAdapter

    val imagePreviewPopUpFragement = ImagePreviewPopUpFragement()

    lateinit var prefs: SharedPrefs

    override fun getLayout(): Int {
        return R.layout.activity_feature_details
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.addItemToCart)
        initializeFeatureDetailsAdapter()
        initView()
        initMvvm()
    }

    private fun initView() {
        progressDialog = ProgressDialog(this)
        secondaryImagesAdapter = SecondaryImagesAdapter(ArrayList(), this)
        reviewAdaptor = ReviewViewPagerAdapter(ArrayList())
//    localStorage = LocalStorage.getInstance(requireContext())!!
        singleWidgetKey = intent.extras?.getString("itemId")
        prefs = SharedPrefs(this)
    }

    private fun initMvvm() {
    viewModel
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.addItemToCart -> {
                val dialog = FeaturesDetailsDialog()
                dialog.show(this.supportFragmentManager, "FeatureDialog")
            }
        }
    }


    fun initializeFeatureDetailsAdapter() {
        binding?.packRecycler?.apply {
            featureDetailsAdapter =
                AppBaseRecyclerStringAdapter(this@FeatureDetailsActivity,
                    StringData(arrayListOf("","",""), RecyclerStringItemType.STRING_LIST.ordinal),this@FeatureDetailsActivity )
            adapter = featureDetailsAdapter
        }
    }

    override fun onStringItemClick(position: Int, item: String?, actionType: Int) {

    }

    override fun imagePreviewPosition(list: ArrayList<String>, pos: Int) {
        val args = Bundle()
        args.putInt("position", pos)
        args.putStringArrayList("list", list)
        imagePreviewPopUpFragement.arguments = args
        imagePreviewPopUpFragement.show(supportFragmentManager, IMAGE_PREVIEW_POPUP_FRAGMENT)
    }

}