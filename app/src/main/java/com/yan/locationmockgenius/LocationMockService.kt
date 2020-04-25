package com.yan.locationmockgenius

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.orhanobut.logger.Logger
import com.yan.locationmockgenius.constant.MockData
import com.yan.locationmockgenius.entity.LMGLocation
import com.yan.locationmockgenius.location.LMGLocationClient
import com.yan.locationmockgenius.location.LMGLocationListener
import com.yan.locationmockgenius.mocker.GPSLocationMocker
import com.yan.locationmockgenius.mocker.LocationMockUtils
import com.yan.locationmockgenius.mocker.LocationMocker
import com.yan.locationmockgenius.mocker.NetworkLocationMocker
import java.util.*
import kotlin.collections.ArrayList

class LocationMockService : Service() {
    val handler = Handler(Looper.getMainLooper())

    inner class Binder : android.os.Binder() {
        fun isStarted(): Boolean = this@LocationMockService.started

        fun startMock() {
            this@LocationMockService.startMock()
        }

        fun updateFloatText(text: String) {
            floatWindow.updateText(text)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }

    private val tag = "LocationMockService"
    private val latLngInfo = "104.06121778639009&30.544111926165282"
    private val logger = Logger.t(tag)

    private lateinit var locationManager: LocationManager
    private val locMockerList = ArrayList<LocationMocker>()
    private var timer = Timer()
    private lateinit var floatWindow: FloatWindow
    private var started: Boolean = false

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locMockerList.add(GPSLocationMocker(baseContext))
        locMockerList.add(NetworkLocationMocker(baseContext))
        floatWindow = FloatWindow(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!started) {
            locMockerList.forEach {
                it.addProvider()
            }

            startMock()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopMock()
        super.onDestroy()
    }

    private fun startMock() {
        startForeground(1, NotificationMaker.make(baseContext))
        floatWindow.showFloatWindow()
        LMGLocationClient.getInstance().registerLocationListener(locationListener)

        timer.schedule(object : TimerTask() {
            override fun run() {
                if (0.0.equals(MockData.lat) || 0.0.equals(MockData.lon)) {
                    handler.post {
                        floatWindow.updateText("请设置模拟位置")
                    }
                    return
                }

                try {
                    locMockerList.forEach {
                        it.setLocation(LocationMockUtils.generateLocation(it.getProviderName(), MockData.lat, MockData.lon))
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    e.message?.let { logger.e(it) }
                }
            }
        }, 500, 2000)
        started = true
    }

    private fun stopMock() {
        stopForeground(true)
        floatWindow.hideFloatWindow()
        LMGLocationClient.getInstance().unregisterLocationListener(locationListener)

        locMockerList.forEach {
            if (it.existProvider()) {
                locationManager.removeTestProvider(it.getProviderName())
            }
        }

        timer.cancel()
        started = false
    }

    private val locationListener = object : LMGLocationListener {
        override fun onLocation(location: LMGLocation?) {
            floatWindow.updateText(location?.addressStr!!)
        }
    }
}
