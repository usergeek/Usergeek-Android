package com.usergeek.stream

/**
 * Client API.
 */
interface UsergeekClient {

    fun setUserId(userId: String): UsergeekClient

    fun resetUserId(regenerateDeviceId: Boolean): UsergeekClient

    fun setUserProperty(property: String, value: Any?): UsergeekClient

    fun setOnceUserProperty(property: String, value: Any?): UsergeekClient

    fun unsetUserProperty(property: String): UsergeekClient

    fun incrementUserProperty(property: String, value: Number): UsergeekClient

    fun appendUserProperty(property: String, value: String): UsergeekClient

    fun removeUserProperty(property: String, value: String): UsergeekClient

    fun logEvent(eventName: String): UsergeekClient

    fun logEvent(eventName: String, eventProperties: EventProperties): UsergeekClient

    fun flush()

    fun getDeviceId(): String
}