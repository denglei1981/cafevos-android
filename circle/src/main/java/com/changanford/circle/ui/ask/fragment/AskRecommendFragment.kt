package com.changanford.circle.ui.ask.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.R
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.databinding.FragmentAskRecommendBinding
import com.changanford.circle.databinding.HeaderCircleAskRecommendBinding
import com.changanford.circle.ui.ask.adapter.HotMechanicAdapter
import com.changanford.circle.ui.ask.adapter.RecommendAskAdapter
import com.changanford.circle.ui.ask.pop.CircleAskScreenDialog
import com.changanford.circle.ui.ask.request.AskRecommendViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.ResultData
import com.changanford.common.listener.AskCallback
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class AskRecommendFragment : BaseLoadSirFragment<FragmentAskRecommendBinding, AskRecommendViewModel>(),
    OnRefreshListener,OnLoadMoreListener {

    val recommendAskAdapter: RecommendAskAdapter by lazy {
        RecommendAskAdapter()
    }

    val hotMechanicAdapter: HotMechanicAdapter by lazy {
        HotMechanicAdapter()

    }
    var circleAskScreenDialog: CircleAskScreenDialog?=null

    var  questionTypes = mutableListOf<String>()
    companion object {
        fun newInstance(): AskRecommendFragment {
//            val bundle = Bundle()
//            bundle.putInt("type", type)
            val fragment = AskRecommendFragment()
//            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {

    }

    override fun initData() {
        binding.ryAsk.adapter = recommendAskAdapter
        recommendAskAdapter.setOnItemClickListener { adapter, view, position ->
//            startARouter(ARouterCirclePath.CreateQuestionActivity, true)
               val recommendData= recommendAskAdapter.getItem(position = position)
             JumpUtils.instans?.jump(recommendData.jumpType.toIntOrNull(),recommendData.jumpValue)
        }
        addHeadView()
        viewModel.getInitQuestion()
        viewModel.getQuestionList(false,questionTypes)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setOnLoadMoreListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getInitQuestion()
        viewModel.getQuestionList(false,questionTypes)
    }


    var headerBinding: HeaderCircleAskRecommendBinding? = null
    private fun addHeadView() {
        if (headerBinding == null) {
            headerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_circle_ask_recommend,
                binding.ryAsk,
                false
            )
            headerBinding?.let {
                recommendAskAdapter.addHeaderView(it.root)
                it.tvMore.setOnClickListener {
                    startARouter(ARouterCirclePath.HotTopicActivity)
                }
                it.ryTopic.adapter=hotMechanicAdapter
                it.tvScreen.setOnClickListener {
                    showScreenDialog()
                }
                hotMechanicAdapter.setOnItemClickListener(object :OnItemClickListener{
                    override fun onItemClick(
                        adapter: BaseQuickAdapter<*, *>,
                        view: View,
                        position: Int
                    ) {
//                        startARouter(ARouterCirclePath.MechanicMainActivity,)
                        JumpUtils.instans?.jump(114,hotMechanicAdapter.getItem(position = position).qaTechnicianId.toString())
                    }

                })

                it.tvLook.setOnClickListener {
                   JumpUtils.instans?.jump(114)
                }

            }

        }
    }

    override fun observe() {
        super.observe()
        viewModel.mechanicLiveData.observe(this, Observer {
            hotMechanicAdapter.setNewInstance(it.tecnicianVoList)
        })
        viewModel.questionListLiveData.observe(this, Observer {
            try {
                if(it.isSuccess){
                    if(it.isLoadMore){
                        binding.refreshLayout.finishLoadMore()
                        recommendAskAdapter.addData(it.data.dataList)
                    }else{
                        binding.refreshLayout.finishRefresh()
                        binding.refreshLayout.setEnableLoadMore(true)
                        if(it.data.dataList.size==0){
                            val emptyList = arrayListOf<AskListMainData>()
                            val askEmpty=AskListMainData(emptyType = 1)
                            emptyList.add(askEmpty)
                            recommendAskAdapter.setNewInstance(emptyList)
                        }else{
                            recommendAskAdapter.setNewInstance(it.data.dataList)
                        }
                    }
                    if(it.data==null||it.data.dataList.size<20){
                        binding.refreshLayout.finishLoadMoreWithNoMoreData()
                        binding.refreshLayout.setEnableLoadMore(false)
                    }
                }else{
                    binding.refreshLayout.finishRefresh()
                }

            }catch (e :Exception){
                e.printStackTrace()
            }

        })
        LiveDataBus.get().with(LiveDataBusKey.CHANGE_TEACH_INFO).observe(this, Observer {
             viewModel.getInitQuestion()
        })

        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when(it){
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS->{
                        viewModel.getInitQuestion()
                    }
                    UserManger.UserLoginStatus.USER_LOGIN_OUT->{
                        viewModel.getInitQuestion()
                    }
                    else -> {}
                }
            }

    }


    fun showScreenDialog(){
        if(circleAskScreenDialog==null){
            circleAskScreenDialog= CircleAskScreenDialog(requireActivity(),this,object :AskCallback{
                override fun onResult(result: ResultData) {
                    when(result.resultCode){
                        ResultData.OK->{
                           val  questionData=  result.data  as List<QuestionData>
                            questionTypes.clear()
                            questionData.forEach {
                                questionTypes.add(it.dictValue)
                            }
                            viewModel.getQuestionList(false,questionTypes)
                        }
                    }
                }
            })
        }
        circleAskScreenDialog?.show()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getQuestionList(true,questionTypes)
    }

    override fun onRetryBtnClick() {

    }

}