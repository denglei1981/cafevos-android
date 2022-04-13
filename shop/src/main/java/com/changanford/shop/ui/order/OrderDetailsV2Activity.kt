package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.OrderReceiveAddress
import com.changanford.common.bean.ShopAddressInfoBean
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.CustomImageSpanV2
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MTextUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.logE
import com.changanford.shop.R
import com.changanford.shop.bean.InvoiceInfo
import com.changanford.shop.bean.RefundBean
import com.changanford.shop.bean.SaleAfterBean
import com.changanford.shop.control.OrderControl
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ActivityOrderDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.order.adapter.OrderDetailsItemV2Adapter
import com.changanford.shop.ui.sale.adapter.OrderSaleStateAdapter
import com.changanford.shop.ui.shoppingcart.MultiplePackageActivity
import com.changanford.shop.ui.shoppingcart.request.NoLogisticsInfoActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import java.text.SimpleDateFormat
import kotlin.math.abs

/**
 * @Author : wenke
 * @Time : 2021/9/26 0026
 * @Description : 商品订单详情2
 */
@Route(path = ARouterShopPath.OrderDetailActivity)
class OrderDetailsV2Activity : BaseActivity<ActivityOrderDetailsBinding, OrderViewModel>() {
    companion object {
        fun start(orderNo: String?) {
            orderNo?.let { JumpUtils.instans?.jump(5, orderNo) }
        }
    }

    private val control by lazy { OrderControl(this, viewModel) }
    private lateinit var dataBean: OrderItemBean
    private var orderNo: String = ""
    private var waitPayCountDown: Long = 1800//支付剩余时间 默认半小时
    private var timeCountControl: PayTimeCountControl? = null

    val orderDetailsItemV2Adapter: OrderDetailsItemV2Adapter by lazy {
        OrderDetailsItemV2Adapter()
    }


    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun initView() {
        binding.topBar.setActivity(this)
        orderNo = intent.getStringExtra("orderNo") ?: ""
        if (orderNo.isEmpty()) {
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal), this)
            this.finish()
            return
        }
//        binding.inGoodsInfo.inGoodsInfo.layoutGoodsInfo.setOnClickListener {
//            control.onceAgainToBuy(viewModel.orderItemLiveData.value)
//        }
        binding.rvShopping.adapter = orderDetailsItemV2Adapter

