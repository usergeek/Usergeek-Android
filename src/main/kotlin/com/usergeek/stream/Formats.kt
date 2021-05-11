package com.usergeek.stream

import androidx.core.util.Supplier
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object Formats {

    const val LIBRARY_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
    const val LIBRARY_VERSION = BuildConfig.VERSION_NAME

    const val SERVER_URL = "https://stream.usergeek.com/collect"
    const val API_VERSION = "1"


    object ReportsField {
        const val UPLOAD_TIME = "t"
        const val LIBRARY = "l"
        const val DEVICE = "d"
        const val REPORTS = "r"
    }

    object LibraryField {
        const val NAME = "n"
        const val VERSION = "v"
    }

    object ReportField {
        const val DEVICE_ID = "di"
        const val USER_ID = "ui"
        const val TIME = "t"
        const val SEQUENCE = "s"
        const val EVENT = "e"
        const val USER = "u"
    }

    object EventField {
        const val NAME = "n"
        const val NAME_WARNING = "nw"
    }

    object ContentField {
        const val PROPERTIES = "p"
        const val PROPERTIES_WARNING = "pw"
    }

    object PropertyField {
        const val NAME = "n"
        const val NAME_WARNING = "nw"
        const val VALUE_TYPE = "vt"
        const val VALUE = "v"
        const val VALUE_WARNING = "vw"
        const val OPERATION = "o"
    }

    object PropertyValueType {
        const val UTC_TIME = "utc_time"
    }

    object Warning {
        const val TRUNCATED = "truncated"
        const val UNSUPPORTED_TYPE = "unsupportedType"
        const val RESOLVE_ERROR = "resolveError"
    }

    object PropertyOperation {
        const val SET = "s"
        const val SET_ONCE = "so"
        const val UNSET = "u"
        const val INCREMENT = "i"
        const val APPEND = "a"
        const val REMOVE = "r"
    }

    object DefaultEvents {
        const val START_APP = "StartApp"
        const val START_SESSION = "StartSession"
    }


    const val PROPERTIES_MAX_COUNT = 128

    const val EVENT_NAME_MAX_LENGTH = 64
    const val PROPERTY_NAME_MAX_LENGTH = 512
    const val PROPERTY_VALUE_MAX_LENGTH = 1024

    internal fun buildUploadReportsContent(reports: List<ReportsStorage.Report>): Pair<Long, String> {
        val uploadReports = getUploadReports(reports)
        return Pair(
            uploadReports.first.first,
            buildUploadReportsContent(uploadReports.first.second, uploadReports.second)
        )
    }

    internal fun getUploadReports(reports: List<ReportsStorage.Report>): Pair<Pair<Long, String?>, List<String>> {
        val iterator = reports.iterator()
        val report = iterator.next()

        val list = ArrayList<String>()
        list.add(report.content)
        var maxSequence = report.sequence
        var deviceContent = report.device

        while (iterator.hasNext()) {
            val nextReport = iterator.next()
            val nextDeviceContent = nextReport.device

            if (nextDeviceContent != deviceContent) {
                break
            }

            maxSequence = nextReport.sequence
            deviceContent = nextDeviceContent
            list.add(nextReport.content)
        }

        return Pair(Pair(maxSequence, deviceContent), list)
    }

    internal fun buildUploadReportsContent(
        deviceContent: String?,
        reports: List<String>
    ): String {
        val uploadTime = "\"${ReportsField.UPLOAD_TIME}\":${System.currentTimeMillis()},"

        val libraryPart =
            "\"${ReportsField.LIBRARY}\":{\"${LibraryField.NAME}\":\"${LIBRARY_NAME}\",\"${LibraryField.VERSION}\":\"${LIBRARY_VERSION}\"},"

        val devicePart = deviceContent?.let {
            "\"${ReportsField.DEVICE}\":${it},"
        } ?: ""

        val reportsPart = "\"${ReportsField.REPORTS}\":${
            reports.joinToString(
                separator = ",",
                prefix = "[",
                postfix = "]"
            )
        }"

        return "{${uploadTime}${libraryPart}${devicePart}${reportsPart}}"
    }

    internal fun buildReport(
        deviceId: String,
        userId: String?,
        time: Long,
        sequence: Long,
        userContent: JSONObject?,
        eventContent: JSONObject?
    ): JSONObject {
        return JSONObject().run {
            put(ReportField.DEVICE_ID, deviceId)

            userId?.let {
                put(ReportField.USER_ID, it)
            }

            put(ReportField.TIME, time)
            put(ReportField.SEQUENCE, sequence)

            userContent?.let {
                put(ReportField.USER, it)
            }

            eventContent?.let {
                put(ReportField.EVENT, it)
            }

            this
        }
    }

    internal fun buildPropertiesContent(userProperties: BaseProperties<out Any?>): JSONObject? {
        val content = JSONObject()
        content.putPropertiesList(
            userProperties.properties.iterator(),
            { entry -> entry.key },
            { entry -> entry.value }
        )
        return if (content.length() == 0) null else content
    }

    internal fun buildEventContent(
        eventName: String,
        eventProperties: BaseProperties<Any?>?
    ): JSONObject? {
        val content = eventProperties?.let {
            buildPropertiesContent(it)
        } ?: JSONObject()

        content.putEventName(eventName)
        return content
    }


    internal fun buildPropertyContent(name: String, value: Any?): JSONObject {
        return JSONObject().run {
            putPropertyName(name)
            putPropertyValue(value)
            this
        }
    }
}

