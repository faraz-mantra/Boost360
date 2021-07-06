package com.appservice.ui.testimonial.newflow.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentTestimonialListBinding
import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.testimonial.newflow.base.BaseTestimonialFragment
import com.appservice.ui.testimonial.newflow.base.startTestimonialFragmentActivity
import com.appservice.ui.testimonial.newflow.model.DataItem
import com.appservice.ui.testimonial.newflow.model.DeleteTestimonialRequestNew
import com.appservice.ui.testimonial.newflow.model.TestimonialListingRequest
import com.appservice.ui.testimonial.newflow.model.TestimonialListingResponse
import com.framework.extensions.observeOnce
import java.util.*

class TestimonialListFragment : BaseTestimonialFragment<FragmentTestimonialListBinding>(),
  RecyclerItemClickListener {
  private val list: ArrayList<DataItem> = arrayListOf()
  private val finalList: ArrayList<DataItem> =
    arrayListOf()
  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PaginationScrollListener.PAGE_START
  private var limit: Int = PaginationScrollListener.PAGE_SIZE
  private var adapterTestimonial: AppBaseRecyclerViewAdapter<DataItem>? =
    null
  private var isLastPageD = false
  private var isEdit: Boolean = false
  private var headerToken = "59c89bbb5d64370a04c9aea1"
  private var testimonialType = "testimonials"
  private val allTestimonialType = listOf("testimonials", "testimonial", "guestreviews")
  private var layoutManagerN: LinearLayoutManager? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): TestimonialListFragment {
      val fragment = TestimonialListFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    //limit: Int?,offset:Int?,fpTag:String?,fpId:String
    layoutManagerN = LinearLayoutManager(baseActivity)
    getListTestimonialFilterApi(isFirst = true)
    layoutManagerN?.let { scrollPagingListener(it) }
    setOnClickListener(binding?.testimonialEmpty?.cbAddTestimonial)

  }


  private fun getListTestimonialFilterApi(
    isFirst: Boolean = false,
    offSet: Int? = null,
    limit: Int? = null
  ) {
    if (isFirst) showProgress()
    viewModel?.getTestimonialListing(TestimonialListingRequest(fpTag, limit, fpId, offSet))
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          setTestimonialDataItem((it as? TestimonialListingResponse)?.result, isFirst)
        } else if (isFirst) showShortToast(it.message())
        if (isFirst) hideProgress()
      })
  }

  private fun setTestimonialDataItem(
    result: com.appservice.ui.testimonial.newflow.model.Result?,
    isFirstLoad: Boolean
  ) {
    val listTestimonial =
      result?.data as? ArrayList<DataItem>
    onTestimonialAddedOrUpdated(listTestimonial?.size ?: 0)
    if (isFirstLoad) finalList.clear()
    if (listTestimonial.isNullOrEmpty().not()) {
      removeLoader()
      setEmptyView(View.GONE)
      TOTAL_ELEMENTS = result?.paging?.count ?: 0
      finalList.addAll(listTestimonial!!)
      list.clear()
      list.addAll(finalList)
      isLastPageD = (finalList.size == TOTAL_ELEMENTS)
      setAdapterNotify()
      setToolbarTitle("${resources.getString(R.string.testimonial)} (${TOTAL_ELEMENTS})")
    } else if (isFirstLoad) setEmptyView(View.VISIBLE)
    if (listTestimonial.isNullOrEmpty().not()) {
      list.clear()
      list.addAll(listTestimonial!!)
      setAdapterNotify()
    }
  }

  private fun setEmptyView(visibility: Int) {
    binding?.testimonialEmpty?.root?.visibility = visibility
    if (visibility == View.VISIBLE) setListingView(View.GONE) else setListingView(View.VISIBLE)
    setEmptyView()
  }

  private fun setListingView(visibility: Int) {
    binding?.rvTestimonial?.visibility = visibility
  }

  private fun setEmptyView() {
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.rvTestimonial?.addOnScrollListener(object :
      PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          adapterTestimonial?.addLoadingFooter(
            DataItem().getLoaderItem()
          )
          offSet += limit
          getListTestimonialFilterApi(offSet = offSet, limit = limit)
        }
      }

      override val totalPageCount: Int
        get() = TOTAL_ELEMENTS
      override val isLastPage: Boolean
        get() = isLastPageD
      override val isLoading: Boolean
        get() = isLoadingD
    })
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  private fun onTestimonialAddedOrUpdated(count: Int) {
  }

  private fun setAdapterNotify() {
    if (adapterTestimonial == null) {
      adapterTestimonial =
        AppBaseRecyclerViewAdapter(baseActivity, list, this@TestimonialListFragment)
      binding?.rvTestimonial?.layoutManager = layoutManagerN
      binding?.rvTestimonial?.adapter = adapterTestimonial
      adapterTestimonial?.runLayoutAnimation(binding?.rvTestimonial)
    } else adapterTestimonial?.notifyDataSetChanged()
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterTestimonial?.removeLoadingFooter()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add -> {
        val bundle: Bundle = Bundle.EMPTY
        startTestimonialFragmentActivity(
          FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT,
          bundle,
          clearTop = false,
          isResult = true
        )
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.TESTIMONIAL_ITEM_CLICK.ordinal -> {
        startAddEditActivity(item as? DataItem)

      }
      RecyclerViewActionType.DELETE_TESTIMONIAL.ordinal -> {
        deleteTestimonial((item as? DataItem)?.testimonialId)
      }
      RecyclerViewActionType.EDIT_TESTIMONIAL.ordinal -> {
        startAddEditActivity(item as? DataItem)

      }
    }

  }

  private fun deleteTestimonial(testimonialId: String?) {
    showProgress()
    viewModel?.deleteTestimonial(DeleteTestimonialRequestNew(testimonialId))?.observeOnce(viewLifecycleOwner,{
      hideProgress()
      if (it.isSuccess()){
        showShortToast("Testimonial deleted")
        getListTestimonialFilterApi()
      }else{
        showShortToast("Unable to delete testimonial.")
      }
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.testimonialEmpty?.cbAddTestimonial -> {
        startTestimonialFragmentActivity(FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) {
        this.offSet = PaginationScrollListener.PAGE_START
        this.limit = PaginationScrollListener.PAGE_SIZE + 1
        getListTestimonialFilterApi(isFirst = true, offSet = offSet, limit = limit)
      }
    }
  }

  private fun startAddEditActivity(dataItem: DataItem?) {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.TESTIMONIAL_DATA.name, dataItem)
    startTestimonialFragmentActivity(
      FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT,
      bundle,
      isResult = true
    )
  }
}