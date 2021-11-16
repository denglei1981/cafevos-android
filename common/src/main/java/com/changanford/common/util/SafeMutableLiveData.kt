package com.changanford.common.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

class SafeMutableLiveData<T> : MutableLiveData<T>() {

    private var lastLifecycleOwner: WeakReference<LifecycleOwner>? = null

    fun safeObserve(owner: LifecycleOwner, observer: Observer<T>) {
        lastLifecycleOwner?.get()?.let {
            removeObservers(it)
        }
        lastLifecycleOwner = WeakReference(owner)
        observe(owner, observer)
    }

}
