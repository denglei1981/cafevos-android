package com.changanford.circle.widget.pop

import android.app.Activity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleSelectTypeAdapter
import com.changanford.circle.databinding.PopCreateCircleSelecttypeBinding
import com.changanford.common.bean.NewCirceTagBean
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 * @Author : wenke
 * @Time : 2022/2/21
 * @Description : CircleSelectType
 */
class CircleSelectTypePop(activity: Activity,val listener:OnSelectedBackListener):BasePopupWindow(activity) {
    private var viewDataBinding: PopCreateCircleSelecttypeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_create_circle_selecttype))!!
    private val mAdapter by lazy { CircleSelectTypeAdapter() }
    init {
        contentView=viewDataBinding.root
        initData()
    }
    private fun initData(){
        viewDataBinding.apply {
            recyclerView.adapter=mAdapter
            btnCancel.setOnClickListener { dismiss() }
            btnDetermine.setOnClickListener {
                listener.onSelectedBackListener(mAdapter.getSelectItemBean())
                dismiss()
            }
        }
        val dataList= arrayListOf<NewCirceTagBean>()
        for (i in 0..3){
            dataList.add(NewCirceTagBean(tagName = "item$i"))
        }
        mAdapter.setList(dataList)
    }
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
interface OnSelectedBackListener {
    fun onSelectedBackListener(itemBean:NewCirceTagBean?)
}