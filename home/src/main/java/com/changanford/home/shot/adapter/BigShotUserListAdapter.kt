package com.changanford.home.shot.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.databinding.ItemBigShotStateBinding

class BigShotUserListAdapter :
    BaseQuickAdapter<BigShotRecommendBean, BaseDataBindingHolder<ItemBigShotStateBinding>>(R.layout.item_big_shot_state) {


    override fun convert(
        holder: BaseDataBindingHolder<ItemBigShotStateBinding>,
        item: BigShotRecommendBean
    ) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.avatar, it.ivHead)
            it.tvName.text = item.nickname
            it.btnFollow.text = item.getIsFollow()
            GlideUtils.loadBD(item.memberIcon, it.ivVip)
        }
    }
}