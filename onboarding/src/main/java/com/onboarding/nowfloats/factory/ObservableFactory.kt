package com.onboarding.nowfloats.factory

import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ObservableFactory {
  companion object
}

fun ObservableFactory.Companion.afterTextChangeEvents(textView: TextView?): Observable<TextViewAfterTextChangeEvent>? {
  if (textView == null) return null

  return RxTextView.afterTextChangeEvents(textView).subscribeOn(Schedulers.computation())
    .debounce(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
}
