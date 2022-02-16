package com.changanford.common.buried

import io.reactivex.disposables.Disposable

/**
 * @Author: hpb
 * @Date: 2020/4/23
 * @Des: ViewModel接口
 */
interface IViewModel {

    /**
     * 弹出加载dialog
     */
    fun showLoadingDialog()

    /**
     * 隐藏加载dialog
     */
    fun dismissLoadingDialog()

    /**
     * 添加
     */
    fun addDispose(disposable: Disposable)

    /**
     * 删除
     */
    fun removeDispose(disposable: Disposable)

    /**
     * 清空
     */
    fun clearDispose()
}