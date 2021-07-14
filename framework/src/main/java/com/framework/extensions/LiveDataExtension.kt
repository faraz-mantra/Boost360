package com.framework.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
  observe(lifecycleOwner, object : Observer<T> {
    override fun onChanged(t: T?) {
      observer.onChanged(t)
      removeObserver(this)
    }
  })
}

fun <T> LiveData<T>.observeChange(
  lifecycleOwner: LifecycleOwner,
  observer: Observer<T>,
  changes: Int
) {
  var count = 0
  observe(lifecycleOwner, object : Observer<T> {
    override fun onChanged(t: T?) {
      observer.onChanged(t)
      count++
      if (count >= changes) removeObserver(this)
    }
  })
}