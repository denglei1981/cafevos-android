package com.changanford.shop.ui.goods

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GioPreBean
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateGoodsDetails
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsImgsAdapter
import com.changanford.shop.control.GoodsDetailsControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 商品详情
 */
@Route(path = ARouterShopPath.ShopGoodsActivity)
class GoodsDetailsActivity : BaseActivity<ActivityGoodsDetailsBinding, GoodsViewModel>() {
    //spuPageType 商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    companion object {
        fun start(spuId: String?) {
            if (null != spuId) {
                JumpUtils.instans?.jump(3, spuId)
            }
        }

        fun start(jumpDataType: Int, jumpDataValue: String?) {
            JumpUtils.instans?.jump(jumpDataType, jumpDataValue)
        }

        fun start(context: Context, isRefresh: Boolean) {
            context.startActivity(
                Intent(
                    context,
                    GoodsDetailsActivity::class.java
                ).putExtra("isRefresh", isRefresh)
            )
        }
    }

    private var spuId: String = "0"//商品IDR.id.img_share->control.share()
    private lateinit var control: GoodsDetailsControl
    private val headerBinding by lazy {
        DataBindingUtil.inflate<HeaderGoodsDetailsBinding>(
            LayoutInflater.from(this),
            R.layout.header_goods_details,
            null,
            false
        )
    }
    private val mAdapter by lazy { GoodsImgsAdapter() }
    private val tabLayout by lazy { binding.inHeader.tabLayout }
    private val tabTitles by lazy {
        arrayOf(
            getString(R.string.str_goods),
            getString(R.string.str_eval),
            getString(R.string.str_details),
            getString(R.string.str_walk)
        )
    }
    private var topBarH = 0
    private var commentH = 300f
    private var detailsH = 0f
    private var walkH = 500f
    private var oldScrollY = 0
    private val topBarBg by lazy { binding.inHeader.layoutHeader.background }
    private var isClickSelect = false//是否点击选中tab
    private var isCollection = false //是否收藏
    private fun initH() {
        topBarH = binding.inHeader.layoutHeader.height + ScreenUtils.dp2px(this, 30f)
        commentH = headerBinding.viewComment.y - topBarH + 60
        detailsH = headerBinding.tvGoodsDetailsTitle.y - topBarH
        walkH = headerBinding.composeView.y - topBarH + 60
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && 0 == topBarH) initH()
    }

    override fun initView() {
        addLiveDataBus()
        updateMainGio("商品详情页", "商品详情页")
        binding.inEmpty.imgBack.setOnClickListener { this.finish() }
        spuId = intent.getStringExtra("spuId") ?: "0"
        if ("0" == spuId) {
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal), this)
            this.finish()
            return
        }
        binding.inEmpty.layoutEmpty.visibility = View.GONE
        binding.rvGoodsImg.adapter = mAdapter
        mAdapter.addHeaderView(headerBinding.root)
        binding.rvGoodsImg.addOnScrollListener(onScrollListener)
        control = GoodsDetailsControl(this, binding, headerBinding, viewModel)
        WCommonUtil.setTextViewStyles(headerBinding.inVip.tvVipExclusive, "#FFE7B2", "#E0AF60")
        initTab()
        viewModel.queryGoodsDetails(spuId, true)
    }

    private fun initTab() {
        tabLayout.removeAllTabs()
        for (it in tabTitles) tabLayout.addTab(tabLayout.newTab().setText(it))
        tabClick()
        LiveDataBus.get().withs<GioPreBean>(LiveDataBusKey.UPDATE_GOODS_DETAILS_GIO).observe(this) {
            gioPreBean = it
        }
    }

    private val goodsDetailsBean = MutableLiveData<GoodsDetailBean>()
    private var gioPreBean = GioPreBean()
    override fun onResume() {
        super.onResume()
        goodsDetailsBean.observe(this) {
            val isSeckill = if (it.killStates == 5) "是" else "否"
            GIOUtils.productDetailPageView(
                it.spuId,
                it.skuId,
                it.spuName,
                it.rmbPrice,
                it.fbPrice,
                isSeckill,
                gioPreBean.prePageName,
                gioPreBean.prePageType
            )
        }
    }

    override fun initData() {
        viewModel.goodsDetailData.observe(this) {
            goodsDetailsBean.value = it
            binding.inEmpty.layoutEmpty.visibility = View.GONE
            control.bindingData(it)
            viewModel.collectionGoodsStates.postValue(it.collect == "YES")
            GlobalScope.launch {
                delay(1000L)
                initH()
            }
        }
        viewModel.responseData.observe(this) {
            it.apply {
                if (!isSuccess) {
                    binding.inEmpty.apply {
                        layoutEmpty.visibility = View.VISIBLE
//                        tvEmptyContent.setText(msg)
                    }
                }
            }
        }
        viewModel.collectionGoodsStates.observe(this) {
            isCollection = it
            binding.inBottom.cbCollect.isChecked = isCollection
            binding.inHeader.imgCollection.setImageResource(
                when {
                    isCollection -> R.mipmap.shop_collect_1
                    oldScrollY < commentH -> R.mipmap.shop_collect_0
                    else -> R.mipmap.shop_collect_00
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra("spuId")?.apply {
            spuId = this
        }
        if (!TextUtils.isEmpty(spuId) && spuId != "0") viewModel.queryGoodsDetails(spuId, false)
    }

    fun onClick(v: View) {
        val vid = v.id
        if (MConstant.token.isEmpty() && R.id.tv_goodsAttrs != vid && R.id.img_back != vid && R.id.tv_goodsCommentLookAll != vid) {
            JumpUtils.instans?.jump(100)
            return
        }
        if (R.id.img_back != vid && (!::control.isInitialized || viewModel.goodsDetailData.value == null)) return
        when (vid) {
            //确认订单
            R.id.btn_buy -> control.submitOrder()
            //加入购物车
            R.id.btn_cart -> control.addShoppingCart(0)
            //查看评价
            R.id.tv_goodsCommentLookAll -> {
                updateGoodsDetails(headerBinding.inGoodsInfo.tvGoodsTitle.text.toString(), "商品评价页")
                GoodsEvaluateActivity.start(spuId)
            }
            //选择商品属性
            R.id.tv_goodsAttrs -> control.createAttribute()
            //收藏商品
            R.id.view_collect, R.id.img_collection -> {
                if (isCollection) viewModel.cancelCollectGoods(spuId)
                else viewModel.collectGoods(spuId)
            }
            //分享商品
            R.id.img_share -> control.share()
            //客服
            R.id.tv_customerService -> {
                updateGoodsDetails("意见反馈页", "意见反馈页")
                JumpUtils.instans?.jump(11)
            }

            //购物车
            R.id.tv_cart -> {
                updateGoodsDetails("购物车页", "购物车页")
                JumpUtils.instans?.jump(119)
            }
            //返回
            R.id.img_back -> this.finish()
        }
    }

    private fun tabClick() {
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
                    val scrollY = when (i) {
                        1 -> (commentH - oldScrollY).toInt()
                        2 -> (detailsH - oldScrollY).toInt()
                        3 -> (walkH - oldScrollY).toInt()
                        else -> 0 - oldScrollY
                    }
                    isClickSelect = true
                    binding.rvGoodsImg.smoothScrollBy(0, scrollY)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isClickSelect = false
                if (oldScrollY <= 100) {
                    topBarBg.alpha = 0
                    tabLayout.alpha = 0f
                }
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && oldScrollY <= 100) {
                topBarBg.alpha = 0
                tabLayout.alpha = 0f
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            oldScrollY += dy
            val selectedTabPosition = tabLayout.selectedTabPosition
            if (oldScrollY < commentH) {
                if (!isClickSelect && selectedTabPosition != 0) tabLayout.getTabAt(0)?.select()
                val alpha = (oldScrollY / commentH * 255).roundToInt()
                topBarBg.alpha = alpha
                val tabAlpha = oldScrollY / commentH * 1.0f
                tabLayout.alpha = tabAlpha

//                val alpha0 = 255-alpha
                val alpha0 = 255
                binding.inHeader.imgBack.background.alpha = alpha0
                binding.inHeader.imgShare.background.alpha = alpha0
                binding.inHeader.imgCollection.background.alpha = alpha0

                binding.inHeader.imgBack.setImageResource(R.mipmap.shop_back)
                binding.inHeader.imgShare.setImageResource(R.mipmap.shop_share)
                binding.inHeader.imgCollection.setImageResource(if (isCollection) R.mipmap.shop_collect_1 else R.mipmap.shop_collect_0)
            } else {
                topBarBg.alpha = 255
                tabLayout.alpha = 1f
                if (tabLayout.tabCount > 2) {
                    if (!isClickSelect && oldScrollY >= walkH && selectedTabPosition != 3) {
                        tabLayout.getTabAt(3)?.select()
                    } else if (!isClickSelect && oldScrollY >= detailsH && selectedTabPosition != 2) {
                        tabLayout.getTabAt(2)?.select()
                    } else if (!isClickSelect && oldScrollY < detailsH && selectedTabPosition != 1) {
                        tabLayout.getTabAt(1)?.select()
                    }
                } else if (!isClickSelect && oldScrollY >= commentH && selectedTabPosition != 1) {
                    tabLayout.getTabAt(1)?.select()
                }
                binding.inHeader.imgBack.background.alpha = 0
                binding.inHeader.imgBack.setImageResource(R.mipmap.shop_back_black)

                binding.inHeader.imgShare.background.alpha = 0
                binding.inHeader.imgShare.setImageResource(R.mipmap.shop_share_black)

                binding.inHeader.imgCollection.background.alpha = 0
                binding.inHeader.imgCollection.setImageResource(if (isCollection) R.mipmap.shop_collect_1 else R.mipmap.shop_collect_00)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::control.isInitialized) control.onDestroy()
    }

    private fun addLiveDataBus() {
        //购物车数量改变
        LiveDataBus.get().with(LiveDataBusKey.SHOP_DELETE_CAR, Int::class.java).observe(this) {
            control.dataBean.shoppingCartCount = it
            binding.inBottom.tvCartNumber.apply {
                visibility = if (it > 0) View.VISIBLE else View.GONE
                text = "$it"
            }
        }
        //下单回调
        LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK).observe(this) {
            if ("2" != it && "0" != spuId) viewModel.queryGoodsDetails(spuId, false)
        }
        //分享回调
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this) {
            if (it == 0 && ::control.isInitialized) control.shareBack()
        }
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                if (UserManger.UserLoginStatus.USER_LOGIN_SUCCESS == it) {
                    if ("0" != spuId) viewModel.queryGoodsDetails(spuId, false)
                }
            }
    }
}