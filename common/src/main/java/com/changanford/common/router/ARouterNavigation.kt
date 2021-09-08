package com.changanford.common.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.util.MConstant

/**
 * @Author hpb
 * @Date 2020/4/30 10:42
 * @Des ARouter相关跳转
 */

/**
 * 无参跳转
 */
@JvmOverloads
fun startARouter(url: String, isNeedLogin: Boolean? = null) {
    ARouter.getInstance().build(url)
        .apply {
            if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
        }
        .navigation()
}

@JvmOverloads
fun startARouterFinish(activity: Activity, url: String, isNeedLogin: Boolean? = null) {
    ARouter.getInstance().build(url).apply {
        if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
    }.navigation(activity, backNavigationCallback(activity))
}

/**
 * 携带数据的页面跳转
 */
@JvmOverloads
fun startARouter(url: String, bundle: Bundle, isNeedLogin: Boolean? = null) {
    ARouter.getInstance().build(url).with(bundle).apply {
        if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
    }.navigation()
}

@JvmOverloads
fun startARouterFinish(
    activity: Activity,
    url: String,
    bundle: Bundle,
    isNeedLogin: Boolean? = null
) {
    ARouter.getInstance().build(url).with(bundle).apply {
        if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
    }
        .navigation(activity, backNavigationCallback(activity))
}

/**
 * 无参、requestCode页面跳转
 */
@JvmOverloads
fun startARouterForResult(
    activity: Activity,
    url: String,
    requestCode: Int,
    isNeedLogin: Boolean? = null
) {
    ARouter.getInstance().build(url).apply {
        if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
    }.navigation(activity, requestCode)
}

/**
 * 有参、requestCode页面跳转
 */
@JvmOverloads
fun startARouterForResult(
    activity: Activity,
    url: String,
    bundle: Bundle,
    requestCode: Int,
    isNeedLogin: Boolean? = null
) {
    ARouter.getInstance().build(url).with(bundle).apply {
        if (isNeedLogin == true) withBoolean(MConstant.LOGIN_INTERCEPT, true)
    }.navigation(activity, requestCode)
}

/**
 * 无参、requestCode页面跳转
 */
@JvmOverloads
fun startARouterForResult(
    fragment: Fragment,
    url: String,
    requestCode: Int,
    isNeedLogin: Boolean? = null
) {
    val intent = Intent(fragment.context, getDestination(url))
    if (isNeedLogin == true) {
        intent.putExtra(MConstant.LOGIN_INTERCEPT, true)
    }
    fragment.startActivityForResult(intent, requestCode)
}

/**
 * 携带数据、requestCode页面跳转
 */
@JvmOverloads
fun startARouterForResult(
    fragment: Fragment,
    url: String,
    bundle: Bundle,
    requestCode: Int,
    isNeedLogin: Boolean? = null
) {
    val intent = Intent(fragment.context, getDestination(url))
    intent.putExtras(bundle)
    if (isNeedLogin == true) {
        bundle.putBoolean(MConstant.LOGIN_INTERCEPT, true)
    }
    fragment.startActivityForResult(intent, requestCode)
}

/**
 * 获取Fragment
 */
fun <T : Fragment> getARouterFragment(url: String): T {
    return ARouter.getInstance().build(url).navigation() as T
}

/**
 * 由于ARouter不支持Fragment startActivityForResult(),需要获取跳转的Class
 * 根据路径获取具体要跳转的class
 */
private fun getDestination(url: String): Class<*>? {
    val postcard = ARouter.getInstance().build(url)
    LogisticsCenter.completion(postcard)
    return postcard.destination
}

/**
 * 结束activity
 */
private fun backNavigationCallback(activity: Activity): NavigationCallback {
    return object : NavigationCallback {
        override fun onLost(postcard: Postcard?) {
        }

        override fun onFound(postcard: Postcard?) {
        }

        override fun onInterrupt(postcard: Postcard?) {
        }

        override fun onArrival(postcard: Postcard?) {
            activity.finish()
        }
    }
}