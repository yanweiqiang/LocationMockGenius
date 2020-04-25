package com.yan.locationmockgenius

import android.content.Context
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.yan.locationmockgenius.entity.LMGLocation
import com.yan.locationmockgenius.entity.LMGPoi

interface MainView {
    fun getContext(): Context

    fun getMapView(): MapView

    fun getMap(): BaiduMap

    fun showSearchResultList(list: List<LMGPoi>)
}