package com.changanford.shop.control

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.changanford.common.bean.CommentItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.ShareBean
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.load
import com.changanford.common.web.ShareViewModule
import com.changanford.common.widget.webview.CustomWebHelper
import com.changanford.shop.R
import com.changanford.shop.control.time.KllTimeCountControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.popupwindow.GoodsAttrsPop
import com.changanford.shop.ui.compose.DetailsWalkCompose
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.view.btn.KillBtnView
import com.changanford.shop.viewmodel.GoodsViewModel
import razerdp.basepopup.BasePopupWindow
import java.text.SimpleDateFormat

/**
 * @Author : wenke
 * @Time : 2021/9/18
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: AppCompatActivity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding,val viewModel: GoodsViewModel) {
    private val shareViewModule by lazy { ShareViewModule() }
    private lateinit var shareBean: ShareBean
    var skuCode=""
    //商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    private var timeCount: CountDownTimer?=null
    lateinit var dataBean: GoodsDetailBean
    @SuppressLint("SimpleDateFormat")
    private val sfDate = SimpleDateFormat("yyyy.MM.dd")
    private var popupWindow:GoodsAttrsPop?=null//规格属性弹窗
    fun bindingData(dataBean:GoodsDetailBean){
        popupWindow=null
        this.dataBean=dataBean
        dataBean.orFbPrice=dataBean.fbPrice
        dataBean.orginPrice0=dataBean.orginPrice
        dataBean.price=dataBean.orginPrice
        dataBean.purchasedNum=dataBean.salesCount
        dataBean.source="1"//标记为原生
        dataBean.buyNum=1
        dataBean.allSkuStock=dataBean.stock
        dataBean.getRMB()
        //初始化 skuCode
        var skuCodeInitValue: String
        if(dataBean.stock>0&&dataBean.skuVos.size==1){//当只有一个sku的时候默认选中
            dataBean.skuVos[0].apply {
                skuCodeInitValue=skuCode
                dataBean.skuImg=skuImg
            }
        }else{
            skuCodeInitValue="${dataBean.spuId}-"
            dataBean.attributes.forEach { _ -> skuCodeInitValue+="0-" }
            skuCodeInitValue=skuCodeInitValue.substring(0,skuCodeInitValue.length-1)
        }
//        getSkuTxt(skuCodeInitValue)
        val fbLine=dataBean.fbLine//划线积分
        BannerControl.bindingBannerFromDetail(headerBinding.banner,dataBean.imgs,0)
        //品牌参数
        val param=dataBean.param
        if(null!=param){
            headerBinding.inGoodsInfo.tvParameter.setHtmlTxt("\t\t\t$param","#333333")
            headerBinding.inGoodsInfo.tvParameter.visibility=View.VISIBLE
        }
        //详情
        val detailsHtml=dataBean.detailsHtml
        CustomWebHelper(activity, headerBinding.webview,false).loadDataWithBaseURL(detailsHtml)
        //运费 0为包邮
        val freightPrice=dataBean.freightPrice
        if(freightPrice!="0.00"&&"0"!=freightPrice)headerBinding.inGoodsInfo.tvFreight.setHtmlTxt("\t\t\t$freightPrice","#333333")
        headerBinding.inVip.imgVip.visibility=View.GONE
        headerBinding.inVip.layoutVip.visibility=View.VISIBLE
        when(dataBean.spuPageType?:""){
            //会员专享
            "MEMBER_EXCLUSIVE"->{
                memberExclusive(dataBean)
                headerBinding.inVip.tvVipExclusive.visibility=View.VISIBLE
            }
            //会员折扣
            "MEMBER_DISCOUNT"-> {
                headerBinding.inVip.apply {
                    imgVip.visibility=View.VISIBLE
                    val textColor=ContextCompat.getColor(activity,R.color.color_F21C44)
                    tvRmbPrice.setTextColor(textColor)
                    tvFbPrice.setTextColor(textColor)
                }
            }
            "SECKILL"->{//秒杀信息
//                dataBean.isUpdateBuyNum=false//秒杀商品禁止修改商品数量
                val secKillInfo=dataBean.secKillInfo
                if(null!=secKillInfo){
                    headerBinding.inGoodsInfo.tvConsumption.visibility=View.VISIBLE
                    headerBinding.inVip.layoutVip.visibility=View.GONE
                    headerBinding.inKill.apply {
                        layoutKill.visibility= View.VISIBLE
                        initTimeCount(dataBean.now,secKillInfo.timeBegin,secKillInfo.timeEnd)
                        val totalStock=dataBean.salesCount+dataBean.stock
                        if(dataBean.killStates==2){//已结束
                            dataBean.salesCount=totalStock
                            dataBean.purchasedNum=dataBean.salesCount
                        }
                        //库存百分比
                        val stockProportion=WCommonUtil.getPercentage(dataBean.salesCount.toDouble(),totalStock.toDouble(),0)
                        dataBean.totalStock=totalStock
                        dataBean.stockProportion=stockProportion
//                        if(null==fbLine)tvFbLine.visibility= View.GONE
                        //限量=库存+销量
                        tvLimitBuyNum.setText("$totalStock")
                        model=dataBean
                    }
                }
            }
        }
        getSkuTxt(skuCodeInitValue)
        bindingComment(dataBean.mallOrderEval)
        //推荐-逛一逛
        headerBinding.composeView.setContent {
            DetailsWalkCompose(dataBean.recommend)
        }
    }
    /**
     * 评价信息
    * */
    @SuppressLint("SetTextI18n")
    fun bindingComment(itemData: CommentItem?){
        headerBinding.inComment.apply {
            tvNoData.visibility=View.VISIBLE
            tvGoodsCommentLookAll.visibility=View.GONE
            itemData?.let {
                tvNoData.visibility=View.GONE
                tvGoodsCommentLookAll.visibility=View.VISIBLE
                if("YES"== it.anonymous) it.nickName=activity.getString(R.string.str_anonymousUsers)
                tvGoodsCommentNumber.text=activity.getString(R.string.str_productEvaluationX, dataBean.evalCount)
                imgGoodsCommentAvatar.load(itemData.avater,R.mipmap.head_default)
                it.evalTimeTxt=sfDate.format(it.evalTime?:0)
//                it.evalTimeTxt= DateTimeUtil.formatFriendly(it.evalTime?:0)
                model= it
            }
        }
    }
    /**
     * 秒杀倒计时
     * [nowTime]当前时间
     * [startTime]秒杀开始时间
     * [endTime]秒杀结束时间
    * */
   private fun initTimeCount(nowTime:Long,startTime:Long,endTime:Long){
        headerBinding.inKill.apply {
            var remainingTime=startTime-nowTime//当前时间小于开始时间说明未开始
            if(remainingTime>0){//未开始
                tvKillStates.setText(R.string.str_fromStart)
                dataBean.killStates=7
            }else{//已开始、已结束
                //距离结束剩余时间
                remainingTime=endTime-nowTime
                if(remainingTime>0){//进行中
                    dataBean.killStates=5
                    tvKillStates.setText(R.string.str_fromEnd)
                }else{//已结束
                    dataBean.killStates=2
                    tvKillStates.setText(R.string.str_hasEnded)
                }
            }
            if(remainingTime<=0)return
            timeCount?.cancel()
            timeCount= KllTimeCountControl(remainingTime,tvKillH,tvKillM,tvKillS,object :
                OnTimeCountListener {
                override fun onFinish() {
                    //秒杀结束刷新数据
                    viewModel.queryGoodsDetails(dataBean.spuId)
                }
            })
            timeCount?.start()
        }
    }
    /**
     * 创建选择商品属性弹窗
    * */
    fun createAttribute(){
        if(::dataBean.isInitialized){
            val spuPageType=dataBean.spuPageType?:""
            if("SECKILL"!=spuPageType||("SECKILL"==spuPageType&&2!=dataBean.killStates)){
                if(null==popupWindow){
                    popupWindow=GoodsAttrsPop(activity,this.dataBean,skuCode,this).apply {
                        showPopupWindow()
                        onDismissListener=object : BasePopupWindow.OnDismissListener() {
                            override fun onDismiss() {
                                getSkuTxt(_skuCode)
                            }
                        }
                    }
                }else popupWindow?.showPopupWindow()
            }
        }
    }
    private fun getSkuTxt(skuCode:String){
        this.skuCode=skuCode
        dataBean.skuVos.find { skuCode== it.skuCode }?.apply {
            dataBean.skuId=skuId
            dataBean.fbPrice=fbPrice
            dataBean.stock=stock.toInt()
            dataBean.orginPrice=orginPrice
            dataBean.price=orginPrice
            dataBean.mallMallSkuSpuSeckillRangeId=mallMallSkuSpuSeckillRangeId
        }
        dataBean.getRMB()
//        headerBinding.inDiscount.tvVipIntegral.setText(dataBean.fbPrice)
        memberExclusive(dataBean)
        val skuCodes=skuCode.split("-")
        var skuCodeTxt=""
        val skuCodeTxtArr= arrayListOf<String>()
        for((i,item) in dataBean.attributes.withIndex()){
            item.optionVos.find { skuCodes[i+1]== it.optionId }?.let {
                val optionName= it.optionName
                skuCodeTxtArr.add(optionName)
                skuCodeTxt+="$optionName  "
            }
        }
        dataBean.skuCodeTxts=skuCodeTxtArr
        headerBinding.inGoodsInfo.tvGoodsAttrs.setHtmlTxt(if(TextUtils.isEmpty(skuCodeTxt))"\t\t\t未选择属性" else "\t\t\t已选：${skuCodeTxt}","#333333")
        headerBinding.inKill.model=dataBean
        headerBinding.inVip.model=dataBean
        headerBinding.inGoodsInfo.model=dataBean
        bindingBtn(dataBean,null, binding.inBottom.btnBuy, binding.inBottom.btnCart,0)
    }
    /**
     * 处理专享数据-并且是折扣数据
    * */
    fun memberExclusive(_dataBean:GoodsDetailBean){
        if("MEMBER_EXCLUSIVE"==_dataBean.spuPageType&&"MEMBER_DISCOUNT"==_dataBean.secondarySpuPageTagType){
            _dataBean.price=_dataBean.fbPrice
        }
    }
    /**
     * [source]来源 0详情 1属性弹窗
    * */
    fun bindingBtn(_dataBean:GoodsDetailBean,_skuCode: String?,btnSubmit: KillBtnView,btnCart:KillBtnView?=null,source:Int=0){
        _dataBean.apply {
            val totalPayFb=fbPrice.toInt()*buyNum
            if("SECKILL"==spuPageType&&5!=killStates)btnSubmit.setStates(killStates,btnSource=source)//2/7 秒杀已结束或者未开始
            else if(stock<1){//库存不足,已售罄、已抢光
                btnSubmit.setStates(if("SECKILL"==spuPageType)1 else 6,true,btnSource=source)
            } else if(1==source||(0==source&&!isInvalidSelectAttrs(this@GoodsDetailsControl.skuCode))){
                if(null!=_skuCode&&isInvalidSelectAttrs(_skuCode)){
                    btnSubmit.setText(R.string.str_immediatelyChange)
                    btnSubmit.updateEnabled(false)
                } else if(MConstant.token.isNotEmpty()&&acountFb<totalPayFb){//福币余额不足
                    btnSubmit.setStates(8,btnSource=source)
                }else btnSubmit.setStates(5,btnSource=source)
            }else btnSubmit.setStates(5,btnSource=source)
            //处理购物车按钮
            if("SECKILL"==spuPageType){//秒杀商品不具备加入购物车功能
                btnCart?.updateEnabled(false,source)
            }else btnCart?.updateEnabled(btnSubmit.isEnabled,source)

        }
    }
    /**
     * 商品分享 需登录和绑定手机
    * */
    fun share(){
        if(MineUtils.getBindMobileJumpDataType(true))return
        if(::dataBean.isInitialized)dataBean.shareBeanVO?.apply {
            shareBean=ShareBean(targetUrl =shareUrl,imageUrl = shareImg,bizId = bizId,title = shareTitle,content = shareDesc,type = type)
           shareViewModule.share(activity,shareBean)
        }
    }
    fun shareBack(){
        if(::shareBean.isInitialized){
            ToastUtils.reToast(R.string.str_shareSuccess)
            shareViewModule.shareBack(shareBean)
        }
    }
    /**
     * 是否是无效选择商品属性
     * return false 有效 、true 无效
     * */
    fun isInvalidSelectAttrs(skuCode:String):Boolean{
        return skuCode.contains("-")&&skuCode.split("-").find { it =="0" }!=null
    }
    fun onDestroy(){
        timeCount?.cancel()
    }
    /**
     * 提交订单
     * */
    fun submitOrder(){
        skuCode.apply {
            if(isInvalidSelectAttrs(this))createAttribute()
            else OrderConfirmActivity.start(0,dataBean)
        }
    }
    /**
     * 加入购物车
    * */
    fun addShoppingCart(){
        skuCode.apply {
//            if(isInvalidSelectAttrs(this))createAttribute()
//            else viewModel.addShoppingCart(dataBean.spuId)
        }
    }
}