package com.changanford.shop.ui.shoppingcart

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.utilext.toast
import com.changanford.shop.databinding.ActivityShoppingCartBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.ui.shoppingcart.adapter.ShoppingCartAdapter
import com.changanford.shop.ui.shoppingcart.request.ShoppingCartViewModel
import com.changanford.shop.utils.WCommonUtil

@Route(path = ARouterShopPath.ShoppingCartActivity)
@SuppressLint("SetTextI18n")
class ShoppingCartActivity : BaseActivity<ActivityShoppingCartBinding, ShoppingCartViewModel>() {

    val shoppingCartAdapter: ShoppingCartAdapter by lazy {
        ShoppingCartAdapter(object : ShoppingCartAdapter.ShopBackListener {
            override fun check() {
                setTitle()
            }

        })

    }

    override fun initView() {
        binding.layoutTop.tvTitle.text = "购物车"
        binding.rvShopping.adapter = shoppingCartAdapter
        binding.layoutTop.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutTop.tvRight.text = "编辑"
        binding.layoutTop.tvRight.visibility = View.VISIBLE
    }

    override fun initData() {
        viewModel.getShoppingCartList()
        binding.checkStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            allCheck(isCheck = isChecked)
        }


        binding.tvOver.setOnClickListener {
             // 跳转到结算
            if(shoppingCartAdapter.shopList.size>0){
                shoppingCartAdapter.shopList.forEach {
                    it.carBeanToOrderBean()
                }
                OrderConfirmActivity.start(shoppingCartAdapter.shopList as ArrayList<GoodsDetailBean>)
            }
        }

    }

    override fun observe() {
        super.observe()
        viewModel.goodsList.observe(this, Observer {
            it.forEach {
                shoppingCartAdapter.checkMap[it.mallMallUserSkuId] = false
            }
            if (it.size > 0) {
                setTvTitle(it.size.toString())
                setTitle()
            }
            shoppingCartAdapter.setList(it)

        })
    }


    fun setTvTitle(count: String) {
        binding.layoutTop.tvTitle.text = "购物车(${count})"
    }

    fun setTitle() {
        if (shoppingCartAdapter.shopList.size > 0) {
            // 加入所有的商品
            binding.tvOver.text = "结算(${shoppingCartAdapter.shopList.size})"
            binding.tvOver.isSelected = true

            var totalFbPrice: Long = 0
            shoppingCartAdapter.shopList.forEach {
                totalFbPrice += it.fbPer?.toLong() ?: 0

            }
            binding.tvBalance.text =
                "合计 ￥(${WCommonUtil.getRMBBigDecimal(totalFbPrice.toString())})"
        } else {
            binding.tvOver.text = "结算"
            binding.tvOver.isSelected = false
            binding.tvBalance.text = "合计 ￥0"
        }
    }


    private fun allCheck(isCheck: Boolean) {
        shoppingCartAdapter.shopList.clear()
        shoppingCartAdapter.checkMap.forEach {
            shoppingCartAdapter.checkMap[it.key] = isCheck
        }
        shoppingCartAdapter.notifyDataSetChanged()
        shoppingCartAdapter.data.size.toString().toast()
        if (isCheck) {
            // 加入所有的商品
            shoppingCartAdapter.shopList.addAll(shoppingCartAdapter.data)
            binding.tvOver.text = "结算(${shoppingCartAdapter.shopList.size})"
            binding.tvOver.isSelected = true
        } else {
            binding.tvOver.text = "结算"
            binding.tvOver.isSelected = false
        }


    }
}