package com.nowfloats.education.toppers.model

import com.nowfloats.education.model.Profileimage


data class UpdateTopperModel(
        val Multi: Boolean,
        val Query: String,
        val UpdateValue: String
)

data class UpdateValue(
        val `$set`: Set
)

data class Set(
        val coursecategory: String,
        val name: String,
        val profileimage: Profileimage,
        val programavailed: String,
        val rank: String,
        val testimonialimage: Testimonialimage,
        val testimonialtext: String
)