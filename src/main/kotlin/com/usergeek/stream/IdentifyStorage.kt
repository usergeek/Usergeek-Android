package com.usergeek.stream

import android.content.Context

class IdentifyStorage(ctx: Context) {

    companion object {
        const val PREFERENCES_NAME = "com.usergeek.android.statistics.preferences"
        const val DEVICE_ID_KEY = "device_id"
        const val USER_ID_KEY = "user_id"
    }

    private val preferences = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    internal fun setDeviceId(deviceId: String) {
        preferences.edit().putString(DEVICE_ID_KEY, deviceId).apply()
    }

    internal fun getDeviceId(): String? {
        return preferences.getString(DEVICE_ID_KEY, null)
    }

    internal fun setUserId(userId: String) {
        preferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    internal fun getUserId(): String? {
        return preferences.getString(USER_ID_KEY, null)
    }

    internal fun removeUserId() {
        preferences.edit().remove(USER_ID_KEY).apply()
    }

}
