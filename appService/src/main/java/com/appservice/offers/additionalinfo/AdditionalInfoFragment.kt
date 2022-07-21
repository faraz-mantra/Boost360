package com.appservice.offers.additionalinfo

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentOfferAdditionalInfoBinding
import com.appservice.model.FileModel
import com.appservice.model.servicev1.ImageModel
import com.appservice.offers.models.OfferModel
import com.appservice.offers.viewmodel.OfferViewModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.framework.imagepicker.ImagePicker
import com.google.android.material.chip.Chip

class AdditionalInfoFragment : AppBaseFragment<FragmentOfferAdditionalInfoBinding, OfferViewModel>(), RecyclerItemClickListener {
  private var tagList = ArrayList<String>()
  private var offerModel: OfferModel? = null
  private var isEdit: Boolean = false
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null

  private var secondaryDataImage: ArrayList<ImageModel>? = null
  private var secondaryImage: ArrayList<FileModel> = ArrayList()
  override fun getLayout(): Int {
    return R.layout.fragment_offer_additional_info
  }

  override fun getViewModelClass(): Class<OfferViewModel> {
    return OfferViewModel::class.java
  }

  companion object {
    fun newInstance(): AdditionalInfoFragment {
      return AdditionalInfoFragment()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnConfirm, binding?.btnClickPhoto, binding?.btnAddTag)
    this.offerModel = arguments?.getSerializable(IntentConstant.OFFER_DATA.name) as? OfferModel
    isEdit = (offerModel != null && offerModel?.offerId.isNullOrEmpty().not())
    secondaryImage = (arguments?.getSerializable(IntentConstant.OFFER_SECONDARY_IMAGE.name) as? ArrayList<FileModel>)
      ?: ArrayList()
    tagList = offerModel?.tags ?: ArrayList()
    if (isEdit) {
      secondaryDataImage = offerModel?.secondaryImages as ArrayList<ImageModel>
      if (secondaryImage.isNullOrEmpty()) secondaryDataImage?.forEach { secondaryImage.add(FileModel(pathUrl = it.ActualImage)) }
    }
    offersTags()
    setAdapter()

  }

  private fun offersTags() {
    binding?.chipsOffers?.removeAllViews()
    tagList.forEach { tag ->
      val mChip: Chip = baseActivity.layoutInflater.inflate(R.layout.item_chip, binding?.chipsOffers, false) as Chip
      mChip.text = tag
      mChip.setOnCloseIconClickListener {
        binding?.chipsOffers?.removeView(mChip)
        tagList.remove(tag)
      }
      binding?.chipsOffers?.addView(mChip)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnConfirm -> validateAnnGoBack()
      binding?.btnClickPhoto -> openImagePicker()
      binding?.btnAddTag -> {
        val txtTag = binding?.edtOfferTag?.text.toString()
        if (txtTag.isNotEmpty()) {
          tagList.add(txtTag)
          offersTags()
          binding?.edtOfferTag?.setText("")
        }
      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@AdditionalInfoFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(true)
      .scale(800, 800)
      .enableDebuggingMode(true).build()
  }

  private fun validateAnnGoBack() {
    offerModel?.tags = tagList
    val output = Intent()
    output.putExtra(IntentConstant.OFFER_DATA.name, offerModel)
    output.putExtra(IntentConstant.OFFER_SECONDARY_IMAGE.name, secondaryImage)
    baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
    baseActivity.finish()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      secondaryImage(mPaths)

    }

  }

  private fun secondaryImage(mPaths: ArrayList<String>) {
    if (secondaryImage.size < 8) {
      if (mPaths.size + secondaryImage.size > 8) showLongToast(resources.getString(R.string.only_eight_files_are_allowed_discarding))
      var index: Int = secondaryImage.size
      while (index < 8 && mPaths.isNotEmpty()) {
        secondaryImage.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      setAdapter()
    } else showLongToast(resources.getString(R.string.only_eight_files_allowed))
  }

  private fun setAdapter() {
    if (adapterImage == null) {
      binding?.rvAdditionalDocs?.apply {
        adapterImage = AppBaseRecyclerViewAdapter(baseActivity, secondaryImage, this@AdditionalInfoFragment)
        adapter = adapterImage
      }
    } else adapterImage?.notifyDataSetChanged()
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.IMAGE_CLEAR_CLICK.ordinal -> {
        val data = item as? FileModel
        if (isEdit && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.ActualImage == data?.pathUrl } ?: return
          showProgress(resources.getString(R.string.removing_image))
          secondaryDataImage?.remove(dataImage)
          secondaryImage.remove(data)
          setAdapter()
          hideProgress()
        } else {
          secondaryImage.remove(data)
          setAdapter()
        }
      }
      RecyclerViewActionType.IMAGE_CHANGE.ordinal -> {
        val data = item as? FileModel
        if (isEdit && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.ActualImage == data?.pathUrl } ?: return
          showProgress(resources.getString(R.string.removing_image))
          secondaryDataImage?.remove(dataImage)
          secondaryImage.remove(data)
          setAdapter()
          hideProgress()
        } else {
          secondaryImage.remove(data)
          setAdapter()
        }
        openImagePicker()
      }
    }
  }
}


