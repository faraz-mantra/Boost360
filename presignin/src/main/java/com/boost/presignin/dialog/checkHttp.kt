package com.boost.presignin.dialog
fun String.checkHttp(): String {
    return if ((this.startsWith("http://") || this.startsWith("https://")).not()) "http://$this" else this
}