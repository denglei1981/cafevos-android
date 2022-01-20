package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.compose.rememberImagePainter
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.CarServiceAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.control.AnimationControl
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil
import java.util.*


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val animationControl by lazy { AnimationControl() }
    private val carTopBanner by lazy {NewCarTopBannerAdapter()}
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderCarBinding>(LayoutInflater.from(requireContext()), R.layout.header_car, null, false) }
    private var oldScrollY=0
    private val maxSlideY=500//最大滚动距离
    private val serviceAdapter by lazy { CarServiceAdapter() }
    @SuppressLint("NewApi")
    override fun initView() {
        binding.apply {
            srl.setOnRefreshListener {
                initData()
                it.finishRefresh()
            }
            recyclerView.adapter=mAdapter
            recyclerView.addOnScrollListener(onScrollListener)
            mAdapter.addHeaderView(headerBinding.root)
            headerBinding.apply {
                rvCarService.adapter=serviceAdapter
                bindingService()
                bindingCompose()
            }
        }
        initBanner()
    }
    override fun initData() {
        viewModel.getTopBanner()
        viewModel.topBannerBean.observe(this,{
            it?.apply {
                if (size == 0) {
                    headerBinding.carTopViewPager.isVisible = false
                    return@observe
                }
                headerBinding.carTopViewPager.isVisible = true
                topBannerList.clear()
                topBannerList.addAll(this)
                headerBinding.carTopViewPager.create(topBannerList)
            }
        })
    }
    private fun initBanner(){
        headerBinding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            setIndicatorView(headerBinding.drIndicator)
            setOnPageClickListener { _, position ->
            if (!FastClickUtils.isFastClick()) {
                    JumpUtils.instans?.jump(topBannerList[position].mainJumpType, topBannerList[position].mainJumpVal)
                }
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.e("wenke","onPageSelected>>position:$position")
//                    topBannerList[position].apply {
//                        headerBinding.imgTop.load(topImg)
//                        headerBinding.imgBottom.load(bottomImg)
//                        animationControl.startAnimation(headerBinding.imgTop,topAni)
//                        animationControl.startAnimation(headerBinding.imgBottom,topAni)
//                    }
                }
            })
            setIndicatorView(headerBinding.drIndicator)
        }
        headerBinding.drIndicator.setIndicatorGap(20).setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)
        headerBinding.carTopViewPager.isSaveEnabled = false
    }
    /**
     * 服务模块
    * */
    private fun bindingService(){
        val dataList = arrayListOf<NewCarTagBean>()
        for (i in 0..3){
            dataList.add(NewCarTagBean(tagName = "Tag$i"))
        }
        serviceAdapter.setList(dataList)
    }
    private fun bindingCompose(){
        val dataList = arrayListOf<NewCarTagBean>()
        for (i in 0..10){
            dataList.add(NewCarTagBean(tagName = "Tag$i"))
        }
        headerBinding.composeView.setContent {
            AfterSalesService(dataList)
        }
    }
    /**
     * 售后服务
    * */
    @Composable
    private fun AfterSalesService(dataList:MutableList<NewCarTagBean>?){
        if(dataList==null||dataList.size==0)return
        //一排几列
        val columnSize=3
        //总共几排
        val rowTotal= WCommonUtil.getHeatNumUP("${dataList.size/columnSize.toFloat()}",0).toInt()
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)) {
            Text(text = "启程 体验售后服务",color = colorResource(R.color.color_33),fontSize = 17.sp)
            Spacer(modifier = Modifier.height(18.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(5.dp))) {
                Spacer(modifier = Modifier.height(25.dp))
                for (row in 0 until rowTotal){
                    val startIndex=row*columnSize
                    val endIndex=if(row!=rowTotal-1)(row+1)*columnSize else dataList.size
                    val itemList=dataList.slice(startIndex until endIndex)
                    val itemListSize=itemList.size
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (i in 0 until columnSize){
                            Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp)) {
                                ItemService(if(itemListSize>i)itemList[i] else null)
                            }
                            if(i<2) Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
    /**
     * 售后服务item
    * */
    @Composable
    private fun ItemService(itemData:NewCarTagBean?){
        itemData?.apply {
            Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(pic) ?: R.mipmap.head_default,
                builder = {placeholder(R.mipmap.head_default)}),
                contentDescription =null,modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = tagName?:"",fontSize = 12.sp,color = colorResource(R.color.color_33),overflow = TextOverflow.Ellipsis,maxLines = 1)
        }
    }
    /**
     * RecyclerView 滚动监听 主要用于控制banner是否自动播放
    * */
    private val onScrollListener=object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== RecyclerView.SCROLL_STATE_IDLE){
                headerBinding.carTopViewPager.apply {
                    if(oldScrollY<maxSlideY){
                        startLoop()
                    }else{
                        stopLoop()
                    }
                }
            }
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            oldScrollY+=dy
        }
    }
    override fun onResume() {
        super.onResume()
        initData()
        if(oldScrollY<maxSlideY){
            headerBinding.carTopViewPager.startLoop()
        }
    }
    override fun onPause() {
        super.onPause()
        headerBinding.carTopViewPager.stopLoop()
    }
}