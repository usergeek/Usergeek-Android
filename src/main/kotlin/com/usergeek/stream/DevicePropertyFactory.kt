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
            this.networkOperatorName?.trim()?.let {
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


//    fun putNetworkType(context: Context, property: String = "ntwType", properties: HashMap<String, Supplier<out Any?>>) {
//        properties[property] = Supplier { getNetworkType(context)
//    }

//    private fun getNetworkType(ctx: Context): String {
//        return (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.let { manager ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                manager.activeNetwork?.let { networkCapabilities ->
//                    manager.getNetworkCapabilities(networkCapabilities)?.let { actNw ->
//                        when {
//                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
//                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getCellularType(
//                                manager.activeNetworkInfo
//                            )
//                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
//                            else -> "other"
//                        }
//                    }
//                }
//            } else {
//                manager.activeNetworkInfo?.run {
//                    if (isConnected) {
//                        @Suppress("DEPRECATION")
//                        when (type) {
//                            ConnectivityManager.TYPE_WIFI -> "wifi"
//                            ConnectivityManager.TYPE_MOBILE -> getCellularType(this)
//                            ConnectivityManager.TYPE_ETHERNET -> "ethernet"
//                            else -> "other"
//                        }
//                    } else {
//                        null
//                    }
//                }
//            }
//        } ?: "notReachable"
//    }

//    private fun getCellularType(networkInfo: NetworkInfo?): String {
//        return networkInfo?.run {
//            when (subtype) {
//                TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT -> "2g"
//                TelephonyManager.NETWORK_TYPE_LTE -> "4g"
//                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "wwan"
//                else -> "3g"
//            }
//        } ?: run {
//            "cellular"
//        }
//    }
//

//    private fun getCountryFromLocation(ctx: Context): String? {
//        if (!this.observeLocation) {
//            return null
//        }
//
//        return getRecentLocation(ctx)?.run {
//            try {
//                if (Geocoder.isPresent()) {
//                    Geocoder(ctx, Locale.ENGLISH).getFromLocation(latitude, longitude, 1)?.let {
//                        for (address in it) {
//                            if (address != null) {
//                                return address.countryCode
//                            }
//                        }
//                    }
//                }
//            } catch (th: Throwable) {
//                apiError({ "Error while get country from location" }, th)
//            }
//
//            return null
//        }
//    }

//    private fun getRecentLocation(ctx: Context): Location? {
//        if (!this.observeLocation) {
//            return null
//        }
//
//        return try {
//            (ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)?.let { manager ->
//                manager.getProviders(true)?.let { providers ->
//                    val locations = ArrayList<Location>()
//                    for (provider in providers) {
//                        try {
//                            manager.getLastKnownLocation(provider)?.let {
//                                locations.add(it)
//                            }
//                        } catch (th: SecurityException) {
//                            internalError({ String.format("Error while get recent location from provider: %s", provider) }, th)
//                        } catch (th: Throwable) {
//                            internalError({ String.format("Error while get recent location from provider: %s", provider) }, th)
//                        }
//                    }
//
//                    var maxTimestamp = -1L
//                    var bestLocation: Location? = null
//                    for (location in locations) {
//                        if (location.time > maxTimestamp) {
//                            maxTimestamp = location.time
//                            bestLocation = location
//                        }
//                    }
//
//                    return bestLocation
//                }
//            }
//        } catch (th: Throwable) {
//            apiError({ "Error while get recent location" }, th)
//            null
//        }
//    }

}

