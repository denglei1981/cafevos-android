package com.changanford.common.widget.smart

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.changanford.common.R
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * @Author: hpb
 * @Date: 2020/5/8
 * @Des: 二楼加载效果
 */
class MyTwoLevelHeader(context: Context?, attrs: AttributeSet?) :
    ClassicsHeader(context, attrs) {

    private lateinit var goTwoView: View

    constructor(context: Context?) :
            this(context, null)

    init {
        View.inflate(context, R.layout.my_two_level_header, this).apply {
            goTwoView = findViewById(R.id.go_two_lin)
        }
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None -> mLastUpdateText.visibility =
                if (mEnableLastTime) View.VISIBLE else View.GONE
            RefreshState.ReleaseToTwoLevel -> {
                mTitleText.visibility = View.GONE
                mArrowView.visibility = View.GONE
                mLastUpdateText.visibility = View.GONE
                mProgressView.visibility = View.GONE
                goTwoView.visibility = View.VISIBLE
            }
            else -> {
                mTitleText.visibility = View.VISIBLE
                mLastUpdateText.visibility = View.VISIBLE
                goTwoView.visibility = View.GONE
            }
        }
        super.onStateChanged(refreshLayout, oldState, newState)
    }

}