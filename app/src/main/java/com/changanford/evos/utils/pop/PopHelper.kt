package com.changanford.evos.utils.pop

import androidx.lifecycle.MutableLiveData
import com.changanford.evos.MainActivity
import com.changanford.evos.PopViewModel
import com.changanford.home.request.HomeV2ViewModel

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose
 */
class PopHelper(
    val activity: MainActivity,
    private val popViewModel: PopViewModel
) {

    private val popJob = HashMap<Int, SingleJob>()
    private val jobState = MutableLiveData<Int>()
    private var jobCurrent = 0

    fun initPopJob() {
        val updatePopJob = UpdatePopJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
        }
        val getFbPopJob = GetFbPopJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
            setLifecycleOwner(activity)
            setHomeV2ViewModel(activity.createViewModel(HomeV2ViewModel::class.java))
        }
        val newEstOnePopJob = NewEstOnePopJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
        }
        val receivePopJob = ReceivePopJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
            setLifecycleOwner(activity)
        }
        val updateAgreeJob = UpdateAgreeJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
        }
        popJob[1] = updatePopJob
        popJob[2] = updateAgreeJob
        popJob[3] = receivePopJob
        popJob[4] = getFbPopJob
        popJob[5] = newEstOnePopJob
        jobCurrent = 1
        jobState.value = jobCurrent

        jobState.observe(activity) {
            popJob[it]?.let { it1 -> doPopJob(it1) }
        }
    }

    private fun doPopJob(job: SingleJob) {
        if (job.handle()) {
            job.launch {
                jobCurrent++
                if (jobCurrent <= popJob.size) {
                    jobState.value = jobCurrent
                }
            }
        } else {
            jobCurrent++
            if (jobCurrent <= popJob.size) {
                jobState.value = jobCurrent
            }
        }
    }
}