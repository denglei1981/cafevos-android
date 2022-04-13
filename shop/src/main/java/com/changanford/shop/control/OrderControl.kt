package com.changanford.shop.control

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.OrderRefundItemBean
import com.changanford.common.bean.OrderSkuItem
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.load
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.adapter.order.OrderGoodsImgAdapter
import com.changanford.shop.bean.InvoiceInfo
import com.changanford.shop.bean.RefundBean
import com.changanford.shop.databinding.InItemOrderGoodsBinding
import com.changanford.shop.popupwindow.PublicPop
import com.changanford.shop.ui.goods.GoodsDetailsActivity
import com.changanford.shop.ui.order.InvoiceActivity
import com.changanford.shop.ui.order.PayConfirmActivity
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson


/**
 * @Author : wenke
 * @Time : 2021/10/15
 * @Description : OrderControl
 */
class OrderControl(val context: Context,val viewModel: OrderViewModel?) {
    private val imgWidthDp by lazy { (ScreenUtils.getScreenWidthDp(context)-105)/3 }
    private val imgWidthPx by lazy { ScreenUtils.dp2px(context,imgWidthDp.toFloat()) }
    /**
     * 绑定订单商品基础信息
     * */
    fun bindingGoodsInfo(dataBinding:InItemOrderGoodsBinding,itemBean: OrderItemBean){
        dataBinding.apply {
            val params = imgGoodsCover.layoutParams
            params.width=imgWidthPx
            params.height=imgWidthPx
            imgGoodsCover.layoutParams=params
            if(itemBean.skuOrderVOList==null||itemBean.skuOrderVOList?.size==1){
                val skuItem= if(itemBean.skuOrderVOList!=null&&itemBean.skuOrderVOList!!.size==1)itemBean.skuOrderVOList?.get(0)?: OrderSkuItem()
                else OrderSkuItem(skuImg =itemBean.skuImg, spuName =itemBean.spuName, busSourse = itemBean.busSourse)
                recyclerViewImgArr.visibility=View.GONE
                skuItem.fbPrice=itemBean.fb
                skuItem.rmbPrice=itemBean.rmb
                val orderType=skuItem.orderType
                imgGoodsCover.scaleType= if(orderType>2||0==orderType) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.CENTER_INSIDE
                imgGoodsCover.load(skuItem.skuImg)
                tvOrderType.apply {
                    visibility = when(itemBean.busSourse) {
                        "1","SECKILL" -> {//秒杀
                            setText(R.string.str_seckill)
                            View.VISIBLE
                        }
                        "2","HAGGLE" -> {//砍价
                            setText(R.string.str_bargaining)
                            View.VISIBLE
                        }
                        "3","WB"->{//维保
                            setText(R.string.str_maintenance)
                            View.VISIBLE
                        }
                        else -> View.GONE
                    }
                }
                //众筹订单自带单位
//                tvIntegral.setEndTxt(if(4==item.orderType)null else context.getString(R.string.str_integral))
                recyclerView.apply {
                    if(!TextUtils.isEmpty(skuItem.specifications)){
                        visibility= View.VISIBLE
                        layoutManager= FlowLayoutManager(context,false,true)
                        adapter= OrderGoodsAttributeAdapter().apply {
                            val specifications=skuItem.specifications?.split(",")?.filter { ""!= it }
                            setList(specifications)
                        }
                    }else{
                        visibility= View.INVISIBLE
                    }
                }
                model=skuItem
            }else {
                recyclerViewImgArr.visibility=View.VISIBLE
                val mAdapter=OrderGoodsImgAdapter()
                recyclerViewImgArr.adapter= mAdapter
                mAdapter.setList(itemBean.skuOrderVOList)
            }
            itemBean.getRMBPrice()
            model0=itemBean
        }
    }
    fun bindingGoodsInfo(dataBinding:InItemOrderGoodsBinding,itemBean: OrderRefundItemBean){
        dataBinding.apply {
            if(itemBean.refundSkus.size==1){
                val skuItem=itemBean.refundSkus[0]
                imgGoodsCover.load(skuItem.skuImg)
                tvGoodsTitle.text=skuItem.spuName
                tvTotalNum.visibility=View.VISIBLE
                tvTotalNum.setText("${skuItem.refundNum}")
                tvOrderType.apply {
                    visibility = when(itemBean.busSourse) {
                        "1","SECKILL" -> {//秒杀
                            setText(R.string.str_seckill)
                            View.VISIBLE
                        }
                        "2","HAGGLE" -> {//砍价
                            setText(R.string.str_bargaining)
                            View.VISIBLE
                        }
                        "3","WB"->{//维保
                            setText(R.string.str_maintenance)
                            View.VISIBLE
                        }
                        else -> View.GONE
                    }
                }
                recyclerView.apply {
                    if(!TextUtils.isEmpty(skuItem.specifications)){
                        visibility= View.VISIBLE
                        layoutManager= FlowLayoutManager(context,false,true)
                        adapter= OrderGoodsAttributeAdapter().apply {
                            val specifications=skuItem.specifications?.split(",")?.filter { ""!= it }
                            setList(specifications)
                        }
                    }else{
                        visibility= View.INVISIBLE
                    }
                }
            }else{
                recyclerViewImgArr.visibility=View.VISIBLE
                val mAdapter=OrderGoodsImgAdapter()
                recyclerViewImgArr.adapter= mAdapter
                mAdapter.setList(itemBean.refundSkus)
            }
        }
    }
    /**
     * 去支付
     * */
    fun toPay(item: OrderItemBean){
//        PayConfirmActivity.start(Gson().toJson(OrderInfoBean(item.orderNo,item.fbCost)))
        PayConfirmActivity.start(item.orderNo)
    }
    /**
     * 再次购买->商品详情
     * */
    fun onceAgainToBuy(item: OrderItemBean?){
        item?.apply {
            if("2"==busSourse){
                //砍价
                JumpUtils.instans?.jump(1,String.format(MConstant.H5_SHOP_BARGAINING,mallMallHaggleSpuId,mallMallHaggleActivityId))
            }else {
                  // TODO 再次购买的逻辑。
                GoodsDetailsActivity.start(jumpDataType?:3,jumpDataValue?:mallMallSpuId)
            }
        }

//        val skuCodeTxt= item.specifications.split(",").filter { ""!=it }
//        val busSourse=item.busSourse
//        val spuPageType=if("2"==busSourse)"2" else if("1"==busSourse)"SECKILL" else if("1"==item.discount)"MEMBER_DISCOUNT" else "NOMROL"
//        val goodsBean= GoodsDetailBean(spuId = item.mallMallSpuId,spuName =item.spuName, buyNum = item.buyNum.toInt(),fbPrice = item.fbOfUnitPrice,
//            freightPrice = item.freightPrice?:"0.00",preferentialFb = item.preferentialFb,acountFb = (item.totalIntegral?:"0").toInt(),skuCode = item.skuCode,
//            skuCodeTxts = skuCodeTxt, addressInfo = item.addressInfo,addressId = item.addressId,skuId = item.mallMallSkuId,skuImg = item.skuImg,source = "3",
//            spuPageType =spuPageType,orginPrice = item.fbOfUnitPrice)
//        OrderConfirmActivity.start(Gson().toJson(goodsBean))
    }
    /**
     * 确认收货
     * */
    fun confirmGoods(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.str_confirmReceiptGoods),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() {
                    dismiss()
                }
                override fun onRightClick() {
                    viewModel?.confirmReceipt(item.orderNo,object : OnPerformListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            ToastUtils.showShortToast(R.string.str_goodsSuccessfully,context)
                            item.orderStatusName=""
                            item.orderStatus="FINISH"
                            item.evalStatus="WAIT_EVAL"
                            listener?.onFinish(0)
                            dismiss()
                        }
                    })
                }
            })
        }
    }

    /**
     * 取消订单
     * */
    fun cancelOrder(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.prompt_cancelOrder),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() { dismiss() }
                override fun onRightClick() {
                    viewModel?.orderCancel(item.orderNo,object: OnPerformListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            ToastUtils.showShortToast(R.string.str_orderCancelledSuccessfully,context)
                            item.orderStatusName=""
                            item.orderStatus="CLOSED"
                            listener?.onFinish(0)
                            dismiss()
                        }
                    })
                }
            })
        }
    }
    /**
     * 申请退换货
    * */
    fun applyRefund(item: OrderItemBean,listener: OnPerformListener?=null){
        PublicPop(context).apply {
            showPopupWindow(context.getString(R.string.str_areYouApplyReturn),null,null,object :
                PublicPop.OnPopClickListener{
                override fun onLeftClick() { dismiss() }
                override fun onRightClick() {
                    viewModel?.applyRefund(item.orderNo,object: OnPerformListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onFinish(code: Int) {
                            item.orderStatusName=""
                            item.orderStatus="RTING"//退换货处理中
                            listener?.onFinish(0)
                            dismiss()
                            ToastUtils.reToast(R.string.str_applyRefundComplete)
//                            PublicPop(context).apply {
//                                showPopupWindow(context.getString(R.string.str_applyRefundComplete),context.getString(R.string.str_iKnow),object :
//                                    PublicPop.OnPopClickListener{
//                                    override fun onLeftClick() { dismiss() }
//                                    override fun onRightClick() { dismiss() }
//                                })
//                            }
                        }
                    })
                }
            })
        }
    }
    /**
     * 订单列表按钮操作
     * [type]
    * */
    fun orderBtnClick(type:Int,item: OrderItemBean){
        item.apply {
            when(type){
                //申请发票 、查看发票
                0,1->{
                    if(invoiced=="NOT_BEGIN"){
                        val invoiceInfo = InvoiceInfo(mallMallOrderId=mallMallOrderId?:"0", mallMallOrderNo=orderNo, invoiceRmb=getRMBExtendsUnit())
                        InvoiceActivity.start(invoiceInfo)
//                        JumpUtils.instans?.jump(120, "{\"orderNo\":\"$orderNo\"}")
                    }else  JumpUtils.instans?.jump(123,orderNo)

                }
                //查看物流
                2->{
                    packageJump?.apply {
                        JumpUtils.instans?.jump(jumpCode,jumpVal)
                    }
                }
                //申请售后-到订单详情
                3->{
                    JumpUtils.instans?.jump(5,orderNo)
                }
                //申请退款
                4->{
                    val gson = Gson()
                    val refundBean =RefundBean(orderNo, payFb, payRmb, "allOrderRefund")
                    val refundJson = gson.toJson(refundBean)
                    JumpUtils.instans?.jump(121,refundJson)
                }
                //售后详情
                5->{

                }
            }
        }
    }
}