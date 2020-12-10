package com.usergeek.stream

/**
 * Client API.
 */
interface UsergeekClient {

    fun setUserId(userId: String): UsergeekClient

    fun resetUserId(regenerateDeviceId: Boolean): UsergeekClient

    fun logUserProperties(userProperties: UserProperties): UsergeekClient

    fun logEvent(eventName: String): UsergeekClient

    fun logEvent(eventName: String, eventProperties: EventProperties): UsergeekClient

    fun flush()

    fun getDeviceId(): String
}