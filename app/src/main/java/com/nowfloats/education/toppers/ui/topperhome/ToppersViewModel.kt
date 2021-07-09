package com.nowfloats.education.toppers.ui.topperhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.models.BaseViewModel
import com.nowfloats.education.batches.model.Query
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.LIMIT
import com.nowfloats.education.helper.Constants.SKIP
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.helper.JsonHelper
import com.nowfloats.education.helper.processRequest
import com.nowfloats.education.model.DeleteModel
import com.nowfloats.education.model.OurTopperResponse
import com.nowfloats.education.model.Set
import com.nowfloats.education.model.UpdatedValue
import com.nowfloats.education.service.IEducationService
import com.nowfloats.education.toppers.model.Data
import io.reactivex.disposables.CompositeDisposable

class ToppersViewModel(private val service: IEducationService) : BaseViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val ourTopperResponse: LiveData<OurTopperResponse>
        get() = _ourTopperResponse
    private var _ourTopperResponse = MutableLiveData<OurTopperResponse>()

    val errorMessage: LiveData<String>
        get() = _errorResponse

    private var _errorResponse = MutableLiveData<String>()

    val deleteTopperResponse: LiveData<String>
        get() = _deleteTopperResponse

    private var _deleteTopperResponse = MutableLiveData<String>()

    fun getOurToppers() {
        val value = "{WebsiteId:'"+WEBSITE_ID_EDUCATION+"'}"
        compositeDisposable.add(service.getOurToppers(AUTH_CODE, value, LIMIT, SKIP).processRequest(
                {
                    _ourTopperResponse.value = it
                },
                { error ->
                    error?.let { _errorResponse.value = it }
                }
        ))
    }

    fun deleteOurTopper(topperData: Data) {
        val query = Query(_id = topperData._id)
        val queryString = JsonHelper.KtToJson(query)

        val set = Set(true)
        val updatedValue = UpdatedValue(`$set` = set)
        val updateValueString = JsonHelper.KtToJson(updatedValue)

        val deleteTopperModel = DeleteModel(Multi = true, Query = queryString, UpdateValue = updateValueString)

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

    fun setDeleteTopperLiveDataValue(s: String) {
        _deleteTopperResponse.value = s
    }
}