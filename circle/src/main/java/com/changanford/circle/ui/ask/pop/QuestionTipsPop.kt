package com.changanford.circle.ui.ask.pop

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.PopQuestionTipsBinding
import com.changanford.common.wutil.ShadowDrawable
import razerdp.basepopup.BasePopupWindow

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
        ShadowDrawable.setShadowDrawable(
            binding.ryManagement, Color.parseColor("#FFFFFF"), 12,
            Color.parseColor("#1a000000"), 6, 0, 0
        )
    }


//    override fun onCreateShowAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withTranslation(
//                TranslationConfig()
//                    .from(Direction.TOP)
//            )
//            .toShow()
//    }
//
//    override fun onCreateDismissAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withTranslation(TranslationConfig.TO_TOP)
//            .toDismiss()
//    }

    fun initData(ruleString: String) {
        binding.ryManagement.text = ruleString
    }

    interface CheckPostType {
        fun checkLongBar()
        fun checkPic()
        fun checkVideo()
    }
}