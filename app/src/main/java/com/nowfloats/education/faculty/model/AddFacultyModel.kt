package com.nowfloats.education.faculty.model

import com.nowfloats.education.model.Profileimage

data class AddFacultyModel(
    val ActionData: ActionData,
    val WebsiteId: String
)

data class ActionData(
        val description: String,
        val name: String,
        val profileimage: Profileimage,
        val title: String
)
