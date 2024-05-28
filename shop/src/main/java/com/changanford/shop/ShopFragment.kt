package com.changanford.shop

import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.databinding.ItemShopTabBinding
import com.changanford.common.manger.UserManger
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.image.ItemCommonPics
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.setDrawableNull
import com.changanford.common.widget.pop.CircleMainMenuPop
import com.changanford.common.wutil.ViewPage2AdapterFragment
import com.changanford.shop.adapter.goods.GoodsKillAdapter
import com.changanford.shop.adapter.goods.ShopRecommendListAdapter1
import com.changanford.shop.control.BannerControl
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.goods.GoodsKillAreaActivity
import com.changanford.shop.ui.goods.GoodsListFragment
import com.changanford.shop.ui.goods.RecommendActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.net.URLDecoder
import kotlin.math.abs


/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopLayoutBinding, GoodsViewModel>(), OnRefreshListener {
    private val fragments = arrayListOf<GoodsListFragment>()
    private val mAdapter by lazy { GoodsKillAdapter() }
    private val dp38 by lazy { ScreenUtils.dp2px(requireContext(), 38f) }
    private val recommendAdapter by lazy { ShopRecommendListAdapter1() }
    private var defaultTagName: String? = null
    private var oldPosition = 0
    private var selectFragmentPosition = 0
    override fun initView() {
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener { appBarLayout, i ->
            val absOffset = abs(i).toFloat() * 2.5F
            val absOffset2 = abs(i).toFloat() * 1.5F
            if (absOffset2 > MConstant.deviceHeight) {
                binding.ivToTop.visibility = View.VISIBLE
                binding.smartRl.isEnabled = false
            } else {
                binding.ivToTop.visibility = View.INVISIBLE
                binding.smartRl.isEnabled = true
            }

            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.toolbar.background.mutate().alpha = mAlpha
                binding.inHeader.apply {
                    tvShopTips.visibility = View.INVISIBLE
                    imgSearch.setImageResource(R.mipmap.shop_search)
                    imgBuyCar.setImageResource(R.mipmap.shop_buy_car)
                    imgMenu.setImageResource(R.mipmap.shop_menu)
                }
            } else {
                binding.toolbar.background.mutate().alpha = 255
                binding.inHeader.apply {
                    tvShopTips.visibility = View.VISIBLE
                    imgSearch.setImageResource(R.mipmap.shop_search_b)
                    imgBuyCar.setImageResource(R.mipmap.shop_buy_car_b)
                    imgMenu.setImageResource(R.mipmap.shop_menu_b)
                }
            }
        }
        AppUtils.setStatusBarPaddingTop(binding.toolbar, requireActivity())
        addLiveDataBus()
        addObserve()
        initKill()
        initTab()
        binding.apply {
            smartRl.setOnRefreshListener(this@ShopFragment)
            //搜索
            inHeader.imgSearch.setOnClickListener {
                JumpUtils.instans?.jump(108, SearchTypeConstant.SEARCH_SHOP.toString())
            }
            //购物车
            inHeader.imgBuyCar.setOnClickListener {
                JumpUtils.instans?.jump(119)
            }
            inHeader.imgMenu.setOnClickListener {
                showPublish(inHeader.imgMenu)
            }
            inTop.apply {
                recyclerViewRecommend.adapter = recommendAdapter
                tvAllList.setOnClickListener {
                    RecommendActivity.start()
                }
            }
            ivToTop.setOnClickListener {
                scrollTop()
            }
        }
        binding.inTop.banner.apply {
            setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    if ("商城页" == GioPageConstant.mainTabName) {
                        val mList = viewModel.advertisingList.value
                        if (!mList.isNullOrEmpty()) {
                            val item = mList[position]
                            mList[position].adName?.let { it1 ->
                                GIOUtils.homePageExposure(
                                    "广告位banner", (position + 1).toString(),
                                    it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                                )
                            }
                        }
                    }

                }

                override fun onPageScrollStateChanged(state: Int) {

                }

            })

        }
        LiveDataBus.get().withs<String>(LiveDataBusKey.FINISH_SHOP_REFRESH).observe(this) {
            binding.smartRl.finishRefresh()
        }
    }

    private fun scrollTop() {
        fragments[selectFragmentPosition].scrollToTop()
        binding.appbarLayout.setExpanded(true)
    }

    private fun scrollToTab(){
        binding.appbarLayout.setExpanded(false)
    }

    private fun initTab() {
//        WCommonUtil.setTabSelectStyle(
//            requireContext(),
//            binding.tabLayout,
//            16f,
//            Typeface.DEFAULT_BOLD,
//            R.color.color_33
//        )
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectFragmentPosition = position
                viewModel.classificationLiveData.value?.get(position)?.apply {
                    WBuriedUtil.clickShopType(tagName)
                }
                fragments[position].startRefresh()

                val oldTitle =
                    binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tv_tab)
                val oldIn =
                    binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tab_in)
                if (oldTitle != null) {
                    oldTitle.textSize = 16F
                    oldTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_9916
                        )
                    )
                    oldTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    oldIn?.isSelected = false
                }

                val title =
                    binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.tv_tab)
                val newIn =
                    binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.tab_in)
                if (title != null) {
                    title.textSize = 18F
                    title.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_01025C
                        )
                    )
                    //加粗
                    title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    newIn?.isSelected = true
                }

                oldPosition = position
            }
        })
    }

    private fun bindingTab(goodsClassification: ArrayList<GoodsTypesItemBean>?) {
        fragments.clear()
        binding.tabLayout.removeAllTabs()
        val tabs = goodsClassification ?: ArrayList<GoodsTypesItemBean>().apply {
            add(GoodsTypesItemBean("0", "全部"))
        }
        for (it in tabs) {
            val fragment = GoodsListFragment.newInstance(it.mallMallTagId, it.tagType)
//            fragment.setParentSmartRefreshLayout(binding.smartRl)
            fragments.add(fragment)
        }
        binding.viewpager.apply {
            adapter = ViewPage2AdapterFragment(this@ShopFragment, fragments)
            offscreenPageLimit = 5
            isSaveEnabled = false
            TabLayoutMediator(binding.tabLayout, this) { tab, tabPosition ->
                val itemHelpTabBinding = ItemShopTabBinding.inflate(layoutInflater)
                itemHelpTabBinding.tvTab.text = tabs[tabPosition].tagName
                //解决第一次进来item显示不完的bug
                itemHelpTabBinding.tabIn.isSelected = tabPosition == 0
                if (tabPosition == 0) {
                    itemHelpTabBinding.tvTab.textSize = 18F
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_01025C
                        )
                    )
                    //加粗
                    itemHelpTabBinding.tvTab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    itemHelpTabBinding.tvTab.textSize = 16F
                    itemHelpTabBinding.tvTab.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_9916
                        )
                    )
                }
                tab.customView = itemHelpTabBinding.root
            }.attach()
        }
        setCurrentItem()
    }

    private fun initKill() {
        binding.inTop.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                WBuriedUtil.clickShopKill(spuName, seckillFb)
                GoodsDetailsActivity.start(getJdType(), getJdValue())
                GIOUtils.homePageClick("限时秒杀", (position + 1).toString(), spuName)
            }

