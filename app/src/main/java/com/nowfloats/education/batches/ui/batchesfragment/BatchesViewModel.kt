package com.nowfloats.education.batches.ui.batchesfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.models.BaseViewModel
import com.google.gson.JsonObject
import com.nowfloats.education.batches.model.Data
import com.nowfloats.education.batches.model.Query
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED
import com.nowfloats.education.helper.Constants.LIMIT
import com.nowfloats.education.helper.Constants.SKIP
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.helper.JsonHelper
import com.nowfloats.education.helper.processRequest
import com.nowfloats.education.model.DeleteModel
import com.nowfloats.education.model.Set
import com.nowfloats.education.model.UpcomingBatchesResponse
import com.nowfloats.education.model.UpdatedValue
import com.nowfloats.education.service.IEducationService
import io.reactivex.disposables.CompositeDisposable

class BatchesViewModel(private val service: IEducationService) : BaseViewModel() {

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  val upcomingBatchResponse: LiveData<UpcomingBatchesResponse>
    get() = _upcomingBatchResponse
  private var _upcomingBatchResponse = MutableLiveData<UpcomingBatchesResponse>()

  val errorResponse: LiveData<String>
    get() = _errorResponse

  private var _errorResponse = MutableLiveData<String>()

  val deleteBatchResponse: LiveData<String>
    get() = _deleteBatchResponse

  private var _deleteBatchResponse = MutableLiveData<String>()

  fun getUpcomingBatches(fpTag: String) {
    val value = "{WebsiteId:'" + fpTag + "'}";
    compositeDisposable.add(service.getUpcomingBatches(AUTH_CODE, value, LIMIT, SKIP)
      .processRequest(
        {
          _upcomingBatchResponse.value = it
        },
        { error ->
          error.let { _errorResponse.value = it }
        }
      ))
  }

  fun deleteUpcomingBatch(batchesData: Data) {
    /*val customQuery = "{" + '"' + "_id" + '"' + ":" + '"' + batchesData._id + '"' + "}"
    val customSet = "{" + '"' + "$" + "set" + '"' + " : {\"IsArchived\":\"true\"}}"*/

    val query = Query(_id = batchesData._id)
    val queryString = JsonHelper.KtToJson(query)

    val set = Set(true)
    val updatedValue = UpdatedValue(`$set` = set)
    val updateValueString = JsonHelper.KtToJson(updatedValue)

    val deleteBatchModel =
      DeleteModel(Multi = true, Query = queryString, UpdateValue = updateValueString)

    compositeDisposable.add(service.deleteUpcomingBatch(AUTH_CODE, deleteBatchModel)
      .processRequest(
        {
          _deleteBatchResponse.value = SUCCESS
        },
        { error ->
          error?.let {
            if (it == JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
              _deleteBatchResponse.value = SUCCESS
            } else {
              _errorResponse.value = it
            }
          }
        }
      ))
  }

  fun setDeleteBatchesLiveDataValue(s: String) {
    _deleteBatchResponse.value = s
  }
}