package com.changanford.shop.popupwindow

import android.text.TextUtils
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
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import com.changanford.shop.ui.order.OrderConfirmActivity
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.view.btn.KillBtnView
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : GoodsAttrsPop
 */
open class GoodsAttrsPop(val activity: AppCompatActivity, private val dataBean:GoodsDetailBean, var _skuCode:String?): BasePopupWindow(activity) {
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
            OrderConfirmActivity.start(activity,Gson().toJson(dataBean))
        }
    }
    private fun initData(){
        viewDataBinding.model=dataBean
        mAdapter.setSkuCodes(_skuCode)
        mAdapter.setList(dataBean.attributes)
        skuCodeLiveData.postValue(_skuCode)
        skuCodeLiveData.observe(activity,{ code ->
            if(TextUtils.isEmpty(code))return@observe
            _skuCode=code
            (dataBean.skuVos.find { it.skuCode==code }?:dataBean.skuVos[0]).apply {
                dataBean.skuImg=skuImg
                dataBean.skuId=skuId
                dataBean.fbPrice=fbPrice
                dataBean.stock=stock.toInt()
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
                val limitBuyNum:String=dataBean.limitBuyNum?:"0"
                val htmlStr=if(limitBuyNum!="0")"<font color=\"#00095B\">限购${dataBean.limitBuyNum}件</font> " else ""
                WCommonUtil.htmlToString( viewDataBinding.tvStock,"（${htmlStr}库存${stock}件）")
                val max: String =if(limitBuyNum!="0")limitBuyNum else stock
                viewDataBinding.addSubtractView.setMax(max.toInt())
                bindingBtn(stock.toInt(),viewDataBinding.btnSubmit)
            }

        })
        viewDataBinding.tvAccountPoints.apply {
            visibility=if(MConstant.token.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            setHtmlTxt(dataBean.acountFb,"#00095B")
        }
        viewDataBinding.addSubtractView.setNumber(dataBean.buyNum,false)
        viewDataBinding.addSubtractView.numberLiveData.observe(activity,{
            dataBean.buyNum= it
        })
    }

    private fun bindingBtn(stock:Int,btnSubmit: KillBtnView){
        val totalPayFb=dataBean.fbPrice.toInt()*dataBean.buyNum
        if(MConstant.token.isNotEmpty()&&dataBean.acountFb<totalPayFb){//积分余额不足
            btnSubmit.setStates(8)
        } else if(dataBean.secKillInfo!=null&&dataBean.now<dataBean.secKillInfo?.timeBegin!!){//秒杀未开始
            btnSubmit.setStates(7)
        }else if(stock<1){//库存不足,已售罄、已抢光
            btnSubmit.setStates(if("SECKILL"==dataBean.spuPageType)1 else 6,true)
        }else btnSubmit.setStates(5)
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