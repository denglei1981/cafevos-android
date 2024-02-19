package com.changanford.car.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.common.adapter.CarHomePicAdapter
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.databinding.ItemHomeCarHistoryBinding
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomeTipsAdapter :
    BaseQuickAdapter<InfoDataBean, BaseDataBindingHolder<ItemHomeCarHistoryBinding>>(R.layout.item_home_car_history) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeCarHistoryBinding>,
        item: InfoDataBean
    ) {
        val picAdapter = CarHomePicAdapter(item.type == 3)
        holder.dataBinding?.let {
//            setLayoutWidth(it.clContent)
            if (!item.pics.isNullOrEmpty()) {
                val layoutManager = LinearLayoutManager(context)
               it.ryPc.layoutManager = layoutManager

               it.ryPc.adapter = picAdapter
                picAdapter.setOnItemClickListener { _, _, _ ->
                    JumpUtils.instans?.jump(2, item.artId)
                }
                picAdapter.setList(listOf(item.pics))
               it.ryPc.isVisible = true

                GlideUtils.loadCircle(item.authors?.avatar,it.ivIcon)
               it.tvName.text = item.authors?.nickname
               it.tvTime.text = item.timeStr
               it.tvContent.text = item.title
            } else {
               it.ryPc.isVisible = false
            }
        }
    }

    override fun getItemCount(): Int {
        if (data.size > 5) return 5
        return super.getItemCount()
    }

    private fun setLayoutWidth(imageView: ConstraintLayout) {
        val layoutParam = imageView.layoutParams
        layoutParam.width = 200.toIntPx()
    }

}