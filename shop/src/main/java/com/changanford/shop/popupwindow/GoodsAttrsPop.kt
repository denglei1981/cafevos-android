package com.changanford.shop.popupwindow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.Attribute
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithAllSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.ui.LoadingDialog
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.AppUtils.getPackageName
import com.changanford.common.util.MConstant
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeIndexAdapter
import com.changanford.shop.api.ShopNetWorkApi
import com.changanford.shop.control.GoodsDetailsControl
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig


/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : GoodsAttrsPop
 */
open class GoodsAttrsPop(
    val activity: AppCompatActivity,
    private val dataBean: GoodsDetailBean,
    var _skuCode: String,
    val control: GoodsDetailsControl
) : BasePopupWindow(activity) {
    private var viewDataBinding: PopGoodsSelectattributeBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_goods_selectattribute))!!
    private var skuCodeLiveData: MutableLiveData<String> = MutableLiveData()
    private val mAdapter by lazy { GoodsAttributeIndexAdapter(skuCodeLiveData) }

    init {
        contentView = viewDataBinding.root
        initView()
        initData()
    }

    private fun initView() {
        setKeyboardAdaptive(true)
        setMaxHeight(ScreenUtils.getScreenHeight(context) / 4 * 3)
        viewDataBinding.apply {
            recyclerView.adapter = mAdapter
            imgClose.setOnClickListener { this@GoodsAttrsPop.dismiss() }
            btnBuy.setOnClickListener {
                if (btnBuy.getStates() == 6) {
                    if (control.isInvalidSelectAttrs(_skuCode)) {
                        "属性未选择完全".toast()
                        return@setOnClickListener
                    }
                    if (checkNotifySetting(activity)) {
                        activity.launchWithCatch {
                            val dialog = LoadingDialog(BaseApplication.curActivity)
                            dialog.show()
                            val bodyPostSet = MyApp.mContext.createHashMap()
                            bodyPostSet["skuId"] = dataBean.skuId

                            val rKey = getRandomKey()
                            ApiClient.createApi<ShopNetWorkApi>()
                                .ifOutStockSubscribe(
                                    bodyPostSet.header(rKey),
                                    bodyPostSet.body(rKey)
                                )
                                .onWithAllSuccess {
                                    dialog.dismiss()
                                    "已设置到货提醒,补货后将通知您".toast()
//                                it.msg.toast()
                                    skuCodeHasTips(true)
                                }.onWithMsgFailure {
                                    dialog.dismiss()
                                    it?.toast()
                                }
                        }
                    } else {
                        val dilaog = AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                        dilaog.setTitle("温馨提示")
                            .setMsg("是否前往设置修改消息推送权限？")
                            .setCancelable(true)
                            .setNegativeButton(
                                "取消", com.changanford.common.R.color.actionsheet_blue
                            ) {
                                dilaog.dismiss()
                            }
                            .setPositiveButton(
                                "去设置",
                                com.changanford.common.R.color.actionsheet_blue
                            ) {
                                com.changanford.common.wutil.WCommonUtil.openNotificationSetting(
                                    context
                                )
                            }.show()
                    }
                } else {
                    dismiss()
                    control.exchangeCtaClick()
                    OrderConfirmActivity.start(dataBean)
                }
            }
            btnCart.setOnClickListener {
                dismiss()
                control.addShoppingCart(1)
            }
        }
    }

    private fun starSetting() {
        val localIntent = Intent()
        //直接跳转到应用通知设置的代码：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0及以上
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", getPackageName(), null)
        } else //5.0以上到8.0以下
            localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        localIntent.putExtra("app_package", getPackageName())
        localIntent.putExtra("app_uid", activity.applicationInfo.uid)
        activity.startActivity(localIntent)

    }

    private fun checkNotifySetting(context: Context): Boolean {
        val manager = NotificationManagerCompat.from(
            context
        )
        return manager.areNotificationsEnabled()
    }


    private fun isOutStockSubscribe() {
        activity.launchWithCatch {
            val dialog = LoadingDialog(BaseApplication.curActivity)
            dialog.show()
            val bodyPostSet = MyApp.mContext.createHashMap()
            bodyPostSet["skuId"] = dataBean.skuId

            val rKey = getRandomKey()
            ApiClient.createApi<ShopNetWorkApi>()
                .isOutStockSubscribe(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .onSuccess {
                    dialog.dismiss()
                    skuCodeHasTips(it == true)
                }.onFailure {
                    dialog.dismiss()
                }
        }
    }

    private fun skuCodeHasTips(hasTips: Boolean) {
        control.bindingBtn(
            dataBean,
            _skuCode,
            viewDataBinding.btnBuy,
            viewDataBinding.btnCart,
            1, hasTips
        )
    }

    @SuppressLint("StringFormatMatches")
    private fun initData() {
        viewDataBinding.model = dataBean
        dataBean.skuVos.forEach { it.skuCodeArr = it.skuCode.split("-") }
        mAdapter.skuVos = dataBean.skuVos

        //没有选中sku时默认选中最低sku （113新增）
        if (control.isInvalidSelectAttrs(_skuCode)) {
            dataBean.skuVos.filter { it.stock.toInt() > 0 }
                .filter { it.skuStatus == "ON_SHELVE" }
                .sortedWith(compareBy { it.fbPrice.toLong() }).let {
                    if (it.isNotEmpty()) _skuCode = it[0].skuCode
                }
        }
        if (_skuCode.isEmpty()) {
//            val co = dataBean.skuVos[0].skuCode.split("-") as ArrayList<String>
//            var cos = ""
//            repeat(co.size) {
//                cos += "0-"
//            }
//            cos = cos.substring(0, cos.length - 1)
//            _skuCode = cos
            dataBean.skuVos
//                .filter { it.stock.toInt() > 0 }
//                .filter { it.skuStatus == "ON_SHELVE" }
                .sortedWith(compareBy { it.fbPrice.toLong() }).let {
                    if (it.isNotEmpty()) _skuCode = it[0].skuCode
                }
        }
        mAdapter.setSkuCodes(_skuCode)
        val useAttributes = ArrayList<Attribute>()
        if (_skuCode.contains("-")) {
            val list = _skuCode.split("-") as ArrayList<String>
            list.removeAt(0)
            list.forEachIndexed { index, s ->
                dataBean.attributes.forEachIndexed { _, attribute ->
                    val addAttribute = attribute.optionVos.find { it.optionId == s }
                    if (addAttribute != null) {
                        useAttributes.add(attribute)
                    }
                }
            }
        }
        if (useAttributes.isNotEmpty()) {
            dataBean.attributes = useAttributes
        }
        mAdapter.setList(dataBean.attributes)
        skuCodeLiveData.postValue(_skuCode)
        skuCodeLiveData.observe(activity) { code ->
            dataBean.skuVos.apply {
                _skuCode = code
                //首页取选中的sku 然后是最低sku价格的
                (find { it.skuCode == code } ?: find { it.fbPrice == dataBean.orFbPrice }
                ?: sortedBy { it.fbPrice.toFloat() }[0]).apply {
                    dataBean.skuId = skuId
                    if (!control.isInvalidSelectAttrs(_skuCode)) {
                        dataBean.stock = stock.toInt()
                        dataBean.skuImg = skuImg
                        dataBean.fbPrice = fbPrice
                        dataBean.orginPrice = orginPrice
                        viewDataBinding.addSubtractView.setIsAdd(true)
                        viewDataBinding.tvRmbPrice.setText(dataBean.getRMB(fbPrice))
                        viewDataBinding.tvFbPrice.setText(fbPrice)
                    } else {
                        dataBean.skuImg = dataBean.imgs[0]
//                        dataBean.stock = dataBean.allSkuStock
                        dataBean.stock = 1
                        dataBean.fbPrice = dataBean.orFbPrice
                        dataBean.orginPrice = dataBean.orginPrice0
                        viewDataBinding.addSubtractView.setIsAdd(false)
                        val price =
                            if ("SECKILL" == dataBean.spuPageType) dataBean.fbPrice else dataBean.orginPrice
                        viewDataBinding.tvFbPrice.setText(price)
                        viewDataBinding.tvRmbPrice.setText(dataBean.getRMB(price))
                    }
                    viewDataBinding.addSubtractView.setIsUpdateBuyNum(
                        !control.isNoStock(
                            _skuCode,
                            dataBean.skuVos
                        )
                    )
                    dataBean.price = dataBean.orginPrice
                    dataBean.mallMallSkuSpuSeckillRangeId = mallMallSkuSpuSeckillRangeId
                    control.memberExclusive(dataBean)
                    val skuCodeTxtArr = arrayListOf<String>()
                    for ((i, item) in dataBean.attributes.withIndex()) {
                        item.optionVos.find { mAdapter.getSkuCodes()[i + 1] == it.optionId }?.let {
                            val optionName = it.optionName
                            skuCodeTxtArr.add(optionName)
                        }
                    }
                    dataBean.skuCodeTxts = skuCodeTxtArr
                    viewDataBinding.sku = this
                    viewDataBinding.imgCover.load(dataBean.skuImg)
                    val limitBuyNum: Int = dataBean.getLimitBuyNum()
                    val htmlStr =
                        if (limitBuyNum != 0) "<font color=\"#1700f4\">限购${limitBuyNum}件</font> " else ""
                    val nowStock = dataBean.stock
                    WCommonUtil.htmlToString(
                        viewDataBinding.tvStock,
                        "（${htmlStr}库存${nowStock}件）"
                    )
                    viewDataBinding.tvStock.visibility = View.INVISIBLE
                    var isLimitBuyNum = false//是否限购
                    val max: Int = if (limitBuyNum in 1..nowStock) {
                        isLimitBuyNum = true
                        limitBuyNum
                    } else nowStock
                    viewDataBinding.addSubtractView.setMax(max, isLimitBuyNum)

                    if (nowStock == 0&&!control.isInvalidSelectAttrs(_skuCode)) {
                        isOutStockSubscribe()
                    } else {
                        control.bindingBtn(
                            dataBean,
                            _skuCode,
                            viewDataBinding.btnBuy,
                            viewDataBinding.btnCart,
                            1
                        )
                    }
                }
            }
        }
        viewDataBinding.tvAccountPoints.apply {
            visibility = if (MConstant.token.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            text = "${dataBean.acountFb}"
//            setHtmlTxt(context.getString(R.string.str_Xfb,"${dataBean.acountFb}"),"#1700f4")
        }
        viewDataBinding.addSubtractView.setNumber(dataBean.buyNum, false)
        viewDataBinding.addSubtractView.numberLiveData.observe(activity) {
            dataBean.buyNum = it
            control.bindingBtn(dataBean, _skuCode, viewDataBinding.btnBuy, null, 1)
        }
//        viewDataBinding.tvFbLine.visibility=if(dataBean.getLineFbEmpty())View.GONE else View.VISIBLE
    }

    //动画
    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }
}