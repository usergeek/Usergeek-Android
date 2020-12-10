package com.usergeek.stream

class EventProperties: BaseProperties<Any?>() {

    fun set(property: String, value: Any?): EventProperties {
        add(property, value)
        return this
    }

}