package com.changanford.circle.ui.ask.adapter

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
class HotMechanicAdapter :
    BaseQuickAdapter<CircleMemberBean, BaseViewHolder>(R.layout.item_hot_mechanic), LoadMoreModule {



    override fun convert(holder: BaseViewHolder, item: CircleMemberBean) {
        val binding = DataBindingUtil.bind<ItemPersonalBinding>(holder.itemView)
        binding?.let {

        }
    }

}