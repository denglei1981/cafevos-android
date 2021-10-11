package com.changanford.shop.control

import android.annotation.SuppressLint
import android.app.Activity
import android.os.CountDownTimer
import android.view.View
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

/**
 * @Author : wenke
 * @Time : 2021/9/18
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: Activity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding) {
    //商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    private var timeCount: CountDownTimer?=null
    fun bindingData(datas:GoodsDetailBean){
        val fbLine=datas.fbLine//划线积分
        headerBinding.inGoodsInfo.model=datas
        BannerControl.bindingBannerFromDetail(headerBinding.banner,datas.imgs,0)
        WCommonUtil.htmlToImgStr(activity,headerBinding.tvDetails,datas.detailsHtml)
        headerBinding.inDiscount.lLayoutVip.visibility=View.GONE
        when(datas.spuPageType){
            "MEMBER_EXCLUSIVE"->{
                headerBinding.inVip.layoutVip.visibility=View.VISIBLE
                headerBinding.inVip.model=datas
            }
            "MEMBER_DISCOUNT"-> {
                headerBinding.inDiscount.lLayoutVip.visibility=View.VISIBLE
                headerBinding.inDiscount.tvVipIntegral.setText(datas.fbPrice)
            }
            "SECKILL"->{
                //秒杀信息
                val secKillInfo=datas.secKillInfo
                if(null!=secKillInfo){
                    headerBinding.inKill.model=datas
                    headerBinding.inGoodsInfo.tvConsumption.visibility=View.VISIBLE
                    headerBinding.inKill.layoutKill.visibility= View.VISIBLE
                    if(null!=datas.timestamp)initTimeCount(datas.timestamp!! -secKillInfo.timeEnd)
                    val purchasedNum=datas.purchasedNum?:0
                    headerBinding.inKill.tvStockProportion.setText("${purchasedNum/datas.stock*100}")
                    if(null==fbLine)headerBinding.inKill.tvFbLine.visibility= View.GONE
                }
            }
        }
        bindingComment(datas.mallOrderEval)
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
     * [remainingTime]剩余时间 毫秒
    * */
   private fun initTimeCount(remainingTime:Long){
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
        val pop= GoodsAttrsPop(activity)
        pop.showPopupWindow()
    }

    fun onDestroy(){
        timeCount?.cancel()
    }
}