package com.usergeek.stream

import android.os.Build
import androidx.core.util.Supplier
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class DevicePropertyConfigTest: BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testAllProperties() {
        val config = DevicePropertyConfig()
            .trackPlatform()
            .trackModel()
            .trackAppVersion()
            .trackOsVersion()
            .trackCountry()
            .trackBrand()
            .trackManufacturer()
            .trackCarrier()
            .trackLanguage()
            .trackCacheProperty("my") { "prop"}
            .trackProperty("time") { _ -> Supplier { Date(1234L) }  }

        Truth.assertThat(config.properties.size).isEqualTo(11)
        Truth.assertThat(config.properties["platform"]!!(context).get()).isEqualTo("android")
        Truth.assertThat(config.properties["model"]!!(context).get()).isEqualTo(Build.MODEL)
        Truth.assertThat(config.properties["appVersion"]!!(context).get()).isEqualTo(DevicePropertyFactory.getAppVersion(context))
        Truth.assertThat(config.properties["osVersion"]!!(context).get()).isEqualTo(Build.VERSION.SDK_INT.toString())
        Truth.assertThat(config.properties["country"]!!(context).get()).isEqualTo(DevicePropertyFactory.getCountry(context))
        Truth.assertThat(config.properties["brand"]!!(context).get()).isEqualTo(Build.BRAND)
        Truth.assertThat(config.properties["manufacturer"]!!(context).get()).isEqualTo(Build.MANUFACTURER)
        Truth.assertThat(config.properties["carrier"]!!(context).get()).isEqualTo(DevicePropertyFactory.getCarrier(context))
        Truth.assertThat(config.properties["language"]!!(context).get()).isEqualTo(Locale.getDefault().language)
        Truth.assertThat(config.properties["my"]!!(context).get()).isEqualTo("prop")
        Truth.assertThat(config.properties["time"]!!(context).get()).isEqualTo(Date(1234L))
    }
}