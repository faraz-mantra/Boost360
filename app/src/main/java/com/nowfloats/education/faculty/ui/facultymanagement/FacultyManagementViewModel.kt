package com.nowfloats.education.faculty.ui.facultymanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.models.BaseViewModel
import com.nowfloats.education.batches.model.Query
import com.nowfloats.education.faculty.model.Data
import com.nowfloats.education.helper.Constants.AUTH_CODE
import com.nowfloats.education.helper.Constants.JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED
import com.nowfloats.education.helper.Constants.LIMIT
import com.nowfloats.education.helper.Constants.SKIP
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.WEBSITE_ID_EDUCATION
import com.nowfloats.education.helper.JsonHelper
import com.nowfloats.education.helper.processRequest
import com.nowfloats.education.model.DeleteModel
import com.nowfloats.education.model.OurFacultyResponse
import com.nowfloats.education.model.Set
import com.nowfloats.education.model.UpdatedValue
import com.nowfloats.education.service.IEducationService
import io.reactivex.disposables.CompositeDisposable

class FacultyManagementViewModel(private val service: IEducationService) : BaseViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val ourFacultyResponse: LiveData<OurFacultyResponse>
        get() = _ourFacultyResponse
    private var _ourFacultyResponse = MutableLiveData<OurFacultyResponse>()

    val errorMessage: LiveData<String>
        get() = _errorResponse

    private var _errorResponse = MutableLiveData<String>()

    val deleteFacultyResponse: LiveData<String>
        get() = _deleteFacultyResponse

    private var _deleteFacultyResponse = MutableLiveData<String>()

    fun getOurFaculty() {
        val value = "{WebsiteId:'"+WEBSITE_ID_EDUCATION+"'}"
        compositeDisposable.add(service.getOurFaculty(AUTH_CODE, value, LIMIT, SKIP).processRequest(
                {
                    _ourFacultyResponse.value = it
                },
                { error ->
                    error?.let { _errorResponse.value = it }
                }
        ))
    }

    fun deleteOurFaculty(facultyData: Data) {
        val query = Query(_id = facultyData._id)
        val queryString = JsonHelper.KtToJson(query)

        val set = Set(IsArchived = true)

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

    fun setDeleteFacultyLiveDataValue(s: String) {
        _deleteFacultyResponse.value = s
    }
}