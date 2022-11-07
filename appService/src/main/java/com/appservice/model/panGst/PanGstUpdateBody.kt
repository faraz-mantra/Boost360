package com.appservice.model.panGst

data class PanGstUpdateBody(
    val ClientId: String,
    val FloatingPointId: String,
    val GSTDetails: GSTDetailsRequest?,
    val PanDetails: PanDetailsRequest?
)
data class GSTDetailsRequest(
    val BusinessName: String?,
    val BusinessRegister: Boolean?,
    val DocumentContent: String?,
    val DocumentName: String?,
    val FileType: String?,
    val GSTIN: String?,
    val RcmApply: Boolean?
)

data class PanDetailsRequest(
    val DocumentContent: String?,
    val DocumentName: String?,
    val FileType: String?,
    val Name: String?,
    val Number: String?,
    val IsImageRemove: Boolean=false
)