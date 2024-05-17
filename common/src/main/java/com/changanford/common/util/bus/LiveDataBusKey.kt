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
    const val BUS_SHOW_LOAD_CONTENT = "BUS_SHOW_LOAD_CONTENT"

    /**
     * WebView传递
     */
    //分享
    const val WEB_SHARE = "agentWeb_shareTo"

    //分享-微信小程序
    const val WEB_SMALL_PROGRAM_WX_SHARE = "agentWeb_shareToSmallProgram"

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
    const val WEB_GET_LOCATION_SERVICE = "agentWeb_getLocation"

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

    const val INVOICE_ADDRESS_SUCCESS = "INVOICE_ADDRESS_SUCCESS"

    //数据库中取出到缓存
    const val COOKIE_DB = "cookie_from_db"

    //保存网络请求数据，用于缓存
    const val SAVE_NETWORK_RESULT = "save_network_result"

    //保存网络请求数据，用于缓存
    const val SAVE_NETWORK_RESULT_2 = "save_network_result_2"

    //先享车主领取U享卡接口调用
    const val GET_FIRST_UNICARD = "get_first_uni_card"

    //我的消息红点显示隐藏
    const val SHOULD_SHOW_MY_MSG_DOT = "should_show_my_msg_dot"

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

    const val MINE_REFRESH_CIRCLE_STATUS = "mine:refresh_circle_status" //审核后，刷新状态
    const val MINE_REFRESH_CIRCLE_STATUS_NO_PEOPLE =
        "mine:refresh_circle_status_no_people" //审核后，刷新状态

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
    const val FABUBAOMINGFINISHI = "FABUBAOMINGFINISHI" // 发布报名活动成功

    const val MINE_CANCEL_ACCOUNT = "mine:cancel_account" //取消注销申请，注销成功
    const val MINE_SIGN_FIX = "mine:sign_fix" //补签成功，刷新数据
    const val MINE_SIGN_SIGNED = "mine:sign_signed" //签到成功，刷新按钮
    const val HOME_UPDATE = "home:update" //首页更新弹框，在后台切换到前台时调用

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

    const val CREATE_LOCATION = "create_loccation" // 自定义位置

    const val CHOOSELOCATIONNOTHING = "chooselocationnothing" //选择不显示定位poi返回时
    const val ColseCHOOSELOCATION = "colsechooselocation" //关闭一级页面

    const val CREATE_COLSE_LOCATION = "CREATE_COLSE_LOCATION" //关闭er 级页面

    const val MINE_SIGN_WX_CODE = "mine:sign_wx_code"//登录微信code

    const val USER_LOGIN_STATUS = "sys:user_login_status"

    const val PICTURESEDITED = "picturesedited"  //图片编辑页面点击下一步
    const val LONGPOSTFM = "LONGPOSTFM"  //发长图图片编辑页面返回封面
    const val LIVE_OPEN_TWO_LEVEL = "LIVE_OPEN_TWO_LEVEL" // 二楼打开

    const val Conversation = "Conversation" //选择话题回调
    const val ConversationNO = "ConversationNo" //不选择任何话题回调


    const val NEWS_DETAIL_CHANGE = "NEWS_DETAIL_CHANGE"//详情数据更改。。。

    const val LIST_FOLLOW_CHANGE = "LIST_FOLLOW_CHANGE"//专题列表关注改变
    const val FOLLOW_USER_CHANGE = "followUserChange"

    const val SHOP_CREATE_ORDER_BACK = "shop_create_order_back"//商品创建订单回调

    const val CHANGE_TEACH_INFO = "CHANGE_TEACH_INFO" // 自定义位置


    const val CIRCLE_CREATE_QUESTION = "CIRCLE_CREATE_QUESTION" // 创建问题

    const val REMOVE_CAR = "remove_car" // 移除爱车。
    const val AGGREE_CAR = "AGGREE_CAR" // 弹窗认车。

    //银联支付
    const val WEB_OPEN_UNION_PAY = "agentWeb_openUnionPay"

    //银联支付回调-暂只代表云闪付
    const val WEB_OPEN_UNION_PAY_BACK = "agentWeb_unionPayBack"

    //选择优惠券回调
    const val COUPONS_CHOOSE_BACK = "coupons_choose_back"

    //开票成功
    const val GET_INVOICE = "get_invoice"

    // 单sku 申请退货 --成功
    const val SINGLE_REFUND = "single_refund"

    // 填写物流信息
    const val FILL_IN_LOGISTICS = "fillInLogistics"

    // 加入购物车成功
    const val ADD_TO_SHOPPING_CAR = "add_to_shopping_car"

    // 清空购物车
    const val SHOP_DELETE_CAR = "SHOP_DELETE_CAR"

    const val CHILD_COMMENT_STAR = "child_comment_star" // 对子评论点赞。

    const val REFRESH_POST_LIKE = "refreshPostLike"
    const val REFRESH_WAIT = "refreshWait"
    const val REFRESH_COMMENT_CIRCLE = "refreshCommentCircle"

    //获取用户认证车辆列表
    const val GET_USER_APPROVE_CAR = "getUserApproveCar"

    const val FORD_ALBUM_RESULT = "fordAlbumResult"
    const val FABUTOUPIAOITEM = "fabuTouPItem"
    const val CLEAR_EDIT_FOCUS_CHANGE = "clearEditFocusChange"
    const val CREATE_CIRCLE_ERROR = "createCircleError"
    const val DISMISS_FORD_PAI_DIALOG = "disMissFordDialog"

    const val MAIN_TAB_CHANGE = "mainTabChange"
    const val IS_CHECK_PERSONAL = "isCheckPersonal"
    const val UPDATE_PERSONAL_GIO = "updatePersonalGio"
    const val UPDATE_CIRCLE_DETAILS_GIO = "updateCircleDetailsGio"
    const val UPDATE_GOODS_DETAILS_GIO = "updateGoodsDetailsGio"
    const val UPDATE_TASK_LIST_GIO = "updateTaskListGio"
    const val UPDATE_MAIN_GIO = "updateMainGio"
    const val UPDATE_INFO_DETAIL_GIO = "updateInfoDetailGio"
    const val UPDATE_MAIN_CHANGE = "updateMainChange"
    const val DISMISS_PAY_WAITING = "dismissPayWait"
    const val SHOW_ERROR_ADDRESS = "showErrorAddress"
    const val REFUND_NOT_SHOP_SUCCESS = "refundNotShoppSuccess"
    const val LONG_POST_CONTENT = "longPostContent"
    const val LONG_POST_JIAO = "longPostJIAO"
    const val OPEN_CHOOSE_POST = "openChoosePost"
    const val CHOOSE_CAR_POST = "chooseCarPost"
    const val CHOOSE_CAR_TOPIC_ID = "chooseCarTopicId"
    const val CLICK_CAR = "clickCar"
    const val UPDATE_SEARCH_RESULT = "updateSearchResult"
    const val CLOSE_POLY = "closePoly"
    const val HOME_CIRCLE_CHECK_ID = "homeCircleCheckId"
    const val HOME_CIRCLE_HOT_BEAN = "homeCircleHotBean"
    const val UPDATE_NEWS_PICS_HEIGHT = "updateNewsPicsHeight"
    const val CIRCLE_INIT_HOT = "circleInitHot"
    const val SIGN_UP_SUCCESS = "signUpSuccess"
    const val REFRESH_INFORMATION_FRAGMENT = "refreshInformationFragment"
    const val REFRESH_POST_FRAGMENT = "refreshPostFragment"
    const val REFRESH_ACTS_FRAGMENT = "refreshActsFragment"
    const val REFRESH_SHOP_FRAGMENT = "refreshShopFragment"
    const val REFRESH_FOOT_CHECK = "refreshFootCheck"
    const val FOOT_UI_CAN_DELETE = "footUiCanDelete"
    const val REFRESH_INFORMATION_DATA = "refreshInformationData"
    const val REFRESH_POST_DATA = "refreshPostData"
    const val REFRESH_ACTS_DATA = "refreshActsData"
    const val REFRESH_SHOP_DATA = "refreshShopData"
    const val DELETE_INFORMATION_DATA = "deleteInformationData"
    const val DELETE_POST_DATA = "deletePostData"
    const val DELETE_ACT_DATA = "deleteActData"
    const val DELETE_SHOP_DATA = "deleteShopData"
    const val SHOP_ORDER_SEARCH = "shopOrderSearch"
    const val ORDERS_GOODS_SHOW_EMPTY = "ordersGoodsShowEmpty"
}

