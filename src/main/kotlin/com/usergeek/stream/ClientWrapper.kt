package com.usergeek.stream

/**
 * Wrapper.
 */
class ClientWrapper : UsergeekClient {

    internal var client: UsergeekClient? = null

    override fun setUserId(userId: String): UsergeekClient {
        client?.setUserId(userId) ?: ignoreWarning("setUserId")
        return this
    }

    override fun resetUserId(regenerateDeviceId: Boolean): UsergeekClient {
        client?.resetUserId(regenerateDeviceId) ?: ignoreWarning("resetUserId")
        return this
    }

    override fun logUserProperties(userProperties: UserProperties): UsergeekClient {
        client?.logUserProperties(userProperties) ?: ignoreWarning("logUserProperties")
        return this
    }

    override fun logEvent(eventName: String): UsergeekClient {
        client?.logEvent(eventName) ?: ignoreWarning("logEvent")
        return this
    }

    override fun logEvent(eventName: String, eventProperties: EventProperties): UsergeekClient {
        client?.logEvent(eventName, eventProperties) ?: ignoreWarning("logEvent")
        return this
    }

    override fun flush() {
        client?.flush() ?: ignoreWarning("flush")
    }

    override fun getDeviceId(): String {
        return client?.getDeviceId() ?: run {
            ignoreWarning("getDeviceId")
            return ""
        }
    }

    private fun ignoreWarning(method: String) {
        warning { String.format("Ignore %s(), Usergeek not initialized", method) }
    }

}