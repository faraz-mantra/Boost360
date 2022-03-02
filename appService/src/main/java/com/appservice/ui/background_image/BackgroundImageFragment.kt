package com.appservice.ui.background_image

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.Constants
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.appservice.model.ImageData
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.bankaccount.BankAccountFragment
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.framework.analytics.SentryController
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.webengageconstant.APPOINTMENT_CATLOG_SETUP_PAGE_LOAD
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.squareup.picasso.Picasso
import java.util.ArrayList
import java.util.HashMap

class BackgroundImageFragment : AppBaseFragment<FragmentBackgroundImageBinding, BackgroundImageViewModel>(), RecyclerItemClickListener {


    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BackgroundImageFragment {
            val fragment = BackgroundImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_background_image
    }

    override fun getViewModelClass(): Class<BackgroundImageViewModel> {
        return BackgroundImageViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        sessionLocal = UserSessionManager(baseActivity)

        getBackgroundImages()
    }

    private fun getBackgroundImages() {

        showProgress()
        viewModel?.getImages(sessionLocal.fPID!!, clientId)?.observeOnce(viewLifecycleOwner) { res ->
            hideProgress()
            if (res.isSuccess()) {
                val response = res.arrayResponse
                val listImages = ArrayList<ImageData>()
                response?.forEach { listImages.add(ImageData(it as String)) }
                binding?.imageList?.apply {
                    val adapterImage = AppBaseRecyclerViewAdapter(baseActivity, listImages, this@BackgroundImageFragment)
                    adapter = adapterImage
                }


            }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.ON_CLICK_BACKGROUND_IMAGE.ordinal -> {

            }
        }

    }
}