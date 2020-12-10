package com.usergeek.stream

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConfigurationTest: BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testInitConfig() {
        val initConfig = InitConfig()

        Truth.assertThat(initConfig.devicePropertyConfig).isNull()
        Truth.assertThat(initConfig.initialDeviceId).isNull()
        Truth.assertThat(initConfig.initialUserId).isNull()
        Truth.assertThat(initConfig.enableStartAppEvent).isFalse()
        Truth.assertThat(initConfig.enableSessionTracking).isNull()

        val devicePropertyConfig = DevicePropertyConfig()
        initConfig.setDevicePropertyConfig(devicePropertyConfig)
        initConfig.setInitialDeviceId("d")
        initConfig.setInitialUserId("u")
        initConfig.enableStartAppEvent()
        initConfig.enableSessionTracking(ApplicationProvider.getApplicationContext())

        Truth.assertThat(initConfig.devicePropertyConfig).isEqualTo(devicePropertyConfig)
        Truth.assertThat(initConfig.initialDeviceId).isEqualTo("d")
        Truth.assertThat(initConfig.initialUserId).isEqualTo("u")
        Truth.assertThat(initConfig.enableStartAppEvent).isTrue()
        Truth.assertThat(initConfig.enableSessionTracking).isNotNull()
    }

    @Test
    fun testDefault() {
        val apiKey = "APIKEY"

        val configuration = Configuration(context, apiKey, InitConfig())

        Truth.assertThat(configuration.apiKey).isEqualTo(apiKey)
        Truth.assertThat(configuration.serverUrl.toString()).isEqualTo(Formats.SERVER_URL)
        Truth.assertThat(configuration.deviceProperties).isNull()
    }

    @Test
    fun testSpecialLocalhost() {
        val apiKey = "APIKEY"

        val initConfig = InitConfig()
        initConfig.setDevicePropertyConfig(
            DevicePropertyConfig()
                .trackCountry()
        )

        val configuration = Configuration(context, apiKey, initConfig)

        Truth.assertThat(configuration.apiKey).isEqualTo(apiKey)
        Truth.assertThat(configuration.serverUrl.toString()).isEqualTo(Formats.SERVER_URL)
        Truth.assertThat(configuration.deviceProperties!!.properties.size).isEqualTo(1)
        Truth.assertThat(configuration.deviceProperties.properties["country"]).isNotNull()

        println(">>>>>> " + configuration)
    }

}