package com.boost.marketplace.ui.popup.removeItems

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.boost.cart.utils.SharedPrefs
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.remove_feature_bottomsheet.*


class RemoveFeatureBottomSheet(val homeListener: CompareListener, val listener: AddonsListener, val image: ImageView?) : DialogFragment() {
    lateinit var root: View
    lateinit var prefs: SharedPrefs
    var addonsListInCart = arrayListOf<String>()
    lateinit var packageDetails: Bundles

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.remove_feature_bottomsheet, container, false)
        prefs = SharedPrefs(requireActivity())
        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addonsListInCart = arguments!!.getStringArrayList("addonsListInCart")?: arrayListOf()
        packageDetails = Gson().fromJson(arguments!!.getString("packageDetails"),
            object : TypeToken<Bundles>() {}.type)
        close_btn.setOnClickListener {
            dismiss()
        }
        yes_button.setOnClickListener {
            if(addonsListInCart.size>0)
                Completable.fromAction {
                    AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        homeListener.onPackageClicked(packageDetails, image)
                        listener.onRefreshCart()
                        dismiss()
                    }
                    .doOnError {
                        Toast.makeText(requireContext(), "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                        listener.onRefreshCart()
                        dismiss()
                    }
                    .subscribe()
        }
        no_button.setOnClickListener {
                dismiss()
        }

    }

}