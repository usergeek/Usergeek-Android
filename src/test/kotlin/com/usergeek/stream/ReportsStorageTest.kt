package com.usergeek.stream

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReportsStorageTest : BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun putEvent() {
        val storage = ReportsStorage(context)

        Truth.assertThat(storage.getReportsCount()).isEqualTo(0)
        Truth.assertThat(storage.getMaxSequence()).isEqualTo(0)
        Truth.assertThat(storage.getReports(1)).isNull()

        val sequence1 = 1L
        val content1 = "content1"
        storage.putReport(sequence1, content1, null)

        Truth.assertThat(storage.getReportsCount()).isEqualTo(1)
        Truth.assertThat(storage.getMaxSequence()).isEqualTo(sequence1)
        val list = storage.getReports(1)!!
        Truth.assertThat(list.size).isEqualTo(1)
        Truth.assertThat(list[0].sequence).isEqualTo(sequence1)
        Truth.assertThat(list[0].content).isEqualTo(content1)
        Truth.assertThat(list[0].device).isNull()

        val sequence3 = 3L
        val content3 = "content3"
        val device3 = "dev3"
        storage.putReport(sequence3, content3, device3)

        Truth.assertThat(storage.getReportsCount()).isEqualTo(2)
        Truth.assertThat(storage.getMaxSequence()).isEqualTo(sequence3)

        run {
            val reports21 = storage.getReports(1)!!
            Truth.assertThat(reports21.size).isEqualTo(1)
            Truth.assertThat(reports21[0].content).isEqualTo(content1)
            Truth.assertThat(reports21[0].sequence).isEqualTo(sequence1)
            Truth.assertThat(reports21[0].device).isNull()
        }

        run {
            val reports22 = storage.getReports(2)!!
            Truth.assertThat(reports22.size).isEqualTo(2)
            Truth.assertThat(reports22[0].content).isEqualTo(content1)
            Truth.assertThat(reports22[0].sequence).isEqualTo(sequence1)
            Truth.assertThat(reports22[0].device).isNull()
            Truth.assertThat(reports22[1].content).isEqualTo(content3)
            Truth.assertThat(reports22[1].sequence).isEqualTo(sequence3)
            Truth.assertThat(reports22[1].device).isEqualTo(device3)
        }

        run {
            val reports23 = storage.getReports(3)!!
            Truth.assertThat(reports23.size).isEqualTo(2)
            Truth.assertThat(reports23[0].content).isEqualTo(content1)
            Truth.assertThat(reports23[0].sequence).isEqualTo(sequence1)
            Truth.assertThat(reports23[0].device).isNull()
            Truth.assertThat(reports23[1].content).isEqualTo(content3)
            Truth.assertThat(reports23[1].sequence).isEqualTo(sequence3)
            Truth.assertThat(reports23[1].device).isEqualTo(device3)
        }

        storage.removeEarlyReports(sequence1)
        Truth.assertThat(storage.getReportsCount()).isEqualTo(1)
        Truth.assertThat(storage.getMaxSequence()).isEqualTo(sequence3)

        run {
            val reports33 = storage.getReports(3)!!
            Truth.assertThat(reports33.size).isEqualTo(1)
            Truth.assertThat(reports33[0].content).isEqualTo(content3)
            Truth.assertThat(reports33[0].sequence).isEqualTo(sequence3)
            Truth.assertThat(reports33[0].device).isEqualTo(device3)
        }

        storage.removeEarlyReports(sequence3)
        Truth.assertThat(storage.getReportsCount()).isEqualTo(0)
        Truth.assertThat(storage.getMaxSequence()).isEqualTo(0)
        Truth.assertThat(storage.getReports(1)).isNull()
    }

    @Test
    fun putDuplicateEvent() {
        val storage = ReportsStorage(context)

        Truth.assertThat(storage.getMaxSequence()).isEqualTo(0)
        Truth.assertThat(storage.getReports(1)).isNull()

        val sequence1 = 1L
        val content1 = "content1"
        storage.putReport(sequence1, content1, null)

        Truth.assertThat(storage.getMaxSequence()).isEqualTo(sequence1)
        val reports1 = storage.getReports(1)!!
        Truth.assertThat(reports1.size).isEqualTo(1)
        Truth.assertThat(reports1[0].content).isEqualTo(content1)
        Truth.assertThat(reports1[0].sequence).isEqualTo(sequence1)
        Truth.assertThat(reports1[0].device).isNull()

        storage.putReport(sequence1, content1, null)

        Truth.assertThat(storage.getMaxSequence()).isEqualTo(sequence1)
        val reports2 = storage.getReports(1)!!
        Truth.assertThat(reports2.size).isEqualTo(1)
        Truth.assertThat(reports2[0].content).isEqualTo(content1)
        Truth.assertThat(reports2[0].sequence).isEqualTo(sequence1)
        Truth.assertThat(reports2[0].device).isNull()
    }
}