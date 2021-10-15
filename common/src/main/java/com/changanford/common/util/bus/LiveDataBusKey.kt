package com.changanford.common.util.bus

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.LiveDataBusKey
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/22 11:51
 * @Description: 　
 * *********************************************************************************
 */
object LiveDataBusKey {
    //支付宝支付结果
    const val ALIPAY_RESULT = "alipay_result"

    //微信支付结果
    const val WXPAY_RESULT = "wxpay_result"

    const val WX_SHARE_BACK = "wx_share_back"

    const val RJP_TYPE = "RJPTYPE"

    const val BUS_HIDE_BOTTOM_TAB = "HIDE_BOTTOM_TAB"

    /**
     * WebView传递
     */
    //分享
    const val WEB_SHARE = "agentWeb_shareTo"

    //设置头部
    const val WEB_SET_NAV_TITLE = "agentWeb_setNavTitle"

    //支付
    const val WEB_OPEN_PAY = "agentWeb_openPay"

    //显示隐藏导航栏
    const val WEB_NAV_HID = "agentWeb_isNavigationHidden"

//登录成功
//    const val WEB_LOGIN_SUCCESS = "agentWeb_loginApp"

    //上传图片
    const val WEB_UPLOAD_IMG = "agentWeb_uploadImg"

    //H5获取经纬度
    const val WEB_GET_LOCATION = "agentWeb_getLocation"

    //获取cac token
    const val WEB_GET_CACTOKEN = "agentWeb_getCacToken"

    //登录app
    const val WEB_LOGIN_APP = "agentWeb_loginApp"

    //绑定手机号
    const val WEB_BIND_PHONE = "agentWeb_bindPhone"

    //自定义返回
    const val WEB_BACKEVENT = "agentWeb_setBackEvent"

    //关闭页面
    const val WEB_CLOSEPAGE = "agentWeb_closepage"

    //h5选择地址
    const val WEB_CHOOSE_ADDRESS = "agentWeb_choose_address"

    //获取用户个人信息
    const val WEB_GET_MYINFO = "agentWeb_get_myInfo"

    //获取用户U享卡信息
    const val WEB_GET_UNICARDS_LIST = "agentWeb_get_uniCards_list"

    //扫码识别
    const val WEB_SHOW_SCAN = "agentWeb_show_scan"

    //H5订单支付
    const val WEB_ORDER_PAY = "mine:agentWeb_order_pay"

    //H5订单支付回调
    const val WEB_ORDER_PAY_STATUS = "mine:agentWeb_order_pay_status"
    const val LOGIN_OUT = "LOGIN_OUT"

    //地址列表点击后回调
    const val MINE_CHOOSE_ADDRESS_SUCCESS = "mine:choose_address_success"

    //数据库中取出到缓存
    const val COOKIE_DB = "cookie_from_db"

    //保存网络请求数据，用于缓存
    const val SAVE_NETWORK_RESULT = "save_network_result"

    //保存网络请求数据，用于缓存
    const val SAVE_NETWORK_RESULT_2 = "save_network_result_2"

    //先享车主领取U享卡接口调用
    const val GET_FIRST_UNICARD = "get_first_uni_card"

    //中间页隐藏悬浮按钮
    const val SHOULD_HIDE_FLOATBAR = "should_hide_floatbar"

    //获取当前默认车辆的vin号
    const val GET_CUR_VIN = "get_cur_vin"

    /**
     * 收藏 发布 管理  一键清楚  删除  发送指令
     */
    const val MINE_MANAGER_SET = "MineSet"

    const val MINE_IS_BIND = "mine:is_bind"

    const val MINE_MEMBER_INFO_ID = "mine:member_info_id"

    const val MINE_MEMBER_INFO_TYPE = "mine:member_info_name"

    const val MINE_ADDRESS_EDIT = "mine:address_edit"


    const val MINE_DELETE_ADDRESS = "mine:delete_address"

    const val MINE_UPDATE_ADDRESS = "mine:update_address"

    const val MINE_SUBMIT_FEEDBACK_SUCCESS = "mine:submit_feedback_success"

    const val MINE_PROVINCE = "mine:province"

    const val MINE_CITY = "mine:city"

    const val MINE_DISTRICT = "mine:district"

    const val MINE_LIKE = "mine:like"

    const val MINE_FANS_TYPE = "mine:fans_type"

    const val MINE_CIRCLE_ID = "mine:circle_id"

    const val MINE_CIRCLE_SET = "mine:circle_set" //圈子操作

