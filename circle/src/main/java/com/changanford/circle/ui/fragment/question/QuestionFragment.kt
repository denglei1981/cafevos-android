package com.changanford.circle.ui.fragment.question

import android.os.Bundle
import com.changanford.circle.databinding.FragmentQuestionBinding
import com.changanford.circle.viewmodel.QuestionViewModel
import com.changanford.common.basic.BaseFragment

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionFragment
 */
class QuestionFragment:BaseFragment<FragmentQuestionBinding,QuestionViewModel>() {
    companion object {
        fun newInstance(topId: Int): QuestionFragment {
            val bundle = Bundle()
            bundle.putInt("topId", topId)
            val fragment = QuestionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun initView() {

    }

    override fun initData() {

    }
}