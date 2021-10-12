package com.changanford.shop.control

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.bean.CommentItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.control.time.KllTimeCountControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.popupwindow.GoodsAttrsPop
import com.changanford.shop.utils.WCommonUtil
import razerdp.basepopup.BasePopupWindow

/**
 * @Author : wenke
 * @Time : 2021/9/18
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: AppCompatActivity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding) {
    private var skuCode=""
    //商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    private var timeCount: CountDownTimer?=null
    private lateinit var dataBean: GoodsDetailBean
    fun bindingData(dataBean:GoodsDetailBean){
        this.dataBean=dataBean
        getSkuTxt(dataBean.skuVos[0].skuCode)
        val fbLine=dataBean.fbLine//划线积分
        headerBinding.inGoodsInfo.model=dataBean
        headerBinding.inVip.model=dataBean
        BannerControl.bindingBannerFromDetail(headerBinding.banner,dataBean.imgs,0)
        WCommonUtil.htmlToImgStr(activity,headerBinding.tvDetails,dataBean.detailsHtml)
        headerBinding.inDiscount.lLayoutVip.visibility=View.GONE
        when(dataBean.spuPageType){
            "MEMBER_EXCLUSIVE"->headerBinding.inVip.tvVipExclusive.visibility=View.VISIBLE
            "MEMBER_DISCOUNT"-> {
                headerBinding.inDiscount.lLayoutVip.visibility=View.VISIBLE
                headerBinding.inDiscount.tvVipIntegral.setText(dataBean.fbPrice)
            }
            "SECKILL"->{//秒杀信息
                val secKillInfo=dataBean.secKillInfo
                if(null!=secKillInfo){
                    headerBinding.inKill.model=dataBean
                    headerBinding.inGoodsInfo.tvConsumption.visibility=View.VISIBLE
                    headerBinding.inKill.layoutKill.visibility= View.VISIBLE
                    if(null!=dataBean.now)initTimeCount(dataBean.now!!,secKillInfo.timeBegin,secKillInfo.timeEnd)
                    val purchasedNum=dataBean.purchasedNum?:0
                    headerBinding.inKill.tvStockProportion.setText("${purchasedNum/dataBean.stock*100}")
                    if(null==fbLine)headerBinding.inKill.tvFbLine.visibility= View.GONE
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
            headerBinding.inComment.layoutComment.visibility=View.VISIBLE
            headerBinding.inComment.model=itemData
            GlideUtils.loadBD(GlideUtils.handleImgUrl(itemData.avater),headerBinding.inComment.imgGoodsCommentAvatar)
            headerBinding.inComment.tvGoodsCommentNumber.text=activity.getString(R.string.str_productEvaluationX, 0)
        }
    }
    /**
     * 秒杀倒计时
     * [timestamp]当前时间
     * [startTime]秒杀开始时间
     * [endTime]秒杀结束时间
    * */
   private fun initTimeCount(timestamp:Long,startTime:Long,endTime:Long){
        var remainingTime=startTime-timestamp//当前时间小于开始时间说明未开始
        if(remainingTime>0){//未开始
            headerBinding.inKill.tvKillStates.setText(R.string.str_fromStart)
        }else{//已开始
            headerBinding.inKill.tvKillStates.setText(R.string.str_fromEnd)
            remainingTime=timestamp-endTime//距离结束剩余时间
        }
        if(remainingTime<=0)return
        timeCount= KllTimeCountControl(remainingTime,headerBinding.inKill.tvKillH,headerBinding.inKill.tvKillM,headerBinding.inKill.tvKillS,object :
            OnTimeCountListener {
            override fun onFinish() {

            }
        })
        timeCount?.start()
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
                        Log.e("okhttp","buyNum:${dataBean.buyNum}")
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
        for((i,item) in dataBean.attributes.withIndex()){
            val optionVosItem=item.optionVos.find { skuCodes[i+1]== it.optionId }
            skuCodeTxt+="${optionVosItem?.optionName}  "
        }
        WCommonUtil.htmlToString(headerBinding.inGoodsInfo.tvGoodsAttrs,"选择 <font color=\"#333333\">已选：${skuCodeTxt}</font>")
        bindingBtn()
    }
    private fun bindingBtn(){
        //库存不足,已售罄、已抢光
        if(dataBean.stock<1){
            binding.inBottom.btnSubmit.setStates(if("SECKILL"==dataBean.spuPageType)1 else 6,true)
        }else binding.inBottom.btnSubmit.setStates(5)
    }
    fun onDestroy(){
        timeCount?.cancel()
    }
}