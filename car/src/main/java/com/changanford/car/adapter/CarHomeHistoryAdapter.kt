package com.changanford.car.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemHomeCarHistoryBinding
import com.changanford.common.bean.PostDataBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx
import com.xiaomi.push.it

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomeHistoryAdapter :
    BaseQuickAdapter<PostDataBean, BaseDataBindingHolder<ItemHomeCarHistoryBinding>>(R.layout.item_home_car_history) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeCarHistoryBinding>,
        item: PostDataBean
    ) {
        val picAdapter = CarHomePicAdapter(item.type == 3)
        holder.dataBinding?.let {
            setLayoutWidth(it.clContent)
            if (!item.picList.isNullOrEmpty()) {
                if (item.picList!!.size < 4) {
                    val layoutManager = LinearLayoutManager(context)
                    it.ryPc.layoutManager = layoutManager
                } else {
                    val layoutManager = GridLayoutManager(context, 2)
                    it.ryPc.layoutManager = layoutManager
                }
                it.ryPc.adapter = picAdapter
                picAdapter.setOnItemClickListener { _, _, _ ->
                    JumpUtils.instans?.jump(4, item.postsId.toString())
                }
                if (item.picList!!.size < 4) {
                    picAdapter.setList(listOf(item.picList!![0]))
                } else {
                    picAdapter.setList(item.picList!!.subList(0,4))
                }
                it.ryPc.isVisible = true

                GlideUtils.loadCircle(item.authorBaseVo?.avatar, it.ivIcon)
                it.tvName.text = item.authorBaseVo?.nickname
                it.tvTime.text = item.timeStr
                it.tvContent.text = item.title
            } else {
                it.ryPc.isVisible = false
            }
        }
    }

    private fun setLayoutWidth(imageView: ConstraintLayout) {
            val layoutParam = imageView.layoutParams
            layoutParam.width = 220.toIntPx()
//            layoutParam.height = layoutParam.width


    }
}