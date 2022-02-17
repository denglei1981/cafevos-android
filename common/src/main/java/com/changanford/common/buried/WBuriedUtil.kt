package com.changanford.common.buried

import com.alibaba.fastjson.JSON
import com.changanford.common.util.MConstant

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.utils.BuriedUtil
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/28 10:05
 * @Description: 　埋点工具
 * *********************************************************************************
 */

object WBuriedUtil {
    /**
     * 数据埋点
     */
    private fun buried(actName: String="", actionType: String="", targetId: String="", targetName: String="", pageStayTime: String="",extend:String="") {
        val buriedBean = BuriedBean()
        val data = buriedBean.getData(
            record = BuriedBean.BuriedRecord(
                actName, actionType, targetName, targetId, MConstant.userId, pageStayTime, extend = extend
            )
        )
        MBuriedWorkerManager.instant?.buried(JSON.toJSONString(data))
    }
    //商城 START
    /**
     * app商城_顶部banner_点击
     * [bannerName]banner商品名称
    * */
    fun clickMallBanner(bannerName: String){
        buried("app商城_顶部banner_点击","app_mall_top_banner", extend = "{\"goods_name\": \"$bannerName\"}")
    }
    //【商品id】,点击商品【商品名称】,1
    fun clickMall(pId:String,pName:String){
        buried("商品名称","click_mall_p",pId,pName)
    }
    //商城END
}