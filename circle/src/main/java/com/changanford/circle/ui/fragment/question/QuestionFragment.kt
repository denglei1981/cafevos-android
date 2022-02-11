package com.changanford.circle.ui.fragment.question

import android.os.Bundle
import android.view.View
import com.changanford.circle.adapter.question.QuestionListAdapter
import com.changanford.circle.databinding.FragmentQuestionBinding
import com.changanford.circle.ui.compose.EmptyCompose
import com.changanford.circle.ui.compose.EmptyQuestionCompose
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
        arguments?.apply {
            conQaUjId=getString("conQaUjId","0")
            personalPageType=getString("personalPageType","0")
            isOneself=getBoolean("isOneself")
        }
        binding.apply {
            recyclerView.apply {
                adapter=mAdapter
//            mAdapter.setEmptyView(R.layout.empty_ask)
            }
            smartRl.setOnLoadMoreListener {
                pageNo++
                viewModel.questionOfPersonal(conQaUjId,personalPageType,pageNo)
            }
            composeView.setContent {
                if(isOneself&&personalPageType=="QUESTION")EmptyQuestionCompose() //是自己并且是提问tab 则展示提问特有缺省页
                else EmptyCompose()//普通缺省页
            }
        }

    }
    override fun initData() {
        viewModel.questionListBean.observe(this){
            it?.dataList?.apply {
                if (1 == pageNo) mAdapter.setList(this)
                else mAdapter.addData(this)
            }
            binding.smartRl.apply {
                binding.composeView.visibility=if(1==pageNo&&it?.dataList.isNullOrEmpty()) View.VISIBLE else View.GONE
                if (null == it || mAdapter.data.size >= it.total) setEnableLoadMore(false)
                else setEnableLoadMore(true)
                finishLoadMore()
                finishRefresh()
            }
        }
        viewModel.questionOfPersonal(conQaUjId,personalPageType,pageNo)
    }
}