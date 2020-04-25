package com.yan.locationmockgenius.search

import android.support.annotation.NonNull
import com.baidu.mapapi.search.sug.SuggestionSearch
import com.baidu.mapapi.search.sug.SuggestionSearchOption
import com.yan.locationmockgenius.entity.ext.toLMGPoi
import com.yan.locationmockgenius.location.LMGLocationClient

class LMGSearchClient {
    private var sugSearch = SuggestionSearch.newInstance()
    private lateinit var listener: LMGSearchListener

    init {
        sugSearch.setOnGetSuggestionResultListener {
            when {
                it.allSuggestions == null -> return@setOnGetSuggestionResultListener
                else -> listener.onSearch(it.allSuggestions.map { info ->
                    info.toLMGPoi()
                })
            }
        }
    }

    fun search(@NonNull keyword: String, @NonNull listener: LMGSearchListener) {
        val option = SuggestionSearchOption()
        option.city(when (LMGLocationClient.locatoin?.city) {
            "", null -> "上海"
            else -> LMGLocationClient.locatoin!!.city
        })
        option.keyword(keyword)
        this.listener = listener
        sugSearch.requestSuggestion(option)
    }
}