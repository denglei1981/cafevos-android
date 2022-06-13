package com.changanford.my.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.bean.Topic
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinCirlceBinding
import com.changanford.evos.databinding.ItemMysJoinTopicBinding
import com.changanford.evos.databinding.ItemPersonMedalV2Binding


class TaMedalAdapter :
    BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemPersonMedalV2Binding>>(R.layout.item_person_medal_v2) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemPersonMedalV2Binding>,
        item: MedalListBeanItem
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.medalImage, t.imMedalIcon)
            t.tvMedalName.text = item.medalName
            t.tvMedalTime.text = item.timeS()
        }
    }


}