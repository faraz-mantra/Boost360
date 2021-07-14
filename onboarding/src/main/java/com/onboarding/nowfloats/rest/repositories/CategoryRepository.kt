package com.onboarding.nowfloats.rest.repositories

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.services.local.category.CategoryLocalDataSource
import com.onboarding.nowfloats.rest.services.remote.category.CategoryRemoteDataSource
import io.reactivex.Observable

object CategoryRepository : AppBaseRepository<CategoryRemoteDataSource, CategoryLocalDataSource>() {

  fun getCategories(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(CategoryLocalDataSource.getCategory(context), Taskcode.GET_CATEGORIES)
  }

  fun getCategoriesPlan(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      CategoryLocalDataSource.getCategoryPlan(context),
      Taskcode.GET_CATEGORIES_PLAN
    )
  }

  override fun getRemoteDataSourceClass(): Class<CategoryRemoteDataSource> {
    return CategoryRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): CategoryLocalDataSource {
    return CategoryLocalDataSource
  }

}