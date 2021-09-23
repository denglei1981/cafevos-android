package com.changanford.shop.popupwindow

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.shop.R
import com.changanford.shop.databinding.PopSetnoticeBinding
import com.changanford.shop.utils.WCommonUtil
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig



/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : 设置通知
 */
open class SetNoticPop(context: Context?): BasePopupWindow(context) {
    private var viewDataBinding: PopSetnoticeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_setnotice))!!
    init {
        contentView=viewDataBinding.root
        initView()
    }
    private fun initView(){
        viewDataBinding.tvCancel.setOnClickListener { this.dismiss() }
        viewDataBinding.tvToSetUp.setOnClickListener {
            WCommonUtil.toSetNotice(context)
        }
    }
    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation().withScale(ScaleConfig().from(Direction.LEFT).to(Direction.RIGHT)).toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation().withScale(ScaleConfig().from(Direction.RIGHT).to(Direction.LEFT)).toDismiss()
    }
}