//            if("ON_GOING"==mAdapter.data[position].seckillStatus)GoodsDetailsActivity.start(mAdapter.data[position].mallMallSpuId)
        }
        binding.inTop.tvShopMoreKill.setOnClickListener {
            GoodsKillAreaActivity.start(requireContext())
            GIOUtils.homePageClick("限时秒杀", 0.toString(), "更多")
        }
    }

    override fun initData() {
        getData(true)
    }

    private fun showPublish(publishLocationView: View) {
        CircleMainMenuPop(
            requireContext(),
            object : CircleMainMenuPop.CheckPostType {
                override fun checkLongBar() {

                }

                override fun checkPic() {

                }

                override fun checkVideo() {

                }

                override fun checkQuestion() {

                }

            }).run {
            setBackgroundColor(Color.TRANSPARENT)
            showPopupWindow(publishLocationView)
            initShopData()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFB()
    }

    private fun getData(showLoading: Boolean = false, isOnRefresh: Boolean = false) {
        viewModel.getActData()
        viewModel.getShopKingKongData()
        viewModel.getBannerData()
        if (!isOnRefresh) {
            viewModel.getShopHomeData(showLoading)
        } else {
            LiveDataBus.get().with(LiveDataBusKey.STAR_SHOP_REFRESH).postValue("")
        }
        viewModel.getShopConfig()
//        viewModel.getClassification()
    }

    private fun addObserve() {
        viewModel.actData.observe(this) {
            binding.ivActs.isVisible = !it.isNullOrEmpty()
            binding.ivDeleteAct.isVisible = !it.isNullOrEmpty()
            if (!it.isNullOrEmpty()) {
                val item = it[0]
                GlideUtils.loadCircle(item.adImg,binding.ivActs)
//                binding.ivActs.loadImage(item.adImg)
                binding.ivActs.setOnClickListener {
                    JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
                }
                binding.ivDeleteAct.setOnClickListener {
                    binding.ivActs.isVisible = false
                    binding.ivDeleteAct.isVisible = false
                }
            }
        }
        viewModel.shopConfigBean.observe(this) {
            binding.inTop.apply {
                val killIsVisible = it.seckill_area_off
                val rankIsVisible = it.rank_off

                tvShopMoreKill.isVisible = killIsVisible == true
                tvKillTitle.isVisible = killIsVisible == true
                recyclerView.isVisible = killIsVisible == true

                tvAllList.isVisible = rankIsVisible == true
                tvRecommendList.isVisible = rankIsVisible == true
                recyclerViewRecommend.isVisible = rankIsVisible == true
            }
        }
        viewModel.advertisingList.observe(this) {
            BannerControl.bindingBanner(
                binding.inTop.banner,
                it,
                ScreenUtils.dp2px(requireContext(), 2.5f), true
            )
            ScreenUtils.setMargin(
                binding.inTop.tvKillTitle,
                0,
                if (null != it && it.size > 0) dp38 else 0,
                9,
                0
            )
            if (!it.isNullOrEmpty()) {
                val item = it[0]
                it[0].adName?.let { it1 ->
                    GIOUtils.homePageExposure(
                        "广告位banner", 1.toString(),
                        it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                    )
                }
            }
        }
        viewModel.shopKingKongData.observe(this) {
            binding.inTop.layoutKg.root.isVisible = !it.isNullOrEmpty()
            ItemCommonPics.setItemShopPics(binding.inTop.layoutKg, it)
        }
        viewModel.fbData.observe(this) {
            //我的福币
//            binding.inTop.compose.setContent {
//                HomeMyIntegralCompose(it.totalIntegral)
//            }
            val isLogin = !TextUtils.isEmpty(MConstant.token)
            binding.inTop.groupMyFb.isVisible = isLogin
            if (isLogin) {
                binding.inTop.tvRightTips.text = getString(R.string.str_earnMoney)
                binding.inTop.tvTips.text = "福币在手 随心兑换"
                binding.inTop.tvTips.setDrawableNull()
            } else {
                binding.inTop.tvRightTips.text = getString(R.string.str_loginToView)
                binding.inTop.tvTips.text = "我的福币"
                binding.inTop.tvTips.setDrawableLeft(R.mipmap.ic_shop_fb)
            }

            binding.inTop.tvFbNum.text = if (isLogin) "${it.totalIntegral}" else "0"
            binding.inTop.tvRightTips.setOnClickListener {
                if (isLogin) {
                    WBuriedUtil.clickShopIntegral()
                    JumpUtils.instans?.jump(16)
                    GIOUtils.homePageClick("我的福币", 0.toString(), "我的福币")
                } else JumpUtils.instans?.jump(100)
            }
        }
        viewModel.shopHomeData.observe(this) {
            bindCarNum(it.shoppingCartCount ?: 0)
            mAdapter.setList(it.indexSeckillDtoList)
            binding.inTop.apply {
//                val visibility = if (mAdapter.data.size > 0) View.VISIBLE else View.GONE
                if (mAdapter.data.size <= 0) {
                    tvShopMoreKill.visibility = View.GONE
                    tvKillTitle.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }

//                //我的福币
//                compose.setContent {
//                    HomeMyIntegralCompose(it.totalIntegral)
//                }
                //推荐
                if (it.mallSpuKindDtos != null && it.mallSpuKindDtos?.size!! > 0) {
//                    tvAllList.visibility = View.VISIBLE
//                    tvRecommendList.visibility = View.VISIBLE
//                    recyclerViewRecommend.visibility = View.VISIBLE
                    recommendAdapter.setList(it.mallSpuKindDtos)
                } else {
                    tvAllList.visibility = View.GONE
                    tvRecommendList.visibility = View.GONE
                    recyclerViewRecommend.visibility = View.GONE
                }
            }
            bindingTab(it.mallTags)
            binding.smartRl.finishRefresh()
        }
        viewModel.classificationLiveData.observe(this) {
            bindingTab(it)
            binding.smartRl.finishRefresh()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        getData(isOnRefresh = true)
//        val currentItem=binding.viewpager.currentItem
//        fragments[currentItem].startRefresh()
    }

    private fun bindCarNum(num: Int) {
        binding.inHeader.tvCarNumber.apply {
            visibility = if (num > 0) {
                text = "$num"
                View.VISIBLE
            } else View.GONE
        }
    }

    private fun addLiveDataBus() {
//        //下单回调
//        LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK).observe(this) {
//            getData()
//        }
        //购物车数量改变
        LiveDataBus.get().with(LiveDataBusKey.SHOP_DELETE_CAR, Int::class.java).observe(this) {
            bindCarNum(it)
        }
        //登录、退出登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS, UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        getData()
                    }

                    else -> {}
                }
            }
    }

    /**
     * [tagName]标签名称  注意：这里需要URL解码
     * */
    fun setCurrentItem(tagName: String? = defaultTagName) {
        if (TextUtils.isEmpty(tagName)) return
        defaultTagName = URLDecoder.decode(tagName, "UTF-8")
        viewModel.shopHomeData.value?.mallTags?.let {
            val index = it.indexOfFirst { item -> item.tagName == tagName }
            if (index > -1) binding.viewpager.currentItem = index
            defaultTagName = null
            scrollToTab()
        }
    }
}

