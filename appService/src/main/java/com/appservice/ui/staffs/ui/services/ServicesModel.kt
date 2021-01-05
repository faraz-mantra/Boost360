package com.appservice.ui.staffs.ui.services

import java.util.*

object ServicesModel {
    val allServices: ArrayList<String>
        get() {
            val list = ArrayList<String>()
            list.add("Hair Removal and Waxing")
            list.add("Hair Color for all ages")
            list.add("Facial Makover")
            list.add("Bridal Makover")
            list.add("Anti-aging therapy")
            list.add("Anti-pimple")
            list.add("Skin Toning")
            return list
        }
}