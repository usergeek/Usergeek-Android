package com.usergeek.stream

class UserProperties: BaseProperties<UserProperties.UserPropertyValue>() {

    data class UserPropertyValue(val operation: String, val value: Any?)

    fun set(property: String, value: Any?): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.SET, value))
        return this
    }

    fun unset(property: String): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.UNSET, null))
        return this
    }

    fun add(property: String, value: Any?): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.ADD, value))
        return this
    }

    fun subtract(property: String, value: Any?): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.SUBTRACT, value))
        return this
    }
}