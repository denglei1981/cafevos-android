package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsImgsAdapter
import com.changanford.shop.control.GoodsDetailsControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.gson.Gson
import kotlin.math.roundToInt

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 商品详情
 */
@Route(path = ARouterShopPath.ShopGoodsActivity)
class GoodsDetailsActivity:BaseActivity<ActivityGoodsDetailsBinding, GoodsViewModel>(){
    //spuPageType 商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    companion object{
        fun start(spuId:String) {
            JumpUtils.instans?.jump(3,spuId)
        }
        fun start(context: Context,isRefresh:Boolean) {
            context.startActivity(Intent(context,GoodsDetailsActivity::class.java).putExtra("isRefresh",isRefresh))
        }
    }
    private var spuId:String="0"//商品IDR.id.img_share->control.share()
    private lateinit var control: GoodsDetailsControl
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderGoodsDetailsBinding>(LayoutInflater.from(this), R.layout.header_goods_details, null, false) }
    private val mAdapter by lazy { GoodsImgsAdapter() }
    private val tabLayout by lazy { binding.inHeader.tabLayout }
    private val tabTitles by lazy {arrayOf(getString(R.string.str_goods), getString(R.string.str_eval),getString(R.string.str_details))}
    private var topBarH =0
    private var commentH=300f
    private var detailsH =0f
    private var oldScrollY=0
    private val topBarBg by lazy { binding.inHeader.layoutHeader.background }
    private var isClickSelect=false//是否点击选中tab
    private var isCollection=false //是否收藏
    private fun initH(){
        topBarH= binding.inHeader.layoutHeader.height+ScreenUtils.dp2px(this,30f)
        commentH=headerBinding.viewComment.y-topBarH
        detailsH=headerBinding.tvGoodsDetailsTitle.y-topBarH-30
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus&&0==topBarH) initH()
    }
    override fun initView() {
        addLiveDataBus()
        binding.inEmpty.imgBack.setOnClickListener { this.finish() }
        spuId=intent.getStringExtra("spuId")?:"0"
        if("0"==spuId){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        binding.rvGoodsImg.adapter=mAdapter
        mAdapter.addHeaderView(headerBinding.root)
        binding.rvGoodsImg.addOnScrollListener(onScrollListener)
        initTab()
        control= GoodsDetailsControl(this,binding,headerBinding,viewModel)
        WCommonUtil.setTextViewStyles(headerBinding.inVip.tvVipExclusive,"#FFE7B2","#E0AF60")
        viewModel.queryGoodsDetails(spuId,true)
        //去掉行高
//        headerBinding.tvDetails.lineHeight=1
//        headerBinding.tvDetails.setLineSpacing(0f,1f)
    }
    private fun initTab(){
        for(it in tabTitles)tabLayout.addTab(tabLayout.newTab().setText(it))
        WCommonUtil.setTabSelectStyle(this,tabLayout,15f, Typeface.DEFAULT_BOLD,R.color.color_00095B)
        tabClick()
    }
    override fun initData() {
        viewModel.goodsDetailData.observe(this,{
            binding.inEmpty.layoutEmpty.visibility=View.GONE
            control.bindingData(it)
            initH()
            viewModel.collectionGoodsStates.postValue(it.collect=="YES")
        })
        viewModel.responseData.observe(this,{
            it.apply {
                if(!isSuccess){
                    binding.inEmpty.apply {
                        layoutEmpty.visibility=View.VISIBLE
                        tvEmptyContent.setText(msg)
                    }
                }
            }
        })
        viewModel.collectionGoodsStates.observe(this,{
            isCollection= it
            binding.inBottom.cbCollect.isChecked=isCollection
            binding.inHeader.imgCollection.setImageResource(
                when {
                    isCollection -> R.mipmap.shop_collect_1
                    oldScrollY<commentH -> R.mipmap.shop_collect_0
                    else -> R.mipmap.shop_collect_00
                }
            )
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply {
            val isRefresh=getBooleanExtra("isRefresh",false)
            if("0"!=spuId&&isRefresh)viewModel.queryGoodsDetails(spuId,false)
        }
    }
    fun onClick(v:View){
        val vid=v.id
        if(MConstant.token.isEmpty()&&R.id.img_back!=vid&&R.id.img_share!=vid){
            JumpUtils.instans?.jump(100)
            return
        }
        if(R.id.img_back!=vid&&(!::control.isInitialized||viewModel.goodsDetailData.value==null))return
        when(vid){
            //确认订单
            R.id.btn_submit->{
                control.skuCode.apply {
                    if(control.isInvalidSelectAttrs(this))control.createAttribute()
                    else OrderConfirmActivity.start(Gson().toJson(viewModel.goodsDetailData.value))
                }
            }
            //查看评价
            R.id.tv_goodsCommentLookAll->GoodsEvaluateActivity.start(this,spuId)
            //选择商品属性
            R.id.tv_goodsAttrs ->control.createAttribute()
            //收藏商品
            R.id.view_collect,R.id.img_collection->viewModel.collectGoods(spuId,isCollection)
            //分享商品
            R.id.img_share->control.share()
            //返回
            R.id.img_back->this.finish()
        }
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
                    binding.rvGoodsImg.smoothScrollBy(0, scrollY)
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
                    topBarBg.alpha=0
                    tabLayout.alpha=0f
                }
            }
            if(newState==RecyclerView.SCROLL_STATE_IDLE&&oldScrollY <= 100){
                topBarBg.alpha=0
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
                topBarBg.alpha=alpha
                val tabAlpha=oldScrollY / commentH * 1.0f
                tabLayout.alpha=tabAlpha

//                val alpha0 = 255-alpha
                val alpha0 = 255
                binding.inHeader.imgBack.background.alpha=alpha0
                binding.inHeader.imgShare.background.alpha=alpha0
                binding.inHeader.imgCollection.background.alpha=alpha0

                binding.inHeader.imgBack.setImageResource(R.mipmap.shop_back)
                binding.inHeader.imgShare.setImageResource(R.mipmap.shop_share)
                binding.inHeader.imgCollection.setImageResource(if(isCollection)R.mipmap.shop_collect_1 else R.mipmap.shop_collect_0)
            }else{
                topBarBg.alpha=255
                tabLayout.alpha=1f
                if(!isClickSelect&&oldScrollY>=detailsH&&selectedTabPosition!=2){
                    tabLayout.getTabAt(2)?.select()
                }else if(!isClickSelect&&oldScrollY<detailsH&&selectedTabPosition!=1){
                    tabLayout.getTabAt(1)?.select()
                }
                binding.inHeader.imgBack.background.alpha=0
                binding.inHeader.imgBack.setImageResource(R.mipmap.shop_back_black)

                binding.inHeader.imgShare.background.alpha=0
                binding.inHeader.imgShare.setImageResource(R.mipmap.shop_share_black)

                binding.inHeader.imgCollection.background.alpha=0
                binding.inHeader.imgCollection.setImageResource(if(isCollection)R.mipmap.shop_collect_1 else R.mipmap.shop_collect_00)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::control.isInitialized)control.onDestroy()
    }
    private fun addLiveDataBus(){
        //下单回调
        LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK).observe(this,  {
            if("2"!=it&&"0"!=spuId)viewModel.queryGoodsDetails(spuId,false)
        })
        //分享回调
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this,  {
            if (it==0&&::control.isInitialized) control.shareBack()
        })
        //登录回调
        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this,{
                if(UserManger.UserLoginStatus.USER_LOGIN_SUCCESS==it) {
                     if("0"!=spuId)viewModel.queryGoodsDetails(spuId,false)
                }
            })

    }
}