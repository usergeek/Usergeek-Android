package com.usergeek.stream

import android.content.Context
import android.os.Build
import androidx.core.util.Supplier
import java.util.*
import kotlin.collections.HashMap

class DevicePropertyConfig {

    val properties = HashMap<String, (Context) -> Supplier<String?>>()

    fun trackPlatform(): DevicePropertyConfig {
        return trackCacheProperty("platform") { "android" }
    }

    fun trackModel(): DevicePropertyConfig {
        return trackCacheProperty("model") { Build.MODEL }
    }

    fun trackBrand(): DevicePropertyConfig {
        return trackCacheProperty("brand") { Build.BRAND }
    }

    fun trackManufacturer(): DevicePropertyConfig {
        return trackCacheProperty("manufacturer") { Build.MANUFACTURER }
    }

    fun trackOsVersion(): DevicePropertyConfig {
        return trackCacheProperty("osVersion") { Build.VERSION.SDK_INT.toString() }
    }

    fun trackAppVersion(): DevicePropertyConfig {
        return trackCacheProperty("appVersion", DevicePropertyFactory::getAppVersion)
    }

    fun trackCountry(): DevicePropertyConfig {
        return trackCacheProperty("country", DevicePropertyFactory::getCountry)
    }

    fun trackLanguage(): DevicePropertyConfig {
        return trackCacheProperty("language") { Locale.getDefault().language }
    }

    fun trackCarrier(): DevicePropertyConfig {
        return trackCacheProperty("carrier", DevicePropertyFactory::getCarrier)
    }

    fun trackCacheProperty(property: String, initializer: (Context) -> String?): DevicePropertyConfig {
        return trackProperty(property) {
            CacheSupplierOnContext(it, initializer)
        }
    }

    fun trackProperty(property: String, initializer: (Context) -> Supplier<String?>): DevicePropertyConfig {
        properties[property] = initializer
        return this
    }

    class CacheSupplierOnContext<K>(context: Context, initializer: (Context) -> K?) : Supplier<K?> {

        private val value: K? by lazy {
            initializer(context)
        }

        override fun get(): K? {
            return value
        }

        override fun toString(): String {
            return "CacheSupplierOnContext(value=$value)"
        }
    }

}