package com.usergeek.stream

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider

open class BaseTest {

    companion object {

        fun getStringWithLength(length: Int): String {
            return String(CharArray(length) { 'a' })
        }
    }

    lateinit var context: Context

    open fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()

        Logger.level = Log.DEBUG
        Logger.consumer = object : LogConsumer {
            override fun acceptLog(logLevel: Int, tag: String, message: String, th: Throwable?) {
                println("${getLevelLabel(logLevel)} [$tag] $message")
                th?.printStackTrace()
            }

            private fun getLevelLabel(logLevel: Int): String {
                return when (logLevel) {
                    Log.ERROR -> "ERROR"
                    Log.WARN -> "WARN"
                    Log.INFO -> "INFO"
                    Log.DEBUG -> "DEBUG"
                    else -> "UNKNOWN"
                }
            }
        }
    }

    open fun setDown() {
        Usergeek.wrapper.client = null
    }

}