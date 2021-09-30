package com.changanford.common.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.changanford.common.R


/**
 * @Author: hpb
 * @Date: 2020/4/26
 * @Des: 父类Dialog
 */
abstract class BaseDialog(context: Context, @StyleRes themeResId: Int) :
    Dialog(context, themeResId) {

    constructor(context: Context) : this(context, R.style.DialogThemeStyle)

    init {
        setContentView(getLayoutId())
        setCanceledOnTouchOutside(true) //默认点击Dialog外部消失
    }

    /**
     * 布局文件
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected fun setParamWidthMatch(){
        setParamWidth(WindowManager.LayoutParams.MATCH_PARENT)
    }

    protected fun setParamWidth(width: Int) {
        window?.attributes?.width = width
    }

    protected fun setParamHeight(width: Int) {
        window?.attributes?.width = width
    }
}