package com.changanford.shop.popupwindow

import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeIndexAdapter
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
open class GoodsAttrsPop(val activity: AppCompatActivity, private val dataBean:GoodsDetailBean, var _skuCode:String): BasePopupWindow(activity) {
    private var viewDataBinding: PopGoodsSelectattributeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_goods_selectattribute))!!
    private var skuCodeLiveData: MutableLiveData<String> = MutableLiveData()
    private val mAdapter by lazy { GoodsAttributeIndexAdapter(skuCodeLiveData) }
    init {
        contentView=viewDataBinding.root
        initView()
        initData()
    }
    private fun initView(){
        viewDataBinding.recyclerView.adapter=mAdapter
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
        viewDataBinding.btnSubmit.setOnClickListener {
            dismiss()
            OrderConfirmActivity.start(activity,Gson().toJson(dataBean))
        }
    }
    private fun initData(){
        viewDataBinding.model=dataBean
        mAdapter.setSkuCodes(_skuCode)
        mAdapter.setList(dataBean.attributes)
        skuCodeLiveData.postValue(_skuCode)
        skuCodeLiveData.observe(activity,{ code ->
            _skuCode=code
            val findItem=dataBean.skuVos.find { it.skuCode==code }?:dataBean.skuVos[0]
            viewDataBinding.sku= findItem
            GlideUtils.loadBD(GlideUtils.handleImgUrl(findItem.skuImg),viewDataBinding.imgCover)
            val limitBuyNum=dataBean.limitBuyNum
            val htmlStr=if(limitBuyNum!=null)"<font color=\"#00095B\">限购${dataBean.limitBuyNum}件</font> " else ""
            WCommonUtil.htmlToString( viewDataBinding.tvStock,"（${htmlStr}库存${findItem.stock}件）")
            val max=limitBuyNum?:findItem.stock
            viewDataBinding.addSubtractView.setMax(max.toInt())
            bindingBtn(findItem.stock.toInt())
        })
        viewDataBinding.addSubtractView.setNumber(dataBean.buyNum,false)
        viewDataBinding.addSubtractView.numberLiveData.observe(activity,{
            dataBean.buyNum=it
        })
    }
    private fun bindingBtn(stock:Int){
        if(stock<1){
            viewDataBinding.btnSubmit.setStates(if("SECKILL"==dataBean.spuPageType)1 else 6,true)
        }else viewDataBinding.btnSubmit.setStates(5)
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