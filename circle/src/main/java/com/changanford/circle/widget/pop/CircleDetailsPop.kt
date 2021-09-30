package com.changanford.circle.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.PopCircleDetailsBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose
 */
class CircleDetailsPop(
    private val context: Context,
    private val listener: CircleMainMenuPop.CheckPostType
) :
    BasePopupWindow(context) {
    private var binding: PopCircleDetailsBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_circle_details))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.TOP or Gravity.CENTER
        initView()
    }

    private fun initView() {
        binding.llLongBar.setOnClickListener {
            listener.checkLongBar()
            dismiss()
        }

        binding.llPicBar.setOnClickListener {
            listener.checkPic()
            dismiss()
        }

        binding.llVideoBar.setOnClickListener {
            listener.checkVideo()
            dismiss()
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.BOTTOM)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withScale(ScaleConfig.TOP_TO_BOTTOM)
            .toDismiss()
    }


}