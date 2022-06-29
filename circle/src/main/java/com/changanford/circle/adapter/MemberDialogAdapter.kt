package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleDialogBeanItem
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.MemberDialogItemBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.ui.view.WTextView


/**
 * @Author: lcw
 * @Date: 2020/11/23
 * @Des:
 */
class MemberDialogAdapter(context: Context, val list: ArrayList<CircleMemberBean>) :
    BaseAdapterOneLayout<CircleDialogBeanItem>(context, R.layout.member_dialog_item) {
    private var lastCheckBox:CheckBox?=null
    var tvHaveNum: WTextView? = null
    var tvConfirm:TextView?=null
    @SuppressLint("SetTextI18n")
    override fun fillData(vdBinding: ViewDataBinding?, item: CircleDialogBeanItem, position: Int) {
        val binding = vdBinding as MemberDialogItemBinding
        binding.checkbox.apply {
            isChecked=item.isCheck
            if(isChecked)lastCheckBox=this
            setOnCheckedChangeListener { _, isChecked ->
                item.isCheck=isChecked
                updateCheck(this,item)
            }
        }
        binding.bean = item
    }
    private fun updateCheck(checkBox: CheckBox,item: CircleDialogBeanItem){
        val isChecked=checkBox.isChecked
        //剩余名额
        val surplusNum=item.surplusNum.toInt()
        tvHaveNum?.visibility=if(isChecked){
            tvHaveNum?.setText("$surplusNum")
            lastCheckBox?.isChecked=false
            View.VISIBLE
        }else View.INVISIBLE
        //筛选可被设置当前身份的成员数（从被选择的成员中）
        val filterResults =if(isChecked)list.filter { it.starOrderNumStr!=item.starName }.size else 0
        tvConfirm?.apply {
            isEnabled= filterResults in 1..surplusNum
            setBackgroundResource(if(isEnabled)R.drawable.shape_00095b_20dp else R.drawable.shape_dd_20dp)
        }
        lastCheckBox=if(isChecked)checkBox else null
    }
}