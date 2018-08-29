package com.hahafather007.voicetotext.view.fragment

import android.database.DatabaseUtils
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.databinding.FragmentNewsBinding
import com.hahafather007.voicetotext.databinding.ItemNewsBinding
import com.hahafather007.voicetotext.utils.RxField
import com.hahafather007.voicetotext.utils.disposable
import com.hahafather007.voicetotext.utils.log
import com.hahafather007.voicetotext.view.activity.WebViewActivity
import com.hahafather007.voicetotext.viewmodel.NewsViewModel
import io.reactivex.disposables.CompositeDisposable
import me.drakeet.multitype.MultiTypeAdapter

class NewsFragment : Fragment(), RxController {
    override val rxComposite = CompositeDisposable()

    private lateinit var binding: FragmentNewsBinding
    private val viewModel = NewsViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)!!
        binding.fragment = this
        binding.viewModel = viewModel

        addChangeListener()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.onCleared()
        onCleared()
    }

    private fun addChangeListener() {
        RxField.of(viewModel.newsList)
                .skip(1)
                .disposable(this)
                .doOnNext {
                    val adapter = binding.recyclerView.adapter as MultiTypeAdapter
                    val beforeSize = adapter.itemCount

                    if (!viewModel.isNewData()) {
                        adapter.items = it
                        adapter.notifyItemRangeInserted(beforeSize, adapter.itemCount)
                    } else {
                        adapter.items = it
                        adapter.notifyDataSetChanged()
                        binding.recyclerView.scheduleLayoutAnimation()
                    }
                }
                .subscribe()
    }

    fun onBindItem(binding: ViewDataBinding, data: Any, position: Int) {
        val itemBinding = binding as ItemNewsBinding
        itemBinding.fragment = this
    }

    fun openNewsDetail(url: String) {
        startActivity(WebViewActivity.intentOfUrl(context, url, getString(R.string.title_news_top)))
    }
}