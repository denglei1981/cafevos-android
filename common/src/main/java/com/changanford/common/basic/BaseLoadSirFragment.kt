package com.changanford.common.basic

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.changanford.common.R
import com.changanford.common.loadsir.EmptyCallback
import com.changanford.common.loadsir.ErrorCallback
import com.changanford.common.loadsir.LoadingCallback
import com.changanford.common.util.toast.ToastUtils
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.kingja.loadsir.core.Transport

abstract class BaseLoadSirFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment<VB, VM>() {

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

    open fun showEmpty() {
        if (null != mLoadService) {
            mLoadService!!.showCallback(EmptyCallback::class.java)
        }
    }

    open fun showFailure(message: String) {
        if (null != mLoadService) {
            if (!isShowedContent) {
                mLoadService!!.setCallBack(
                    ErrorCallback::class.java,
                    Transport { context: Context, view: View ->
                        val mTvEmpty = view.findViewById<TextView>(R.id.tv_empty) as TextView
                        mTvEmpty.text = message
                    } as Transport)
                mLoadService!!.showCallback(ErrorCallback::class.java)
            } else {

                ToastUtils.showShortToast(message,requireActivity())
            }
        }
    }
    open fun showContent() {
        if (null != mLoadService) {
            isShowedContent = true
            mLoadService!!.showSuccess()
        }
    }


}