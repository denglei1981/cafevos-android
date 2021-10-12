package com.changanford.shop.popupwindow

import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.SkuVo
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeAdapter
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import com.changanford.shop.utils.WCommonUtil
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : GoodsAttrsPop
 */
open class GoodsAttrsPop(val activity: AppCompatActivity, private val dataBean:GoodsDetailBean): BasePopupWindow(activity) {
    private var viewDataBinding: PopGoodsSelectattributeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_goods_selectattribute))!!
    private val colorAdapter by lazy { GoodsAttributeAdapter(0) }
    private val specificationAdapter by lazy { GoodsAttributeAdapter(0) }
    var skuItem: MutableLiveData<SkuVo> = MutableLiveData()
    init {
        contentView=viewDataBinding.root
        initView()
        initData()
    }
    private fun initView(){
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
        viewDataBinding.rvColor.adapter=colorAdapter
        viewDataBinding.rvSpecifications.adapter=specificationAdapter
    }
    private fun initData(){
        viewDataBinding.model=dataBean
        skuItem.postValue(dataBean.skuVos[0])
        skuItem.observe(activity,{
            viewDataBinding.sku=it
            GlideUtils.loadBD(GlideUtils.handleImgUrl(it.skuImg),viewDataBinding.imgCover)
            val limitBuyNum=dataBean.limitBuyNum
            val htmlStr=if(limitBuyNum!=null)"<font color=\"#00095B\">限购${dataBean.limitBuyNum}件</font> " else ""
            WCommonUtil.htmlToString( viewDataBinding.tvStock,"（${htmlStr}库存${skuItem.value?.stock}件）")
            val max=limitBuyNum?:it.stock
            viewDataBinding.addSubtractView.setMax(max.toInt())
        })
        colorAdapter.setList(arrayListOf("红色","黑色","蓝色"))
        specificationAdapter.setList(arrayListOf("64G","128G","512G"))
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