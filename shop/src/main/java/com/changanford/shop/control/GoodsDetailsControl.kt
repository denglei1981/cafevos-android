package com.changanford.shop.control

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.bean.CommentItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.ShareBean
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.web.ShareViewModule
import com.changanford.shop.R
import com.changanford.shop.control.time.KllTimeCountControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.popupwindow.GoodsAttrsPop
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.GoodsViewModel
import razerdp.basepopup.BasePopupWindow

/**
 * @Author : wenke
 * @Time : 2021/9/18
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: AppCompatActivity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding,val viewModel: GoodsViewModel) {
    private val shareViewModule by lazy { ShareViewModule() }
    private var skuCode=""
    //商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    private var timeCount: CountDownTimer?=null
    lateinit var dataBean: GoodsDetailBean
    fun bindingData(dataBean:GoodsDetailBean){
        this.dataBean=dataBean
        dataBean.buyNum=1
        getSkuTxt(dataBean.skuVos[0].skuCode)
        val fbLine=dataBean.fbLine//划线积分
        BannerControl.bindingBannerFromDetail(headerBinding.banner,dataBean.imgs,0)
        WCommonUtil.htmlToImgStr(activity,headerBinding.tvDetails,dataBean.detailsHtml)
        //品牌参数
        val param=dataBean.param
        if(null!=param){
            WCommonUtil.htmlToString(headerBinding.inGoodsInfo.tvParameter,"参数 <font color=\"#333333\">$param</font>")
            headerBinding.inGoodsInfo.tvParameter.visibility=View.VISIBLE
        }
        //运费 0为包邮
        val freightPrice=dataBean.freightPrice
        if(freightPrice!="0.00")WCommonUtil.htmlToString(headerBinding.inGoodsInfo.tvFreight,"运费 <font color=\"#333333\">$freightPrice</font>")
        headerBinding.inDiscount.lLayoutVip.visibility=View.GONE
        headerBinding.inVip.layoutVip.visibility=View.VISIBLE
        when(dataBean.spuPageType){
            "MEMBER_EXCLUSIVE"->headerBinding.inVip.tvVipExclusive.visibility=View.VISIBLE
            "MEMBER_DISCOUNT"-> {
                headerBinding.inDiscount.apply {
                    lLayoutVip.visibility=View.VISIBLE
                    tvVipIntegral.setText(dataBean.fbPrice)
                }
            }
            "SECKILL"->{//秒杀信息
                val secKillInfo=dataBean.secKillInfo
                if(null!=secKillInfo){
                    headerBinding.inGoodsInfo.tvConsumption.visibility=View.VISIBLE
                    headerBinding.inVip.layoutVip.visibility=View.GONE
                    headerBinding.inKill.apply {
                        model=dataBean
                        layoutKill.visibility= View.VISIBLE
                        initTimeCount(dataBean.now,secKillInfo.timeBegin,secKillInfo.timeEnd)
                        val purchasedNum=dataBean.purchasedNum?:0
                        tvStockProportion.setText("${purchasedNum/dataBean.stock*100}")
                        if(null==fbLine)tvFbLine.visibility= View.GONE
                        //限量=库存+销量
                        val limitBuyNum=dataBean.salesCount+dataBean.stock
                        tvLimitBuyNum.setText("$limitBuyNum")
//                        val limitBuyNum=dataBean.limitBuyNum?:"0"
//                        if("0"!=limitBuyNum)tvLimitBuyNum.visibility=View.VISIBLE
                    }
                }
            }
        }
        bindingComment(dataBean.mallOrderEval)
    }
    /**
     * 评价信息
    * */
    @SuppressLint("SetTextI18n")
    fun bindingComment(itemData: CommentItem?){
        if(null!=itemData){
            headerBinding.inComment.apply {
                model=itemData
                layoutComment.visibility=View.VISIBLE
                tvGoodsCommentNumber.text=activity.getString(R.string.str_productEvaluationX, 0)
                GlideUtils.loadBD(GlideUtils.handleImgUrl(itemData.avater),imgGoodsCommentAvatar)
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
            }else{//已开始、已结束
                //距离结束剩余时间
                remainingTime=endTime-nowTime
                tvKillStates.setText(if(remainingTime>0)R.string.str_fromEnd else R.string.str_hasEnded)
            }
            if(remainingTime<=0)return
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
        if(::dataBean.isInitialized&&skuCode.isNotEmpty()){
            GoodsAttrsPop(activity,this.dataBean,skuCode).apply {
                showPopupWindow()
                onDismissListener=object : BasePopupWindow.OnDismissListener() {
                    override fun onDismiss() {
                        getSkuTxt(_skuCode)
                    }
                }
            }
        }
    }
    private fun getSkuTxt(skuCode:String){
        this.skuCode=skuCode
        val findItem=dataBean.skuVos.find { skuCode== it.skuCode }?:dataBean.skuVos[0]
        dataBean.skuId=findItem.skuId
        dataBean.fbPrice=findItem.fbPrice
        dataBean.stock=findItem.stock.toInt()
        dataBean.skuCodeTxts= arrayListOf()
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
        headerBinding.inGoodsInfo.tvGoodsAttrs.setHtmlTxt("  已选：${skuCodeTxt}","#333333")
        headerBinding.inVip.model=dataBean
        headerBinding.inGoodsInfo.model=dataBean
        bindingBtn()
    }
    private fun bindingBtn(){
        binding.inBottom.btnSubmit.apply {
            val totalPayFb=dataBean.fbPrice.toInt()*dataBean.buyNum
            if(MConstant.token.isNotEmpty()&&dataBean.acountFb<totalPayFb){//积分余额不足
                setStates(8)
            } else if(dataBean.secKillInfo!=null&&dataBean.now<dataBean.secKillInfo?.timeBegin!!){//秒杀未开始
                setStates(7)
            }else if(dataBean.stock<1){//库存不足,已售罄、已抢光
                setStates(if("SECKILL"==dataBean.spuPageType)1 else 6,true)
            }else setStates(5)
        }
    }
    fun share(){
        if(::dataBean.isInitialized)dataBean.shareBeanVO?.apply {
           shareViewModule.share(activity, ShareBean(targetUrl =shareUrl,imageUrl = shareImg,bizId = bizId,title = shareTitle,content = shareDesc,type = type))
        }
    }
    fun onDestroy(){
        timeCount?.cancel()
    }
}