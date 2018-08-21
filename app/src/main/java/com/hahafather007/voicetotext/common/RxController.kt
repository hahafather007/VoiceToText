package com.hahafather007.voicetotext.common

import io.reactivex.disposables.CompositeDisposable

interface RxController {
    /**
     * 在子类中这样写：
     * override val rxComposite = CompositeDisposable()
     */
    val rxComposite: CompositeDisposable

    /**
     * 继承的子类在activity销毁时调用该方法释放资源
     */
    fun onCleared() {
        rxComposite.clear()
    }
}