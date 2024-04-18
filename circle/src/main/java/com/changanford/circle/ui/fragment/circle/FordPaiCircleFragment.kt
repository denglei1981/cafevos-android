package com.changanford.circle.ui.fragment.circle

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleRecommendAdapterV2
import com.changanford.circle.databinding.FragmentFordPaiCircleBinding
import com.changanford.circle.databinding.FragmentNewFordPaiCircleBinding
import com.changanford.circle.utils.CommunityCircleHelper
import com.changanford.circle.utils.CommunityHotHelper
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.CirceHomeBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateCircleDetailsData
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toIntPx
import com.changanford.common.widget.pop.MyCirclePop
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout


/**
 * @author: niubobo
 * @date: 2024/3/12
 * @description：
 */
class FordPaiCircleFragment : BaseFragment<FragmentNewFordPaiCircleBinding, NewCircleViewModel>() {

    private var checkPosition: Int? = null
    private lateinit var headBinding: FragmentFordPaiCircleBinding
    private val circleDetailsViewModel = CircleDetailsViewModel()
    private val headView by lazy {
        layoutInflater.inflate(R.layout.fragment_ford_pai_circle, null)
    }
    private val adapter by lazy {
        CircleRecommendAdapterV2(requireContext(), this)
    }
    private var selectTab = MutableLiveData<Int>()
    private lateinit var communityHotHelper: CommunityHotHelper
    private lateinit var communityCircleHelper: CommunityCircleHelper
    private val leftViews = arrayListOf<ShapeableImageView>()
    private val rightViews = arrayListOf<ShapeableImageView>()
    private var nowCircleId: String = ""
    private var nowType = 0
    private var isLoginChange = false
    private var page = 1
    private var adapterList = ArrayList<PostDataBean>()
    private var isFirst = true

    override fun initView() {

    }

    private fun initListener() {
        headBinding.apply {
            vLeft.setOnClickListener { selectTab.value = 0 }
            vRight.setOnClickListener { selectTab.value = 1 }
            tvJoinNum.setOnClickListener { showMyCirclePop() }
            ivIconRight.setOnClickListener { showMyCirclePop() }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            getCircleDeleteData()
        }
        binding.srl.setOnRefreshListener {
            page = 1
//            communityCircleHelper.initCommunity(nowCircleId)
            communityHotHelper.initData()
            it.finishRefresh()
        }
        adapter.setOnItemClickListener { _, view, position ->
            GIOUtils.circleDetailPageResourceClick(
                "帖子信息流",
                (position + 1).toString(),
                adapter.getItem(position).title
            )
            updateCircleDetailsData(adapter.getItem(position).title.toString(), "帖子详情页")
            val bundle = Bundle()
            bundle.putString("postsId", adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            checkPosition = position
        }
    }

    private fun initTab() {
        nowType = circleDetailsViewModel.circleType[0].toInt()
        headBinding.layoutCircle.homeTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.white
            )
        )
        headBinding.layoutCircle.homeTab.tabRippleColor = null

        headBinding.layoutCircle.homeTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page = 1
                selectTab(tab, true)
                nowType =
                    circleDetailsViewModel.circleType[headBinding.layoutCircle.homeTab.selectedTabPosition].toInt()
                getCircleDeleteData(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        val tabs = circleDetailsViewModel.tabList
        for (i in 0 until tabs.size) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_circle, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)
            val bean = tabs[i]

            mTabText.text = bean
            if (0 == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(
                    ContextCompat.getColor(
                        MyApp.mContext,
                        R.color.color_1700F4
                    )
                )
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_8016))
                mTabText.textSize = 16f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
            val tab = headBinding.layoutCircle.homeTab.newTab()
            tab.customView = view
            headBinding.layoutCircle.homeTab.addTab(tab)
            headBinding.layoutCircle.homeTab.selectedTabPosition
