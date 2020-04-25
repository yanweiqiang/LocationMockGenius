package com.yan.locationmockgenius.entity.ext

import com.baidu.mapapi.search.sug.SuggestionResult
import com.yan.locationmockgenius.entity.LMGPoi

fun SuggestionResult.SuggestionInfo.toLMGPoi(): LMGPoi {
    return LMGPoi(pt?.latitude, pt?.longitude, this.getKey())
}