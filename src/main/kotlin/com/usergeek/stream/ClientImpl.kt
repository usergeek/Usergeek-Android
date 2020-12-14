package com.usergeek.stream

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import org.json.JSONObject
import java.util.*
import kotlin.math.max

/**
 * Implementation.
 */
class ClientImpl(
    context: Context,
    apiKey: String,
    config: InitConfig
) : UsergeekClient {

    private val ctx = context.applicationContext

    private val configuration = Configuration(ctx, apiKey, config)
    internal val identifyStorage = IdentifyStorage(ctx)
    internal val reportsStorage = ReportsStorage(ctx)
    internal val uploader = Uploader(configuration)

    private var scheduleUpload = false
    private var uploading = false

    internal val logThread = LogThread()

    internal var deviceId = initDeviceId(config.initialDeviceId)
        private set

    internal var userId = initUserId(config.initialUserId)
        private set

    internal var sequence = reportsStorage.getMaxSequence()
        private set

    init {
        logThread.start()

        info {
            String.format(
                "Statistics initialized, deviceId: %s, userId: %s, config: %s",
                deviceId, userId, configuration
            )
        }

        if (config.enableStartAppEvent) {
            logEvent(Formats.DefaultEvents.START_APP)
        }

        config.enableSessionTracking?.let {
            SessionTracker.enableSessionTracking(it)
        }

        config.enableFlushOnClose?.registerActivityLifecycleCallbacks(object : LifecycleCallbacks() {
            @Override
            override fun onActivityPaused(activity: Activity) {
                flush()
            }
        })
    }


    // ------------------------------------------------------------------------
    // StatisticsClient interface
    // ------------------------------------------------------------------------

    override fun setUserId(userId: String): ClientImpl {
        if (userId.isBlank()) {
            warning { "Ignore setUserId, value is blank" }
            return this
        }

        logThread.post {
            identifyStorage.setUserId(userId)
            this.userId = userId

            info { String.format("Set new userId: %s", userId) }
        }
        return this
    }

    override fun resetUserId(regenerateDeviceId: Boolean): UsergeekClient {
        logThread.post {
            identifyStorage.removeUserId()
            this.userId = null

            info { "Reset userId" }

            if (regenerateDeviceId) {
                val newDeviceId = generateDeviceId()
                identifyStorage.setDeviceId(newDeviceId)
                deviceId = newDeviceId
            }

            info { String.format("Regenerate new deviceId: %s", deviceId) }
        }
        return this
    }

    override fun logUserProperties(userProperties: UserProperties): UsergeekClient {
        val time = System.currentTimeMillis()

        logThread.post {
            Formats.buildPropertiesContent(userProperties)?.let { userContent ->
                logReport(time, null, userContent)
            }
        }
        return this
    }

    override fun logEvent(eventName: String): UsergeekClient {
        val time = System.currentTimeMillis()

        logThread.post {
            Formats.buildEventContent(eventName, null)?.let { eventContent ->
                logReport(time, eventContent, null)
            }
        }
        return this
    }

    override fun logEvent(eventName: String, eventProperties: EventProperties): UsergeekClient {
        val time = System.currentTimeMillis()

        logThread.post {
            Formats.buildEventContent(eventName, eventProperties)?.let { eventContent ->
                logReport(time, eventContent, null)
            }
        }
        return this
    }

    override fun flush() {
        logThread.post {
            debug { "Flush" }
            uploadReports()
        }
    }

    override fun getDeviceId(): String {
        return deviceId
    }


    // ------------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------------

    private fun initDeviceId(initialDeviceId: String?): String {
        return identifyStorage.getDeviceId() ?: run {
            val deviceId = if (initialDeviceId.isNullOrBlank()) {
                generateDeviceId()
            } else {
                info { String.format("Set initial deviceId: %s", initialDeviceId) }
                initialDeviceId
            }

            identifyStorage.setDeviceId(deviceId)
            return deviceId
        }
    }

    private fun generateDeviceId(): String {
        return UUID.randomUUID().toString()
    }

    private fun initUserId(initialUserId: String?): String? {
        return identifyStorage.getUserId() ?: run {
            if (initialUserId.isNullOrBlank()) {
                null
            } else {
                info { String.format("Set initial userId: %s", initialUserId) }
                identifyStorage.setUserId(initialUserId)
                initialUserId
            }
        }
    }

    private fun logReport(time: Long, eventContent: JSONObject?, userContent: JSONObject?) {
        val reportSequence = ++this.sequence

        // save
        val report = Formats.buildReport(
            deviceId, userId,
            time, reportSequence,
            userContent, eventContent
        )
        val reportContent = report.toString()
        val reportDeviceContent = configuration.deviceProperties?.let { deviceProperties ->
            Formats.buildPropertiesContent(deviceProperties)?.toString()
        }
        reportsStorage.putReport(reportSequence, reportContent, reportDeviceContent)

        debug {
            String.format(
                "Saved report: %s, %s, %s",
                reportSequence,
                reportContent,
                reportDeviceContent
            )
        }

        // remove oldest if full
        val reportsCount = reportsStorage.getReportsCount()
        if (reportsCount > configuration.maxReportsCountInStorage) {
            val removeCount = reportsCount * configuration.removeReportsPercentWhenFull / 100
            val sequenceForRemove = reportSequence - max(1, reportsCount - removeCount)
            reportsStorage.removeEarlyReports(sequenceForRemove)

            debug {
                String.format(
                    "Removed old reports. Count: %s, Before: %s.",
                    removeCount,
                    sequenceForRemove
                )
            }
        }

        // upload signal
        if ((reportsCount % configuration.uploadReportsCount) == 0L) {
            uploadReports()
        } else {
            scheduleUploadReports()
        }
    }

    private fun scheduleUploadReports() {
        if (scheduleUpload) {
            return
        } else {
            scheduleUpload = true
        }

        val delayMillis = configuration.uploadReportsPeriodMillis

        logThread.postDelay(delayMillis) {
            scheduleUpload = false
            uploadReports()
        }

        debug { String.format("Schedule upload reports. Delay: %s.", delayMillis) }
    }

    private fun uploadReports() {
        if (uploading) {
            return
        } else {
            uploading = true
        }

        try {
            reportsStorage.getReports(configuration.uploadReportsCount)?.let { reports ->
                val result = Formats.buildUploadReportsContent(reports)
                val maxSequence = result.first
                val reportsContent = result.second

                uploader.uploadReports(reportsContent) { success ->
                    logThread.post {
                        handleUploadReportsResult(maxSequence, success)
                    }
                }
            } ?: run {
                uploading = false
            }

        } catch (th: Throwable) {
            error({ "Error while prepare upload reports task" }, th)
            uploading = false
        }
    }

    private fun handleUploadReportsResult(maxSequence: Long, success: Boolean) {
        debug { String.format("Handle uploaded reports result: %s.", success) }

        if (success) {
            reportsStorage.removeEarlyReports(maxSequence)
        }

        uploading = false

        if (success) {
            uploadReports()
        }
    }

    internal class LogThread : HandlerThread("logStatisticsThread") {
        private val handler by lazy { Handler(looper) }

        fun post(task: () -> Unit) {
            handler.post(task)
        }

        fun postDelay(delayMillis: Long, task: () -> Unit) {
            handler.postDelayed(task, delayMillis)
        }
    }
}