package com.yan.locationmockgenius.mocker

import android.content.Context
import android.location.LocationManager
import android.location.LocationProvider
import java.lang.Exception

class GPSLocationMocker(context: Context) : AbsLocationMocker(context) {
    init {
//        provider = LocationManager.GPS_PROVIDER
        provider = LocationManager.GPS_PROVIDER
    }
}