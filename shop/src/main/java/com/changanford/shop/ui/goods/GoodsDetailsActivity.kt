package com.changanford.shop.ui.goods

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.R
import com.changanford.shop.databinding.ActGoodsDetailsBinding
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
    private val tabLayout by lazy { binding.tabLayout }
    private val tabBg by lazy {tabLayout.background}
    private val tabH by lazy { tabLayout.y+tabLayout.height }
    private val commentH by lazy { binding.inTop.tvComment.y-tabH}
    private val detailsH by lazy { binding.inTop.recycler.y-tabH }
    private val tabTitles by lazy {arrayOf(getString(R.string.str_goods), getString(R.string.str_comment),getString(R.string.str_details))}
    private var oldScrollY=0
    override fun initView() {
        initTab()
    }
    override fun initData() {
    }
    @SuppressLint("ClickableViewAccessibility")
    private  fun initTab(){
        binding.nScrollView.setOnScrollChangeListener(onScrollChangeListener)
        tabLayout.background.alpha=0
        tabLayout.alpha=0f
        for(it in tabTitles)tabLayout.addTab(tabLayout.newTab().setText(it))
        tabClick()
    }
    private fun tabClick(){
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i) ?: return
            //这里使用到反射，拿到Tab对象后获取Class
            val c: Class<*> = tab.javaClass
            try {
                //获取tab的view属性  name可能会不一样 可以进源码看看
                val field = c.getDeclaredField("view")
                //反射的对象在使用时取消Java语言访问检查
                field.isAccessible = true
                //获取view
                val view: View = field.get(tab) as View
                view.tag = i
                view.setOnClickListener {
                    when(i){
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val onScrollChangeListener= NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
        this.oldScrollY=oldScrollY
        val selectedTabPosition=tabLayout.selectedTabPosition
        if(oldScrollY<commentH&&selectedTabPosition!=0)tabLayout.getTabAt(0)?.select()
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
                    tabLayout.getTabAt(2)?.select()
                }else if(oldScrollY<detailsH&&selectedTabPosition!=1){
                    tabLayout.getTabAt(1)?.select()
                }
            }
        }
    }

}