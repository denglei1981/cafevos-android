package com.changanford.common.router

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant


/**
 * @Des: ARouter登录拦截
 *  拦截器会按优先级顺序priority依次执行
 */
@Interceptor(name = "login", priority = 5)
class LoginInterceptorImpl : IInterceptor {

    private lateinit var mContext: Context

    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        val bundle = postcard.extras
        //拦截
        if (bundle != null && bundle.getBoolean(MConstant.LOGIN_INTERCEPT)) {
            bundle.remove(MConstant.LOGIN_INTERCEPT)
            if (MConstant.token.isEmpty()) {
                bundle.putString(MConstant.LOGIN_INTERCEPT_PATH, postcard.path)
                callback.onInterrupt(null)//中断路由流程
                startARouter(ARouterMyPath.SignUI, bundle)//跳转登录
                return
            }
        } else if (MConstant.token.isEmpty() && postcard.extra == MConstant.ROUTER_LOGIN_CODE) {//需要拦截的页面，extra 设置为100
            bundle.putString(MConstant.LOGIN_INTERCEPT_PATH, postcard.path)
            callback.onInterrupt(null)//中断路由流程
            startARouter(ARouterMyPath.SignUI, bundle)//跳转登录
            return
        }
        // 处理完成，交还控制权
        callback.onContinue(postcard)
    }

    /**
     * 初始化一次
     */
    override fun init(context: Context) {
        mContext = context
    }
}