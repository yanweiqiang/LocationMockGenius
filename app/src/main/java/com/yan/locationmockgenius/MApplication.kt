package com.yan.locationmockgenius

import android.app.Application

import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.yan.locationmockgenius.location.LMGLocationClient

class MApplication : Application() {
    companion object {
        lateinit var application: MApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this)
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)

        LMGLocationClient.getInstance().initLocationClient(this)

        Logger.addLogAdapter(AndroidLogAdapter())
    }
}