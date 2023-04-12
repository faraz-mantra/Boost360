package com.boost.marketplace.ui.details.dictate_services

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.KeyboardUtils.onGlobalLayout
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityDictateServicesBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.popup.dictate.SuccessPreferencesBottomSheet
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.layout_acceptance.*
import kotlinx.android.synthetic.main.layout_target_customers.*
import kotlinx.android.synthetic.main.layout_target_customers.btn1
import kotlinx.android.synthetic.main.layout_target_customers.img1
import kotlinx.android.synthetic.main.layout_target_customers.img2
import kotlinx.android.synthetic.main.layout_word_count.*
import kotlinx.android.synthetic.main.view_select_number.*
import kotlinx.android.synthetic.main.view_select_website.*


class DictateServicesActivity :
    AppBaseActivity<ActivityDictateServicesBinding, ComparePacksViewModel>(),CompareListener, AddonsListener
{

    private var tagList = ArrayList<String>()
    private var tagList1 = ArrayList<String>()
    var bundleData: Bundles? = null
    var singleAddon: FeaturesModel? = null
    val sameAddonsInCart = java.util.ArrayList<String>()
    val addonsListInCart = java.util.ArrayList<String>()
    lateinit var prefs: SharedPrefs
    var cartList: List<CartModel>? = null
    var fpName: String? = null
    var itemInCart = false
    var fpid: String? = null
    var fpTag: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    var isDeepLink: Boolean = false
    var experienceCode: String? = null
    var isOpenCardFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = java.util.ArrayList<String>()
    var screenType: String? = null
    private var widgetFeatureCode: String? = null
    var accountType: String? = null
    var dictateSelectionForPack = false
    var dictateSelectionForCart = false
    var itemInCartStatus = false


    override fun getLayout(): Int {
        return com.boost.marketplace.R.layout.activity_dictate_services
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            WindowInsetsControllerCompat(window, window.decorView).setAppearanceLightStatusBars(false)
            window.statusBarColor = ResourcesCompat.getColor(resources, com.boost.cart.R.color.common_text_color, null)
        }

        itemInCartStatus = intent.getBooleanExtra("itemInCartStatus",false)
        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7
        accountType = intent.getStringExtra("accountType")
        fpid = intent.getStringExtra("fpid")
        fpTag = intent.getStringExtra("fpTag")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        fpName = intent.getStringExtra("fpName")
        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7
        experienceCode = intent.getStringExtra("expCode")
        screenType = intent.getStringExtra("screenType")
        fpName = intent.getStringExtra("fpName")
        fpid = intent.getStringExtra("fpid")
        fpTag = intent.getStringExtra("fpTag")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        widgetFeatureCode = intent.getStringExtra("buyItemKey")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)
        prefs =SharedPrefs(this)
        singleAddon = Gson().fromJson<FeaturesModel>(
            intent.getStringExtra("addonData"),
            object : TypeToken<FeaturesModel>() {}.type
        )
        bundleData = Gson().fromJson<Bundles>(
            intent.getStringExtra("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )
        dictateSelectionForPack = intent.getBooleanExtra("dictateSelectionForPack", false)
        dictateSelectionForCart = intent.getBooleanExtra("dictateSelectionForCart", false)

        loadData()
        initMvvm()

       // tagList = product?.tags ?: ArrayList()

        binding?.addonsBack?.setOnClickListener{
            super.onBackPressed()
        }

        binding?.layoutCustomers?.setOnClickListener {
            dialogCustomers()
        }

        binding?.layoutCount?.setOnClickListener {
            dialogCount()
        }

        binding?.layoutHashtag?.setOnClickListener {
            dialogAcceptance()
        }

        binding?.layoutAddress?.setOnClickListener {
            dialogAcceptance2()
        }

        binding?.layoutLogo?.setOnClickListener {
            dialogAcceptance3()
        }

        binding?.mainBtn1?.setOnClickListener {
            if (dictateSelectionForPack){
                addToCart()
            } else if(dictateSelectionForCart){
                finish()
            }
            else{
                val dialogCard = SuccessPreferencesBottomSheet()
                val args = Bundle()
                dialogCard.arguments = args
                dialogCard.show(
                    this.supportFragmentManager,
                    SuccessPreferencesBottomSheet::class.java.name
                )
            }
        }

        binding?.btnAddTag?.setOnClickListener {
            val txtTag = binding?.edtProductTag?.text.toString()
            if (txtTag.isNotEmpty()) {
                tagList.add(txtTag)
                serviceTagsSet()
                binding?.edtProductTag?.setText("")
            }
        }

        loadData1()
    }

    fun ViewGroup.addViewObserver(function: () -> Unit) {
        val view = this
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                function.invoke()
            }
        })
    }

    //this has to be checked half implemented for cta to be enabled aftr filling mandatory fields. this is pending.
    private fun loadData1() {
        if ((!prefs.getSelectedDictatePrefs().isNullOrEmpty()) && (!prefs.getSelectedDictateKeywordsPrefs().isNullOrEmpty())){
            binding!!.mainBtn1.background = ContextCompat.getDrawable(
                applicationContext,
                com.boost.marketplace.R.drawable.orange_button_click_effect
            )
            binding!!.mainBtn1.setTextColor(getResources().getColor(com.boost.marketplace.R.color.primaryDark))
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val edit = v
                val outR = Rect()
                edit.getGlobalVisibleRect(outR)
                val isKeyboardOpen: Boolean = !outR.contains(ev.rawX.toInt(), ev.rawY.toInt())
                print("Is Keyboard? $isKeyboardOpen")
                if (isKeyboardOpen) {
                    print("Entro al IF")
                    edit.clearFocus()
                    val imm: InputMethodManager =
                        this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edit.windowToken, 0)
                }
                edit.isCursorVisible = !isKeyboardOpen
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun dialogCustomers(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_target_customers)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.btn_add_tag1.setOnClickListener {
            val txtTag = dialog.edt_places_tag?.text.toString()
            if (txtTag.isNotEmpty()) {
                tagList1.add(txtTag)
                dialog.chipsPlaces?.removeAllViews()

                tagList1.forEach { tag ->

                    val mChip: Chip = this.layoutInflater.inflate(com.boost.marketplace.R.layout.items_chip, binding?.chipsTags, false) as Chip
                    mChip.text = tag
                    mChip.isClickable=true
                    mChip.isCheckable=false
                    if (tagList1.size>0){
                        dialog.chipsPlaces?.visibility=View.VISIBLE
                    }else{
                        dialog.chipsPlaces?.visibility=View.GONE
                    }

                    mChip.setOnCloseIconClickListener {
                        dialog.chipsPlaces?.removeView(mChip)
                        tagList1.remove(tag)
                    }
                    dialog.chipsPlaces?.addView(mChip)
                }
                dialog.edt_places_tag?.setText("")

            }
        }



        dialog.radio_button1?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            prefs.storeSelectedDictatePrefs("Mandatory")
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "Mandatory"
            val text="In my city only "
            val radio = "radio1"
            doneButton(dialog,Values,text,radio)
        }

        dialog.radio_button2?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "Mandatory"
            val text="In my state only "
            val radio = "radio2"
            doneButton(dialog,Values,text,radio)
        }

        dialog.radio_button3?.setOnClickListener {
            dialog.hide_layut.visibility=View.GONE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "Mandatory"
            val text="PAN India "
            val radio = "radio3"
            doneButton(dialog,Values,text,radio)
        }

        dialog.radio_button4?.setOnClickListener {
            dialog.hide_layut.visibility=View.VISIBLE
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img3.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            dialog.img4.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            var Values = "Mandatory"
            val num = tagList1.toString().replace("[", "").replace("]", "")
            val text=num
            val radio = "radio4"
            doneButton(dialog,Values,text,radio)
        }

    }

    private fun serviceTagsSet1() {
        TODO("Not yet implemented")
    }

    fun dialogCount(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_word_count)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
        dialog.radio_button5.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="100 - 240 characters "
            val radio = "radio5"
            doneButton(dialog,Values,text,radio)
        }
        dialog.radio_button6.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="240 - 500 characters "
            val radio = "radio6"
            doneButton(dialog,Values,text,radio)
        }
    }

    private fun dialogAcceptance() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_acceptance)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.radio_button7.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="Yes "
            val radio = "radio7"
            doneButton(dialog,Values,text,radio)
        }
        dialog.radio_button8.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="No "
            val radio = "radio8"
            doneButton(dialog,Values,text,radio)
        }
    }

    private fun dialogAcceptance2() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_acceptance)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.radio_button7.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="Yes "
            val radio = "radio9"
            doneButton(dialog,Values,text,radio)
        }
        dialog.radio_button8.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="No "
            val radio = "radio10"
            doneButton(dialog,Values,text,radio)
        }
    }

    private fun dialogAcceptance3() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(com.boost.marketplace.R.layout.layout_acceptance)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        dialog.radio_button7.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="Yes "
            val radio = "radio11"
            doneButton(dialog,Values,text,radio)
        }
        dialog.radio_button8.setOnClickListener {
            dialog.btn1.isEnabled=true
            dialog.btn1.isClickable=true
            dialog.img2.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_orange)
            dialog.img1.setImageResource(com.boost.marketplace.R.drawable.ic_radio_buttons_grey)
            var Values = "optional"
            val text="No "
            val radio = "radio12"
            doneButton(dialog,Values,text,radio)
        }
    }

    private fun doneButton(dialog: Dialog,value:String,text:String,radio:String) {
        dialog.btn1?.setOnClickListener {
            prefs.storeSelectedDictatePrefs(value)
            if (radio.equals("radio5")||radio.equals("radio6")){
                binding?.etCount?.text=text
            } else if (radio.equals("radio1")||radio.equals("radio2")||radio.equals("radio3")){
                binding?.etEmail?.text=text
            } else if(radio.equals("radio4")){
                val num = tagList1.toString().replace("[", "").replace("]", "")
                binding?.etEmail?.text=num
            } else if (radio.equals("radio7")||radio.equals("radio8")){
                binding?.etHashtag?.text=text
            } else if (radio.equals("radio9")||radio.equals("radio10")){
                binding?.etAddress?.text=text
            }else if (radio.equals("radio11")||radio.equals("radio12")){
                binding?.etLogo?.text=text
            }
            dialog.btn1.background = ContextCompat.getDrawable(
                applicationContext,
                com.boost.marketplace.R.drawable.grey_button_click_effect)
            dialog.btn1.setTextColor(getResources().getColor(com.boost.marketplace.R.color.tv_color_BB))
            dialog.btn1.text = "Done"
            Handler().postDelayed({
                dialog.dismiss()
            }, 600)
            loadData1()
        }
    }

    private fun serviceTagsSet() {
        binding?.chipsTags?.removeAllViews()

        tagList.forEach { tag ->

              val mChip: Chip = this.layoutInflater.inflate(com.boost.marketplace.R.layout.items_chip, binding?.chipsTags, false) as Chip
              mChip.text = tag
            mChip.isClickable=true
            mChip.isCheckable=false
            if (tagList.size>0){
                binding?.chipsTags?.visibility=View.VISIBLE
                prefs.storeSelectedDictateKeywordsPrefs("mandatory")
            }else{
                binding?.chipsTags?.visibility=View.GONE
                binding?.chipsTags?.layout(0,0,0,0)
            }

            mChip.setOnCloseIconClickListener {
                binding?.chipsTags?.removeView(mChip)
                tagList.remove(tag)
            }
            binding?.chipsTags?.addView(mChip)
        }
    }

    private fun loadData() {
        try {
            val pref = this?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
            viewModel.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            cartList = it
        })

    }

    private fun addToCart() {
        if (bundleData != null) {
            prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in bundleData!!.included_features) {
                itemIds.add(i.feature_code)
            }

            CompositeDisposable().add(
                AppDatabase.getInstance(Application())!!
                    .featuresDao()
                    .getallFeaturesInList(itemIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            if (cartList != null) {
                                //same features available in cart
                                for (singleItem in cartList!!) {
                                    for (singleFeature in it) {
                                        if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                            sameAddonsInCart.add(singleFeature.name!!)
                                            addonsListInCart.add(singleItem.item_id)
                                        }
                                    }
                                    //if there is any other bundle available remove it
                                    if (singleItem.item_type.equals("bundles")) {
                                        addonsListInCart.add(singleItem.item_id)
                                    }
                                }
                            }

                            if (sameAddonsInCart.size > 0) {
                                val removeFeatureBottomSheet =
                                    RemoveFeatureBottomSheet(this, this, null)
                                val args = Bundle()
                                args.putStringArrayList("addonNames", sameAddonsInCart)
                                args.putStringArrayList("addonsListInCart", addonsListInCart)
                                args.putString("packageDetails", Gson().toJson(bundleData))
                                removeFeatureBottomSheet.arguments = args
                                fragmentManager?.let { it1 ->
                                    removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name
                                    )
                                }
                                finish()
                            } else {
                                var bundleMonthlyMRP = 0.0
                                val minMonth: Int =
                                    if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                for (singleItem in it) {
                                    for (item in bundleData!!.included_features) {
                                        if (singleItem.feature_code == item.feature_code) {
                                            bundleMonthlyMRP += RootUtil.round(
                                                singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                2
                                            )
                                        }
                                    }
                                }
                                offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                                originalBundlePrice = (bundleMonthlyMRP * minMonth)

                                if (bundleData!!.overall_discount_percent > 0)
                                    offeredBundlePrice = RootUtil.round(
                                        originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100),
                                        2
                                    )
                                else
                                    offeredBundlePrice = originalBundlePrice

                                //clear cartOrderInfo from SharedPref to requestAPI again
                                prefs.storeCartOrderInfo(null)

                                //remove other bundle and add existing bundle to cart
                                removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundleData!!, offeredBundlePrice, originalBundlePrice)

                                val intent = Intent(
                                    this,
                                    CartActivity::class.java
                                )
                                intent.putExtra("fpid", fpid)
                                intent.putExtra("expCode", experienceCode)
                                intent.putExtra("isDeepLink", isDeepLink)
                                intent.putExtra("deepLinkViewType", deepLinkViewType)
                                intent.putExtra("deepLinkDay", deepLinkDay)
                                intent.putExtra("isOpenCardFragment", isOpenCardFragment)
                                intent.putExtra(
                                    "accountType",
                                    accountType
                                )
                                intent.putStringArrayListExtra(
                                    "userPurchsedWidgets",
                                    userPurchsedWidgets
                                )
                                if (email != null) {
                                    intent.putExtra("email", email)
                                } else {
                                    intent.putExtra("email", "ria@nowfloats.com")
                                }
                                if (mobileNo != null) {
                                    intent.putExtra("mobileNo", mobileNo)
                                } else {
                                    intent.putExtra("mobileNo", "9160004303")
                                }
                                intent.putExtra("profileUrl", profileUrl)
                                startActivity(intent)
                                finish()
                            }
                        },
                        {
                            it.printStackTrace()
                        }
                    )
            )
        } else {
            if(itemInCartStatus == false) {
                prefs.storeCartOrderInfo(null)
                singleAddon?.let { viewModel?.addItemToCart1(it,this,null) }
                val event_attributes: HashMap<String, Any> = HashMap()
                singleAddon!!.name?.let { it1 ->
                    event_attributes.put(
                        "Addon Name",
                        it1
                    )
                }
                event_attributes.put("Addon Price", singleAddon!!.price)
                event_attributes.put(
                    "Addon Discounted Price",
                    getDiscountedPrice(
                        singleAddon!!.price,
                        singleAddon!!.discount_percent
                    )
                )
                event_attributes.put(
                    "Addon Discount %",
                    singleAddon!!.discount_percent
                )
                event_attributes.put("Addon Validity", 1)
                event_attributes.put(
                    "Addon Feature Key",
                    singleAddon!!.boost_widget_key
                )
                singleAddon!!.target_business_usecase?.let { it1 ->
                    event_attributes.put(
                        "Addon Tag",
                        it1
                    )
                }
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
                    ADDONS_MARKETPLACE,
                    event_attributes
                )
                itemInCartStatus = true
                viewModel?.getCartItems()
            }

            val pref = this?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("fpid", fpid)
            intent.putExtra("fpTag", fpTag)
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra("accountType", accountType)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            if (email != null) {
                intent.putExtra("email", email)
            } else {
                intent.putExtra("email", "ria@nowfloats.com")
            }
            if (mobileNo != null) {
                intent.putExtra("mobileNo", mobileNo)
            } else {
                intent.putExtra("mobileNo", "9160004303")
            }
            intent.putExtra("profileUrl", profileUrl)
            startActivity(intent)
            }
    }

    override fun onAddonsClicked(item: FeaturesModel) {
    }

    override fun onRefreshCart() {
        viewModel.getCartItems()
    }

    private fun getDiscountedPrice(price: Double, discountPercent: Double): Double {
        return price - ((discountPercent / 100) * price)
    }

    override fun onPackageClicked(item: Bundles?, image: ImageView?) {
        val itemIds = arrayListOf<String>()
        for (i in item?.included_features!!) {
            itemIds.add(i.feature_code)
        }
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getallFeaturesInList(itemIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        var bundleMonthlyMRP = 0.0
                        val minMonth: Int =
                            if (item.min_purchase_months != null && item.min_purchase_months!! > 1) item.min_purchase_months!! else 1

                        for (singleItem in it) {
                            for (items in item.included_features) {
                                if (singleItem.feature_code == items.feature_code) {
                                    bundleMonthlyMRP += RootUtil.round(
                                        singleItem.price - ((singleItem.price * items.feature_price_discount_percent) / 100.0),
                                        2
                                    )
                                }
                            }
                        }

                        offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                        originalBundlePrice = (bundleMonthlyMRP * minMonth)

                        if (item.overall_discount_percent > 0)
                            offeredBundlePrice = RootUtil.round(
                                originalBundlePrice - (originalBundlePrice * item.overall_discount_percent / 100),
                                2
                            )
                        else
                            offeredBundlePrice = originalBundlePrice

                        //clear cartOrderInfo from SharedPref to requestAPI again
                        prefs.storeCartOrderInfo(null)
                        viewModel.addItemToCartPackage1(
                            CartModel(
                                item._kid,
                                null,
                                null,
                                item.name,
                                "",
                                item.primary_image!!.url,
                                offeredBundlePrice.toDouble(),
                                originalBundlePrice.toDouble(),
                                item.overall_discount_percent,
                                1,
                                if (item.min_purchase_months != null) item.min_purchase_months!! else 1,
                                "bundles",
                                null,
                                ""
                            )
                        )
                    },
                    {
                        it.printStackTrace()

                    }
                )
        )
        viewModel.getCartItems()
    }

    fun removeOtherBundlesAndAddExistingBundle(addonsListInCart: List<String>, bundle: Bundles, offerBundlePrice: Double, originalBundlePrice: Double ){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
               // binding?.packageImg?.let { it1 -> makeFlyAnimation(it1) }
                viewModel.addItemToCartPackage1(
                    CartModel(
                        bundle._kid,
                        null,
                        null,
                        bundle.name,
                        "",
                        bundle.primary_image!!.url,
                        offerBundlePrice,
                        originalBundlePrice,
                        bundle.overall_discount_percent,
                        1,
                        if (!prefs.getYearPricing() && bundle.min_purchase_months != null) bundle.min_purchase_months!! else 1,
                        "bundles",
                        null,
                        ""
                    )
                )
                viewModel.getCartItems()
            }
            .doOnError {
                Toast.makeText(this, "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                viewModel.getCartItems()
            }
            .subscribe()
    }


}