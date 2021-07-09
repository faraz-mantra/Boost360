package com.nowfloats.education.faculty.model

import com.nowfloats.education.model.Profileimage

data class UpdateFacultyModel(
        val Multi: Boolean,
        val Query: String,
        val UpdateValue: String
)

data class UpdateValue(
        val `$set`: Set
)

data class Set(
        val description: String,
        val name: String,
        val profileimage: Profileimage,
        val title: String
)