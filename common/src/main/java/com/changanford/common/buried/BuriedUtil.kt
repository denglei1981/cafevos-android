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

class BuriedUtil {
    companion object {
        @JvmStatic
        var instant: BuriedUtil? = null
            get() {
                if (field == null) {
                    field = BuriedUtil()
                }
                return field
            }
    }

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

    private fun buried(actName: String,actionType: String,targetId: String,targetName: String) {
        buried(actName, actionType, targetId, targetName,"")
    }

    private fun buried(actName: String, actionType: String) {
        buried(actName, actionType, "", "")
    }
    fun crash_report(version:String,msg:String){buried("安卓崩溃日志","crash_report",version,msg)}//,崩溃日志,1
    fun click_my_card(cardId:String){buried("点击我的页面顶部的uni卡（传cardid）","click_my_card",cardId,"")}//,点击车控悬浮按钮,1
    fun click_uni_card(cardId:String){buried("点击uni页面的uni卡（不管是否购买，传cardid）","click_uni_card",cardId,"")}//,点击车控悬浮按钮,1
    fun click_floaticon(){buried("点击车控悬浮按钮","click_floaticon")}//,点击车控悬浮按钮,1
    fun open_homepage(){buried("请求首页推荐列表时","open_homepage")}//,请求首页推荐列表时,1
    fun click_sharewechat(){buried("点击微信好友分享","click_sharewechat")}//, 点击微信好友分享,1
    fun click_sharecircle(){buried("点击微信朋友圈分享","click_sharecircle")}//, 点击微信朋友圈分享,1
    fun click_shareqq(){buried("点击QQ好友分享","click_shareqq")}//, 点击QQ好友分享,1
    fun click_shareqzone(){buried("点击QQ空间分享","click_shareqzone")}//, 点击QQ空间分享,1
    fun click_shareweibo(){buried("点击新浪微博分享","click_shareweibo")}//, 点击新浪微博分享,1
    fun click_sharecopy(){buried("点击复制链接分享","click_sharecopy")}//, 点击复制链接分享,1
    fun click_ad(adName:String,adId:String){buried("点击启动广告","click_ad",adName,adId)}//【adId】,点击启动广告【广告名称】,1
    fun open_find(){buried("发现点击数","open_find")}//,发现点击数,1
    fun open_activity(){buried("活动点击数","open_activity")}//,活动点击数,1
    fun open_uni(){buried("Uni点击数","open_uni")}//,Uni点击数,1
    fun open_mall(){buried("商城点击数","open_mall")}//,商城点击数,1
    fun open_my(){buried("我的点击数","open_my")}//,我的点击数,1
    fun click_search(){buried("点击首页搜索输入框","click_search")}//,点击首页搜索输入框,1
    fun click_fatie(){buried("点击首页发帖","click_fatie")}//,点击首页发帖,1
    fun click_recommend(){buried("查看推荐TAB","click_recommend")}//,查看推荐TAB,1
    fun click_information(){buried("查看资讯TAB","click_information")}//,查看资讯TAB,1
    fun click_daka(){buried("查看大咖TAB","click_daka")}//,查看大咖TAB,1
    fun click_shequ(){buried("查看社区TAB","click_shequ")}//,查看社区TAB,1
    fun click_find_banner(bannerId:String,bannerName:String){buried("banner名字","click_find_banner",bannerId,bannerName)}//【banner的id】,点击首页banner【banner名字】,1
    fun click_find_func(adId:String,adName:String){buried("点击推荐广告","click_find_func",adId,adName)}//【广告的id】,点击推荐广告（三大上门的）【广告名字】,1
    fun click_information_more(){buried("点击资讯专题更多","click_information_more")}//,点击资讯专题更多,1
    fun click_zhuanti(ztId:String,ztName:String){buried("专题名称","click_zhuanti",ztId,ztName)}//【专题ID】,点击专题(专题列表同)【专题名称】,1
    fun click_zixun(zxId:String,zxName:String){buried("资讯名称","click_zixun",zxId,zxName)}//【资讯ID】,点击资讯（推荐列表资讯同）【资讯名称】,1
    fun click_quanzi(qzId:String,qzName:String){buried("圈子名称","click_quanzi",qzId,qzName)}//【圈子ID】,点击圈子（圈子列表同）【圈子名称】,1
    fun click_huati(htId:String,htName:String){buried("话题名称","click_huati",htId,htName)}//【话题ID】,点击话题（话题列表同）【话题名称】,1
    fun click_tiezi(tzId:String,tzName:String){buried("帖子ID","click_tiezi",tzId,tzName)}//【帖子ID】,点击帖子
    fun click_fabuhuodong(){buried("发布活动","click_fabuhuodong")}//,发布活动,1
    fun click_activity_ad(adId: String,adName: String){buried("点击活动广告","click_activity_ad",adId,adName)}//【广告ID】,点击活动广告【广告名称】,1
    fun click_item_activity(actId:String,actName: String){buried("点击某项活动","click_item_activity",actId,actName)}//【活动ID】,点击某项活动【活动名称】,1
    fun click_uni_banner(bannerId: String,bannerName: String){buried("点击Unibanner","click_uni_banner",bannerId,bannerName)}//【banner的id】,点击Unibanner(有车时)【banner名字】,1
    fun click_uni_service(servieName:String){buried("点击Uni优质服务","click_uni_service","",servieName)}//,点击Uni优质服务【优质服务名字】,1
    fun click_uni_orderCar(){buried("点击Uni订车","click_uni_orderCar")}//,点击Uni订车,1
    fun click_uni_orderCar(targetId: String,targetName: String){buried("点击Uni订车","click_uniBanner_orderCar",targetId,targetName)}//,点击Uni订车,1
    fun click_mail_jifenmingxi(){buried("点击商城U币明细","click_mail_jifenmingxi")}//,点击商城U币明细,1
    fun click_my_woyaoqiandao(){buried("点击我要签到","click_my_woyaoqiandao")}//,点击我要签到,1
    fun click_my_setting(){buried("点击设置","click_my_setting")}//,点击设置,1
    fun click_my_msg(){buried("点击消息","click_my_msg")}//,点击消息,1
    fun click_my_msg_xitong(){buried("系统消息","click_my_msg_xitong")}//,系统消息,1
    fun click_my_msg_hudong(){buried("互动消息","click_my_msg_hudong")}//,互动消息,1
    fun click_my_msg_jiaoyi(){buried("交易消息","click_my_msg_jiaoyi")}//,交易消息,1
    fun click_my_msg_hongbao(){buried("红包消息","click_my_msg_hongbao")}//,红包消息,1
    fun click_my_head(){buried("点击头像","click_my_head")}//,点击头像,1
    fun click_my_shenfen(){buried("点击身份","click_my_shenfen")}//,点击身份,1
    fun click_my_jifen(){buried("点击U币","click_my_jifen")}//,点击U币,1
    fun click_my_fans(){buried("点击粉丝","click_my_fans")}//,点击粉丝,1
    fun click_my_focuse(){buried("点击关注","click_my_focuse")}//,点击关注,1
    fun click_my_fabu(){buried("点击发布","click_my_fabu")}//,点击发布,1
    fun click_my_favor(){buried("点击收藏","click_my_favor")}//,点击收藏,1
    fun click_my_qushengji(){buried("点击去升级","click_my_qushengji")}//,点击去升级,1
    fun click_my_chengzhangzhi(){buried("点击成长值","click_my_chengzhangzhi")}//,点击成长值,1
    fun click_my_liaojiexunzhang(){buried("点击了解勋章","click_my_liaojiexunzhang")}//,点击了解勋章,1
    fun click_my_wodeaiche(){buried("点击我的爱车","click_my_wodeaiche")}//,点击我的爱车,1
    fun click_my_changyonggongneng(name:String){buried("点击常用功能","click_my_changyonggongneng","",name)}//,点击常用功能,1
    fun stay_home_faxian(time:String){buried("发现页停留时长","stay_home_faxian","","",time)}//,发现页停留时长,1
    fun stay_home_huodong(time: String){buried("活动页停留时长","stay_home_huodong","","",time)}//,活动页停留时长,1
    fun stay_articial(actId: String,actName: String,time: String){buried("资讯详情（某一）停留时长","stay_articial",actId,actName,time)}//【资讯ID】,资讯详情（某一）停留时长,1
    fun stay_topic(targetId: String,targetName: String,time: String){buried("话题（某一）停留时长","stay_topic",targetId,targetName,time)}//【话题ID】,话题（某一）停留时长,1
    fun stay_tiezi(targetId: String,targetName: String,time: String){buried("帖子（某一）停留时长","stay_tiezi",targetId,targetName,time)}//【帖子ID】,帖子（某一）停留时长,1
    fun stay_quanzi(targetId: String,targetName: String,time: String){buried("圈子（某一）停留时长","stay_quanzi",targetId,targetName,time)}//【圈子ID】,圈子（某一）停留时长,1
    fun stay_product(targetId: String,targetName: String,time: String){buried("商品页（某一）停留时长","stay_product",targetId,targetName,time)}//【商品ID】,商品页（某一）停留时长,1
    fun stay_my_carownerauthority(time: String){buried("车主认证停留时长","stay_my_carownerauthority","","",time)}//,车主认证停留时长,1
    fun stay_my_circle(time: String){buried("我的圈子停留时长","stay_my_circle","","",time)}//,我的圈子停留时长,1
    fun stay_my_activity(time: String){buried("我的活动停留时长","stay_my_activity","","",time)}//,我的活动停留时长,1
    fun stay_my_favoriate(time: String){buried("我的收藏停留时长","stay_my_favoriate","","",time)}//,我的收藏停留时长,1
    fun stay_my_order(time: String){buried("我的订单停留时长","stay_my_order","","",time)}//,我的订单停留时长,1
    fun stay_my_edit(time: String){buried("编辑个人信息页停留时长","stay_my_edit","","",time)}//,编辑个人信息页停留时长,1
    fun stay_my_fans(time: String){buried("粉丝页停留时长","stay_my_fans","","",time)}//,粉丝页停留时长,1
    fun stay_my_follow(time: String){buried("关注页停留时长","stay_my_follow","","",time)}//,关注页停留时长,1

    /**
     * 商城 START
    * */
    //【banner的id】,点击商城banner【banner名字】,1
    fun clickMallBanner(bannerId: String,bannerName: String){
        buried("app商城_顶部banner_点击","click_mall_banner",bannerId,bannerName)
    }
    //【商品id】,点击商品【商品名称】,1
    fun clickMall(pId:String,pName:String){
        buried("商品名称","click_mall_p",pId,pName)
    }
    //商城END
}