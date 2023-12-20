package com.changanford.common.widget.smart

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.changanford.common.R
import com.changanford.common.util.ext.setAppColor
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.scwang.smart.refresh.layout.util.SmartUtil

class MyHeaderView : LinearLayout, RefreshHeader {
    private var imageView: AnimatorImageView? = null

    constructor(context: Context?) : super(context) {
        initview(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initview(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initview(context)
    }

    override fun getView(): View {
        return this
    }

    fun initview(context: Context?) {
        gravity = Gravity.CENTER
        imageView = AnimatorImageView(context)
        addView(imageView)
        minimumHeight = SmartUtil.dp2px(60f)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
//        imageView.animator(false);
        imageView?.clearColorFilter()
        imageView?.setImageResource(R.mipmap.refreshtitlenomale)
        return 100
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None -> {
                imageView?.clearColorFilter()
                imageView?.setImageResource(R.mipmap.refreshtitlenomale)
            }

            RefreshState.PullDownToRefresh -> {
                imageView?.clearColorFilter()
                imageView?.setImageResource(R.mipmap.refreshtitlenomale)
            }

            RefreshState.Refreshing -> {
                imageView?.setAppColor()
                imageView?.setImageResource(R.drawable.animation_listpicitem)
                imageView?.animator(true)
            }

            RefreshState.ReleaseToRefresh -> {
                imageView?.setAppColor()
                imageView?.setImageResource(R.mipmap.refreshtitleset)
            }

            else -> {}
        }
    }
}