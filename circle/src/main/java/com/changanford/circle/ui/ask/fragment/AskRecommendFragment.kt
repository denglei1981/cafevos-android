package com.changanford.circle.ui.ask.fragment

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.bean.moreJumpData
import com.changanford.circle.databinding.FragmentAskRecommendBinding
import com.changanford.circle.databinding.HeaderCircleAskRecommendBinding
import com.changanford.circle.ui.ask.adapter.HotMechanicAdapter
import com.changanford.circle.ui.ask.adapter.RecommendAskAdapter
import com.changanford.circle.ui.ask.pop.CircleAskScreenDialog
import com.changanford.circle.ui.ask.request.AskRecommendViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.JumpDataBean
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.ResultData
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.listener.AskCallback
import com.changanford.common.manger.UserManger
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.toIntPx
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class AskRecommendFragment :
    BaseLoadSirFragment<FragmentAskRecommendBinding, AskRecommendViewModel>(),
    OnRefreshListener, OnLoadMoreListener {

    val recommendAskAdapter: RecommendAskAdapter by lazy {
        RecommendAskAdapter()
    }

    val hotMechanicAdapter: HotMechanicAdapter by lazy {
        HotMechanicAdapter()

    }
    var circleAskScreenDialog: CircleAskScreenDialog? = null

    var questionTypes = mutableListOf<String>()
    var questionTypeNames = mutableListOf<String>()

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
            val recommendData = recommendAskAdapter.getItem(position = position)
            // 埋点
            BuriedUtil.instant?.communityQuestion(
                recommendData.questionTypeName,
                recommendData.title
            )
            GIOUtils.homePageClick("热门问答", (position + 1).toString(), recommendData.title)
            JumpUtils.instans?.jump(recommendData.jumpType.toIntOrNull(), recommendData.jumpValue)
        }
        recommendAskAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.cl_user -> {
                    val bean = recommendAskAdapter.getItem(position)
                    JumpUtils.instans?.jump(114, bean.qaAnswer?.qaUserVO?.conQaUjId.toString())
                }
            }
        }
        addHeadView()
        initMarginTab()
        viewModel.getInitQuestion()
        viewModel.getQuestionList(false, questionTypes)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setOnLoadMoreListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getInitQuestion()
        viewModel.getQuestionList(false, questionTypes)
    }


    var headerBinding: HeaderCircleAskRecommendBinding? = null

    var moreJumpData: moreJumpData? = null
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
                    if (moreJumpData != null) {
                        JumpUtils.instans?.jump(
                            moreJumpData!!.jumpCode.toInt(),
                            moreJumpData!!.jumpVal
                        )
                        GIOUtils.homePageClick("问答红人", 0.toString(), "更多")
                    }
                }
                it.ryTopic.adapter = hotMechanicAdapter
                it.tvScreen.setOnClickListener {
                    showScreenDialog()
                }
                hotMechanicAdapter.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(
                        adapter: BaseQuickAdapter<*, *>,
                        view: View,
                        position: Int
                    ) {
                        val item = hotMechanicAdapter.getItem(position = position)
                        // 埋点
                        BuriedUtil.instant?.communityHOtEngineer(item.nickName)
                        GIOUtils.homePageClick("问答红人", (position + 1).toString(), item.nickName)
                        JumpUtils.instans?.jump(114, item.conQaUjId)
                    }

                })

                it.tvLook.setOnClickListener {
                    if (isLogin()) {
                        GIOUtils.homePageClick("我的问答", 0.toString(), "我的问答")
                        val param = SPUtils.getParam(requireContext(), "qaUjId", "")
                        JumpUtils.instans?.jump(114, param.toString())
                    }
                }
                it.tvGoQuestion.setOnClickListener {
                    if (isLogin()) {
                        JumpUtils.instans?.jump(116)
                    }
                }
