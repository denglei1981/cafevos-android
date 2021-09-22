package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.view.View
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.adapter.goods.GoodsEvalutaeAdapter
import com.changanford.shop.databinding.ActGoodsEvaluateBinding

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : 商品评价
 */
class GoodsEvaluateActivity:BaseActivity<ActGoodsEvaluateBinding,GoodsViewModel>(){
    companion object{
        fun start(context: Context, goodsId:String) {
            context.startActivity(Intent(context,GoodsEvaluateActivity::class.java).putExtra("goodsId",goodsId))
        }
    }
    private val mAdapter by lazy { GoodsEvalutaeAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
    }
    override fun initData() {
        mAdapter.setList(arrayListOf("","","","","","","",""))
    }
    fun onBack(v: View)=this.finish()
}