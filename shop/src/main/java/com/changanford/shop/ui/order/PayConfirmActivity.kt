package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.OrderInfoBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayWayBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.wutil.UnionPayUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.PayTimeCountControl
import com.changanford.shop.databinding.ShopActPayconfirmBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.view.TopBar
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 确认支付
 */
@Route(path = ARouterShopPath.PayConfirmActivity)
class PayConfirmActivity : BaseActivity<ShopActPayconfirmBinding, OrderViewModel>(),
    TopBar.OnBackClickListener, OnTimeCountListener {
    companion object {
        fun start(orderNo: String) {
            JumpUtils.instans?.jump(110, orderNo)
        }

        fun start(orderInfoBean: OrderInfoBean) {
            val bundle = Bundle()
            bundle.putParcelable("orderInfoBean", orderInfoBean)
            startARouter(ARouterShopPath.PayConfirmActivity, bundle, true)
        }
    }

    private var timeCountControl: PayTimeCountControl? = null
    private var orderInfoBean: OrderInfoBean? = null
    private var newOrderInfoBean: OrderInfoBean? = null
    private var dataBean: OrderItemBean? = null
    private var waitPayCountDown: Long = 1800//支付剩余时间 默认半小时
    private var isPaySuccessful = false//是否支付成功
    private var isClickSubmit = false
    private var orderNo: String? = null
    private var isFromOrder = false
    private var mSelectedTag = ""
    private var mPayType = 0 //0福币 1现金

    //    private var orderInfoBean: OrderInfoBean?=null
    override fun initView() {
        binding.topBar.setOnBackClickListener(this)
    }

    override fun initData() {
        orderNo = intent.getStringExtra("orderNo")
        newOrderInfoBean = intent.getParcelableExtra("orderInfoBean")
        if (TextUtils.isEmpty(orderNo)) {
            //兼容以前版本
            intent.getStringExtra("orderInfo")?.apply {
                if (this.startsWith("{")) {
                    orderInfoBean = Gson().fromJson(this, OrderInfoBean::class.java)
                    orderNo = orderInfoBean?.orderNo
                }
            }
        }
//        if (TextUtils.isEmpty(orderNo)) {
//            ToastUtils.reToast(R.string.str_parameterIllegal)
//            return
//        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.orderItemLiveData.observe(this) { orderItem ->
            dataBean = orderItem
            bindingData()
        }
        newOrderInfoBean?.let { orderBean ->
            viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this) {
                it?.let {
                    dataBean = OrderItemBean(
                        waitPayCountDown = (orderBean.waitPayDuration)?.div(1000),
                        payFb = orderBean.payFb,
                        payRmb = orderBean.payRmb,
                        payType = orderBean.payType.toString(),
                        totalIntegral = it.integral.toInt().toString(),
                        orderNo = orderBean.orderNo
                    )
                    isFromOrder = true
                    bindingOrderData()
                }
            }
        }
        orderNo?.let { viewModel.getOrderDetail(it) }
        //福币支付回调
        viewModel.responseData.observe(this) {
            ToastUtils.showLongToast(it.msg, this)
            isClickSubmit = false
            payResults(it.isSuccess)
        }
        //现金支付下单回调
        viewModel.payBackBeanLiveData.observe(this) {
            it?.apply {
                when {
                    aliPay != null -> UnionPayUtils.goUnionPay(this@PayConfirmActivity, 1, aliPay)
                    wxPay != null -> UnionPayUtils.goUnionPay(
                        this@PayConfirmActivity,
                        2,
                        Gson().toJson(wxPay)
                    )

                    uacPay != null -> UnionPayUtils.goUnionPay(this@PayConfirmActivity, 3, uacPay)
                }
            }
        }
        //银联-支付回调
        LiveDataBus.get().with(LiveDataBusKey.WEB_OPEN_UNION_PAY_BACK).observe(this) {
            when (it) {
                0 -> {//成功
                    payResults(true)
                }

                else -> {//1 失败 2 取消
                    payResults(false)
                }
            }
        }
        //微信支付结果
        LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).observe(this) {
            when (it) {
                0 -> {//成功
                    payResults(true)
                }

                1 -> {//失败
                    payResults(false)
                }

                2 -> {//取消
                    payResults(false)
                }
            }
        }
        //支付宝支付结果
        LiveDataBus.get().with(LiveDataBusKey.ALIPAY_RESULT).observe(this) {
            when (it) {
                true -> {
                    payResults(true)
                }

                false -> {
                    payResults(false)
                }
            }
        }
    }

    //订单直接过来的
    private fun bindingOrderData() {
        dataBean?.apply {
            if (waitPayCountDown == null || waitPayCountDown == 0L) {
                payResults(false)
                return
            }
            when (payType) {
                "0" -> {
                    binding.apply {
                        layoutPay.visibility = View.VISIBLE
                        btnSubmit.visibility = View.VISIBLE
                        composeView.visibility = View.GONE
                        model = dataBean
                        tvAccountPoints.setHtmlTxt(
                            getString(R.string.str_Xfb, totalIntegral),
                            "#1700f4"
                        )
                        //账户余额小于所支付额度 则余额不足
                        if (totalIntegral!!.toFloat() < (payFb
                                ?: "0").toFloat()
                        ) btnSubmit.setStates(8)
                        else btnSubmit.setStates(12)
                        val payCountDown =
                            waitPayCountDown ?: this@PayConfirmActivity.waitPayCountDown
                        if (payCountDown > 0) {
                            timeCountControl?.cancel()
                            timeCountControl = PayTimeCountControl(
                                payCountDown * 1000,
                                tv = binding.tvPayTime,
                                null,
                                this@PayConfirmActivity
                            )
                            timeCountControl?.start()
                        }
                    }
                    mPayType = 0
                }
                //现金支付和混合支付 使用银联支付
                else -> {
                    binding.apply {
                        layoutPay.visibility = View.INVISIBLE
                        btnSubmit.visibility = View.INVISIBLE
                        composeView.visibility = View.VISIBLE
                        composeView.setContent {
                            UnionPayCompose(dataBean, this@PayConfirmActivity)
                        }
                        mPayType = 1
                    }
                }
            }
        }
    }

    private fun bindingData() {
        dataBean?.apply {
            if (waitPayCountDown == null || waitPayCountDown == 0L) {
                payResults(false)
                return
            }
            when (payType) {
                "FB_PAY" -> {
                    binding.apply {
                        layoutPay.visibility = View.VISIBLE
                        btnSubmit.visibility = View.VISIBLE
                        composeView.visibility = View.GONE
                        model = dataBean
                        tvAccountPoints.setHtmlTxt(
                            getString(R.string.str_Xfb, totalIntegral),
                            "#1700f4"
                        )
                        //账户余额小于所支付额度 则余额不足
                        if (totalIntegral!!.toFloat() < (payFb
                                ?: "0").toFloat()
                        ) btnSubmit.setStates(8)
                        else btnSubmit.setStates(12)
                        val payCountDown =
                            waitPayCountDown ?: this@PayConfirmActivity.waitPayCountDown
                        if (payCountDown > 0) {
                            timeCountControl?.cancel()
                            timeCountControl = PayTimeCountControl(
                                payCountDown * 1000,
                                tv = binding.tvPayTime,
                                null,
                                this@PayConfirmActivity
                            )
                            timeCountControl?.start()
                        }
                    }
                    mPayType = 0
                }
                //现金支付和混合支付 使用银联支付
                else -> {
                    binding.apply {
                        layoutPay.visibility = View.INVISIBLE
                        btnSubmit.visibility = View.INVISIBLE
                        composeView.visibility = View.VISIBLE
                        composeView.setContent {
                            UnionPayCompose(dataBean, this@PayConfirmActivity)
                        }
                    }
                    mPayType = 1
                }
            }
        }

    }

    /**
     * isSuccessful支付成功、支付失败
     * */
    @SuppressLint("SetTextI18n")
    private fun payResults(isSuccessful: Boolean) {
        this.isPaySuccessful = isSuccessful
        binding.layoutPay.visibility = View.INVISIBLE
        binding.composeView.visibility = View.GONE
        binding.inPayResults.apply {
            model = dataBean
            layoutPayResults.visibility = View.VISIBLE
            if (isFromOrder) {
                if (dataBean?.payType != "0") tvPayResultsPrice.text = "共计￥${dataBean?.payRmb}"
                else tvPayResultsPrice.text = "共计${dataBean?.payFb}福币"
            } else {
                if (dataBean?.payType != "FB_PAY") tvPayResultsPrice.text =
                    "共计￥${dataBean?.payRmb}"
                else tvPayResultsPrice.text = "共计${dataBean?.payFb}福币"
            }
            tvPayResultsState.setText(if (isPaySuccessful) R.string.str_paySucces else R.string.str_payFailure)
            val dTop = ContextCompat.getDrawable(
                this@PayConfirmActivity,
                if (isPaySuccessful) R.mipmap.shop_pay_succes else R.mipmap.shop_pay_failure
            )
            tvPayResultsState.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                dTop,
                null,
                null
            )
        }
        binding.btnSubmit.visibility = View.VISIBLE
        if (isPaySuccessful) {
            binding.btnSubmit.setStates(13)
        } else {
            binding.btnSubmit.setStates(14)
        }
    }

    fun btnSubmit(v: View) {
        dataBean?.apply {
            if (!isClickSubmit) {
                isClickSubmit = true
                when (binding.btnSubmit.text) {
                    getString(R.string.str_payConfirm), getString(R.string.str_Repayment) -> {
                        if (isFromOrder) {
                            if (mPayType == 1) {
                                payRmb?.let {
                                    viewModel.rmbPayBatch(
                                        orderNo,
                                        it,
                                        mSelectedTag
                                    )
                                }
                            } else {
                                payFb?.let { viewModel.fbPayBatch(orderNo, it) }
                            }
                        } else {
                            if (mPayType == 1) {
                                viewModel.rmbPay(orderNo, mSelectedTag)
                            } else {
                                viewModel.fbPay(orderNo)
                            }
                        }
                    }

                    getString(R.string.str_order_list) -> {
                        JumpUtils.instans?.jump(52)
                        if (isPaySuccessful) this@PayConfirmActivity.finish()
                    }

                    getString(R.string.str_orderDetails) -> {
                        val jumpDataType = dataBean?.jumpDataType
                        when {
                            null != jumpDataType -> {
                                JumpUtils.instans?.jump(jumpDataType, dataBean?.jumpDataValue)
                            }

                            "3" == busSourse || "WB" == busSource -> {//维保商品订单详情
                                JumpUtils.instans?.jump(
                                    1,
                                    String.format(MConstant.H5_SHOP_MAINTENANCE, orderNo)
                                )
                            }

                            else -> OrderDetailsV2Activity.start(orderNo)
                        }
                        isClickSubmit = false
                        if (isPaySuccessful) this@PayConfirmActivity.finish()
                    }
                }
            }
            GlobalScope.launch {
                delay(3000L)
                isClickSubmit = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeCountControl?.cancel()
    }

    override fun onBackClick() {
        //确认支付 返回到订单详情
        if (binding.btnSubmit.text == getString(R.string.str_payConfirm)) {
            dataBean?.apply {
                val jumpDataType = dataBean?.jumpDataType
                when {
                    null != jumpDataType -> {
                        JumpUtils.instans?.jump(jumpDataType, dataBean?.jumpDataValue)
                    }

                    "3" == busSourse || "WB" == busSource -> {//维保商品订单详情
                        JumpUtils.instans?.jump(
                            1,
                            String.format(MConstant.H5_SHOP_MAINTENANCE, orderNo)
                        )
                    }

//                    else -> OrderDetailsV2Activity.start(orderNo)
                }
            }
        }
        this.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick()
        }
        return false
    }

    /**
     * 倒计时 结束回调
     * */
    override fun onFinish() {
        payResults(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.apply { UnionPayUtils.payOnActivityResult(this) }
    }

    /**
     * 确认支付-银联支付
     * */
    @Composable
    private fun UnionPayCompose(dataBean: OrderItemBean? = null, listener: OnTimeCountListener) {
        val timeStr = "00:00:00"
        //剩余支付时间
        val countdown = remember { mutableStateOf(timeStr) }
        val payWayArr = ArrayList<PayWayBean>()
        val nameArr = arrayOf(
            stringResource(R.string.str_wxPay),
            stringResource(R.string.str_zfbPay),
//            stringResource(R.string.str_unionPayCloudFlashPayment)
        )
        val payTypeArr = arrayOf("2", "1", "3")
        val iconArr = arrayOf(
            painterResource(R.mipmap.ic_shop_wx),
            painterResource(R.mipmap.ic_shop_zfb),
//            painterResource(R.mipmap.ic_shop_ysf)
        )
        for ((i, it) in nameArr.withIndex()) {
            payWayArr.add(
                PayWayBean(
                    payType = payTypeArr[i],
                    isCheck = remember { mutableStateOf(0 == i) },
                    payWayName = it,
                    icon = iconArr[i]
                )
            )
        }
        val selectedTag = remember { mutableStateOf("0") }
        dataBean?.apply {
            val payCountDown = waitPayCountDown ?: 0
            if (payCountDown > 0 && timeCountControl == null) {
                timeCountControl = PayTimeCountControl(
                    payCountDown * 1000,
                    tv = null,
                    countdownCompose = countdown,
                    object : OnTimeCountListener {
                        override fun onFinish() {
                            countdown.value = timeStr
                            listener.onFinish()
                        }
                    })
                timeCountControl?.start()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = colorResource(R.color.color_F4))
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(26.dp))
                    //支付金额
                    Text(
                        text = "￥$payRmb",
                        fontSize = 28.sp,
                        color = colorResource(R.color.color_33)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    //剩余支付时间
                    Text(
                        text = "${stringResource(R.string.str_remainingTimePayment)}${countdown.value}",
                        fontSize = 13.sp,
                        color = colorResource(R.color.color_33)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    Divider(color = colorResource(R.color.color_F5), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(5.dp))
                    for ((i, item) in payWayArr.withIndex()) {
                        item.apply {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 15.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        selectedTag.value = payType
                                    }) {
                                Image(
                                    painter = icon ?: painterResource(R.mipmap.ic_shop_wx),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = payWayName ?: "",
                                    color = colorResource(R.color.color_33),
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Image(
                                    painter = painterResource(if (selectedTag.value == payType) R.mipmap.shop_order_cb_1 else R.mipmap.shop_order_cb_0),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp), contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            if (isFromOrder) {
                                payRmb?.let {
                                    viewModel.rmbPayBatch(
                                        orderNo,
                                        it,
                                        selectedTag.value
                                    )
                                }
                            } else {
                                viewModel.rmbPay(orderNo, selectedTag.value)
                            }
                            mSelectedTag = selectedTag.value
                        },
                        enabled = selectedTag.value != "0" && countdown.value != timeStr,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(if (selectedTag.value != "0" && countdown.value != timeStr) R.color.color_1700f4 else R.color.color_DD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text(
                            stringResource(R.string.str_payConfirm),
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}