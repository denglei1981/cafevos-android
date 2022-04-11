package com.changanford.shop.bean

import android.content.Context
import android.text.TextUtils
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.util.PictureUtil
import com.changanford.shop.R
import com.luck.picture.lib.entity.LocalMedia

data class OrderFormState(
    val content:String?=null,
    val contentLength:Int=0,
    val isDataValid: Boolean = false,
    val rating:Int=0,
    var imgPathArr:ArrayList<String>?=null,
    var selectPics:ArrayList<LocalMedia>?=null
){
    fun getImgPaths(selectPics:ArrayList<LocalMedia>?=this.selectPics){
        imgPathArr= arrayListOf()
        selectPics?.forEach {media->
            imgPathArr?.add(PictureUtil.getFinallyPath(media))
        }
    }
}
data class PostEvaluationListBean(
    var orderNo:String,//订单号
    var reviewEval:Boolean?=false,//是否追评
    var orderSkuItems:List<OrderItemBean>?=null,//追评的列表 主要需要 skuImg、spuName
)
/**
 * 发布评价
* */
data class PostEvaluationBean(
    var anonymous: String?=null,//是否匿名
    var evalScore: Int? = null,//评分(0-5)
    var evalText: String? = null,//评价内容
    var imgUrls: List<String>? = null,
    var mallMallOrderSkuId:String?= null,//订单skuId
    var isComplete:Boolean=false,
){
    /**
     * 是否完成（评分、评价内容为必填项）
    * */
    fun updateStatus():Boolean{
        isComplete=!TextUtils.isEmpty(evalText)&&evalScore!=null&&evalScore!!>0
        return isComplete
    }
    fun getEvalText(context:Context,rating:Int=evalScore?:0): String {
        val ratingStr= when {
            rating == 0->""
            rating < 3 -> context.getString(R.string.str_badReview)
            rating > 3 ->context.getString(R.string.str_goodReview)
            rating==3 ->context.getString(R.string.str_mediumReview)
            else ->""
        }
        return ratingStr
    }
}
data class RefundBean(var orderNo:String,var payFb:String?,var payRmb:String?,var refundType:String){

}