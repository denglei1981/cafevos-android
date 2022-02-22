package com.changanford.circle.ui.fragment.question

import android.os.Bundle
import android.view.View
import com.changanford.circle.R
import com.changanford.circle.adapter.question.QuestionListAdapter
import com.changanford.circle.databinding.FragmentQuestionBinding
import com.changanford.circle.ui.compose.EmptyCompose
import com.changanford.circle.ui.compose.EmptyQuestionCompose
import com.changanford.circle.viewmodel.question.QuestionViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.listener.OnPerformListener

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
        fun newInstance(conQaUjId:String,personalPageType: String,isOneself:Boolean,identity:Int): QuestionFragment {
            val bundle = Bundle()
            bundle.putString("conQaUjId", conQaUjId)
            bundle.putString("personalPageType", personalPageType)
            bundle.putBoolean("isOneself", isOneself)
            bundle.putInt("identity", identity)
            val fragment = QuestionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    val mAdapter by lazy { QuestionListAdapter(requireActivity()) }
    private var conQaUjId=""
    private var personalPageType=""
    private var isOneself=true
    private var pageNo=1
    private var identity=0//身份标识 0 普通  1 技师  2车主
    private var listener: OnPerformListener?=null
    override fun initView() {
        arguments?.apply {
            conQaUjId=getString("conQaUjId","0")
            personalPageType=getString("personalPageType","0")
            isOneself=getBoolean("isOneself",false)
            identity=getInt("identity",0)
        }
        binding.apply {
            mAdapter.identity=identity
            recyclerView.apply {
                adapter=mAdapter
                setBackgroundResource(if(identity==1)R.color.transparent else R.drawable.circle_white_5_bg)
            }
//            mAdapter.setEmptyView(R.layout.empty_ask)
            smartRl.setOnLoadMoreListener {
                pageNo++
                getData()
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
                if (1 == pageNo)mAdapter.setList(this)
                else mAdapter.addData(this)
            }
            binding.smartRl.apply {
                listener?.onFinish(mAdapter.data.size)
                binding.composeView.visibility=if(1==pageNo&&it?.dataList.isNullOrEmpty()) View.VISIBLE else View.GONE
                if (null == it || mAdapter.data.size >= it.total) setEnableLoadMore(false)
                else setEnableLoadMore(true)
                finishLoadMore()
                finishRefresh()
            }
        }
        getData()
    }
    private fun getData(){
        //是自己并且是技师，查看邀请回答列表
        if(personalPageType=="TECHNICIAN"&&isOneself) viewModel.questionOfInvite(pageNo)
        else viewModel.questionOfPersonal(conQaUjId,personalPageType,pageNo)
    }
    fun startRefresh(){
        pageNo=1
        getData()
    }
    fun setOnPerformListener(listener: OnPerformListener){
        this.listener=listener
    }
}