package com.framework.rest.errorTicketGenerate

import com.framework.base.BaseLocalService
import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.firestore.restApi.TaskCode
import io.reactivex.Observable
import retrofit2.Retrofit

object SalesAssistErrorRepository : BaseRepository<SalesAssignRemoteData, BaseLocalService>() {

    fun createErrorTicket(fpTag :String, subject :String, comment :String, submitterEmail :String): Observable<BaseResponse> {
        return makeRemoteRequest(
            remoteDataSource.createErrorTicket(fpTag = fpTag, subject = subject, comment = comment, submitterEmail = submitterEmail),
            TaskCode.CREATE_ERROR_TICKET.ordinal
        )
    }

    override fun getRemoteDataSourceClass(): Class<SalesAssignRemoteData> {
        return SalesAssignRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): BaseLocalService {
       return BaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return SalesAssignErrorApiClient.shared.retrofit
    }
}