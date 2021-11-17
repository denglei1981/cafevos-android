package com.changanford.circle.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleMainMenuAdapter
import com.changanford.circle.bean.CircleMainMenuBean
import com.changanford.circle.databinding.PopCircleMainMenuBinding
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class CircleMainMenuPop(private val context: Context, private val listener: CheckPostType) :
    BasePopupWindow(context) {

    private var binding: PopCircleMainMenuBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_circle_main_menu))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
    }

    private val adapter by lazy {
        CircleMainMenuAdapter(context)
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.TOP)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_TOP)
            .toDismiss()
    }

    fun initData() {
        val list = arrayListOf(
            CircleMainMenuBean(R.mipmap.circle_post_long_bar, "发长帖"),
            CircleMainMenuBean(R.mipmap.circle_post_pic, "图片"),
            CircleMainMenuBean(R.mipmap.circle_post_video, "视频")
        )
        adapter.setItems(list)
        binding.ryManagement.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

                when (position) {
                    0 -> {
                        listener.checkLongBar()
                    }
                    1 -> {
                        listener.checkPic()
                    }
                    2 -> {
                        listener.checkVideo()
                    }
                }
                dismiss()
            }

        })
    }

    interface CheckPostType {
        fun checkLongBar()
        fun checkPic()
        fun checkVideo()
    }
}