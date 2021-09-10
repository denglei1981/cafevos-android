package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.R
import com.changanford.shop.databinding.ActGoodsDetailsBinding
import com.google.android.material.tabs.TabLayout
import kotlin.math.roundToInt

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 商品详情
 */
class GoodsDetailsActivity:BaseActivity<ActGoodsDetailsBinding,GoodsViewModel>(){
    companion object{
        fun start(context: Context,goodsId:String) {
            context.startActivity(Intent(context,GoodsDetailsActivity::class.java).putExtra("goodsId",goodsId))
        }
    }
    override fun initView() {
        initTab()
    }
    override fun initData() {
    }
    private val tabH by lazy { binding.tabLayout.y+binding.tabLayout.height }
    private val commentH by lazy { binding.inTop.tvComment.y-tabH}
    private val detailsH by lazy { binding.inTop.recycler.y-tabH }
    private val tabLayout by lazy { binding.tabLayout }
    private val tabTitles by lazy {arrayOf(getString(R.string.str_goods), getString(R.string.str_comment),getString(R.string.str_details))}
    private var oldScrollY=0
    private var isAutoSelect=false//是否自动选中
    private  fun initTab(){
        with(binding) { nScrollView.setOnScrollChangeListener(onScrollChangeListener) }
        tabLayout.background.alpha=0
        tabLayout.alpha=0f
        for(it in tabTitles){
            tabLayout.addTab(tabLayout.newTab().setText(it))
        }
        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(isAutoSelect)return
                when(tab.position){
                    0->binding.nScrollView.fullScroll(ScrollView.FOCUS_UP)
                    1->{
                        val scrollY=(commentH-oldScrollY).toInt()
                        binding.nScrollView.smoothScrollBy(0, scrollY)
                    }
                    2->{
                        val scrollY=(detailsH-oldScrollY).toInt()
                        binding.nScrollView.smoothScrollBy(0, scrollY)
                    }
                }
                isAutoSelect=false
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
    private val onScrollChangeListener= NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
        this.oldScrollY=oldScrollY
        val tabBg=tabLayout.background
        val selectedTabPosition=tabLayout.selectedTabPosition
        if(oldScrollY<commentH&&selectedTabPosition!=0){
            isAutoSelect=true
            tabLayout.getTabAt(0)?.select()
        }
        when {
            oldScrollY <= 100 -> {
                tabBg.alpha=0
                tabLayout.alpha=0f
            }
            oldScrollY < commentH -> {
                val alpha = (oldScrollY / commentH * 255).roundToInt()
                tabBg.alpha=alpha
                val tabAlpha=oldScrollY / commentH * 1.0f
                tabLayout.alpha=tabAlpha
            }
            oldScrollY >= commentH ->{
                tabBg.alpha=255
                tabLayout.alpha=1f
                if(oldScrollY>=detailsH&&selectedTabPosition!=2){
                    isAutoSelect=true
                    tabLayout.getTabAt(2)?.select()
                }else if(oldScrollY<detailsH&&selectedTabPosition!=1){
                    isAutoSelect=true
                    tabLayout.getTabAt(1)?.select()
                }
            }
        }
    }
}