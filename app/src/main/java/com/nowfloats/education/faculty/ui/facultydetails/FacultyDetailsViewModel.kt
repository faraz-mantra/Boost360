package com.nowfloats.education.faculty.ui.facultydetails

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nowfloats.education.batches.model.Query
import com.nowfloats.education.faculty.model.*
import com.nowfloats.education.faculty.model.Set
import com.nowfloats.education.helper.*
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.EDUCATION_API_BASE_URL
import com.nowfloats.education.helper.Constants.FACULTY_PROFILE_IMAGE
import com.nowfloats.education.helper.Constants.JPEG_FORMAT
import com.nowfloats.education.helper.Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.UPLOAD_OUR_FACULTY_IMAGE_URL
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.model.*
import com.nowfloats.education.service.IEducationService
import com.nowfloats.education.service.UploadImageService
import com.nowfloats.education.service.UploadImageServiceListener
import io.reactivex.disposables.CompositeDisposable

class FacultyDetailsViewModel(
  private val service: IEducationService,
  private val fileProvider: FileProvider,
  private val saveImageHelper: SaveImageHelper,
  private val exifInterfaceHelper: ExifInterfaceHelper
) : ViewModel(), UploadImageServiceListener {

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  val addFacultyResponse: LiveData<String>
    get() = _addFacultyResponse
  private var _addFacultyResponse = MutableLiveData<String>()

  val updateFacultyResponse: LiveData<String>
    get() = _updateFacultyResponse
  private var _updateFacultyResponse = MutableLiveData<String>()

  val errorResponse: LiveData<String>
    get() = _errorResponse

  private var _errorResponse = MutableLiveData<String>()

  val deleteFacultyResponse: LiveData<String>
    get() = _deleteFacultyResponse

  private var _deleteFacultyResponse = MutableLiveData<String>()

  private var filePath: String? = null

  val uploadImageResponse: LiveData<MutableList<ResponseImageModel>>
    get() = _uploadImageResponse

  private var _uploadImageResponse = MutableLiveData<MutableList<ResponseImageModel>>()

  fun setFilePath(filePath: String?) {
    this.filePath = filePath
  }

  fun getFilePath() = this.filePath

  fun addOurFaculty(fpTag: String, facultyData: Data, imageUrl: String) {
    val profileImage = Profileimage(
      url = imageUrl,
      description = facultyData.name
    )
    val actionData = ActionData(
      name = facultyData.name,
      title = facultyData.title,
      description = facultyData.description,
      profileimage = profileImage
    )
    val addFacultyModel = AddFacultyModel(
      WebsiteId = fpTag,
      ActionData = actionData
    )

    compositeDisposable.add(service.addOurFaculty(AUTH_CODE, addFacultyModel)
      .processRequest(
        {
          _addFacultyResponse.value = it
        },
        { error ->
          error?.let { _errorResponse.value = it }
        }
      ))
  }

  fun deleteOurFaculty(facultyData: Data) {
    val query = Query(_id = facultyData._id)
    val queryString = JsonHelper.KtToJson(query)

    val set = com.nowfloats.education.model.Set(IsArchived = true)

    val updateValue = UpdatedValue(set)
    val updateValueString = JsonHelper.KtToJson(updateValue)

    val deleteFacultyModel = DeleteModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

    compositeDisposable.add(service.deleteOurFaculty(AUTH_CODE, deleteFacultyModel)
      .processRequest(
        {
          _deleteFacultyResponse.value = SUCCESS
        },
        { error ->
          error?.let {
            if (it == JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
              _deleteFacultyResponse.value = SUCCESS
            } else {
              _errorResponse.value = it
            }
          }
        }
      ))
  }

  fun updateOurFaculty(facultyData: Data, imageUrl: String?) {
    val query = Query(_id = facultyData._id)
    val queryString = JsonHelper.KtToJson(query)

    val profileImage = Profileimage(
      url = imageUrl ?: facultyData.profileimage.url,
      description = facultyData.name
    )

    val set = Set(
      name = facultyData.name,
      title = facultyData.title,
      description = facultyData.description,
      profileimage = profileImage
    )

    val updateValue = UpdateValue(`$set` = set)
    val updateValueString = JsonHelper.KtToJson(updateValue)

    val updateFacultyModel = UpdateFacultyModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

    compositeDisposable.add(service.updateOurFaculty(AUTH_CODE, updateFacultyModel).processRequest(
      {
        _updateFacultyResponse.value = SUCCESS
      },
      { error ->
        error?.let {
          if (it == JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
            _updateFacultyResponse.value = SUCCESS
          } else {
            _errorResponse.value = it
          }
        }
      }
    ))
  }

  fun getFacultyProfileImageUrl(filePath: String) {
    // Todo : response is not in proper json format while using retrofit with rxjava
    /*val file = File(path)
    val requestFile = RequestBody.create(
            context.contentResolver.getType(uri).toMediaTypeOrNull(),
            file
    )
    // MultipartBody.Part is used to send also the actual file name
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
    compositeDisposable.add(service.getImageUrl("5812233c9ec6682dbce36860", "faculty_image", body).processRequest(
            {
                it
            },
            {
                it
            }
    ))*/

    val url =
      EDUCATION_API_BASE_URL + UPLOAD_OUR_FACULTY_IMAGE_URL + FACULTY_PROFILE_IMAGE + JPEG_FORMAT
    val uploadImageModel = UploadImageModel(url, filePath, FACULTY_PROFILE_IMAGE)
    UploadImageService(this, mutableListOf(uploadImageModel)).execute()
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