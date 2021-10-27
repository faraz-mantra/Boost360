package com.appservice.utils

import java.util.regex.Pattern

object Validations {

    fun isDomainValid(domainString: CharSequence?): Boolean {
        var domainWithoutWWW = ""
        if (domainString.isNullOrEmpty() || domainString.isNullOrBlank())
            return false

        domainWithoutWWW = if (domainString.startsWith("www"))
            domainString.toString().replace("www.", "")
        else
            domainString.toString()

        val domainSelection = arrayListOf("com", "net", "in", "ca", "org")
        if (Pattern.compile(DOMAIN_FORMAT).matcher(domainWithoutWWW).matches()) {
            val substringAfterLast = domainWithoutWWW.substringAfterLast(".").lowercase()
            return domainSelection.contains(substringAfterLast) || substringAfterLast.contains("co.za") || substringAfterLast.contains("co.in")
        }

        return false
    }
}