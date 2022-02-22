package com.changanford.circle.ui.ask.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.bean.TecnicianVo
import com.changanford.circle.databinding.ItemHotMechanicBinding
import com.changanford.common.utilext.GlideUtils

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class HotMechanicAdapter :
    BaseQuickAdapter<TecnicianVo, BaseViewHolder>(R.layout.item_hot_mechanic), LoadMoreModule {



    override fun convert(holder: BaseViewHolder, item: TecnicianVo) {
        val binding = DataBindingUtil.bind<ItemHotMechanicBinding>(holder.itemView)
        binding?.let {
            GlideUtils.loadBD(item.avater,it.ivHeader)
            it.tvUserName.text=item.nickName
            when(holder.layoutPosition){
                0->{
                    it.ivHot.visibility= View.VISIBLE
                    it.ivHot.setImageResource(R.drawable.icon_home_mechanic_hot_one)
                    it.ivHeader.strokeColor= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.color_yellow_F9D24B))
                }
                1->{
                    it.ivHot.visibility= View.VISIBLE
                    it.ivHot.setImageResource(R.drawable.icon_home_mechanic_hot_two)
                    it.ivHeader.strokeColor= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.color_yellow_F9D24B))
                }
                2->{
                    it.ivHot.visibility= View.VISIBLE
                    it.ivHot.setImageResource(R.drawable.icon_home_mechanic_hot_three)
                    it.ivHeader.strokeColor= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.color_yellow_F9D24B))
                }
                else->{
                    it.ivHot.visibility= View.INVISIBLE
                    it.ivHeader.strokeColor=null
                }
            }
        }
    }

}