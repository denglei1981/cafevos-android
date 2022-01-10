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
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.utils.WConstant
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
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
    }
    private lateinit var dataBean:GoodsDetailBean
    private var isClickSubmit=false
    private var spuPageType=""//商品类型
    override fun initView() {
        binding.topBar.setActivity(this)
        val goodsInfo=intent.getStringExtra("goodsInfo")
        if(TextUtils.isEmpty(goodsInfo)){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        if(MConstant.isShowLog)Log.e("okhttp","goodsInfo:$goodsInfo")
        dataBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
        spuPageType=dataBean.spuPageType
        dataBean.isAgree=false
        initLiveDataBus()
    }

    override fun initData() {
        binding.inGoodsInfo.addSubtractView.apply {
            val stock=dataBean.stock
            val limitBuyNum:Int=dataBean.getLimitBuyNum()
            var isLimitBuyNum=false//是否限购
            val max: Int =if(limitBuyNum in 1..stock) {
                isLimitBuyNum=true
                limitBuyNum
            } else stock
            setMax(max,isLimitBuyNum)
            setNumber(dataBean.buyNum,false)
            setIsUpdateBuyNum(dataBean.isUpdateBuyNum)
            numberLiveData.observe(this@OrderConfirmActivity,{
                dataBean.buyNum= it
                bindingBaseData()
            })
        }
        //非维保商品 需要选择地址
        if(WConstant.maintenanceType!=spuPageType){
            viewModel.addressList.observe(this,{ addressList ->
                //默认获取地址列表的默认收货地址
                val item:AddressBeanItem?=addressList?.find { it.isDefault==1 }
                bindingAddress(item)
            })
            val addressInfo=dataBean.addressInfo
            if(TextUtils.isEmpty(addressInfo))viewModel.getAddressList()
            else viewModel.addressList.postValue(arrayListOf(Gson().fromJson(addressInfo,AddressBeanItem::class.java)))
        }
        viewModel.orderInfoLiveData.observe(this,{
            isClickSubmit=false
            val source=it.source
            if(source!="0")it.source=if(dataBean.spuPageType=="2") "2" else dataBean.source
            PayConfirmActivity.start(it.orderNo)
            LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK, String::class.java).postValue(it.source)
            this.finish()
        })
        bindingBaseData()
    }
    @SuppressLint("StringFormatMatches")
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
        val totalPayFb:Int=totalFb+freightPrice
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.apply {
            if(dataBean.freightPrice=="0")dataBean.freightPrice="0.00"
            //单价（原价）
            val originalPrice=(dataBean.orginPrice?:dataBean.fbPrice).toInt()
            //原总商品价 单价*购买数量
            val totalOriginalFb=originalPrice*buyNum
            tvAmountValue.setText("$totalOriginalFb")
            tvTotal.setHtmlTxt(getString(R.string.str_Xfb,"$totalPayFb"),"#00095B")
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
            OrderGoodsAttributeAdapter().apply {
                rvGoodsProperty.layoutManager= FlowLayoutManager(this@OrderConfirmActivity,false,true)
                rvGoodsProperty.adapter= this
                setList(dataBean.skuCodeTxts?.filter { ""!=it })
            }
            imgGoodsCover.load(dataBean.skuImg)
//            if(freightPrice!=0)tvDistributionType
            model=dataBean
        }
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
        }
    }
    @DelicateCoroutinesApi
    private fun submitOrder(){
        if(!isClickSubmit){
            isClickSubmit=true
            val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
            dataBean.apply {
                viewModel.orderCreate(skuId,addressId,spuPageType,buyNum,consumerMsg,mallMallSkuSpuSeckillRangeId,mallMallHaggleUserGoodsId)
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
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this, {
            it?.let {
                bindingAddress(Gson().fromJson(it,AddressBeanItem::class.java))
            }
        })
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
            .background(Color.White).padding(bottom = 17.dp)) {
            Spacer(modifier = Modifier
                .height(10.dp)
                .background(colorResource(R.color.color_F4)))
            for (i in 0..1){//0 vin码 1车型
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp,top = if(0==i)19.dp else 29.dp)) {
                    Text(text = stringResource(if(0==i)R.string.str_vidCode else R.string.str_models),color= colorResource(R.color.color_33),fontSize = 14.sp,
                    modifier = Modifier.weight(1f).padding(end = 10.dp))
                    Text(text = if(0==i)dataBean.vinCode?:"" else dataBean.models?:"",color= colorResource(R.color.color_33),fontSize = 14.sp,overflow = TextOverflow.Ellipsis,maxLines = 1)
                }
            }
        }
    }
}