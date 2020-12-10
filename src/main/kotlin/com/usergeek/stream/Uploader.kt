package com.usergeek.stream

import android.os.Handler
import android.os.HandlerThread
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPOutputStream
import kotlin.text.Charsets.UTF_8

class Uploader(private val configuration: Configuration) {

    internal val sendThread = SendThread()

    init {
        sendThread.start()
    }

    internal fun uploadReports(reportsContent: String, callback: (success: Boolean) -> Unit) {
        sendThread.post {
            debug { String.format("Upload reports: %s.", reportsContent) }

            try {
                val encodedContent = encodeContent(reportsContent)

                val response = doPostRequest(
                    configuration.serverUrl,
                    configuration.connectTimeoutMillis,
                    configuration.readTimeoutMillis,
                    configuration.apiKey,
                    encodedContent.first,
                    encodedContent.second
                )

                when (response) {
                    200 -> callback(true)
                    else -> {
                        debug {
                            String.format(
                                "Wrong response while do post request: %s",
                                response
                            )
                        }


                        callback(false)
                    }
                }
            } catch (th: Throwable) {
                debug { String.format("Error while do post request: %s", th.message) }
                callback(false)
            }
        }
    }

    private fun doPostRequest(
        url: URL,
        connectTimeoutMillis: Long, readTimeoutMillis: Long,
        apiKey: String,
        content: ByteArray,
        contentEncoding: String?
    ): Int {
        val connection = url.openConnection() as HttpURLConnection
        try {
            with(connection) {
                connectTimeout = connectTimeoutMillis.toInt()
                readTimeout = readTimeoutMillis.toInt()
                doOutput = true
                requestMethod = "POST"
                setRequestProperty(
                    "User-Agent",
                    "${Formats.LIBRARY_NAME}/${Formats.LIBRARY_VERSION}"
                )
                setRequestProperty("Api-Version", Formats.API_VERSION)
                setRequestProperty("Api-Key", apiKey)
                setRequestProperty("Content-Type", "application/json")
                contentEncoding?.let {
                    setRequestProperty("Content-Encoding", it)
                }

                setFixedLengthStreamingMode(content.size)

                BufferedOutputStream(outputStream).use { out ->
                    out.write(content)
                    out.flush()
                }

                return connection.responseCode
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun encodeContent(content: String): Pair<ByteArray, String?> {
        if (content.length > 200) {
            when (configuration.uploadContentEncoding) {
                "gzip" -> {
                    val gzipContent = gzip(content)
                    debug {
                        String.format(
                            "Use gzip encoding for upload, original size: %s, compressed: %s",
                            content.length, gzipContent.size
                        )
                    }
                    return Pair(gzipContent, "gzip")
                }
            }
        }

        return Pair(content.toByteArray(), null)
    }

    private fun gzip(content: String): ByteArray {
        val buffer = ByteArrayOutputStream(content.length / 2)

        GZIPOutputStream(buffer).bufferedWriter(UTF_8).use {
            it.write(content)
            it.flush()
        }

        return buffer.toByteArray()
    }

    internal class SendThread : HandlerThread("sendStatisticsThread") {
        private val handler by lazy { Handler(looper) }

        fun post(task: () -> Unit) {
            handler.post(task)
        }
    }

}

