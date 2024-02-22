package com.changanford.circle.adapter.circle

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.bean.ChoseCircleBean
import com.changanford.common.R
import com.changanford.common.databinding.ItemSearchAutoBinding
import com.changanford.common.util.SpannableStringUtils

/**
 * @Author: hpb
 * @Date: 2020/5/12
 * @Des: 首页搜索结果item
 */
class CircleSearchAdapter :
    BaseQuickAdapter<ChoseCircleBean, BaseDataBindingHolder<ItemSearchAutoBinding>>(
        R.layout.item_search_auto
    ),LoadMoreModule {

    var searchContent = ""

    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchAutoBinding>,
        item: ChoseCircleBean
    ) {
        holder.dataBinding?.tvContent?.text = SpannableStringUtils.findSearch(
            ContextCompat.getColor(context, R.color.color_1700f4),
            item.name,
            arrayListOf(searchContent)
        )
    }
}