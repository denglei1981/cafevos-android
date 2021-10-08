package com.changanford.shop
import android.graphics.Typeface
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.GoodsTypesItemBean
import com.changanford.common.util.JumpUtils
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.adapter.goods.GoodsKillAdapter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.control.BannerControl
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.exchange.ExchangeListFragment
import com.changanford.shop.ui.goods.GoodsKillAreaActivity
import com.changanford.shop.ui.order.AllOrderActivity
import com.changanford.shop.ui.order.OrderEvaluationActivity
import com.changanford.shop.ui.order.OrdersGoodsActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.ShopViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopLayoutBinding, ShopViewModel>(), OnRefreshListener {
    private  val fragments= arrayListOf<ExchangeListFragment>()
    private val adapter by lazy { GoodsKillAdapter() }
    override fun initView() {
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { _: AppBarLayout?, i: Int ->
            binding.smartRl.isEnabled = i >= 0
        } as AppBarLayout.BaseOnOffsetChangedListener<*>)
        addObserve()
        initKill()
        binding.inTop.btnToTask.setOnClickListener { JumpUtils.instans?.jump(16) }
        binding.smartRl.setOnRefreshListener(this)
        //test
        binding.inTop.btnPj.setOnClickListener { OrderEvaluationActivity.start(requireContext(),"0") }
        binding.inTop.btnOrdersGoods.setOnClickListener { OrdersGoodsActivity.start(requireContext()) }
        binding.inTop.btnAllOrder.setOnClickListener { AllOrderActivity.start(requireContext(),0) }
    }
    private fun bindingTab(tabsData:MutableList<GoodsTypesItemBean>){
        for(it in tabsData){
            val fragment=ExchangeListFragment.newInstance(it.typeId)
            fragment.setParentSmartRefreshLayout(binding.smartRl)
            fragments.add(fragment)
        }
        binding.viewpager.adapter= ViewPage2Adapter(requireActivity(),fragments)
        binding.viewpager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabsData[tabPosition].typeName
        }.attach()
        WCommonUtil.setTabSelectStyle(requireContext(),binding.tabLayout,18f, Typeface.DEFAULT,R.color.color_01025C)
    }
    private fun initKill(){
        binding.inTop.recyclerView.adapter=adapter
        adapter.setOnItemClickListener { _, _, position ->
            JumpUtils.instans?.jump(3,"$position")
        }
        binding.inTop.tvShopMoreKill.setOnClickListener { GoodsKillAreaActivity.start(requireContext()) }
        //添加测试数据
        val datas= arrayListOf<GoodsBean>()
        val title=StringBuffer("Title")
        for (i in 0..4){
            title.append("Title$i>>")
            val item= GoodsBean(i,if(i%3>0)"Title$i" else "$title")
            datas.add(item)
        }
        adapter.setList(datas)
    }
    override fun initData() {
        viewModel.getBannerData()
        viewModel.getShopHomeData()
        viewModel.getGoodsTypeList()
    }
    private fun addObserve(){
        viewModel.advertisingList.observe(this,{
            BannerControl.bindingBanner(binding.inTop.banner,it,ScreenUtils.dp2px(requireContext(),5f))
        })
        viewModel.goodsClassificationData.observe(this,{
            bindingTab(it)
        })
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        val currentItem=binding.viewpager.currentItem
        fragments[currentItem].startRefresh()
    }
}

