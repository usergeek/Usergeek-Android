package com.usergeek.stream

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule


object SessionTracker {

    interface Listener {
        fun sessionStarted(activity: Activity, firstSession: Boolean)
        fun sessionEnded(activity: Activity, durationMillis: Long)
    }

    private val enabled = AtomicBoolean(false)
    private var timeoutMillis: Long = 0
    private lateinit var timer: Timer
    private lateinit var listener: Listener
    private var state: State = BackgroundState(true)

    fun enableSessionTracking(app: Application) {
        enableSessionTracking(app, 30, object : Listener {
            override fun sessionStarted(activity: Activity, firstSession: Boolean) {
                Usergeek.getClient().logEvent(Formats.DefaultEvents.START_SESSION)
            }

            override fun sessionEnded(activity: Activity, durationMillis: Long) {
                Usergeek.getClient().flush()
            }
        })
    }

    fun enableSessionTracking(app: Application, timeoutSec: Int, listener: Listener) {
        if (enabled.getAndSet(true)) {
            return
        }

        this.timeoutMillis = TimeUnit.SECONDS.toMillis(timeoutSec.toLong())
        this.listener = listener

        timer = Timer("EndSessionTimer", true)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                toBackground(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                toForeground(activity)
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
            }
        })

        info { String.format("Enable session tracker. Between session timeout: %s", timeoutSec) }
    }

    @Synchronized
    private fun toBackground(activity: Activity) {
        state.toBackground(activity)
    }

    @Synchronized
    private fun toForeground(activity: Activity) {
        state.toForeground(activity)
    }

    @Synchronized
    private fun sessionExpirationEvent(onState: State) {
        state.sessionExpirationEvent(onState)
    }

    interface State {
        fun toBackground(activity: Activity) {}
        fun toForeground(activity: Activity) {}
        fun sessionExpirationEvent(onState: State) {}
    }

    class BackgroundState(private val coldStart: Boolean) : State {
        init {
            debug { String.format("Init state, cold start: %s", coldStart) }
        }

        override fun toForeground(activity: Activity) {
            val startSession = System.currentTimeMillis()

            listener.sessionStarted(activity, coldStart)
            state = ForegroundState(startSession, activity)
        }
    }

    class ForegroundState(private val startSession: Long, private val sessionActivity: Activity) :
        State {
        init {
            debug { String.format("Init state, start session: %s", sessionActivity) }
        }

        override fun toBackground(activity: Activity) {
            val time = System.currentTimeMillis()
            state = if (sessionActivity == activity) {
                SessionBackgroundState(startSession, time, activity)
            } else {
                BackgroundState(false)
            }
        }
    }

    class SessionBackgroundState(
        private val startSession: Long,
        private val startBackground: Long,
        private val sessionActivity: Activity

    ) : State {

        init {
            debug { String.format("Init state, go to background from: %s", sessionActivity) }
        }

        private val timerTask = timer.schedule(timeoutMillis) {
            SessionTracker.sessionExpirationEvent(this@SessionBackgroundState)
        }

        override fun toForeground(activity: Activity) {
            val time = System.currentTimeMillis()

            timerTask.cancel()

            state = if ((time - startBackground) < timeoutMillis) {
                ForegroundState(startSession, activity)
            } else {
                listener.sessionEnded(sessionActivity, startBackground - startSession)
                listener.sessionStarted(activity, false)
                ForegroundState(time, activity)
            }
        }

        override fun sessionExpirationEvent(onState: State) {
            if (onState == this) {
                listener.sessionEnded(sessionActivity, startBackground - startSession)
                state = BackgroundState(false)
            }
        }
    }
}