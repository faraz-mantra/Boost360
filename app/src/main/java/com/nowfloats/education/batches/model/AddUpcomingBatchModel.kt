package com.nowfloats.education.batches.model

data class AddUpcomingBatchModel(
        var ActionData: ActionData,
        var WebsiteId: String
)

data class ActionData(
        var Coursecategorytag: String,
        var batchtiming: String,
        var commencementdate: String,
        var duration: String
)