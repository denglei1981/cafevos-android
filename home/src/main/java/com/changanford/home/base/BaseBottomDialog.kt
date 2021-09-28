package com.changanford.home.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.changanford.common.basic.BaseViewModel
import com.changanford.home.net.getVmClazz
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



/**
 * @Description: java类作用描述
 * @Author: newway
 * @CreateDate: 2020/8/4 16:55
 * @UpdateUser:
 * @UpdateDate: 2020/8/4 16:55
 * @UpdateRemark: 更新说明
 */

abstract  class BaseBottomDialog<VM: BaseViewModel,DB : ViewDataBinding> : BottomSheetDialogFragment() {




    //是否第一次加载
    private var isFirst: Boolean = true
    //该类负责绑定视图数据的Viewmodel
    lateinit var mViewModel: VM
    //该类绑定的ViewDataBinding
    lateinit var mDatabind: DB
    //界面状态管理者
//     var mLoadService: LoadService<Any>?=null
    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mDatabind = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        mDatabind.lifecycleOwner = this
        return mDatabind.root
    }
    open fun setLoadSir(view: View?) {
//        if (mLoadService == null) {
//            mLoadService = LoadSir.getDefault().register(view, OnReloadListener { v: View? -> onRetryBtnClick() })
//        }
    }

    /**
     * 失败重试,重新加载事件
     */
    protected abstract fun onRetryBtnClick()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = createViewModel()
        initView(savedInstanceState)
        onVisible()
        initData()
    }

    /**
     * 网络变化监听 子类重写
     */
//    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory(this.requireActivity().application)
        ).get(getVmClazz(this))
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 创建观察者
     */
    abstract fun createObserver()


    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
            createObserver()
//            NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
//                onNetworkStateChanged(it)
//            })
        }
    }

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()
    private var isShowedContent = false
//    open fun showLoading() {
//        if (null != mLoadService) {
//            mLoadService!!.showCallback(LoadingCallback::class.java)
//        }
//    }

//    open fun showEmpty() {
//        if (null != mLoadService) {
//            mLoadService!!.showCallback(EmptyCommentCallback::class.java)
//        }
//    }

//    open fun showFailure(message: String?) {
//        if (null != mLoadService) {
//            if (!isShowedContent) {
//                mLoadService!!.showCallback(ErrorCallback::class.java)
//            } else {
////                ToastUtil.show(, message)
//            }
//        }
//    }
//    open fun showContent() {
//        if (null != mLoadService) {
//            isShowedContent = true
//            mLoadService!!.showSuccess()
//        }
//    }

}