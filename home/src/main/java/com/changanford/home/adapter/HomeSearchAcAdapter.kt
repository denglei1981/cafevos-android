package com.changanford.home.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.SpannableStringUtils
import com.changanford.home.R
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.databinding.ItemSearchAutoBinding

/**
 * @Author: hpb
 * @Date: 2020/5/12
 * @Des: 首页搜索结果item
 */
class HomeSearchAcAdapter :
    BaseQuickAdapter<SearchKeyBean, BaseDataBindingHolder<ItemSearchAutoBinding>>(
        R.layout.item_search_auto
    ) {

    var searchContent = ""

    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchAutoBinding>,
        item: SearchKeyBean
    ) {
        holder.dataBinding?.tvContent?.text = SpannableStringUtils.findSearch(
            ContextCompat.getColor(context, R.color.color_1700f4),
            item.keyword,
            arrayListOf(searchContent)
        )
    }
}