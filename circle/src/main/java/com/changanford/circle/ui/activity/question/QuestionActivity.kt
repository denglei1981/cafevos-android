package com.changanford.circle.ui.activity.question

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ActivityQuestionBinding
import com.changanford.circle.viewmodel.QuestionViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath

/**
 * @Author : wenke
 * @Time : 2022/1/24
 * @Description : 我的问答、TA的问答
 */
@Route(path = ARouterCirclePath.QuestionActivity)
class QuestionActivity:BaseActivity<ActivityQuestionBinding, QuestionViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }
}