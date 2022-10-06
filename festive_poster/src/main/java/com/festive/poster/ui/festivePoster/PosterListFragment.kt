package com.festive.poster.ui.festivePoster

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentPosterListBinding
import com.festive.poster.databinding.SheetEditShareMessageBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.response.GetFestivePosterResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.changeColorOfSubstring
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList
import com.framework.webengageconstant.FESTIVAL_POSTER_PURCHASED_GALLERY
import com.google.android.material.bottomsheet.BottomSheetDialog

class PosterListFragment : AppBaseFragment<FragmentPosterListBinding, FestivePosterViewModel>(), RecyclerItemClickListener {

  private var adapter: AppBaseRecyclerViewAdapter<PosterModel>? = null
  private var sharedViewModel: FestivePosterSharedViewModel? = null
  private val TAG = "PosterListFragment"
  private var selectedPosterModelForDownload:PosterModel?=null
  private var selectedPositionForEdit:Int=-1

  private val RC_STORAGE_PERMISSION=200

  companion object {
    val BK_TAG = "BK_TAG"

    @JvmStatic
    fun newInstance(tag: String): PosterListFragment {
      val bundle = Bundle().apply {
        putString(BK_TAG, tag)
      }
      val fragment = PosterListFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  var packTag: String? = null
  private var session: UserSessionManager? = null
  private var dataList: ArrayList<PosterModel>? = null
  override fun getLayout(): Int {
    return R.layout.fragment_poster_list
  }

  override fun getViewModelClass(): Class<FestivePosterViewModel> {
    return FestivePosterViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    packTag = arguments?.getString(BK_TAG)
    if (packTag==null){
      return
    }
    WebEngageController.trackEvent(FESTIVAL_POSTER_PURCHASED_GALLERY, event_value = HashMap())
    sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
    session = UserSessionManager(requireActivity())
    observeCustomization()
  }

  private fun observeCustomization() {
    sharedViewModel?.keyValueSaved?.observe(viewLifecycleOwner,{

      Log.i(TAG, "observeCustomization: ")

      setupList()


    })
    /*sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner, {
      Log.i(TAG, "observeCustomization: ${Gson().toJson(it)}")


      dataList?.forEach { template ->
        template.greeting_message=it.greetingMessage
        template.keys.forEach { posterKeyModel ->
          when (posterKeyModel.name) {
            "user_name" -> {
              posterKeyModel.custom = it.name
            }
            "business_website" -> {
              posterKeyModel.custom = it.website
            }
            "business_email" -> {
              posterKeyModel.custom = it.email
            }
            "business_name" -> {
              posterKeyModel.custom = session?.fPName
            }
            "user_contact" -> {
              posterKeyModel.custom = it.whatsapp
            }
            "user_image" -> {
              posterKeyModel.custom = it.imgPath
            }
            "business_logo" -> {
              posterKeyModel.custom = session?.fPLogo
            }

          }

        }
      }
      adapter?.notifyDataSetChanged()
    })*/
  }

  private fun setupList() {
    showShimmerAnimation()
    viewModel?.getTemplates(
      session?.fPID,
      session?.fpTag,
      arrayListOf(packTag!!)
    )?.observeOnce(viewLifecycleOwner, {
        val response = it as? GetFestivePosterResponse
        response?.let {
          dataList = response.Result.templates?.toArrayList()
          dataList?.forEach { posterModel -> posterModel.isPurchased = true
            posterModel.greeting_message = sharedViewModel?.selectedPosterPack?.tagsModel?.description
            posterModel.layout_id = RecyclerViewItemType.POSTER_SHARE.getLayout()

          }
          dataList?.let {
            adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>, it, this)
            binding?.rvPosters?.adapter = adapter
            binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
            adapter?.notifyDataSetChanged()
          }

        }
        hideShimmerAnimation()
    })


  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal -> {
        sharedViewModel?.selectedPoster = item as PosterModel
/*        CustomizePosterSheet.newInstance(packTag!!, true,
          PosterListFragment::class.java.name,
          (item as PosterModel).id).show(requireActivity().supportFragmentManager,
          CustomizePosterSheet::class.java.name)*/

        CustomizePosterSheet.newInstance(
            packTag!!, true,
            PosterListFragment::class.java.name
        ).show(requireActivity().supportFragmentManager,
          CustomizePosterSheet::class.java.name)

      }
      RecyclerViewActionType.POSTER_GREETING_MSG_CLICKED.ordinal -> {
        item as PosterModel
        showEditGreetingMessageSheet(position)

      }
      RecyclerViewActionType.POSTER_DOWNLOAD_CLICKED.ordinal->{
        selectedPosterModelForDownload = item as PosterModel
        checkStoragePermission()
      }
    }
  }

  private fun checkStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),RC_STORAGE_PERMISSION)
      }else{
        downloadSelectedPoster()
      }
    } else {
      downloadSelectedPoster()
    }

  }

  private fun downloadSelectedPoster() {
    SvgUtils.shareUncompressedSvg(
      selectedPosterModelForDownload?.variants?.firstOrNull()?.svgUrl,
      selectedPosterModelForDownload)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode==RC_STORAGE_PERMISSION&&grantResults.isNotEmpty()){
      if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
        downloadSelectedPoster()
      else
        Toast.makeText(requireActivity(), "We need access of storage to save the image", Toast.LENGTH_SHORT).show()
    }
  }

  private fun showEditGreetingMessageSheet(position: Int) {
    val sheet = BottomSheetDialog(requireActivity(), R.style.BottomSheetTheme)
    val sheetBinding = DataBindingUtil.inflate<SheetEditShareMessageBinding>(layoutInflater, R.layout.sheet_edit_share_message, null, false)
    sheet.setContentView(sheetBinding.root)

    changeColorOfSubstring(R.string.greeting_description, R.color.colorAccent, "*", sheetBinding?.customTextView!!)

    sheetBinding.etDesc.setText(dataList?.get(position)?.greeting_message)
    sheetBinding.tvUpdateInfo.setOnClickListener {
      dataList?.get(position)?.greeting_message = sheetBinding.etDesc.text.toString()
      adapter?.notifyItemChanged(position)
      sheet.dismiss()
    }
    sheet.show()
  }

  fun showShimmerAnimation(){
    binding?.shimmerLayout?.visible()
    binding?.rvPosters?.gone()
  }

  fun hideShimmerAnimation(){
    binding?.shimmerLayout?.gone()
    binding?.rvPosters?.visible()
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