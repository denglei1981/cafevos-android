package com.changanford.shop.ui.goods

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.SeckillTimeRange
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsKillAreaAdapter
import com.changanford.shop.adapter.goods.GoodsKillAreaTimeAdapter
import com.changanford.shop.adapter.goods.GoodsKillDateAdapter
import com.changanford.shop.databinding.ActGoodsKillAreaBinding
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.GoodsViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import java.text.SimpleDateFormat

/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : 秒杀专区
 */
@SuppressLint("SimpleDateFormat")
@Route(path = ARouterShopPath.GoodsKillAreaActivity)
class GoodsKillAreaActivity: BaseActivity<ActGoodsKillAreaBinding, GoodsViewModel>(),
    GoodsKillDateAdapter.SelectBackListener, GoodsKillAreaTimeAdapter.SelectTimeBackListener,
    OnRefreshLoadMoreListener, TopBar.OnRightClickListener {
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, GoodsKillAreaActivity::class.java))
        }
    }
    private val statesTxtArr by lazy { arrayOf(getString(R.string.str_hasEnded),getString(R.string.str_ongoing),getString(R.string.str_notStart)) }
    private var nowTime=System.currentTimeMillis()//当前时间挫
    private val dateAdapter by lazy { GoodsKillDateAdapter(0,this) }
    private val timeAdapter by lazy { GoodsKillAreaTimeAdapter(0,this) }
    private val mAdapter by lazy { GoodsKillAreaAdapter(viewModel) }
    private var pageNo=1
    private val sf = SimpleDateFormat("HH:mm")
    private val sfDate = SimpleDateFormat("yyyyMMdd")
    private val totalTime:Long=30*60*1000
    private val countDownInterval:Long=60*1000//更新当前时间的间隔时间
    private val timeCountDownTimer=object : CountDownTimer(totalTime,countDownInterval){
        override fun onTick(millisUntilFinished: Long) {
            nowTime+=countDownInterval
        }
        override fun onFinish() {}
    }.start()
    override fun initView() {
        binding.rvDate.adapter=dateAdapter
        binding.rvTime.adapter=timeAdapter
        binding.rvList.adapter=mAdapter
        binding.topBar.setActivity(this)
        binding.topBar.setOnRightClickListener(this)
        binding.smartRl.setOnRefreshLoadMoreListener(this)
        mAdapter.setEmptyView(R.layout.view_empty)
        addObserve()
        mAdapter.setOnItemClickListener { _, _, position ->
            GoodsDetailsActivity.start(mAdapter.data[position].spuId)
        }
    }
    override fun initData() {
        viewModel.getSckills()
    }
    private fun addObserve(){
        viewModel.seckillSessionsData.observe(this,{item->
            item.now?.let {now-> nowTime= now }
            item.seckillSessions?.apply {
                dateAdapter.setList(this)
                val nowTimeSf=sfDate.format(nowTime).toInt()
                //将时间转换为 yyyyMMdd 格式便于筛选出当天
                for((i,it)in this.withIndex()){
                    it.apply {
                        dateFormat=sfDate.format(date).toInt()
                        index=i
                    }
                }
                //根据条件将日期分成两份
                val (match, rest)=this.partition {it.dateFormat>=nowTimeSf}
                //优先选中当天,其次是当天后的最近一天,最后默认选第一天（今天21号 有[19,21,23]选取21、[18,23]选取23、[18,19]选取18）
                val dateI=if(match.isNotEmpty())match[0].index else if(rest.isNotEmpty())rest[0].index else 0
                onSelectBackListener(dateI,this[dateI].seckillTimeRanges)
                dateAdapter.selectPos=dateI
                binding.rvDate.scrollToPosition(dateI)
            }
        })
        viewModel.killGoodsListData.observe(this,{
            val dataList=it?.dataList
            if(1==pageNo)mAdapter.setList(dataList)
            else if(dataList!=null)mAdapter.addData(dataList)
            binding.smartRl.finishLoadMore()
            binding.smartRl.finishRefresh()
        })
    }
    /**
     * 秒杀时间段数据格式化
    * */
    private fun calculateStates(seckillTimeRange:ArrayList<SeckillTimeRange>){
//        nowTime=System.currentTimeMillis()//当前时间挫
        var timeI=-1
        for((i,it) in seckillTimeRange.withIndex()){
            it.apply {
                states= when {
                    nowTime<timeBegin -> 2  //当前时间小于开始时间则表示未开始
                    nowTime>=timeEnd -> 0 //当前时间大于等于结束时间表示已结束
                    else ->{//进行中
                        if(-1==timeI)timeI=i//同时多个进行中的状态下，默认选中第一个
                        1
                    }
                }
                statesTxt=statesTxtArr[states]
                time=sf.format(timeBegin)
                index=i
            }
        }
        //未找到正在进行中的场次
        if(timeI==-1){
            val (match, rest)=seckillTimeRange.partition { it.timeBegin>=nowTime}
            timeI=if(match.isNotEmpty())match[0].index else if(rest.isNotEmpty()) rest[0].index else 0
        }
        timeAdapter.selectPos=0
        timeAdapter.setList(seckillTimeRange)
        onSelectTimeBackListener(timeI,seckillTimeRange[timeI])
        timeAdapter.selectPos=timeI
        binding.rvTime.scrollToPosition(timeI)
    }
    /**
     * 秒杀日期选择回调
     * [position]下标
     * */
    override fun onSelectBackListener(position: Int, seckillTimeRanges: ArrayList<SeckillTimeRange>) {
        calculateStates(seckillTimeRanges)
    }

    /**
     * 秒杀时间段回调
     * */
    override fun onSelectTimeBackListener(position: Int, seckillTimeRanges: SeckillTimeRange) {
        pageNo=1
        viewModel.getGoodsKillList(seckillTimeRanges.timeRangeId,pageNo)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        if(dateAdapter.data.size>0)onSelectBackListener(dateAdapter.selectPos, dateAdapter.data[dateAdapter.selectPos].seckillTimeRanges)
        else binding.smartRl.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        if(timeAdapter.data.size>0)viewModel.getGoodsKillList(timeAdapter.data[timeAdapter.selectPos].timeRangeId,pageNo)
        else binding.smartRl.finishLoadMore()
    }

    override fun onRightClick() {
        JumpUtils.instans?.jump(108)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeCountDownTimer.cancel()
    }
}