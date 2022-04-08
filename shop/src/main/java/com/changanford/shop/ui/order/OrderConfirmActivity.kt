package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.*
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toast
import com.changanford.common.web.AndroidBug5497Workaround
import com.changanford.common.wutil.WCommonUtil
import com.changanford.common.wutil.wLogE
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.ConfirmOrderGoodsInfoAdapter
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged
import com.changanford.shop.utils.WConstant
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 订单确认
 */
@Route(path = ARouterShopPath.OrderConfirmActivity)
class OrderConfirmActivity:BaseActivity<ActOrderConfirmBinding, OrderViewModel>(){
    companion object{
        fun start(goodsInfo:String) {
            JumpUtils.instans?.jump(109,goodsInfo)
        }
        fun start(dataBean:GoodsDetailBean) {
//            val bean=ConfirmOrderBean(orderConfirmType=orderConfirmType, dataList = arrayListOf(dataBean))
            JumpUtils.instans?.jump(109,Gson().toJson(dataBean))
        }
        /**
         * [orderConfirmType]确认订单来源 0详情 1购物车
        * */
        fun start(listBean:ArrayList<GoodsDetailBean>) {
//            val bean= ConfirmOrderBean(orderConfirmType=orderConfirmType, dataList = listBean)
            JumpUtils.instans?.jump(109,Gson().toJson(listBean))
        }
    }
    private lateinit var infoBean: ConfirmOrderBean
//    private lateinit var dataBean:GoodsDetailBean
    private var isClickSubmit=false
//    private var spuPageType=""//商品类型
    private var dataListBean:ArrayList<GoodsDetailBean>?=null
    private val goodsInfoAdapter by lazy { ConfirmOrderGoodsInfoAdapter() }
    private val rbPayWayArr by lazy { arrayListOf(binding.inPayWay.rbFbAndRmb,binding.inPayWay.rbRmb,binding.inPayWay.rbCustom) }
    private var maxUseFb=0//本次最大可使用福币 默认等于用户余额
    private var totalPayFb:Int=0//支付总额 福币
    private var minRmbProportion:Float=0f//最低使用人民币比例
    private var minRmb="0"
    private var payFb:String?="0"//福币支付额度
    private var payRmb:String?="0"//人民币支付额度
    private var orderConfirmType=0//确认订单来源 0商品详情 1购物车
    private var isAgree:Boolean=false//是否同意协议
    private var couponsItem:CouponsItemBean?=null
    private var createOrderBean:CreateOrderBean?=null
    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        binding.topBar.setActivity(this)
        val goodsInfo=intent.getStringExtra("goodsInfo")
        "goodsInfo:$goodsInfo".wLogE("okhttp")
        if(TextUtils.isEmpty(goodsInfo)){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        if(goodsInfo!!.startsWith("[")){
            infoBean=ConfirmOrderBean().apply {
                dataList = Gson().fromJson(goodsInfo, object : TypeToken<ArrayList<GoodsDetailBean?>?>() {}.type)
                dataListBean=dataList
                orderConfirmType=1//传来的是集合则表示从购物车过来
            }
        }else if(goodsInfo.startsWith("{")){
            infoBean=ConfirmOrderBean().apply {
                orderConfirmType=0
                val itemBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
                dataList= arrayListOf(itemBean)
                dataListBean= dataList
            }
        }
        orderConfirmType=infoBean.orderConfirmType?:0
        initObserve()
        edtCustomOnTextChanged()
        formattingData()
    }
    /**
     * 格式化数据
    * */
    private fun formattingData(){
        val skuItems= arrayListOf<ConfirmOrderInfoBean>()
        var totalBuyNum=0
        var totalOriginalFb=0
        dataListBean?.forEach {
            it.getRMB()
            val spuPageType=it.spuPageType
            val skuItem=ConfirmOrderInfoBean(skuId = it.skuId, num = it.buyNum, vin = it.vinCode,
                mallMallHaggleUserGoodsId = it.mallMallHaggleUserGoodsId, carModel = it.models)
            skuItem.initBean(spuPageType)
            skuItems.add(skuItem)
            totalBuyNum+=it.buyNum
            //单价（原价）
            val originalPrice=(it.orginPrice?:it.fbPrice).toInt()
            //原总商品价 单价*购买数量
            totalOriginalFb+=originalPrice*it.buyNum
            //本条数据为维保商品
            if(WConstant.maintenanceType==spuPageType&&TextUtils.isEmpty(infoBean.vinCode)){
                infoBean.vinCode=it.vinCode
                infoBean.models=it.models
            }
        }
        infoBean.totalBuyNum=totalBuyNum
        infoBean.totalOriginalFb=totalOriginalFb
        bindInfo()
        //获取优惠券信息
        viewModel.confirmOrder(orderConfirmType,skuItems)
    }
    override fun initData() {}

