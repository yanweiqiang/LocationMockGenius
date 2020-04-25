package com.yan.locationmockgenius.mocker

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import com.orhanobut.logger.Logger
import java.lang.Exception

abstract class AbsLocationMocker(val context: Context) : LocationMocker {
    var provider = LocationManager.NETWORK_PROVIDER
    val locMgr: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val logger = Logger.t("gps-mock")

    override fun getProviderName(): String {
        return provider
    }

    override fun rmProvider() {
        if (!existProvider()) {
            return
        }

        locMgr.removeTestProvider(provider)
    }

    override fun existProvider(): Boolean {
        return locMgr.getProvider(provider) != null
    }

    override fun setLocation(loc: Location) {
        logger.d("setLocation")
        locMgr.setTestProviderLocation(provider, loc)
    }

    override fun addProvider() {
        if (!locMgr.isProviderEnabled(provider)) {
            return
        }

        val locProvider = locMgr.getProvider(provider)

        if (locProvider != null) {
            try {
                locMgr.removeTestProvider(provider)
            } catch (ignore: Exception) {

            }

            locMgr.addTestProvider(
                    provider,
                    locProvider.requiresNetwork(),
                    locProvider.requiresSatellite(),
                    locProvider.requiresCell(),
                    locProvider.hasMonetaryCost(),
                    locProvider.supportsAltitude(),
                    locProvider.supportsSpeed(),
                    locProvider.supportsBearing(),
                    locProvider.powerRequirement,
                    5)
        }
        locMgr.setTestProviderEnabled(provider, true)
        locMgr.setTestProviderStatus(provider, LocationProvider.AVAILABLE, null, System.currentTimeMillis())
    }
}