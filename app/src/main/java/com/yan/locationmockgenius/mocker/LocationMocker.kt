package com.yan.locationmockgenius.mocker

import android.location.Location

interface LocationMocker {

    fun getProviderName(): String

    fun addProvider()

    fun rmProvider()

    fun existProvider(): Boolean

    fun setLocation(loc: Location)
}