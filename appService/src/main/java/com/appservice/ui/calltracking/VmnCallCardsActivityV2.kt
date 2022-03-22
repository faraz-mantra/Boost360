package com.appservice.ui.calltracking

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.base.addPlus91
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ActivityVmnCallCardsV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.VmnCallsViewModel
import com.framework.constants.PremiumCode
import com.framework.constants.SupportVideoType
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences
import com.framework.pref.clientId
import com.framework.utils.*
import com.framework.utils.ExoPlayerUtils.play
import com.framework.views.customViews.CustomToolbar
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.BUSINESS_CALLS
import com.framework.webengageconstant.EVENT_LABEL_BUSINESS_CALLS
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.onboarding.nowfloats.constant.IntentConstant

class VmnCallCardsActivityV2 : AppBaseActivity<ActivityVmnCallCardsV2Binding, VmnCallsViewModel>(), RecyclerItemClickListener, AppOnZeroCaseClicked {

  private var seeMoreLessStatus = false
  private var totalPotentialCallCount = 0
  private var stopApiCall = false
  private var headerList: ArrayList<VmnCallModel> = ArrayList()
  private var vmnCallAdapter: AppBaseRecyclerViewAdapter<VmnCallModel>? = null
  private var selectedViewType = "ALL"
  private var offset = 0
  private var appFragmentZeroCase: AppFragmentZeroCase? = null

  override fun getLayout(): Int {
    return R.layout.activity_vmn_call_cards_v2
  }

  override fun getViewModelClass(): Class<VmnCallsViewModel> {
    return VmnCallsViewModel::class.java
  }

  enum class CallType {
    CONNECTED,
    MISSED,
    ALL
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.toolbar
  }

  override fun getToolbarTitle(): String? {
    return getString(R.string.business_calls)
  }

