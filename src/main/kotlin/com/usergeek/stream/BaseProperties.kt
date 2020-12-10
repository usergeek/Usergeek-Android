package com.usergeek.stream

open class BaseProperties<V> {

    internal val properties = HashMap<String, V>()

    internal fun add(property: String, value: V) {
        this.properties[property] = value
    }

    override fun toString(): String {
        return "BaseProperties(properties=$properties)"
    }

}