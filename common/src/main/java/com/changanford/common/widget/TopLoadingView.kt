package com.changanford.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieAnimationView
import com.changanford.common.R
import com.changanford.common.databinding.DialogLoadingBinding
import com.changanford.common.databinding.LayoutCommentLoadingTopBinding

/**
 * Author lcw
 * Time on 2023/2/3
 * Purpose
 */
class TopLoadingView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_comment_loading_top, this, true)
        val loading = findViewById<LottieAnimationView>(R.id.loading)
        loading.scale = 8f
    }
}