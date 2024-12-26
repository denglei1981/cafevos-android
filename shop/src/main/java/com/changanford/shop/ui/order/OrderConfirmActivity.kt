package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.ConfirmOrderBean
import com.changanford.common.bean.ConfirmOrderInfoBean
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.CreateOrderBean
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.OrderConfirmSkuItems
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.request.GetRequestResult
import com.changanford.common.util.request.addRecord
import com.changanford.common.util.request.getBizCode
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toIntPx
import com.changanford.common.utilext.toast
import com.changanford.common.web.AndroidBug5497Workaround
import com.changanford.common.widget.pop.PayWaitingPop
import com.changanford.common.wutil.WCommonUtil
import com.changanford.common.wutil.wLogE
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.ConfirmOrderGoodsInfoAdapter
import com.changanford.shop.bean.OrderCarItem
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged
import com.changanford.shop.utils.WConstant
import com.changanford.shop.viewmodel.OrderViewModel
import com.faendir.rhino_android.RhinoAndroidHelper
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.collections.forEachWithIndex
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.Undefined
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 订单确认
 */
@Route(path = ARouterShopPath.OrderConfirmActivity)
class OrderConfirmActivity : BaseActivity<ActOrderConfirmBinding, OrderViewModel>() {
    companion object {
        fun start(goodsInfo: String) {
            JumpUtils.instans?.jump(109, goodsInfo)
        }

        fun start(dataBean: GoodsDetailBean) {
//            val bean=ConfirmOrderBean(orderConfirmType=orderConfirmType, dataList = arrayListOf(dataBean))
            dataBean.recommend = null
            JumpUtils.instans?.jump(109, Gson().toJson(dataBean))
        }

        /**
         * [orderConfirmType]确认订单来源 0详情 1购物车
         * */
        fun start(listBean: ArrayList<GoodsDetailBean>) {
//            val bean= ConfirmOrderBean(orderConfirmType=orderConfirmType, dataList = listBean)
            JumpUtils.instans?.jump(109, Gson().toJson(listBean))
        }
    }

    private lateinit var infoBean: ConfirmOrderBean

    //    private lateinit var dataBean:GoodsDetailBean
    private var isClickSubmit = false
    private var ruleId = ""
    private val orderSkuItems = ArrayList<OrderConfirmSkuItems>()

