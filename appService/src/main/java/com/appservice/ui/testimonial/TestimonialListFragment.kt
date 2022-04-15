package com.appservice.ui.testimonial

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentTestimonialListBinding
import com.appservice.model.testimonial.ListTestimonialRequest
import com.appservice.model.testimonial.response.TestimonialData
import com.appservice.model.testimonial.response.TestimonialListResponse
import com.appservice.model.testimonial.response.TestimonialResult
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.utils.WebEngageController
import com.framework.constants.SupportVideoType
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.*

class TestimonialListFragment : BaseTestimonialFragment<FragmentTestimonialListBinding>(), AppOnZeroCaseClicked, RecyclerItemClickListener {

  private var fragmentZeroCase: AppFragmentZeroCase? = null
  private val finalList: ArrayList<TestimonialData> = arrayListOf()
  private var adapterTestimonial: AppBaseRecyclerViewAdapter<TestimonialData>? = null
  private var layoutManagerN: LinearLayoutManager? = null

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PaginationScrollListener.PAGE_START
  private var limit: Int = PaginationScrollListener.PAGE_SIZE
  private var isLastPageD = false

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
    setOnClickListener(binding?.addTestimonial)
    layoutManagerN = LinearLayoutManager(baseActivity)
    WebEngageController.trackEvent(TESTIMONIAL_PAGE_VIEW, PAGE_VIEW, sessionLocal.fpTag)
    this.fragmentZeroCase = AppRequestZeroCaseBuilder(AppZeroCases.TESTIMONIAL, this, baseActivity).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, fragmentZeroCase, false)
    loadData(isFirst = true, offSet = offSet, limit = limit)
    layoutManagerN?.let { scrollPagingListener(it) }
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.rvTestimonial?.addOnScrollListener(object :
      PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          adapterTestimonial?.addLoadingFooter(TestimonialData().getLoaderItem())
          offSet += limit
          loadData(offSet = offSet, limit = limit)
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

  private fun loadData(isFirst: Boolean = false, offSet: Int? = null, limit: Int? = null) {
    if (isFirst) showProgress()
    viewModel?.getTestimonialList(ListTestimonialRequest(sessionLocal.fPID, sessionLocal.fpTag, limit, offSet))?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        val response = it as? TestimonialListResponse
        setDataTestimonial(response?.result, isFirst)
      } else if (isFirst) showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      hideProgress()
    }
  }

  private fun setDataTestimonial(result: TestimonialResult?, isFirstLoad: Boolean) {
    val listTestimonial = result?.data as? ArrayList<TestimonialData>
    if (isFirstLoad) finalList.clear()
    if (listTestimonial.isNullOrEmpty().not()) {
      removeLoader()
      setEmptyView(View.GONE)
      finalList.addAll(listTestimonial!!)
      TOTAL_ELEMENTS = finalList.size
      isLastPageD = false
      setAdapterNotify()
      setToolbarTitle("${resources.getString(if (TOTAL_ELEMENTS > 1) R.string.testimonial_title else R.string.testimonial_)} ${if (TOTAL_ELEMENTS > 0) "(${TOTAL_ELEMENTS})" else ""}")
    } else {
      if (isFirstLoad) setEmptyView(View.VISIBLE) else {
        removeLoader()
        isLastPageD = true
      }
    }
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterTestimonial?.removeLoadingFooter()
    }
  }

  private fun setAdapterNotify() {
    if (adapterTestimonial == null) {
      binding?.rvTestimonial?.apply {
        adapterTestimonial = AppBaseRecyclerViewAdapter(baseActivity, finalList, this@TestimonialListFragment)
        layoutManager = layoutManagerN
        adapter = adapterTestimonial
      }
    } else adapterTestimonial?.notifyDataSetChanged()
  }

  private fun setEmptyView(visibility: Int) {
    when (visibility) {
      View.GONE -> {
        binding?.rvTestimonial?.visible()
        binding?.addTestimonial?.visible()
        binding?.childContainer?.gone()
      }
      View.VISIBLE -> {
        binding?.rvTestimonial?.gone()
        binding?.addTestimonial?.gone()
        binding?.childContainer?.visible()
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.addTestimonial -> addTestimonial()
    }
  }

  private fun addTestimonial() {
    WebEngageController.trackEvent(TESTIMONIAL_ADD_CLICK, CLICK, sessionLocal.fpTag)
    startTestimonialFragmentActivity(FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT, isResult = true)
  }

  override fun primaryButtonClicked() {
    addTestimonial()
  }

  override fun secondaryButtonClicked() {
    WebEngageController.trackEvent(TESTIMONIAL_ADD_CLICK, CLICK, sessionLocal.fpTag)
   baseActivity.startHelpSupportVideoActivity(SupportVideoType.TESTIMONIALS.value)
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.EDIT_TESTIMONIAL_CLICK.ordinal -> {
        val data = item as? TestimonialData ?: return
        val b = Bundle().apply { putSerializable(IntentConstant.OBJECT_DATA.name, data) }
        startTestimonialFragmentActivity(FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT, bundle = b, isResult = true)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) {
        this.offSet = PaginationScrollListener.PAGE_START
        this.limit = PaginationScrollListener.PAGE_SIZE
        isLastPageD = false
        loadData(isFirst = true, offSet = offSet, limit = limit)
      }
    }
  }

  fun Context.startHelpSupportVideoActivity(supportType: String) {
    try {
      val i = Intent(this, Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity"))
      i.putExtra(com.onboarding.nowfloats.constant.IntentConstant.SUPPORT_VIDEO_TYPE.name, supportType)
      this.startActivity(i)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}