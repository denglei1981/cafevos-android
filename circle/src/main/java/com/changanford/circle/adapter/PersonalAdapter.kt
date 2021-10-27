package com.changanford.circle.adapter

import android.os.Bundle
import android.os.UserManager
import android.view.View
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.ItemPersonalBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.MUtils
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class PersonalAdapter :
    BaseQuickAdapter<CircleMemberBean, BaseViewHolder>(R.layout.item_personal), LoadMoreModule {

    init {
        addChildClickViewIds(R.id.tv_out)
    }

    var isApply = ""

    override fun convert(holder: BaseViewHolder, item: CircleMemberBean) {
        val binding = DataBindingUtil.bind<ItemPersonalBinding>(holder.itemView)
        binding?.let {
            MUtils.setTopMargin(binding.clItem, 27, holder.layoutPosition)

            binding.ivIcon.loadImage(item.avatar, ImageOptions().apply {
                circleCrop = true
                error = R.mipmap.head_default
            })
            binding.tvName.text = item.nickname
            if (!item.starOrderNumStr.isNullOrEmpty()) {
                binding.tvOwner.visibility = View.VISIBLE
                binding.tvOwner.text = item.starOrderNumStr
            } else {
                binding.tvOwner.visibility = View.GONE
            }
            val labelAdapter = LabelAdapter(context, 20)
            labelAdapter.setItems(item.imags)
            binding.ryImage.adapter = labelAdapter

            if (isApply == "2" && item.userId == UserManger.getSysUserInfo()?.uid?:"") {
                binding.tvOut.visibility = View.VISIBLE
            } else {
                binding.tvOut.visibility = View.GONE
            }

            binding.ivIcon.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("value", item.userId)
                startARouter(ARouterMyPath.TaCentreInfoUI, bundle)
            }

            binding.bean = item
        }
    }

}