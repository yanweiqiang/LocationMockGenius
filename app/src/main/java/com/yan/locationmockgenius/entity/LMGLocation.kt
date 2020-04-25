package com.yan.locationmockgenius.entity

import com.baidu.location.BDLocation

data class LMGLocation(val bdLocation: BDLocation, val lat: Double, val lon: Double, val addressStr: String)