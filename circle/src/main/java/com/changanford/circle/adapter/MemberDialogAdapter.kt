package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleDialogBeanItem
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.MemberDialogItemBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout


/**
 * @Author: lcw
 * @Date: 2020/11/23
 * @Des:
 */
class MemberDialogAdapter(
    private val context: Context,
    private val size: Int,
    val list: ArrayList<CircleMemberBean>
) :
    BaseAdapterOneLayout<CircleDialogBeanItem>(context, R.layout.member_dialog_item) {

    @SuppressLint("SetTextI18n")
    override fun fillData(vdBinding: ViewDataBinding?, item: CircleDialogBeanItem, position: Int) {
        val binding = vdBinding as MemberDialogItemBinding
        val linearParams = binding.llItem.layoutParams
        linearParams.width = getScreenWidth(context) / getItems()!!.size
        var count = size
        //如果选中列表的权限等于当前权限 踢出
        list.forEach { cirBean ->
            if (item.starName == cirBean.starOrderNumStr) {
                count--
            }
        }
        binding.llItem.layoutParams = linearParams
        binding.haveNum.text = "剩余名额：${item.surplusNum}"
        //剩余名额等于0或者大于筛选后的选中人数才能选择
        binding.checkbox.isEnabled =
            item.surplusNum.toInt() >= count && !(item.surplusNum.toInt() == 0 && count == 0)
        if (binding.checkbox.isEnabled) {
            binding.haveNum.visibility = View.INVISIBLE
            binding.checkbox.visibility = View.VISIBLE
        } else {
            binding.haveNum.visibility = View.VISIBLE
            binding.checkbox.visibility = View.INVISIBLE
        }
        binding.checkbox.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                getItems()?.forEachIndexed { index, bean ->
                    if (index == position) {
                        item.isCheck = true
                    } else {
                        bean.isCheck = false
                    }
                }
                notifyDataSetChanged()
            }
        }
        binding.bean = item
    }

    private fun getScreenWidth(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    //        checkbox.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                checkString = "星推官"
//                checkbox2.isChecked = false
//                checkbox3.isChecked = false
//            } else {
//                if (checkString == "星推官") {
//                    checkbox.isChecked = true
//                    checkbox2.isChecked = false
//                    checkbox3.isChecked = false
//                }
//            }
//        }
}