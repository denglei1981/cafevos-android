package com.changanford.shop.popupwindow

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
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
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : GoodsAttrsPop
 */
open class GoodsAttrsPop(val activity: AppCompatActivity, private val dataBean:GoodsDetailBean, var _skuCode:String,val control:GoodsDetailsControl): BasePopupWindow(activity) {
    private var viewDataBinding: PopGoodsSelectattributeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_goods_selectattribute))!!
    private var skuCodeLiveData: MutableLiveData<String> = MutableLiveData()
    private val mAdapter by lazy { GoodsAttributeIndexAdapter(skuCodeLiveData) }
    init {
        contentView=viewDataBinding.root
        initView()
        initData()
    }
    private fun initView(){
        setKeyboardAdaptive(true)
        setMaxHeight(ScreenUtils.getScreenHeight(context)/4*3)
        viewDataBinding.recyclerView.adapter=mAdapter
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
        viewDataBinding.btnSubmit.setOnClickListener {
            dismiss()
            OrderConfirmActivity.start(Gson().toJson(dataBean))
        }
    }
    @SuppressLint("StringFormatMatches")
    private fun initData(){
        viewDataBinding.model=dataBean
        dataBean.skuVos.forEach { it.skuCodeArr=it.skuCode.split("-") }
        mAdapter.skuVos=dataBean.skuVos
        mAdapter.setSkuCodes(_skuCode)
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
                        val price=if ("SECKILL" == dataBean.spuPageType) dataBean.fbPrice else dataBean.orginPrice
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
                    WCommonUtil.htmlToString(viewDataBinding.tvStock, "（${htmlStr}库存${nowStock}件）")
                    var isLimitBuyNum = false//是否限购
                    val max: Int = if (limitBuyNum in 1..nowStock) {
                        isLimitBuyNum = true
                        limitBuyNum
                    } else nowStock
                    viewDataBinding.addSubtractView.setMax(max, isLimitBuyNum)
                    control.bindingBtn(dataBean, _skuCode, viewDataBinding.btnSubmit, 1)
                }
            }
        }
        viewDataBinding.tvAccountPoints.apply {
            visibility=if(MConstant.token.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            text="${dataBean.acountFb}"
//            setHtmlTxt(context.getString(R.string.str_Xfb,"${dataBean.acountFb}"),"#00095B")
        }
        viewDataBinding.addSubtractView.setNumber(dataBean.buyNum,false)
        viewDataBinding.addSubtractView.numberLiveData.observe(activity) {
            dataBean.buyNum = it
            control.bindingBtn(dataBean, _skuCode, viewDataBinding.btnSubmit, 1)
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