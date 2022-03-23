package com.changanford.shop
import android.graphics.Typeface
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.bean.ShopRecommendBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.manger.UserManger
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.adapter.goods.GoodsKillAdapter
import com.changanford.shop.adapter.goods.ShopRecommendListAdapter1
import com.changanford.shop.control.BannerControl
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.compose.HomeMyIntegralCompose
import com.changanford.shop.ui.goods.ExchangeListFragment
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.goods.GoodsKillAreaActivity
import com.changanford.shop.ui.goods.RecommendActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopLayoutBinding, GoodsViewModel>(), OnRefreshListener {
    private  val fragments= arrayListOf<ExchangeListFragment>()
    private val mAdapter by lazy { GoodsKillAdapter() }
    private val dp38 by lazy { ScreenUtils.dp2px(requireContext(),38f) }
    private val recommendAdapter by lazy { ShopRecommendListAdapter1() }
    override fun initView() {
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { _: AppBarLayout?, i: Int ->
            binding.smartRl.isEnabled = i >= 0
        } as AppBarLayout.BaseOnOffsetChangedListener<*>)
        addLiveDataBus()
        addObserve()
        initKill()
        initTab()
        binding.apply {
            inHeader.imgSearch.setOnClickListener {JumpUtils.instans?.jump(108, SearchTypeConstant.SEARCH_SHOP.toString())  }
            smartRl.setOnRefreshListener(this@ShopFragment)
            inTop.apply {
                recyclerViewRecommend.adapter=recommendAdapter
                tvAllList.setOnClickListener {
                    RecommendActivity.start()
                }
            }
        }
    }
    private fun initTab(){
        WCommonUtil.setTabSelectStyle(requireContext(),binding.tabLayout,18f, Typeface.DEFAULT_BOLD,R.color.color_01025C)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.classificationLiveData.value?.get(position)?.apply {
                    WBuriedUtil.clickShopType(tagName)
                }
                fragments[position].startRefresh()
            }
        })
    }
    private fun bindingTab(goodsClassification:ArrayList<GoodsTypesItemBean>?){
        fragments.clear()
        binding.tabLayout.removeAllTabs()
        val tabs=goodsClassification?:ArrayList<GoodsTypesItemBean>().apply {
            add(GoodsTypesItemBean("0","全部"))
        }
        for(it in tabs){
            val fragment= ExchangeListFragment.newInstance(it.mallMallTagId,it.tagType)
            fragment.setParentSmartRefreshLayout(binding.smartRl)
            fragments.add(fragment)
        }
        val adapter=ViewPage2Adapter(requireActivity(),fragments)
        binding.viewpager.adapter= adapter
        binding.viewpager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabs[tabPosition].tagName
        }.attach()
    }
    private fun initKill(){
        binding.inTop.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].apply {
                WBuriedUtil.clickShopKill(spuName,seckillFb)
                GoodsDetailsActivity.start(getJdType(),getJdValue())
            }

//            if("ON_GOING"==mAdapter.data[position].seckillStatus)GoodsDetailsActivity.start(mAdapter.data[position].mallMallSpuId)
        }
        binding.inTop.tvShopMoreKill.setOnClickListener { GoodsKillAreaActivity.start(requireContext()) }
    }
    override fun initData() {
        viewModel.getBannerData()
        viewModel.getShopHomeData()
//        viewModel.getClassification()
    }
    private fun addObserve(){
        viewModel.advertisingList.observe(this) {
            BannerControl.bindingBanner(binding.inTop.banner,it,ScreenUtils.dp2px(requireContext(), 2.5f))
            ScreenUtils.setMargin(binding.inTop.tvKillTitle,0,if (null != it && it.size > 0) dp38 else 0,9,0)
        }
        viewModel.shopHomeData.observe(this) {
            mAdapter.setList(it.indexSeckillDtoList)
            binding.inTop.apply {
                val visibility = if (mAdapter.data.size > 0) View.VISIBLE else View.GONE
                tvShopMoreKill.visibility = visibility
                tvKillTitle.visibility = visibility
                //我的福币
                compose.setContent {
                    HomeMyIntegralCompose(it.totalIntegral)
                }
                //推荐
                recommendAdapter.setList(it.mallSpuKindDtos)
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
        initData()
//        val currentItem=binding.viewpager.currentItem
//        fragments[currentItem].startRefresh()
    }
    private fun addLiveDataBus(){
        //登录回调
        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                if (UserManger.UserLoginStatus.USER_LOGIN_SUCCESS == it) {
                    initData()
                }
            }
    }
}

