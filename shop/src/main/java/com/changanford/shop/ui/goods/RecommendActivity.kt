package com.changanford.shop.ui.goods

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.databinding.ActRecommendBinding
import com.changanford.shop.viewmodel.GoodsViewModel

/**
 * @Author : wenke
 * @Time : 2022/3/18
 * @Description : 商品推荐
 */
@Route(path = ARouterShopPath.RecommendActivity)
class RecommendActivity:BaseActivity<ActRecommendBinding,GoodsViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }
}