  override fun onCreateView() {
    super.onCreateView()
    ExoPlayerUtils.newInstance() //required for call playback on list
    mangePlayerOnList()
    appFragmentZeroCase = AppRequestZeroCaseBuilder(AppZeroCases.BUSINESS_CALLS, this, this, isPremium).getRequest().build()
    addFragment(binding?.childContainer?.id, appFragmentZeroCase, false)
    WebEngageController.trackEvent(BUSINESS_CALLS, EVENT_LABEL_BUSINESS_CALLS, null)

    val phone = addPlus91(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER))
    binding?.tvTrackedCall?.text = spanColor(getString(R.string.tracked_calls) + " " + phone, R.color.colorPrimary, phone ?: "")
    setOnClickListener(binding?.seeMoreLess, binding?.websiteHelper, binding?.phoneHelper)
    //tracking calls
    showTrackedCalls()
    adapterCallView()
    websiteCallCount()
    checkPremium()
  }

  private fun adapterCallView() {
    val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    binding?.callRecycler?.layoutManager = linearLayoutManager
    binding?.callRecycler?.setHasFixedSize(true)
    vmnCallAdapter = AppBaseRecyclerViewAdapter(this, headerList)
    binding?.callRecycler?.adapter = vmnCallAdapter
    binding?.callRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        if (lastVisibleItem >= totalItemCount - 2 && !stopApiCall) calls()
      }
    })
  }

  private fun checkPremium() {
    if (isPremium) {
      nonEmptyView()
      binding?.callRecycler?.visible()
      binding?.secondLayout?.visible()
      calls()
    } else {
      binding?.callRecycler?.gone()
      binding?.secondLayout?.gone()
      emptyView()
    }
  }

  private fun mangePlayerOnList() {
    ExoPlayerUtils.onProgressChanged {
      val model = getCurrentPlayerModel()
      model?.audioPosition = ExoPlayerUtils.player.currentPosition
      model?.audioLength = ExoPlayerUtils.player.duration
      vmnCallAdapter?.notifyItemChanged(currentPlayerIndex(), Unit)
    }

    ExoPlayerUtils.isPlayingChanged = { isPlaying ->
      val model = getCurrentPlayerModel()
      model?.isAudioPlayState = isPlaying
      when (ExoPlayerUtils.player.playbackState) {
        Player.STATE_ENDED -> model?.audioPosition = 0L
      }
      vmnCallAdapter?.notifyItemChanged(currentPlayerIndex(), Unit)
    }

    ExoPlayerUtils.playBackStateChanged = { state -> if (state == ExoPlayer.STATE_BUFFERING) showProgress() else hideProgress() }
  }

  fun currentPlayerIndex(): Int {
    return ExoPlayerUtils.player.currentMediaItem?.mediaId?.toInt() ?: 0
  }

  fun getCurrentPlayerModel(): VmnCallModel? {
    val index = ExoPlayerUtils.player.currentMediaItem?.mediaId?.toInt()
    return if (index != null && vmnCallAdapter?.list?.size ?: 0 > index) (vmnCallAdapter?.list?.get(index) as VmnCallModel) else null
  }

  private val isPremium: Boolean
    get() {
      val keys = session.getStoreWidgets()
      return keys != null && keys.contains(PremiumCode.CALLTRACKER.value).not()
    }

  private fun showTrackedCalls() {
    binding?.parentLayout?.setOnTouchListener { _, _ -> infoCallHide();true }
    binding?.tableLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(tab: TabLayout.Tab) {
        if (tab.position == 0) {
          if (selectedViewType != "ALL") {
            selectedViewType = "ALL"
            updateRecyclerData(null)
          }
        } else if (tab.position == 1) {
          if (selectedViewType != "MISSED") {
            selectedViewType = "MISSED"
            updateRecyclerData(null)
          }
        } else if (tab.position == 2) {
          if (selectedViewType != "CONNECTED") {
            selectedViewType = "CONNECTED"
            updateRecyclerData(null)
          }
        }
        infoCallHide()
      }

      override fun onTabUnselected(tab: TabLayout.Tab) {}
      override fun onTabReselected(tab: TabLayout.Tab) {}
    })
  }

  fun calls() {
    Log.i(TAG, "getCalls: function called")
    stopApiCall = true
    showProgress()
    val startOffset = offset.toString()
    val hashMap = HashMap<String, String?>()
    hashMap["clientId"] = clientId
    hashMap["fpid"] = if (session.iSEnterprise.equals("true")) session.fPParentId else session.fPID
    hashMap["offset"] = startOffset
    hashMap["limit"] = 100.toString()
    hashMap["identifierType"] = if (session.iSEnterprise?.equals("true") == true) "MULTI" else "SINGLE"
    viewModel.trackerCalls(hashMap).observeOnce(this) {
      Log.i(TAG, "getCalls success: ")
      val vmnCallModels = (it.anyResponse as? ArrayList<VmnCallModel>)
      setTabCount(vmnCallModels)
      if (it.isSuccess() && vmnCallModels != null) {
        stopApiCall = false
        val size = vmnCallModels.size
        Log.v("getCalls", " $size")
        stopApiCall = size < 100
        if (size <= 0 && offset == 0) {
          emptyView()
          binding?.firstLayout?.gone()
          binding?.view2?.gone()
          binding?.secondLayout?.gone()
          binding?.callRecycler?.gone()
          appFragmentZeroCase?.setRootBG(R.color.grey_f9f9f9)
        } else {
          binding?.firstLayout?.visible()
          binding?.view2?.visible()
          binding?.secondLayout?.visible()
          binding?.callRecycler?.visible()
          nonEmptyView()
        }
        updateRecyclerData(vmnCallModels)
        if (size != 0) offset += 100
      } else showLongToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      hideProgress()
    }
  }

  private fun setTabCount(vmnCallModels: ArrayList<VmnCallModel>?) {
    binding?.tableLayout?.getTabAt(0)?.text = getString(R.string.all) + " (" + vmnCallModels?.size + ")"
    binding?.tableLayout?.getTabAt(1)?.text = getString(R.string.missed) + " (" + vmnCallModels?.filter { it.callStatus == CallType.MISSED.name }?.size + ")"
    binding?.tableLayout?.getTabAt(2)?.text = getString(R.string.received) + " (" + vmnCallModels?.filter { it.callStatus == CallType.CONNECTED.name }?.size + ")"

  }

  private fun updateRecyclerData(newItems: ArrayList<VmnCallModel>?) {
    ExoPlayerUtils.player.stop()
    if (newItems != null) {
      val sizeOfList = headerList.size
      val listSize = newItems.size
      for (i in 0 until listSize) {
        val model: VmnCallModel = newItems[i]
        headerList.add(model)
        vmnCallAdapter?.notifyItemInserted(sizeOfList + i)
      }
    }
    vmnCallAdapter = AppBaseRecyclerViewAdapter(this, getSelectedTypeList(headerList), this)
    binding?.callRecycler?.adapter = vmnCallAdapter
  }

  private fun getSelectedTypeList(list: ArrayList<VmnCallModel>): ArrayList<VmnCallModel> {
    var selectedItems: ArrayList<VmnCallModel> = ArrayList<VmnCallModel>()
    when (selectedViewType) {
      CallType.ALL.name -> selectedItems = list
      CallType.MISSED.name -> {
        var i = 0
        while (i < list.size) {
          if (list[i].callStatus.equals("MISSED")) selectedItems.add(list[i])
          i++
        }
      }
      CallType.CONNECTED.name -> {
        var i = 0
        while (i < list.size) {
          if (list[i].callStatus.equals("CONNECTED")) selectedItems.add(list[i])
          i++
        }
      }
    }
    return selectedItems
  }

  fun websiteCallCount() {
    showProgress()
    viewModel.getCallCountByType(session.fpTag, "POTENTIAL_CALLS", "WEB").observeOnce(this) {
      hideProgress()
      val jsonObject = it.anyResponse as? JsonObject
      if (it.isSuccess().not()) {
        showEmptyScreen()
        showSnackBarNegative(this, getString(R.string.something_went_wrong))
      } else {
        val callCount: String? = jsonObject?.get("POTENTIAL_CALLS")?.asString
        binding?.webCallCount!!.text = callCount
        totalPotentialCallCount += callCount?.toIntOrNull() ?: 0
        binding?.totalNumberOfCalls?.text = "View potential calls ($totalPotentialCallCount)"
        getPhoneCallCount()
      }
    }
  }

  private fun showEmptyScreen() {

  }

  private fun getPhoneCallCount() {
    showProgress()
    viewModel.getCallCountByType(session.fpTag, "POTENTIAL_CALLS", "MOBILE").observeOnce(this) {
      hideProgress()
      if (it.isSuccess().not()) {
        showEmptyScreen()
        showSnackBarNegative(this, getString(R.string.something_went_wrong))
      } else {
        val jsonObject = it.anyResponse as JsonObject
        val callCount: String = jsonObject.get("POTENTIAL_CALLS").asString
        binding?.phoneCallCount?.text = callCount
        totalPotentialCallCount += callCount.toInt()
        binding?.totalNumberOfCalls!!.text = "View potential calls ($totalPotentialCallCount)"
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.seeMoreLess -> {
        if (!seeMoreLessStatus) {
          seeMoreLessStatus = true
          binding?.seeMoreLessImage?.rotation = 90F
          binding?.helpWebPhoneLayout?.visibility = View.VISIBLE
        } else {
          seeMoreLessStatus = false
          binding?.seeMoreLessImage?.rotation = 270F
          binding?.helpWebPhoneLayout?.visibility = View.GONE
          infoCallHide()
        }
      }
      binding?.phoneHelper -> {
        binding?.helpPhoneInfo?.visibility = View.GONE
        if (binding?.helpWebsiteInfo?.visibility == View.VISIBLE) {
          binding?.helpWebsiteInfo?.visibility = View.GONE
        } else {
          binding?.helpWebsiteInfo?.visibility = View.VISIBLE
        }
      }
      binding?.websiteHelper -> {
        binding?.helpWebsiteInfo?.visibility = View.GONE
        if (binding?.helpPhoneInfo?.visibility == View.VISIBLE) {
          binding?.helpPhoneInfo?.visibility = View.GONE
        } else {
          binding?.helpPhoneInfo?.visibility = View.VISIBLE
        }
      }
    }
  }

  private fun infoCallHide() {
    if (binding?.helpPhoneInfo?.visibility == View.VISIBLE || binding?.helpWebsiteInfo?.visibility == View.VISIBLE) {
      binding?.helpPhoneInfo?.visibility = View.GONE
      binding?.helpWebsiteInfo?.visibility = View.GONE
    }
  }


  private fun nonEmptyView() {
    binding?.childContainer?.visibility = View.GONE
  }

  private fun emptyView() {
    binding?.childContainer?.visibility = View.VISIBLE
  }

  override fun primaryButtonClicked() {
    initiateBuyFromMarketplace("CALLTRACKER")
  }

  override fun secondaryButtonClicked() {
    try {
      startActivity(
        Intent(this, Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity"))
          .putExtra(IntentConstant.SUPPORT_VIDEO_TYPE.name, SupportVideoType.TOB.value)
      )
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    }
  }

  override fun ternaryButtonClicked() {}
  override fun appOnBackPressed() {}

  override fun onStop() {
    super.onStop()
    if (vmnCallAdapter?.itemCount ?: 0 > 1) {
      InAppReviewUtils.showInAppReview(this, InAppReviewUtils.Events.in_app_review_out_of_customer_calls)
    }
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      com.appservice.constant.RecyclerViewActionType.VMN_PLAY_CLICKED.ordinal -> {
        if ((ExoPlayerUtils.player.isLoading || ExoPlayerUtils.player.isPlaying) && position != ExoPlayerUtils.player.currentMediaItem?.mediaId?.toInt()) {
          ExoPlayerUtils.player.pause()
        }
        if (ExoPlayerUtils.player.isPlaying) {
          ExoPlayerUtils.player.pause()
        } else {
          val listItem = item as? VmnCallModel
          if (position != ExoPlayerUtils.player.currentMediaItem?.mediaId?.toInt() || getCurrentPlayerModel()?.audioPosition == 0L) {
            listItem?.callRecordingUri?.let { it1 -> play(it1, position, listItem.audioPosition) }
          } else ExoPlayerUtils.player.play()
        }
      }
    }
    infoCallHide()
  }

  override fun onDestroy() {
    super.onDestroy()
    ExoPlayerUtils.release()
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.pbLoading?.visible()
  }

  override fun hideProgress() {
    binding?.pbLoading?.gone()
  }
}