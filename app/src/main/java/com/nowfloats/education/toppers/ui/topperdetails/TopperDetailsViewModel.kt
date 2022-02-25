package com.nowfloats.education.toppers.ui.topperdetails

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nowfloats.education.batches.model.Query
import com.nowfloats.education.helper.*
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.EDUCATION_API_BASE_URL
import com.nowfloats.education.helper.Constants.JPEG_FORMAT
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TOPPER_PROFILE_IMAGE
import com.nowfloats.education.helper.Constants.TOPPER_TESTIMONIAL_IMAGE
import com.nowfloats.education.helper.Constants.UPLOAD_OUR_TOPPER_IMAGE_URL
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.model.*
import com.nowfloats.education.service.IEducationService
import com.nowfloats.education.service.UploadImageService
import com.nowfloats.education.service.UploadImageServiceListener
import com.nowfloats.education.toppers.model.*
import com.nowfloats.education.toppers.model.Set
import com.nowfloats.education.toppers.model.Testimonialimage
import io.reactivex.disposables.CompositeDisposable

class TopperDetailsViewModel(
  private val service: IEducationService,
  private val fileProvider: FileProvider,
  private val saveImageHelper: SaveImageHelper,
  private val exifInterfaceHelper: ExifInterfaceHelper
) : ViewModel(), UploadImageServiceListener {

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  val addTopperResponse: LiveData<String>
    get() = _addTopperResponse
  private var _addTopperResponse = MutableLiveData<String>()

  val updateTopperResponse: LiveData<String>
    get() = _updateTopperResponse
  private var _updateTopperResponse = MutableLiveData<String>()

  val errorResponse: LiveData<String>
    get() = _errorResponse

  private var _errorResponse = MutableLiveData<String>()

  val deleteTopperResponse: LiveData<String>
    get() = _deleteTopperResponse

  private var _deleteTopperResponse = MutableLiveData<String>()

  val uploadImageResponse: LiveData<MutableList<ResponseImageModel>>
    get() = _uploadImageResponse

  private var _uploadImageResponse = MutableLiveData<MutableList<ResponseImageModel>>()

  private var profileImagePath: String? = null

  private var testimonialImagePath: String? = null

  var topperImageFlag = Topper.NULL

  fun setProfileImagePath(path: String?) {
    this.profileImagePath = path
  }

  fun setTestimonialImagePath(path: String?) {
    this.testimonialImagePath = path
  }

  fun getProfileImagePath() = this.profileImagePath

  fun getTestimonialImagePath() = this.testimonialImagePath

  fun addOurTopper(fpTag: String, topperData: Data, profileImageUrl: String?, testimonialImageUrl: String?) {
    val profileImage = Profileimage(
      url = profileImageUrl ?: topperData.profileimage.url,
      description = topperData.name
    )

    val testimonialImage = Testimonialimage(
      url = testimonialImageUrl ?: topperData.testimonialimage.url,
      description = "Testimonial Image"
    )

    val actionData = ActionData(
      coursecategory = topperData.coursecategory,
      name = topperData.name,
      profileimage = profileImage,
      programavailed = topperData.programavailed,
      rank = topperData.rank,
      testimonialimage = testimonialImage,
      testimonialtext = topperData.testimonialtext
    )

    val addTopperModel = AddTopperModel(
      ActionData = actionData,
      WebsiteId = fpTag
    )

    compositeDisposable.add(service.addOurTopper(AUTH_CODE, addTopperModel)
      .processRequest(
        {
          _addTopperResponse.value = it
        },
        { error ->
          error?.let { _errorResponse.value = it }
        }
      ))
  }

  fun deleteOurTopper(topperData: Data) {
    val query = Query(_id = topperData._id)
    val queryString = JsonHelper.KtToJson(query)

    val set = com.nowfloats.education.model.Set(
      IsArchived = true
    )

    val updatedValue = UpdatedValue(`$set` = set)
    val updateValueString = JsonHelper.KtToJson(updatedValue)

    val deleteTopperModel = DeleteModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

    compositeDisposable.add(service.deleteOurTopper(AUTH_CODE, deleteTopperModel)
      .processRequest(
        {
          _deleteTopperResponse.value = SUCCESS
        },
        { error ->
          error?.let {
            if (it == Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
              _deleteTopperResponse.value = SUCCESS
            } else {
              _errorResponse.value = it
            }
          }
        }
      ))
  }

  fun updateOurTopper(topperData: Data, profileImageUrl: String?, testimonialImageUrl: String?) {
    val query = Query(_id = topperData._id)
    val queryString = JsonHelper.KtToJson(query)

    val profileImage = Profileimage(
      url = profileImageUrl ?: topperData.profileimage.url,
      description = topperData.name
    )

    val testimonialImage = Testimonialimage(
      url = testimonialImageUrl ?: topperData.testimonialimage.url,
      description = "Testimonial Image"
    )

    val set = Set(
      coursecategory = topperData.coursecategory,
      name = topperData.name,
      profileimage = profileImage,
      programavailed = topperData.programavailed,
      rank = topperData.rank, testimonialimage = testimonialImage,
      testimonialtext = topperData.testimonialtext
    )

    val updateValue = UpdateValue(`$set` = set)
    val updateValueString = JsonHelper.KtToJson(updateValue)

    val updateTopperModel = UpdateTopperModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

    compositeDisposable.add(service.updateOurTopper(AUTH_CODE, updateTopperModel).processRequest(
      {
        _updateTopperResponse.value = SUCCESS
      },
      { error ->
        error?.let {
          if (it == Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
            _updateTopperResponse.value = SUCCESS
          } else {
            _errorResponse.value = it
          }
        }
      }
    ))
  }

  fun getToppersImagesUrl(profileImagePath: String?, testimonialImagePath: String?) {
    val uploadImages: MutableList<UploadImageModel> = mutableListOf()
    val profileImageApiUrl =
      EDUCATION_API_BASE_URL + UPLOAD_OUR_TOPPER_IMAGE_URL + TOPPER_PROFILE_IMAGE + JPEG_FORMAT
    val testimonialImageApiUrl =
      EDUCATION_API_BASE_URL + UPLOAD_OUR_TOPPER_IMAGE_URL + TOPPER_TESTIMONIAL_IMAGE + JPEG_FORMAT

    profileImagePath?.let {
      val profileImageModel = UploadImageModel(profileImageApiUrl, it, TOPPER_PROFILE_IMAGE)
      uploadImages.add(profileImageModel)
    }

    testimonialImagePath?.let {
      val testimonialImageModel =
        UploadImageModel(testimonialImageApiUrl, it, TOPPER_TESTIMONIAL_IMAGE)
      uploadImages.add(testimonialImageModel)
    }

    UploadImageService(this, uploadImages).execute()
  }

  fun createImageUri(fileName: String): Uri? = fileProvider.createImageUri(fileName)

  fun saveImage(bitmap: Bitmap?, photoName: String): String? =
    saveImageHelper.writePhotoFile(bitmap, photoName)

  fun rotateImageIfRequired(path: String?, bitmap: Bitmap): Bitmap =
    exifInterfaceHelper.rotateImageIfRequired(path, bitmap)

  override fun onSuccess(response: MutableList<ResponseImageModel>) {
    _uploadImageResponse.value = response
  }

  override fun onFailed(error: String) {
    _errorResponse.value = error
  }
}