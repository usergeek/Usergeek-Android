package com.usergeek.stream

import android.util.Log

object Logger {
    var level = Log.INFO
    var consumer : LogConsumer = DefaultLogConsumer()
}

inline fun Any.debug(messageSupplier: () -> String) {
    log(Log.DEBUG, messageSupplier, null)
}

inline fun Any.info(messageSupplier: () -> String) {
    log(Log.INFO, messageSupplier, null)
}

inline fun Any.warning(messageSupplier: () -> String) {
    log(Log.WARN, messageSupplier, null)
}

inline fun Any.error(messageSupplier: () -> String, th: Throwable) {
    log(Log.ERROR, messageSupplier, th)
}

inline fun Any.error(messageSupplier: () -> String) {
    log(Log.ERROR, messageSupplier, null)
}

inline fun Any.log(level: Int, messageSupplier: () -> String, th: Throwable?) {
    if (Logger.level <= level) {
        val tag = "STATISTICS/${this.javaClass.simpleName}"
        val message = messageSupplier()
        Logger.consumer.acceptLog(level, tag, message, th)
    }
}

interface LogConsumer {
    fun acceptLog(logLevel: Int, tag: String, message: String, th: Throwable?)
}

class DefaultLogConsumer: LogConsumer {

    override fun acceptLog(logLevel: Int, tag: String, message: String, th: Throwable?) {
        when (logLevel) {
            Log.ERROR -> Log.e(tag, message, th)
            Log.WARN -> Log.w(tag, message, th)
            Log.INFO -> Log.i(tag, message, th)
            Log.DEBUG -> Log.d(tag, message, th)
        }
    }
}