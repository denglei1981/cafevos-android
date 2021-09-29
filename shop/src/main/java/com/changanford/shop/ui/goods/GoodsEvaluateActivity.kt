package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.adapter.goods.GoodsEvalutaeAdapter
import com.changanford.shop.databinding.ActGoodsEvaluateBinding
import com.changanford.shop.viewmodel.GoodsViewModel

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : 商品评价
 */
@Route(path = ARouterShopPath.GoodsEvaluateActivity)
class GoodsEvaluateActivity:BaseActivity<ActGoodsEvaluateBinding, GoodsViewModel>(){
    companion object{
        fun start(context: Context, goodsId:String) {
            context.startActivity(Intent(context,GoodsEvaluateActivity::class.java).putExtra("goodsId",goodsId))
        }
    }
    private val mAdapter by lazy { GoodsEvalutaeAdapter() }
    override fun initView() {
        binding.topBar.setActivity(this)
        binding.recyclerView.adapter=mAdapter
    }
    override fun initData() {
        mAdapter.setList(arrayListOf("","","","","","","",""))
    }
}