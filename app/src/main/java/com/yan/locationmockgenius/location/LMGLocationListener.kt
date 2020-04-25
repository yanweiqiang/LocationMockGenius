package com.yan.locationmockgenius.location

import com.yan.locationmockgenius.entity.LMGLocation

interface LMGLocationListener {

    fun onLocation(location: LMGLocation?)
}