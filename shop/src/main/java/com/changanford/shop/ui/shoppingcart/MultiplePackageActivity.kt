package com.changanford.shop.ui.shoppingcart

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.databinding.ActivityMultiplePackageBinding
import com.changanford.shop.databinding.ActivityShoppingCartBinding
import com.changanford.shop.databinding.BaseRecyclerViewBinding
import com.changanford.shop.ui.shoppingcart.adapter.MultiplePackageAdapter
import com.changanford.shop.ui.shoppingcart.request.MultiplePackageViewModel
import com.changanford.shop.ui.shoppingcart.request.ShoppingCartViewModel
import com.google.gson.Gson

/**
 *  多包裹发货
 * */
@Route(path = ARouterShopPath.MultiplePackageActivity)
class MultiplePackageActivity : BaseActivity<ActivityMultiplePackageBinding, MultiplePackageViewModel>() {

    val multiplePackageAdapter: MultiplePackageAdapter by lazy {
        MultiplePackageAdapter()
    }
    companion object{
        fun start(orderNo: String) {
            JumpUtils.instans?.jump(128,orderNo)
        }
    }


    override fun initView() {
        binding.recyclerView.adapter =multiplePackageAdapter
    }

    override fun initData() {
        val orderNo=intent.getStringExtra("value")
        if (orderNo != null) {
            viewModel.getMultiplePackInfo(orderNo)
        }

    }
}