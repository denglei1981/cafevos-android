package com.changanford.shop.ui.shoppingcart

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.utilext.toast
import com.changanford.shop.databinding.ActivityShoppingCartBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.ui.shoppingcart.adapter.ShoppingCartAdapter
import com.changanford.shop.ui.shoppingcart.adapter.ShoppingCartInvaildAdapter
import com.changanford.shop.ui.shoppingcart.request.ShoppingCartViewModel
import com.changanford.shop.utils.WCommonUtil
import java.math.BigDecimal

@Route(path = ARouterShopPath.ShoppingCartActivity)
@SuppressLint("SetTextI18n")
class ShoppingCartActivity : BaseActivity<ActivityShoppingCartBinding, ShoppingCartViewModel>() {

    val shoppingCartAdapter: ShoppingCartAdapter by lazy {
        ShoppingCartAdapter(this,object : ShoppingCartAdapter.ShopBackListener {
            override fun check() {
                setTitle()
            }
        })

    }
    val shoppingCartInvaildAdapter: ShoppingCartInvaildAdapter by lazy {
        ShoppingCartInvaildAdapter(object : ShoppingCartInvaildAdapter.ShopBackListener {
            override fun check() {

            }
        })

    }
    private var shoppingEdit: Boolean = false  // 购物车编辑状态

    override fun initView() {
        binding.layoutTop.tvTitle.text = "购物车"
        binding.rvShopping.adapter = shoppingCartAdapter
        binding.layoutTop.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutTop.tvRight.text = "编辑"
        binding.layoutTop.tvRight.visibility = View.VISIBLE
        binding.layoutTop.tvRight.setOnClickListener {
            val menuTxt = binding.layoutTop.tvRight.text
            when (menuTxt) {
                "编辑" -> {
                    shoppingEdit = true
                    binding.layoutTop.tvRight.text = "完成"
                    if (shoppingCartAdapter.shopList.size > 0) {
                        binding.tvOver.text = "删除(${shoppingCartAdapter.shopList.size})"
                        binding.tvOver.isSelected = true
                    } else {
                        binding.tvOver.text = "删除"
                        binding.tvOver.isSelected = false
                    }
                    binding.tvBalance.visibility = View.GONE

                }
                "完成" -> {
                    shoppingEdit = false
                    binding.layoutTop.tvRight.text = "编辑"
                    if (shoppingCartAdapter.shopList.size > 0) {
                        binding.tvOver.text = "结算(${shoppingCartAdapter.shopList.size})"
                    } else {
                        binding.tvOver.text = "结算"
                    }
                    binding.tvOver.isSelected = true
                    binding.tvBalance.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun initData() {
        viewModel.getShoppingCartList()
        binding.checkStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            allCheck(isCheck = isChecked)
        }


        binding.tvOver.setOnClickListener {
            // 跳转到结算
            val menuTxt = binding.tvOver.text
            if (menuTxt.contains("结算")) {
                if (shoppingCartAdapter.shopList.size > 0) {
                    shoppingCartAdapter.shopList.forEach {
                        it.carBeanToOrderBean()
                    }
                    OrderConfirmActivity.start(shoppingCartAdapter.shopList as ArrayList<GoodsDetailBean>)
                }
            }
            if (menuTxt.contains("删除")) { // 删除购物车商品
                if (shoppingCartAdapter.shopList.size > 0) {
                    // 选中的商品
                    val mallUserSkuIds: ArrayList<String> = arrayListOf()
                    shoppingCartAdapter.shopList.forEach {
                        mallUserSkuIds.add(it.mallMallUserSkuId.toString())
                    }
                    viewModel.deleteCartShopping(mallUserSkuIds)
                }
            }
        }
        binding.rvInvaild.adapter = shoppingCartInvaildAdapter

        binding.tvClear.setOnClickListener {
            // 清空失效商品
            val mallUserSkuIds: ArrayList<String> = arrayListOf()
            shoppingCartInvaildAdapter.data.forEach {
                mallUserSkuIds.add(it.mallMallUserSkuId.toString())
            }
            viewModel.deleteCartShopping(mallUserSkuIds = mallUserSkuIds)
        }
        shoppingCartInvaildAdapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) { // 一条一条删除
                val mallUserSkuIds: ArrayList<String> = arrayListOf()
                mallUserSkuIds.add(shoppingCartInvaildAdapter.getItem(position = position).mallMallUserSkuId.toString())
                viewModel.deleteCartShopping(mallUserSkuIds = mallUserSkuIds)
            }
        })

    }

