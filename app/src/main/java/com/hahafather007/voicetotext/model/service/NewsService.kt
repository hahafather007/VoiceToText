package com.hahafather007.voicetotext.model.service

import com.hahafather007.voicetotext.model.data.NewsData
import com.hahafather007.voicetotext.model.data.NewsResponse
import com.hahafather007.voicetotext.model.service.interceptor.AppHttpLoggingInterceptor
import com.hahafather007.voicetotext.utils.log
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface NewsApi {
    @GET(value = "index?type=top&key=c116bf742a3fa1f619a4632b1059c051")
    fun getNews(): Single<NewsResponse>
}

class NewsService {
    private val api: NewsApi

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(AppHttpLoggingInterceptor())
                .build()

        api = Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApi::class.java)
    }

    fun getNews(): Single<List<NewsData>> {
        return api.getNews()
                .map {
                    it.log()

                    it
                }
                .map { it.result.newsList }
    }

    companion object {
        const val URL = "http://v.juhe.cn/toutiao/"
    }
}