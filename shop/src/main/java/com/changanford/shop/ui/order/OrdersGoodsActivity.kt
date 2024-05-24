package com.changanford.shop.ui.order

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ItemShopTabBinding
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.wutil.ViewPage2AdapterAct
import com.changanford.shop.R
import com.changanford.shop.databinding.ActGoodsOrderBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author : wenke
 * @Time : 2021/9/24
 * @Description : 商品订单
 */
@Route(path = ARouterShopPath.OrderGoodsActivity)
class OrdersGoodsActivity : BaseActivity<ActGoodsOrderBinding, OrderViewModel>() {

    companion object {
        fun start(states: Int = 0) {
            JumpUtils.instans?.jump(52, "$states")
        }
    }

    private var oldPosition = 0
    private var lastSearchContent = ""

    override fun initView() {
        binding.topBar.setActivity(this)
        binding.tvToShop.setOnClickListener {
            JumpUtils.instans?.jump(104)
            finish()
        }
        binding.searchContent.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = binding.searchContent.text.toString()
                    if (content != lastSearchContent) {
                        LiveDataBus.get().with(LiveDataBusKey.SHOP_ORDER_SEARCH).postValue(content)
                    }
                    lastSearchContent = content
                    HideKeyboardUtil.hideKeyboard(binding.searchContent.windowToken)
                    return true;
                }
                return false;
            }
        })
        initTab()
        LiveDataBus.get().withs<String>(LiveDataBusKey.ORDERS_GOODS_SHOW_EMPTY).observe(this) {
            binding.clEmpty.isVisible = true
        }
    }

    override fun initData() {
        viewModel.getShopConfig()
        viewModel.shopConfigBean.observe(this) {
            it?.order_list_roll?.apply {
                //跑马灯
                binding.topContent.apply {
                    isVisible = !content.isNullOrEmpty()
                    text = content
                    isSelected = true
                    setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString(
                            "value",
                            "{\"title\": \"协议详情\",\"bizCode\": \"$protocol_code\"}"
                        )
                        startARouter(ARouterShopPath.RulDescriptionActivity, bundle)
                    }
                }
            }
        }
    }

    private fun initTab() {
        val states = intent.getIntExtra("states", 0)
        val tabTitles = arrayListOf(
            getString(R.string.str_all),
            getString(R.string.str_toBePaid),
            getString(R.string.str_toSendGoods),
            getString(R.string.str_forGoods),
            getString(R.string.str_toEvaluate),
            getString(R.string.str_refundOrAfterSale)
        )
        val fragments = arrayListOf<Fragment>()
        val tabSize = tabTitles.size
        for (i in 0 until tabSize) {
            if (i < tabSize - 1) fragments.add(OrdersGoodsFragment.newInstance(i - 1))
            else fragments.add(OrdersGoodsRefundFragment.newInstance(i - 1))
        }
        binding.viewPager2.apply {
            adapter = ViewPage2AdapterAct(this@OrdersGoodsActivity, fragments)
            offscreenPageLimit = 5
            isSaveEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val oldTitle =
                        binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tv_tab)
                    val oldIn =
                        binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tab_in)
                    if (oldTitle != null) {
                        oldTitle.textSize = 16F
                        oldTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_8016
                            )
                        )
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
                                context,
                                R.color.text_01025C
                            )
                        )
                        //加粗
                        newIn?.isSelected = true
                    }

                    oldPosition = position
                }
            })
            TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, tabPosition ->
                val itemHelpTabBinding = ItemShopTabBinding.inflate(layoutInflater)
                itemHelpTabBinding.tvTab.text = tabTitles[tabPosition]
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
                } else {
                    itemHelpTabBinding.tvTab.textSize = 16F
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_8016
                        )
                    )
                }
                tab.customView = itemHelpTabBinding.root
            }.attach()
        }

        if (states < tabTitles.size) binding.tabLayout.getTabAt(states)?.select()
    }
}