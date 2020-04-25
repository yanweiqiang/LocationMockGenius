package com.yan.locationmockgenius

import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.yan.locationmockgenius.constant.MockData
import com.yan.locationmockgenius.entity.LMGLocation
import com.yan.locationmockgenius.entity.LMGPoi
import com.yan.locationmockgenius.location.LMGLocationClient
import com.yan.locationmockgenius.location.LMGLocationListener
import com.yan.locationmockgenius.search.LMGSearchClient
import com.yan.locationmockgenius.search.LMGSearchListener

class MainPresenter(private val view: MainView) {
    private val searchClient = LMGSearchClient()

    var locationListener = object : LMGLocationListener {
        override fun onLocation(location: LMGLocation?) {
            if (0.0.equals(MockData.lat) || 0.0.equals(MockData.lon)) {
                location?.let {
                    MockData.lat = it.lat
                    MockData.lon = it.lon
                }
            }

            //mapView 销毁后不在处理新接收的位置
            if (location == null) {
                return
            }

            Log.i("temp", location.addressStr)

            setMapViewLocation(location.bdLocation)
            setMyLocation(location.bdLocation)
        }
    }

    fun startLocate() {
        //注册LocationListener监听器
        LMGLocationClient.getInstance().registerLocationListener(locationListener)
        LMGLocationClient.getInstance().startLocate()
    }

    fun stopLocate() {
        LMGLocationClient.getInstance().unregisterLocationListener(locationListener)
        LMGLocationClient.getInstance().stopLocate()
    }

    fun setMyLocation(location: BDLocation) {
        val locData = MyLocationData.Builder()
                .accuracy(location.radius)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.direction).latitude(location.latitude)
                .longitude(location.longitude).build()
        view.getMap().setMyLocationData(locData)
    }

    fun setMapViewLocation(location: BDLocation) {
        val latLng = LatLng(location.latitude, location.longitude)
        val status = MapStatusUpdateFactory.newLatLng(latLng)
        view.getMap().setMapStatus(status)
    }

    fun searchPoi(keyword: String) {
        searchClient.search(keyword, object : LMGSearchListener {
            override fun onSearch(poiList: List<LMGPoi>) {
                view.showSearchResultList(poiList)
                poiList.forEach {
                    Log.i("temp", it.key)
                }
            }
        })
    }
}