    const val MINE_IS_FOLLOW = "mine:is_follow" //是否发布


    const val MINE_SIGN_OTHER_CODE = 18 //三方登录未绑定

    const val MINE_CAR_AUTH_ING = "mine:car_auth_ing"//车辆认证中

    const val MINE_INDUSTRY = "mine:industry" //行业选择

    const val MINE_GET_MEDAL = "mine:get_medal"//领取勋章

    const val MINE_CREATE_CIRCLE_SUCCESS = "mine:create_circle_success"//创建圈子成功

    const val MINE_DELETE_CIRCLE_USER = "mine:delete_circle_user"//删除圈子成员

    const val MINE_CAR_CARD_NUM = "mine:mine_card_num"//添加车牌成功

    const val MINE_UNI_CARD_CHOOSE_CAR = "mine:uni_card_choose_car" // 购卡选择车辆

    const val UNI_CARD_BUY_SUCCESS = "uni:uni_card_buy_success"//购买uni卡成功

    const val UNI_CARD_SHOW_DIALOG = "uni:uni_card_show_dialog"//打卡uni页面

    const val UNI_USE_LOG_TOTAL_NUM = "mine:uni_use_log_total" // uni卡使用记录统计

    const val UNI_UPDATE_CARD = "mine:uni_update_card" // uni卡升级成功

    const val H5POST_SUCCESS = "h5postsuccess" //h5调用发帖成功

    const val CIRCLE_POST_SUCCESS = "circle_success" //圈子发帖成功

    const val TOP_POST_SUCCESS = "top_success" //圈子发帖成功

    const val MINE_UNI_CARD_CHECK = "mine:uni_card_check" //uni切换

    const val MINE_SET_HEAD_CASE_CLICK = "mine:set_head_case_click"//设置头像框点击事件

    const val MINE_SET_HEAD_CASE_FRESHEN = "mine:set_head_case_freshen"//刷新头像框数据

    const val MINE_UNI_DEALER = "mine:uni_dealer"//专属经销商

    const val HOME_CIRCLE_MEMBER_MANAGE = "home:CircleMemberManage"//成员管理checkbox变化

    const val HOME_CIRCLE_MEMBER_MANAGE_FINISH = "home:CircleMemberManageFinish"//成员管理关闭

    const val HOME_CIRCLE_MEMBER_FINISH = "home:CircleMemberFinish"//成员关闭

    const val STAR_EXCL_SIVEBRICK_CARD = "card:CircleMemberManageFinish"//跳转专属经销商卡

    const val MINE_UNI_SERVICE_DEALER_SUCCESS = "mine:uni_service_dealer_success" // 专属经销商购买成功

    const val MINE_CANCEL_ACCOUNT = "mine:cancel_account" //取消注销申请，注销成功

    /**
     * 车主认证，crm提交资料成功
     */
    const val MINE_ADD_CAR_SUCCESS = "mine:add_crm_car_success"

    /**
     * 车辆卡片，请求成功后发送通知
     */
    const val MINE_CAR_BANNER_REFRESH = "mine:car_banner_refresh"

    /**
     * 获取经销商
     */
    const val MINE_SERVICE_DEALER = "mine:service_dealer"

    /**
     * H5调取添加车牌，需要回调车牌，不请求接口
     */
    const val MINE_ADD_PLATE_NUM = "mine:add_plate_num"

    /**
     * 粉丝挖掘弹框
     */
    const val MINE_SHOW_FANS_POP = "mine:show_fans_pop"


    const val JUMP_JRSDK = "jump_jrsdk"

    const val JUMP_JR_BACK = "jump_jr_back"

    const val CHOOSELOCATION = "chooselocation" //选择定位poi返回时
    const val CHOOSELOCATIONNOTHING = "chooselocationnothing" //选择不显示定位poi返回时
    const val ColseCHOOSELOCATION = "colsechooselocation" //关闭一级页面

    const val MINE_SIGN_WX_CODE = "mine:sign_wx_code"//登录微信code

    const val USER_LOGIN_STATUS = "sys:user_login_status"

    const val PICTURESEDITED ="picturesedited"  //图片编辑页面点击下一步

    const val LONGPOSTFM ="LONGPOSTFM"  //发长图图片编辑页面返回封面
    const val LIVE_OPEN_TWO_LEVEL = "LIVE_OPEN_TWO_LEVEL" // 二楼打开

    const val Conversation= "Conversation" //选择话题回调


    const val  NEWS_DETAIL_CHANGE="NEWS_DETAIL_CHANGE"//详情数据更改。。。

}

