package com.supnet.common

class VEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getData(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}