//        orderDetailsItemV2Adapter.setOnItemClickListener(object : OnItemClickListener{
//            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
//
//            }
//        })

    }


    override fun initData() {
        viewModel.orderItemLiveData.observe(this, {
            bindingData(it)
        })
        addLiveDataBus()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (null != intent) {
            orderNo = intent.getStringExtra("orderNo") ?: orderNo
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOrderDetail(orderNo, !::dataBean.isInitialized)
    }

    @SuppressLint("SetTextI18n")

    private fun bindingData(dataBean: OrderItemBean) {
        timeCountControl?.cancel()
        val evalStatus = dataBean.evalStatus
        val orderStatus = dataBean.orderStatus
        //应付总额
        var totalPayName = R.string.str_copeWithTotalAmount
        binding.inOrderInfo.layoutOrderClose.visibility = View.VISIBLE
        binding.inBottom.apply {
            topBShow()
        }
        binding.inOrderInfo.tvOther.visibility = View.GONE
        binding.inOrderInfo.tvOtherValue.visibility = View.GONE
        showShoppingInfo(dataBean)
        showTotalTag(binding.inGoodsInfo1.tvTotalPrice, dataBean)
        // 商品金额原价
        binding.inGoodsInfo1.tvIntegralGoods.text =
            WCommonUtil.getRMBBigDecimal(dataBean.fbOfOrderPrice)
        viewModel.getOrderStatus(orderStatus, evalStatus).apply {
            dataBean.orderStatusName = this
            when (this) {
                "待付款", "待支付" -> {
                    binding.inOrderInfo.tvOther.visibility = View.GONE
                    binding.inOrderInfo.tvOtherValue.visibility = View.GONE
                    val payCountDown = dataBean.waitPayCountDown ?: waitPayCountDown
                    if (payCountDown > 0) {
                        timeCountControl = PayTimeCountControl(
                            payCountDown * 1000,
                            binding.tvOrderRemainingTime, null,
                            object : OnTimeCountListener {
                                override fun onFinish() {
                                    createCloseOrderPop()
                                    //支付倒计时结束 刷新
//                                GlobalScope.launch {
//                                    delay(1500L)
//                                    viewModel.getOrderDetail(orderNo)
//                                }
                                }
                            })
                        timeCountControl?.start()
                    }
                    binding.inBottom.apply {
                        btnOrderCancle.visibility = View.VISIBLE
                        btnOrderCancle.setText(R.string.str_cancelOrder)
                        btnOrderConfirm.setText(R.string.str_immediatePayment)
                    }
                    binding.inAddress.imgRight.visibility = View.VISIBLE
                    topBShow()
                }
                "待发货" -> {
                    totalPayName = R.string.str_realPayTotalAmount
                    //支付时间
                    dataBean.otherName = getString(R.string.str_payTime)
                    val otherValue = simpleDateFormat.format(dataBean.payTime ?: 0)
                    dataBean.otherValue = otherValue
                    if (TextUtils.isEmpty(dataBean.statusDesc)) {
                        binding.tvOrderRemainingTime.text =
                            getString(R.string.prompt_paymentHasBeen)
                    }
                    dataBean.statusDesc?.let {
                        binding.tvOrderRemainingTime.text = it
                    }
                    BottomBShow()
                    showInvoiceState(dataBean)
                    showRefund(dataBean) // 待发货 申请退款

                }
                "待收货" -> {
                    totalPayName = R.string.str_realPayTotalAmount
                    //发货时间
                    dataBean.otherName = getString(R.string.str_deliveryTime)
                    dataBean.otherValue = simpleDateFormat.format(dataBean.sendTime ?: 0)
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenShipped)
                    binding.inBottom.layoutBottom.visibility = View.GONE
                    BottomBShow()
                    showExpress(dataBean, true) // 展示物流
                    showInvoiceState(dataBean)
                    showGetShop(true)
                    topRefundShow(dataBean,false)
                }
                "退款中" -> {
                    BottomGon()
                    topRefundShow(dataBean)
                }
                "待评价" -> {
                    totalPayName = R.string.str_realPayTotalAmount
                    //发货时间
                    dataBean.otherName = getString(R.string.str_deliveryTime)
                    dataBean.otherValue = simpleDateFormat.format(dataBean.sendTime ?: 0)
                    binding.inBottom.layoutBottom.visibility = View.GONE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_evaluate)
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_eval)
                    BottomBShow()
                    showInvoiceState(dataBean)
                    showExpress(dataBean, true)
                    showComment(dataBean, false)
                }
                "已完成" -> {
                    totalPayName = R.string.str_realPayTotalAmount
                    //发货时间
                    dataBean.otherName = getString(R.string.str_deliveryTime)
                    dataBean.otherValue = simpleDateFormat.format(dataBean.sendTime ?: 0)
                    binding.inBottom.layoutBottom.visibility = View.GONE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_hasBeenCompleted)
                    binding.inBottom.btnOrderConfirm.setText(R.string.str_onceAgainToBuy)
                    if ("2" == dataBean.busSourse) binding.inBottom.btnOrderConfirm.visibility =
                        View.INVISIBLE
                    showExpress(dataBean, true)
                    showComment(dataBean, true) // 展示追评
                    showInvoiceState(dataBean)
                    BottomBShow()
                }
                "售后已处理" -> {
                    totalPayName = R.string.str_realPayTotalAmount
                    binding.inAddress.apply {
                        if (!TextUtils.isEmpty(dataBean.courierNo)) {//已发货
                            //发货时间
                            dataBean.otherName = getString(R.string.str_deliveryTime)
                            dataBean.otherValue = simpleDateFormat.format(dataBean.sendTime ?: 0)
                        } else {//未发货
                            //支付时间
                            dataBean.otherName = getString(R.string.str_payTime)
                            dataBean.otherValue = simpleDateFormat.format(dataBean.payTime ?: 0)
                        }
                    }
                    binding.inBottom.btnOrderConfirm.visibility = View.GONE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_refundComplete)
                }
                "已关闭" -> {
                    binding.inOrderInfo.layoutOrderClose.visibility = View.GONE
                    dataBean.statusDesc?.let {
                        binding.tvOrderRemainingTime.text = dataBean.statusDesc
                    }
                    if ("2" == dataBean.busSourse) binding.inBottom.btnOrderConfirm.visibility =
                        View.INVISIBLE
                    BottomGon()
                    if (!TextUtils.isEmpty(dataBean.refundStatus)) {
                        topRefundShow(dataBean = dataBean)
                    }
                }
            }
        }
        isApplyRefund(dataBean)
        bindingAddressInfo(dataBean.orderReceiveAddress, false)
        //优惠积分
        val preferentialFb = dataBean.preferentialFb
        if (null != preferentialFb && "0" != preferentialFb) {
            //取绝对值 因接口有时返回带-有时不带
            dataBean.preferentialFb = "${abs(preferentialFb.toInt())}"
            binding.inGoodsInfo1.apply {
                tvIntegralVip.visibility = View.VISIBLE
                if ("2" == dataBean.busSourse) tvMemberDiscount.setText(R.string.str_bargainingFavorable)
                tvMemberDiscount.visibility = View.VISIBLE
            }
        }
        val freightPrice = dataBean.freightPrice
        if ("0" == freightPrice) dataBean.freightPrice = "0.00"
        dataBean.orderTimeTxt = simpleDateFormat.format(dataBean.orderTime ?: 0)
        binding.model = dataBean
        binding.inGoodsInfo1.model = dataBean
        binding.inOrderInfo.apply {
            //支付方式
            if ("FB_PAY" != dataBean.payType) tvPaymentValue.setText(R.string.str_other)
            //留言
            if (TextUtils.isEmpty(dataBean.consumerMsg)) {
                tvLeaveMessage.visibility = View.GONE
                tvLeaveMessageValue.visibility = View.GONE
            }
            model = dataBean
        }
        binding.inBottom.apply {
            model = dataBean
        }
        this.dataBean = dataBean
        showCounpon()
        showHaggle()
        showCopy()
    }

    private fun topRefundShow(dataBean: OrderItemBean,needShow: Boolean=true) {

        binding.inRefund.conRefundProgress.setOnClickListener {
            JumpUtils.instans?.jump(124, dataBean.mallMallOrderId)
        }
        if(needShow){
            binding.inRefund.conRefundProgress.visibility = View.VISIBLE
            binding.inAddress.conAddress.visibility = View.GONE
        }else{
            binding.inRefund.conRefundProgress.visibility = View.GONE
            binding.inAddress.conAddress.visibility = View.VISIBLE
        }

        binding.tvOrderRemainingTime.text = dataBean.statusDesc
        dataBean.refundStatus?.let {
            viewModel.StatusEnum(
                "MallRefundStatusEnum",
                it,
                binding.inRefund.tvStatus
            )
        }
        binding.inRefund.tvTips.text = "退款进度"
    }

    private fun showCopy() {
        binding.inOrderInfo.tvCopy.setOnClickListener {
            MTextUtil.copystr(this, dataBean.orderNo)
        }

    }

    // 是否展示确认收货
    private fun showGetShop(isGet: Boolean) {
        if (isGet) {
            binding.inSaleBottom.btnOrderShopGet.isSelected = true
            binding.inSaleBottom.btnOrderShopGet.visibility = View.VISIBLE
            binding.inSaleBottom.btnOrderShopGet.setOnClickListener {
                confirmGoods()
            }
        } else {
            binding.inSaleBottom.btnOrderShopGet.visibility = View.GONE
        }

    }

    private fun showHaggle() {
        when (dataBean.busSource) {
            "HAGGLE" -> {
                binding.inGoodsInfo1.grHaggle.visibility = View.VISIBLE
                binding.inGoodsInfo1.tvHaggleMoney.text =
                    "-".plus(WCommonUtil.getRMB(dataBean.haggleDiscount))
            }
            else -> {
                binding.inGoodsInfo1.grHaggle.visibility = View.GONE
            }
        }
    }

    // 优惠券是否展示
    private fun showCounpon() {
        if (TextUtils.isEmpty(dataBean.couponDiscount)) {
            binding.inGoodsInfo1.grCoupon.visibility = View.GONE
        } else {
            if (dataBean.couponDiscount.toInt() > 0) {
                binding.inGoodsInfo1.grCoupon.visibility = View.VISIBLE
                binding.inGoodsInfo1.tvCouponMoney.text ="-".plus(WCommonUtil.getRMBBigDecimal(dataBean.couponDiscount))
            } else {
                binding.inGoodsInfo1.grCoupon.visibility = View.GONE
            }
        }
    }


    fun showBuyAgain() {
        binding.inSaleBottom.btnOrderBuyAgain.visibility = View.VISIBLE
        binding.inSaleBottom.btnOrderBuyAgain.setOnClickListener {


        }
        binding.inSaleBottom.btnOrderComment.visibility = View.GONE
        binding.inSaleBottom.btnOrderExpress.visibility = View.GONE
        binding.inSaleBottom.btnOrderInvoice.visibility = View.GONE


    }

    // 订单状态相关展示
    private fun BottomBShow() {
        binding.inBottom.layoutBottom.visibility = View.GONE
        binding.inSaleBottom.layoutBottom.visibility = View.VISIBLE
    }

    private fun BottomGon() {
        binding.inBottom.layoutBottom.visibility = View.GONE
        binding.inSaleBottom.layoutBottom.visibility = View.GONE
    }

    // 支付相关按钮展示
    private fun topBShow() {
        binding.inBottom.layoutBottom.visibility = View.VISIBLE
        binding.inSaleBottom.layoutBottom.visibility = View.GONE
    }

    /**
     * 订单关闭弹窗
     * */
    private fun createCloseOrderPop() {
        PublicPop(this).apply {
            showPopupWindow(context.getString(R.string.str_orderClosed), null, null)
            onDismissListener = object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    viewModel.getOrderDetail(orderNo)
                }
            }
        }
    }

    /**
     * 是否可申请售后 待发货、待收货、待评价、已完成时可以申请
     * */
    private fun isApplyRefund(dataBean: OrderItemBean) {
        //需要判断是否可以申请退货
        binding.inBottom.btnOrderCancle.apply {
            when {
                //可申请售后
                "YES" == dataBean.canApplyServiceOfAfterSales -> {
                    val orderStatus = dataBean.orderStatus
                    if ("WAIT_SEND" == orderStatus || (context.getString(R.string.str_onceAgainToBuy) == binding.inBottom.btnOrderConfirm.text && "2" == dataBean.busSourse)) binding.inBottom.btnOrderConfirm.visibility =
                        View.GONE
                    visibility = View.VISIBLE
                    setText(R.string.str_applyRefund)
                }
                //售后已处理
                "AFERT_SALE_FINISH" == dataBean.orderStatus -> {
                    visibility = View.VISIBLE
                    setText(R.string.str_contactCustomerService)
                    binding.inBottom.btnOrderConfirm.visibility = View.GONE
                    binding.tvOrderRemainingTime.setText(R.string.prompt_refundComplete)
                }
            }
        }
    }

    private fun localAddressObserve(addressInfoJson: String) {
        Gson().fromJson(addressInfoJson, ShopAddressInfoBean::class.java).apply {
            //更新收货地址
            val addressInfo = getAddress()
            dataBean.mallMallOrderId?.let {
                viewModel.updateAddressByOrderNoV2(it, addressId, object :
                    OnPerformListener {
                    override fun onFinish(code: Int) {
                        dataBean.addressInfo = addressInfo
                        dataBean.addressId = addressId
                        addressInfo.let {
                            val orderReceiveAddress = OrderReceiveAddress(
                                addressId.toString(),
                                addressInfo,
                                phone,
                                consignee
                            )
                            resetAddress(orderReceiveAddress)
                        }

                    }
                })
            }
        }
    }

    fun resetAddress(orderReceiveAddress: OrderReceiveAddress) {
        binding.inAddress.tvUserInfo.text = orderReceiveAddress.getUserInfo()
        binding.inAddress.tvLocationInfo.text = orderReceiveAddress.addressName

    }

    private fun bindingAddressInfo(addressInfo: OrderReceiveAddress, isUpdate: Boolean = false) {
        updateAddressInfo(addressInfo)
    }

    private fun updateAddressInfo(item: OrderReceiveAddress) {
        item.apply {
//            binding.inAddress.addressInfo = this
//            dataBean.addressId = addressId.toInt()
//            dataBean.addressInfo = addressName
            resetAddress(item)
        }
    }

    fun showShoppingInfo(localDataBean: OrderItemBean) {
        orderDetailsItemV2Adapter.orderNo = localDataBean.orderNo
        orderDetailsItemV2Adapter.orderStatus = localDataBean.orderStatus
        orderDetailsItemV2Adapter.setList(localDataBean.skuList)
    }

    /**
     * 取消订单、申请退货
     * */
    private fun cancelOrder() {
        when (binding.inBottom.btnOrderCancle.text) {
            getString(R.string.str_cancelOrder) -> {
                control.cancelOrder(dataBean, object : OnPerformListener {
                    override fun onFinish(code: Int) {
                        timeCountControl?.cancel()
                        binding.inBottom.btnOrderCancle.visibility = View.GONE
                        viewModel.getOrderDetail(orderNo)
                    }
                })
            }
            getString(R.string.str_applyRefund), getString(R.string.str_contactCustomerService) -> {
                JumpUtils.instans?.jump(
                    11,
                    "{\"tagId\": \"28\",\"content\": \"订单号：${dataBean.orderNo}，${dataBean.orderStatusName}\\n（请不要修改订单号和订单状态）\"}"
                )//默认选中商城相关
//                control.applyRefund(dataBean,object :OnPerformListener{
//                    override fun onFinish(code: Int) {
//                        binding.inBottom.btnOrderCancle.visibility=View.GONE
//                        viewModel.getOrderDetail(orderNo)
//                    }
//                })
            }
        }

    }

    /**
     * 确认收货
     * */
    private fun confirmGoods() {
        control.confirmGoods(dataBean, object : OnPerformListener {
            override fun onFinish(code: Int) {
                viewModel.getOrderDetail(orderNo)
            }
        })
    }

    private fun confirmOrder() {
        when (binding.inBottom.btnOrderConfirm.text) {
            //再次购买
            getString(R.string.str_onceAgainToBuy) -> control.onceAgainToBuy(dataBean)
            //评价
            getString(R.string.str_eval) -> OrderEvaluationActivity.start(orderNo)
            //确认收货
            getString(R.string.str_confirmGoods) -> confirmGoods()
            //立即支付
            getString(R.string.str_immediatePayment) -> control.toPay(dataBean)
        }
    }

    fun onClick(v: View) {
        if (!::dataBean.isInitialized) return
        when (v.id) {
            //复制物流信息
            R.id.tv_copy -> MTextUtil.copystr(this, dataBean.courierNo)
            //取消订单
            R.id.btn_order_cancle -> cancelOrder()
            //支付、确认收货、评价、再次购买
            R.id.btn_order_confirm -> confirmOrder()
            //修改收货地址
            R.id.img_right, R.id.tv_userInfo, R.id.tv_locationInfo -> if ("WAIT_PAY" == dataBean.orderStatus) JumpUtils.instans?.jump(
                20,
                "1"
            )
        }
    }

    /**
     * 点击地址列表的回调监听
     * */
    private fun addLiveDataBus() {
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java)
            .observe(this@OrderDetailsV2Activity, {
                it?.let {
                    // TODO 更换地址。
                    localAddressObserve(it)
                }
            })
    }

    fun showTotalTag(text: AppCompatTextView?, item: OrderItemBean) {
        if (TextUtils.isEmpty(item.payFb)) {
            showZero(text, item)
            return
        }
        item.payFb?.let { // 福币为0
            if (it.toInt() <= 0) {
                showZero(text, item)
                return
            }
        }
        val fbNumber = item.payFb

        val starStr = "合计: "
        val str = if (TextUtils.isEmpty(item.payRmb)) {
            "$starStr[icon] ${item.payFb}"
        } else {
            "$starStr[icon] ${item.payFb}+￥${item.payRmb}"
        }
        //先设置原始文本
        text?.text = str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text?.post { //获取第一行的宽度
            val stringBuilder: StringBuilder = StringBuilder(str)
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(this, R.mipmap.question_fb)
            drawable?.apply {
                val imageSpan = CustomImageSpanV2(this)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val strLength = spannableString.length
                val numberLength = fbNumber?.length
                val startIndex = strLength - numberLength!! - 1
                spannableString.setSpan(
                    imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text.text = spannableString
            }
        }
    }

    // 开票状态
    fun showInvoiceState(localDataBean: OrderItemBean) { // 开票状态
        localDataBean.invoiced?.let { invoice ->
            when (invoice) {
                "NOT_BEGIN" -> {// 未申请
                    binding.inSaleBottom.btnOrderInvoice.text = "申请发票"
                    binding.inSaleBottom.btnOrderInvoice.setOnClickListener {
                        if (dataBean != null) {
                            dataBean.addressInfo = localDataBean.orderReceiveAddress.addressName
                            dataBean.addressId = localDataBean.orderReceiveAddress.addressId.toInt()
                        }
                        localDataBean.mallMallOrderId?.let {
                            val invoiceInfo = InvoiceInfo(
                                mallMallOrderId = it,
                                mallMallOrderNo = localDataBean.orderNo,
                                invoiceRmb = localDataBean.getRMBExtendsUnit()
                            )
                            InvoiceActivity.start(invoiceInfo)
                        }
                    }
                }
                "ON_GOING" -> {// 已申请未开票
                    binding.inSaleBottom.btnOrderInvoice.text = "查看发票"
                    binding.inSaleBottom.btnOrderInvoice.setOnClickListener {
                        JumpUtils.instans?.jump(123, localDataBean.orderNo)

                    }
                }
                "ENDED" -> { // 已开票
                    binding.inSaleBottom.btnOrderInvoice.text = "查看发票"
                }
            }
            if (!TextUtils.isEmpty(localDataBean.fb)) {
                localDataBean.fb?.let {
                    if (it.toInt() > 0) {
                        binding.inSaleBottom.btnOrderInvoice.visibility = View.VISIBLE
                    } else {
                        binding.inSaleBottom.btnOrderInvoice.visibility = View.GONE
                    }
                }
            } else {
                binding.inSaleBottom.btnOrderInvoice.visibility = View.GONE
            }
        }
    }

    fun showRefund(localDataBean: OrderItemBean) {
        binding.inSaleBottom.btnOrderRefund.visibility = View.VISIBLE
        binding.inSaleBottom.btnOrderRefund.setOnClickListener {
            // 申请退款 0 是整单退, 1 是退单个商品
            val gson = Gson()
            val refundBean =
                RefundBean(
                    localDataBean.orderNo,
                    localDataBean.payFb,
                    localDataBean.payRmb,
                    "allOrderRefund"
                )
            val refundJson = gson.toJson(refundBean)
            refundJson.toString().logE()
            JumpUtils.instans?.jump(121, refundJson)
        }
    }

    // 物流状态
    fun showExpress(localDataBean: OrderItemBean, needShow: Boolean) {
        if (needShow) {
            binding.inSaleBottom.btnOrderExpress.visibility = View.VISIBLE
        } else {
            binding.inSaleBottom.btnOrderExpress.visibility = View.GONE
        }
        binding.inSaleBottom.btnOrderExpress.text = "查看物流"
        binding.inSaleBottom.btnOrderExpress.setOnClickListener {
            localDataBean.historyPackage?.let { hs ->
                if (hs == "NO") {
                    if (localDataBean.packageJump == null) {
                        MultiplePackageActivity.start(localDataBean.orderNo)
                    } else {
                        localDataBean.packageJump?.apply {
                            JumpUtils.instans?.jump(jumpCode, jumpVal)
                        }
                    }

                } else {
                    startActivity(
                        Intent(
                            this@OrderDetailsV2Activity,
                            NoLogisticsInfoActivity::class.java
                        )
                    )
                }
            }
        }

    }

    // 评价
    fun showComment(localDataBean: OrderItemBean, isNextComment: Boolean) {
        if (isNextComment) {
            if (localDataBean.canReview == "YES") {
                binding.inSaleBottom.btnOrderComment.text = "追加评价"
                binding.inSaleBottom.btnOrderComment.isSelected = false
            }
        } else {
            binding.inSaleBottom.btnOrderComment.text = "评价"
            binding.inSaleBottom.btnOrderComment.isSelected = true
        }
        binding.inSaleBottom.btnOrderComment.visibility = View.VISIBLE
        binding.inSaleBottom.btnOrderShopGet.visibility = View.GONE
        binding.inSaleBottom.btnOrderComment.setOnClickListener {
            if (localDataBean.canReview == "YES") {
                PostEvaluationActivity.start(true, localDataBean)
            } else {
                PostEvaluationActivity.start(localDataBean.orderNo)
            }
        }
    }


    fun showZero(text: AppCompatTextView?, item: OrderItemBean) {
        val tagName = item.payRmb
        //先设置原始文本
        text?.text = "合计".plus("  ￥${tagName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }
}