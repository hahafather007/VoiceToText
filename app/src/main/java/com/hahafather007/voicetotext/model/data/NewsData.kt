package com.hahafather007.voicetotext.model.data

import com.google.gson.annotations.SerializedName

data class NewsData(val title: String,
                    val date: String,
                    @SerializedName("author_name")
                    val author: String,
                    val url: String,
                    @SerializedName("thumbnail_pic_s")
                    val image: String)

data class NewsResponse(val result: Result) {
    data class Result(@SerializedName("data")
                      val newsList: List<NewsData>)
}