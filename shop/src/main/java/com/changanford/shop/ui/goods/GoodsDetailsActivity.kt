package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsImgsAdapter
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import kotlin.math.roundToInt

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 商品详情
 */
class GoodsDetailsActivity:BaseActivity<ActivityGoodsDetailsBinding,GoodsViewModel>(){
    companion object{
        fun start(context: Context,goodsId:String) {
            context.startActivity(Intent(context,GoodsDetailsActivity::class.java).putExtra("goodsId",goodsId))
        }
    }
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderGoodsDetailsBinding>(LayoutInflater.from(this), R.layout.header_goods_details, binding.recyclerView, false)}
    private val mAdapter by lazy { GoodsImgsAdapter() }
    private val tabLayout by lazy { binding.inHeader.tabLayout }
    private val tabTitles by lazy {arrayOf(getString(R.string.str_goods), getString(R.string.str_comment),getString(R.string.str_details))}
    private var topBarH =0
    private var commentH=0f
    private var detailsH =0f
    private var oldScrollY=0
    private lateinit var topBarBg: Drawable
    private var isClickSelect=false//是否点击选中tab
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus&&0==topBarH)initH()
    }
    private fun initH(){
        topBarBg=binding.inHeader.layoutHeader.background
        topBarH= binding.inHeader.layoutHeader.height
        commentH=headerBinding.inComment.layoutComment.y-topBarH
        detailsH=headerBinding.tvGoodsDetailsTitle.y-topBarH
        tabLayout.alpha=0f
        topBarBg.alpha=0
    }
    override fun initView() {
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        mAdapter.addHeaderView(headerBinding.root)
        binding.recyclerView.adapter=mAdapter
        binding.recyclerView.addOnScrollListener(onScrollListener)
        initTab()
    }
    private  fun initTab(){
        for(it in tabTitles)tabLayout.addTab(tabLayout.newTab().setText(it))
        tabClick()
    }
    override fun initData() {
        val imgs= arrayListOf("","","","","","","","","","","","","","","","")
        mAdapter.setList(imgs)
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
                    val scrollY=when(i){
                        1->(commentH-oldScrollY).toInt()
                        2->(detailsH-oldScrollY).toInt()
                        else ->0-oldScrollY
                    }
                    isClickSelect=true
                    binding.recyclerView.smoothScrollBy(0, scrollY)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val onScrollListener=object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState==RecyclerView.SCROLL_STATE_IDLE){
                isClickSelect=false
                if(oldScrollY <= 100){
                    binding.inHeader.layoutHeader.background.alpha=0
                    tabLayout.alpha=0f
                }
            }
            if(newState==RecyclerView.SCROLL_STATE_IDLE&&oldScrollY <= 100){
                binding.inHeader.layoutHeader.background.alpha=0
                tabLayout.alpha=0f
            }
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            oldScrollY+=dy
            val selectedTabPosition=tabLayout.selectedTabPosition
            if(oldScrollY<commentH){
                if(!isClickSelect&&selectedTabPosition!=0)tabLayout.getTabAt(0)?.select()
                val alpha = (oldScrollY / commentH * 255).roundToInt()
                binding.inHeader.layoutHeader.background.alpha=alpha
                val tabAlpha=oldScrollY / commentH * 1.0f
                tabLayout.alpha=tabAlpha
            }else{
                binding.inHeader.layoutHeader.background.alpha=255
                tabLayout.alpha=1f
                if(!isClickSelect&&oldScrollY>=detailsH&&selectedTabPosition!=2){
                    tabLayout.getTabAt(2)?.select()
                }else if(!isClickSelect&&oldScrollY<detailsH&&selectedTabPosition!=1){
                    tabLayout.getTabAt(1)?.select()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recyclerView.removeOnScrollListener(onScrollListener)
    }
}