package com.usergeek.stream

import androidx.core.util.Supplier
import com.google.common.truth.Truth
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
class FormatsTest : BaseTest() {

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun putPropertiesList() {
        val jsonObject = JSONObject()
        jsonObject.putPropertiesList(
            listOf("a1", "a2").iterator(),
            { s -> s },
            { s -> s + "v" })

        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        val list = jsonObject[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(2)
        val p1 = list[0] as JSONObject
        val p2 = list[1] as JSONObject
        Truth.assertThat(p1.length()).isEqualTo(2)
        Truth.assertThat(p1[Formats.PropertyField.NAME]).isEqualTo("a1")
        Truth.assertThat(p1[Formats.PropertyField.VALUE]).isEqualTo("a1v")
        Truth.assertThat(p2.length()).isEqualTo(2)
        Truth.assertThat(p2[Formats.PropertyField.NAME]).isEqualTo("a2")
        Truth.assertThat(p2[Formats.PropertyField.VALUE]).isEqualTo("a2v")
    }

    @Test
    fun putLongPropertiesList() {
        val arrayList = ArrayList<String>()
        for (i in 0..Formats.PROPERTIES_MAX_COUNT) {
            arrayList.add("a$i")
        }

        val jsonObject = JSONObject()
        jsonObject.putPropertiesList(
            arrayList.iterator(),
            { s -> s },
            { s -> s + "v" })

        Truth.assertThat(jsonObject.length()).isEqualTo(2)
        Truth.assertThat(jsonObject[Formats.ContentField.PROPERTIES_WARNING])
            .isEqualTo(Formats.Warning.TRUNCATED)
        val list = jsonObject[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(Formats.PROPERTIES_MAX_COUNT)
        val p1 = list[0] as JSONObject
        Truth.assertThat(p1.length()).isEqualTo(2)
        Truth.assertThat(p1[Formats.PropertyField.NAME]).isEqualTo("a0")
        Truth.assertThat(p1[Formats.PropertyField.VALUE]).isEqualTo("a0v")
    }

    @Test
    fun buildPropertyContent() {
        val jsonObject = Formats.buildPropertyContent("pr", "abc")
        Truth.assertThat(jsonObject.length()).isEqualTo(2)
        Truth.assertThat(jsonObject[Formats.PropertyField.NAME]).isEqualTo("pr")
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
    }

    @Test
    fun putEventName() {
        val jsonObject = JSONObject()
        jsonObject.putEventName("abc")
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.EventField.NAME]).isEqualTo("abc")
    }

    @Test
    fun putEventLongName() {
        val jsonObject = JSONObject()
        jsonObject.putEventName(getStringWithLength(Formats.EVENT_NAME_MAX_LENGTH + 1))
        Truth.assertThat(jsonObject.length()).isEqualTo(2)
        Truth.assertThat(jsonObject[Formats.EventField.NAME])
            .isEqualTo(getStringWithLength(Formats.EVENT_NAME_MAX_LENGTH))
        Truth.assertThat(jsonObject[Formats.EventField.NAME_WARNING])
            .isEqualTo(Formats.Warning.TRUNCATED)
    }

