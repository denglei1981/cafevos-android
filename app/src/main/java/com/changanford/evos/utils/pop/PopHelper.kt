package com.changanford.evos.utils.pop

import android.app.Dialog
import androidx.lifecycle.MutableLiveData
import com.changanford.evos.MainActivity
import com.changanford.evos.PopViewModel
import com.changanford.home.request.HomeV2ViewModel

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose
 */
object PopHelper {

    private lateinit var activity: MainActivity
    private lateinit var popViewModel: PopViewModel


    private val popJob = HashMap<Int, SingleJob>()
    private val jobState = MutableLiveData<Int>()
    private var jobCurrent = 0
    var updateDialog: Dialog? = null
    private var insertUpdate = false

    fun initPopHelper(activity: MainActivity, popViewModel: PopViewModel) {
        this.activity = activity
        this.popViewModel = popViewModel
    }

    fun initPopJob() {
        val updatePopJob = UpdatePopJob().apply {
            setContext(activity)
            setPopViewMode(popViewModel)
        }
        val holdCircleJob = HoldCircleJob().apply {
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
        popJob[2] = holdCircleJob
        popJob[3] = updateAgreeJob
        popJob[4] = receivePopJob
        popJob[5] = getFbPopJob
        popJob[6] = newEstOnePopJob
        jobCurrent = 1
        jobState.value = jobCurrent

        jobState.observe(activity) {
            popJob[it]?.let { it1 ->
                if (!insertUpdate) {
                    doPopJob(it1)
                }
            }
        }
    }

    fun isInsertUpdate() {
        insertUpdate = true
    }

    fun resumeRule() {
        insertUpdate = false
        jobState.value = if (jobCurrent == 2) jobCurrent else jobCurrent++
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