//                it.cdMyAsk.setOnClickListener {
//                    //  跳转到我的问答
//                    if (isLogin()) {
//                        val param = SPUtils.getParam(requireContext(), "qaUjId", "")
//                        JumpUtils.instans?.jump(114, param.toString())
//                    }
//
//                }

            }
        }
    }

    fun isLogin(): Boolean {
        return if (TextUtils.isEmpty(MConstant.token)) {
            startARouter(ARouterMyPath.SignUI)
            false
        } else {
            true
        }
    }

    override fun observe() {
        super.observe()

//        viewModel.errorLiveData.observe(this, Observer {
//
//            binding.refreshLayout.finishRefresh()
//
//        })

        viewModel.mechanicLiveData.observe(this, Observer {

            SPUtils.setParam(requireContext(), "qaUjId", it.qaUjId)
            moreJumpData = it.moreTecnicians
            hotMechanicAdapter.setNewInstance(it.tecnicianVoList)
        })
        viewModel.questionListLiveData.observe(this, Observer {
            try {
                if (it.isSuccess) {
                    if (it.isLoadMore) {
                        binding.refreshLayout.finishLoadMore()
                        recommendAskAdapter.addData(it.data.dataList)
                    } else {
                        binding.refreshLayout.finishRefresh()
                        if (it.data.dataList.size == 0) {
                            val emptyList = arrayListOf<AskListMainData>()
                            val askEmpty = AskListMainData(emptyType = 1)
                            emptyList.add(askEmpty)
                            recommendAskAdapter.setNewInstance(emptyList)
                        } else {
                            recommendAskAdapter.setNewInstance(it.data.dataList)
                            binding.refreshLayout.setEnableLoadMore(true)
                        }
                    }
                    if (it.data == null || it.data.dataList.size < 20) {
                        binding.refreshLayout.finishLoadMoreWithNoMoreData()
                        binding.refreshLayout.setEnableLoadMore(false)
                    }
                } else {
                    binding.refreshLayout.finishRefresh()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
        LiveDataBus.get().with(LiveDataBusKey.CHANGE_TEACH_INFO).observe(this, Observer {
            viewModel.getInitQuestion()
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        viewModel.getInitQuestion()
                    }
                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        viewModel.getInitQuestion()
                    }
                    else -> {
                    }
                }
            }

    }


    fun showScreenDialog() {
        if (circleAskScreenDialog == null) {
            circleAskScreenDialog =
                CircleAskScreenDialog(requireActivity(), this, object : AskCallback {
                    override fun onResult(result: ResultData) {
                        when (result.resultCode) {
                            ResultData.OK -> {
                                val questionData = result.data as List<QuestionData>
                                questionTypes.clear()
                                questionData.forEach {
                                    questionTypes.add(it.dictValue)
                                    questionTypeNames.add(it.dictLabel)
                                }
                                //埋点
                                if (questionTypes.size > 0) {
                                    BuriedUtil.instant?.communityScreen(questionTypeNames.toString())
                                }
                                viewModel.getQuestionList(false, questionTypes)
                            }
                        }
                    }
                })
        }
        circleAskScreenDialog?.show()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getQuestionList(true, questionTypes)
    }

    override fun onRetryBtnClick() {

    }
    fun initMarginTab(){
        val body = HashMap<String, String>()
        body["dictType"] = "qa_question_type"
        val rkey = getRandomKey()
        lifecycleScope.launch {
            ApiClient.createApi<CircleNetWork>().getQuestionType(body.header(rkey), body.body(rkey))
                .onSuccess {
                    if (it!=null && it.size>0) {
                        //筛选改tab
                        val magicIndicator = headerBinding?.magicTabRemen
                        magicIndicator?.setBackgroundColor(Color.TRANSPARENT)
                        val commonNavigator = CommonNavigator(context)
                        commonNavigator.scrollPivotX = 0.8f
                        commonNavigator.adapter = object : CommonNavigatorAdapter() {
                            override fun getCount(): Int {
                                return it?.size ?: 0
                            }

                            override fun getTitleView(
                                context: Context,
                                index: Int
                            ): IPagerTitleView {
                                val simplePagerTitleView: SimplePagerTitleView =
                                    ScaleTransitionPagerTitleView(context)
                                simplePagerTitleView.text = it[index].dictLabel
//                simplePagerTitleView.textSize = 18f
                                simplePagerTitleView.setPadding(0, 0, 18.toIntPx(), 0)
                                simplePagerTitleView.normalColor =
                                    ContextCompat.getColor(context, R.color.color_8016)
                                simplePagerTitleView.selectedColor =
                                    ContextCompat.getColor(context, R.color.color_1700F4)
                                simplePagerTitleView.setOnClickListener {_->
//                                    val questionData = it[index]
                                    questionTypes.clear()
                                    questionTypes.add(it[index].dictValue)
                                    questionTypeNames.add(it[index].dictLabel)
                                    //埋点
                                    if (questionTypes.size > 0) {
                                        BuriedUtil.instant?.communityScreen(questionTypeNames.toString())
                                    }
                                    viewModel.getQuestionList(false, questionTypes)
                                    magicIndicator?.onPageSelected(index)
                                    magicIndicator?.onPageScrolled(index,0f,0)
                                }
                                return simplePagerTitleView
                            }

                            override fun getIndicator(context: Context): IPagerIndicator {
                                val indicator = LinePagerIndicator(context)
                                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                                indicator.lineHeight =
                                    UIUtil.dip2px(context, 3.0).toFloat()
                                indicator.lineWidth =
                                    UIUtil.dip2px(context, 22.0).toFloat()
                                indicator.roundRadius =
                                    UIUtil.dip2px(context, 1.5).toFloat()
                                indicator.startInterpolator = AccelerateInterpolator()
                                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                                indicator.setColors(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.transparent
                                    )
                                )
                                return indicator
                            }
                        }
                        magicIndicator?.navigator = commonNavigator
                    }
                }
        }

    }
}