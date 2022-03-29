package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
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
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toast
import com.changanford.common.web.AndroidBug5497Workaround
import com.changanford.common.wutil.WCommonUtil
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.ConfirmOrderGoodsInfoAdapter
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged
import com.changanford.shop.utils.WConstant
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
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
            val listBean= ArrayList<GoodsDetailBean>()
            listBean.add(dataBean)
            JumpUtils.instans?.jump(109,Gson().toJson(listBean))
        }
        fun start(listBean:ArrayList<GoodsDetailBean>) {
            JumpUtils.instans?.jump(109,Gson().toJson(listBean))
        }
    }
    private lateinit var dataBean:GoodsDetailBean
    private var isClickSubmit=false
    private var spuPageType=""//商品类型
    private lateinit var dataListBean:ArrayList<GoodsDetailBean>
    private val goodsInfoAdapter by lazy { ConfirmOrderGoodsInfoAdapter() }
    private val rbPayWayArr by lazy { arrayListOf(binding.inPayWay.rbFbAndRmb,binding.inPayWay.rbRmb,binding.inPayWay.rbCustom) }
    private var maxUseFb=0//本次最大可使用福币 默认等于用户余额
    private var totalPayFb:Int=0//支付总额 福币
    private var minRmbProportion:Float=0.1f//最低使用人民币比例
    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        binding.topBar.setActivity(this)
        val goodsInfo=intent.getStringExtra("goodsInfo")
        if(TextUtils.isEmpty(goodsInfo)){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        if(MConstant.isShowLog)Log.e("okhttp","goodsInfo:$goodsInfo")
        if(goodsInfo!!.startsWith("[")){
            val dataList: ArrayList<GoodsDetailBean> = Gson().fromJson(goodsInfo, object : TypeToken<ArrayList<GoodsDetailBean?>?>() {}.type)
            dataListBean=dataList
            dataBean=dataList[0]
        }else if(goodsInfo.startsWith("{")){
            dataBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
            dataListBean= ArrayList()
            dataListBean.add(dataBean)
        }
        spuPageType=dataBean.spuPageType
        dataBean.isAgree=false
        initLiveDataBus()
        edtCustomOnTextChanged()
    }

    override fun initData() {
        maxUseFb=dataBean.acountFb
//        binding.inGoodsInfo.addSubtractView.apply {
//            val stock=dataBean.stock
//            val limitBuyNum:Int=dataBean.getLimitBuyNum()
//            var isLimitBuyNum=false//是否限购
//            val max: Int =if(limitBuyNum in 1..stock) {
//                isLimitBuyNum=true
//                limitBuyNum
//            } else stock
//            setMax(max,isLimitBuyNum)
//            setNumber(dataBean.buyNum,false)
//            setIsUpdateBuyNum(dataBean.isUpdateBuyNum)
//            numberLiveData.observe(this@OrderConfirmActivity) {
//                dataBean.buyNum = it
//                bindingBaseData()
//            }
//        }
        bindInfo()
        //非维保商品 需要选择地址
        if(WConstant.maintenanceType!=spuPageType){
            viewModel.addressList.observe(this) { addressList ->
                //默认获取地址列表的默认收货地址
                val item: AddressBeanItem? = addressList?.find { it.isDefault == 1 }
                bindingAddress(item)
            }
            val addressInfo=dataBean.addressInfo
            if(TextUtils.isEmpty(addressInfo))viewModel.getAddressList()
            else viewModel.addressList.postValue(arrayListOf(Gson().fromJson(addressInfo,AddressBeanItem::class.java)))
        }
        viewModel.orderInfoLiveData.observe(this) {
            isClickSubmit = false
            val source = it.source
            if (source != "0") it.source = if (dataBean.spuPageType == "2") "2" else dataBean.source
            PayConfirmActivity.start(it.orderNo)
            LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK, String::class.java)
                .postValue(it.source)
            this.finish()
        }
        bindingBaseData()
    }
    private fun bindInfo(){
        binding.inGoodsInfo.apply {
            recyclerView.adapter=goodsInfoAdapter
            goodsInfoAdapter.setList(dataListBean)
        }

    }
    @SuppressLint("StringFormatMatches", "SetTextI18n")
    private fun bindingBaseData(){
        //秒杀情况下 原价=现价
        if("SECKILL"==spuPageType){
            dataBean.orginPrice=dataBean.fbPrice
        }else if(WConstant.maintenanceType==spuPageType){//维保商品
            manageMaintenance()
        }
        //购买数量
        val buyNum=dataBean.buyNum
        //运费 1元=100积分
        val freightPrice=((dataBean.freightPrice?:"0.00").toFloat()*100).toInt()
        //单价（现价）
        val fbPrice=dataBean.fbPrice.toInt()
        //总商品价 单价*购买数量
        val totalFb=fbPrice*buyNum
        //总共支付 (商品金额+运费)
        totalPayFb=totalFb+freightPrice
        //最少使用多少人民币（fb）=总金额*最低现金比
        var minRmb:Float=totalPayFb*minRmbProportion
        val maxFb=WCommonUtil.getHeatNumUP("${totalFb-minRmb}",0).toInt()
        //最大可使用福币
        maxUseFb=if(maxUseFb>maxFb)maxFb else {
            minRmb= (totalPayFb-maxUseFb).toFloat()
            maxUseFb
        }
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.apply {
            if(dataBean.freightPrice=="0")dataBean.freightPrice="0.00"
            //单价（原价）
            val originalPrice=(dataBean.orginPrice?:dataBean.fbPrice).toInt()
            //原总商品价 单价*购买数量
            val totalOriginalFb=originalPrice*buyNum
            tvAmountValue.setText("$totalOriginalFb")
            tvTotal.setHtmlTxt(getString(R.string.str_Xfb,"$totalPayFb"),"#00095B")
            binding.inPayWay.apply {
                rbFbAndRmb.text="$maxUseFb+¥${getRMB("$minRmb")}"
                rbRmb.text = "￥${getRMB("$totalPayFb")}"
            }
//            val spuPageType=dataBean.spuPageType
            //会员折扣、砍价
            if("MEMBER_DISCOUNT"==spuPageType||"MEMBER_DISCOUNT"==dataBean.secondarySpuPageTagType||"2"==spuPageType){
                //会员优惠/砍价优惠=原总价-现总价
                (totalOriginalFb-totalFb).apply {
                    if(this>0){
                        dataBean.preferentialFb="$this"
                        //砍价优惠
                        if("2"==spuPageType)tvMemberDiscount.setText(R.string.str_bargainingFavorable)
                        tvMemberDiscount.visibility=View.VISIBLE
                        tvMemberDiscountValue.visibility=View.VISIBLE
                    }
                }
            }
            model=dataBean
        }
        binding.inGoodsInfo.apply {
//            OrderGoodsAttributeAdapter().apply {
//                rvGoodsProperty.layoutManager= FlowLayoutManager(this@OrderConfirmActivity,false,true)
//                rvGoodsProperty.adapter= this
//                setList(dataBean.skuCodeTxts?.filter { ""!=it })
//            }
//            imgGoodsCover.load(dataBean.skuImg)
//            if(freightPrice!=0)tvDistributionType
            model=dataBean
        }
        //支付方式
        initPayWay()
        binding.inBottom.apply {
            model=dataBean
            tvAcountFb.setText("${dataBean.acountFb}")
            updateBtnUi()
        }
    }
    /**
     * 更新底部提交按钮状态
    * */
    private fun updateBtnUi(){
        dataBean.apply {
            if(WConstant.maintenanceType==spuPageType){//维保商品
                binding.inBottom.btnSubmit.updateEnabled(isAgree&&totalPayFb.toInt()<=acountFb)
            }else binding.inBottom.btnSubmit.updateEnabled(isAgree&&null!=addressId&&totalPayFb.toInt()<=acountFb)

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
                dataBean.isAgree=binding.checkBox.isChecked
                updateBtnUi()
            }
            //福币+人民币支付
            R.id.rb_fbAndRmb->clickPayWay(0)
            //人民币支付
            R.id.rb_rmb->clickPayWay(1)
            //自定义支付
            R.id.rb_custom->clickPayWay(2)
        }
    }
    @DelicateCoroutinesApi
    private fun submitOrder(){
        if(!isClickSubmit){
            isClickSubmit=true
            val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
            dataBean.apply {
                viewModel.orderCreate(skuId,addressId,spuPageType,buyNum,consumerMsg,mallMallSkuSpuSeckillRangeId,mallMallHaggleUserGoodsId,vinCode = vinCode,mallMallWbVinSpuId=mallMallWbVinSpuId)
            }
        }
        GlobalScope.launch {
            delay(3000L)
            isClickSubmit=false
        }
    }
    @SuppressLint("SetTextI18n")
    private fun bindingAddress(item:AddressBeanItem?){
        if(null==item){//未绑定地址
            dataBean.addressId=null
            binding.inAddress.tvAddress.setText(R.string.str_pleaseAddShippingAddress)
            binding.inAddress.tvAddressRemark.visibility=View.GONE
            binding.inBottom.btnSubmit.updateEnabled(false)
        }else{
            dataBean.addressId=item.addressId
            updateBtnUi()
            binding.inAddress.tvAddressRemark.visibility=View.VISIBLE
            binding.inAddress.tvAddress.text="${item.provinceName}${item.cityName}${item.districtName}${item.addressName}"
            binding.inAddress.tvAddressRemark.text="${item.consignee}   ${item.phone}"
        }
    }
    private fun initLiveDataBus(){
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this) {
            it?.let {
                bindingAddress(Gson().fromJson(it, AddressBeanItem::class.java))
            }
        }
    }
    /**
     * 处理维保商品
    * */
    private fun manageMaintenance(){
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
                    Text(text = if(0==i)dataBean.vinCode?:"" else dataBean.models?:"",color= colorResource(R.color.color_33),fontSize = 14.sp,overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
            }
        }
    }
    private fun initPayWay(){
        binding.inPayWay.apply {
            tvFbBalance.setText("$maxUseFb")
            if(maxUseFb>0){
                rbFbAndRmb.visibility=View.VISIBLE
                rbCustom.visibility=View.VISIBLE
                clickPayWay(0)
            }else clickPayWay(1)
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
    }
    private fun edtCustomOnTextChanged(){
        binding.inPayWay.apply {
            edtCustom.onTextChanged {
                val inputFb=it.s
                if(!TextUtils.isEmpty(inputFb)){
                    //输入的福币超出可使用的范围
                    if(inputFb.toString().toInt()>maxUseFb){
                        edtCustom.setText("$maxUseFb")
                        getString(R.string.str_hasMaxUseFb).toast()
                    }
                    calculateFbAndRbm()
                }else{
                    tvCustomRmb.text=""
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun calculateFbAndRbm(){
        binding.inPayWay.apply {
            val inputFb= edtCustom.text.toString()
            tvCustomFb.text=inputFb
            tvCustomRmb.text="+￥${getRMB("${totalPayFb-inputFb.toInt()}")}"
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
        return rmbPrice
    }
    private fun updatePayCustom(){
        if(maxUseFb>0){
            binding.inPayWay.apply {
                val isCheck=rbCustom.isChecked
                if(isCheck){
                    rbCustom.visibility=View.INVISIBLE
                    layoutCustom.visibility=View.VISIBLE
                }else{
                    layoutCustom.visibility=View.GONE
                    rbCustom.visibility=View.VISIBLE
                }
            }
        }
    }
}