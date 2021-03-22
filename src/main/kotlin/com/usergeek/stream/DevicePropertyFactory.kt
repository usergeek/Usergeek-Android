package com.usergeek.stream

import android.content.Context
import android.telephony.TelephonyManager
import java.util.*

object DevicePropertyFactory {

    fun getAppVersion(ctx: Context): String? {
        return ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
    }

    fun getCarrier(ctx: Context): String? {
        return (ctx.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.run {
            this.networkOperatorName?.trim()?.let<String, String> {
                return if (it.isEmpty()) null else it
            }
        }
    }

    fun getCountry(ctx: Context): String {
        return (getCountryFromNetwork(ctx) ?: getCountryFromLocale()).toLowerCase(Locale.ENGLISH)
    }

    private fun getCountryFromNetwork(ctx: Context): String? {
        return try {
            (ctx.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.run {
                when (this.phoneType) {
                    TelephonyManager.PHONE_TYPE_CDMA -> null
                    else -> {
                        this.networkCountryIso?.run {
                            toLowerCase(Locale.US).run {
                                if (this.isBlank()) null else this
                            }
                        }
                    }
                }
            }
        } catch (th: Throwable) {
            error({ "Error while get country from network" }, th)
            null
        }
    }

    private fun getCountryFromLocale(): String {
        return Locale.getDefault().country.toLowerCase(Locale.ENGLISH)
    }
}

