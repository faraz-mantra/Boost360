package com.nowfloats.education.batches.ui.batchesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nowfloats.education.batches.model.*
import com.nowfloats.education.batches.model.Set
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.DATE_FORMAT
import com.nowfloats.education.helper.Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TIME_FORMAT
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.helper.JsonHelper
import com.nowfloats.education.helper.processRequest
import com.nowfloats.education.model.DeleteModel
import com.nowfloats.education.model.UpdatedValue
import com.nowfloats.education.service.IEducationService
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

class BatchesDetailsViewModel(private val service: IEducationService) : ViewModel() {

  val calendar: Calendar = Calendar.getInstance()

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  private var _onDateSelected = MutableLiveData<String>()
  val onDateSelected: LiveData<String>
    get() = _onDateSelected

  val addBatchResponse: LiveData<String>
    get() = _addBatchResponse
  private var _addBatchResponse = MutableLiveData<String>()

  val updateBatchResponse: LiveData<String>
    get() = _updateBatchResponse
  private var _updateBatchResponse = MutableLiveData<String>()

  val errorResponse: LiveData<String>
    get() = _errorResponse

  private var _errorResponse = MutableLiveData<String>()

  val deleteBatchResponse: LiveData<String>
    get() = _deleteBatchResponse

  private var _deleteBatchResponse = MutableLiveData<String>()

  fun addUpcomingBatch(fpTag: String, batchesData: Data) {
    val actionData = ActionData(
      Coursecategorytag = batchesData.Coursecategorytag,
      batchtiming = batchesData.batchtiming,
      commencementdate = batchesData.commencementdate,
      duration = batchesData.duration
    )

    val addUpcomingBatchModel = AddUpcomingBatchModel(actionData, fpTag)

    compositeDisposable.add(service.addUpcomingBatches(AUTH_CODE, addUpcomingBatchModel)
      .processRequest(
        {
          _addBatchResponse.value = it
        },
        { error ->
          error?.let { _errorResponse.value = it }
        }
      ))
  }

  fun deleteUpcomingBatch(batchesData: Data) {
    /*val customQuery = "{" + '"' + "_id" + '"' + ":" + '"' + batchesData._id + '"' + "}"
    val customSet = "{" + '"' + "$" + "set" + '"' + " : {\"IsArchived\":\"true\"}}"*/

    val query = Query(_id = batchesData._id)
    val queryString = JsonHelper.KtToJson(query)

    val set = com.nowfloats.education.model.Set(
      IsArchived = true
    )

    val updatedValue = UpdatedValue(
      `$set` = set
    )
    val updateValueString = JsonHelper.KtToJson(updatedValue)

    val deleteBatchModel = DeleteModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

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

  fun getDate() {
    _onDateSelected.value = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time)
  }

  fun updateUpcomingBatch(batchData: Data) {
//        val queryString = "{_id:'5f1e96872c54b70001caf8a5'}"
//        val updateValueString = "{" + "$" + "set : {\"batchtiming\":\"10Am to 12Pm\",\"Coursecategorytag\":\"Bank PO\",\"duration\":\"10 months\",\"commencementdate\":\"2020-07-30T00:00:00Z\",\"CreatedOn\":\"2020-07-20T09:30:14.949Z\"}}"

    val query = Query(_id = batchData._id)
    val queryString = JsonHelper.KtToJson(query)

    val set = Set(
      Coursecategorytag = batchData.Coursecategorytag,
      batchtiming = batchData.batchtiming,
      commencementdate = batchData.commencementdate,
      duration = batchData.duration
    )

    val updateValue = UpdateValue(
      `$set` = set
    )
    val updateValueString = JsonHelper.KtToJson(updateValue)

    val updateBatchModel = UpdateUpcomingBatchModel(
      Multi = true,
      Query = queryString,
      UpdateValue = updateValueString
    )

    compositeDisposable.add(service.updateUpcomingBatch(
      AUTH_CODE,
      updateUpcomingBatchModel = updateBatchModel
    ).processRequest(
      {
        _updateBatchResponse.value = SUCCESS
      },
      { error ->
        error?.let {
          if (it == JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED) {
            _updateBatchResponse.value = SUCCESS
          } else {
            _errorResponse.value = it
          }
        }
      }
    ))
  }

  fun isTimeAfter(startTime: String, endTime: String): Boolean {
    val start = SimpleDateFormat(TIME_FORMAT).parse(startTime)
    val end = SimpleDateFormat(TIME_FORMAT).parse(endTime)
    return !end.before(start)
  }
}