package com.usergeek.stream

import android.app.Application

class InitConfig {

    internal var devicePropertyConfig: DevicePropertyConfig? = null
    internal var initialDeviceId: String? = null
    internal var initialUserId: String? = null
    internal var enableStartAppEvent = false
    internal var enableSessionTracking: Application? = null
    internal var enableFlushOnClose: Application? = null

    fun setDevicePropertyConfig(config: DevicePropertyConfig?): InitConfig {
        devicePropertyConfig = config
        return this
    }

    fun setInitialDeviceId(id: String): InitConfig {
        initialDeviceId = id
        return this
    }

    fun setInitialUserId(id: String): InitConfig {
        initialUserId = id
        return this
    }

    fun enableStartAppEvent(): InitConfig {
        enableStartAppEvent = true
        return this
    }

    fun enableSessionTracking(app: Application): InitConfig {
        enableSessionTracking = app
        return this
    }

    fun enableFlushOnClose(app: Application): InitConfig {
        enableFlushOnClose = app
        return this
    }
}