    private fun initObserve(){
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this) {
            it?.let {
                bindingAddress(Gson().fromJson(it, AddressBeanItem::class.java))
            }
        }
        //优惠券、skuItems
        viewModel.createOrderBean.observe(this){
            createOrderBean=it
            infoBean.freightPrice=it?.freight?:"0.00"
            binding.inOrderInfo.apply {
                if(it!=null){
                    infoBean.fbBalance=it.totalIntegral
                    minRmbProportion=it.getRmbBfb()//得到人民币最低使用百分比
//                    tvFreightValue.setText(infoBean.freightPrice)
                }
            }
            formattingCouponsData()
        }
        //下单回调
        viewModel.orderInfoLiveData.observe(this) {
            isClickSubmit = false
            PayConfirmActivity.start(it.orderNo)
            LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK, String::class.java)
                .postValue("$orderConfirmType")
            this.finish()
        }
        //选择优惠券回调
        LiveDataBus.get().with(LiveDataBusKey.COUPONS_CHOOSE_BACK, CouponsItemBean::class.java).observe(this) {
            bindCoupon(it)
        }
    }
    /**
     * 格式优惠券信息-筛选有用 和找出最大优惠
    * */
    private fun formattingCouponsData(){
        val couponListBean=createOrderBean?.coupons
        val skuItems=createOrderBean?.skuItems
        if(couponListBean!=null&&couponListBean.size>0){
            //判断每个优惠券是否可用
            for ((i,item)in couponListBean.withIndex()){
                item.isAvailable=false
                var totalPrice:Long=0//满足优惠券的总价
                item.mallMallSkuIds?.forEach{skuId->
                    //查询skuId是否在订单中
                    skuItems!!.find { skuId== it.skuId }?.apply {
                        //计算总价=单价福币*数量
                        totalPrice+=((unitPriceFb?:"0").toLong()*num)
                    }
                }
                //该券满足优惠条件
                if(totalPrice>=item.getRmbToFb()){
                    //标注该券可用
                    item.isAvailable=true
                    //计算实际优惠
                    val discountsFb:Long=when(item.discountType){
                        //折扣
                        "DISCOUNT"->{
                            //折扣金额 福币
                            val discountAmountFb=item.discountAmount(totalPrice)
                            //最大折扣
                            if(discountAmountFb<=(item.couponMoney*100))discountAmountFb else item.getRmbToFb(item.couponMoney)
                        }
                        //满减和立减
                        else -> item.getRmbToFb(item.couponMoney)
                    }
                    item.discountsFb=discountsFb
                }else item.discountsFb=item.getRmbToFb(item.couponMoney)
                couponListBean[i]=item
            }
            //优惠金额排序（从小到大） 然后倒叙（则结果是从大到小）
            val sortList=couponListBean.sortedWith(compareBy { it.discountsFb}).reversed()
                //.sortedWith(compareBy { it.validityEndTime})
            //根据条件将优惠分成两份
            val (match, rest) = sortList.partition { it.isAvailable }
            val newList= arrayListOf<CouponsItemBean>()
            newList.addAll(match)
            newList.addAll(rest)
            createOrderBean?.coupons=newList
            bindCoupon(newList[0])
        }else bindCoupon(null)

    }
    /**
     * 绑定优惠券和支付信息
    * */
    @SuppressLint("SetTextI18n")
    private fun bindCoupon(itemCoupon:CouponsItemBean?=null){
        couponsItem=itemCoupon
        var couponsAmount="0"//福币
        binding.inOrderInfo.tvCouponsValue.apply {
            val item0=if(createOrderBean?.coupons!=null&&createOrderBean?.coupons?.size!! >0)createOrderBean?.coupons?.get(0) else null
            if(item0==null || !item0.isAvailable){
//                isEnabled=false
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity,R.color.color_99))
                setText(R.string.str_temporarilyNoUse)
            }else if(itemCoupon==null){
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity,R.color.color_33))
                setText(R.string.str_pleaseSelectCoupons)
            } else{
                couponsAmount="${itemCoupon.discountsFb}"
                isEnabled=true
                setTextColor(ContextCompat.getColor(this@OrderConfirmActivity,R.color.color_33))
                setText(WCommonUtil.getRMB(couponsAmount))
            }
        }
        //总共支付 (商品金额+运费)
        totalPayFb=infoBean.getTotalPayFbPrice(couponsAmount,true)
        infoBean.totalPayFb=totalPayFb
        binding.inOrderInfo.tvTotal.setHtmlTxt(WCommonUtil.getRMB("$totalPayFb"),"#00095B")
        //最少使用多少人民币（fb）=总金额*最低现金比 向上取整
        var minFb:Int=WCommonUtil.getHeatNumUP("${totalPayFb*minRmbProportion}",0).toInt()
        val maxFb:Int=totalPayFb -minFb
        //最大可使用福币
        maxUseFb=if((infoBean.fbBalance?:0)>=maxFb)maxFb else {
            minFb= totalPayFb-maxUseFb
            infoBean.fbBalance?:0
        }
        binding.inPayWay.apply {
            minRmb=getRMB("$minFb")
            minRmb=if(minRmb.toFloat()>0f)"+¥$minRmb" else ""
            rbFbAndRmb.text="$maxUseFb$minRmb"
            rbRmb.text = "¥${getRMB("$totalPayFb")}"
        }
        "totalPayFb:$totalPayFb>>>minRmbProportion:$minRmbProportion>>>>minFb:$minFb>>>>maxFb:$maxFb>>>maxUseFb:$maxUseFb>>>totalOriginalFb:${infoBean.totalOriginalFb}".wLogE("okhttp")
        initPayWay()
    }
    private fun bindInfo(){
//        maxUseFb=infoBean.fbBalance?:0
        //地址信息
        viewModel.addressList.observe(this) { addressList ->
            //默认获取地址列表的默认收货地址
            val item: AddressBeanItem? = addressList?.find { it.isDefault == 1 }
            bindingAddress(item)
        }
        val addressInfo=infoBean.addressInfo
        if(TextUtils.isEmpty(addressInfo))viewModel.getAddressList()
        else if(addressInfo!!.startsWith("{"))viewModel.addressList.postValue(arrayListOf(Gson().fromJson(addressInfo,AddressBeanItem::class.java)))

        //商品列表
        binding.inGoodsInfo.apply {
            recyclerView.adapter=goodsInfoAdapter
            goodsInfoAdapter.setList(dataListBean)
        }
        //维保商品
        manageMaintenance()

        //订单信息 商品金额运费
        binding.inOrderInfo.apply {
            tvAmountValue.text=WCommonUtil.getRMB("${infoBean.totalOriginalFb}")
        }
    }
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->submitOrder()
            //选择地址
            R.id.in_address->JumpUtils.instans?.jump(20,"1")
            //服务协议
            R.id.tv_agreement->JumpUtils.instans?.jump(1,MConstant.H5_SHOP_AGREEMENT)
            //协议勾选
            R.id.checkBox->{
                isAgree=binding.checkBox.isChecked
                updateBtnUi()
            }
            //福币+人民币支付
            R.id.rb_fbAndRmb->clickPayWay(0)
            //人民币支付
            R.id.rb_rmb->clickPayWay(1)
            //自定义支付
            R.id.rb_custom->clickPayWay(2)
            //选择优惠券
            R.id.tv_coupons_value->{
                createOrderBean?.apply {
                    ChooseCouponsActivity.start("${couponsItem?.couponId}_${couponsItem?.couponRecordId}",this)
                }
            }
        }
    }
    private fun submitOrder(){
        if(!isClickSubmit){
            isClickSubmit=true
            val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
            viewModel.createOrder(orderConfirmType = orderConfirmType, payFb = payFb, payRmb = payRmb, addressId = infoBean.addressId,
                consumerMsg =consumerMsg, skuItems =createOrderBean?.skuItems, couponId =couponsItem?.couponId, couponRecordId = couponsItem?.couponRecordId,
            freight =infoBean.freightPrice, payBfb =createOrderBean?.payBfb)
//            dataBean.apply {
//                viewModel.orderCreate(skuId,addressId,spuPageType,buyNum,consumerMsg,mallMallSkuSpuSeckillRangeId,mallMallHaggleUserGoodsId,vinCode = vinCode,mallMallWbVinSpuId=mallMallWbVinSpuId)
//            }
        }
        GlobalScope.launch {
            delay(3000L)
            isClickSubmit=false
        }
    }
    @SuppressLint("SetTextI18n")
    private fun bindingAddress(item:AddressBeanItem?){
        if(null==item){//未绑定地址
            infoBean.addressId=null
            binding.inAddress.tvAddress.setText(R.string.str_pleaseAddShippingAddress)
            binding.inAddress.tvAddressRemark.visibility=View.GONE
            binding.inBottom.btnSubmit.updateEnabled(false)
        }else{
            infoBean.addressId=item.addressId
            updateBtnUi()
            binding.inAddress.tvAddressRemark.visibility=View.VISIBLE
            binding.inAddress.tvAddress.text="${item.provinceName}${item.cityName}${item.districtName}${item.addressName}"
            binding.inAddress.tvAddressRemark.text="${item.consignee}   ${item.phone}"
        }
    }
    /**
     * 处理维保商品
    * */
    private fun manageMaintenance(){
        if(TextUtils.isEmpty(infoBean.vinCode))return
        binding.apply {
            //维保商品不需要收货地址
            inAddress.layoutAddress.visibility=View.GONE
            composeView.setContent { MaintenanceCompose() }
        }

    }
    /**
     * 维保商品信息
    * */
    @Composable
   private fun MaintenanceCompose(){
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 17.dp)) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(colorResource(R.color.color_F4)))
            for (i in 0..1){//0 vin码 1车型
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = if (0 == i) 19.dp else 29.dp)) {
                    Text(text = stringResource(if(0==i)R.string.str_vinCode else R.string.str_models),color= colorResource(R.color.color_33),fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp))
                    Text(text = if(0==i)infoBean.vinCode?:"" else infoBean.models?:"",color= colorResource(R.color.color_33),fontSize = 14.sp,overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
            }
        }
    }
    private fun initPayWay(){
        binding.inPayWay.apply {
            tvMaxUseFb.setText("$maxUseFb")
            when {
                maxUseFb>0 -> {
                    rbFbAndRmb.visibility=View.VISIBLE
                    rbCustom.visibility=View.VISIBLE
                    clickPayWay(0)
                }
                totalPayFb==0 -> {
                    rbFbAndRmb.visibility=View.VISIBLE
                    clickPayWay(0)
                }
                else -> {
                    if(maxUseFb==0)rbFbAndRmb.visibility=View.GONE
                    rbRmb.visibility=View.VISIBLE
                    clickPayWay(1)
                }
            }
        }
    }
    /**
     * 支付方式选择点击
     * [type]0 福币+人民币、1人民币、2自定义福币
    * */
    private fun clickPayWay(type:Int){
        for((index,it) in rbPayWayArr.withIndex()){
            it.isChecked= type==index
        }
        updatePayCustom()
        updateBtnUi()
    }
    /**
     * 自定义混合支付 福币输入监听
    * */
    private fun edtCustomOnTextChanged(){
        binding.inPayWay.apply {
            edtCustom.onTextChanged {
                val inputFb= it.s
                "输入结果：${it.before}>>>>${it.start}>>>>$inputFb".wLogE()
                if(!TextUtils.isEmpty(inputFb)){
                    //输入的福币超出可使用的范围
                    if(inputFb.toString().toInt()>maxUseFb){
                        edtCustom.setText("$maxUseFb")
                        edtCustom.setSelection(edtCustom.text.length)//光标移动到末尾
                        getString(R.string.str_hasMaxUseFb).toast()
                    }
                    calculateFbAndRbm()
                }else{
                    tvCustomRmb.text=""
                }
                updateBtnUi()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun calculateFbAndRbm(){
        binding.inPayWay.apply {
            val inputFb= edtCustom.text.toString()
            tvCustomFb.text=inputFb
            tvCustomRmb.text="+¥${getRMB("${totalPayFb-inputFb.toInt()}")}"
        }
    }
    /**
     * 获取支付组合额度
    * */
    @SuppressLint("SetTextI18n")
    private fun getPayLines():Boolean{
        binding.inPayWay.apply {
            when{
                //选中了混合支付
                rbFbAndRmb.isChecked->{
                    val str=rbFbAndRmb.text.toString()
                    if(str.contains("+¥")){
                        val splitArr=str.split("+¥")
                        payFb=splitArr[0]
                        payRmb=splitArr[1]
                    }else{
                        payFb=str
                        payRmb="0"
                    }
                }
                //选中了人民币支付
                rbRmb.isChecked->{
                    payFb="0"
                    payRmb=getRMB("$totalPayFb")
                }
                //选中了自定义混合支付
                rbCustom.isChecked->{
                    payFb=edtCustom.text.toString()
                    payRmb = if(!TextUtils.isEmpty(payFb)){
                        getRMB("${totalPayFb-(payFb?:"0").toInt()}")
                    }else {
                        payFb=null
                        null
                    }
                }
            }
        }
        val isPrice=!TextUtils.isEmpty(payFb)&&!TextUtils.isEmpty(payRmb)
        bindBottomPrice()
        return isPrice
    }
    private fun bindBottomPrice(){
        binding.inBottom.tvPayPrice.apply {
            var drawableStart=if(!TextUtils.isEmpty(payFb)&&payFb!!.toFloat()>0f)ContextCompat.getDrawable(context,R.mipmap.ic_shop_fb_42) else null
            val endStr=if(!TextUtils.isEmpty(payRmb)&&payRmb!!.toFloat()>0)"￥$payRmb" else ""
            text=if(drawableStart!=null&&!TextUtils.isEmpty(endStr))"$payFb+$endStr" else if(TextUtils.isEmpty(endStr)) payFb?:"" else endStr
            if(TextUtils.isEmpty(endStr))drawableStart=ContextCompat.getDrawable(context,R.mipmap.ic_shop_fb_42)
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart,null,null,null)
        }
    }
    /**
     * 将福币转换为人民币 1元=100福币
     * */
    private fun getRMB(fb:String?="$totalPayFb"):String{
        var rmbPrice="0"
        if(fb!=null){
            val fbToFloat=fb.toFloat()
            val remainder=fbToFloat%100
            rmbPrice = if(remainder>0) "${fbToFloat/100}"
            else "${fbToFloat.toInt()/100}"
        }
        "getRMB>fb:$fb>>rmbPrice:$rmbPrice".wLogE("okhttp")
        return rmbPrice
    }
    private fun updatePayCustom(){
        binding.inPayWay.apply {
            rbRmb.visibility=View.VISIBLE
            if(maxUseFb>0){
                val isCheck=rbCustom.isChecked
                if(isCheck){
                    rbCustom.visibility=View.INVISIBLE
                    layoutCustom.visibility=View.VISIBLE
                }else{
                    layoutCustom.visibility=View.GONE
                    rbCustom.visibility=View.VISIBLE
                }
            }else if(totalPayFb==0){//总价为0
                rbRmb.visibility=View.GONE
                payFb="0"
                rbFbAndRmb.visibility=View.VISIBLE
            }else{
                rbFbAndRmb.visibility=View.GONE
            }
        }
    }
    /**
     * 更新底部提交按钮状态
     * */
    private fun updateBtnUi(){
        infoBean.apply {
            val isPrice=getPayLines()
            if(!TextUtils.isEmpty(vinCode)){//维保商品
                binding.inBottom.btnSubmit.updateEnabled(isAgree&& (payFb?:"0").toFloat() <=fbBalance?:0&&isPrice)
            }else binding.inBottom.btnSubmit.updateEnabled(isAgree&&null!=addressId&& (payFb?:"0").toFloat() <=fbBalance?:0&&isPrice)
        }
    }
}