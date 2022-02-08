package com.changanford.circle.ui.fragment.question

import android.os.Bundle
import com.changanford.circle.R
import com.changanford.circle.adapter.question.QuestionListAdapter
import com.changanford.circle.databinding.FragmentQuestionBinding
import com.changanford.circle.viewmodel.QuestionViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.QuestionInfoBean

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
    private val mAdapter by lazy { QuestionListAdapter(requireActivity()) }
    override fun initView() {
        binding.recyclerView.apply {
            adapter=mAdapter
            mAdapter.setEmptyView(R.layout.base_layout_empty)
        }
    }

    override fun initData() {
        val dataList= arrayListOf<QuestionInfoBean>()
        for(i in 0..10){
            dataList.add(QuestionInfoBean())
        }
        mAdapter.setList(dataList)
    }
}