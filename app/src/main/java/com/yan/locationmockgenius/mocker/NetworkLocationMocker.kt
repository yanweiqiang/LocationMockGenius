package com.yan.locationmockgenius.mocker

import android.content.Context
import android.location.LocationManager

class NetworkLocationMocker(context: Context) : AbsLocationMocker(context) {

    init {
//        provider = LocationManager.NETWORK_PROVIDER
        provider = LocationManager.NETWORK_PROVIDER
    }
}