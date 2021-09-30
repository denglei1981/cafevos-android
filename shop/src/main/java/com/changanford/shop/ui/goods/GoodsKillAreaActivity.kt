package com.changanford.shop.ui.goods

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.SeckillTimeRange
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsKillAreaAdapter
import com.changanford.shop.adapter.goods.GoodsKillAreaTimeAdapter
import com.changanford.shop.adapter.goods.GoodsKillDateAdapter
import com.changanford.shop.bean.GoodsBean
import com.changanford.shop.databinding.ActGoodsKillAreaBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import java.text.SimpleDateFormat

/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : 秒杀专区
 */
@Route(path = ARouterShopPath.GoodsKillAreaActivity)
class GoodsKillAreaActivity: BaseActivity<ActGoodsKillAreaBinding, GoodsViewModel>(),
    GoodsKillDateAdapter.selectBackListener {
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, GoodsKillAreaActivity::class.java))
        }
    }
    private val statesTxt by lazy { arrayOf(getString(R.string.str_hasEnded),getString(R.string.str_ongoing),getString(R.string.str_notStart)) }
    private var nowTime=System.currentTimeMillis()//当前时间挫
    private val dateAdapter by lazy { GoodsKillDateAdapter(0,this) }
    private val timeAdapter by lazy { GoodsKillAreaTimeAdapter(0) }
    private val mAdapter by lazy { GoodsKillAreaAdapter() }
    @SuppressLint("SimpleDateFormat")
    private val sf = SimpleDateFormat("HH:mm")
    override fun initView() {
        binding.rvDate.adapter=dateAdapter
        binding.rvTime.adapter=timeAdapter
        binding.rvList.adapter=mAdapter
        binding.topBar.setActivity(this)
        addObserve()
    }
    override fun initData() {
        viewModel.getSckills()
        val datas= arrayListOf<GoodsBean>()
        for(i in 0..15){
            val item=GoodsBean(i,"Title$i")
            if(i<5)item.states=i
            datas.add(item)
        }
        mAdapter.setList(datas)
    }
    private fun addObserve(){
        viewModel.seckillSessionsData.observe(this,{
            if(it.now!=null)nowTime= it.now!!
            dateAdapter.setList(it.seckillSessions)
            onSelectBackListener(0,it.seckillSessions[0].seckillTimeRanges)
        })
    }
    /**
     * 秒杀时间段数据格式化
    * */
    private fun calculateStates(seckillTimeRange:ArrayList<SeckillTimeRange>){
        for(it in seckillTimeRange){
            it.states= when {
                nowTime<it.timeBegin -> 2  //当前时间小于开始时间则表示未开始
                nowTime>it.timeEnd -> 0 //当前时间大于结束时间表示已结束
                else -> 1//进行中
            }
            it.statesTxt=statesTxt[it.states]
            it.time=sf.format(it.timeBegin)
        }
        timeAdapter.setList(seckillTimeRange)
    }
    /**
     * 秒杀日期选择回调
     * [position]下标
     * */

    override fun onSelectBackListener(position: Int, seckillTimeRanges: ArrayList<SeckillTimeRange>) {
        calculateStates(seckillTimeRanges)
    }
}