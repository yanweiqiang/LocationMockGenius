package com.yan.locationmockgenius.search

import com.yan.locationmockgenius.entity.LMGPoi

interface LMGSearchListener {

    fun onSearch(poiList: List<LMGPoi>)
}