package com.changanford.common.buried

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.dialog.LoadDialogdy
import com.changanford.common.util.MConstant
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author hpb
 * @Date 2020/4/2 23:52
 * @Des 父类ViewModel
 */
open class BaseViewModel(val context: Context) : AndroidViewModel(BaseApplication.INSTANT),
    IViewModel {

    //统一管理retrofit请求接口
    private var cd: CompositeDisposable? = null

    //加载dialog
    private var loadingDialog: LoadDialogdy? = null
    /**
     * 弹出加载dialog
     */
    override fun showLoadingDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                if (loadingDialog == null) {
                    loadingDialog = LoadDialogdy(context)
                }
                loadingDialog!!.show()
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 隐藏加载dialog
     */
    override fun dismissLoadingDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                loadingDialog?.run {
                    if (isShowing) dismiss()
                }
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 添加请求
     */
    override fun addDispose(disposable: Disposable) {
        if (cd == null) {
            cd = CompositeDisposable()
        }
        cd!!.add(disposable)
    }

    /**
     * 删除相关请求
     */
    override fun removeDispose(disposable: Disposable) {
        cd?.remove(disposable)
    }

    /**
     * 清除所有请求
     */
    override fun clearDispose() {
        cd?.clear()
    }

    /**
     * ViewModel结束自动结束掉接口请求
     */
    override fun onCleared() {
        dismissLoadingDialog()
        clearDispose()
        super.onCleared()
    }





//    /**
//     * 获取绑定手机jumpDataType true跳转 false 不跳转
//     */
//    fun getBindMobileJumpDataType(): Boolean {
//        if (!MConstant.mine_bind_mobile_jump_data.isNullOrEmpty() &&
//            MConstant.mine_bind_mobile_jump_data == LiveDataBusKey.MINE_SIGN_OTHER_CODE.toString()
//        ) {
//            return true
//        }
//        return false
//    }


    /**
     * 是否登录，token不null:true登录，
     */
    fun isLogin(): Boolean = MConstant.token.isNotEmpty()
}