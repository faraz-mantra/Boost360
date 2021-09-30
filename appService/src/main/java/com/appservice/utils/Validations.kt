package com.appservice.utils

import java.util.regex.Matcher
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

        return Pattern.compile(DOMAIN_FORMAT).matcher(domainWithoutWWW).matches()
    }
}