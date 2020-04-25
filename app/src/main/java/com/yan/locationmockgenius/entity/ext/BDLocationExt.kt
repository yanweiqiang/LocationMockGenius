package com.yan.locationmockgenius.entity.ext

import com.baidu.location.BDLocation
import com.yan.locationmockgenius.entity.LMGLocation

fun BDLocation.toLMGLocation(): LMGLocation {
    return LMGLocation(this, latitude, longitude, addrStr)
}