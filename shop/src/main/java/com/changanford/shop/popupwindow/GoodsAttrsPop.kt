package com.changanford.shop.popupwindow

import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeIndexAdapter
import com.changanford.shop.control.GoodsDetailsControl
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
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
        viewDataBinding.recyclerView.adapter=mAdapter
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
        viewDataBinding.btnSubmit.setOnClickListener {
            dismiss()
            OrderConfirmActivity.start(Gson().toJson(dataBean))
        }
    }
    private fun initData(){
        viewDataBinding.model=dataBean
        mAdapter.skuVos=dataBean.skuVos
        mAdapter.setSkuCodes(_skuCode)
        mAdapter.setList(dataBean.attributes)
        skuCodeLiveData.postValue(_skuCode)
        skuCodeLiveData.observe(activity,{ code ->
            dataBean.skuVos.apply {
                _skuCode=code
                (this.find { it.skuCode==code }?:this.find { it.fbPrice==dataBean.fbPrice }?:this[0]).apply {
                    dataBean.skuImg=skuImg
                    dataBean.skuId=skuId
                    dataBean.fbPrice=fbPrice
                    dataBean.orginPrice=orginPrice
                    if(!control.isInvalidSelectAttrs(_skuCode))dataBean.stock=stock.toInt()
                    dataBean.mallMallSkuSpuSeckillRangeId=mallMallSkuSpuSeckillRangeId
                    val skuCodeTxtArr= arrayListOf<String>()
                    for((i,item) in dataBean.attributes.withIndex()){
                        item.optionVos.find { mAdapter.getSkuCodes()[i+1]== it.optionId }?.let {
                            val optionName= it.optionName
                            skuCodeTxtArr.add(optionName)
                        }
                    }
                    dataBean.skuCodeTxts=skuCodeTxtArr
                    viewDataBinding.sku= this
                    GlideUtils.loadBD(GlideUtils.handleImgUrl(skuImg),viewDataBinding.imgCover)
                    val limitBuyNum:Int=(dataBean.limitBuyNum?:"0").toInt()
                    val htmlStr=if(limitBuyNum!=0)"<font color=\"#00095B\">限购${limitBuyNum}件</font> " else ""
                    val nowStock=dataBean.stock
                    WCommonUtil.htmlToString( viewDataBinding.tvStock,"（${htmlStr}库存${nowStock}件）")
                    val max: Int =if(limitBuyNum!=0) limitBuyNum else nowStock
                    viewDataBinding.addSubtractView.setMax(max)
                    control.bindingBtn(dataBean,_skuCode,viewDataBinding.btnSubmit)
                }
            }
        })
        viewDataBinding.tvAccountPoints.apply {
            visibility=if(MConstant.token.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            setHtmlTxt(dataBean.acountFb,"#00095B")
        }
        viewDataBinding.addSubtractView.setNumber(dataBean.buyNum,false)
        viewDataBinding.addSubtractView.numberLiveData.observe(activity,{
            dataBean.buyNum= it
            control.bindingBtn(dataBean,_skuCode,viewDataBinding.btnSubmit)
        })
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