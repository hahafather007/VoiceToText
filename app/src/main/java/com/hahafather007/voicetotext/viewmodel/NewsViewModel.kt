package com.hahafather007.voicetotext.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.model.data.NewsData
import com.hahafather007.voicetotext.model.service.NewsService
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import io.reactivex.disposables.CompositeDisposable

class NewsViewModel : RxController {
    override val rxComposite = CompositeDisposable()

    var newsList: ObservableList<NewsData> = ObservableArrayList()
    var loading = ObservableBoolean()
    var newsEnd = ObservableBoolean()

    //因为api服务器不支持加载更多，所以本地缓存实现假加载更多
    private var newsHolder: List<NewsData>? = null
    //用来标记是否为新加载的数据
    private var newData: Boolean = false

    private val newsService = NewsService()

    init {
        refresh()
    }

    fun refresh() {
        if (!loading.get()) {
            newsService.getNews()
                    .asyncSwitch()
                    .disposable(this)
                    .doOnSuccess {
                        newData = true
                        newsList.clear()
                        newsList.addAll(it.subList(0, 8))
                        newsEnd.set(false)
                        newsHolder = it
                    }
                    .doOnSubscribe { loading.set(true) }
                    .doFinally { loading.set(false) }
                    .subscribe()
        }
    }

    fun loadMore() {
        if (newsList.size < newsHolder!!.size) {
            newData = false
            if (newsHolder!!.size >= newsList.size + 8) {
                newsList.addAll(newsHolder!!.subList(newsList.size, newsList.size + 8))
            } else {
                newsList.addAll(newsHolder!!.subList(newsList.size, newsHolder!!.size))
                newsEnd.set(true)
            }
        }
    }

    fun isNewData(): Boolean {
        return newData
    }
}