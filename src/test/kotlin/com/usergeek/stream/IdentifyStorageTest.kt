package com.usergeek.stream

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IdentifyStorageTest: BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }


    @Test
    fun setDeviceId() {
        val storage = IdentifyStorage(context)

        val deviceId = "ABC"

        Truth.assertThat(storage.getDeviceId()).isNull()
        storage.setDeviceId(deviceId)
        Truth.assertThat(storage.getDeviceId()).isEqualTo(deviceId)
    }

    @Test
    fun setUserId() {
        val storage = IdentifyStorage(context)

        val userId = "ABC"

        Truth.assertThat(storage.getUserId()).isNull()
        storage.setUserId(userId)
        Truth.assertThat(storage.getUserId()).isEqualTo(userId)
    }

}