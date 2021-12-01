package com.festive.poster.ui.festivePoster

import android.util.Log
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentPosterPackListingBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList
import com.framework.webengageconstant.GET_FESTIVAL_POSTER_PACK_CLICK
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class PosterPackListingFragment : AppBaseFragment<FragmentPosterPackListingBinding, FestivePosterViewModel>(), RecyclerItemClickListener {

  private val TAG = "PosterPackListingFragme"
  private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>? = null
  private var sharedViewModel: FestivePosterSharedViewModel? = null
  var dataList: ArrayList<PosterPackModel>? = null
  private var session: UserSessionManager? = null

  companion object {
    @JvmStatic
    fun newInstance(): PosterPackListingFragment {
      return PosterPackListingFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_poster_pack_listing
  }

  override fun getViewModelClass(): Class<FestivePosterViewModel> {
    return FestivePosterViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    ViewCompat.setNestedScrollingEnabled(binding?.rvPosters!!, false);
    Log.i(TAG, "onCreateView: ")
    session = UserSessionManager(requireActivity())
    sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
    setObserver()

  }

  private fun setObserver() {
    sharedViewModel?.keyValueSaved?.observe(viewLifecycleOwner,{
      Log.i(TAG, "keyvaluesaved called: ")
        getTemplateViewConfig()
    })

  }


  private fun getTemplateViewConfig() {
    sharedViewModel?.posterPackLoadListener?.value = false
    showShimmerAnimation()
    viewModel?.getTemplateConfig(session?.fPID, session?.fpTag)
      ?.observeOnce(viewLifecycleOwner, {
        Log.i(TAG, "template config: ${Gson().toJson(it)}")
          val response = it as? GetTemplateViewConfigResponse
          response?.let {
            val tagArray = prepareTagForApi(response.Result.templatePacks.tags)
            fetchTemplates(tagArray, response)
        }

      })
  }

  private fun fetchTemplates(tagArray: ArrayList<String>, response: GetTemplateViewConfigResponse) {
    viewModel?.getTemplates(session?.fPID, session?.fpTag, tagArray)
      ?.observeOnce(viewLifecycleOwner, {
        dataList = ArrayList()
        val templates_response = it as? GetTemplatesResponse
        templates_response?.let {
          response.Result.templatePacks.tags.forEach { pack_tag ->
            val templateList = ArrayList<PosterModel>()
            templates_response.Result.templates.forEach { template ->
              var posterTag = template.tags.find { posterTag -> posterTag == pack_tag.tag }
              if ( posterTag != null && template.active) {
                template.greeting_message = pack_tag.description
                templateList.add(template.clone()!!)
              }
            }
            dataList?.add(PosterPackModel(pack_tag, templateList.toArrayList(),isPurchased = pack_tag.isPurchased,list_layout = RecyclerViewItemType.POSTER_PACK.getLayout()))

          }
          getPriceOfPosterPacks()
        }
      })
  }

  private fun getPriceOfPosterPacks() {
    viewModel?.getUpgradeData()?.observeOnce(viewLifecycleOwner, {
      val response = it as? UpgradeGetDataResponse
      response?.let {
        dataList?.forEach { pack ->
          val feature_festive = response.Data.firstOrNull()?.features?.find { feature ->
            feature.feature_code == pack.tagsModel.tag
          }
          pack.price = feature_festive?.price ?: 0.0

          Log.i(TAG, "festive price: ${feature_festive?.price}")
        }

        sharedViewModel?.posterPackLoadListener?.value = true

         // rearrangeList()
          adapter = AppBaseRecyclerViewAdapter(baseActivity, dataList!!, this)
          binding?.rvPosters?.adapter = adapter
          binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())


        hideShimmerAnimation()
      }
    })
  }

  private fun rearrangeList() {
    dataList?.sortBy { it.tagsModel.tag }
  }

  private fun getGreetingMessages(tag: String): String {
    return when(tag) {
      "DURGAPUJA2021" -> "May Maa Durga strengthen you to fight all evils, " +
              "may she give you the courage to face all upheavals.\n" +
              "Happy Durga Puja.\n"
      "DUSSEHRA2021" -> "May the Lord always bless you with wisdom and good health. May Goddess Durga " +
              "shower her choicest wishes over you and remove all evil obstacles in your life. Happy Dussehra!"
      "NAVRATRI2021" -> "Wishing you and your family a very Happy Navratri. --" +
              " May the nine days of Navratri light up your lives.\n"
      "DIWALI2021" -> "Hoping this Diwali brings health, wealth, and happiness to you & your family. Happy Diwali.\n"
      else -> ""
    }
  }

  /*private fun checkPurchasedOrNot() {
    viewModel?.getFeatureDetails(session?.fPID, clientId)?.observe(viewLifecycleOwner, {
      val featureList = it.arrayResponse as? Array<GetFeatureDetailsItem>
      featureList?.let {
        dataList?.forEach { posterPackModel ->
          posterPackModel.isPurchased = it.find { feature -> feature.featureKey == posterPackModel.tagsModel.tag }?.featureState == 1
          Log.i(TAG, "checkPurchasedOrNot: ${posterPackModel.isPurchased}")
        }
      }
    })
  }*/


  private fun prepareTagForApi(tags: List<PosterPackTagModel>): ArrayList<String> {
    val list = ArrayList<String>()
    tags.forEach {
      list.add(it.tag)
    }
    return list
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.GET_POSTER_PACK_CLICK.ordinal -> {
        WebEngageController.trackEvent(GET_FESTIVAL_POSTER_PACK_CLICK, event_value = HashMap())
        item as PosterPackModel
        sharedViewModel?.selectedPosterPack = item

        if (item.isPurchased){
          //sharedViewModel?.keyValueSaved?.value=null
          addFragment(R.id.container,
              PosterListFragment.newInstance(item.tagsModel.tag!!), true,true)
        }else{
          CustomizePosterSheet.newInstance(item.tagsModel.tag, item.isPurchased)
              .show(baseActivity.supportFragmentManager, CustomizePosterSheet::class.java.name)
        }
      }
    }
  }


  override fun onChildClick(childPosition: Int, parentPosition: Int, childItem: BaseRecyclerViewItem?, parentItem: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal -> {
        WebEngageController.trackEvent(GET_FESTIVAL_POSTER_PACK_CLICK, event_value = HashMap())
        parentItem as PosterPackModel
        sharedViewModel?.selectedPosterPack = parentItem
        sharedViewModel?.selectedPoster = childItem as PosterModel
        CustomizePosterSheet.newInstance(parentItem.tagsModel.tag, parentItem.isPurchased)
            .show(requireActivity().supportFragmentManager, CustomizePosterSheet::class.java.name)
      }
    }
  }

  fun showShimmerAnimation(){
    binding?.shimmerLayout?.visible()
    binding?.listLayout?.gone()
  }

  fun hideShimmerAnimation(){
    binding?.shimmerLayout?.gone()
    binding?.listLayout?.visible()
  }

  override fun onResume() {
    super.onResume()
    binding?.shimmerLayout?.startShimmer()

  }

  override fun onPause() {
    super.onPause()
    binding?.shimmerLayout?.stopShimmer()
  }
}
