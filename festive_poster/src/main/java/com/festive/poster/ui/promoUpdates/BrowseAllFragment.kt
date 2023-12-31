package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentBrowseAllBinding
import com.festive.poster.models.*
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.TemplateDiffUtil
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.posterPostClicked
import com.festive.poster.utils.posterWhatsappShareClicked
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.pref.UserSessionManager
import com.framework.rest.NetworkResult
import com.framework.utils.showToast
import com.framework.utils.toArrayList
import com.framework.webengageconstant.*

class BrowseAllFragment : AppBaseFragment<FragmentBrowseAllBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {

  private var promoUpdatesViewModel: PromoUpdatesViewModel? = null
  private var session: UserSessionManager? = null
  private var selectedPos: Int = 0
  private val DEFAULT_SELECTED_POS = 0
  private var argTag: String? = null
  private var posterRvAdapter: AppBaseRecyclerViewAdapter<BrowseAllTemplate>? = null
  private var categoryAdapter: AppBaseRecyclerViewAdapter<BrowseAllCategory>? = null
  var categoryList = ArrayList<BrowseAllCategory>()

  override fun getLayout(): Int {
    return R.layout.fragment_browse_all
  }

  override fun getViewModelClass(): Class<PostUpdatesViewModel> {
    return PostUpdatesViewModel::class.java
  }

  companion object {
    val BK_SELECTED_POS_TAG = "BK_SELECTED_POS"
    fun newInstance(selectedPosTag: String? = null): BrowseAllFragment {
      val bundle = Bundle()
      bundle.putString(BK_SELECTED_POS_TAG, selectedPosTag)
      val fragment = BrowseAllFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(Promotional_Update_Browse_All_Loaded)
    argTag = arguments?.getString(BK_SELECTED_POS_TAG)
    promoUpdatesViewModel = ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
    initUi()
    getTemplatesData()
  }

  private fun initUi() {
    binding.rvCat.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
    binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
    categoryAdapter = AppBaseRecyclerViewAdapter(
      requireActivity() as BaseActivity<*, *>, categoryList, this
    )
    binding.rvCat.adapter = categoryAdapter

    posterRvAdapter = AppBaseRecyclerViewAdapter(
      requireActivity() as BaseActivity<*, *>, ArrayList(), this
    )
    binding.rvPosters.adapter = posterRvAdapter
    binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
  }


  private fun getTemplatesData() {
    promoUpdatesViewModel?.browseAllLData?.observe(viewLifecycleOwner) {
      when (it) {
        is NetworkResult.Loading -> {
          if (posterRvAdapter?.isEmpty() == true) showProgress()
        }
        is NetworkResult.Success -> {
          hideProgress()
          val data = it.data ?: return@observe

          findDefaultSelectedPos(data)
          if (categoryList.isEmpty()) {
            setCatgories(data)
            switchToSelectedItem(selectedPos, data[selectedPos].getParentTemplates()?.asBrowseAllModels())
          } else {
            switchToSelectedItem(selectedPos, data[selectedPos].getParentTemplates()?.asBrowseAllModels())
            setCatgories(data)
          }
        }
        is NetworkResult.Error -> {
          hideProgress()
          showToast(it.msg)
        }
      }

    }
  }

  private fun findDefaultSelectedPos(data: java.util.ArrayList<CategoryUi>) {
    selectedPos = if (argTag == null) {
      DEFAULT_SELECTED_POS
    } else {
      data.indexOfFirst { it.id == argTag }
    }
  }

  override fun onResume() {
    super.onResume()
    session = UserSessionManager(requireActivity())


  }


  private fun setCatgories(it: java.util.ArrayList<CategoryUi>) {
    categoryList.clear()
    categoryList.addAll(it.asBrowseAllModels().toArrayList())
    selectCategory()
  }


  private fun switchToSelectedItem(positon: Int, newList: List<BrowseAllTemplate>?) {
    if (newList == null) return
    selectedPos = positon
    selectCategory()
    posterRvAdapter?.setUpUsingDiffUtil(newList)
    WebEngageController.trackEvent(PROMOTIONAL_UPDATE_CATEGORY_LOADED, NO_EVENT_LABLE, NO_EVENT_VALUE)
  }

  private fun selectCategory() {
    val selectedItem = categoryList[selectedPos]
    categoryList.forEach { it.isSelected = false }
    selectedItem.isSelected = true
    categoryAdapter?.notifyDataSetChanged()
    binding.tvCatTitle.text = selectedItem.name
    binding.tvCatSize.text = selectedItem.templates?.size.toString()
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BROWSE_ALL_POSTER_CAT_CLICKED.ordinal -> {
        WebEngageController.trackEvent(PROMOTIONAL_UPDATE_CATEGORY_CLICK, CLICK, CLICKED)
        switchToSelectedItem(position, categoryList[position].templates)
      }
      RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal -> {
        (item as? TemplateUi)?.let { posterWhatsappShareClicked(it, baseActivity) }
      }
      RecyclerViewActionType.POST_CLICKED.ordinal -> {
        (item as? TemplateUi)?.let { posterPostClicked(it, requireActivity() as AppBaseActivity<*, *>) }
      }

      RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal -> {
        (item as? TemplateUi)?.let {
          WebEngageController.trackEvent(if(it.isFavourite) UPDATE_STUDIO_UNMARK_FAVOURITE_CLICK else UPDATE_STUDIO_MARK_FAVOURITE_CLICK, CLICK, CLICKED)
          callFavApi(it)
        }
      }
    }


  }


  private fun callFavApi(posterModel: TemplateUi) {
    promoUpdatesViewModel?.markAsFav(posterModel.isFavourite.not(), posterModel.id)
    promoUpdatesViewModel?.favStatus?.observe(viewLifecycleOwner) {
      when (it) {
        is NetworkResult.Loading -> {
          showProgress()
        }
        else -> {
          hideProgress()
        }
      }
    }

  }

  private fun AppBaseRecyclerViewAdapter<BrowseAllTemplate>.setUpUsingDiffUtil(newList: List<BrowseAllTemplate>) {
    val templateDiffUtil = TemplateDiffUtil(this.list, newList)
    val diffResult = DiffUtil.calculateDiff(templateDiffUtil)
    this.list.clear()
    this.list.addAll(newList)
    diffResult.dispatchUpdatesTo(this)
  }
}