package com.usergeek.stream

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test

class UserPropertiesTest {

    @Test
    fun test() {
        val userProperties = UserProperties()
        val content = userProperties.properties

        assertThat(content.size).isEqualTo(0)

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
            userProperties.unset(property1)
            assertThat(content.size).isEqualTo(1)
            assertThat(content[property1]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property1]!!.value).isNull()
        }

        run {
            userProperties.unset(property2)
            assertThat(content.size).isEqualTo(2)
            assertThat(content[property2]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property2]!!.value).isNull()
        }

        run {
            userProperties.unset(property3)
            assertThat(content.size).isEqualTo(3)
            assertThat(content[property3]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property3]!!.value).isNull()
        }

        run {
            userProperties.unset(property4)
            assertThat(content.size).isEqualTo(4)
            assertThat(content[property4]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property4]!!.value).isNull()
        }

        run {
            userProperties.unset(property5)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property1]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property1]!!.value).isNull()
            assertThat(content[property2]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property2]!!.value).isNull()
            assertThat(content[property3]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property3]!!.value).isNull()
            assertThat(content[property4]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property4]!!.value).isNull()
            assertThat(content[property5]!!.operation).isEqualTo(Formats.PropertyOperation.UNSET)
            assertThat(content[property5]!!.value).isNull()
        }

        run {
            userProperties.set(property1, value1)
            userProperties.set(property2, value2)
            userProperties.set(property3, value3)
            userProperties.set(property4, value4)
            userProperties.setOnce(property5, value5)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property1]!!.operation).isEqualTo(Formats.PropertyOperation.SET)
            assertThat(content[property1]!!.value).isEqualTo(value1)
            assertThat(content[property2]!!.operation).isEqualTo(Formats.PropertyOperation.SET)
            assertThat(content[property2]!!.value).isEqualTo(value2)
            assertThat(content[property3]!!.operation).isEqualTo(Formats.PropertyOperation.SET)
            assertThat(content[property3]!!.value).isEqualTo(value3)
            assertThat(content[property4]!!.operation).isEqualTo(Formats.PropertyOperation.SET)
            assertThat(content[property4]!!.value).isNull()
            assertThat(content[property5]!!.operation).isEqualTo(Formats.PropertyOperation.SET_ONCE)
            assertThat(content[property5]!!.value).isEqualTo(value5)
        }

        run {
            userProperties.set(property1, value12)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property1]!!.operation).isEqualTo(Formats.PropertyOperation.SET)
            assertThat(content[property1]!!.value).isEqualTo(value12)
        }

       run {
            userProperties.increment(property2, 34)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property2]!!.operation).isEqualTo(Formats.PropertyOperation.INCREMENT)
            assertThat(content[property2]!!.value).isEqualTo(34)
        }

       run {
            userProperties.increment(property3, -67.78)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property3]!!.operation).isEqualTo(Formats.PropertyOperation.INCREMENT)
            assertThat(content[property3]!!.value).isEqualTo(-67.78)
        }

       run {
            userProperties.append(property2, "aaa")
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property2]!!.operation).isEqualTo(Formats.PropertyOperation.APPEND)
            assertThat(content[property2]!!.value).isEqualTo("aaa")
        }

       run {
            userProperties.remove(property3, value2)
            assertThat(content.size).isEqualTo(5)
            assertThat(content[property3]!!.operation).isEqualTo(Formats.PropertyOperation.REMOVE)
            assertThat(content[property3]!!.value).isEqualTo(value2)
        }

        "abc" isEqualsTo "abc"
    }



    private infix fun Any.isEqualsTo(expected: Any?) {
        assertEquals(expected, this)
    }
}