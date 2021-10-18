package com.changanford.shop
import android.graphics.Typeface
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.adapter.goods.GoodsKillAdapter
import com.changanford.shop.control.BannerControl
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.exchange.ExchangeListFragment
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.goods.GoodsKillAreaActivity
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
    override fun initView() {
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { _: AppBarLayout?, i: Int ->
            binding.smartRl.isEnabled = i >= 0
        } as AppBarLayout.BaseOnOffsetChangedListener<*>)
        addObserve()
        initKill()
        binding.inTop.btnToTask.setOnClickListener { JumpUtils.instans?.jump(16) }
        binding.smartRl.setOnRefreshListener(this)
    }
    private fun bindingTab(goodsClassification:ArrayList<GoodsTypesItemBean>?){
        if(fragments.size>0)return
        val tabs=goodsClassification?:ArrayList<GoodsTypesItemBean>().apply {
            add(GoodsTypesItemBean("0","全部"))
        }
//        val tabs=ArrayList<GoodsTypesItemBean>().apply {
//            add(GoodsTypesItemBean("0","全部"))
//            if(homeData!=null)addAll(homeData.list)
//        }
        for(it in tabs){
            val fragment=ExchangeListFragment.newInstance(it.mallMallTagId)
            fragment.setParentSmartRefreshLayout(binding.smartRl)
            fragments.add(fragment)
        }
        binding.viewpager.adapter= ViewPage2Adapter(requireActivity(),fragments)
        binding.viewpager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabs[tabPosition].tagName
        }.attach()
        WCommonUtil.setTabSelectStyle(requireContext(),binding.tabLayout,18f, Typeface.DEFAULT,R.color.color_01025C)
    }
    private fun initKill(){
        binding.inTop.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            GoodsDetailsActivity.start(mAdapter.data[position].mallMallSpuId,"SECKILL")
        }
        binding.inTop.tvShopMoreKill.setOnClickListener { GoodsKillAreaActivity.start(requireContext()) }
    }
    override fun initData() {
        viewModel.getBannerData()
        viewModel.getShopHomeData()
        viewModel.getClassification()
    }
    private fun addObserve(){
        viewModel.advertisingList.observe(this,{
            BannerControl.bindingBanner(binding.inTop.banner,it,ScreenUtils.dp2px(requireContext(),5f))
        })
        viewModel.shopHomeData.observe(this,{
            mAdapter.setList(it.indexSeckillDtoList)
        })
        viewModel.classificationLiveData.observe(this,{
            bindingTab(it)
        })
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getBannerData()
        viewModel.getShopHomeData()
        val currentItem=binding.viewpager.currentItem
        fragments[currentItem].startRefresh()
    }
}

