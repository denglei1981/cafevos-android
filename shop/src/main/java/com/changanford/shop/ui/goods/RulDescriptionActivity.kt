package com.changanford.shop.ui.goods

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.shop.databinding.ActRuleDescriptionBinding
import com.changanford.shop.viewmodel.GoodsViewModel

/**
 * @Author : wenke
 * @Time : 2022/4/14
 * @Description : 规则说明
 */
@Route(path = ARouterShopPath.RulDescriptionActivity)
class RulDescriptionActivity:BaseActivity<ActRuleDescriptionBinding,GoodsViewModel>() {
    companion object{
        fun start() {
            RouterManger.startARouter(ARouterShopPath.RulDescriptionActivity)
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
    }
    override fun initData() {
        viewModel.otherInfoBeanLiveData.observe(this){
            it?.content?.apply {
                CustomWebHelper(this@RulDescriptionActivity, binding.webView,false).loadDataWithBaseURL(this)
            }
        }
        viewModel.agreementHub("mall_seckill_rule")
    }
}