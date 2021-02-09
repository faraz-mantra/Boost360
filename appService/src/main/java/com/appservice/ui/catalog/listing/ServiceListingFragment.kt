package com.appservice.ui.catalog.listing

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
import com.appservice.databinding.FragmentServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.CatalogServiceContainerActivity
import com.appservice.ui.model.ItemsItem
import com.appservice.ui.model.ResultItem
import com.appservice.ui.model.ServiceListingRequest
import com.appservice.ui.model.ServiceListingResponse
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import java.util.*
import kotlin.collections.ArrayList

class ServiceListingFragment : AppBaseFragment<FragmentServiceListingBinding, ServiceViewModel>(), RecyclerItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var searchView: SearchView
    private var list: ArrayList<ItemsItem> = arrayListOf()
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


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_service_listing, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.isVisible = list.isNullOrEmpty().not()
        this.searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Product"
        searchView.setOnQueryTextListener(this)
        searchView.clearFocus()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setServiceListing() {
        showProgress(resources.getString(R.string.loading))
        viewModel?.getServiceListing(ServiceListingRequest(arrayListOf(), "ALL", UserSession.fpTag))?.observeOnce(lifecycleOwner = viewLifecycleOwner, Observer {
            hideProgress()
            when (it.isSuccess()) {
                true -> {
                    val map = (it as ServiceListingResponse).result?.map { resultItem -> (resultItem as ResultItem).services?.items }
                    if (map.isNullOrEmpty().not()) {
                        this.list = ArrayList<ItemsItem>()
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

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}