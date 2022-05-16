package com.appservice.model

data class PanGstUpdateBody(
    val ClientId: String,
    val FloatingPointId: String,
    val GSTDetails: GSTDetails?,
    val PanDetails: PanDetails?
)
data class GSTDetails(
    val BusinessName: String?,
    val BusinessRegister: Boolean?,
    val DocumentContent: String?,
    val DocumentName: String?,
    val FileType: String?,
    val GSTIN: String?,
    val RcmApply: Boolean?
)

data class PanDetails(
    val DocumentContent: String?,
    val DocumentName: String?,
    val FileType: String?,
    val Name: String?,
    val Number: String?
)