package com.nowfloats.education.service

import com.nowfloats.education.model.ResponseImageModel

interface UploadImageServiceListener {
    fun onSuccess(response: MutableList<ResponseImageModel>)
    fun onFailed(message : String)
}