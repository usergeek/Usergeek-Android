package com.usergeek.stream

import android.content.Context

/**
 * Singleton pattern.
 */
object Usergeek {

    internal val wrapper = ClientWrapper()

    fun initialize(context: Context, apiKey: String): UsergeekClient {
        return initialize(context, apiKey, InitConfig())
    }

    @Synchronized
    fun initialize(context: Context, apiKey: String, config: InitConfig): UsergeekClient {
        if (wrapper.client != null) {
            warning { "Ignore initialize. Usergeek yet initialized." }
        } else {
            try {
                wrapper.client = ClientImpl(context, apiKey, config)
            } catch (th: Throwable) {
                error({ String.format("Error while initialize Usergeek, config: {}", config) }, th)
            }
        }

        return wrapper
    }

    fun getClient(): UsergeekClient {
        return wrapper
    }
}