package com.yan.locationmockgenius.mocker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlin.random.Random

object LocationMockUtils {

    var satellitesNum = 10

    //generate a location
    fun generateLocation(provider: String, lat: Double, lon: Double): Location {
        val loc = Location(provider.removeSuffix("Mock"))

        val bundle = Bundle()
        satellitesNum = 20 + (Math.random() * 10).toInt()
        bundle.putInt("satellites", satellitesNum)
        loc.extras = bundle

        Log.i("temp", provider + "\\" + satellitesNum)


        loc.latitude = lat + (Math.random() * 1e-12)
        loc.longitude = lon + (Math.random() * 1e-12)
        loc.accuracy = 1.0f + (Math.random() * 10).toFloat()
        loc.altitude = 10.00 + (Math.random() * 10).toFloat()
        loc.bearing = (Math.random() * 260).toFloat()
        loc.time = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            loc.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }

        return loc
    }


    //模拟位置权限是否开启
    fun isAllowMockLocation(context: Context): Boolean {
        var canMockPosition: Boolean

        if (Build.VERSION.SDK_INT <= 22) {//6.0以下
            canMockPosition = Settings.Secure.getInt(context.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0
        } else {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val providerStr = "test-provider"
                val provider = locationManager.getProvider(providerStr)

                if (provider == null) {
                    locationManager.addTestProvider(
                            providerStr, true, true, false, false, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE)
                }

                locationManager.setTestProviderEnabled(providerStr, true)
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis())
                canMockPosition = true
                locationManager.setTestProviderEnabled(providerStr, false)
                locationManager.removeTestProvider(providerStr)
            } catch (e: SecurityException) {
                canMockPosition = false
            }

        }
        return canMockPosition
    }


    fun openDevSettings(activity: Activity) {
        AlertDialog.Builder(activity)
                .setTitle("启用位置模拟")
                .setMessage("请在开发者选项->选择模拟位置信息应用中进行设置")
                .setPositiveButton("设置")
                { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(activity, "无法跳转到开发者选项,请先确保您的设备已处于开发者模式", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("取消")
                { _, _ ->
                    activity.finish()
                }
                .show()
    }
}
