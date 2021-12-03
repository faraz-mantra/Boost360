package com.boost.marketplace.infra.utils

import timber.log.Timber

object LogUtils {
    fun debug(tag: String?, message: String?) {
        Timber.tag(tag).d(message)
    }

    fun error(tag: String?, message: String?) {
        Timber.tag(tag).e(message)
    }
}
