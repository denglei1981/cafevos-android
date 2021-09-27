package com.changanford.common.util.bus

import androidx.lifecycle.LifecycleOwner
import com.jeremyliao.liveeventbus.LiveEventBus

object Bus {

    inline fun <reified T> post(channel: String, value: T) =
        LiveEventBus.get(channel, T::class.java).post(value)

    inline fun <reified T> observe(
        channel: String,
        owner: LifecycleOwner,
        crossinline observer: ((value: T) -> Unit)
    ) =
        LiveEventBus.get(channel, T::class.java).observe(owner, { observer(it) })

}
