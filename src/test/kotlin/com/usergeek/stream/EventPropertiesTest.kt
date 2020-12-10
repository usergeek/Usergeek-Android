package com.usergeek.stream

import com.google.common.truth.Truth
import org.junit.Test

class EventPropertiesTest {

    @Test
    fun test() {
        val eventProperties = EventProperties()
        val content = eventProperties.properties

        Truth.assertThat(content.size).isEqualTo(0)

        val property1 = "property1"
        val value1 = "value2"
        val value12 = "value23"

        val property2 = " property2"
        val value2 = " value2 "

        val property3 = BaseTest.getStringWithLength(Formats.PROPERTY_NAME_MAX_LENGTH + 1)
        val value3 = "value2"

        val property4 = ""
        val value4 = null

        val property5 = "   "
        val value5 = BaseTest.getStringWithLength(Formats.PROPERTY_VALUE_MAX_LENGTH + 1)

        run {
            eventProperties.set(property1, value1)
            eventProperties.set(property2, value2)
            eventProperties.set(property3, value3)
            eventProperties.set(property4, value4)
            eventProperties.set(property5, value5)
            Truth.assertThat(content.size).isEqualTo(5)
            Truth.assertThat(content[property1]).isEqualTo(value1)
            Truth.assertThat(content[property2]).isEqualTo(value2)
            Truth.assertThat(content[property3]).isEqualTo(value3)
            Truth.assertThat(content[property4]).isNull()
            Truth.assertThat(content[property5]).isEqualTo(value5)
        }

        run {
            eventProperties.set(property1, value12)
            Truth.assertThat(content.size).isEqualTo(5)
            Truth.assertThat(content[property1]).isEqualTo(value12)
        }
    }

}