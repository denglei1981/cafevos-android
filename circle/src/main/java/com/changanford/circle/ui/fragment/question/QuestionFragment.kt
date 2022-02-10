package com.changanford.circle.ui.fragment.question

import android.os.Bundle
import com.changanford.circle.R
import com.changanford.circle.adapter.question.QuestionListAdapter
import com.changanford.circle.databinding.FragmentQuestionBinding
import com.changanford.circle.viewmodel.question.QuestionViewModel
import com.changanford.common.basic.BaseFragment

/**
 * @Author : wenke
 * @Time : 2022/1/24 0024
 * @Description : QuestionFragment
 */
class QuestionFragment:BaseFragment<FragmentQuestionBinding, QuestionViewModel>() {
    companion object {
        /**
         * [conQaUjId]被查看人的问答参与表id
         * [personalPageType]
         * [isOneself]是否是自己查看
        * */
        fun newInstance(conQaUjId:String,personalPageType: String,isOneself:Boolean): QuestionFragment {
            val bundle = Bundle()
            bundle.putString("conQaUjId", conQaUjId)
            bundle.putString("personalPageType", personalPageType)
            bundle.putBoolean("isOneself", isOneself)
            val fragment = QuestionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val mAdapter by lazy { QuestionListAdapter(requireActivity()) }
    private var conQaUjId=""
    private var personalPageType=""
    private var isOneself=true
    private var pageNo=1
    override fun initView() {
        binding.recyclerView.apply {
            adapter=mAdapter
            mAdapter.setEmptyView(R.layout.base_layout_empty)
        }
        binding.smartRl.setOnLoadMoreListener {
            pageNo++
            viewModel.questionOfPersonal(conQaUjId,personalPageType,pageNo)
        }
    }

    override fun initData() {
        arguments?.apply {
            conQaUjId=getString("conQaUjId","0")
            personalPageType=getString("personalPageType","0")
            isOneself=getBoolean("isOneself")
            viewModel.questionOfPersonal(conQaUjId,personalPageType,pageNo)
        }
        viewModel.questionListBean.observe(this){
            it?.dataList?.apply {
                if (1 == pageNo) mAdapter.setList(this)
                else mAdapter.addData(this)
            }
            binding.smartRl.apply {
                if (null == it || mAdapter.data.size >= it.total) setEnableLoadMore(false)
                else setEnableLoadMore(true)
                finishLoadMore()
                finishRefresh()
            }
        }
    }
}