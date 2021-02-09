package com.appservice.ui.catalog.listing

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.CatalogServiceContainerActivity
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.model.ItemsItem
import com.appservice.ui.model.ResultItem
import com.appservice.ui.model.ServiceListingRequest
import com.appservice.ui.model.ServiceListingResponse
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.recycler_item_service_timing.*
import java.util.*
import kotlin.collections.ArrayList

class ServiceListingFragment : AppBaseFragment<FragmentServiceListingBinding, ServiceViewModel>(), RecyclerItemClickListener {
    // For sharing
    private val STORAGE_CODE = 120
    var targetMap: Target? = null
    var defaultShareGlobal = true
    var shareType = 2
    var shareProduct: ItemsItem? = null

    private lateinit var searchView: SearchView
    private var list: ArrayList<ItemsItem> = arrayListOf()
    private var copylist: ArrayList<ItemsItem> = arrayListOf()
    private lateinit var adapter: AppBaseRecyclerViewAdapter<ItemsItem>

    override fun getLayout(): Int {
        return R.layout.fragment_service_listing
    }

    companion object {
        fun newInstance(): ServiceListingFragment {
            return ServiceListingFragment()
        }
    }

    override fun getViewModelClass(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setServiceListing()
        setHasOptionsMenu(true)

        setOnClickListener(binding?.cbSortAndFilter, binding?.cbAddService)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_service_listing, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
            searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
            searchAutoComplete?.setTextColor(getColor(R.color.white))
            searchView.setIconifiedByDefault(true)
            searchView.queryHint = getString(R.string.search_services)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { startFilter(it.trim().toLowerCase(Locale.ROOT)) }
                    return false
                }

            })
        }
    }

    private fun startFilter(query: String) {
        copylist.removeAll(list)
        copylist.addAll(list)
        val searchList = ArrayList<ItemsItem>()
        if (query.isNotEmpty() || query.isNotBlank()) {
            list.forEach { if (it.name?.toLowerCase(Locale.ROOT)?.contains(query) == true) searchList.add(it) }
            adapter.notify(searchList)
        } else {
            adapter.notify(copylist)
        }
    }

    private fun setServiceListing() {
        showProgress(resources.getString(R.string.loading))
        viewModel?.getServiceListing(ServiceListingRequest(arrayListOf(), "ALL", UserSession.fpTag))?.observeOnce(lifecycleOwner = viewLifecycleOwner, Observer {
            hideProgress()
            when (it.isSuccess()) {
                true -> {
                    val map = (it as ServiceListingResponse).result?.map { resultItem -> (resultItem as ResultItem).services?.items }
                    if (map.isNullOrEmpty().not()) {
                        map?.forEach { list1 -> list1?.forEach { itemsItem -> list.add(itemsItem!!) } }
                        (requireActivity() as CatalogServiceContainerActivity).getToolbar()?.title = "${resources.getString(R.string.services)} (${list.size})"
                        setEmptyView(View.GONE)
                        this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = list, itemClickListener = this@ServiceListingFragment)
                        binding!!.baseRecyclerView.adapter = adapter
                    } else {
                        setEmptyView(View.VISIBLE)
                    }

                }
                else -> {
                    setEmptyView(View.VISIBLE)
                }
            }
        })
    }

    private fun setListingView(visibility: Int) {
        binding?.baseRecyclerView?.visibility = visibility
        binding?.llActionButtons?.visibility = visibility
    }

    private fun setEmptyView(visibility: Int) {
        binding?.serviceListingEmpty?.root?.visibility = visibility
        setEmptyView()
        if (visibility == View.VISIBLE) setListingView(View.GONE) else setListingView(View.VISIBLE)
    }

    private fun setEmptyView() {
        val spannableString = SpannableString(resources.getString(R.string.you_don_t_have_any_service_added_to_your_digital_catalog_as_of_yet_watch_video))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showShortToast("video link")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.black_4a4a4a)), spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(UnderlineSpan(), spannableString.length.minus(11), spannableString.length, 0)
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.text = spannableString
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.movementMethod = LinkMovementMethod.getInstance()
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.highlightColor = resources.getColor(android.R.color.transparent)

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        if (actionType == RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) {
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, item as ItemsItem)
            startFragmentActivity(FragmentType.SERVICE_DETAIL_VIEW, bundle, false, isResult = true)
        }

        if (actionType == RecyclerViewActionType.SERVICE_WHATS_APP_SHARE.ordinal) {
        }
        if (actionType == RecyclerViewActionType.SERVICE_DATA_SHARE_CLICK.ordinal) {
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.cbAddService -> {
                startFragmentActivity(FragmentType.SERVICE_DETAIL_VIEW, isResult = true)
            }
            binding?.cbSortAndFilter -> {
                openSortingBottomSheet()
            }
        }
    }

    private fun openSortingBottomSheet() {

    }
}

