package com.framework.rest

sealed class NetworkResult<T>(
    val data:T?=null,
    val msg:String?=null
){
    class Loading<T>():NetworkResult<T>()
    class Success<T>(data: T?,msg: String?=null):NetworkResult<T>(data,msg)
    class Error<T>(msg: String?):NetworkResult<T>(msg=msg)

}
