package com.changanford.common.basic

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.changanford.common.loadsir.EmptyCallback
import com.changanford.common.loadsir.ErrorCallback
import com.changanford.common.loadsir.LoadingCallback
import com.changanford.common.loadsir.TimeoutCallback
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir

/**
 *  界面 加载状态 管理。。。。
 *   @author nw
 * */
abstract class BaseLoadSirActivity<VB : ViewBinding, VM : ViewModel> :
    BaseActivity<VB, VM>(){

  //界面状态管理者
  var mLoadService: LoadService<Any>? = null
    open fun setLoadSir(view: View?) {
        if (mLoadService == null) {
            mLoadService = LoadSir.getDefault().register(view,
                Callback.OnReloadListener { v: View? -> onRetryBtnClick() })
        }
    }

    /**
     * 失败重试,重新加载事件
     */
    protected abstract fun onRetryBtnClick()

    private var isShowedContent = false
    open fun showLoading() {
        if (null != mLoadService) {
            mLoadService!!.showCallback(LoadingCallback::class.java)
        }
    }

    open fun showEmptyLoadView() {
        if (null != mLoadService) {
            mLoadService!!.showCallback(EmptyCallback::class.java)
        }
    }

    open fun showFailure(message: String?) {
        if (null != mLoadService) {
            if (!isShowedContent) {
                mLoadService!!.showCallback(ErrorCallback::class.java)
            } else {
//                ToastUtil.show(, message)
            }
        }
    }
    open fun showContent() {
        if (null != mLoadService) {
            isShowedContent = true
            mLoadService!!.showSuccess()
        }
    }
    open fun showTimeOut() {
        if (null != mLoadService) {
            mLoadService!!.showCallback(TimeoutCallback::class.java)
        }
    }
}