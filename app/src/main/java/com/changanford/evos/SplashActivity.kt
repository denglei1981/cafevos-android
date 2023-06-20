package com.changanford.evos

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.MConstant
import com.changanford.evos.databinding.ActivitySplashBinding
import com.changanford.my.BaseMineUI
import com.xiaomi.push.it
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Route(path = ARouterHomePath.SplashActivity)
class SplashActivity : BaseMineUI<ActivitySplashBinding, EmptyViewModel>() {

    override fun initView() {
//        makeStateBarTransparent(true)
//        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).init()

    }

    override fun initData() {
//
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
//        checkHasMain()
    }

    private fun checkHasMain() {
        if (MConstant.mainActivityIsOpen) {
            //获取activity任务栈
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runningTaskInfo = activityManager.appTasks
            runningTaskInfo.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val topClassName = it.taskInfo.topActivity?.className
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.e("topClassName", "${topClassName.toString()}====${it.taskInfo.taskId}")
                    }
                    if (topClassName == "com.changanford.evos.SplashActivity") {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            it.finishAndRemoveTask()
                        }
                    }
//                    else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            activityManager.moveTaskToFront(it.taskInfo.taskId, 0)
//                        }
////                        finish()
//                    }
                }

            }

        }
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }
}