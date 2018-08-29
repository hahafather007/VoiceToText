package com.hahafather007.voicetotext.model.data

import com.google.gson.annotations.SerializedName

data class NewsResult(@SerializedName("data")
                  val newsList: List<NewsData>)