    @Test
    fun putPropertyName() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyName("abc")
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.NAME]).isEqualTo("abc")
    }

    @Test
    fun putPropertyLongName() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyName(getStringWithLength(Formats.PROPERTY_NAME_MAX_LENGTH + 1))
        Truth.assertThat(jsonObject.length()).isEqualTo(2)
        Truth.assertThat(jsonObject[Formats.PropertyField.NAME])
            .isEqualTo(getStringWithLength(Formats.PROPERTY_NAME_MAX_LENGTH))
        Truth.assertThat(jsonObject[Formats.PropertyField.NAME_WARNING])
            .isEqualTo(Formats.Warning.TRUNCATED)
    }

    @Test
    fun putPropertyStringValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue("abc")
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
    }

    @Test
    fun putPropertyBooleanValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(true)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(true)
    }

    @Test
    fun putPropertyLongValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(12L)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(12L)
    }

    @Test
    fun putPropertyIntValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(12)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(12L)
    }

    @Test
    fun putPropertyShortValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(12.toShort())
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(12L)
    }

    @Test
    fun putPropertyByteValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(12.toByte())
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(12L)
    }

    @Test
    fun putPropertyLongStringValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(getStringWithLength(Formats.PROPERTY_VALUE_MAX_LENGTH + 1))
        Truth.assertThat(jsonObject.length()).isEqualTo(2)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE])
            .isEqualTo(getStringWithLength(Formats.PROPERTY_VALUE_MAX_LENGTH))
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE_WARNING])
            .isEqualTo(Formats.Warning.TRUNCATED)
    }

    @Test
    fun putPropertyFloatValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(123.5f)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(123.5f)
    }

    @Test
    fun putPropertyDoubleValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(23.4)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(23.4)
    }

    @Test
    fun putPropertyNullValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(null)
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(JSONObject.NULL)
    }

    @Test
    fun putPropertyWrongValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(AtomicBoolean())
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE_WARNING])
            .isEqualTo(Formats.Warning.UNSUPPORTED_TYPE)
    }

    @Test
    fun putPropertyUserPropertyValue() {
        kotlin.run {
            val jsonObject = JSONObject()
            jsonObject.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.SET,
                    "abc"
                )
            )
            Truth.assertThat(jsonObject.length()).isEqualTo(2)
            Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
            Truth.assertThat(jsonObject[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.SET)
        }

        kotlin.run {
            val jsonObject2 = JSONObject()
            jsonObject2.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.UNSET,
                    null
                )
            )
            Truth.assertThat(jsonObject2.length()).isEqualTo(2)
            Truth.assertThat(jsonObject2[Formats.PropertyField.VALUE]).isEqualTo(JSONObject.NULL)
            Truth.assertThat(jsonObject2[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.UNSET)
        }

        kotlin.run {
            val jsonObject = JSONObject()
            jsonObject.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.INCREMENT,
                    23
                )
            )
            Truth.assertThat(jsonObject.length()).isEqualTo(2)
            Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(23)
            Truth.assertThat(jsonObject[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.INCREMENT)
        }

        kotlin.run {
            val jsonObject = JSONObject()
            jsonObject.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.INCREMENT,
                    -43.45
                )
            )
            Truth.assertThat(jsonObject.length()).isEqualTo(2)
            Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo(-43.45)
            Truth.assertThat(jsonObject[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.INCREMENT)
        }

        kotlin.run {
            val jsonObject = JSONObject()
            jsonObject.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.APPEND,
                    "abc"
                )
            )
            Truth.assertThat(jsonObject.length()).isEqualTo(2)
            Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
            Truth.assertThat(jsonObject[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.APPEND)
        }

        kotlin.run {
            val jsonObject = JSONObject()
            jsonObject.putPropertyValue(
                UserProperties.UserPropertyValue(
                    Formats.PropertyOperation.REMOVE,
                    "abc"
                )
            )
            Truth.assertThat(jsonObject.length()).isEqualTo(2)
            Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
            Truth.assertThat(jsonObject[Formats.PropertyField.OPERATION])
                .isEqualTo(Formats.PropertyOperation.REMOVE)
        }
    }

    @Test
    fun putPropertySupplierValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(Supplier<String> { "abc" })
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE]).isEqualTo("abc")
    }

    @Test
    fun putPropertySupplierErrorValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(Supplier<String> { throw IllegalArgumentException() })
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE_WARNING])
            .isEqualTo(Formats.Warning.RESOLVE_ERROR)
    }

    @Test
    fun putPropertySupplierWrongValue() {
        val jsonObject = JSONObject()
        jsonObject.putPropertyValue(Supplier<AtomicInteger> { AtomicInteger(3) })
        Truth.assertThat(jsonObject.length()).isEqualTo(1)
        Truth.assertThat(jsonObject[Formats.PropertyField.VALUE_WARNING])
            .isEqualTo(Formats.Warning.UNSUPPORTED_TYPE)
    }

    @Test
    fun validateEmptyUserProperties() {
        val userProperties = UserProperties()
        Truth.assertThat(Formats.buildPropertiesContent(userProperties)).isNull()
    }

    @Test
    fun validateSetUserProperties() {
        val userProperties = UserProperties()
            .set("a", "1")

        val validateUserProperties = Formats.buildPropertiesContent(userProperties)
//        println(">>>>>> " + validateUserProperties)

        Truth.assertThat(validateUserProperties!!.length()).isEqualTo(1)
        val list = validateUserProperties[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val obj = list.get(0) as JSONObject
        Truth.assertThat(obj.length()).isEqualTo(3)
        Truth.assertThat(obj.get(Formats.PropertyField.NAME)).isEqualTo("a")
        Truth.assertThat(obj.get(Formats.PropertyField.VALUE)).isEqualTo("1")
        Truth.assertThat(obj.get(Formats.PropertyField.OPERATION))
            .isEqualTo(Formats.PropertyOperation.SET)
    }

    @Test
    fun validateUnsetUserProperties() {
        val userProperties = UserProperties()
            .unset("a")

        val validateUserProperties = Formats.buildPropertiesContent(userProperties)
//        println(">>>>>> " + validateUserProperties)

        Truth.assertThat(validateUserProperties!!.length()).isEqualTo(1)
        val list = validateUserProperties[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val obj = list.get(0) as JSONObject
        Truth.assertThat(obj.length()).isEqualTo(3)
        Truth.assertThat(obj.get(Formats.PropertyField.NAME)).isEqualTo("a")
        Truth.assertThat(obj.get(Formats.PropertyField.VALUE)).isEqualTo(JSONObject.NULL)
        Truth.assertThat(obj.get(Formats.PropertyField.OPERATION))
            .isEqualTo(Formats.PropertyOperation.UNSET)
    }

    @Test
    fun validateDuplicateUserProperties() {
        val userProperties = UserProperties()
            .set("a", "1")
            .set("a", "2")

        val validateUserProperties = Formats.buildPropertiesContent(userProperties)
//        println(">>>>>> " + validateUserProperties)

        Truth.assertThat(validateUserProperties!!.length()).isEqualTo(1)
        val list = validateUserProperties[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val obj = list.get(0) as JSONObject
        Truth.assertThat(obj.length()).isEqualTo(3)
        Truth.assertThat(obj.get(Formats.PropertyField.NAME)).isEqualTo("a")
        Truth.assertThat(obj.get(Formats.PropertyField.VALUE)).isEqualTo("2")
        Truth.assertThat(obj.get(Formats.PropertyField.OPERATION))
            .isEqualTo(Formats.PropertyOperation.SET)
    }

    @Test
    fun validateEmptyEventProperties() {
        val eventProperties = EventProperties()
        Truth.assertThat(Formats.buildPropertiesContent(eventProperties)).isNull()
    }

    @Test
    fun validateSetEventProperties() {
        val eventProperties = EventProperties()
            .set("a", "1")

        val validateEventProperties = Formats.buildPropertiesContent(eventProperties)
//        println(">>>>>> " + validateUserProperties)

        Truth.assertThat(validateEventProperties!!.length()).isEqualTo(1)
        val list = validateEventProperties[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val obj = list.get(0) as JSONObject
        Truth.assertThat(obj.length()).isEqualTo(2)
        Truth.assertThat(obj.get(Formats.PropertyField.NAME)).isEqualTo("a")
        Truth.assertThat(obj.get(Formats.PropertyField.VALUE)).isEqualTo("1")
    }

    @Test
    fun validateDuplicateEventProperties() {
        val eventProperties = EventProperties()
            .set("a", "1")
            .set("a", "2")

        val validateEventProperties = Formats.buildPropertiesContent(eventProperties)
//        println(">>>>>> " + validateUserProperties)

        Truth.assertThat(validateEventProperties!!.length()).isEqualTo(1)
        val list = validateEventProperties[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val obj = list.get(0) as JSONObject
        Truth.assertThat(obj.length()).isEqualTo(2)
        Truth.assertThat(obj.get(Formats.PropertyField.NAME)).isEqualTo("a")
        Truth.assertThat(obj.get(Formats.PropertyField.VALUE)).isEqualTo("2")
    }


    @Test
    fun validateBuildEventContent() {
        val content = Formats.buildEventContent("abc", null)
        Truth.assertThat(content!!.length()).isEqualTo(1)
        Truth.assertThat(content[Formats.EventField.NAME]).isEqualTo("abc")

        val content2 = Formats.buildEventContent("abc", EventProperties())
        Truth.assertThat(content2!!.length()).isEqualTo(1)
        Truth.assertThat(content2[Formats.EventField.NAME]).isEqualTo("abc")

        val content3 = Formats.buildEventContent("abc", EventProperties().set("p1", "v1"))
        Truth.assertThat(content3!!.length()).isEqualTo(2)
        Truth.assertThat(content3[Formats.EventField.NAME]).isEqualTo("abc")
        val list = content3[Formats.ContentField.PROPERTIES] as JSONArray
        Truth.assertThat(list.length()).isEqualTo(1)
        val property = list.get(0) as JSONObject
        Truth.assertThat(property.length()).isEqualTo(2)
        Truth.assertThat(property[Formats.PropertyField.NAME]).isEqualTo("p1")
        Truth.assertThat(property[Formats.PropertyField.VALUE]).isEqualTo("v1")
    }

    @Test
    fun validateBuildReport() {
        run {
            val content = Formats.buildReport("device", null, 123L, 1L, null, null)
            Truth.assertThat(content.length()).isEqualTo(3)
            Truth.assertThat(content[Formats.ReportField.DEVICE_ID]).isEqualTo("device")
            Truth.assertThat(content[Formats.ReportField.TIME]).isEqualTo(123L)
            Truth.assertThat(content[Formats.ReportField.SEQUENCE]).isEqualTo(1L)
        }

        run {
            val content = Formats.buildReport(
                "device",
                "user",
                123L,
                1L,
                JSONObject().put("a", "b"),
                JSONObject()
            )
            Truth.assertThat(content.length()).isEqualTo(6)
            Truth.assertThat(content[Formats.ReportField.DEVICE_ID]).isEqualTo("device")
            Truth.assertThat(content[Formats.ReportField.USER_ID]).isEqualTo("user")
            Truth.assertThat(content[Formats.ReportField.TIME]).isEqualTo(123L)
            Truth.assertThat(content[Formats.ReportField.SEQUENCE]).isEqualTo(1L)
            Truth.assertThat((content[Formats.ReportField.EVENT] as JSONObject).length())
                .isEqualTo(0)
            Truth.assertThat((content[Formats.ReportField.USER] as JSONObject).length())
                .isEqualTo(1)
        }
    }

    @Test
    fun validateBuildUploadReportsContent() {
        run {
            val content = Formats.buildUploadReportsContent(null, listOf("report"))
            val json = JSONObject(content)

            Truth.assertThat(json.length()).isEqualTo(3)
            Truth.assertThat(json[Formats.ReportsField.UPLOAD_TIME]).isNotNull()
            val library = json[Formats.ReportsField.LIBRARY] as JSONObject
            Truth.assertThat(library.length()).isEqualTo(2)
            Truth.assertThat(library[Formats.LibraryField.NAME]).isEqualTo(Formats.LIBRARY_NAME)
            Truth.assertThat(library[Formats.LibraryField.VERSION])
                .isEqualTo(Formats.LIBRARY_VERSION)
            val reports = json[Formats.ReportsField.REPORTS] as JSONArray
            Truth.assertThat(reports.length()).isEqualTo(1)
            Truth.assertThat(reports[0]).isEqualTo("report")
        }

        run {
            val content = Formats.buildUploadReportsContent("{\"p\":\"v\"}", listOf("{\"aa\": 23}"))
            val json = JSONObject(content)

            Truth.assertThat(json.length()).isEqualTo(4)
            Truth.assertThat(json[Formats.ReportsField.UPLOAD_TIME]).isNotNull()
            val device = json[Formats.ReportsField.DEVICE] as JSONObject
            Truth.assertThat(device.length()).isEqualTo(1)
            Truth.assertThat(device["p"]).isEqualTo("v")
            val reports = json[Formats.ReportsField.REPORTS] as JSONArray
            Truth.assertThat(reports.length()).isEqualTo(1)
            val obj = reports[0] as JSONObject
            Truth.assertThat(obj.length()).isEqualTo(1)
            Truth.assertThat(obj["aa"]).isEqualTo(23L)
        }
    }

    @Test
    fun getUploadReportsSingle() {
        run {
            val sequence = 123L
            val content = "content"

            val reports = listOf(
                ReportsStorage.Report(sequence, content, null)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence)
            Truth.assertThat(result.first.second).isNull()
            Truth.assertThat(result.second.size).isEqualTo(1)
            Truth.assertThat(result.second[0]).isEqualTo(content)
        }

        run {
            val sequence = 123L
            val content = "content"
            val device = "device"

            val reports = listOf(
                ReportsStorage.Report(sequence, content, device)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence)
            Truth.assertThat(result.first.second).isEqualTo(device)
            Truth.assertThat(result.second.size).isEqualTo(1)
            Truth.assertThat(result.second[0]).isEqualTo(content)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val sequence2 = 124L
            val content2 = "content2"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, null),
                ReportsStorage.Report(sequence2, content2, null)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence2)
            Truth.assertThat(result.first.second).isNull()
            Truth.assertThat(result.second.size).isEqualTo(2)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
            Truth.assertThat(result.second[1]).isEqualTo(content2)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val sequence2 = 124L
            val content2 = "content2"
            val device = "device"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device),
                ReportsStorage.Report(sequence2, content2, device)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence2)
            Truth.assertThat(result.first.second).isEqualTo(device)
            Truth.assertThat(result.second.size).isEqualTo(2)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
            Truth.assertThat(result.second[1]).isEqualTo(content2)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val device1 = "device1"
            val sequence2 = 124L
            val content2 = "content2"
            val device2 = "device2"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device1),
                ReportsStorage.Report(sequence2, content2, device2)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence1)
            Truth.assertThat(result.first.second).isEqualTo(device1)
            Truth.assertThat(result.second.size).isEqualTo(1)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val device1 = "device1"
            val sequence2 = 124L
            val content2 = "content2"
            val device2 = null

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device1),
                ReportsStorage.Report(sequence2, content2, device2)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence1)
            Truth.assertThat(result.first.second).isEqualTo(device1)
            Truth.assertThat(result.second.size).isEqualTo(1)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val device1 = null
            val sequence2 = 124L
            val content2 = "content2"
            val device2 = "device2"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device1),
                ReportsStorage.Report(sequence2, content2, device2)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence1)
            Truth.assertThat(result.first.second).isNull()
            Truth.assertThat(result.second.size).isEqualTo(1)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
        }

        run {
            val sequence1 = 123L
            val content1 = "content1"
            val device = "device"
            val sequence2 = 124L
            val content2 = "content2"
            val sequence3 = 125L
            val content3 = "content3"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device),
                ReportsStorage.Report(sequence2, content2, device),
                ReportsStorage.Report(sequence3, content3, device)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence3)
            Truth.assertThat(result.first.second).isEqualTo(device)
            Truth.assertThat(result.second.size).isEqualTo(3)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
            Truth.assertThat(result.second[1]).isEqualTo(content2)
            Truth.assertThat(result.second[2]).isEqualTo(content3)
        }


        run {
            val sequence1 = 123L
            val content1 = "content1"
            val device = "device"
            val sequence2 = 124L
            val content2 = "content2"
            val sequence3 = 125L
            val content3 = "content3"
            val device3 = "device3"

            val reports = listOf(
                ReportsStorage.Report(sequence1, content1, device),
                ReportsStorage.Report(sequence2, content2, device),
                ReportsStorage.Report(sequence3, content3, device3)
            )

            val result = Formats.getUploadReports(reports)

            Truth.assertThat(result.first.first).isEqualTo(sequence2)
            Truth.assertThat(result.first.second).isEqualTo(device)
            Truth.assertThat(result.second.size).isEqualTo(2)
            Truth.assertThat(result.second[0]).isEqualTo(content1)
            Truth.assertThat(result.second[1]).isEqualTo(content2)
        }
    }

}