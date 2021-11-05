package com.changanford.shop.popupwindow

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.shop.R
import com.changanford.shop.databinding.PopLayoutBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig



/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : PublicPop
 */
open class PublicPop(context: Context?): BasePopupWindow(context) {
    private var listener:OnPopClickListener?=null
    private var viewDataBinding: PopLayoutBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_layout))!!
    init {
        contentView=viewDataBinding.root
        initView()
    }
    private fun initView(){
        viewDataBinding.tvBtnLeft.setOnClickListener { this.listener?.onLeftClick()}
        viewDataBinding.tvBtnRight.setOnClickListener {
            this.listener?.onRightClick()
        }
    }
    fun showPopupWindow(content:String?,leftTxt:String?,rightTxt:String?,listener:OnPopClickListener?){
        this.listener=listener
        viewDataBinding.tvContent.text=content
        if(null!=leftTxt)viewDataBinding.tvBtnLeft.text=leftTxt
        if(null!=rightTxt)viewDataBinding.tvBtnRight.text=rightTxt
        this.showPopupWindow()
    }
    fun showPopupWindow(content:String?,rightTxt:String?,listener:OnPopClickListener?){
        this.listener=listener
        viewDataBinding.tvContent.text=content
        viewDataBinding.tvBtnLeft.visibility= View.GONE
        if(null!=rightTxt)viewDataBinding.tvBtnRight.text=rightTxt
        this.showPopupWindow()
    }
    fun setOnPopClickListener(listener:OnPopClickListener){
        this.listener=listener
    }
    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation().withScale(ScaleConfig().from(Direction.LEFT).to(Direction.RIGHT)).toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation().withScale(ScaleConfig().from(Direction.RIGHT).to(Direction.LEFT)).toDismiss()
    }
    interface OnPopClickListener {
        fun onLeftClick()

        fun onRightClick()
    }
}