//            headBinding.layoutCircle.homeTab.getTabAt(i)?.customView = view
        }
    }

    private fun getCircleDeleteData(isShowLoading: Boolean = false) {
        if (MConstant.token.isNotEmpty()) {
            circleDetailsViewModel.getListDataCircle(
                nowType,
                "",
                nowCircleId,
                page,
                MConstant.userId,
                isShowLoading
            )
        }
    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        val mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            // 埋点
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_1700F4))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_8016))
            mTabText?.textSize = 16f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗
        }
    }

    private fun setCircleNumSize(isLeft: Boolean, num: String) {
        val content = if (isLeft) "已加入" else "待加入"
        val all = "$content${num}个"
        val spanString = SpannableString(all)
        val span = AbsoluteSizeSpan(45)

        spanString.setSpan(
            span,
            content.length,
            content.length + num.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        if (isLeft) {
            headBinding.tvJoinNum.text = spanString
        } else {
            headBinding.tvHotNum.text = spanString
        }
    }

    override fun initData() {

    }


    override fun observe() {
        super.observe()

    }

    private fun initMyObServe() {
        circleDetailsViewModel.listBean.observe(this) {
            if (page == 1) {
                adapter.setList(it.dataList)
                headBinding.layoutCircle.headEmpty.root.isVisible = it.dataList.size == 0
                if (selectTab.value == 1) {
                    adapterList.clear()
                    adapterList.addAll(adapter.data)
                    adapter.data.clear()
                }
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
        LiveDataBus.get().withs<String>(LiveDataBusKey.HOME_CIRCLE_CHECK_ID).observe(this) {
            nowCircleId = it
            communityCircleHelper.initCommunity(it)
            page = 1
            getCircleDeleteData()
        }
        selectTab.observe(this) {
            headBinding.apply {
                if (it == 0) {
                    adapter.setList(adapterList)
                    isShowCircle(false)
                    ivTabBg.setImageResource(R.mipmap.ic_circle_tab_my_circle)
                    tvMyCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_16
                        )
                    )
                    tvJoinNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_16
                        )
                    )
                    tvHotCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    tvHotNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    ivIconRight.setImageResource(R.mipmap.ic_circle_jion_num_right_b)

                    val layoutParam = tvMyCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam.topMargin = 40.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 18f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 53.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 14f

                } else {
//                    adapter.setEmptyView(R.layout.empty_nothing)
                    adapterList.clear()
                    adapterList.addAll(adapter.data)
                    adapter.data.clear()
//                    adapterList= adapter.data as ArrayList<PostDataBean>
                    isShowCircle(true)
                    ivTabBg.setImageResource(R.mipmap.ic_circle_tab_hot_circle)
                    tvMyCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    tvJoinNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    tvHotCircle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_16
                        )
                    )
                    tvHotNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_16
                        )
                    )
                    ivIconRight.setImageResource(R.mipmap.ic_circle_jion_num_right_w)

                    val layoutParam = tvMyCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam.topMargin = 50.toIntPx()
                    tvMyCircle.layoutParams = layoutParam
                    tvMyCircle.textSize = 14f

                    val layoutParam2 = tvHotCircle.layoutParams as ConstraintLayout.LayoutParams
                    layoutParam2.topMargin = 35.toIntPx()
                    tvHotCircle.layoutParams = layoutParam2
                    tvHotCircle.textSize = 18f
                }
            }

        }

        communityHotHelper.myCircles.observe(this) {
            if (!it.isNullOrEmpty()) {
                page = 1
                communityCircleHelper.initCommunity(it[0].circleId)
                nowCircleId = it[0].circleId
                val useList = if (it.size > 3) {
                    it.subList(0, 3)
                } else it
                useList.forEachIndexed { index, data ->
                    leftViews[index].setCircular(4)
                    leftViews[index].load(data.pic)
                }
                getCircleDeleteData()
            }
        }
        LiveDataBus.get().withs<CirceHomeBean?>(LiveDataBusKey.HOME_CIRCLE_HOT_BEAN).observe(this) {
            it?.let {
                setCircleNumSize(false, it.noJoinCircleNum.toString())
                setCircleNumSize(true, it.joinCircleNum.toString())
                val useList = if (it.noJoinCirclePics.size > 3) {
                    it.noJoinCirclePics.subList(0, 3)
                } else it.noJoinCirclePics
                useList.forEachIndexed { index, data ->
                    rightViews[index].setCircular(4)
                    rightViews[index].load(data)
                }
            }
        }
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this) {
            val bean = checkPosition?.let { it1 -> adapter.getItem(it1) }
            bean?.let { _ ->
                bean.isLike = it
                if (bean.isLike == 1) {
                    bean.likesCount++
                } else {
                    bean.likesCount--
                }
            }

            checkPosition?.let { it1 -> adapter.notifyItemChanged(it1) }
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.DELETE_CIRCLE_POST).observe(this) {
            checkPosition?.let { it1 -> adapter.data.removeAt(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRemoved(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRangeChanged(it1, adapter.itemCount) }
        }
    }

    private fun isShowCircle(isShowLeft: Boolean) {
        headBinding.layoutHot.root.isVisible = isShowLeft
        headBinding.layoutCircle.root.isVisible = !isShowLeft
    }

    private fun setTopCircleData() {

        headBinding.layoutLeftThree.apply {
            leftViews.add(ivOne)
            leftViews.add(ivTwo)
            leftViews.add(ivThree)
        }
        headBinding.layoutRightThree.apply {
            rightViews.add(ivOne)
            rightViews.add(ivTwo)
            rightViews.add(ivThree)
        }

    }

    private fun showMyCirclePop() {
        MyCirclePop(requireContext()).run {
            setBackgroundColor(Color.TRANSPARENT)
            showPopupWindow(headBinding.vPop)
            communityHotHelper.myCircles.value?.let { initPopData(it, nowCircleId) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            binding.ryFragment.adapter = adapter
            headBinding = DataBindingUtil.bind(headView)!!
            adapter.addHeaderView(headView)
            communityHotHelper = CommunityHotHelper(
                headBinding.layoutHot,
                headBinding,
                viewModel,
                this@FordPaiCircleFragment
            )
            communityCircleHelper =
                CommunityCircleHelper(headBinding.layoutCircle, this)
            communityHotHelper.initCommunity()
            if (MConstant.token.isEmpty()) {
                selectTab.value = 1
            }
            initTab()
            initListener()
            setTopCircleData()
            addLiveDataBus()
            adapter.headerWithEmptyEnable = true

            initMyObServe()
        }
        if (isLoginChange) {
            isLoginChange = false
            if (MConstant.token.isEmpty()) {
                selectTab.value = 1
            } else {
                selectTab.value = 0
            }
            communityHotHelper.initData()
        }
        headBinding.clTab.isVisible = MConstant.token.isNotEmpty()
    }

    private fun addLiveDataBus() {
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        isLoginChange = true
                    }

                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        isLoginChange = true
                    }

                    else -> {}
                }
            }
    }
}