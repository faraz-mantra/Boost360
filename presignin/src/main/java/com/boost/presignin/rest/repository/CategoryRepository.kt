package com.boost.presignin.rest.repository

import android.content.Context
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.services.local.CategoryLocalDataSource
import com.boost.presignin.rest.services.remote.CategoryRemoteDataSource
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import io.reactivex.Observable

object CategoryRepository : AppBaseRepository<CategoryRemoteDataSource, CategoryLocalDataSource>() {

  fun getCategories(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(CategoryLocalDataSource.getCategory(context), TaskCode.GET_CATEGORIES)
  }

  fun getCategoriesPlan(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      CategoryLocalDataSource.getCategoryPlan(context),
      TaskCode.GET_CATEGORIES_PLAN
    )
  }

  override fun getRemoteDataSourceClass(): Class<CategoryRemoteDataSource> {
    return CategoryRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): CategoryLocalDataSource {
    return CategoryLocalDataSource
  }
}