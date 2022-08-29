package com.changanford.circle.ui.ask.pop

import android.content.Context
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.PopQuestionTipsBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class QuestionTipsPop(private val context: Context, private val listener: CheckPostType) :
    BasePopupWindow(context) {

    private var binding: PopQuestionTipsBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_question_tips))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
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

    fun initData(ruleString: String) {
        binding.ryManagement.text = ruleString
    }

    interface CheckPostType {
        fun checkLongBar()
        fun checkPic()
        fun checkVideo()
    }
}