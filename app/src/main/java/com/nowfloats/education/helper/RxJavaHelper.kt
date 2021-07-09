package com.nowfloats.education.helper

import android.util.Log
import com.nowfloats.education.koindi.KoinBaseApplication
import com.thinksity.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

inline fun <T> Observable<T>.processRequest(
        crossinline onNext: (result: T) -> Unit,
        crossinline onError: (message: String?) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        onNext(result)
                    },
                    { err ->
                        val message = getMessage(err)
                        Log.e("RxJavaHelper", err.message?:"")
                        onError(message)
                    }
            )
}

fun getMessage(t: Throwable) = when (t) {
    is IOException -> if (t.message == null) KoinBaseApplication.instance.getString(R.string.failed_to_connect_to_server) else t.message
    is SocketTimeoutException -> if (t.message == null) KoinBaseApplication.instance.getString(R.string.request_timeout) else t.message
    is HttpException -> {
        val err = t.response()?.errorBody()
        if (t.code() == 401) KoinBaseApplication.instance.getString(R.string.session_expired)
        else if (t.code() == 400) KoinBaseApplication.instance.getString(R.string.bad_request)
        else if (err == null) KoinBaseApplication.instance.getString(R.string.request_failed_)
        else {
            try {
                val jObjError = JSONObject(err.string())
                val error = JsonHelper.jsonToKt<Error>(jObjError.toString())
                error.error_description
            } catch (ex: Exception) {
                "Request failed."
            }
        }

    }
    else -> t.message ?: "Request failed."
}