    //    private var spuPageType=""//商品类型
    private var dataListBean: ArrayList<GoodsDetailBean>? = null
    private val goodsInfoAdapter by lazy { ConfirmOrderGoodsInfoAdapter() }
    private val rbPayWayArr by lazy {
        arrayListOf(
            binding.inPayWay.rbFbAndRmb,
            binding.inPayWay.rbRmb,
            binding.inPayWay.rbCustom
        )
    }
    private var maxUseFb = 0//本次最大可使用福币 默认等于用户余额
    private var totalPayFb: Int = 0//支付总额 福币
    private var minRmbProportion: Float = 0f//最低使用人民币比例 0只能福币支付 -1则不限制
    private var minRmb = "0"
    private var payFb: String? = "0"//福币支付额度
    private var payRmb: String? = "0"//人民币支付额度
    private var orderConfirmType = 0//确认订单来源 0商品详情 1购物车
    private var isAgree: Boolean = false//是否同意协议
    private var couponsItem: CouponsItemBean? = null
    private var createOrderBean: CreateOrderBean? = null
    private var mWbType: String = ""//是否ssp维保商品
    private var isWbShop: Boolean = false
    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        binding.topBar.setActivity(this)
        val goodsInfo = intent.getStringExtra("goodsInfo")
        "goodsInfo:$goodsInfo".wLogE("okhttp")
        if (TextUtils.isEmpty(goodsInfo)) {
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal), this)
            this.finish()
            return
        }
        if (goodsInfo!!.startsWith("[")) {
            infoBean = ConfirmOrderBean().apply {
                dataList = Gson().fromJson(
                    goodsInfo,
                    object : TypeToken<ArrayList<GoodsDetailBean?>?>() {}.type
                )
                dataListBean = dataList
                orderConfirmType = 1//传来的是集合则表示从购物车过来
            }
        } else if (goodsInfo.startsWith("{")) {
            infoBean = ConfirmOrderBean().apply {
                orderConfirmType = 0
                val itemBean = Gson().fromJson(goodsInfo, GoodsDetailBean::class.java)
                dataList = arrayListOf(itemBean)
                dataListBean = dataList
            }
        }
        dataListBean?.sortBy { (it.fbPrice.toInt()) * (it.buyNum) }
        orderConfirmType = infoBean.orderConfirmType ?: 0
        initObserve()
        edtCustomOnTextChanged()
        formattingData(true)
        initOrderSkuItems()
        binding.layoutSsp.tvBxDaySelect.setOnClickListener {
            selectDay()
        }
    }

    /**
     * 格式化数据
     * */
    private fun formattingData(isConfim: Boolean = false) {
        val skuItems = arrayListOf<ConfirmOrderInfoBean>()
        var totalBuyNum = 0
        var totalOriginalFb = 0
        var totalFb = 0
        dataListBean?.forEach {
            //秒杀情况下 原价=现价
            if ("SECKILL" == it.spuPageType) {
                it.orginPrice = it.fbPrice
            }
            it.getRMB(it.orginPrice)
            val spuPageType = it.spuPageType
            val skuItem = ConfirmOrderInfoBean(
                skuId = it.skuId, num = it.buyNum, vin = it.vinCode,
                mallMallHaggleUserGoodsId = it.mallMallHaggleUserGoodsId, carModel = it.models
            )
            skuItem.initBean(spuPageType)
            skuItems.add(skuItem)
            totalBuyNum += it.buyNum
            //单价（原价）
            val originalPrice = (it.orginPrice ?: it.fbPrice).toInt()
            //原总商品价 单价*购买数量
            totalOriginalFb += originalPrice * it.buyNum
            totalFb += (it.fbPrice.toInt()) * it.buyNum
            //本条数据为维保商品
            if (WConstant.maintenanceType == spuPageType && TextUtils.isEmpty(infoBean.vinCode)) {
                infoBean.vinCode = it.vinCode
                infoBean.models = it.models
                infoBean.dealerId = it.dealerId
                infoBean.wbType = it.wbType
                infoBean.dealerName = it.dealerName
            }
        }
        infoBean.totalBuyNum = totalBuyNum
        infoBean.totalOriginalFb = totalOriginalFb
        infoBean.totalFb = totalFb
        if (isConfim) {
            bindInfo()
            //获取优惠券信息
            viewModel.confirmOrder(orderConfirmType, skuItems)
        }
    }

    private fun initOrderSkuItems() {
        dataListBean?.forEach {
            val spuPageType = it.spuPageType
            val skuItem = OrderConfirmSkuItems(
                skuId = it.skuId, num = it.buyNum
            )
            skuItem.initBean(spuPageType)
            orderSkuItems.add(skuItem)
        }
    }

    override fun initData() {}

    private fun initObserve() {
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java)
            .observe(this) {
                it?.let {
                    bindingAddress(Gson().fromJson(it, AddressBeanItem::class.java))
                }
            }
        // 地址错误
        LiveDataBus.get().withs<String>(LiveDataBusKey.SHOW_ERROR_ADDRESS).observe(this) {
            showAddressError(it)
        }
        LiveDataBus.get().with(LiveDataBusKey.DISMISS_PAY_WAITING)
            .observe(this@OrderConfirmActivity) {
                payWaitingPop?.dismiss()
            }
        //优惠券、skuItems
        viewModel.createOrderBean.observe(this) {
            it?.let { it1 -> setDiscount(it1) }
            it?.skuItems?.forEach { skuItems ->
                dataListBean?.forEach { goods ->
                    if (goods.skuId == skuItems.skuId) {
                        skuItems.unitPriceFb?.let {
                            goods.fbPrice = it
                        }
                    }
                }
            }
            formattingData()
            createOrderBean = it
            infoBean.freightPrice = it?.freight ?: "0.00"
            binding.inOrderInfo.apply {
                if (it != null) {
                    infoBean.fbBalance = it.totalIntegral
                    minRmbProportion = it.getRmbBfb()//得到人民币最低使用百分比
//                    tvFreightValue.setText(infoBean.freightPrice)
                }
            }
            formattingCouponsData()
        }
        //下单回调
        viewModel.orderInfoLiveData.observe(this) {
            isClickSubmit = false
            productOrderCreate(it.privatePayNo)
            orderCreate(it.privatePayNo)
            if (isWbShop) {
                val bundle = Bundle()
                bundle.putParcelable("orderInfoBean", it)
                startARouter(ARouterCommonPath.SLAActivity, bundle, true)
            } else {
                PayConfirmActivity.start(it)
            }
            LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK, String::class.java)
                .postValue("$orderConfirmType")
            this.finish()
        }
        //选择优惠券回调
        LiveDataBus.get().with(LiveDataBusKey.COUPONS_CHOOSE_BACK, CouponsItemBean::class.java)
            .observe(this) {
                bindCoupon(it)
            }
        viewModel.jdCheckBean.observe(this) {
            if (!it.isNullOrEmpty()) {
                it.forEach { skuItem ->
                    dataListBean?.forEach { goodsBean ->
                        if (skuItem.skuId == goodsBean.skuId) {
                            if (skuItem.stockState == "0") {
                                goodsBean.noStock = true
                            } else if (skuItem.stockState == "1") {
                                goodsBean.showSevenTips = true
                            }

                        }
                    }
                }
                goodsInfoAdapter.setList(dataListBean)
                val noStocks = dataListBean?.find { it.noStock }
                if (noStocks == null) {
                    binding.inBottom.btnSubmit.noStock = false
                } else {
                    binding.inBottom.btnSubmit.noStock = true
                    binding.inBottom.btnSubmit.updateEnabled(false)
                }
            }
        }
    }

    private fun setDiscount(order: CreateOrderBean) {
        var disCount = 0
        order.skuItems?.forEach { goodsBean ->
            goodsBean.unitPriceFbOld?.let {
                disCount += (it.toInt() - (goodsBean.unitPriceFb?.toInt() ?: 0)) * goodsBean.num
            }
        }
        binding.inOrderInfo.apply {
            tvMemberDiscountValue.visibility = View.VISIBLE
            tvMemberDiscountValue.setText(WCommonUtil.getRMB("$disCount"))
        }
    }

    var productNumber = 0

    private fun productOrderCreate(orderId: String?) {
        dataListBean?.forEach {
            it.run {
                val isSeckill = if (killStates == 5) "是" else "否"
                GIOUtils.productOrderCreate(
                    spuId,
                    skuId,
                    spuName,
                    rmbPrice,
                    fbPrice,
                    isSeckill,
                    buyNum.toString(),
                    orderId
                )
                productNumber += buyNum
            }

        }
    }

    private fun orderCreate(orderId: String?) {
        val ifCoupon = if (couponsItem == null) {
            "否"
        } else "是"
        GIOUtils.orderCreate(
            orderId,
            payRmb,
            payFb,
            productNumber.toString(),
            ifCoupon,
            couponsItem?.couponId,
            if (ifCoupon == "否") "无" else WCommonUtil.getRMB(
                couponsItem?.discountsFb?.toString(),
                ""
            ),
            couponsItem?.couponName
        )
    }

    /**
     * 格式优惠券信息-筛选有用 和找出最大优惠
     * */
    private fun formattingCouponsData() {
        val skuItems = createOrderBean?.skuItems
        //先时间排序
        var couponListBean =
            createOrderBean?.coupons?.sortedWith(compareBy { it.validityEndTime })?.toMutableList()
        var newCoupons = ArrayList<CouponsItemBean>()
        couponListBean?.forEach { coupon ->
            coupon.mallMallSkuIds?.forEach { cskuid ->
                skuItems?.forEach {
                    if (it.skuId == cskuid) {
                        if (!newCoupons.contains(coupon)) {
                            newCoupons.add(coupon)
                        }
                    }
                }
            }
        }
        couponListBean = newCoupons
        if (couponListBean != null && couponListBean.isNotEmpty()) {
            //判断每个优惠券是否可用
            for ((i, item) in couponListBean.withIndex()) {
                item.isAvailable = false
                var totalPrice: Long = 0//满足优惠券的总价
                item.mallMallSkuIds?.forEach { skuId ->
                    //查询skuId是否在订单中
                    skuItems!!.find { skuId == it.skuId }?.apply {
                        //计算总价=单价福币*数量
                        totalPrice += ((unitPriceFb ?: "0").toLong() * num)
                    }
                }
                //该券满足优惠条件
                if (totalPrice >= item.getRmbToFb()) {
                    //标注该券可用
                    item.isAvailable = true
                    //计算实际优惠
                    val discountsFb: Long = when (item.discountType) {
                        //折扣
                        "DISCOUNT" -> {
                            //折扣金额 福币
                            val discountAmountFb = item.discountAmount(totalPrice)
                            //最大折扣
                            if (discountAmountFb <= (item.getRmbToFb(item.couponMoney))) discountAmountFb else item.getRmbToFb(
                                item.couponMoney
                            )
                        }
                        //满减和立减
                        else -> item.getRmbToFb(item.couponMoney)
                    }
                    item.discountsFb = discountsFb
                } else item.discountsFb = item.getRmbToFb(item.couponMoney)
                couponListBean[i] = item
            }
            //优惠金额排序（从小到大） 然后倒叙（则结果是从大到小）
            val sortList = couponListBean.sortedWith(compareBy { it.discountsFb }).reversed()
            //.sortedWith(compareBy { it.validityEndTime})
            //根据条件将优惠分成两份
            val (match, rest) = sortList.partition { it.isAvailable }
            val newList = arrayListOf<CouponsItemBean>()
            newList.addAll(match)
            newList.addAll(rest)
            createOrderBean?.coupons = newList
            //默认选中最大
            val defaultCoupons = if (match.isNotEmpty()) match[0] else null
            bindCoupon(defaultCoupons)
        } else bindCoupon(null)

    }

    //固定金额支付方法的时候,是否支持开启现金支付
    private var allCashOff = false

    //是否是固定金额支付
    private fun isMixPayRegular(): Boolean {
        return try {
            val json = JSON.parseObject(createOrderBean?.mallPayConfig)
            val mPayType = json.getInteger("pay_type")
            val mixPayType = json.getInteger("mix_pay_type")
            allCashOff = json.getBooleanValue("all_cash")
            mPayType == 2 && mixPayType == 1
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun getExpressionMoney(money: Float): Int {
        //这里参数context使用Android的Context
        val ctx = RhinoAndroidHelper(this).enterContext()
        val scope: Scriptable = ctx.initStandardObjects()
        var useMoney = money
        if (useMoney < 0) {
            useMoney = 0f
        }
        try {
            val obj: JSONObject = JSON.parseObject(createOrderBean?.mallPayConfig)
            val fixedAmountExpression: JSONArray = obj.getJSONArray("fixed_amount_expression")
            if (fixedAmountExpression.size == 0) {
                "商城支付配置错误".toast()
            }
            fixedAmountExpression.forEachIndexed { index, any ->
                val item = fixedAmountExpression.getJSONObject(index)
                val express = item.getString("expression").replace("x", useMoney.toString())
                val o = ctx.evaluateString(scope, express, "", 1, null)
                if (o !== Scriptable.NOT_FOUND && o !is Undefined) {
                    o as Boolean //执行
                    if (o) {
                        return (item.getString("default_amount").replace("x", useMoney.toString())
                            .toFloat() * 100).toInt()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 绑定优惠券和支付信息
     * */
    @SuppressLint("SetTextI18n")
    private fun bindCoupon(itemCoupon: CouponsItemBean? = null) {
        discountMin = 0L
        couponsItem = itemCoupon
        var couponsAmount = "0"//福币
        binding.inOrderInfo.tvCouponsValue.apply {
            val coupons = createOrderBean?.coupons
            val item0 = if (coupons != null && coupons.size > 0) coupons[0] else null
            if (item0 == null || !item0.isAvailable) {
//                isEnabled=false
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity, R.color.color_99))
                setText(R.string.str_temporarilyNoUse)
            } else if (itemCoupon == null) {
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity, R.color.color_33))
                setText(R.string.str_pleaseSelectCoupons)
            } else {
                couponsAmount = "${itemCoupon.discountsFb}"
                isEnabled = true
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity, R.color.color_33))
                setText(WCommonUtil.getRMB(couponsAmount, ""))
            }
        }

        //总共支付 (商品金额+运费)
        totalPayFb = infoBean.getTotalPayFbPrice(couponsAmount, true)
//        if (isMixPayRegular() && itemCoupon != null) {
//            val mustPay = countMinFb()
//            if (totalPayFb == 0) {//混合固定支付不允许零元购
//                if (itemCoupon.discountsFb > infoBean.totalOriginalFb ) {//优惠券的金额大于商品金额就不允许用优惠券
//                    bindCoupon()
//                } else {
//                    itemCoupon.discountsFb -= mustPay //优惠券减少优惠价格,必须要支付金额
//                    couponsAmount = "${itemCoupon.discountsFb}"
//                    totalPayFb = infoBean.getTotalPayFbPrice(couponsAmount, true)
//                    binding.inOrderInfo.tvCouponsValue.apply {
//                        setText(WCommonUtil.getRMB(couponsAmount, ""))
//                    }
//                }
//            }
//        }
        infoBean.totalPayFb = totalPayFb
        binding.inOrderInfo.tvTotal.setHtmlTxt(WCommonUtil.getRMB("$totalPayFb"), "#1700f4")
        //最少使用多少人民币（fb）=总金额*最低现金比 向上取整
        var minFb = 0
        if (totalPayFb != 0 || isMixPayRegular()) {
            minFb = countMinFb(itemCoupon)
            binding.inOrderInfo.tvCouponsTips.isVisible = isMixPayRegular() && discountMin != 0L
            if (discountMin != 0L) {
                couponsAmount = "${itemCoupon?.discountsFb?.minus(discountMin)}"
            }
            //总共需要支付多少钱
            val oldPay = infoBean.getTotalPayFbPrice(couponsAmount, true)
            //优惠券抵扣后会出现低于最小支付的情况,这种情况就使用最低支付rmb作为总金额
            totalPayFb = if (isMixPayRegular() && (oldPay == 0 || oldPay < minFb)) minFb else oldPay
            binding.inOrderInfo.tvCouponsTips.text =
                "本次订单每件商品需支付 ${
                    WCommonUtil.getRMB(
                        getExpressionMoney(
                            FBToRmb("1").toFloat()
                        ).toString()
                    )
                }元，系统会自动从您的优惠券中扣除，以符合订单支付规则。"
            val useCoupons =
                if (itemCoupon != null && isMixPayRegular() && itemCoupon.discountsFb > infoBean.getAllFbPrice()) {
                    binding.inOrderInfo.tvCouponsTips.isVisible = true
                    (WCommonUtil.getRMB(
                        (infoBean.getAllFbPrice() - totalPayFb).toString(), ""
                    ))
                } else WCommonUtil.getRMB(couponsAmount, "")
            var visibilityCoupons = useCoupons
            if (visibilityCoupons == "0" && itemCoupon != null) {
                visibilityCoupons = WCommonUtil.getRMB(infoBean.getAllFbPrice().toString(), "")
            }
            binding.inOrderInfo.tvCouponsValue.setText(visibilityCoupons)
            infoBean.totalPayFb = totalPayFb
            binding.inOrderInfo.tvTotal.setHtmlTxt(WCommonUtil.getRMB("$totalPayFb"), "#1700f4")
            if (!canUseCoupons && itemCoupon != null) {//说明优惠券用不起(最小支付等于商品价格的时候会出现)
                bindCoupon()
            }
        }
        var maxFb: Int = totalPayFb - minFb
        if (maxFb < 0) {
            maxFb = 0
        }
        //用户余额
        val fbBalance = infoBean.fbBalance ?: 0
        //最大可使用福币
        maxUseFb = when {
            createOrderBean?.getPayType() == 1 -> 0
            fbBalance >= maxFb -> maxFb
            minRmbProportion != 0f -> {
                minFb = totalPayFb - fbBalance
                fbBalance
            }

            isMixPayRegular() -> {
                if ( fbBalance >= maxFb )maxFb else fbBalance
            }
            else -> {
                minFb = 0
                fbBalance
            }
        }
        if (createOrderBean?.getPayType() == 0) {//纯福币支付
            minFb = 0
        }
        binding.inPayWay.apply {
            minRmb = getRMB("$minFb")
            minRmb = if (minRmb.toFloat() > 0f) "+¥$minRmb" else ""
            rbFbAndRmb.text =
                if (minRmbProportion != 0f || isMixPayRegular()) "${totalPayFb-minFb}$minRmb" else "$totalPayFb"
            rbRmb.text = "¥${getRMB("$totalPayFb")}"
        }
        ("totalPayFb:$totalPayFb>>>minRmbProportion:$minRmbProportion>>>>minFb:$minFb>>>>maxFb:$maxFb>>>maxUseFb:$maxUseFb>>>>" +
                ">totalFb:${infoBean.totalFb}>totalOriginalFb:${infoBean.totalOriginalFb}").wLogE("okhttp")
        initPayWay()
    }

    //优惠券减少多少钱 混合支付固定支付的情况下会减少
    private var discountMin = 0L
    private var canUseCoupons = false

    private fun countMinFb(itemCoupon: CouponsItemBean? = null): Int {
        var minFb = 0
        var useAddPreferential = 0
        canUseCoupons = false
        discountMin = 0L
        if (!isMixPayRegular()) {//不是固定金额就取比例
            minFb = if (minRmbProportion > 0f) WCommonUtil.getHeatNumUP(
                "${totalPayFb * minRmbProportion}",
                0
            ).toInt() else 0
        } else {
            if (itemCoupon == null) {//没有优惠券
                dataListBean?.forEachWithIndex { index, goodsDetailBean ->
                    minFb += getExpressionMoney(
                        FBToRmb(goodsDetailBean.fbPrice).toFloat()
                    ) * goodsDetailBean.buyNum
                }
            } else {//有优惠券，先计算不满足的
                //能用优惠券的商品
                val canUseCouponsData = ArrayList<GoodsDetailBean>()
                itemCoupon?.mallMallSkuIds?.let {
                    label@ for (i in it.indices) {
                        val couponSkuId = it[i]
                        dataListBean?.let { dataList ->
                            label2@ for (l in dataList.indices) {
                                val dataSkuId = dataList[l].skuId
                                if (couponSkuId == dataSkuId) {
                                    canUseCouponsData.add(dataList[l])
                                    break@label2
                                }
                            }
                        }
                    }
                }
                val noCouponsData =
                    dataListBean?.filterNot { it in canUseCouponsData } as ArrayList<GoodsDetailBean>?
                noCouponsData?.sortBy { (it.fbPrice.toInt()) * (it.buyNum) }
                var noCouponsUseFb = 0
                noCouponsData?.forEachWithIndex { i, goodsDetailBean ->
                    minFb += getExpressionMoney(FBToRmb(goodsDetailBean.fbPrice).toFloat()) * goodsDetailBean.buyNum
                    noCouponsUseFb += (goodsDetailBean.fbPrice.toFloat() * goodsDetailBean.buyNum).toInt()
                }
                //扣除不满足的，还剩余商品价值总额
                val haveUseFb = infoBean.totalOriginalFb - noCouponsUseFb
                canUseCouponsData.sortBy { (it.fbPrice.toInt()) * (it.buyNum) }
                //在计算满足优惠券的
                canUseCouponsData.forEachWithIndex { index, goodsDetailBean ->
                    val preferential =//一个商品折扣多少钱
                        if (index != canUseCouponsData.size - 1)
                            WCommonUtil.getHeatNumUP(
                                "${((goodsDetailBean.fbPrice.toFloat() * goodsDetailBean.buyNum) / haveUseFb) * itemCoupon.discountsFb}",
                                0
                            ).toInt() else itemCoupon.discountsFb.toInt() - useAddPreferential
                    if (index != canUseCouponsData.size - 1) {
                        useAddPreferential += preferential
                    }
                    var needPayPb = 0f
                    needPayPb = if (index != canUseCouponsData.size - 1) {
                        //一项商品扣除优惠后价格
                        ((goodsDetailBean.fbPrice.toFloat() * goodsDetailBean.buyNum) - preferential)
                    } else {
                        //是最后一个要取剩余优惠
                        (goodsDetailBean.fbPrice.toFloat() * goodsDetailBean.buyNum) - (itemCoupon.discountsFb - useAddPreferential).toFloat()
                    }
                    //优惠后一项商品一个数量的价格
                    val onePayFb = WCommonUtil.getHeatNum(
                        "${needPayPb / goodsDetailBean.buyNum}",
                        0
                    )
                    Log.e(
                        "asdasd",
                        "${needPayPb}==${goodsDetailBean.buyNum}==${preferential}====${goodsDetailBean.fbPrice}"
                    )
                    if (onePayFb.toString() != "0") {
                        val oneMinFb =
                            getExpressionMoney(FBToRmb((onePayFb.toFloat()).toString()).toFloat())
                        if (oneMinFb != goodsDetailBean.fbPrice.toInt()) {
                            canUseCoupons = true
                        }
                        minFb += oneMinFb * goodsDetailBean.buyNum
                        //如果最小支付金额大于实际支付金额 就从优惠券里面扣除
                        if (oneMinFb > onePayFb.toInt() && onePayFb.toInt() > 0) {
                            discountMin += (oneMinFb - onePayFb.toInt()) * goodsDetailBean.buyNum
//                            Log.e("asdasd11", "${oneMinFb}===${onePayFb}===${preferential}===${discountMin}")
                        }
                    } else if (onePayFb.toString() == "0") {
                        val oneMinFb =
                            getExpressionMoney(FBToRmb((goodsDetailBean.fbPrice.toFloat()).toString()).toFloat()) * goodsDetailBean.buyNum
                        minFb += oneMinFb
                        if (oneMinFb != goodsDetailBean.fbPrice.toInt()) {
                            canUseCoupons = true
                        }
                        discountMin += oneMinFb - ((goodsDetailBean.fbPrice.toInt() * goodsDetailBean.buyNum) - preferential)
//                        Log.e(
//                            "asdasd",
//                            "${oneMinFb}===${(goodsDetailBean.fbPrice.toInt() * goodsDetailBean.buyNum)}===${preferential}"
//                        )
                    }
                }
            }
        }
        return minFb
    }

    private fun bindInfo() {
        //地址信息
        viewModel.addressList.observe(this) { addressList ->
            //默认获取地址列表的默认收货地址
            val item: AddressBeanItem? = addressList?.find { it.isDefault == 1 }
            bindingAddress(item)
        }
        val addressInfo = infoBean.addressInfo
        if (TextUtils.isEmpty(addressInfo)) viewModel.getAddressList()
        else if (addressInfo!!.startsWith("{")) viewModel.addressList.postValue(
            arrayListOf(
                Gson().fromJson(
                    addressInfo,
                    AddressBeanItem::class.java
                )
            )
        )

        //商品列表
        binding.inGoodsInfo.apply {
            recyclerView.adapter = goodsInfoAdapter
            goodsInfoAdapter.setList(dataListBean)
        }
        //维保商品
        manageMaintenance()


        //订单信息 商品金额运费
        binding.inOrderInfo.apply {
            tvAmountValue.text = WCommonUtil.getRMB("${infoBean.totalOriginalFb}")
            //是否有砍价
            dataListBean?.find { it.spuPageType == "2" }?.let {
                tvBargaining.visibility = View.VISIBLE
                tvBargaining.setText(R.string.str_bargainingFavorable)
                tvTvBargainingValue.visibility = View.VISIBLE
                //砍价优惠=原总价-减现总价（注：前提砍价不能加入购物车）
                val preferentialFb = infoBean.totalOriginalFb - infoBean.totalFb
                tvTvBargainingValue.setText(WCommonUtil.getRMB("$preferentialFb"))
                tvMemberDiscountValue.setText(WCommonUtil.getRMB("0"))
            }
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            //提交订单
            R.id.btn_submit -> {
                if (!isWbShop) {
                    submitOrder()
                } else {
                    val vinMileage = binding.layoutSsp.etKm.text.toString()
                    val insurabceEffDate = binding.layoutSsp.tvBxDaySelect.text.toString()
                    val insurabceBillNo = binding.layoutSsp.etBxNum.text.toString()
                    val insurationName = binding.layoutSsp.etCpName.text.toString()
                    if (mWbType == "EW" && vinMileage.isEmpty()) {
                        "请完善补充信息".toast()
                        return
                    }
                    if (mWbType == "RWF" && insurabceEffDate == "请选择") {
                        "请完善补充信息".toast()
                        return
                    }
                    viewModel.esbCheck(
                        infoBean.dealerId,
                        insurabceEffDate,
                        infoBean.dataList?.get(0)?.skuId,
                        infoBean.vinCode,
                        vinMileage.ifEmpty { "1" },
                        insurabceBillNo,
                        insurationName
                    ) {
                        submitOrder()
                    }
                }
            }
            //选择地址
            R.id.in_address -> JumpUtils.instans?.jump(20, "1")
            //服务协议
            R.id.tv_agreement -> JumpUtils.instans?.jump(1, MConstant.H5_SHOP_AGREEMENT)
            //协议勾选
            R.id.checkBox -> {
                isAgree = binding.checkBox.isChecked
                if (isAgree) {
                    getBizCode(this, MConstant.agreementShop, object : GetRequestResult {
                        override fun success(data: Any) {
                            ruleId = data.toString()
                        }

                    })
                }
                updateBtnUi()
            }
            //福币+人民币支付
            R.id.rb_fbAndRmb -> clickPayWay(0)
            //人民币支付
            R.id.rb_rmb -> clickPayWay(1)
            //自定义支付
            R.id.rb_custom -> clickPayWay(2)
            //选择优惠券
            R.id.tv_coupons_value -> {
                createOrderBean?.apply {
                    ChooseCouponsActivity.start(
                        "${couponsItem?.couponId}_${couponsItem?.couponRecordId}",
                        this
                    )
                }
            }
        }
    }

    private fun submitOrder() {
        if (!isClickSubmit) {
            showWaitPay("订单提交中")
            isClickSubmit = true
            val consumerMsg = binding.inGoodsInfo.edtLeaveMsg.text.toString()
            if (ruleId.isNotEmpty()) {
                addRecord(ruleId)
            }
            val vinMileage = binding.layoutSsp.etKm.text.toString()
            val insurabceEffDate = binding.layoutSsp.tvBxDaySelect.text.toString()
            val insurabceBillNo = binding.layoutSsp.etBxNum.text.toString()
            val insurationName = binding.layoutSsp.etCpName.text.toString()

            viewModel.createOrder(
                orderConfirmType = orderConfirmType,
                payFb = payFb,
                payRmb = payRmb,
                addressId = infoBean.addressId,
                consumerMsg = consumerMsg,
                skuItems = createOrderBean?.skuItems,
                couponId = couponsItem?.couponId,
                couponRecordId = couponsItem?.couponRecordId,
                freight = infoBean.freightPrice,
                payBfb = createOrderBean?.payBfb,
                dealerId = infoBean.dealerId,
                insurabceEffDate = insurabceEffDate,
                insurabceBillNo = insurabceBillNo,
                insurationName = insurationName,
                vinMileage = vinMileage.ifEmpty { "1" }
            )

        }
        GlobalScope.launch {
            delay(3000L)
            isClickSubmit = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindingAddress(item: AddressBeanItem?) {
        if (null == item) {//未绑定地址
            infoBean.addressId = null
            binding.inAddress.tvAddress.setText(R.string.str_pleaseAddShippingAddress)
            binding.inAddress.tvAddressRemark.visibility = View.GONE
            binding.inBottom.btnSubmit.updateEnabled(false)
        } else {
            infoBean.addressId = item.addressId
            updateBtnUi()
            binding.inAddress.tvAddressRemark.visibility = View.VISIBLE
            binding.inAddress.tvAddress.text =
                "${item.provinceName}${item.cityName ?: ""}${item.districtName ?: ""}${item.addressName}"
            binding.inAddress.tvAddressRemark.text = "${item.consignee}   ${item.phone}"
            showWaitPay("订单处理中")
            viewModel.jdOrderCreateBeforeCheck(item.addressId.toString(), orderSkuItems)
        }
    }

    /**
     * 处理维保商品
     * */
    private fun manageMaintenance() {
        isWbShop = false
        if (TextUtils.isEmpty(infoBean.vinCode)) return
        binding.apply {
            //维保商品不需要收货地址
            inAddress.layoutAddress.visibility = View.GONE
            layoutSsp.root.isVisible = infoBean.wbType == "EW" || infoBean.wbType == "RWF"
            infoBean.apply {
                if (wbType == "EW") {
                    layoutSsp.clEw.isVisible = true
                }
                if (wbType == "RWF") {
                    layoutSsp.clRwf.isVisible = true
                }
            }
            mWbType = infoBean.wbType
            isWbShop = true
            composeView.setContent { MaintenanceCompose() }
        }

    }

    /**
     * 维保商品信息
     * */
    @Composable
    private fun MaintenanceCompose() {
        val carList = ArrayList<OrderCarItem>()
        carList.add(OrderCarItem(resources.getString(R.string.str_vinCode), infoBean.vinCode ?: ""))
        carList.add(OrderCarItem(resources.getString(R.string.str_models), infoBean.models ?: ""))
        if (!infoBean.dealerId.isNullOrEmpty()) {
            val useContent = infoBean.dealerName
            carList.add(
                OrderCarItem(
                    resources.getString(R.string.str_dealName),
                    useContent ?: ""
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(bottom = 17.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(colorResource(R.color.color_F4))
            )
            carList.forEachWithIndex { i, orderCarItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = if (i != carList.size - 1) 19.dp else 19.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = orderCarItem.title,
                        color = colorResource(R.color.color_33),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(end = 20.dp)
                    )
                    Text(
                        text = orderCarItem.content,
                        color = colorResource(R.color.color_33),
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,

                        )
                }
            }
//            for (i in 0..1) {//0 vin码 1车型
//
//            }
        }
    }

    private fun initPayWay() {
        binding.inPayWay.apply {
            tvMaxUseFb.setText("$maxUseFb")
            //createOrderBean
            //0 福币+人民币、1人民币、2自定义福币
            try {
                val json = JSON.parseObject(createOrderBean?.mallPayConfig)
                val mPayType = json.getInteger("pay_type")
                val mixPayType = json.getInteger("mix_pay_type")
                //pay_type:支付方式 0福币支付，1现金支付  2混合支付
                //mix_pay_type:混合支付方式 0按比例支付 1固定金额支付
                when (mPayType) {
                    //纯福币支付
                    0 -> {
                        rbFbAndRmb.visibility = View.VISIBLE
                        rbCustom.visibility = View.GONE
                        rbRmb.visibility = View.GONE
                        clickPayWay(0, false)
                    }
                    //现金支付
                    1 -> {
                        //                        if (maxUseFb == 0) rbFbAndRmb.visibility = View.GONE
                        rbFbAndRmb.visibility = View.GONE
                        rbCustom.visibility = View.GONE
                        rbRmb.visibility = View.VISIBLE
                        clickPayWay(1, false)
                    }
                    //混合支付
                    2 -> {
                        //比例支付
                        if (mixPayType == 0) {
                            rbFbAndRmb.visibility = View.VISIBLE
                            rbCustom.visibility = View.GONE
                            clickPayWay(0)
                        } else {//固定金额支付
                            if (maxUseFb == 0 && minRmb.isNotEmpty()) {
//                                rbRmb.isEnabled = false
                                rbRmb.isVisible = true
                                rbCustom.isVisible = false
                                rbFbAndRmb.isVisible = false
                                clickPayWay(1, false)
                            } else if (allCashOff && getRMB("$totalPayFb") != "0") {
                                rbRmb.isVisible = true
                                rbCustom.isVisible = true
                                rbFbAndRmb.isVisible = true
                                clickPayWay(0, true)
                            } else {
                                rbRmb.isVisible = false
                                rbCustom.isVisible = true
                                rbFbAndRmb.isVisible = true
                                clickPayWay(0, true)
                            }
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                "参数错误".toast()
            }

        }
        calculateFbAndRbm()
    }

    /**
     * 支付方式选择点击
     * [type]0 福币+人民币、1人民币、2自定义福币
     * */
    private fun clickPayWay(type: Int, isUpdatePayCus: Boolean = true) {
        for ((index, it) in rbPayWayArr.withIndex()) {
            it.isChecked = type == index
        }
        if (isUpdatePayCus) {
            updatePayCustom()
        }
        updateBtnUi()
    }

    /**
     * 自定义混合支付 福币输入监听
     * */
    private fun edtCustomOnTextChanged() {
        binding.inPayWay.apply {
            edtCustom.onTextChanged {
                val inputFb = it.s
                "输入结果：${it.before}>>>>${it.start}>>>>$inputFb".wLogE("okhttp")
                if (!TextUtils.isEmpty(inputFb)) {
                    //输入的福币超出可使用的范围
                    if (inputFb.toString().toInt() > maxUseFb) {
                        edtCustom.setText("$maxUseFb")
                        edtCustom.setSelection(edtCustom.text.length)//光标移动到末尾
                        getString(R.string.str_hasMaxUseFb).toast()
                    }
                    calculateFbAndRbm()
                } else {
                    tvCustomRmb.text = ""
                }
                updateBtnUi()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateFbAndRbm() {
        binding.inPayWay.apply {
            val inputFb = edtCustom.text.toString()
            if (!TextUtils.isEmpty(inputFb)) {
                tvCustomFb.text = inputFb
                tvCustomRmb.text = "+¥${getRMB("${totalPayFb - inputFb.toInt()}")}"
            } else {
                tvCustomFb.text = ""
                tvCustomRmb.text = ""
            }
        }
    }

    /**
     * 获取支付组合额度
     * */
    @SuppressLint("SetTextI18n")
    private fun getPayLines(): Boolean {
        binding.inPayWay.apply {
            when {
                //选中了混合支付
                rbFbAndRmb.isChecked -> {
                    val str = rbFbAndRmb.text.toString()
                    if (str.contains("+¥")) {
                        val splitArr = str.split("+¥")
                        if (isMixPayRegular()) {
                            payRmb = splitArr[1]
                            val regularPayFb = (totalPayFb - (payRmb!!.toFloat() * 100))
                            payFb = if (regularPayFb < 0) "0" else regularPayFb.toString()
                        } else {
                            payFb = splitArr[0]
                            payRmb = splitArr[1]
                        }
                    } else {
                        payFb = str
                        payRmb = "0"
                    }
                }
                //选中了人民币支付
                rbRmb.isChecked -> {
                    payFb = "0"
                    payRmb = getRMB("$totalPayFb")
                }
                //选中了自定义混合支付
                rbCustom.isChecked -> {
                    payFb = edtCustom.text.toString()
                    payRmb = if (!TextUtils.isEmpty(payFb)) {
                        getRMB("${totalPayFb - (payFb ?: "0").toInt()}")
                    } else {
                        payFb = null
                        null
                    }
                }
            }
        }
        val isPrice = !TextUtils.isEmpty(payFb) && !TextUtils.isEmpty(payRmb)
        bindBottomPrice()
        return isPrice
    }

    private fun bindBottomPrice() {
        binding.inBottom.tvPayPrice.apply {
            val showPayFb = payFb?.substringBefore(".")
            var drawableStart =
                if (!TextUtils.isEmpty(payFb) && payFb!!.toFloat() > 0f) ContextCompat.getDrawable(
                    context,
                    R.mipmap.ic_shop_fb_42
                ) else null
            val endStr =
                if (!TextUtils.isEmpty(payRmb) && payRmb!!.toFloat() > 0) "￥$payRmb" else ""
            text =
                if (drawableStart != null && !TextUtils.isEmpty(endStr)) "$showPayFb+$endStr" else if (TextUtils.isEmpty(
                        endStr
                    )
                ) showPayFb ?: "" else endStr
            if (TextUtils.isEmpty(endStr)) drawableStart =
                ContextCompat.getDrawable(context, R.mipmap.ic_shop_fb_42)
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null)
        }
    }

    /**
     * 将福币转换为人民币 1元=100福币
     * */
    private fun getRMB(fb: String? = "$totalPayFb"): String {
        var rmbPrice = "0"
        if (fb != null) {
            val fbToFloat = fb.toFloat()
            if (fbToFloat < 0) return "0"
            val remainder = fbToFloat % 100
            rmbPrice = if (remainder > 0) "${fbToFloat / 100}"
            else "${fbToFloat.toInt() / 100}"
        }
        "getRMB>fb:$fb>>rmbPrice:$rmbPrice".wLogE("okhttp")
        return rmbPrice
    }

    private fun updatePayCustom() {
        binding.inPayWay.apply {
            rbRmb.visibility =
                if (minRmbProportion != 0f || (isMixPayRegular() && allCashOff && getRMB("$totalPayFb") != "0")) View.VISIBLE else View.GONE
            if ((maxUseFb > 0 && minRmbProportion != 0f) || isMixPayRegular()) {
                //是否选中自定义支付
                val isCheck = rbCustom.isChecked
                if (isCheck) {
                    rbCustom.visibility = View.INVISIBLE
                    layoutCustom.visibility = View.VISIBLE
                } else {
                    layoutCustom.visibility = View.GONE
                    rbCustom.visibility = View.VISIBLE
                }
            } else if (totalPayFb == 0) {//总价为0
                rbRmb.visibility = View.GONE
                payFb = "0"
                rbFbAndRmb.visibility = View.VISIBLE
            } else if (minRmbProportion != 0f) {
                rbFbAndRmb.visibility = View.GONE
            } else if (minRmbProportion == 0f) {//纯福币支付
//                rbFbAndRmb.visibility=View.VISIBLE
//                payFb="$totalPayFb"
            }
        }
    }

    /**
     * 更新底部提交按钮状态
     * */
    private fun updateBtnUi() {
        val isPrice = getPayLines()
        infoBean.apply {
            binding.inBottom.btnSubmit.apply {
                //福币不足
                if (isPrice && (payFb ?: "0").toFloat() > fbBalance ?: 0) setStates(8)
                else {
                    setText(R.string.str_submitOrder)
                    var isSubmit: Boolean = isPrice && isAgree
                    //非维保商品 需要判断地址是否为空
                    if (isSubmit && TextUtils.isEmpty(vinCode)) isSubmit = null != addressId
                    updateEnabled(isSubmit)
                }
            }
        }
    }

    private var payWaitingPop: PayWaitingPop? = null

    private fun showWaitPay(content: String) {
        if (payWaitingPop == null) {
            payWaitingPop = PayWaitingPop(this).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setBlurBackgroundEnable(false)
                setContent(content)
                showPopupWindow()
            }
        } else {
            payWaitingPop?.setContent(content)
            payWaitingPop?.showPopupWindow()
        }

    }

    /**
     * 将福币转换为人民币 1元=100福币
     * */
    private fun FBToRmb(fb: String): String {
        val fbToFloat = fb.toFloat()
        val remainder = fbToFloat % 100
        return if (remainder > 0) "${fbToFloat / 100}"
        else "${fb.toFloat() / 100}"
    }

    private fun showAddressError(errorMsg: String) {
        AlertDialog(this).builder()
            .setMsg(errorMsg)
            .setMsgSize(15)
            .setMsgHeight(80.toIntPx())
            .setMsgGravityCenter()
            .setMsgColor(ContextCompat.getColor(this, R.color.color_33))
            .setNegativeButton("好的", R.color.color_01025C) { JumpUtils.instans?.jump(20, "1") }
            .show()
    }

    private var datePicker: DatePicker? = null

    @SuppressLint("SetTextI18n")
    private fun selectDay() {
        val bTime = getCurrentDate()
        val bb: List<String> = bTime.split('-')

        datePicker = DatePicker(this).apply {
            wheelLayout.setDateLabel("年", "月", "日")
            wheelLayout.setRange(
                DateEntity.target(bb[0].toInt() - 5, 1, 1),
                DateEntity.target(bb[0].toInt(), bb[1].toInt(), bb[2].toInt())
            )
            if (bb.size == 3) {
                wheelLayout.setDefaultValue(
                    DateEntity.target(
                        bb[0].toInt(),
                        bb[1].toInt(),
                        bb[2].toInt()
                    )
                )
            }
        }
        datePicker?.setOnDatePickedListener { year, month, day ->
            binding.layoutSsp.tvBxDaySelect.text = "$year-$month-$day"
        }
        datePicker?.show()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(Calendar.getInstance().time)
    }
}
