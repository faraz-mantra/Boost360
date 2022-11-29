package com.framework.utils

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun RecyclerView.showDecoration(decoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)) {
  addItemDecoration(decoration)
}

fun Observable<BaseResponse>.getResponse(func:(BaseResponse)->Unit){
  subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({s-> func.invoke(s) },{t->func.invoke(
      BaseResponse(status = 400))})

}