    override fun observe() {
        super.observe()
        viewModel.goodsListLiveData.observe(this, Observer {
            it.forEach {
                shoppingCartAdapter.checkMap[it.mallMallUserSkuId] = false
            }
            if (it.size > 0) {
                setTvTitle(it.size.toString())
                setTitle()
            }
            shoppingCartAdapter.setList(it)
        })
        viewModel.goodsInvaildListLiveData.observe(this, Observer {
            if (it.size > 0) {
                binding.flInvaild.visibility = View.VISIBLE
                shoppingCartInvaildAdapter.setList(it)
            } else {
                binding.flInvaild.visibility = View.GONE
            }

        })
        viewModel.deleteShoppingCar.observe(this, Observer {
            // 删除成功
            shoppingCartAdapter.shopList.clear()
            if (shoppingCartAdapter.shopList.size > 0) {
                binding.tvOver.text = "删除(${shoppingCartAdapter.shopList.size})"
            } else {
                binding.tvOver.text = "删除"
            }
        })
    }


    fun setTvTitle(count: String) {
        binding.layoutTop.tvTitle.text = "购物车(${count})"
    }



    fun setTitle() {
        if (shoppingCartAdapter.shopList.size > 0) {
            if (shoppingEdit) {
                binding.tvOver.text = "删除(${shoppingCartAdapter.shopList.size})"
                binding.tvOver.isSelected = true
                binding.tvBalance.visibility = View.GONE
            } else {
                // 加入所有的商品
                binding.tvOver.text = "结算(${shoppingCartAdapter.shopList.size})"
                binding.tvOver.isSelected = false
                var totalFbPrice: BigDecimal= BigDecimal(0)
                shoppingCartAdapter.shopList.forEach {
                    val bb=BigDecimal(it.fbPer)
                    val buyNum=it.num
                    buyNum?.let {
                        val thisPrice =bb.multiply(BigDecimal(it))
                        totalFbPrice= totalFbPrice.add(thisPrice)
                    }


                }
                binding.tvBalance.visibility = View.VISIBLE
                binding.tvBalance.text =
                    "合计 ￥(${WCommonUtil.getRMBBigDecimal(totalFbPrice.toString())})"
            }

        } else {
            if (shoppingEdit) {
                binding.tvOver.text = "删除"
                binding.tvOver.isSelected = false
                binding.tvBalance.text = ""
            } else {
                binding.tvOver.text = "结算"
                binding.tvOver.isSelected = false
                binding.tvBalance.text = "合计 ￥0"
            }

        }
    }


    private fun allCheck(isCheck: Boolean) {
        shoppingCartAdapter.shopList.clear()
        shoppingCartAdapter.checkMap.forEach {
            shoppingCartAdapter.checkMap[it.key] = isCheck
        }
        shoppingCartAdapter.notifyDataSetChanged()
//        shoppingCartAdapter.data.size.toString().toast()
        if (isCheck) {
            // 加入所有的商品
            if (shoppingEdit) {
                shoppingCartAdapter.shopList.addAll(shoppingCartAdapter.data)
                binding.tvOver.text = "删除(${shoppingCartAdapter.shopList.size})"
                binding.tvOver.isSelected = true
            } else {
                shoppingCartAdapter.shopList.addAll(shoppingCartAdapter.data)
                binding.tvOver.text = "结算(${shoppingCartAdapter.shopList.size})"
                binding.tvOver.isSelected = true
            }

        } else {
            if (shoppingEdit) {
                binding.tvOver.text = "删除"
                binding.tvOver.isSelected = false
            } else {
                binding.tvOver.text = "结算"
                binding.tvOver.isSelected = false
            }
        }


    }
}