package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.adapter.goods.GoodsKillAreaAdapter
import com.changanford.shop.adapter.goods.GoodsKillAreaTimeAdapter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ActGoodsKillAreaBinding

/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : 秒杀专区
 */
class GoodsKillAreaActivity: BaseActivity<ActGoodsKillAreaBinding, GoodsViewModel>() {
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, GoodsKillAreaActivity::class.java))
        }
    }
    private val timeAdapter by lazy { GoodsKillAreaTimeAdapter(0) }
    private val mAdapter by lazy { GoodsKillAreaAdapter() }

    override fun initView() {
        binding.rvTime.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvTime.adapter=timeAdapter
        binding.rvList.adapter=mAdapter

    }
    override fun initData() {
        val datas= arrayListOf<GoodsBean>()
        for(i in 0..15){
            val item=GoodsBean(i,"Title$i")
            datas.add(item)
        }
        mAdapter.setList(datas)
        timeAdapter.setList(datas)
    }
    fun onBack(v:View)=this.finish()
}