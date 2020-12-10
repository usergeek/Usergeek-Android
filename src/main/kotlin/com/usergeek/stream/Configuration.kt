package com.usergeek.stream

import android.content.Context
import androidx.core.util.Supplier
import java.net.URL
import java.util.concurrent.TimeUnit

class Configuration(context: Context, internal val apiKey: String, initConfig: InitConfig) {

    internal val serverUrl = URL(Formats.SERVER_URL)
    internal val connectTimeoutMillis = TimeUnit.SECONDS.toMillis(30)
    internal val readTimeoutMillis = TimeUnit.SECONDS.toMillis(30)
    internal val uploadContentEncoding: String? = "gzip"

    internal val maxReportsCountInStorage = 1000L
    internal val removeReportsPercentWhenFull = 2L
    internal val uploadReportsCount = 30
    internal val uploadReportsPeriodMillis = TimeUnit.SECONDS.toMillis(30)
    internal val deviceProperties: BaseProperties<Supplier<String?>>?

    init {
        deviceProperties = initConfig.devicePropertyConfig?.let {
            val properties = BaseProperties<Supplier<String?>>()

            for (deviceProperty in it.properties) {
                properties.add(deviceProperty.key, deviceProperty.value(context))
            }
            properties
        }
    }

    override fun toString(): String {
        return "Configuration(apiKey='$apiKey', serverUrl=$serverUrl, connectTimeoutMillis=$connectTimeoutMillis, readTimeoutMillis=$readTimeoutMillis, maxReportsCountInStorage=$maxReportsCountInStorage, removeReportsPercentWhenFull=$removeReportsPercentWhenFull, uploadReportsCount=$uploadReportsCount, uploadReportsPeriodMillis=$uploadReportsPeriodMillis, deviceProperties=$deviceProperties)"
    }


}