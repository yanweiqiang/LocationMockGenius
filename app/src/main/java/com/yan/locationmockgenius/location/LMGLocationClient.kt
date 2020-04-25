package com.yan.locationmockgenius.location

import android.app.Application
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.yan.locationmockgenius.entity.ext.toLMGLocation

class LMGLocationClient {
    private lateinit var locationClient: LocationClient
    private val listenerList = ArrayList<LMGLocationListener>()

    private val listener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(p0: BDLocation?) {
            locatoin = p0

            listenerList.forEach {
                it.onLocation(p0?.toLMGLocation())
            }
        }
    }

    fun initLocationClient(application: Application) {
        locationClient = LocationClient(application)
        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(5 * 1000)
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        option.setIsNeedAddress(true)
        option.setIsNeedLocationDescribe(true)
        //设置locationClientOption
        locationClient.locOption = option
        //开启地图定位图层

        locatoin = locationClient.lastKnownLocation
    }

    fun startLocate() {
        if (locationClient.isStarted) {
            return
        }

        locationClient.registerLocationListener(listener)
        locationClient.start()
    }

    fun stopLocate() {
        if (!locationClient.isStarted) {
            return
        }

        locationClient.unRegisterLocationListener(listener)
        locationClient.stop()
    }

    fun registerLocationListener(listener: LMGLocationListener) {
        if (listenerList.contains(listener)) {
            return
        }
        listenerList.add(listener)
    }

    fun unregisterLocationListener(listener: LMGLocationListener) {
        if (!listenerList.contains(listener)) {
            return
        }
        listenerList.remove(listener)
    }

    companion object {
        private val instance = LMGLocationClient()

        @JvmStatic
        fun getInstance(): LMGLocationClient {
            return instance
        }

        var locatoin: BDLocation? = null
    }
}