package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActGoodsDetailsBinding

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : 商品详情
 */
class GoodsDetailsActivity:BaseActivity<ActGoodsDetailsBinding,GoodsViewModel>(){
    companion object{
        fun start(context: Context,goodsId:String) {
            context.startActivity(Intent(context,GoodsDetailsActivity::class.java).putExtra("goodsId",goodsId))
        }
    }
    override fun initView() {

    }

    override fun initData() {

    }
}