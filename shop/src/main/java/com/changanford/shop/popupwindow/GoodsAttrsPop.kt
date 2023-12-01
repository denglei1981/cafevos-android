package com.changanford.shop.popupwindow

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.common.bean.Attribute
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.load
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeIndexAdapter
import com.changanford.shop.control.GoodsDetailsControl
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.utils.ScreenUtils
import com.changanford.shop.utils.WCommonUtil
import com.faendir.rhino_android.RhinoAndroidHelper
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.Undefined
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
                dismiss()
                control.exchangeCtaClick()
                //这里参数context使用Android的Context
                val ctx= RhinoAndroidHelper(context).enterContext()
                val scope: Scriptable = ctx.initStandardObjects()
                var result: Any? = null
                try {
                    val o = ctx.evaluateString(scope, "18 > 17", "", 1, null)
                    if (o !== Scriptable.NOT_FOUND && o !is Undefined) {
                        result = o as Any //执行
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                OrderConfirmActivity.start(dataBean)
            }
            btnCart.setOnClickListener {
                dismiss()
                control.addShoppingCart(1)
            }
        }

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
            val co = dataBean.skuVos[0].skuCode.split("-") as ArrayList<String>
            var cos = ""
            repeat(co.size) {
                cos += "0-"
            }
            cos = cos.substring(0, cos.length - 1)
            _skuCode = cos
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
        dataBean.attributes = useAttributes
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
                        dataBean.stock = dataBean.allSkuStock
                        dataBean.fbPrice = dataBean.orFbPrice
                        dataBean.orginPrice = dataBean.orginPrice0
                        viewDataBinding.addSubtractView.setIsAdd(false)
                        val price =
                            if ("SECKILL" == dataBean.spuPageType) dataBean.fbPrice else dataBean.orginPrice
                        viewDataBinding.tvFbPrice.setText(price)
                        viewDataBinding.tvRmbPrice.setText(dataBean.getRMB(price))
                    }
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
                        if (limitBuyNum != 0) "<font color=\"#00095B\">限购${limitBuyNum}件</font> " else ""
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
        viewDataBinding.tvAccountPoints.apply {
            visibility = if (MConstant.token.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            text = "${dataBean.acountFb}"
//            setHtmlTxt(context.getString(R.string.str_Xfb,"${dataBean.acountFb}"),"#00095B")
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