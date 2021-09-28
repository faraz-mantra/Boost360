package com.appservice.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object Validations {

    fun isDomainValid(domainString: CharSequence?): Boolean {
        val patternDomain: Pattern = Pattern.compile(DOMAIN_FORMAT)
        val matcher: Matcher = patternDomain.matcher(domainString!!)

        return if (domainString.isNullOrEmpty())
            false
        else matcher.matches()

    }
}