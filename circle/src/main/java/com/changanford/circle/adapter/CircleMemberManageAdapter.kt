package com.changanford.circle.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.ItemCircleMemberManageBinding
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils

/**
 *Author lcw
 *Time on 2021/10/14
 *Purpose
 */
class CircleMemberManageAdapter :
    BaseQuickAdapter<CircleMemberBean, BaseViewHolder>(R.layout.item_circle_member_manage),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CircleMemberBean) {
        val binding = DataBindingUtil.bind<ItemCircleMemberManageBinding>(holder.itemView)
        binding?.let {
            binding.bean = item
            if (item.starOrderNumStr?.isNotEmpty() == true) {
                binding.masterTv.visibility = View.VISIBLE
            } else {
                binding.masterTv.visibility = View.GONE
            }
            GlideUtils.loadCircle(item.avatar,binding.avatarImg,R.mipmap.ic_def_square_img)
            binding.checkbox.isChecked = item.isCheck
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.isCheck = isChecked
                LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_MEMBER_MANAGE).postValue("")
            }
            val labelAdapter = LabelAdapter(binding.avatarImg.context, 20)
            labelAdapter.setItems(item.imags)
            binding.rlIdentification.adapter = labelAdapter
            binding.executePendingBindings()
        }
    }
}