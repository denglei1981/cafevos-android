package com.changanford.shop
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.JumpUtils
import com.changanford.shop.adapter.ViewPage2Adapter
import com.changanford.shop.adapter.goods.GoodsKillAdapter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.FragmentShopLayoutBinding
import com.changanford.shop.ui.IntegralDetailsActivity
import com.changanford.shop.ui.exchange.ExchangeListFragment
import com.changanford.shop.ui.goods.GoodsKillAreaActivity
import com.changanford.shop.ui.order.AllOrderActivity
import com.changanford.shop.ui.order.OrderEvaluationActivity
import com.changanford.shop.ui.order.OrdersGoodsActivity
import com.changanford.shop.utils.WCommonUtil
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator


/**
 * A fragment representing a list of Items.
 */
class ShopFragment : BaseFragment<FragmentShopLayoutBinding, ShopViewModel>() {
    private val adapter by lazy { GoodsKillAdapter() }
    override fun initView() {
        //tab吸顶的时候禁止掉 SmartRefreshLayout或者有滑动冲突
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { _: AppBarLayout?, i: Int ->
            binding.smartRl.isEnabled = i >= 0
        } as AppBarLayout.BaseOnOffsetChangedListener<*>)
        initTab()
        initKill()
        //test
        binding.inTop.btnJfmx.setOnClickListener { IntegralDetailsActivity.start(requireContext()) }
        binding.inTop.btnPj.setOnClickListener { OrderEvaluationActivity.start(requireContext(),"0") }
        binding.inTop.btnOrdersGoods.setOnClickListener { OrdersGoodsActivity.start(requireContext()) }
        binding.inTop.btnAllOrder.setOnClickListener { AllOrderActivity.start(requireContext(),0) }
    }
    private fun initTab(){
        val tabTitles= arrayListOf<String>()
        val fragments= arrayListOf<Fragment>()
        for(i in 0..20){
            tabTitles.add("Tab$i")
            fragments.add(ExchangeListFragment.newInstance("$i"))
        }
        binding.viewpager.adapter= ViewPage2Adapter(requireActivity(),fragments)
        binding.viewpager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
            tab.text = tabTitles[tabPosition]
        }.attach()
        WCommonUtil.setTabSelectStyle(requireContext(),binding.tabLayout,18f, Typeface.DEFAULT,R.color.color_01025C)
    }
    private fun initKill(){
        binding.inTop.recyclerView.adapter=adapter
        adapter.setOnItemClickListener { _, _, position ->
            JumpUtils.instans?.jump(3,"$position")
        }
        binding.inTop.tvShopMoreKill.setOnClickListener { GoodsKillAreaActivity.start(requireContext()) }
    }
    override fun initData() {
        val datas= arrayListOf<GoodsBean>()
        val title=StringBuffer("Title")
        for (i in 0..4){
            title.append("Title$i>>")
            val item= GoodsBean(i,if(i%3>0)"Title$i" else "$title")
            datas.add(item)
        }
        adapter.setList(datas)
    }
}

