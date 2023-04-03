package com.changanford.home.util

import com.changanford.home.recommend.request.RecommendViewModel
import java.util.Timer
import java.util.TimerTask

object HomeTimer {
    private var timer: Timer? = null
    var task: FourHourTask? = null
    var count = 1
    var recommendViewModel: RecommendViewModel? = null
    const val timePeriod: Long = (2 * 60 -1) * 60 * 1000

    class FourHourTask : TimerTask() {
        override fun run() {
            recommendViewModel?.getRecommend(false)
        }
    }

    private fun doCountingWork() {
        if (timer == null) {
            timer = Timer()
        }
        if (task == null) {
            task = FourHourTask()
        }
        timer?.schedule(task, timePeriod, timePeriod)
    }

    fun refreshTask(recommendViewModel: RecommendViewModel) {
        this.recommendViewModel = recommendViewModel
        timer?.cancel()
        timer = null
        task?.cancel()
        task = null
        doCountingWork()
    }
}