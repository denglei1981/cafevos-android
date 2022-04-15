package com.changanford.shop.ui.goods

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
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
        fun start(title:String?=null,bizCode:String?) {
            if(bizCode!=null){
                val bundle= Bundle()
                bundle.putString("value","{\"title\": \"$title\",\"bizCode\": \"$bizCode\"}")
                RouterManger.startARouter(ARouterShopPath.RulDescriptionActivity,bundle)
            }
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
    }
    override fun initData() {
        intent.getStringExtra("value")?.apply {
            val jsonObject = JSON.parseObject(this)
            jsonObject.getString("title")?.apply {
                binding.topBar.setTitle(this)
            }
            jsonObject.getString("bizCode")?.apply {
                viewModel.agreementHub(this)
            }
        }
        viewModel.otherInfoBeanLiveData.observe(this){
            it?.content?.apply {
                CustomWebHelper(this@RulDescriptionActivity, binding.webView,false).loadDataWithBaseURL(this)
            }
        }

    }
}