internal fun <V> JSONObject.putPropertiesList(
    properties: Iterator<V>?,
    propertyNameSupplier: (V) -> String,
    propertyValueSupplier: (V) -> Any?
) {
    properties?.let {
        val list = JSONArray()
        var warning: String? = null

        for (property in it) {
            val propertyName = propertyNameSupplier(property)
            val propertyValue = propertyValueSupplier(property)

            val propertyContent = Formats.buildPropertyContent(propertyName, propertyValue)
            list.put(propertyContent)

            if (list.length() >= Formats.PROPERTIES_MAX_COUNT) {
                warning = Formats.Warning.TRUNCATED
                break
            }
        }

        if (list.length() > 0) {
            put(Formats.ContentField.PROPERTIES, list)
            if (warning != null) {
                put(Formats.ContentField.PROPERTIES_WARNING, warning)
            }
        }
    }
}

internal fun JSONObject.putEventName(name: String) {
    when {
        name.length > Formats.EVENT_NAME_MAX_LENGTH -> {
            put(Formats.EventField.NAME, name.substring(0, Formats.EVENT_NAME_MAX_LENGTH))
            put(Formats.EventField.NAME_WARNING, Formats.Warning.TRUNCATED)
        }
        else -> put(Formats.EventField.NAME, name)
    }
}

internal fun JSONObject.putPropertyName(name: String) {
    when {
        name.length > Formats.PROPERTY_NAME_MAX_LENGTH -> {
            put(Formats.PropertyField.NAME, name.substring(0, Formats.PROPERTY_NAME_MAX_LENGTH))
            put(Formats.PropertyField.NAME_WARNING, Formats.Warning.TRUNCATED)
        }
        else -> put(Formats.PropertyField.NAME, name)
    }
}

internal fun JSONObject.putPropertyValue(value: Any?) {
    when (value) {
        is String -> {
            when {
                value.length > Formats.PROPERTY_VALUE_MAX_LENGTH -> {
                    put(
                        Formats.PropertyField.VALUE, value.substring(
                            0,
                            Formats.PROPERTY_VALUE_MAX_LENGTH
                        )
                    )
                    put(Formats.PropertyField.VALUE_WARNING, Formats.Warning.TRUNCATED)

                    warning { String.format("Truncate property value '%s'", value) }
                }
                else -> {
                    put(Formats.PropertyField.VALUE, value)
                }
            }
        }
        is Boolean, is Long, is Int, is Short, is Byte, is Double, is Float -> {
            put(Formats.PropertyField.VALUE, value)
        }
        is Date -> {
            put(Formats.PropertyField.VALUE_TYPE, "utc_time")
            put(Formats.PropertyField.VALUE, value.time)
        }
        is UserProperties.UserPropertyValue -> {
            putPropertyValue(value.value)
            put(Formats.PropertyField.OPERATION, value.operation)
        }
        is Supplier<out Any?> -> {
            try {
                putPropertyValue(value.get())
            } catch (th: Throwable) {
                put(Formats.PropertyField.VALUE_WARNING, Formats.Warning.RESOLVE_ERROR)

                warning {
                    String.format(
                        "Error resolve property value from supplier '%s': %s", value, th
                    )
                }
            }
        }
        null -> {
            put(Formats.PropertyField.VALUE, JSONObject.NULL)
        }
        else -> {
            put(Formats.PropertyField.VALUE_WARNING, Formats.Warning.UNSUPPORTED_TYPE)
            warning { String.format("Unsupported property value type: %s", value) }
        }
    }
}
