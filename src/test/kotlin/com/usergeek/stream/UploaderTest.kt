package com.usergeek.stream

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class UploaderTest: BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testDoPostRequest() {
        val initConfig = InitConfig()
        val configuration = Configuration(context, "API_KEY", initConfig)
        val uploader = Uploader(configuration)

        val sendLooper = Shadows.shadowOf(uploader.sendThread.looper)

        uploader.uploadReports("{reports}") { success ->
            Truth.assertThat(success).isFalse()
        }

        sendLooper.runToEndOfTasks()

//        println(">>>>> " + uploader.doPostRequest(
//            URL("http://localhost:8877/aa"),
//            5000, 200000,
//            "123456",
//            "content".toByteArray()
//        ))
//        println(">>>>> " + uploader.doPostRequest(
//            URL("http://localhost:8877/aa"),
//            5000, 200000,
//            "123456",
//            "content".toByteArray()
//        ))
//        println(">>>>> " + uploader.doPostRequest(
//            URL("http://localhost:8877/aa"),
//            5000, 200000,
//            "123456",
//            "content".toByteArray()
//        ))
    }
}