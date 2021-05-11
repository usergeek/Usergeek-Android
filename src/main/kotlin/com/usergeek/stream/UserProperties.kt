package com.usergeek.stream

class UserProperties: BaseProperties<UserProperties.UserPropertyValue>() {

    data class UserPropertyValue(val operation: String, val value: Any?)

    internal fun set(property: String, value: Any?): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.SET, value))
        return this
    }

    internal fun setOnce(property: String, value: Any?): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.SET_ONCE, value))
        return this
    }

    internal fun unset(property: String): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.UNSET, null))
        return this
    }

    internal fun increment(property: String, value: Number): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.INCREMENT, value))
        return this
    }

    internal fun append(property: String, value: String): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.APPEND, value))
        return this
    }

    internal fun remove(property: String, value: String): UserProperties {
        add(property, UserPropertyValue(Formats.PropertyOperation.REMOVE, value))
        return this
    }
}