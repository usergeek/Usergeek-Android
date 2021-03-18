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

    override fun setUserProperty(property: String, value: Any?): UsergeekClient {
        client?.setUserProperty(property, value) ?: ignoreWarning("setUserProperty")
        return this
    }

    override fun unsetUserProperty(property: String): UsergeekClient {
        client?.unsetUserProperty(property) ?: ignoreWarning("unsetUserProperty")
        return this
    }

    override fun incrementUserProperty(property: String, value: Number): UsergeekClient {
        client?.incrementUserProperty(property, value) ?: ignoreWarning("incrementUserProperty")
        return this
    }

    override fun appendUserProperty(property: String, value: String): UsergeekClient {
        client?.appendUserProperty(property, value) ?: ignoreWarning("appendUserProperty")
        return this
    }

    override fun removeUserProperty(property: String, value: String): UsergeekClient {
        client?.removeUserProperty(property, value) ?: ignoreWarning("removeUserProperty")
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